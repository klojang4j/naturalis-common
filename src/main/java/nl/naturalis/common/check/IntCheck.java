package nl.naturalis.common.check;

import java.util.function.Function;
import java.util.function.IntPredicate;
import nl.naturalis.common.function.IntRelation;

public final class IntCheck<E extends Exception> extends Check<Integer, E> {

  private final int arg;

  public IntCheck(int arg, String argName, Function<String, E> excFactory) {
    super(argName, excFactory);
    this.arg = arg;
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
  public Integer ok() {
    return arg;
  }

  @Override
  public int intValue() {
    return arg;
  }
}
