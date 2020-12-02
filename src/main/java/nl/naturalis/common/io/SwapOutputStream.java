package nl.naturalis.common.io;

import java.io.IOException;
import java.io.OutputStream;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.gte;
import static nl.naturalis.common.check.CommonChecks.notNull;

/**
 * An output stream implementing a swap mechanism. A {@code SwapOutputStream} first fills up an
 * internal buffer. If the buffer reaches full capacity, it is flushed to an underlying output
 * stream. From then onwards all write actions are forwarded to the underlying output stream. The
 * underlying output stream would typically write to some form of persistent storage (e.g. a swap
 * file). It is transparent to clients whether or not data has actually been swapped out of memory.
 * Once they are done writing data, clients can collect the data without having to know whether or
 * not it was swapped. The collection mechanism is left to concrete subclasses of {@code
 * SwapOutputStream}.
 *
 * <p>This class is not thread-safe. If necessary, method calls must be synchronized externally by
 * the caller.
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
   * buffer, the buffer is flushed to the underlying output stream and all subsequent write actions
   * will be forwarded to the underlying output stream.
   */
  @Override
  public void write(int b) throws IOException {
    if (hasSwapped()) {
      out.write((byte) b);
    } else if (sz == buf.length) { // time to swap
      out.write(buf);
      out.write((byte) b);
      buf = null;
    } else { // just fill up the buffer
      buf[sz++] = (byte) b;
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
    if (hasSwapped()) {
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
   * Retrieves all data written to this {@code SwapOutputStream} and writes it to the specified
   * output stream. Subclasses are expected to close the underlying output stream, even if no data
   * has been written it yet (in which case they would only have to read the contents of the
   * internal buffer).
   *
   * @param output The output stream to which to write the data
   * @throws IOException If an I/O error occurs
   */
  public abstract void collect(OutputStream output) throws IOException;

  /**
   * Deletes the resource that the underlying output stream was writing to (e.g. a swap file). Can
   * be called after {@link #collect(OutputStream) collect} if the resource is no longer needed, or
   * in the {@code catch} block of an exception. The {@code cleanup} method of {@code
   * SwapOutputStream} does nothing.
   *
   * @throws IOException
   */
  public void cleanup() throws IOException {}

  /**
   * Calls {@link OutputStream#flush() flush()} on the underlying output stream if the {@code
   * SwapOutputStream} has started writing to it. Otherwise this method does nothing.
   */
  @Override
  public void flush() throws IOException {
    if (hasSwapped()) {
      out.flush();
    }
  }

  /**
   * Closes the underlying output stream. Regular code does not have to call this method, because
   * the {@link #collect(OutputStream) collect} method already implicitly closes the output stream.
   * However you may have to call this method if you find yourself in the {@code catch} block of an
   * exception.
   */
  public void closeSwapToStream() throws IOException {
    out.close();
  }

  /**
   * Does nothing. Notably this method doesn't close the underlying output stream as this is done
   * either implicitly by the {@link #collect(OutputStream) collect} method or explicitly by the
   * {@link #closeSwapToStream() closeSwapToStream} method. Therefore it's probably rather more than
   * less confusing to instantiate a {@code SwapOutputStream} using a <i>try-with-resources</i>
   * block, as the {@code collect} method would have to be called within the
   * <i>try-with-resources</i> block.
   */
  @Override
  public void close() throws IOException {}

  /**
   * Forces the internal buffer to be flushed the underlying output stream, even if the internal
   * buffer has not reached full capacity yet. You should not ordinarily have to call this method,
   * but it might be useful for debug purposes (e.g. to inspect a swap file).
   *
   * @throws IOException If an I/O error occurs
   */
  public void swap() throws IOException {
    if (!hasSwapped()) {
      out.write(buf, 0, sz);
      buf = null;
    }
  }

  /**
   * Returns whether or not the {@code SwapOutputStream} has swapped out its internal buffer to the
   * underlying output stream.
   */
  protected boolean hasSwapped() {
    return buf == null;
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
   * buffer has already been swapped out to the underlying output stream, this method throws an
   * {@link IllegalStateException}.
   *
   * @param to The output stream to which to write the contents of the internal buffer
   * @throws IOException If an I/O error occurs
   * @throws IllegalStateException If the swap has already taken place
   */
  protected void writeBuffer(OutputStream to) throws IOException {
    Check.that(buf, IllegalStateException::new).is(notNull(), "Already swapped");
    Check.notNull(to).then(out -> out.write(buf, 0, sz));
  }
}
