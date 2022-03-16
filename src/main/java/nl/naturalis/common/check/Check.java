package nl.naturalis.common.check;

import nl.naturalis.common.StringMethods;

import java.util.List;
import java.util.function.Function;

import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.MsgUtil.getPrefabMessage;

/**
 * Facilitates precondition and postcondition checking. See {@linkplain nl.naturalis.common.check
 * package description}.
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
   * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
   * argument.
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
   * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
   * argument.
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
   * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
   * argument. The argument will have already passed the {@link CommonChecks#notNull() notNull}
   * test.
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
   * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
   * argument. The argument will have already passed the {@link CommonChecks#notNull() notNull}
   * test.
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
   * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
   * argument. The argument will have already passed the {@link CommonChecks#notNull() notNull}
   * test.
   *
   * @param excFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @param arg The argument
   * @param <U> The type of the argument
   * @param <X> The type of {@code Exception} thrown if the argument fails to pass a test
   * @return A new {@code Check} object
   * @throws X If the argument fails to pass the {@code notNull} test or any subsequent tests called
   *     on the returned {@code Check} object
   */
  public static <U, X extends Exception> ObjectCheck<U, X> notNull(
      Function<String, X> excFactory, U arg) throws X {
    if (arg == null) {
      String msg = getPrefabMessage(CommonChecks.notNull(), false, null, null, null, null);
      throw excFactory.apply(msg);
    }
    return new ObjectCheck<>(arg, null, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
   * argument. The argument will have already passed the {@link CommonChecks#notNull() notNull}
   * test.
   *
   * @param excFactory A {@code Function} that will produce the exception if a test fails. The
   *     {@code Function} takes a {@code String} (the error message) and returns the {@code
   *     Exception}
   * @param arg The argument
   * @param argName The name of the argument
   * @param <U> The type of the argument
   * @param <X> The type of {@code Exception} thrown if the argument fails to pass a test
   * @return A new {@code Check} object
   * @throws X If the argument fails to pass the {@code notNull} test or any subsequent tests called
   *     on the returned {@code Check} object
   */
  public static <U, X extends Exception> ObjectCheck<U, X> notNull(
      Function<String, X> excFactory, U arg, String argName) throws X {
    if (arg == null) {
      String msg = getPrefabMessage(CommonChecks.notNull(), false, argName, null, null, null);
      throw excFactory.apply(msg);
    }
    return new ObjectCheck<>(arg, argName, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing integers.
   *
   * @param excFactory A {@code Function} that will produce the exception if a test fails. The
   *     {@code Function} will be passed a {@code String} (the error message) and must return the
   *     {@code Exception} to be thrown
   * @param arg The argument
   * @param <X> The type of {@code Exception} thrown if the argument fails to pass a test
   * @return A {@code Check} object suitable for testing {@code int} arguments
   */
  public static <X extends Exception> IntCheck<X> on(Function<String, X> excFactory, int arg) {
    return new IntCheck<>(arg, null, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
   * argument.
   *
   * @param <U> The type of the argument
   * @param <X> The type of {@code Exception} thrown if the argument fails to pass a test
   * @param excFactory A {@code Function} that will produce the exception if a test fails. The
   *     {@code Function} will be passed a {@code String} (the error message) and must return the
   *     {@code Exception} to be thrown
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
   * @param excFactory A {@code Function} that will produce the exception if a test fails. The
   *     {@code Function} will be passed a {@code String} (the error message) and must return the
   *     {@code Exception} to be thrown
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
   * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
   * argument.
   *
   * @param <U> The type of the argument
   * @param <X> The type of {@code Exception} thrown if the argument fails to pass a test
   * @param excFactory A {@code Function} that will produce the exception if a test fails. The
   *     {@code Function} will be passed a {@code String} (the error message) and must return the
   *     {@code Exception} to be thrown
   * @param arg The argument
   * @param argName The name of the argument
   * @return A new {@code Check} object
   */
  public static <U, X extends Exception> ObjectCheck<U, X> on(
      Function<String, X> excFactory, U arg, String argName) {
    return new ObjectCheck<>(arg, argName, excFactory);
  }

  /**
   * Verifies that that specified offset and length can be used for the specified byte array. This
   * is a special-purpose check that stands somewhat apart from the rest of the check framework. It
   * is specifically meant for read/write calls against (extensions of) {@code java.io} and {@code
   * java.nio} classes. It can, however, also used for any method that uses the <i>offset &amp;
   * length</i> paradigm to extract a segment from an array-like object (rather than the <i>from
   * &amp; to</i> paradigm). This method performs a null-check on the {@code array} argument.
   *
   * @see #offsetLength(int, int, int)
   * @param array The byte array
   * @param off The offset within byte array
   * @param len The length of the segment within the byte array
   */
  public static void offsetLength(byte[] array, int off, int len) {
    Check.notNull(array, "array");
    Check.offsetLength(array.length, off, len);
  }

  /**
   * Verifies that specified offset and length can be used for an array with the specified length.
   * The array would typically be a {@code byte} or {@code char} array. This is a special-purpose
   * check that stands somewhat apart from the rest of the check framework. It is specifically meant
   * for read/write calls against (extensions of) {@code java.io} and {@code java.nio} classes. It
   * can, however, also used for any method that uses the <i>offset &amp; length</i> paradigm to
   * extract a segment from an array-like object (rather than the <i>from &amp; to</i> paradigm).
   *
   * @param bufLen The length of the array from to extract a segment
   * @param off The offset of the segment
   * @param len The length of the segment
   */
  public static void offsetLength(int bufLen, int off, int len) {
    Check.on(indexOutOfBounds(), off, "offset").isNot(negative());
    Check.on(indexOutOfBounds(), len, "length").isNot(negative());
    Check.on(indexOutOfBounds(), off + len, "offset + length").is(lte(), bufLen);
  }

  /**
   * Verifies that the specified {@code from} and {@code to} indices can be used to extract a
   * substring from the specified {@code String}. This method performs a null-check on the {@code
   * src} argument.
   *
   * @see #fromTo(int, int, int)
   * @see String#substring(int, int)
   * @param src The string to extract the substring from
   * @param from The start index
   * @param to The end index
   */
  public static void fromTo(String src, int from, int to) {
    Check.notNull(src, "src");
    Check.fromTo(src.length(), from, to);
  }

  /**
   * Verifies that the specified {@code from} and {@code to} indices can be used to extract a
   * sublist from the specified {@code List}. This method performs a null-check on the {@code src}
   * argument.
   *
   * @see #fromTo(int, int, int)
   * @see List#subList(int, int)
   * @param src The string to extract the substring from
   * @param from The start index
   * @param to The end index
   */
  public static <E> void fromTo(List<E> src, int from, int to) {
    Check.notNull(src, "src");
    Check.fromTo(src.size(), from, to);
  }

  /**
   * Verifies that the specified {@code from} and {@code to} indices can be used for typical segment
   * extraction methods like {@link String#substring(int, int) String.substring} and {@link
   * java.util.List#subList(int, int) List.subList}. Note that both {@code from} and to {@code to}
   * may be one position past the end of the {@code String}, {@code List}, etc. In other words, they
   * may both be equal to {@code len}.
   *
   * @param len The length of the {@code String}, {@code List}, array (etc.) from which to extract
   *     the segment
   * @param from The start index of the segment
   * @param to The end index of the segment
   */
  public static void fromTo(int len, int from, int to) {
    Check.that(from, "from").isNot(negative()).isNot(gt(), to);
    Check.that(to, "to").isNot(gt(), len);
  }

  /**
   * Throws an {@code IllegalArgumentException} with the specified message and message arguments.
   * The method is still declared to return a value of type &lt;U&gt; so it can be used as the
   * expression for a {@code return statement}.
   *
   * @param <U> The type of the object that would have been returned if it had passed the checks
   * @param msg The message
   * @param msgArgs The message argument
   * @return Nothing, but allows {@code fail} to be used as the expression in a {@code return}
   *     statement
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
   * @return Nothing, but allows {@code fail} to be used as the expression in a {@code return}
   *     statement
   * @throws X The exception that is thrown
   */
  public static <U, X extends Exception> U fail(Function<String, X> excFactory) throws X {
    return fail(excFactory, StringMethods.EMPTY);
  }

  /**
   * Throws an exception created by the specified exception factory with the specified message and
   * message arguments.
   *
   * @param <U> The type of the object that would have been returned if it had passed the checks
   * @param <X> The type of the exception
   * @param msg The message
   * @param msgArgs The message argument
   * @return Nothing, but allows {@code fail} to be used as the expression in a {@code return}
   *     statement
   * @throws X The exception that is thrown
   */
  public static <U, X extends Exception> U fail(
      Function<String, X> excFactory, String msg, Object... msgArgs) throws X {
    return Check.on(excFactory, 1).is(eq(), 0, msg, msgArgs).ok(i -> null);
  }
}
