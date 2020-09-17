package nl.naturalis.common.collection;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.Checks.*;

/**
 * A fast enum-to-int Map implementation. The map is backed by a simple int array with the same
 * length as the number of constants in the {@code enum} class. One integer needs to be designated
 * as the NULL value. By default this is {@code Integer.MIN_VALUE}. If the array element
 * corresponding to an enum constant has this value, it means the enum constant is not in the map
 * (see {@link #containsKey(Enum) containsKey)}. The value designated to be the NULL value must
 * never be {@link #put(Enum, int) put} into the map. It may not even be used as the argument to
 * {@link #containsValue(int) containsValue}. In both cases an {@code IllegalArgumentException} is
 * thrown.
 *
 * @author Ayco Holleman
 */
public final class EnumToIntMap<T extends Enum<T>> {

  private static final String ERR_NULL_NOT_ALLOWED =
      "Illegal attempt to insert/retrieve NULL value";

  private final T[] consts;
  private final int[] data;
  private final int nval;

  /**
   * Creates a new empty {@code EnumToIntMap} for the specfied enum class using {@link
   * Integer#MIN_VALUE Integer.MIN_VALUE} as the NULL value. All elements in the backing array will
   * be initialized to this value (meaning that the map is empty).
   *
   * @param enumClass
   */
  public EnumToIntMap(Class<T> enumClass) {
    this(enumClass, Integer.MIN_VALUE);
  }

  /**
   * Creates a new {@code EnumToIntMap} for the specfied enum class with the specified integer as
   * the NULL value. All elements in the backing array will be initialized to this value (meaning
   * that the map is empty).
   *
   * @param enumClass
   * @param nullValue
   */
  public EnumToIntMap(Class<T> enumClass, int nullValue) {
    this.consts = enumClass.getEnumConstants();
    this.data = new int[consts.length];
    this.nval = nullValue;
    if (nullValue != 0) {
      clear();
    }
  }

  /**
   * Creates a new {@code EnumToIntMap} using Integer.MIN_VALUE as the NULL value and with its keys
   * initialized using the specified value initializer function. This allows for simple one-to-one
   * mappings like {@code new EnumToIntMap(WeekDay.class, k -> k.ordinal() +1)}. It also allows you
   * to initialize the map values to something else than the NULL value, e.g. {@code new
   * EnumToIntMap(MyEnum.class, k -> 0}.
   *
   * @param enumClass
   * @param initializer
   */
  public EnumToIntMap(Class<T> enumClass, ToIntFunction<T> initializer) {
    this(enumClass, Integer.MIN_VALUE, initializer);
  }

  /**
   * Creates a new {@code EnumToIntMap} with the specified NULL value and the specified value
   * initializer function.
   *
   * @param enumClass
   * @param nullValue
   * @param initializer
   */
  public EnumToIntMap(Class<T> enumClass, int nullValue, ToIntFunction<T> initializer) {
    this.consts = enumClass.getEnumConstants();
    this.data = new int[consts.length];
    this.nval = nullValue;
    for (T c : consts) {
      set(c, initializer.applyAsInt(c));
    }
  }

  /**
   * Copy constructor. The new map inherits the NULL value from the other map.
   *
   * @param other The {@code EnumToIntMap} whose key-value mappings to copy
   */
  public EnumToIntMap(EnumToIntMap<T> other) {
    this.consts = other.consts;
    this.data = new int[consts.length];
    this.nval = other.nval;
    System.arraycopy(other.data, 0, this.data, 0, consts.length);
  }

  /**
   * Instantiates a new {@code EnumToIntMap} with the key-value mappings of the provided {@code
   * EnumToIntMap}, but (potentially) with a new NULL value.
   *
   * @param other
   * @param nullValue
   */
  public EnumToIntMap(EnumToIntMap<T> other, int nullValue) {
    this.consts = other.consts;
    this.data = new int[consts.length];
    this.nval = nullValue;
    putAll(other);
  }

  /**
   * Wheter or not the map contains an entry for the specified enum constant.
   *
   * @param key
   * @return
   */
  public boolean containsKey(T key) {
    return get(key) != nval;
  }

  /**
   * Whether or not the map contains the specified value. Note that you are not permitted to search
   * for the value designated to be the NULL value. An {@code IllegalArgumentException} is thrown if
   * you attempt to do so.
   *
   * @param val
   * @return
   */
  public boolean containsValue(int val) {
    Check.argument(val, "val").and(notEqualTo(), nval, ERR_NULL_NOT_ALLOWED);
    for (int v : data) {
      if (v == val) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the value associated with the specified enum constant.
   *
   * @param key
   * @return
   */
  public int get(T key) {
    return data[key.ordinal()];
  }

  /**
   * Returns the value associated with the specified enum constant or {@coded dfault} if the map did
   * not contain an entry for the specified enum constant.
   *
   * @param key
   * @param dfault
   * @return
   */
  public int getOrDefault(T key, int dfault) {
    return containsKey(key) ? data[key.ordinal()] : dfault;
  }

  /**
   * Adds or overwrite the value for the specified enum constant. Note that you are not permitted to
   * use the NULL value as the second argument. An {@code IllegalArgumentException} is thrown if you
   * do so.
   *
   * @param key
   * @param val
   * @return The previous value associated with the specified enum constant or the NULL value if the
   *     map did not contain an entry for the enum constant yet.
   */
  public int put(T key, int val) {
    int i = get(key);
    set(key, val);
    return i;
  }

  /**
   * Much like {@code put}, but provides a fluent API for adding entries to the map.
   *
   * @param key
   * @param val
   * @return
   */
  public EnumToIntMap<T> set(T key, int val) {
    Check.argument(val, "val").and(notEqualTo(), nval, ERR_NULL_NOT_ALLOWED);
    data[key.ordinal()] = val;
    return this;
  }

  /**
   * Adds all entries of the specified map to this map, overwriting any previous values. The other
   * map is allowed to have a different NULL value, but it must not contain <i>this</i> map's NULL
   * value. An {@link IllegalArgumentException} is thrown if it does.
   *
   * @param other
   */
  public void putAll(EnumToIntMap<T> other) {
    for (int i = 0; i < other.data.length; ++i) {
      if (other.data[i] != other.nval) {
        set(consts[i], other.data[i]);
      }
    }
  }

  /**
   * Adds all entries of the specified map to this map. This method acts as a bridge to
   * fully-generic map implementations.
   *
   * @param other
   */
  public void putAll(Map<T, ? extends Number> other) {
    for (T t : consts) {
      if (other.containsKey(t)) {
        set(
            t,
            other
                .get(t)
                .intValue()); // Throw IllegalArgumentException if other.get(t).intValue() ==
        // this.nval
      }
    }
  }

  /**
   * Returns a fully-generic version of this map.
   *
   * @return
   */
  public Map<T, Integer> toGenericMap() {
    @SuppressWarnings("unchecked")
    EnumMap<T, Integer> map = new EnumMap<>((Class<T>) consts[0].getClass());
    for (T t : consts) {
      if (containsKey(t)) {
        map.put(t, Integer.valueOf(data[t.ordinal()]));
      }
    }
    return map;
  }

  /**
   * Removes the entry corresponding to the specified enum constant.
   *
   * @param key
   * @return
   */
  public int remove(T key) {
    int v = get(key);
    data[key.ordinal()] = nval;
    return v;
  }

  /**
   * Returns a Set view of the keys contained in this map.
   *
   * @return
   */
  public Set<T> keySet() {
    @SuppressWarnings("unchecked")
    EnumSet<T> keys = EnumSet.noneOf(consts[0].getClass());
    for (int i = 0; i < data.length; ++i) {
      if (data[i] != nval) {
        keys.add(consts[i]);
      }
    }
    return keys;
  }

  /**
   * Returns a {@code Collection} view of the values contained in this map.
   *
   * @return
   */
  public Set<Integer> values() {
    Set<Integer> keys = new HashSet<>(size());
    for (int val : data) {
      if (val != nval) {
        keys.add(Integer.valueOf(val));
      }
    }
    return keys;
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
  public void forEach(ObjIntConsumer<T> consumer) {
    for (int i = 0; i < data.length; ++i) {
      consumer.accept(consts[i], data[i]);
    }
  }

  /** Removes all of the mappings from this map. */
  public void clear() {
    Arrays.fill(data, nval);
  }

  /**
   * Returns the number of key-value mappings in this map.
   *
   * @return The number of key-value mappings in this map
   */
  public int size() {
    return (int) Arrays.stream(data).filter(v -> v != nval).count();
  }

  /**
   * Returns the integer that functions as the NULL value for this map.
   *
   * @return The integer that functions as the NULL value for this map
   */
  public int getNullValue() {
    return nval;
  }

  /**
   * Returns true if the provided object is an instance of {@code EnumToIntMap} and if it contains
   * the same NULL value and the same key-value mappings, {@code false} otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != EnumToIntMap.class) {
      return false;
    }
    EnumToIntMap<?> other = (EnumToIntMap<?>) obj;
    if (consts[0].getClass() != other.consts[0].getClass()) {
      return false;
    }
    return Objects.equals(this.nval, other.nval) && Arrays.equals(this.data, other.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nval, data);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    for (int i = 0; i < data.length; ++i) {
      if (data[i] == nval) {
        continue;
      }
      if (sb.length() != 1) {
        sb.append("; ");
      }
      sb.append('"').append(consts[i].toString()).append("\": ").append(data[i]);
    }
    sb.append('}');
    return sb.toString();
  }
}
