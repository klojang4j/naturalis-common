package nl.naturalis.common.collection;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.instanceOf;
import static nl.naturalis.common.check.CommonChecks.notNull;

/**
 * A {@link Map} implementation that returns a non-null value for a type if either the type itself
 * or any of its super types is present in the map. For example, suppose the map contains two
 * entries: one for {@code Integer.class} and one for {@code Number.class}. If the map is queried
 * for key {@code Integer.class}, then the value associated with {@code Integer.class} is returned.
 * But if the map is queried for key {@code Short.class}, then the value associated with {@code
 * Number.class} is returned. If the map is queried for, say, {@code InputStream.class}, a {@link
 * TypeNotSupportedException} is thrown. (In other words it will never return {@code null}.)
 *
 * <p>This {@code Map} implementation is useful if you want to define fall-back values or shared
 * values for types that have not been explicitly added to the map. If the map contains an entry for
 * {@code Object.class}, {@code containsKey} will always return {@code true} and {@code get} will
 * always return a non-null value (it will never throw a {@code TypeNotSupportedException}).
 *
 * <p>This {@code Map} implementation does not support {@code null} keys or {@code null} values and
 * is unmodifiable to the outside world. All map-altering methods will throw an {@link
 * UnsupportedOperationException}. Thus the map will only ever contain the <i>values</i> contained
 * in the map passed to the static factory methods, and it will never contain any types (keys) that
 * are not equal to, or extending from the types already present in the original map. However a
 * {@code TypeMap} may or may not grow internally, depending on which of the static factory methods
 * is used. The {@link TypeMap#withValues(Map, int) static factory methods} that take an extra
 * integer argument silently gobble up missing subtypes upon being requested (via {@code
 * containsKey} or via {@code get}). The requested type will then be associated with the super
 * type's value. Thus the next time that type is requested it will result in a direct hit. The
 * {@link TypeMap#withTypes(Map) static factory methods} that do not take an extra integer argument
 * remain completely immutable.
 *
 * @author Ayco Holleman
 * @param <V> The type of the values in the {@code Map}
 */
public class TypeMap<V> extends AbstractTypeMap<V> {

  /**
   * A builder class for {@code TypeMap} instances
   *
   * @author Ayco Holleman
   * @param <U> The type of the values in the {@code TypeMap} to be built
   */
  public static final class Builder<U> {
    private final Class<U> valueType;
    private final HashMap<Class<?>, U> tmp = new HashMap<>();
    private Integer expectedSize;
    private boolean autobox;

    private Builder(Class<U> valueType) {
      this.valueType = valueType;
    }

    /**
     * Configures the resulting {@code TypeMap} to automatically add missing subtypes, associating
     * them with the values of the nearest super type in the map. The map will be expected to grow
     * to about twice the number of entries added through the {@link #put(Class, Object) put}
     * method.
     *
     * @param expectedSize The expected size to which the map will grow
     * @return This {@code Builder} instance
     */
    public Builder<U> autoExpand() {
      return autoExpand(0);
    }

    /**
     * Configures the resulting {@code TypeMap} to automatically add missing subtypes, associating
     * them with the values of the nearest super type in the map. You can specify 0 (zero) or any
     * number less than the number of entries added through the {@link #add(Class, Object) put}
     * method to indicate that expect the map to grow to about twice its original size.
     *
     * @param expectedSize The expected size to which the map will grow
     * @return This {@code Builder} instance
     */
    public Builder<U> autoExpand(int expectedSize) {
      this.expectedSize = expectedSize;
      return this;
    }

    /**
     * Configures the resulting {@code TypeMap} to search for the boxed version of a primitive type
     * if the primitive type itself is not present, <i>and</i> it will search for the unboxed
     * version of a primitive wrapper class if the primitive wrapper class is itself not present.
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
      if (expectedSize == null) {
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
