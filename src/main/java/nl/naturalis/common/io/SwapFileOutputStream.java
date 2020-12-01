package nl.naturalis.common.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.IOMethods.*;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * A {@link SwapOutputStream} that swaps to file once its internal buffer overflows. The {@link
 * FileOutputStream} created for the swap file is already wrapped into a {@link
 * BufferedOutputStream}, so it's pointless to wrap the {@code SwapFileOutputStream} itself into a
 * {@code BufferedOutputStream}.
 *
 * @see SwapOutputStream
 * @author Ayco Holleman
 */
public class SwapFileOutputStream extends SwapOutputStream {

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer will be 4 kilobytes. The {@code BufferedOutputStream} created for the swap file will
   * have a 512-byte buffer. (In other words, the {@code SwapFileOutputStream} first fills up an
   * internal buffer of 4 kilobytes, and then starts writing to the swap file in chunks of 512
   * bytes.)
   *
   * @return A {@code SwapFileOutputStream} that swaps to an auto-generated temp file
   * @throws IOException If an I/O error occurs
   */
  public static SwapFileOutputStream newInstance() throws IOException {
    return new SwapFileOutputStream(tempFile());
  }

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer is specified through the {@code treshold} parameter. The {@code BufferedOutputStream}
   * created for the swap file will have a 512-byte buffer.
   *
   * @param treshold The size in bytes of the internal buffer
   * @return A {@code SwapFileOutputStream} that swaps to an auto-generated temp file
   * @throws IOException If an I/O error occurs
   */
  public static SwapFileOutputStream newInstance(int treshold) throws IOException {
    return new SwapFileOutputStream(tempFile(), treshold);
  }

  /**
   * Creates a new instance that swaps to an auto-generated temp file. The size of the internal
   * buffer is specified through the {@code treshold} parameter. The {@code BufferedOutputStream}
   * created for the swap file will have a size of {@code bufSize} bytes.
   *
   * @param treshold The size in bytes of the internal buffer
   * @param bufSize The buffer size of the {@code BufferedOutputStream} created for the swap file
   * @return A {@code SwapFileOutputStream} that swaps to an auto-generated temp file
   * @throws IOException If an I/O error occurs
   */
  public static SwapFileOutputStream newInstance(int treshold, int bufSize) throws IOException {
    return new SwapFileOutputStream(tempFile(), treshold, bufSize);
  }

  private File swapFile;

  /**
   * Creates a new instance that swaps to the specified file. The size of the internal buffer will
   * be 4 kilobytes. The {@code BufferedOutputStream} created for the swap file will have a 512-byte
   * buffer. (In other words, the {@code SwapFileOutputStream} first fills up an internal buffer of
   * 4 kilobytes, and then starts writing to the swap file in chunks of 512 bytes.)
   *
   * @param swapFile The file to write to once the internal buffer overflows
   * @throws IOException If an I/O error occurs
   */
  public SwapFileOutputStream(File swapFile) throws IOException {
    super(swapTo(swapFile, 512));
  }

  /**
   * Creates a new instance that swaps to the specified file. The size of the internal buffer is
   * specified through the {@code treshold} parameter. The {@code BufferedOutputStream} created for
   * the swap file will have a 512-byte buffer.
   *
   * @param swapFile The file to write to once the internal buffer overflows
   * @param treshold The size in bytes of the internal buffer
   * @throws IOException
   */
  public SwapFileOutputStream(File swapFile, int treshold) throws IOException {
    super(swapTo(swapFile, 512), treshold);
    this.swapFile = swapFile;
  }

  /**
   * Creates a new instance that swaps to the specified file. The size of the internal buffer is
   * specified through the {@code treshold} parameter. The {@code BufferedOutputStream} created for
   * the swap file will have a size of {@code bufSize} bytes.
   *
   * @param swapFile The file to write to once the internal buffer overflows
   * @param treshold The size in bytes of the internal buffer
   * @param bufSize The buffer size of the {@code BufferedOutputStream} created for the swap file
   * @throws IOException If an I/O error occurs
   */
  public SwapFileOutputStream(File swapFile, int treshold, int bufSize) throws IOException {
    super(swapTo(swapFile, bufSize), treshold);
    this.swapFile = swapFile;
  }

  public void collect(OutputStream output) throws IOException {
    if (hasSwapped()) {
      Check.that(swapFile, IOException::new)
          .is(fileExists(), "Swap file gone missing: %s", swapFile.getPath());
      close();
      try (FileInputStream fis = new FileInputStream(swapFile)) {
        pipe(fis, output, 2048);
      }
    } else {
      writeBuffer(output);
    }
  }

  /**
   * Deletes the swap file and closes the {@code SwapFileOutputStream}.
   *
   * @throws IOException
   */
  public void cleanupAndClose() throws IOException {
    close();
    if (swapFile.exists()) {
      swapFile.delete();
    }
  }

  private static OutputStream swapTo(File swapFile, int bufSize) throws IOException {
    Check.that(swapFile, "Swap file").is(fileNotExists());
    return new BufferedOutputStream(new FileOutputStream(swapFile), bufSize);
  }

  /**
   * NB Creating a temp file using File.createTempFile is not satisfactory as the swap files may be
   * created in rapid succession while File.createTempFile seems to use System.currentTimeMillis()
   * to invent a file name.
   */
  private static File tempFile() {
    StringBuilder sb = new StringBuilder(64);
    sb.append(System.getProperty("java.io.tmpdir"));
    sb.append('/');
    sb.append(System.identityHashCode(new Object()));
    sb.append('.');
    sb.append(System.currentTimeMillis());
    sb.append('.');
    sb.append(SwapFileOutputStream.class.getSimpleName().toLowerCase());
    sb.append(".swp");
    return new File(sb.toString());
  }
}
