package nl.naturalis.common.internal;

import java.util.function.Predicate;
import nl.naturalis.common.Check;

public class ObjectCheck<T> extends Check {

  final T arg;

  public ObjectCheck(T arg, String argName) {
    super(argName);
    this.arg = arg;
  }

  @Override
  public Check test(Predicate<Object> test, String descr) {
    argument(arg, test, argName, "%s fails test %s", argName, descr);
    return this;
  }

  @Override
  public ObjectCheck<T> notNull() {
    notNull(arg, argName);
    return this;
  }

  @Override
  public ObjectCheck<T> noneNull() {
    noneNull(arg, argName);
    return this;
  }

  @Override
  public ObjectCheck<T> notEmpty() {
    notEmpty(arg, argName);
    return this;
  }

  @Override
  public ObjectCheck<T> noneEmpty() {
    noneEmpty(arg, argName);
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> U value() {
    return (U) arg;
  }

}
