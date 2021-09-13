package nl.naturalis.common.collection;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.ClassMethods.isWrapper;
import static nl.naturalis.common.ClassMethods.unbox;
import static nl.naturalis.common.check.CommonChecks.instanceOf;
import static nl.naturalis.common.check.CommonChecks.notNull;

/**
 * A {@link Map} implementation with {@link Class} objects as keys, using the Java type hierarchy
 * mechanism to find values for missing keys. It returns a non-null value for a type if either the
 * type itself or any of its super types is present in the map. For example, suppose the map
 * contains two entries: one for {@code Integer.class} and one for {@code Number.class}. If the map
 * is queried for key {@code Integer.class}, then the value associated with {@code Integer.class} is
 * returned. But if the map is queried for key {@code Short.class}, then the value associated with
 * {@code Number.class} is returned. If the value for key {@code InputStream.class} is requested, a
 * {@link TypeNotSupportedException} is thrown.
 *
 * <p>A {@code TypeMap} can optionally also be configured to "auto-box" and "auto-unbox" keys. For
 * the map described above that would mean that the map would to also return values for types {@code
 * int.class} and {@code short.class} (and any primitive number type for that matter).
 *
 * <p>This {@code Map} implementation is useful if you want to define fall-back values or shared
 * values for types that have not been explicitly added to the map. If the map contains an entry for
 * {@code Object.class}, that will be the ultimate fall-back entry: {@code containsKey} will always
 * return {@code true} and {@code get} will always return a non-null value. If a type is not present
 * in the map, {@code containsKey} and {@code get} will first go up its class hierarchy, then they
 * will check the interfaces implemented or extended by the type (preferring the most specific
 * interface), and finally they will check if {@code Object.class} is present in the map.
 *
 * <p>A {@code TypeMap} does not accept {@code null} keys and {@code null} values and it is
 * unmodifiable to the outside world. All map-altering methods will throw an {@link
 * UnsupportedOperationException}. Thus the map will only ever contain the values contained in the
 * source map. Also, it will never contain any types (keys) that are not equal to, or extending from
 * the types already present in the source map. However a {@code TypeMap} may or may not grow
 * internally, depending on which of its static factory methods is used. The {@code withValues}
 * methods produce instances that will silently gobble up missing subtypes upon being requested via
 * {@code containsKey} or {@code get}. The requested type will be associated with the super type's
 * value. Thus the next time that type is requested it will result in a direct hit. The {@code
 * withTypes} methods produce instances that remain completely static internally.
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
    private boolean autobox;

    private Builder(Class<U> valueType) {
      this.valueType = valueType;
    }

    /**
     * Disables the automatic addition of new subtypes. Note that by default auto-expansion is
     * enabled.
     *
     * @return This {@code Builder} instance
     */
    public Builder<U> noAutoExpand() {
      expectedSize = null;
      return this;
    }

    /**
     * Enables the automatic addition of missing subtypes. Equivalent to {@code autoExpand(0)}. The
     * map will be expected to grow to about twice the number of entries added through the {@link
     * #add(Class, Object) add} method. There is in fact no real reason to call this method, because
     * this is how the {@code Builder} configures {@code TypeMap} instances by default.
     *
     * @return This {@code Builder} instance
     */
    public Builder<U> autoExpand() {
      return autoExpand(0);
    }

    /**
     * Enables the automatic addition of missing subtypes. You can specify 0 (zero) or any number
     * less than the number of entries added through the {@link #add(Class, Object) put} method to
     * indicate that you expect the map to grow to about twice its original size.
     *
     * @param expectedSize The size to which you expect the map to grow as it gobbles up new
     *     subtypes presented to it {@code containsKey} and {@code get} methods.
     * @return This {@code Builder} instance
     */
    public Builder<U> autoExpand(int expectedSize) {
      this.expectedSize = expectedSize;
      return this;
    }

    /**
     * Enables the "auto-boxing" and "auto-unboxing" feature. See class comments above.
     *
     * @return This {@code Builder} instance
     */
    public Builder<U> autobox() {
      this.autobox = true;
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
     * Returns a new {@code TypeMap} instance with the configured types and behaviour.
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
      return (TypeMap<W>) new TypeMap<>(tmp, expectedSize, autobox);
    }
  }

  /**
   * Returns a {@code TypeMap} instance that will never contain any other keys or values than the
   * ones in the specified map. See class description above.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to turn into a {@code TypeMap}
   * @return A {@code TypeMap} instance that will never grow beyond the size of the specified map
   */
  public static <U> TypeMap<U> withTypes(Map<Class<?>, U> src) {
    return withTypes(src, false);
  }

  /**
   * Returns a {@code TypeMap} instance that will never contain any other keys or values than the
   * ones in the specified map. See class description above.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to turn into a {@code TypeMap}
   * @param autobox Specifying {@code true} causes the following behaviour: if the value for a
   *     primitive type (e.g. {@code int.class}) is requested and there is no entry for the
   *     primitive type, then, if present, the value for the corresponding wrapper type ({@code
   *     Integer.class}) will be returned - <i>and vice versa</i>.
   * @return A {@code TypeMap} instance that will never grow beyond the size of the specified map
   */
  public static <U> TypeMap<U> withTypes(Map<Class<?>, U> src, boolean autobox) {
    Check.notNull(src);
    return new TypeMap<>(src, autobox);
  }

  /**
   * Returns a {@code TypeMap} instance that will never contain any other values than the ones
   * already present in the specified map, and that will never contain any types (keys) that are not
   * equal to, or extending from the types already present in the specified map, but that
   * <i>will</i> grow as new subtypes are being associated with pre-existing values. Equivalent to
   * {@code withValues(src, 0)}.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to turn into a {@code TypeMap}
   * @return A {@code TypeMap} instance that will grow as new types are being requested from it
   */
  public static <U> TypeMap<U> withValues(Map<Class<?>, U> src) {
    return withValues(src, 0);
  }

  /**
   * Returns a {@code TypeMap} instance that will never contain any other values than the ones in
   * the specified map (as per {@link Map#values()}), and that will never contain any types (keys)
   * that are not equal to, or extending from the types already present in the specified map, but
   * that <i>will</i> grow as new subtypes are being associated with pre-existing values.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to turn into a {@code TypeMap}
   * @param expectedSize The expected number of types (pre-existing and new) that the map will be
   *     catering for. Specifying any value less than {@code src.size()} is equivalent to specifying
   *     {@code src.size() * 2}. (In other words, you expect the type map to silently gobble up
   *     about as many subtypes as there are types in the specified map.)
   * @return A {@code TypeMap} instance that will grow as new types are being requested from it
   */
  public static <U> TypeMap<U> withValues(Map<Class<?>, U> src, int expectedSize) {
    return withValues(src, expectedSize, false);
  }

  /**
   * Returns a {@code TypeMap} instance that will never contain any other values than the ones in
   * the specified map (as per {@link Map#values()}), and that will never contain any types (keys)
   * that are not equal to, or extending from the types already present in the specified map, but
   * that <i>will</i> grow as new subtypes are being associated with pre-existing values.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to turn into a {@code TypeMap}
   * @param expectedSize The expected number of types (pre-existing and new) that the map will be
   *     catering for. Specifying a value less than {@code src.size()} is equivalent to specifying
   *     {@code src.size() * 2}. (In other words, you expect the type map to silently gobble up
   *     about as many subtypes as there are types in the specified map.)
   * @param autobox Specifying {@code true} causes the following behaviour: if the value for a
   *     primitive type (e.g. {@code int.class}) is requested and there is no entry for the
   *     primitive type, then, if present, the value for the corresponding wrapper type ({@code
   *     Integer.class}) will be returned - <i>and vice versa</i>.
   * @return A {@code TypeMap} instance that will grow as new types are being requested from it
   */
  public static <U> TypeMap<U> withValues(Map<Class<?>, U> src, int expectedSize, boolean autobox) {
    Check.notNull(src, "src");
    int sz = expectedSize < src.size() ? src.size() * 2 : expectedSize;
    return new TypeMap<>(src, sz, autobox);
  }

  /**
   * Returns a {@code Builder} instance that lets you configure a {@code TypeMap}
   *
   * @param <U> The type of the values in the {@code TypeMap} to be built
   * @param valueType The class object corresponding to the type of the values in the {@code
   *     TypeMap} to be built
   * @return A {@code Builder} instance that lets you configure a {@code TypeMap}
   */
  public static <U> Builder<U> build(Class<U> valueType) {
    Check.notNull(valueType);
    return new Builder<>(valueType);
  }

  TypeMap(Map<? extends Class<?>, ? extends V> m, boolean autobox) {
    super(m, autobox);
  }

  TypeMap(Map<? extends Class<?>, ? extends V> m, int sz, boolean autobox) {
    super(m, sz, autobox);
  }

  @Override
  Map<Class<?>, V> createBackend(Map<? extends Class<?>, ? extends V> m, boolean autobox) {
    return Map.copyOf(m); // implicit check on null keys & values
  }

  @Override
  Map<Class<?>, V> createBackend(Map<? extends Class<?>, ? extends V> m, int sz, boolean autobox) {
    Map<Class<?>, V> backend = new IdentityHashMap<>(sz);
    m.forEach(
        (k, v) -> {
          Check.that(k).is(notNull(), ERR_NULL_KEY);
          Check.that(v).is(notNull(), ERR_NULL_VAL, k.getName());
          backend.put(k, v);
        });
    return backend;
  }
}
