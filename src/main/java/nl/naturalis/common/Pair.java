package nl.naturalis.common;

/**
 * A generic, immutable container of two objects of the same type.
 *
 * @author Ayco Holleman
 * @param <T>
 */
public final class Pair<T> {

  /**
   * Returns a new {@code Pair} consisting of the specified objects.
   *
   * @param <U> The type of the objects
   * @param first The first element of the {@code Pair}
   * @param second The second element of the {@code Pair}
   * @return A new {@code Pair} consisting of the specified objects
   */
  public static <U> Pair<U> of(U first, U second) {
    return new Pair<>(first, second);
  }

  private final T one;
  private final T two;

  /**
   * Constructs a new {@code Pair} from the specified objects.
   *
   * @param first The first element of the {@code Pair}
   * @param second The second element of the {@code Pair}
   */
  private Pair(T first, T second) {
    one = first;
    two = second;
  }

  /**
   * Returns the first element of this {@code Pair}.
   *
   * @return The first element of this {@code Pair}.
   */
  public T getFirst() {
    return one;
  }

  /**
   * Returns the second element of this {@code Pair}.
   *
   * @return The second element of this {@code Pair}.
   */
  public T getSecond() {
    return two;
  }
}
