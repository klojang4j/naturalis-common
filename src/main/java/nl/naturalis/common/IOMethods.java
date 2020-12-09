package nl.naturalis.common;

import java.io.*;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.io.SimpleFileSwapOutputStream;
import static nl.naturalis.common.check.CommonChecks.*;

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
   */
  public static void pipe(InputStream in, OutputStream out, int bufsize) {
    Check.notNull(in, "is");
    Check.notNull(out, "out");
    if (!(in instanceof BufferedInputStream || in instanceof ByteArrayInputStream)) {
      in = new BufferedInputStream(in, bufsize);
    }
    if (!(out instanceof BufferedOutputStream || out instanceof ByteArrayOutputStream)) {
      out = new BufferedOutputStream(out, bufsize);
    }
    byte[] data = new byte[bufsize];
    try {
      int n = in.read(data, 0, data.length);
      while (n != -1) {
        out.write(data, 0, n);
        out.flush();
        n = in.read(data, 0, data.length);
      }
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Creates a {@code File} object for a new, empty file in the file system's temp directory. The
   * file extension will be ".tmp".
   *
   * @see #createTempFile(String)
   * @return A {@code File} object for a new, empty file in the file system's temp directory
   * @throws IOException
   */
  public static File createTempFile() throws IOException {
    return createTempFile(".tmp");
  }

  /**
   * Creates a {@code File} object for a new, empty file in the file system's temp directory. Using
   * {@link File#createTempFile(String, String)} fails if temporary files are created in rapid
   * succession as it seems to use only System.currentTimeMillis() to invent a file name.
   *
   * @param extension The extension to append to the generated file name
   * @return A {@code File} object for a new, empty file in the file system's temp directory
   * @throws IOException
   */
  public static File createTempFile(String extension) throws IOException {
    StringBuilder sb = new StringBuilder(64);
    sb.append(System.getProperty("java.io.tmpdir"))
        .append('/')
        .append(SimpleFileSwapOutputStream.class.getSimpleName())
        .append(System.identityHashCode(new Object()))
        .append(System.currentTimeMillis())
        .append(extension);
    File f = new File(sb.toString());
    return Check.with(IOException::new, f).is(fileNotExists()).ok();
  }
}
