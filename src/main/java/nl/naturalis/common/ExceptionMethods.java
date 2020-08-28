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
 *
 */
public class ExceptionMethods {

  private ExceptionMethods() {}

  /**
   * Returns the root cause of the provided throwable, or the throwable itself if
   * it has no cause.
   *
   * @param t
   * @return
   */
  public static Throwable getRootCause(Throwable t) {
    while (t.getCause() != null) {
      t = t.getCause();
    }
    return t;
  }

  /**
   * Returns the stack trace of the root cause of {@code t} as a string.
   *
   * @param t
   * @return
   */
  public static String getRootStackTraceAsString(Throwable t) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
    getRootCause(t).printStackTrace(new PrintStream(baos));
    return baos.toString(StandardCharsets.UTF_8);
  }

  /**
   * Returns a detailed exception message that includes the class, method and line
   * of the absolute origin of the provided exception. Equivalent to
   * {@code new ExceptionSource(getRootCause(t)).getDetailedMessage()}.
   *
   * @see ExceptionOrigin#getDetailedMessage()
   *
   * @param t The exception to extract the extra information from
   * @return
   */
  public static String getDetailedMessage(Throwable t) {
    return new ExceptionOrigin(getRootCause(t)).getDetailedMessage();
  }

  /**
   * Returns a detailed exception message that gives better insight into where
   * exactly in your own code things flew off the rails. Works well with
   * {@link #uncheck(Throwable) ExceptionMethods.uncheck}. For example:
   *
   * <pre>
   * try {
   *
   *   // stuff
   *
   * } catch (IOException e) {
   *   throw unckeck(getDetailedMessage(e), e);
   * }
   * </pre>
   *
   * @param t The exception to extract the extra information from
   * @param origin The (partial) name of the package or class you want to zoom in
   *        on
   * @return
   */
  public static String getDetailedMessage(Throwable t, String origin) {
    return new ExceptionOrigin(t, origin).getDetailedMessage();
  }

  /**
   * Returns the provided throwable if it already is a {@link RuntimeException},
   * else a {@code RuntimeException} wrapping the throwable.
   *
   * @param t A checked or unchecked exception
   * @return The provided throwable or a {@code RuntimeException} wrapping it
   */
  public static RuntimeException wrap(Throwable t) {
    if (t instanceof RuntimeException) {
      return (RuntimeException) t;
    }
    return new RuntimeException(t);
  }

  /**
   * Returns the provided throwable if it already is a {@link RuntimeException},
   * else an {@link UncheckedException} wrapping the throwable.
   *
   * @param t A checked or unchecked exception
   * @param customMessage A custom message to pass to the constructor of
   *        {@code UncheckedException}
   * @return The provided throwable or an {@code UncheckedException} wrapping it
   */
  public static RuntimeException uncheck(Throwable t, String customMessage) {
    if (t instanceof RuntimeException) {
      return (RuntimeException) t;
    }
    return new UncheckedException(customMessage, t);
  }

  /**
   * Returns the provided throwable if it already is a {@link RuntimeException},
   * else an {@link UncheckedException} wrapping the throwable.
   *
   * @param t A checked or unchecked exception
   * @return The provided throwable or an {@code UncheckedException} wrapping it
   */
  public static RuntimeException uncheck(Throwable t) {
    if (t instanceof RuntimeException) {
      return (RuntimeException) t;
    }
    return new UncheckedException(t);
  }

}
