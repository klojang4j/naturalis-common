package nl.naturalis.common.check;

import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.Messages.getMessage;

/** Helper class for ObjectCheck. */
class ObjHasInt<T, E extends Exception> {

  static <T0, E0 extends Exception> ObjHasInt<T0, E0> get(ObjectCheck<T0, E0> check) {
    return new ObjHasInt<>(check);
  }

  private final ObjectCheck<T, E> check;

  private ObjHasInt(ObjectCheck<T, E> check) {
    this.check = check;
  }

  <O> ObjectCheck<T, E> has(ToIntFunction<T> prop, IntPredicate test) throws E {
    ObjectCheck<T, E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (test.test(val)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, Function.class);
    throw check.createException(getMessage(test, false, name, val, int.class));
  }

  <O> ObjectCheck<T, E> notHas(ToIntFunction<T> prop, IntPredicate test) throws E {
    ObjectCheck<T, E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (!test.test(val)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, Function.class);
    throw check.createException(getMessage(test, true, name, val, int.class));
  }

  <O> ObjectCheck<T, E> has(ToIntFunction<T> prop, String name, IntPredicate test) throws E {
    ObjectCheck<T, E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (test.test(val)) {
      return check;
    }
    throw check.createException(getMessage(test, false, check.FQN(name), val, int.class));
  }

  <O> ObjectCheck<T, E> notHas(ToIntFunction<T> prop, String name, IntPredicate test) throws E {
    ObjectCheck<T, E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (!test.test(val)) {
      return check;
    }
    throw check.createException(getMessage(test, true, check.FQN(name), val, int.class));
  }

  <O> ObjectCheck<T, E> has(ToIntFunction<T> prop, IntPredicate test, String msg, Object[] msgArgs)
      throws E {
    ObjectCheck<T, E> check = this.check;
    if (test.test(prop.applyAsInt(check.arg))) {
      return check;
    }
    throw check.createException(test, msg, msgArgs);
  }

  <O, X extends Exception> ObjectCheck<T, E> has(
      ToIntFunction<T> prop, IntPredicate test, Supplier<X> exception) throws X {
    ObjectCheck<T, E> check = this.check;
    if (test.test(prop.applyAsInt(check.arg))) {
      return check;
    }
    throw exception.get();
  }

  public <O> ObjectCheck<T, E> has(ToIntFunction<T> prop, IntObjRelation<O> test, O obj) throws E {
    ObjectCheck<T, E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (test.exists(val, obj)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, Function.class);
    throw check.createException(getMessage(test, false, name, val, int.class, obj));
  }

  public <O> ObjectCheck<T, E> notHas(ToIntFunction<T> prop, IntObjRelation<O> test, O obj)
      throws E {
    ObjectCheck<T, E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (!test.exists(val, obj)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, Function.class);
    throw check.createException(getMessage(test, true, name, val, int.class, obj));
  }

  <O> ObjectCheck<T, E> has(ToIntFunction<T> prop, String name, IntObjRelation<O> test, O obj)
      throws E {
    ObjectCheck<T, E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (test.exists(val, obj)) {
      return check;
    }
    throw check.createException(getMessage(test, false, check.FQN(name), val, int.class, obj));
  }

  <O> ObjectCheck<T, E> notHas(ToIntFunction<T> prop, String name, IntObjRelation<O> test, O obj)
      throws E {
    ObjectCheck<T, E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (!test.exists(val, obj)) {
      return check;
    }
    throw check.createException(getMessage(test, true, check.FQN(name), val, int.class, obj));
  }

  <O> ObjectCheck<T, E> has(
      ToIntFunction<T> prop, IntObjRelation<O> test, O obj, String msg, Object[] msgArgs) throws E {
    ObjectCheck<T, E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (test.exists(val, obj)) {
      return check;
    }
    throw check.createException(test, obj, msg, msgArgs);
  }

  <O, X extends Exception> ObjectCheck<T, E> has(
      ToIntFunction<T> prop, IntObjRelation<O> test, O obj, Supplier<X> exception) throws X {
    ObjectCheck<T, E> check = this.check;
    if (test.exists(prop.applyAsInt(check.arg), obj)) {
      return check;
    }
    throw exception.get();
  }

  public ObjectCheck<T, E> has(ToIntFunction<T> prop, IntRelation test, int obj) throws E {
    ObjectCheck<T, E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (test.exists(val, obj)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, Function.class);
    throw check.createException(getMessage(test, false, name, val, int.class, obj));
  }

  public ObjectCheck<T, E> notHas(ToIntFunction<T> prop, IntRelation test, int obj) throws E {
    ObjectCheck<T, E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (!test.exists(val, obj)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, prop, Function.class);
    throw check.createException(getMessage(test, true, name, val, int.class, obj));
  }

  ObjectCheck<T, E> has(ToIntFunction<T> prop, String name, IntRelation test, int obj) throws E {
    ObjectCheck<T, E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (test.exists(val, obj)) {
      return check;
    }
    throw check.createException(getMessage(test, false, check.FQN(name), val, int.class, obj));
  }

  ObjectCheck<T, E> notHas(ToIntFunction<T> prop, String name, IntRelation test, int obj) throws E {
    ObjectCheck<T, E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (!test.exists(val, obj)) {
      return check;
    }
    throw check.createException(getMessage(test, true, check.FQN(name), val, int.class, obj));
  }

  ObjectCheck<T, E> has(
      ToIntFunction<T> prop, IntRelation test, int obj, String msg, Object[] msgArgs) throws E {
    ObjectCheck<T, E> check = this.check;
    int val = prop.applyAsInt(check.arg);
    if (test.exists(val, obj)) {
      return check;
    }
    throw check.createException(test, obj, msg, msgArgs);
  }

  <X extends Exception> ObjectCheck<T, E> has(
      ToIntFunction<T> prop, IntRelation test, int obj, Supplier<X> exception) throws X {
    ObjectCheck<T, E> check = this.check;
    if (test.exists(prop.applyAsInt(check.arg), obj)) {
      return check;
    }
    throw exception.get();
  }
}
