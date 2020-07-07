package nl.naturalis.common.function;

import java.util.function.Function;

/**
 * An alternative to Java's {@link Function} interface where the {@code apply} method is allowed to throw a checked exception.
 */
@FunctionalInterface
public interface ThrowingFunction<X, Y, E extends Exception> {

  /**
   * Calculates a value for x while potentially throwing an exception of type {@code E}.
   * 
   * @param x
   * @return
   * @throws E
   */
  Y apply(X x) throws E;

}
