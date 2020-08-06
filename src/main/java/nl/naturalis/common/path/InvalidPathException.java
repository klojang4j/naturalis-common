package nl.naturalis.common.path;

import static java.lang.String.format;

public class InvalidPathException extends RuntimeException {

  public static InvalidPathException missingArrayIndex(Path path, String segment) {
    return new InvalidPathException(format("Missing array index after %s in path %s", segment, path));
  }

  public InvalidPathException(String message) {
    super(message);
  }

}
