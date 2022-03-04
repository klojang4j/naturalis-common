package nl.naturalis.common.check;

import nl.naturalis.common.function.Relation;

import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.Messages.createMessage;

class IntHasObj<E extends Exception> {

  static <EXC extends Exception> IntHasObj<EXC> get(IntCheck<EXC> check) {
    return new IntHasObj<>(check);
  }

  private final IntCheck<E> check;

  private IntHasObj(IntCheck<E> check) {
    this.check = check;
  }

  <U> IntCheck<E> has(IntFunction<U> property, Predicate<U> test) throws E {
    U value = property.apply(check.arg);
    if (test.test(value)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, IntFunction.class);
    String msg = createMessage(test, false, name, value);
    throw check.exc.apply(msg);
  }

  <U> IntCheck<E> notHas(IntFunction<U> property, Predicate<U> test) throws E {
    U value = property.apply(check.arg);
    if (!test.test(value)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, IntFunction.class);
    String msg = createMessage(test, true, name, value);
    throw check.exc.apply(msg);
  }

  <U> IntCheck<E> has(IntFunction<U> property, String name, Predicate<U> test) throws E {
    U value = property.apply(check.arg);
    if (test.test(value)) {
      return check;
    }
    throw check.exc.apply(createMessage(test, false, fqn(name), value));
  }

  <U> IntCheck<E> notHas(IntFunction<U> property, String name, Predicate<U> test) throws E {
    U value = property.apply(check.arg);
    if (!test.test(value)) {
      return check;
    }
    throw check.exc.apply(createMessage(test, true, fqn(name), value));
  }

  <U> IntCheck<E> has(IntFunction<U> property, Predicate<U> test, String message, Object[] msgArgs)
      throws E {
    U value = property.apply(check.arg);
    if (test.test(value)) {
      return check;
    }
    throw check.createException(test, message, msgArgs);
  }

  <U, X extends Exception> IntCheck<E> has(
      IntFunction<U> property, Predicate<U> test, Supplier<X> exception) throws X {
    if (test.test(property.apply(check.arg))) {
      return check;
    }
    throw exception.get();
  }

  <U, V> IntCheck<E> has(IntFunction<U> property, String name, Relation<U, V> test, V object)
      throws E {
    U value = property.apply(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    throw check.exc.apply(createMessage(test, false, fqn(name), value, object));
  }

  <U, V> IntCheck<E> notHas(IntFunction<U> property, String name, Relation<U, V> test, V object)
      throws E {
    U value = property.apply(check.arg);
    if (!test.exists(value, object)) {
      return check;
    }
    throw check.exc.apply(createMessage(test, true, fqn(name), value, object));
  }

  <U, V> IntCheck<E> has(IntFunction<U> property, Relation<U, V> test, V object) throws E {
    U value = property.apply(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, IntFunction.class);
    String msg = createMessage(test, false, name, value, object);
    throw check.exc.apply(msg);
  }

  <U, V> IntCheck<E> notHas(IntFunction<U> property, Relation<U, V> test, V object) throws E {
    U value = property.apply(check.arg);
    if (!test.exists(value, object)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, IntFunction.class);
    String msg = createMessage(test, true, name, value, object);
    throw check.exc.apply(msg);
  }

  <U, V> IntCheck<E> has(
      IntFunction<U> property, Relation<U, V> test, V object, String message, Object[] msgArgs)
      throws E {
    if (test.exists(property.apply(check.arg), object)) {
      return check;
    }
    throw check.createException(test, object, message, msgArgs);
  }

  <U, V, X extends Exception> IntCheck<E> has(
      IntFunction<U> property, Relation<U, V> test, V object, Supplier<X> exception) throws X {
    if (test.exists(property.apply(check.arg), object)) {
      return check;
    }
    throw exception.get();
  }

  private String fqn(String name) {
    return check.argName + "." + name;
  }
}
