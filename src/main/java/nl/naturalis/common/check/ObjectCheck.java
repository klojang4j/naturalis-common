package nl.naturalis.common.check;

import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;

import java.util.function.Function;
import java.util.function.Predicate;

import static nl.naturalis.common.check.InvalidCheckException.notApplicable;
import static nl.naturalis.common.check.Messages.createMessage;

final class ObjectCheck<T, E extends Exception> extends Check<T, E> {

  private static final String ERR_INT_VALUE = "Cannot return int value for %s";
  private static final String ERR_NULL_TO_INT = ERR_INT_VALUE + " (was null)";
  private static final String ERR_NUMBER_TO_INT = ERR_INT_VALUE + " (was %s)";
  private static final String ERR_OBJECT_TO_INT = ERR_INT_VALUE + " (%s)";

  private final T arg;

  ObjectCheck(T arg, String argName, Function<String, E> excFactory) {
    super(argName, excFactory);
    this.arg = arg;
  }

  @Override
  public Check<T, E> is(Predicate<T> test, String message, Object... msgArgs) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw exception(test, message, msgArgs);
  }

  @Override
  public <U> Check<T, E> is(IntObjRelation<U> test, U object) throws E {
    if (applicable()) {
      int i = (((Number) arg).intValue());
      if (test.exists(i, object)) {
        return this;
      }
      String msg = createMessage(test, false, getArgName(arg), i, object);
      throw excFactory.apply(msg);
    }
    throw notApplicable(test, arg, getArgName(arg));
  }

  @Override
  public <U> Check<T, E> isNot(IntObjRelation<U> test, U object) throws E {
    if (applicable()) {
      int i = (((Number) arg).intValue());
      if (!test.exists(i, object)) {
        return this;
      }
      String msg = createMessage(test, true, getArgName(arg), i, object);
      throw excFactory.apply(msg);
    }
    throw notApplicable(test, arg, getArgName(arg));
  }

  @Override
  public <U> Check<T, E> is(IntObjRelation<U> test, U object, String message, Object... msgArgs)
      throws E {
    if (applicable()) {
      int i = (((Number) arg).intValue());
      if (test.exists(i, object)) {
        return this;
      }
      throw exception(test, object, message, msgArgs);
    }
    throw notApplicable(test, arg, getArgName(arg));
  }

  @Override
  public Check<T, E> is(IntRelation test, int object) throws E {
    if (applicable()) {
      int i = (((Number) arg).intValue());
      if (test.exists(i, object)) {
        return this;
      }
      throw excFactory.apply(createMessage(test, false, argName, i, object));
    }
    throw notApplicable(test, arg, argName);
  }

  @Override
  public Check<T, E> isNot(IntRelation test, int object) throws E {
    if (applicable()) {
      int i = (((Number) arg).intValue());
      if (!test.exists(i, object)) {
        return this;
      }
      throw excFactory.apply(createMessage(test, true, getArgName(arg), i, object));
    }
    throw notApplicable(test, arg, getArgName(arg));
  }

  @Override
  public Check<T, E> is(IntRelation test, int object, String message, Object... msgArgs) throws E {
    if (applicable()) {
      int i = (((Number) arg).intValue());
      if (test.exists(i, object)) {
        return this;
      }
      throw exception(test, object, message, msgArgs);
    }
    throw notApplicable(test, arg, getArgName(arg));
  }

  @Override
  public T ok() {
    return arg;
  }

  @Override
  public int intValue() throws E {
    if (arg == null) {
      String msg = String.format(ERR_NULL_TO_INT, getArgName(arg));
      throw excFactory.apply(msg);
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
    throw excFactory.apply(msg);
  }

  @SuppressWarnings({"raw-types"})
  private boolean applicable() {
    Class c = arg.getClass();
    return c == Integer.class || c == Short.class || c == Byte.class;
  }
}
