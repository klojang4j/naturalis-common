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

/**
 * A {@link TypeMap} extension that stores its entries in a data structure that is similar to the
 * one used by {@link TypeGraphMap}, but more sensitive to the insertion order of the entries. Thus,
 * if you expect, for example, {@code String.class} to be requested very often, it pays to {@link
 * LinkedTypeGraphMapBuilder#add(Class, Object) add} that type first to the map.
 *
 * @param <V> The type of the values in the {@code Map}
 * @see TypeMap
 * @see TypeGraphMap
 * @see LinkedTypeGraphMapBuilder
 * @see SimpleTypeMap
 */
public final class LinkedTypeGraphMap<V> extends TypeMap<V> {

  // ================================================================== //
  // ======================= [ LinkedTypeNode ] ======================= //
  // ================================================================== //

  static final class LinkedTypeNode {

    private final Class<?> type;
    private final Object value;
    private final LinkedTypeNode[] subtypes;

    LinkedTypeNode(Class<?> type, Object value, LinkedTypeNode[] subtypes) {
      this.type = type;
      this.value = value;
      this.subtypes = subtypes;
    }

    Object find(Class<?> type, boolean autobox) {
      if (isA(type, this.type)) {
        if (type.isInterface()) {
          // interfaces can only be subtypes of other interfaces
          for (int i = subtypes.length - 1; i >= 0 && subtypes[i].type.isInterface(); --i) {
            Object val = subtypes[i].find(type, false);
            if (val != null) {
              return val;
            }
          }
          return value;
        } else {
          for (LinkedTypeNode subtype : subtypes) {
            Object val = subtype.find(type, false);
            if (val != null) {
              return val;
            }
          }
        }
      } else if (type.isPrimitive()) {
        // this must be the root node b/c *everything*
        // is an Object except primitive types
        return autobox ? find(box(type), false) : this.value;
      }
      return null;
    }

    void collectTypes(Set<Class<?>> set) {
      for (LinkedTypeNode tn : subtypes) {
        set.add(tn.type);
        tn.collectTypes(set);
      }
    }

    @SuppressWarnings({"unchecked"})
    <E> void collectValues(Set<E> set) {
      for (LinkedTypeNode tn : subtypes) {
        set.add((E) tn.value);
        tn.collectValues(set);
      }
    }

    @SuppressWarnings({"unchecked"})
    <E> void collectEntries(Set<Entry<Class<?>, E>> set) {
      for (LinkedTypeNode tn : subtypes) {
        set.add(new SimpleImmutableEntry<>(tn.type, (E) tn.value));
        tn.collectEntries(set);
      }
    }

  }

  // ================================================================== //
  // ===================== [ LinkedTypeGraphMap ] ===================== //
  // ================================================================== //

  /**
   * Converts the specified {@code Map} to a {@code TypeGraphMap}. Autoboxing will be enabled.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @return A {@code TypeGraphMap}
   */
  public static <U> LinkedTypeGraphMap<U> copyOf(Map<Class<?>, U> src) {
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
  public static <U> LinkedTypeGraphMap<U> copyOf(Map<Class<?>, U> src, boolean autobox) {
    Check.notNull(src, "source map");
    LinkedTypeGraphMapBuilder<U> builder = (LinkedTypeGraphMapBuilder<U>) build(Object.class);
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
  public static <U> LinkedTypeGraphMapBuilder<U> build(Class<U> valueType) {
    return Check.notNull(valueType).ok(LinkedTypeGraphMapBuilder::new);
  }

  private final LinkedTypeNode root;
  private final int size;

  private Set<Class<?>> keys;
  private Collection<V> values;
  private Set<Entry<Class<?>, V>> entries;

  LinkedTypeGraphMap(LinkedTypeNode root, int size, boolean autobox) {
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
    if (key instanceof Class type) {
      return (V) root.find(type, autobox);
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsKey(Object key) {
    if (key instanceof Class type) {
      LinkedTypeNode[] subtypes = root.subtypes;
      if (root.value == null) {
        if (type.isInterface()) {
          for (int i = subtypes.length - 1; i >= 0 && subtypes[i].type.isInterface(); --i) {
            if (isA(type, subtypes[i].type)) {
              return true;
            }
          }
          return false;
        } else if (autobox && type.isPrimitive()) {
          return containsKey(box(type));
        } else {
          for (LinkedTypeNode subtype : subtypes) {
            if (isA(type, subtype.type)) {
              return true;
            }
          }
          return false;
        }
      }
      return true;
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
