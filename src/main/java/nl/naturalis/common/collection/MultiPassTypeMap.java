package nl.naturalis.common.collection;

import nl.naturalis.common.Tuple2;
import nl.naturalis.common.check.Check;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.check.CommonChecks.sameAs;
import static nl.naturalis.common.check.CommonGetters.type;

abstract sealed class MultiPassTypeMap<V> extends TypeMap<V> permits SimpleTypeMap {

  final boolean autoExpand;

  MultiPassTypeMap(boolean autoExpand, boolean autobox) {
    super(autobox);
    this.autoExpand = autoExpand;
  }

  abstract Map<Class<?>, V> backend();

  @Override
  public V get(Object key) {
    Check.notNull(key, "key").has(type(), sameAs(), Class.class);
    Class<?> type = (Class<?>) key;
    Tuple2<Class<?>, V> tuple = find(type);
    if (tuple == null) {
      return null;
    }
    if (autoExpand && type != tuple.one()) {
      backend().put(type, tuple.two());
    }
    return tuple.two();
  }

  @Override
  public boolean containsKey(Object key) {
    Check.notNull(key, "key").has(type(), sameAs(), Class.class);
    Class<?> type = (Class<?>) key;
    Tuple2<Class<?>, V> entry = find(type);
    if (entry == null) {
      return false;
    }
    if (autoExpand && type != entry.one()) {
      backend().put(type, entry.two());
    }
    return true;
  }

  private Tuple2<Class<?>, V> find(Class<?> k) {
    V v = backend().get(k);
    return v == null ? k.isArray() ? findArrayType(k) : findSimpleType(k) : Tuple2.of(k, v);
  }

  private Tuple2<Class<?>, V> findSimpleType(Class<?> k) {
    if (k.isInterface()) {
      Tuple2<Class<?>, V> t = climbInterfaces(k, false);
      return t == null ? defaultValue() : t;
    }
    // We don't want to search for Object.class just yet.
    for (Class<?> c = k.getSuperclass(); c != null && c != Object.class; c = c.getSuperclass()) {
      V v = backend().get(c);
      if (v != null) {
        return Tuple2.of(c, v);
      }
    }
    for (Class<?> c = k; c != null && c != Object.class; c = c.getSuperclass()) {
      Tuple2<Class<?>, V> t = climbInterfaces(c, false);
      if (t != null) {
        return t;
      }
    }
    if (autobox && k.isPrimitive()) {
      return find(box(k));
    }
    return defaultValue();
  }

  private Tuple2<Class<?>, V> findArrayType(Class<?> k) {
    Class<?> elementType = k.componentType();
    if (elementType.isInterface()) {
      Tuple2<Class<?>, V> t = climbInterfaces(elementType, true);
      return t == null ? defaultValue() : t;
    }
    for (Class<?> c = elementType.getSuperclass(); c != null; c = c.getSuperclass()) {
      Class<?> arrayType = c.arrayType();
      V v = backend().get(arrayType);
      if (v != null) {
        return Tuple2.of(arrayType, v);
      }
    }
    for (Class<?> c = elementType; c != null; c = c.getSuperclass()) {
      Tuple2<Class<?>, V> t = climbInterfaces(c, true);
      if (t != null) {
        return t;
      }
    }
    if (autobox && elementType.isPrimitive()) {
      return find(box(elementType).arrayType());
    }
    return defaultValue();
  }

  private AtomicReference<Tuple2<Class<?>, V>> defVal;

  private Tuple2<Class<?>, V> defaultValue() {
    if (defVal == null) {
      V val = backend().get(Object.class);
      Tuple2<Class<?>, V> tuple = val == null ? null : Tuple2.of(Object.class, val);
      defVal = new AtomicReference<>(tuple);
    }
    return defVal.getPlain();
  }

  private Tuple2<Class<?>, V> climbInterfaces(Class<?> clazz, boolean array) {
    for (Class<?> c : clazz.getInterfaces()) {
      Class<?> c0 = array ? c.arrayType() : c;
      V v = backend().get(c0);
      if (v != null) {
        return Tuple2.of(c0, v);
      }
    }
    // Start new loop for breadth-first traversal. We prefer
    // more specific interfaces over less specific interfaces
    for (Class<?> c : clazz.getInterfaces()) {
      Tuple2<Class<?>, V> t = climbInterfaces(c, array);
      if (t != null) {
        return t;
      }
    }
    return null;
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

}
