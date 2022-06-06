package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.Arrays;
import java.util.HashMap;

import static nl.naturalis.common.check.CommonChecks.*;

/**
 * A builder class for {@code TypeHashMap} instances.
 *
 * @param <V> The type of the values in the {@code TypeHashMap}
 * @author Ayco Holleman
 */
public final class TypeHashMapBuilder<V> {

  private final Class<V> valueType;
  private final HashMap<Class<?>, V> tmp = new HashMap<>();

  private int expectedSize = 0;
  private boolean autobox = true;

  TypeHashMapBuilder(Class<V> valueType) {
    this.valueType = valueType;
  }

  /**
   * Enables or disables the auto-expansion feature. See {@link TypeHashMap}.
   * Specifying {@code true} is equivalent to calling {@link #autoExpand(int)
   * autoExpand(2)}.
   *
   * @return This {@code Builder} instance
   */
  public TypeHashMapBuilder<V> autoExpand(boolean autoExpand) {
    expectedSize = autoExpand ? 2 : 0;
    return this;
  }

  /**
   * Enables or disables the auto-expansion feature. See {@link TypeHashMap}. If
   * {@code expectedSize} equals zero, auto-expansion will be disabled. If it is less
   * than or equal to the total number of entries you {@link #add(Class, Object) add}
   * through this builder, it will be interpreted as a multiplier. So, for example, 3
   * would mean that you expect the map to grow to about three times its original
   * size. Otherwise it provides an absolute estimate of how large you expect the map
   * to become as a consequence of auto-expansion.
   *
   * @param expectedSize An estimate of the final size of the auto-expanding map
   * @return This {@code Builder} instance
   */
  public TypeHashMapBuilder<V> autoExpand(int expectedSize) {
    this.expectedSize = Check.that(expectedSize)
        .is(gte(), 0)
        .ok(x -> x == 0 ? null : x);
    return this;
  }

  /**
   * Whether to enable the "autoboxing" feature. See {@link TypeMap} for an
   * explanation of this feature. By default, autoboxing is enabled.
   *
   * @return This {@code Builder} instance
   */
  public TypeHashMapBuilder<V> autobox(boolean autobox) {
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
  public TypeHashMapBuilder<V> add(Class<?> type, V value) {
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
  public TypeHashMapBuilder<V> addMultiple(V value, Class<?>... types) {
    Check.notNull(value, "value").is(instanceOf(), valueType);
    Check.that(types, "types").is(deepNotNull());
    Arrays.stream(types).forEach(t -> tmp.put(t, value));
    return this;
  }

  /**
   * Returns a {@code TypeHashMap} with the configured types and behaviour.
   *
   * @return A new {@code TypeMap} instance with the configured types and behaviour
   */
  @SuppressWarnings({"unchecked"})
  public TypeHashMap<V> freeze() {
    if (expectedSize == 0) { // No auto-expand
      return new TypeHashMap<>(tmp, 0, autobox);
    }
    int sz = expectedSize > tmp.size() ? expectedSize : tmp.size() * expectedSize;
    return new TypeHashMap<>(tmp, sz, autobox);
  }

}
