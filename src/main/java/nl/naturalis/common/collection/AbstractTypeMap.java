package nl.naturalis.common.collection;

import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.Tuple;
import nl.naturalis.common.check.Check;

import java.util.*;

import static java.util.stream.Collectors.toUnmodifiableList;
import static nl.naturalis.common.ClassMethods.*;
import static nl.naturalis.common.ObjectMethods.ifNull;
import static nl.naturalis.common.check.CommonChecks.sameAs;
import static nl.naturalis.common.check.CommonGetters.type;

abstract class AbstractTypeMap<V> implements Map<Class<?>, V> {

  static final String ERR_NULL_KEY = "Source map must not contain null keys";
  static final String ERR_NULL_VAL = "Illegal null value for type ${0}";

  final boolean autoExpand;
  final boolean autobox;

  AbstractTypeMap(boolean autoExpand, boolean autobox) {
    this.autoExpand = autoExpand;
    this.autobox = autobox;
  }

  abstract Map<Class<?>, V> backend();

  @Override
  public V get(Object key) {
    Check.notNull(key, "key").has(type(), sameAs(), Class.class);
    Class<?> type = (Class<?>) key;
    Tuple<Class<?>, V> entry = find(type);
    if (entry == null) {
      return null;
    }
    if (autoExpand && type != entry.getLeft()) {
      backend().put(type, entry.getRight());
    }
    return entry.getRight();
  }

  @Override
  public boolean containsKey(Object key) {
    Class<?> type = (Class<?>) key;
    Tuple<Class<?>, V> entry = find(type);
    if (entry == null) {
      return false;
    }
    if (autoExpand && type != entry.getLeft()) {
      backend().put(type, entry.getRight());
    }
    return true;
  }

  private Tuple<Class<?>, V> find(Class<?> k) {
    V v = backend().get(k);
    if (v != null) {
      return Tuple.of(k, v);
    }
    if (k.isArray()) {
      return findArrayType(k);
    }
    return findSimpleType(k);
  }

  private Tuple<Class<?>, V> findSimpleType(Class<?> k) {
    if (k.isInterface()) {
      Tuple<Class<?>, V> t = climbInterfaces(k, false);
      if (t != null) {
        return t;
      }
    }
    Class<?> root = Object.class;
    for (Class<?> c = k.getSuperclass(); ifNull(c, root) != root; c = c.getSuperclass()) {
      V v = backend().get(c);
      if (v != null) {
        return Tuple.of(c, v);
      }
    }
    for (Class<?> c = k; ifNull(c, root) != root; c = c.getSuperclass()) {
      Tuple<Class<?>, V> t = climbInterfaces(c, false);
      if (t != null) {
        return t;
      }
    }
    if (autobox) {
      if (k.isPrimitive()) {
        return find(box(k));
      }
      if (isWrapper(k)) {
        Class<?> c = unbox(k);
        V v = backend().get(c);
        if (v != null) {
          return Tuple.of(c, v);
        }
      }
    }
    return null;
  }

  private Tuple<Class<?>, V> findArrayType(Class<?> k) {
    Class<?> elementType = k.componentType();
    if (elementType.isInterface()) {
      Tuple<Class<?>, V> t = climbInterfaces(elementType, true);
      if (t != null) {
        return t;
      }
    }
    for (Class<?> c = elementType.getSuperclass(); c != null; c = c.getSuperclass()) {
      Class<?> arrayType = c.arrayType();
      V v = backend().get(arrayType);
      if (v != null) {
        return Tuple.of(arrayType, v);
      }
    }
    for (Class<?> c = elementType; c != null; c = c.getSuperclass()) {
      Tuple<Class<?>, V> t = climbInterfaces(c, true);
      if (t != null) {
        return t;
      }
    }
    if (autobox) {
      if (elementType.isPrimitive()) {
        return find(box(elementType).arrayType());
      }
      if (isWrapper(elementType)) {
        Class<?> c = unbox(elementType).arrayType();
        V v = backend().get(c);
        if (v != null) {
          return Tuple.of(c, v);
        }
      }
    }
    return null;
  }

  private Tuple<Class<?>, V> climbInterfaces(Class<?> clazz, boolean array) {
    for (Class<?> c : clazz.getInterfaces()) {
      Class<?> c0 = array ? c.arrayType() : c;
      V v = backend().get(c0);
      if (v != null) {
        return Tuple.of(c0, v);
      }
    }
    for (Class<?> c : clazz.getInterfaces()) {
      Tuple<Class<?>, V> t = climbInterfaces(c, array);
      if (t != null) {
        return t;
      }
    }
    V v = backend().get(Object.class);
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
    return backend().size();
  }

  @Override
  public boolean isEmpty() {
    return backend().isEmpty();
  }

  @Override
  public boolean containsValue(Object value) {
    return backend().containsValue(value);
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
    return Collections.unmodifiableSet(backend().keySet());
  }

  @Override
  public Collection<V> values() {
    return Collections.unmodifiableCollection(backend().values());
  }

  @Override
  public Set<Entry<Class<?>, V>> entrySet() {
    return Collections.unmodifiableSet(backend().entrySet());
  }

  @Override
  public int hashCode() {
    return backend().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return backend().equals(obj);
  }

  @Override
  public String toString() {
    return backend().toString();
  }

  public List<String> typeNames() {
    return keySet().stream().map(ClassMethods::className).collect(toUnmodifiableList());
  }

  public List<String> simpleTypeNames() {
    return keySet().stream().map(ClassMethods::simpleClassName).collect(toUnmodifiableList());
  }

  private UnsupportedOperationException notModifiable() {
    return new UnsupportedOperationException(getClass().getSimpleName() + " not modifiable");
  }
}
