package nl.naturalis.common.check;

import nl.naturalis.common.function.Relation;

import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.Messages.createMessage;

class IntHasObj<E extends Exception> {

  static <E0 extends Exception> IntHasObj<E0> get(IntCheck<E0> check) {
    return new IntHasObj<>(check);
  }

  private final IntCheck<E> check;

  private IntHasObj(IntCheck<E> check) {
    this.check = check;
  }

  <P> IntCheck<E> has(IntFunction<P> property, Predicate<P> test) throws E {
    P value = property.apply(check.arg);
    if (test.test(value)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, IntFunction.class);
    String msg = createMessage(test, false, name, value);
    throw check.exc.apply(msg);
  }

  <P> IntCheck<E> notHas(IntFunction<P> property, Predicate<P> test) throws E {
    P value = property.apply(check.arg);
    if (!test.test(value)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, IntFunction.class);
    String msg = createMessage(test, true, name, value);
    throw check.exc.apply(msg);
  }

  <P> IntCheck<E> has(IntFunction<P> property, String name, Predicate<P> test) throws E {
    P value = property.apply(check.arg);
    if (test.test(value)) {
      return check;
    }
    throw check.exc.apply(createMessage(test, false, FQN(name), value));
  }

  <P> IntCheck<E> notHas(IntFunction<P> property, String name, Predicate<P> test) throws E {
    P value = property.apply(check.arg);
    if (!test.test(value)) {
      return check;
    }
    throw check.exc.apply(createMessage(test, true, FQN(name), value));
  }

  <P> IntCheck<E> has(IntFunction<P> property, Predicate<P> test, String message, Object[] msgArgs)
      throws E {
    P value = property.apply(check.arg);
    if (test.test(value)) {
      return check;
    }
    throw check.createException(test, message, msgArgs);
  }

  <P, X extends Exception> IntCheck<E> has(
      IntFunction<P> property, Predicate<P> test, Supplier<X> exception) throws X {
    if (test.test(property.apply(check.arg))) {
      return check;
    }
    throw exception.get();
  }

  <P, O> IntCheck<E> has(IntFunction<P> property, String name, Relation<P, O> test, O object)
      throws E {
    P value = property.apply(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    throw check.exc.apply(createMessage(test, false, FQN(name), value, object));
  }

  <P, O> IntCheck<E> notHas(IntFunction<P> property, String name, Relation<P, O> test, O object)
      throws E {
    P value = property.apply(check.arg);
    if (!test.exists(value, object)) {
      return check;
    }
    throw check.exc.apply(createMessage(test, true, FQN(name), value, object));
  }

  <P, O> IntCheck<E> has(IntFunction<P> property, Relation<P, O> test, O object) throws E {
    P value = property.apply(check.arg);
    if (test.exists(value, object)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, IntFunction.class);
    String msg = createMessage(test, false, name, value, object);
    throw check.exc.apply(msg);
  }

  <P, O> IntCheck<E> notHas(IntFunction<P> property, Relation<P, O> test, O object) throws E {
    P value = property.apply(check.arg);
    if (!test.exists(value, object)) {
      return check;
    }
    String name = formatProperty(check.arg, check.argName, property, IntFunction.class);
    String msg = createMessage(test, true, name, value, object);
    throw check.exc.apply(msg);
  }

  <P, O> IntCheck<E> has(
      IntFunction<P> property, Relation<P, O> test, O object, String message, Object[] msgArgs)
      throws E {
    if (test.exists(property.apply(check.arg), object)) {
      return check;
    }
    throw check.createException(test, object, message, msgArgs);
  }

  <P, O, X extends Exception> IntCheck<E> has(
      IntFunction<P> property, Relation<P, O> test, O object, Supplier<X> exception) throws X {
    if (test.exists(property.apply(check.arg), object)) {
      return check;
    }
    throw exception.get();
  }

  private String FQN(String name) {
    return check.argName + "." + name;
  }
}
