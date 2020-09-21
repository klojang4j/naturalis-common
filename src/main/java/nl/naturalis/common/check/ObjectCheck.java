package nl.naturalis.common.check;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;

class ObjectCheck<T, E extends Exception> extends Check<T, E> {

  private static final String ERR_INT_VALUE = "Cannot return int value for %s";
  private static final String ERR_NULL_TO_INT = ERR_INT_VALUE + " (was null)";
  private static final String ERR_NUMBER_TO_INT = ERR_INT_VALUE + " (was %s)";
  private static final String ERR_STRING_TO_INT = ERR_INT_VALUE + " (was \"%s\")";
  private static final String ERR_OBJECT_TO_INT = ERR_INT_VALUE + " (%s)";
  private static final String ERR_NOT_APPLICABLE = "Test not applicable to %s (%s)";

  final T arg;

  ObjectCheck(T arg, String argName, Function<String, E> excFactory) {
    super(argName, excFactory);
    this.arg = arg;
  }

  @Override
  public Check<T, E> and(Predicate<T> test, String message, Object... msgArgs) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
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
  public Check<T, E> and(IntPredicate test, String message, Object... msgArgs) throws E {
    if (arg.getClass() == Integer.class) {
      if (test.test((Integer) arg)) {
        return this;
      }
      throw excFactory.apply(String.format(message, msgArgs));
    }
    throw notApplicable();
  }

  @Override
  public Check<T, E> and(IntRelation test, int relateTo) throws E {
    if (arg.getClass() == Integer.class) {
      if (test.exists((Integer) arg, relateTo)) {
        return this;
      }
      throw excFactory.apply(Messages.get(test, arg, argName, relateTo));
    }
    throw notApplicable();
  }

  @Override
  public Check<T, E> and(IntRelation test, int relateTo, String message, Object... msgArgs)
      throws E {
    if (arg.getClass() == Integer.class) {
      if (test.exists((Integer) arg, relateTo)) {
        return this;
      }
      throw excFactory.apply(String.format(message, msgArgs));
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
      Function<T, U> getter, Predicate<U> test, String message, Object... msgArgs) throws E {
    if (test.test(getter.apply(arg))) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public Check<T, E> andAsInt(ToIntFunction<T> getter, String propName, IntPredicate test)
      throws E {
    if (test.test(getter.applyAsInt(arg))) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, prop(propName)));
  }

  @Override
  public Check<T, E> andAsInt(
      ToIntFunction<T> getter, IntPredicate test, String message, Object... msgArgs) throws E {
    if (test.test(getter.applyAsInt(arg))) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public <U, V> Check<T, E> and(
      Function<T, U> getter, String propName, Relation<U, V> relation, V relateTo) throws E {
    if (relation.exists(getter.apply(arg), relateTo)) {
      return this;
    }
    throw excFactory.apply(Messages.get(relation, arg, prop(propName), relateTo));
  }

  @Override
  public <U, V> Check<T, E> and(
      Function<T, U> getter, Relation<U, V> relation, V relateTo, String message, Object... msgArgs)
      throws E {
    if (relation.exists(getter.apply(arg), relateTo)) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public <U> Check<T, E> and(
      Function<T, U> getter, String propName, ObjIntRelation<U> relation, int relateTo) throws E {
    if (relation.exists(getter.apply(arg), relateTo)) {
      return this;
    }
    throw excFactory.apply(Messages.get(relation, arg, prop(propName), relateTo));
  }

  @Override
  public <U> Check<T, E> and(
      Function<T, U> getter,
      ObjIntRelation<U> relation,
      int relateTo,
      String message,
      Object... msgArgs)
      throws E {
    if (relation.exists(getter.apply(arg), relateTo)) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public Check<T, E> and(ToIntFunction<T> getter, String propName, IntRelation test, int relateTo)
      throws E {
    if (test.exists(getter.applyAsInt(arg), relateTo)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, prop(propName), relateTo));
  }

  @Override
  public Check<T, E> and(
      ToIntFunction<T> getter, IntRelation test, int relateTo, String message, Object... msgArgs)
      throws E {
    if (test.exists(getter.applyAsInt(arg), relateTo)) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public T ok() {
    return arg;
  }

  @Override
  public int intValue() {
    if (arg == null) {
      String message = String.format(ERR_NULL_TO_INT, argName);
      throw new UnsupportedOperationException(message);
    } else if (arg instanceof Number) {
      Number n = (Number) arg;
      if (NumberMethods.isLossless(n, Integer.class)) {
        return n.intValue();
      }
      String message = String.format(ERR_NUMBER_TO_INT, argName, n);
      throw new UnsupportedOperationException(message);
    } else if (arg instanceof CharSequence) {
      try {
        Double d = Double.valueOf(arg.toString());
        if (NumberMethods.isLossless(d, Integer.class)) {
          return d.intValue();
        }
        String message = String.format(ERR_STRING_TO_INT, argName, arg);
        throw new UnsupportedOperationException(message);
      } catch (NumberFormatException e) {
        String message = String.format(ERR_STRING_TO_INT, argName, arg);
        throw new UnsupportedOperationException(message);
      }
    }
    String message = String.format(ERR_OBJECT_TO_INT, argName, arg.getClass().getName());
    throw new UnsupportedOperationException(message);
  }

  private String prop(String name) {
    return argName + "." + name;
  }

  private UnsupportedOperationException notApplicable() {
    if (arg == null) {
      String message = String.format(ERR_NOT_APPLICABLE, argName, "was null");
      throw new UnsupportedOperationException(message);
    }
    String message = String.format(ERR_NOT_APPLICABLE, argName, arg.getClass().getName());
    return new UnsupportedOperationException(message);
  }
}
