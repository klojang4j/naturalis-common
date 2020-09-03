package nl.naturalis.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import nl.naturalis.common.exception.ExceptionOrigin;
import nl.naturalis.common.exception.UncheckedException;

/**
 * Methods related to exception handling.
 *
 * @author Ayco Holleman
 */
public final class ExceptionMethods {

  private ExceptionMethods() {}

  /**
   * Returns the root cause of the provided throwable, or the throwable itself if it has no cause.
   *
   * @param exc The exception whose root cause to retrieve
   * @return The root cause of the exception
   */
  public static Throwable getRootCause(Throwable exc) {
    Check.notNull(exc, "exc");
    while (exc.getCause() != null) {
      exc = exc.getCause();
    }
    return exc;
  }

  /**
   * Returns the stack trace of the root cause of {@code exc} as a string.
   *
   * @param exc The exception
   * @return The root stack trace as a string
   */
  public static String getRootStackTraceAsString(Throwable exc) {
    Check.notNull(exc, "exc");
    ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
    getRootCause(exc).printStackTrace(new PrintStream(baos));
    return baos.toString(StandardCharsets.UTF_8);
  }

  /**
   * Returns a detailed exception message that includes the class, method and line number of the
   * absolute origin of the provided exception. Equivalent to {@code new
   * ExceptionSource(getRootCause(t)).getDetailedMessage()}.
   *
   * @see ExceptionOrigin#getDetailedMessage()
   * @param exc The exception to extract the extra information from
   * @return A detailed exception message
   */
  public static String getDetailedMessage(Throwable exc) {
    Check.notNull(exc, "exc");
    return new ExceptionOrigin(getRootCause(exc)).getDetailedMessage();
  }

  /**
   * Returns a detailed exception message that traces the thrown exception back to a particular
   * package or class in your own code.
   *
   * <pre>
   * try {
   *
   *   // stuff
   *
   * } catch (IOException e) {
   *   throw ExceptionMethods.unckeck(getDetailedMessage(e), e);
   * }
   * </pre>
   *
   * @param exc The exception to extract the extra information from
   * @param search The (partial) name of the package or class you want to zoom in on
   * @return A detailed exception message
   */
  public static String getDetailedMessage(Throwable exc, String search) {
    Check.notNull(exc, "exc");
    return new ExceptionOrigin(exc, search).getDetailedMessage();
  }

  /**
   * Returns the provided throwable if it already is a {@link RuntimeException}, else a {@code
   * RuntimeException} wrapping the throwable.
   *
   * @param exc A checked or unchecked exception
   * @return The provided throwable or a {@code RuntimeException} wrapping it
   */
  public static RuntimeException wrap(Throwable exc) {
    if (Check.notNull(exc, "exc") instanceof RuntimeException) {
      return (RuntimeException) exc;
    }
    return new RuntimeException(exc);
  }

  /**
   * Returns the provided throwable if it already is a {@link RuntimeException}, else an {@link
   * UncheckedException} wrapping the throwable.
   *
   * @param exc A checked or unchecked exception
   * @param customMessage A custom message to pass to the constructor of {@code UncheckedException}
   * @return The provided throwable or an {@code UncheckedException} wrapping it
   */
  public static RuntimeException uncheck(Throwable exc, String customMessage) {
    if (Check.notNull(exc, "exc") instanceof RuntimeException) {
      return (RuntimeException) exc;
    }
    return new UncheckedException(customMessage, exc);
  }

  /**
   * Returns the provided throwable if it already is a {@link RuntimeException}, else an {@link
   * UncheckedException} wrapping the throwable.
   *
   * @param exc A checked or unchecked exception
   * @return The provided throwable or an {@code UncheckedException} wrapping it
   */
  public static RuntimeException uncheck(Throwable exc) {
    if (Check.notNull(exc, "exc") instanceof RuntimeException) {
      return (RuntimeException) exc;
    }
    return new UncheckedException(exc);
  }
}
