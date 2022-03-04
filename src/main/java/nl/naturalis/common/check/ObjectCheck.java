package nl.naturalis.common.check;

import nl.naturalis.common.function.*;

import java.util.function.*;
import java.util.stream.IntStream;

import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.check.Check.DEF_ARG_NAME;
import static nl.naturalis.common.check.CommonChecks.NAMES;
import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.Messages.createMessage;

public final class ObjectCheck<T, E extends Exception> {

  private static final String ERR_INT_VALUE = "Cannot return int value for %s";
  private static final String ERR_NULL_TO_INT = ERR_INT_VALUE + " (was null)";
  private static final String ERR_NUMBER_TO_INT = ERR_INT_VALUE + " (was %s)";
  private static final String ERR_OBJECT_TO_INT = ERR_INT_VALUE + " (%s)";

  final T arg;
  final String argName;
  final Function<String, E> exc;

  ObjectCheck(T arg, String argName, Function<String, E> exc) {
    this.arg = arg;
    this.argName = argName;
    this.exc = exc;
  }

  public ObjectCheck<T, E> is(Predicate<T> test) throws E {
    if (test.test(arg)) {
      return this;
    }
    String msg = createMessage(test, false, getArgName(arg), arg);
    throw exc.apply(msg);
  }

  public ObjectCheck<T, E> isNot(Predicate<T> test) throws E {
    if (!test.test(arg)) {
      return this;
    }
    String msg = createMessage(test, true, getArgName(arg), arg);
    throw exc.apply(msg);
  }

  public ObjectCheck<T, E> is(Predicate<T> test, String message, Object... msgArgs) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw exception(test, message, msgArgs);
  }

  public ObjectCheck<T, E> isNot(Predicate<T> test, String message, Object... msgArgs) throws E {
    return is(test.negate(), message, msgArgs);
  }

  public <X extends Exception> ObjectCheck<T, E> is(Predicate<T> test, Supplier<X> exception)
      throws X {
    if (test.test(arg)) {
      return this;
    }
    throw exception.get();
  }

  public <X extends Exception> ObjectCheck<T, E> isNot(Predicate<T> test, Supplier<X> exception)
      throws X {
    return is(test.negate(), exception);
  }

  public <U> ObjectCheck<T, E> is(Relation<T, U> test, U object) throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, false, getArgName(arg), arg, object);
    throw exc.apply(msg);
  }

  public <U> ObjectCheck<T, E> isNot(Relation<T, U> test, U object) throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, true, getArgName(arg), arg, object);
    throw exc.apply(msg);
  }

  public <U> ObjectCheck<T, E> is(Relation<T, U> test, U object, String message, Object... msgArgs)
      throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    throw exception(test, object, message, msgArgs);
  }

  public <U> ObjectCheck<T, E> isNot(
      Relation<T, U> test, U object, String message, Object... msgArgs) throws E {
    return is(test.negate(), object, message, msgArgs);
  }

  public <U, X extends Exception> ObjectCheck<T, E> is(
      Relation<T, U> test, U object, Supplier<X> exception) throws X {
    if (test.exists(arg, object)) {
      return this;
    }
    throw exception.get();
  }

  public <U, X extends Exception> ObjectCheck<T, E> isNot(
      Relation<T, U> test, U object, Supplier<X> exception) throws X {
    return is(test.negate(), object, exception);
  }

  public ObjectCheck<T, E> is(ObjIntRelation<T> test, int object) throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, false, getArgName(arg), arg, object);
    throw exc.apply(msg);
  }

  public ObjectCheck<T, E> isNot(ObjIntRelation<T> test, int object) throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, true, getArgName(arg), arg, object);
    throw exc.apply(msg);
  }

  public ObjectCheck<T, E> is(ObjIntRelation<T> test, int object, String message, Object... msgArgs)
      throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    throw exception(test, object, message, msgArgs);
  }

  public ObjectCheck<T, E> isNot(
      ObjIntRelation<T> test, int object, String message, Object... msgArgs) throws E {
    return is(test.negate(), object, message, msgArgs);
  }

  public <X extends Exception> ObjectCheck<T, E> is(
      ObjIntRelation<T> test, int object, Supplier<X> exception) throws X {
    if (test.exists(arg, object)) {
      return this;
    }
    throw exception.get();
  }

  public <X extends Exception> ObjectCheck<T, E> isNot(
      ObjIntRelation<T> test, int object, Supplier<X> exception) throws X {
    return is(test.negate(), object, exception);
  }

  public <U> ObjectCheck<T, E> has(Function<T, U> property, Predicate<U> test) throws E {
    return ObjHasObj.get(this).has(property, test);
  }

  public <U> ObjectCheck<T, E> notHas(Function<T, U> property, Predicate<U> test) throws E {
    return ObjHasObj.get(this).notHas(property, test);
  }

  public <U> ObjectCheck<T, E> has(Function<T, U> property, String name, Predicate<U> test)
      throws E {
    return ObjHasObj.get(this).has(property, name, test);
  }

  public <U> ObjectCheck<T, E> notHas(Function<T, U> property, String name, Predicate<U> test)
      throws E {
    return ObjHasObj.get(this).notHas(property, name, test);
  }

  public <U> ObjectCheck<T, E> has(
      Function<T, U> property, Predicate<U> test, String message, Object... msgArgs) throws E {
    return ObjHasObj.get(this).has(property, test, message, msgArgs);
  }

  public <U> ObjectCheck<T, E> notHas(
      Function<T, U> property, Predicate<U> test, String message, Object... msgArgs) throws E {
    return ObjHasObj.get(this).has(property, test.negate(), message, msgArgs);
  }

  public <U, X extends Exception> ObjectCheck<T, E> has(
      Function<T, U> property, Predicate<U> test, Supplier<X> exception) throws X {
    return ObjHasObj.get(this).has(property, test, exception);
  }

  public <U, X extends Exception> ObjectCheck<T, E> notHas(
      Function<T, U> property, Predicate<U> test, Supplier<X> exception) throws X {
    return ObjHasObj.get(this).has(property, test.negate(), exception);
  }

  public <U, V> ObjectCheck<T, E> has(Function<T, U> property, Relation<U, V> test, V object)
      throws E {
    return ObjHasObj.get(this).has(property, test, object);
  }

  public <U, V> ObjectCheck<T, E> notHas(Function<T, U> property, Relation<U, V> test, V object)
      throws E {
    return ObjHasObj.get(this).notHas(property, test, object);
  }

  public <U, V> ObjectCheck<T, E> has(
      Function<T, U> property, String name, Relation<U, V> test, V object) throws E {
    return ObjHasObj.get(this).has(property, name, test, object);
  }

  public <U, V> ObjectCheck<T, E> notHas(
      Function<T, U> property, String name, Relation<U, V> test, V object) throws E {
    return ObjHasObj.get(this).notHas(property, name, test, object);
  }

  public <U, V> ObjectCheck<T, E> has(
      Function<T, U> property, Relation<U, V> test, V object, String message, Object... msgArgs)
      throws E {
    return ObjHasObj.get(this).has(property, test, object, message, msgArgs);
  }

  public <U, V> ObjectCheck<T, E> notHas(
      Function<T, U> property, Relation<U, V> test, V object, String message, Object... msgArgs)
      throws E {
    return ObjHasObj.get(this).has(property, test.negate(), object, message, msgArgs);
  }

  public <U> ObjectCheck<T, E> has(Function<T, U> property, ObjIntRelation<U> test, int object)
      throws E {
    return ObjHasObj.get(this).has(property, test, object);
  }

  public <U> ObjectCheck<T, E> notHas(Function<T, U> property, ObjIntRelation<U> test, int object)
      throws E {
    return ObjHasObj.get(this).notHas(property, test, object);
  }

  public <U> ObjectCheck<T, E> has(
      Function<T, U> property, String name, ObjIntRelation<U> test, int object) throws E {
    return ObjHasObj.get(this).has(property, name, test, object);
  }

  public <U> ObjectCheck<T, E> notHas(
      Function<T, U> property, String name, ObjIntRelation<U> test, int object) throws E {
    return ObjHasObj.get(this).notHas(property, name, test, object);
  }

  public <U> ObjectCheck<T, E> has(
      Function<T, U> property,
      ObjIntRelation<U> test,
      int object,
      String message,
      Object... msgArgs)
      throws E {
    return ObjHasObj.get(this).has(property, test, object, message, msgArgs);
  }

  public <U> ObjectCheck<T, E> notHas(
      Function<T, U> property,
      ObjIntRelation<U> test,
      int object,
      String message,
      Object... msgArgs)
      throws E {
    return ObjHasObj.get(this).has(property, test.negate(), object, message, msgArgs);
  }

  public ObjectCheck<T, E> has(ToIntFunction<T> property, String name, IntPredicate test) throws E {
    int value = property.applyAsInt(arg);
    if (test.test(value)) {
      return this;
    }
    String msg = createMessage(test, false, fqn(name), value);
    throw exc.apply(msg);
  }

  public ObjectCheck<T, E> notHas(ToIntFunction<T> property, String name, IntPredicate test)
      throws E {
    int value = property.applyAsInt(arg);
    if (!test.test(value)) {
      return this;
    }
    String msg = createMessage(test, true, fqn(name), value);
    throw exc.apply(msg);
  }

  public ObjectCheck<T, E> has(ToIntFunction<T> property, IntPredicate test) throws E {
    int value = property.applyAsInt(arg);
    if (test.test(value)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, ToIntFunction.class);
    String msg = createMessage(test, false, name, value);
    throw exc.apply(msg);
  }

  public ObjectCheck<T, E> notHas(ToIntFunction<T> property, IntPredicate test) throws E {
    int value = property.applyAsInt(arg);
    if (!test.test(value)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, ToIntFunction.class);
    String msg = createMessage(test, true, name, value);
    throw exc.apply(msg);
  }

  public ObjectCheck<T, E> has(
      ToIntFunction<T> property, IntPredicate test, String message, Object... msgArgs) throws E {
    int value = property.applyAsInt(arg);
    if (test.test(value)) {
      return this;
    }
    throw exception(test, message, msgArgs);
  }

  public ObjectCheck<T, E> notHas(
      ToIntFunction<T> property, IntPredicate test, String message, Object... msgArgs) throws E {
    return has(property, test.negate(), message, msgArgs);
  }

  public <U> ObjectCheck<T, E> has(
      ToIntFunction<T> property, String name, IntObjRelation<U> test, U object) throws E {
    int value = property.applyAsInt(arg);
    if (test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw exc.apply(msg);
  }

  public <U> ObjectCheck<T, E> notHas(
      ToIntFunction<T> property, String name, IntObjRelation<U> test, U object) throws E {
    int value = property.applyAsInt(arg);
    if (!test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw exc.apply(msg);
  }

  public <U> ObjectCheck<T, E> has(ToIntFunction<T> property, IntObjRelation<U> test, U object)
      throws E {
    int value = property.applyAsInt(arg);
    if (test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, Function.class);
    String msg = createMessage(test, false, name, value, object);
    throw exc.apply(msg);
  }

  public <U> ObjectCheck<T, E> notHas(ToIntFunction<T> property, IntObjRelation<U> test, U object)
      throws E {
    int value = property.applyAsInt(arg);
    if (!test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, Function.class);
    String msg = createMessage(test, true, name, value, object);
    throw exc.apply(msg);
  }

  public <U> ObjectCheck<T, E> has(
      ToIntFunction<T> property,
      IntObjRelation<U> test,
      U object,
      String message,
      Object... msgArgs)
      throws E {
    int value = property.applyAsInt(arg);
    if (test.exists(value, object)) {
      return this;
    }
    throw exception(test, object, message, msgArgs);
  }

  public <U> ObjectCheck<T, E> notHas(
      ToIntFunction<T> property,
      IntObjRelation<U> test,
      U object,
      String message,
      Object... msgArgs)
      throws E {
    return has(property, test, object, message, msgArgs);
  }

  public ObjectCheck<T, E> has(ToIntFunction<T> property, String name, IntRelation test, int object)
      throws E {
    int value = property.applyAsInt(arg);
    if (test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, false, fqn(name), value, object);
    throw exc.apply(msg);
  }

  public ObjectCheck<T, E> notHas(
      ToIntFunction<T> property, String name, IntRelation test, int object) throws E {
    int value = property.applyAsInt(arg);
    if (!test.exists(value, object)) {
      return this;
    }
    String msg = createMessage(test, true, fqn(name), value, object);
    throw exc.apply(msg);
  }

  public ObjectCheck<T, E> has(ToIntFunction<T> property, IntRelation test, int object) throws E {
    int value = property.applyAsInt(arg);
    if (test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, ToIntFunction.class);
    String msg = createMessage(test, false, name, value, object);
    throw exc.apply(msg);
  }

  public ObjectCheck<T, E> notHas(ToIntFunction<T> property, IntRelation test, int object)
      throws E {
    int value = property.applyAsInt(arg);
    if (!test.exists(value, object)) {
      return this;
    }
    String name = formatProperty(arg, argName, property, ToIntFunction.class);
    String msg = createMessage(test, true, name, value, object);
    throw exc.apply(msg);
  }

  public ObjectCheck<T, E> has(
      ToIntFunction<T> property, IntRelation test, int object, String message, Object... msgArgs)
      throws E {
    int value = property.applyAsInt(arg);
    if (test.exists(value, object)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw exc.apply(msg);
  }

  public ObjectCheck<T, E> notHas(
      ToIntFunction<T> property, IntRelation test, int object, String message, Object... msgArgs)
      throws E {
    return has(property, test.negate(), object, message, msgArgs);
  }

  public T ok() {
    return arg;
  }

  public int intValue() throws E {
    if (arg == null) {
      String msg = String.format(ERR_NULL_TO_INT, getArgName(arg));
      throw exc.apply(msg);
    } else if (arg.getClass() == Integer.class) {
      return (Integer) arg;
    } else if (arg.getClass().isEnum()) {
      return ((Enum) arg).ordinal();
    } else if (arg.getClass() == Byte.class) {
      return (Byte) arg;
    } else if (arg.getClass() == Short.class) {
      return (Short) arg;
    }
    String msg = String.format(ERR_OBJECT_TO_INT, getArgName(arg), arg.getClass().getName());
    throw exc.apply(msg);
  }

  @SuppressWarnings({"raw-types"})
  private boolean applicable() {
    Class c = arg.getClass();
    return c == Integer.class || c == Short.class || c == Byte.class;
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
  public <U, F extends Throwable> U ok(ThrowingFunction<T, U, F> transformer) throws F {
    return transformer.apply(arg);
  }

  /**
   * Passes the validated argument to the specified {@code Consumer}. To be used as the last call
   * after a chain of checks.
   *
   * @param consumer The {@code Consumer}
   */
  public <F extends Throwable> void then(ThrowingConsumer<T, F> consumer) throws F {
    consumer.accept(arg);
  }

  E exception(Object test, String msg, Object[] msgArgs) {
    return exception(test, null, msg, msgArgs);
  }

  E exception(Object test, Object object, String msg, Object[] msgArgs) {
    return exception(test, arg, object, msg, msgArgs);
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
    all[2] = ifNotNull(subject, ObjectCheck::className);
    all[3] = argName;
    all[4] = Messages.toStr(object);
    System.arraycopy(msgArgs, 0, all, 5, msgArgs.length);
    return exc.apply(String.format(fmt, all));
  }

  String getArgName(Object arg) {
    return argName != null ? argName : arg != null ? className(arg) : DEF_ARG_NAME;
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
