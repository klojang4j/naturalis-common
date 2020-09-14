package nl.naturalis.common.check;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.Relation;

public class ObjectCheck<T, E extends Exception> extends Check<T, E> {

  final T arg;

  public ObjectCheck(T arg, String argName, Function<String, E> excFactory) {
    super(argName, excFactory);
    this.arg = arg;
  }

  @Override
  public Check<T, E> and(Predicate<T> test) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, argName));
  }

  @Override
  public Check<T, E> and(Predicate<T> test, String msg, Object... msgArgs) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw excFactory.apply(String.format(msg, msgArgs));
  }

  @Override
  public <U> Check<T, E> and(Function<T, U> getter, Predicate<U> test) throws E {
    if (test.test(getter.apply(arg))) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, argName));
  }

  @Override
  public <U> Check<T, E> and(
      Function<T, U> getter, Predicate<U> test, String msg, Object... msgArgs) throws E {
    if (test.test(getter.apply(arg))) {
      return this;
    }
    throw excFactory.apply(String.format(msg, msgArgs));
  }

  @Override
  public Check<T, E> and(ToIntFunction<T> getter, IntPredicate test) throws E {
    if (test.test(getter.applyAsInt(arg))) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, argName));
  }

  @Override
  public Check<T, E> and(ToIntFunction<T> getter, IntPredicate test, String msg, Object... msgArgs)
      throws E {
    if (test.test(getter.applyAsInt(arg))) {
      return this;
    }
    throw excFactory.apply(String.format(msg, msgArgs));
  }

  @Override
  public <U> Check<T, E> and(Relation<T, U> test, U target) throws E {
    if (test.exists(arg, target)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, argName, target));
  }

  @Override
  public <U> Check<T, E> and(Relation<T, U> test, U target, String msg, Object... msgArgs)
      throws E {
    if (test.exists(arg, target)) {
      return this;
    }
    throw excFactory.apply(String.format(msg, msgArgs));
  }

  @Override
  public <U, V> Check<T, E> and(Function<T, U> getter, Relation<U, V> test, V target) throws E {
    if (test.exists(getter.apply(arg), target)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, argName, target));
  }

  @Override
  public <U, V> Check<T, E> and(
      Function<T, U> getter, Relation<U, V> test, V target, String msg, Object... msgArgs)
      throws E {
    if (test.exists(getter.apply(arg), target)) {
      return this;
    }
    throw excFactory.apply(String.format(msg, msgArgs));
  }

  @Override
  public Check<T, E> and(ToIntFunction<T> getter, IntRelation test, int target) throws E {
    if (test.exists(getter.applyAsInt(arg), target)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, argName, target));
  }

  @Override
  public Check<T, E> and(
      ToIntFunction<T> getter, IntRelation test, int target, String msg, Object... msgArgs)
      throws E {
    if (test.exists(getter.applyAsInt(arg), target)) {
      return this;
    }
    throw excFactory.apply(String.format(msg, msgArgs));
  }

  @Override
  public T ok() {
    return arg;
  }
}
