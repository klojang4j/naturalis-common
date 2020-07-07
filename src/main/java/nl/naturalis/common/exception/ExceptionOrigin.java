package nl.naturalis.common.exception;

import static nl.naturalis.common.ArrayMethods.isEmpty;
import static nl.naturalis.common.ArrayMethods.notEmpty;
import static nl.naturalis.common.StringMethods.appendIfAbsent;
import static nl.naturalis.common.StringMethods.ifBlank;

/**
 * Provides detailed information about the origin of an exception.
 * 
 * <pre>
 * try {
 *   // stuff
 * } catch (Exception e) {
 *   ExceptionOrigin origin = new ExceptionOrigin(e, "nl.naturalis");
 *   // Returns the original exception message plus class and line number
 *   // within the nl.naturalis code base where things flew off the rails
 *   logger.error(origin.getDetailedMessage());
 * }
 * </pre>
 */
public class ExceptionOrigin {

  private final Throwable exc;
  private final StackTraceElement ste;
  private final String search;

  /**
   * Equivalent to {@code new ExceptionSource(t, null)}. Looks at the 1st entry in the stack trace, which is the point of origin <i>if</i>
   * the exception has no cause. If you expect wrapped exceptions (and who knows ...), this constructor is pretty useless.
   * 
   * @param t
   */
  public ExceptionOrigin(Throwable t) {
    this(t, null);
  }

  /**
   * Creates a new {@code ExceptionSource} for the provided exception, searching its stack trace for an execution point matching
   * {@code search}. The {@code search} argument is explicitly allowed to be null, in which case the first element of the stack trace is
   * used to provide extra information about the exception.
   * 
   * @param t The exception to inspect
   * @param search The string to search for in the stack trace.
   */
  public ExceptionOrigin(Throwable t, String search) {
    this.exc = t;
    this.search = ifBlank(search, null);
    if (isEmpty(t.getStackTrace())) {
      this.ste = null;
    } else if (search == null) {
      this.ste = t.getStackTrace()[0];
    } else {
      this.ste = findOrigin(t, search);
    }
  }

  /**
   * Provides a detailed exception message that includes the class, method and line at which the exception occurred, or at the execution
   * point matching the search.
   * 
   * @return
   */
  public String getDetailedMessage() {
    StringBuilder sb = new StringBuilder(100);
    if (exc.getMessage() != null) {
      sb.append(appendIfAbsent(exc.getMessage().strip(), "  =====  "));
    }
    sb.append(exc.getClass().getName());
    if (ste != null) {
      if (search != null) {
        sb.append("  =====  Originating in ")
            .append(getClassName())
            .append('.')
            .append(getMethod())
            .append(" (line ")
            .append(getLine())
            .append(")");
      } else {
        sb.append(" (No origin in package/class \"")
            .append(search)
            .append("\")");
      }
    }
    return sb.toString();
  }

  /**
   * Returns {@link #getDetailedMessage() getDetailedMessage}.
   */
  public String toString() {
    return getDetailedMessage();
  }

  /**
   * Returns the exception wrapped by this {@code ExceptionSource}.
   */
  public Throwable getException() {
    return exc;
  }

  /**
   * Returns the execution point that was inspected.
   * 
   * @return
   */
  public StackTraceElement geStackTraceElement() {
    return ste;
  }

  /**
   * Returns the module in which the exception occurred or null if the exception came without a stack trace.
   * 
   * @return
   */
  public String getModule() {
    return ste == null ? null : ste.getModuleName();
  }

  /**
   * Returns the class in which the exception occurred or null if the exception came without a stack trace.
   * 
   * @return
   */
  public String getClassName() {
    return ste == null ? null : ste.getClassName();
  }

  /**
   * Returns the method in which the exception occurred or null if the exception came without a stack trace.
   * 
   * @return
   */
  public String getMethod() {
    return ste == null ? null : ste.getMethodName();
  }

  /**
   * Returns the line at which the exception occurred or -1 if the exception came without a stack trace.
   * 
   * @return
   */

  public int getLine() {
    return ste == null ? -1 : ste.getLineNumber();
  }

  private static StackTraceElement findOrigin(Throwable exc, String search) {
    for (Throwable t = exc; t != null; t = t.getCause()) {
      StackTraceElement[] trace = t.getStackTrace();
      if (notEmpty(trace)) {
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
