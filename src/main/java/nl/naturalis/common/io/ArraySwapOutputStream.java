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
 * A {@code SwapOutputStream} that uses a byte array as internal buffer.
 *
 * @author Ayco Holleman
 */
public class ArraySwapOutputStream extends SwapOutputStream {

  /**
   * Creates a new instance that swaps to an auto-generated temp file.
   *
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

  /**
   * Creates a new {@code ArraySwapOutputStream} with an internal buffer of 64 kB, swapping to the
   * specified file once its internal buffer fills up
   *
   * @param swapFile The swap file
   */
  public ArraySwapOutputStream(File swapFile) {
    this(swapFile, 64 * 1024);
  }

  /**
   * Creates a new {@code ArraySwapOutputStream} with an internal buffer of {@code bufSize} bytes,
   * swapping to the specified file once its internal buffer fills up
   *
   * @param swapFile The swap file
   * @param bufSize The size in bytes of the internal buffer
   */
  public ArraySwapOutputStream(File swapFile, int bufSize) {
    super(swapFile);
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
   * If the {@code ArraySwapOutputStream} has started writing to the swap file, any remaining bytes
   * in the internal buffer will be flushed to it and the output stream to the swap file will be
   * closed. Otherwise this method does nothing. Any remaining bytes in the internal buffer will
   * just stay there.
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
