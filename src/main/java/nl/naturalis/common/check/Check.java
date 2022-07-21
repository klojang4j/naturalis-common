package nl.naturalis.common.check;

import nl.naturalis.common.StringMethods;
import nl.naturalis.common.function.Relation;
import nl.naturalis.common.x.Param;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.MsgUtil.getCustomMessage;

/**
 * Facilitates precondition and postcondition checking. See the
 * {@linkplain nl.naturalis.common.check package description} for a detailed
 * explanation.
 *
 * @author Ayco Holleman
 */
public final class Check {

  private static final Supplier<IndexOutOfBoundsException> NEGATIVE_SIZE_OR_INDEX = () -> new IndexOutOfBoundsException(
      "Negative indices and length/size not allowed");

  private static final Supplier<IndexOutOfBoundsException> NEGATIVE_OFFSET_LENGTH = () -> new IndexOutOfBoundsException(
      "Negative offset, length and size not allowed");

  private static final Supplier<IndexOutOfBoundsException> FROM_GREATER_THAN_TO = () -> new IndexOutOfBoundsException(
      "from-index must not be greater than to-index");

  private static final Supplier<IndexOutOfBoundsException> TO_GREATER_THAN_SIZE = () -> new IndexOutOfBoundsException(
      "to-index must not be greater than size or length");

  private Check() {
    throw new UnsupportedOperationException();
  }

  static final String DEF_ARG_NAME = "argument";

  static final Function<String, IllegalArgumentException> DEF_EXC_FACTORY = IllegalArgumentException::new;

  /**
   * Static factory method. Returns a new {@code IntCheck}.
   *
   * @param arg the argument
   * @return a new {@code IntCheck}
   */
  public static IntCheck<IllegalArgumentException> that(int arg) {
    return new IntCheck<>(arg, null, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing
   * the provided argument.
   *
   * @param <U> the type of the argument
   * @param arg the argument
   * @return a {@code Check} object suitable for testing the provided argument
   */
  public static <U> ObjectCheck<U, IllegalArgumentException> that(U arg) {
    return new ObjectCheck<>(arg, null, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing
   * integers.
   *
   * @param arg the argument
   * @param argName the name of the argument
   * @return a new {@code Check} object
   */
  public static IntCheck<IllegalArgumentException> that(int arg, String argName) {
    return new IntCheck<>(arg, argName, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing
   * the provided argument.
   *
   * @param <U> the type of the argument
   * @param arg the argument
   * @param argName the name of the argument
   * @return a new {@code Check} object
   */
  public static <U> ObjectCheck<U, IllegalArgumentException> that(U arg,
      String argName) {
    return new ObjectCheck<>(arg, argName, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing
   * the provided argument. The argument will have already passed the
   * {@link CommonChecks#notNull() notNull} test.
   *
   * @param <U> the type of the argument
   * @param arg the argument
   * @return a new {@code Check} object
   */
  public static <U> ObjectCheck<U, IllegalArgumentException> notNull(U arg)
      throws IllegalArgumentException {
    if (arg == null) {
      throw new IllegalArgumentException("illegal null value");
    }
    return new ObjectCheck<>(arg, null, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing
   * the provided argument. The argument will have already passed the
   * {@link CommonChecks#notNull() notNull} test.
   *
   * @param <U> the type of the argument
   * @param arg the argument
   * @param argName the name of the argument
   * @return a new {@code Check} object
   */
  public static <U> ObjectCheck<U, IllegalArgumentException> notNull(U arg,
      String argName) throws IllegalArgumentException {
    if (arg == null) {
      throw new IllegalArgumentException(argName + " must not be null");
    }
    return new ObjectCheck<>(arg, argName, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing
   * integers.
   *
   * @param excFactory a {@code Function} that will produce the exception if a
   *     test fails. The {@code Function} will be passed a {@code String} (the error
   *     message) and must return the {@code Exception} to be thrown
   * @param arg the argument
   * @param <X> the type of {@code Exception} thrown if the argument fails to
   *     pass a test
   * @return a {@code Check} object suitable for testing {@code int} arguments
   */
  public static <X extends Exception> IntCheck<X> on(Function<String, X> excFactory,
      int arg) {
    return new IntCheck<>(arg, null, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing
   * the provided argument.
   *
   * @param <U> the type of the argument
   * @param <X> the type of {@code Exception} thrown if the argument fails to
   *     pass a test
   * @param excFactory a {@code Function} that will produce the exception if a
   *     test fails. The {@code Function} will be passed a {@code String} (the error
   *     message) and must return the {@code Exception} to be thrown
   * @param arg the argument
   * @return a {@code Check} object suitable for testing the provided argument
   */
  public static <U, X extends Exception> ObjectCheck<U, X> on(Function<String, X> excFactory,
      U arg) {
    return new ObjectCheck<>(arg, null, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing
   * integers.
   *
   * @param excFactory a {@code Function} that will produce the exception if a
   *     test fails. The {@code Function} will be passed a {@code String} (the error
   *     message) and must return the {@code Exception} to be thrown
   * @param arg the argument
   * @param argName the name of the argument
   * @param <X> the type of {@code Exception} thrown if the argument fails to
   *     pass a test
   * @return a new {@code Check} object
   */
  public static <X extends Exception> IntCheck<X> on(Function<String, X> excFactory,
      int arg,
      String argName) {
    return new IntCheck<>(arg, argName, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} instance suitable for testing
   * the provided argument.
   *
   * @param <U> the type of the argument
   * @param <X> the type of {@code Exception} thrown if the argument fails to
   *     pass a test
   * @param excFactory a {@code Function} that will produce the exception if a
   *     test fails. The {@code Function} will be passed a {@code String} (the error
   *     message) and must return the {@code Exception} to be thrown
   * @param arg the argument
   * @param argName the name of the argument
   * @return a new {@code Check} object
   */
  public static <U, X extends Exception> ObjectCheck<U, X> on(Function<String, X> excFactory,
      U arg,
      String argName) {
    return new ObjectCheck<>(arg, argName, excFactory);
  }

  /**
   * Throws an {@link IllegalArgumentException} if the provided array is
   * {@code null}. Throws an {@link IndexOutOfBoundsException} if:
   * <ul>
   *   <li>{@code offset} <i>or</i> {@code length} is less than zero
   *   <li>{@code offset} + {@code length} is greater than the array's length
   * </ul>
   *
   * @param array the  array
   * @param offset the offset within the array
   * @param length the length of the segment
   * @return the {@code toIndex} of the segment
   * @see #offsetLength(int, int, int)
   */
  public static void offsetLength(byte[] array, int offset, int length) {
    if (array == null) {
      throw new IllegalArgumentException("array must not be null");
    }
    if ((offset | length) < 0 || offset + length > array.length) {
      throw new IndexOutOfBoundsException();
    }
  }

  /**
   * An all-in-one check for the provided size (supposedly of an array or array-like
   * object), offset and length. Verifies that the segment defined by the specified
   * offset and length stays within the boundaries defined by the specified size.
   * Throws an {@link IndexOutOfBoundsException} if:
   * <ul>
   *   <li>{@code size} <i>or</i> {@code offset} <i>or</i> {@code length} is less than zero
   *   <li>{@code offset} + {@code length} is greater than {@code size}
   * </ul>
   *
   * @param size the length/size of the array or array-like object
   * @param offset the offset
   * @param length the length of the segment
   * @return the {@code toIndex} of the segment
   */
  public static void offsetLength(int size, int offset, int length) {
    if ((size | offset | length) < 0 || size < offset + length) {
      throw new IndexOutOfBoundsException();
    }
  }

  /**
   * An all-in-one check for the provided {@code List}, from-index and to-index.
   * Verifies that the segment defined by the specified from-index and to-index stays
   * within the boundaries of the list. Throws an {@link IllegalArgumentException} if
   * the provided list is {@code null}. Throws an {@link IndexOutOfBoundsException}
   * if:
   * <ul>
   *   <li>{@code fromIndex} <i>or</i> {@code toIndex} is less than zero
   *   <li>{@code toIndex} is less than {@code fromIndex}
   *   <li>the list's size is less than {@code toIndex}
   * </ul>
   *
   * @param list the list
   * @param fromIndex the start index of the sublist
   * @param toIndex the end index of the sublist
   * @return the {@code size} of the sublist
   * @see #fromTo(int, int, int)
   * @see List#subList(int, int)
   */
  public static int fromTo(List<?> list, int fromIndex, int toIndex) {
    if (list == null) {
      throw new IllegalArgumentException("list must not be null");
    }
    if ((fromIndex | toIndex) < 0
        || toIndex < fromIndex
        || list.size() < toIndex) {
      throw new IndexOutOfBoundsException();
    }
    return toIndex - fromIndex;
  }

  /**
   * An all-in-one check for the provided array, from-index and to-index. Verifies
   * that the segment defined by the specified from-index and to-index stays within
   * the boundaries of the array. Throws an {@link IllegalArgumentException} if the
   * provided list is {@code null}. Throws an {@link IndexOutOfBoundsException} if:
   * <ul>
   *   <li>{@code fromIndex} <i>or</i> {@code toIndex} is less than zero
   *   <li>{@code toIndex} is less than {@code fromIndex}
   *   <li>the array's length is less than {@code toIndex}
   * </ul>
   *
   * @param array the array
   * @param fromIndex the start index of the array segment
   * @param toIndex the end index of the array segment
   * @return the {@code length} of the array segment
   * @see #fromTo(int, int, int)
   * @see Arrays#copyOfRange(Object[], int, int)
   */
  public static <T> int fromTo(T[] array, int fromIndex, int toIndex) {
    if (array == null) {
      throw new IllegalArgumentException("array must not be null");
    }
    if ((fromIndex | toIndex) < 0
        || toIndex < fromIndex
        || array.length < toIndex) {
      throw new IndexOutOfBoundsException();
    }
    return toIndex - fromIndex;
  }

  /**
   * An all-in-one check for the provided array, from-index and to-index. Verifies
   * that the segment defined by the specified from-index and to-index stays within
   * the boundaries of the array. Throws an {@link IllegalArgumentException} if the
   * provided list is {@code null}. Throws an {@link IndexOutOfBoundsException} if:
   * Throws an {@link IllegalArgumentException} if the provided string is
   * {@code null}. Throws an {@link IndexOutOfBoundsException} if:
   * <ul>
   *   <li>{@code fromIndex} <i>or</i> {@code toIndex} is less than zero
   *   <li>{@code toIndex} is less than {@code fromIndex}
   *   <li>the string's length is less than {@code toIndex}
   * </ul>
   *
   * @param string The string
   * @param fromIndex the start index of the substring
   * @param toIndex the end index of the substring
   * @return the {@code length} of the substring
   * @see #fromTo(int, int, int)
   * @see String#substring(int, int)
   */
  public static int fromTo(String string, int fromIndex, int toIndex) {
    if (string == null) {
      throw new IllegalArgumentException("string must not be null");
    }
    if ((fromIndex | toIndex) < 0
        || toIndex < fromIndex
        || string.length() < toIndex) {
      throw new IndexOutOfBoundsException();
    }
    return toIndex - fromIndex;
  }

  /**
   * An all-in-one check for the provided size (supposedly of an array or array-like
   * object), from-index and to-index. Verifies that the segment defined by the
   * specified from-index and to-index stays within the boundaries defined by the
   * specified size. Throws an {@link IndexOutOfBoundsException} if:
   * <ul>
   *   <li>{@code size} <i>or</i> {@code fromIndex} <i>or</i> {@code toIndex} is less than zero
   *   <li>{@code toIndex} is less than {@code fromIndex}
   *   <li>{@code size} is less than {@code toIndex}
   * </ul>
   *
   * @param size the size (or length) of the array, string, list, etc.
   * @param fromIndex the start index of the segment
   * @param toIndex the end index of the segment
   * @return the {@code length} of the segment
   */
  public static int fromTo(int size, int fromIndex, int toIndex) {
    if ((size | fromIndex | toIndex) < 0 || toIndex < fromIndex || size < toIndex) {
      throw new IndexOutOfBoundsException();
    }
    return toIndex - fromIndex;
  }

  /**
   * Throws an {@code IllegalArgumentException} with the specified message and
   * message arguments. The method is still declared to return a value of type
   * &lt;U&gt; so it can be used as the expression for a {@code return statement}.
   *
   * @param <U> the type of the object that would have been returned if it had
   *     passed the checks
   * @param msg The message
   * @param msgArgs The message argument
   * @return nothing, but allows {@code fail} to be used as the expression in a
   *     {@code return} statement
   */
  public static <U> U fail(String msg, Object... msgArgs) {
    return fail(DEF_EXC_FACTORY, msg, msgArgs);
  }

  /**
   * Throws the exception created by the specified exception factory.
   *
   * @param <U> the type of the object that would have been returned if it had
   *     passed the checks
   * @param <X> the type of the exception
   * @param excFactory The exception supplier
   * @return nothing, but allows {@code fail} to be used as the expression in a
   *     {@code return} statement
   * @throws X The exception that is thrown
   */
  public static <U, X extends Throwable> U fail(Function<String, X> excFactory)
      throws X {
    return fail(excFactory, StringMethods.EMPTY);
  }

  /**
   * Throws an exception created by the specified exception factory with the
   * specified message and message arguments.
   *
   * @param <U> the type of the object that would have been returned if it had
   *     passed the checks
   * @param <X> the type of the exception
   * @param msg The message
   * @param msgArgs The message argument
   * @return nothing, but allows {@code fail} to be used as the expression in a
   *     {@code return} statement
   * @throws X The exception that is thrown
   */
  public static <U, X extends Throwable> U fail(Function<String, X> excFactory,
      String msg,
      Object... msgArgs) throws X {
    String s = getCustomMessage(msg, msgArgs, null, null, null, null, null);
    throw excFactory.apply(s);
  }

}
