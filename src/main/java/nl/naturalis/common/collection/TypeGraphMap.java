package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.AbstractMap.SimpleImmutableEntry;
import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.ObjectMethods.ifNull;
import static nl.naturalis.common.check.CommonChecks.instanceOf;

/**
 * A {@link TypeMap} extension that stores its entries in a data structure that reflects the Java
 * type hierarchy. The requested type and its supertypes, if present, will be found in a single
 * traversal of the data structure. While a {@link SimpleTypeMap} climbs the type's class and
 * interface hierarchy, a {@code TypeGraphMap} comes in from the top of the type hierarchy and works
 * its way down until it encounters the requested type. Consequently, it exhibits a pointed
 * lopsidedness in the performance of {@link #containsKey(Object)} and {@link #get(Object) get}.
 * {@code containsKey} can return ({@code true}) as soon as it encounters a supertype of the
 * requested type (which would be practically immediate if the map contains {@code Object.class}).
 * The {@code get} method, on the other hand, needs to descend into the type hierarchy until it
 * finds the requested type, or knows for sure it is absent.
 *
 * <p>The key set of a {@code TypeGraphMap} consists of depth-first slices of the type hierarchy.
 *
 * @param <V> The type of the values in the  {@code Map}
 * @see TypeMap
 * @see TypeGraphMapBuilder
 * @see SimpleTypeMap
 */
public final class TypeGraphMap<V> extends TypeMap<V> {

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
      if (isA(type, this.type)) {
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
      if (isA(type, this.type)) {
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

    void collectTypes(Set<Class<?>> set) {
      for (var node : subclasses.values()) {
        set.add(node.type);
        node.collectTypes(set);
      }
      for (var node : subinterfaces.values()) {
        set.add((node.type));
        node.collectTypes(set);
      }
    }

    @SuppressWarnings({"unchecked"})
    <E> void collectValues(Set<E> set) {
      for (var node : subclasses.values()) {
        set.add((E) node.value);
        node.collectValues(set);
      }
      for (var node : subinterfaces.values()) {
        set.add((E) node.value);
        node.collectValues(set);
      }
    }

    @SuppressWarnings({"unchecked"})
    <E> void collectEntries(Set<Entry<Class<?>, E>> set) {
      for (var node : subclasses.values()) {
        set.add(new SimpleImmutableEntry<>(node.type, (E) node.value));
        node.collectEntries(set);
      }
      for (var node : subinterfaces.values()) {
        set.add(new SimpleImmutableEntry<>(node.type, (E) node.value));
        node.collectEntries(set);
      }
    }

  }

  // ================================================================== //
  // ========================= [ TypeGraphMap ] ======================= //
  // ================================================================== //

  /**
   * Converts the specified {@code Map} to a {@code TypeGraphMap}. Autoboxing will be enabled.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @return A {@code TypeGraphMap}
   */
  public static <U> TypeGraphMap<U> copyOf(Map<Class<?>, U> src) {
    return copyOf(src, true);
  }

  /**
   * Converts the specified {@code Map} to a {@code TypeGraphMap}.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @param autobox Whether to enable "autoboxing" (see {@link TypeMap})
   * @return A {@code TypeGraphMap}
   */
  @SuppressWarnings({"unchecked"})
  public static <U> TypeGraphMap<U> copyOf(Map<Class<?>, U> src, boolean autobox) {
    Check.notNull(src, "source map");
    TypeGraphMapBuilder<U> builder = (TypeGraphMapBuilder<U>) build(Object.class);
    builder.autobox(autobox);
    src.forEach(builder::add);
    return builder.freeze();
  }

  /**
   * Returns a builder for {@code TypeHashMap} instances.
   *
   * @param <U> The type of the values in the map
   * @param valueType The class of the values in the map
   * @return A builder for {@code TypeHashMap} instances
   */
  public static <U> TypeGraphMapBuilder<U> build(Class<U> valueType) {
    return Check.notNull(valueType).ok(TypeGraphMapBuilder::new);
  }

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

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings({"unchecked"})
  public V get(Object key) {
    Class<?> type = Check.notNull(key).is(instanceOf(), Class.class).ok(Class.class::cast);
    // interfaces can only be subtypes of other interfaced,
    // so we  don't need to bother with the regular classes
    // in the type tree
    return (V) (type.isInterface() ? root.findInterface(type) : root.findClass(type, autobox));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsKey(Object key) {
    Class<?> type = Check.notNull(key).is(instanceOf(), Class.class).ok(Class.class::cast);
    if (root.value != null) {
      return true;
    }
    if (!type.isInterface()) {
      for (TypeNode tn : root.subclasses.values()) {
        if (isA(type, tn.type)) {
          return true;
        }
      }
    }
    for (TypeNode tn : root.subinterfaces.values()) {
      if (isA(type, tn.type)) {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsValue(Object value) {
    return values().contains(value);
  }

  /**
   * {@inheritDoc}
   */
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

  /**
   * {@inheritDoc}
   */
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

  /**
   * {@inheritDoc}
   */
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

  /**
   * {@inheritDoc}
   */
  @Override
  public int size() {
    return size;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isEmpty() {
    return size != 0;
  }

}
