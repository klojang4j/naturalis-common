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
public interface ThrowingSupplier<T, E extends Exception> {

  /**
   * Produces a value while potentially throwing an exception of type {@code E}.
   *
   * @return A value of type {@code T}.
   * @throws An exception of type {@code E}.
   */
  T get() throws E;
}
