package nl.naturalis.common.exception;

import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * A subclass of {@code RuntimeException} that behaves just like {@link Exception} it
 * wraps. It overrides all methods from {@code Exception} by calling the same method
 * on the wrapped exception. For example {@code uncheckedException.getCause ()}
 * returns {@code wrappedException.getCause()} (the cause of the cause!). You
 * <i>can</i> provide a custom message, though, which you can retrieve through
 * {@link #getCustomMessage()} ({@link #getMessage()} returns {@code
 * wrappedException.getMessage()}).
 *
 * <p>This behaviour can be useful when wrapping checked exceptions that in
 * practice cannot sensibly be dealt with. This is often the case with, for example,
 * {@code IOException}, {@code SQLException} and other exceptions where the Javadocs
 * basically say that they are thrown "when something goes wrong". (No kiddin' ...)
 * These exceptions are runtime exceptions for all practical purposes.
 *
 * <p>By hiding completely behind the wrapped exception, an {@code
 * UncheckedException} has a less cumbersome stack trace than a straight {@code
 * RuntimeException}. Beware of surprises though, as the only way of knowing you are
 * dealing with an {@code UncheckedException} is by calling {@code getClass()} on
 * it.
 *
 * <p>You don't need to worry about wrapping an {code UncheckedException} with an
 * {@code UncheckedException}. The constructors peal away all causes until they find
 * something that is not an {@code UncheckedException}.
 *
 * @author Ayco Holleman
 * @see RootException
 * @see ExceptionMethods#uncheck(Throwable)
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
   * Creates a {@code UncheckedException} with the provided custom message, wrapping
   * the provided {@code Throwable}.
   *
   * @param cause
   */
  public UncheckedException(String message, Throwable cause) {
    // Peal until we find something other than an UncheckedException
    super(Check.notNull(cause, "cause").ok(UncheckedException::pealAway));
    this.customMessage = Optional.ofNullable(message);
  }

  /**
   * Returns the exception wrapped by this {@code UncheckedException}. Note that
   * {@link #getCause()} does <i>not</i> return that exception. It returns the
   * <i>cause</i> of that exception.
   *
   * @return The exception directly wrapped by this {@code UncheckedException}
   */
  @SuppressWarnings("unchecked")
  public <E extends Throwable> E unwrap() {
    return (E) super.getCause();
  }

  /**
   * Returns the message of the wrapped {@code Exception}.
   */
  @Override
  public String getMessage() {
    return super.getCause().getMessage();
  }

  /**
   * Returns an {@code Optional} containing the custom message passed in through the
   * {@link #UncheckedException(String, Throwable) constructor} or an empty {@code
   * Optional} if the single-arg constructor was used.
   *
   * @return An {@code Optional} containing the custom message passed in through the
   *     constructor
   */
  public Optional<String> getCustomMessage() {
    return customMessage;
  }

  /**
   * Prints the stack trace of the wrapped {@code Exception}.
   */
  @Override
  public void printStackTrace(PrintWriter s) {
    super.getCause().printStackTrace(s);
  }

  /**
   * Calls {@code toString()} on the wrapped {@code Exception}.
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

  private static Throwable pealAway(Throwable t) {
    while (t.getClass() == UncheckedException.class) {
      t = ((UncheckedException) t).unwrap();
    }
    return t;
  }

}
