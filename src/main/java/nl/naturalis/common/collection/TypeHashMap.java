package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static nl.naturalis.common.check.CommonChecks.*;

/**
 * A subclass of {@link MultiPassTypeMap} that is internally backed by a {@link HashMap}. See {@link
 * TypeMap} for an explanation of what type maps are about.
 *
 * @param <V> The type of the values in the {@code Map}
 * @author Ayco Holleman
 */
public final class TypeHashMap<V> extends MultiPassTypeMap<V> {

  /**
   * A builder class for {@code TypeMap} instances.
   *
   * @param <U> The type of the values in the {@code TypeMap} to be built
   * @author Ayco Holleman
   */
  public static final class Builder<U> {

    private final Class<U> valueType;
    private final HashMap<Class<?>, U> tmp = new HashMap<>();
    private Integer expectedSize = null;
    private boolean autobox = true;

    private Builder(Class<U> valueType) {
      this.valueType = valueType;
    }

    /**
     * Whether to enable the auto-expand feature. See description above. Specifying {@code true} is
     * equivalent to calling {@link #autoExpand(int) autoExpand(2)}.
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
      this.expectedSize = Check.that(expectedSize).is(gt(), 1).ok();
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
     * Associates the specified value with the specified types.
     *
     * @param value The value
     * @param types The types to associate the value with
     * @return This {@code Builder} instance
     */
    public Builder<U> addMultiple(U value, Class<?>... types) {
      Check.notNull(value, "value").is(instanceOf(), valueType);
      Check.that(types, "types").is(deepNotNull());
      Arrays.stream(types).forEach(t -> tmp.put(t, value));
      return this;
    }

    /**
     * Returns an unmodifiable {@code TypeMap} with the configured types and behaviour.
     *
     * @param <W> The type of the values in the returned {@code TypeMap}
     * @return S new {@code TypeMap} instance with the configured types and behaviour
     */
    @SuppressWarnings("unchecked")
    public <W> TypeHashMap<W> freeze() {
      if (expectedSize == null) { // No auto-expand
        return (TypeHashMap<W>) new TypeHashMap<>(tmp, autobox);
      }
      int sz = expectedSize > tmp.size() ? expectedSize : tmp.size() * expectedSize;
      return (TypeHashMap<W>) new TypeHashMap<>(tmp, sz, autobox);
    }

  }

  /**
   * Converts the specified {@code Map} to a non-auto-expanding, autoboxing {@code TypeMap} .
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @return A non-auto-expanding, non-autoboxing {@code TypeMap}
   */
  public static <U> TypeHashMap<U> copyOf(Map<Class<?>, U> src) {
    return copyOf(src, true);
  }

  /**
   * Converts the specified {@code Map} to a non-auto-expanding {@code TypeMap} .
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @param autobox Whether to enable the "autoboxing" feature (see class comments)
   * @return A non-auto-expanding {@code TypeMap}
   */
  public static <U> TypeHashMap<U> copyOf(Map<Class<?>, U> src, boolean autobox) {
    Check.that(src, "src").is(deepNotNull());
    return new TypeHashMap<>(src, autobox);
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
  public static <U> TypeHashMap<U> copyOf(Map<Class<?>, U> src, int expectedSize) {
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
  public static <U> TypeHashMap<U> copyOf(Map<Class<?>, U> src, int expectedSize, boolean autobox) {
    Check.that(src, "src").is(deepNotNull());
    Check.that(expectedSize, "expectedSize").is(gt(), 1);
    int sz = expectedSize > src.size() ? expectedSize : expectedSize * src.size();
    return new TypeHashMap<>(src, sz, autobox);
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

  TypeHashMap(Map<Class<?>, ? extends V> m, boolean autobox) {
    super(false, autobox);
    this.backend = Map.copyOf(m);
  }

  TypeHashMap(Map<Class<?>, ? extends V> m, int sz, boolean autobox) {
    super(true, autobox);
    Map<Class<?>, V> tmp = new HashMap<>(1 + sz * 4 / 3);
    tmp.putAll(m);
    this.backend = tmp;
  }

  @Override
  Map<Class<?>, V> backend() {
    return backend;
  }

}
