package nl.naturalis.common.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingSupplier;
import static nl.naturalis.common.check.CommonChecks.gte;
import static nl.naturalis.common.check.CommonChecks.lte;

/**
 * A {@link RecallOutputStream} implementing a swap mechanism. Data is compressed as it enters the
 * {@code ZipSwapOutputStream}. If the internal of the {@code ZipSwapOutputStream} reaches full
 * capacity, a swap file is created to sink the data into. When reading back the data using the
 * {@link #recall(OutputStream) recall} method, the data is uncompressed again. This class combines
 * the functionality of {@link DeflaterOutputStream} and {@link BufferedOutputStream}. Therefore
 * there is no performance gain to be had from wrapping a {@code SwapOutputStream} into a {@code
 * BufferedOutputStream}.
 *
 * @author Ayco Holleman
 */
public class ZipSwapOutputStream extends RecallOutputStream {

  /**
   * Creates a new instance that swaps to an auto-generated temp file.
   *
   * @see SwapOutputStream#SwapOutputStream(ThrowingSupplier)
   * @return A {@code ZipSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static ZipSwapOutputStream newInstance() {
    return new ZipSwapOutputStream(FileSwapOutputStream.newInstance());
  }

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer is specified through the {@code bufSize} parameter.
   *
   * @see SwapOutputStream#SwapOutputStream(ThrowingSupplier, int)
   * @param bufSize The size in bytes of the internal buffer
   * @return A {@code ZipSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static ZipSwapOutputStream newInstance(int bufSize) {
    return new ZipSwapOutputStream(FileSwapOutputStream.newInstance(bufSize));
  }

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer is specified through the {@code bufSize} parameter.
   *
   * @see SwapOutputStream#SwapOutputStream(ThrowingSupplier, int)
   * @param bufSize The size in bytes of the internal buffer
   * @param compressionLevel The compression level (0-9)
   * @return A {@code ZipSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static ZipSwapOutputStream newInstance(int bufSize, int compressionLevel) {
    return new ZipSwapOutputStream(FileSwapOutputStream.newInstance(bufSize), compressionLevel);
  }

  private final FileSwapOutputStream fsos;
  private final Deflater def;
  private final byte[] buf = new byte[1024];

  /**
   * Creates a {@code SwapOutputStream} with an internal buffer of {@code treshold} bytes, swapping
   * to the specified resource once the buffer overflows. Data entering the {@code
   * ZipSwapOutputStream} is compressed using the {@link Deflater#DEFAULT_COMPRESSION default
   * compression level}.
   *
   * @param swapTo A {@code Supplier} of the swap-to outputstream.
   * @param bufSize The size in bytes of the internal buffer
   */
  public ZipSwapOutputStream(File swapTo) {
    this(new FileSwapOutputStream(swapTo));
  }

  /**
   * Creates a {@code SwapOutputStream} with an internal buffer of {@code treshold} bytes, swapping
   * to the specified resource once the buffer overflows. Data entering the {@code
   * ZipSwapOutputStream} is compressed using the {@link Deflater#DEFAULT_COMPRESSION default
   * compression level}.
   *
   * @param swapTo A {@code Supplier} of the swap-to outputstream.
   * @param bufSize The size in bytes of the internal buffer
   */
  public ZipSwapOutputStream(File swapTo, int bufSize) {
    this(new FileSwapOutputStream(swapTo, bufSize));
  }

  /**
   * Creates a {@code SwapOutputStream} with an internal buffer of {@code treshold} bytes, swapping
   * to the specified resource once the buffer overflows.
   *
   * @param swapTo A {@code Supplier} of the swap-to outputstream.
   * @param bufSize The size in bytes of the internal buffer
   * @param compressionLevel The compression level (0-9)
   */
  public ZipSwapOutputStream(File swapTo, int bufSize, int compressionLevel) {
    this(new FileSwapOutputStream(swapTo, bufSize), compressionLevel);
  }

  private ZipSwapOutputStream(FileSwapOutputStream fsos) {
    this.fsos = fsos;
    this.def = new Deflater();
  }

  private ZipSwapOutputStream(FileSwapOutputStream fsos, int compressionLevel) {
    this.fsos = fsos;
    this.def = Check.that(compressionLevel).is(gte(), 0).is(lte(), 9).ok(Deflater::new);
  }

  /** Writes the specified byte to this output stream. */
  @Override
  public void write(int b) throws IOException {
    write(new byte[] {(byte) (b & 0xff)}, 0, 1);
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code>
   * to this output stream.
   */
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    def.setInput(b, off, len);
    while (!def.needsInput()) {
      deflate();
    }
  }

  /** @see RecallOutputStream#recall(OutputStream) */
  @Override
  public void recall(OutputStream output) throws IOException {
    Check.notNull(output);
    closeSession();
    fsos.recall(new InflaterOutputStream(output));
  }

  /** See {@link RecallOutputStream#cleanup()} */
  public void cleanup() {
    fsos.cleanup();
  }

  /**
   * Calls {@link #closeSession()} and then releases all resource held by the {@code
   * ZipSwapOutputStream}. You cannot re-use a {@code ZipSwapOutputStream} after it has been closed.
   */
  public void close() throws IOException {
    closeSession();
    def.end();
  }

  /**
   * Tells the internally used {@link Deflater} to compress and flush whatever it still has in its
   * buffers.
   *
   * @throws IOException If an I/O error occurs
   */
  public void closeSession() throws IOException {
    finish();
    fsos.close();
  }

  /** See {@link SwapOutputStream#swap()}. */
  public void swap() throws IOException {
    fsos.swap();
  }

  /** See {@link SwapOutputStream#hasSwapped()}. */
  public boolean hasSwapped() {
    return fsos.hasSwapped();
  }

  /** See {@link FileSwapOutputStream#getSwapFile()}. */
  public Optional<File> getSwapFile() {
    return fsos.getSwapFile();
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
      fsos.write(buf, 0, len);
    }
  }
}
