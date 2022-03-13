package nl.naturalis.common.check;

import nl.naturalis.common.function.Relation;

import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.MsgUtil.getMessage;

final class IntHasObj<E extends Exception> {

  static <E0 extends Exception> IntHasObj<E0> get(IntCheck<E0> check) {
    return new IntHasObj<>(check);
  }

  private final IntCheck<E> check;

  private IntHasObj(IntCheck<E> check) {
    this.check = check;
  }

  <P> IntCheck<E> has(IntFunction<P> prop, Predicate<P> test) throws E {
    IntCheck<E> check = this.check;
    P val = prop.apply(check.arg);
    if (test.test(val)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, IntFunction.class);
    throw check.createException(getMessage(test, false, name, val));
  }

  <P> IntCheck<E> notHas(IntFunction<P> prop, Predicate<P> test) throws E {
    IntCheck<E> check = this.check;
    P val = prop.apply(check.arg);
    if (!test.test(val)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, IntFunction.class);
    throw check.createException(getMessage(test, true, name, val));
  }

  <P> IntCheck<E> has(IntFunction<P> prop, String name, Predicate<P> test) throws E {
    IntCheck<E> check = this.check;
    P val = prop.apply(check.arg);
    if (test.test(val)) {
      return check;
    }
    throw check.createException(getMessage(test, false, check.FQN(name), val));
  }

  <P> IntCheck<E> notHas(IntFunction<P> prop, String name, Predicate<P> test) throws E {
    IntCheck<E> check = this.check;
    P val = prop.apply(check.arg);
    if (!test.test(val)) {
      return check;
    }
    throw check.createException(getMessage(test, true, check.FQN(name), val));
  }

  <P> IntCheck<E> has(IntFunction<P> prop, Predicate<P> test, String msg, Object[] msgArgs)
      throws E {
    IntCheck<E> check = this.check;
    P val = prop.apply(check.arg);
    if (test.test(val)) {
      return check;
    }
    throw check.createException(test, msg, msgArgs);
  }

  <P> IntCheck<E> notHas(IntFunction<P> prop, Predicate<P> test, String msg, Object[] msgArgs)
      throws E {
    IntCheck<E> check = this.check;
    P val = prop.apply(check.arg);
    if (!test.test(val)) {
      return check;
    }
    throw check.createException(test, msg, msgArgs);
  }

  <P, X extends Exception> IntCheck<E> has(
      IntFunction<P> prop, Predicate<P> test, Supplier<X> exception) throws X {
    IntCheck<E> check = this.check;
    if (test.test(prop.apply(check.arg))) {
      return check;
    }
    throw exception.get();
  }

  <P, O> IntCheck<E> has(IntFunction<P> prop, String name, Relation<P, O> test, O obj) throws E {
    IntCheck<E> check = this.check;
    P val = prop.apply(check.arg);
    if (test.exists(val, obj)) {
      return check;
    }
    throw check.createException(getMessage(test, false, check.FQN(name), val, obj));
  }

  <P, O> IntCheck<E> notHas(IntFunction<P> prop, String name, Relation<P, O> test, O obj) throws E {
    IntCheck<E> check = this.check;
    P val = prop.apply(check.arg);
    if (!test.exists(val, obj)) {
      return check;
    }
    throw check.createException(getMessage(test, true, check.FQN(name), val, obj));
  }

  <P, O> IntCheck<E> has(IntFunction<P> prop, Relation<P, O> test, O obj) throws E {
    IntCheck<E> check = this.check;
    P val = prop.apply(check.arg);
    if (test.exists(val, obj)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, IntFunction.class);
    throw check.createException(getMessage(test, false, name, val, obj));
  }

  <P, O> IntCheck<E> notHas(IntFunction<P> prop, Relation<P, O> test, O obj) throws E {
    IntCheck<E> check = this.check;
    P val = prop.apply(check.arg);
    if (!test.exists(val, obj)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, IntFunction.class);
    throw check.createException(getMessage(test, true, name, val, obj));
  }

  <P, O> IntCheck<E> has(
      IntFunction<P> prop, Relation<P, O> test, O obj, String msg, Object[] msgArgs) throws E {
    IntCheck<E> check = this.check;
    if (test.exists(prop.apply(check.arg), obj)) {
      return check;
    }
    throw check.createException(test, obj, msg, msgArgs);
  }

  <P, O> IntCheck<E> notHas(
      IntFunction<P> prop, Relation<P, O> test, O obj, String msg, Object[] msgArgs) throws E {
    IntCheck<E> check = this.check;
    if (!test.exists(prop.apply(check.arg), obj)) {
      return check;
    }
    throw check.createException(test, obj, msg, msgArgs);
  }

  <P, O, X extends Exception> IntCheck<E> has(
      IntFunction<P> prop, Relation<P, O> test, O obj, Supplier<X> exception) throws X {
    IntCheck<E> check = this.check;
    if (test.exists(prop.apply(check.arg), obj)) {
      return check;
    }
    throw exception.get();
  }
}
