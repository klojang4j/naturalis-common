package nl.naturalis.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingSupplier;
import static nl.naturalis.common.IOMethods.pipe;
import static nl.naturalis.common.check.CommonChecks.fileExists;
import static nl.naturalis.common.IOMethods.*;

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
public class SimpleFileSwapOutputStream extends FileSwapOutputStream {

  /**
   * Creates a new instance that swaps to an auto-generated temp file.
   *
   * @see SwapOutputStream#SwapOutputStream(ThrowingSupplier)
   * @return A {@code FileSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static SimpleFileSwapOutputStream newInstance() {
    try {
      return new SimpleFileSwapOutputStream(createTempFile());
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer is specified through the {@code bufSize} parameter.
   *
   * @see SwapOutputStream#SwapOutputStream(ThrowingSupplier, int)
   * @param bufSize The size in bytes of the internal buffer
   * @return A {@code FileSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static SimpleFileSwapOutputStream newInstance(int bufSize) {
    try {
      return new SimpleFileSwapOutputStream(createTempFile(), bufSize);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Creates a new instance that swaps to the specified file.
   *
   * @see SwapOutputStream#SwapOutputStream(ThrowingSupplier)
   * @param swapFile The file to write to once the internal buffer overflows
   */
  public SimpleFileSwapOutputStream(File swapFile) {
    super(swapFile);
  }

  /**
   * Creates a new instance that swaps to the specified file.
   *
   * @see SwapOutputStream#SwapOutputStream(ThrowingSupplier, int)
   * @param swapFile The file to write to once the internal buffer overflows
   * @param bufSize The size in bytes of the internal buffer
   */
  public SimpleFileSwapOutputStream(File swapFile, int bufSize) {
    super(swapFile, bufSize);
  }

  /** See {@link RecallOutputStream#recall(OutputStream)}. */
  public void recall(OutputStream output) throws IOException {
    Check.notNull(output);
    if (hasSwapped()) {
      Check.with(IOException::new, getSwapFile()).is(fileExists(), "Swap file gone");
      close();
      try (FileInputStream fis = new FileInputStream(getSwapFile())) {
        pipe(fis, output, bufferSize());
      }
    } else {
      readBuffer(output);
    }
  }

  @Override
  protected void intercept(int b) throws IOException {
    doWrite(b);
  }

  @Override
  protected void intercept(byte[] b, int off, int len) throws IOException {
    doWrite(b, off, len);
  }
}
