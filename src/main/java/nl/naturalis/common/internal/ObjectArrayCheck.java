package nl.naturalis.common.internal;

import java.util.function.Function;
import static nl.naturalis.common.ObjectMethods.isDeepNotEmpty;
import static nl.naturalis.common.ObjectMethods.isDeepNotNull;

public class ObjectArrayCheck<E extends Exception> extends ObjectCheck<Object[], E> {

  public ObjectArrayCheck(Object[] arg, String argName, Function<String, E> excProvider) {
    super(arg, argName, excProvider);
  }

  @Override
  public ObjectArrayCheck<E> noneNull() throws E {
    that(isDeepNotNull(arg), smash(ERR_NONE_NULL, argName));
    return this;
  }

  @Override
  public ObjectArrayCheck<E> noneEmpty() throws E {
    that(isDeepNotEmpty(arg), smash(ERR_NONE_EMPTY, argName));
    return this;
  }

  @Override
  public ObjectArrayCheck<E> gt(int minVal) throws E {
    that(arg.length > minVal, smash(ERR_GREATER_THAN, length()));
    return this;
  }

  @Override
  public ObjectArrayCheck<E> gte(int minVal) throws E {
    that(arg.length > minVal, smash(ERR_GREATER_OR_EQUAL, length()));
    return this;
  }

  @Override
  public ObjectArrayCheck<E> lt(int maxVal) throws E {
    that(arg.length > maxVal, smash(ERR_LESS_OR_EQUAL, length()));
    return this;
  }

  @Override
  public ObjectArrayCheck<E> lte(int maxVal) throws E {
    that(arg.length > maxVal, smash(ERR_LESS_OR_EQUAL, length()));
    return this;
  }

  private String length() {
    return argName + ".length";
  }
}
