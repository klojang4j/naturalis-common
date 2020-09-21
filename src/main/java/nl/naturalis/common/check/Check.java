package nl.naturalis.common.check;

import java.util.function.*;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;

/**
 * Facilitates the validation of object state, arguments and array indices. Validating object state
 * and array indices happens through static methods. Validation of arguments happens by means of an
 * actual instance of the {@code Check} class. You obtain an instance through one of the static
 * factory methods. For example:
 *
 * <p>
 *
 * <pre>
 * this.numChairs = Check.that(numChairs, "numChairs", atLeast(), 2).and(atMost(), 10).and(isEven()).ok();
 * </pre>
 *
 * <h3>Standard checks</h3>
 *
 * <p>The {@link Checks} class contains a number of common checks for arguments. These are already
 * associated with short, informative error messages, so you don't have to invent them yourself. For
 * example:
 *
 * <p>
 *
 * <pre>
 * Check.that(numChairs, "numChairs", atLeast(), 2);
 * // -> "numChairs must be >= 2 (was 0)"
 * </pre>
 *
 * <h3>Null checks</h3>
 *
 * <p>Some tests in the {@link Checks} class contain custom code, intended to be used in combination
 * with the {@code Check} class. These tests do not perform preliminary null checks. They rely on
 * being embedded in a chain of checks on a {@code Check} object. Therefore, unless it is clear that
 * the argument cannot possibly be null, the first check in a chain of checks should always be the
 * {@link Checks#notNull() notNull()} check. There are two static factory methods that have this
 * check baked into them. For example:
 *
 * <p>
 *
 * <pre>
 * Check.notNull(name, "name").and(String::startsWith, "John");
 * </pre>
 *
 * <p>(NB Some tests in the {@code Checks} class implicitly do a null check, like {@link
 * Checks#notEmpty() Checks.notEmpty()} and {@link Checks#noneNull() Checks.noneNull()}. These can
 * therefore also be used as the first check.)
 *
 * <h3>Checking argument properties</h3>
 *
 * <p>A {@code Check} object lets you check not just arguments but also argument properties. For
 * example:
 *
 * <p>
 *
 * <pre>
 * Check.notNull(employee, "employee").and(Employee::getAge, "age", lessThan(), 50);
 * Check.notNull(intArray, "intArray").and(Array::getLength, "length", isEven());
 * Check.notNull(employees, "employees").and(Collection::size, "size", atLeast(), 100);
 * </pre>
 *
 * <h3>Changing the Exception type</h3>
 *
 * <p>By default an {@code IllegalArgumentException} is thrown if an argument fails to pass a test.
 * This can be customized through the static factory methods. For example:
 *
 * <p>
 *
 * <pre>
 * this.query = Check.argument(query, "query", InvalidQueryException::new)
 *  .and(QuerySpec::getFrom, x -> nvl(x) == 0, "from must be null or zero")
 *  .and(QuerySpec::getSize, "size", atLeast(), MIN_BATCH_SIZE)
 *  .and(QuerySpec::getSize, "size", atMost(), MAX_BATCH_SIZE)
 *  .and(QuerySpec::getSortFields, "sortFields", isEmpty())
 *  .ok();
 * </pre>
 *
 * @author Ayco Holleman
 * @param <T> The type of the object being checked
 * @param <E> The type of exception thrown if a test fails
 */
public abstract class Check<T, E extends Exception> {

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing integers.
   *
   * @param arg The argument
   * @param argName The name of the argument
   * @return A new {@code Check} object
   */
  public static Check<Integer, IllegalArgumentException> argument(int arg, String argName) {
    return new IntCheck<>(arg, argName, IllegalArgumentException::new);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing integers.
   *
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass the {@code
   *     notNull} test, or any subsequent tests executed on the returned {@code Check} object
   * @param arg The argument
   * @param argName The name of the argument
   * @param excFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} object
   */
  public static <F extends Exception> Check<Integer, F> argument(
      int arg, String argName, Function<String, F> excFactory) {
    return new IntCheck<>(arg, argName, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @param argName The name of the argument
   * @return A new {@code Check} object
   */
  public static <U> Check<U, IllegalArgumentException> argument(U arg, String argName) {
    return new ObjectCheck<>(arg, argName, IllegalArgumentException::new);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument.
   *
   * @param <U> The type of the argument
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass a test
   * @param arg The argument
   * @param argName The name of the argument
   * @param excFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} object
   */
  public static <U, F extends Exception> Check<U, F> argument(
      U arg, String argName, Function<String, F> excFactory) {
    return new ObjectCheck<>(arg, argName, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument. The argument will have already passed the {@link Checks#notNull() notNull} test.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @param argName The name of the argument
   * @return A new {@code Check} object
   */
  public static <U> Check<U, IllegalArgumentException> notNull(U arg, String argName)
      throws IllegalArgumentException {
    return that(arg, argName, Checks.notNull(), IllegalArgumentException::new);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument. The argument will have already passed the {@link Checks#notNull() notNull} test.
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
   * Static factory method. Returns a new {@code Check} object suitable for testing integers if the
   * argument passes the specified (first) test, else throws an {@code IllegalArgumentException}.
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
   * Static factory method. Returns a new {@code Check} object suitable for testing integers if the
   * argument passes the specified (first) test, else throws the {@code Exception} produced by the
   * specified {@code Exception} factory.
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
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument if the argument passes the specified (first) test, else throws an {@code
   * IllegalArgumentException}.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @param argName The name of the argument
   * @param test The test
   * @return A new {@code Check} object
   * @throws IllegalArgumentException If the argument fails to pass the specified test or any
   *     subsequent tests called on the returned {@code Check} object
   */
  public static <U> Check<U, IllegalArgumentException> that(
      U arg, String argName, Predicate<U> test) throws IllegalArgumentException {
    return that(arg, argName, test, IllegalArgumentException::new);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument if the argument passes the specified (first) test, else throws the {@code Exception}
   * produced by the specified {@code Exception} factory.
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
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument if it passes an initial test, else throws an {@code IllegalArgumentException}.
   *
   * @param <U> The type of the argument
   * @param <V> The type of the object of the relationship
   * @param arg The argument
   * @param argName The name of the argument
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @return A new {@code Check} object
   * @throws IllegalArgumentException If the argument fails to pass the specified test or any
   *     subsequent tests called on the returned {@code Check} object
   */
  public static <U, V> Check<U, IllegalArgumentException> that(
      U arg, String argName, Relation<U, V> relation, V relateTo) throws IllegalArgumentException {
    return that(arg, argName, relation, relateTo, IllegalArgumentException::new);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument if the argument passes the specified test, else throws the {@code Exception} produced
   * by the specified {@code Exception} factory.
   *
   * @param <U> The type of the argument
   * @param <V> The type of the object of the relationship
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass the specified
   *     test, or any subsequent tests executed on the returned {@code Check} object
   * @param arg The argument
   * @param argName The name of the argument
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @param excFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} object
   * @throws F If the argument fails to pass the specified test or any subsequent tests called on
   *     the returned {@code Check} object
   */
  public static <U, V, F extends Exception> Check<U, F> that(
      U arg, String argName, Relation<U, V> relation, V relateTo, Function<String, F> excFactory)
      throws F {
    return new ObjectCheck<>(arg, argName, excFactory).and(relation, relateTo);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument if it passes an initial test, else throws an {@code IllegalArgumentException}.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @param argName The name of the argument
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @return A new {@code Check} object
   * @throws IllegalArgumentException If the argument fails to pass the specified test or any
   *     subsequent tests called on the returned {@code Check} object
   */
  public static <U> Check<U, IllegalArgumentException> that(
      U arg, String argName, ObjIntRelation<U> relation, int relateTo)
      throws IllegalArgumentException {
    return that(arg, argName, relation, relateTo, IllegalArgumentException::new);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument if the argument passes the specified test, else throws the {@code Exception} produced
   * by the specified {@code Exception} factory.
   *
   * @param <U> The type of the argument
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass the specified
   *     test, or any subsequent tests executed on the returned {@code Check} object
   * @param arg The argument
   * @param argName The name of the argument
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @param excFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} object
   * @throws F If the argument fails to pass the specified test or any subsequent tests called on
   *     the returned {@code Check} object
   */
  public static <U, F extends Exception> Check<U, F> that(
      U arg,
      String argName,
      ObjIntRelation<U> relation,
      int relateTo,
      Function<String, F> excFactory)
      throws F {
    return new ObjectCheck<>(arg, argName, excFactory).and(relation, relateTo);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing {@code int}
   * arguments if it passes an the specified test, else throws an {@code IllegalArgumentException}.
   *
   * @param arg The argument
   * @param argName The argument name
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @return A new {@code Check} object
   * @throws IllegalArgumentException If the argument fails to pass the specified test or any
   *     subsequent tests called on the returned {@code Check} object
   */
  public static Check<Integer, IllegalArgumentException> that(
      int arg, String argName, IntRelation relation, int relateTo) throws IllegalArgumentException {
    return new IntCheck<>(arg, argName, IllegalArgumentException::new).and(relation, relateTo);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing integers if the
   * argument passes the specified (first) test, else throws an {@code IllegalArgumentException}.
   *
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass the specified
   *     test, or any subsequent tests executed on the returned {@code Check} object
   * @param arg The argument
   * @param argName The argument name
   * @param relation The relation to verify between the argument and the specified integer ({@code
   *     relateTo})
   * @param relateTo The integer at the other end of the relationship
   * @param excFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} instance
   * @throws F If the argument fails to pass the specified test or any subsequent tests called on
   *     the returned {@code Check} object
   */
  public static <F extends Exception> Check<Integer, F> that(
      int arg, String argName, IntRelation relation, int relateTo, Function<String, F> excFactory)
      throws F {
    return new IntCheck<>(arg, argName, excFactory).and(relation, relateTo);
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
   * Verifies that the argument is a valid array index. Throws an {@code
   * ArrayIndexOutOfBoundsException} if {@code arg} is less than zero or greater than or equal to
   * {@code maxExclusive}, else returns {@code arg}. This is especially useful to test "from"
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
   * Verifies that the argument is a valid array index. Throws an {@code
   * ArrayIndexOutOfBoundsException} if {@code arg} is less than {@code min} or greater than {@code
   * max}, else returns {@code arg}. This is especially useful to test "to" or "until" arguments,
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
   * Check.that(foo.equals(bar), () -> new IllegalArgumentException(String.format("%s must equal %s", foo, bar)));
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
    if (test.test(ok())) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, ok(), argName));
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
    if (test.test(ok())) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
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
  public abstract Check<T, E> and(IntPredicate test) throws E;

  /**
   * Submits the argument to the specified test. Allows you to provide a custom error message.
   *
   * @param test The test
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public abstract Check<T, E> and(IntPredicate test, String message, Object... msgArgs) throws E;

  /**
   * Submits the argument to the specified test. This method is especially useful when using the
   * (statically imported) tests in thw {@link Checks} class, as they have predefined, informative
   * error messages associated with them.
   *
   * @param <U> The type of the object of the relationship
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> and(Relation<T, U> relation, U relateTo) throws E {
    if (relation.exists(ok(), relateTo)) {
      return this;
    }
    throw excFactory.apply(Messages.get(relation, ok(), argName, relateTo));
  }

  /**
   * Submits the argument to the specified relation. Allows you to provide a custom error message.
   *
   * @param <U> The type of the object of the relationship
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the relation fails
   */
  public <U> Check<T, E> and(Relation<T, U> relation, U relateTo, String message, Object... msgArgs)
      throws E {
    if (relation.exists(ok(), relateTo)) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  /**
   * Submits the argument to the specified relation. This method is especially useful when using the
   * (statically imported) tests in thw {@link Checks} class, as they have predefined, informative
   * error messages associated with them.
   *
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the relation fails
   */
  public Check<T, E> and(ObjIntRelation<T> relation, int relateTo) throws E {
    if (relation.exists(ok(), relateTo)) {
      return this;
    }
    throw excFactory.apply(Messages.get(relation, ok(), argName, relateTo));
  }

  /**
   * Submits the argument to the specified relation. Allows you to provide a custom error message.
   *
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the relation fails
   */
  public Check<T, E> and(
      ObjIntRelation<T> relation, int relateTo, String message, Object... msgArgs) throws E {
    if (relation.exists(ok(), relateTo)) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  /**
   * Submits the argument to the specified relation. This method is especially useful when using the
   * (statically imported) tests in thw {@link Checks} class, as they have predefined, informative
   * error messages associated with them.
   *
   * @param relation The relation to verify between the argument and the specified integer ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the relation fails
   */
  public abstract Check<T, E> and(IntRelation relation, int relateTo) throws E;

  /**
   * Submits the argument to the specified relation. Allows you to provide a custom error message.
   *
   * @param relation The relation to verify between the argument and the specified integer ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the relation fails
   */
  public abstract Check<T, E> and(
      IntRelation relation, int relateTo, String message, Object... msgArgs) throws E;

  /**
   * Submits a property of the argument to the specified test.
   *
   * @param <U> The type of the property
   * @param getter A {@code Function} with the argument as input and the value to be tested as
   *     output. Usually a getter like {@code Employee::getName}.
   * @param propName The name of the property
   * @param test The test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public abstract <U> Check<T, E> and(Function<T, U> getter, String propName, Predicate<U> test)
      throws E;

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
  public abstract <U> Check<T, E> and(
      Function<T, U> getter, Predicate<U> test, String message, Object... msgArgs) throws E;

  /**
   * Submits an integer property of the argument to the specified test.
   *
   * @param getter A {@code Function} with the argument as input and the value to be tested as
   *     output. Usually a getter like {@code Employee::getAge}.
   * @param propName The name of the property
   * @param test The test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public abstract Check<T, E> andAsInt(ToIntFunction<T> getter, String propName, IntPredicate test)
      throws E;

  /**
   * Submits an {@code int} property of the argument to the specified test.
   *
   * @param getter A {@code Function} with the argument as input and the value to be tested as
   *     output. Usually a getter like {@code Employee::getAge}.
   * @param test The test
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public abstract Check<T, E> andAsInt(
      ToIntFunction<T> getter, IntPredicate test, String message, Object... msgArgs) throws E;

  /**
   * Submits a property of the argument to the specified relation.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param getter A {@code Function} returning the subject of the relationship
   * @param propName The name of the property
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the relation fails
   */
  public abstract <U, V> Check<T, E> and(
      Function<T, U> getter, String propName, Relation<U, V> relation, V relateTo) throws E;

  /**
   * Submits a property of the argument to the specified relation.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param getter A {@code Function} returning the subject of the relationship
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the relation fails
   */
  public abstract <U, V> Check<T, E> and(
      Function<T, U> getter, Relation<U, V> relation, V relateTo, String message, Object... msgArgs)
      throws E;

  /**
   * Submits a property of the argument to the specified relation.
   *
   * @param <U> The type of the property
   * @param getter A {@code Function} returning the subject of the relationship
   * @param propName The name of the property
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the relation fails
   */
  public abstract <U> Check<T, E> and(
      Function<T, U> getter, String propName, ObjIntRelation<U> relation, int relateTo) throws E;

  /**
   * Submits a property of the argument to the specified relation.
   *
   * @param <U> The type of the property
   * @param getter A {@code Function} returning the subject of the relationship
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the relation fails
   */
  public abstract <U> Check<T, E> and(
      Function<T, U> getter,
      ObjIntRelation<U> relation,
      int relateTo,
      String message,
      Object... msgArgs)
      throws E;

  /**
   * Submits a property of the argument to the specified relation.
   *
   * @param getter A {@code Function} returning the subject of the relationship
   * @param propName The name of the property
   * @param relation The relation to verify between the argument and the specified integer ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the relation fails
   */
  public abstract Check<T, E> and(
      ToIntFunction<T> getter, String propName, IntRelation relation, int relateTo) throws E;

  /**
   * Submits a property of the argument to the specified relation.
   *
   * @param getter A {@code Function} returning the subject of the relationship
   * @param relation The relation to verify between the argument and the specified integer ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the relation fails
   */
  public abstract Check<T, E> and(
      ToIntFunction<T> getter,
      IntRelation relation,
      int relateTo,
      String message,
      Object... msgArgs)
      throws E;

  /**
   * Returns the argument being tested. To be used as the last call after a chain of checks. For
   * example:
   *
   * <pre>
   * int age = Check.notNull(employee, "employee").and(Employee::getAge, "age", lessThan(), 50).ok().getAge();
   * </pre>
   *
   * @return The argument
   */
  public abstract T ok();

  /**
   * Returns the argument being tested as an {@code int}. To be used as the last call after a chain
   * of checks .If the argument being tested actually is an {@code int} (rather than an {@code
   * Integer}), this method saves the cost of a boxing-unboxing round trip incurred by {@link
   * #ok()}. The following applies:
   *
   * <p>
   *
   * <ul>
   *   <li>If the argument is null, an {@code UnsupportedOperationException} is thrown.
   *   <li>If the argument is a {@link Number} <i>and</i> the {@code Number} can be converted to an
   *       integer without loss of precision, {@link Number#intValue() Number.intValue()} will be
   *       returned
   *   <li>If the argument is a {@link CharSequence} <i>and</i> it can be parsed into an integer
   *       without loss of precision, UnsupportedOperationException}, the value of {@link
   *       Integer#parseInt(String) Integer.parseInt()} will be returned.
   *   <li>Otherwise, an {@code UnsupportedOperationException} is thrown.
   * </ul>
   *
   * @return The argument cast or converted to an {@code int}
   */
  public abstract int intValue();
}
