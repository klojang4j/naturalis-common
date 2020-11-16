package nl.naturalis.common.check;

import java.util.function.*;
import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;
import static nl.naturalis.common.check.CommonGetters.getGetterDescription;
import static nl.naturalis.common.check.Messages.createMessage;

/**
 * Facilitates the validation of arguments and object state. Validating object state and array
 * indices happens through a single public static method. Validation of arguments happens by means
 * of an actual instance of the {@code Check} class. You obtain an instance through one of the
 * static factory methods. For example:
 *
 * <p>
 *
 * <pre>
 * this.numChairs = Check.that(numChairs).is(notNegative()).is(lte(), 10).is(even()).ok();
 * </pre>
 *
 * <h4>Common checks</h4>
 *
 * <p>The {@link CommonChecks} class contains a number of common checks for arguments. These are
 * already associated with short, informative error messages, so you don't have to invent them
 * yourself. For example:
 *
 * <p>
 *
 * <pre>
 * Check.that(numChairs, "numChairs").is(gte(), 2);
 * // "numChairs must be >= 2 (was 0)"
 * </pre>
 *
 * <h4>Checking argument properties</h4>
 *
 * <p>A {@code Check} object lets you check not just arguments but also argument properties. For
 * example:
 *
 * <p>
 *
 * <pre>
 * Check.notNull(name, "name").has(String::length, "length", gte(), 10);
 * Check.notNull(employee, "employee").has(Employee::getAge, "age", lt(), 50);
 * Check.notNull(intArray, "intArray").has(Array::getLength, "length", even());
 * Check.notNull(employees, "employees").has(Collection::size, "size", gte(), 100);
 * </pre>
 *
 * <p>The {@link CommonGetters} class defines some common getters that you can use for conciseness:
 *
 * <pre>
 * Check.notNull(name, "name").has(strlen(), gte(), 10);
 * Check.notNull(intArray, "intArray").has(length(), even());
 * Check.notNull(employees, "employees").has(size(), gte(), 100);
 * </pre>
 *
 * <h4>Lambdas</h4>
 *
 * <p>Checks are done via the various {@code is(...)} and {@code has(...)} methods. Generally, the
 * compiler has no problem deciding which of the overloaded methods is targeted. When using lambdas,
 * however, the compiler may run into ambiguities. This will result in a compiler error like: <b>The
 * method has [...] is ambigious for type Check [...]</b>. To resolve this, simply specify the type
 * of the parameters in the lambda:
 *
 * <p>
 *
 * <pre>
 * // WILL NOT COMPILE:
 * // Check.notNull(employee, "employee").has(Employee::getId, "id", x -> x > 0);
 * // WILL COMPILE:
 * Check.notNull(employee, "employee").has(Employee::getId, "id", (int x) -> x > 0); // and(IntPredicate)
 * Check.notNull(employee, "employee").has(Employee::getId, "id", (Integer x) -> x > 0); // and(Predicate&lt;Integer&gt;)
 * </pre>
 *
 * <h4>Changing the Exception type</h4>
 *
 * <p>By default an {@code IllegalArgumentException} is thrown if an argument fails to pass a test.
 * This can be customized through the static factory methods. For example:
 *
 * <p>
 *
 * <pre>
 * this.query = Check.that(query, "query", InvalidQueryException::new)
 *  .has(QuerySpec::getFrom, "from", nullOr(), 0)
 *  .has(QuerySpec::getSize, "size", gte(), MIN_BATCH_SIZE)
 *  .has(QuerySpec::getSize, "size", lt(), MAX_BATCH_SIZE)
 *  .has(QuerySpec::getSortFields, "sortFields", empty())
 *  .ok();
 * </pre>
 *
 * @author Ayco Holleman
 * @param <T> The type of the object being checked
 * @param <E> The type of exception thrown if a test fails
 */
public abstract class Check<T, E extends Exception> {

  static final String DEFAULT_ARG_NAME = "argument";

  private static final Function<String, IllegalArgumentException> DEFAULT_EXCEPTION =
      IllegalArgumentException::new;

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing integers.
   *
   * @param arg The argument
   * @return A new {@code Check} object
   */
  public static Check<Integer, IllegalArgumentException> that(int arg) {
    return new IntCheck<>(arg, DEFAULT_ARG_NAME, DEFAULT_EXCEPTION);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing integers.
   *
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass a test
   * @param arg The argument
   * @param exceptionFactory
   * @return
   */
  public static <F extends Exception> Check<Integer, F> that(
      int arg, Function<String, F> exceptionFactory) {
    return new IntCheck<>(arg, DEFAULT_ARG_NAME, exceptionFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @return A new {@code Check} object
   */
  public static <U> Check<U, IllegalArgumentException> that(U arg) {
    return new ObjectCheck<>(arg, DEFAULT_ARG_NAME, DEFAULT_EXCEPTION);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument.
   *
   * @param <U> The type of the argument
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass a test
   * @param arg The argument
   * @param exceptionFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} object
   */
  public static <U, F extends Exception> Check<U, F> that(
      U arg, Function<String, F> exceptionFactory) {
    return new ObjectCheck<>(arg, DEFAULT_ARG_NAME, exceptionFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing integers.
   *
   * @param arg The argument
   * @param argName The name of the argument
   * @return A new {@code Check} object
   */
  public static Check<Integer, IllegalArgumentException> that(int arg, String argName) {
    return new IntCheck<>(arg, argName, DEFAULT_EXCEPTION);
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
  public static <U> Check<U, IllegalArgumentException> that(U arg, String argName) {
    return new ObjectCheck<>(arg, argName, DEFAULT_EXCEPTION);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument. The argument will have already passed the {@link CommonChecks#notNull() notNull}
   * test.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @return A new {@code Check} object
   */
  public static <U> Check<U, IllegalArgumentException> notNull(U arg)
      throws IllegalArgumentException {
    return that(arg, DEFAULT_ARG_NAME, DEFAULT_EXCEPTION).is(CommonChecks.notNull());
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument. The argument will have already passed the {@link CommonChecks#notNull() notNull}
   * test.
   *
   * @param <U> The type of the argument
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass a test
   * @param arg The argument
   * @param exceptionFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} object
   * @throws F If the argument fails to pass the {@code notNull} test or any subsequent tests called
   *     on the returned {@code Check} object
   */
  public static <U, F extends Exception> Check<U, F> notNull(
      U arg, Function<String, F> exceptionFactory) throws F {
    return that(arg, DEFAULT_ARG_NAME, exceptionFactory).is(CommonChecks.notNull());
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument. The argument will have already passed the {@link CommonChecks#notNull() notNull}
   * test.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @param argName The name of the argument
   * @return A new {@code Check} object
   */
  public static <U> Check<U, IllegalArgumentException> notNull(U arg, String argName)
      throws IllegalArgumentException {
    return that(arg, argName, DEFAULT_EXCEPTION).is(CommonChecks.notNull());
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument. The argument will have already passed the {@link CommonChecks#notNull() notNull}
   * test.
   *
   * @param <U> The type of the argument
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass a test
   * @param arg The argument
   * @param argName The name of the argument
   * @param exceptionFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} object
   * @throws F If the argument fails to pass the {@code notNull} test or any subsequent tests called
   *     on the returned {@code Check} object
   */
  public static <U, F extends Exception> Check<U, F> notNull(
      U arg, String argName, Function<String, F> exceptionFactory) throws F {
    return that(arg, argName, exceptionFactory).is(CommonChecks.notNull());
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing integers.
   *
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass a test
   * @param arg The argument
   * @param argName The name of the argument
   * @param exceptionFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @return A new {@code Check} object
   */
  public static <F extends Exception> Check<Integer, F> that(
      int arg, String argName, Function<String, F> exceptionFactory) {
    return new IntCheck<>(arg, argName, exceptionFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument.
   *
   * @param arg The argument
   * @param argName The name of the argument
   * @param exceptionFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @param <U> The type of the argument
   * @param <F> The type of {@code Exception} thrown if the argument fails to pass a test
   * @return A new {@code Check} object
   */
  public static <U, F extends Exception> Check<U, F> that(
      U arg, String argName, Function<String, F> exceptionFactory) {
    return new ObjectCheck<>(arg, argName, exceptionFactory);
  }

  /**
   * Generic check method, not related the static factory methods with the same name. Throws an
   * {@code IllegalArgumentException} if the provided condition evaluates to false, else does
   * nothing.
   *
   * @param condition The condition to evaluate
   * @param message The error message
   * @param msgArgs The message arguments
   * @throws IllegalArgumentException If the condition to evaluate
   */
  public static void that(boolean condition, String message, Object... msgArgs)
      throws IllegalArgumentException {
    if (!condition) {
      throw new IllegalArgumentException(String.format(message, msgArgs));
    }
  }

  /**
   * Generic check method, not related the static factory methods with the same name. Throws the
   * exception supplied by the provided supplier if the provided condition evaluates to false, else
   * does nothing.
   *
   * @param <F> The type of exception thrown if {@code condition} evaluates to false
   * @param condition The condition to evaluate
   * @param exceptionSupplier The exception supplier
   * @throws F The exception thrown if the condition evaluates to false
   */
  public static <F extends Exception> void that(boolean condition, Supplier<F> exceptionSupplier)
      throws F {
    if (!condition) {
      throw exceptionSupplier.get();
    }
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
  public static Function<String, IllegalArgumentException> badArgument(
      String message, Object... msgArgs) {
    return (s) -> new IllegalArgumentException(String.format(message, msgArgs));
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////
  // Instance fields / methods start here
  //////////////////////////////////////////////////////////////////////////////////////////////////////

  final String argName;
  final Function<String, E> excFactory;

  Check(String argName, Function<String, E> exceptionFactory) {
    this.argName = argName;
    this.excFactory = exceptionFactory;
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
   * (statically imported) tests in the {@link CommonChecks} class, as they have predefined,
   * informative error messages associated with them.
   *
   * @param test The test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> is(Predicate<T> test) throws E {
    if (test.test(ok())) {
      return this;
    }
    String msg = createMessage(test, argName, ok());
    throw excFactory.apply(msg);
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
  public Check<T, E> is(Predicate<T> test, String message, Object... msgArgs) throws E {
    if (test.test(ok())) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Submits the argument to the specified test. This method is especially useful when using the
   * (statically imported) tests in thw {@link CommonChecks} class, as they have predefined,
   * informative error messages associated with them.
   *
   * @param test The test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public abstract Check<T, E> is(IntPredicate test) throws E;

  /**
   * Submits the argument to the specified test. Allows you to provide a custom error message.
   *
   * @param test The test
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public abstract Check<T, E> is(IntPredicate test, String message, Object... msgArgs) throws E;

  /**
   * Verifies that there is some relation between the argument and some other value. This method is
   * especially useful when using the (statically imported) tests in thw {@link CommonChecks} class,
   * as they have predefined, informative error messages associated with them.
   *
   * @param <U> The type of the object of the relationship
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> is(Relation<T, U> relation, U relateTo) throws E {
    if (relation.exists(ok(), relateTo)) {
      return this;
    }
    String msg = createMessage(relation, argName, ok(), relateTo);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that there is some relation between the argument and some other value. Allows you to
   * provide a custom error message.
   *
   * @param <U> The type of the object of the relationship
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public <U> Check<T, E> is(Relation<T, U> relation, U relateTo, String message, Object... msgArgs)
      throws E {
    if (relation.exists(ok(), relateTo)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that there is some relation between the argument and some other value. This method is
   * especially useful when using the (statically imported) tests in thw {@link CommonChecks} class,
   * as they have predefined, informative error messages associated with them.
   *
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public Check<T, E> is(ObjIntRelation<T> relation, int relateTo) throws E {
    if (relation.exists(ok(), relateTo)) {
      return this;
    }
    String msg = createMessage(relation, argName, ok(), relateTo);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that there is some relation between the argument and some other value. Allows you to
   * provide a custom error message.
   *
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public Check<T, E> is(ObjIntRelation<T> relation, int relateTo, String message, Object... msgArgs)
      throws E {
    if (relation.exists(ok(), relateTo)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that there is some relation between the integer argument and some object.
   *
   * @param <U> The type of the object of the relationship
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public abstract <U> Check<T, E> is(IntObjRelation<U> relation, U relateTo) throws E;

  /**
   * Verifies that there is some relation between the integer argument and some object. Allows you
   * to provide a custom error message.
   *
   * @param <U> The type of the object of the relationship
   * @param relation The relation to verify between the argument and the specified value ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public abstract <U> Check<T, E> is(
      IntObjRelation<U> relation, U relateTo, String message, Object... msgArgs) throws E;

  /**
   * Verifies that there is some relation between the argument and some other value. This method is
   * especially useful when using the (statically imported) tests in thw {@link CommonChecks} class,
   * as they have predefined, informative error messages associated with them.
   *
   * @param relation The relation to verify between the argument and the specified integer ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public abstract Check<T, E> is(IntRelation relation, int relateTo) throws E;

  /**
   * Verifies that there is some relation between the argument and some other value. Allows you to
   * provide a custom error message.
   *
   * @param relation The relation to verify between the argument and the specified integer ({@code
   *     relateTo})
   * @param relateTo The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public abstract Check<T, E> is(
      IntRelation relation, int relateTo, String message, Object... msgArgs) throws E;

  /**
   * Submits a property of the argument to the specified test.
   *
   * @param <U> The type of the property
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the value to be tested
   * @param property The name of the property
   * @param test The test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> has(Function<T, U> getter, String property, Predicate<U> test) throws E {
    U value = getter.apply(ok());
    if (test.test(value)) {
      return this;
    }
    String name = getFullyQualified(property);
    String msg = createMessage(test, name, value);
    throw excFactory.apply(msg);
  }

  /**
   * Submits a property of the argument to the specified test. Can be used if you pass one of the
   * getters defined in {@link CommonGetters} as these are already associated with a property name.
   *
   * @param <U> The type of the property
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the value to be tested
   * @param test The test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> has(Function<T, U> getter, Predicate<U> test) throws E {
    U value = getter.apply(ok());
    if (test.test(value)) {
      return this;
    }
    String name = getGetterDescription(argName, getter);
    String msg = createMessage(test, name, value);
    throw excFactory.apply(msg);
  }

  /**
   * Submits a property of the argument to the specified test.
   *
   * @param <U> The type of the property
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the value to be tested
   * @param test The test
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> has(
      Function<T, U> getter, Predicate<U> test, String message, Object... msgArgs) throws E {
    U value = getter.apply(ok());
    if (test.test(value)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Submits an integer property of the argument to the specified test.
   *
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the value to be tested
   * @param property The name of the property
   * @param test The test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> has(ToIntFunction<T> getter, String property, IntPredicate test) throws E {
    int value = getter.applyAsInt(ok());
    if (test.test(value)) {
      return this;
    }
    String name = getFullyQualified(property);
    String msg = createMessage(test, name, value);
    throw excFactory.apply(msg);
  }

  /**
   * Submits an integer property of the argument to the specified test. Can be used if you pass one
   * of the getters defined in {@link CommonGetters} as these are already associated with a property
   * name.
   *
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the value to be tested
   * @param test The test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> has(ToIntFunction<T> getter, IntPredicate test) throws E {
    int value = getter.applyAsInt(ok());
    if (test.test(value)) {
      return this;
    }
    String name = getGetterDescription(argName, getter);
    String msg = createMessage(test, name, value);
    throw excFactory.apply(msg);
  }

  /**
   * Submits an {@code int} property of the argument to the specified test.
   *
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the value to be tested
   * @param test The test
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> has(
      ToIntFunction<T> getter, IntPredicate test, String message, Object... msgArgs) throws E {
    int value = getter.applyAsInt(ok());
    if (test.test(value)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that there is some relation between a property of the argument and some other value.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the subject of the relationship
   * @param property The name of the property
   * @param relation The relation to verify between subject and object
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public <U, V> Check<T, E> has(
      Function<T, U> getter, String property, Relation<U, V> relation, V relateTo) throws E {
    U value = getter.apply(ok());
    if (relation.exists(value, relateTo)) {
      return this;
    }
    String name = getFullyQualified(property);
    String msg = createMessage(relation, name, value, relateTo);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that there is some relation between a property of the argument and some other value.
   * Can be used if you pass one of the getters defined in {@link CommonGetters} as these are
   * already associated with a property name.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the subject of the relationship
   * @param relation The relation to verify between subject and object
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public <U, V> Check<T, E> has(Function<T, U> getter, Relation<U, V> relation, V relateTo)
      throws E {
    U value = getter.apply(ok());
    if (relation.exists(value, relateTo)) {
      return this;
    }
    String name = getGetterDescription(argName, getter);
    String msg = createMessage(relation, name, value, relateTo);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that there is some relation between a property of the argument and some other value.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the subject of the relationship
   * @param relation The relation to verify between subject and object
   * @param relateTo The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public <U, V> Check<T, E> has(
      Function<T, U> getter, Relation<U, V> relation, V relateTo, String message, Object... msgArgs)
      throws E {
    U value = getter.apply(ok());
    if (relation.exists(value, relateTo)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that there is some relation between a property of the argument and some other value.
   *
   * @param <U> The type of the property
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the subject of the relationship
   * @param property The name of the property
   * @param relation The relation to verify between subject and object
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public <U> Check<T, E> has(
      Function<T, U> getter, String property, ObjIntRelation<U> relation, int relateTo) throws E {
    U value = getter.apply(ok());
    if (relation.exists(value, relateTo)) {
      return this;
    }
    String name = getFullyQualified(property);
    String msg = createMessage(relation, name, value, relateTo);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that there is some relation between a property of the argument and some other value.
   * Can be used if you pass one of the getters defined in {@link CommonGetters} as these are
   * already associated with a property name.
   *
   * @param <U> The type of the property
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the subject of the relationship
   * @param relation The relation to verify between subject and object
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public <U> Check<T, E> has(Function<T, U> getter, ObjIntRelation<U> relation, int relateTo)
      throws E {
    U value = getter.apply(ok());
    if (relation.exists(value, relateTo)) {
      return this;
    }
    String name = getGetterDescription(argName, getter);
    String msg = createMessage(relation, name, value, relateTo);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that there is some relation between a property of the argument and some other value.
   *
   * @param <U> The type of the property
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the subject of the relationship
   * @param relation The relation to verify between subject and object
   * @param relateTo The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public <U> Check<T, E> has(
      Function<T, U> getter,
      ObjIntRelation<U> relation,
      int relateTo,
      String message,
      Object... msgArgs)
      throws E {
    U value = getter.apply(ok());
    if (relation.exists(value, relateTo)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that there is some relation between a property of the argument and some other value.
   *
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the subject of the relationship
   * @param property The name of the property
   * @param relation The relation to verify between subject and object
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public Check<T, E> has(
      ToIntFunction<T> getter, String property, IntRelation relation, int relateTo) throws E {
    int value = getter.applyAsInt(ok());
    if (relation.exists(value, relateTo)) {
      return this;
    }
    String name = getFullyQualified(property);
    String msg = createMessage(relation, name, value, relateTo);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that there is some relation between a property of the argument and some other value.
   * Can be used if you pass one of the getters defined in {@link CommonGetters} as these are
   * already associated with a property name.
   *
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the subject of the relationship
   * @param relation The relation to verify between subject and object
   * @param relateTo The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public Check<T, E> has(ToIntFunction<T> getter, IntRelation relation, int relateTo) throws E {
    int value = getter.applyAsInt(ok());
    if (relation.exists(value, relateTo)) {
      return this;
    }
    String name = getGetterDescription(argName, getter);
    String msg = createMessage(relation, name, value, relateTo);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that there is some relation between a property of the argument and some other value.
   *
   * @param getter A {@code Function} which is given the argument as input and which should return
   *     the subject of the relationship
   * @param relation The relation to verify between subject and object
   * @param relateTo The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified relation does not exist between subject and object
   */
  public Check<T, E> has(
      ToIntFunction<T> getter,
      IntRelation relation,
      int relateTo,
      String message,
      Object... msgArgs)
      throws E {
    int value = getter.applyAsInt(ok());
    if (relation.exists(value, relateTo)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Returns the argument being tested. To be used as the last call after a chain of checks. For
   * example:
   *
   * <pre>
   * int age = Check.notNull(employee, "employee").has(Employee::getAge, "age", lt(), 50).ok().getAge();
   * </pre>
   *
   * @return The argument
   */
  public abstract T ok();

  /**
   * Passes the validated argument to the specified {@code Function} and returns the value computed
   * by the {@code Function}. To be used as the last call after a chain of checks.
   *
   * @param <U> The type of the returned value
   * @param transformer A {@code Function} that transforms the argument into some other value
   * @return The value computed by the {@code Function}
   */
  public <U> U ok(Function<T, U> transformer) {
    return transformer.apply(ok());
  }

  /**
   * Passes the validated argument to a {@code Consumer} to be processed safely. To be used as the
   * last call after a chain of checks.
   *
   * @param consumer The {@code Consumer}
   */
  public void then(Consumer<T> consumer) {
    consumer.accept(ok());
  }

  /**
   * Returns the argument being tested as an {@code int}. To be used as the last call after a chain
   * of checks. If the argument being tested actually is an {@code int} (rather than an {@code
   * Integer}), this method saves the cost of a boxing-unboxing round trip incurred by {@link
   * #ok()}.
   *
   * @see NumberMethods#fitsInto(Number, Class)
   * @return The argument cast or converted to an {@code int}
   */
  public abstract int intValue() throws E;

  /**
   * Passes the validated argument to the specified {@code Function} and returns the value computed
   * by the {@code Function}. To be used as the last call after a chain of checks.
   *
   * @param transformer An {@code IntFunction} that transforms the argument into some other value
   */
  public <U> U intValue(IntFunction<U> transformer) throws E {
    return transformer.apply(intValue());
  }

  private String getFullyQualified(String property) {
    return argName + "." + property;
  }
}
