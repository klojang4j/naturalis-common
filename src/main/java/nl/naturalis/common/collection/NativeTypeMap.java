package nl.naturalis.common.collection;

import nl.naturalis.common.ArrayInfo;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.x.collection.ArraySet;

import java.util.*;

import static nl.naturalis.common.ClassMethods.*;
import static nl.naturalis.common.CollectionMethods.implode;
import static nl.naturalis.common.check.CommonChecks.instanceOf;

abstract sealed class NativeTypeMap<V, TYPE_NODE extends AbstractTypeNode> extends
    AbstractTypeMap<V> permits TypeGraph,
    LinkedTypeGraph {

  TYPE_NODE root;
  int size;

  private Set<Class<?>> keys; // depth-first
  private Set<Class<?>> keysBF; // breadth-first
  private Collection<V> values;
  private Set<Entry<Class<?>, V>> entries;

  NativeTypeMap(TYPE_NODE root, int size, boolean autobox) {
    super(autobox);
    this.root = root;
    this.size = size;
  }

  @Override
  public V get(Object key) {
    Class<?> type = Check.notNull(key)
        .is(instanceOf(), Class.class)
        .ok(Class.class::cast);
    V val;
    if (type.isPrimitive()) {
      if ((val = root.getPrimitive(type)) == null) {
        if (autobox) {
          val = root.get(box(type));
        }
        if (val == null) {
          val = root.value();
        }
      }
    } else if (isPrimitiveArray(type)) {
      var info = ArrayInfo.forClass(type);
      if ((val = root.getPrimitive(type)) == null) {
        if (autobox) {
          val = root.get(info.box());
        }
        if (val == null) {
          val = root.value();
        }
      }
    } else {
      val = root.get(type);
    }
    return val;
  }

  @Override
  public boolean containsKey(Object key) {
    Class<?> type = Check.notNull(key)
        .is(instanceOf(), Class.class)
        .ok(Class.class::cast);
    boolean found = false;
    if (root.value != null
        || (!type.isInterface() && root.findClassExact(type) != null)
        || (type.isInterface() && root.findInterfaceExact(type) != null)) {
      found = true;
    } else if (type.isPrimitive()) {
      if (autobox) {
        found = containsPrimitiveOrBoxedType(type, box(type));
      } else {
        found = containsPrimitiveType(type);
      }
    } else if (isPrimitiveArray(type)) {
      if (autobox) {
        Class<?> boxed = ArrayInfo.forClass(type).box();
        found = containsPrimitiveOrBoxedType(type, boxed);
      } else {
        found = containsPrimitiveType(type);
      }
    } else if (!type.isInterface()) {
      found = containsExactOrSuperType(type, root.subclasses());
      if (!found) {
        found = containsExactOrSuperType(type, root.subinterfaces());
      }
    } else {
      found = containsExactOrSuperType(type, root.subinterfaces());
    }
    return found;
  }

  private boolean containsPrimitiveType(Class<?> type) {
    for (var node : root.subclasses()) {
      if (node.type == type) {
        return true;
      }
    }
    return false;
  }

  private boolean containsPrimitiveOrBoxedType(Class<?> primitive, Class<?> boxed) {
    for (var node : root.subclasses()) {
      if (node.type == primitive || isSupertype(node.type, boxed)) {
        return true;
      }
    }
    return false;
  }

  private boolean containsExactOrSuperType(Class<?> type,
      Collection<? extends AbstractTypeNode> nodes) {
    for (var node : nodes) {
      if (isSupertype(node.type, type)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean containsValue(Object value) {
    return values().contains(value);
  }

  @Override
  public Set<Class<?>> keySet() {
    if (keys == null) {
      List<Class<?>> bucket = new ArrayList<>(size);
      if (root.value() != null) { // map contains Object.class
        bucket.add(Object.class);
      }
      root.collectTypes(bucket);
      keys = ArraySet.copyOf(bucket, true);
    }
    return keys;
  }

  public Set<Class<?>> keySetBreadthFirst() {
    if (keysBF == null) {
      List<Class<?>> bucket = new ArrayList<>(size);
      if (root.value() != null) {
        bucket.add(Object.class);
      }
      root.collectTypes(bucket);
      keysBF = ArraySet.copyOf(bucket, true);
    }
    return keys;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public Collection<V> values() {
    if (values == null) {
      Set<V> bucket = new HashSet<>(1 + size * 4 / 3);
      if (root.value() != null) {
        bucket.add(root.value());
      }
      root.collectValues(bucket);
      values = Set.copyOf(bucket);
    }
    return values;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public Set<Entry<Class<?>, V>> entrySet() {
    if (entries == null) {
      List<Entry<Class<?>, V>> bucket = new ArrayList<>(size);
      if (root.value() != null) {
        bucket.add(new AbstractMap.SimpleImmutableEntry<>(
            Object.class,
            root.value()));
      }
      root.collectEntries(bucket);
      entries = ArraySet.copyOf(bucket, true);
    }
    return entries;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Map m) {
      if (size == m.size()) {
        return entrySet().equals(m.entrySet());
      }
    }
    return false;
  }

  private int hash;

  @Override
  public int hashCode() {
    if (hash == 0) {
      hash = entrySet().hashCode();
    }
    return hash;
  }

  @Override
  public String toString() {
    return '[' + implode(entrySet()) + ']';
  }

}
