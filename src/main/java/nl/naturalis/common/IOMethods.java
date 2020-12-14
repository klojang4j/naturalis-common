package nl.naturalis.common;

import java.io.*;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.fileNotExists;

/**
 * I/O-related methods.
 *
 * @author Ayco Holleman
 */
public class IOMethods {

  private IOMethods() {}

  /**
   * Reads all bytes from the specified input stream and writes them to the specified output stream.
   * Bytes are read and written in chunks of {@code bufsize} length. Both the input stream and the
   * output stream are first wrapped into a {@link BufferedInputStream} c.q. {@link
   * BufferedOutputStream} if they aren't already instances of those classes. Neither the input
   * stream nor the output stream is closed when done.
   *
   * @param in The input stream
   * @param out The output stream
   * @param bufsize The buffer size
   * @throws IOException If an I/O error occurs
   */
  public static void pipe(InputStream in, OutputStream out, int bufsize) throws IOException {
    Check.notNull(in, "in");
    Check.notNull(out, "out");
    if (!(in instanceof BufferedInputStream || in instanceof ByteArrayInputStream)) {
      in = new BufferedInputStream(in, bufsize);
    }
    if (!(out instanceof BufferedOutputStream || out instanceof ByteArrayOutputStream)) {
      out = new BufferedOutputStream(out, bufsize);
    }
    byte[] data = new byte[bufsize];
    int n = in.read(data, 0, data.length);
    while (n != -1) {
      out.write(data, 0, n);
      out.flush();
      n = in.read(data, 0, data.length);
    }
  }

  /**
   * Creates a {@code File} object for a new, empty file in the file system's temp directory. The
   * file extension will be ".tmp".
   *
   * @return A {@code File} object for a new, empty file in the file system's temp directory
   * @throws IOException If an I/O error occurs
   */
  public static File createTempFile() throws IOException {
    return createTempFile(IOMethods.class);
  }

  /**
   * Creates a {@code File} object for a new, empty file in the file system's temp directory.
   *
   * @param extension The extension to append to the generated file name
   * @return A {@code File} object for a new, empty file in the file system's temp directory
   * @throws IOException If an I/O error occurs
   */
  public static File createTempFile(String extension) throws IOException {
    return createTempFile(IOMethods.class, extension);
  }

  /**
   * Creates a {@code File} object for a new, empty file in the file system's temp directory. The
   * file extension will be ".tmp".
   *
   * @param requester The class requesting the temp file (simple name will become part of the file
   *     name)
   * @return A {@code File} object for a new, empty file in the file system's temp directory
   * @throws IOException If an I/O error occurs
   */
  public static File createTempFile(Class<?> requester) throws IOException {
    return createTempFile(requester, ".tmp");
  }

  /**
   * Creates a {@code File} object for a new, empty file in the file system's temp directory. Using
   * {@link File#createTempFile(String, String)} may fail if temporary files are created in rapid
   * succession as it seems to use only System.currentTimeMillis() to invent a file name. This
   * method has a 100% chance of generating a unique path.
   *
   * @param requester The class requesting the temp file (simple name will become part of the file
   *     name)
   * @param extension The extension to append to the generated file name
   * @return A {@code File} object for a new, empty file in the file system's temp directory
   * @throws IOException If an I/O error occurs
   */
  public static File createTempFile(Class<?> requester, String extension) throws IOException {
    String path =
        StringMethods.append(
                new StringBuilder(64),
                System.getProperty("java.io.tmpdir"),
                "/",
                requester.getSimpleName(),
                tempCount++,
                System.currentTimeMillis(),
                extension)
            .toString();
    File f = new File(path);
    return Check.with(IOException::new, f).is(fileNotExists()).ok();
  }

  private static int tempCount = 100000;
}
