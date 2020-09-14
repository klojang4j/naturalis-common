package nl.naturalis.common.check;

import java.util.function.*;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.Relation;

/**
 * Facilitates checking preconditions and array indices. One {@code Check} object can be used to
 * check multiple preconditions for the same argument, and for multiple properties of the argument.
 * For example:
 *
 * <p>
 *
 * <pre>
 * int i = Check.that(numChairs, "numChairs", greaterThan(), 2).and(lessThan(), 10).intValue();
 * int i = Check.that(numChairs, "numChairs", gt(), 2).and(lt(), 10).intValue();
 * </pre>
 *
 * <h3>Exception messages</h3>
 *
 * <p>Some static methods only take the argument to be tested and the <i>name</i> of the argument,
 * not a complete error message. For example:
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
 * <p>(If the first message argument and null or empty, it and ignored.)
 *
 * <h3>Checking properties and changing the Exception type</h3>
 *
 * <p>When using the instance methods you can not just check the argument itself, but also its
 * properties. In addition, the instance methods also allow you the change the type of {@code
 * Exception} being thrown (defaults to {@link IllegalArgumentException}). For example:
 *
 * <p>
 *
 * <pre>
 * this.query = Check.that(query, "query", InvalidQueryException::new)
 *  .notNull()
 *  .and(QuerySpec::getFrom, x -> nvl(x) == 0, "from must be null or zero")
 *  .and(QuerySpec::getSize, GTE, MIN_BATCH_SIZE, "size must be >= %d", MIN_BATCH_SIZE)
 *  .and(QuerySpec::getSize, LTE, MAX_BATCH_SIZE, "size must be <= %d", MAX_BATCH_SIZE)
 *  .and(QuerySpec::getSortFields, CollectionMethods::isEmpty, "sortFields must be empty")
 *  .value();
 * </pre>
 *
 * <p>(See the {@link #and(Function, Relation, Object, String, Object...) and methods} and the
 * {@link Relation} and {@link IntRelation} interfaces.)
 *
 * @author Ayco Holleman
 * @param <T> The type of the object being checked
 * @param <E> The type of exception thrown if a test fails
 */
public abstract class Check<T, E extends Exception> {

  /**
   * Returns a new {@code Check} object suitable for testing the provided argument. The argument
   * will have already passed the {@link Checks#notNull() notNull} test.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @param argName The name of the argument
   * @return A new {@code Check} object
   * @throws IllegalArgumentException If the argument fails to pass the {@code notNull} test or any
   *     subsequent tests called on the returned {@code Check} object
   */
  public static <U> Check<U, IllegalArgumentException> notNull(U arg, String argName)
      throws IllegalArgumentException {
    return that(arg, argName, Checks.notNull(), IllegalArgumentException::new);
  }

  /**
   * Returns a new {@code Check} object suitable for testing the provided argument. The argument
   * will have already passed the {@link Checks#notNull() notNull} test.
   *
   * @param <U> The type of the argument
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass the {@code
   *     notNull} test, or any subsequent tests executed on the returned {@code Check} object
   * @param arg The argument
   * @param argName The name of the argument
   * @param excFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} object
   * @throws F If the argument fails to pass the {2code notNull} test or any subsequent tests called
   *     on the returned {@code Check} object
   */
  public static <U, F extends Exception> Check<U, F> notNull(
      U arg, String argName, Function<String, F> excFactory) throws F {
    return that(arg, argName, Checks.notNull(), excFactory);
  }

  /**
   * Returns a new {@code Check} object suitable for testing integers if the argument passes the
   * specified (first) test, else throws an {@code IllegalArgumentException}.
   *
   * @param arg The argument
   * @param argName The name of the argument
   * @param test The test
   * @return A new {@code Check} object
   * @throws IllegalArgumentException If the argument fails to pass the specified test or any
   *     subsequent tests called on the returned {@code Check} object
   */
  public static Check<Integer, IllegalArgumentException> that(
      int arg, String argName, IntPredicate test) throws IllegalArgumentException {
    return that(arg, argName, test, IllegalArgumentException::new);
  }

  /**
   * Returns a new {@code Check} object suitable for testing integers if the argument passes the
   * specified (first) test, else throws the {@code Exception} produced by the specified {@code
   * Exception} factory.
   *
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass the specified
   *     test, or any subsequent tests executed on the returned {@code Check} object
   * @param arg The argument
   * @param argName The name of the argument
   * @param test The test
   * @param excFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} object
   * @throws F If the argument fails to pass the specified test or any subsequent tests called on
   *     the returned {@code Check} object
   */
  public static <F extends Exception> Check<Integer, F> that(
      int arg, String argName, IntPredicate test, Function<String, F> excFactory) throws F {
    return new IntCheck<>(arg, argName, excFactory).and(test);
  }

  /**
   * Returns a new {@code Check} object suitable for testing the provided argument if the argument
   * passes the specified (first) test, else throws an {@code IllegalArgumentException}.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @param argName The name of the argument
   * @param test The test
   * @param excFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} object
   * @throws IllegalArgumentException If the argument fails to pass the specified test or any
   *     subsequent tests called on the returned {@code Check} object
   */
  public static <U> Check<U, IllegalArgumentException> that(
      U arg, String argName, Predicate<U> test) throws IllegalArgumentException {
    return that(arg, argName, test, IllegalArgumentException::new);
  }

  /**
   * Returns a new {@code Check} object suitable for testing the provided argument if the argument
   * passes the specified (first) test, else throws the {@code Exception} produced by the specified
   * {@code Exception} factory.
   *
   * @param <U> The type of the argument
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass the specified
   *     test, or any subsequent tests executed on the returned {@code Check} object
   * @param arg The argument
   * @param argName The name of the argument
   * @param test The first test to submit the argument to
   * @param excFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} object
   * @throws F If the argument fails to pass the specified test or any subsequent tests called on
   *     the returned {@code Check} object
   */
  public static <U, F extends Exception> Check<U, F> that(
      U arg, String argName, Predicate<U> test, Function<String, F> excFactory) throws F {
    return new ObjectCheck<>(arg, argName, excFactory).and(test);
  }

  /**
   * Returns a new {@code Check} object suitable for testing {@code int} arguments if it passes an
   * the specified test, else throws an {@code IllegalArgumentException}.
   *
   * @param arg The argument
   * @param argName The argument name
   * @param test
   * @param target
   * @return
   * @throws IllegalArgumentException If the argument fails to pass the specified test or any
   *     subsequent tests called on the returned {@code Check} object
   */
  public static Check<Integer, IllegalArgumentException> that(
      int arg, String argName, IntRelation test, int target) throws IllegalArgumentException {
    return new IntCheck<>(arg, argName, IllegalArgumentException::new).and(test, target);
  }

  /**
   * Returns a new {@code Check} object suitable for testing integers if the argument passes the
   * specified (first) test, else throws an {@code IllegalArgumentException}.
   *
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass the specified
   *     test, or any subsequent tests executed on the returned {@code Check} object
   * @param arg The argument
   * @param argName The argument name
   * @param test The relation to verify between the argument and the specified integer ({@code
   *     target})
   * @param target The integer at the other end of the relationship
   * @param excFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} instance
   * @throws F If the argument fails to pass the specified test or any subsequent tests called on
   *     the returned {@code Check} object
   */
  public static <F extends Exception> Check<Integer, F> that(
      int arg, String argName, IntRelation test, int target, Function<String, F> excFactory)
      throws F {
    return new IntCheck<>(arg, argName, excFactory).and(test, target);
  }

  /**
   * Returns a new {@code Check} object suitable for testing the provided argument if it passes an
   * initial test, else throws an {@code IllegalArgumentException}.
   *
   * @param <U> The type of the argument
   * @param <V> The type of the object at the other end of the specified {@code Relation}
   * @param arg The argument
   * @param argName The name of the argument
   * @param test The relation to verify between the argument and the specified object ({@code
   *     target})
   * @param target The integer at the other end of the relationship
   * @return A new {@code Check} object
   * @throws IllegalArgumentException If the argument fails to pass the specified test or any
   *     subsequent tests called on the returned {@code Check} object
   */
  public static <U, V> Check<U, IllegalArgumentException> that(
      U arg, String argName, Relation<U, V> test, V target) throws IllegalArgumentException {
    return that(arg, argName, test, target, IllegalArgumentException::new);
  }

  /**
   * Returns a new {@code Check} object suitable for testing the provided argument if the argument
   * passes the specified (first) test, else throws the {@code Exception} produced by the specified
   * {@code Exception} factory.
   *
   * @param <U> The type of the argument
   * @param <V> The type of the object at the other end of the specified {@code Relation}
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass the specified
   *     test, or any subsequent tests executed on the returned {@code Check} object
   * @param arg The argument
   * @param argName The name of the argument
   * @param test The test
   * @param excFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} object
   * @throws F If the argument fails to pass the specified test or any subsequent tests called on
   *     the returned {@code Check} object
   */
  public static <U, V, F extends Exception> Check<U, F> that(
      U arg, String argName, Relation<U, V> test, V target, Function<String, F> excFactory)
      throws F {
    return new ObjectCheck<>(arg, argName, excFactory).and(test, target);
  }

  /**
   * Generic check method. Throws the exception supplied by the provided supplier if the provided
   * condition evaluates to false, else does nothing.
   *
   * @param <F> The type of exception thrown if {@code condition} evaluates to false
   * @param condition The condition to evaluate
   * @param excSupplier The exception supplier
   * @throws F The exception thrown if the condition evaluates to false
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
   * Verifies that the argument and a valid array index. Throws an {@code
   * ArrayIndexOutOfBoundsException} if {@code arg} and less than zero or greater than or equal to
   * {@code maxExclusive}, else returns {@code arg}. This and especially useful to test "from"
   * arguments, which generally should be <i>less than</i> the length or size of the object operated
   * upon.
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
   * Verifies that the argument and a valid array index. Throws an {@code
   * ArrayIndexOutOfBoundsException} if {@code arg} and less than {@code min} or greater than {@code
   * max}, else returns {@code arg}. This and especially useful to test "to" or "until" arguments,
   * which generally should be <i>less than or equal to</i> the length or size of the object
   * operated upon.
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
   * Does nothing if the provided condition evaluates to {@code true}, else throws an {@link
   * IllegalStateException}.
   *
   * @param condition The condition to evaluate
   * @param message The exception message
   * @param msgArgs The {@code String.format} message arguments
   * @throws IllegalStateException If the condition evaluates to {@code false}
   */
  public static void state(boolean condition, String message, Object... msgArgs)
      throws IllegalStateException {
    that(condition, () -> new IllegalStateException(String.format(message, msgArgs)));
  }

  /**
   * Utility method returning an {@code IllegalArgumentException Supplier} using the provided
   * message and message arguments to create an exception message. Can be used in various cases as a
   * static import to avoid bloated code:
   *
   * <p>
   *
   * <pre>
   * Check.that(foo.equals(bar), ()-> new IllegalArgumentException(String.format("%s must equal %s", foo, bar)));
   *    // versus:
   * Check.that(foo.equals(bar), badArgument("%s must equal %s", foo, bar));
   *
   * </pre>
   *
   * @param message The exception message
   * @param msgArgs The {@code String.format} message arguments
   * @return An {@code IllegalArgumentException Supplier}
   */
  public static Supplier<IllegalArgumentException> badArgument(String message, Object... msgArgs) {
    return () -> new IllegalArgumentException(String.format(message, msgArgs));
  }

  private static ArrayIndexOutOfBoundsException badIndex(int arg, String argName) {
    String fmt = "Index variable \"%s\" out of range: %d";
    return new ArrayIndexOutOfBoundsException(String.format(fmt, argName, arg));
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////
  // Instance fields / methods start here
  //////////////////////////////////////////////////////////////////////////////////////////////////////

  final String argName;
  final Function<String, E> excFactory;

  Check(String argName, Function<String, E> excFactory) {
    this.argName = argName;
    this.excFactory = excFactory;
  }

  /**
   * Returns the name of the argument being tested.
   *
   * @return The name of the argument being tested
   */
  public String argName() {
    return argName;
  }

  /**
   * Submits the argument to the specified test. This method is especially useful when using the
   * (statically imported) tests in thw {@link Checks} class, as they have predefined, informative
   * error messages associated with them.
   *
   * @param test The test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> and(Predicate<T> test) throws E {
    throw notApplicable();
  }

  /**
   * Submits the argument to the specified test. Allows you to provide a custom error message.
   *
   * @param test The test
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> and(Predicate<T> test, String message, Object... msgArgs) throws E {
    throw notApplicable();
  }

  /**
   * Submits the argument to the specified test. This method is especially useful when using the
   * (statically imported) tests in thw {@link Checks} class, as they have predefined, informative
   * error messages associated with them.
   *
   * @param test The test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> and(IntPredicate test) throws E {
    throw notApplicable();
  }

  /**
   * Submits the argument to the specified test. Allows you to provide a custom error message.
   *
   * @param test The test
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> and(IntPredicate test, String message, Object... msgArgs) throws E {
    throw notApplicable();
  }

  /**
   * Submits the argument to the specified test. This method is especially useful when using the
   * (statically imported) tests in thw {@link Checks} class, as they have predefined, informative
   * error messages associated with them.
   *
   * @param <U> The type of the object at the other end of the specified {@code Relation}
   * @param test The relation to verify between the argument and the specified object ({@code
   *     target})
   * @param target The object at the other end of the specified {@code Relation}
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> and(Relation<T, U> test, U target) throws E {
    throw notApplicable();
  }

  /**
   * Submits the argument to the specified test. Allows you to provide a custom error message.
   *
   * @param <U> The type of the object at the other end of the specified {@code Relation}
   * @param test The relation to verify between the argument and the specified object ({@code
   *     target})
   * @param target The object at the other end of the specified {@code Relation}
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> and(Relation<T, U> test, U target, String message, Object... msgArgs)
      throws E {
    throw notApplicable();
  }

  /**
   * Submits the argument to the specified test. This method is especially useful when using the
   * (statically imported) tests in thw {@link Checks} class, as they have predefined, informative
   * error messages associated with them.
   *
   * @param test The relation to verify between the argument and the specified integer ({@code
   *     target})
   * @param target The integer at the other end of the specified {@code Relation}
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> and(IntRelation test, int target) throws E {
    throw notApplicable();
  }

  /**
   * Submits the argument to the specified test. Allows you to provide a custom error message.
   *
   * @param test The relation to verify between the argument and the specified integer ({@code
   *     target})
   * @param target The integer at the other end of the specified {@code Relation}
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> and(IntRelation test, int target, String message, Object... msgArgs) throws E {
    throw notApplicable();
  }

  /**
   * Submits a property of the argument to the specified test.
   *
   * @param <U> The type of the property
   * @param getter A {@code Function} with the argument as input and the value to be tested as
   *     output. Usually a getter like {@code Employee::getName}.
   * @param test The test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> and(Function<T, U> getter, Predicate<U> test) throws E {
    throw notApplicable();
  }

  /**
   * Submits a property of the argument to the specified test.
   *
   * @param <U> The type of the property
   * @param getter A {@code Function} with the argument as input and the value to be tested as
   *     output. Usually a getter like {@code Employee::getName}.
   * @param test The test
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> and(
      Function<T, U> getter, Predicate<U> test, String message, Object... msgArgs) throws E {
    throw notApplicable();
  }

  /**
   * Submits an integer property of the argument to the specified test.
   *
   * @param getter A {@code Function} with the argument as input and the value to be tested as
   *     output. Usually a getter like {@code Employee::getAge}.
   * @param test The test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> and(ToIntFunction<T> getter, IntPredicate test) throws E {
    throw notApplicable();
  }

  /**
   * Submits an integer property of the argument to the specified test.
   *
   * @param getter A {@code Function} with the argument as input and the value to be tested as
   *     output. Usually a getter like {@code Employee::getAge}.
   * @param test The test
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> and(
      ToIntFunction<T> getter, IntPredicate test, String message, Object... msgArgs) throws E {
    throw notApplicable();
  }

  /**
   * Submits a property of the argument to the specified test.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object at the other end of the specified {@code Relation}
   * @param getter A {@code Function} with the argument as input and the value to be tested as
   *     output. Usually a getter like {@code Employee::getName}.
   * @param test The relation to verify between the argument and the specified object ({@code
   *     target})
   * @param target The object at the other end of the specified {@code Relation}
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U, V> Check<T, E> and(Function<T, U> getter, Relation<U, V> test, V target) throws E {
    throw notApplicable();
  }

  /**
   * Submits a property of the argument to the specified test.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object at the other end of the specified {@code Relation}
   * @param getter A {@code Function} with the argument as input and the value to be tested as
   *     output. Usually a getter like {@code Employee::getName}.
   * @param test The relation to verify between the argument and the specified object ({@code
   *     target})
   * @param target The object at the other end of the specified {@code Relation}
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U, V> Check<T, E> and(
      Function<T, U> getter, Relation<U, V> test, V target, String message, Object... msgArgs)
      throws E {
    throw notApplicable();
  }

  /**
   * Submits a property of the argument to the specified test.
   *
   * @param getter A {@code Function} with the argument as input and the value to be tested as
   *     output. Usually a getter like {@code Employee::getName}.
   * @param test The relation to verify between the argument and the specified integer ({@code
   *     target})
   * @param target The integer at the other end of the specified {@code Relation}
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> and(ToIntFunction<T> getter, IntRelation test, int target) throws E {
    throw notApplicable();
  }

  /**
   * Submits a property of the argument to the specified test.
   *
   * @param getter A {@code Function} with the argument as input and the value to be tested as
   *     output. Usually a getter like {@code Employee::getName}.
   * @param test The relation to verify between the argument and the specified integer ({@code
   *     target})
   * @param target The integer at the other end of the specified {@code Relation}
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> and(
      ToIntFunction<T> getter, IntRelation test, int target, String message, Object... msgArgs)
      throws E {
    throw notApplicable();
  }

  /**
   * Returns the argument being tested. To be used as the last call after a chain of checks. For
   * example:
   *
   * <pre>
   * Integer i = Check.that(counter, "counter").notNull().ok();
   * </pre>
   *
   * @return The argument
   */
  public abstract T ok();

  /**
   * Returns the argument being tested as an {@code int}. If the argument being tested actually and
   * an {@code int} rather than an {@code Integer}, this method saves the cost of unboxing incurred
   * by {@link #ok()}. If the argument neither and {@code int} nor an {@code Integer} this method
   * throws a {@code ClassCastException}.
   *
   * @return The argument cast or converted to an {@code int}
   */
  public int intValue() {
    String fmt = "Argument %s (%s) cannot be cast to int";
    String msg = String.format(fmt, getClass().getName(), argName);
    throw new ClassCastException(msg);
  }

  protected UnsupportedOperationException notApplicable() {
    String fmt = "Cannot apply %s to %s";
    String msg = String.format(fmt, getClass().getSimpleName(), argName);
    return new UnsupportedOperationException(msg);
  }
}
