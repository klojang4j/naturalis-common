package nl.naturalis.common.internal;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import nl.naturalis.common.Check;
import nl.naturalis.common.ObjectMethods;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.Relation;

public class ObjectCheck<T, E extends Exception> extends Check<T, E> {

  final T arg;

  public ObjectCheck(T arg, String argName, Function<String, E> excProvider) {
    super(argName, excProvider);
    this.arg = arg;
  }

  @Override
  public <U> Check<T, E> and(
      Function<T, U> getter, Predicate<U> test, String message, Object... msgArgs) throws E {
    that(test.test(getter.apply(arg)), smash(message, msgArgs));
    return this;
  }

  @Override
  public Check<T, E> andInt(
      ToIntFunction<T> getter, IntPredicate test, String message, Object... msgArgs) throws E {
    that(test.test(getter.applyAsInt(arg)), smash(message, msgArgs));
    return this;
  }

  @Override
  public <U, V> Check<T, E> and(
      Function<T, U> getter, Relation<U, V> relation, V value, String message, Object... msgArgs)
      throws E {
    that(relation.exists(getter.apply(arg), value), smash(message, msgArgs));
    return this;
  }

  @Override
  public Check<T, E> and(
      ToIntFunction<T> getter, IntRelation relation, int value, String message, Object... msgArgs)
      throws E {
    that(relation.existsAsInt(getter.applyAsInt(arg), value), smash(message, msgArgs));
    return this;
  }

  @Override
  public ObjectCheck<T, E> notNull() throws E {
    that(arg != null, smash(ERR_NOT_NULL, argName));
    return this;
  }

  @Override
  public ObjectCheck<T, E> noneNull() throws E {
    that(ObjectMethods.isDeepNotNull(arg), smash(ERR_NONE_NULL, argName));
    return this;
  }

  @Override
  public ObjectCheck<T, E> notEmpty() throws E {
    that(ObjectMethods.isNotEmpty(arg), smash(ERR_NOT_EMPTY, argName));
    return this;
  }

  @Override
  public ObjectCheck<T, E> noneEmpty() throws E {
    that(ObjectMethods.isDeepNotEmpty(arg), smash(ERR_NONE_EMPTY, argName));
    return this;
  }

  @Override
  public Check<T, E> isNull() throws E {
    that(arg == null, smash(ERR_MUST_BE_NULL, argName));
    return this;
  }

  @Override
  public Check<T, E> isEmpty() throws E {
    that(ObjectMethods.isEmpty(arg), smash(ERR_MUST_BE_EMPTY, argName));
    return this;
  }

  @Override
  public T value() {
    return arg;
  }
}
