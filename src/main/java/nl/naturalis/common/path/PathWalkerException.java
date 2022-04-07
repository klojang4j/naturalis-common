package nl.naturalis.common.path;

import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.invoke.NoSuchPropertyException;

import static java.lang.String.format;
import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.ClassMethods.simpleClassName;

/**
 * Runtime exception thrown while the {@link PathWalker} is walking a path through an object.
 *
 * @author Ayco Holleman
 */
public class PathWalkerException extends RuntimeException {

  private static final String INVALID_PATH = "Invalid path: %s";

  static PathWalkerException noSuchProperty(Path p, NoSuchPropertyException e) {
    String fmt = INVALID_PATH + " (%s)";
    String msg = String.format(fmt, p, e.getMessage());
    return new PathWalkerException(msg);
  }

  static PathWalkerException noSuchKey(Path p) {
    String fmt = INVALID_PATH + " (key not found: %s)";
    String msg = String.format(fmt, p, p.segment(0));
    return new PathWalkerException(msg);
  }

  static PathWalkerException arrayIndexExpected(Path p) {
    String fmt = INVALID_PATH + " (%s is not a valid array index)";
    String msg = String.format(fmt, p, p.segment(-1));
    return new PathWalkerException(msg);
  }

  static PathWalkerException indexOutOfBounds(Path p) {
    String fmt = INVALID_PATH + " (array index %s out of bounds)";
    String msg = String.format(fmt, p, p.segment(-1));
    return new PathWalkerException(msg);
  }

  static PathWalkerException pathExtendsBeyondPrimitive(Path p) {
    String fmt = INVALID_PATH + " (path cannot continue after reaching primitive value)";
    String msg = String.format(fmt, p);
    return new PathWalkerException(msg);
  }

  static PathWalkerException emptySegment(Path p) {
    String fmt = INVALID_PATH + " (null or empty segment)";
    String msg = String.format(fmt, p);
    return new PathWalkerException(msg);
  }

  static PathWalkerException cannotWriteToNullObject() {
    return new PathWalkerException("Cannot write to null object");
  }

  static PathWalkerException cannotWriteToDeadEnd(Path p) {
    String fmt = "Cannot set %s on dead end %s";
    String msg = String.format(fmt, p.subpath(-1), p.subpath(0, p.size() - 1));
    return new PathWalkerException(msg);
  }

  static PathWalkerException cannotWrite(Object host) {
    String fmt = "Writing not supported for instances of %s";
    String msg = String.format(fmt, className(host));
    return new PathWalkerException(msg);
  }

  static PathWalkerException nullInvalidForPrimitiveArray(Path p, Object array) {
    String fmt = "Cannot assign null to %s (%s)";
    String msg = String.format(fmt, p, simpleClassName(array));
    return new PathWalkerException(msg);
  }

  static PathWalkerException invalidType(Path p, Class<?> expected, Class<?> actual) {
    String fmt = "Cannot assign %s to %s (expected type: %s)";
    String msg = String.format(fmt, p, simpleClassName(expected), simpleClassName(actual));
    return new PathWalkerException(msg);
  }

  static PathWalkerException invalidType(Path p, ClassCastException e) {
    String fmt = "Cannot assign %s to %s: %s";
    String msg = String.format(fmt, p, e.getMessage());
    return new PathWalkerException(msg);
  }

  static PathWalkerException wrap(Throwable t) {
    if (t.getClass() == PathWalkerException.class) {
      return (PathWalkerException) t;
    }
    return new PathWalkerException(ExceptionMethods.getRootCause(t));
  }

  static PathWalkerException nullSegmentNotAllowed(Object obj) {
    String fmt = "Null segment not allowed when reading/writing %s";
    return new PathWalkerException(format(fmt, className(obj)));
  }

  private PathWalkerException(String message) {
    super(message);
  }

  private PathWalkerException(Throwable cause) {
    super(cause);
  }

  private PathWalkerException(String message, Throwable cause) {
    super(message, cause);
  }

}
