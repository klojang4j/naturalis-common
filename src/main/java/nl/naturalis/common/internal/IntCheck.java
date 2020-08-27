package nl.naturalis.common.internal;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import nl.naturalis.common.Check;

public final class IntCheck extends Check<Integer> {

  private final int arg;

  public IntCheck(int arg, String argName) {
    super(argName);
    this.arg = arg;
  }

  @Override
  public IntCheck gt(int min) {
    gt(arg, min, argName);
    return this;
  }

  @Override
  public IntCheck gte(int min) {
    gte(arg, min, argName);
    return this;
  }

  @Override
  public IntCheck lt(int max) {
    lt(arg, max, argName);
    return this;
  }

  @Override
  public IntCheck lte(int max) {
    lte(arg, max, argName);
    return this;
  }

  @Override
  public IntCheck between(int minInclusive, int maxExclusive) {
    between(arg, minInclusive, maxExclusive, argName);
    return this;
  }

  @Override
  public IntCheck inRange(int minInclusive, int maxInclusive) {
    inRange(arg, minInclusive, maxInclusive, argName);
    return this;
  }

  @Override
  public IntCheck test(Predicate<Integer> test, String descr) throws IllegalArgumentException {
    return test(test, descr, IllegalArgumentException::new);
  }

  @Override
  public <E extends Exception> IntCheck test(Predicate<Integer> test, String descr, Function<String, E> excProvider) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw excProvider.apply(String.format(ERR_FAILED_TEST, argName, descr));
  }

  @Override
  public Check<Integer> testInt(IntPredicate test, String descr) throws IllegalArgumentException {
    if (test.test(arg)) {
      return this;
    }
    throw new IllegalArgumentException(String.format(ERR_FAILED_TEST, argName, descr));
  }

  @Override
  public <E extends Exception> IntCheck testInt(IntPredicate test, String descr, Function<String, E> excProvider) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw excProvider.apply(String.format(ERR_FAILED_TEST, argName, descr));
  }

  @Override
  public Integer value() {
    return Integer.valueOf(arg);
  }

  @Override
  public int intValue() {
    return arg;
  }

}
