package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.check.CommonChecks.instanceOf;

public final class TypeGraphMap<V> implements Map<Class<?>, V> {

  static final class TypeNode {

    private final Class<?> type;
    private final Object value;
    private final TypeNode[] subtypes;

    TypeNode(Class<?> type, Object value, TypeNode[] subtypes) {
      this.type = type;
      this.value = value;
      this.subtypes = subtypes;
    }

    Object find(Class<?> type, boolean autobox) {
      if (type == this.type) {
        return value;
      } else if (isA(type, this.type)) {
        if (type.isInterface()) {
          for (int i = subtypes.length - 1; i >= 0 && subtypes[i].type.isInterface(); --i) {
            Object val = find(subtypes[i].type, false);
            if (val != null) {
              return val;
            }
          }
        } else {
          for (TypeNode subtype : subtypes) {
            Object val = find(subtype.type, false);
            if (val != null) {
              return val;
            }
          }
        }
        return value;
      } else if (autobox && type.isPrimitive()) {
        return find(box(type), false);
      }
      return null;
    }

  }

  private final TypeNode root;
  private final boolean autobox;

  TypeGraphMap(TypeNode root, boolean autobox) {
    this.root = root;
    this.autobox = autobox;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public V get(Object key) {
    Check.that(key, "key").is(instanceOf(), Class.class);
    return (V) root.find((Class<?>) key, autobox);
  }

  @Override
  public boolean containsKey(Object key) {
    Check.that(key, "key").is(instanceOf(), Class.class);
    return root.find((Class<?>) key, autobox) != null;
  }

  @Override
  public boolean containsValue(Object value) {
    return false;
  }

  @Override
  public Set<Class<?>> keySet() {
    return null;
  }

  @Override
  public Collection<V> values() {
    return null;
  }

  @Override
  public Set<Entry<Class<?>, V>> entrySet() {
    return null;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public V put(Class<?> type, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map<? extends Class<?>, ? extends V> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

}
