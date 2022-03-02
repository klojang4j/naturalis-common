package nl.naturalis.common.check;

import nl.naturalis.common.function.*;

import java.util.function.*;
import java.util.stream.IntStream;

import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.check.Check.DEF_ARG_NAME;
import static nl.naturalis.common.check.CommonChecks.NAMES;
import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.Messages.createMessage;

public final class IntCheck<E extends Exception> {

  private final int arg;
  private final String argName;
  private final Function<String, E> exc;

  IntCheck(int arg, String argName, Function<String, E> exc) {
    this.arg = arg;
    this.argName = argName;
    this.exc = exc;
  }

  /**
   * Ensures that the argument passes the specified test. While not strictly required, this method
   * is meant to be used with a check from the {@link CommonChecks} class so that an informative
   * error message is generated if the argument turns out to be invalid.
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
   * Ensures that the argument does not pass the specified test.
   *
   * @see #is(IntPredicate)
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
   * Ensures that the argument passes the specified test. Allows you to provide a custom error
   * message. See the {@link nl.naturalis.common.check package description} for how to specify a
   * custom error message.
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
   * Ensures that the argument does not pass the specified test.
   *
   * @see #is(IntPredicate, String, Object...)
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
   * Ensures that the argument passes the specified test. Allows you to throw a different type of
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
   * Ensures that the argument does not pass the specified test.
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
   * Ensures that the argument passes the specified test. While not strictly required, this method
   * is meant to be used in combination with a check from the {@link CommonChecks} class so that an
   * informative error message is generated if the argument turns out to be invalid. Since this is a
   * heavily overloaded method, pay attention when providing your own lambda or method reference. A
   * plain lambda or method reference will cause the compiler to complain about an <b>Ambiguous
   * method call</b>. You can circumvent this in several ways:
   *
   * <ol>
   *   <li>Specify the type of the lambda arguments
   *   <li>Use the {@link CommonChecks#intInt(IntRelation)} intInt} method from the {@code
   *       CommonChecks} class.
   *   <li>Cast the lambda or method reference to an {@code IntRelation}.
   * </ol>
   *
   * <blockquote>
   *
   * <pre>{@code
   * (x, y) -> x > y // WON'T COMPILE! Ambiguous method call
   * (int x, int y) -> x > y
   * intInt((x, y) -> x > y)
   * (IntRelation) (x, y) -> x > y
   * }</pre>
   *
   * </blockquote>
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
   * Ensures that the argument does not pass the specified test.
   *
   * @see #is(IntRelation, int)
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
   * Ensures that the argument passes the specified test. Allows you to provide a custom error
   * message. See the {@link nl.naturalis.common.check package description} for how to specify a
   * custom error message.
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
   * Ensures that the argument does not pass the specified test.
   *
   * @see #is(IntRelation, int, String, Object...)
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
   * Ensures that the argument passes the specified test. Allows you to throw a different type of
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
   * Ensures that the argument does not pass the specified test.
   *
   * @see #is(Relation, Object, Supplier)
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
   * Ensures that the argument passes the specified test. While not strictly required, this method
   * is meant to be used in combination with a check from the {@link CommonChecks} class so that an
   * informative error message is generated if the argument turns out to be invalid. Since this is a
   * heavily overloaded method, pay attention when providing your own lambda or method reference. A
   * plain lambda or method reference will cause the compiler to complain about an <b>Ambiguous
   * method call</b>. You can circumvent this in several ways:
   *
   * <ol>
   *   <li>Specify the type of the lambda arguments
   *   <li>Use the {@link CommonChecks#objObj(Relation) objObj} method from the {@code CommonChecks}
   *       class.
   *   <li>Cast the lambda or method reference to a {@code Relation}.
   * </ol>
   *
   * <blockquote>
   *
   * <pre>{@code
   * (x, y) -> y.contains(x) // WON'T COMPILE! Ambiguous method call
   * (Integer x, Set<Integer> y) -> y.contains(x)
   * objObj((x, y) -> y.contains(x))
   * (Relation<Integer, Set<Integer>>) (x,y) -> y.contains(x)
   * }</pre>
   *
   * </blockquote>
   *
   * @param test The test
   * @param object The object of the {@code Relation}
   * @param <U> The type of the object of the {@code Relation}
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> is(Relation<Integer, U> test, U object) throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, false, getArgName(arg), arg, object);
    throw exc.apply(msg);
  }

  /**
   * Ensures that the argument does not pass the specified test.
   *
   * @see #is(Relation, Object)
   * @param test The test
   * @param object The object of the {@code Relation}
   * @param <U> The type of the object of the {@code Relation}
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> isNot(Relation<Integer, U> test, U object) throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, true, getArgName(arg), arg, object);
    throw exc.apply(msg);
  }

  /**
   * Ensures that the argument passes the specified test. Allows you to provide a custom error
   * message. See the {@link nl.naturalis.common.check package description} for how to specify a
   * custom error message.
   *
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> is(Relation<Integer, U> test, U object, String message, Object... msgArgs)
      throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw exc.apply(msg);
  }

  /**
   * Ensures that the argument does not pass the specified test.
   *
   * @see #is(Relation, Object, String, Object...)
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> isNot(
      Relation<Integer, U> test, U object, String message, Object... msgArgs) throws E {
    return is(test.negate(), object, message, msgArgs);
  }

  /**
   * Ensures that the argument passes the specified test. Allows you to throw a different type of
   * exception for this particular test.
   *
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param exception The {@code Supplier} of the exception to be thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <U, X extends Exception> IntCheck<E> is(
      Relation<Integer, U> test, U object, Supplier<X> exception) throws X {
    if (test.exists(arg, object)) {
      return this;
    }
    throw exception.get();
  }

  /**
   * Ensures that the argument does not pass the specified test.
   *
   * @see #is(Relation, Object, Supplier)
   * @param test The test
   * @param object The object of the {@code IntObjRelation}
   * @param exception The {@code Supplier} of the exception to be thrown if the argument is invalid
   * @return This instance
   * @throws X If the argument is invalid
   */
  public <U, X extends Exception> IntCheck<E> isNot(
      Relation<Integer, U> test, U object, Supplier<X> exception) throws X {
    return is(test.negate(), object, exception);
  }

  /**
   * Ensures that the argument passes the specified test. While not strictly required, this method
   * is meant to be used in combination with a check from the {@link CommonChecks} class so that an
   * informative error message is generated if the argument turns out to be invalid. Since this is a
   * heavily overloaded method, pay attention when providing your own lambda or method reference. A
   * plain lambda or method reference will cause the compiler to complain about an <b>Ambiguous
   * method call</b>. You can circumvent this in several ways:
   *
   * <ol>
   *   <li>Specify the type of the lambda arguments
   *   <li>Use the {@link CommonChecks#intObj(IntObjRelation)} intObj} method from the {@code
   *       CommonChecks} class.
   *   <li>Cast the lambda or method reference to an {@code IntObjRelation}.
   * </ol>
   *
   * @see #is(Relation, Object)
   * @see CommonChecks#intObj(IntObjRelation)
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
   * Ensures that the argument does not pass the specified test.
   *
   * @see #is(IntObjRelation, Object)
   * @see #is(Relation, Object)
   * @see CommonChecks#intObj(IntObjRelation)
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
   * Ensures that the argument passes the specified test. Allows you to provide a custom error
   * message. See the {@link nl.naturalis.common.check package description} for how to specify a
   * custom error message.
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
   * Ensures that the argument does not pass the specified test.
   *
   * @see #is(IntObjRelation, Object, String, Object...)
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
   * Ensures that the argument passes the specified test. Allows you to throw a different type of
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
   * Ensures that the argument does not pass the specified test.
   *
   * @see #is(Relation, Object, Supplier)
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
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the specified test. Although not strictly required, this method is meant to be used with
   * a test from the {@link CommonChecks} class <i>and</i> a getter from the {@link CommonGetters}
   * class so that an informative error message is generated if the argument fails the test.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test A {@code Predicate} expressing the test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> has(IntFunction<U> property, Predicate<U> test) throws E {
    U value = property.apply(ok());
    if (test.test(value)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, IntFunction.class);
    String msg = createMessage(test, false, name, value);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * does not pass the specified test.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> notHas(IntFunction<U> property, Predicate<U> test) throws E {
    U value = property.apply(ok());
    if (!test.test(value)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, IntFunction.class);
    String msg = createMessage(test, true, name, value);
    throw exc.apply(msg);
  }

  /**
   * Ensures that a property of the argument, retrieved through the specified {@code Function},
   * passes the specified test. While not strictly required, this method is meant to be used in
   * combination with a check from the {@link CommonChecks} class so that an informative error
   * message is generated if the argument turns out to be invalid.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> has(IntFunction<U> property, String name, Predicate<U> test) throws E {
    Check.that(property.apply(arg), fqn(name)).is(test);
    return this;
  }

  /**
   * Ensures that a property of the argument, retrieved through the specified {@code Function}, does
   * not pass the specified test.
   *
   * @see #has(IntFunction, String, Predicate)
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The test
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> notHas(IntFunction<U> property, String name, Predicate<U> test) throws E {
    Check.that(property.apply(arg), fqn(name)).isNot(test);
    return this;
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
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> has(
      IntFunction<U> property, Predicate<U> test, String message, Object... msgArgs) throws E {
    U value = property.apply(ok());
    if (test.test(value)) {
      return this;
    }
    throw exception(test, message, msgArgs);
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
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public <U> IntCheck<E> notHas(
      IntFunction<U> property, Predicate<U> test, String message, Object... msgArgs) throws E {
    return has(property, test.negate(), message, msgArgs);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, passes the test expressed through the specified {@code Predicate}. Although not
   * required this method is meant to be used with an {@code IntPredicate} from the {@link
   * CommonChecks} class so that an informative error message is generated if the argument fails the
   * test.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> has(IntUnaryOperator property, String name, IntPredicate test) throws E {
    int value = property.applyAsInt(ok());
    if (test.test(value)) {
      return this;
    }
    String msg = createMessage(test, false, fqn(name), value);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, ducks the test expressed through the specified {@code Predicate}. Although not
   * required this method is meant to be used with an {@code IntPredicate} from the {@link
   * CommonChecks} class so that an informative error message is generated if the argument fails the
   * test.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> notHas(IntUnaryOperator property, String name, IntPredicate test) throws E {
    int value = property.applyAsInt(ok());
    if (!test.test(value)) {
      return this;
    }
    String msg = createMessage(test, true, fqn(name), value);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, passes the test expressed through the specified {@code IntPredicate}. Although
   * not required this method is meant to be used with an {@code IntPredicate} from the {@link
   * CommonChecks} class <i>and</i> a {@code ToIntFunction} from the {@link CommonGetters} class so
   * that an informative error message is generated if the argument fails the test.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> has(IntUnaryOperator property, IntPredicate test) throws E {
    int value = property.applyAsInt(ok());
    if (test.test(value)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, IntUnaryOperator.class);
    String msg = createMessage(test, false, name, value);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, ducks the test expressed through the specified {@code IntPredicate}. Although
   * not required this method is meant to be used with an {@code IntPredicate} from the {@link
   * CommonChecks} class <i>and</i> a {@code ToIntFunction} from the {@link CommonGetters} class so
   * that an informative error message is generated if the argument fails the test.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> notHas(IntUnaryOperator property, IntPredicate test) throws E {
    int value = property.applyAsInt(ok());
    if (!test.test(value)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, IntUnaryOperator.class);
    String msg = createMessage(test, true, name, value);
    throw exc.apply(msg);
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
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the argument is invalid
   */
  public IntCheck<E> has(
      IntUnaryOperator property, IntPredicate test, String message, Object... msgArgs) throws E {
    int value = property.applyAsInt(ok());
    if (test.test(value)) {
      return this;
    }
    throw exception(test, message, msgArgs);
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
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the test expressed through the specified {@code Predicate}. Although not strictly
   * required, this method is meant to be used with a {@code Relation} from the {@link CommonChecks}
   * class so that an informative error message is generated if the argument fails the test.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U, V> IntCheck<E> has(IntFunction<U> property, String name, Relation<U, V> test, V object)
      throws E {
    U value = property.apply(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * ducks the test expressed through the specified {@code Relation}. Although not strictly
   * required, this method is meant to be used with a {@code Relation} from the {@link CommonChecks}
   * class so that an informative error message is generated if the argument fails the test.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U, V> IntCheck<E> notHas(
      IntFunction<U> property, String name, Relation<U, V> test, V object) throws E {
    U value = property.apply(ok());
    if (!test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the test expressed through the specified {@code Relation}. Although not strictly
   * required, this method is meant to be used with a {@code Relation} from the {@link CommonChecks}
   * class <i>and</i> a {@code Function} from the {@link CommonGetters} class so that an informative
   * error message is generated if the argument fails the test.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U, V> IntCheck<E> has(IntFunction<U> property, Relation<U, V> test, V object) throws E {
    U value = property.apply(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, IntFunction.class);
    String msg = createMessage(test, false, name, value, object);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * ducks the test expressed through the specified {@code Relation}. Although not strictly
   * required, this method is meant to be used with a {@code Relation} from the {@link CommonChecks}
   * class <i>and</i> a {@code Function} from the {@link CommonGetters} class so that an informative
   * error message is generated if the argument fails the test.
   *
   * @param <U> The type of the property
   * @param <V> The type of the object of the relationship
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U, V> IntCheck<E> notHas(IntFunction<U> property, Relation<U, V> test, V object)
      throws E {
    U value = property.apply(ok());
    if (!test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, IntFunction.class);
    String msg = createMessage(test, true, name, value, object);
    throw exc.apply(msg);
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
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U, V> IntCheck<E> has(
      IntFunction<U> property, Relation<U, V> test, V object, String message, Object... msgArgs)
      throws E {
    U value = property.apply(ok());
    if (test.exists(value, object)) {
      return this;
    }
    throw exception(test, object, message, msgArgs);
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
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U, V> IntCheck<E> notHas(
      IntFunction<U> property, Relation<U, V> test, V object, String message, Object... msgArgs)
      throws E {
    return has(property, test.negate(), object, message, msgArgs);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the test expressed through the specified {@code ObjIntRelation}. Although not strictly
   * required, this method is meant to be used with the {@link CommonChecks} class so that an
   * informative error message is generated if the argument fails the test.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> IntCheck<E> has(
      IntFunction<U> property, String name, ObjIntRelation<U> test, int object) throws E {
    U value = property.apply(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * ducks the test expressed through the specified {@code ObjIntRelation}. Although not strictly
   * required, this method is meant to be used with the {@link CommonChecks} class so that an
   * informative error message is generated if the argument fails the test.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> IntCheck<E> notHas(
      IntFunction<U> property, String name, ObjIntRelation<U> test, int object) throws E {
    U value = property.apply(ok());
    if (!test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the test expressed through the specified {@code ObjIntRelation}. Although not strictly
   * required, this method is meant to be used with a {@code ObjIntRelation} from the {@link
   * CommonChecks} class <i>and</i> a {@code Function} from the {@link CommonGetters} class so that
   * an informative error message is generated if the argument fails the test.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> IntCheck<E> has(IntFunction<U> property, ObjIntRelation<U> test, int object) throws E {
    U value = property.apply(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, IntFunction.class);
    String msg = createMessage(test, false, name, value, object);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * ducks the test expressed through the specified {@code ObjIntRelation}. Although not strictly
   * required, this method is meant to be used with a {@code ObjIntRelation} from the {@link
   * CommonChecks} class <i>and</i> a {@code Function} from the {@link CommonGetters} class so that
   * an informative error message is generated if the argument fails the test.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> IntCheck<E> notHas(IntFunction<U> property, ObjIntRelation<U> test, int object)
      throws E {
    U value = property.apply(ok());
    if (!test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, IntFunction.class);
    String msg = createMessage(test, true, name, value, object);
    throw exc.apply(msg);
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
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> IntCheck<E> has(
      IntFunction<U> property,
      ObjIntRelation<U> test,
      int object,
      String message,
      Object... msgArgs)
      throws E {
    U value = property.apply(ok());
    if (test.exists(value, object)) {
      return this;
    }
    throw exception(test, object, message, msgArgs);
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
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> IntCheck<E> notHas(
      IntFunction<U> property,
      ObjIntRelation<U> test,
      int object,
      String message,
      Object... msgArgs)
      throws E {
    return has(property, test, object, message, msgArgs);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the test expressed through the specified {@code ObjIntRelation}. Although not strictly
   * required, this method is meant to be used with the {@link CommonChecks} class so that an
   * informative error message is generated if the argument fails the test.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> IntCheck<E> has(
      IntUnaryOperator property, String name, IntObjRelation<U> test, U object) throws E {
    int value = property.applyAsInt(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * ducks the test expressed through the specified {@code ObjIntRelation}. Although not strictly
   * required, this method is meant to be used with the {@link CommonChecks} class so that an
   * informative error message is generated if the argument fails the test.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> IntCheck<E> notHas(
      IntUnaryOperator property, String name, IntObjRelation<U> test, U object) throws E {
    int value = property.applyAsInt(ok());
    if (!test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * passes the test expressed through the specified {@code ObjIntRelation}. Although not strictly
   * required, this method is meant to be used with a {@code ObjIntRelation} from the {@link
   * CommonChecks} class <i>and</i> a {@code Function} from the {@link CommonGetters} class so that
   * an informative error message is generated if the argument fails the test.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> IntCheck<E> has(IntUnaryOperator property, IntObjRelation<U> test, U object) throws E {
    int value = property.applyAsInt(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, IntFunction.class);
    String msg = createMessage(test, false, name, value, object);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code Function},
   * ducks the test expressed through the specified {@code ObjIntRelation}. Although not strictly
   * required, this method is meant to be used with a {@code ObjIntRelation} from the {@link
   * CommonChecks} class <i>and</i> a {@code Function} from the {@link CommonGetters} class so that
   * an informative error message is generated if the argument fails the test.
   *
   * @param <U> The type of the property
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> IntCheck<E> notHas(IntUnaryOperator property, IntObjRelation<U> test, U object)
      throws E {
    int value = property.applyAsInt(ok());
    if (!test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, IntFunction.class);
    String msg = createMessage(test, true, name, value, object);
    throw exc.apply(msg);
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
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> IntCheck<E> has(
      IntUnaryOperator property,
      IntObjRelation<U> test,
      U object,
      String message,
      Object... msgArgs)
      throws E {
    int value = property.applyAsInt(ok());
    if (test.exists(value, object)) {
      return this;
    }
    throw exception(test, object, message, msgArgs);
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
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public <U> IntCheck<E> notHas(
      IntUnaryOperator property,
      IntObjRelation<U> test,
      U object,
      String message,
      Object... msgArgs)
      throws E {
    return has(property, test, object, message, msgArgs);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, passes the test expressed through the specified {@code IntRelation}. Although
   * not required this method is meant to be used with the {@link CommonChecks} class so that an
   * informative error message is generated if the argument fails the test.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public IntCheck<E> has(IntUnaryOperator property, String name, IntRelation test, int object)
      throws E {
    int value = property.applyAsInt(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, ducks the test expressed through the specified {@code IntRelation}. Although
   * not required this method is meant to be used with the {@link CommonChecks} class so that an
   * informative error message is generated if the argument fails the test.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param name The name of the property
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public IntCheck<E> notHas(IntUnaryOperator property, String name, IntRelation test, int object)
      throws E {
    int value = property.applyAsInt(ok());
    if (!test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, passes the test expressed through the specified {@code IntRelation}. Although
   * not required this method is meant to be used with a {@code IntRelation} from the {@link
   * CommonChecks} class <i>and</i> a {@code ToIntFunction} from the {@link CommonGetters} class so
   * that an informative error message is generated if the argument fails the test.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public IntCheck<E> has(IntUnaryOperator property, IntRelation test, int object) throws E {
    int value = property.applyAsInt(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, IntUnaryOperator.class);
    String msg = createMessage(test, false, name, value, object);
    throw exc.apply(msg);
  }

  /**
   * Verifies that a property of the argument, retrieved through the specified {@code
   * ToIntFunction}, ducks the test expressed through the specified {@code IntRelation}. Although
   * not required this method is meant to be used with a {@code IntRelation} from the {@link
   * CommonChecks} class <i>and</i> a {@code ToIntFunction} from the {@link CommonGetters} class so
   * that an informative error message is generated if the argument fails the test.
   *
   * @param property A function which is given the argument as input and returns the value to be
   *     tested
   * @param test The relation to verify between the property (as the subject of the relationship)
   *     and the specified value (as the object of the relationship)
   * @param object The object of the relationship
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public IntCheck<E> notHas(IntUnaryOperator property, IntRelation test, int object) throws E {
    int value = property.applyAsInt(ok());
    if (!test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, IntUnaryOperator.class);
    String msg = createMessage(test, true, name, value, object);
    throw exc.apply(msg);
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
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public IntCheck<E> has(
      IntUnaryOperator property, IntRelation test, int object, String message, Object... msgArgs)
      throws E {
    int value = property.applyAsInt(ok());
    if (test.exists(value, object)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw exc.apply(msg);
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
   * @param message The message pattern
   * @param msgArgs The message arguments
   * @return This instance
   * @throws E If the specified test does not exist between subject and object
   */
  public IntCheck<E> notHas(
      IntUnaryOperator property, IntRelation test, int object, String message, Object... msgArgs)
      throws E {
    return has(property, test.negate(), object, message, msgArgs);
  }

  /**
   * Returns the argument. To be used as the last call after a chain of checks.
   *
   * @return The argument
   */
  public Integer ok() {
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
    return transformer.apply(ok());
  }

  /**
   * Passes the validated argument to the specified {@code Consumer}. To be used as the last call
   * after a chain of checks.
   *
   * @param consumer The {@code Consumer}
   */
  public <F extends Throwable> void then(ThrowingIntConsumer<F> consumer) throws F {
    consumer.accept(ok());
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

  String getArgName(Object arg) {
    return argName != null ? argName : arg != null ? className(arg) : DEF_ARG_NAME;
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
