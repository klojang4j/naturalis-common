package nl.naturalis.common.check;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.Relation;

class ObjectCheck<T, E extends Exception> extends Check<T, E> {

  private static final String ERR_NO_INT_VALUE = "Cannot return int value for %s (%s)";
  private static final String ERR_NOT_APPLICABLE = "Test not applicable to %s (%s)";

  final T arg;

  ObjectCheck(T arg, String argName, Function<String, E> excFactory) {
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
  public Check<T, E> and(IntPredicate test) throws E {
    if (arg.getClass() == Integer.class) {
      if (test.test((Integer) arg)) {
        return this;
      }
      throw excFactory.apply(Messages.get(test, arg, argName));
    }
    throw notApplicable();
  }

  @Override
  public Check<T, E> and(IntPredicate test, String msg, Object... msgArgs) throws E {
    if (arg.getClass() == Integer.class) {
      if (test.test((Integer) arg)) {
        return this;
      }
      throw excFactory.apply(String.format(msg, msgArgs));
    }
    throw notApplicable();
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
  public Check<T, E> and(IntRelation test, int target) throws E {
    if (arg.getClass() == Integer.class) {
      if (test.exists((Integer) arg, target)) {
        return this;
      }
      throw excFactory.apply(Messages.get(test, arg, argName, target));
    }
    throw notApplicable();
  }

  @Override
  public Check<T, E> and(IntRelation test, int target, String msg, Object... msgArgs) throws E {
    if (arg.getClass() == Integer.class) {
      if (test.exists((Integer) arg, target)) {
        return this;
      }
      throw excFactory.apply(String.format(msg, msgArgs));
    }
    throw notApplicable();
  }

  @Override
  public <U> Check<T, E> and(Function<T, U> getter, String propName, Predicate<U> test) throws E {
    if (test.test(getter.apply(arg))) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, prop(propName)));
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
  public Check<T, E> and(ToIntFunction<T> getter, String propName, IntPredicate test) throws E {
    if (test.test(getter.applyAsInt(arg))) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, prop(propName)));
  }

  @Override
  public Check<T, E> andAsInt(
      ToIntFunction<T> getter, IntPredicate test, String msg, Object... msgArgs) throws E {
    if (test.test(getter.applyAsInt(arg))) {
      return this;
    }
    throw excFactory.apply(String.format(msg, msgArgs));
  }

  @Override
  public <U, V> Check<T, E> and(
      Function<T, U> getter, String propName, Relation<U, V> test, V target) throws E {
    if (test.exists(getter.apply(arg), target)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, prop(propName), target));
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
  public Check<T, E> and(ToIntFunction<T> getter, String propName, IntRelation test, int target)
      throws E {
    if (test.exists(getter.applyAsInt(arg), target)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, prop(propName), target));
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

  @Override
  public int intValue() {
    if (arg == null) {
      String msg = String.format(ERR_NO_INT_VALUE, argName, "was null");
      throw new UnsupportedOperationException(msg);
    } else if (arg instanceof Number) {
      return ((Number) arg).intValue();
    }
    String msg = String.format(ERR_NO_INT_VALUE, argName, arg.getClass().getName());
    throw new UnsupportedOperationException(msg);
  }

  private String prop(String name) {
    return argName + "." + name;
  }

  private UnsupportedOperationException notApplicable() {
    if (arg == null) {
      String msg = String.format(ERR_NOT_APPLICABLE, argName, "was null");
      throw new UnsupportedOperationException(msg);
    }
    String msg = String.format(ERR_NOT_APPLICABLE, argName, arg.getClass().getName());
    return new UnsupportedOperationException(msg);
  }
}
