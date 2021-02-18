package nl.naturalis.common.function;

import java.util.function.ToIntFunction;

/**
 * An alternative to Java's {@link ToIntFunction} interface where the {@code apply} method is
 * allowed to throw a checked exception.
 *
 * @author Ayco Holleman
 * @param <T> The type of the input variable
 * @param <E> The type of the exception potentially being thrown
 */
@FunctionalInterface
public interface ThrowingToIntFunction<T, E extends Throwable> {

  /**
   * Calculates a value for the provided argument while potentially throwing an exception of type
   * {@code E}.
   *
   * @param arg The input variable
   * @return An {@code int} value
   * @throws E The exception potentially being thrown
   */
  int apply(T arg) throws E;
}
