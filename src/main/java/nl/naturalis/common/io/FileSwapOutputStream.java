package nl.naturalis.common.io;

import java.io.*;
import java.util.Optional;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingSupplier;
import static nl.naturalis.common.IOMethods.pipe;
import static nl.naturalis.common.check.CommonChecks.fileExists;
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
   * Creates a new instance that swaps to an auto-generated temp file.
   *
   * @see SwapOutputStream#SwapOutputStream(ThrowingSupplier)
   * @return A {@code FileSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static FileSwapOutputStream newInstance() {
    return new FileSwapOutputStream(createTempFile());
  }

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer is specified through the {@code bufSize} parameter.
   *
   * @see SwapOutputStream#SwapOutputStream(ThrowingSupplier, int)
   * @param bufSize The size in bytes of the internal buffer
   * @return A {@code FileSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static FileSwapOutputStream newInstance(int bufSize) {
    return new FileSwapOutputStream(createTempFile(), bufSize);
  }

  private final File swapFile;

  /**
   * Creates a new instance that swaps to the specified file.
   *
   * @see SwapOutputStream#SwapOutputStream(ThrowingSupplier)
   * @param swapFile The file to write to once the internal buffer overflows
   */
  public FileSwapOutputStream(File swapFile) {
    super(createOutputStream(swapFile));
    this.swapFile = swapFile;
  }

  /**
   * Creates a new instance that swaps to the specified file.
   *
   * @see SwapOutputStream#SwapOutputStream(ThrowingSupplier, int)
   * @param swapFile The file to write to once the internal buffer overflows
   * @param bufSize The size in bytes of the internal buffer
   */
  public FileSwapOutputStream(File swapFile, int bufSize) {
    super(createOutputStream(swapFile), bufSize);
    this.swapFile = swapFile;
  }

  /**
   * Reads back the data written to this instance and writes it to the specified output stream. If
   * the data written to the {@code FileSwapOutputStream} still resides in the internal buffer (no
   * swapping has taken place yet), the contents of the internal buffer is written to the specified
   * output stream. Otherwise this method will first close the output stream to the swap file and
   * then open a {@link FileInputStream} on it in order to read back the data.
   */
  public void recall(OutputStream output) throws IOException {
    Check.notNull(output);
    if (hasSwapped()) {
      Check.with(IOException::new, swapFile).is(fileExists(), "Swap file gone");
      close();
      try (FileInputStream fis = new FileInputStream(swapFile)) {
        pipe(fis, output, bufferSize());
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
  public void cleanup() {
    if (swapFile.exists()) {
      swapFile.delete();
    }
  }

  /**
   * Returns an {@code Optional} containing a {@code File} object corresponding to the swap file if
   * a swap file was created and still exists. Otherwise returns an empy {@code Optional}.
   *
   * @return The swap file
   */
  public Optional<File> getSwapFile() {
    return swapFile.isFile() ? Optional.of(swapFile) : Optional.empty();
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
