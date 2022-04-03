package nl.naturalis.common.check;

import nl.naturalis.common.function.*;

import java.util.function.*;

import static nl.naturalis.common.check.MsgUtil.*;

/**
 * Facilitates the validation of arbitrarily typed values. See the {@linkplain
 * nl.naturalis.common.check package description} for a detailed explanation.
 *
 * @param <T> The type of the value to be validated
 * @param <E> The type of the exception thrown if the value does not pass the test
 */
public final class ObjectCheck<T, E extends Exception> {

  final T arg;
  final String argName;
  final Function<String, E> exc;

  ObjectCheck(T arg, String argName, Function<String, E> exc) {
    this.arg = arg;
    this.argName = argName;
    this.exc = exc;
  }

  /**
   * Returns the argument. To be used as the last call after a chain of checks.
   *
   * @return The argument
   */
  public T ok() {
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
   * @param <P> The type of the extracted value The type of the returned value
   * @param transformer A {@code Function} that transforms the argument into some other value
   * @return This instance The value computed by the {@code Function}
   * @throws F The exception potentially thrown by the {@code Function}
   */
  public <P, F extends Throwable> P ok(ThrowingFunction<T, P, F> transformer) throws F {
    return transformer.apply(arg);
  }

  /**
   * Passes the validated argument to the specified {@code Consumer}. To be used as the last call
   * after a chain of checks.
   *
   * @param consumer The {@code Consumer}
   */
  public <F extends Throwable> void then(ThrowingConsumer<T, F> consumer) throws F {
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
  public ObjectCheck<T, E> is(Predicate<T> test) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw exc.apply(getPrefabMessage(test, false, argName, arg, null, null));
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
  public ObjectCheck<T, E> isNot(Predicate<T> test) throws E {
    if (!test.test(arg)) {
      return this;
    }
    throw exc.apply(getPrefabMessage(test, true, argName, arg, null, null));
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
  public ObjectCheck<T, E> is(Predicate<T> test, String message, Object... msgArgs) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw exc.apply(getCustomMessage(message, msgArgs, test, argName, arg, null, null));
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
  public ObjectCheck<T, E> isNot(Predicate<T> test, String message, Object... msgArgs) throws E {
    // WATCH OUT. Don't call: is(test.negate(), message, msgArgs)
    // If the test came from the CommonChecks class it must preserve its identity
    // in order to be looked up in the CommonChecks.NAMES map
    if (!test.test(arg)) {
      return this;
    }
    throw exc.apply(getCustomMessage(message, msgArgs, test, argName, arg, null, null));
  }

  /**
   * Validates the argument using the specified test. Allows you to throw a different type of
   * exception for this particular test.
   *
   * @param test The test
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> ObjectCheck<T, E> is(Predicate<T> test, Supplier<X> exception)
      throws X {
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
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> ObjectCheck<T, E> isNot(Predicate<T> test, Supplier<X> exception)
      throws X {
    return is(test.negate(), exception);
  }

  /**
   * Validates the argument using the specified test. While not strictly required, this method is
   * meant to be used in combination with a check from the {@link CommonChecks} class so that an
   * informative error message is generated if the argument turns out to be invalid.
   *
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <O> ObjectCheck<T, E> is(Relation<T, O> test, O object) throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    throw exc.apply(getPrefabMessage(test, false, argName, arg, null, object));
  }

  /**
   * Validates the argument using the specified test. While not strictly required, this method is
   * meant to be used in combination with a check from the {@link CommonChecks} class so that an
   * informative error message is generated if the argument turns out to be invalid.
   *
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <O> ObjectCheck<T, E> isNot(Relation<T, O> test, O object) throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    throw exc.apply(getPrefabMessage(test, true, argName, arg, null, object));
  }

  /**
   * Validates the argument using the specified test. Allows you to provide a custom error message.
   * See the {@link nl.naturalis.common.check package description} for how to specify a custom error
   * message.
   *
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <O> ObjectCheck<T, E> is(Relation<T, O> test, O object, String message, Object... msgArgs)
      throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    throw exc.apply(getCustomMessage(message, msgArgs, test, argName, arg, null, object));
  }

  /**
   * Validates the argument using the specified test. Allows you to provide a custom error message.
   * See the {@link nl.naturalis.common.check package description} for how to specify a custom error
   * message.
   *
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <O> ObjectCheck<T, E> isNot(Relation<T, O> test,
      O object,
      String message,
      Object... msgArgs) throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    throw exc.apply(getCustomMessage(message, msgArgs, test, argName, arg, null, object));
  }

  /**
   * Validates the argument using the specified test. Allows you to throw a different type of
   * exception for this particular test.
   *
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <O> The type of the value being tested against
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <O, X extends Exception> ObjectCheck<T, E> is(Relation<T, O> test,
      O object,
      Supplier<X> exception) throws X {
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
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <O> The type of the value being tested against
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <O, X extends Exception> ObjectCheck<T, E> isNot(Relation<T, O> test,
      O object,
      Supplier<X> exception) throws X {
    return is(test.negate(), object, exception);
  }

  /**
   * Validates the argument using the specified test. While not strictly required, this method is
   * meant to be used in combination with a check from the {@link CommonChecks} class so that an
   * informative error message is generated if the argument turns out to be invalid.
   *
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> is(ObjIntRelation<T> test, int object) throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    throw exc.apply(getPrefabMessage(test, false, argName, arg, null, object));
  }

  /**
   * Validates the argument using the specified test. While not strictly required, this method is
   * meant to be used in combination with a check from the {@link CommonChecks} class so that an
   * informative error message is generated if the argument turns out to be invalid.
   *
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> isNot(ObjIntRelation<T> test, int object) throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    throw exc.apply(getPrefabMessage(test, true, argName, arg, null, object));
  }

  /**
   * Validates the argument using the specified test. Allows you to provide a custom error message.
   * See the {@link nl.naturalis.common.check package description} for how to specify a custom error
   * message.
   *
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> is(ObjIntRelation<T> test, int object, String message, Object... msgArgs)
      throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    throw exc.apply(getCustomMessage(message, msgArgs, test, argName, arg, null, object));
  }

  /**
   * Validates the argument using the specified test. Allows you to provide a custom error message.
   * See the {@link nl.naturalis.common.check package description} for how to specify a custom error
   * message.
   *
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> isNot(ObjIntRelation<T> test,
      int object,
      String message,
      Object... msgArgs) throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    throw exc.apply(getCustomMessage(message, msgArgs, test, argName, arg, null, object));
  }

  /**
   * Validates the argument using the specified test. Allows you to throw a different type of
   * exception for this particular test.
   *
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> ObjectCheck<T, E> is(ObjIntRelation<T> test,
      int object,
      Supplier<X> exception) throws X {
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
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> ObjectCheck<T, E> isNot(ObjIntRelation<T> test,
      int object,
      Supplier<X> exception) throws X {
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
  public <P> ObjectCheck<T, E> has(Function<T, P> property, Predicate<P> test) throws E {
    return ObjHasObj.get(this).has(property, test);
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
  public <P> ObjectCheck<T, E> notHas(Function<T, P> property, Predicate<P> test) throws E {
    return ObjHasObj.get(this).notHas(property, test);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified
   *     name will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param <P> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P> ObjectCheck<T, E> has(Function<T, P> property, String name, Predicate<P> test)
      throws E {
    return ObjHasObj.get(this).has(property, name, test);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified
   *     name will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param <P> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P> ObjectCheck<T, E> notHas(Function<T, P> property, String name, Predicate<P> test)
      throws E {
    return ObjHasObj.get(this).notHas(property, name, test);
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
  public <P> ObjectCheck<T, E> has(Function<T, P> property,
      Predicate<P> test,
      String message,
      Object... msgArgs) throws E {
    return ObjHasObj.get(this).has(property, test, message, msgArgs);
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
  public <P> ObjectCheck<T, E> notHas(Function<T, P> property,
      Predicate<P> test,
      String message,
      Object... msgArgs) throws E {
    return ObjHasObj.get(this).notHas(property, test, message, msgArgs);
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
  public <P, X extends Exception> ObjectCheck<T, E> has(Function<T, P> property,
      Predicate<P> test,
      Supplier<X> exception) throws X {
    return ObjHasObj.get(this).has(property, test, exception);
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
  public <P, X extends Exception> ObjectCheck<T, E> notHas(Function<T, P> property,
      Predicate<P> test,
      Supplier<X> exception) throws X {
    return ObjHasObj.get(this).has(property, test.negate(), exception);
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
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P, O> ObjectCheck<T, E> has(Function<T, P> property, Relation<P, O> test, O object)
      throws E {
    return ObjHasObj.get(this).has(property, test, object);
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
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P, O> ObjectCheck<T, E> notHas(Function<T, P> property, Relation<P, O> test, O object)
      throws E {
    return ObjHasObj.get(this).notHas(property, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified
   *     name will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P, O> ObjectCheck<T, E> has(Function<T, P> property,
      String name,
      Relation<P, O> test,
      O object) throws E {
    return ObjHasObj.get(this).has(property, name, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified
   *     name will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P, O> ObjectCheck<T, E> notHas(Function<T, P> property,
      String name,
      Relation<P, O> test,
      O object) throws E {
    return ObjHasObj.get(this).notHas(property, name, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P, O> ObjectCheck<T, E> has(Function<T, P> property,
      Relation<P, O> test,
      O object,
      String message,
      Object... msgArgs) throws E {
    return ObjHasObj.get(this).has(property, test, object, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P, O> ObjectCheck<T, E> notHas(Function<T, P> property,
      Relation<P, O> test,
      O object,
      String message,
      Object... msgArgs) throws E {
    return ObjHasObj.get(this).notHas(property, test, object, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <P, O, X extends Exception> ObjectCheck<T, E> has(Function<T, P> property,
      Relation<P, O> test,
      O object,
      Supplier<X> exception) throws X {
    return ObjHasObj.get(this).has(property, test, object, exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <P> The type of the extracted value
   * @param <O> The type of the value being tested against
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <P, O, X extends Exception> ObjectCheck<T, E> notHas(Function<T, P> property,
      Relation<P, O> test,
      O object,
      Supplier<X> exception) throws X {
    return ObjHasObj.get(this).has(property, test.negate(), object, exception);
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
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param <P> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P> ObjectCheck<T, E> has(Function<T, P> property, ObjIntRelation<P> test, int object)
      throws E {
    return ObjHasObj.get(this).has(property, test, object);
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
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param <P> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P> ObjectCheck<T, E> notHas(Function<T, P> property, ObjIntRelation<P> test, int object)
      throws E {
    return ObjHasObj.get(this).notHas(property, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified
   *     name will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param <P> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P> ObjectCheck<T, E> has(Function<T, P> property,
      String name,
      ObjIntRelation<P> test,
      int object) throws E {
    return ObjHasObj.get(this).has(property, name, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified
   *     name will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param <P> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P> ObjectCheck<T, E> notHas(Function<T, P> property,
      String name,
      ObjIntRelation<P> test,
      int object) throws E {
    return ObjHasObj.get(this).notHas(property, name, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <P> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P> ObjectCheck<T, E> has(Function<T, P> property,
      ObjIntRelation<P> test,
      int object,
      String message,
      Object... msgArgs) throws E {
    return ObjHasObj.get(this).has(property, test, object, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <P> The type of the extracted value
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <P> ObjectCheck<T, E> notHas(Function<T, P> property,
      ObjIntRelation<P> test,
      int object,
      String message,
      Object... msgArgs) throws E {
    return ObjHasObj.get(this).notHas(property, test, object, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <P> The type of the extracted value
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <P, X extends Exception> ObjectCheck<T, E> has(Function<T, P> property,
      ObjIntRelation<P> test,
      int object,
      Supplier<X> exception) throws X {
    return ObjHasObj.get(this).has(property, test, object, exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <P> The type of the extracted value
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <P, X extends Exception> ObjectCheck<T, E> notHas(Function<T, P> property,
      ObjIntRelation<P> test,
      int object,
      Supplier<X> exception) throws X {
    return ObjHasObj.get(this).has(property, test.negate(), object, exception);
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
  public ObjectCheck<T, E> has(ToIntFunction<T> property, IntPredicate test) throws E {
    return ObjHasInt.get(this).has(property, test);
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
  public ObjectCheck<T, E> notHas(ToIntFunction<T> property, IntPredicate test) throws E {
    return ObjHasInt.get(this).notHas(property, test);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified
   *     name will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> has(ToIntFunction<T> property, String name, IntPredicate test) throws E {
    return ObjHasInt.get(this).has(property, name, test);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified
   *     name will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> notHas(ToIntFunction<T> property, String name, IntPredicate test)
      throws E {
    return ObjHasInt.get(this).notHas(property, name, test);
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
  public ObjectCheck<T, E> has(ToIntFunction<T> property,
      IntPredicate test,
      String message,
      Object... msgArgs) throws E {
    return ObjHasInt.get(this).has(property, test, message, msgArgs);
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
  public ObjectCheck<T, E> notHas(ToIntFunction<T> property,
      IntPredicate test,
      String message,
      Object... msgArgs) throws E {
    return ObjHasInt.get(this).notHas(property, test, message, msgArgs);
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
  public <X extends Exception> ObjectCheck<T, E> has(ToIntFunction<T> property,
      IntPredicate test,
      Supplier<X> exception) throws X {
    return ObjHasInt.get(this).has(property, test, exception);
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
  public <X extends Exception> ObjectCheck<T, E> notHas(ToIntFunction<T> property,
      IntPredicate test,
      Supplier<X> exception) throws X {
    return ObjHasInt.get(this).has(property, test.negate(), exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class <i>and</i> a "getter" from the {@link
   * CommonGetters} class so that an informative error message is generated if the argument turns
   * out to be invalid.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <O> ObjectCheck<T, E> has(ToIntFunction<T> property, IntObjRelation<O> test, O object)
      throws E {
    return ObjHasInt.get(this).has(property, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class <i>and</i> a "getter" from the {@link
   * CommonGetters} class so that an informative error message is generated if the argument turns
   * out to be invalid.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <O> ObjectCheck<T, E> notHas(ToIntFunction<T> property, IntObjRelation<O> test, O object)
      throws E {
    return ObjHasInt.get(this).notHas(property, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified
   *     name will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <O> ObjectCheck<T, E> has(ToIntFunction<T> property,
      String name,
      IntObjRelation<O> test,
      O object) throws E {
    return ObjHasInt.get(this).has(property, name, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified
   *     name will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <O> ObjectCheck<T, E> notHas(ToIntFunction<T> property,
      String name,
      IntObjRelation<O> test,
      O object) throws E {
    return ObjHasInt.get(this).notHas(property, name, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <O> ObjectCheck<T, E> has(ToIntFunction<T> property,
      IntObjRelation<O> test,
      O object,
      String message,
      Object... msgArgs) throws E {
    return ObjHasInt.get(this).has(property, test, object, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <O> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <O> ObjectCheck<T, E> notHas(ToIntFunction<T> property,
      IntObjRelation<O> test,
      O object,
      String message,
      Object... msgArgs) throws E {
    return ObjHasInt.get(this).notHas(property, test, object, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <O> The type of the value being tested against
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <O, X extends Exception> ObjectCheck<T, E> has(ToIntFunction<T> property,
      IntObjRelation<O> test,
      O object,
      Supplier<X> exception) throws X {
    return ObjHasInt.get(this).has(property, test, object, exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <O> The type of the value being tested against
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <O, X extends Exception> ObjectCheck<T, E> notHas(ToIntFunction<T> property,
      IntObjRelation<O> test,
      O object,
      Supplier<X> exception) throws X {
    return ObjHasInt.get(this).has(property, test.negate(), object, exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class <i>and</i> a "getter" from the {@link
   * CommonGetters} class so that an informative error message is generated if the argument turns
   * out to be invalid.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> has(ToIntFunction<T> property, IntRelation test, int object) throws E {
    return ObjHasInt.get(this).has(property, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class <i>and</i> a "getter" from the {@link
   * CommonGetters} class so that an informative error message is generated if the argument turns
   * out to be invalid.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> notHas(ToIntFunction<T> property, IntRelation test, int object)
      throws E {
    return ObjHasInt.get(this).notHas(property, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified
   *     name will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> has(ToIntFunction<T> property, String name, IntRelation test, int object)
      throws E {
    return ObjHasInt.get(this).has(property, name, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. While not strictly required, this method is meant to be used in combination
   * with a check from the {@link CommonChecks} class so that an informative error message is
   * generated if the argument turns out to be invalid.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified
   *     name will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> notHas(ToIntFunction<T> property,
      String name,
      IntRelation test,
      int object) throws E {
    return ObjHasInt.get(this).notHas(property, name, test, object);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> has(ToIntFunction<T> property,
      IntRelation test,
      int object,
      String message,
      Object... msgArgs) throws E {
    return ObjHasInt.get(this).has(property, test, object, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to provide a custom error message. See the {@link
   * nl.naturalis.common.check package description} for how to specify a custom error message.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> notHas(ToIntFunction<T> property,
      IntRelation test,
      int object,
      String message,
      Object... msgArgs) throws E {
    return ObjHasInt.get(this).notHas(property, test, object, message, msgArgs);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> ObjectCheck<T, E> has(ToIntFunction<T> property,
      IntRelation test,
      int object,
      Supplier<X> exception) throws X {
    return ObjHasInt.get(this).has(property, test, object, exception);
  }

  /**
   * Validates a property of the argument, retrieved through the specified function, using the
   * specified test. Allows you to throw a different type of exception for this particular test.
   *
   * <p>Note that this method is heavily overloaded. Therefore you need to pay attention when
   * providing a lambda or method reference for <b>both</b> the {@code property} argument <b>and</b>
   * the {@code test} argument. Plain lambdas or method references will cause the compiler to
   * complain about an <b>Ambiguous method call</b>. (If the {@code property} argument is a getter
   * from the {@code CommonGetters} class or the {@code test} argument is a check from the {@code
   * CommonChecks} class this won't happen.) There are various ways around this:
   *
   * <ul>
   *   <li>Specify the type of the lambda arguments (not applicable when providing a method
   *       reference). So in stead of<br>
   *       {@code (x,y) -> x.length() < y}<br>
   *       write:<br>
   *       {@code (String x, int y) -> x.length() < y}.
   *   <li>Use one of the utility methods in the {@code CommonChecks} class dedicated to this issue
   *       (e.g. {@link CommonChecks#objObj(Relation) objObj} or {@link
   *       CommonChecks#toInt(ToIntFunction) toInt})
   *   <li>Cast the lambda or method reference to the appropriate type
   * </ul>
   *
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a
   *     relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> ObjectCheck<T, E> notHas(ToIntFunction<T> property,
      IntRelation test,
      int object,
      Supplier<X> exception) throws X {
    return ObjHasInt.get(this).has(property, test.negate(), object, exception);
  }

  String FQN(String propName) {
    return argName + "." + propName;
  }
}
