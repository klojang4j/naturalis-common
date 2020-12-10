package nl.naturalis.common.io;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingSupplier;
import static nl.naturalis.common.IOMethods.createTempFile;
import static nl.naturalis.common.IOMethods.pipe;

/**
 * A {@link RecallOutputStream} implementing a swap mechanism. Data is compressed as it enters the
 * {@code ZipFileSwapOutputStream}. If the internal of the {@code ZipFileSwapOutputStream} reaches
 * full capacity, a swap file is created to sink the data into. When reading back the data using the
 * {@link #recall(OutputStream) recall} method, the data is uncompressed again. This class combines
 * the functionality of {@link DeflaterOutputStream} and {@link BufferedOutputStream}. Therefore
 * there is no performance gain to be had from wrapping a {@code SwapOutputStream} into a {@code
 * BufferedOutputStream}.
 *
 * @author Ayco Holleman
 */
public class ZipFileSwapOutputStream extends FileSwapOutputStream {

  /**
   * Creates a new instance that swaps to an auto-generated temp file.
   *
   * @see SwapOutputStream#SwapOutputStream(ThrowingSupplier)
   * @return A {@code ZipFileSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static ZipFileSwapOutputStream newInstance() {
    try {
      return new ZipFileSwapOutputStream(createTempFile());
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
   * @return A {@code ZipFileSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static ZipFileSwapOutputStream newInstance(int bufSize) {
    try {
      return new ZipFileSwapOutputStream(createTempFile(), bufSize);
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
   * @param compressionLevel The compression level (0-9)
   * @return A {@code ZipFileSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static ZipFileSwapOutputStream newInstance(int bufSize, int compressionLevel) {
    try {
      return new ZipFileSwapOutputStream(createTempFile(), bufSize, compressionLevel);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  private final Deflater def;
  private final byte[] buf = new byte[1024];

  /**
   * Creates a {@code SwapOutputStream} with an internal buffer of {@code treshold} bytes, swapping
   * to the specified resource once the buffer overflows. Data entering the {@code
   * ZipFileSwapOutputStream} is compressed using the {@link Deflater#DEFAULT_COMPRESSION default
   * compression level}.
   *
   * @param swapFile A {@code Supplier} of the swap-to outputstream.
   */
  public ZipFileSwapOutputStream(File swapFile) {
    super(swapFile);
    this.def = new Deflater();
  }

  /**
   * Creates a {@code SwapOutputStream} with an internal buffer of {@code treshold} bytes, swapping
   * to the specified resource once the buffer overflows. Data entering the {@code
   * ZipFileSwapOutputStream} is compressed using the {@link Deflater#DEFAULT_COMPRESSION default
   * compression level}.
   *
   * @param swapFile A {@code Supplier} of the swap-to outputstream.
   * @param bufSize The size in bytes of the internal buffer
   */
  public ZipFileSwapOutputStream(File swapFile, int bufSize) {
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
  public ZipFileSwapOutputStream(File swapFile, int bufSize, int compressionLevel) {
    super(swapFile, bufSize);
    this.def = new Deflater(compressionLevel);
  }

  /** Writes the specified byte to this output stream. */
  @Override
  protected void intercept(int b) throws IOException {
    write(new byte[] {(byte) (b & 0xff)}, 0, 1);
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code>
   * to this output stream.
   */
  @Override
  public void intercept(byte[] b, int off, int len) throws IOException {
    def.setInput(b, off, len);
    while (!def.needsInput()) {
      deflate();
    }
  }

  /** @see RecallOutputStream#recall(OutputStream) */
  @Override
  public void recall(OutputStream output) throws IOException {
    Check.notNull(output);
    finish();
    if (hasSwapped()) {
      super.close();
      try (InflaterInputStream iis = new InflaterInputStream(new FileInputStream(getSwapFile()))) {
        pipe(iis, output, bufferSize());
      }
    } else {
      readBuffer(output);
    }
  }

  /**
   * Closes the {@code ZipFileSwapOutputStream} and releases all resources held by it. You cannot
   * re-use a {@code ZipFileSwapOutputStream} once you have called this method.
   */
  public void close() throws IOException {
    try {
      closeSession();
    } finally {
      def.end();
    }
  }

  /**
   * Compresseses any remaining data and, if open, closes the swap-to output stream. Because the
   * swap-to output stream is lazily instantiated via a {@link ThrowingSupplier}, a {@code
   * SwapOutputStream} can safely be re-used after this method has been called. A subsequent {@code
   * write} action will simply cause the swap-to output stream to be retrieved again from the
   * supplier.
   *
   * @throws IOException If an I/O error occurs
   */
  public void closeSession() throws IOException {
    finish();
    super.close();
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
      doWrite(buf, 0, len);
    }
  }
}
