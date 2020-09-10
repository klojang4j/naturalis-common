package nl.naturalis.common.internal;

import java.util.Map;
import java.util.function.Function;
import static nl.naturalis.common.ObjectMethods.isDeepNotEmpty;
import static nl.naturalis.common.ObjectMethods.isDeepNotNull;

@SuppressWarnings("rawtypes")
public class MapCheck<E extends Exception> extends ObjectCheck<Map, E> {

  public MapCheck(Map arg, String argName, Function<String, E> excProvider) {
    super(arg, argName, excProvider);
  }

  @Override
  public MapCheck<E> noneNull() throws E {
    that(isDeepNotNull(arg), smash(ERR_NONE_NULL, argName));
    return this;
  }

  @Override
  public MapCheck<E> noneEmpty() throws E {
    that(isDeepNotEmpty(arg), smash(ERR_NONE_EMPTY, argName));
    return this;
  }
}
