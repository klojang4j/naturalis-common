package nl.naturalis.common.exception;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import nl.naturalis.common.ExceptionMethods;

/**
 * A subclass of {@link RuntimeException} that behaves just like {@link Exception} it wraps. All methods overridden from {@code Exception}
 * do nothing but delegate to the same method on the wrapped exception. This behaviour might be useful when wrapping checked exceptions that
 * in practice often cannot sensibly be dealt with (like {@link IOException}), and are therefore runtime exceptions for all practical
 * purposes. By hiding completely behind the wrapped exception an {@code UncheckedException} has a less cumbersome stack trace than a
 * straight {@code RuntimeException}.
 * 
 * @see RootException
 * @see ExceptionMethods#uncheck(Throwable)
 * 
 * @author Ayco Holleman
 *
 */
public class UncheckedException extends RuntimeException {

  /**
   * Creates a {@code UncheckedException} wrapping the provided {@code Throwable}.
   * 
   * @param cause
   */
  public UncheckedException(Throwable cause) {
    this(cause.getMessage(), cause);
  }

  /**
   * Creates a {@code UncheckedException} with the provided custome message, wrapping the provided {@code Throwable}.
   * 
   * @param cause
   */
  public UncheckedException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Prints the stack trace of the wrapped {@code Exception}.
   */
  @Override
  public void printStackTrace(PrintWriter s) {
    super.getCause().printStackTrace(s);
  }

  /**
   * Calls {@code toString{}} on the wrapped {@code Exception}.
   */
  @Override
  public String toString() {
    return super.getCause().toString();
  }

  /**
   * Returns the cause of the cause!
   */
  @Override
  public synchronized Throwable getCause() {
    return super.getCause().getCause();
  }

  /**
   * Prints the stack trace of the wrapped {@code Exception}.
   */
  @Override
  public void printStackTrace() {
    super.getCause().printStackTrace();
  }

  /**
   * Prints the stack trace of the wrapped {@code Exception}.
   */
  @Override
  public void printStackTrace(PrintStream s) {
    super.getCause().printStackTrace(s);
  }

  /**
   * Returns the stack trace of the wrapped {@code Exception}.
   */
  @Override
  public StackTraceElement[] getStackTrace() {
    return super.getCause().getStackTrace();
  }

}
