package nl.naturalis.common.internal;

import java.util.Collection;
import java.util.function.Function;
import static nl.naturalis.common.ObjectMethods.isDeepNotEmpty;
import static nl.naturalis.common.ObjectMethods.isDeepNotNull;

@SuppressWarnings("rawtypes")
public class CollectionCheck<E extends Exception> extends ObjectCheck<Collection, E> {

  public CollectionCheck(Collection arg, String argName, Function<String, E> excProvider) {
    super(arg, argName, excProvider);
  }

  @Override
  public CollectionCheck<E> noneNull() throws E {
    that(isDeepNotNull(arg), smash(ERR_NONE_NULL, argName));
    return this;
  }

  @Override
  public CollectionCheck<E> noneEmpty() throws E {
    that(isDeepNotEmpty(arg), smash(ERR_NONE_EMPTY, argName));
    return this;
  }
}
