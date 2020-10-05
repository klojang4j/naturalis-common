package nl.naturalis.common.exception;

import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.ObjectMethods.isNotEmpty;
import static nl.naturalis.common.StringMethods.rtrim;

/**
 * Provides detailed information about the origin of an exception. Useful for tracing back an
 * exception to a statement within your own code.
 *
 * <pre>
 * try {
 *
 *   // stuff
 *
 * } catch (Exception e) {
 *
 *   // Returns exception message plus class and line number
 *   // within nl.naturalis code where things flew off the rails
 *
 *   logger.error(new ExceptionOrigin(e, "nl.naturalis").getDetailedMessage());
 * }
 * </pre>
 *
 * @see ExceptionMethods#getDetailedMessage(Throwable, String)
 */
public class ExceptionOrigin {

  private final Throwable exc;
  private final StackTraceElement ste;
  private final String search;

  /**
   * Equivalent to {@code new ExceptionSource(t, null)}. Looks at the 1st entry in the stack trace,
   * which is the point of origin <i>if</i> the exception has no cause. Since ordinarily you cannot
   * know this, this constructor is useless unless you explicitly pass it the root cause of an
   * exception. See {@link ExceptionMethods#getDetailedMessage(Throwable)
   * ExceptionMethods.getDetailedMessage} and {@link ExceptionMethods#getRootCause(Throwable)
   * ExceptionMethods#getRootCause}.
   *
   * @param exc The exception to analyze
   */
  public ExceptionOrigin(Throwable exc) {
    this(exc, null);
  }

  /**
   * Creates a new {@code ExceptionOrigin} for the provided exception, searching its stack trace for
   * an execution point matching {@code search}. The {@code search} argument is explicitly allowed
   * to be null, in which case the first element of the stack trace is used to provide extra
   * information about the exception.
   *
   * @param exc The exception to analyze
   * @param search Any part of the package name or fully-qualified class name that you want the
   *     exception to be traced back to. May be null.
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
   * Provides a detailed exception message that includes the class, method and line at which the
   * exception occurred, or at the execution point matching the search.
   *
   * @return
   */
  public String getDetailedMessage() {
    StringBuilder sb = new StringBuilder(100);
    if (exc.getMessage() != null) {
      sb.append(rtrim(exc.getMessage(), ". ")).append(". ");
    }
    sb.append(exc.getClass().getName());
    if (ste != null) {
      if (search != null) {
        sb.append(" originating from ")
            .append(getClassName())
            .append('.')
            .append(getMethod())
            .append(" (line ")
            .append(getLine())
            .append(")");
      } else {
        sb.append(" (no origin in \"").append(search).append("\")");
      }
    }
    return sb.toString();
  }

  /** Returns {@link #getDetailedMessage() getDetailedMessage}. */
  @Override
  public String toString() {
    return getDetailedMessage();
  }

  /** Returns the exception wrapped by this {@code ExceptionSource}. */
  public Throwable getException() {
    return exc;
  }

  /**
   * Returns the first stack trace element found to contain {@code search}
   *
   * @return
   */
  public StackTraceElement geStackTraceElement() {
    return ste;
  }

  /**
   * Returns the module in which the exception occurred or null if the exception came without a
   * stack trace.
   *
   * @return
   */
  public String getModule() {
    return ste == null ? null : ste.getModuleName();
  }

  /**
   * Returns the class in which the exception occurred or null if the exception came without a stack
   * trace.
   *
   * @return
   */
  public String getClassName() {
    return ste == null ? null : ste.getClassName();
  }

  /**
   * Returns the method in which the exception occurred or null if the exception came without a
   * stack trace.
   *
   * @return
   */
  public String getMethod() {
    return ste == null ? null : ste.getMethodName();
  }

  /**
   * Returns the line at which the exception occurred or -1 if the exception came without a stack
   * trace.
   *
   * @return
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
}
