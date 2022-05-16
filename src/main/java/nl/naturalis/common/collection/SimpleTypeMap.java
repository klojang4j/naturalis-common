package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.*;
import java.util.function.UnaryOperator;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static nl.naturalis.common.check.CommonChecks.deepNotNull;

/**
 * A {@link TypeMap} that is internally backed by a regular {@link Map}. See {@link TypeMap} for an
 * explanation of what type maps are about. The type lookup mechanism may involve several queries
 * against the backing map. If the type requested via {@link #containsKey(Object) containsKey} or
 * {@link #get(Object)} is not itself present in the backing map, the {@code SimpleTypeMap} will
 * first climb the type's class hierarchy up to, but not including {@code Object.class}. If none of
 * the intermediate types were found, the {@code SimpleTypeMap} will climb the type's interface
 * hierarchy, and finally check whether {@code Object.class} is present in the map.
 *
 * <h4>Auto-expansion</h4>
 *
 * <p>A {@code SimpleTypeMap} is immutable. All map-altering methods will throw an
 * {@link UnsupportedOperationException}. However, a {@code SimpleTypeMap} can be configured to
 * automatically absorb subtypes of types that already are in the map. With auto-expansion enabled,
 * if a type is not present in the map, but one of its supertypes is, the {@link #get(Object) get}
 * and {@link #containsKey(Object) containsKey} methods will tacitly add the subtype to the map,
 * associating it with the same value as the supertype. Thus, when the subtype is requested again,
 * it will result in a direct hit. The more types are absorbed by a {@code SimpleTypeMap} the more
 * it wil become nothing but a pass-through for
 *
 * @param <V> The type of the values in the {@code Map}
 * @author Ayco Holleman
 */
public final class SimpleTypeMap<V> extends MultiPassTypeMap<V> {

  /**
   * Converts the specified {@code Map} to a {@code SimpleTypeMap}. Autoboxing will be enabled.
   * Auto-expansion will be disabled.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @return A {@code SimpleTypeMap}
   */
  public static <U> SimpleTypeMap<U> copyOf(Map<Class<?>, U> src) {
    return copyOf(src, false, true);
  }

  /**
   * Converts the specified {@code Map} to a {@code SimpleTypeMap}.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @param autoExpand Whether to enable auto-expansion (see class comments)
   * @param autobox Whether to enable "autoboxing" (see {@link TypeMap})
   * @return A {@code SimpleTypeMap}
   */
  public static <U> SimpleTypeMap<U> copyOf(Map<Class<?>, U> src,
      boolean autoExpand,
      boolean autobox) {
    Check.that(src, "src").is(deepNotNull());
    return autoExpand ? new SimpleTypeMap<>(src, 2, autobox) : new SimpleTypeMap<>(src, 0, autobox);
  }

  /**
   * Returns a builder for {@code SimpleTypeMap} instances.
   *
   * @param <U> The type of the values in the map
   * @param valueType The class of the values in the map
   * @return A builder for {@code SimpleTypeMap} instances
   */
  public static <U> SimpleTypeMapBuilder<U> build(Class<U> valueType) {
    return Check.notNull(valueType).ok(SimpleTypeMapBuilder::new);
  }

  private final Map<Class<?>, V> backend;

  SimpleTypeMap(Map<Class<?>, ? extends V> src, int size, boolean autobox) {
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
    return autoExpand ? Set.copyOf(backend.values()) : backend.values();
  }

  @Override
  public Set<Entry<Class<?>, V>> entrySet() {
    return autoExpand ? immutableEntrySet() : backend.entrySet();
  }

  private Set<Entry<Class<?>, V>> immutableEntrySet() {
    return backend.entrySet().stream().map(makeImmutable()).collect(toUnmodifiableSet());
  }

  private UnaryOperator<Entry<Class<?>, V>> makeImmutable() {
    return e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), e.getValue());
  }

}
