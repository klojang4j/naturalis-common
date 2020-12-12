package nl.naturalis.common.io;

import java.io.*;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * An extension of {@code OutputStream} that allows for data written to it to be recalled.
 *
 * @author Ayco Holleman
 */
public abstract class SwapOutputStream extends OutputStream {

  final File swapFile;

  /**
   * Creates a new instance with a 64 kB internal buffer, swapping to the specified file once the
   * buffer reaches full capacity.
   *
   * @param swapFile The file to write to once the internal buffer reaches full capacity
   * @param bufSize The size in bytes of the internal buffer
   */
  public SwapOutputStream(File swapFile) {
    this.swapFile = Check.notNull(swapFile).ok();
  }

  /**
   * Creates a new instance with an internal buffer of the specified size, swapping to the specified
   * file once the buffer reaches full capacity.
   *
   * @param swapFile The file to write to once the internal buffer reaches full capacity
   * @param bufSize The size in bytes of the internal buffer
   */
  public SwapOutputStream(File swapFile, int bufSize) {
    this(swapFile);
  }

  /**
   * Collects the data written to this instance and writes it to the specified output stream.
   *
   * @param output The output stream to which to write the data
   * @throws IOException If an I/O error occurs
   */
  public abstract void recall(OutputStream output) throws IOException;

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
   * Forces the internal buffer to be flushed to the swap file, even if the internal buffer has not
   * reached full capacity yet. You should not normally call this method as swapping is intended to
   * be taken care of transparently. But it might be useful for debug purposes (e.g. to inspect the
   * contents of a swap file).
   *
   * @throws IOException If an I/O error occurs
   */
  public abstract void swap() throws IOException;

  /**
   * Returns whether or not the {@code ArraySwapOutputStream} has started to write to the swap-to
   * output stream. You should not normally need to call this method as swapping is taken care of
   * automatically, but it could be used for debug or logging purposes.
   */
  public abstract boolean hasSwapped();

  final OutputStream open() throws IOException {
    Check.that(swapFile).is(fileNotExists());
    return new FileOutputStream(swapFile);
  }
}
