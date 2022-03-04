package nl.naturalis.common.check;

import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.Messages.createMessage;

/** Helper class for ObjectCheck. */
class ObjHasObj<T, E extends Exception> {

  static <U, EXC extends Exception> ObjHasObj<U, EXC> get(ObjectCheck<U, EXC> check) {
    return new ObjHasObj<>(check);
  }

  private final ObjectCheck<T, E> check;

  private ObjHasObj(ObjectCheck<T, E> check) {
    this.check = check;
  }

  <U> ObjectCheck<T, E> has(Function<T, U> property, String name, Predicate<U> test) throws E {
    U value = property.apply(check.arg);
    if (test.test(value)) {
      return check;
    }
    String msg = createMessage(test, false, fqn(name), value);
    throw check.exc.apply(msg);
  }

  <U> ObjectCheck<T, E> notHas(Function<T, U> property, String name, Predicate<U> test) throws E {
    U value = property.apply(check.arg);
    if (!test.test(value)) {
      return check;
    }
    String msg = createMessage(test, true, fqn(name), value);
    throw check.exc.apply(msg);
  }

  <U> ObjectCheck<T, E> has(Function<T, U> property, Predicate<U> test) throws E {
    U value = property.apply(check.arg);
    if (test.test(value)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, Function.class);
    String msg = createMessage(test, false, name, value);
    throw check.exc.apply(msg);
  }

  <U> ObjectCheck<T, E> notHas(Function<T, U> property, Predicate<U> test) throws E {
    U value = property.apply(check.arg);
    if (!test.test(value)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, Function.class);
    String msg = createMessage(test, true, name, value);
    throw check.exc.apply(msg);
  }

  <U> ObjectCheck<T, E> has(
      Function<T, U> property, Predicate<U> test, String message, Object[] msgArgs) throws E {
    if (test.test(property.apply(check.arg))) {
      return check;
    }
    throw check.exception(test, message, msgArgs);
  }

  <U, X extends Exception> ObjectCheck<T, E> has(
      Function<T, U> property, Predicate<U> test, Supplier<X> exception) throws X {
    if (test.test(property.apply(check.arg))) {
      return check;
    }
    throw exception.get();
  }

  public <U, V> ObjectCheck<T, E> has(Function<T, U> property, Relation<U, V> test, V object)
      throws E {
    U value = property.apply(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, Function.class);
    String msg = createMessage(test, false, name, value, object);
    throw check.exc.apply(msg);
  }

  public <U, V> ObjectCheck<T, E> notHas(Function<T, U> property, Relation<U, V> test, V object)
      throws E {
    U value = property.apply(check.arg);
    if (!test.exists(value, object)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, Function.class);
    String msg = createMessage(test, true, name, value, object);
    throw check.exc.apply(msg);
  }

  <U, V> ObjectCheck<T, E> has(Function<T, U> property, String name, Relation<U, V> test, V object)
      throws E {
    U value = property.apply(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw check.exc.apply(msg);
  }

  <U, V> ObjectCheck<T, E> notHas(
      Function<T, U> property, String name, Relation<U, V> test, V object) throws E {
    U value = property.apply(check.arg);
    if (!test.exists(value, object)) {
      return check;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw check.exc.apply(msg);
  }

  <U, V> ObjectCheck<T, E> has(
      Function<T, U> property, Relation<U, V> test, V object, String message, Object[] msgArgs)
      throws E {
    U value = property.apply(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    throw check.exception(test, object, message, msgArgs);
  }

  public <U> ObjectCheck<T, E> has(Function<T, U> property, ObjIntRelation<U> test, int object)
      throws E {
    U value = property.apply(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, Function.class);
    String msg = createMessage(test, false, name, value, object);
    throw check.exc.apply(msg);
  }

  public <U> ObjectCheck<T, E> notHas(Function<T, U> property, ObjIntRelation<U> test, int object)
      throws E {
    U value = property.apply(check.arg);
    if (!test.exists(value, object)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, Function.class);
    String msg = createMessage(test, true, name, value, object);
    throw check.exc.apply(msg);
  }

  <U> ObjectCheck<T, E> has(
      Function<T, U> property, String name, ObjIntRelation<U> test, int object) throws E {
    U value = property.apply(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw check.exc.apply(msg);
  }

  <U> ObjectCheck<T, E> notHas(
      Function<T, U> property, String name, ObjIntRelation<U> test, int object) throws E {
    U value = property.apply(check.arg);
    if (!test.exists(value, object)) {
      return check;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw check.exc.apply(msg);
  }

  <U> ObjectCheck<T, E> has(
      Function<T, U> property, ObjIntRelation<U> test, int object, String message, Object[] msgArgs)
      throws E {
    U value = property.apply(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    throw check.exception(test, object, message, msgArgs);
  }

  private String fqn(String name) {
    return check.argName + "." + name;
  }
}
