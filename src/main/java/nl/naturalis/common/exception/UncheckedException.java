package nl.naturalis.common.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Optional;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;

/**
 * A subclass of {@code RuntimeException} that behaves just like {@link Exception} it wraps. All
 * methods overridden from {@code Exception} do nothing but delegate to the same method on the
 * wrapped exception. This behaviour might be useful when wrapping checked exceptions that in
 * practice cannot sensibly be dealt with. This is often the case with, for example, {@code
 * IOException}, {@code SQLException} and other exceptions where the Javadocs basically specify that
 * they happen "when something goes wrong". These exceptions then become runtime exceptions for all
 * practical purposes.
 *
 * <p>By hiding completely behind the wrapped exception an {@code UncheckedException} has a less
 * cumbersome stack trace than a straight {@code RuntimeException}. Beware of surprises though,
 * because you will only know you are dealing with an {@code UncheckedException} by means of the
 * {@code instanceof} operator or by calling {@link Object#getClass() getClass()} on the exception
 * instance.
 *
 * @see RootException
 * @see ExceptionMethods#uncheck(Throwable)
 * @author Ayco Holleman
 */
public class UncheckedException extends RuntimeException {

  private final Optional<String> customMessage;

  /**
   * Creates a {@code UncheckedException} wrapping the provided {@code Throwable}.
   *
   * @param cause
   */
  public UncheckedException(Throwable cause) {
    this(null, Check.notNull(cause, "cause").ok(UncheckedException::pealAway));
  }

  /**
   * Creates a {@code UncheckedException} with the provided custome message, wrapping the provided
   * {@code Throwable}.
   *
   * @param cause
   */
  public UncheckedException(String message, Throwable cause) {
    // Peal until we find something other than an UncheckedException
    super(Check.notNull(cause, "cause").ok(UncheckedException::pealAway));
    this.customMessage = Optional.ofNullable(message);
  }

  /**
   * Returns the exception wrapped by this {@code UncheckedException}. Note that {@link #getCause()}
   * does <i>not</i> return that exception. It returns the <i>cause</i> of that exception.
   *
   * @return The exception directly wrapped by this {@code UncheckedException}
   */
  @SuppressWarnings("unchecked")
  public <E extends Throwable> E unwrap() {
    return (E) super.getCause();
  }

  /** Returns the message of the wrapped {@code Exception}. */
  @Override
  public String getMessage() {
    return super.getCause().getMessage();
  }

  /**
   * Returns an {@code Optional} containing the custom message passed in through the {@link
   * #UncheckedException(String, Throwable) constructor} or an empty {@code Optional} if the
   * single-arg constructor was used.
   *
   * @return An {@code Optional} containing the custom message passed in through the constructor
   */
  public Optional<String> getCustomMessage() {
    return customMessage;
  }

  /** Prints the stack trace of the wrapped {@code Exception}. */
  @Override
  public void printStackTrace(PrintWriter s) {
    super.getCause().printStackTrace(s);
  }

  /** Calls {@code toString()} on the wrapped {@code Exception}. */
  @Override
  public String toString() {
    return super.getCause().toString();
  }

  /** Returns the cause of the cause! */
  @Override
  public synchronized Throwable getCause() {
    return super.getCause().getCause();
  }

  /** Prints the stack trace of the wrapped {@code Exception}. */
  @Override
  public void printStackTrace() {
    super.getCause().printStackTrace();
  }

  /** Prints the stack trace of the wrapped {@code Exception}. */
  @Override
  public void printStackTrace(PrintStream s) {
    super.getCause().printStackTrace(s);
  }

  /** Returns the stack trace of the wrapped {@code Exception}. */
  @Override
  public StackTraceElement[] getStackTrace() {
    return super.getCause().getStackTrace();
  }

  private static Throwable pealAway(Throwable t) {
    while (t.getClass() == UncheckedException.class) {
      t = ((UncheckedException) t).unwrap();
    }
    return t;
  }
}
