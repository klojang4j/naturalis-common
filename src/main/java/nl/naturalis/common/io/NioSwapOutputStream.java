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
 * A {@code SwapOutputStream} implementation based on the {@code java.nio} package. This
 * implementation might give you a performance benefit when writing large amounts of data.
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
      return new NioSwapOutputStream(tempFile());
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
      return new NioSwapOutputStream(tempFile(), bufSize);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  // The internal buffer
  protected final ByteBuffer buf;
  // Temporary storage when flushing the buffer to an output stream
  private final byte[] tmp;

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

  /** See {@link SwapOutputStream#SwapOutputStream(File)}. */
  public NioSwapOutputStream(File swapFile) {
    this(swapFile, 64 * 1024);
  }

  /** See {@link SwapOutputStream#SwapOutputStream(File, int)}. */
  public NioSwapOutputStream(File swapFile, int bufSize) {
    super(swapFile);
    this.buf = Check.that(bufSize).is(gt(), 0).ok(ByteBuffer::allocateDirect);
    tmp = new byte[Math.min(2048, buf.capacity())];
  }

  /** Writes the specified byte to this output stream. */
  @Override
  public void write(int b) throws IOException {
    if (recalled) {
      out.write(b);
      return;
    }
    if (buf.position() + 1 > buf.capacity()) {
      sendToSwapFile();
    }
    buf.put((byte) b);
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
      sendToSwapFile();
      chan.write(ByteBuffer.wrap(b, off, len));
    } else {
      if (buf.position() + len > buf.capacity()) {
        sendToSwapFile();
      }
      buf.put(b, off, len);
    }
  }

  @Override
  public void flush() throws IOException {
    if (dataInBuffer()) {
      if (recalled) {
        sendTo(out);
        out.flush();
      } else if (swapped) {
        sendToSwapFile();
      }
    }
  }

  @Override
  public void forceFlush() throws IOException {
    if (dataInBuffer()) {
      if (recalled) {
        sendTo(out);
        out.flush();
      } else if (swapped) {
        sendToSwapFile();
        chan.force(false);
      }
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
    if (chan != null && chan.isOpen()) {
      chan.close();
    }
    if (recalled) {
      if (dataInBuffer()) {
        sendTo(out);
        out.flush();
      }
    }
  }

  /** See {@link SwapOutputStream#recall(OutputStream)}. */
  public void recall(OutputStream target) throws IOException {
    Check.notNull(target);
    Check.with(IOException::new, recalled).is(no(), "Data already recalled");
    prepareRecall();
    OutputStream wrapped = wrap(target);
    if (swapped) {
      if (dataInBuffer()) {
        sendToSwapFile();
      }
      chan.close();
      try (FileChannel fc = FileChannel.open(swapFile.toPath(), READ)) {
        for (int i = fc.read(buf); i > 0; i = fc.read(buf)) {
          sendTo(wrapped);
        }
      }
    } else if (dataInBuffer()) {
      sendTo(wrapped);
    }
    this.out = target;
    this.recalled = true;
  }

  @Override
  public boolean hasSwapped() {
    return swapped;
  }

  // Allow subclasses to flush pending output to the internal buffer.
  /** @throws IOException */
  void prepareRecall() throws IOException {}

  // Allow subclasses to override this method in order to wrap the recall output stream in another
  // outstream that does the reverse of their write actions (zip/unzip)
  OutputStream wrap(OutputStream target) {
    return target;
  }

  boolean dataRecalled() {
    return recalled;
  }

  private boolean dataInBuffer() {
    return buf.position() > 0;
  }

  // Empties the contents of the buffer into the swap file
  private void sendToSwapFile() throws IOException {
    if (chan == null) {
      Check.with(IOException::new, swapFile).is(fileNotExists());
      chan = FileChannel.open(swapFile.toPath(), CREATE_NEW, WRITE);
      swapped = true;
    }
    buf.flip();
    chan.write(buf);
    buf.clear();
  }

  // Empties the contents of the buffer into the post-recall output stream
  private void sendTo(OutputStream out) throws IOException {
    buf.flip();
    while (buf.hasRemaining()) {
      int len = Math.min(tmp.length, buf.remaining());
      buf.get(tmp, 0, len);
      out.write(tmp, 0, len);
    }
    buf.clear();
  }

  private static File tempFile() throws IOException {
    return createTempFile(NioSwapOutputStream.class, false);
  }
}
