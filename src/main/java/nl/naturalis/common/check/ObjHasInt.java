package nl.naturalis.common.check;

import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.Messages.createMessage;

/** Helper class for ObjectCheck. */
class ObjHasInt<T, E extends Exception> {

  static <U, EXC extends Exception> ObjHasInt<U, EXC> get(ObjectCheck<U, EXC> check) {
    return new ObjHasInt<>(check);
  }

  private final ObjectCheck<T, E> check;

  private ObjHasInt(ObjectCheck<T, E> check) {
    this.check = check;
  }

  <U> ObjectCheck<T, E> has(ToIntFunction<T> property, String name, IntPredicate test) throws E {
    int value = property.applyAsInt(check.arg);
    if (test.test(value)) {
      return check;
    }
    String msg = createMessage(test, false, fqn(name), value);
    throw check.exc.apply(msg);
  }

  <U> ObjectCheck<T, E> notHas(ToIntFunction<T> property, String name, IntPredicate test) throws E {
    int value = property.applyAsInt(check.arg);
    if (!test.test(value)) {
      return check;
    }
    String msg = createMessage(test, true, fqn(name), value);
    throw check.exc.apply(msg);
  }

  <U> ObjectCheck<T, E> has(ToIntFunction<T> property, IntPredicate test) throws E {
    int value = property.applyAsInt(check.arg);
    if (test.test(value)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, Function.class);
    String msg = createMessage(test, false, name, value);
    throw check.exc.apply(msg);
  }

  <U> ObjectCheck<T, E> notHas(ToIntFunction<T> property, IntPredicate test) throws E {
    int value = property.applyAsInt(check.arg);
    if (!test.test(value)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, Function.class);
    String msg = createMessage(test, true, name, value);
    throw check.exc.apply(msg);
  }

  <U> ObjectCheck<T, E> has(
      ToIntFunction<T> property, IntPredicate test, String message, Object[] msgArgs) throws E {
    if (test.test(property.applyAsInt(check.arg))) {
      return check;
    }
    throw check.createException(test, message, msgArgs);
  }

  <U, X extends Exception> ObjectCheck<T, E> has(
      ToIntFunction<T> property, IntPredicate test, Supplier<X> exception) throws X {
    if (test.test(property.applyAsInt(check.arg))) {
      return check;
    }
    throw exception.get();
  }

  public <U> ObjectCheck<T, E> has(ToIntFunction<T> property, IntObjRelation<U> test, U object)
      throws E {
    int value = property.applyAsInt(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, Function.class);
    String msg = createMessage(test, false, name, value, object);
    throw check.exc.apply(msg);
  }

  public <U> ObjectCheck<T, E> notHas(ToIntFunction<T> property, IntObjRelation<U> test, U object)
      throws E {
    int value = property.applyAsInt(check.arg);
    if (!test.exists(value, object)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, Function.class);
    String msg = createMessage(test, true, name, value, object);
    throw check.exc.apply(msg);
  }

  <U> ObjectCheck<T, E> has(
      ToIntFunction<T> property, String name, IntObjRelation<U> test, U object) throws E {
    int value = property.applyAsInt(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw check.exc.apply(msg);
  }

  <U> ObjectCheck<T, E> notHas(
      ToIntFunction<T> property, String name, IntObjRelation<U> test, U object) throws E {
    int value = property.applyAsInt(check.arg);
    if (!test.exists(value, object)) {
      return check;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw check.exc.apply(msg);
  }

  <U> ObjectCheck<T, E> has(
      ToIntFunction<T> property, IntObjRelation<U> test, U object, String message, Object[] msgArgs)
      throws E {
    int value = property.applyAsInt(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    throw check.createException(test, object, message, msgArgs);
  }

  <U, X extends Exception> ObjectCheck<T, E> has(
      ToIntFunction<T> property, IntObjRelation<U> test, U object, Supplier<X> exception) throws X {
    if (test.exists(property.applyAsInt(check.arg), object)) {
      return check;
    }
    throw exception.get();
  }

  public ObjectCheck<T, E> has(ToIntFunction<T> property, IntRelation test, int object) throws E {
    int value = property.applyAsInt(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, Function.class);
    String msg = createMessage(test, false, name, value, object);
    throw check.exc.apply(msg);
  }

  public ObjectCheck<T, E> notHas(ToIntFunction<T> property, IntRelation test, int object)
      throws E {
    int value = property.applyAsInt(check.arg);
    if (!test.exists(value, object)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, Function.class);
    String msg = createMessage(test, true, name, value, object);
    throw check.exc.apply(msg);
  }

  ObjectCheck<T, E> has(ToIntFunction<T> property, String name, IntRelation test, int object)
      throws E {
    int value = property.applyAsInt(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw check.exc.apply(msg);
  }

  ObjectCheck<T, E> notHas(ToIntFunction<T> property, String name, IntRelation test, int object)
      throws E {
    int value = property.applyAsInt(check.arg);
    if (!test.exists(value, object)) {
      return check;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw check.exc.apply(msg);
  }

  ObjectCheck<T, E> has(
      ToIntFunction<T> property, IntRelation test, int object, String message, Object[] msgArgs)
      throws E {
    int value = property.applyAsInt(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    throw check.createException(test, object, message, msgArgs);
  }

  <X extends Exception> ObjectCheck<T, E> has(
      ToIntFunction<T> property, IntRelation test, int object, Supplier<X> exception) throws X {
    if (test.exists(property.applyAsInt(check.arg), object)) {
      return check;
    }
    throw exception.get();
  }

  private String fqn(String name) {
    return check.argName + "." + name;
  }
}
