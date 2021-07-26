package nl.naturalis.common;

import java.util.*;
import java.util.function.Supplier;
import nl.naturalis.common.check.Check;

/**
 * Generic, immutable Tuple class.
 *
 * @author Ayco Holleman
 * @param <T> The type of the first element (or key) of the tuple
 * @param <U> The type of the second element (or value) of the tuple
 */
public final class Tuple<T, U> {

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
   * Returns a modifiable {@code Map} containing the specified tuples.
   *
   * @param <K> The key type
   * @param <V> The value type
   * @param tuples The tuples
   * @return A {@code Map} containing the specified tuples.
   */
  @SuppressWarnings("unchecked")
  public static <K, V> Map<K, V> toUnmodifiableMap(Tuple<K, V>[] tuples) {
    Check.notNull(tuples);
    return Map.ofEntries(Arrays.stream(tuples).map(Tuple::toEntry).toArray(Map.Entry[]::new));
  }

  /**
   * Returns the {@code Map} produced by the specified function and filled with the specified
   * tuples.
   *
   * @param <K> The key type
   * @param <V> The value type
   * @param tuples The tuples
   * @param mapFactory A function providing the map into which to insert the tuples. The function is
   *     given the length of the tuples array as input.
   * @return A {@code Map} containing the specified tuples.
   */
  public static <K, V> Map<K, V> toMap(Tuple<K, V>[] tuples, Supplier<Map<K, V>> mapFactory) {
    Check.notNull(tuples);
    Map<K, V> map = mapFactory.get();
    Arrays.stream(tuples).forEach(t -> t.insertInto(map));
    return map;
  }

  private final T left;
  private final U right;

  private Tuple(T left, U right) {
    this.left = left;
    this.right = right;
  }

  /**
   * Returns the first element of the tuple.
   *
   * @return
   */
  public T getLeft() {
    return left;
  }

  /**
   * Returns the second element of the tuple.
   *
   * @return
   */
  public U getRight() {
    return right;
  }

  /**
   * Returns a new tuple in which the two elements have swapped places.
   *
   * @return A new tuple in which the two elements have swapped places
   */
  public Tuple<U, T> swap() {
    return Tuple.of(right, left);
  }

  /**
   * Returns a singleton map with the left element as the key and the right element as the value.
   *
   * @return A singleton map with the left element as the key and the right element as the value
   */
  public Map<T, U> toMap() {
    return Collections.singletonMap(left, right);
  }

  /**
   * Inserts this tuple into the specified {@code Map} using the left element as key and the right
   * element as value.
   *
   * @param map The {@code Map} to insert the tuple into.
   * @return The previous value associated with key, or null if there was no mapping for key
   */
  public U insertInto(Map<T, U> map) {
    return map.put(left, right);
  }

  /**
   * Converts the tuple to a map entry.
   *
   * @return
   */
  public Map.Entry<T, U> toEntry() {
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
