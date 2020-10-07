package nl.naturalis.common.check;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;
import static nl.naturalis.common.check.CommonGetters.getGetterName;

class ObjectCheck<T, E extends Exception> extends Check<T, E> {

  private static final String ERR_INT_VALUE = "Cannot return int value for %s";
  private static final String ERR_NULL_TO_INT = ERR_INT_VALUE + " (was null)";
  private static final String ERR_NUMBER_TO_INT = ERR_INT_VALUE + " (was %s)";
  private static final String ERR_OBJECT_TO_INT = ERR_INT_VALUE + " (%s)";
  private static final String ERR_NOT_APPLICABLE = "Test not applicable to argument %s (%s)";

  final T arg;

  ObjectCheck(T arg, String argName, Function<String, E> excFactory) {
    super(argName, excFactory);
    this.arg = arg;
  }

  @Override
  public Check<T, E> is(Predicate<T> test, String message, Object... msgArgs) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public Check<T, E> is(IntPredicate test) throws E {
    if (arg.getClass() == Integer.class) {
      if (test.test(((Integer) arg).intValue())) {
        return this;
      }
      throw excFactory.apply(Messages.get(test, arg, argName));
    }
    throw notApplicable();
  }

  @Override
  public Check<T, E> is(IntPredicate test, String message, Object... msgArgs) throws E {
    if (arg.getClass() == Integer.class) {
      if (test.test(((Integer) arg).intValue())) {
        return this;
      }
      throw excFactory.apply(String.format(message, msgArgs));
    }
    throw notApplicable();
  }

  @Override
  public <U> Check<T, E> is(IntObjRelation<U> relation, U relateTo) throws E {
    if (arg.getClass() == Integer.class) {
      if (relation.exists(((Integer) arg).intValue(), relateTo)) {
        return this;
      }
    }
    throw excFactory.apply(Messages.get(relation, arg, argName, relateTo));
  }

  @Override
  public <U> Check<T, E> is(
      IntObjRelation<U> relation, U relateTo, String message, Object... msgArgs) throws E {
    if (arg.getClass() == Integer.class) {
      if (relation.exists(((Integer) arg).intValue(), relateTo)) {
        return this;
      }
      throw excFactory.apply(String.format(message, msgArgs));
    }
    throw notApplicable();
  }

  @Override
  public Check<T, E> is(IntRelation relation, int relateTo) throws E {
    if (arg.getClass() == Integer.class) {
      if (relation.exists(((Integer) arg).intValue(), relateTo)) {
        return this;
      }
      throw excFactory.apply(Messages.get(relation, arg, argName, relateTo));
    }
    throw notApplicable();
  }

  @Override
  public Check<T, E> is(IntRelation relation, int relateTo, String message, Object... msgArgs)
      throws E {
    if (arg.getClass() == Integer.class) {
      if (relation.exists(((Integer) arg).intValue(), relateTo)) {
        return this;
      }
      throw excFactory.apply(String.format(message, msgArgs));
    }
    throw notApplicable();
  }

  @Override
  public <U> Check<T, E> has(Function<T, U> getter, String property, Predicate<U> test) throws E {
    U propVal = getter.apply(arg);
    if (test.test(propVal)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, propVal, propName(property)));
  }

  @Override
  public <U> Check<T, E> has(Function<T, U> getter, Predicate<U> test) throws E {
    return has(getter, getGetterName(getter), test);
  }

  @Override
  public <U> Check<T, E> has(
      Function<T, U> getter, Predicate<U> test, String message, Object... msgArgs) throws E {
    if (test.test(getter.apply(arg))) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public Check<T, E> has(ToIntFunction<T> getter, String property, IntPredicate test) throws E {
    int propVal = getter.applyAsInt(arg);
    if (test.test(propVal)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, propVal, propName(property)));
  }

  @Override
  public Check<T, E> has(ToIntFunction<T> getter, IntPredicate test) throws E {
    return has(getter, getGetterName(getter), test);
  }

  @Override
  public Check<T, E> has(
      ToIntFunction<T> getter, IntPredicate test, String message, Object... msgArgs) throws E {
    if (test.test(getter.applyAsInt(arg))) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public <U, V> Check<T, E> has(
      Function<T, U> getter, String property, Relation<U, V> relation, V relateTo) throws E {
    U propVal = getter.apply(arg);
    if (relation.exists(propVal, relateTo)) {
      return this;
    }
    throw excFactory.apply(Messages.get(relation, propVal, propName(property), relateTo));
  }

  @Override
  public <U, V> Check<T, E> has(Function<T, U> getter, Relation<U, V> relation, V relateTo)
      throws E {
    return has(getter, getGetterName(getter), relation, relateTo);
  }

  @Override
  public <U, V> Check<T, E> has(
      Function<T, U> getter, Relation<U, V> relation, V relateTo, String message, Object... msgArgs)
      throws E {
    if (relation.exists(getter.apply(arg), relateTo)) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public <U> Check<T, E> has(
      Function<T, U> getter, String property, ObjIntRelation<U> relation, int relateTo) throws E {
    U propVal = getter.apply(arg);
    if (relation.exists(propVal, relateTo)) {
      return this;
    }
    throw excFactory.apply(Messages.get(relation, propVal, propName(property), relateTo));
  }

  @Override
  public <U> Check<T, E> has(Function<T, U> getter, ObjIntRelation<U> relation, int relateTo)
      throws E {
    return has(getter, getGetterName(getter), relation, relateTo);
  }

  @Override
  public <U> Check<T, E> has(
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
  public Check<T, E> has(ToIntFunction<T> getter, String property, IntRelation relation, int relateTo)
      throws E {
    int propVal = getter.applyAsInt(arg);
    if (relation.exists(propVal, relateTo)) {
      return this;
    }
    throw excFactory.apply(Messages.get(relation, propVal, propName(property), relateTo));
  }

  @Override
  public Check<T, E> has(ToIntFunction<T> getter, IntRelation relation, int relateTo) throws E {
    return has(getter, getGetterName(getter), relation, relateTo);
  }

  @Override
  public Check<T, E> has(
      ToIntFunction<T> getter, IntRelation relation, int relateTo, String message, Object... msgArgs)
      throws E {
    if (relation.exists(getter.applyAsInt(arg), relateTo)) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public T ok() {
    return arg;
  }

  @Override
  public int intValue() throws E {
    if (arg == null) {
      String msg = String.format(ERR_NULL_TO_INT, argName);
      throw excFactory.apply(msg);
    } else if (arg instanceof Number) {
      Number n = (Number) arg;
      if (NumberMethods.fitsInto(n, Integer.class)) {
        return n.intValue();
      }
      String msg = String.format(ERR_NUMBER_TO_INT, argName, n);
      throw excFactory.apply(msg);
    }
    String msg = String.format(ERR_OBJECT_TO_INT, argName, arg.getClass().getName());
    throw excFactory.apply(msg);
  }

  private String propName(String name) {
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
