package nl.naturalis.common.internal;

import java.util.function.Function;
import java.util.function.IntPredicate;

public final class IntegerCheck<E extends Exception> extends ObjectCheck<Integer, E> {

  public IntegerCheck(Integer arg, String argName, Function<String, E> excProvider) {
    super(arg.intValue(), argName, excProvider);
  }

  @Override
  public IntegerCheck<E> gt(int minVal) throws E {
    that(arg > minVal, smash(ERR_GREATER_THAN, argName, minVal));
    return this;
  }

  @Override
  public IntegerCheck<E> gte(int minVal) throws E {
    that(arg >= minVal, smash(ERR_GREATER_OR_EQUAL, argName, minVal));
    return this;
  }

  @Override
  public IntegerCheck<E> lt(int maxVal) throws E {
    that(arg < maxVal, smash(ERR_LESS_THAN, argName, maxVal));
    return this;
  }

  @Override
  public IntegerCheck<E> lte(int maxVal) throws E {
    that(arg <= maxVal, smash(ERR_LESS_OR_EQUAL, argName, maxVal));
    return this;
  }

  @Override
  public IntegerCheck<E> between(int minInclusive, int maxExclusive) throws E {
    that(arg >= minInclusive && arg < maxExclusive, smash(ERR_BETWEEN, argName, minInclusive, maxExclusive));
    return this;
  }

  @Override
  public IntegerCheck<E> inRange(int minInclusive, int maxInclusive) throws E {
    that(arg >= minInclusive && arg <= maxInclusive, smash(ERR_IN_RANGE, argName, minInclusive, maxInclusive));
    return this;
  }

  @Override
  public IntegerCheck<E> testInt(IntPredicate test, String descr) throws E {
    that(test.test(arg), smash(ERR_FAILED_TEST, argName, descr));
    return this;
  }

  @Override
  public int intValue() {
    return arg.intValue();
  }
}
