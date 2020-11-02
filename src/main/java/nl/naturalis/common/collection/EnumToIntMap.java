package nl.naturalis.common.collection;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import nl.naturalis.common.Tuple;
import nl.naturalis.common.check.Check;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static nl.naturalis.common.Tuple.tuple;
import static nl.naturalis.common.check.CommonChecks.hasValue;
import static nl.naturalis.common.check.CommonChecks.ne;
import static nl.naturalis.common.check.CommonChecks.notEmpty;
import static nl.naturalis.common.check.CommonChecks.notHasKey;
import static nl.naturalis.common.check.CommonGetters.enumConstants;

/**
 * A fast enum-to-int Map implementation. The map is backed by a simple int array with the same
 * length as the number of constants in the {@code enum} class. One integer must be designated to
 * signify the absence of a key (i.e. an enum constant). By default this is {@link Integer#MIN_VALUE
 * Integer.MIN_VALUE}. If an element in the int array has this value, it means there is no entry for
 * the corresponding enum constant in the {@code EnumToIntMap}. It is not allowed to add a key with
 * this value to the map, as is would in effect amount to <i>removing</i> that key from the map. It
 * is also not allowed to pass this value to {@link #containsValue(int) containsValue}. In both
 * cases an {@code IllegalArgumentException} is thrown.
 *
 * @author Ayco Holleman
 * @param <K> The type of the enum class
 */
public final class EnumToIntMap<K extends Enum<K>> {

  private final K[] keys;
  private final int[] data;
  private final int kav;

  /**
   * Creates a new empty {@code EnumToIntMap} for the specfied enum class using {@link
   * Integer#MIN_VALUE Integer.MIN_VALUE} as the <i>key-absent-value</i> value. All elements in the
   * backing array will be initialized to this value (meaning that the map is empty).
   *
   * @param enumClass The type of the enum class
   */
  public EnumToIntMap(Class<K> enumClass) {
    this(enumClass, Integer.MIN_VALUE);
  }

  /**
   * Creates a new {@code EnumToIntMap} for the specfied enum class with the specified integer as
   * the <i>key-absent-value</i> value. All elements in the backing array will be initialized to
   * this value (meaning that the map is empty).
   *
   * @param enumClass The type of the enum class
   * @param keyAbsentValue The value used to signify the absence of a key
   */
  public EnumToIntMap(Class<K> enumClass, int keyAbsentValue) {
    this(enumClass, keyAbsentValue, k -> keyAbsentValue);
  }

  /**
   * Creates a new {@code EnumToIntMap} using Integer.MIN_VALUE as the <i>key-absent-value</i> value
   * and with its keys initialized using the specified initializer function. For example: {@code new
   * EnumToIntMap(WeekDay.class, k -> k.ordinal() +1)}.
   *
   * @param enumClass The type of the enum class
   * @param initializer A function called to initialize the array elements
   */
  public EnumToIntMap(Class<K> enumClass, ToIntFunction<K> initializer) {
    this(enumClass, Integer.MIN_VALUE, initializer);
  }

  /**
   * Creates a new {@code EnumToIntMap} with the specified <i>key-absent-value</i> value and the
   * specified initializer function.
   *
   * @param enumClass The type of the enum class
   * @param keyAbsentValue The value used to signify the absence of a key
   * @param initializer A function called to initialize the array elements
   */
  public EnumToIntMap(Class<K> enumClass, int keyAbsentValue, ToIntFunction<K> initializer) {
    Check.notNull(enumClass, "enumClass")
        .has(enumConstants(), notEmpty(), "Empty enum not supported");
    this.keys = enumClass.getEnumConstants();
    this.data = new int[keys.length];
    this.kav = keyAbsentValue;
    Arrays.stream(keys).forEach(k -> assign(k, initializer.applyAsInt(k)));
  }

  /**
   * Instantiates a new {@code EnumToIntMap} with the same key-value mappings as the specifed {@code
   * EnumToIntMap} and with the same <i>key-absent-value</i> value.
   *
   * @param other The {@code EnumToIntMap} whose key-value mappings to copy
   */
  public EnumToIntMap(EnumToIntMap<K> other) {
    Check.notNull(other, "other");
    this.keys = other.keys;
    this.data = new int[keys.length];
    this.kav = other.kav;
    System.arraycopy(other.data, 0, this.data, 0, keys.length);
  }

  /**
   * Instantiates a new {@code EnumToIntMap} with the same key-value mappings as the specifed {@code
   * EnumToIntMap}, but (potentially) with a new <i>key-absent-value</i>.
   *
   * @param other The {@code EnumToIntMap} whose key-value mappings to copy
   * @param keyAbsentValue The value used to signify the absence of a key
   */
  public EnumToIntMap(EnumToIntMap<K> other, int keyAbsentValue) {
    Check.notNull(other, "other");
    this.keys = other.keys;
    this.data = new int[keys.length];
    this.kav = keyAbsentValue;
    copyEntries(other);
  }

  /**
   * Wheter or not the map contains an entry for the specified enum constant.
   *
   * @param key The enum constant
   * @return Whether or not the map contains an entry for the enum constant
   */
  public boolean containsKey(K key) {
    Check.notNull(key, "key");
    return isPresent(key);
  }

  /**
   * Whether or not the map contains the specified value. It is not permitted to search for the
   * <i>key-absent-value</i> value. An {@code IllegalArgumentException} is thrown if you do.
   *
   * @param val The value
   * @return Whether or not the map contains the value
   */
  public boolean containsValue(int val) {
    Check.that(val, "val").is(ne(), kav);
    return Arrays.stream(data).filter(x -> x == val).findFirst().isPresent();
  }

  /**
   * Returns the value to which the specified key is mapped, or the <i>key-absent-value</i> if this
   * map contains no mapping for the key. (A regular {@code Map} would return {@code null} in the
   * latter case.)
   *
   * @param key The key whose associated value is to be returned
   * @return The value to which the specified key is mapped, or the <i>key-absent-value</i> if this
   *     map contains no mapping for the key
   */
  public int get(K key) {
    Check.notNull(key, "key");
    return valueOf(key);
  }

  /**
   * Returns the value associated with the specified enum constant or {@coded dfault} if the map did
   * not contain an entry for the specified enum constant.
   *
   * @param key The key to retrieve the value of.
   * @param dfault The integer to return if the map did not contain the key
   * @return The value associated with the key or {@code dfault}
   */
  public int getOrDefault(K key, int dfault) {
    return containsKey(key) ? valueOf(key) : dfault;
  }

  /**
   * Associates the specified value with the specified key in this map.
   *
   * @param key The key
   * @param val The value
   * @return The previous value associated with the specified enum constant or the
   *     <i>key-absent-value</i> value if the map did not contain an entry for the enum constant
   *     yet.
   */
  public int put(K key, int val) {
    Check.notNull(key, "key");
    Check.that(val, "val").is(ne(), kav);
    int orig = valueOf(key);
    assign(key, val);
    return orig;
  }

  /**
   * Much like {@code put}, but provides a fluent API for adding entries to the map.
   *
   * @param key The key
   * @param val The value
   * @return This instance
   */
  public EnumToIntMap<K> set(K key, int val) {
    Check.notNull(key, "key");
    Check.that(val, "val").is(ne(), kav);
    assign(key, val);
    return this;
  }

  /**
   * Adds all entries of the specified map to this map, overwriting any previous values. The source
   * map must not contain the <i>key-absent-value</i> of this map. An {@link
   * IllegalArgumentException} is thrown if it does.
   *
   * @param other The {@code EnumToIntMap} whose key-value mappings to copy
   */
  public void putAll(EnumToIntMap<K> other) {
    Check.notNull(other, "other");
    copyEntries(other);
  }

  /**
   * Adds all entries of the specified map to this map. This method acts as a bridge to
   * fully-generic map implementations. The source map must not contain the <i>key-absent-value</i>
   * of this map. An {@link IllegalArgumentException} is thrown if it does.
   *
   * @param other The {@code Map} whose key-value mappings to copy
   */
  public void putAll(Map<K, Integer> other) {
    Check.notNull(other, "other")
        .is(notHasKey(), null)
        .is(hasValue(), kav)
        .then(m -> m.entrySet().forEach(e -> assign(e.getKey(), e.getValue())));
  }

  /**
   * Returns a fully-generic version of this map.
   *
   * @return A fully-generic version of this map
   */
  public Map<K, Integer> toGenericMap() {
    return streamKeys().collect(toMap(e -> e, Enum::ordinal));
  }

  /**
   * Removes the mapping for a key from this map if it is present.
   *
   * @param key The key
   * @return The previous value associated with key, or the <i>key-absent-value</i> value if there
   *     was no mapping for key.
   */
  public int remove(K key) {
    Check.notNull(key, "key");
    int v = valueOf(key);
    assign(key, kav);
    return v;
  }

  /**
   * Returns a Set view of the keys contained in this map.
   *
   * @return A Set view of the keys contained in this map
   */
  public Set<K> keySet() {
    return streamKeys().collect(toSet());
  }

  /**
   * Returns a {@code Collection} view of the values contained in this map.
   *
   * @return A {@code Collection} view of the values contained in this map
   */
  public Set<Integer> values() {
    return streamKeys().map(this::valueOf).collect(toSet());
  }

  /**
   * Returns a Set view of the mappings contained in this map.
   *
   * @return A set view of the mappings contained in this map
   */
  public Set<Map.Entry<K, Integer>> entrySet() {
    return streamKeys().map(k -> tuple(k, valueOf(k))).map(Tuple::toEntry).collect(toSet());
  }

  /**
   * Returns true if this map contains no key-value mappings, false otherwise.
   *
   * @return
   */
  public boolean isEmpty() {
    return size() == 0;
  }

  /**
   * Performs the given action for each entry in this map until all entries have been processed or
   * the action throws an exception.
   *
   * @param consumer
   */
  public void forEach(ObjIntConsumer<K> consumer) {
    streamKeys().forEach(k -> consumer.accept(k, valueOf(k)));
  }

  /** Removes all of the mappings from this map. */
  public void clear() {
    Arrays.fill(data, kav);
  }

  /**
   * Returns the number of key-value mappings in this map.
   *
   * @return The number of key-value mappings in this map
   */
  public int size() {
    return (int) streamKeys().count();
  }

  /**
   * Returns the type of the enum keys.
   *
   * @return The type of the enum keys
   */
  @SuppressWarnings("unchecked")
  public Class<K> keyType() {
    return (Class<K>) keys[0].getClass();
  }

  /**
   * Returns the integer used to signify the absence of a key within this map.
   *
   * @return The integer used to signify the absence of a key
   */
  public int keyAbsentValue() {
    return kav;
  }

  /**
   * Returns {@code true} if the argument is an {@code EnumToIntMap} for the same {@code enum} class
   * and if it contains the same key-value mappings, {@code false} otherwise. The two maps need not
   * have the same <i>key-absent-value</i> value.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null || obj.getClass() != EnumToIntMap.class) {
      return false;
    }
    EnumToIntMap<?> other = (EnumToIntMap<?>) obj;
    if (keyType() != other.keyType()) {
      return false;
    }
    for (int i = 0; i < keys.length; i++) {
      if (data[i] == kav) {
        if (other.data[i] != other.kav) {
          return false;
        }
      } else if (data[i] == other.kav || data[i] != other.data[i]) {
        return false;
      }
    }
    return true;
  }

  /** {@inheritDoc} */
  public int hashCode() {
    return Objects.hash(data, kav);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return toGenericMap().toString();
  }

  private void copyEntries(EnumToIntMap<K> other) {
    if (kav != other.kav && other.containsValue(kav)) {
      throw new IllegalArgumentException("Source map must not contain value " + kav);
    }
    other.streamKeys().forEach(k -> assign(k, other.valueOf(k)));
  }

  private Stream<K> streamKeys() {
    return Arrays.stream(keys).filter(this::isPresent);
  }

  private boolean isPresent(K key) {
    return valueOf(key) != kav;
  }

  private void assign(K key, int val) {
    data[key.ordinal()] = val;
  }

  private int valueOf(K key) {
    return data[key.ordinal()];
  }
}
