package nl.naturalis.common.check;

import nl.naturalis.common.function.IntRelation;

import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.Messages.createMessage;

/** Helper class for IntCheck. */
final class IntHasInt<E extends Exception> {

  static <EXC extends Exception> IntHasInt<EXC> get(IntCheck<EXC> check) {
    return new IntHasInt<>(check);
  }

  private final IntCheck<E> check;

  private IntHasInt(IntCheck<E> check) {
    this.check = check;
  }

  IntCheck<E> has(IntUnaryOperator property, IntPredicate test) throws E {
    int value = property.applyAsInt(check.arg);
    if (test.test(value)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, IntUnaryOperator.class);
    String msg = createMessage(test, false, name, value);
    throw check.exc.apply(msg);
  }

  IntCheck<E> notHas(IntUnaryOperator property, IntPredicate test) throws E {
    int value = property.applyAsInt(check.arg);
    if (!test.test(value)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, IntUnaryOperator.class);
    String msg = createMessage(test, true, name, value);
    throw check.exc.apply(msg);
  }

  IntCheck<E> has(IntUnaryOperator property, String name, IntPredicate test) throws E {
    int value = property.applyAsInt(check.arg);
    if (test.test(value)) {
      return check;
    }
    throw check.exc.apply(createMessage(test, false, fqn(name), value));
  }

  IntCheck<E> notHas(IntUnaryOperator property, String name, IntPredicate test) throws E {
    int value = property.applyAsInt(check.arg);
    if (!test.test(value)) {
      return check;
    }
    throw check.exc.apply(createMessage(test, true, fqn(name), value));
  }

  IntCheck<E> has(IntUnaryOperator property, IntPredicate test, String message, Object[] msgArgs)
      throws E {
    int value = property.applyAsInt(check.arg);
    if (test.test(value)) {
      return check;
    }
    throw check.createException(test, message, msgArgs);
  }

  <X extends Exception> IntCheck<E> has(
      IntUnaryOperator property, IntPredicate test, Supplier<X> exception) throws X {
    if (test.test(property.applyAsInt(check.arg))) {
      return check;
    }
    throw exception.get();
  }

  IntCheck<E> has(IntUnaryOperator property, IntRelation test, int object) throws E {
    int value = property.applyAsInt(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, IntUnaryOperator.class);
    String msg = createMessage(test, false, name, value, object);
    throw check.exc.apply(msg);
  }

  IntCheck<E> notHas(IntUnaryOperator property, IntRelation test, int object) throws E {
    int value = property.applyAsInt(check.arg);
    if (!test.exists(value, object)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, IntUnaryOperator.class);
    String msg = createMessage(test, true, name, value, object);
    throw check.exc.apply(msg);
  }

  IntCheck<E> has(IntUnaryOperator property, String name, IntRelation test, int object) throws E {
    int value = property.applyAsInt(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw check.exc.apply(msg);
  }

  IntCheck<E> notHas(IntUnaryOperator property, String name, IntRelation test, int object)
      throws E {
    int value = property.applyAsInt(check.arg);
    if (!test.exists(value, object)) {
      return check;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw check.exc.apply(msg);
  }

  IntCheck<E> has(
      IntUnaryOperator property, IntRelation test, int object, String message, Object[] msgArgs)
      throws E {
    int value = property.applyAsInt(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    String msg = String.format(message, msgArgs);
    throw check.createException(test, message, msgArgs);
  }

  <X extends Exception> IntCheck<E> has(
      IntUnaryOperator property, IntRelation test, int object, Supplier<X> exception) throws X {
    if (test.exists(property.applyAsInt(check.arg), object)) {
      return check;
    }
    throw exception.get();
  }

  private String fqn(String name) {
    return check.argName + "." + name;
  }
}
