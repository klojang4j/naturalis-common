package nl.naturalis.common.collection;

import java.util.HashMap;
import java.util.Map;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.notNull;

/**
 * A {@code Map} extension that returns a non-null value for a type if either the type itself or any
 * of its super types is present in the map. This allows you to define fall-back values for types
 * that have not been explicitly added to the map. If the requested type is not present in the map,
 * but one of its super types is, a new entry is automatically created, associating the requested
 * type with the super type's value. When searching for a super type within the map, superclasses
 * will take precedence over interfaces. Keys and values must not be null.
 *
 * @author Ayco Holleman
 * @param <V> The type of the values in the {@code}
 */
public class TypeMap<V> extends HashMap<Class<?>, V> {

  public TypeMap() {}

  public TypeMap(int initialCapacity) {
    super(initialCapacity);
  }

  public TypeMap(Map<? extends Class<?>, ? extends V> m) {
    this(m.size());
    putAll(m);
  }

  public TypeMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  @Override
  public V get(Object key) {
    Check.notNull(key);
    return containsKey(key) ? super.get(key) : null;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public boolean containsKey(Object key) {
    Check.notNull(key);
    if (super.containsKey(key)) {
      return true;
    }
    final Class clazz = (Class) key;
    if (clazz.isInterface()) {
      V v = climbInterfaces(clazz);
      if (v != null) {
        super.put(clazz, v);
        return true;
      }
      return false;
    }
    for (Class c = clazz.getSuperclass(); c != null; c = c.getSuperclass()) {
      V v = super.get(c);
      if (v != null) {
        super.put(clazz, v);
        return true;
      }
    }
    for (Class c = clazz; c != null; c = c.getSuperclass()) {
      V v = climbInterfaces(clazz);
      if (v != null) {
        super.put(clazz, v);
        return true;
      }
    }
    return false;
  }

  @SuppressWarnings({"rawtypes"})
  private V climbInterfaces(Class clazz) {
    for (Class c : clazz.getInterfaces()) {
      V v = super.get(c);
      if (v != null) {
        return v;
      }
    }
    for (Class c : clazz.getInterfaces()) {
      V v = climbInterfaces(c);
      if (v != null) {
        return v;
      }
    }
    return super.get(Object.class);
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
  public V putIfAbsent(Class<?> key, V value) {
    throw new UnsupportedOperationException();
  }
}
