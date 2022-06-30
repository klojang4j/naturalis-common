package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.*;
import java.util.function.UnaryOperator;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static nl.naturalis.common.check.CommonChecks.deepNotNull;

/**
 * A {@link TypeMap} that is internally backed by a regular {@link Map}. Type lookups
 * may involve several queries against the backing map. If a type, requested via
 * {@code get} or {@code containsKey}, is not itself present in the backing map, the
 * {@code TypeHashMap} will first climb up the type's class hierarchy and then up its
 * interface hierarchy to see if any of its supertypes is.
 *
 * <h4>Auto-expansion</h4>
 *
 * <p>As required by the {@link TypeMap} specification, {@code TypeHashMap} is
 * an immutable class. All map-altering methods throw an {@link
 * UnsupportedOperationException}. However, it can be configured to tacitly absorb
 * new types upon being requested, provided the new type is a subtype of a type that
 * is already in the map. The new type will then be associated with the same value as
 * the supertype. Thus, when the type is requested again, it will result in a direct
 * hit. In the end, a {@code TypeHashMap} will be nothing but a pass-through shell
 * around the {@code HashMap} that backs it - and perform as such.
 *
 * <p>(Implementation node: with auto-expansion <i>disabled</i>, the backing map is
 * not
 * actually a {@code HashMap}.)
 *
 * @param <V> The type of the values in the {@code Map}
 * @author Ayco Holleman
 * @see TypeMap
 * @see TypeGraph
 * @see LinkedTypeGraph
 */
public final class TypeHashMap<V> extends MultiPassTypeMap<V> {

  /**
   * Converts the specified {@code Map} to a {@code TypeHashMap}. Autoboxing will be
   * enabled. Auto-expansion will be disabled.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @return A {@code TypeHashMap}
   */
  public static <U> TypeHashMap<U> copyOf(Map<Class<?>, U> src) {
    return copyOf(true, false, src);
  }

  /**
   * Converts the specified {@code Map} to a {@code TypeHashMap}.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param autobox Whether to enable "autoboxing" (see {@link AbstractTypeMap})
   * @param autoExpand Whether to enable auto-expansion (see class comments)
   * @param src The {@code Map} to convert
   * @return A {@code TypeHashMap}
   */
  public static <U> TypeHashMap<U> copyOf(boolean autobox,
      boolean autoExpand,
      Map<Class<?>, U> src) {
    Check.that(src, "src").is(deepNotNull());
    return autoExpand
        ? new TypeHashMap<>(src, 2, autobox)
        : new TypeHashMap<>(src, 0, autobox);
  }

  /**
   * Returns a builder for {@code TypeHashMap} instances.
   *
   * @param <U> The type of the values in the map
   * @param valueType The class of the values in the map
   * @return A builder for {@code TypeHashMap} instances
   */
  public static <U> TypeHashMapBuilder<U> build(Class<U> valueType) {
    return Check.notNull(valueType).ok(TypeHashMapBuilder::new);
  }

  private final Map<Class<?>, V> backend;

  TypeHashMap(Map<Class<?>, ? extends V> src, int size, boolean autobox) {
    super(size != 0, autobox);
    if (size == 0) {
      backend = Map.copyOf(src);
    } else {
      Map<Class<?>, V> tmp = new HashMap<>(1 + size * 4 / 3);
      tmp.putAll(src);
      this.backend = tmp;
    }
  }

  @Override
  Map<Class<?>, V> backend() {
    return backend;
  }

  @Override
  public Set<Class<?>> keySet() {
    return autoExpand ? Set.copyOf(backend.keySet()) : backend.keySet();
  }

  @Override
  public Collection<V> values() {
    return Set.copyOf(backend.values());
  }

  @Override
  public Set<Entry<Class<?>, V>> entrySet() {
    return autoExpand ? immutableEntrySet() : backend.entrySet();
  }

  private Set<Entry<Class<?>, V>> immutableEntrySet() {
    return backend.entrySet()
        .stream()
        .map(makeImmutable())
        .collect(toUnmodifiableSet());
  }

  private UnaryOperator<Entry<Class<?>, V>> makeImmutable() {
    return e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), e.getValue());
  }

}
