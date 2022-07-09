package nl.naturalis.common.path;

import java.util.function.Supplier;

import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.ClassMethods.simpleClassName;
import static nl.naturalis.common.path.ErrorCode.*;

/**
 * Thrown by a {@link PathWalker} if a path-read or path-write error occurs.
 */
public final class PathWalkerException extends RuntimeException {

  interface Factory extends Supplier<PathWalkerException> {}

  private static final String INVALID_PATH = "Invalid path: \"%s\" (segment %d). ";

  static Factory noSuchProperty(Path path, int segment, Class<?> clazz) {
    String fmt = INVALID_PATH + "No accessible property named \"%s\" in %s.class";
    String className = className(clazz);
    String msg = String.format(fmt, path, segment, path.segment(segment), className);
    return () -> new PathWalkerException(NO_SUCH_PROPERTY, msg);
  }

  static Factory opaqueType(Path path, int segment, Class<?> clazz) {
    String fmt = INVALID_PATH + "No such property in class %s: %s";
    String className = simpleClassName(clazz);
    String msg = String.format(fmt, path, className, path.segment(segment));
    return () -> new PathWalkerException(NO_SUCH_PROPERTY, msg);
  }

  static Factory noSuchKey(Path path, int segment, Object key) {
    String fmt = INVALID_PATH + "No such key: \"%s\"";
    String msg = String.format(fmt, path, segment, path.segment(segment));
    return () -> new PathWalkerException(NO_SUCH_KEY, msg);
  }

  static Factory indexExpected(Path path, int segment) {
    String fmt = INVALID_PATH + "Array index expected. Found: %s";
    String msg = String.format(fmt, path, segment, path.segment(segment));
    return () -> new PathWalkerException(INDEX_EXPECTED, msg);
  }

  static Factory indexOutOfBounds(Path path, int segment) {
    String fmt = INVALID_PATH + "Index out of bounds: %s";
    String msg = String.format(fmt, path, segment, path.segment(segment));
    return () -> new PathWalkerException(INDEX_OUT_OF_BOUNDS, msg);
  }

  static Factory nullValue(Path path, int segment) {
    String fmt = INVALID_PATH
        + "Cannot proceed past terminal value at segment %s: null";
    String msg = String.format(fmt, path, segment, path.segment(segment));
    return () -> new PathWalkerException(TERMINAL_VALUE, msg);
  }

  static Factory terminalValue(Path path, int segment, Object value) {
    String fmt = INVALID_PATH
        + "Cannot proceed past terminal value at segment %s: (%s) %s";
    String className = simpleClassName(value.getClass());
    String msg = String.format(fmt,
        path,
        segment,
        path.segment(segment),
        className,
        value);
    return () -> new PathWalkerException(TERMINAL_VALUE, msg);
  }

  static Factory emptySegment(Path path, int segment) {
    String fmt = INVALID_PATH + "Segment must not be null or empty";
    String msg = String.format(fmt, path, segment);
    return () -> new PathWalkerException(EMPTY_SEGMENT, msg);
  }

  static Factory typeMismatch(Path path, int segment, Class expected, Class actual) {
    String fmt = INVALID_PATH + "Cannot assign %s to %s";
    String scn0 = simpleClassName(expected);
    String scn1 = simpleClassName(actual);
    String msg = String.format(fmt, path, segment, scn0, scn1);
    return () -> new PathWalkerException(TYPE_MISMATCH, msg);
  }

  static Factory typeNotSupported(Object host) {
    String fmt = "Don't know how to write instances of %s";
    String msg = String.format(fmt, className(host));
    return () -> new PathWalkerException(TYPE_NOT_SUPPORTED, msg);
  }

  static Factory notModifiable(Path path, Class<?> type, String method) {
    String fmt = "%s found at %s does not support %s operation";
    String msg = String.format(fmt, simpleClassName(type), path, method);
    return () -> new PathWalkerException(NOT_MODIFIABLE, msg);
  }

  static Factory keyDeserializationFailed(Path path,
      int segment,
      KeyDeserializationException exc) {
    String msg;
    if (exc.getMessage() == null) {
      String fmt = "Error while processing segment %s of %s: failed to deserialize %s into map key";
      msg = String.format(fmt, segment, path, path.segment(segment));
    } else {
      String fmt = "Error while processing segment %s of %s: failed to deserialize %s into map key";
      msg = String.format(fmt, segment, path, exc.getMessage());
    }
    return () -> new PathWalkerException(KEY_DESERIALIZATION_FAILED, msg);
  }

  static Factory unexpectedError(Path path, int segment, Throwable t) {
    String fmt = "Error while processing segment %s of %s: %s";
    String msg = String.format(fmt, segment, path, t);
    return () -> new PathWalkerException(EXCEPTION, msg);
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
