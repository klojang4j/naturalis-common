package nl.naturalis.common.internal;

import java.util.function.Function;
import java.util.function.IntPredicate;

public final class IntegerCheck extends ObjectCheck<Integer> {

  public IntegerCheck(Integer arg, String argName) {
    super(arg.intValue(), argName);
  }

  @Override
  public IntegerCheck gt(int max) {
    gt(arg.intValue(), max, argName);
    return this;
  }

  @Override
  public IntegerCheck gte(int max) {
    gte(arg.intValue(), max, argName);
    return this;
  }

  @Override
  public IntegerCheck lt(int max) {
    lt(arg.intValue(), max, argName);
    return this;
  }

  @Override
  public IntegerCheck lte(int max) {
    lte(arg.intValue(), max, argName);
    return this;
  }

  @Override
  public IntegerCheck between(int minInclusive, int maxExclusive) {
    between(arg.intValue(), minInclusive, maxExclusive, argName);
    return this;
  }

  @Override
  public IntegerCheck inRange(int minInclusive, int maxInclusive) {
    inRange(arg.intValue(), minInclusive, maxInclusive, argName);
    return this;
  }

  @Override
  public IntegerCheck testInt(IntPredicate test, String descr) throws IllegalArgumentException {
    return testInt(test, descr, IllegalArgumentException::new);
  }

  @Override
  public <E extends Exception> IntegerCheck testInt(IntPredicate test, String descr, Function<String, E> excProvider) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw excProvider.apply(String.format(ERR_FAILED_TEST, argName, descr));
  }

  @Override
  public int intValue() {
    return arg.intValue();
  }
}
