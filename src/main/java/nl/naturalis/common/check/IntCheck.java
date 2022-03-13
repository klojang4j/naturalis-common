package nl.naturalis.common.check;

import nl.naturalis.common.function.*;

import java.util.function.*;

import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.check.CommonChecks.NAMES;
import static nl.naturalis.common.check.MsgUtil.getMessage;

/**
 * Facilitates the validation of {@code int} values. See the {@link nl.naturalis.common.check
 * package description} for more details.
 *
 * @param <E> The type of the exception throw if the argument turns out to be invalid
 */
public final class IntCheck<E extends Exception> {

  final int arg;
  final String argName;

  private final Function<String, E> exc;

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
   * @param <P> The type of the returned value
   * @param transformer A {@code Function} that transforms the argument into some other value
   * @return The value computed by the {@code Function}
   * @throws F The exception potentially thrown by the {@code Function}
   */
  public <P, F extends Throwable> P ok(ThrowingIntFunction<P, F> transformer) throws F {
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
    throw exc.apply(getMessage(test, false, getArgName(), arg, int.class));
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
    throw exc.apply(getMessage(test, true, argName, arg, int.class));
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
    throw createException(test, message, msgArgs);
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
    // WATCH OUT. Don't call: is(test.negate(), message, msgArgs)
    // If the test came from the CommonChecks class it must preserve its identity
    // in order to be looked up in the CommonChecks.NAMES map
    if (!test.test(arg)) {
      return this;
    }
    throw createException(test, message, msgArgs);
  }

  /**
   * Validates the argument using the specified test. Allows you to throw a different type of
   * exception for this particular test.
   *
   * @param test The test
   * @param exception The supplier of the exception to be thrown if the argument is invalid The
   *     {@code Supplier} of the exception to be thrown if the argument is invalid
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
   * Validates the argument using the specified test. Allows you to throw a different type of
   * exception for this particular test.
   *
   * @param test The test
   * @param exception The supplier of the exception to be thrown if the argument is invalid The
   *     {@code Supplier} of the exception to be thrown if the argument is invalid
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
    throw exc.apply(getMessage(test, false, getArgName(), arg, int.class, object));
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
    throw exc.apply(getMessage(test, true, getArgName(), arg, int.class, object));
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
    throw createException(test, object, message, msgArgs);
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
    if (!test.exists(arg, object)) {
      return this;
    }
    throw createException(test, object, message, msgArgs);
  }

  /**
   * Validates the argument using the specified test. Allows you to throw a different type of
   * exception for this particular test.
   *
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param exception The supplier of the exception to be thrown if the argument is invalid The
   *     {@code Supplier} of the exception to be thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <P, X extends Exception> IntCheck<E> is(
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
   * @param exception The supplier of the exception to be thrown if the argument is invalid The
   *     {@code Supplier} of the exception to be thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <P, X extends Exception> IntCheck<E> isNot(
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
   * @param <O> The type of the object of the {@code IntObjRelation}
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <O> IntCheck<E> is(IntObjRelation<O> test, O object) throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    throw exc.apply(getMessage(test, false, getArgName(), arg, int.class, object));
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
  public <O> IntCheck<E> isNot(IntObjRelation<O> test, O object) throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    throw exc.apply(getMessage(test, true, getArgName(), arg, int.class, object));
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
  public <O> IntCheck<E> is(IntObjRelation<O> test, O object, String message, Object... msgArgs)
      throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    throw createException(test, object, message, msgArgs);
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
  public <O> IntCheck<E> isNot(IntObjRelation<O> test, O object, String message, Object... msgArgs)
      throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    throw createException(test, object, message, msgArgs);
  }

  /**
   * Validates the argument using the specified test. Allows you to throw a different type of
   * exception for this particular test.
   *
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param exception The supplier of the exception to be thrown if the argument is invalid The
   *     {@code Supplier} of the exception to be thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <O, X extends Exception> IntCheck<E> is(
      IntObjRelation<O> test, O object, Supplier<X> exception) throws X {
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
   * @param exception The supplier of the exception to be thrown if the argument is invalid The
   *     {@code Supplier} of the exception to be thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <O, X extends Exception> IntCheck<E> isNot(
      IntObjRelation<O> test, O object, Supplier<X> exception) throws X {
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
   * @param <P> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P> IntCheck<E> has(IntFunction<P> property, Predicate<P> test) throws E {
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
   * @param <P> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P> IntCheck<E> notHas(IntFunction<P> property, Predicate<P> test) throws E {
    return IntHasObj.get(this).notHas(property, test);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param <P> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P> IntCheck<E> has(IntFunction<P> property, String name, Predicate<P> test) throws E {
    return IntHasObj.get(this).has(property, name, test);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param <P> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P> IntCheck<E> notHas(IntFunction<P> property, String name, Predicate<P> test) throws E {
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
   * @param <P> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P> IntCheck<E> has(
      IntFunction<P> property, Predicate<P> test, String message, Object... msgArgs) throws E {
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
   * @param <P> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P> IntCheck<E> notHas(
      IntFunction<P> property, Predicate<P> test, String message, Object... msgArgs) throws E {
    return IntHasObj.get(this).notHas(property, test, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <P> The type of the extracted value
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <P, X extends Exception> IntCheck<E> has(
      IntFunction<P> property, Predicate<P> test, Supplier<X> exception) throws X {
    return IntHasObj.get(this).has(property, test, exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <P> The type of the extracted value
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <P, X extends Exception> IntCheck<E> notHas(
      IntFunction<P> property, Predicate<P> test, Supplier<X> exception) throws X {
    return IntHasObj.get(this).has(property, test.negate(), exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P, O> IntCheck<E> has(IntFunction<P> property, String name, Relation<P, O> test, O object)
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
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P, O> IntCheck<E> notHas(
      IntFunction<P> property, String name, Relation<P, O> test, O object) throws E {
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
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P, O> IntCheck<E> has(IntFunction<P> property, Relation<P, O> test, O object) throws E {
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
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P, O> IntCheck<E> notHas(IntFunction<P> property, Relation<P, O> test, O object)
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
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P, O> IntCheck<E> has(
      IntFunction<P> property, Relation<P, O> test, O object, String message, Object... msgArgs)
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
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P, O> IntCheck<E> notHas(
      IntFunction<P> property, Relation<P, O> test, O object, String message, Object... msgArgs)
      throws E {
    return IntHasObj.get(this).notHas(property, test, object, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <P, O, X extends Exception> IntCheck<E> has(
      IntFunction<P> property, Relation<P, O> test, O object, Supplier<X> exception) throws X {
    return IntHasObj.get(this).has(property, test, object, exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <P, O, X extends Exception> IntCheck<E> notHas(
      IntFunction<P> property, Relation<P, O> test, O object, Supplier<X> exception) throws X {
    return IntHasObj.get(this).has(property, test.negate(), object, exception);
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
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
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
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
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
    return IntHasInt.get(this).notHas(property, test, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception thrown if the argument is invalid
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
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> IntCheck<E> notHas(
      IntUnaryOperator property, IntPredicate test, Supplier<X> exception) throws X {
    return IntHasInt.get(this).has(property, test.negate(), exception);
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
   * @param object The value that the argument is tested against (called "the object" of a relation)
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
   * @param object The value that the argument is tested against (called "the object" of a relation)
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
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
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
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
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
   * @param object The value that the argument is tested against (called "the object" of a relation)
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
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> notHas(
      IntUnaryOperator property, IntRelation test, int object, String message, Object... msgArgs)
      throws E {
    return IntHasInt.get(this).notHas(property, test, object, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception thrown if the argument is invalid
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
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> IntCheck<E> notHas(
      IntUnaryOperator property, IntRelation test, int object, Supplier<X> exception) throws X {
    return has(property, test.negate(), object, exception);
  }

  E createException(String msg) {
    return exc.apply(msg);
  }

  E createException(Object test, String msg, Object[] msgArgs) {
    return createException(test, null, msg, msgArgs);
  }

  E createException(Object test, Object object, String msg, Object[] msgArgs) {
    return createException(test, arg, object, msg, msgArgs);
  }

  E createException(Object test, Object arg, Object obj, String pattern, Object[] msgArgs) {
    if (pattern == null) {
      throw new InvalidCheckException("message pattern must not be null");
    }
    if (msgArgs == null) {
      throw new InvalidCheckException("message arguments must not be null");
    }
    String fmt = FormatNormalizer.normalize(pattern);
    Object[] all = new Object[msgArgs.length + 5];
    all[0] = NAMES.getOrDefault(test, test.getClass().getSimpleName());
    all[1] = MsgUtil.toStr(arg);
    all[2] = ifNotNull(arg, MsgUtil::simpleClassName);
    all[3] = argName;
    all[4] = MsgUtil.toStr(obj);
    System.arraycopy(msgArgs, 0, all, 5, msgArgs.length);
    return exc.apply(String.format(fmt, all));
  }

  /* Returns fully-qualified name of the property with the specified name */
  String FQN(String name) {
    return argName + "." + name;
  }

  private String getArgName() {
    return argName != null ? argName : int.class.getSimpleName();
  }
}
