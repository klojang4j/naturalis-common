package nl.naturalis.common.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingSupplier;
import static nl.naturalis.common.check.CommonChecks.fileNotExists;

public abstract class FileSwapOutputStream extends SwapOutputStream {

  private final File swapFile;

  /**
   * Creates a new instance that swaps to the specified file.
   *
   * @see SwapOutputStream#SwapOutputStream(ThrowingSupplier)
   * @param swapFile The file to write to once the internal buffer overflows
   */
  public FileSwapOutputStream(File swapFile) {
    super(createOutputStream(swapFile));
    this.swapFile = swapFile;
  }

  /**
   * Creates a new instance that swaps to the specified file.
   *
   * @see SwapOutputStream#SwapOutputStream(ThrowingSupplier, int)
   * @param swapFile The file to write to once the internal buffer overflows
   * @param bufSize The size in bytes of the internal buffer
   */
  public FileSwapOutputStream(File swapFile, int bufSize) {
    super(createOutputStream(swapFile), bufSize);
    this.swapFile = swapFile;
  }

  /**
   * Deletes the swap file.
   *
   * @throws IOException If an I/O error occurs
   */
  public void cleanup() {
    if (swapFile.exists()) {
      swapFile.delete();
    }
  }

  /**
   * Returns a {@code File} object for the swap file. Note that the swap file may not actually have
   * been created because the data written to the {@code FileSwapOutputStream} still reside in its
   * internal buffer.
   *
   * @returna A {@code File} object for the swap file
   */
  public final File getSwapFile() {
    return swapFile;
  }

  private static ThrowingSupplier<OutputStream, IOException> createOutputStream(File swapFile) {
    Check.notNull(swapFile).is(fileNotExists());
    return () -> new FileOutputStream(swapFile);
  }
}
