package nl.naturalis.common.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.InflaterOutputStream;
import nl.naturalis.common.ExceptionMethods;
import static nl.naturalis.common.IOMethods.createTempFile;

/**
 * A {@code SwapOutputStream} that compresses the data as it enters the internal buffer, thereby
 * decreasing the chance that the internal buffer will have to be swapped out to file. The {@link
 * #recall(OutputStream) recall} method will uncompress the data again. After the {@code recall}
 * method has been called, the {@code DeflatedArraySwapOutputStream}, like any {@link
 * SwapOutputStream}, becomes a regular {@code BufferedOutputStream} and it will no longer compress
 * the data.
 *
 * @author Ayco Holleman
 */
public class DeflatedArraySwapOutputStream extends ArraySwapOutputStream {

  /**
   * Creates a new instance that swaps to an auto-generated temp file.
   *
   * @return A {@code DeflatedArraySwapOutputStream} that swaps to an auto-generated temp file
   */
  public static DeflatedArraySwapOutputStream newInstance() {
    try {
      return new DeflatedArraySwapOutputStream(createTempFile(DeflatedArraySwapOutputStream.class));
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer is specified through the {@code bufSize} parameter.
   *
   * @param bufSize The size in bytes of the internal buffer
   * @return A {@code DeflatedArraySwapOutputStream} that swaps to an auto-generated temp file
   */
  public static DeflatedArraySwapOutputStream newInstance(int bufSize) {
    try {
      return new DeflatedArraySwapOutputStream(
          createTempFile(DeflatedArraySwapOutputStream.class), bufSize);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer is specified through the {@code bufSize} parameter.
   *
   * @param bufSize The size in bytes of the internal buffer
   * @param compressionLevel The compression level (0-9)
   * @return A {@code DeflatedArraySwapOutputStream} that swaps to an auto-generated temp file
   */
  public static DeflatedArraySwapOutputStream newInstance(int bufSize, int compressionLevel) {
    try {
      return new DeflatedArraySwapOutputStream(
          createTempFile(DeflatedArraySwapOutputStream.class), bufSize, compressionLevel);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  private final Deflater def;
  // Bucket for deflated data, written to by the deflater, read by us
  private final byte[] temp = new byte[1024];

  /**
   * Creates a new {@code DeflatedArraySwapOutputStream} with an internal buffer of 64 kB, swapping
   * to the specified file once its internal buffer fills up
   *
   * @param swapFile The swap file
   */
  public DeflatedArraySwapOutputStream(File swapFile) {
    super(swapFile);
    this.def = new Deflater();
  }

  /**
   * Creates a new {@code ArraySwapOutputStream} with an internal buffer of {@code bufSize} bytes,
   * swapping to the specified file once its internal buffer fills up
   *
   * @param swapFile The swap file
   * @param bufSize The size in bytes of the internal buffer
   */
  public DeflatedArraySwapOutputStream(File swapFile, int bufSize) {
    super(swapFile, bufSize);
    this.def = new Deflater();
  }

  /**
   * Creates a {@code DeflatedArraySwapOutputStream} with an internal buffer of {@code treshold}
   * bytes, swapping to the specified resource once the buffer overflows.
   *
   * @param swapFile A {@code Supplier} of the swap-to outputstream.
   * @param bufSize The size in bytes of the internal buffer
   * @param compressionLevel The compression level (0-9)
   */
  public DeflatedArraySwapOutputStream(File swapFile, int bufSize, int compressionLevel) {
    super(swapFile, bufSize);
    this.def = new Deflater(compressionLevel);
  }

  @Override
  public void write(int b) throws IOException {
    write(new byte[] {(byte) b}, 0, 1);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    if (recalled()) {
      super.write(b, off, len);
    } else {
      def.setInput(b, off, len);
      while (!def.needsInput()) {
        deflate();
      }
    }
  }

  @Override
  public void close() throws IOException {
    if (!recalled()) {
      finish();
      def.end();
    }
    super.close();
  }

  @Override
  void prepareRecall() throws IOException {
    finish();
  }

  @Override
  OutputStream wrap(OutputStream target) {
    return new InflaterOutputStream(target);
  }

  private void finish() throws IOException {
    if (!def.finished()) {
      def.finish();
      while (!def.finished()) {
        deflate();
      }
    }
  }

  private void deflate() throws IOException {
    int len = def.deflate(temp, 0, temp.length);
    if (len > 0) {
      super.write(temp, 0, len);
    }
  }
}
