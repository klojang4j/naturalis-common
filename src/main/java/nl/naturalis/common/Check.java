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
import static nl.naturalis.common.ObjectMethods.deepNotEmpty;

/**
 * <p>
 * Methods for checking preconditions. If you want to check a single
 * precondition for an argument, you can use the static methods. If you need to
 * check multiple preconditions for a single argument, you might prefer to use
 * the instance methods instead. For example:
 * </p>
 * <p>
 *
 * <pre>
 * int i = Check.that(numChairs, "numChairs")
 *     .gte(2)
 *     .lte(10)
 *     .test(numChairs % 2 == 0, "numChairs%2==0")
 *     .value();
 * </pre>
 * </p>
 * <p>
 * To facilitate concise coding, some methods take just the argument to be
 * tested and the name of the argument, not a complete error message. For
 * example:
 * </p>
 * <p>
 *
 * <pre>
 * Check.notNull(name, "name"); // -> resulting error message: "name must not null"
 * </pre>
 * </p>
 * <p>
 * If you want to provide a custom message, choose the overloaded method that
 * takes message arguments:
 *
 * <pre>
 * Check.notNull(name, "Please specify a name", null); // -> "Please specify a name"
 * Check.notNull(name, "Please specify a name", new Object[0]); // -> "Please specify a name"
 * Check.notNull(name, "Please specify a name", ""); // -> "Please specify a name"
 * Check.notNull(name, "Please specify a %s", "toy"); // -> "Please specify a toy"
 * </pre>
 * </p>
 *
 * @author Ayco Holleman
 */
public abstract class Check<T> {

  protected static final String ERR_FAILED_TEST = "%s failed test %s";

  /**
   * Returns a {@code Check} object that allows you to chain multiple checks on
   * the provided integer without having to repeat the argument name every time.
   *
   * @param arg The argument to be tested
   * @param argName The argument name
   * @return
   */
  public static Check<Integer> that(int arg, String argName) {
    return new IntCheck(arg, argName);
  }

  /**
   * Returns a {@code Check} object that allows you to chain multiple checks on
   * the provided {@code String} without having to repeat the argument name every
   * time.
   *
   *
   * @param arg The argument to be tested
   * @param argName The argument name
   * @return
   */
  public static Check<String> that(String arg, String argName) {
    return new StringCheck(arg, argName);
  }

  /**
   * Returns a {@code Check} object that allows you to chain multiple checks on
   * the provided {@code Integer} without having to repeat the argument name every
   * time.
   *
   * @param arg The argument to be tested
   * @param argName The argument name
   * @return
   */
  public static Check<Integer> that(Integer arg, String argName) {
    return new IntegerCheck(arg, argName);
  }

  /**
   * Returns a {@code Check} object that allows you to chain multiple checks on
   * the provided argument without having to repeat the argument name every time.
   *
   * @param arg The argument to be tested
   * @param argName The argument name
   * @return
   */
  public static <U> Check<U> that(U arg, String argName) {
    return new ObjectCheck<>(arg, argName);
  }

  /**
   * Generic check method. Can also be used for other purposes than checking
   * preconditions. Throws the exception supplied by the provided supplier if the
   * provided condition evaluates to false, else does nothing.
   *
   * @param <T> The type of exception thrown if the condition fails
   * @param condition The condition to evaluate
   * @param exceptionSupplier The exception supplier
   * @throws T The exception thrown if the condition fails
   */
  public static <T extends Exception> void that(boolean condition, Supplier<T> exceptionSupplier) throws T {
    if (!condition) {
      throw exceptionSupplier.get();
    }
  }

  /**
   * Generic check method. Throws the exception supplied by the provided exception
   * supplier if the provided condition evaluates to false, else returns
   * {@code result}.
   *
   * @param <T> The type of exception thrown if the condition fails
   * @param <U> The type of object returned if the condition is met
   * @param condition The condition to evaluate
   * @param result The object returned if the condition is met
   * @param exceptionSupplier The exception supplier
   * @return The object supplied by the resultSupplier
   * @throws T The exception thrown if the condition fails
   */
  public static <T extends Exception, U> U that(boolean condition, U result, Supplier<T> exceptionSupplier) throws T {
    if (condition) {
      return result;
    }
    throw exceptionSupplier.get();
  }

  /**
   * Generic check method. Throws the exception supplied by the provided exception
   * supplier if the provided condition evaluates to false, else returns the
   * result supplied by the result supplier.
   *
   * @param <T> The type of exception thrown if the condition fails
   * @param <U> The type of object returned if the condition is met
   * @param condition The condition to evaluate
   * @param resultSupplier The result supplier
   * @param exceptionSupplier The exception supplier
   * @return The object supplied by the resultSupplier
   * @throws T The exception thrown if the condition fails
   */
  public static <T extends Exception, U> U that(boolean condition, Supplier<U> resultSupplier, Supplier<T> exceptionSupplier) throws T {
    if (condition) {
      return resultSupplier.get();
    }
    throw exceptionSupplier.get();
  }

  /**
   * Does nothing if the provided condition evaluates to {@code true}, else throws
   * an {@code IllegalArgumentException} with the provided message.
   *
   * @param condition The condition to be evaluated
   * @param message The exception message
   * @throws IllegalArgumentException If the condition evaluates to false
   */
  public static void argument(boolean condition, String message) {
    that(condition, () -> badArgument(message));
  }

  /**
   * Returns {@code arg} if it passes the provided {@code test}, else throws an
   * {@code IllegalArgumentException} with the provided message.
   *
   * @param <T>
   * @param arg The argument to be tested
   * @param test
   * @param message
   * @return
   */
  public static <T> T argument(T arg, Predicate<T> test, String message) {
    return that(test.test(arg), arg, () -> badArgument(message));
  }

  /**
   * Returns {@code arg} if it passes the provided {@code test}, else throws an
   * {@code IllegalArgumentException} with the provided message.
   *
   * @param arg The argument to be tested
   * @param test
   * @param message
   * @return
   */
  public static int integer(int arg, IntPredicate test, String message) {
    if (test.test(arg)) {
      return arg;
    }
    throw badArgument(message);
  }

  /**
   * Does nothing if the provided condition evaluates to {@code true}, else throws
   * an {@code IllegalArgumentException} with the provided message and message
   * arguments. The message arguments may be {@code null}, in which case they are
   * ignored.
   *
   * @param condition The condition to be evaluated
   * @param message The exception message
   * @param msgArgs The {@code String.format} message arguments (may be null or
   *        empty)
   * @throws IllegalArgumentException If the condition evaluates to false
   */
  public static void argument(boolean condition, String message, Object... msgArgs) {
    that(condition, () -> badArgument(message, msgArgs));
  }

  /**
   * Returns {@code arg} if it passes the provided {@code test}, else throws an
   * {@code IllegalArgumentException} with the provided message and message
   * arguments. The message arguments may be {@code null}, in which case they are
   * ignored as message arguments.
   *
   * @param <T>
   * @param arg The argument to be tested
   * @param test The test
   * @param message The exception message
   * @param msgArgs The {@code String.format} message arguments (may be null or
   *        empty)
   * @return The argument
   */
  public static <T> T argument(T arg, Predicate<T> test, String message, Object... msgArgs) {
    return that(test.test(arg), arg, () -> badArgument(message, msgArgs));
  }

  /**
   * Returns {@code arg} if it passes the provided {@code test}, else throws an
   * {@code IllegalArgumentException} with the provided message and message
   * arguments. The message arguments may be {@code null}, in which case they are
   * ignored.
   *
   * @param arg The argument to be tested
   * @param test
   * @param message
   * @param msgArgs The {@code String.format} message arguments (may be null or
   *        empty)
   * @return The argument
   */
  public static int integer(int arg, IntPredicate test, String message, Object... msgArgs) {
    if (test.test(arg)) {
      return arg;
    }
    throw badArgument(message, msgArgs);
  }

  /**
   * Throws an {@code ArrayIndexOutOfBoundsException} if {@code arg} is less than
   * zero or greater than or equal to {@code maxExclusive}, else returns
   * {@code arg}. This is especially useful to test "from" arguments, which
   * generally should be less than length or size of the object operated upon
   * ({@code maxExclusive} would then be that length or size).
   *
   * @param arg The argument to be tested The argument to test
   * @param maxExclusive The maximum allowed value (exclusive)
   * @return The argument
   * @throws ArrayIndexOutOfBoundsException
   */
  public static int index(int arg, int maxExclusive, String argName) throws ArrayIndexOutOfBoundsException {
    if (arg < maxExclusive) {
      return arg;
    }
    throw badIndex(arg, argName);
  }

  /**
   * Throws an {@code ArrayIndexOutOfBoundsException} if {@code arg} is less than
   * {@code min} or greater than {@code max}, else returns {@code arg}. This is
   * especially useful to test "to" or "until" arguments, which generally should
   * be greater than or equal to the "from" argument, and less than <i>or equal
   * to</i> to the length or size of the object operated upon.
   *
   * @param arg The argument to be tested The argument to test
   * @param min The minimum allowed value (inclusive)
   * @param max The maximum allowed value (inclusive)
   * @return The argument
   * @throws ArrayIndexOutOfBoundsException
   */
  public static int index(int arg, int min, int max, String argName) throws ArrayIndexOutOfBoundsException {
    if (arg >= min && arg <= max) {
      return arg;
    }
    throw badIndex(arg, argName);
  }

  /**
   * Returns {@code arg} if it is not null, else throws an
   * {@code IllegalArgumentException} with the message: <i>${argName} must not be
   * null</i>.
   *
   * @param <T> The type of the argument to be tested
   * @param arg The argument to be tested
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException If the argument is null
   */
  public static <T> T notNull(T arg, String argName) throws IllegalArgumentException {
    return notNull(arg, "%s must not be null", argName);
  }

  /**
   * Returns {@code arg} if it is not null, else throws an
   * {@code IllegalArgumentException} with the provided message and message
   * arguments. The message arguments may be {@code null}, in which case they are
   * ignored.
   *
   * @param <T> The type of the argument to be tested
   * @param arg The argument to be tested
   * @param message The exception message
   * @param msgArgs The {@code String.format} message arguments (may be null or
   *        empty)
   * @return The argument
   * @throws IllegalArgumentException If the argument is null
   */
  public static <T> T notNull(T arg, String message, Object... msgArgs) throws IllegalArgumentException {
    return argument(arg, Objects::nonNull, message, msgArgs);
  }

  /**
   * Checks that the provided argument is null, else throws an
   * {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param argName The argument name
   */
  public static void isNull(Object arg, String argName) throws IllegalArgumentException {
    argument(arg == null, "%s must be null", argName);
  }

  /**
   * Returns {@code arg} if it is not null and, in case of an array or
   * {@code Collection} or {@code Map}, none of its elements/values are null.
   * Otherwise this method throws an {@code IllegalArgumentException} with the
   * message: <i>${argName} must not be null or contain null values</i>.
   *
   * @see ObjectMethods#deepNotNull(Object)
   *
   * @param <T> The type of the argument to be tested
   * @param arg The argument to be tested
   * @param argName The argument name
   * @return The argument
   */
  public static <T> T noneNull(T arg, String argName) throws IllegalArgumentException {
    return argument(arg, ObjectMethods::deepNotNull, "%s must not be null or contain null values", argName);
  }

  /**
   * Returns {@code arg} if it is not empty, else throws an
   * {@code IllegalArgumentException} with the message: <i>${argName} must not be
   * empty</i>.
   *
   * @see ObjectMethods#notEmpty(Object)
   *
   * @param arg The argument to be tested
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException If the argument is empty
   */
  public static <T> T notEmpty(T arg, String argName) throws IllegalArgumentException {
    return notEmpty(arg, "%s must not be empty", argName);
  }

  /**
   * Returns {@code arg} if it is not empty, else throws an
   * {@code IllegalArgumentException} with the provided message and message
   * arguments. The message arguments may be {@code null}, in which case they are
   * ignored.
   *
   * @see ObjectMethods#notEmpty(Object)
   *
   * @param arg The argument to be tested
   * @param message The exception message
   * @param msgArgs The {@code String.format} message arguments (may be null or
   *        empty)
   * @return The argument
   * @throws IllegalArgumentException If the argument is empty
   */
  public static <T> T notEmpty(T arg, String message, Object... msgArgs) throws IllegalArgumentException {
    return argument(arg, ObjectMethods::notEmpty, message, msgArgs);
  }

  /**
   * Returns {@code arg} if it is {@link ObjectMethods#deepNotEmpty(Object) deeply
   * non-empty}. Otherwise this method throws an {@code IllegalArgumentException}
   * with the message: <i>${argName} must not be empty or contain empty
   * values</i>.
   *
   * @see ObjectMethods#deepNotEmpty(Object)
   *
   * @param <T> The type of the argument
   * @param arg The argument to be tested
   * @param argName The argument name
   * @return The argument
   */
  public static <T> T noneEmpty(T arg, String argName) {
    return argument(arg, ObjectMethods::deepNotEmpty, "%s must not be empty or contain empty values", argName);
  }

  /**
   * Returns {@code arg} if it is {@link ObjectMethods#deepNotEmpty(Object) deeply
   * non-empty} Otherwise this method throws an {@code IllegalArgumentException}
   * with the provided message and message arguments. The message arguments may be
   * {@code null}, in which case they are ignored.
   *
   * @param <T> The type of the argument
   * @param arg The argument to be tested
   * @param message The exception message
   * @param msgArgs The {@code String.format} message arguments (may be null or
   *        empty)
   * @return The argument
   */
  public static <T> T noneEmpty(T arg, String message, Object... msgArgs) throws IllegalArgumentException {
    return argument(arg, ObjectMethods::deepNotEmpty, message, msgArgs);
  }

  /**
   * Returns {@code arg} if it is not blank, else throws an
   * {@code IllegalArgumentException} with the message: <i>${argName} must not be
   * blank</i>.
   *
   * @param arg The argument to be tested
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException If the argument is blank
   */
  public static String notBlank(String arg, String argName) throws IllegalArgumentException {
    return argument(arg, StringMethods::notBlank, "%s must not be blank", argName);
  }

  /**
   * Returns {@code arg} if it is not blank, else throws an
   * {@code IllegalArgumentException} with the provided message and message
   * arguments. {@code msgArg} is allowed to be null, in which case it is ignored
   * as a message argument.
   *
   * @param arg The argument to be tested
   * @param message The exception message
   * @param msgArgs The {@code String.format} message arguments (may be null or
   *        empty)
   * @return The argument
   * @throws IllegalArgumentException If the argument is blank
   */
  public static String notBlank(String arg, String message, Object... msgArgs) throws IllegalArgumentException {
    return argument(arg, StringMethods::notBlank, message, msgArgs);
  }

  /**
   * Returns {@code arg} if it is greater than {@code minVal}, else throws an
   * {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param minVal The argument's lower bound (exclusive)
   * @param argName The argument name
   * @return
   */
  public static int gt(int arg, int minVal, String argName) throws IllegalArgumentException {
    return integer(arg, x -> x > minVal, "%s must be greater than %d", argName, minVal);
  }

  /**
   * Returns {@code arg} if it is greater than {@code minVal}, else throws an
   * {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param minVal The argument's lower bound (exclusive)
   * @param message The error message
   * @param msgArgs The {@code String.format} message arguments (may be null or
   *        empty)
   * @return The argument
   * @throws IllegalArgumentException
   */
  public static int gt(int arg, int minVal, String message, Object... msgArgs) throws IllegalArgumentException {
    return integer(arg, x -> x > minVal, message, msgArgs);
  }

  /**
   * Returns {@code arg} if it is greater than or equal to {@code minVal}, else
   * throws an {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param minVal The argument's lower bound (inclusive)
   * @param argName The argument name
   * @return
   * @throws IllegalArgumentException
   */
  public static int gte(int arg, int minVal, String argName) throws IllegalArgumentException {
    return integer(arg, x -> x >= minVal, "%s must be greater than or equal to %d", argName, minVal);
  }

  /**
   * Returns {@code arg} if it is greater than or equal to {@code minVal}, else
   * throws an {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param minVal The argument's lower bound (inclusive)
   * @param message The error message
   * @param msgArgs The {@code String.format} message arguments (may be null or
   *        empty)
   * @return The argument
   * @throws IllegalArgumentException
   */
  public static int gte(int arg, int minVal, String message, Object... msgArgs) throws IllegalArgumentException {
    return integer(arg, x -> x >= minVal, message, msgArgs);
  }

  /**
   * Returns {@code arg} if it is less than {@code maxVal}, else throws an
   * {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param maxVal The argument's upper bound (exclusive)
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException
   */
  public static int lt(int arg, int maxVal, String argName) throws IllegalArgumentException {
    return integer(arg, x -> x < maxVal, "%s must be less than %d", argName, maxVal);
  }

  /**
   * Returns {@code arg} if it is less than {@code maxVal}, else throws an
   * {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param maxVal The argument's upper bound (exclusive)
   * @param message The error message
   * @param msgArgs The {@code String.format} message arguments (may be null or
   *        empty)
   * @retur The argument
   * @throws IllegalArgumentException
   */
  public static int lt(int arg, int maxVal, String message, Object... msgArgs) throws IllegalArgumentException {
    return integer(arg, x -> x > maxVal, message, msgArgs);
  }

  /**
   * Returns {@code arg} is less than or equal to {@code maxVal}, else throws an
   * {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param maxVal The argument's upper bound (inclusive)
   * @param argName The argument name
   * @return The argument
   * @throws IllegalArgumentException
   */
  public static int lte(int arg, int maxVal, String argName) throws IllegalArgumentException {
    return integer(arg, x -> x <= maxVal, "%s must be less than or equal to %d", argName, maxVal);
  }

  /**
   * Returns {@code arg} is less than or equal to {@code maxVal}, else throws an
   * {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param maxVal The argument's upper bound (inclusive)
   * @param message The error message
   * @param msgArgs The {@code String.format} message arguments (may be null or
   *        empty)
   * @return The argument
   * @throws IllegalArgumentException
   */
  public static int lte(int arg, int maxVal, String message, Object... msgArgs) throws IllegalArgumentException {
    return integer(arg, x -> x <= maxVal, message, msgArgs);
  }

  /**
   * Returns {@code arg} if it is between {@code minInclusive} and
   * {@code maxExclusive}, else throws an {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param minInclusive
   * @param maxExclusive
   * @param argName The argument name
   * @return The argument
   */
  public static int between(int arg, int minInclusive, int maxExclusive, String argName) throws IllegalArgumentException {
    return integer(arg,
        x -> x >= minInclusive && x < maxExclusive,
        "%s must be between %d and %d (exclusive)",
        argName,
        minInclusive,
        maxExclusive);
  }

  /**
   * Returns {@code arg} if it is between {@code minInclusive} and
   * {@code maxInclusive}, else throws an {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param minInclusive
   * @param maxInclusive
   * @param argName The argument name
   * @return The argument
   */
  public static int inRange(int arg, int minInclusive, int maxInclusive, String argName) {
    return integer(arg,
        x -> x >= minInclusive && x <= maxInclusive,
        "%s must be between %d and %d (inclusive)",
        argName,
        minInclusive,
        maxInclusive);
  }

  /**
   * Does nothing if the provided condition evaluates to {@code true}, else throws
   * an {@link IllegalStateException} with the provided message.
   *
   * @param condition The condition to be evaluated
   * @param message The exception message
   * @throws IllegalStateException If the condition evaluates to {@code false}
   */
  public static void state(boolean condition, String message) {
    that(condition, () -> badState(message));
  }

  /**
   * Does nothing if the provided condition evaluates to {@code true}, else throws
   * an {@link IllegalStateException} with the provided message and message
   * arguments. {@code msgArg} is allowed to be null, in which case it is ignored
   * as a message argument.
   *
   * @param condition
   * @param message The exception message
   * @param msgArgs The {@code String.format} message arguments (may be null or
   *        empty)
   * @throws IllegalStateException If the condition evaluates to {@code false}
   */
  public static void state(boolean condition, String message, Object... msgArgs) throws IllegalStateException {
    that(condition, () -> badState(message, msgArgs));
  }

  private static IllegalArgumentException badArgument(String msg, Object... msgArgs) {
    if (deepNotEmpty(msgArgs)) {
      return new IllegalArgumentException(String.format(msg, msgArgs));
    }
    throw new IllegalArgumentException(msg);
  }

  private static IllegalStateException badState(String msg, Object... msgArgs) {
    if (deepNotEmpty(msgArgs)) {
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

  protected Check(String argName) {
    this.argName = argName;
  }

  /**
   * Throws an {@code IllegalArgumentException} if the provided condition
   * evaluates to false, else return this {@code Check} instance.
   *
   * @param condition The condition to evaluate
   * @param descr A description of the test.
   * @return This {@code Check} object
   * @throws IllegalArgumentException If the condition evaluates to false
   */
  public Check<T> test(boolean condition, String descr) throws IllegalArgumentException {
    return test(condition, descr, IllegalArgumentException::new);
  }

  /**
   *
   * @param <E> The type of the exception being thrown
   * @param condition The condition to evaluate
   * @param descr A description of the test
   * @param excProvider A function that takes a {@code String} and returns an
   *        {@code Exception}
   * @return This {@code Check} object
   * @throws E If the condition evaluates to false
   */
  public <E extends Exception> Check<T> test(boolean condition, String descr, Function<String, E> excProvider) throws E {
    that(condition, () -> excProvider.apply(String.format(ERR_FAILED_TEST, argName, descr)));
    return this;
  }

  /**
   * Throws an {@l IllegalArgumentException} if the provided predicate evaluates
   * to false for the argument in this {@code Check} object, else return this
   * {@code Check} instance.
   *
   * @param test The condition to apply to the argument
   * @param descr A description of the test.
   * @return This {@code Check} object
   * @throws IllegalArgumentException If {@code test} evaluates to false
   */
  public Check<T> test(Predicate<T> test, String descr) throws IllegalArgumentException {
    throw notSupported("test");
  }

  /**
   * Throws an {@l IllegalArgumentException} if the provided predicate evaluates
   * to false for the argument in this {@code Check} object, else return this
   * {@code Check} instance. The type of exception being thrown is determined by
   * the {@code excProvider} function which is passed the error message and
   * returns the exception to be thrown.
   *
   * @param <E> The type of the exception being thrown
   * @param test The condition to apply to the argument
   * @param descr A description of the test.
   * @param excProvider A function that takes a {@code String} and returns an
   *        {@code Exception}
   * @return This {@code Check} object
   * @throws E If {@code test} evaluates to false
   */
  public <E extends Exception> Check<T> test(Predicate<T> test, String descr, Function<String, E> excProvider) throws E {
    throw notSupported("test");
  }

  /**
   * Throws an {@l IllegalArgumentException} if the provided predicate evaluates
   * to false for the argument in this {@code Check} object, else return this
   * {@code Check} instance.
   *
   * @param test The condition to apply to the argument
   * @param descr A description of the test.
   * @return This {@code Check} object
   * @throws IllegalArgumentException If {@code test} evaluates to false
   */
  public Check<T> testInt(IntPredicate test, String descr) throws IllegalArgumentException {
    throw notSupported("testInt");
  }

  /**
   * Throws an {@l IllegalArgumentException} if the provided predicate evaluates
   * to false for the argument in this {@code Check} object, else return this
   * {@code Check} instance. The type of exception being thrown is determined by
   * the {@code excProvider} function which is passed the error message and
   * returns the exception to be thrown.
   *
   * @param <E> The type of the exception being thrown
   * @param test The condition to apply to the argument
   * @param descr A description of the test.
   * @param excProvider Afunction that takes a {@code String} and returns an
   *        {@code Exception}.
   * @return This {@code Check} object
   * @throws E If {@code test} evaluates to false
   */
  public <E extends Exception> Check<T> testInt(IntPredicate test, String descr, Function<String, E> excProvider) throws E {
    throw notSupported("testInt");
  }

  /**
   * See {@link #notNull(Object, String) notNull}.
   *
   * @return This {@code Check} object
   */
  public Check<T> notNull() {
    throw notSupported("notNull");
  }

  /**
   * See {@link #noneNull(Object, String) noneNull}.
   *
   * @return This {@code Check} object
   */
  public Check<T> noneNull() {
    throw notSupported("noneNull");
  }

  /**
   * See {@link #notEmpty(Object, String) notEmpty}.
   *
   * @return This {@code Check} object
   */
  public Check<T> notEmpty() {
    throw notSupported("notEmpty");
  }

  /**
   * See {@link #noneEmpty(Object, String) noneEmpty}.
   *
   * @return This {@code Check} object
   */
  public Check<T> noneEmpty() {
    throw notSupported("noneEmpty");
  }

  /**
   * See {@link #notBlank(String, String) notBlank}.
   *
   * @return This {@code Check} object
   */
  public Check<T> notBlank() {
    throw notSupported("notBlank");
  }

  /**
   * See {@link #gt(int, int, String) gt}.
   *
   * @return This {@code Check} object
   */
  @SuppressWarnings("unused")
  public Check<T> gt(int minVal) {
    throw notSupported("gt");
  }

  /**
   * See {@link #gte(int, int, String) gte}.
   *
   * @return This {@code Check} object
   */
  @SuppressWarnings("unused")
  public Check<T> gte(int minVal) {
    throw notSupported("gte");
  }

  /**
   * See {@link #lt(int, int, String) lt}.
   *
   * @return This {@code Check} object
   */
  @SuppressWarnings("unused")
  public Check<T> lt(int maxVal) {
    throw notSupported("lt");
  }

  /**
   * See {@link #lte(int, int, String) lte}.
   *
   * @return This {@code Check} object
   */
  @SuppressWarnings("unused")
  public Check<T> lte(int maxVal) {
    throw notSupported("lte");
  }

  /**
   * See {@link #between(int, int, int, String) between}.
   *
   * @return This {@code Check} object
   */
  @SuppressWarnings("unused")
  public Check<T> between(int minInclusive, int maxExclusive) {
    throw notSupported("between");
  }

  /**
   * See {@link #inRange(int, int, int, String) inRange}.
   *
   * @return This {@code Check} object
   */
  @SuppressWarnings("unused")
  public Check<T> inRange(int minInclusive, int maxInclusive) {
    throw notSupported("inRange");
  }

  /**
   * Returns the argument being tested. To be used as the last call after a chain
   * of checks. For example:
   *
   * <pre>
   * Integer i = Check.that(counter, "counter").notNull().value();
   * </pre>
   *
   * @param <U>
   * @return The argument
   */
  public abstract T value();

  /**
   * Returns the argument being tested as an {@code int}. If the argument being
   * tested actually is an {@code int} rather than an {@code Integer}, this method
   * saves the cost of unboxing incurred by {@link #value()}. If the argument
   * neither is {@code int} nor an {@code Integer} this method throws a
   * {@code ClassCastException}.
   *
   * @return The argument cast or converted to an {@code int}
   */
  public int intValue() {
    throw notSupported("intValue");

  }

  private UnsupportedOperationException notSupported(String check) {
    return new UnsupportedOperationException(String.format("%s method not supported for %s", check, argName));
  }

}
