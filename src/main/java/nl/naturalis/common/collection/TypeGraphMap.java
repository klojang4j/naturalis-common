package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.*;

import static java.util.AbstractMap.SimpleImmutableEntry;
import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.check.CommonChecks.instanceOf;

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
          // interfaces can only be subtypes of other interfaces
          for (int i = subtypes.length - 1; i >= 0 && subtypes[i].type.isInterface(); --i) {
            Object val = find(subtypes[i].type, false);
            if (val != null) {
              return val;
            }
          }
        } else {
          // class can be subtypes of classes and interfaces
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

    int countDescendants() {
      int x = subtypes.length;
      for (TypeNode tn : subtypes) {
        x += tn.countDescendants();
      }
      return x;
    }

    void collectTypes(Set<Class<?>> set) {
      for (TypeNode tn : subtypes) {
        set.add(tn.type);
      }
      for (TypeNode tn : subtypes) {
        tn.collectTypes(set);
      }
    }

    @SuppressWarnings({"unchecked"})
    <E> void collectValues(Set<E> set) {
      for (TypeNode tn : subtypes) {
        set.add((E) tn.value);
        tn.collectValues(set);
      }
    }

    @SuppressWarnings({"unchecked"})
    <E> void collectEntries(Set<Entry<Class<?>, E>> set) {
      for (TypeNode tn : subtypes) {
        set.add(new SimpleImmutableEntry<>(tn.type, (E) tn.value));
      }
      for (TypeNode tn : subtypes) {
        tn.collectValues(set);
      }
    }

  }

  private final TypeNode root;

  private int size = -1;
  private Set<Class<?>> keys;
  private Collection<V> values;
  private Set<Entry<Class<?>, V>> entries;

  TypeGraphMap(TypeNode root, boolean autobox) {
    super(autobox);
    this.root = root;
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
    return values().contains(value);
  }

  @Override
  public Set<Class<?>> keySet() {
    if (keys == null) {
      Set<Class<?>> set = new LinkedHashSet<>(1 + size() * 4 / 3);
      if (root.type != null) { // map contains Object.class
        set.add(Object.class);
      }
      root.collectTypes(set);
      keys = Collections.unmodifiableSet(set);
    }
    return keys;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public Collection<V> values() {
    if (values == null) {
      Set<V> set = new HashSet<>(1 + size() * 4 / 3);
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
      Set<Entry<Class<?>, V>> set = new LinkedHashSet<>(1 + size() * 4 / 3);
      if (root.type != null) {
        set.add(new SimpleImmutableEntry<>(Object.class, (V) root.value));
      }
      root.collectEntries(set);
      entries = Collections.unmodifiableSet(set);
    }
    return entries;
  }

  @Override
  public int size() {
    if (size == -1) {
      size = root.countDescendants();
      if (root.type != null) {
        ++size;
      }
    }
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size() != 0;
  }

}
