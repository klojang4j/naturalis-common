package nl.naturalis.common.exception;

import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Optional;

import static nl.naturalis.common.ExceptionMethods.getRootCause;

/**
 * A {@code RuntimeException} that wraps the root cause of another exception. It
 * behaves as though it is the root cause itself: it overrides all methods from
 * {@code Exception} by calling the same method on the root cause. This makes its
 * stack trace very small and informative, at the cost of not knowing how the
 * original exception bubbled up.
 *
 * @author Ayco Holleman
 * @see UncheckedException
 * @see ExceptionMethods#getRootCause(Throwable)
 */
public class RootException extends RuntimeException {

  private final Optional<String> customMessage;

  /**
   * Creates a {@code RootCause} wrapping the provided {@code Exception}.
   *
   * @param cause The exception to wrap
   */
  public RootException(Throwable cause) {
    this(getRootCause(cause).getMessage(), getRootCause(cause));
  }

  /**
   * Creates a {@code RootCause} with a custom message.
   *
   * @param message A custom message
   * @param cause The exception to wrap
   */
  public RootException(String message, Throwable cause) {
    super(Check.notNull(cause, "cause").ok(UncheckedException::peal));
    this.customMessage = Optional.ofNullable(message);
  }

  /**
   * Returns the exception wrapped by this {@code RootException}. Note that {@link
   * #getCause()} does <i>not</i> return that exception. It returns the
   * <i>cause</i> of the root cause (i.e. {@code null}).
   *
   * @return The exception directly wrapped by this {@code UncheckedException}
   */
  @SuppressWarnings("unchecked")
  public <E extends Throwable> E unwrap() {
    return (E) super.getCause();
  }

  /**
   * Returns an {@code Optional} containing the custom message passed in through the
   * two-arg constructor or an empty {@code Optional} if the single-arg constructor
   * was used.
   *
   * @return An {@code Optional} containing the custom message passed in through the
   *     constructor
   */
  public Optional<String> getCustomMessage() {
    return customMessage;
  }

  /**
   * Prints the stack trace of the root cause.
   */
  @Override
  public void printStackTrace(PrintWriter s) {
    super.getCause().printStackTrace(s);
  }

  /**
   * Calls {@code toString()} on the root cause.
   */
  @Override
  public String toString() {
    return super.getCause().toString();
  }

  /**
   * Returns the cause of the root cause, so null!
   */
  @Override
  public synchronized Throwable getCause() {
    return super.getCause().getCause();
  }

  /**
   * Prints the stack trace of the root cause.
   */
  @Override
  public void printStackTrace() {
    super.getCause().printStackTrace();
  }

  /**
   * Prints the stack trace of the root cause.
   */
  @Override
  public void printStackTrace(PrintStream s) {
    super.getCause().printStackTrace(s);
  }

  /**
   * Returns the stack trace of the root cause.
   */
  @Override
  public StackTraceElement[] getStackTrace() {
    return super.getCause().getStackTrace();
  }

}
