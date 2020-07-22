package nl.naturalis.common;

import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Methods for checking preconditions.
 *
 * @author Ayco Holleman
 */
public abstract class Check {

  /**
   * Returns a {@code Check} object that allows you to chain multiple checks on the provided integer
   * without having to repeat the argument name every time. For example:
   * 
   * <pre>
   * int i = Check.that(numChairs, "numChairs").positive().satisfies(x -> x % 2 == 0).value();
   * </pre>
   * 
   * @param arg
   * @param argName
   * @return
   */
  public static Check that(int arg, String argName) {
    return new IntCheck(arg, argName);
  }

  /**
   * Returns a {@code Check} object that allows you to chain multiple checks on the provided
   * {@code String} without having to repeat the argument name every time.
   * 
   * 
   * @param arg
   * @param argName
   * @return
   */
  public static Check that(String arg, String argName) {
    return new StringCheck(arg, argName);
  }

  /**
   * Returns a {@code Check} object that allows you to chain multiple checks on the provided
   * {@code Integer} without having to repeat the argument name every time.
   * 
   * @param arg
   * @param argName
   * @return
   */
  public static Check that(Integer arg, String argName) {
    return new IntegerCheck(arg, argName);
  }

  /**
   * Returns a {@code Check} object that allows you to chain multiple checks on the provided argument
   * without having to repeat the argument name every time.
   * 
   * @param arg
   * @param argName
   * @return
   */
  public static <T> Check that(T arg, String argName) {
    return new ObjectCheck<>(arg, argName);
  }

  /**
   * Generic check method. Can also be used for other purposes than checking preconditions. Throws the
   * exception supplied by the provided supplier if the provided condition evaluates to false.
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
   * Generic check method. Throws the exception supplied by the provided exception supplier if the
   * provided condition evaluates to false, else returns {@code result}.
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
   * Generic check method. Throws the exception supplied by the provided exception supplier if the
   * provided condition evaluates to false, else returns the result supplied by the result supplier.
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
   * Does nothing if the provided condition evaluates to {@code true}, else throws an
   * {@code IllegalArgumentException} with the provided message.
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
   * @param arg
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
   * @param arg
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
   * Does nothing if the provided condition evaluates to {@code true}, else throws an
   * {@code IllegalArgumentException} with the provided message and message arguments. The message
   * arguments may be {@code null}, in which case they are ignored.
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
   * {@code IllegalArgumentException} with the provided message and message arguments. The message
   * arguments may be {@code null}, in which case they are ignored as message arguments.
   * 
   * @param <T>
   * @param arg
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
   * {@code IllegalArgumentException} with the provided message and message arguments. The message
   * arguments may be {@code null}, in which case they are ignored.
   * 
   * @param arg
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
   * Returns {@code arg} if it is not null, else throws an {@code IllegalArgumentException} with the
   * message: <b>Illegal argument: null</b>.
   * 
   * @param <T> The type of the argument being tested
   * @param arg The argument being tested
   * @return The argument
   * @throws IllegalArgumentException If the argument is null
   */
  public static <T> T notNull(T arg) {
    return notNull(arg, "Illegal argument: null", null);
  }

  /**
   * Returns {@code arg} if it is not null, else throws an {@code IllegalArgumentException} with the
   * message: <b>${argName} must not be null</b>.
   * 
   * @param <T> The type of the argument being tested
   * @param arg The argument being tested
   * @param argName The name of the argument being tested
   * @return The argument
   * @throws IllegalArgumentException If the argument is null
   */
  public static <T> T notNull(T arg, String argName) {
    return notNull(arg, "%s must not be null", argName);
  }

  /**
   * Returns {@code arg} if it is not null, else throws an {@code IllegalArgumentException} with the
   * provided message and message arguments. The message arguments may be {@code null}, in which case
   * they are ignored.
   * 
   * @param <T> The type of the argument being tested
   * @param arg The argument being tested
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
   * Returns {@code arg} if it is not null and, in case of an array or {@code Collection}, none of its
   * elements are null. Otherwise this method throws an {@code IllegalArgumentException} with the
   * message: <b>${argName} must not be null or contain null values</b>.
   * 
   * @see ObjectMethods#notNullRecursive(Object)
   * 
   * @param <T> The type of the argument being tested
   * @param arg The argument being tested
   * @param argName The argument name
   * @return
   */
  public static <T> T noneNull(T arg, String argName) {
    return argument(arg, ObjectMethods::notNullRecursive, "%s must not be null or contain null values", argName);
  }

  /**
   * Returns {@code arg} if it is not empty, else throws an {@code IllegalArgumentException} with the
   * message: <b>Illegal argument: empty</b>.
   * 
   * @see ObjectMethods#isEmpty(Object)
   * 
   * @param arg The argument being tested
   * @return The argument
   * @throws IllegalArgumentException If the argument is empty
   */
  public static <T> T notEmpty(T arg) {
    return notEmpty(arg, "Illegal argument: empty", null);
  }

  /**
   * Returns {@code arg} if it is not empty, else throws an {@code IllegalArgumentException} with the
   * message: <b>${argName} must not be empty</b>.
   * 
   * @see ObjectMethods#isEmpty(Object)
   * 
   * @param arg The argument being tested
   * @param argName The name of the argument being tested
   * @return The argument
   * @throws IllegalArgumentException If the argument is empty
   */
  public static <T> T notEmpty(T arg, String argName) {
    return notEmpty(arg, "%s must not be empty", argName);
  }

  /**
   * Returns {@code arg} if it is not empty, else throws an {@code IllegalArgumentException} with the
   * provided message and message arguments. The message arguments may be {@code null}, in which case
   * they are ignored.
   * 
   * @see ObjectMethods#isEmpty(Object)
   * 
   * @param arg The argument being tested
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
   * Returns {@code arg} if it is not empty and, in case of an array or {@code Collection}, none of
   * its elements are empty. Otherwise this method throws an {@code IllegalArgumentException} with the
   * message: <b>${argName} empty not be null or contain empty values</b>.
   * 
   * @see ObjectMethods#notEmptyRecursive(Object)
   * 
   * @param <T> The argument being tested
   * @param arg The exception message
   * @param argName The argument name
   * @return
   */
  public static <T> T noneEmpty(T arg, String argName) {
    return argument(arg, ObjectMethods::notEmptyRecursive, "%s must not be empty or contain empty values", argName);
  }

  /**
   * Returns {@code arg} if it is not blank, else throws an {@code IllegalArgumentException} with the
   * message: <b>Illegal argument: blank</b>.
   * 
   * @param arg The argument being tested
   * @return The argument
   * @throws IllegalArgumentException If the argument is blank
   */
  public static String notBlank(String arg) {
    return argument(arg, StringMethods::notBlank, "Illegal argument: blank");
  }

  /**
   * Returns {@code arg} if it is not blank, else throws an {@code IllegalArgumentException} with the
   * message: <b>${argName} must not be blank</b>.
   * 
   * @param arg The argument being tested
   * @param argName The name of the argument being tested
   * @return The argument
   * @throws IllegalArgumentException If the argument is blank
   */
  public static String notBlank(String arg, String argName) {
    return argument(arg, StringMethods::notBlank, "%s must not be blank", argName);
  }

  /**
   * Returns {@code arg} if it is not blank, else throws an {@code IllegalArgumentException} with the
   * provided message and message arguments. {@code msgArg} is allowed to be null, in which case it is
   * ignored as a message argument.
   * 
   * @param arg The argument being tested
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
   * @param arg
   * @param minVal
   * @param argName
   * @return
   */
  public static int gt(int arg, int minVal, String argName) {
    return integer(arg, x -> x > minVal, "%s must be greater than %d", argName, minVal);
  }

  /**
   * Returns {@code arg} if it is greater than or equal to {@code minVal}, else throws an
   * {@code IllegalArgumentException}.
   * 
   * @param arg
   * @param minVal
   * @param argName
   * @return
   */
  public static int gte(int arg, int minVal, String argName) {
    return integer(arg, x -> x >= minVal, "%s must be greater than or equal to %d", argName, minVal);
  }

  /**
   * Returns {@code arg} if it is less than {@code maxVal}, else throws an
   * {@code IllegalArgumentException}.
   * 
   * @param arg
   * @param maxVal
   * @param argName
   * @return
   */
  public static int lt(int arg, int maxVal, String argName) {
    return integer(arg, x -> x < maxVal, "%s must be less than %d", argName, maxVal);
  }

  /**
   * Returns {@code arg} is less than or equal to {@code maxVal}, else throws an
   * {@code IllegalArgumentException}.
   * 
   * @param arg
   * @param maxVal
   * @param argName
   * @return
   */
  public static int lte(int arg, int maxVal, String argName) {
    return integer(arg, x -> x <= maxVal, "%s must be less than or equal to %d", argName, maxVal);
  }

  /**
   * Returns {@code arg} if it is between {@code minInclusive} and {@code maxExclusive}, else throws
   * an {@code IllegalArgumentException}.
   * 
   * @param arg
   * @param minInclusive
   * @param maxExclusive
   * @param argName
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
   * Returns {@code arg} if it is between {@code minInclusive} and {@code maxInclusive}, else throws
   * an {@code IllegalArgumentException}.
   * 
   * @param arg
   * @param minInclusive
   * @param maxInclusive
   * @param argName
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
   * Does nothing if the provided condition evaluates to {@code true}, else throws an
   * {@link IllegalStateException} with the provided message.
   * 
   * @param condition The condition to be evaluated
   * @param message The exception message
   * @throws IllegalStateException If the condition evaluates to {@code false}
   */
  public static void state(boolean condition, String message) {
    that(condition, () -> badState(message));
  }

  /**
   * Does nothing if the provided condition evaluates to {@code true}, else throws an
   * {@link IllegalStateException} with the provided message and message arguments. {@code msgArg} is
   * allowed to be null, in which case it is ignored as a message argument.
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
  // Instance fiels / methods
  //////////////////////////////////////////////////////////////////////////////////////////////////////

  final String argName;

  private Check() { // won't be called
    this(null);
  }

  private Check(String argName) {
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

  public Check noneNull() {
    throw notApplicable("noneNull");
  }

  /**
   * See #notEmpty(Object, String).
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
   * Equivalent to <code>gt(0)</code>.
   *
   * @return
   */
  public Check positive() {
    return gt(0);
  }

  /**
   * Equivalent to <code>lt(0)</code>.
   *
   * @return
   */
  public Check negative() {
    return lt(0);
  }

  /**
   * Equivalent to <code>lte(0)</code>.
   *
   * @return
   */
  public Check notPositive() {
    return lte(0);
  }

  /**
   * Equivalent to <code>gte(0)</code>.
   *
   * @return
   */
  public Check notNegative() {
    return gte(0);
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
   * Returns the argument being tested. To be used as the last call after a chain of checks. For
   * example:
   * 
   * <pre>
   * Integer i = Check.that(counter, "counter").notNull().notNegative().value();
   * </pre>
   * 
   * @param <U>
   * @return
   */
  public <U> U value() {
    return null;
  }

  /**
   * Returns the argument being tested as an {@code int}. If the argument being tested actually is an
   * {@code int} rather than an {@code Integer}, this method saves the cost of unboxing incurred by
   * {@link #value()}. If the argument neither is {@code int} nor an {@code Integer} this method
   * throws a {@code ClassCastException}.
   * 
   * @return
   */
  public int intValue() {
    throw new ClassCastException(argName + "cannot be cast to int");
  }

  private ClassCastException notApplicable(String check) {
    return new ClassCastException(String.format("%s check not applicable to %s", check, argName));
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////
  // Private subclasses of Check
  //////////////////////////////////////////////////////////////////////////////////////////////////////

  private static class ObjectCheck<T> extends Check {

    final T arg;

    private ObjectCheck(T arg, String argName) {
      super(argName);
      this.arg = arg;
    }

    @Override
    public Check satisfies(Predicate<Object> test) {
      argument(arg, test, argName, "%s does not satisfy %s", argName, test);
      return this;
    }

    @Override
    public ObjectCheck<T> notNull() {
      notNull(arg, argName);
      return this;
    }

    @Override
    public ObjectCheck<T> noneNull() {
      noneNull(arg, argName);
      return this;
    }

    @Override
    public ObjectCheck<T> notEmpty() {
      notEmpty(arg, argName);
      return this;
    }

    @Override
    public ObjectCheck<T> noneEmpty() {
      noneEmpty(arg, argName);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> U value() {
      return (U) arg;
    }

  }

  private static class StringCheck extends ObjectCheck<String> {

    private StringCheck(String arg, String argName) {
      super(arg, argName);
    }

    @Override
    public StringCheck notBlank() {
      notBlank(arg, argName);
      return this;
    }
  }

  private static class IntCheck extends Check {

    final int arg;

    private IntCheck(int arg, String argName) {
      super(argName);
      this.arg = arg;
    }

    @Override
    public Check satisfies(IntPredicate test) {
      integer(arg, test, argName, " %s does not satisfy %s", argName, test);
      return this;
    }

    @Override
    public IntCheck gt(int min) {
      gt(arg, min, argName);
      return this;
    }

    @Override
    public IntCheck gte(int min) {
      gte(arg, min, argName);
      return this;
    }

    @Override
    public IntCheck lt(int max) {
      lt(arg, max, argName);
      return this;
    }

    @Override
    public IntCheck lte(int max) {
      lte(arg, max, argName);
      return this;
    }

    @Override
    public Check between(int minInclusive, int maxExclusive) {
      between(arg, minInclusive, maxExclusive, argName);
      return this;
    }

    @Override
    public Check inRange(int minInclusive, int maxInclusive) {
      inRange(arg, minInclusive, maxInclusive, argName);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> U value() {
      return (U) Integer.valueOf(arg);
    }

    @Override
    public int intValue() {
      return arg;
    }

  }

  private static class IntegerCheck extends ObjectCheck<Integer> {
    private IntegerCheck(Integer arg, String argName) {
      super(arg.intValue(), argName);
    }

    @Override
    public Check satisfies(IntPredicate test) {
      integer(arg.intValue(), test, argName, " %s does not satisfy %s", argName, test);
      return this;
    }

    @Override
    public IntegerCheck gt(int max) {
      gt(arg.intValue(), max, argName);
      return this;
    }

    @Override
    public IntegerCheck gte(int max) {
      gte(arg.intValue(), max, argName);
      return this;
    }

    @Override
    public IntegerCheck lt(int max) {
      lt(arg.intValue(), max, argName);
      return this;
    }

    @Override
    public IntegerCheck lte(int max) {
      lte(arg.intValue(), max, argName);
      return this;
    }

    @Override
    public Check between(int minInclusive, int maxExclusive) {
      between(arg.intValue(), minInclusive, maxExclusive, argName);
      return this;
    }

    @Override
    public Check inRange(int minInclusive, int maxInclusive) {
      inRange(arg.intValue(), minInclusive, maxInclusive, argName);
      return this;
    }

    @Override
    public int intValue() {
      return arg.intValue();
    }
  }

}
