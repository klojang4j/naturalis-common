package nl.naturalis.common.collection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.AbstractMap.SimpleImmutableEntry;
import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.ObjectMethods.ifNull;

/**
 * The {@code TypeGraphMap} class provides a native implementation of the logic specified by the
 * {@link TypeMap} class. It directly implements the {@link Map} interface and is not backed by a
 * regular map to do the heavy lifting (as is the case with {@link TypeHashMap} and {@link
 * TypeTreeMap}). Consequently, the requested type or, if absent, its supertype(s) will be found in
 * a single operation. In fact, its supertypes (if present) will always be encountered first by the
 * lookup mechanism. While {@code TypeHashMap} and {@code TypeTreeMap} climb the type's class and
 * interface hierarchy, A {@code TypeGraphMap} comes in from the top of the over-all class and
 * interface hierarchy and works its way down until it encounters the requested type. The {@link
 * #keySet() keys} of a {@code TypeGraphMap} are ordered such that no key following another key will
 * ever be a supertype of that key. In other words: broadly from "high" ({@code Object.class}, if
 * present) to "low". See {@link TypeMap} for an explanation of what type maps are about.
 *
 * @param <V> The type of the values in the  {@code Map}
 * @see TypeMap
 * @see TypeHashMap
 * @see TypeTreeMap
 */
public final class TypeGraphMap<V> extends TypeMap<V> {

  // ================================================================== //
  // ========================== [ TypeNode ] ========================== //
  // ================================================================== //

  static final class TypeNode {

    private final Class<?> type;
    private final Object value;
    Map<Class<?>, TypeNode> subclasses;
    Map<Class<?>, TypeNode> subinterfaces;

    TypeNode(Class<?> type,
        Object value,
        Map<Class<?>, TypeNode> subclasses,
        Map<Class<?>, TypeNode> subinterfaces) {
      this.type = type;
      this.value = value;
      this.subclasses = subclasses;
      this.subinterfaces = subinterfaces;
    }

    Object find(Class<?> type, boolean autobox) {
      if (isA(type, this.type)) {
        if (type.isInterface()) {
          // interfaces can only be subtypes of other interfaces
          return ifNull(find(type, subinterfaces), value);
        }
        Object val = find(type, subclasses);
        if (val == null) {
          val = find(type, subinterfaces);
        }
        return ifNull(val, value);
      } else if (autobox && type.isPrimitive()) {
        return find(box(type), false);
      }
      return null;
    }

    static Object find(Class<?> type, Map<Class<?>, TypeNode> typeNodes) {
      TypeNode tn = typeNodes.get(type);
      if (tn == null) {
        for (TypeNode typeNode : typeNodes.values()) {
          Object val = typeNode.find(type, false);
          if (val != null) {
            return val;
          }
        }
        return null;
      }
      return tn.value;
    }

    void collectTypes(Set<Class<?>> set) {
      for (TypeNode tn : subclasses.values()) {
        set.add(tn.type);
        tn.collectTypes(set);
      }
      for (TypeNode tn : subinterfaces.values()) {
        set.add((tn.type));
        tn.collectTypes(set);
      }
    }

    @SuppressWarnings({"unchecked"})
    <E> void collectValues(Set<E> set) {
      for (TypeNode tn : subclasses.values()) {
        set.add((E) tn.value);
        tn.collectValues(set);
      }
      for (TypeNode tn : subinterfaces.values()) {
        set.add((E) tn.value);
        tn.collectValues(set);
      }
    }

    @SuppressWarnings({"unchecked"})
    <E> void collectEntries(Set<Entry<Class<?>, E>> set) {
      for (TypeNode tn : subclasses.values()) {
        set.add(new SimpleImmutableEntry<>(tn.type, (E) tn.value));
        tn.collectEntries(set);
      }
      for (TypeNode tn : subinterfaces.values()) {
        set.add(new SimpleImmutableEntry<>(tn.type, (E) tn.value));
        tn.collectEntries(set);
      }
    }

  }

  // ================================================================== //
  // ========================= [ TypeGraphMap ] ======================== //
  // ================================================================== //

  private final TypeNode root;
  private final int size;

  private Set<Class<?>> keys;
  private Collection<V> values;
  private Set<Entry<Class<?>, V>> entries;

  TypeGraphMap(TypeNode root, int size, boolean autobox) {
    super(autobox);
    this.root = root;
    this.size = size;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public V get(Object key) {
    if (key instanceof Class type) {
      return (V) root.find(type, autobox);
    }
    return null;
  }

  @Override
  public boolean containsKey(Object key) {
    if (key instanceof Class type) {
      if (root.value != null) {
        return true;
      }
      for (TypeNode tn : root.subclasses.values()) {
        if (isA(type, tn.type)) {
          return true;
        }
      }
      for (TypeNode tn : root.subinterfaces.values()) {
        if (isA(type, tn.type)) {
          return true;
        }
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
      Set<Class<?>> set = new HashSet<>(1 + size * 4 / 3);
      if (root.value != null) { // map contains Object.class
        set.add(Object.class);
      }
      root.collectTypes(set);
      keys = Set.copyOf(set);
    }
    return keys;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public Collection<V> values() {
    if (values == null) {
      Set<V> set = new HashSet<>(1 + size * 4 / 3);
      if (root.value != null) {
        set.add((V) root.value);
      }
      root.collectValues(set);
      values = Set.copyOf(set);
    }
    return values;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public Set<Entry<Class<?>, V>> entrySet() {
    if (entries == null) {
      Set<Entry<Class<?>, V>> set = new HashSet<>(1 + size() * 4 / 3);
      if (root.value != null) {
        set.add(new SimpleImmutableEntry<>(Object.class, (V) root.value));
      }
      root.collectEntries(set);
      entries = Set.copyOf(set);
    }
    return entries;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size != 0;
  }

}
