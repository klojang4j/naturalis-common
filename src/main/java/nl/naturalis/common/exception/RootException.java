package nl.naturalis.common.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

import nl.naturalis.common.ExceptionMethods;

import static nl.naturalis.common.ExceptionMethods.getRootCause;

/**
 * A subclass of {@code RuntimeException} that behaves just like the root cause of the exception it
 * wraps. The {@code RuntimeException} methods it overrides does nothing but delegete to the same
 * method on the root cause. This makes its stack trace small and informative, at the cost of not
 * knowing how the original exception bubbled up.
 *
 * @see UncheckedException
 * @see ExceptionMethods#getRootCause(Throwable)
 * @author Ayco Holleman
 */
public class RootException extends RuntimeException {

  /**
   * Creates a {@code RootException} wrapping the provided {@code Exception}.
   *
   * @param cause The exception to wrap
   */
  public RootException(Throwable cause) {
    this(getRootCause(cause).getMessage(), getRootCause(cause));
  }

  /**
   * Creates a {@code RootException} with a custom message, wrapping the provided {@code Exception}.
   *
   * @param message A custom message
   * @param cause The exception to wrap
   */
  public RootException(String message, Throwable cause) {
    super(message, getRootCause(cause));
  }

  /** Prints the stack trace of the root cause. */
  @Override
  public void printStackTrace(PrintWriter s) {
    super.getCause().printStackTrace(s);
  }

  /** Calls {@code toString()} on the root cause. */
  @Override
  public String toString() {
    return super.getCause().toString();
  }

  /** Returns the cause of the root cause, so null! */
  @Override
  public synchronized Throwable getCause() {
    return super.getCause().getCause();
  }

  /** Prints the stack trace of the root cause. */
  @Override
  public void printStackTrace() {
    super.getCause().printStackTrace();
  }

  /** Prints the stack trace of the root cause. */
  @Override
  public void printStackTrace(PrintStream s) {
    super.getCause().printStackTrace(s);
  }

  /** Returns the stack trace of the root cause. */
  @Override
  public StackTraceElement[] getStackTrace() {
    return super.getCause().getStackTrace();
  }
}
