package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.x.collection.ArraySet;

import java.util.*;

import static java.util.AbstractMap.SimpleImmutableEntry;
import static nl.naturalis.common.ClassMethods.*;
import static nl.naturalis.common.CollectionMethods.implode;
import static nl.naturalis.common.check.CommonChecks.instanceOf;

/**
 * A {@link TypeMap} that stores its entries in a data structure that reflects the
 * Java type hierarchy. While {@link TypeHashMap} climbs the requested type's class
 * and interface hierarchy, a {@code TypeGraph} comes in from the top of the type
 * hierarchy and works its way down until it encounters the requested type.
 * Consequently, it exhibits a pointed lopsidedness in the performance of {@link
 * #containsKey(Object)} and {@link #get(Object) get}. {@code containsKey} can return
 * ({@code true}) as soon as it encounters a supertype of the requested type (which
 * would be practically immediate if the map contains {@code Object.class}). The
 * {@code get} method, on the other hand, needs to descend into the type hierarchy
 * until it finds the requested type, or knows for sure it is absent.
 *
 * <p>The key set provides a depth-first view of the type hierarchy.
 *
 * @param <V> The type of the values in the  {@code Map}
 * @see TypeGraphBuilder
 * @see LinkedTypeGraph
 * @see TypeHashMap
 */
public final class TypeGraph<V> extends AbstractTypeMap<V> {

  // ================================================================== //
  // ========================== [ TypeNode ] ========================== //
  // ================================================================== //

  static final class TypeNode {

    private final Class<?> type;
    private final Object value;
    private final Map<Class<?>, TypeNode> subclasses;
    private final Map<Class<?>, TypeNode> subinterfaces;

    TypeNode(Class<?> type,
        Object value,
        Map<Class<?>, TypeNode> subclasses,
        Map<Class<?>, TypeNode> subinterfaces) {
      this.type = type;
      this.value = value;
      this.subclasses = subclasses;
      this.subinterfaces = subinterfaces;
    }

    @SuppressWarnings({"unchecked"})
    private <X> X value() {
      return (X) value;
    }

    private Object findClass(Class<?> type) {
      if (isSubtype(type, this.type)) {
        Object val = findClass(type, subclasses);
        if (val == null) {
          val = findClass(type, subinterfaces);
        }
        return val == null ? value : val;
      }
      return null;
    }

    private Object findInterface(Class<?> type) {
      if (isSubtype(type, this.type)) {
        Object val = findInterface(type, subinterfaces);
        return val == null ? value : val;
      }
      return null;
    }

    private static Object findClass(Class<?> type, Map<Class<?>, TypeNode> nodes) {
      var node = nodes.get(type);
      if (node == null) {
        for (TypeNode n : nodes.values()) {
          Object val = n.findClass(type);
          if (val != null) {
            return val;
          }
        }
        return null;
      }
      return node.value;
    }

    private static Object findInterface(Class<?> type,
        Map<Class<?>, TypeNode> nodes) {
      var node = nodes.get(type);
      if (node == null) {
        for (TypeNode typeNode : nodes.values()) {
          Object val = typeNode.findInterface(type);
          if (val != null) {
            return val;
          }
        }
        return null;
      }
      return node.value;
    }

    private void collectTypes(List<Class<?>> bucket) {
      for (var node : subclasses.values()) {
        bucket.add(node.type);
        node.collectTypes(bucket);
      }
      for (var node : subinterfaces.values()) {
        bucket.add((node.type));
        node.collectTypes(bucket);
      }
    }

    @SuppressWarnings({"unchecked"})
    private <E> void collectValues(Set<E> bucket) {
      for (var node : subclasses.values()) {
        bucket.add(node.value());
        node.collectValues(bucket);
      }
      for (var node : subinterfaces.values()) {
        bucket.add(node.value());
        node.collectValues(bucket);
      }
    }

    @SuppressWarnings({"unchecked"})
    private <E> void collectEntries(List<Entry<Class<?>, E>> bucket) {
      for (var node : subclasses.values()) {
        bucket.add(new SimpleImmutableEntry<>(node.type, node.value()));
        node.collectEntries(bucket);
      }
      for (var node : subinterfaces.values()) {
        bucket.add(new SimpleImmutableEntry<>(node.type, node.value()));
        node.collectEntries(bucket);
      }
    }

  }

  // ================================================================== //
  // ========================= [ TypeGraph ] ======================= //
  // ================================================================== //

  /**
   * Converts the specified {@code Map} to a {@code TypeGraph} with "autoboxing"
   * enabled.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param valueType The class of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @return A {@code TypeGraph} built from the entries in the provided map
   */
  public static <U> TypeGraph<U> copyOf(Class<U> valueType, Map<Class<?>, U> src) {
    return copyOf(valueType, true, src);
  }

  /**
   * Converts the specified {@code Map} to a {@code TypeGraph}.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param valueType The class of the values in the {@code Map}
   * @param autobox Whether to enable "autoboxing" (see {@link AbstractTypeMap})
   * @param src The {@code Map} to convert
   * @return A {@code TypeGraph} built from the entries in the provided map
   */
  public static <U> TypeGraph<U> copyOf(Class<U> valueType,
      boolean autobox,
      Map<Class<?>, U> src) {
    Check.notNull(src, "source map");
    TypeGraphBuilder<U> builder = build(valueType);
    builder.autobox(autobox);
    src.forEach(builder::add);
    return builder.freeze();
  }

  /**
   * Returns a builder for {@code TypeGraph} instances.
   *
   * @param <U> The type of the values in the map
   * @param valueType The class of the values in the map
   * @return A builder for {@code TypeHashMap} instances
   */
  public static <U> TypeGraphBuilder<U> build(Class<U> valueType) {
    return Check.notNull(valueType).ok(TypeGraphBuilder::new);
  }

  private final TypeNode root;
  private final int size;

  private Set<Class<?>> keys;
  private Collection<V> values;
  private Set<Entry<Class<?>, V>> entries;

  TypeGraph(TypeNode root, int size, boolean autobox) {
    super(autobox);
    this.root = root;
    this.size = size;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public V get(Object key) {
    Class<?> type = Check.notNull(key)
        .is(instanceOf(), Class.class)
        .ok(Class.class::cast);
    if (type.isPrimitive()) {
      TypeNode exactMatch = root.subclasses.get(type);
      if (exactMatch == null) {
        if (autobox) {
          return getValue(box(type));
        }
        return root.value();
      }
      return exactMatch.value();
    } else if (type.isArray()) {
      Class<?> elemType = type.getComponentType();
      if (elemType.isPrimitive()) {
        TypeNode exactMatch = root.subclasses.get(type);
        if (exactMatch == null) {
          if (autobox) {
            return getValue(box(elemType).arrayType());
          }
          return root.value();
        }
        return exactMatch.value();
      }
    }
    return getValue(type);
  }

  @SuppressWarnings({"unchecked"})
  private V getValue(Class<?> type) {
    return (V) (type.isInterface()
                    ? root.findInterface(type)
                    : root.findClass(type));
  }

  @Override
  public boolean containsKey(Object key) {
    Class<?> type = Check.notNull(key)
        .is(instanceOf(), Class.class)
        .ok(Class.class::cast);
    if (type.isPrimitive()) {
      boolean exactMatch = root.subclasses.containsKey(type);
      if (!autobox) {
        return exactMatch || root.value != null;
      }
      return exactMatch || hasKey(box(type));
    } else if (type.isArray()) {
      Class<?> elemType = type.getComponentType();
      if (elemType.isPrimitive()) {
        boolean exactMatch = root.subclasses.containsKey(type);
        if (!autobox) {
          return exactMatch || root.value != null;
        }
        return exactMatch || hasKey(box(elemType).arrayType());
      }
    }
    return hasKey(type);
  }

  @Override
  public boolean containsValue(Object value) {
    return values().contains(value);
  }

  @Override
  public Set<Class<?>> keySet() {
    if (keys == null) {
      List<Class<?>> bucket = new ArrayList<>(size);
      if (root.value != null) { // map contains Object.class
        bucket.add(Object.class);
      }
      root.collectTypes(bucket);
      keys = ArraySet.copyOf(bucket, true);
    }
    return keys;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public Collection<V> values() {
    if (values == null) {
      Set<V> bucket = new HashSet<>(1 + size * 4 / 3);
      if (root.value != null) {
        bucket.add((V) root.value);
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
      if (root.value != null) {
        bucket.add(new SimpleImmutableEntry<>(Object.class, (V) root.value));
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

  private boolean hasKey(Class<?> type) {
    if (root.value != null) { // Map contains Object.class
      return true;
    } else if (type.isInterface()) {
      return contains(root.subinterfaces, type);
    }
    return contains(root.subclasses, type) || contains(root.subinterfaces, type);
  }

  private static boolean contains(Map<Class<?>, TypeNode> nodes, Class<?> type) {
    if (nodes.containsKey(type)) {
      return true;
    }
    for (Class<?> c : nodes.keySet()) {
      if (isSupertype(c, type)) {
        return true;
      }
    }
    return false;
  }

}
