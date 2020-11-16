package nl.naturalis.common.exception;

import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.ObjectMethods.isNotEmpty;
import static nl.naturalis.common.StringMethods.append;
import static nl.naturalis.common.StringMethods.rtrim;

/**
 * Provides detailed information about the origin of an exception. Useful for tracing back an
 * exception to a statement within some code base (e.g. your own). Example:
 *
 * <p>
 *
 * <pre>
 * try {
 *
 *   // stuff
 *
 * } catch (Exception e) {
 *
 *   // Log exception message plus class and line number within the
 *   // nl.naturalis code base where things flew off the rails
 *
 *   logger.error(new ExceptionOrigin(e, "nl.naturalis").getDetailedMessage());
 * }
 * </pre>
 *
 * @see ExceptionMethods#getDetailedMessage(Throwable, String)
 */
public final class ExceptionOrigin {

  private final Throwable exc;
  private final String search;
  // The stack trace element matching the search string
  private final StackTraceElement ste;

  /**
   * Equivalent to {@code new ExceptionOrigin(t, null)}. Looks at the 1st entry in the stack trace,
   * which contains the statement that cause the exception <i>if</i> the exception has no cause.
   * Since you cannot ordinarily know this, this constructor is useless unless you explicitly pass
   * it the root cause of an exception. See {@link ExceptionMethods#getRootCause(Throwable)
   * ExceptionMethods#getRootCause}.
   *
   * @param exc The exception to analyze
   */
  public ExceptionOrigin(Throwable exc) {
    this(exc, null);
  }

  /**
   * Creates a new {@code ExceptionOrigin} for the provided exception, searching its stack trace for
   * an element matching the search string. Matching happens through a simple {@link
   * String#contains(CharSequence) String.contains} on the fully-qualified class name. The {@code
   * search} argument may be null, in which case the first element of the stack trace is used to
   * provide extra information about the exception.
   *
   * @param exc The exception to analyze
   * @param search Any part of the package name or class name that you want the exception to be
   *     traced back to. May be null.
   */
  public ExceptionOrigin(Throwable exc, String search) {
    this.exc = Check.notNull(exc, "exc").ok();
    this.search = search;
    if (isEmpty(exc.getStackTrace())) {
      this.ste = null;
    } else if (search == null) {
      this.ste = exc.getStackTrace()[0];
    } else {
      this.ste = findOrigin(exc, search);
    }
  }

  /**
   * Provides a detailed exception message that includes the class, method and line of the first
   * statement in the stack trace that matches the search string.
   *
   * @return A detailed exception message
   */
  public String getDetailedMessage() {
    StringBuilder sb = new StringBuilder(100);
    if (exc.getMessage() != null) {
      append(sb, rtrim(exc.getMessage(), ". "), ". ");
    }
    sb.append(exc.getClass().getName());
    if (search == null) {
      if (ste == null) {
        sb.append(" (no stack trace available)");
      } else {
        addStackTraceInfo(sb);
      }
    } else if (ste == null) {
      append(sb, " (not originating from ", search, ")");
    } else {
      addStackTraceInfo(sb);
    }
    return sb.toString();
  }

  /** Returns {@link #getDetailedMessage() getDetailedMessage}. */
  @Override
  public String toString() {
    return getDetailedMessage();
  }

  /**
   * Returns the exception wrapped by this {@code ExceptionOrigin}.
   *
   * @return The exception wrapped by this {@code ExceptionOrigin}
   */
  public Throwable getException() {
    return exc;
  }

  /**
   * Returns the first stack trace element matching the search string, or the very first stack trace
   * element if no search string was specified.
   *
   * @return The first stack trace element matching the search string, or the very first stack trace
   *     element if no search string was specified
   */
  public StackTraceElement geStackTraceElement() {
    return ste;
  }

  /**
   * Returns the module in which the exception occurred or null if the exception came without a
   * stack trace.
   *
   * @return The module in which the exception occurred or null if the exception came without a
   *     stack trace
   */
  public String getModule() {
    return ste == null ? null : ste.getModuleName();
  }

  /**
   * Returns the class in which the exception occurred or null if the exception came without a stack
   * trace.
   *
   * @return The class in which the exception occurred or null if the exception came without a stack
   *     trace
   */
  public String getClassName() {
    return ste == null ? null : ste.getClassName();
  }

  /**
   * Returns the method in which the exception occurred or null if the exception came without a
   * stack trace.
   *
   * @return The method in which the exception occurred or null if the exception came without a
   *     stack trace
   */
  public String getMethod() {
    return ste == null ? null : ste.getMethodName();
  }

  /**
   * Returns the line at which the exception occurred or -1 if the exception came without a stack
   * trace.
   *
   * @return The line at which the exception occurred or -1 if the exception came without a stack
   *     trace
   */
  public int getLine() {
    return ste == null ? -1 : ste.getLineNumber();
  }

  private static StackTraceElement findOrigin(Throwable exc, String search) {
    for (Throwable t = exc; t != null; t = t.getCause()) {
      StackTraceElement[] trace = t.getStackTrace();
      if (isNotEmpty(trace)) {
        for (StackTraceElement ste : trace) {
          if (ste.getClassName().contains(search)) {
            return ste;
          }
        }
      }
    }
    return null;
  }

  private void addStackTraceInfo(StringBuilder sb) {
    append(sb, " at ", getClassName(), ".", getMethod(), " (line ", getLine(), ")");
  }
}
