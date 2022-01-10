package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import static nl.naturalis.common.ClassMethods.*;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * A specialized {@link Map} implementation used to map types to values. Its main feature is that,
 * if the requested type is not present, but one of its super types is, it will return the value
 * associated with the super type. A {@code TypeMap} does not allow {@code null} keys or values. If
 * you add {@code Object.class} to the map, it is guaranteed to always return a non-null value.
 *
 * <h4>Autoboxing</h4>
 *
 * <p>The map is configured by default to "autobox" (and unbox) types: if the requested type is a
 * primitive type, and there is no entry for it in the map, but there is one for the corresponding
 * wrapper type, then the map will return the value associated with the wrapper type (and vice
 * versa). You can {@link #autobox disable} the autoboxing feature.
 *
 * <h4>Auto-expansion</h4>
 *
 * <p>A {@code TypeMap} unmodifiable. All map-altering methods will throw an {@link
 * UnsupportedOperationException}. However, the map can be configured to auto-expand internally: if
 * the requested type is not present, but one of its super types is, then the requested type will
 * tacitly be added to the map, acquiring the value associated with the super type. Thus, when the
 * subtype is requested again, it will result in a direct hit. Auto-expansion is enabled by default
 * for the {@code TypeMap} class, but it is disabled by default for its sibling class, the {@link
 * TypeTreeMap}.
 *
 * <h4>Type-lookup Logic</h4>
 *
 * <p>When looking for a super type of the requested type, the map will first climb the type's class
 * hierarchy up to, but not including {@code Object.class}; then it will climb up the type's
 * interfaces (if any); and finally it will check to see if it contains an entry for {@code
 * Object.class}.
 *
 * @param <V> The type of the values in the {@code Map}
 * @author Ayco Holleman
 */
public class TypeMap<V> extends AbstractTypeMap<V> {

  /**
   * A builder class for {@code TypeMap} instances.
   *
   * @param <U> The type of the values in the {@code TypeMap} to be built
   * @author Ayco Holleman
   */
  public static final class Builder<U> {
    private final Class<U> valueType;
    private final HashMap<Class<?>, U> tmp = new HashMap<>();
    private Integer expectedSize = 2;
    private boolean autobox = true;

    private Builder(Class<U> valueType) {
      this.valueType = valueType;
    }

    /**
     * Whether to enable the auto-expand feature. See description above. Specifying {@code true} is
     * equivalent to calling {@link #autoExpand(int) autoExpand(2)}. By default auto-expansion is
     * enabled.
     *
     * @return This {@code Builder} instance
     */
    public Builder<U> autoExpand(boolean autoExpand) {
      expectedSize = autoExpand ? 2 : null;
      return this;
    }

    /**
     * Enables the auto-expansion feature. See description above. If the {@code expectedSize}
     * argument is less than or equal to the total number of entries you {@link #add(Class, Object)
     * added}, it will be interpreted as a multiplier. So, for example, 3 would mean that you expect
     * the map to grow to about three times its original size.
     *
     * @param expectedSize An estimate of the final size of the auto-expanding map
     * @return This {@code Builder} instance
     */
    public Builder<U> autoExpand(int expectedSize) {
      this.expectedSize = Check.that(expectedSize).is(gt(), 1).intValue();
      return this;
    }

    /**
     * Whether to enable the "autoboxing" feature. See description above. By default, autoboxing is
     * enabled.
     *
     * @return This {@code Builder} instance
     */
    public Builder<U> autobox(boolean autobox) {
      this.autobox = autobox;
      return this;
    }

    /**
     * Associates the specified type with the specified value.
     *
     * @param type The type
     * @param value The value
     * @return This {@code Builder} instance
     */
    public Builder<U> add(Class<?> type, U value) {
      Check.notNull(type, "type");
      Check.notNull(value, "value").is(instanceOf(), valueType);
      tmp.put(type, value);
      return this;
    }

    /**
     * Returns an unmodifiable {@code TypeMap} with the configured types and behaviour. If
     * autoboxing was enabled, but auto-expansion was disabled, the map will first tacitly be
     * enlarged with the wrapper types corresponding to the primitive types in the map (without
     * overwriting already-present wrapper types), and with the primitive types corresponding to the
     * wrapper types in the map (without overwriting already-present primitive types).
     *
     * @param <W> The type of the values in the returned {@code TypeMap}
     * @return S new {@code TypeMap} instance with the configured types and behaviour
     */
    @SuppressWarnings("unchecked")
    public <W> TypeMap<W> freeze() {
      if (expectedSize == null) { // No auto-expand
        if (autobox) {
          tmp.forEach(
              (k, v) -> {
                if (k.isPrimitive() && !tmp.containsKey(box(k))) {
                  tmp.put(box(k), v);
                } else if (isWrapper(k) && !tmp.containsKey(unbox(k))) {
                  tmp.put(unbox(k), v);
                }
              });
        }
        return (TypeMap<W>) new TypeMap<>(tmp, autobox);
      }
      int sz = expectedSize > tmp.size() ? expectedSize : tmp.size() * expectedSize;
      return (TypeMap<W>) new TypeMap<>(tmp, sz, autobox);
    }
  }

  /**
   * Converts the specified {@code Map} to a non-auto-expanding, autoboxing {@code TypeMap} .
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @return A non-auto-expanding, non-autoboxing {@code TypeMap}
   */
  public static <U> TypeMap<U> copyOf(Map<Class<?>, U> src) {
    Check.notNull(src);
    if (src.getClass() == TypeMap.class) {
      TypeMap<U> tm = (TypeMap<U>) src;
      if (!tm.autobox && !tm.autoExpand) {
        return tm;
      }
    }
    return copyOf(src, false);
  }

  /**
   * Converts the specified {@code Map} to a non-auto-expanding {@code TypeMap} .
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @param autobox Whether to enable the "autoboxing" feature (see class comments)
   * @return A non-auto-expanding {@code TypeMap}
   */
  public static <U> TypeMap<U> copyOf(Map<Class<?>, U> src, boolean autobox) {
    Check.notNull(src);
    if (src.getClass() == TypeMap.class) {
      TypeMap<U> tm = (TypeMap<U>) src;
      if (tm.autobox == autobox && !tm.autoExpand) {
        return tm;
      }
    }
    return new TypeMap<>(src, autobox);
  }

  /**
   * Converts the specified {@code Map} to an auto-expanding, autoboxing {@code TypeMap}. If the
   * {@code expectedSize} argument is less than or equal to the size of the source map, it will be
   * interpreted as a multiplier. So, for example, 3 would mean that you expect the map to grow to
   * about three times its original size.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @param expectedSize The expected size of the map
   * @return An auto-expanding, autoboxing {@code TypeMap}
   */
  public static <U> TypeMap<U> copyOf(Map<Class<?>, U> src, int expectedSize) {
    return copyOf(src, expectedSize, true);
  }

  /**
   * Converts the specified {@code Map} to an auto-expanding {@code TypeMap}. If the {@code
   * expectedSize} argument is less than or equal to the size of the source map, it will be
   * interpreted as a multiplier. So, for example, 3 would mean that you expect the map to grow to
   * about three times its original size.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @param expectedSize An estimate of the final size of the auto-expanding map
   * @param autobox Whether to enable the "autoboxing" feature (see class comments)
   * @return An auto-expanding {@code TypeMap}
   */
  public static <U> TypeMap<U> copyOf(Map<Class<?>, U> src, int expectedSize, boolean autobox) {
    Check.notNull(src, "src");
    Check.that(expectedSize, "expectedSize").is(gt(), 1);
    int sz = expectedSize > src.size() ? expectedSize : expectedSize * src.size();
    return new TypeMap<>(src, sz, autobox);
  }

  /**
   * Returns a {@code Builder} instance that lets you configure a {@code TypeMap}
   *
   * @param <U> The type of the values in the {@code TypeMap}
   * @param valueType The class object corresponding to the type of the values in the {@code
   *     TypeMap} to be built
   * @return A {@code Builder} instance that lets you configure a {@code TypeMap}
   */
  public static <U> Builder<U> build(Class<U> valueType) {
    Check.notNull(valueType);
    return new Builder<>(valueType);
  }

  private final Map<Class<?>, V> backend;

  TypeMap(Map<? extends Class<?>, ? extends V> m, boolean autobox) {
    super(false, autobox);
    this.backend = Map.copyOf(m); // implicit check on null keys & values
  }

  TypeMap(Map<? extends Class<?>, ? extends V> m, int sz, boolean autobox) {
    super(true, autobox);
    Map<Class<?>, V> tmp = new IdentityHashMap<>(sz);
    m.forEach(
        (k, v) -> {
          Check.that(k).is(notNull(), ERR_NULL_KEY);
          Check.that(v).is(notNull(), ERR_NULL_VAL, k.getName());
          tmp.put(k, v);
        });
    this.backend = tmp;
  }

  @Override
  Map<Class<?>, V> backend() {
    return backend;
  }
}
