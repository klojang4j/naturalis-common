package nl.naturalis.common.function;

import java.util.function.Supplier;

/**
 * An alternative to Java's {@link Supplier} interface where the {@code get} method is allowed to
 * throw a checked exception.
 *
 * @author Ayco Holleman
 * @param <T> The type of the return value
 * @param <E> The type of the exception potentially being thrown
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {

  /**
   * Produces a value while potentially throwing an exception.
   *
   * @return The value
   * @throws E If an exception occurred while producing the object
   */
  T get() throws E;
}
