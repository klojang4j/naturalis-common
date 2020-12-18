package nl.naturalis.common.io;

import java.io.*;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.IOMethods.createTempFile;
import static nl.naturalis.common.IOMethods.pipe;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.length;

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
      return new ArraySwapOutputStream(createTempFile(ArraySwapOutputStream.class));
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
      return new ArraySwapOutputStream(createTempFile(ArraySwapOutputStream.class), bufSize);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  // The internal buffer
  private final byte buf[];

  // The number wriiten to the buffer
  private int cnt;

  // The output stream to the swap file before the recall, or the output stream that the recalled
  // data was written to
  private OutputStream out;

  // Whether or not we had a buffer flow and thus had to swap to file
  private boolean swapped;

  // Whether or not data has been recalled - and hence write actions are now taking place on the
  // output stream that the recalled data was written to
  private boolean recalled;

  /** See {@link SwapOutputStream#SwapOutputStream(File)}. */
  public ArraySwapOutputStream(File swapFile) {
    this(swapFile, 64 * 1024);
  }

  /** See {@link SwapOutputStream#SwapOutputStream(File, int)}. */
  public ArraySwapOutputStream(File swapFile, int bufSize) {
    super(swapFile);
    this.buf = Check.that(bufSize).is(gt(), 0).ok(byte[]::new);
  }

  /** Writes the specified byte to this output stream. */
  @Override
  public void write(int b) throws IOException {
    if (cnt == buf.length) {
      flushBuffer();
    }
    buf[cnt++] = (byte) b;
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code>
   * to this output stream.
   */
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    Check.notNull(b, "b").has(length(), gte(), off + len).given(off >= 0, len >= 0);
    // If the incoming byte array is bigger than the internal buffer we don't bother buffering it.
    // We flush the internal buffer and then write the byte array directly to the output stream
    if (len > buf.length) {
      flushBuffer();
      out.write(b, off, len);
    } else {
      if (cnt + len > buf.length) {
        flushBuffer();
      }
      System.arraycopy(b, off, buf, cnt, len);
      cnt += len;
    }
  }

  @Override
  public void flush() throws IOException {
    if (dataInBuffer() && (swapped || recalled)) {
      flushBuffer();
      out.flush();
    }
  }

  @Override
  public void forceFlush() throws IOException {
    flush();
  }

  /**
   * If the {@code ArraySwapOutputStream} has started writing to the swap file, any remaining bytes
   * in the internal buffer will be flushed to it and the output stream to the swap file will be
   * closed. Otherwise this method does nothing. Any remaining bytes in the internal buffer will
   * just stay there.
   */
  @Override
  public void close() throws IOException {
    if (recalled) {
      if (dataInBuffer()) {
        flushBuffer();
      }
      out.flush();
    } else if (swapped) {
      out.close();
    }
  }

  /** See {@link SwapOutputStream#hasSwapped()}. */
  public final boolean hasSwapped() {
    return swapped;
  }

  /** See {@link SwapOutputStream#recall(OutputStream)}. */
  public void recall(OutputStream target) throws IOException {
    Check.notNull(target);
    Check.with(IOException::new, recalled).is(no(), "Data already recalled");
    prepareRecall();
    OutputStream wrapped = wrap(target);
    if (swapped) {
      if (dataInBuffer()) {
        flushBuffer();
      }
      out.close();
      try (FileInputStream fis = new FileInputStream(swapFile)) {
        pipe(fis, wrapped, buf.length);
      }
    } else if (dataInBuffer()) {
      wrapped.write(buf, 0, cnt);
      cnt = 0;
    }
    this.out = target;
    this.recalled = true;
  }

  // Allow subclasses to flush pending output to the internal buffer.
  /** @throws IOException */
  void prepareRecall() throws IOException {}

  // Allow subclasses to wrap the recall output stream in an outstream that does the reverse of
  // their write actions (zip/unzip)
  OutputStream wrap(OutputStream target) {
    return target;
  }

  boolean dataRecalled() {
    return recalled;
  }

  private boolean dataInBuffer() {
    return cnt > 0;
  }

  private void flushBuffer() throws IOException {
    if (out == null) {
      out = Check.with(IOException::new, swapFile).is(fileNotExists()).ok(FileOutputStream::new);
      swapped = true;
    }
    out.write(buf, 0, cnt);
    cnt = 0;
  }
}
