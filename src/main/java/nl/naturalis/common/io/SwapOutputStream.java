package nl.naturalis.common.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingSupplier;
import static nl.naturalis.common.check.CommonChecks.gt;

/**
 * An output stream implementing a swap mechanism. A {@code SwapOutputStream} first fills up an
 * internal buffer. If the buffer reaches full capacity, an outstream to some swap-to resource is
 * created. The swap-to resource typically is some form of persistent storage (e.g. a swap file). It
 * is transparent to clients whether or not data has actually been swapped out of memory. Once they
 * are done writing data, clients can {@link #collect(OutputStream) collect} the data again without
 * having to know whether it came from the internal buffer or the swap-to resource.
 *
 * <p>Once the {@code SwapOutputStream} has started writing to the swap-to resource it basically
 * becomes a {@link BufferedOutputStream} with a buffer size equal to the size of the internal
 * buffer. Therefore there is no point in wrapping a {@code SwapOutputStream} into a {@code
 * BufferedOutputStream}.
 *
 * <p>This class is not thread-safe. Synchronization, if necessary, must be enforced by the calling
 * method.
 *
 * @author Ayco Holleman
 */
public abstract class SwapOutputStream extends OutputStream {

  /** A factory producing the output stream to the swap-to resource. */
  protected final ThrowingSupplier<OutputStream, IOException> factory;

  /** The output stream to the swap-to resource. */
  protected OutputStream out;

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
   * @param treshold The size in bytes of the internal buffer
   */
  public SwapOutputStream(ThrowingSupplier<OutputStream, IOException> swapTo, int treshold) {
    this.factory = Check.notNull(swapTo, "swapTo").ok();
    this.buf = Check.that(treshold).is(gt(), 0).ok(byte[]::new);
  }

  /**
   * Writes the specified byte to this output stream. If this causes an overflow of the internal
   * buffer, the buffer is flushed to the underlying output stream and all subsequent write actions
   * will be forwarded to the underlying output stream.
   */
  @Override
  public void write(int b) throws IOException {
    if (cnt == buf.length) {
      flushBuffer();
      out.write(b);
    } else {
      buf[cnt++] = (byte) b;
    }
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
      flushBuffer();
      out.write(b, off, len);
    } else if (cnt + len > buf.length) {
      flushBuffer();
      out.write(b, off, len);
    } else {
      System.arraycopy(b, off, buf, cnt, len);
      cnt += len;
    }
  }

  /**
   * Reads back the data written to this {@code SwapOutputStream} and copies it to the specified
   * output
   *
   * @param output The output stream to which to write the data
   * @throws IOException If an I/O error occurs
   */
  public abstract void collect(OutputStream output) throws IOException;

  /**
   * Deletes the swap-to resource (e.g. a swap file). Can be called after {@link
   * #collect(OutputStream) collect()} if the resource is no longer needed, or in the {@code catch}
   * block of an exception. The {@code cleanup} method of {@code SwapOutputStream} does nothing.
   *
   * @throws IOException
   */
  public void cleanup() throws IOException {}

  /**
   * Calls {@link OutputStream#flush() flush()} on the swap-to output stream <i>if</i> the {@code
   * SwapOutputStream} has started writing to it. Otherwise this method does nothing.
   */
  @Override
  public void flush() throws IOException {
    if (out != null) {
      out.flush();
    }
  }

  /**
   * Closes the swap-to output stream <i>if</i> the {@code SwapOutputStream} has started writing to
   * it. Otherwise this method does nothing.
   */
  @Override
  public void close() throws IOException {
    if (out != null && !closed) {
      if (cnt > 0) {
        out.write(buf, 0, cnt);
      }
      out.close();
      closed = true;
    }
  }

  /**
   * Forces the internal buffer to be flushed to the underlying output stream, even if the internal
   * buffer has not reached full capacity yet. You should call this method under normal
   * circumstances, as swapping is meant to be taken care of automatically, but it could be useful
   * for debug purposes (e.g. to inspect the contents of a swap file).
   *
   * @throws IOException If an I/O error occurs
   */
  public void swap() throws IOException {
    flushBuffer();
  }

  /**
   * Returns whether or not the {@code SwapOutputStream} has started to write to the swap-to output
   * stream.
   */
  protected boolean hasSwapped() {
    return out != null;
  }

  /**
   * Returns the size of the internal buffer.
   *
   * @return The size of the internal buffer
   */
  protected int treshold() {
    return buf.length;
  }

  /**
   * Copies the contents of the internal buffer to the specified output stream. This method is meant
   * to be used by subclasses in order to read back the contents of the internal buffer in case the
   * {@code SwapOutputStream} has not yet started writing to the swap-to output stream.
   *
   * @param to The output stream to which to copy the contents of the internal buffer
   * @throws IOException If an I/O error occurs
   * @throws IllegalStateException If the swap has already taken place
   */
  protected void readBuffer(OutputStream to) throws IOException {
    Check.state(out == null, "Already swapped");
    Check.notNull(to);
    if (cnt > 0) {
      to.write(buf, 0, cnt);
    }
  }

  private void flushBuffer() throws IOException {
    if (out == null) {
      out = factory.get();
    }
    out.write(buf, 0, cnt);
    cnt = 0;
  }
}
