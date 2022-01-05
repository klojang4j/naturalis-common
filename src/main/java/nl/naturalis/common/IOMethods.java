package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.unsafe.UnsafeByteArrayOutputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static nl.naturalis.common.check.CommonChecks.*;

/**
 * I/O-related methods.
 *
 * @author Ayco Holleman
 */
public class IOMethods {

  public static final String INVALID_PATH = "Invalid path: \"${0}\"";

  private IOMethods() {}

  public static String toString(Class<?> clazz, String path) {
    return toString(clazz, path, 512);
  }

  public static String toString(Class<?> clazz, String path, int chunkSize) {
    Check.notNull(clazz, "clazz");
    Check.that(path, "path").isNot(empty());
    try (InputStream in = clazz.getResourceAsStream(path)) {
      Check.that(in).is(notNull(), INVALID_PATH, path);
      return toString(in, chunkSize);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  public static String toString(Path path) {
    Check.notNull(path).has(Path::isAbsolute, yes(), "Path must be absolute");
    try (FileInputStream fis = new FileInputStream(path.toFile())) {
      return toString(fis);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  public static String toString(InputStream in) {
    return toString(in, 512);
  }

  public static String toString(InputStream in, int chunkSize) {
    UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream(chunkSize);
    pipe(in, out, chunkSize);
    return new String(out.getBackingArray(), 0, out.size(), StandardCharsets.UTF_8);
  }

  public static byte[] read(InputStream in) {
    return read(in, 512);
  }

  public static byte[] read(InputStream in, int chunkSize) {
    ByteArrayOutputStream out = new ByteArrayOutputStream(chunkSize);
    pipe(in, out, chunkSize);
    return out.toByteArray();
  }

  /**
   * Reads all bytes from the specified input stream and writes them to the specified output stream.
   * Bytes are read and written in chunks of 512 bytes at a time. Neither the input stream nor the
   * output stream is closed when done.
   *
   * @param in The input stream
   * @param out The output stream
   */
  public static void pipe(InputStream in, OutputStream out) {
    pipe(in, out, 512);
  }

  /**
   * Reads all bytes from the specified input stream and writes them to the specified output stream.
   * Bytes are read and written in chunks of the specified size. Neither the input stream nor the
   * output stream is closed when done.
   *
   * @param in The input stream
   * @param out The output stream
   * @param chunkSize The number of bytes read/written at a time
   */
  public static void pipe(InputStream in, OutputStream out, int chunkSize) {
    Check.notNull(in, "in");
    Check.notNull(out, "out");
    Check.that(chunkSize, "chunkSize").is(gt(), 0);
    byte[] data = new byte[chunkSize];
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
   * Creates a new, empty file in the file system's temp directory. Equivalent to <code>
   * createTempFile(IOMethods.class, ".tmp", true)</code>.
   *
   * @return A {@code File} object for a new, empty file in the file system's temp directory
   * @throws IOException If an I/O error occurs
   */
  public static File createTempFile() throws IOException {
    return createTempFile(IOMethods.class);
  }

  /**
   * Creates a new, empty file in the file system's temp directory. Equivalent to <code>
   * createTempFile(IOMethods.class, extension, true)</code>.
   *
   * @param extension The extension to append to the generated file name
   * @return A {@code File} object for a new, empty file in the file system's temp directory
   * @throws IOException If an I/O error occurs
   */
  public static File createTempFile(String extension) throws IOException {
    return createTempFile(IOMethods.class, extension, true);
  }

  /**
   * Creates a new, empty file in the file system's temp directory. Equivalent to <code>
   * createTempFile(requester ".tmp", true)</code>.
   *
   * @param requester The class requesting the temp file (simple name will become part of the file
   *     name)
   * @return A {@code File} object for a new, empty file in the file system's temp directory
   * @throws IOException If an I/O error occurs
   */
  public static File createTempFile(Class<?> requester) throws IOException {
    return createTempFile(requester, true);
  }

  /**
   * Creates a {@code File} object with a unique file name, located file system's temp directory.
   * Equivalent to <code>createTempFile(requester ".tmp", touch)</code>.
   *
   * @param requester The class requesting the temp file (simple name will become part of the file
   *     name)
   * @return A {@code File} object for a new, empty file in the file system's temp directory
   * @throws IOException If an I/O error occurs
   */
  public static File createTempFile(Class<?> requester, boolean touch) throws IOException {
    return createTempFile(requester, ".tmp", touch);
  }

  /**
   * Creates a {@code File} object with a unique file name, located file system's temp directory.
   * Using {@link File#createTempFile(String, String)} may fail if temporary files are created in
   * rapid succession as it seems to use only System.currentTimeMillis() to invent a file name. This
   * method has a 100% chance of generating a unique file name.
   *
   * @param requester The class requesting the temp file (simple name will become part of the file
   *     name)
   * @param extension The extension to append to the generated file name
   * @param touch Whether to actually create the file on the file system
   * @return A {@code File} object for a new, empty file in the file system's temp directory
   * @throws IOException If an I/O error occurs
   */
  public static synchronized File createTempFile(
      Class<?> requester, String extension, boolean touch) throws IOException {
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
    if (touch) {
      if (f.createNewFile()) {
        return f;
      }
      String fmt = "Failed to created temp file %s (already existed)";
      throw new IOException(String.format(fmt, path));
    }
    return f;
  }

  private static int tempCount = 100000;

  /**
   * Deletes the file or directory denoted by the specified path. Directories need not be empty. If
   * the file or directory does not exist, this method returns quietly.
   *
   * @param path The path of the file/directory to be deleted
   * @throws IOException Thrown from {@link Files#walkFileTree(Path, java.nio.file.FileVisitor)}
   */
  public static void rm(String path) throws IOException {
    Path p = Path.of(path);
    if (!Files.exists(p)) {
      return;
    }
    Files.walkFileTree(
        p,
        new SimpleFileVisitor<>() {
          @Override
          public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
              throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
          }
        });
  }
}
