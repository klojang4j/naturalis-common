package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.exception.ExceptionOrigin;
import nl.naturalis.common.exception.RootException;
import nl.naturalis.common.exception.UncheckedException;
import nl.naturalis.common.io.UnsafeByteArrayOutputStream;
import nl.naturalis.common.x.Param;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Methods related to exception handling.
 *
 * @author Ayco Holleman
 */
public final class ExceptionMethods {

  private static final String EXCEPTION = "exception";

  private ExceptionMethods() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the root cause of the provided throwable, or the throwable itself if it
   * has no cause.
   *
   * @param exc the exception whose root cause to retrieve
   * @return the root cause of the exception
   */
  public static Throwable getRootCause(Throwable exc) {
    Check.notNull(exc, EXCEPTION);
    while (exc.getCause() != null) {
      exc = exc.getCause();
    }
    return exc;
  }

  /**
   * Returns the stack trace of the root cause of {@code exc} as a {@code String}.
   *
   * @param exc the exception
   * @return the root stack trace as a string
   */
  public static String getRootStackTraceAsString(Throwable exc) {
    Check.notNull(exc, EXCEPTION);
    UnsafeByteArrayOutputStream bucket = new UnsafeByteArrayOutputStream(2048);
    getRootCause(exc).printStackTrace(new PrintStream(bucket));
    return bucket.toString().strip();
  }

  /**
   * Returns the exception message and stack trace of the root cause of {@code exc},
   * using the specified string(s) to filter stack trace elements. {@link
   * StackTraceElement#getClassName()} contains the filter string, the stack trace
   * element will be included in the output.
   *
   * @param exc the exception
   * @param filter One or more filters on stack trace elements
   * @return the root stack trace as a string
   */
  public static String getRootStackTraceAsString(Throwable exc, String... filter) {
    Check.notNull(exc, EXCEPTION);
    Check.notNull(filter, Param.FILTER);
    UnsafeByteArrayOutputStream baos = new UnsafeByteArrayOutputStream(1024);
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
    return baos.toString().strip();
  }

  /**
   * Returns the stack trace of the root cause of the specified exception, using the
   * specified string(s) to filter stack trace elements. If the {@link
   * StackTraceElement#getClassName() class name} of the stack trace element {@link
   * String#contains(CharSequence) contains} the filter string, the stack trace
   * element will be included in the returned array.
   *
   * @param exc the exception
   * @param filter One or more filters on stack trace elements
   * @return the root stack trace
   */
  public static StackTraceElement[] getRootStackTrace(Throwable exc,
      String... filter) {
    Check.notNull(exc, EXCEPTION);
    Check.notNull(filter, Param.FILTER);
    if (filter.length == 0) {
      return getRootCause(exc).getStackTrace();
    }
    var trace = new ArrayList<StackTraceElement>();
    for (StackTraceElement ste : getRootCause(exc).getStackTrace()) {
      for (String f : filter) {
        if (ste.getClassName().contains(f)) {
          trace.add(ste);
          break;
        }
      }
    }
    return trace.toArray(StackTraceElement[]::new);
  }

  /**
   * Returns a detailed exception message that includes the class, method and line
   * number of the absolute origin of the provided exception. Equivalent to {@code
   * new ExceptionSource(getRootCause(t)).getDetailedMessage()}.
   *
   * @param exc the exception to extract the extra information from
   * @return A detailed exception message
   * @see ExceptionOrigin#getDetailedMessage()
   */
  public static String getDetailedMessage(Throwable exc) {
    Check.notNull(exc, EXCEPTION);
    return new ExceptionOrigin(getRootCause(exc)).getDetailedMessage();
  }

  /**
   * Returns a detailed exception message that traces the exception back to the point
   * of origin within your own code (or any other code base of interest).
   *
   * <blockquote><pre>{@code
   * try {
   *
   *   // stuff ...
   *
   * } catch (IOException e) {
   *   throw ExceptionMethods.unckeck(getDetailedMessage(e, "nl.naturalis"), e);
   * }
   * }</pre></blockquote>
   *
   * @param exc the exception to extract the extra information from
   * @param search The (partial) name of the package or class you want to zoom in
   *     on
   * @return A detailed exception message
   * @see ExceptionOrigin#getDetailedMessage()
   */
  public static String getDetailedMessage(Throwable exc, String search) {
    Check.notNull(exc, EXCEPTION);
    return new ExceptionOrigin(exc, search).getDetailedMessage();
  }

  /**
   * Returns the specified throwable if it already is a {@code RuntimeException},
   * else a {@code RuntimeException} wrapping the throwable.
   *
   * @param exc A checked or unchecked exception
   * @return the specified throwable or a {@code RuntimeException} wrapping it
   */
  public static RuntimeException wrap(Throwable exc) {
    if (Check.notNull(exc, EXCEPTION).ok() instanceof RuntimeException rte) {
      return rte;
    }
    return new RuntimeException(exc);
  }

  /**
   * Returns the specified throwable if it already is a {@code RuntimeException},
   * else a {@code RuntimeException} wrapping the throwable.
   *
   * @param exc A checked or unchecked exception
   * @param customMessage A custom message passed on to the {@code
   *     RuntimeException} wrapping the original exception
   * @param msgArgs The {@code String.format} message arguments to the custom
   *     message
   * @return the specified throwable or a {@code RuntimeException} wrapping it
   */
  public static RuntimeException wrap(Throwable exc,
      String customMessage,
      Object... msgArgs) {
    Check.notNull(exc, EXCEPTION);
    Check.notNull(customMessage, "customMessage");
    Check.notNull(msgArgs, Param.MSG_ARGS);
    if (exc instanceof RuntimeException rte) {
      return rte;
    }
    if (msgArgs.length == 0) {
      return new RuntimeException(customMessage, exc);
    }
    return new RuntimeException(String.format(customMessage, msgArgs), exc);
  }

  /**
   * Returns the specified throwable if it already is a {@code RuntimeException},
   * else a {@code RuntimeException} produced by the specified function.
   *
   * @param <T> The type of the {@code RuntimeException}
   * @param exc the exception to be wrapped if it is not a {@code
   *     RuntimeException}
   * @param exceptionFactory The producer of the {@code RuntimeException},
   *     typically the one-arg constructor of an {@code Exception} that takes a
   *     {@code Throwable} argument ("cause")
   * @return the specified throwable or a {@code RuntimeException} wrapping it
   */
  public static <T extends RuntimeException> RuntimeException wrap(
      Throwable exc, Function<Throwable, T> exceptionFactory) {
    Check.notNull(exc, EXCEPTION);
    Check.notNull(exceptionFactory, "exceptionFactory");
    if (exc instanceof RuntimeException rte) {
      return rte;
    }
    return exceptionFactory.apply(exc);
  }

  /**
   * Returns the specified throwable if it already is a {@code RuntimeException},
   * else a {@code RuntimeException} produced by the specified function. For
   * example:
   *
   * <blockquote><pre>{@code
   * try {
   *  // stuff ...
   * } catch(Throwable t) {
   *  throw ExceptionMethods.wrap(t, "Bad stuff happening", IllegalStateException::new);
   * }
   * }</pre></blockquote>
   *
   * @param <T> The type of the {@code RuntimeException}
   * @param exception the exception to be wrapped if it is not a {@code
   *     RuntimeException}
   * @param exceptionFactory The producer of the {@code RuntimeException},
   *     typically the two-argument constructor of an {@code Exception} that takes a
   *     {@code String} argument and a {@code Throwable} argument
   * @param customMessage A custom message passed on to the {@code
   *     RuntimeException} wrapping the original exception
   * @param msgArgs The {@code String.format} message arguments to the custom
   *     message
   * @return the specified throwable or a {@code RuntimeException} wrapping it
   */
  public static <T extends RuntimeException> RuntimeException wrap(
      Throwable exception,
      BiFunction<String, Throwable, T> exceptionFactory,
      String customMessage,
      Object... msgArgs) {
    if (Check.notNull(exception, EXCEPTION).ok() instanceof RuntimeException) {
      return (RuntimeException) exception;
    }
    Check.notNull(customMessage, "customMessage");
    Check.notNull(exceptionFactory, "exceptionFactory");
    if (msgArgs.length == 0) {
      return exceptionFactory.apply(customMessage, exception);
    }
    return exceptionFactory.apply(String.format(customMessage, msgArgs), exception);
  }

  /**
   * Returns the specified throwable if it already is a {@code RuntimeException},
   * else an {@link UncheckedException} wrapping the throwable. This method is
   * primarily meant to "uncheck" checked exceptions that you cannot in practice
   * properly deal with, like some {@code IOException}s thrown, according to the
   * Javadocs, "if an I/O error occurs".
   *
   * @param exc A checked or unchecked exception
   * @return the provided {@code Throwable} or an {@code UncheckedException} wrapping
   *     it
   * @see UncheckedException
   * @see UncheckedException
   */
  public static RuntimeException uncheck(Throwable exc) {
    return wrap(exc, UncheckedException::new);
  }

  /**
   * Returns the specified throwable if it already is a {@code RuntimeException},
   * else an {@link UncheckedException} wrapping the throwable. This method is
   * primarily meant to "uncheck" checked exceptions that you cannot in practice
   * properly deal with, like some {@code IOException}s thrown, according to the
   * Javadocs, "if an I/O error occurs".
   *
   * @param exc A checked or unchecked exception
   * @param customMessage A custom message to pass to the constructor of {@code
   *     UncheckedException}
   * @return the provided {@code Throwable} or an {@code UncheckedException} wrapping
   *     it
   * @see UncheckedException
   */
  public static RuntimeException uncheck(Throwable exc, String customMessage) {
    return wrap(exc, UncheckedException::new, customMessage);
  }

  /**
   * Returns the specified throwable if it already is a {@code RuntimeException},
   * else an {@link RootException} exception wrapping the <i>root cause</i>  of the
   * provided exception.
   *
   * @param exc A checked or unchecked exception
   * @return the provided {@code Throwable} or an {@code UncheckedException} wrapping
   *     it
   * @see RootException
   */
  public static RuntimeException rootCause(Throwable exc) {
    return wrap(exc, RootException::new);
  }

  /**
   * Returns the specified throwable if it already is a {@code RuntimeException},
   * else an {@link RootException} exception wrapping the <i>root cause</i> of the
   * provided exception.
   *
   * @param exc A checked or unchecked exception
   * @param customMessage A custom message to pass to the constructor of {@code
   *     UncheckedException}
   * @return the provided {@code Throwable} or a {@code RootException} wrapping its
   *     root cause
   * @see RootException
   */
  public static RuntimeException rootCause(Throwable exc, String customMessage) {
    return wrap(exc, RootException::new, customMessage);
  }

}
