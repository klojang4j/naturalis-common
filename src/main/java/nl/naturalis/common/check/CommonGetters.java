package nl.naturalis.common.check;

import nl.naturalis.common.NumberMethods;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * Defines various functions that retrieve some oft-used property of a well-known class. For
 * example: {@link Object#toString() Object::toString}. They can optionally be used as the first
 * argument to the various {@code has(...)} methods of the {@link Check} class. The advantage of
 * using these functions rather than the method references they return is that they have been
 * associated with a description of the property they expose, so generating an error message
 * requires very little hand-crafting. For example:
 *
 * <blockquote>
 *
 * <pre>{@code
 * Check.that(car, "car").has(stringValue(), equalTo(), "BMW");
 * // Error message: "car.toString() must be equal to BMW (was Toyota)"
 * }</pre>
 *
 * </blockquote>
 *
 * <p>Note that the word "getter" is suggestive, but also misleading. The functions defined here
 * really are just that: single-argument functions that get passed the argument being validated:
 *
 * <blockquote>
 *
 * <pre>{@code
 * Check.that(temperature, "temperature").has(abs(), lt(), 20);
 * // Error message: "abs(temperature) must be &lt; 20 (was -39)"
 * }</pre>
 *
 * </blockquote>
 *
 * <p>As with the checks in the {@link CommonChecks} class, <i>none of the functions defined here
 * execute a preliminary null check</i>. Most of them simply return a method reference. They rely
 * upon being embedded in chain of checks on a {@link Check} object, the first of which should be
 * the {@link CommonChecks#notNull() not-null} check. Also note that there's nothing stopping you
 * from using the functions defined here for other purposes than precondition checking. For example:
 *
 * <blockquote>
 *
 * <pre>{@code
 * myMap.entrySet().stream().map(e -> { some conversion }).collect(toMap(key(), value()));
 * // in stead of:
 * myMap.entrySet().stream().map(e -> { some conversion }).collect(toMap(Entry::getKey, Entry::getValue));
 * }</pre>
 *
 * </blockquote>
 *
 * @author Ayco Holleman
 */
public class CommonGetters {

  private CommonGetters() {}

  private static final HashMap<Object, String> tmp = new HashMap<>();
  private static final IdentityHashMap<Object, String> names;

  /**
   * Equivalent to {@link Object#toString() Object::toString}.
   *
   * @param <T> The type of the object on which to call {@code toString{}}.
   * @return A {@code Function} that returns the result of calling {@code toString()} on the object.
   */
  public static <T> Function<T, String> asString() {
    return Object::toString;
  }

  static {
    tmp.put(asString(), "%s.toString()");
  }

  /**
   * A {@code Function} that returns the {@code Class} of an object. Equivalent to {@link
   * Object#getClass() Object::getClass}.
   *
   * @param <T> The type of the object
   * @return A {@code Function} that returns the {@code Class} of an object
   */
  public static <T> Function<T, Class<? extends Object>> type() {
    return Object::getClass;
  }

  static {
    tmp.put(type(), "%s.getClass()");
  }

  /**
   * A {@code Function} that returns the constants of an {@code Enum} class. Equivalent to {@link
   * Class#getEnumConstants() Class::getEnumConstants}.
   *
   * @param <T> The enum class
   * @return A {@code Function} that returns all enum constants of an {@code Enum} class
   */
  public static <T extends Enum<T>> Function<Class<T>, T[]> constants() {
    return Class::getEnumConstants;
  }

  static {
    tmp.put(constants(), "%s.getEnumConstants()");
  }

  /**
   * A function that returns the name of an enum constant. Equivalent to {@link Enum#name()
   * Enum::name}.
   *
   * @param <T> The type of the enum class
   * @return A {@code Function} that returns the name of the enum constant
   */
  public static <T extends Enum<T>> Function<T, String> enumName() {
    return Enum::name;
  }

  static {
    tmp.put(enumName(), "%s.name()");
  }

  /**
   * A function that returns the ordinal of an enum constant. Equivalent to {@link Enum#ordinal()
   * Enum::ordinal}.
   *
   * @param <T> The type of the enum class
   * @return A {@code Function} that returns the ordinal of the enum constant
   */
  public static <T extends Enum<T>> ToIntFunction<T> ordinal() {
    return Enum::ordinal;
  }

  static {
    tmp.put(ordinal(), "%s.ordinal()");
  }

  /**
   * A {@code Function} that returns the length of a {@code CharSequence}. Equivalent to {@code
   * CharSequence::length}.
   *
   * @return A {@code Function} that returns the length of a {@code CharSequence}
   */
  public static <T extends CharSequence> ToIntFunction<T> strlen() {
    return CharSequence::length;
  }

  static {
    tmp.put(strlen(), "%s.length()");
  }

  /**
   * A {@code Function} that returns the length of an array. Equivalent to {@link
   * Array#getLength(Object) Array::getLength}.
   *
   * @param <T> The type of the array.
   * @return A {@code Function} that returns the length of an array
   */
  public static <T> ToIntFunction<T> length() {
    return Array::getLength;
  }

  static {
    tmp.put(length(), "%s.length");
  }

  /**
   * A {@code Function} that returns the size of a {@code Collection}. Equivalent to {@code
   * Collection::size}.
   *
   * @param <C> The type of the {@code Collection}
   * @return A {@code Function} that returns the size of a {@code Collection}
   */
  public static <C extends Collection<?>> ToIntFunction<C> size() {
    return Collection::size;
  }

  static {
    tmp.put(size(), "%s.size()");
  }

  /**
   * A {@code Function} that returns the size of a {@code Map}. Equivalent to {@code Map::size}.
   *
   * @param <M> The type of the {@code Map}
   * @return A {@code Function} that returns the size of a {@code Map}
   */
  public static <M extends Map<?, ?>> ToIntFunction<M> mapSize() {
    return Map::size;
  }

  static {
    tmp.put(mapSize(), tmp.get(size())); // recycle
  }

  /**
   * A {@code Function} that returns the keys of a {@code Map}. Equivalent to {@code Map::keySet}.
   *
   * @param <K> The type of the keys in the map
   * @param <V> The type of the values in the map
   * @return A {@code Function} that returns the keys of a {@code Map}
   */
  public static <K, V> Function<Map<K, V>, Set<? extends K>> keySet() {
    return Map::keySet;
  }

  static {
    tmp.put(values(), "%s.keySet()");
  }

  public static <K, V> Function<Map<K, V>, Collection<? extends V>> values() {
    return Map::values;
  }

  static {
    tmp.put(values(), "%s.values()");
  }

  /**
   * A {@code Function} that returns the size of a {@code List}. Equivalent to {@code List::size}.
   * Can be used if there already is a {@code size()} method in the class in which to execute a size
   * check.
   *
   * @param <L> The type of the {@code List}
   * @return A {@code Function} that returns the size of a {@code List}
   */
  public static <L extends List<?>> ToIntFunction<L> listSize() {
    return List::size;
  }

  static {
    tmp.put(listSize(), tmp.get(size()));
  }

  /**
   * A {@code Function} that returns the size of a {@code Set}. Equivalent to {@code Set::size}. Can
   * be used if there already is a {@code size()} method in the class in which to execute a size
   * check.
   *
   * @param <S> The type of the {@code Set}.
   * @return A {@code Function} that returns the size of a {@code Set}
   */
  public static <S extends Set<?>> ToIntFunction<S> setSize() {
    return Set::size;
  }

  static {
    tmp.put(setSize(), tmp.get(size()));
  }

  /**
   * A {@code Function} that returns the absolute value of an integer. Equivalent to {@link
   * Math#abs(int) Math::abs}.
   *
   * @return A {@code Function} that returns the absolute value of an integer
   */
  public static ToIntFunction<Integer> abs() {
    return Math::abs;
  }

  static {
    tmp.put(abs(), "abs(%s)");
  }

  /**
   * A {@code Function} that returns the absolute value of a {@code Number}. Equivalent to {@link
   * NumberMethods#abs(Number) NumberMethods::abs}.
   *
   * @param <T> The type of the {@code Number}
   * @return A {@code Function} that returns the absolute value of a {@code Number}
   */
  public static <T extends Number> Function<T, T> absNumber() {
    return NumberMethods::abs;
  }

  static {
    tmp.put(absNumber(), tmp.get(abs()));
  }

  /**
   * A {@code Function} that returns the key of a {@code Map} entry. Equivalent to {@code
   * Map.Entry::getKey}.
   *
   * @param <K> The type of the key of the entry
   * @param <V> The type of the value of the entry
   * @return A {@code Function} that returns the key of a {@code Map} entry
   */
  public static <K, V> Function<Map.Entry<K, V>, K> key() {
    return Map.Entry::getKey;
  }

  static {
    tmp.put(key(), "%s.getKey()");
  }

  /**
   * A {@code Function} that returns the value of a {@code Map} entry. Equivalent to {@code
   * Map.Entry::getValue}.
   *
   * @param <K> The type of the key of the entry
   * @param <V> The type of the value of the entry
   * @return A {@code Function} that returns the value of a {@code Map} entry
   */
  public static <K, V> Function<Map.Entry<K, V>, V> value() {
    return Map.Entry::getValue;
  }

  static {
    tmp.put(value(), "%s.getValue()");
  }

  /* +++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
  /*            End of getter definitions                    */
  /* +++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

  static String formatProperty(String argName, Object getter) {
    String fmt = names.get(getter);
    if (fmt == null) {
      return argName;
    }
    return String.format(fmt, argName);
  }

  static {
    names = new IdentityHashMap<>(tmp.size());
    names.putAll(tmp);
  }
}
