package nl.naturalis.common.check;

import nl.naturalis.common.function.IntRelation;

import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.Messages.createMessage;

/** Helper class for IntCheck. */
final class IntHasInt<E extends Exception> {

  static <E0 extends Exception> IntHasInt<E0> get(IntCheck<E0> check) {
    return new IntHasInt<>(check);
  }

  private final IntCheck<E> check;

  private IntHasInt(IntCheck<E> check) {
    this.check = check;
  }

  IntCheck<E> has(IntUnaryOperator prop, IntPredicate test) throws E {
    int value = prop.applyAsInt(check.arg);
    if (test.test(value)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, IntUnaryOperator.class);
    throw check.exc.apply(createMessage(test, false, name, value));
  }

  IntCheck<E> notHas(IntUnaryOperator prop, IntPredicate test) throws E {
    int value = prop.applyAsInt(check.arg);
    if (!test.test(value)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, IntUnaryOperator.class);
    throw check.exc.apply(createMessage(test, true, name, value));
  }

  IntCheck<E> has(IntUnaryOperator prop, String name, IntPredicate test) throws E {
    int value = prop.applyAsInt(check.arg);
    if (test.test(value)) {
      return check;
    }
    throw check.exc.apply(createMessage(test, false, FQN(name), value));
  }

  IntCheck<E> notHas(IntUnaryOperator prop, String name, IntPredicate test) throws E {
    int value = prop.applyAsInt(check.arg);
    if (!test.test(value)) {
      return check;
    }
    throw check.exc.apply(createMessage(test, true, FQN(name), value));
  }

  IntCheck<E> has(IntUnaryOperator prop, IntPredicate test, String message, Object[] msgArgs)
      throws E {
    int value = prop.applyAsInt(check.arg);
    if (test.test(value)) {
      return check;
    }
    throw check.createException(test, message, msgArgs);
  }

  <X extends Exception> IntCheck<E> has(
      IntUnaryOperator prop, IntPredicate test, Supplier<X> exception) throws X {
    if (test.test(prop.applyAsInt(check.arg))) {
      return check;
    }
    throw exception.get();
  }

  IntCheck<E> has(IntUnaryOperator prop, IntRelation test, int obj) throws E {
    int value = prop.applyAsInt(check.arg);
    if (test.exists(value, obj)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, IntUnaryOperator.class);
    throw check.exc.apply(createMessage(test, false, name, value, obj));
  }

  IntCheck<E> notHas(IntUnaryOperator prop, IntRelation test, int obj) throws E {
    int value = prop.applyAsInt(check.arg);
    if (!test.exists(value, obj)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, IntUnaryOperator.class);
    throw check.exc.apply(createMessage(test, true, name, value, obj));
  }

  IntCheck<E> has(IntUnaryOperator prop, String name, IntRelation test, int obj) throws E {
    int value = prop.applyAsInt(check.arg);
    if (test.exists(value, obj)) {
      return check;
    }
    throw check.exc.apply(createMessage(test, false, FQN(name), value, obj));
  }

  IntCheck<E> notHas(IntUnaryOperator prop, String name, IntRelation test, int obj) throws E {
    int value = prop.applyAsInt(check.arg);
    if (!test.exists(value, obj)) {
      return check;
    }
    throw check.exc.apply(createMessage(test, true, FQN(name), value, obj));
  }

  IntCheck<E> has(
      IntUnaryOperator prop, IntRelation test, int obj, String message, Object[] msgArgs) throws E {
    int value = prop.applyAsInt(check.arg);
    if (test.exists(value, obj)) {
      return check;
    }
    String msg = String.format(message, msgArgs);
    throw check.createException(test, message, msgArgs);
  }

  <X extends Exception> IntCheck<E> has(
      IntUnaryOperator prop, IntRelation test, int obj, Supplier<X> exception) throws X {
    if (test.exists(prop.applyAsInt(check.arg), obj)) {
      return check;
    }
    throw exception.get();
  }

  private String FQN(String name) {
    return check.argName + "." + name;
  }
}
