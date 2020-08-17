package nl.naturalis.common.internal;

import java.util.function.IntPredicate;
import nl.naturalis.common.Check;

public class IntCheck extends Check {

  final int arg;

  public IntCheck(int arg, String argName) {
    super(argName);
    this.arg = arg;
  }

  @Override
  public Check satisfies(IntPredicate test) {
    integer(arg, test, argName, " %s does not satisfy %s", argName, test);
    return this;
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
  public Check between(int minInclusive, int maxExclusive) {
    between(arg, minInclusive, maxExclusive, argName);
    return this;
  }

  @Override
  public Check inRange(int minInclusive, int maxInclusive) {
    inRange(arg, minInclusive, maxInclusive, argName);
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> U value() {
    return (U) Integer.valueOf(arg);
  }

  @Override
  public int intValue() {
    return arg;
  }

}
