package nl.naturalis.common.check;

import java.util.function.Function;
import java.util.function.IntPredicate;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import static nl.naturalis.common.check.Messages.createMessage;

class IntCheck<E extends Exception> extends Check<Integer, E> {

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
    String msg = createMessage(test, false, argName, arg);
    throw excFactory.apply(msg);
  }

  @Override
  public IntCheck<E> isNot(IntPredicate test) throws E {
    if (!test.test(arg)) {
      return this;
    }
    String msg = createMessage(test, true, argName, arg);
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
  public <U> Check<Integer, E> is(IntObjRelation<U> test, U object) throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, false, argName, arg, object);
    throw excFactory.apply(msg);
  }

  @Override
  public <U> Check<Integer, E> isNot(IntObjRelation<U> test, U object) throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, true, argName, arg, object);
    throw excFactory.apply(msg);
  }

  @Override
  public <U> Check<Integer, E> is(
      IntObjRelation<U> test, U object, String message, Object... msgArgs) throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  @Override
  public IntCheck<E> is(IntRelation test, int object) throws E {
    if (test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, false, argName, arg, object);
    throw excFactory.apply(msg);
  }

  @Override
  public IntCheck<E> isNot(IntRelation test, int object) throws E {
    if (!test.exists(arg, object)) {
      return this;
    }
    String msg = createMessage(test, true, argName, arg, object);
    throw excFactory.apply(msg);
  }

  @Override
  public IntCheck<E> is(IntRelation test, int object, String message, Object... msgArgs) throws E {
    if (test.exists(arg, object)) {
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
