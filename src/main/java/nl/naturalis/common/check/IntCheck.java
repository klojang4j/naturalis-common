package nl.naturalis.common.check;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;
import static nl.naturalis.common.check.CommonGetters.getGetterName;

final class IntCheck<E extends Exception> extends Check<Integer, E> {

  private final int arg;

  IntCheck(int arg, String argName, Function<String, E> excFactory) {
    super(argName, excFactory);
    this.arg = arg;
  }

  @Override
  public IntCheck<E> is(IntPredicate test) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, arg, argName));
  }

  @Override
  public IntCheck<E> is(IntPredicate test, String message, Object... msgArgs) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public <U> Check<Integer, E> is(IntObjRelation<U> relation, U relateTo) throws E {
    if (relation.exists(arg, relateTo)) {
      return this;
    }
    throw excFactory.apply(Messages.get(relation, arg, argName, relateTo));
  }

  @Override
  public <U> Check<Integer, E> is(
      IntObjRelation<U> relation, U relateTo, String message, Object... msgArgs) throws E {
    if (relation.exists(arg, relateTo)) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public IntCheck<E> is(IntRelation relation, int relateTo) throws E {
    if (relation.exists(arg, relateTo)) {
      return this;
    }
    throw excFactory.apply(Messages.get(relation, arg, argName, relateTo));
  }

  @Override
  public IntCheck<E> is(IntRelation relation, int relateTo, String message, Object... msgArgs)
      throws E {
    if (relation.exists(arg, relateTo)) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public <U> Check<Integer, E> has(Function<Integer, U> getter, String propName, Predicate<U> test)
      throws E {
    throw notAnObject();
  }

  @Override
  public <U> Check<Integer, E> has(Function<Integer, U> getter, Predicate<U> test) throws E {
    throw notAnObject();
  }

  @Override
  public <U> Check<Integer, E> has(
      Function<Integer, U> getter, Predicate<U> test, String message, Object... msgArgs) throws E {
    throw notAnObject();
  }

  @Override
  public Check<Integer, E> has(ToIntFunction<Integer> getter, String property, IntPredicate test)
      throws E {
    int propVal = getter.applyAsInt(arg);
    if (test.test(propVal)) {
      return this;
    }
    throw excFactory.apply(Messages.get(test, propVal, propName(property)));
  }

  @Override
  public Check<Integer, E> has(ToIntFunction<Integer> getter, IntPredicate test) throws E {
    return has(getter, getGetterName(getter), test);
  }

  @Override
  public Check<Integer, E> has(
      ToIntFunction<Integer> getter, IntPredicate test, String message, Object... msgArgs)
      throws E {
    if (test.test(getter.applyAsInt(arg))) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public <U, V> Check<Integer, E> has(
      Function<Integer, U> getter, String propName, Relation<U, V> relation, V relateTo) throws E {
    throw notAnObject();
  }

  @Override
  public <U, V> Check<Integer, E> has(
      Function<Integer, U> getter, Relation<U, V> relation, V relateTo) throws E {
    throw notAnObject();
  }

  @Override
  public <U, V> Check<Integer, E> has(
      Function<Integer, U> getter,
      Relation<U, V> relation,
      V relateTo,
      String message,
      Object... msgArgs)
      throws E {
    throw notAnObject();
  }

  @Override
  public <U> Check<Integer, E> has(
      Function<Integer, U> getter, String propName, ObjIntRelation<U> relation, int relateTo)
      throws E {
    throw notAnObject();
  }

  @Override
  public <U> Check<Integer, E> has(
      Function<Integer, U> getter, ObjIntRelation<U> relation, int relateTo) throws E {
    throw notAnObject();
  }

  @Override
  public <U> Check<Integer, E> has(
      Function<Integer, U> getter,
      ObjIntRelation<U> relation,
      int relateTo,
      String message,
      Object... msgArgs)
      throws E {
    throw notAnObject();
  }

  @Override
  public Check<Integer, E> has(
      ToIntFunction<Integer> getter, String property, IntRelation relation, int relateTo) throws E {
    int propVal = getter.applyAsInt(arg);
    if (relation.exists(propVal, relateTo)) {
      return this;
    }
    throw excFactory.apply(Messages.get(relation, propVal, propName(property), relateTo));
  }

  @Override
  public Check<Integer, E> has(ToIntFunction<Integer> getter, IntRelation relation, int relateTo)
      throws E {
    return has(getter, getGetterName(getter), relation, relateTo);
  }

  @Override
  public Check<Integer, E> has(
      ToIntFunction<Integer> getter,
      IntRelation relation,
      int relateTo,
      String message,
      Object... msgArgs)
      throws E {
    if (relation.exists(getter.applyAsInt(arg), relateTo)) {
      return this;
    }
    throw excFactory.apply(String.format(message, msgArgs));
  }

  @Override
  public Integer ok() {
    return arg;
  }

  @Override
  public int intValue() {
    return arg;
  }

  private String propName(String name) {
    return argName + "." + name;
  }

  private UnsupportedOperationException notAnObject() {
    String fmt = "Cannot check properties for non-object (int %s )";
    return new UnsupportedOperationException(String.format(fmt, argName));
  }
}
