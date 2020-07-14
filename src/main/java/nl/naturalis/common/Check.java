package nl.naturalis.common;

import java.util.Arrays;
import java.util.Collection;
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

  public static Check that(String arg, String argName) {
    return new StringCheck(arg, argName);
  }

  public static Check that(int arg, String argName) {
    return new IntCheck(arg, argName);
  }

  public static Check that(Integer arg, String argName) {
    return new IntegerCheck(arg, argName);
  }

  public static <T> Check that(T arg, String argName) {
    return new ObjectCheck<>(arg, argName);
  }

  /**
   * Generic check method. Can also be used for other purposes than checking preconditions. Throws the exception supplied by the provided
   * supplier if the provided condition evaluates to false.
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
   * Generic check method. Can also be used for other purposes than checking preconditions. Throws the exception supplied by the provided
   * exception supplier if the provided condition evaluates to false, else returns {@code result}.
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
   * Generic check method. Can also be used for other purposes than checking preconditions. Throws the exception supplied by the provided
   * exception supplier if the provided condition evaluates to false, else returns the result supplied by the result supplier.
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
   * Does nothing if the provided condition evaluates to {@code true}, else throws an {@link IllegalArgumentException} with the provided
   * message.
   * 
   * @param condition The condition to be evaluated
   * @param message The exception message
   * @throws IllegalArgumentException If the condition evaluates to false
   */
  public static void argument(boolean condition, String message) {
    that(condition, () -> badArgument(message));
  }

  /**
   * Returns {@code arg} if it passes the provided {@code test}, else throws an {@link IllegalArgumentException} with the provided message.
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
   * Returns {@code arg} if it passes the provided {@code test}, else throws an {@link IllegalArgumentException} with the provided message.
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
   * Does nothing if the provided condition evaluates to {@code true}, else throws an {@link IllegalArgumentException} with the provided
   * message and message arguments.
   * 
   * @param condition The condition to be evaluated
   * @param message The exception message
   * @param msgArg The 1st {@code String.format} message argument
   * @param moreMsgArgs The remaining {@code String.format} message arguments
   * @throws IllegalArgumentException If the condition evaluates to false
   */
  public static void argument(boolean condition, String message, Object msgArg, Object... moreMsgArgs) {
    that(condition, () -> badArgument(message, withArguments(msgArg, moreMsgArgs)));
  }

  /**
   * Returns {@code arg} if it passes the provided {@code test}, else throws an {@link IllegalArgumentException} with the provided message
   * and message arguments. {@code msgArg} is allowed to be null, in which case it is ignored as a message argument.
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
    return that(test.test(arg), arg, () -> badArgument(message, withArguments(msgArg, moreMsgArgs)));
  }

  /**
   * Returns {@code arg} if it passes the provided {@code test}, else throws an {@link IllegalArgumentException} with the provided message
   * and message arguments. {@code msgArg} is allowed to be null, in which case it is ignored as a message argument.
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
    throw badArgument(message, withArguments(msgArg, moreMsgArgs));
  }

  /**
   * Returns {@code arg} if it is not null, else throws an {@link IllegalArgumentException} with the message:
   * <code>Illegal argument: null</code>.
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
   * Returns {@code arg} if it is not null, else throws an {@link IllegalArgumentException} with the message:
   * <code>&lt;argName&gt; must not be null</code>. In other words, the 2nd argument is not supposed to be a complete message but just the
   * name of the argument being tested. For example:
   * 
   * <pre>
   * public void accelerate(Engine engine) {
   *   Check.notNull(engine, "engine"); // engine must not be null
   * }
   * </pre>
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
   * Returns {@code arg} if it is not null, else throws an {@link IllegalArgumentException} with the provided message and message arguments.
   * {@code msgArg} is allowed to be null, in which case it is ignored as a message argument.
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
   * Returns the provided array if it is not null and none of its elements are null, else throws an {@link IllegalArgumentException}.
   * 
   * @param <T>
   * @param array
   * @param argName
   * @return
   */
  public static <T> T[] noneNull(T[] array, String argName) {
    Arrays.stream(notNull(array, argName)).forEach(e -> notNull(e, "%s must not contain null values", argName));
    return array;
  }

  /**
   * Returns the provided collection if it is not null and none of its elements are null, else throws an {@link IllegalArgumentException}.
   * 
   * @param <E>
   * @param <T>
   * @param collection
   * @param argName
   * @return
   */
  public static <E, T extends Collection<E>> T noneNull(T collection, String argName) {
    notNull(collection, argName).stream().forEach(e -> notNull(e, "%s must not contain null values", argName));
    return collection;
  }

  /**
   * Returns {@code arg} if it is not empty, else throws an {@link IllegalArgumentException} with the message:
   * <code>Illegal argument: empty string</code>.
   * 
   * @param arg The argument being tested
   * @return The argument
   * @throws IllegalArgumentException If the argument is empty
   */
  public static String notEmpty(String arg) {
    return argument(arg, StringMethods::notEmpty, "Illegal argument: empty string");
  }

  /**
   * Returns {@code arg} if it is not empty, else throws an {@link IllegalArgumentException} with the message:
   * <code>&lt;argName&gt; must not be empty</code>. In other words, the 2nd argument is not supposed to be a complete message but just the
   * name of the argument being tested.
   * 
   * @param arg The argument being tested
   * @param argName The name of the argument being tested
   * @return The argument
   * @throws IllegalArgumentException If the argument is empty
   */
  public static String notEmpty(String arg, String argName) {
    return argument(arg, StringMethods::notEmpty, "%s must not be empty", argName);
  }

  /**
   * Returns {@code arg} if it is not empty, else throws an {@link IllegalArgumentException} with the provided message and message
   * arguments. {@code msgArg} is allowed to be null, in which case it is ignored as a message argument.
   * 
   * @param arg The argument being tested
   * @param message The exception message
   * @param msgArg The 1st {@code String.format} message argument
   * @param moreMsgArgs The remaining {@code String.format} message arguments
   * @return The argument
   * @throws IllegalArgumentException If the argument is empty
   */
  public static String notEmpty(String arg, String message, Object msgArg, Object... moreMsgArgs) {
    return argument(arg, StringMethods::notEmpty, message, msgArg, moreMsgArgs);
  }

  /**
   * Returns {@code arg} if it is not blank, else throws an {@link IllegalArgumentException} with the message:
   * <code>Illegal argument: blank string</code>.
   * 
   * @param arg The argument being tested
   * @return The argument
   * @throws IllegalArgumentException If the argument is blank
   */
  public static String notBlank(String arg) {
    return argument(arg, StringMethods::notBlank, "Illegal argument: blank string");
  }

  /**
   * Returns {@code arg} if it is not blank, else throws an {@link IllegalArgumentException} with the message:
   * <code>&lt;argName&gt; must not be blank</code>. In other words, the 2nd argument is not supposed to be a complete message but just the
   * name of the argument being tested.
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
   * Returns {@code arg} if it is not blank, else throws an {@link IllegalArgumentException} with the provided message and message
   * arguments. {@code msgArg} is allowed to be null, in which case it is ignored as a message argument.
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
   * Returns {@code arg} if it is greater than <code>min</code>, else throws an {@link IllegalArgumentException}.
   * 
   * @param arg
   * @param min
   * @param argName
   * @return
   */
  public static int greaterThan(int arg, int min, String argName) {
    return integer(arg, x -> x > min, "%s must be greater than %d", argName, min);
  }

  /**
   * Returns {@code arg} if it is greater than or equal to <code>min</code>, else throws an {@link IllegalArgumentException}.
   * 
   * @param arg
   * @param min
   * @param argName
   * @return
   */
  public static int notLessThan(int arg, int min, String argName) {
    return integer(arg, x -> x >= min, "%s must be greater than or equal to %d", argName, min);
  }

  /**
   * Returns {@code arg} if it is less than <code>max</code>, else throws an {@link IllegalArgumentException}.
   * 
   * @param arg
   * @param max
   * @param argName
   * @return
   */
  public static int lessThan(int arg, int max, String argName) {
    return integer(arg, x -> x < max, "%s must be less than %d", argName, max);
  }

  /**
   * Returns {@code arg} is less than or equal to <code>max</code>, else throws an {@link IllegalArgumentException}.
   * 
   * @param arg
   * @param max
   * @param argName
   * @return
   */
  public static int notGreaterThan(int arg, int max, String argName) {
    return integer(arg, x -> x <= max, "%s must be less than or equal to %d", argName, max);
  }

  /**
   * Does nothing if the provided condition evaluates to {@code true}, else throws an {@link IllegalStateException} with the provided
   * message.
   * 
   * @param condition The condition to be evaluated
   * @param message The exception message
   * @throws IllegalStateException If the condition evaluates to {@code false}
   */
  public static void state(boolean condition, String message) {
    that(condition, () -> badState(message));
  }

  /**
   * Does nothing if the provided condition evaluates to {@code true}, else throws an {@link IllegalStateException} with the provided
   * message and message arguments. {@code msgArg} is allowed to be null, in which case it is ignored as a message argument.
   * 
   * @param condition
   * @param message The exception message
   * @param msgArg The 1st {@code String.format} message argument
   * @param moreMsgArgs The remaining {@code String.format} message arguments
   * @throws IllegalStateException If the condition evaluates to {@code false}
   */
  public static void state(boolean condition, String message, Object msgArg, Object... moreMsgArgs) {
    that(condition, () -> badState(message, withArguments(msgArg, moreMsgArgs)));
  }

  private static IllegalArgumentException badArgument(String msg, Object... msgArgs) {
    return new IllegalArgumentException(String.format(msg, msgArgs));
  }

  private static IllegalStateException badState(String msg, Object... msgArgs) {
    return new IllegalStateException(String.format(msg, msgArgs));
  }

  private static Object[] withArguments(Object msgArg, Object[] moreMsgArgs) {
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

  public Check notNull() {
    throw notApplicable("notNull");
  }

  public Check notEmpty() {
    throw notApplicable("notEmpty");
  }

  public Check notBlank() {
    throw notApplicable("notBlank");
  }

  /**
   * @param minValue
   */
  public Check greaterThan(int minValue) {
    throw notApplicable("greaterThan");
  }

  /**
   * @param maxValue
   */
  public Check lessThan(int maxValue) {
    throw notApplicable("lessThan");
  }

  /**
   * @param maxValue
   */
  public Check notGreaterThan(int maxValue) {
    throw notApplicable("notGreaterThan");
  }

  /**
   * @param minValue
   */
  public Check notLessThan(int minValue) {
    throw notApplicable("notLessThan");
  }

  public Check positive() {
    return greaterThan(0);
  }

  public Check negative() {
    return lessThan(0);
  }

  public Check notPositive() {
    return notGreaterThan(0);
  }

  public Check notNegative() {
    return notLessThan(0);
  }

  public <U> U value() {
    return null;
  }

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

    public ObjectCheck<T> notNull() {
      notNull(arg, argName);
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
    public StringCheck notEmpty() {
      notEmpty(arg, argName);
      return this;
    }

    @Override
    public StringCheck notBlank() {
      notBlank(arg, argName);
      return this;
    }
  }

  public static class IntCheck extends Check {

    final int arg;

    private IntCheck(int arg, String argName) {
      super(argName);
      this.arg = arg;
    }

    @Override
    public IntCheck greaterThan(int max) {
      greaterThan(arg, max, argName);
      return this;
    }

    @Override
    public IntCheck lessThan(int max) {
      lessThan(arg, max, argName);
      return this;
    }

    @Override
    public IntCheck notGreaterThan(int max) {
      notGreaterThan(arg, max, argName);
      return this;
    }

    @Override
    public IntCheck notLessThan(int max) {
      notLessThan(arg, max, argName);
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
    public IntegerCheck greaterThan(int max) {
      notLessThan(arg.intValue(), max, argName);
      return this;
    }

    @Override
    public IntegerCheck lessThan(int max) {
      lessThan(arg.intValue(), max, argName);
      return this;
    }

    @Override
    public IntegerCheck notLessThan(int max) {
      notLessThan(arg.intValue(), max, argName);
      return this;
    }

    @Override
    public IntegerCheck notGreaterThan(int max) {
      notGreaterThan(arg.intValue(), max, argName);
      return this;
    }

    @Override
    public int intValue() {
      return arg.intValue();
    }
  }

}
