package nl.naturalis.common;

import java.util.*;
import nl.naturalis.common.check.Check;

/**
 * Generic, immutable Tuple class. Both the "left" and the "right" element of the tuple may be null.
 *
 * @author Ayco Holleman
 * @param <LEFT> The type of the first element (or key) of the tuple
 * @param <RIGHT> The type of the second element (or value) of the tuple
 */
public final class Tuple<LEFT, RIGHT> {

  /**
   * Creates a tuple containing the specified values.
   *
   * @param <K> The type of the first element (or key) of the tuple
   * @param <V> The type of the second element (or value) of the tuple
   * @param left The first element (or key) of the tuple
   * @param right The second element (or value) of the tuple
   * @return A tuple containing the specified values
   */
  public static <K, V> Tuple<K, V> of(K left, V right) {
    return new Tuple<>(left, right);
  }

  /**
   * Returns a modifiable {@code Map} containing the specified tuples.
   *
   * @param <K> The key type
   * @param <V> The value type
   * @param tuples The tuples
   * @return A {@code Map} containing the specified tuples.
   */
  public static <K, V> Map<K, V> toMap(Tuple<K, V>[] tuples) {
    Check.notNull(tuples);
    Map<K, V> map = new HashMap<>(tuples.length);
    Arrays.stream(tuples).forEach(t -> t.insertInto(map));
    return map;
  }

  /**
   * Returns a modifiable {@link LinkedHashMap} containing the specified tuples.
   *
   * @param <K> The key type
   * @param <V> The value type
   * @param tuples The tuples
   * @return A {@code LinkedHashMap} containing the specified tuples.
   */
  public static <K, V> Map<K, V> toLinkedHashMap(Tuple<K, V>[] tuples) {
    Check.notNull(tuples);
    Map<K, V> map = new LinkedHashMap<>(tuples.length);
    Arrays.stream(tuples).forEach(t -> t.insertInto(map));
    return map;
  }

  private final LEFT left;
  private final RIGHT right;

  private Tuple(LEFT left, RIGHT right) {
    this.left = left;
    this.right = right;
  }

  /**
   * Returns the first element of the tuple.
   *
   * @return
   */
  public LEFT getLeft() {
    return left;
  }

  /**
   * Returns the second element of the tuple.
   *
   * @return
   */
  public RIGHT getRight() {
    return right;
  }

  /**
   * Returns a new tuple in which the two elements have swapped places.
   *
   * @return A new tuple in which the two elements have swapped places
   */
  public Tuple<RIGHT, LEFT> swap() {
    return Tuple.of(right, left);
  }

  /**
   * Returns a singleton map with the left element as the key and the right element as the value.
   *
   * @return A singleton map with the left element as the key and the right element as the value
   */
  public Map<LEFT, RIGHT> toMap() {
    return Collections.singletonMap(left, right);
  }

  /**
   * Inserts this tuple into the specified {@code Map} using the left element as key and the right
   * element as value.
   *
   * @param map The {@code Map} to insert the tuple into.
   * @return The previous value associated with key, or null if there was no mapping for key
   */
  public RIGHT insertInto(Map<LEFT, RIGHT> map) {
    return map.put(left, right);
  }

  /**
   * Converts the tuple to a map entry.
   *
   * @return
   */
  public Map.Entry<LEFT, RIGHT> toEntry() {
    return Map.entry(left, right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null || obj.getClass() != Tuple.class) {
      return false;
    }
    Tuple other = (Tuple) obj;
    return Objects.equals(left, other.left) && Objects.equals(right, other.right);
  }

  @Override
  public String toString() {
    return "[" + Objects.toString(left) + "," + Objects.toString(right) + "]";
  }
}
