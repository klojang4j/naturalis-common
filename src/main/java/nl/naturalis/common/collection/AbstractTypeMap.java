package nl.naturalis.common.collection;

import java.util.*;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.Tuple;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.ClassMethods.isWrapper;
import static nl.naturalis.common.ClassMethods.unbox;
import static nl.naturalis.common.check.CommonChecks.sameAs;
import static nl.naturalis.common.check.CommonGetters.type;
import static java.util.stream.Collectors.*;

abstract class AbstractTypeMap<V> implements Map<Class<?>, V> {

  static final String ERR_NULL_KEY = "Source map must not contain null keys";
  static final String ERR_NULL_VAL = "Illegal null value for type %s";

  final Map<Class<?>, V> backend;
  final boolean autoExpand;
  final boolean autobox;

  AbstractTypeMap(Map<? extends Class<?>, ? extends V> m, boolean autobox) {
    this.backend = createBackend(m, autobox);
    this.autoExpand = false;
    this.autobox = autobox;
  }

  /*
   * The expectedSize parameter *can* be used by subclasses to create a backing map of a certain
   * size, if applicable. (It is not applicable for TreeTypeMap since that class is backed by a
   * TreeMap, which cannot be given a size through its constructors.) However, the mere presence of
   * this parameter signals that an auto-expanding instance is requested. Thus the backing map
   * provided by the subclasses must not be unmodifiable.
   */
  AbstractTypeMap(Map<? extends Class<?>, ? extends V> m, int expectedSize, boolean autobox) {
    this.backend = createBackend(m, expectedSize, autobox);
    this.autoExpand = true;
    this.autobox = autobox;
  }

  abstract Map<Class<?>, V> createBackend(Map<? extends Class<?>, ? extends V> m, boolean autobox);

  abstract Map<Class<?>, V> createBackend(
      Map<? extends Class<?>, ? extends V> m, int sz, boolean autobox);

  @Override
  public V get(Object key) {
    Check.notNull(key, "key").has(type(), sameAs(), Class.class);
    Class<?> type = (Class<?>) key;
    Tuple<Class<?>, V> entry = find(type);
    if (entry == null) {
      return null;
    }
    if (autoExpand && type != entry.getLeft()) {
      backend.put(type, entry.getRight());
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
      backend.put(type, entry.getRight());
    }
    return true;
  }

  private Tuple<Class<?>, V> find(Class<?> k) {
    V v = backend.get(k);
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
      if (null != (v = backend.get(c))) {
        return Tuple.of(c, v);
      }
    }
    for (Class<?> c = k; c != null; c = c.getSuperclass()) {
      Tuple<Class<?>, V> t = climbInterfaces(c);
      if (t != null) {
        return t;
      }
    }
    if (autobox) {
      if (k.isPrimitive()) {
        return find(box(k));
      } else if (isWrapper(k)) {
        return find(unbox(k));
      }
    }
    return null;
  }

  private Tuple<Class<?>, V> climbInterfaces(Class<?> clazz) {
    V v;
    for (Class<?> c : clazz.getInterfaces()) {
      if (null != (v = backend.get(c))) {
        return Tuple.of(c, v);
      }
    }
    for (Class<?> c : clazz.getInterfaces()) {
      Tuple<Class<?>, V> t = climbInterfaces(c);
      if (t != null) {
        return t;
      }
    }
    v = backend.get(Object.class);
    return v == null ? null : Tuple.of(Object.class, v);
  }

  @Override
  public V put(Class<?> key, V value) {
    throw notModifiable();
  }

  @Override
  public void putAll(Map<? extends Class<?>, ? extends V> m) {
    throw notModifiable();
  }

  @Override
  public int size() {
    return backend.size();
  }

  @Override
  public boolean isEmpty() {
    return backend.isEmpty();
  }

  @Override
  public boolean containsValue(Object value) {
    return backend.containsValue(value);
  }

  @Override
  public V remove(Object key) {
    throw notModifiable();
  }

  @Override
  public void clear() {
    throw notModifiable();
  }

  @Override
  public Set<Class<?>> keySet() {
    return autoExpand ? Set.copyOf(backend.keySet()) : backend.keySet();
  }

  @Override
  public Collection<V> values() {
    return autoExpand ? Set.copyOf(backend.values()) : backend.values();
  }

  @Override
  public Set<Entry<Class<?>, V>> entrySet() {
    return autoExpand ? Set.copyOf(backend.entrySet()) : backend.entrySet();
  }

  @Override
  public int hashCode() {
    return backend.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return backend.equals(obj);
  }

  @Override
  public String toString() {
    return backend.toString();
  }

  public List<String> prettyTypeNames() {
    return keySet().stream().map(ClassMethods::className).collect(toUnmodifiableList());
  }

  public List<String> prettySimpleTypeNames() {
    return keySet().stream().map(ClassMethods::simpleClassName).collect(toUnmodifiableList());
  }

  private UnsupportedOperationException notModifiable() {
    return new UnsupportedOperationException(getClass().getSimpleName() + " not modifiable");
  }
}
