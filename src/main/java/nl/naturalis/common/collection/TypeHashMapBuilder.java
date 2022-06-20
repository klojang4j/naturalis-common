package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.Arrays;
import java.util.HashMap;

import static nl.naturalis.common.check.CommonChecks.*;

/**
 * A builder class for {@link TypeHashMap} instances.
 *
 * @param <V> The type of the values in the {@code TypeHashMap}
 * @author Ayco Holleman
 * @see TypeMap
 */
public final class TypeHashMapBuilder<V> {

  private final Class<V> valueType;
  private final HashMap<Class<?>, V> temp = new HashMap<>();

  private int expectedSize = 0;
  private boolean autobox = true;

  TypeHashMapBuilder(Class<V> valueType) {
    this.valueType = valueType;
  }

  /**
   * Enables or disables the auto-expansion feature. See {@link TypeHashMap} for an
   * explanation of this feature. Specifying {@code true} is equivalent to calling
   * {@link #autoExpand(int) autoExpand(2)}.
   *
   * @return This {@code Builder} instance
   */
  public TypeHashMapBuilder<V> autoExpand(boolean autoExpand) {
    expectedSize = autoExpand ? 2 : 0;
    return this;
  }

  /**
   * Enables or disables the auto-expansion feature. See {@link TypeHashMap} for an
   * explanation of this feature. If {@code expectedSize} is zero, auto-expansion
   * will be disabled. If {@code expectedSize } is greater than the number of types
   * you have added, it is taken as an estimate of how large you expect the map to
   * become as a consequence of auto-expansion. If it is less than or equal to the
   * number of types, it is taken as a  multiplier. So, for example, specifying 3
   * would mean that you expect the map to grow to about three times its original
   * size.
   *
   * @param expectedSize An estimate of the final size of the auto-expanding map
   * @return This {@code Builder} instance
   * @throws IllegalArgumentException If {@code expectedSize} is less than zero
   */
  public TypeHashMapBuilder<V> autoExpand(int expectedSize) {
    Check.that(expectedSize, "expected size").isNot(negative());
    this.expectedSize = expectedSize;
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
    Check.notNull(type, "type").isNot(keyIn(), temp,
        () -> new DuplicateKeyException(type));
    Check.notNull(value, "value").is(instanceOf(), valueType);
    temp.put(type, value);
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
    Check.notNull(types, "types").ok(Arrays::stream).forEach(t -> add(t, value));
    return this;
  }

  /**
   * Returns a {@code TypeHashMap} with the configured types and behaviour.
   *
   * @return A {@code TypeMap} with the configured types and behaviour
   */
  @SuppressWarnings({"unchecked"})
  public TypeHashMap<V> freeze() {
    if (expectedSize <= temp.size()) {
      expectedSize *= temp.size();
    }
    return new TypeHashMap<>(temp, expectedSize, autobox);
  }

}
