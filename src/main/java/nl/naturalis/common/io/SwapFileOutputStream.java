package nl.naturalis.common.io;

import java.io.*;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.IOMethods.pipe;
import static nl.naturalis.common.check.CommonChecks.fileExists;
import static nl.naturalis.common.check.CommonChecks.fileNotExists;
import static nl.naturalis.common.check.CommonChecks.gte;

/**
 * A {@link SwapOutputStream} that swaps to file once its internal buffer overflows. Example:
 *
 * <p>
 *
 * <pre>
 *   String data = "Is this going to be swapped?";
 *   // Create SwapFileOutputStream that swaps to a temp file if more
 *   // than 8 bytes are written to it
 *   SwapFileOutputStream sfos = SwapFileOutputStream.newInstance(8);
 *   sfos.write(data.getBytes());
 *   ByteArrayOutputStream baos = new ByteArrayOutputStream();
 *   sfos.collect(baos);
 *   sfos.cleanup(); // delete swap file
 *   assertEquals(data, baos.toString());
 * </pre>
 *
 * @see SwapOutputStream
 * @author Ayco Holleman
 */
public class SwapFileOutputStream extends SwapOutputStream {

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer will be 4 kilobytes. The {@code BufferedOutputStream} created for the swap file will
   * have a 512-byte buffer. (In other words, the {@code SwapFileOutputStream} first fills up an
   * internal buffer of 4 kilobytes, and then starts writing to the swap file in chunks of 512
   * bytes.)
   *
   * @return A {@code SwapFileOutputStream} that swaps to an auto-generated temp file
   * @throws IOException If an I/O error occurs
   */
  public static SwapFileOutputStream newInstance() throws IOException {
    return new SwapFileOutputStream(createTempFile());
  }

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer is specified through the {@code treshold} parameter. The {@code BufferedOutputStream}
   * created for the swap file will have a 512-byte buffer.
   *
   * @param treshold The size in bytes of the internal buffer
   * @return A {@code SwapFileOutputStream} that swaps to an auto-generated temp file
   * @throws IOException If an I/O error occurs
   */
  public static SwapFileOutputStream newInstance(int treshold) throws IOException {
    return new SwapFileOutputStream(createTempFile(), treshold);
  }

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer is specified through the {@code treshold} parameter. The {@code BufferedOutputStream}
   * created for the swap file will have a size of {@code bufSize} bytes.
   *
   * @param treshold The size in bytes of the internal buffer
   * @param ioBufSize The buffer size of the {@code BufferedOutputStream} used to write to the swap
   *     file and the {@code BufferedInpuStream} used to read from the swap file (in the {@link
   *     #collect(OutputStream) collect} method)
   * @return A {@code SwapFileOutputStream} that swaps to an auto-generated temp file
   * @throws IOException If an I/O error occurs
   */
  public static SwapFileOutputStream newInstance(int treshold, int ioBufSize) throws IOException {
    return new SwapFileOutputStream(createTempFile(), treshold, ioBufSize);
  }

  private final File swapFile;

  /*
   * This is the buffer size for the BufferedOutputStream and the BufferedInputStream writing
   * to/reading from the swap file. It has nothing to do with the size of the internal buffer
   * maintained by SwapOutputStream.
   */
  private final int bufSize;

  /**
   * Creates a new instance that swaps to the specified file. The size of the internal buffer will
   * be 4 kilobytes. The {@code BufferedOutputStream} created for the swap file will have a 512-byte
   * buffer. (In other words, the {@code SwapFileOutputStream} first fills up an internal buffer of
   * 4 kilobytes, and then starts writing to the swap file in chunks of 512 bytes.)
   *
   * @param swapFile The file to write to once the internal buffer overflows
   * @throws IOException If an I/O error occurs
   */
  public SwapFileOutputStream(File swapFile) throws IOException {
    this(swapFile, 4 * 1024, 512);
  }

  /**
   * Creates a new instance that swaps to the specified file. The size of the internal buffer is
   * specified through the {@code treshold} parameter. The {@code BufferedOutputStream} created for
   * the swap file will have a 512-byte buffer.
   *
   * @param swapFile The file to write to once the internal buffer overflows
   * @param treshold The size in bytes of the internal buffer
   * @throws IOException
   */
  public SwapFileOutputStream(File swapFile, int treshold) throws IOException {
    this(swapFile, treshold, 512);
  }

  /**
   * Creates a new instance that swaps to the specified file. The size of the internal buffer is
   * specified through the {@code treshold} parameter. The {@code BufferedOutputStream} created for
   * the swap file will have a size of {@code bufSize} bytes.
   *
   * @param swapFile The file to write to once the internal buffer overflows
   * @param treshold The size in bytes of the internal buffer
   * @param bufSize The buffer size of the {@code BufferedOutputStream} used to write to the swap
   *     file and the {@code BufferedInpuStream} used to read from the swap file (in the {@link
   *     #collect(OutputStream) collect} method)
   * @throws IOException If an I/O error occurs
   */
  public SwapFileOutputStream(File swapFile, int treshold, int bufSize) throws IOException {
    super(createOutputStream(swapFile, bufSize), treshold);
    this.swapFile = swapFile;
    this.bufSize = bufSize;
  }

  /** See {@link SwapOutputStream#collect(OutputStream)}. */
  public void collect(OutputStream output) throws IOException {
    Check.notNull(output);
    Check.that(swapFile, IllegalStateException::new).is(fileExists());
    out.close();
    if (hasSwapped()) {
      try (FileInputStream fis = new FileInputStream(swapFile)) {
        pipe(fis, output, bufSize);
      }
    } else {
      writeBuffer(output);
    }
  }

  /**
   * Deletes the swap file.
   *
   * @throws IOException If an I/O error occurs
   */
  public void cleanup() throws IOException {
    if (swapFile.exists()) {
      swapFile.delete();
    }
  }

  /**
   * Returns the swap file.
   *
   * @return The swap file
   */
  public File getSwapFile() {
    return swapFile;
  }

  private static OutputStream createOutputStream(File swapFile, int bufSize) throws IOException {
    Check.notNull(swapFile, "swapFile").is(fileNotExists());
    Check.that(bufSize, "bufSize").is(gte(), 0);
    return new BufferedOutputStream(new FileOutputStream(swapFile), bufSize);
  }

  /*
   * Creating a temp file using File.createTempFile() is not satisfactory as the swap files may
   * be created in rapid succession and File.createTempFile() seems to use only
   * System.currentTimeMillis() to invent a file name.
   */
  private static File createTempFile() {
    StringBuilder sb = new StringBuilder(64);
    sb.append(System.getProperty("java.io.tmpdir"))
        .append('/')
        .append(SwapFileOutputStream.class.getSimpleName())
        .append(System.identityHashCode(new Object()))
        .append(System.currentTimeMillis())
        .append(".swp");
    return new File(sb.toString());
  }
}
