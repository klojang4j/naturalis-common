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
 * are done writing data, clients can {@link #collect(OutputStream) collect} the data again without
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
public abstract class SwapOutputStream extends OutputStream {

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
    byte[] filtered = receive(b);
    write(filtered, 0, filtered.length);
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code>
   * to this output stream. If this causes an overflow of the internal buffer, the buffer is flushed
   * to the underlying output stream and all subsequent write actions will be forwarded to the
   * underlying output stream.
   */
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    byte[] filtered = receive(b, off, len);
    int x = filtered.length;
    if (x > buf.length) {
      swap();
      send(out, filtered, 0, x);
    } else {
      if (cnt + x > buf.length) {
        swap();
      }
      System.arraycopy(filtered, 0, buf, cnt, x);
      cnt += x;
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
    send(out, buf, 0, cnt);
    cnt = 0;
  }

  /**
   * Returns whether or not the {@code SwapOutputStream} has started to write to the swap-to output
   * stream. You should not normally need to call this method as swapping is meant to be taken care
   * of automatically, but it could be used for debug or logging purposes.
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
   * Copies the contents of the internal buffer to the specified output stream. Subclasses need this
   * method to implement the {@link #collect(OutputStream) collect} method for the case that the
   * data written to the {@code SwapOutputStream} still resides in the internal buffer (no swapping
   * was necessary). An IOException is thrown if the {@code SwapOutputStream} has already started
   * writing to the swap-to outputstream. Otherwise this method can be called as often as desired.
   *
   * @param to The output stream to which to copy the contents of the internal buffer
   * @throws IOException If an I/O error occurs or if the {@code SwapOutputStream} has already
   *     started writing to the swap-to outputstream.
   */
  protected final void readBuffer(OutputStream to) throws IOException {
    Check.notNull(to);
    Check.that(out, IOException::new).is(nullPointer(), "Already swapped");
    if (cnt > 0) {
      send(to, buf, 0, cnt);
    }
  }

  /**
   * Processes the byte on its way into the internal buffer. Can be overridden by subclasses to
   * apply apply a filtering mechanism like compression. The {@code SwapOutputStream} class simply
   * returns a on-element byte array containing the byte.
   *
   * @param b The byte
   * @return A byte array resulting from the transformation of the byte
   * @throws IOException If an I/O error occurs
   */
  protected byte[] receive(int b) throws IOException {
    return new byte[] {(byte) (b & 0xff)};
  }

  /**
   * Processes the bytes on their way into the internal buffer. Can be overridden by subclasses to
   * apply apply a filtering mechanism like compression. The {@code SwapOutputStream} class simply
   * returns the byte array if {@code off} equals 0 and {@code len} equals {@code b.length}, or the
   * sub-array specified by {@code off} and {@code len}.
   *
   * @param b The byte array
   * @param off The offset in the byte array
   * @param len The length of the sub-array
   * @return A byte array resulting from the transformation of the byte
   * @throws IOException
   */
  protected byte[] receive(byte[] b, int off, int len) throws IOException {
    if (off == 0 && len == b.length) {
      return b;
    }
    byte[] b2 = new byte[len];
    System.arraycopy(b, off, b2, 0, len);
    return b2;
  }

  /**
   * Writes the specified byte array to the specified output stream. Can be overridden by subclasses
   * to process the bytes on their way out of the internal buffer. The implementation of {@code
   * SwapOutpuStream} simply executes <code>out.write(b, off, len)</code>
   *
   * @param out The output stream
   * @param b The byte array
   * @param off The offset in the byte array
   * @param len The number of bytes to write
   * @throws IOException If an I/O error occurs
   */
  protected void send(OutputStream out, byte[] b, int off, int len) throws IOException {
    out.write(b, off, len);
  }
}
