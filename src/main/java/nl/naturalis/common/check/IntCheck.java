package nl.naturalis.common.check;

import java.util.function.Function;
import java.util.function.IntPredicate;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import static nl.naturalis.common.check.Messages.createMessage;

class IntCheck<E extends Exception> extends Check<Integer, E> {

  private int arg;

  IntCheck(int arg, String argName, Function<String, E> excFactory) {
    super(argName, excFactory);
    this.arg = arg;
  }

  @Override
  public IntCheck<E> is(IntPredicate test) throws E {
    if (test.test(arg)) {
      return this;
    }
    String msg = createMessage(test, argName, arg);
    throw excFactory.apply(msg);
  }

  @Override
  public IntCheck<E> is(IntPredicate test, String message, Object... msgArgs) throws E {
    if (test.test(arg)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  @Override
  public <U> Check<Integer, E> is(IntObjRelation<U> relation, U relateTo) throws E {
    if (relation.exists(arg, relateTo)) {
      return this;
    }
    String msg = createMessage(relation, argName, arg, relateTo);
    throw excFactory.apply(msg);
  }

  @Override
  public <U> Check<Integer, E> is(
      IntObjRelation<U> relation, U relateTo, String message, Object... msgArgs) throws E {
    if (relation.exists(arg, relateTo)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  @Override
  public IntCheck<E> is(IntRelation relation, int relateTo) throws E {
    if (relation.exists(arg, relateTo)) {
      return this;
    }
    String msg = createMessage(relation, argName, arg, relateTo);
    throw excFactory.apply(msg);
  }

  @Override
  public IntCheck<E> is(IntRelation relation, int relateTo, String message, Object... msgArgs)
      throws E {
    if (relation.exists(arg, relateTo)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  @Override
  public Integer ok() {
    return arg;
  }

  @Override
  public int intValue() {
    return arg;
  }
}
