package nl.naturalis.common.internal;

import nl.naturalis.common.Check;

public class IntegerCheck extends ObjectCheck<Integer> {

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
  public Check between(int minInclusive, int maxExclusive) {
    between(arg.intValue(), minInclusive, maxExclusive, argName);
    return this;
  }

  @Override
  public Check inRange(int minInclusive, int maxInclusive) {
    inRange(arg.intValue(), minInclusive, maxInclusive, argName);
    return this;
  }

  @Override
  public int intValue() {
    return arg.intValue();
  }
}
