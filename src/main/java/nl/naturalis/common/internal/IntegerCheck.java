package nl.naturalis.common.internal;

import java.util.function.IntPredicate;
import nl.naturalis.common.Check;

public class IntegerCheck extends ObjectCheck<Integer> {

  public IntegerCheck(Integer arg, String argName) {
    super(arg.intValue(), argName);
  }

  @Override
  public Check satisfies(IntPredicate test) {
    integer(arg.intValue(), test, argName, " %s does not satisfy %s", argName, test);
    return this;
  }

  @Override
  public IntegerCheck greaterThan(int max) {
    greaterThan(arg.intValue(), max, argName);
    return this;
  }

  @Override
  public IntegerCheck atLeast(int max) {
    atLeast(arg.intValue(), max, argName);
    return this;
  }

  @Override
  public IntegerCheck lessThan(int max) {
    lessThan(arg.intValue(), max, argName);
    return this;
  }

  @Override
  public IntegerCheck atMost(int max) {
    atMost(arg.intValue(), max, argName);
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
