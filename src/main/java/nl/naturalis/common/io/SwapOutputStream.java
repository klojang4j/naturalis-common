package nl.naturalis.common.io;

import nl.naturalis.common.check.Check;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * An {@code OutputStream} that can read back the data written to it. Data written to a {@code
 * SwapOutputStream} first fills up an internal buffer. If (and only if) the buffer reaches full
 * capacity, a swap file is created to sink the data into. Thus, whatever the amount of data written
 * to the {@code SwapOutputStream}, it can always be recalled. It is transparent to clients whether
 * data has actually been swapped out of memory.
 *
 * <p>{@code SwapOutputStream} and its subclasses are not thread-safe.
 *
 * <p>It is pointless to wrap a {@code SwapOutputStream} into a {@code BufferedOutputStream}.
 *
 * @author Ayco Holleman
 */
public abstract class SwapOutputStream extends OutputStream {

  final File swapFile;

  /**
   * Creates a new {@code SwapOutputStream} with an internal buffer of 64 kB, swapping to the
   * specified file the buffer starts overflowing
   *
   * @param swapFile The swap file
   */
  public SwapOutputStream(File swapFile) {
    this.swapFile = Check.notNull(swapFile).ok();
  }

  /**
   * @param swapFile The swap file
   * @param bufSize The size in bytes of the internal buffer
   */
  public SwapOutputStream(File swapFile, int bufSize) {
    this.swapFile = Check.notNull(swapFile).ok();
  }

  /**
   * Collects the data written to this instance and writes it to the specified output stream. This
   * method may be called only once. Subsequent calls result in an {@link IOException}. You can
   * still write data to the {@code SwapOutputStream} after having called this method. The {@code
   * SwapOutputStream} tacitly closes the output stream to the swap file (if it had to be created)
   * and turns itself into a {@code BufferedOutputStream} around the specified output stream.
   *
   * @param target The output stream to which to write the data
   * @throws IOException If you call this method more than once or if an I/O error occurs
   */
  public abstract void recall(OutputStream target) throws IOException;

  /**
   * Deletes the swap file, if it was created. Can be called if, after the data has been recalled,
   * the swap file is no longer needed. You might also want to call this method in the {@code catch}
   * block of an exception.
   */
  public void cleanup() {
    if (swapFile.exists()) {
      swapFile.delete();
    }
  }

  /**
   * If a swap file had to be created and no recall has taken place yet, this method flushes the
   * output stream to the swap file. After the data has been recalled this method flushes the output
   * stream passed in through the {@link #recall(OutputStream) recall} method. The {@code java.nio}
   * implementations of {@code SwapOutputStream} will <i>not</i> call {@link
   * FileChannel#force(boolean) force} on the FileChannel writing to the swap file. Call {@link
   * #forceFlush()} if necessary.
   */
  public abstract void flush() throws IOException;

  /**
   * For the non-{@code java.nio} implementations of {@code SwapOutputStream} this method behaves
   * exactly like {@link #flush()}. For the {@code java.nio} implementations of {@code
   * SwapOutputStream} this method will call ileChannel#force(boolean) force(false)} on the
   * FileChannel writing to the swap file.
   *
   * @throws IOException
   */
  public abstract void forceFlush() throws IOException;

  /**
   * If created and still open, this method closes the {@link OutputStream} or {@link FileChannel}
   * writing to the swap file. Note that the {@link #recall(OutputStream) recall} method tacitly
   * closes the {@code OutputStream} c.q. {@code FileChannel} before it starts reading the swap
   * file. The {@code close} method will not close the output stream passed in through the {@code
   * recall} method. Thus you can safely keep using that output stream outside the
   * <i>try-with-resources</i> block for a {@code SwapOutputStream}.
   */
  public abstract void close() throws IOException;

  /**
   * Returns whether or not a swap file was created. You should not normally need to call this
   * method as swapping is taken care of automatically, but it could be useful for debug or logging
   * purposes.
   */
  public abstract boolean hasSwapped();
}
