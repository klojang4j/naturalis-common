package nl.naturalis.common.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingSupplier;
import static nl.naturalis.common.check.CommonChecks.gt;
import static nl.naturalis.common.check.CommonChecks.nullPointer;

/**
 * An output stream implementing a swap mechanism. A {@code SwapOutputStream} first fills up an
 * internal buffer. If the buffer reaches full capacity, an outstream to some swap-to resource is
 * created. The swap-to resource typically is some form of persistent storage (e.g. a swap file). It
 * is transparent to clients whether or not data has actually been swapped out of memory. Once they
 * are done writing data, clients can {@link #recall(OutputStream) collect} the data again without
 * having to know whether it came from the internal buffer or the swap-to resource.
 *
 * <p>{@code SwapOutputStream} is basically a {@link BufferedOutputStream}, except that the
 * underlying output stream is lazily instantiated and you can read back what you have written to
 * it. Therefore it makes little sense to wrap a {@code SwapOutputStream} into a {@code
 * BufferedOutputStream}.
 *
 * <p>This class is not thread-safe. Synchronization, if necessary, must be enforced by the calling
 * method.
 *
 * @author Ayco Holleman
 */
public abstract class SwapOutputStream extends RecallOutputStream {

  /** A factory producing the output stream to the swap-to resource. */
  private final ThrowingSupplier<OutputStream, IOException> factory;

  /** The output stream to the swap-to resource. */
  private OutputStream out;

  // The internal buffer
  private byte buf[];

  // The number of bytes written to the buffer
  private int cnt;

  // Whether or not the swap-to output stream has been closed
  private boolean closed;

  /**
   * Creates a {@code SwapOutputStream} with an internal buffer of 8 kilobytes, swapping to the
   * specified resource once the buffer overflows.
   *
   * @param swapTo A supplier supplying an outputstream to the swap-to resource. The supplier's
   *     {@link ThrowingSupplier#get() get()} method will only be called if the internal buffer
   *     overflows.
   */
  public SwapOutputStream(ThrowingSupplier<OutputStream, IOException> swapTo) {
    this(swapTo, 8 * 1024);
  }

  /**
   * Creates a {@code SwapOutputStream} with an internal buffer of {@code treshold} bytes, swapping
   * to the specified resource once the buffer overflows.
   *
   * @param swapTo A supplier supplying an outputstream to the swap-to resource. The supplier's
   *     {@link ThrowingSupplier#get() get()} method will only be called if the internal buffer
   *     overflows.
   * @param bufSize The size in bytes of the internal buffer
   */
  public SwapOutputStream(ThrowingSupplier<OutputStream, IOException> swapTo, int bufSize) {
    this.factory = Check.notNull(swapTo, "swapTo").ok();
    this.buf = Check.that(bufSize).is(gt(), 0).ok(byte[]::new);
  }

  /**
   * Writes the specified byte to this output stream. If this causes an overflow of the internal
   * buffer, the buffer is flushed to the underlying output stream and all subsequent write actions
   * will be forwarded to the underlying output stream.
   */
  @Override
  public void write(int b) throws IOException {
    if (cnt == buf.length) {
      swap();
    }
    buf[cnt++] = (byte) b;
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code>
   * to this output stream. If this causes an overflow of the internal buffer, the buffer is flushed
   * to the underlying output stream and all subsequent write actions will be forwarded to the
   * underlying output stream.
   */
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    if (len > buf.length) {
      swap();
      swap(out, b, off, len);
    } else {
      if (cnt + len > buf.length) {
        swap();
      }
      System.arraycopy(b, off, buf, cnt, len);
      cnt += len;
    }
  }

  /**
   * Deletes the swap-to resource (e.g. a swap file). Can be called after {@link
   * #recall(OutputStream) collect()} if the swap-to resource (e.g. a swap file) is no longer
   * needed, or in the {@code catch} block of an exception. The {@code cleanup} method of {@code
   * SwapOutputStream} does nothing.
   *
   * @throws IOException
   */
  public void cleanup() throws IOException {}

  /**
   * Calls {@link OutputStream#flush() flush()} on the swap-to output stream if the {@code
   * SwapOutputStream} has started writing to it. Otherwise this method does nothing.
   */
  @Override
  public void flush() throws IOException {
    if (out != null) {
      out.flush();
    }
  }

  /**
   * If the {@code SwapOutputStream} has started writing to the swap-to output stream, any remaining
   * bytes in the internal buffer will be flushed to it and then its {@code close()} method will be
   * called. Otherwise this method does nothing. Any remaining bytes in the internal buffer will
   * just stay there.
   */
  @Override
  public void close() throws IOException {
    if (out != null && !closed) {
      if (cnt > 0) {
        swap();
      }
      out.close();
      closed = true;
    }
  }

  /**
   * Forces the internal buffer to be flushed to the swap-to output stream, even if the internal
   * buffer has not reached full capacity yet. You should not normally call this method as swapping
   * is taken care of automatically, but it could be useful for debug purposes (e.g. to inspect the
   * contents of a swap file).
   *
   * @throws IOException If an I/O error occurs
   */
  public final void swap() throws IOException {
    if (out == null) {
      out = factory.get();
    }
    swap(out, buf, 0, cnt);
    cnt = 0;
  }

  /**
   * Returns whether or not the {@code SwapOutputStream} has started to write to the swap-to output
   * stream. You should not normally need to call this method as swapping is taken care of
   * automatically, but it could be used for debug or logging purposes.
   */
  public final boolean hasSwapped() {
    return out != null;
  }

  /**
   * Returns the size of the internal buffer.
   *
   * @return The size of the internal buffer
   */
  protected final int bufferSize() {
    return buf.length;
  }

  /**
   * Returns the number of live bytes in the internal buffer.
   *
   * @return The live bytes in the internal buffer
   */
  protected final int byteCount() {
    return cnt;
  }

  /**
   * Copies the contents of the internal buffer to the specified output stream. An IOException is
   * thrown if the {@code SwapOutputStream} has already started writing to the swap-to outputstream.
   *
   * @param to The output stream to which to copy the contents of the internal buffer
   * @throws IOException If an I/O error occurs
   * @throws IllegalStateException If the swap has already taken place
   */
  protected final void readBuffer(OutputStream to) throws IOException {
    Check.notNull(to);
    Check.with(IOException::new, out).is(nullPointer(), "Already swapped");
    if (cnt > 0) {
      swap(to, buf, 0, cnt);
    }
  }

  private static void swap(OutputStream out, byte[] b, int off, int len) throws IOException {
    out.write(b, off, len);
  }
}
