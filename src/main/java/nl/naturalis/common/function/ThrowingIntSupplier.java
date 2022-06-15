package nl.naturalis.common.function;

import java.util.function.IntSupplier;

/**
 * An alternative to Java's {@link IntSupplier} interface where the {@code get}
 * method is allowed to throw a checked exception.
 *
 * @param <E> The type of the exception potentially being thrown
 * @author Ayco Holleman
 */
@FunctionalInterface
public interface ThrowingIntSupplier<E extends Throwable> {

  /**
   * Produces an integer value while potentially throwing an exception.
   *
   * @return The value produced by the supplier
   * @throws E If an exception occurred while producing the integer
   */
  int get() throws E;

}
