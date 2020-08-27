package nl.naturalis.common.internal;

import java.util.function.Function;
import java.util.function.Predicate;
import nl.naturalis.common.Check;

public class ObjectCheck<T> extends Check<T> {

  final T arg;

  public ObjectCheck(T arg, String argName) {
    super(argName);
    this.arg = arg;
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
  public Check<T> test(Predicate<T> test, String descr) throws IllegalArgumentException {
    return test(test, descr, IllegalArgumentException::new);
  }

  @Override
  public <E extends Exception> Check<T> test(Predicate<T> test, String descr, Function<String, E> excProvider) throws E {
    if (test.test(arg)) {
      return this;
    }
    throw excProvider.apply(String.format(ERR_FAILED_TEST, argName, descr));
  }

  @Override
  public T value() {
    return arg;
  }

}
