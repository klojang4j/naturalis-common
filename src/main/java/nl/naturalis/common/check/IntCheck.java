package nl.naturalis.common.check;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.Relation;

final class IntCheck<E extends Exception> extends Check<Integer, E> {

  private final int arg;

  IntCheck(int arg, String argName, Function<String, E> excFactory) {
    super(argName, excFactory);
    this.arg = arg;
  }

  @Override
  public Check<Integer, E> and(Predicate<Integer> test) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, argName));
  }

  @Override
  public Check<Integer, E> and(Predicate<Integer> test, String msg, Object... msgArgs) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw excFactory.apply(String.format(msg, msgArgs));
  }

  @Override
  public IntCheck<E> and(IntPredicate test) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, argName));
  }

  @Override
  public IntCheck<E> and(IntPredicate test, String msg, Object... msgArgs) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw excFactory.apply(String.format(msg, msgArgs));
  }

  @Override
  public <U> Check<Integer, E> and(Relation<Integer, U> test, U target) throws E {
    if (test.exists(arg, target)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, argName, target));
  }

  @Override
  public <U> Check<Integer, E> and(
      Relation<Integer, U> test, U target, String msg, Object... msgArgs) throws E {
    if (test.exists(arg, target)) {
      return this;
    }
    throw excFactory.apply(String.format(msg, msgArgs));
  }

  @Override
  public IntCheck<E> and(IntRelation test, int target) throws E {
    if (test.exists(arg, target)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, argName, target));
  }

  @Override
  public IntCheck<E> and(IntRelation test, int target, String msg, Object... msgArgs) throws E {
    if (test.exists(arg, target)) {
      return this;
    }
    throw excFactory.apply(String.format(msg, msgArgs));
  }

  @Override
  public <U> Check<Integer, E> and(Function<Integer, U> getter, String propName, Predicate<U> test)
      throws E {
    throw notAnObject();
  }

  @Override
  public <U> Check<Integer, E> and(
      Function<Integer, U> getter, Predicate<U> test, String message, Object... msgArgs) throws E {
    throw notAnObject();
  }

  @Override
  public Check<Integer, E> and(ToIntFunction<Integer> getter, String propName, IntPredicate test)
      throws E {
    throw notAnObject();
  }

  @Override
  public Check<Integer, E> andAsInt(
      ToIntFunction<Integer> getter, IntPredicate test, String message, Object... msgArgs)
      throws E {
    throw notAnObject();
  }

  @Override
  public <U, V> Check<Integer, E> and(
      Function<Integer, U> getter, String propName, Relation<U, V> test, V target) throws E {
    throw notAnObject();
  }

  @Override
  public <U, V> Check<Integer, E> and(
      Function<Integer, U> getter, Relation<U, V> test, V target, String message, Object... msgArgs)
      throws E {
    throw notAnObject();
  }

  @Override
  public Check<Integer, E> and(
      ToIntFunction<Integer> getter, String propName, IntRelation test, int target) throws E {
    throw notAnObject();
  }

  @Override
  public Check<Integer, E> and(
      ToIntFunction<Integer> getter,
      IntRelation test,
      int target,
      String message,
      Object... msgArgs)
      throws E {
    throw notAnObject();
  }

  @Override
  public Integer ok() {
    return arg;
  }

  @Override
  public int intValue() {
    return arg;
  }

  private UnsupportedOperationException notAnObject() {
    String fmt = "Cannot check properties for %s (type is int)";
    return new UnsupportedOperationException(String.format(fmt, argName));
  }
}
