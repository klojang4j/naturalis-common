package nl.naturalis.common.function;

import java.util.function.Consumer;

/**
 * An alternative to Java's {@link Consumer} interface where the {@code accept} method is allowed to
 * throw a checked exception.
 *
 * @author Ayco Holleman
 * @param <T> The type of the input to the operation
 * @param <E> The type of the exception potentially being thrown by the operation
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t The input argument
   * @throws E If the operation fails
   */
  public void accept(T t) throws E;
}
