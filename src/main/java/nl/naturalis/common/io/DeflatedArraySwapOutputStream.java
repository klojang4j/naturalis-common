package nl.naturalis.common.io;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.InflaterOutputStream;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingSupplier;
import static nl.naturalis.common.IOMethods.createTempFile;
import static nl.naturalis.common.IOMethods.pipe;

/**
 * A subclass of {@code ArraySwapOutputStream} that swaps to file once its internal buffer
 * overflows. Data is compressed as it is written to an internal buffer, thereby decreasing the
 * chance that the internal buffer will have to be swapped out to file. This class combines the
 * functionality of {@link DeflaterOutputStream} and {@link BufferedOutputStream}. Therefore you
 * don't anything from wrapping a {@code ZipFileSwapOutputStream} into a {@code
 * BufferedOutputStream}.
 *
 * @author Ayco Holleman
 */
public class DeflatedArraySwapOutputStream extends ArraySwapOutputStream {

  /**
   * Creates a new instance that swaps to an auto-generated temp file.
   *
   * @see ArraySwapOutputStream#SwapOutputStream(ThrowingSupplier)
   * @return A {@code ZipFileSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static DeflatedArraySwapOutputStream newInstance() {
    try {
      return new DeflatedArraySwapOutputStream(createTempFile());
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer is specified through the {@code bufSize} parameter.
   *
   * @see ArraySwapOutputStream#SwapOutputStream(ThrowingSupplier, int)
   * @param bufSize The size in bytes of the internal buffer
   * @return A {@code ZipFileSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static DeflatedArraySwapOutputStream newInstance(int bufSize) {
    try {
      return new DeflatedArraySwapOutputStream(createTempFile(), bufSize);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer is specified through the {@code bufSize} parameter.
   *
   * @see ArraySwapOutputStream#SwapOutputStream(ThrowingSupplier, int)
   * @param bufSize The size in bytes of the internal buffer
   * @param compressionLevel The compression level (0-9)
   * @return A {@code ZipFileSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static DeflatedArraySwapOutputStream newInstance(int bufSize, int compressionLevel) {
    try {
      return new DeflatedArraySwapOutputStream(createTempFile(), bufSize, compressionLevel);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  private final Deflater def;
  private final byte[] buf = new byte[1024];

  private boolean closed;

  /**
   * Creates a {@code ArraySwapOutputStream} with an internal buffer of {@code treshold} bytes,
   * swapping to the specified resource once the buffer overflows. Data entering the {@code
   * ZipFileSwapOutputStream} is compressed using the {@link Deflater#DEFAULT_COMPRESSION default
   * compression level}.
   *
   * @param swapFile A {@code Supplier} of the swap-to outputstream.
   */
  public DeflatedArraySwapOutputStream(File swapFile) {
    super(swapFile);
    this.def = new Deflater();
  }

  /**
   * Creates a {@code ArraySwapOutputStream} with an internal buffer of {@code treshold} bytes,
   * swapping to the specified resource once the buffer overflows. Data entering the {@code
   * ZipFileSwapOutputStream} is compressed using the {@link Deflater#DEFAULT_COMPRESSION default
   * compression level}.
   *
   * @param swapFile A {@code Supplier} of the swap-to outputstream.
   * @param bufSize The size in bytes of the internal buffer
   */
  public DeflatedArraySwapOutputStream(File swapFile, int bufSize) {
    super(swapFile, bufSize);
    this.def = new Deflater();
  }

  /**
   * Creates a {@code ZipFileSwapOutputStream} with an internal buffer of {@code treshold} bytes,
   * swapping to the specified resource once the buffer overflows.
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
    write(new byte[] {(byte) (b & 0xff)}, 0, 1);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    def.setInput(b, off, len);
    while (!def.needsInput()) {
      deflate();
    }
  }

  @Override
  public void recall(OutputStream output) throws IOException {
    Check.notNull(output);
    // Flush any remaining bytes in the Deflater's buffer to the internal buffer
    finish();
    if (hasSwapped()) {
      // Flush any remaining bytes in the internal buffer to the swap-to output stream
      super.close();
      try (InflaterInputStream iis = new InflaterInputStream(new FileInputStream(swapFile))) {
        pipe(iis, output, bufferSize());
      }
    } else {
      readBuffer(new InflaterOutputStream(output));
    }
  }

  /**
   * Closes the {@code ZipFileSwapOutputStream} and releases all resources held by it. You cannot
   * re-use a {@code ZipFileSwapOutputStream} once you have called this method.
   */
  public void close() throws IOException {
    if (!closed) {
      finish();
      super.close();
      def.end();
      closed = true;
    }
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
    int len = def.deflate(buf, 0, buf.length);
    if (len > 0) {
      super.write(buf, 0, len);
    }
  }
}
