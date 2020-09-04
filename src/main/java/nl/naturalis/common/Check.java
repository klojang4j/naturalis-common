package nl.naturalis.common;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import nl.naturalis.common.internal.IntCheck;
import nl.naturalis.common.internal.IntegerCheck;
import nl.naturalis.common.internal.ObjectCheck;
import nl.naturalis.common.internal.StringCheck;
import static nl.naturalis.common.ArrayMethods.prefix;
import static nl.naturalis.common.ObjectMethods.*;
import static nl.naturalis.common.StringMethods.isNotBlank;

/**
 * Methods for checking preconditions. If you want to check a single precondition for an argument,
 * you can use the static methods. If you need to check multiple preconditions for a single
 * argument, you might prefer to use the instance methods instead. For example:
 *
 * <p>
 *
 * <pre>
 * int i = Check.that(numChairs, "numChairs").gte(2).lte(10).test(numChairs % 2 == 0, "must be even").intValue();
 * </pre>
 *
 * <p>Some methods only take the argument to be tested and the <i>name</i> of the argument, not a
 * complete error message. For example:
 *
 * <p>
 *
 * <pre>
 * Check.notNull(name, "name"); // -> error message: "name must not null"
 * </pre>
 *
 * <p>To provide a custom message, choose the overloaded method that takes message arguments:
 *
 * <pre>
 * Check.notNull(name, "Please specify a name", null); // -> "Please specify a name"
 * Check.notNull(name, "Please specify a name", ""); // -> "Please specify a name"
 * Check.notNull(name, "Please specify a %s", "toy"); // -> "Please specify a toy"
 * </pre>
 *
 * @author Ayco Holleman
 */
public abstract class Check<T, E extends Exception> {

  protected static final String ERR_IN_RANGE =
      "%s must be between %d (inclusive) and %d (inclusive)";
  protected static final String ERR_BETWEEN =
      "%s must be between %d (inclusive) and %d (exclusive)";
  protected static final String ERR_LESS_OR_EQUAL = "%s must be less than or equal to %d";
  protected static final String ERR_LESS_THAN = "%s must be less than %d";
  protected static final String ERR_GREATER_OR_EQUAL = "%s must be greater than or equal to %d";
  protected static final String ERR_GREATER_THAN = "%s must be greater than %d";
  protected static final String ERR_NONE_EMPTY = "%s must not be empty or contain empty values";
  protected static final String ERR_NONE_NULL = "%s must not be null or contain null values";
  protected static final String ERR_NOT_NULL = "%s must not be null";
  protected static final String ERR_NOT_EMPTY = "%s must not be empty";
  protected static final String ERR_NOT_BLANK = "%s must not be blank";
  protected static final String ERR_FAILED_TEST = "Invalid value for %s: %s";
  protected static final String ERR_MUST_BE_NULL = "%s must be null";
  protected static final String ERR_MUST_BE_EMPTY = "%s must be empty";

  /**
   * Returns a {@code Check} object for int arguments that will throw an {@code
   * IllegalArgumentException} if the argument fails to pass a test.
   *
   * @param arg The argument
   * @param argName The argument name
   * @return A new {@code Check} object
   */
  public static Check<Integer, IllegalArgumentException> that(int arg, String argName) {
    return new IntCheck<>(arg, argName, IllegalArgumentException::new);
  }

  /**
   * Returns a {@code Check} object for int arguments that will throw a custom exception if the
   * argument fails to pass a test.
   *
   * @param <F> The type of the exception being thrown
   * @param arg The argument
   * @param argName The argument name
   * @param excProvider A function that takes a string (the error message) and returns an {@code
   *     Exception}
   * @return A new {@code Check} object
   */
  public static <F extends Exception> Check<Integer, F> that(
      int arg, String argName, Function<String, F> excProvider) {
    return new IntCheck<>(arg, argName, excProvider);
  }

  /**
   * Returns a {@code Check} object for {@code String} arguments that will throw an {@code
   * IllegalArgumentException} if the argument fails to pass a test.
   *
   * @param arg The argument
   * @param argName The argument name
   * @return A new {@code Check} object
   */
  public static Check<String, IllegalArgumentException> that(String arg, String argName) {
    return new StringCheck<>(arg, argName, IllegalArgumentException::new);
  }

  /**
   * Returns a {@code Check} object for {@code String} arguments that will throw a custom exception
   * if the argument fails to pass a test.
   *
   * @param <F> The type of the exception being thrown
   * @param arg The argument
   * @param argName The argument name
   * @param excProvider A function that takes a string (the error message) and returns an {@code
   *     Exception}
   * @return A new {@code Check} object
   */
  public static <F extends Exception> Check<String, F> that(
      String arg, String argName, Function<String, F> excProvider) {
    return new StringCheck<>(arg, argName, excProvider);
  }

  /**
   * Returns a {@code Check} object for {@code Integer} arguments that will throw a custom exception
   * if the argument fails to pass a test.
   *
   * @param arg The argument
   * @param argName The argument name
   * @return A new {@code Check} object
   */
  public static Check<Integer, IllegalArgumentException> that(Integer arg, String argName) {
    return new IntegerCheck<>(arg, argName, IllegalArgumentException::new);
  }

  /**
   * Returns a {@code Check} object for {@code Integer} arguments that will throw a custom exception
   * if the argument fails to pass a test.
   *
   * @param <F> The type of the exception being thrown
   * @param arg The argument
   * @param argName The argument name
   * @param excProvider A function that takes a string (the error message) and returns an {@code
   *     Exception}
   * @return A new {@code Check} object
   */
  public static <F extends Exception> Check<Integer, F> that(
      Integer arg, String argName, Function<String, F> excProvider) {
    return new IntegerCheck<>(arg, argName, excProvider);
  }

  /**
   * Returns a {@code Check} object for a generic argument that will throw an {@code
   * IllegalArgumentException} if the argument fails to pass a test.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @param argName The argument name
   * @return
   */
  public static <U> Check<U, IllegalArgumentException> that(U arg, String argName) {
    return new ObjectCheck<>(arg, argName, IllegalArgumentException::new);
  }

  /**
   * Returns a {@code Check} object for a generic argument that will throw a custom exception if the
   * argument fails to pass a test.
   *
   * @param <U> The type of the argument
   * @param <F> The type of the exception being thrown
   * @param arg The argument
   * @param argName The argument name
   * @param excProvider A function that takes a string (the error message) and returns an {@code
   *     Exception}
   * @return A new {@code Check} object
   */
  public static <U, F extends Exception> Check<U, F> that(
      U arg, String argName, Function<String, F> excProvider) {
    return new ObjectCheck<>(arg, argName, excProvider);
  }

  /**
   * Generic check method. Throws the exception supplied by the provided supplier if the provided
   * condition evaluates to false, else does nothing.
   *
   * @param <F> The type of exception thrown if {@code condition} evaluates to false
   * @param condition The condition to evaluate
   * @param excSupplier The exception supplier
   * @throws F The exception thrown if the condition fails
   */
  public static <F extends Exception> void that(boolean condition, Supplier<F> excSupplier)
      throws F {
    if (!condition) {
      throw excSupplier.get();
    }
  }

  /**
   * Generic check method. Throws the exception supplied by the provided exception supplier if the
   * provided condition evaluates to false, else returns {@code result}.
   *
   * @param <U> The type of the argument
   * @param <F> The type of exception thrown if {@code condition} evaluates to false
   * @param condition The condition to evaluate
   * @param result The value returned if {@code condition} evaluates to true
   * @param excSupplier The exception supplier
   * @return The value supplied by the resultSupplier
   * @throws F The exception thrown if {@code condition} evaluates to false
   */
  public static <U, F extends Exception> U that(
      boolean condition, U result, Supplier<F> excSupplier) throws F {
    if (condition) {
      return result;
    }
    throw excSupplier.get();
  }

  /**
   * Generic check method. Throws the exception supplied by the provided exception supplier if the
   * provided condition evaluates to false, else returns the result supplied by the result supplier.
   *
   * @param <U> The type of the argument
   * @param <F> The type of the exception
   * @param condition The condition to evaluate
   * @param resultSupplier The result supplier
   * @param excSupplier The exception supplier
   * @return The object supplied by the resultSupplier
   * @throws F The exception thrown If {@code condition} evaluates to false
   */
  public static <U, F extends Exception> U that(
      boolean condition, Supplier<U> resultSupplier, Supplier<F> excSupplier) throws F {
    if (condition) {
      return resultSupplier.get();
    }
    throw excSupplier.get();
  }

  /**
   * Does nothing if the provided condition evaluates to {@code true}, else throws an {@code
   * IllegalArgumentException} with the provided message and message arguments.
   *
   * @param condition The condition to be evaluated
   * @param message The exception message
   * @param msgArgs The message arguments
   * @throws IllegalArgumentException If the condition evaluates to false
   */
  public static void argument(boolean condition, String message, Object... msgArgs) {
    that(condition, badArgument(message, msgArgs));
  }

  /**
   * Returns {@code arg} if it passes the provided {@code test}, else throws an {@code
   * IllegalArgumentException} with the provided message and message arguments.
   *
   * @param <T> The type of the argument
   * @param arg The argument
   * @param test The test to apply to the argument
   * @param message The exception message
   * @param msgArgs The message arguments
   * @return The argument
   * @throws IllegalArgumentException If the {@code Predicate} evaluates to false
   */
  public static <T> T argument(T arg, Predicate<T> test, String message, Object... msgArgs) {
    return that(test.test(arg), arg, badArgument(message, msgArgs));
  }

  /**
   * Returns {@code arg} if it passes the provided {@code test}, else throws an {@code
   * IllegalArgumentException} with the provided message and message arguments.
   *
   * @param arg The argument
   * @param test The test to apply to the argument
   * @param message The exception message
   * @param msgArgs The message arguments
   * @return The argument
   * @throws IllegalArgumentException If the {@code Predicate} evaluates to false
   */
  public static int integer(int arg, IntPredicate test, String message, Object... msgArgs) {
    if (test.test(arg)) {
      return arg;
    }
    throw badArgument(message, msgArgs).get();
  }

  /**
   * Throws an {@code ArrayIndexOutOfBoundsException} if {@code arg} is less than zero or greater
   * than or equal to {@code maxExclusive}, else returns {@code arg}. This is especially useful to
   * test "from" arguments, which generally should be <i>less than</i> the length or size of the
   * object operated upon.
   *
   * @param arg The argument The argument to test
   * @param maxExclusive The maximum allowed value (exclusive)
   * @return The argument
   * @throws ArrayIndexOutOfBoundsException
   */
  public static int index(int arg, int maxExclusive, String argName) {
    if (arg < maxExclusive) {
      return arg;
    }
    throw badIndex(arg, argName);
  }

  /**
   * Throws an {@code ArrayIndexOutOfBoundsException} if {@code arg} is less than {@code min} or
   * greater than {@code max}, else returns {@code arg}. This is especially useful to test "to" or
   * "until" arguments, which generally should be <i>less than or equal to</i> the length or size of
   * the object operated upon.
   *
   * @param arg The argument The argument to test
   * @param minInclusive The minimum allowed value (inclusive)
   * @param MaxInclusive The maximum allowed value (inclusive)
   * @return The argument
   * @throws ArrayIndexOutOfBoundsException
   */
  public static int index(int arg, int minInclusive, int MaxInclusive, String argName) {
    if (arg >= minInclusive && arg <= MaxInclusive) {
      return arg;
    }
    throw badIndex(arg, argName);
  }

  /**
   * Returns {@code arg} if it is not null, else throws an {@code IllegalArgumentException}.
   *
   * @param <T> The type of the argument
   * @param arg The argument
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException If the argument is null
   */
  public static <T> T notNull(T arg, String argName) throws IllegalArgumentException {
    return argument(arg, Objects::nonNull, ERR_NOT_NULL, argName);
  }

  /**
   * Returns {@code arg} if it is not null, else throws an {@code IllegalArgumentException} with the
   * provided message and message arguments.
   *
   * @param <T> The type of the argument
   * @param arg The argument
   * @param message The exception message
   * @param msgArg0 The first message argument
   * @param msgArgs The remaining message arguments
   * @return The argument
   * @throws IllegalArgumentException If the argument is null
   */
  public static <T> T notNull(T arg, String message, Object msgArg0, Object... msgArgs) {
    return that(arg != null, arg, badArg(message, msgArg0, msgArgs));
  }

  /**
   * Checks that the provided argument is null, else throws an {@code IllegalArgumentException}.
   *
   * @param arg The argument
   * @param argName The argument name
   * @throws IllegalArgumentException If the argument is not null
   */
  public static void isNull(Object arg, String argName) {
    argument(arg == null, ERR_MUST_BE_NULL, argName);
  }

  /**
   * Throws an {@code IllegalArgumentException} if the argument is null or contains null values. The
   * latter is applicable if the argument is an array, {@code Collection} or {@code Map}. For {@code
   * Map} arguments only the values are tested, not the keys.
   *
   * @see ObjectMethods#isDeepNotNull(Object)
   * @param <T> The type of the argument
   * @param arg The argument
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException If the argument is null or contains null values
   */
  public static <T> T noneNull(T arg, String argName) {
    return argument(arg, ObjectMethods::isDeepNotNull, ERR_NONE_NULL, argName);
  }

  /**
   * Throws an {@code IllegalArgumentException} if the argument is null or contains null values. The
   * latter is applicable if the argument is an array, {@code Collection} or {@code Map}. For {@code
   * Map} arguments only the values are tested, not the keys.
   *
   * @see ObjectMethods#isDeepNotNull(Object)
   * @param <T> The type of the argument
   * @param arg The argument
   * @param message The exception message
   * @param msgArg0 The first message argument
   * @param msgArgs The remaining message arguments
   * @return The argument
   * @throws IllegalArgumentException If the argument is null or contains null values
   */
  public static <T> T noneNull(T arg, String message, Object msgArg0, Object... msgArgs) {
    return that(isDeepNotNull(arg), arg, badArg(message, msgArg0, msgArgs));
  }

  /**
   * Throws an {@code IllegalArgumentException} if the argument is ObjectMethods#isEmpty(Object)
   * empty} or contains empty values. The latter is applicable if the argument is an array, {@code
   * Collection} or {@code Map}. For {@code Map} arguments only the values are tested, not the keys.
   *
   * @see ObjectMethods#isNotEmpty(Object)
   * @param arg The argument
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException If the argument is empty
   */
  public static <T> T notEmpty(T arg, String argName) {
    return argument(arg, ObjectMethods::isNotEmpty, ERR_NOT_EMPTY, argName);
  }

  /**
   * Returns {@code arg} if it is not empty, else throws an {@code IllegalArgumentException} with
   * the provided message and message arguments.
   *
   * @see ObjectMethods#isNotEmpty(Object)
   * @param arg The argument
   * @param message The exception message
   * @param msgArgs The {@code String.format} message arguments
   * @return The argument
   * @throws IllegalArgumentException If the argument is empty
   */
  public static <T> T notEmpty(T arg, String message, Object msgArg0, Object... msgArgs) {
    return that(isNotEmpty(arg), arg, badArg(message, msgArg0, msgArgs));
  }

  /**
   * Throws an {@code IllegalArgumentException} if the argument is empty or contains empty values.
   * The latter is applicable if the argument is an array, {@code Collection} or {@code Map}. For
   * {@code Map} arguments only the values are tested, not the keys.
   *
   * @see ObjectMethods#isDeepNotEmpty(Object)
   * @param <T> The type of the argument
   * @param arg The argument
   * @param argName The argument name
   * @return The argument
   */
  public static <T> T noneEmpty(T arg, String argName) {
    return argument(arg, ObjectMethods::isDeepNotEmpty, ERR_NONE_EMPTY, argName);
  }

  /**
   * Throws an {@code IllegalArgumentException} if the argument is empty or contains empty values.
   * The latter is applicable if the argument is an array, {@code Collection} or {@code Map}. For
   * {@code Map} arguments only the values are tested, not the keys.
   *
   * @param <T> The type of the argument
   * @param arg The argument
   * @param message The exception message
   * @param msgArg0 The first message argument
   * @param msgArgs The remaining message arguments
   * @return The argument
   * @throws IllegalArgumentException If the argument is empty or contains empty values
   */
  public static <T> T noneEmpty(T arg, String message, Object msgArg0, Object... msgArgs) {
    return that(isDeepNotEmpty(arg), arg, badArg(message, msgArg0, msgArgs));
  }

  /**
   * Returns {@code arg} if it is not blank, else throws an {@code IllegalArgumentException}.
   *
   * @see StringMethods#isBlank(Object)
   * @param arg The argument
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException If the argument is blank
   */
  public static String notBlank(String arg, String argName) {
    return argument(arg, StringMethods::isNotBlank, ERR_NOT_BLANK, argName);
  }

  /**
   * Returns {@code arg} if it is not blank, else throws an {@code IllegalArgumentException}.
   *
   * @param arg The argument
   * @param message The exception message
   * @param msgArg0 The first message argument
   * @param msgArgs The remaining message arguments
   * @return The argument
   * @throws IllegalArgumentException If the argument is blank
   */
  public static String notBlank(String arg, String message, Object msgArg0, Object... msgArgs) {
    return that(isNotBlank(arg), arg, badArg(message, msgArg0, msgArgs));
  }

  /**
   * Returns {@code arg} if it is greater than {@code minVal}, else throws an {@code
   * IllegalArgumentException}.
   *
   * @param arg The argument
   * @param minVal The argument's lower bound (exclusive)
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException If the argument fails the test
   */
  public static int gt(int arg, int minVal, String argName) {
    return integer(arg, x -> x > minVal, ERR_GREATER_THAN, argName, minVal);
  }

  /**
   * Returns {@code arg} if it is greater than {@code minVal}, else throws an {@code
   * IllegalArgumentException}.
   *
   * @param arg The argument
   * @param minVal The argument's lower bound (exclusive)
   * @param message The error message
   * @param msgArg0 The first message argument
   * @param msgArgs The remaining message arguments
   * @return The argument
   * @throws IllegalArgumentException If the argument fails the test
   */
  public static int gt(int arg, int minVal, String message, Object msgArg0, Object... msgArgs) {
    if (arg > minVal) {
      return arg;
    }
    throw badArg(message, msgArg0, msgArgs).get();
  }

  /**
   * Returns {@code arg} if it is greater than or equal to {@code minVal}, else throws an {@code
   * IllegalArgumentException}.
   *
   * @param arg The argument
   * @param minVal The argument's lower bound (inclusive)
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException If the argument fails the test
   */
  public static int gte(int arg, int minVal, String argName) {
    return integer(arg, x -> x >= minVal, ERR_GREATER_OR_EQUAL, argName, minVal);
  }

  /**
   * Returns {@code arg} if it is greater than or equal to {@code minVal}, else throws an {@code
   * IllegalArgumentException}.
   *
   * @param arg The argument
   * @param minVal The argument's lower bound (inclusive)
   * @param message The error message
   * @param msgArg0 The first message argument
   * @param msgArgs The remaining message arguments
   * @return The argument
   * @throws IllegalArgumentException If the argument fails the test
   */
  public static int gte(int arg, int minVal, String message, Object msgArg0, Object... msgArgs) {
    if (arg >= minVal) {
      return arg;
    }
    throw badArg(message, msgArg0, msgArgs).get();
  }

  /**
   * Returns {@code arg} if it is less than {@code maxVal}, else throws an {@code
   * IllegalArgumentException}.
   *
   * @param arg The argument
   * @param maxVal The argument's upper bound (exclusive)
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException If the argument fails the test
   */
  public static int lt(int arg, int maxVal, String argName) {
    return integer(arg, x -> x < maxVal, ERR_LESS_THAN, argName, maxVal);
  }

  /**
   * Returns {@code arg} if it is less than {@code maxVal}, else throws an {@code
   * IllegalArgumentException} with the provided message and message arguments.
   *
   * @param arg The argument
   * @param maxVal The argument's upper bound (exclusive)
   * @param message The error message
   * @param msgArg0 The first message argument
   * @param msgArgs The remaining message arguments
   * @return The argument
   * @throws IllegalArgumentException If the argument fails the test
   */
  public static int lt(int arg, int maxVal, String message, Object msgArg0, Object... msgArgs) {
    if (arg < maxVal) {
      return arg;
    }
    throw badArg(message, msgArg0, msgArgs).get();
  }

  /**
   * Returns {@code arg} is less than or equal to {@code maxVal}, else throws an {@code
   * IllegalArgumentException}.
   *
   * @param arg The argument
   * @param maxVal The argument's upper bound (inclusive)
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException If the argument fails the test
   */
  public static int lte(int arg, int maxVal, String argName) {
    return integer(arg, x -> x <= maxVal, ERR_LESS_OR_EQUAL, argName, maxVal);
  }

  /**
   * Returns {@code arg} is less than or equal to {@code maxVal}, else throws an {@code
   * IllegalArgumentException}.
   *
   * @param arg The argument
   * @param maxVal The argument's upper bound (inclusive)
   * @param message The error message
   * @param msgArg0 The first message argument
   * @param msgArgs The remaining message arguments
   * @return The argument
   * @throws IllegalArgumentException If the argument fails the test
   */
  public static int lte(int arg, int maxVal, String message, Object msgArg0, Object... msgArgs) {
    if (arg <= maxVal) {
      return arg;
    }
    throw badArg(message, msgArg0, msgArgs).get();
  }

  /**
   * Returns {@code arg} if it is between {@code minInclusive} and {@code maxExclusive}, else throws
   * an {@code IllegalArgumentException}.
   *
   * @param arg The argument
   * @param minInclusive The minimum allowed value (inclusive)
   * @param maxExclusive The maximum allowed value (exclusive)
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException If the argument fails the test
   */
  public static int between(int arg, int minInclusive, int maxExclusive, String argName) {
    if (arg >= minInclusive && arg < maxExclusive) {
      return arg;
    }
    throw badArgument(ERR_BETWEEN, argName, minInclusive, maxExclusive).get();
  }

  /**
   * Returns {@code arg} if it is between {@code minInclusive} and {@code maxInclusive}, else throws
   * an {@code IllegalArgumentException}.
   *
   * @param arg The argument
   * @param minInclusive The minimum allowed value (inclusive)
   * @param maxInclusive The maximum allowed value (inclusive)
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException If the argument fails the test
   */
  public static int inRange(int arg, int minInclusive, int maxInclusive, String argName) {
    if (arg >= minInclusive && arg <= minInclusive) {
      return arg;
    }
    throw badArgument(ERR_IN_RANGE, argName, minInclusive, minInclusive).get();
  }

  /**
   * Does nothing if the provided condition evaluates to {@code true}, else throws an {@link
   * IllegalStateException} with the provided message and message arguments.
   *
   * @param condition The condition to evaluate
   * @param message The exception message
   * @param msgArgs The {@code String.format} message arguments (may be null or empty)
   * @throws IllegalStateException If the condition evaluates to {@code false}
   */
  public static void state(boolean condition, String message, Object... msgArgs)
      throws IllegalStateException {
    that(condition, () -> badState(message, msgArgs));
  }

  /**
   * Returns a {@code Supplier} of an {@code IllegalArgumentException} with the provided message and
   * message arguments. Useful as static import:
   *
   * <p>
   *
   * <pre>
   * Check.that(year % 13 != 0,i badArgument("Not a lucky year: %d", year));
   * </pre>
   *
   * @param message The exception message
   * @param msgArgs The message arguments
   * @return A {@code Supplier} of an {@code IllegalArgumentException} with the provided message and
   *     message arguments
   */
  public static Supplier<IllegalArgumentException> badArgument(String message, Object... msgArgs) {
    if (message == null) { // Do not use Check.notNull! Causes stack overflow error
      throw new IllegalArgumentException("message must not be null");
    }
    if (msgArgs == null) {
      return () -> new IllegalArgumentException(message);
    }
    return () -> new IllegalArgumentException(String.format(message, msgArgs));
  }

  private static Supplier<IllegalArgumentException> badArg(
      String msg, Object msgArg0, Object... msgArgs) {
    if (StringMethods.isEmpty(msgArg0)) {
      return () -> new IllegalArgumentException(msg);
    } else if (msgArgs == null) {
      return () -> new IllegalArgumentException(String.format(msg, msgArg0));
    }
    return () -> new IllegalArgumentException(String.format(msg, prefix(msgArgs, msgArg0)));
  }

  private static IllegalStateException badState(String msg, Object... msgArgs) {
    if (isDeepNotEmpty(msgArgs)) {
      return new IllegalStateException(String.format(msg, msgArgs));
    }
    throw new IllegalStateException(msg);
  }

  private static ArrayIndexOutOfBoundsException badIndex(int arg, String argName) {
    String fmt = "Index variable \"%s\" out of range: %d";
    return new ArrayIndexOutOfBoundsException(String.format(fmt, argName, arg));
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////
  // Instance fields / methods start here
  //////////////////////////////////////////////////////////////////////////////////////////////////////

  protected final String argName;
  protected final Function<String, E> excProvider;

  protected Check(String argName, Function<String, E> excProvider) {
    this.argName = argName;
    this.excProvider = excProvider;
  }

  /**
   * Verifies that the specified condition (supposedly related to the argument) evaluates to true.
   *
   * @param condition The condition to evaluate
   * @param descr A description of the test
   * @return This {@code Check} object
   * @throws IllegalArgumentException If the condition evaluates to false
   */
  public Check<T, E> test(boolean condition, String descr) throws E {
    that(condition, smash(ERR_FAILED_TEST, argName, descr));
    return this;
  }

  /**
   * Verifies that the specified condition (supposedly related to the specified field within the
   * argument) evaluates to true.
   *
   * @param condition The condition to evaluate
   * @param descr A description of the test
   * @return This {@code Check} object
   * @throws IllegalArgumentException If the condition evaluates to false
   */
  public Check<T, E> test(String field, boolean condition, String descr) throws E {
    that(condition, smash(ERR_FAILED_TEST, field(field), descr));
    return this;
  }

  /**
   * Verifies that the argument passes the provided test.
   *
   * @param test The test to apply to the argument
   * @param descr A description of the test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> test(Predicate<T> test, String descr) throws E {
    throw notSupported("test");
  }

  /**
   * Verifies that the specified field within the argument passes the provided test.
   *
   * @param field The name of the field
   * @param test The test to apply to the field
   * @param descr A description of the test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> test(String field, Predicate<T> test, String descr) throws E {
    throw notSupported("test");
  }

  /**
   * Verifies that the argument passes the provided test.
   *
   * @param test The condition to apply to the argument
   * @param descr The test to apply to the field
   * @return This {@code Check} object
   * @throws E If {@code test} evaluates to false
   */
  public Check<T, E> testInt(IntPredicate test, String descr) throws E {
    throw notSupported("testInt");
  }

  /**
   * Verifies that the specified field within the argument passes the provided test.
   *
   * @param field The name of the field
   * @param test The test to apply to the field
   * @param descr A description of the test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> testInt(String field, IntPredicate test, String descr) throws E {
    throw notSupported("testInt");
  }

  /**
   * Verifies that the argument is not null.
   *
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> notNull() throws E {
    throw notSupported("notNull");
  }

  /**
   * Verifies that the argument is {@link ObjectMethods#isDeepNotNull(Object) deepNotNull}.
   *
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> noneNull() throws E {
    throw notSupported("noneNull");
  }

  /**
   * Verifies that the argument is not {@link ObjectMethods#isNotEmpty(Object) empty}.
   *
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> notEmpty() throws E {
    throw notSupported("notEmpty");
  }

  /**
   * Verifies that the argument is {@link ObjectMethods#isDeepNotEmpty(Object) deepNotEmpty}.
   *
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> noneEmpty() throws E {
    throw notSupported("noneEmpty");
  }

  /**
   * Verifies that the argument is not {@link StringMethods#isNotBlank(Object) blank}.
   *
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> notBlank() throws E {
    throw notSupported("notBlank");
  }

  /**
   * Verifies that the argument is null.
   *
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> isNull() throws E {
    throw notSupported("isNull");
  }

  /**
   * Verifies that the argument is {@link ObjectMethods#isEmpty(Object) empty}.
   *
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> isEmpty() throws E {
    throw notSupported("isEmpty");
  }

  /**
   * Verifies that the argument is greater than the specified value.
   *
   * @param minVal The minimum allowed value (exclusive)
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> gt(int minVal) throws E {
    throw notSupported("gt");
  }

  /**
   * Verifies that the argument is greater than or equal to the specified value.
   *
   * @param minVal The minimum allowed value (inclusive)
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> gte(int minVal) throws E {
    throw notSupported("gte");
  }

  /**
   * Verifies that the argument is less than the specified value.
   *
   * @param maxVal The maximum allowed value (exclusive)
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> lt(int maxVal) throws E {
    throw notSupported("lt");
  }

  /**
   * Verifies that the argument is less than or equal to the specified value.
   *
   * @param maxVal The maximum allowed value (inclusive)
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> lte(int maxVal) throws E {
    throw notSupported("lte");
  }

  /**
   * Verifies that the argument is greater than or equal to the 1st argument and less than the 2nd
   * argument.
   *
   * @param minInclusive The minimum allowed value (inclusive)
   * @param maxExclusive The maximum allowed value (exclusive)
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> between(int minInclusive, int maxExclusive) throws E {
    throw notSupported("between");
  }

  /**
   * Verifies that the argument is greater than or equal to the 1st argument and less than or equal
   * to the 2nd argument.
   *
   * @param minInclusive The minimum allowed value (inclusive)
   * @param maxInclusive The maximum allowed value (inclusive)
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> inRange(int minInclusive, int maxInclusive) throws E {
    throw notSupported("inRange");
  }

  /**
   * Returns the argument being tested. To be used as the last call after a chain of checks. For
   * example:
   *
   * <pre>
   * Integer i = Check.that(counter, "counter").notNull().value();
   * </pre>
   *
   * @return The argument
   */
  public abstract T value();

  /**
   * Returns the argument being tested as an {@code int}. If the argument being tested actually is
   * an {@code int} rather than an {@code Integer}, this method saves the cost of unboxing incurred
   * by {@link #value()}. If the argument neither is {@code int} nor an {@code Integer} this method
   * throws a {@code ClassCastException}.
   *
   * @return The argument cast or converted to an {@code int}
   */
  public int intValue() {
    throw notSupported("intValue");
  }

  protected Supplier<E> smash(String msg, Object... msgArgs) {
    return () -> excProvider.apply(String.format(msg, msgArgs));
  }

  protected String field(String name) {
    return argName + "." + name;
  }

  private UnsupportedOperationException notSupported(String check) {
    return new UnsupportedOperationException(
        String.format("%s method not supported for %s", check, argName));
  }
}
