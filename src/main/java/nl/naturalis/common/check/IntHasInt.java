package nl.naturalis.common.check;

import nl.naturalis.common.function.IntRelation;

import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.MsgUtil.getMessage;

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
    IntCheck<E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (test.test(val)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, IntUnaryOperator.class);
    throw check.createException(getMessage(test, false, name, val, int.class));
  }

  IntCheck<E> notHas(IntUnaryOperator prop, IntPredicate test) throws E {
    IntCheck<E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (!test.test(val)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, IntUnaryOperator.class);
    throw check.createException(getMessage(test, true, name, val, int.class));
  }

  IntCheck<E> has(IntUnaryOperator prop, String name, IntPredicate test) throws E {
    IntCheck<E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (test.test(val)) {
      return check;
    }
    throw check.createException(getMessage(test, false, check.FQN(name), val));
  }

  IntCheck<E> notHas(IntUnaryOperator prop, String name, IntPredicate test) throws E {
    IntCheck<E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (!test.test(val)) {
      return check;
    }
    throw check.createException(getMessage(test, true, check.FQN(name), val));
  }

  IntCheck<E> has(IntUnaryOperator prop, IntPredicate test, String message, Object[] msgArgs)
      throws E {
    IntCheck<E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (test.test(val)) {
      return check;
    }
    throw check.createException(test, message, msgArgs);
  }

  <X extends Exception> IntCheck<E> has(IntUnaryOperator prop, IntPredicate test, Supplier<X> exc)
      throws X {
    IntCheck<E> check = this.check;
    if (test.test(prop.applyAsInt(check.arg))) {
      return check;
    }
    throw exc.get();
  }

  IntCheck<E> has(IntUnaryOperator prop, IntRelation test, int obj) throws E {
    IntCheck<E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (test.exists(val, obj)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, IntUnaryOperator.class);
    throw check.createException(getMessage(test, false, name, val, int.class, obj));
  }

  IntCheck<E> notHas(IntUnaryOperator prop, IntRelation test, int obj) throws E {
    IntCheck<E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (!test.exists(val, obj)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, IntUnaryOperator.class);
    throw check.createException(getMessage(test, true, name, val, obj));
  }

  IntCheck<E> has(IntUnaryOperator prop, String name, IntRelation test, int obj) throws E {
    IntCheck<E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (test.exists(val, obj)) {
      return check;
    }
    throw check.createException(getMessage(test, false, check.FQN(name), val, int.class, obj));
  }

  IntCheck<E> notHas(IntUnaryOperator prop, String name, IntRelation test, int obj) throws E {
    IntCheck<E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (!test.exists(val, obj)) {
      return check;
    }
    throw check.createException(getMessage(test, true, check.FQN(name), val, int.class, obj));
  }

  IntCheck<E> has(
      IntUnaryOperator prop, IntRelation test, int obj, String message, Object[] msgArgs) throws E {
    IntCheck<E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (test.exists(val, obj)) {
      return check;
    }
    String msg = String.format(message, msgArgs);
    throw check.createException(test, message, msgArgs);
  }

  <X extends Exception> IntCheck<E> has(
      IntUnaryOperator prop, IntRelation test, int obj, Supplier<X> exc) throws X {
    IntCheck<E> check = this.check;
    if (test.exists(prop.applyAsInt(check.arg), obj)) {
      return check;
    }
    throw exc.get();
  }

  private String FQN(String name) {
    return check.argName + "." + name;
  }
}
