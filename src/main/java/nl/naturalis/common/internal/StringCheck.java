package nl.naturalis.common.internal;

import java.util.function.Function;
import nl.naturalis.common.StringMethods;

public final class StringCheck<E extends Exception> extends ObjectCheck<String, E> {

  public StringCheck(String arg, String argName, Function<String, E> excProvider) {
    super(arg, argName, excProvider);
  }

  @Override
  public StringCheck<E> notBlank() throws E {
    that(StringMethods.isBlank(arg), smash(ERR_NOT_BLANK, argName));
    return this;
  }
}
