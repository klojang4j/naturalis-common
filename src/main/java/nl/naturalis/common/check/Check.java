package nl.naturalis.common.check;

import java.io.OutputStream;
import java.util.function.*;
import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.function.*;
import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.Messages.createMessage;

/**
 * Facilitates precondition checking. For example:
 *
 * <p>
 *
 * <pre>
 * this.numChairs = Check.that(numChairs).is(gt(), 0).is(lte(), 10).is(even()).ok();
 * </pre>
 *
 * <h4>Common checks</h4>
 *
 * The {@link CommonChecks} class provides a grab bag of common checks for arguments. These are
 * already associated with short, informative error messages, so you don't have to invent them
 * yourself. For example:
 *
 * <p>
 *
 * <pre>
 * Check.that(numChairs, "numChairs").is(gt(), 0);
 * // Auto-generated error message: "numChairs must be > 0 (was -3)"
 * </pre>
 *
 * <h4>Checking argument properties</h4>
 *
 * A {@code Check} object lets you check not just arguments but also argument properties. For
 * example:
 *
 * <pre>
 * Check.notNull(name, "name").has(String::length, "length", gte(), 10);
 * Check.notNull(employee, "employee").has(Employee::getAge, "age", lt(), 50);
 * Check.notNull(employees, "emps").has(Collection::size, "size", gte(), 100);
 * </pre>
 *
 * <p>The {@link CommonGetters} class defines some common getters which, again, are already
 * associated with the name of the name they expose:
 *
 * <pre>
 * Check.notNull(employees, "emps").has(size(), gte(), 100);
 * // Auto-generated error message: "emps.size() must be >= 100 (was 42)"
 * </pre>
 *
 * <h4>Lambdas</h4>
 *
 * Checks are done via the various {@code is(...)} and {@code has(...)} methods. These methods are
 * overloaded to take either a {@link Predicate} or an {@link IntPredicate}. This is not a problem
 * when passing them a method reference or a check from the {@code CommonChecks} class. When passing
 * a lambda, however, the compiler will be unable to decide whether it is dealing with a {@code
 * Predicate} or an {@code IntPredicate} - an unfortunate side effect of the combination of type
 * erasure and auto-boxing. This will result in a compiler error like <i>The method
 * is(Predicate&lt;String&gt;) is ambigious for the type Check&lt;String,
 * IllegalArgumentException&gt;</i>:
 *
 * <p>
 *
 * <pre>
 * // Won't compile even though it's clear this can't be an IntPredicate:
 * Check.that(fullName).is(s -> s.charAt(0) == 'A');
 * </pre>
 *
 * <p>To resolve this, simply specify the type of the lambda parameter:
 *
 * <pre>
 * Check.that(fullName).is((String s) -> s.charAt(0) == 'A');
 * </pre>
 *
 * <p>Alternatively, you can use the {@link CommonChecks#asObj(Predicate) asObj} and {@link
 * CommonChecks#asInt(Predicate) asInt} utility methods:
 *
 * <pre>
 * Check.that(numChairs).is(asInt(x -> x <= 10)); // IntPredicate
 * Check.that(numChairs).is(asObj(x -> x <= 10)); // Predicate&lt;Integer&gt;
 * </pre>
 *
 * <p>Or, as another alternative, every {@code Predicate} can also be written as a {@link Relation}:
 *
 * <pre>
 * Check.that(fullName).has(s -> s.charAt(0), eq(), 'A');
 * </pre>
 *
 * <h4>Changing the Exception type</h4>
 *
 * By default an {@code IllegalArgumentException} is thrown if an argument fails to pass a test.
 * This can be customized through the static factory methods. For example:
 *
 * <p>
 *
 * <pre>
 * this.query = Check.with(InvalidQueryException::new, query, "query").has(Query::getFrom, "from", nullOr(), 0).has(Query::getSize, "size", gte(), 10).has(
 *     Query::getSize, "size", lte(), 10000).ok();
 * </pre>
 *
 * @author Ayco Holleman
 * @param <T> The type of the object being checked
 * @param <E> The type of exception thrown if a test fails
 */
public abstract class Check<T, E extends Exception> {

  static final String DEF_ARG_NAME = "argument";

  private static final Function<String, IllegalArgumentException> DEF_EXC_FACTORY =
      IllegalArgumentException::new;

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing integers.
   *
   * @param arg The argument
   * @return A {@code Check} object suitable for testing integers
   */
  public static Check<Integer, IllegalArgumentException> that(int arg) {
    return new IntCheck<>(arg, DEF_ARG_NAME, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument.
   *
   * @param <U> The type of the argument
   * @param arg The argument
   * @return A {@code Check} object suitable for testing the provided argument
   */
  public static <U> Check<U, IllegalArgumentException> that(U arg) {
    return new ObjectCheck<>(arg, DEF_ARG_NAME, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing integers.
   *
   * @param arg The argument
   * @param argName The name of the argument
   * @return A new {@code Check} object
   */
  public static Check<Integer, IllegalArgumentException> that(int arg, String argName) {
    return new IntCheck<>(arg, argName, DEF_EXC_FACTORY);
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
    return new ObjectCheck<>(arg, argName, DEF_EXC_FACTORY);
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
    /*
     * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ NB we
     * construct the Check instance right here, rather than calling another static
     * factory method, e.g. notNull(arg, DEFAULT_ARG_NAME). In performance tests it
     * turned out that for some reason the JVM had a hard time inlining these calls,
     * making the notNull check about 20% slower than a "manual" not null check.
     * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */
    if (arg == null) {
      String msg = createMessage(CommonChecks.notNull(), false, DEF_ARG_NAME, null);
      throw DEF_EXC_FACTORY.apply(msg);
    }
    return new ObjectCheck<>(arg, DEF_ARG_NAME, DEF_EXC_FACTORY);
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
    if (arg == null) {
      String msg = createMessage(CommonChecks.notNull(), false, argName, null);
      throw DEF_EXC_FACTORY.apply(msg);
    }
    return new ObjectCheck<>(arg, argName, DEF_EXC_FACTORY);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument. The argument will have already passed the {@link CommonChecks#notNull() notNull}
   * test.
   *
   * @param excFactory A {@code Function} that takes a {@code String} (the error message) and
   *     returns an {@code Exception}
   * @param arg The argument
   * @param <U> The type of the argument
   * @param <X> The type of {@code Exception} thrown if the argument fails to pass a test
   * @return A new {@code Check} object
   * @throws X If the argument fails to pass the {@code notNull} test or any subsequent tests called
   *     on the returned {@code Check} object
   */
  public static <U, X extends Exception> Check<U, X> notNull(Function<String, X> excFactory, U arg)
      throws X {
    if (arg == null) {
      String msg = createMessage(CommonChecks.notNull(), false, DEF_ARG_NAME, null);
      throw excFactory.apply(msg);
    }
    return new ObjectCheck<>(arg, DEF_ARG_NAME, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
   * argument. The argument will have already passed the {@link CommonChecks#notNull() notNull}
   * test.
   *
   * @param excFactory A {@code Function} that will produce the exception if a test fails. The
   *     {@code Function} takes a {@code String} (the error message) and returns the {@code
   *     Exception}
   * @param arg The argument
   * @param argName The name of the argument
   * @param <U> The type of the argument
   * @param <X> The type of {@code Exception} thrown if the argument fails to pass a test
   * @return A new {@code Check} object
   * @throws X If the argument fails to pass the {@code notNull} test or any subsequent tests called
   *     on the returned {@code Check} object
   */
  public static <U, X extends Exception> Check<U, X> notNull(
      Function<String, X> excFactory, U arg, String argName) throws X {
    if (arg == null) {
      String msg = createMessage(CommonChecks.notNull(), false, argName, null);
      throw excFactory.apply(msg);
    }
    return new ObjectCheck<>(arg, argName, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing integers.
   *
   * @param excFactory A {@code Function} that will produce the exception if a test fails. The
   *     {@code Function} will be passed a {@code String} (the error message) and must return the
   *     {@code Exception} to be thrown
   * @param arg The argument
   * @param <X> The type of {@code Exception} thrown if the argument fails to pass a test
   * @return A {@code Check} object suitable for testing {@code int} arguments
   */
  public static <X extends Exception> Check<Integer, X> on(
      Function<String, X> excFactory, int arg) {
    return new IntCheck<>(arg, DEF_ARG_NAME, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
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
  public static <U, X extends Exception> Check<U, X> on(Function<String, X> excFactory, U arg) {
    return new ObjectCheck<>(arg, DEF_ARG_NAME, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing integers.
   *
   * @param excFactory A {@code Function} that will produce the exception if a test fails. The
   *     {@code Function} will be passed a {@code String} (the error message) and must return the
   *     {@code Exception} to be thrown
   * @param arg The argument
   * @param argName The name of the argument
   * @param <X> The type of {@code Exception} thrown if the argument fails to pass a test
   * @return A new {@code Check} object
   */
  public static <X extends Exception> Check<Integer, X> on(
      Function<String, X> excFactory, int arg, String argName) {
    return new IntCheck<>(arg, argName, excFactory);
  }

  /**
   * Static factory method. Returns a new {@code Check} object suitable for testing the provided
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
  public static <U, X extends Exception> Check<U, X> on(
      Function<String, X> excFactory, U arg, String argName) {
    return new ObjectCheck<>(arg, argName, excFactory);
  }

  /**
   * Throws an {@code IllegalArgumentException} with the specified message and message arguments.
   * The method is still declared to return a value of type &lt;U&gt; so it can be used as the
   * expression for a {@code return statement}.
   *
   * @param <U> The type of the object that would have been returned if it had passed the checks
   * @param msg The message
   * @param msgArgs The message argument
   * @return Nothing, but allows {@code fail} to be used as the expresion in a {@code return}
   *     statement
   */
  public static <U> U fail(String msg, Object... msgArgs) {
    throw DEF_EXC_FACTORY.apply(String.format(msg, msgArgs));
  }

  /**
   * Throws an exception created by the specified exception factory.
   *
   * @param <U> The type of the object that would have been returned if it had passed the checks
   * @param <X> The type of the exception
   * @param excFactory
   * @return Nothing, but allows {@code fail} to be used as the expresion in a {@code return}
   *     statement
   * @throws X The exception that is thrown
   */
  public static <U, X extends Exception> U failOn(Function<String, X> excFactory) throws X {
    // Message "Invalid argument" likely to be ignored by factory, but why risk an
    // NPE
    throw excFactory.apply("Invalid argument");
  }

  /**
   * Throws an exception created by the specified exception factory with the specified message and
   * message arguments.
   *
   * @param <U> The type of the object that would have been returned if it had passed the checks
   * @param <X> The type of the exception
   * @param msg The message
   * @param msgArgs The message argument
   * @return Nothing, but allows {@code fail} to be used as the expresion in a {@code return}
   *     statement
   * @throws X The exception that is thrown
   */
  public static <U, X extends Exception> U failOn(
      Function<String, X> excFactory, String msg, Object... msgArgs) throws X {
    throw excFactory.apply(String.format(msg, msgArgs));
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
   * Verifies that the argument passes the test expressed through the specified {@code Predicate}.
   * Although not required this method is meant to be used with a {@code Predicate} from the {@link
   * CommonChecks} class so that an informative error message is generated upon failure.
   *
   * @param test A {@code Predicate} expressing the test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> is(Predicate<T> test) throws E {
    if (test.test(ok())) {
      return this;
    }
    String msg = createMessage(test, false, argName, ok());
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that the argument ducks the test expressed through the specified {@code Predicate}.
   * Although not required this method is meant to be used with a {@code Predicate} from the {@link
   * CommonChecks} class so that an informative error message is generated upon failure.
   *
   * @param test A {@code Predicate} expressing the test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> isNot(Predicate<T> test) throws E {
    if (!test.test(ok())) {
      return this;
    }
    String msg = createMessage(test, true, argName, ok());
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that the argument passes the test expressed through the specified {@code Predicate}.
   * Allows you to provide a custom error message.
   *
   * @param test A {@code Predicate} expressing the test
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
   * Verifies that the argument ducks the test expressed through the specified {@code Predicate}.
   * Allows you to provide a custom error message.
   *
   * @param test A {@code Predicate} expressing the test
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> isNot(Predicate<T> test, String message, Object... msgArgs) throws E {
    return is(test.negate(), message, msgArgs);
  }

  /**
   * Verifies that the argument passes the test expressed through the specified {@code
   * IntPredicate}. Although not required this method is meant to be used with an {@code
   * IntPredicate} from the {@link CommonChecks} class so that an informative error message is
   * generated upon failure.
   *
   * @param test An {@code Predicate} expressing the test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public abstract Check<T, E> is(IntPredicate test) throws E;

  /**
   * Verifies that the argument ducks the test expressed through the specified {@code IntPredicate}.
   * Although not required this method is meant to be used with an {@code IntPredicate} from the
   * {@link CommonChecks} class so that an informative error message is generated upon failure.
   *
   * @param test An {@code IntPredicate} expressing the test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public abstract Check<T, E> isNot(IntPredicate test) throws E;

  /**
   * Verifies that the argument passes the test expressed through the specified {@code
   * IntPredicate}. Allows you to provide a custom error message.
   *
   * @param test An {@code IntPredicate} expressing the test
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public abstract Check<T, E> is(IntPredicate test, String message, Object... msgArgs) throws E;

  /**
   * Verifies that the argument ducks the test expressed through the specified {@code IntPredicate}.
   * Allows you to provide a custom error message.
   *
   * @param test An {@code IntPredicate} expressing the test
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> isNot(IntPredicate test, String message, Object... msgArgs) throws E {
    return is(test.negate(), message, msgArgs);
  }

  /**
   * Verifies that the argument passes the test expressed through the specified {@code Relation}.
   * Although not required this method is meant to be used with a {@code Relation} from the {@link
   * CommonChecks} class so that an informative error message is generated upon failure.
   *
   * @param <U> The type of the object of the relationship
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> is(Relation<T, U> test, U object) throws E {
    if (test.exists(ok(), object)) {
      return this;
    }
    String msg = createMessage(test, false, argName, ok(), object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that the argument ducks the test expressed through the specified {@code Relation}.
   * Although not required this method is meant to be used with a {@code Relation} from the {@link
   * CommonChecks} class so that an informative error message is generated upon failure.
   *
   * @param <U> The type of the object of the relationship
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> isNot(Relation<T, U> test, U object) throws E {
    if (!test.exists(ok(), object)) {
      return this;
    }
    String msg = createMessage(test, true, argName, ok(), object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that the argument passes the test expressed through the specified {@code Relation}.
   * Allows you to provide a custom error message.
   *
   * @param <U> The type of the object of the relationship
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> Check<T, E> is(Relation<T, U> test, U object, String message, Object... msgArgs)
      throws E {
    if (test.exists(ok(), object)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that the argument ducks the test expressed through the specified {@code Relation}.
   * Allows you to provide a custom error message.
   *
   * @param <U> The type of the object of the relationship
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> Check<T, E> isNot(Relation<T, U> test, U object, String message, Object... msgArgs)
      throws E {
    return is(test.negate(), object, message, msgArgs);
  }

  /**
   * Verifies that the argument passes the test expressed through the specified {@code
   * ObjIntRelation}. Although not required this method is meant to be used with an {@code
   * ObjIntRelation} from the {@link CommonChecks} class so that an informative error message is
   * generated upon failure.
   *
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public Check<T, E> is(ObjIntRelation<T> test, int object) throws E {
    if (test.exists(ok(), object)) {
      return this;
    }
    String msg = createMessage(test, false, argName, ok(), object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that the argument ducks the test expressed through the specified {@code
   * ObjIntRelation}. Although not required this method is meant to be used with an {@code
   * ObjIntRelation} from the {@link CommonChecks} class so that an informative error message is
   * generated upon failure.
   *
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public Check<T, E> isNot(ObjIntRelation<T> test, int object) throws E {
    if (!test.exists(ok(), object)) {
      return this;
    }
    String msg = createMessage(test, true, argName, ok(), object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that the argument passes the test expressed through the specified {@code
   * ObjIntRelation}. Allows you to provide a custom error message.
   *
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public Check<T, E> is(ObjIntRelation<T> test, int object, String message, Object... msgArgs)
      throws E {
    if (test.exists(ok(), object)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that the argument ducks the test expressed through the specified {@code
   * ObjIntRelation}. Allows you to provide a custom error message.
   *
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public Check<T, E> isNot(ObjIntRelation<T> test, int object, String message, Object... msgArgs)
      throws E {
    return is(test.negate(), object, message, msgArgs);
  }

  /**
   * Verifies that the argument passes the test expressed through the specified {@code
   * IntObjRelation}. Although not required this method is meant to be used with an {@code
   * IntObjRelation} from the {@link CommonChecks} class so that an informative error message is
   * generated upon failure.
   *
   * @param <U> The type of the object of the relationship
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public abstract <U> Check<T, E> is(IntObjRelation<U> test, U object) throws E;

  /**
   * Verifies that the argument ducks the test expressed through the specified {@code
   * IntObjRelation}. Although not required this method is meant to be used with an {@code
   * IntObjRelation} from the {@link CommonChecks} class so that an informative error message is
   * generated upon failure.
   *
   * @param <U> The type of the object of the relationship
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public abstract <U> Check<T, E> isNot(IntObjRelation<U> test, U object) throws E;

  /**
   * Verifies that the argument passes the test expressed through the specified {@code
   * IntObjRelation}. Allows you to provide a custom error message.
   *
   * @param <U> The type of the object of the relationship
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public abstract <U> Check<T, E> is(
      IntObjRelation<U> test, U object, String message, Object... msgArgs) throws E;

  /**
   * Verifies that the argument ducks the test expressed through the specified {@code
   * IntObjRelation}. Allows you to provide a custom error message.
   *
   * @param <U> The type of the object of the relationship
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> Check<T, E> isNot(IntObjRelation<U> test, U object, String message, Object... msgArgs)
      throws E {
    return is(test.negate(), object, message, msgArgs);
  }

  /**
   * Verifies that the argument passes the test expressed through the specified {@code IntRelation}.
   * Although not required this method is meant to be used with an {@code IntRelation} from the
   * {@link CommonChecks} class so that an informative error message is generated upon failure.
   *
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public abstract Check<T, E> is(IntRelation test, int object) throws E;

  /**
   * Verifies that the argument ducks the test expressed through the specified {@code IntRelation}.
   * Although not required this method is meant to be used with an {@code IntRelation} from the
   * {@link CommonChecks} class so that an informative error message is generated upon failure.
   *
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public abstract Check<T, E> isNot(IntRelation test, int object) throws E;

  /**
   * Verifies that the argument passes the test expressed through the specified {@code IntRelation}.
   * Allows you to provide a custom error message.
   *
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public abstract Check<T, E> is(IntRelation test, int object, String message, Object... msgArgs)
      throws E;

  /**
   * Verifies that the argument ducks the test expressed through the specified {@code IntRelation}.
   * Allows you to provide a custom error message.
   *
   * @param test The relation to verify between the argument (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public Check<T, E> isNot(IntRelation test, int object, String message, Object... msgArgs)
      throws E {
    return is(test.negate(), object, message, msgArgs);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the test expressed through the specified {@code Predicate}. Although not required this
   * method is meant to be used with a {@code Predicate} from the {@link CommonChecks} class so that
   * an informative error message is generated upon failure.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test A {@code Predicate} expressing the test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> has(Function<T, U> property, String name, Predicate<U> test) throws E {
    U value = property.apply(ok());
    if (test.test(value)) {
      return this;
    }
    String msg = createMessage(test, false, fqn(name), value);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * ducks the test expressed through the specified {@code Predicate}. Although not required this
   * method is meant to be used with a {@code Predicate} from the {{@link CommonChecks} class so
   * that an informative error message is generated upon failure.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test A {@code Predicate} expressing the test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> notHas(Function<T, U> property, String name, Predicate<U> test) throws E {
    U value = property.apply(ok());
    if (!test.test(value)) {
      return this;
    }
    String msg = createMessage(test, true, fqn(name), value);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the test expressed through the specified {@code Predicate}. Although not required this
   * method is meant to be used with a {@code Predicate} from the {@link CommonChecks} class
   * <i>and</i> a {@code Function} from the {@link CommonGetters} class so that an informative error
   * message is generated upon failure.
   *
   * @param <U> The type of the property
   * @param name A function which is given the argument as input and returns the value to be tested
   * @param test A {@code Predicate} expressing the test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> has(Function<T, U> property, Predicate<U> test) throws E {
    U value = property.apply(ok());
    if (test.test(value)) {
      return this;
    }
    String name = formatProperty(argName, property);
    String msg = createMessage(test, false, name, value);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * ducks the test expressed through the specified {@code Predicate}. Although not required this
   * method is meant to be used with a {@code Predicate} from the {@link CommonChecks} class
   * <i>and</i> a {@code Function} from the {@link CommonGetters} class so that an informative error
   * message is generated upon failure.
   *
   * @param <U> The type of the property
   * @param name A function which is given the argument as input and returns the value to be tested
   * @param test A {@code Predicate} expressing the test
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> notHas(Function<T, U> property, Predicate<U> test) throws E {
    U value = property.apply(ok());
    if (!test.test(value)) {
      return this;
    }
    String name = formatProperty(argName, property);
    String msg = createMessage(test, true, name, value);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the test expressed through the specified {@code Predicate}. Allows you to provide a
   * custom error message.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> has(
      Function<T, U> property, Predicate<U> test, String message, Object... msgArgs) throws E {
    U value = property.apply(ok());
    if (test.test(value)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * ducks the test expressed through the specified {@code Predicate}. Allows you to provide a
   * custom error message.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public <U> Check<T, E> notHas(
      Function<T, U> property, Predicate<U> test, String message, Object... msgArgs) throws E {
    return has(property, test.negate(), message, msgArgs);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, passes the test expressed through the specified {@code Predicate}. Although not
   * required this method is meant to be used with an {@code IntPredicate} from the {@link
   * CommonChecks} class so that an informative error message is generated upon failure.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> has(ToIntFunction<T> property, String name, IntPredicate test) throws E {
    int value = property.applyAsInt(ok());
    if (test.test(value)) {
      return this;
    }
    String msg = createMessage(test, false, fqn(name), value);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, ducks the test expressed through the specified {@code Predicate}. Although not
   * required this method is meant to be used with an {@code IntPredicate} from the {@link
   * CommonChecks} class so that an informative error message is generated upon failure.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> notHas(ToIntFunction<T> property, String name, IntPredicate test) throws E {
    int value = property.applyAsInt(ok());
    if (!test.test(value)) {
      return this;
    }
    String msg = createMessage(test, true, fqn(name), value);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, passes the test expressed through the specified {@code IntPredicate}. Although
   * not required this method is meant to be used with an {@code IntPredicate} from the {@link
   * CommonChecks} class <i>and</i> a {@code ToIntFunction} from the {@link CommonGetters} class so
   * that an informative error message is generated upon failure.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> has(ToIntFunction<T> property, IntPredicate test) throws E {
    int value = property.applyAsInt(ok());
    if (test.test(value)) {
      return this;
    }
    String name = formatProperty(argName, property);
    String msg = createMessage(test, false, name, value);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, ducks the test expressed through the specified {@code IntPredicate}. Although
   * not required this method is meant to be used with an {@code IntPredicate} from the {@link
   * CommonChecks} class <i>and</i> a {@code ToIntFunction} from the {@link CommonGetters} class so
   * that an informative error message is generated upon failure.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> notHas(ToIntFunction<T> property, IntPredicate test) throws E {
    int value = property.applyAsInt(ok());
    if (!test.test(value)) {
      return this;
    }
    String name = formatProperty(argName, property);
    String msg = createMessage(test, true, name, value);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, passes the test expressed through the specified {@code Predicate}. Allows you
   * to provide a custom error message.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> has(
      ToIntFunction<T> property, IntPredicate test, String message, Object... msgArgs) throws E {
    int value = property.applyAsInt(ok());
    if (test.test(value)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, ducks the test expressed through the specified {@code Predicate}. Allows you to
   * provide a custom error message.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the test fails
   */
  public Check<T, E> notHas(
      ToIntFunction<T> property, IntPredicate test, String message, Object... msgArgs) throws E {
    return has(property, test.negate(), message, msgArgs);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the test expressed through the specified {@code Predicate}. Although not required this
   * method is meant to be used with a {@code Relation} from the {@link CommonChecks} class so that
   * an informative error message is generated upon failure.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U, V> Check<T, E> has(Function<T, U> property, String name, Relation<U, V> test, V object)
      throws E {
    U value = property.apply(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * ducks the test expressed through the specified {@code Relation}. Although not required this
   * method is meant to be used with a {@code Relation} from the {@link CommonChecks} class so that
   * an informative error message is generated upon failure.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U, V> Check<T, E> notHas(
      Function<T, U> property, String name, Relation<U, V> test, V object) throws E {
    U value = property.apply(ok());
    if (!test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the test expressed through the specified {@code Relation}. Although not required this
   * method is meant to be used with a {@code Relation} from the {@link CommonChecks} class
   * <i>and</i> a {@code Function} from the {@link CommonGetters} class so that an informative error
   * message is generated upon failure.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U, V> Check<T, E> has(Function<T, U> property, Relation<U, V> test, V object) throws E {
    U value = property.apply(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(argName, property);
    String msg = createMessage(test, false, name, value, object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * ducks the test expressed through the specified {@code Relation}. Although not required this
   * method is meant to be used with a {@code Relation} from the {@link CommonChecks} class
   * <i>and</i> a {@code Function} from the {@link CommonGetters} class so that an informative error
   * message is generated upon failure.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U, V> Check<T, E> notHas(Function<T, U> property, Relation<U, V> test, V object)
      throws E {
    U value = property.apply(ok());
    if (!test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(argName, property);
    String msg = createMessage(test, true, name, value, object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the test expressed through the specified {@code Predicate}. Allows you to provide a
   * custom error message.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U, V> Check<T, E> has(
      Function<T, U> property, Relation<U, V> test, V object, String message, Object... msgArgs)
      throws E {
    U value = property.apply(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * ducks the test expressed through the specified {@code Predicate}. Allows you to provide a
   * custom error message.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U, V> Check<T, E> notHas(
      Function<T, U> property, Relation<U, V> test, V object, String message, Object... msgArgs)
      throws E {
    return has(property, test.negate(), object, message, msgArgs);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the test expressed through the specified {@code ObjIntRelation}. Although not required
   * this method is meant to be used with the {@link CommonChecks} class so that an informative
   * error message is generated upon failure.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> Check<T, E> has(
      Function<T, U> property, String name, ObjIntRelation<U> test, int object) throws E {
    U value = property.apply(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * ducks the test expressed through the specified {@code ObjIntRelation}. Although not required
   * this method is meant to be used with the {@link CommonChecks} class so that an informative
   * error message is generated upon failure.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> Check<T, E> notHas(
      Function<T, U> property, String name, ObjIntRelation<U> test, int object) throws E {
    U value = property.apply(ok());
    if (!test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the test expressed through the specified {@code ObjIntRelation}. Although not required
   * this method is meant to be used with a {@code ObjIntRelation} from the {@link CommonChecks}
   * class <i>and</i> a {@code Function} from the {@link CommonGetters} class so that an informative
   * error message is generated upon failure.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> Check<T, E> has(Function<T, U> property, ObjIntRelation<U> test, int object) throws E {
    U value = property.apply(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(argName, property);
    String msg = createMessage(test, false, name, value, object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * ducks the test expressed through the specified {@code ObjIntRelation}. Although not required
   * this method is meant to be used with a {@code ObjIntRelation} from the {@link CommonChecks}
   * class <i>and</i> a {@code Function} from the {@link CommonGetters} class so that an informative
   * error message is generated upon failure.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> Check<T, E> notHas(Function<T, U> property, ObjIntRelation<U> test, int object)
      throws E {
    U value = property.apply(ok());
    if (!test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(argName, property);
    String msg = createMessage(test, true, name, value, object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified function, passes the
   * test expressed through the specified {@code ObjIntRelation}. Allows you to provide a custom
   * error message.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> Check<T, E> has(
      Function<T, U> property,
      ObjIntRelation<U> test,
      int object,
      String message,
      Object... msgArgs)
      throws E {
    U value = property.apply(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified function, ducks the
   * test expressed through the specified {@code ObjIntRelation}. Allows you to provide a custom
   * error message.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> Check<T, E> notHas(
      Function<T, U> property,
      ObjIntRelation<U> test,
      int object,
      String message,
      Object... msgArgs)
      throws E {
    return has(property, test, object, message, msgArgs);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, passes the test expressed through the specified {@code IntRelation}. Although
   * not required this method is meant to be used with the {@link CommonChecks} class so that an
   * informative error message is generated upon failure.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public Check<T, E> has(ToIntFunction<T> property, String name, IntRelation test, int object)
      throws E {
    int value = property.applyAsInt(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, ducks the test expressed through the specified {@code IntRelation}. Although
   * not required this method is meant to be used with the {@link CommonChecks} class so that an
   * informative error message is generated upon failure.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public Check<T, E> notHas(ToIntFunction<T> property, String name, IntRelation test, int object)
      throws E {
    int value = property.applyAsInt(ok());
    if (!test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, passes the test expressed through the specified {@code IntRelation}. Although
   * not required this method is meant to be used with a {@code IntRelation} from the {@link
   * CommonChecks} class <i>and</i> a {@code ToIntFunction} from the {@link CommonGetters} class so
   * that an informative error message is generated upon failure.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public Check<T, E> has(ToIntFunction<T> property, IntRelation test, int object) throws E {
    int value = property.applyAsInt(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(argName, property);
    String msg = createMessage(test, false, name, value, object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, ducks the test expressed through the specified {@code IntRelation}. Although
   * not required this method is meant to be used with a {@code IntRelation} from the {@link
   * CommonChecks} class <i>and</i> a {@code ToIntFunction} from the {@link CommonGetters} class so
   * that an informative error message is generated upon failure.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public Check<T, E> notHas(ToIntFunction<T> property, IntRelation test, int object) throws E {
    int value = property.applyAsInt(ok());
    if (!test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(argName, property);
    String msg = createMessage(test, true, name, value, object);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified function, passes the
   * test expressed through the specified {@code IntRelation}. Allows you to provide a custom error
   * message.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public Check<T, E> has(
      ToIntFunction<T> property, IntRelation test, int object, String message, Object... msgArgs)
      throws E {
    int value = property.applyAsInt(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified function, ducks the
   * test expressed through the specified {@code IntRelation}. Allows you to provide a custom error
   * message.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @param message The error message
   * @param msgArgs The message arguments
   * @return This {@code Check} object
   * @throws E If the specified test does not exist between subject and object
   */
  public Check<T, E> notHas(
      ToIntFunction<T> property, IntRelation test, int object, String message, Object... msgArgs)
      throws E {
    return has(property, test.negate(), object, message, msgArgs);
  }

  /**
   * Imposes extra preconditions that may or may not be related to the argument being checked by
   * this instance. For example a precondition check for a typical {@link OutputStream#write(byte[],
   * int, int) OutputStream.write(b, off, len)} implementation might look like this:
   *
   * <p>
   *
   * <pre>
   * write(byte[] b, int off, int len) {
   *   Check.notNull(b, "b").has(length(), gte(), off + len).given(off >= 0, len >= 0);
   *   // ...
   * }
   * </pre>
   *
   * @param conditions The conditions to evaluate
   * @return This {@code Check} object
   * @throws E If any of the conditions evaluate to false
   */
  public Check<T, E> given(boolean... conditions) throws E {
    if (conditions == null || conditions.length == 0) {
      throw new InvalidCheckException("No conditions provided");
    }
    for (int i = 0; i < conditions.length; ++i) {
      if (!conditions[i]) {
        String s = argName.equals(DEF_ARG_NAME) ? " " : argName + " ";
        String msg = String.format("Argument %snot valid given condition %d", s, i + 1);
        throw excFactory.apply(msg);
      }
    }
    return this;
  }

  /**
   * Imposes extra preconditions that may or may not be related to the argument being checked by
   * this instance. Lazy but practical. For example a precondition check for a typical {@link
   * OutputStream#write(byte[], int, int) OutputStream.write(b, off, len)} implementation might look
   * like:
   *
   * <p>
   *
   * <pre>
   * write(byte[] b, int off, int len) {
   *   Check.notNull(b, "b").has(length(), gte(), off + len).given("No can do buddy: check condition %d", off >= 0, len >= 0);
   *   // ...
   * }
   * </pre>
   *
   * @param message A custom message that may (but does not have to) contain one %d message
   *     argument. The message argument is substituted with the one-based number of the first
   *     condition that failed.
   * @param conditions The conditions to evaluate
   * @return This {@code Check} object
   * @throws E If any of the conditions evaluate to false
   */
  public Check<T, E> given(String message, boolean... conditions) throws E {
    if (conditions == null || conditions.length == 0) {
      throw new InvalidCheckException("No conditions provided");
    }
    for (int i = 0; i < conditions.length; ++i) {
      if (!conditions[i]) {
        String msg = String.format(message, i + 1);
        throw excFactory.apply(msg);
      }
    }
    return this;
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
   * Passes the argument to the specified {@code Function} and returns the value it computes. To be
   * used as the last call after a chain of checks.
   *
   * @param <U> The type of the returned value
   * @param transformer A {@code Function} that transforms the argument into some other value
   * @return The value computed by the {@code Function}
   * @throws F The exception potentially thrown by the {@code Function}
   */
  public <U, F extends Throwable> U ok(ThrowingFunction<T, U, F> transformer) throws F {
    return transformer.apply(ok());
  }

  /**
   * Passes the validated argument to a {@code Consumer} to be processed safely. To be used as the
   * last call after a chain of checks.
   *
   * @param consumer The {@code Consumer}
   */
  public <F extends Throwable> void then(ThrowingConsumer<T, F> consumer) throws F {
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

  /* Returns fully-qualified name of the property with the specified name */
  private String fqn(String name) {
    return argName + "." + name;
  }
}
