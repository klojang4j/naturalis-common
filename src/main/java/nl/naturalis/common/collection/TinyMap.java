package nl.naturalis.common.collection;

import java.util.*;
import java.util.stream.IntStream;
import nl.naturalis.common.check.Check;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static nl.naturalis.common.check.CommonChecks.eq;
import static nl.naturalis.common.check.CommonChecks.illegalState;
import static nl.naturalis.common.check.CommonChecks.lt;
import static nl.naturalis.common.check.CommonChecks.notEqualTo;

/**
 * A {@code Map} implementation that is potentially efficient in a write-once/read-once scnenario
 * with no more than a couple of key-value pairs. The key-value pairs are simply appended to a
 * two-dimensional array and key retrieval happens through iteration over the array. The map is not
 * modifiable. All {@code Map} operations that would modify the map throw an {@link
 * UnsupportedOperationException}.
 *
 * @author Ayco Holleman
 */
@SuppressWarnings({"unchecked"})
public class TinyMap<K, V> implements Map<K, V> {

  /**
   * A builder class for {@code TinyMap} instances.
   *
   * @author Ayco Holleman
   * @param <K> The type of the key
   * @param <V> The type of the value
   */
  public static class Builder<K, V> {

    private static final String ERR_NO_CAPACITY = "Capacity exceeded";
    private static final String ERR_NOT_COMPLETE = "Exactly %d key-value pairs required";

    private final Object[][] data;

    private int cnt;

    private Builder(int size) {
      this.data = new Object[size][];
    }

    /**
     * Adds a new key-value pair. You must add exactly as many key-value pairs as specified through
     * {@link TinyMap#open(int) TinyMap.open}.
     *
     * @param k The key
     * @param v The value
     * @return This {@code Builder} instance
     */
    public Builder<K, V> add(K k, V v) {
      Check.with(ArrayIndexOutOfBoundsException::new, cnt).is(lt(), data.length, ERR_NO_CAPACITY);
      for (int i = 0; i < cnt; ++i) {
        Check.that(k).is(notEqualTo(), (K) data[i][0], "Duplicate key: %s", k);
      }
      data[cnt][0] = k;
      data[cnt][1] = v;
      ++cnt;
      return this;
    }

    /**
     * Returns a new {@code TinyMap}.
     *
     * @param <K0> The type of the keys in the map
     * @param <V0> The type of the values in the map
     * @return A new {@code TinyMap}
     */
    public <K0, V0> TinyMap<K0, V0> seal() {
      Check.with(illegalState(), cnt).is(eq(), data.length, ERR_NOT_COMPLETE, data.length);
      return new TinyMap<>(data);
    }
  }

  /**
   * Creates a {@code TinyMap} for 5 key-value pairs.
   *
   * @param <K> The type of the keys in the map
   * @param <V> The type of the values in the map
   * @param k0 The 1st key
   * @param v0 The 1st value
   * @param k1 The 2nd key
   * @param v1 The 2nd value
   * @return a {@code TinyMap} with 2 key-value pairs
   */
  public static <K, V> TinyMap<K, V> of(K k0, V v0, K k1, V v1) {
    TinyMap<K, V> m = new TinyMap<>(2);
    m.data[0][0] = k0;
    m.data[0][1] = v0;
    m.data[1][0] = k1;
    m.data[1][1] = v1;
    return m;
  }

  /**
   * Creates a {@code TinyMap} for 3 key-value pairs.
   *
   * @param <K> The type of the keys in the map
   * @param <V> The type of the values in the map
   * @param k0 The 1st key
   * @param v0 The 1st value
   * @param k1 The 2nd key
   * @param v1 The 2nd value
   * @param k2 The 3rd key
   * @param v2 The 3rd value
   * @return a {@code TinyMap} with 3 key-value pairs
   */
  public static <K, V> TinyMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2) {
    TinyMap<K, V> m = new TinyMap<>(3);
    m.data[0][0] = k0;
    m.data[0][1] = v0;
    m.data[1][0] = k1;
    m.data[1][1] = v1;
    m.data[2][0] = k2;
    m.data[2][1] = v2;
    return m;
  }

  /**
   * Creates a {@code TinyMap} for 4 key-value pairs.
   *
   * @param <K> The type of the keys in the map
   * @param <V> The type of the values in the map
   * @param k0 The 1st key
   * @param v0 The 1st value
   * @param k1 The 2nd key
   * @param v1 The 2nd value
   * @param k2 The 3rd key
   * @param v2 The 3rd value
   * @param k3 The 4th key
   * @param v3 The 4th value
   * @return a {@code TinyMap} with 4 key-value pairs
   */
  public static <K, V> TinyMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3) {
    TinyMap<K, V> m = new TinyMap<>(4);
    m.data[0][0] = k0;
    m.data[0][1] = v0;
    m.data[1][0] = k1;
    m.data[1][1] = v1;
    m.data[2][0] = k2;
    m.data[2][1] = v2;
    m.data[3][0] = k3;
    m.data[3][1] = v3;
    return m;
  }

  /**
   * Creates a {@code TinyMap} for 5 key-value pairs.
   *
   * @param <K> The type of the keys in the map
   * @param <V> The type of the values in the map
   * @param k0 The 1st key
   * @param v0 The 1st value
   * @param k1 The 2nd key
   * @param v1 The 2nd value
   * @param k2 The 3rd key
   * @param v2 The 3rd value
   * @param k3 The 4th key
   * @param v3 The 4th value
   * @param k4 The 5th key
   * @param v4 The 5th value
   * @return a {@code TinyMap} with 5 key-value pairs
   */
  public static <K, V> TinyMap<K, V> of(
      K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    TinyMap<K, V> m = new TinyMap<>(3);
    m.data[0][0] = k0;
    m.data[0][1] = v0;
    m.data[1][0] = k1;
    m.data[1][1] = v1;
    m.data[2][0] = k2;
    m.data[2][1] = v2;
    m.data[3][0] = k3;
    m.data[3][1] = v3;
    m.data[4][0] = k0;
    m.data[4][1] = v4;
    return m;
  }

  /**
   * Returns a builder object for {@code TinyMap} instances.
   *
   * @param <K> The type of the keys in the map
   * @param <V> The type of the values in the map
   * @param size The number of key-value pairs you intend to {@link Builder#add add} to the map. You
   *     <i>must</i> add exactly this many key-value pairs.
   * @return A object for {@code TinyMap} instances
   */
  public static <K, V> Builder<K, V> open(int size) {
    return new Builder<>(size);
  }

  private final Object[][] data;

  private TinyMap(Object[][] data) {
    this.data = data;
  }

  private TinyMap(int size) {
    data = new Object[size][2];
  }

  @Override
  public int size() {
    return data.length;
  }

  @Override
  public boolean isEmpty() {
    return data.length == 0;
  }

  @Override
  public boolean containsKey(Object key) {
    return stream().mapToObj(i -> data[i][0]).anyMatch(k -> Objects.equals(k, key));
  }

  @Override
  public boolean containsValue(Object value) {
    return stream().mapToObj(i -> data[i][1]).anyMatch(v -> Objects.equals(v, value));
  }

  @Override
  public V get(Object key) {
    for (int i = 0; i < data.length; ++i) {
      if (Objects.equals(key, data[i][0])) {
        return (V) data[i][1];
      }
    }
    return null;
  }

  /** Throws an {@link UnsupportedOperationException}. */
  @Override
  public V put(K key, V value) {
    throw new UnsupportedOperationException();
  }

  /** Throws an {@link UnsupportedOperationException}. */
  @Override
  public V remove(Object key) {
    throw new UnsupportedOperationException();
  }

  /** Throws an {@link UnsupportedOperationException}. */
  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    throw new UnsupportedOperationException();
  }

  /** Throws an {@link UnsupportedOperationException}. */
  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<K> keySet() {
    return (Set<K>) stream().mapToObj(i -> data[i][0]).collect(toCollection(LinkedHashSet::new));
  }

  @Override
  public Collection<V> values() {
    return (Collection<V>) stream().mapToObj(i -> data[i][1]).collect(toList());
  }

  @Override
  public Set<Map.Entry<K, V>> entrySet() {
    return stream()
        .mapToObj(i -> data[i])
        .map(
            kv ->
                new Map.Entry<K, V>() {

                  @Override
                  public K getKey() {
                    return (K) kv[0];
                  }

                  @Override
                  public V getValue() {
                    return (V) kv[1];
                  }

                  @Override
                  public V setValue(V value) {
                    throw new UnsupportedOperationException();
                  }
                })
        .collect(toCollection(LinkedHashSet::new));
  }

  private IntStream stream() {
    return IntStream.range(0, data.length);
  }
}
