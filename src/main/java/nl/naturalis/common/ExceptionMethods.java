package nl.naturalis.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;
import java.util.function.Function;
import nl.naturalis.common.check.Check;
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
   * Returns the stack trace of the root cause of {@code exc} as a {@code String}.
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
   * Returns a detailed exception message that traces the exception back to the point of origin
   * within your own code (or any other code base of interest).
   *
   * <p>
   *
   * <pre>
   * try {
   *
   *   // stuff
   *
   * } catch (IOException e) {
   *   throw ExceptionMethods.unckeck(getDetailedMessage(e, "nl.naturalis"), e);
   * }
   * </pre>
   *
   * @param exc The exception to extract the extra information from
   * @param search The (partial) name of the package or class you want to zoom in on
   * @return A detailed exception message
   */
  public static String getDetailedMessage(Throwable exc, String search) {
    return new ExceptionOrigin(Check.notNull(exc, "exc").ok(), search).getDetailedMessage();
  }

  /**
   * Returns the specified throwable if it already is a {@link RuntimeException}, else a {@code
   * RuntimeException} wrapping the throwable.
   *
   * @param exc A checked or unchecked exception
   * @return The specified throwable or a {@code RuntimeException} wrapping it
   */
  public static RuntimeException wrap(Throwable exc) {
    if (Check.notNull(exc, "exc").ok() instanceof RuntimeException) {
      return (RuntimeException) exc;
    }
    return new RuntimeException(exc);
  }

  /**
   * Returns the specified throwable if it already is a {@link RuntimeException}, else a {@code
   * RuntimeException} wrapping the throwable.
   *
   * @param exc A checked or unchecked exception
   * @param customMessage A custom message passed on to the {@code RuntimeException} wrapping the
   *     original exception
   * @return The specified throwable or a {@code RuntimeException} wrapping it
   */
  public static RuntimeException wrap(Throwable exc, String customMessage) {
    if (Check.notNull(exc, "exc").ok() instanceof RuntimeException) {
      return (RuntimeException) exc;
    }
    Check.notNull(customMessage, "customMessage");
    return new RuntimeException(customMessage, exc);
  }

  /**
   * Returns the specified throwable if it already is a {@link RuntimeException}, else a {@code
   * RuntimeException} producer by the specified function.
   *
   * @param <T> The type of the {@code RuntimeException}
   * @param exc The exception to be wrapped if it is not a {@code RuntimeException}
   * @param excProducer The producer of the {@code RuntimeException}, basically reflecting the
   *     one-argument constructor (Throwable cause) of an {@code Exception}.
   * @return The specified throwable or a {@code RuntimeException} wrapping it
   */
  public static <T extends RuntimeException> RuntimeException wrap(
      Throwable exc, Function<Throwable, T> excProducer) {
    if (Check.notNull(exc, "exc").ok() instanceof RuntimeException) {
      return (RuntimeException) exc;
    }
    Check.notNull(excProducer, "excProducer");
    return excProducer.apply(exc);
  }

  /**
   * Returns the specified throwable if it already is a {@link RuntimeException}, else a {@code
   * RuntimeException} producer by the specified function. For example:
   *
   * <p>
   *
   * <pre>
   * try {
   *  // stuff ...
   * } catch(Throwable t) {
   *  throw ExceptionMethods.wrap(t, "Something went wrong", MyRuntimeException::new);
   * }
   * </pre>
   *
   * @param <T> The type of the {@code RuntimeException}
   * @param exc The exception to be wrapped if it is not a {@code RuntimeException}
   * @param excProducer The producer of the {@code RuntimeException}, basically reflecting the
   *     two-argument constructor (String message, Throwable cause) of an {@code Exception}.
   * @return The specified throwable or a {@code RuntimeException} wrapping it
   */
  public static <T extends RuntimeException> RuntimeException wrap(
      Throwable exc, String customMessage, BiFunction<String, Throwable, T> excProducer) {
    if (Check.notNull(exc, "exc").ok() instanceof RuntimeException) {
      return (RuntimeException) exc;
    }
    Check.notNull(customMessage, "customMessage");
    Check.notNull(excProducer, "excProducer");
    return excProducer.apply(customMessage, exc);
  }

  /**
   * Returns the specified throwable if it already is a {@link RuntimeException}, else an {@link
   * UncheckedException} wrapping the throwable. This method is primarily meant to "uncheck" checked
   * exceptions that you cannot in practice properly deal with, like some {@code IOException}s
   * thrown, according to the Javadocs, "if an I/O error occurs", or some of the {@code
   * SQLException}s thrown from the {@code java.sql} package.
   *
   * @param exc A checked or unchecked exception
   * @param customMessage A custom message to pass to the constructor of {@code UncheckedException}
   * @return The provided {@code Throwable} or an {@code UncheckedException} wrapping it
   */
  public static RuntimeException uncheck(Throwable exc, String customMessage) {
    return wrap(exc, customMessage, UncheckedException::new);
  }

  /**
   * Returns the specified throwable if it already is a {@link RuntimeException}, else an {@link
   * UncheckedException} wrapping the throwable. This method is primarily meant to "uncheck" checked
   * exceptions that you cannot in practice properly deal with, like some {@code IOException}s
   * thrown, according to the Javadocs, "if an I/O error occurs", or some of the {@code
   * SQLException}s thrown from the {@code java.sql} package.
   *
   * @param exc A checked or unchecked exception
   * @return The provided {@code Throwable} or an {@code UncheckedException} wrapping it
   */
  public static RuntimeException uncheck(Throwable exc) {
    return wrap(exc, UncheckedException::new);
  }
}
