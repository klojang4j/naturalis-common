package nl.naturalis.common.function;

import java.util.function.Function;

/**
 * An alternative to Java's {@link Function} interface where the {@code apply}
 * method is allowed to throw a checked exception.
 *
 * @author Ayco Holleman
 *
 * @param <X> The type of the input variable
 * @param <Y> The type of the return value
 * @param <E> The type of the exception potentially being thrown
 */
@FunctionalInterface
public interface ThrowingFunction<X, Y, E extends Exception> {

  /**
   * Calculates a value for x while potentially throwing an exception of type
   * {@code E}.
   *
   * @param x The input variable
   * @return A value of type {@code Y}
   * @throws E The exception potentially being thrown
   */
  Y apply(X x) throws E;

}
