package nl.naturalis.common;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Methods for working with files and directories.
 *
 * @author Ayco Holleman
 */
public class FileMethods {

  private FileMethods() {}

  /**
   * Creates a new file or directory underneath the provided directory. NB the file/subdirectory is
   * not actually created on the file system. It is only a {@code File} object that allows you to do
   * so (e.g. using {@code File.mkdirs()}). The {@code path} argument can be a path string like
   * "temp/test/hello.txt" or an array of path elements ("temp", "test", "hello.txt").
   *
   * @param directory The directory under which to create a new file or directory
   * @param path An array of path elements, stringified using {@code toString()}
   * @return
   */
  public static File newFile(File directory, Object... path) {
    String[] elems = Arrays.stream(path).map(Object::toString).toArray(String[]::new);
    return Paths.get(directory.getAbsolutePath(), elems).toFile();
  }

}
