package nl.naturalis.common.check;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import static nl.naturalis.common.check.Messages.createMessage;

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
    String msg = String.format(message, msgArgs);
    throw excFactory.apply(msg);
  }

  @Override
  public Check<T, E> is(IntPredicate test) throws E {
    if (ArrayMethods.isOneOf(arg.getClass(), Integer.class, Short.class, Byte.class)) {
      if (test.test(((Number) arg).intValue())) {
        return this;
      }
      String msg = createMessage(test, argName, arg);
      throw excFactory.apply(msg);
    }
    throw notApplicable();
  }

  @Override
  public Check<T, E> is(IntPredicate test, String message, Object... msgArgs) throws E {
    if (ArrayMethods.isOneOf(arg.getClass(), Integer.class, Short.class, Byte.class)) {
      if (test.test(((Number) arg).intValue())) {
        return this;
      }
      String msg = String.format(message, msgArgs);
      throw excFactory.apply(msg);
    }
    throw notApplicable();
  }

  @Override
  public <U> Check<T, E> is(IntObjRelation<U> relation, U relateTo) throws E {
    if (ArrayMethods.isOneOf(arg.getClass(), Integer.class, Short.class, Byte.class)) {
      if (relation.exists(((Number) arg).intValue(), relateTo)) {
        return this;
      }
    }
    String msg = createMessage(relation, argName, arg, relateTo);
    throw excFactory.apply(msg);
  }

  @Override
  public <U> Check<T, E> is(
      IntObjRelation<U> relation, U relateTo, String message, Object... msgArgs) throws E {
    if (ArrayMethods.isOneOf(arg.getClass(), Integer.class, Short.class, Byte.class)) {
      if (relation.exists(((Number) arg).intValue(), relateTo)) {
        return this;
      }
      String msg = String.format(message, msgArgs);
      throw excFactory.apply(msg);
    }
    throw notApplicable();
  }

  @Override
  public Check<T, E> is(IntRelation relation, int relateTo) throws E {
    if (ArrayMethods.isOneOf(arg.getClass(), Integer.class, Short.class, Byte.class)) {
      if (relation.exists(((Number) arg).intValue(), relateTo)) {
        return this;
      }
      throw excFactory.apply(createMessage(relation, argName, arg, relateTo));
    }
    throw notApplicable();
  }

  @Override
  public Check<T, E> is(IntRelation relation, int relateTo, String message, Object... msgArgs)
      throws E {
    if (ArrayMethods.isOneOf(arg.getClass(), Integer.class, Short.class, Byte.class)) {
      if (relation.exists(((Number) arg).intValue(), relateTo)) {
        return this;
      }
      throw excFactory.apply(String.format(message, msgArgs));
    }
    throw notApplicable();
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

  private UnsupportedOperationException notApplicable() {
    if (arg == null) {
      String message = String.format(ERR_NOT_APPLICABLE, argName, "was null");
      throw new UnsupportedOperationException(message);
    }
    String message = String.format(ERR_NOT_APPLICABLE, argName, arg.getClass().getName());
    return new UnsupportedOperationException(message);
  }
}
