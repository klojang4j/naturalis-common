package nl.naturalis.common.check;

import nl.naturalis.common.StringMethods;

import java.util.List;
import java.util.function.Function;

import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.MsgUtil.getPrefabMessage;

/**
 * Facilitates precondition and postcondition checking. See {@linkplain nl.naturalis.common.check package description}.
 *
 * @author Ayco Holleman
 */
public abstract class Check {

  static final String DEF_ARG_NAME = "argument";

  private static final Function<String, IllegalArgumentException> DEF_EXC_FACTORY =
          IllegalArgumentException::new;

  /**
   * Static factory method. Returns a new {@code IntCheck}.
   *
   * @param arg The argument
   * @return A new {@code IntCheck}
   */
  public static IntCheck<IllegalArgumentException> that(int arg) {
    return new IntCheck<>(arg, null, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing the provided argument.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @return A {@code Check} object suitable for testing the provided argument
   */
  public static <U> ObjectCheck<U, IllegalArgumentException> that(U arg) {
    return new ObjectCheck<>(arg, null, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing integers.
   *
   * @param arg The argument
   * @param argName The name of the argument
   * @return A new {@code Check} object
   */
  public static IntCheck<IllegalArgumentException> that(int arg, String argName) {
    return new IntCheck<>(arg, argName, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing the provided argument.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @param argName The name of the argument
   * @return A new {@code Check} object
   */
  public static <U> ObjectCheck<U, IllegalArgumentException> that(U arg, String argName) {
    return new ObjectCheck<>(arg, argName, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing the provided argument. The
   * argument will have already passed the {@link CommonChecks#notNull() notNull} test.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @return A new {@code Check} object
   */
  public static <U> ObjectCheck<U, IllegalArgumentException> notNull(U arg)
          throws IllegalArgumentException {
    if (arg == null) {
      String msg = getPrefabMessage(CommonChecks.notNull(), false, null, null, null, null);
      throw DEF_EXC_FACTORY.apply(msg);
    }
    return new ObjectCheck<>(arg, null, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing the provided argument. The
   * argument will have already passed the {@link CommonChecks#notNull() notNull} test.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @param argName The name of the argument
   * @return A new {@code Check} object
   */
  public static <U> ObjectCheck<U, IllegalArgumentException> notNull(U arg, String argName)
          throws IllegalArgumentException {
    if (arg == null) {
      String msg = getPrefabMessage(CommonChecks.notNull(), false, argName, null, null, null);
      throw DEF_EXC_FACTORY.apply(msg);
    }
    return new ObjectCheck<>(arg, argName, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing integers.
   *
   * @param excFactory A {@code Function} that will produce the exception if a test fails. The {@code Function} will be
   * passed a {@code String} (the error message) and must return the {@code Exception} to be thrown
   * @param arg The argument
   * @param <X> The type of {@code Exception} thrown if the argument fails to pass a test
   * @return A {@code Check} object suitable for testing {@code int} arguments
   */
  public static <X extends Exception> IntCheck<X> on(Function<String, X> excFactory, int arg) {
    return new IntCheck<>(arg, null, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing the provided argument.
   *
   * @param <U> The type of the argument
   * @param <X> The type of {@code Exception} thrown if the argument fails to pass a test
   * @param excFactory A {@code Function} that will produce the exception if a test fails. The {@code Function} will be
   * passed a {@code String} (the error message) and must return the {@code Exception} to be thrown
   * @param arg The argument
   * @return A {@code Check} object suitable for testing the provided argument
   */
  public static <U, X extends Exception> ObjectCheck<U, X> on(
          Function<String, X> excFactory, U arg) {
    return new ObjectCheck<>(arg, null, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing integers.
   *
   * @param excFactory A {@code Function} that will produce the exception if a test fails. The {@code Function} will be
   * passed a {@code String} (the error message) and must return the {@code Exception} to be thrown
   * @param arg The argument
   * @param argName The name of the argument
   * @param <X> The type of {@code Exception} thrown if the argument fails to pass a test
   * @return A new {@code Check} object
   */
  public static <X extends Exception> IntCheck<X> on(
          Function<String, X> excFactory, int arg, String argName) {
    return new IntCheck<>(arg, argName, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing the provided argument.
   *
   * @param <U> The type of the argument
   * @param <X> The type of {@code Exception} thrown if the argument fails to pass a test
   * @param excFactory A {@code Function} that will produce the exception if a test fails. The {@code Function} will be
   * passed a {@code String} (the error message) and must return the {@code Exception} to be thrown
   * @param arg The argument
   * @param argName The name of the argument
   * @return A new {@code Check} object
   */
  public static <U, X extends Exception> ObjectCheck<U, X> on(
          Function<String, X> excFactory, U arg, String argName) {
    return new ObjectCheck<>(arg, argName, excFactory);
  }

  /**
   * Verifies that both {@code off} and {@code off + len} remain with the bounds of the specified byte array. If not, an
   * {@link IndexOutOfBoundsException} is thrown. Returns {@code off + len}.
   *
   * @param array The byte array
   * @param off The offset within byte array
   * @param len The length of the segment within the byte array
   * @see #offsetLength(int, int, int)
   */
  public static void offsetLength(byte[] array, int off, int len) {
    Check.notNull(array, "array");
    Check.offsetLength(array.length, off, len);
  }

  /**
   * Verifies that both {@code off} and {@code off + len} remain with the bounds of an array or array-like object of the
   * specified size. If not, an {@link IndexOutOfBoundsException} is thrown. Returns {@code off + len}.
   *
   * @param size The length of the array or array-like object from which to extract the segment
   * @param off The offset of the segment
   * @param len The length of the segment
   * @return The "to" index of the segment
   */
  public static int offsetLength(int size, int off, int len) {
    Check.on(indexOutOfBounds(), size | off | len, "size/offset/length").isNot(negative());
    return Check.on(indexOutOfBounds(), off + len, "offset + length").is(lte(), size).ok();
  }

  /**
   * Verifies that the specified {@code from} and {@code to} indices can be used to extract a substring from the
   * specified {@code String}. This method already performs a null-check on the {@code src} argument, so there is no
   * need to do that first, so there is no need to do that first.
   *
   * @param src The string to extract the substring from
   * @param from The start index
   * @param to The end index
   * @see #fromTo(int, int, int)
   * @see String#substring(int, int)
   */
  public static void fromTo(String src, int from, int to) {
    Check.notNull(src, "src");
    Check.fromTo(src.length(), from, to);
  }

  /**
   * Verifies that the specified {@code from} and {@code to} indices can be used to extract a sublist from the specified
   * {@code List}. This method already performs a null-check on the {@code src} argument, so there is no need to do that
   * first.
   *
   * @param src The string to extract the substring from
   * @param from The start index
   * @param to The end index
   * @see #fromTo(int, int, int)
   * @see List#subList(int, int)
   */
  public static <E> void fromTo(List<E> src, int from, int to) {
    Check.notNull(src, "src");
    Check.fromTo(src.size(), from, to);
  }

  /**
   * Verifies that the specified {@code from} and {@code to} indices can be used for typical segment extraction methods
   * like {@link String#substring(int, int) String.substring} and {@link java.util.List#subList(int, int) List.subList}.
   * Note that both {@code from} and to {@code to} may be one position past the end of the {@code String}, {@code List},
   * etc. In other words, they may both be equal to {@code len}.
   *
   * @param size The length of the {@code String}, {@code List}, (etc.) from which to extract the segment
   * @param from The start index of the segment
   * @param to The end index of the segment
   */
  public static void fromTo(int size, int from, int to) {
    Check.on(indexOutOfBounds(), from, "from").isNot(negative()).isNot(gt(), to);
    Check.on(indexOutOfBounds(), to, "to").isNot(gt(), size);
  }

  /**
   * Throws an {@code IllegalArgumentException} with the specified message and message arguments. The method is still
   * declared to return a value of type &lt;U&gt; so it can be used as the expression for a {@code return statement}.
   *
   * @param <U> The type of the object that would have been returned if it had passed the checks
   * @param msg The message
   * @param msgArgs The message argument
   * @return Nothing, but allows {@code fail} to be used as the expression in a {@code return} statement
   */
  public static <U> U fail(String msg, Object... msgArgs) {
    return fail(DEF_EXC_FACTORY, msg, msgArgs);
  }

  /**
   * Throws the exception created by the specified exception factory.
   *
   * @param <U> The type of the object that would have been returned if it had passed the checks
   * @param <X> The type of the exception
   * @param excFactory The exception supplier
   * @return Nothing, but allows {@code fail} to be used as the expression in a {@code return} statement
   * @throws X The exception that is thrown
   */
  public static <U, X extends Exception> U fail(Function<String, X> excFactory) throws X {
    return fail(excFactory, StringMethods.EMPTY);
  }

  /**
   * Throws an exception created by the specified exception factory with the specified message and message arguments.
   *
   * @param <U> The type of the object that would have been returned if it had passed the checks
   * @param <X> The type of the exception
   * @param msg The message
   * @param msgArgs The message argument
   * @return Nothing, but allows {@code fail} to be used as the expression in a {@code return} statement
   * @throws X The exception that is thrown
   */
  public static <U, X extends Exception> U fail(
          Function<String, X> excFactory, String msg, Object... msgArgs) throws X {
    return Check.on(excFactory, 1).is(eq(), 0, msg, msgArgs).ok(i -> null);
  }
}
