package nl.naturalis.common.collection;

import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.x.collection.ArraySet;

import java.util.*;

import static java.util.AbstractMap.SimpleImmutableEntry;
import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.ObjectMethods.ifNull;
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

    Object findClass(Class<?> type, boolean autobox) {
      if (ClassMethods.isSubtype(type, this.type)) {
        Object val = findClass(type, subclasses);
        if (val == null) {
          val = findClass(type, subinterfaces);
        }
        return ifNull(val, value);
      } else if (type.isPrimitive()) {
        // this must be the root node b/c *everything*
        // is an Object except primitive types
        return autobox ? findClass(box(type), false) : this.value;
      }
      return null;
    }

    Object findInterface(Class<?> type) {
      if (ClassMethods.isSubtype(type, this.type)) {
        return ifNull(findInterface(type, subinterfaces), this.value);
      }
      return null;
    }

    static Object findClass(Class<?> type, Map<Class<?>, TypeNode> typeNodes) {
      var node = typeNodes.get(type);
      if (node == null) {
        for (TypeNode n : typeNodes.values()) {
          Object val = n.findClass(type, false);
          if (val != null) {
            return val;
          }
        }
        return null;
      }
      return node.value;
    }

    static Object findInterface(Class<?> type, Map<Class<?>, TypeNode> typeNodes) {
      var node = typeNodes.get(type);
      if (node == null) {
        for (TypeNode typeNode : typeNodes.values()) {
          Object val = typeNode.findInterface(type);
          if (val != null) {
            return val;
          }
        }
        return null;
      }
      return node.value;
    }

    void collectTypes(List<Class<?>> bucket) {
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
    <E> void collectValues(Set<E> bucket) {
      for (var node : subclasses.values()) {
        bucket.add((E) node.value);
        node.collectValues(bucket);
      }
      for (var node : subinterfaces.values()) {
        bucket.add((E) node.value);
        node.collectValues(bucket);
      }
    }

    @SuppressWarnings({"unchecked"})
    <E> void collectEntries(List<Entry<Class<?>, E>> bucket) {
      for (var node : subclasses.values()) {
        bucket.add(new SimpleImmutableEntry<>(node.type, (E) node.value));
        node.collectEntries(bucket);
      }
      for (var node : subinterfaces.values()) {
        bucket.add(new SimpleImmutableEntry<>(node.type, (E) node.value));
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
   * @param src The {@code Map} to convert
   * @return A {@code TypeGraph}
   */
  public static <U> TypeGraph<U> copyOf(Map<Class<?>, U> src) {
    return copyOf(src, true);
  }

  /**
   * Converts the specified {@code Map} to a {@code TypeGraph}.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @param autobox Whether to enable "autoboxing" (see {@link AbstractTypeMap})
   * @return A {@code TypeGraph}
   */
  @SuppressWarnings({"unchecked"})
  public static <U> TypeGraph<U> copyOf(Map<Class<?>, U> src, boolean autobox) {
    Check.notNull(src, "source map");
    TypeGraphBuilder<U> builder = (TypeGraphBuilder<U>) build(Object.class);
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
    // interfaces can only be subtypes of other interfaced,
    // so we  don't need to bother with the regular classes
    // in the type tree
    return (V) (type.isInterface() ? root.findInterface(type) : root.findClass(type,
        autobox));
  }

  @Override
  public boolean containsKey(Object key) {
    Class<?> type = Check.notNull(key)
        .is(instanceOf(), Class.class)
        .ok(Class.class::cast);
    if (root.value != null) {
      return true;
    }
    if (!type.isInterface()) {
      for (TypeNode tn : root.subclasses.values()) {
        if (ClassMethods.isSubtype(type, tn.type)) {
          return true;
        }
      }
    }
    for (TypeNode tn : root.subinterfaces.values()) {
      if (ClassMethods.isSubtype(type, tn.type)) {
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
    return size != 0;
  }

}
