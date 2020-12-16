package nl.naturalis.common.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import nl.naturalis.common.check.Check;

/**
 * An extension of {@code OutputStream} that allows for data written to it to be recalled. Data
 * written to a {@code SwapOutputStream} first fills up an internal buffer. If (and only if) the
 * buffer reaches full capacity, a swap file is created to sink the data into. This ensures that
 * whatever the total size of the data written to a {@code SwapOutputStream}, it can always be
 * recalled. It is transparent to clients whether or not data has actually been swapped out of
 * memory. Clients can recall the data without having to know whether it came from the internal
 * buffer or from the swap file.
 *
 * <p>In many respects {@code SwapOutputStream} and its subclasses work just like a {@link
 * BufferedOutputStream}. Therefore it doesn't make much sense to wrap a {@code SwapOutputStream}
 * into a {@code BufferedOutputStream}.
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
   * Deletes the swap file. Can be called if, after the data has been recalled, the swap file is no
   * longer needed. You might also want to call this method in the {@code catch} block of an
   * exception.
   */
  public void cleanup() {
    if (swapFile.exists()) {
      swapFile.delete();
    }
  }

  /**
   * Returns whether or not a swap file was created. You should not normally need to call this
   * method as swapping is taken care of automatically, but it could be used for debug or logging
   * purposes.
   */
  public abstract boolean hasSwapped();
}
