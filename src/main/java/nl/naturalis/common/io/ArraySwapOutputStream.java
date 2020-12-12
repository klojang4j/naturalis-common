package nl.naturalis.common.io;

import java.io.*;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.IOMethods.createTempFile;
import static nl.naturalis.common.IOMethods.pipe;
import static nl.naturalis.common.check.CommonChecks.fileExists;
import static nl.naturalis.common.check.CommonChecks.gt;
import static nl.naturalis.common.check.CommonChecks.no;

/**
 * Abstract base class for {@link SwapOutputStream} classes that use memory swapping to ensure data
 * can be read back. A {@code ArraySwapOutputStream} first fills up an internal buffer. If (and only
 * if) the buffer reaches full capacity, an underlying outstream to some persistent resource is
 * created to sink the data into. It is transparent to clients whether or not data has actually been
 * swapped out of memory. Clients can {@link #recall(OutputStream) recall} the data without having
 * to know whether it came from the internal buffer or persistent storage.
 *
 * <p>{@code ArraySwapOutputStream} basically is a {@link BufferedOutputStream}, except that the
 * underlying output stream is lazily instantiated and that you can read back what you wrote to it.
 * Therefore you don't anything from wrapping a {@code ArraySwapOutputStream} into a {@code
 * BufferedOutputStream}.
 *
 * @author Ayco Holleman
 */
public class ArraySwapOutputStream extends SwapOutputStream {

  /**
   * Creates a new instance that swaps to an auto-generated temp file.
   *
   * @see ArraySwapOutputStream#SwapOutputStream(ThrowingSupplier)
   * @return A {@code FileSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static ArraySwapOutputStream newInstance() {
    try {
      return new ArraySwapOutputStream(createTempFile());
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
   * @return A {@code FileSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static ArraySwapOutputStream newInstance(int bufSize) {
    try {
      return new ArraySwapOutputStream(createTempFile(), bufSize);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  // The internal buffer
  private final byte buf[];

  // The number live bytes in the buffer
  private int cnt;

  // The output stream to the swap file
  private OutputStream out;

  private boolean closed;

  public ArraySwapOutputStream(File swapFile) {
    this(swapFile, 64 * 1024);
  }

  public ArraySwapOutputStream(File swapFile, int bufSize) {
    super(swapFile, bufSize);
    this.buf = Check.that(bufSize).is(gt(), 0).ok(byte[]::new);
  }

  /** Writes the specified byte to this output stream. */
  @Override
  public void write(int b) throws IOException {
    if (cnt == buf.length) {
      swap();
    }
    buf[cnt++] = (byte) b;
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code>
   * to this output stream.
   */
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
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
   * Calls {@link OutputStream#flush() flush()} on the swap-to output stream if the {@code
   * ArraySwapOutputStream} has started writing to it. Otherwise this method does nothing.
   */
  @Override
  public void flush() throws IOException {
    if (out != null) {
      out.flush();
    }
  }

  /** See {@link SwapOutputStream#recall(OutputStream)}. */
  public void recall(OutputStream output) throws IOException {
    Check.notNull(output);
    if (hasSwapped()) {
      Check.with(IOException::new, swapFile).is(fileExists(), "Swap file gone");
      close();
      try (FileInputStream fis = new FileInputStream(swapFile)) {
        pipe(fis, output, buf.length);
      }
    } else {
      readBuffer(output);
    }
  }

  /**
   * If the {@code ArraySwapOutputStream} has started writing to the swap-to output stream, any
   * remaining bytes in the internal buffer will be flushed to the swap-to output stream and then
   * its {@code close()} method will be called. If the swap-to output stream had not been opened yet
   * this method does nothing. Any remaining bytes in the internal buffer will just stay there.
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

  /** See {@link SwapOutputStream#swap()}. */
  public final void swap() throws IOException {
    if (out == null) {
      out = open();
    }
    out.write(buf, 0, cnt);
    cnt = 0;
  }

  /** See {@link SwapOutputStream#hasSwapped()}. */
  public final boolean hasSwapped() {
    return out != null;
  }

  /**
   * Copies the contents of the internal buffer to the specified output stream. An IOException is
   * thrown if the {@code ArraySwapOutputStream} has already started writing to the swap-to
   * outputstream.
   *
   * @param to The output stream to which to copy the contents of the internal buffer
   * @throws IOException If an I/O error occurs
   * @throws IllegalStateException If the swap has already taken place
   */
  final void readBuffer(OutputStream to) throws IOException {
    Check.notNull(to);
    Check.with(IOException::new, hasSwapped()).is(no(), "Already swapped");
    if (cnt > 0) {
      to.write(buf, 0, cnt);
    }
  }

  /**
   * Returns the size of the internal buffer.
   *
   * @return The size of the internal buffer
   */
  final int bufferSize() {
    return buf.length;
  }
}
