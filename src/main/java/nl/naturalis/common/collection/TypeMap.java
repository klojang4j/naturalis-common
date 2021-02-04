package nl.naturalis.common.collection;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.Tuple;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.notContainingKey;
import static nl.naturalis.common.check.CommonChecks.notContainingValue;
import static nl.naturalis.common.check.CommonChecks.notKeyIn;

/**
 * A {@code Map} implementation that will return a non-null value for a type if either the type
 * itself or any of its super types is present in the map. This is useful if you want to define
 * default values or actions for types that have not been explicitly added to the map. Contrary to
 * ordinary maps it is not permitted to add strictly duplicate keys. In other words, once a type is
 * in the map, you cannot overwrite its value any longer. Also, it is only permitted to add a type
 * if none of the super types is present yet. Therefore you must add types in a specific order: the
 * most specific types first, base classes and interfaces last. {@code null} keys and {@null} values
 * are not allowed. It is permitted to add {@code Object.class} but note that {@link
 * Map#containsKey(Object) containsKey()} will then always return true.
 *
 * <p>Although {@code TypeMap} is a modifiable map, the only way to get data into it is via the
 * constructor or via de {@link #put(Class, Object) put} method. All other methods that modify the
 * map through an {@link UnsupportedOperationException}.
 *
 * <p>Obviously this map implementation strongly violates the general {@link Map} contract.
 *
 * @author Ayco Holleman
 * @param <V> The type of the values in the {@code}
 */
public class TypeMap<V> extends TreeMap<Class<?>, V> {

  static final String ERR_DUPLICATE =
      "Class %s or one of its superclasses or interfaces already present. Overwriting not permitted";

  static final Comparator<Class<?>> comp1 =
      (c1, c2) -> {
        if (ClassMethods.isA(c1, c2)) {
          return 0;
        }
        return c1.hashCode() - c2.hashCode();
      };

  static final Comparator<Map.Entry<Class<?>, ?>> comp2 =
      (e1, e2) -> {
        if (e1.getKey() == e2.getKey()) {
          return 0;
        } else if (ClassMethods.isA(e1.getKey(), e2.getKey())) {
          return Integer.MIN_VALUE;
        } else if (ClassMethods.isA(e2.getKey(), e1.getKey())) {
          return Integer.MAX_VALUE;
        }
        return e1.getKey().hashCode() - e2.getKey().hashCode();
      };

  static final Comparator<Tuple<Class<?>, ?>> comp3 =
      (t1, t2) -> {
        if (t1.getLeft() == t2.getLeft()) {
          return 0;
        } else if (ClassMethods.isA(t1.getLeft(), t2.getLeft())) {
          return Integer.MIN_VALUE;
        } else if (ClassMethods.isA(t2.getLeft(), t1.getLeft())) {
          return Integer.MAX_VALUE;
        }
        return t1.getLeft().hashCode() - t2.getLeft().hashCode();
      };

  public TypeMap() {
    super(comp1);
  }

  /**
   * Creates a new {@code TypeMap} from the entries in the specified source map. The source map's
   * entries are first copied and sorted uch that the most specific classes enter this {@code Map}
   * first while base classes and interfaces come last.
   *
   * @param source
   */
  public TypeMap(Map<Class<?>, V> source) {
    super(comp1);
    Check.notNull(source).is(notContainingKey(), null).is(notContainingValue(), null);
    Set<Map.Entry<Class<?>, V>> temp = new TreeSet<>(comp2);
    temp.addAll(source.entrySet());
    temp.forEach(e -> TypeMap.super.put(e.getKey(), e.getValue()));
  }

  TypeMap(TreeSet<Tuple<Class<?>, V>> pairs) {
    if (Check.notNull(pairs).ok().comparator() != comp3) {
      // Just an extra check; constructor is package-private anyhow
      throw new IllegalArgumentException("Bad Comparator");
    }
    pairs.forEach(t -> TypeMap.super.put(t.getLeft(), t.getRight()));
  }

  @Override
  public V put(Class<?> key, V value) {
    Check.notNull(key).is(notKeyIn(), this, ERR_DUPLICATE, key.getName());
    Check.notNull(value, "value");
    return super.put(key, value);
  }

  @Override
  public V putIfAbsent(Class<?> key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V computeIfAbsent(Class<?> key, Function<? super Class<?>, ? extends V> mappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V computeIfPresent(
      Class<?> key, BiFunction<? super Class<?>, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V compute(
      Class<?> key, BiFunction<? super Class<?>, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V merge(
      Class<?> key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map<? extends Class<?>, ? extends V> map) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean replace(Class<?> key, V oldValue, V newValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V replace(Class<?> key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void replaceAll(BiFunction<? super Class<?>, ? super V, ? extends V> function) {
    throw new UnsupportedOperationException();
  }
}
