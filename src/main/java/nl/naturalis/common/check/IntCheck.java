package nl.naturalis.common.check;

import nl.naturalis.common.function.*;

import java.util.function.*;
import java.util.stream.IntStream;

import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.check.Check.DEF_ARG_NAME;
import static nl.naturalis.common.check.CommonChecks.NAMES;
import static nl.naturalis.common.check.Messages.createMessage;

/**
 * Facilitates the validation of {@code int} values. See the {@link nl.naturalis.common.check
 * package description} for more details.
 *
 * @param <E> The type of the exception throw if the argument turns out to be invalid
 */
public final class IntCheck<E extends Exception> {

  final int arg;
  final String argName;
  final Function<String, E> exc;

  IntCheck(int arg, String argName, Function<String, E> exc) {
    this.arg = arg;
    this.argName = argName;
    this.exc = exc;
  }

  /**
   * Returns the argument. To be used as the last call after a chain of checks.
   *
   * @return The argument
   */
  public int ok() {
    return arg;
  }

  /**
   * Passes the argument to the specified {@code Function} and returns the value it computes. To be
   * used as the last call after a chain of checks. For example:
   *
   * <blockquote>
   *
   * <pre>{@code
   * int age = Check.that(person).has(Person::getAge, "age", lt(), 50).ok(Person::getAge);
   * }</pre>
   *
   * </blockquote>
   *
   * @param <U> The type of the returned value
   * @param transformer A {@code Function} that transforms the argument into some other value
   * @return The value computed by the {@code Function}
   * @throws F The exception potentially thrown by the {@code Function}
   */
  public <U, F extends Throwable> U ok(ThrowingIntFunction<U, F> transformer) throws F {
    return transformer.apply(arg);
  }

  /**
   * Passes the argument to the specified {@code Consumer}. To be used as the last call after a
   * chain of checks.
   *
   * @param consumer The {@code Consumer}
   */
  public <F extends Throwable> void then(ThrowingIntConsumer<F> consumer) throws F {
    consumer.accept(arg);
  }

  /**
   * Validates the argument using the specified test. While not strictly required, this method is
   * meant to be used with a check from the {@link CommonChecks} class so that an informative error
   * message is generated if the argument turns out to be invalid.
   *
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> is(IntPredicate test) throws E {
    if (test.test(arg)) {
      return this;
    }
    String msg = createMessage(test, false, getArgName(arg), ok());
    throw exc.apply(msg);
  }

  /**
   * Validates the argument using the specified test. While not strictly required, this method is
   * meant to be used with a check from the {@link CommonChecks} class so that an informative error
   * message is generated if the argument turns out to be invalid.
   *
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> isNot(IntPredicate test) throws E {
    if (!test.test(arg)) {
      return this;
    }
    String msg = createMessage(test, true, argName, ok());
    throw exc.apply(msg);
  }

  /**
   * Validates the argument using the specified test. Allows you to provide a custom error message.
   * See the {@link nl.naturalis.common.check package description} for how to specify a custom error
   * message.
   *
   * @param test The test
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> is(IntPredicate test, String message, Object... msgArgs) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw exception(test, message, msgArgs);
  }

  /**
   * Validates the argument using the specified test. Allows you to provide a custom error message.
   * See the {@link nl.naturalis.common.check package description} for how to specify a custom error
   * message.
   *
   * @param test The test
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> isNot(IntPredicate test, String message, Object... msgArgs) throws E {
    return is(test.negate(), message, msgArgs);
  }

  /**
   * Validates the argument using the specified test. Allows you to throw a different type of
   * exception for this particular test.
   *
   * @param test The test
   * @param exception The {@code Supplier} of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> IntCheck<E> is(IntPredicate test, Supplier<X> exception) throws X {
    if (test.test(arg)) {
      return this;
    }
    throw exception.get();
  }

  /**
   * Validates the argument using the specified test.
   *
   * @see #is(IntPredicate, Supplier)
   * @param test The test
   * @param exception The {@code Supplier} of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> IntCheck<E> isNot(IntPredicate test, Supplier<X> exception)
      throws X {
    return is(test.negate(), exception);
  }

  /**
   * Validates the argument using the specified test. While not strictly required, this method is
   * meant to be used in combination with a check from the {@link CommonChecks} class so that an
   * informative error message is generated if the argument turns out to be invalid.
   *
   * @param test The test
   * @param object The object of the {@code Relation}
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> is(IntRelation test, int object) throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, false, getArgName(arg), arg, object);
    throw exc.apply(msg);
  }

  /**
   * Validates the argument using the specified test. While not strictly required, this method is
   * meant to be used in combination with a check from the {@link CommonChecks} class so that an
   * informative error message is generated if the argument turns out to be invalid.
   *
   * @param test The test
   * @param object The object of the {@code Relation}
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> isNot(IntRelation test, int object) throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, true, getArgName(arg), arg, object);
    throw exc.apply(msg);
  }

  /**
   * Validates the argument using the specified test. Allows you to provide a custom error message.
   * See the {@link nl.naturalis.common.check package description} for how to specify a custom error
   * message.
   *
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> is(IntRelation test, int object, String message, Object... msgArgs) throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw exc.apply(msg);
  }

  /**
   * Validates the argument using the specified test. Allows you to provide a custom error message.
   * See the {@link nl.naturalis.common.check package description} for how to specify a custom error
   * message.
   *
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> isNot(IntRelation test, int object, String message, Object... msgArgs)
      throws E {
    return is(test.negate(), object, message, msgArgs);
  }

  /**
   * Validates the argument using the specified test. Allows you to throw a different type of
   * exception for this particular test.
   *
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param exception The {@code Supplier} of the exception to be thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <U, X extends Exception> IntCheck<E> is(
      IntRelation test, int object, Supplier<X> exception) throws X {
    if (test.exists(arg, object)) {
      return this;
    }
    throw exception.get();
  }

  /**
   * Validates the argument using the specified test. Allows you to throw a different type of
   * exception for this particular test.
   *
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param exception The {@code Supplier} of the exception to be thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <U, X extends Exception> IntCheck<E> isNot(
      IntRelation test, int object, Supplier<X> exception) throws X {
    return is(test.negate(), object, exception);
  }

  /**
   * Validates the argument using the specified test. While not strictly required, this method is
   * meant to be used in combination with a check from the {@link CommonChecks} class so that an
   * informative error message is generated if the argument turns out to be invalid.
   *
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param <U> The type of the object of the {@code IntObjRelation}
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> is(IntObjRelation<U> test, U object) throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, false, getArgName(arg), arg, object);
    throw exc.apply(msg);
  }

  /**
   * Validates the argument using the specified test. While not strictly required, this method is
   * meant to be used in combination with a check from the {@link CommonChecks} class so that an
   * informative error message is generated if the argument turns out to be invalid.
   *
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> isNot(IntObjRelation<U> test, U object) throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, true, argName, getArgName(arg), object);
    throw exc.apply(msg);
  }

  /**
   * Validates the argument using the specified test. Allows you to provide a custom error message.
   * See the {@link nl.naturalis.common.check package description} for how to specify a custom error
   * message.
   *
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> is(IntObjRelation<U> test, U object, String message, Object... msgArgs)
      throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw exc.apply(msg);
  }

  /**
   * Validates the argument using the specified test. Allows you to provide a custom error message.
   * See the {@link nl.naturalis.common.check package description} for how to specify a custom error
   * message.
   *
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> isNot(IntObjRelation<U> test, U object, String message, Object... msgArgs)
      throws E {
    return is(test.negate(), object, message, msgArgs);
  }

  /**
   * Validates the argument using the specified test. Allows you to throw a different type of
   * exception for this particular test.
   *
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param exception The {@code Supplier} of the exception to be thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <U, X extends Exception> IntCheck<E> is(
      IntObjRelation<U> test, U object, Supplier<X> exception) throws X {
    if (test.exists(arg, object)) {
      return this;
    }
    throw exception.get();
  }

  /**
   * Validates the argument using the specified test. Allows you to throw a different type of
   * exception for this particular test.
   *
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param exception The {@code Supplier} of the exception to be thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <U, X extends Exception> IntCheck<E> isNot(
      IntObjRelation<U> test, U object, Supplier<X> exception) throws X {
    return is(test.negate(), object, exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class <i>and</i> a "getter" from the {@link
   * CommonGetters} class so that an informative error message is generated if the argument turns
   * out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param <U> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> has(IntFunction<U> property, Predicate<U> test) throws E {
    return IntHasObj.get(this).has(property, test);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class <i>and</i> a "getter" from the {@link
   * CommonGetters} class so that an informative error message is generated if the argument turns
   * out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param <U> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> notHas(IntFunction<U> property, Predicate<U> test) throws E {
    return IntHasObj.get(this).notHas(property, test);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property
   * @param test The test
   * @param <U> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> has(IntFunction<U> property, String name, Predicate<U> test) throws E {
    return IntHasObj.get(this).has(property, name, test);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property
   * @param test The test
   * @param <U> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> notHas(IntFunction<U> property, String name, Predicate<U> test) throws E {
    return IntHasObj.get(this).has(property, name, test);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <U> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> has(
      IntFunction<U> property, Predicate<U> test, String message, Object... msgArgs) throws E {
    return IntHasObj.get(this).has(property, test, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <U> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> notHas(
      IntFunction<U> property, Predicate<U> test, String message, Object... msgArgs) throws E {
    return has(property, test.negate(), message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param exception
   * @param <U> The type of the extracted value
   * @param <X> The type of the exception potentially being thrown
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <U, X extends Exception> IntCheck<E> has(
      IntFunction<U> property, Predicate<U> test, Supplier<X> exception) throws X {
    return IntHasObj.get(this).has(property, test, exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param exception
   * @param <U> The type of the extracted value
   * @param <X> The type of the exception potentially being thrown
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <U, X extends Exception> IntCheck<E> notHas(
      IntFunction<U> property, Predicate<U> test, Supplier<X> exception) throws X {
    return has(property, test.negate(), exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property
   * @param test The test
   * @param object The object of the relationship
   * @param <U> The type of the extracted value
   * @param <V> The type of the object of the relationship
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U, V> IntCheck<E> has(IntFunction<U> property, String name, Relation<U, V> test, V object)
      throws E {
    return IntHasObj.get(this).has(property, name, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property
   * @param test The test
   * @param object The object of the relationship
   * @param <U> The type of the extracted value
   * @param <V> The type of the object of the relationship
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U, V> IntCheck<E> notHas(
      IntFunction<U> property, String name, Relation<U, V> test, V object) throws E {
    return IntHasObj.get(this).notHas(property, name, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class <i>and</i> a "getter" from the {@link
   * CommonGetters} class so that an informative error message is generated if the argument turns
   * out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The object of the relationship
   * @param <U> The type of the extracted value
   * @param <V> The type of the object of the relationship
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U, V> IntCheck<E> has(IntFunction<U> property, Relation<U, V> test, V object) throws E {
    return IntHasObj.get(this).has(property, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class <i>and</i> a "getter" from the {@link
   * CommonGetters} class so that an informative error message is generated if the argument turns
   * out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The object of the relationship
   * @param <U> The type of the extracted value
   * @param <V> The type of the object of the relationship
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U, V> IntCheck<E> notHas(IntFunction<U> property, Relation<U, V> test, V object)
      throws E {
    return IntHasObj.get(this).notHas(property, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The object of the relationship
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <U> The type of the extracted value
   * @param <V> The type of the object of the relationship
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U, V> IntCheck<E> has(
      IntFunction<U> property, Relation<U, V> test, V object, String message, Object... msgArgs)
      throws E {
    return IntHasObj.get(this).has(property, test, object, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The object of the relationship
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <U> The type of the extracted value
   * @param <V> The type of the object of the relationship
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U, V> IntCheck<E> notHas(
      IntFunction<U> property, Relation<U, V> test, V object, String message, Object... msgArgs)
      throws E {
    return has(property, test.negate(), object, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The object of the relationship
   * @param exception
   * @param <U> The type of the extracted value
   * @param <V> The type of the object of the relationship
   * @param <X> The type of the exception potentially being thrown
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <U, V, X extends Exception> IntCheck<E> has(
      IntFunction<U> property, Relation<U, V> test, V object, Supplier<X> exception) throws X {
    return IntHasObj.get(this).has(property, test, object, exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The object of the relationship
   * @param exception
   * @param <U> The type of the extracted value
   * @param <V> The type of the object of the relationship
   * @param <X> The type of the exception potentially being thrown
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <U, V, X extends Exception> IntCheck<E> notHas(
      IntFunction<U> property, Relation<U, V> test, V object, Supplier<X> exception) throws X {
    return has(property, test.negate(), object, exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class <i>and</i> a "getter" from the {@link
   * CommonGetters} class so that an informative error message is generated if the argument turns
   * out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> has(IntUnaryOperator property, IntPredicate test) throws E {
    return IntHasInt.get(this).has(property, test);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class <i>and</i> a "getter" from the {@link
   * CommonGetters} class so that an informative error message is generated if the argument turns
   * out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> notHas(IntUnaryOperator property, IntPredicate test) throws E {
    return IntHasInt.get(this).notHas(property, test);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> has(IntUnaryOperator property, String name, IntPredicate test) throws E {
    return IntHasInt.get(this).has(property, name, test);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> notHas(IntUnaryOperator property, String name, IntPredicate test) throws E {
    return IntHasInt.get(this).notHas(property, name, test);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> has(
      IntUnaryOperator property, IntPredicate test, String message, Object... msgArgs) throws E {
    return IntHasInt.get(this).has(property, test, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> notHas(
      IntUnaryOperator property, IntPredicate test, String message, Object... msgArgs) throws E {
    return has(property, test.negate(), message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param exception
   * @param <X> The type of the exception potentially being thrown
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> IntCheck<E> has(
      IntUnaryOperator property, IntPredicate test, Supplier<X> exception) throws X {
    return IntHasInt.get(this).has(property, test, exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param exception
   * @param <X> The type of the exception potentially being thrown
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> IntCheck<E> notHas(
      IntUnaryOperator property, IntPredicate test, Supplier<X> exception) throws X {
    return has(property, test.negate(), exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class <i>and</i> a "getter" from the {@link
   * CommonGetters} class so that an informative error message is generated if the argument turns
   * out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> has(IntUnaryOperator property, IntRelation test, int object) throws E {
    return IntHasInt.get(this).has(property, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class <i>and</i> a "getter" from the {@link
   * CommonGetters} class so that an informative error message is generated if the argument turns
   * out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> notHas(IntUnaryOperator property, IntRelation test, int object) throws E {
    return IntHasInt.get(this).notHas(property, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property
   * @param test The test
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> has(IntUnaryOperator property, String name, IntRelation test, int object)
      throws E {
    return IntHasInt.get(this).has(property, name, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property
   * @param test The test
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> notHas(IntUnaryOperator property, String name, IntRelation test, int object)
      throws E {
    return IntHasInt.get(this).notHas(property, name, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The object of the relationship
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> has(
      IntUnaryOperator property, IntRelation test, int object, String message, Object... msgArgs)
      throws E {
    return IntHasInt.get(this).has(property, test, object, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The object of the relationship
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> notHas(
      IntUnaryOperator property, IntRelation test, int object, String message, Object... msgArgs)
      throws E {
    return has(property, test.negate(), object, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The object of the relationship
   * @param exception
   * @param <X> The type of the exception potentially being thrown
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> IntCheck<E> has(
      IntUnaryOperator property, IntRelation test, int object, Supplier<X> exception) throws X {
    return IntHasInt.get(this).has(property, test, object, exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The object of the relationship
   * @param exception
   * @param <X> The type of the exception potentially being thrown
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> IntCheck<E> notHas(
      IntUnaryOperator property, IntRelation test, int object, Supplier<X> exception) throws X {
    return has(property, test.negate(), object, exception);
  }

  E exception(Object test, String msg, Object[] msgArgs) {
    return exception(test, null, msg, msgArgs);
  }

  E exception(Object test, Object object, String msg, Object[] msgArgs) {
    return exception(test, ok(), object, msg, msgArgs);
  }

  E exception(Object test, Object subject, Object object, String pattern, Object[] msgArgs) {
    if (pattern == null) {
      throw new InvalidCheckException("message must not be null");
    }
    if (msgArgs == null) {
      throw new InvalidCheckException("message arguments must not be null");
    }
    String fmt = FormatNormalizer.normalize(pattern);
    Object[] all = new Object[msgArgs.length + 5];
    all[0] = NAMES.getOrDefault(test, test.getClass().getSimpleName());
    all[1] = Messages.toStr(subject);
    all[2] = ifNotNull(subject, IntCheck::className);
    all[3] = argName;
    all[4] = Messages.toStr(object);
    System.arraycopy(msgArgs, 0, all, 5, msgArgs.length);
    return exc.apply(String.format(fmt, all));
  }

  String getArgName(int arg) {
    return argName != null ? argName : int.class.getSimpleName();
  }

  private static String className(Object obj) {
    Class<?> clazz = obj.getClass();
    if (clazz.isArray()) {
      Class<?> c = clazz.getComponentType();
      int i = 0;
      for (; c.isArray(); c = c.getComponentType()) {
        ++i;
      }
      StringBuilder sb = new StringBuilder(c.getSimpleName());
      IntStream.rangeClosed(0, i).forEach(x -> sb.append("[]"));
      return sb.toString();
    }
    return clazz.getSimpleName();
  }

  /* Returns fully-qualified name of the property with the specified name */
  private String fqn(String name) {
    return argName + "." + name;
  }
}
