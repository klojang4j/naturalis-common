package nl.naturalis.common.io;

import java.io.IOException;
import java.io.OutputStream;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.gte;
import static nl.naturalis.common.check.CommonChecks.notNull;

/**
 * An output stream implementing a swap mechanism. A {@code SwapOutputStream} first fills up an
 * internal buffer. If the buffer reaches full capacity, it is flushed to an underlying output
 * stream (typically some form of persistent storage). From that moment onwards all write actions
 * are forwarded to the underlying output stream. It is transparent to clients whether or not data
 * has actually been swapped to the underlying output stream. Once done writing data clients can
 * collect the data without having to know whether a swap took place. The collection mechanism is
 * left to concrete subclasses of {@code SwapOutputStream}.
 *
 * @author Ayco Holleman
 */
public abstract class SwapOutputStream extends OutputStream {

  /** The output stream to which data will be written once the internal buffer overflows. */
  protected final OutputStream out;

  // The internal buffer
  private byte buf[];

  // The number of bytes written to the buffer
  private int sz;

  /**
   * Creates a {@code SwapOutputStream} with an internal buffer of 4 kilobytes, swapping to the
   * specified {@code OutputStream} once the buffer overflows.
   *
   * @param swapTo The output stream to which data is written once the internal buffer overflows
   */
  public SwapOutputStream(OutputStream swapTo) {
    this(swapTo, 4 * 1024);
  }

  /**
   * Creates a {@code SwapOutputStream} with an internal buffer of {@code treshold} bytes, swapping
   * to the specified {@code OutputStream} once the buffer overflows.
   *
   * @param The output stream to which data is written once the internal buffer overflows
   * @param treshold The size in bytes of the internal buffer
   */
  public SwapOutputStream(OutputStream swapTo, int treshold) {
    this.out = Check.notNull(swapTo, "swapTo").ok();
    this.buf = Check.that(treshold).is(gte(), 0).ok(byte[]::new);
  }

  /**
   * Writes the specified byte to this output stream. If this causes an overflow of the internal
   * buffer, the buffer is emptied into the underlying output stream and all subsequent write
   * actions will be forwarded to the underlying output stream. This method is not thread-safe.
   * Synchronization, if necessary, must be enforced by the caller.
   */
  @Override
  public void write(int b) throws IOException {
    if (buf == null) { // we have swapped already
      out.write((byte) b);
    } else if (sz >= buf.length) { // time to swap
      out.write(buf);
      out.write((byte) b);
      buf = null;
    } else { // just fill up the buffer
      buf[sz++] = (byte) b;
    }
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code>
   * to this output stream. If this causes an overflow of the internal buffer, the buffer is emptied
   * into the underlying output stream and all subsequent write actions will be forwarded to the
   * underlying output stream. This method is not thread-safe. Synchronization, if necessary, must
   * be enforced by the caller.
   */
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    Check.that(len, "len").is(gte(), 0);
    if (buf == null) { // we have swapped already
      out.write(b, off, len);
    } else if (sz + len > buf.length) { // time to swap
      out.write(buf, 0, sz);
      out.write(b, off, len);
      buf = null;
    } else { // just fill up the buffer
      System.arraycopy(b, off, buf, sz, len);
      sz += len;
    }
  }

  /**
   * Returns whether or not the {@code SwapOutputStream} write actions are done on the underlying
   * output stream.
   */
  public boolean hasSwapped() {
    return buf == null;
  }

  /**
   * Forces a swap the underlying output stream, even if the internal buffer has not reached full
   * capacity yet. An {@link IllegalStateException} is thrown if the swap has already taken place.
   * All subsequent write actions will be forwarded to the underlying output stream. This method is
   * not thread-safe. Synchronization, if necessary, must be enforced by the caller.
   *
   * @throws IOException If an I/O error occurs
   * @throws IllegalStateException If the swap has already taken place
   */
  public void swap() throws IOException, IllegalStateException {
    checkNotSwapped();
    out.write(buf, 0, sz);
    buf = null;
  }

  /**
   * Writes all data written to this {@code SwapOutputStream} to the specified output stream.
   *
   * @param output The output stream to which to write the data
   * @throws IOException If an I/O error occurs
   */
  public abstract void collect(OutputStream output) throws IOException;

  /**
   * Calls {@code flush()} on the underlying {@link OutputStream} if the swap has taken place, else
   * does nothing.
   */
  @Override
  public void flush() throws IOException {
    if (buf == null) {
      out.flush();
    }
  }

  /**
   * Calls {@code close()} on the underlying output stream. This will not induce an implicit swap.
   * If the internal buffer has not been swapped out yet to the output stream, the output stream
   * will be closed without any data being written to it.
   */
  @Override
  public void close() throws IOException {
    out.close();
  }

  /**
   * Returns the number of bytes written to the internal buffer.
   *
   * @return
   */
  protected int size() {
    return sz;
  }

  /**
   * Writes the contents of the internal buffer to the specified output stream. If the internal
   * buffer has already been emptied into the underlying output stream, this method throws an {@link
   * IllegalStateException}.
   *
   * @param to The output stream to which to write the contents of the internal buffer
   * @throws IOException If an I/O error occurs.
   * @throws IllegalStateException If the swap has already taken place
   */
  protected void writeBuffer(OutputStream to) throws IOException {
    checkNotSwapped();
    Check.notNull(to).then(out -> out.write(buf, 0, sz));
  }

  private void checkNotSwapped() {
    Check.that(buf, IllegalStateException::new).is(notNull(), "Already swapped");
  }
}
