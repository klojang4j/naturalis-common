package nl.naturalis.common.check;

import nl.naturalis.common.function.*;

import java.util.function.*;

import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.check.Check.DEF_ARG_NAME;
import static nl.naturalis.common.check.CommonChecks.NAMES;
import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.Messages.createMessage;

public final class ObjectCheck<T, E extends Exception> {

  private static final String ERR_INT_VALUE = "Cannot return int value for %s";
  private static final String ERR_NULL_TO_INT = ERR_INT_VALUE + " (was null)";
  private static final String ERR_NUMBER_TO_INT = ERR_INT_VALUE + " (was %s)";
  private static final String ERR_OBJECT_TO_INT = ERR_INT_VALUE + " (%s)";

  final T arg;
  final String argName;
  final Function<String, E> exc;

  ObjectCheck(T arg, String argName, Function<String, E> exc) {
    this.arg = arg;
    this.argName = argName;
    this.exc = exc;
  }

  /**
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> is(Predicate<T> test) throws E {
    if (test.test(arg)) {
      return this;
    }
    String msg = createMessage(test, false, getArgName(arg), arg);
    throw exc.apply(msg);
  }

  /**
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> isNot(Predicate<T> test) throws E {
    if (!test.test(arg)) {
      return this;
    }
    String msg = createMessage(test, true, getArgName(arg), arg);
    throw exc.apply(msg);
  }

  /**
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
    throw createException(test, message, msgArgs);
  }

  /**
   * @param test The test
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> isNot(Predicate<T> test, String message, Object... msgArgs) throws E {
    return is(test.negate(), message, msgArgs);
  }

  /**
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
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> is(Relation<T, U> test, U object) throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, false, getArgName(arg), arg, object);
    throw exc.apply(msg);
  }

  /**
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> isNot(Relation<T, U> test, U object) throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, true, getArgName(arg), arg, object);
    throw exc.apply(msg);
  }

  /**
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> is(Relation<T, U> test, U object, String message, Object... msgArgs)
      throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    throw createException(test, object, message, msgArgs);
  }

  /**
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> isNot(
      Relation<T, U> test, U object, String message, Object... msgArgs) throws E {
    return is(test.negate(), object, message, msgArgs);
  }

  /**
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <U> The type of the value being tested against
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <U, X extends Exception> ObjectCheck<T, E> is(
      Relation<T, U> test, U object, Supplier<X> exception) throws X {
    if (test.exists(arg, object)) {
      return this;
    }
    throw exception.get();
  }

  /**
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <U> The type of the value being tested against
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <U, X extends Exception> ObjectCheck<T, E> isNot(
      Relation<T, U> test, U object, Supplier<X> exception) throws X {
    return is(test.negate(), object, exception);
  }

  /**
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> is(ObjIntRelation<T> test, int object) throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, false, getArgName(arg), arg, object);
    throw exc.apply(msg);
  }

  /**
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> isNot(ObjIntRelation<T> test, int object) throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, true, getArgName(arg), arg, object);
    throw exc.apply(msg);
  }

  /**
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
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
    throw createException(test, object, message, msgArgs);
  }

  /**
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> isNot(
      ObjIntRelation<T> test, int object, String message, Object... msgArgs) throws E {
    return is(test.negate(), object, message, msgArgs);
  }

  /**
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> ObjectCheck<T, E> is(
      ObjIntRelation<T> test, int object, Supplier<X> exception) throws X {
    if (test.exists(arg, object)) {
      return this;
    }
    throw exception.get();
  }

  /**
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> ObjectCheck<T, E> isNot(
      ObjIntRelation<T> test, int object, Supplier<X> exception) throws X {
    return is(test.negate(), object, exception);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> has(Function<T, U> property, Predicate<U> test) throws E {
    return ObjHasObj.get(this).has(property, test);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> notHas(Function<T, U> property, Predicate<U> test) throws E {
    return ObjHasObj.get(this).notHas(property, test);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> has(Function<T, U> property, String name, Predicate<U> test)
      throws E {
    return ObjHasObj.get(this).has(property, name, test);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> notHas(Function<T, U> property, String name, Predicate<U> test)
      throws E {
    return ObjHasObj.get(this).notHas(property, name, test);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> has(
      Function<T, U> property, Predicate<U> test, String message, Object... msgArgs) throws E {
    return ObjHasObj.get(this).has(property, test, message, msgArgs);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> notHas(
      Function<T, U> property, Predicate<U> test, String message, Object... msgArgs) throws E {
    return ObjHasObj.get(this).has(property, test.negate(), message, msgArgs);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <U> The type of the value being tested against
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <U, X extends Exception> ObjectCheck<T, E> has(
      Function<T, U> property, Predicate<U> test, Supplier<X> exception) throws X {
    return ObjHasObj.get(this).has(property, test, exception);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <U> The type of the value being tested against
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <U, X extends Exception> ObjectCheck<T, E> notHas(
      Function<T, U> property, Predicate<U> test, Supplier<X> exception) throws X {
    return ObjHasObj.get(this).has(property, test.negate(), exception);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <U> The type of the value being tested against
   * @param <V>
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U, V> ObjectCheck<T, E> has(Function<T, U> property, Relation<U, V> test, V object)
      throws E {
    return ObjHasObj.get(this).has(property, test, object);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <U> The type of the value being tested against
   * @param <V>
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U, V> ObjectCheck<T, E> notHas(Function<T, U> property, Relation<U, V> test, V object)
      throws E {
    return ObjHasObj.get(this).notHas(property, test, object);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <U> The type of the value being tested against
   * @param <V>
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U, V> ObjectCheck<T, E> has(
      Function<T, U> property, String name, Relation<U, V> test, V object) throws E {
    return ObjHasObj.get(this).has(property, name, test, object);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <U> The type of the value being tested against
   * @param <V>
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U, V> ObjectCheck<T, E> notHas(
      Function<T, U> property, String name, Relation<U, V> test, V object) throws E {
    return ObjHasObj.get(this).notHas(property, name, test, object);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <U> The type of the value being tested against
   * @param <V>
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U, V> ObjectCheck<T, E> has(
      Function<T, U> property, Relation<U, V> test, V object, String message, Object... msgArgs)
      throws E {
    return ObjHasObj.get(this).has(property, test, object, message, msgArgs);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <U> The type of the value being tested against
   * @param <V>
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U, V> ObjectCheck<T, E> notHas(
      Function<T, U> property, Relation<U, V> test, V object, String message, Object... msgArgs)
      throws E {
    return ObjHasObj.get(this).has(property, test.negate(), object, message, msgArgs);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> has(Function<T, U> property, ObjIntRelation<U> test, int object)
      throws E {
    return ObjHasObj.get(this).has(property, test, object);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> notHas(Function<T, U> property, ObjIntRelation<U> test, int object)
      throws E {
    return ObjHasObj.get(this).notHas(property, test, object);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> has(
      Function<T, U> property, String name, ObjIntRelation<U> test, int object) throws E {
    return ObjHasObj.get(this).has(property, name, test, object);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> notHas(
      Function<T, U> property, String name, ObjIntRelation<U> test, int object) throws E {
    return ObjHasObj.get(this).notHas(property, name, test, object);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> has(
      Function<T, U> property,
      ObjIntRelation<U> test,
      int object,
      String message,
      Object... msgArgs)
      throws E {
    return ObjHasObj.get(this).has(property, test, object, message, msgArgs);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> notHas(
      Function<T, U> property,
      ObjIntRelation<U> test,
      int object,
      String message,
      Object... msgArgs)
      throws E {
    return ObjHasObj.get(this).has(property, test.negate(), object, message, msgArgs);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> has(ToIntFunction<T> property, IntPredicate test) throws E {
    return ObjHasInt.get(this).has(property, test);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> notHas(ToIntFunction<T> property, IntPredicate test) throws E {
    return ObjHasInt.get(this).notHas(property, test);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> has(ToIntFunction<T> property, String name, IntPredicate test) throws E {
    return ObjHasInt.get(this).has(property, name, test);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> notHas(ToIntFunction<T> property, String name, IntPredicate test)
      throws E {
    return ObjHasInt.get(this).notHas(property, name, test);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> has(
      ToIntFunction<T> property, IntPredicate test, String message, Object... msgArgs) throws E {
    return ObjHasInt.get(this).has(property, test, message, msgArgs);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> notHas(
      ToIntFunction<T> property, IntPredicate test, String message, Object... msgArgs) throws E {
    return ObjHasInt.get(this).has(property, test.negate(), message, msgArgs);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> ObjectCheck<T, E> has(
      ToIntFunction<T> property, IntPredicate test, Supplier<X> exception) throws X {
    return ObjHasInt.get(this).has(property, test, exception);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param exception The supplier of the exception to be thrown if the argument is invalid
   * @param <X> The type of the exception thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <X extends Exception> ObjectCheck<T, E> notHas(
      ToIntFunction<T> property, IntPredicate test, Supplier<X> exception) throws X {
    return ObjHasInt.get(this).has(property, test.negate(), exception);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> has(
      ToIntFunction<T> property, String name, IntObjRelation<U> test, U object) throws E {
    return ObjHasInt.get(this).has(property, name, test, object);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> notHas(
      ToIntFunction<T> property, String name, IntObjRelation<U> test, U object) throws E {
    return ObjHasInt.get(this).notHas(property, name, test, object);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> has(ToIntFunction<T> property, IntObjRelation<U> test, U object)
      throws E {
    int value = property.applyAsInt(arg);
    if (test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, Function.class);
    String msg = createMessage(test, false, name, value, object);
    throw exc.apply(msg);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> notHas(ToIntFunction<T> property, IntObjRelation<U> test, U object)
      throws E {
    int value = property.applyAsInt(arg);
    if (!test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, Function.class);
    String msg = createMessage(test, true, name, value, object);
    throw exc.apply(msg);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> has(
      ToIntFunction<T> property,
      IntObjRelation<U> test,
      U object,
      String message,
      Object... msgArgs)
      throws E {
    int value = property.applyAsInt(arg);
    if (test.exists(value, object)) {
      return this;
    }
    throw createException(test, object, message, msgArgs);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @param <U> The type of the value being tested against
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> ObjectCheck<T, E> notHas(
      ToIntFunction<T> property,
      IntObjRelation<U> test,
      U object,
      String message,
      Object... msgArgs)
      throws E {
    return has(property, test, object, message, msgArgs);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> has(ToIntFunction<T> property, String name, IntRelation test, int object)
      throws E {
    int value = property.applyAsInt(arg);
    if (test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw exc.apply(msg);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param name The name of the property being tested. In error messages the fully-qualified name
   *     will be used and constructed using {@code argName + "." + name}.
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> notHas(
      ToIntFunction<T> property, String name, IntRelation test, int object) throws E {
    int value = property.applyAsInt(arg);
    if (!test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw exc.apply(msg);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> has(ToIntFunction<T> property, IntRelation test, int object) throws E {
    int value = property.applyAsInt(arg);
    if (test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, ToIntFunction.class);
    String msg = createMessage(test, false, name, value, object);
    throw exc.apply(msg);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> notHas(ToIntFunction<T> property, IntRelation test, int object)
      throws E {
    int value = property.applyAsInt(arg);
    if (!test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, ToIntFunction.class);
    String msg = createMessage(test, true, name, value, object);
    throw exc.apply(msg);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> has(
      ToIntFunction<T> property, IntRelation test, int object, String message, Object... msgArgs)
      throws E {
    int value = property.applyAsInt(arg);
    if (test.exists(value, object)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw exc.apply(msg);
  }

  /**
   * @param property A function that extracts the value to be tested from the argument
   * @param test The test
   * @param object The value that the argument is tested against (called "the object" of a relation)
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public ObjectCheck<T, E> notHas(
      ToIntFunction<T> property, IntRelation test, int object, String message, Object... msgArgs)
      throws E {
    return has(property, test.negate(), object, message, msgArgs);
  }

  public T ok() {
    return arg;
  }

  @SuppressWarnings({"raw-types"})
  private boolean applicable() {
    Class c = arg.getClass();
    return c == Integer.class || c == Short.class || c == Byte.class;
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
   * @param <U> The type of the value being tested against The type of the returned value
   * @param transformer A {@code Function} that transforms the argument into some other value
   * @return This instance The value computed by the {@code Function}
   * @throws F The exception potentially thrown by the {@code Function}
   */
  public <U, F extends Throwable> U ok(ThrowingFunction<T, U, F> transformer) throws F {
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

  E createException(Object test, String msg, Object[] msgArgs) {
    return createException(test, null, msg, msgArgs);
  }

  E createException(Object test, Object object, String msg, Object[] msgArgs) {
    return createException(test, arg, object, msg, msgArgs);
  }

  E createException(Object test, Object subject, Object object, String pattern, Object[] msgArgs) {
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
    all[2] = ifNotNull(subject, Messages::simpleClassName);
    all[3] = argName;
    all[4] = Messages.toStr(object);
    System.arraycopy(msgArgs, 0, all, 5, msgArgs.length);
    return exc.apply(String.format(fmt, all));
  }

  String getArgName(Object arg) {
    return argName != null ? argName : arg != null ? Messages.simpleClassName(arg) : DEF_ARG_NAME;
  }

  /* Returns fully-qualified name of the property with the specified name */
  private String fqn(String name) {
    return argName + "." + name;
  }
}
