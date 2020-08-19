package nl.naturalis.common;

import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import nl.naturalis.common.internal.IntCheck;
import nl.naturalis.common.internal.IntegerCheck;
import nl.naturalis.common.internal.ObjectCheck;
import nl.naturalis.common.internal.StringCheck;

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
 *     .satisfies(x -> x % 2 == 0)
 *     .value();
 * </pre>
 * </p>
 * <p>
 * To facilitate concise coding, some check methods just require you to pass the
 * name of the argument (along with the argument itself). For example:
 * </p>
 * <p>
 *
 * <pre>
 * Check.notNull(name, "name"); // -> "name must not null"
 * </pre>
 * </p>
 * <p>
 * If you want to provide a custom message, choose the overloaded method that
 * takes message arguments:
 *
 * <pre>
 * Check.notNull(name, "Please specify a name", null); // -> "Please specify a name"
 * Check.notNull(name, "Please specify a %s", "toy"); // -> "Please specify a toy"
 * </pre>
 * </p>
 *
 * @author Ayco Holleman
 */
public abstract class Check {

  /**
   * Returns a {@code Check} object that allows you to chain multiple checks on
   * the provided integer without having to repeat the argument name every time.
   *
   * @param arg The argument to be tested
   * @param argName The argument name
   * @return
   */
  public static Check that(int arg, String argName) {
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
  public static Check that(String arg, String argName) {
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
  public static Check that(Integer arg, String argName) {
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
  public static <T> Check that(T arg, String argName) {
    return new ObjectCheck<>(arg, argName);
  }

  /**
   * Generic check method. Can also be used for other purposes than checking
   * preconditions. Throws the exception supplied by the provided supplier if the
   * provided condition evaluates to false.
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
   * @param msgArg The 1st {@code String.format} message argument
   * @param moreMsgArgs The remaining {@code String.format} message arguments
   * @throws IllegalArgumentException If the condition evaluates to false
   */
  public static void argument(boolean condition, String message, Object msgArg, Object... moreMsgArgs) {
    that(condition, () -> badArgument(message, withMessageArguments(msgArg, moreMsgArgs)));
  }

  /**
   * Returns {@code arg} if it passes the provided {@code test}, else throws an
   * {@code IllegalArgumentException} with the provided message and message
   * arguments. The message arguments may be {@code null}, in which case they are
   * ignored as message arguments.
   *
   * @param <T>
   * @param arg The argument to be tested
   * @param test
   * @param message
   * @param msgArg
   * @param moreMsgArgs
   * @return
   */
  public static <T> T argument(T arg, Predicate<T> test, String message, Object msgArg, Object... moreMsgArgs) {
    return that(test.test(arg), arg, () -> badArgument(message, withMessageArguments(msgArg, moreMsgArgs)));
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
   * @param msgArg
   * @param moreMsgArgs
   * @return
   */
  public static int integer(int arg, IntPredicate test, String message, Object msgArg, Object... moreMsgArgs) {
    if (test.test(arg)) {
      return arg;
    }
    throw badArgument(message, withMessageArguments(msgArg, moreMsgArgs));
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
   * @return
   * @throws ArrayIndexOutOfBoundsException
   */
  public static int index(int arg, int maxExclusive) throws ArrayIndexOutOfBoundsException {
    if (arg < maxExclusive) {
      return arg;
    }
    throw new ArrayIndexOutOfBoundsException(arg);
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
   * @return
   * @throws ArrayIndexOutOfBoundsException
   */
  public static int index(int arg, int min, int max) throws ArrayIndexOutOfBoundsException {
    if (arg >= min && arg <= max) {
      return arg;
    }
    throw new ArrayIndexOutOfBoundsException(arg);
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
  public static <T> T notNull(T arg, String argName) {
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
   * @param msgArg The 1st {@code String.format} message argument
   * @param moreMsgArgs The remaining {@code String.format} message arguments
   * @return The argument
   * @throws IllegalArgumentException If the argument is null
   */
  public static <T> T notNull(T arg, String message, Object msgArg, Object... moreMsgArgs) {
    return argument(arg, Objects::nonNull, message, msgArg, moreMsgArgs);
  }

  /**
   * Checks that the provided argument is null, else throws an
   * {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param argName The argument name
   */
  public static void isNull(Object arg, String argName) {
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
   * @return
   */
  public static <T> T noneNull(T arg, String argName) {
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
  public static <T> T notEmpty(T arg, String argName) {
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
   * @param msgArg The 1st {@code String.format} message argument
   * @param moreMsgArgs The remaining {@code String.format} message arguments
   * @return The argument
   * @throws IllegalArgumentException If the argument is empty
   */
  public static <T> T notEmpty(T arg, String message, Object msgArg, Object... moreMsgArgs) {
    return argument(arg, ObjectMethods::notEmpty, message, msgArg, moreMsgArgs);
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
   * @return
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
   * @param msgArg The 1st message argument (or null if the message has no message
   *        arguments)
   * @param moreMsgArgs The remaining message arguments (or null if the message
   *        has no message arguments)
   * @return The argument
   */
  public static <T> T noneEmpty(T arg, String message, Object msgArg, Object... moreMsgArgs) {
    return argument(arg, ObjectMethods::deepNotEmpty, message, msgArg, moreMsgArgs);
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
  public static String notBlank(String arg, String argName) {
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
   * @param msgArg The 1st {@code String.format} message argument
   * @param moreMsgArgs The remaining {@code String.format} message arguments
   * @return The argument
   * @throws IllegalArgumentException If the argument is blank
   */
  public static String notBlank(String arg, String message, Object msgArg, Object... moreMsgArgs) {
    return argument(arg, StringMethods::notBlank, message, msgArg, moreMsgArgs);
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
  public static int gt(int arg, int minVal, String argName) {
    return integer(arg, x -> x > minVal, "%s must be greater than %d", argName, minVal);
  }

  /**
   * Returns {@code arg} if it is greater than {@code minVal}, else throws an
   * {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param minVal The argument's lower bound (exclusive)
   * @param message The error message
   * @param msgArg The first message argument (may be null)
   * @param moreMsgArgs The remaining message arguments (may be null or empty)
   * @return
   * @throws IllegalArgumentException
   */
  public static int gt(int arg, int minVal, String message, Object msgArg, Object... moreMsgArgs) throws IllegalArgumentException {
    return integer(arg, x -> x > minVal, message, msgArg, moreMsgArgs);
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
   * @param msgArg The first message argument (may be null)
   * @param moreMsgArgs The remaining message arguments (may be null or empty)
   * @return
   * @throws IllegalArgumentException
   */
  public static int gte(int arg, int minVal, String message, Object msgArg, Object... moreMsgArgs) throws IllegalArgumentException {
    return integer(arg, x -> x >= minVal, message, msgArg, moreMsgArgs);
  }

  /**
   * Returns {@code arg} if it is less than {@code maxVal}, else throws an
   * {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param maxVal The argument's upper bound (exclusive)
   * @param argName The argument name
   * @return
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
   * @param msgArg The first message argument (may be null)
   * @param moreMsgArgs The remaining message arguments (may be null or empty)
   * @return
   * @throws IllegalArgumentException
   */
  public static int lt(int arg, int maxVal, String message, Object msgArg, Object... moreMsgArgs) throws IllegalArgumentException {
    return integer(arg, x -> x > maxVal, message, msgArg, moreMsgArgs);
  }

  /**
   * Returns {@code arg} is less than or equal to {@code maxVal}, else throws an
   * {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param maxVal The argument's upper bound (inclusive)
   * @param argName The argument name
   * @return
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
   * @param msgArg The first message argument (may be null)
   * @param moreMsgArgs The remaining message arguments (may be null or empty)
   * @return
   * @throws IllegalArgumentException
   */
  public static int lte(int arg, int maxVal, String message, Object msgArg, Object... moreMsgArgs) throws IllegalArgumentException {
    return integer(arg, x -> x <= maxVal, message, msgArg, moreMsgArgs);
  }

  /**
   * Returns {@code arg} if it is between {@code minInclusive} and
   * {@code maxExclusive}, else throws an {@code IllegalArgumentException}.
   *
   * @param arg The argument to be tested
   * @param minInclusive
   * @param maxExclusive
   * @param argName The argument name
   * @return
   */
  public static int between(int arg, int minInclusive, int maxExclusive, String argName) {
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
   * @return
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
   * @param msgArg The 1st {@code String.format} message argument
   * @param moreMsgArgs The remaining {@code String.format} message arguments
   * @throws IllegalStateException If the condition evaluates to {@code false}
   */
  public static void state(boolean condition, String message, Object msgArg, Object... moreMsgArgs) {
    that(condition, () -> badState(message, withMessageArguments(msgArg, moreMsgArgs)));
  }

  private static IllegalArgumentException badArgument(String msg, Object... msgArgs) {
    return new IllegalArgumentException(String.format(msg, msgArgs));
  }

  private static IllegalStateException badState(String msg, Object... msgArgs) {
    return new IllegalStateException(String.format(msg, msgArgs));
  }

  private static Object[] withMessageArguments(Object msgArg, Object[] moreMsgArgs) {
    if (msgArg == null) {
      if (moreMsgArgs == null) {
        return ArrayMethods.EMPTY_OBJECT_ARRAY;
      }
      return moreMsgArgs;
    } else if (moreMsgArgs == null) {
      return new Object[] {msgArg};
    }
    return ArrayMethods.prefix(moreMsgArgs, msgArg);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////
  // Instance fields / methods
  //////////////////////////////////////////////////////////////////////////////////////////////////////

  protected final String argName;

  protected Check(String argName) {
    this.argName = argName;
  }

  /**
   * See {@link #argument(Object, Predicate, String)}.
   *
   * @param test
   * @return
   */
  public Check satisfies(Predicate<Object> test) {
    throw notApplicable("satisfies");
  }

  /**
   * See {@link #integer(int, IntPredicate, String)}.
   *
   * @param test
   * @return
   */
  public Check satisfies(IntPredicate test) {
    throw notApplicable("satisfies");
  }

  /**
   * See {@link #notNull(Object, String)}.
   *
   * @return
   */
  public Check notNull() {
    throw notApplicable("notNull");
  }

  /**
   * See {@link #noneNull(Object, String)}.
   *
   * @return
   */
  public Check noneNull() {
    throw notApplicable("noneNull");
  }

  /**
   * See {@link #notEmpty(Object, String)}.
   *
   * @return
   */
  public Check notEmpty() {
    throw notApplicable("notEmpty");
  }

  /**
   * See {@link #noneEmpty(Object, String)}.
   *
   * @return
   */
  public Check noneEmpty() {
    throw notApplicable("noneEmpty");
  }

  /**
   * See {@link #notBlank(String, String)}.
   *
   * @return
   */
  public Check notBlank() {
    throw notApplicable("notBlank");
  }

  /**
   * See {@link #gt(int, int, String)}.
   *
   * @param minVal
   */
  public Check gt(int minVal) {
    throw notApplicable("gt");
  }

  /**
   * See {@link #gte(int, int, String)}.
   *
   * @param minVal
   */
  public Check gte(int minVal) {
    throw notApplicable("gte");
  }

  /**
   * See {@link #lt(int, int, String)}.
   *
   * @param maxVal
   */
  public Check lt(int maxVal) {
    throw notApplicable("lt");
  }

  /**
   * See {@link #lte(int, int, String)}.
   *
   * @param maxVal
   */
  public Check lte(int maxVal) {
    throw notApplicable("lte");
  }

  /**
   * See {@link #between(int, int, int, String)}.
   *
   * @param minInclusive
   * @param maxExclusive
   * @return
   */
  public Check between(int minInclusive, int maxExclusive) {
    throw notApplicable("between");
  }

  /**
   * See {@link #inRange(int, int, int, String)}.
   *
   * @param minInclusive
   * @param maxInclusive
   * @return
   */
  public Check inRange(int minInclusive, int maxInclusive) {
    throw notApplicable("inRange");
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
   * @return
   */
  public <U> U value() {
    return null;
  }

  /**
   * Returns the argument being tested as an {@code int}. If the argument being
   * tested actually is an {@code int} rather than an {@code Integer}, this method
   * saves the cost of unboxing incurred by {@link #value()}. If the argument
   * neither is {@code int} nor an {@code Integer} this method throws a
   * {@code ClassCastException}.
   *
   * @return
   */
  public int intValue() {
    throw new ClassCastException(argName + "cannot be cast to int");
  }

  private ClassCastException notApplicable(String check) {
    return new ClassCastException(String.format("%s check not applicable to %s", check, argName));
  }

}
