package nl.naturalis.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
   * Returns the exception message and stack trace of the root cause of {@code exc}, using the
   * specified string(s) to filter stack trace elements. If the {@link
   * StackTraceElement#getClassName() class name} of the stack trace element {@link
   * String#contains(CharSequence) contains} the filter string, the stack trace element will be
   * included in the output.
   *
   * @param exc The exception
   * @param filter One or more filters on stacke trace elemenets
   * @return The root stack trace as a string
   */
  public static String getRootStackTraceAsString(Throwable exc, String... filter) {
    Check.notNull(exc, "exc");
    Check.notNull(filter, "filter");
    ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
    PrintStream pw = new PrintStream(baos);
    Throwable t = getRootCause(exc);
    pw.println(t);
    for (StackTraceElement ste : t.getStackTrace()) {
      for (String f : filter) {
        if (ste.getClassName().contains(f)) {
          pw.println("\tat " + ste);
          break;
        }
      }
    }
    return baos.toString(StandardCharsets.UTF_8);
  }

  /**
   * Returns the stack trace of the root cause of the specified exception, using the specified
   * string(s) to filter stack trace elements. If the {@link StackTraceElement#getClassName() class
   * name} of the stack trace element {@link String#contains(CharSequence) contains} the filter
   * string, the stack trace element will be included in the returned array.
   *
   * @param exc The exception
   * @param filter One or more filters on stacke trace elemenets
   * @return The root stack trace
   */
  public static StackTraceElement[] getRootStackTrace(Throwable exc, String... filter) {
    Check.notNull(exc, "exc");
    Check.notNull(filter, "filter");
    return Arrays.stream(getRootCause(exc).getStackTrace())
        .filter(
            ste -> {
              for (String f : filter) {
                if (ste.getClassName().contains(f)) {
                  return true;
                }
              }
              return false;
            })
        .toArray(StackTraceElement[]::new);
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
   * @param msgArgs The {@code String.format} message arguments to the custom message
   * @return The specified throwable or a {@code RuntimeException} wrapping it
   */
  public static RuntimeException wrap(Throwable exc, String customMessage, Object... msgArgs) {
    if (Check.notNull(exc, "exc").ok() instanceof RuntimeException) {
      return (RuntimeException) exc;
    }
    Check.notNull(customMessage, "customMessage");
    if (msgArgs.length == 0) {
      return new RuntimeException(customMessage, exc);
    }
    return new RuntimeException(String.format(customMessage, msgArgs), exc);
  }

  /**
   * Returns the specified throwable if it already is a {@link RuntimeException}, else a {@code
   * RuntimeException} produced by the specified function.
   *
   * @param <T> The type of the {@code RuntimeException}
   * @param exc The exception to be wrapped if it is not a {@code RuntimeException}
   * @param excProducer The producer of the {@code RuntimeException}, typically the single-argument
   *     constructor of an {@code Exception} that takes a {@code Throwable} argument
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
   * RuntimeException} produced by the specified function. For example:
   *
   * <blockquote>
   *
   * <pre>{@code
   * try {
   *  // stuff ...
   * } catch(Throwable t) {
   *  throw ExceptionMethods.wrap(t, "Something went wrong", MyRuntimeException::new);
   * }
   * }</pre>
   *
   * </blockquote>
   *
   * @param <T> The type of the {@code RuntimeException}
   * @param exc The exception to be wrapped if it is not a {@code RuntimeException}
   * @param excProducer The producer of the {@code RuntimeException}, typically the two-argument
   *     constructor of an {@code Exception} that takes a {@code String} argument and a @code
   *     Throwable} argument
   * @param customMessage A custom message passed on to the {@code RuntimeException} wrapping the
   *     original exception
   * @param msgArgs The {@code String.format} message arguments to the custom message
   * @return The specified throwable or a {@code RuntimeException} wrapping it
   */
  public static <T extends RuntimeException> RuntimeException wrap(
      Throwable exc,
      BiFunction<String, Throwable, T> excProducer,
      String customMessage,
      Object... msgArgs) {
    if (Check.notNull(exc, "exc").ok() instanceof RuntimeException) {
      return (RuntimeException) exc;
    }
    Check.notNull(customMessage, "customMessage");
    Check.notNull(excProducer, "excProducer");
    if (msgArgs.length == 0) {
      return excProducer.apply(customMessage, exc);
    }
    return excProducer.apply(String.format(customMessage, msgArgs), exc);
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
    return wrap(exc, UncheckedException::new, customMessage);
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
