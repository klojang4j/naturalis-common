package nl.naturalis.common.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import nl.naturalis.common.Tuple;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.greaterThan;
import static nl.naturalis.common.check.CommonChecks.negative;
import static nl.naturalis.common.check.CommonChecks.notNull;
import static nl.naturalis.common.check.CommonChecks.sameAs;
import static nl.naturalis.common.check.CommonGetters.type;

/**
 * A {@link HashMap} extension that returns a non-null value for a type if either the type itself or
 * any of its super types is present in the map. For example, suppose the map contains two entries:
 * one for {@code Integer.class} and one for {@code Number.class}. If the client requests the value
 * associated with {@code Integer.class}, then the value of the {@code Integer.class} entry is
 * returned. But if the client requests the value associated with {@code Short.class}, then the
 * value of the {@code Number.class} entry is returned. Thus this {@code Map} implementation is
 * useful if you want to define fall-back values for types that have not been explicitly added to
 * the map.
 *
 * <p>Note that as, in the above example, the value of the{@code Number.class} entry is returned, a
 * new entry for {@code Short.class} is tacitly created so that next time round the client will hit
 * that key directly. Keep this in mind when sizing the map. You can suppress this behaviour via the
 * constructors.
 *
 * <p>All map-altering methods except {@code put} and {@code putAll} throw an {@link
 * UnsupportedOperationException}. The same applies to all {@code compute} methods as well as {@code
 * getOrDefault}.
 *
 * @author Ayco Holleman
 * @param <V> The type of the values in the {@code}
 */
public class TypeMap<V> extends HashMap<Class<?>, V> {

  private final boolean autoExpand;

  public TypeMap() {
    this(16);
  }

  public TypeMap(int initialCapacity) {
    this(initialCapacity, .75F, true);
  }

  public TypeMap(Map<? extends Class<?>, ? extends V> m) {
    this(m, true);
  }

  public TypeMap(Map<? extends Class<?>, ? extends V> m, boolean autoGrow) {
    this(capacity(m, autoGrow), 0.75F, autoGrow);
    putAll(m);
  }

  private static <V0> int capacity(Map<? extends Class<?>, ? extends V0> m, boolean autoExpand) {
    Check.notNull(m);
    int i = 1 + ((4 * m.size()) / 3);
    // If autoGrow, reserve some extra space for tacit additions
    return autoExpand ? (int) (1.25 * i) : i;
  }

  public TypeMap(int initialCapacity, float loadFactor, boolean autoExpand) {
    super(
        Check.that(initialCapacity, "initialCapacity").isNot(negative()).intValue(),
        Check.that(loadFactor, "loadFactor").is(greaterThan(), 0).ok());
    this.autoExpand = autoExpand;
  }

  @Override
  public V get(Object key) {
    Check.notNull(key, "key").has(type(), sameAs(), Class.class);
    Class<?> type = (Class<?>) key;
    Tuple<Class<?>, V> entry = find(type);
    if (entry == null) {
      return null;
    }
    if (autoExpand && type != entry.getLeft()) {
      super.put(type, entry.getRight());
    }
    return entry.getRight();
  }

  @Override
  public boolean containsKey(Object key) {
    Check.notNull(key).has(type(), sameAs(), Class.class);
    Class<?> type = (Class<?>) key;
    Tuple<Class<?>, V> entry = find(type);
    if (entry == null) {
      return false;
    }
    if (autoExpand && type != entry.getLeft()) {
      super.put(type, entry.getRight());
    }
    return true;
  }

  private Tuple<Class<?>, V> find(Class<?> k) {
    V v = super.get(k);
    if (v != null) {
      return Tuple.of(k, v);
    }
    if (k.isInterface()) {
      Tuple<Class<?>, V> t = climbInterfaces(k);
      if (t != null) {
        return t;
      }
    }
    for (Class<?> c = k.getSuperclass(); c != null; c = c.getSuperclass()) {
      if (null != (v = super.get(c))) {
        return Tuple.of(c, v);
      }
    }
    for (Class<?> c = k; c != null; c = c.getSuperclass()) {
      Tuple<Class<?>, V> t = climbInterfaces(c);
      if (t != null) {
        return t;
      }
    }
    return null;
  }

  private Tuple<Class<?>, V> climbInterfaces(Class<?> clazz) {
    V v;
    for (Class<?> c : clazz.getInterfaces()) {
      if (null != (v = super.get(c))) {
        return Tuple.of(c, v);
      }
    }
    for (Class<?> c : clazz.getInterfaces()) {
      Tuple<Class<?>, V> t = climbInterfaces(c);
      if (t != null) {
        return t;
      }
    }
    v = super.get(Object.class);
    return v == null ? null : Tuple.of(Object.class, v);
  }

  @Override
  public V put(Class<?> key, V value) {
    Check.notNull(key, "key");
    Check.that(value).is(notNull(), "Illegal null value for type %s", key.getName());
    return super.put(key, value);
  }

  @Override
  public void putAll(Map<? extends Class<?>, ? extends V> m) {
    m.entrySet().forEach(e -> put(e.getKey(), e.getValue()));
  }

  @Override
  public V getOrDefault(Object key, V defaultValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V putIfAbsent(Class<?> key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean replace(Class<?> key, V oldValue, V newValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V replace(Class<?> key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V computeIfAbsent(Class<?> key, Function<? super Class<?>, ? extends V> mappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V computeIfPresent(
      Class<?> key, BiFunction<? super Class<?>, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V compute(
      Class<?> key, BiFunction<? super Class<?>, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V merge(
      Class<?> key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V remove(Object key) {
    return super.remove(key);
  }

  @Override
  public boolean remove(Object key, Object value) {
    return super.remove(key, value);
  }

  @Override
  public void replaceAll(BiFunction<? super Class<?>, ? super V, ? extends V> function) {
    throw new UnsupportedOperationException();
  }
}
