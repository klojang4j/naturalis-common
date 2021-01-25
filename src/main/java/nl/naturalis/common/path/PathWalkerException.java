package nl.naturalis.common.path;

import static java.lang.String.format;
import static nl.naturalis.common.ClassMethods.getClassName;

/**
 * Runtime exception thrown while the {@link PathWalker} is walking a path through an object.
 *
 * @author Ayco Holleman
 */
public class PathWalkerException extends RuntimeException {

  static PathWalkerException invalidPath(Path p) {
    return new PathWalkerException("Invalid path: " + p);
  }

  static PathWalkerException readWriteError(Throwable t, Object obj, String segment) {
    String fmt = "Cannot read/write \"%s\" in class %s: %s";
    return new PathWalkerException(format(fmt, segment, getClassName(obj), t.getMessage()), t);
  }

  static PathWalkerException nullSegmentNotAllowed(Object obj) {
    String fmt = "Null segment not allowed when reading/writing %s";
    return new PathWalkerException(format(fmt, getClassName(obj)));
  }

  PathWalkerException(String message) {
    super(message);
  }

  PathWalkerException(String message, Throwable cause) {
    super(message, cause);
  }
}
