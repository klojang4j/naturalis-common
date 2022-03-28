package nl.naturalis.common;

import nl.naturalis.common.check.Check;

/**
 * A 2-tuple of non-null objects of the same type.
 *
 * @param <T> The type of the values
 * @author Ayco Holleman
 */
public record Pair<T>(T one, T two) {

  /**
   * Returns a new {@code Pair} consisting of the specified elements.
   *
   * @param <U> The type of the elements
   * @param one The first element of the {@code Pair}
   * @param two The second element of the {@code Pair}
   * @return A new {@code Pair} consisting of the specified elements
   */
  public static <U> Pair<U> of(U one, U two) {
    return new Pair<>(one, two);
  }

  public Pair(T one, T two) {
    this.one = Check.notNull(one).ok();
    this.two = Check.notNull(two).ok();
  }

}
