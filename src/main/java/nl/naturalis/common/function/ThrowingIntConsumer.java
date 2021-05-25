package nl.naturalis.common.function;

import java.util.function.IntConsumer;

/**
 * An alternative to Java's {@link IntConsumer} interface where the {@code accept} method is allowed
 * to throw a checked exception.
 *
 * @author Ayco Holleman
 * @param <E> The type of the exception potentially being thrown
 */
@FunctionalInterface
public interface ThrowingIntConsumer<E extends Throwable> {

  /**
   * Performs this operation on the given argument.
   *
   * @param value The input argument
   * @throws E The exception potentially being throw from the operation
   */
  void accept(int value) throws E;
}
