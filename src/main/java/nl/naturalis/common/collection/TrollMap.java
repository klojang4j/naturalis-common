package nl.naturalis.common.collection;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * A minimalist {@code Map} implementation potentially very efficient when storing a tiny amount of
 * key-value pairs in a write-once/read-once scenario, with compile-time knowledge about what the
 * keys will be. Though this class implements the {@code Map} interface, it does not obey its
 * contract (hence the name). Keys are not checked for uniqueness. The key-value pairs are simply
 * added to a two-dimensinal array. The {@link #get(Object) get} method returns the first matching
 * element within the array. A {@code TrollMap} is not modifiable. All operations that would modify
 * the map throw an {@link UnsupportedOperationException}.
 *
 * @author Ayco Holleman
 */
@SuppressWarnings({"unchecked"})
public class TrollMap<K, V> implements Map<K, V> {

  public static class Builder<K, V> {
    private static final String ERR_NO_CAPACITY = "TrollMap at full capacity";
    private final Object[][] data;
    private int cnt;

    private Builder(int size) {
      this.data = new Object[size][];
    }

    public Builder<K, V> add(K k, V v) {
      Check.with(ArrayIndexOutOfBoundsException::new, cnt).is(lt(), data.length, ERR_NO_CAPACITY);
      data[cnt][0] = k;
      data[cnt][1] = v;
      return this;
    }

    public <K0, V0> TrollMap<K0, V0> seal() {
      return new TrollMap<>(data);
    }
  }

  public static class Entry<K, V> implements Map.Entry<K, V> {
    private final Object[] kv;

    private Entry(Object[] kv) {
      this.kv = kv;
    }

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

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.deepHashCode(kv);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      @SuppressWarnings("rawtypes")
      Entry other = (Entry) obj;
      return Arrays.deepEquals(kv, other.kv);
    }
  }

  public static <K, V> TrollMap<K, V> of(K k0, V v0, K k1, V v1) {
    TrollMap<K, V> m = new TrollMap<>(2);
    m.data[0][0] = k0;
    m.data[0][1] = v0;
    m.data[1][0] = k1;
    m.data[1][1] = v1;
    return m;
  }

  public static <K, V> TrollMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2) {
    TrollMap<K, V> m = new TrollMap<>(3);
    m.data[0][0] = k0;
    m.data[0][1] = v0;
    m.data[1][0] = k1;
    m.data[1][1] = v1;
    m.data[2][0] = k2;
    m.data[2][1] = v2;
    return m;
  }

  public static <K, V> TrollMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3) {
    TrollMap<K, V> m = new TrollMap<>(4);
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

  public static <K, V> TrollMap<K, V> of(
      K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    TrollMap<K, V> m = new TrollMap<>(3);
    m.data[0][0] = k0;
    m.data[0][1] = v0;
    m.data[1][0] = k1;
    m.data[1][1] = v1;
    m.data[2][0] = k2;
    m.data[2][1] = v2;
    m.data[3][0] = k3;
    m.data[3][1] = v3;
    m.data[4][0] = k4;
    m.data[4][1] = v4;
    return m;
  }

  public static <K, V> Builder<K, V> open(int size) {
    return new Builder<>(size);
  }

  private final Object[][] data;

  private TrollMap(Object[][] data) {
    this.data = data;
  }

  private TrollMap(int size) {
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
    for (int i = 0; i < data.length; ++i) {
      if (Objects.equals(key, data[i][0])) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean containsValue(Object value) {
    for (int i = 0; i < data.length; ++i) {
      if (Objects.equals(value, data[i][1])) {
        return true;
      }
    }
    return false;
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

  @Override
  public V put(K key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<K> keySet() {
    return (Set<K>)
        IntStream.range(0, data.length)
            .mapToObj(i -> data[i][0])
            .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public Collection<V> values() {
    return (Collection<V>)
        IntStream.range(0, data.length).mapToObj(i -> data[i][1]).collect(Collectors.toList());
  }

  @Override
  public Set<Map.Entry<K, V>> entrySet() {
    return IntStream.range(0, data.length)
        .mapToObj(i -> data[i])
        .map(kv -> new Entry<K, V>(kv))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
