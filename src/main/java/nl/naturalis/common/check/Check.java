package nl.naturalis.common.check;

import nl.naturalis.common.StringMethods;
import nl.naturalis.common.function.Relation;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static nl.naturalis.common.check.CommonChecks.*;

/**
 * Facilitates precondition and postcondition checking. See the {@linkplain
 * nl.naturalis.common.check package description} for a detailed explanation.
 *
 * @author Ayco Holleman
 */
public final class Check {

  private Check() {
    throw new UnsupportedOperationException();
  }

  static final String DEF_ARG_NAME = "argument";

  static final Function<String, IllegalArgumentException> DEF_EXC_FACTORY =
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
      throw new IllegalArgumentException(DEF_ARG_NAME + " must not be null");
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
      throw new IllegalArgumentException(argName + " must not be null");
    }
    return new ObjectCheck<>(arg, argName, DEF_EXC_FACTORY);
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
  public static <U, X extends Exception> ObjectCheck<U, X> on(Function<String, X> excFactory,
      U arg) {
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
  public static <X extends Exception> IntCheck<X> on(Function<String, X> excFactory,
      int arg,
      String argName) {
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
  public static <U, X extends Exception> ObjectCheck<U, X> on(Function<String, X> excFactory,
      U arg,
      String argName) {
    return new ObjectCheck<>(arg, argName, excFactory);
  }

  /**
   * Verifies that {@code offset + length} is not greater than the size of the specified array and
   * that {@code offset} is not negative and not greater than the length of the specified array. If
   * any of these requirements do not apply, an {@link IndexOutOfBoundsException} is thrown. Returns
   * {@code off + len} (i.e. the {@code toIndex}). A null check is executed on the string argument
   * first.
   *
   * @param array The  array
   * @param offset The offset within the array
   * @param length The length of the segment
   * @return The {@code toIndex} of the segment
   * @see #offsetLength(int, int, int)
   */
  public static int offsetLength(byte[] array, int offset, int length) {
    Check.notNull(array, "array");
    return Check.offsetLength(array.length, offset, length);
  }

  /**
   * Verifies that {@code offset + length} is not greater than the size of the specified list and
   * that {@code offset} is not negative and not greater than the length of the specified list. If
   * any of these requirements do not apply, an {@link IndexOutOfBoundsException} is thrown. Returns
   * {@code off + len} (i.e. the {@code toIndex}). A null check is executed on the string argument
   * first.
   *
   * @param list The list
   * @param offset The offset within the list
   * @param length The length of the segment
   * @return The {@code toIndex} of the segment
   * @see #offsetLength(int, int, int)
   */
  public static int offsetLength(List<?> list, int offset, int length) {
    Check.notNull(list, "list");
    return Check.offsetLength(list.size(), offset, length);
  }

  /**
   * Verifies that {@code offset + length} is not greater than the size of the specified string and
   * that {@code offset} is not negative and not greater than the length of the specified string. If
   * any of these requirements do not apply, an {@link IndexOutOfBoundsException} is thrown. Returns
   * {@code off + len} (i.e. the {@code toIndex}). A null check is executed on the string argument
   * first.
   *
   * @param string The string
   * @param offset The offset within the string
   * @param length The length of the segment
   * @return The {@code toIndex} of the segment
   * @see #offsetLength(int, int, int)
   */
  public static int offsetLength(String string, int offset, int length) {
    Check.notNull(string, "string");
    return Check.offsetLength(string.length(), offset, length);
  }

  /**
   * Verifies that {@code offset + length} is not greater than the specified size and that {@code
   * offset} is not negative and not greater than the specified size. If any of these requirements
   * do not apply, an {@link IndexOutOfBoundsException} is thrown. Returns {@code off + len} (i.e.
   * the {@code toIndex}). This check stands somewhat apart from the rest of the check framework: it
   * tests multiple things at once, and it does not ask you to provide a {@link Predicate} or {@link
   * Relation} implementing the tests. It is included for convenience and performance reasons, and
   * because it is such a ubiquitous check.
   *
   * @param size The length of the array or array-like object from which to extract the segment
   * @param offset The offset of the segment
   * @param length The length of the segment
   * @return The {@code toIndex} of the segment
   */
  public static int offsetLength(int size, int offset, int length) {
    Check.on(indexOutOfBounds(), size | offset | length, "size/offset/length").isNot(negative());
    return Check.on(indexOutOfBounds(), offset + length, "offset + length").is(lte(), size).ok();
  }

  /**
   * Verifies that {@code toIndex} is not greater than the size of the specified list and that
   * {@code fromIndex} is not negative and not greater than {@code toIndex}. If any of these
   * requirements do not apply, an {@link IndexOutOfBoundsException} is thrown. Returns {@code
   * toIndex - fromIndex} (i.e. the {@code length} of the segment). A null check is executed on the
   * string argument first.
   *
   * @param list The list
   * @param fromIndex The start index
   * @param toIndex The end index
   * @return the {@code length} of the segment
   * @see #fromTo(int, int, int)
   * @see List#subList(int, int)
   */
  public static int fromTo(List<?> list, int fromIndex, int toIndex) {
    Check.notNull(list, "list");
    return Check.fromTo(list.size(), fromIndex, toIndex);
  }

  /**
   * Verifies that {@code toIndex} is not greater than the size of the specified string and that
   * {@code fromIndex} is not negative and not greater than {@code toIndex}. If any of these
   * requirements do not apply, an {@link IndexOutOfBoundsException} is thrown. Returns {@code
   * toIndex - fromIndex} (i.e. the {@code length} of the segment). A null check is executed on the
   * string argument first.
   *
   * @param string The string
   * @param fromIndex The start index
   * @param toIndex The end index
   * @return the {@code length} of the segment
   * @see #fromTo(int, int, int)
   * @see String#substring(int, int)
   */
  public static int fromTo(String string, int fromIndex, int toIndex) {
    Check.notNull(string, "string");
    return Check.fromTo(string.length(), fromIndex, toIndex);
  }

  /**
   * Verifies that {@code toIndex} is not greater than the specified size and that {@code fromIndex}
   * is not negative and not greater than {@code toIndex}. If any of these requirements do not
   * apply, an {@link IndexOutOfBoundsException} is thrown. Returns {@code toIndex - fromIndex}
   * (i.e. the {@code length} of the segment). This check stands somewhat apart from the rest of the
   * check framework: it tests multiple things at once, and it does not ask you to provide a {@link
   * Predicate} or {@link Relation} implementing the tests. It is included for convenience and
   * performance reasons, and because it is such a ubiquitous check.
   *
   * @param size The size
   * @param fromIndex The start index of the segment
   * @param toIndex The end index of the segment
   * @return the {@code length} of the segment
   */
  public static int fromTo(int size, int fromIndex, int toIndex) {
    Check.on(indexOutOfBounds(), fromIndex, "fromIndex").isNot(negative()).isNot(gt(), toIndex);
    Check.on(indexOutOfBounds(), toIndex, "toIndex").isNot(gt(), size);
    return toIndex - fromIndex;
  }

  /**
   * Throws an {@code IllegalArgumentException} with the specified message and message arguments.
   * The method is still declared to return a value of type &lt;U&gt; so it can be used as the
   * expression for a {@code return statement}.
   *
   * @param <U> The type of the object that would have been returned if it had passed the
   *     checks
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
   * @param <U> The type of the object that would have been returned if it had passed the
   *     checks
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
   * @param <U> The type of the object that would have been returned if it had passed the
   *     checks
   * @param <X> The type of the exception
   * @param msg The message
   * @param msgArgs The message argument
   * @return Nothing, but allows {@code fail} to be used as the expression in a {@code return}
   *     statement
   * @throws X The exception that is thrown
   */
  public static <U, X extends Exception> U fail(Function<String, X> excFactory,
      String msg,
      Object... msgArgs) throws X {
    if (msg == null) {
      throw excFactory.apply(StringMethods.EMPTY);
    } else if (msgArgs == null || msgArgs.length == 0) {
      throw excFactory.apply(msg);
    }
    Object[] args = new Object[msgArgs.length + 5];
    System.arraycopy(msgArgs, 0, args, 5, msgArgs.length);
    throw excFactory.apply(CustomMsgFormatter.format(msg, args));
  }

}
