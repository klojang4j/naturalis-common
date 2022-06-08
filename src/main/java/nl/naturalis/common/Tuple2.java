package nl.naturalis.common;

import nl.naturalis.common.check.Check;

/**
 * Generic 2-tuple of non-null objects.
 *
 * @param first The first component of the 2-tuple
 * @param second The second component of the 2-tuple
 * @param <T> The type of the first component
 * @param <U> The type of the second component
 */
public record Tuple2<T, U>(T first, U second) {

  /**
   * Returns a {@code Tuple2} consisting of the specified elements.
   *
   * @param first The first component of the 2-tuple
   * @param second The second component of the 2-tuple
   * @param <ONE> The type of the first component
   * @param <TWO> The type of the second component
   * @return A {@code Tuple2} instance containing the specified values
   */
  public static <ONE, TWO> Tuple2<ONE, TWO> of(ONE first, TWO second) {
    return new Tuple2(first, second);
  }

  /**
   * Instantiates a new {@code Tuple2}.
   *
   * @param first The first component of the 2-tuple
   * @param second The second component of the 2-tuple
   */
  public Tuple2(T first, U second) {
    this.first = Check.notNull(first, "first component").ok();
    this.second = Check.notNull(second, "second component").ok();
  }

}
