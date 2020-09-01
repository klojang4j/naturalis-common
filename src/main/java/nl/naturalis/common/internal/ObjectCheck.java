package nl.naturalis.common.internal;

import java.util.function.Function;
import java.util.function.Predicate;
import nl.naturalis.common.Check;
import nl.naturalis.common.ObjectMethods;

public class ObjectCheck<T, E extends Exception> extends Check<T, E> {

  final T arg;

  public ObjectCheck(T arg, String argName, Function<String, E> excProvider) {
    super(argName, excProvider);
    this.arg = arg;
  }

  @Override
  public ObjectCheck<T, E> notNull() throws E {
    that(arg != null, smash(ERR_NOT_NULL, argName));
    return this;
  }

  @Override
  public ObjectCheck<T, E> noneNull() throws E {
    that(ObjectMethods.isDeepNotNull(arg), smash(ERR_NONE_NULL, argName));
    return this;
  }

  @Override
  public ObjectCheck<T, E> notEmpty() throws E {
    that(ObjectMethods.isNotEmpty(arg), smash(ERR_NOT_EMPTY, argName));
    return this;
  }

  @Override
  public ObjectCheck<T, E> noneEmpty() throws E {
    that(ObjectMethods.isDeeptNotEmpty(arg), smash(ERR_NONE_EMPTY, argName));
    return this;
  }

  @Override
  public Check<T, E> isNull() throws E {
    that(arg == null, smash(ERR_MUST_BE_NULL, argName));
    return this;
  }

  @Override
  public Check<T, E> isEmpty() throws E {
    that(ObjectMethods.isEmpty(arg), smash(ERR_MUST_BE_EMPTY, argName));
    return this;
  }

  @Override
  public Check<T, E> test(Predicate<T> test, String descr) throws E {
    that(test.test(arg), smash(ERR_FAILED_TEST, argName, descr));
    return this;
  }

  @Override
  public Check<T, E> test(String field, Predicate<T> test, String descr) throws E {
    that(test.test(arg), smash(ERR_FAILED_TEST, field(field), descr));
    return this;
  }

  @Override
  public T value() {
    return arg;
  }

}
