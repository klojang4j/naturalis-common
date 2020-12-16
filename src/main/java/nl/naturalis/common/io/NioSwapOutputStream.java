package nl.naturalis.common.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static nl.naturalis.common.IOMethods.createTempFile;
import static nl.naturalis.common.check.CommonChecks.fileNotExists;
import static nl.naturalis.common.check.CommonChecks.gt;
import static nl.naturalis.common.check.CommonChecks.gte;
import static nl.naturalis.common.check.CommonChecks.no;
import static nl.naturalis.common.check.CommonGetters.length;

/**
 * A {@code SwapOutputStream} that uses {@code java.nio} classes to implement buffering and
 * swapping.
 *
 * @author Ayco Holleman
 */
public class NioSwapOutputStream extends SwapOutputStream {

  /**
   * Creates a new instance that swaps to an auto-generated temp file.
   *
   * @return A {@code FileSwapOutputStream} that swaps to an auto-generated temp file
   */
  public static NioSwapOutputStream newInstance() {
    try {
      return new NioSwapOutputStream(createTempFile(NioSwapOutputStream.class));
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
  public static NioSwapOutputStream newInstance(int bufSize) {
    try {
      return new NioSwapOutputStream(createTempFile(NioSwapOutputStream.class), bufSize);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  // The internal buffer
  protected final ByteBuffer buf;

  // FileChannel writing to he swap file
  private FileChannel chan;

  // The output stream to write the recalled data to and the target of all write actions following
  // the recall
  private OutputStream out;

  // Whether or not we had a buffer flow and thus had to swap to file
  private boolean swapped;

  // Whether or not data has been recalled - and hence write actions are now taking place on the
  // output stream that the recalled data was written to
  private boolean recalled;

  private boolean closed;

  /** See {@link SwapOutputStream#SwapOutputStream(File)}. */
  public NioSwapOutputStream(File swapFile) {
    this(swapFile, 64 * 1024);
  }

  /** See {@link SwapOutputStream#SwapOutputStream(File, int)}. */
  public NioSwapOutputStream(File swapFile, int bufSize) {
    super(swapFile);
    this.buf = Check.that(bufSize).is(gt(), 0).ok(ByteBuffer::allocateDirect);
  }

  /** Writes the specified byte to this output stream. */
  @Override
  public void write(int b) throws IOException {
    if (recalled) {
      out.write(b);
    } else {
      if (buf.position() + 1 > buf.capacity()) {
        flushBuffer();
      }
      buf.put((byte) b);
    }
  }

  /**
   * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code>
   * to this output stream.
   */
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    Check.notNull(b, "b").has(length(), gte(), off + len).given(off >= 0, len >= 0);
    if (recalled) {
      out.write(b, off, len);
      return;
    }
    // If the incoming byte array is bigger than the internal buffer we don't bother buffering it.
    // We flush the internal buffer and then write the byte array directly to the output stream
    if (len > buf.capacity()) {
      flushBuffer();
      chan.write(ByteBuffer.wrap(b, off, len));
    } else {
      if (buf.position() + len > buf.capacity()) {
        flushBuffer();
      }
      buf.put(b, off, len);
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
      if (chan != null) {
        if (buf.position() > 0) {
          flushBuffer();
        }
        chan.close();
      }
      closed = true;
    }
  }

  /** See {@link SwapOutputStream#recall(OutputStream)}. */
  public void recall(OutputStream target) throws IOException {
    Check.notNull(target);
    if (hasSwapped()) {
      readSwapFile(target);
    } else {
      readBuffer(target);
    }
  }

  @Override
  public boolean hasSwapped() {
    return chan != null;
  }

  final void readSwapFile(OutputStream target) throws IOException {
    close();
    try (FileChannel fc = FileChannel.open(swapFile.toPath(), READ)) {
      int sz = Math.min(2048, buf.capacity());
      byte[] tmp = new byte[sz];
      for (int i = fc.read(buf); i > 0; i = fc.read(buf)) {
        buf.flip();
        pipe(tmp, target);
        buf.clear();
      }
    }
  }

  final void readBuffer(OutputStream target) throws IOException {
    Check.notNull(target);
    Check.with(IOException::new, hasSwapped()).is(no(), "Already swapped");
    // The readBuffer in ArraySwapOutputStream (unintentionally) is idempotent; you can read the
    // buffer while you're still writing to it. buf.flip() would disrupt this however. For
    // predictability's sake, let's make this implementation of readBuffer idempotent as well.
    if (buf.position() > 0) {
      int pos = buf.position();
      int lim = buf.limit();
      buf.flip();
      int sz = Math.min(2048, buf.capacity());
      byte[] tmp = new byte[sz];
      pipe(tmp, target);
      buf.position(pos).limit(lim);
    }
  }

  private void pipe(byte[] tmp, OutputStream out) throws IOException {
    while (buf.hasRemaining()) {
      int len = Math.min(tmp.length, buf.remaining());
      buf.get(tmp, 0, len);
      out.write(tmp, 0, len);
    }
  }

  private void flushBuffer() throws IOException {
    if (chan == null) {
      Check.with(IOException::new, swapFile).is(fileNotExists());
      chan = FileChannel.open(swapFile.toPath(), CREATE_NEW, WRITE);
    }
    buf.flip();
    chan.write(buf);
    buf.clear();
  }
}
