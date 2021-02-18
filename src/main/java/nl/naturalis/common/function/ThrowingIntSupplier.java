package nl.naturalis.common.function;

import java.util.function.IntSupplier;

/**
 * An alternative to Java's {@link IntSupplier} interface where the {@code get} method is allowed to
 * throw a checked exception.
 *
 * @author Ayco Holleman
 * @param <E> The type of the exception potentially being thrown
 */
@FunctionalInterface
public interface ThrowingIntSupplier<E extends Throwable> {

  /**
   * Produces an integer value while potentially throwing an exception.
   *
   * @return The value
   * @throws E If an exception occurred while producing the object
   */
  int get() throws E;
}
