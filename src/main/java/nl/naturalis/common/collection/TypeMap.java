package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import static nl.naturalis.common.ClassMethods.*;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * A specialized {@link Map} implementation used to associate Java types with values or actions
 * (i&#46;e&#46; lambdas). Its main feature is that, if the requested type is not present, but one
 * of its super types is, it will return the value associated with the super type. The map will
 * first climb the type's class hierarchy up to, but excluding {@code Object.class}; then it will
 * climb up the type's interfaces (if any); and finally it will check to see if it contains an entry
 * for {@code Object.class}.
 *
 * <p>The map can optionally also be configured to "autobox" (and unbox) types: if the requested
 * type is a primitive type, and it is not present, but the corresponding wrapper type is, then it
 * will return the value associated with the wrapper type (and vice versa). By default, autoboxing
 * is enabled.
 *
 * <p>A {@code TypeMap} is unmodifiable. All map-altering methods will throw an {@link
 * UnsupportedOperationException}. However, the map can be configured to auto-expand internally: if
 * the requested type is not present, but one of its super types is, then the requested type will
 * tacitly be added to the map with the same value as the super type's value. Thus, when the type is
 * requested again, it will result in a direct hit. Auto-expansion is enabled by default
 * for the {@code TypeMap} class while it is disabled by default for its sibling class, {@link
 * TypeTreeMap}.
 *
 * <p>Null keys and {@code null} values are not allowed.
 *
 * @author Ayco Holleman
 * @param <V> The type of the values in the {@code Map}
 */
public class TypeMap<V> extends AbstractTypeMap<V> {

  /**
   * A builder class for {@code TypeMap} instances.
   *
   * @author Ayco Holleman
   * @param <U> The type of the values in the {@code TypeMap} to be built
   */
  public static final class Builder<U> {
    private final Class<U> valueType;
    private final HashMap<Class<?>, U> tmp = new HashMap<>();
    private Integer expectedSize = 0;
    private boolean autobox = true;

    private Builder(Class<U> valueType) {
      this.valueType = valueType;
    }

    /**
     * Disables the auto-expand feature. See description above. By default auto-expansion is
     * enabled.
     *
     * @return This {@code Builder} instance
     */
    public Builder<U> noAutoExpand() {
      expectedSize = null;
      return this;
    }

    /**
     * Enables the auto-expand feature. See description above. If the {@code expectedSize} argument
     * is less than or equal to the total number of entries you {@link #add(Class, Object) added},
     * it will be interpreted as a multiplier. So, for example, 3 would mean that you expect the map
     * to grow to about three times its original size.
     *
     * @param expectedSize The expected size of the auto-expanding map
     * @return This {@code Builder} instance
     */
    public Builder<U> autoExpand(int expectedSize) {
      this.expectedSize = Check.that(expectedSize).is(gt(), 1).intValue();
      return this;
    }

    /**
     * Disables the "autoboxing" feature. See description above. By default, autoboxing is enabled.
     *
     * @return This {@code Builder} instance
     */
    public Builder<U> noAutoboxing() {
      this.autobox = false;
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
   * @param src The {@code Map} to turn into a {@code TypeMap}
   * @return A non-auto-expanding, non-autoboxing {@code TypeMap}
   */
  public static <U> TypeMap<U> copyOf(Map<Class<?>, U> src) {
    Check.notNull(src);
    if(src instanceof TypeMap<U> tm && tm.autobox && !tm.autoExpand) {
      return tm;
    }
    return copyOf(src, false);
  }

  /**
   * Converts the specified {@code Map} to a non-auto-expanding {@code TypeMap} .
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to turn into a {@code TypeMap}
   * @param autobox Whether to enable the "autoboxing" feature (see class comments)
   * @return A non-auto-expanding {@code TypeMap}
   */
  public static <U> TypeMap<U> copyOf(Map<Class<?>, U> src, boolean autobox) {
    Check.notNull(src);
    if(src instanceof TypeMap<U> tm && tm.autobox == autobox && !tm.autoExpand) {
      return tm;
    }
    return new TypeMap<>(src, autobox);
  }

  /**
   * Converts the specified {@code Map} to an auto-expanding, autoboxing {@code TypeMap}. If the {@code
   * expectedSize} argument is less than or equal to the size of the source map, it will be
   * interpreted as a multiplier. So, for example, 3 would mean that you expect the map to grow to
   * about three times its original size.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to turn into a {@code TypeMap}
   * @param expectedSize The expected size of the map
   * @return An auto-expanding, autoboxing {@code TypeMap}
   */
  public static <U> TypeMap<U> copyOf(Map<Class<?>, U> src, int expectedSize) {
    return copyOf(src, expectedSize, true);
  }

  /**
   * Converts the specified {@code Map} to an auto-expanding {@code TypeMap}. If the {@code expectedSize}
   * argument is less than or equal to the size of the source map, it will be
   * interpreted as a multiplier. So, for example, 3 would mean that you expect the map to grow to
   * about three times its original size.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to turn into a {@code TypeMap}
   * @param expectedSize The expected size of the map
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
