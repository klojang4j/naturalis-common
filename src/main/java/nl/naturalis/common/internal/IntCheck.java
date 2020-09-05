package nl.naturalis.common.internal;

import java.util.function.Function;
import nl.naturalis.common.Check;

public final class IntCheck<E extends Exception> extends Check<Integer, E> {

  private final int arg;

  public IntCheck(int arg, String argName, Function<String, E> excProvider) {
    super(argName, excProvider);
    this.arg = arg;
  }

  @Override
  public IntCheck<E> gt(int minVal) throws E {
    that(arg > minVal, smash(ERR_GREATER_THAN, argName, minVal));
    return this;
  }

  @Override
  public IntCheck<E> gte(int minVal) throws E {
    that(arg >= minVal, smash(ERR_GREATER_OR_EQUAL, argName, minVal));
    return this;
  }

  @Override
  public IntCheck<E> lt(int maxVal) throws E {
    that(arg < maxVal, smash(ERR_LESS_THAN, argName, maxVal));
    return this;
  }

  @Override
  public IntCheck<E> lte(int maxVal) throws E {
    that(arg <= maxVal, smash(ERR_LESS_OR_EQUAL, argName, maxVal));
    return this;
  }

  @Override
  public IntCheck<E> between(int minInclusive, int maxExclusive) throws E {
    that(
        arg >= minInclusive && arg < maxExclusive,
        smash(ERR_BETWEEN, argName, minInclusive, maxExclusive));
    return this;
  }

  @Override
  public IntCheck<E> inRange(int minInclusive, int maxInclusive) throws E {
    that(
        arg >= minInclusive && arg <= maxInclusive,
        smash(ERR_IN_RANGE, argName, minInclusive, maxInclusive));
    return this;
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
