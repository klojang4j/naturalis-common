package nl.naturalis.common.collection;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static nl.naturalis.common.check.CommonChecks.index;
import static nl.naturalis.common.check.CommonChecks.ne;
import static nl.naturalis.common.check.CommonChecks.positive;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import nl.naturalis.common.check.Check;

abstract class AbstractTinyMap<K, V> implements Map<K, V> {

  final Object[][] entries;
  int sz;

  public AbstractTinyMap(int size) {
    entries = Check.that(size, "size").is(positive()).ok(i -> new Object[i][2]);
  }

  @Override
  public int size() {
    return sz;
  }

  @Override
  public boolean isEmpty() {
    return sz == 0;
  }

  @Override
  public boolean containsKey(Object key) {
    return Check.notNull(key).ok(this::indexOf) != -1;
  }

  @Override
  public boolean containsValue(Object value) {
    Check.notNull(value);
    for (int i = 0; i < sz; ++i) {
      if (value.equals(entries[i][1])) {
        return true;
      }
    }
    return false;
  }

  @Override
  @SuppressWarnings("unchecked")
  public V get(Object key) {
    int i = Check.notNull(key).ok(this::indexOf);
    return (V) (i == -1 ? null : entries[i][1]);
  }

  @Override
  @SuppressWarnings("unchecked")
  public V put(K key, V value) {
    Check.notNull(value, "value");
    int i = Check.notNull(key, "key").ok(this::indexOf);
    if (i == -1) {
      Check.on(index(), sz).is(ne(), entries.length, "Map full");
      entries[sz][0] = key;
      entries[sz][1] = value;
      ++sz;
      return null;
    }
    V old = (V) entries[i][1];
    entries[i][1] = value;
    return old;
  }

  @Override
  @SuppressWarnings("unchecked")
  public V remove(Object key) {
    int i = Check.notNull(key).ok(this::indexOf);
    if (i == -1) {
      return null;
    }
    V old = (V) entries[i][1];
    if (i != sz) {
      entries[i][0] = entries[sz][0];
      entries[i][1] = entries[sz][1];
    }
    --sz;
    return old;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    m.entrySet().forEach(e -> put(e.getKey(), e.getValue()));
  }

  @Override
  public void clear() {
    sz = 0;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Set<K> keySet() {
    return Arrays.stream(entries).limit(sz).map(e -> (K) e[0]).collect(toUnmodifiableSet());
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<V> values() {
    return Arrays.stream(entries).limit(sz).map(e -> (V) e[1]).collect(toUnmodifiableSet());
  }

  @Override
  @SuppressWarnings("unchecked")
  public Set<Entry<K, V>> entrySet() {
    return Arrays.stream(entries)
        .limit(sz)
        .map(e -> Map.entry((K) e[0], (V) e[1]))
        .collect(toUnmodifiableSet());
  }

  abstract int indexOf(Object key);
}
