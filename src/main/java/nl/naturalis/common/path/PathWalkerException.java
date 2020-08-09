package nl.naturalis.common.path;

import static java.lang.String.format;

/**
 * Runtime exception thrown while the {@link PathWalker} is walking a path
 * through an object.
 *
 * @author Ayco Holleman
 *
 */
public class PathWalkerException extends RuntimeException {

  public static PathWalkerException illegalAccess(IllegalAccessException e, Object obj, String segment) {
    return new PathWalkerException(format("Failed to read value of field \"%s\" in %s: %s", obj, segment, e));
  }

  public PathWalkerException(String message) {
    super(message);
  }

  public PathWalkerException(Throwable cause) {
    super(cause);
  }

  public PathWalkerException(String message, Throwable cause) {
    super(message, cause);
  }

}
