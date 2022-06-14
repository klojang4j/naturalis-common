package nl.naturalis.common.collection;

import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.x.collection.ArraySet;

import java.util.*;

import static java.util.AbstractMap.SimpleImmutableEntry;
import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.ObjectMethods.ifNull;
import static nl.naturalis.common.check.CommonChecks.instanceOf;

/**
 * A {@link TypeMap} that stores its entries in a data structure similar to the one
 * used by {@link TypeGraph}, but is sensitive to the order in which the types are
 * inserted into the map. Thus, if you expect, for example, {@code String.class} to
 * be requested often compared to the other types, it pays to {@link
 * LinkedTypeGraphBuilder#add(Class, Object) add} that type first.
 *
 * <p>The key set provides a depth-first view of the type hierarchy.
 *
 * @param <V> The type of the values in the {@code Map}
 * @see TypeGraph
 * @see LinkedTypeGraphBuilder
 * @see TypeHashMap
 */
public final class LinkedTypeGraph<V> extends AbstractTypeMap<V> {

  // ================================================================== //
  // ======================= [ LinkedTypeNode ] ======================= //
  // ================================================================== //

  static final class LinkedTypeNode {

    private final Class<?> type;
    private final Object value;
    private final LinkedTypeNode[] subclasses;
    private final LinkedTypeNode[] subinterfaces;

    LinkedTypeNode(Class<?> type,
        Object value,
        LinkedTypeNode[] subclasses,
        LinkedTypeNode[] subinterfaces) {
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

    static Object findClass(Class<?> type, LinkedTypeNode[] nodes) {
      for (var node : nodes) {
        Object val = node.findClass(type, false);
        if (val != null) {
          return val;
        }
      }
      return null;
    }

    static Object findInterface(Class<?> type, LinkedTypeNode[] nodes) {
      for (var node : nodes) {
        Object val = node.findClass(type, false);
        if (val != null) {
          return val;
        }
      }
      return null;
    }

    void collectTypesDepthFirst(List<Class<?>> bucket) {
      LinkedTypeNode[] subtypes = ArrayMethods.concat(subinterfaces, subclasses);
      for (var node : subtypes) {
        bucket.add(node.type);
        node.collectTypesDepthFirst(bucket);
      }
    }

    void collectTypesBreadthFirst(List<Class<?>> bucket) {
      LinkedTypeNode[] subtypes = ArrayMethods.concat(subinterfaces, subclasses);
      for (var node : subtypes) {
        bucket.add(node.type);
      }
      for (var node : subtypes) {
        node.collectTypesBreadthFirst(bucket);
      }
    }

    @SuppressWarnings({"unchecked"})
    <E> void collectValues(Set<E> set) {
      for (var node : subclasses) {
        set.add((E) node.value);
        node.collectValues(set);
      }
      for (var node : subinterfaces) {
        set.add((E) node.value);
        node.collectValues(set);
      }
    }

    @SuppressWarnings({"unchecked"})
    <E> void collectEntries(List<Entry<Class<?>, E>> bucket) {
      for (var node : subclasses) {
        bucket.add(new SimpleImmutableEntry<>(node.type, (E) node.value));
        node.collectEntries(bucket);
      }
      for (var node : subinterfaces) {
        bucket.add(new SimpleImmutableEntry<>(node.type, (E) node.value));
        node.collectEntries(bucket);
      }
    }

  }

  // ============================================================== //
  // ==================== [ LinkedTypeGraph ] ===================== //
  // ============================================================== //

  static final LinkedTypeNode[] NO_SUBTYPES = new LinkedTypeNode[0];

  /**
   * Converts the specified {@code Map} to a {@code TypeGraphMap}. Autoboxing will be
   * enabled.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @return A {@code TypeGraphMap}
   */
  public static <U> LinkedTypeGraph<U> copyOf(Map<Class<?>, U> src) {
    return copyOf(src, true);
  }

  /**
   * Converts the specified {@code Map} to a {@code TypeGraphMap}.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @param autobox Whether to enable "autoboxing" (see {@link AbstractTypeMap})
   * @return A {@code TypeGraphMap}
   */
  @SuppressWarnings({"unchecked"})
  public static <U> LinkedTypeGraph<U> copyOf(Map<Class<?>, U> src,
      boolean autobox) {
    Check.notNull(src, "source map");
    LinkedTypeGraphBuilder<U> builder = (LinkedTypeGraphBuilder<U>) build(Object.class);
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
  public static <U> LinkedTypeGraphBuilder<U> build(Class<U> valueType) {
    return Check.notNull(valueType).ok(LinkedTypeGraphBuilder::new);
  }

  private final LinkedTypeNode root;
  private final int size;

  private Set<Class<?>> keysDF; // depth-first sorted keys
  private Set<Class<?>> keysBF; // breadth-first sorted keys
  private Collection<V> values;
  private Set<Entry<Class<?>, V>> entries;

  LinkedTypeGraph(LinkedTypeNode root, int size, boolean autobox) {
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
    Class<?> type = Check.notNull(key)
        .is(instanceOf(), Class.class)
        .ok(Class.class::cast);
    return type.isInterface()
        ? (V) root.findInterface(type)
        : (V) root.findClass(type, autobox);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsKey(Object key) {
    Class<?> type = Check.notNull(key)
        .is(instanceOf(), Class.class)
        .ok(Class.class::cast);
    if (root.value != null) {
      return true;
    }
    if (!type.isInterface()) {
      if (containsSuper(root.subclasses, type)) {
        return true;
      }
    }
    if (containsSuper(root.subinterfaces, type)) {
      return true;
    }
    return false;
  }

  private static boolean containsSuper(LinkedTypeNode[] nodes, Class<?> type) {
    return Arrays.stream(nodes)
        .filter(node -> isA(type, node.type))
        .findAny()
        .isPresent();
  }

  @Override
  public boolean containsValue(Object value) {
    return values().contains(value);
  }

  /**
   * Returns a depth-first view of the type hierarchy.
   *
   * @return A depth-first view of the type hierarchy
   */
  @Override
  public Set<Class<?>> keySet() {
    if (keysDF == null) {
      List<Class<?>> keys = new ArrayList<>(size);
      if (root.value != null) { // map contains Object.class
        keys.add(Object.class);
      }
      root.collectTypesDepthFirst(keys);
      keysDF = ArraySet.copyOf(keys, true);
    }
    return keysDF;
  }

  /**
   * Returns a breadth-first view of the type hierarchy.
   *
   * @return A breadth-first view of the type hierarchy
   */
  public Set<Class<?>> breadthFirstKeySet() {
    if (keysBF == null) {
      List<Class<?>> keys = new ArrayList<>(size);
      if (root.value != null) { // map contains Object.class
        keys.add(Object.class);
      }
      root.collectTypesBreadthFirst(keys);
      keysBF = ArraySet.copyOf(keys, true);
    }
    return keysBF;
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
      List<Entry<Class<?>, V>> list = new ArrayList<>(size);
      if (root.value != null) {
        list.add(new SimpleImmutableEntry<>(Object.class, (V) root.value));
      }
      root.collectEntries(list);
      entries = ArraySet.copyOf(list, true);
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
