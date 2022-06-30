package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.io.UnsafeByteArrayOutputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.*;
import static nl.naturalis.common.StringMethods.lpad;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * I/O-related methods.
 *
 * @author Ayco Holleman
 */
public class IOMethods {

  private static final String INVALID_PATH = "No such resource: \"${0}\"";

  private IOMethods() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the contents of the specified file.
   *
   * @param path the path to the file
   * @return the contents of the specified file
   */
  public static String getContents(String path) {
    Check.notNull(path);
    File f = Check.that(new File(path)).is(file()).ok();
    try (FileInputStream fis = new FileInputStream(f)) {
      return getContents(fis);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Returns the contents of the specified resource. Bytes are read in chunks of 512
   * bytes.
   *
   * @param clazz the {@code Class} to call {@link Class#getResourceAsStream(String)
   *     getResourceAsStream} on
   * @param path the path to the resource
   * @return the contents of the specified resource
   */
  public static String getContents(Class<?> clazz, String path) {
    return getContents(clazz, path, 512);
  }

  /**
   * Returns the contents of the specified resource.
   *
   * @param clazz the {@code Class} to call {@link Class#getResourceAsStream(String)
   *     getResourceAsStream} on
   * @param path the path to the resource
   * @param chunkSize the number of bytes read at a time
   * @return the contents of the specified resource
   */
  public static String getContents(Class<?> clazz, String path, int chunkSize) {
    Check.notNull(clazz, "clazz");
    Check.notNull(path, "path");
    try (InputStream in = clazz.getResourceAsStream(path)) {
      Check.that(in).is(notNull(), INVALID_PATH, path);
      return getContents(in, chunkSize);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Returns a {@code String} created from the bytes read from the specified input
   * stream. Bytes are read in chunks of 512 bytes. The input stream is <i>not</i>
   * closed once all bytes have been read.
   *
   * @param in the input stream
   * @return a {@code String} from the bytes read from the specified input stream
   */
  public static String getContents(InputStream in) {
    return getContents(in, 512);
  }

  /**
   * Returns a {@code String} created from the bytes read from the specified input
   * stream. The input stream is <i>not</i> closed once all bytes have been read.
   *
   * @param in the input stream
   * @param chunkSize the number of bytes read at a time
   * @return a {@code String} from the bytes read from the specified input stream
   */
  public static String getContents(InputStream in, int chunkSize) {
    Check.that(chunkSize, "chunkSize").is(gt(), 0);
    UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream(chunkSize);
    pipe(in, out, chunkSize);
    return new String(out.getBackingArray(), 0, out.size(), UTF_8);
  }

  /**
   * Simple file-write method. Not efficient, but easy to use. Overwrites
   * pre-existing contents!
   *
   * @param path The path to the file
   * @param contents The contents to be written
   */
  public static void write(String path, String contents) {
    Check.notNull(path, "path");
    Check.notNull(contents, "contents");
    try {
      Files.writeString(Path.of(path), contents, UTF_8, CREATE, TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Simple file-write method. Not efficient, but easy to use. Appends the specified
   * string to the contents of the specified file.
   *
   * @param path The path to the file
   * @param contents The contents to be written
   */
  public static void append(String path, String contents) {
    Check.notNull(path, "path");
    Check.notNull(contents, "contents");
    try {
      Files.writeString(Path.of(path), contents, UTF_8, CREATE, APPEND);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Returns the contents of the specified resource as a byte array. Bytes are read
   * in chunks of 512 bytes.
   *
   * @param clazz the {@code Class} to call {@link Class#getResourceAsStream(String)
   *     getResourceAsStream} on
   * @param path the path to the resource
   * @return the bytes contained in the specified resource
   */
  public static byte[] read(Class<?> clazz, String path) {
    return read(clazz, path, 512);
  }

  /**
   * Returns the contents of the specified resource as a byte array.
   *
   * @param clazz the {@code Class} to call {@link Class#getResourceAsStream(String)
   *     getResourceAsStream} on
   * @param path the path to the resource
   * @param chunkSize the number of bytes read at a time
   * @return the contents of the specified resource
   */
  public static byte[] read(Class<?> clazz, String path, int chunkSize) {
    Check.notNull(clazz, "clazz");
    Check.notNull(path, "path");
    try (InputStream in = clazz.getResourceAsStream(path)) {
      Check.that(in).is(notNull(), INVALID_PATH, path);
      return read(in, chunkSize);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
  }

  /**
   * Returns a {@code byte[]} array containing the bytes read from the specified
   * input stream. Bytes are read in chunks of 512 bytes. The input stream is
   * <i>not</i> closed once all bytes have been read.
   *
   * @param in the input stream
   * @return a {@code byte[]} array containing the bytes read from the specified
   *     input stream
   */
  public static byte[] read(InputStream in) {
    return read(in, 512);
  }

  /**
   * Returns a {@code byte[]} array containing the bytes read from the specified
   * input stream. Bytes are read in chunks of the specified size. The input stream
   * is <i>not</i> closed once all bytes have been read.
   *
   * @param in the input stream
   * @param chunkSize the number of bytes read at a time
   * @return a {@code byte[]} array containing the bytes read from the specified
   *     input stream
   */
  public static byte[] read(InputStream in, int chunkSize) {
    ByteArrayOutputStream out = new ByteArrayOutputStream(chunkSize);
    pipe(in, out, chunkSize);
    return out.toByteArray();
  }

  /**
   * Reads all bytes from the specified input stream and writes them to the specified
   * output stream. Bytes are read and written in chunks of 512 bytes at a time.
   * Neither the input stream nor the output stream is closed when done.
   *
   * @param in the input stream
   * @param out the output stream
   */
  public static void pipe(InputStream in, OutputStream out) {
    pipe(in, out, 512);
  }

  /**
   * Reads all bytes from the specified input stream and writes them to the specified
   * output stream. Bytes are read and written in chunks of the specified size.
   * Neither the input stream nor the output stream is closed when done.
   *
   * @param in the input stream
   * @param out the output stream
   * @param chunkSize the number of bytes read/written at a time
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
   * Creates a new, empty file in the file system's temp directory. Equivalent to:
   *
   * <blockquote><pre>{@code
   * createTempFile(IOMethods.class, "tmp", true)
   * }</pre></blockquote>
   *
   * @return a {@code File} object for a new, empty file in the file system's temp
   *     directory
   * @throws IOException If an I/O error occurs
   */
  public static File createTempFile() throws IOException {
    return createTempFile(IOMethods.class);
  }

  /**
   * Creates a new, empty file in the file system's temp directory. Equivalent to:
   *
   * <blockquote><pre>{@code
   * createTempFile(requester "tmp", true)
   * }</pre></blockquote>
   *
   * @param requester the class requesting the temp file (simple name will become
   *     part of the file name)
   * @return a {@code File} object for a new, empty file in the file system's temp
   *     directory
   * @throws IOException if an I/O error occurs
   */
  public static File createTempFile(Class<?> requester) throws IOException {
    return createTempFile(requester, true);
  }

  /**
   * Creates a {@code File} object with a unique file name, located file system's
   * temp directory. Equivalent to:
   *
   * <blockquote><pre>{@code
   * createTempFile(requester "tmp", touch)
   * }</pre></blockquote>
   *
   * @param requester the class requesting the temp file (simple name will become
   *     part of the file name)
   * @return a {@code File} object for a new, empty file in the file system's temp
   *     directory
   * @throws IOException If an I/O error occurs
   */
  public static File createTempFile(Class<?> requester, boolean touch)
      throws IOException {
    return createTempFile(requester, "tmp", touch);
  }

  /**
   * Creates a {@code File} object with a unique file name, located in file system's
   * temp directory. Using {@link File#createTempFile(String, String)} appears to
   * fail if many threads are creating temp files in rapid succession. This method
   * has a 100% chance of generating a unique file name.
   *
   * @param requester the class requesting the temp file (simple name will become
   *     part of the file name)
   * @param extension The extension to append to the generated file name
   * @param touch Whether to actually create the file on the file system
   * @return a {@code File} object for a new, empty file in the file system's temp
   *     directory
   * @throws IOException If an I/O error occurs
   */
  public static synchronized File createTempFile(Class<?> requester,
      String extension,
      boolean touch) throws IOException {
    String path = uniquePath(requester, extension);
    File f = new File(path);
    if (touch) {
      Check.on(IOException::new, f)
          .isNot(fileExists(), "File exists already: ${arg}");
      f.createNewFile();
    }
    return f;
  }

  /**
   * Creates a new, empty directory in the file system's temp directory. Equivalent
   * to <code> createTempFile(IOMethods.class, ".dir", true)</code>.
   *
   * @return a {@code File} object for a new, empty directory in the file system's
   *     temp directory
   * @throws IOException If an I/O error occurs
   */
  public static File createTempDir() throws IOException {
    return createTempDir(IOMethods.class);
  }

  /**
   * Creates a new, empty file in the file system's temp directory. Equivalent to
   * <code> createTempFile(requester ".dir", true)</code>.
   *
   * @param requester the class requesting the temp file (simple name will become
   *     part of the file name)
   * @return a {@code File} object for a new, empty file in the file system's temp
   *     directory
   * @throws IOException If an I/O error occurs
   */
  public static File createTempDir(Class<?> requester) throws IOException {
    return createTempDir(requester, true);
  }

  /**
   * Creates a {@code File} object with a unique file name, located file system's
   * temp directory. Equivalent to <code>createTempFile(requester "dir",
   * touch)</code>.
   *
   * @param requester the class requesting the temp file (simple name will become
   *     part of the file name)
   * @return a {@code File} object for a new, empty directory in the file system's
   *     temp directory
   * @throws IOException If an I/O error occurs
   */
  public static File createTempDir(Class<?> requester, boolean touch)
      throws IOException {
    return createTempDir(requester, "dir", touch);
  }

  /**
   * Creates a {@code File} object with a unique file name, located in file system's
   * temp directory. Using {@link File#createTempFile(String, String)} may fail if
   * temporary files are created in rapid succession as it seems to use only
   * System.currentTimeMillis() to invent a file name. This method has a 100% chance
   * of generating a unique file name.
   *
   * @param requester the class requesting the temp directory (simple name will
   *     become part of the file name)
   * @param extension The extension to append to the generated directory name
   * @param touch Whether to actually create the directory on the file system
   * @return a {@code File} object for a new, empty directory in the file system's
   *     temp directory
   * @throws IOException If an I/O error occurs
   */
  public static synchronized File createTempDir(Class<?> requester,
      String extension,
      boolean touch) throws IOException {
    String path = uniquePath(requester, extension);
    File f = new File(path);
    if (touch) {
      Check.on(IOException::new, f).isNot(fileExists());
      f.mkdir();
    }
    return f;
  }

  /**
   * Deletes the file or directory denoted by the specified path. Directories need
   * not be empty. If the file or directory does not exist, this method returns
   * quietly.
   *
   * @param path The path of the file/directory to be deleted
   * @throws IOException Thrown from {@link Files#walkFileTree(Path,
   *     java.nio.file.FileVisitor)}
   */
  public static void rm(String path) throws IOException {
    Check.notNull(path);
    Path p = Path.of(path);
    if (!Files.exists(p)) {
      return;
    }
    Files.walkFileTree(p, new SimpleFileVisitor<>() {
      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc)
          throws IOException {
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

  private static final AtomicLong counter = new AtomicLong();

  private static String uniquePath(Class<?> requester, String extension) {
    Check.notNull(requester, "requester");
    Check.that(extension, "extension").isNot(blank());
    String path = StringMethods.append(new StringBuilder(64),
        System.getProperty("java.io.tmpdir"),
        "/",
        requester.getSimpleName().toLowerCase(),
        ".",
        Thread.currentThread().getName().toLowerCase(),
        ".",
        lpad(counter.incrementAndGet(), 8, '0'),
        ".",
        System.currentTimeMillis(),
        ".",
        extension).toString();
    return path;
  }

}
