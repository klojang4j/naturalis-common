package nl.naturalis.common.path;

import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.path.DeadEnd.*;

public final class DeadEndException extends RuntimeException {

  private static final String INVALID_PATH = "Invalid path: \"%s\". ";

  static DeadEndException noSuchProperty(Path p) {
    String fmt = INVALID_PATH + " No such property: \"%s\"";
    String msg = String.format(fmt, p, p.segment(-1));
    return new DeadEndException(NO_SUCH_PROPERTY, msg);
  }

  static DeadEndException noSuchKey(Path p) {
    String fmt = INVALID_PATH + "No such key: \"%s\"";
    String msg = String.format(fmt, p, p.segment(-1));
    return new DeadEndException(NO_SUCH_KEY, msg);
  }

  static DeadEndException indexExpected(Path p) {
    String fmt = INVALID_PATH + "Array index expected. Found: \"%s\"";
    String msg = String.format(fmt, p, p.segment(-1));
    return new DeadEndException(INDEX_EXPECTED, msg);
  }

  static DeadEndException indexOutOfBounds(Path p) {
    String fmt = INVALID_PATH + "Index out of bounds: %s";
    String msg = String.format(fmt, p, p.segment(-1));
    return new DeadEndException(INDEX_OUT_OF_BOUNDS, msg);
  }

  static DeadEndException terminalValue(Path p) {
    String fmt = INVALID_PATH + "Path continued beyond terminal value";
    String msg = String.format(fmt, p);
    return new DeadEndException(TERMINAL_VALUE, msg);
  }

  static DeadEndException emptySegment(Path p) {
    String fmt = INVALID_PATH + "Null or empty segment";
    String msg = String.format(fmt, p);
    return new DeadEndException(EMPTY_SEGMENT, msg);
  }

  static DeadEndException typeNotSupported(Object host) {
    String fmt = "Don't know how to read/write instances of %s";
    String msg = String.format(fmt, className(host));
    return new DeadEndException(TYPE_NOT_SUPPORTED, msg);
  }

  static DeadEndException readError(Path p, Throwable t) {
    String fmt = "error while reading/writing \"%s\": t";
    String msg = String.format(fmt, p, t);
    return new DeadEndException(READ_ERROR, msg);
  }

  private final DeadEnd deadEnd;

  private DeadEndException(DeadEnd deadEnd, String message) {
    super(message);
    this.deadEnd = deadEnd;
  }

  public DeadEnd getDeadEnd() {
    return deadEnd;
  }

}
