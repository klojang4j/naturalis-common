package nl.naturalis.common.io;

import java.io.*;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingSupplier;
import static nl.naturalis.common.IOMethods.pipe;
import static nl.naturalis.common.check.CommonChecks.fileNotExists;

/**
 * A {@link SwapOutputStream} that swaps to file once its internal buffer overflows. Example:
 *
 * <p>
 *
 * <pre>
 *   String data = "Is this going to be swapped?";
 *   // Create FileSwapOutputStream that swaps to a temp file if more
 *   // than 8 bytes are written to it
 *   FileSwapOutputStream sfos = FileSwapOutputStream.newInstance(8);
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
public class FileSwapOutputStream extends SwapOutputStream {

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer will be 8 kilobytes. (In other words, the {@code FileSwapOutputStream} will create a
   * swap file if more than 8192 bytes are written to it.)
   *
   * @return A {@code FileSwapOutputStream} that swaps to an auto-generated temp file
   * @throws IOException If an I/O error occurs
   */
  public static FileSwapOutputStream newInstance() throws IOException {
    return new FileSwapOutputStream(createTempFile());
  }

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer is specified through the {@code treshold} parameter. The {@code BufferedOutputStream}
   * created for the swap file will have a 512-byte buffer.
   *
   * @param treshold The size in bytes of the internal buffer
   * @return A {@code FileSwapOutputStream} that swaps to an auto-generated temp file
   * @throws IOException If an I/O error occurs
   */
  public static FileSwapOutputStream newInstance(int treshold) throws IOException {
    return new FileSwapOutputStream(createTempFile(), treshold);
  }

  private final File swapFile;

  /**
   * Creates a new instance that swaps to the specified file. The size of the internal buffer will
   * be 4 kilobytes. The {@code BufferedOutputStream} created for the swap file will have a 512-byte
   * buffer. (In other words, the {@code FileSwapOutputStream} first fills up an internal buffer of
   * 8 kilobytes, and then starts writing to the swap file in chunks of 512 bytes.)
   *
   * @param swapFile The file to write to once the internal buffer overflows
   * @throws IOException If an I/O error occurs
   */
  public FileSwapOutputStream(File swapFile) throws IOException {
    this(swapFile, 8 * 1024);
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
  public FileSwapOutputStream(File swapFile, int treshold) throws IOException {
    super(createOutputStream(swapFile), treshold);
    this.swapFile = swapFile;
  }

  /** See {@link SwapOutputStream#collect(OutputStream)}. */
  public void collect(OutputStream output) throws IOException {
    Check.notNull(output);
    if (hasSwapped()) {
      close();
      try (FileInputStream fis = new FileInputStream(swapFile)) {
        pipe(fis, output, treshold());
      }
    } else {
      readBuffer(output);
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

  private static ThrowingSupplier<OutputStream, IOException> createOutputStream(File swapFile) {
    Check.notNull(swapFile, "Swap file").is(fileNotExists());
    return () -> new FileOutputStream(swapFile);
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
        .append(FileSwapOutputStream.class.getSimpleName())
        .append(System.identityHashCode(new Object()))
        .append(System.currentTimeMillis())
        .append(".swp");
    return new File(sb.toString());
  }
}
