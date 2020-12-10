package nl.naturalis.common.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingSupplier;
import static nl.naturalis.common.check.CommonChecks.gt;
import static nl.naturalis.common.check.CommonChecks.nullPointer;

/**
 * A {@link RecallOutputStream} implementing a swap mechanism. A {@code SwapOutputStream} first
 * fills up an internal buffer. If (and only if) the buffer reaches full capacity, an underlying
 * outstream to some resource is created to sink the data into. The swap-to resource typically is
 * some form of persistent storage (e.g. a swap file). It is transparent to clients whether or not
 * data has actually been swapped out of memory. Clients can {@link #recall(OutputStream) recall}
 * the data without having to know whether it came from the internal buffer or persistent storage.
 *
 * <p>{@code SwapOutputStream} basically is a {@link BufferedOutputStream}, except that the
 * underlying output stream is lazily instantiated and you can read back what you wrote to it.
 * Therefore there is no performance gain to be had from wrapping a {@code SwapOutputStream} into a
 * {@code BufferedOutputStream}.
 *
 * @author Ayco Holleman
 */
public abstract class SwapOutputStream extends RecallOutputStream {

  // A factory producing the output stream to the swap-to resource
  private final ThrowingSupplier<OutputStream, IOException> factory;

  // The internal buffer
  private final byte buf[];

  // The live bytes in the buffer
  private int cnt;

  // The output stream to the swap-to resource
  private OutputStream out;

  private boolean closed;

  /**
   * Creates a {@code SwapOutputStream} with an internal buffer of 8 kilobytes, swapping to the
   * specified resource once the buffer overflows.
   *
   * @param swapTo The supplier of an outputstream to the swap-to resource (e.g. a swap file). The
   *     supplier will only be called upon if the internal buffer overflows.
   */
  public SwapOutputStream(ThrowingSupplier<OutputStream, IOException> swapTo) {
    this(swapTo, 8 * 1024);
  }

  /**
   * Creates a {@code SwapOutputStream} with an internal buffer of {@code bufSize} bytes, swapping
   * to the specified resource once the buffer overflows.
   *
   * @param swapTo The supplier of an outputstream to the swap-to resource (e.g. a swap file). The
   *     supplier will only be called upon if the internal buffer overflows.
   * @param bufSize The size in bytes of the internal buffer
   */
  public SwapOutputStream(ThrowingSupplier<OutputStream, IOException> swapTo, int bufSize) {
    this.factory = Check.notNull(swapTo, "swapTo").ok();
    this.buf = Check.that(bufSize).is(gt(), 0).ok(byte[]::new);
  }

  /** Writes the specified byte to this output stream. */
  @Override
  public final void write(int b) throws IOException {
    intercept(b);
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code>
   * to this output stream.
   */
  @Override
  public final void write(byte[] b, int off, int len) throws IOException {
    intercept(b, off, len);
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
   * bytes in the internal buffer will be flushed to the swap-to output stream and then its {@code
   * close()} method will be called. If the swap-to output stream had not been opened yet this
   * method does nothing. Any remaining bytes in the internal buffer will just stay there.
   *
   * <p>Because the swap-to output stream is lazily instantiated via a {@link ThrowingSupplier}, a
   * {@code SwapOutputStream} can safely be re-used even after it has been closed. A subsequent
   * {@code write} action will simply cause the swap-to output stream to be retrieved again from the
   * supplier.
   */
  @Override
  public void close() throws IOException {
    if (!closed) {
      if (out != null) {
        if (cnt > 0) {
          swap();
        }
        out.close();
      }
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
    out.write(buf, 0, cnt);
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
   * The {@link #write(int)} method does nothing but call this method. Subclasses must implement
   * this method so they can filter or modify the incoming byte before it is actually written to the
   * {@code SwapOutputStream}. Unless something exceptional happens, or the byte is to be discarded,
   * the {@code intercept} method should end with a call to one of the {@code doWrite} methods.
   *
   * @param b The byte
   * @throws IOException If an I/O error occurs
   */
  protected abstract void intercept(int b) throws IOException;

  /**
   * The {@link #write(byte[], int, int)} method does nothing but call this method. Subclasses must
   * implement this method so they can filter or modify the incoming bytes before they are actually
   * written to the {@code SwapOutputStream}. Unless something exceptional happens, or the byte
   * array is to be discarded, the {@code intercept} method should end with a call to one of the
   * {@code doWrite} methods.
   *
   * @param b The byte array
   * @throws IOException If an I/O error occurs
   */
  protected abstract void intercept(byte[] b, int off, int len) throws IOException;

  /**
   * Writes the specified byte to this output stream.
   *
   * @param b The byte
   * @throws IOException If an I/O error occurs
   */
  protected final void doWrite(int b) throws IOException {
    if (cnt == buf.length) {
      swap();
    }
    buf[cnt++] = (byte) b;
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code>
   * to this output stream.
   *
   * @param b the data.
   * @param off the start offset in the data.
   * @param len the number of bytes to write.
   * @throws IOException If an I/O error occurs
   */
  protected final void doWrite(byte[] b, int off, int len) throws IOException {
    // If the incoming byte array is bigger than the internal buffer we don't bother buffering it.
    // We flush the internal buffer and then write the byte array directly to the output stream
    if (len > buf.length) {
      swap();
      out.write(b, off, len);
    } else {
      if (cnt + len > buf.length) {
        swap();
      }
      System.arraycopy(b, off, buf, cnt, len);
      cnt += len;
    }
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
      to.write(buf, 0, cnt);
    }
  }

  /**
   * Returns the size of the internal buffer.
   *
   * @return The size of the internal buffer
   */
  protected final int bufferSize() {
    return buf.length;
  }

  private static void swap(OutputStream out, byte[] b, int off, int len) throws IOException {
    out.write(b, off, len);
  }
}
