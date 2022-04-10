package nl.naturalis.common.path;

import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.path.ErrorCode.*;

/**
 * Thrown by a {@link PathWalker} if a path-read or path-write error occurs.
 */
public final class PathWalkerException extends RuntimeException {

  private static final String INVALID_PATH = "Invalid path: \"%s\". ";

  static PathWalkerException noSuchProperty(Path p) {
    String fmt = INVALID_PATH + " No such property: \"%s\"";
    String msg = String.format(fmt, p, p.segment(-1));
    return new PathWalkerException(NO_SUCH_PROPERTY, msg);
  }

  static PathWalkerException noSuchKey(Path p) {
    String fmt = INVALID_PATH + "No such key: \"%s\"";
    String msg = String.format(fmt, p, p.segment(-1));
    return new PathWalkerException(NO_SUCH_KEY, msg);
  }

  static PathWalkerException indexExpected(Path p) {
    String fmt = INVALID_PATH + "Array index expected. Found: \"%s\"";
    String msg = String.format(fmt, p, p.segment(-1));
    return new PathWalkerException(INDEX_EXPECTED, msg);
  }

  static PathWalkerException indexOutOfBounds(Path p) {
    String fmt = INVALID_PATH + "Index out of bounds: %s";
    String msg = String.format(fmt, p, p.segment(-1));
    return new PathWalkerException(INDEX_OUT_OF_BOUNDS, msg);
  }

  static PathWalkerException terminalValue(Path p) {
    String fmt = INVALID_PATH + "Path continued beyond terminal value";
    String msg = String.format(fmt, p);
    return new PathWalkerException(TERMINAL_VALUE, msg);
  }

  static PathWalkerException emptySegment(Path p) {
    String fmt = INVALID_PATH + "Null or empty segment";
    String msg = String.format(fmt, p);
    return new PathWalkerException(EMPTY_SEGMENT, msg);
  }

  static PathWalkerException typeNotSupported(Object host) {
    String fmt = "Don't know how to read/write instances of %s";
    String msg = String.format(fmt, className(host));
    return new PathWalkerException(TYPE_NOT_SUPPORTED, msg);
  }

  static PathWalkerException readError(Path p, Throwable t) {
    String fmt = "error while reading/writing \"%s\": t";
    String msg = String.format(fmt, p, t);
    return new PathWalkerException(EXCEPTION, msg);
  }

  private final ErrorCode errorCode;

  private PathWalkerException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  /**
   * Return a symbolic constant for the error encountered by the {@link PathWalker}
   *
   * @return A symbolic constant for the error encountered by the {@link PathWalker}.
   */
  public ErrorCode getErrorCode() {
    return errorCode;
  }

}
