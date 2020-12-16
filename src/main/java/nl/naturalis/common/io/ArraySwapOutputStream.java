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

  /**
   * Calls {@link OutputStream#flush() flush()} on the swap-to output stream if the {@code
   * ArraySwapOutputStream} has started writing to it. Otherwise this method does nothing.
   */
  @Override
  public void flush() throws IOException {
    if (swapped || recalled) {
      flushBuffer();
      out.flush();
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
    if (recalled) {
      if (cnt > 0) {
        flushBuffer();
      }
      out.flush();
    } else if (swapped) {
      out.close();
    }
  }

  final void flushBuffer() throws IOException {
    if (out == null) {
      out = openSwapFile();
      swapped = true;
    }
    out.write(buf, 0, cnt);
    cnt = 0;
  }

  /** See {@link SwapOutputStream#hasSwapped()}. */
  public final boolean hasSwapped() {
    return swapped;
  }

  /** See {@link SwapOutputStream#recall(OutputStream)}. */
  public void recall(OutputStream target) throws IOException {
    Check.notNull(target);
    Check.with(IOException::new, recalled).is(no(), "Already recalled");
    prepareRecall();
    if (swapped) {
      if (cnt > 0) {
        flushBuffer();
      }
      out.close();
      readSwapFile(wrapRecallOutputStream(target));
    } else {
      readBuffer(wrapRecallOutputStream(target));
    }
    this.out = target;
    this.recalled = true;
  }

  /** @throws IOException */
  void prepareRecall() throws IOException {}

  OutputStream wrapRecallOutputStream(OutputStream target) {
    return target;
  }

  void readSwapFile(OutputStream target) throws IOException, FileNotFoundException {
    try (FileInputStream fis = new FileInputStream(swapFile)) {
      pipe(fis, target, buf.length);
    }
  }

  void readBuffer(OutputStream target) throws IOException {
    if (cnt > 0) {
      target.write(buf, 0, cnt);
      cnt = 0;
    }
  }

  boolean recalled() {
    return recalled;
  }

  private OutputStream openSwapFile() throws IOException {
    Check.with(IOException::new, swapFile).is(fileNotExists());
    return new FileOutputStream(swapFile);
  }
}
