package nl.naturalis.common.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import nl.naturalis.common.check.Check;

/**
 * An {@code OutputStream} that allows allows you to read back the data that were written to it.
 * Data written to a {@code SwapOutputStream} first fills up an internal buffer. If (and only if)
 * the buffer reaches full capacity, a swap file is created to sink the data into. This ensures that
 * whatever the total size of the data written to the {@code SwapOutputStream}, it can always be
 * recalled. It is transparent to clients whether or not data has actually been swapped out of
 * memory. Clients can recall the data without having to know whether it came from the internal
 * buffer or from the swap file.
 *
 * <p>After you recalled the data you can continue writing data to the {@code SwapOutputStream}. It
 * has in effect become a {@link BufferedOutputStream} around the output stream that you passed to
 * the {@link #recall(OutputStream) recall} method. For this and other reasons it doesn't make sense
 * to wrap a {@code SwapOutputStream} into a {@code BufferedOutputStream}.
 *
 * <p>{@code SwapOutputStream} and its subclasses are not thread-safe. Except for the {@link
 * #cleanup()} method als method calls need to be synchronized using a lock on the entire instance
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
   * Collects the data written to this instance and writes it to the specified output stream.
   *
   * @param target The output stream to which to write the data
   * @throws IOException If an I/O error occurs
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
   * Closes the {@link OutputStream} or {@link FileChannel} (depending on the implementation)
   * writing to the swap file. It will not close the output stream specified by the {@link
   * #recall(OutputStream)} method. The {@code SwapOutputStream} will "swallow" this output stream
   * to turn itself into a {@code BufferedOutputStream} around the output stream. However you don't
   * have it make use of this, so closing the output stream is left to the user.
   */
  public abstract void close() throws IOException;

  /**
   * Returns whether or not a swap file was created. You should not normally need to call this
   * method as swapping is taken care of automatically, but it could be useful for debug or logging
   * purposes.
   */
  public abstract boolean hasSwapped();
}
