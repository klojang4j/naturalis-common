package nl.naturalis.common;

import nl.naturalis.common.check.Check;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Generic 2-tuple of objects, both of which are allowed to be {@code null}.
 *
 * @param first the first component of the 2-tuple
 * @param second the second component of the 2-tuple
 * @param <T> the type of the first component
 * @param <U> the type of the second component
 */
public record LaxTuple2<T, U>(T first, U second) implements Emptyable {

  /**
   * Returns a {@code LaxTuple2} consisting of the specified elements.
   *
   * @param first the first component of the 2-tuple
   * @param second the second component of the 2-tuple
   * @param <ONE> the type of the first component
   * @param <TWO> the type of the second component
   * @return a {@code LaxTuple2} instance containing the specified values
   */
  public static <ONE, TWO> LaxTuple2<ONE, TWO> of(ONE first, TWO second) {
    return new LaxTuple2(first, second);
  }

  /**
   * Converts this instance to a {@code Map} entry.
   *
   * @return a {@code Map} entry
   */
  public Map.Entry<T, U> toEntry() {
    return Map.entry(first, second);
  }

  /**
   * Returns {@code true} if both components of the tuple are {@code null}.
   *
   * @return {@code true} if both components of the tuple are {@code null}
   */
  @Override
  public boolean isEmpty() {
    return first == null && second == null;
  }

}
