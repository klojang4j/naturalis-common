package nl.naturalis.common.function;

import java.util.function.Function;

/**
 * An alternative to Java's {@link Function} interface where the {@code apply}
 * method is allowed to throw a checked exception.
 *
 * @author Ayco Holleman
 *
 * @param <T> The type of the input variable
 * @param <R> The type of the return value
 * @param <E> The type of the exception potentially being thrown
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> {

  /**
   * Calculates a value for the provided argument while potentially throwing an
   * exception of type {@code E}.
   *
   * @param arg The input variable
   * @return A value of type {@code R}
   * @throws E The exception potentially being thrown
   */
  R apply(T arg) throws E;

}
