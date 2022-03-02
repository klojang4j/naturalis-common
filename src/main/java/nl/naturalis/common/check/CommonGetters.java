package nl.naturalis.common.check;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.function.Relation;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.*;

import static nl.naturalis.common.ClassMethods.simpleClassName;
import static nl.naturalis.common.ObjectMethods.ifNull;

/**
 * Defines various functions that retrieve some oft-used property of a well-known class. For
 * example: {@link Object#toString() Object::toString}. They can optionally be used as the first
 * argument to the various {@code has(...)} and {@code notHas(...) } methods of {@link IntCheck} and
 * {@link ObjectCheck}. The advantage of using these functions rather than the method references
 * they return is that they have been associated with a description of the property they expose, so
 * generating an error message requires very little hand-crafting. For example:
 *
 * <blockquote>
 *
 * <pre>{@code
 * Check.that(car, "car").has(strval(), equalTo(), "BMW");
 * // Error message: "car.toString() must be equal to BMW (was Toyota)"
 * }</pre>
 *
 * </blockquote>
 *
 * <p>Note that the word "getter" is suggestive, but also misleading. The functions defined here
 * really are just that: functions that transform the argument into some other value, which can then
 * be subjected to further tests.
 *
 * <blockquote>
 *
 * <pre>{@code
 * Check.that(temperature, "temperature").has(abs(), lt(), 20);
 * // Error message: "abs(temperature) must be < 20 (was -39)"
 * }</pre>
 *
 * </blockquote>
 *
 * <p>As with the checks in the {@link CommonChecks} class, <i>none of the functions defined here
 * execute a preliminary null check</i>. Many of them simply return a method reference. They rely
 * upon being embedded in chain of checks, the first of which should be the {@link
 * CommonChecks#notNull() notNull} check (if necessary).
 *
 * @author Ayco Holleman
 */
public class CommonGetters {

  private CommonGetters() {}

  private static final Map<Object, BiFunction<Object, String, String>> NAMES;

  private static Map<Object, BiFunction<Object, String, String>> tmp = new HashMap<>();

  /**
   * Returns the boxed version of an {@code int} argument. Equivalent to {@link Integer#valueOf(int)
   * Integer::valueOf}. This "getter" is especially useful to get access to the many {@link
   * Relation} checks in the {@link CommonChecks} class when validating an {@code int} argument:
   *
   * <blockquote>
   *
   * <pre>{@code
   * // Won't compile because IntCheck does not have an is(Relation, Object) method
   * Check.that(42).is(keyIn(), map);
   *
   * // OK, but not very elegant
   * Check.that((Integer) 42).is(keyIn(), map);
   *
   * Check.that(42).has(box(), keyIn(), map);
   *
   * }</pre>
   *
   * </blockquote>
   *
   * @return The boxed version of an {@code int} argument
   */
  public static IntFunction<Integer> box() {
    return Integer::valueOf;
  }

  /**
   * Returns the result of calling {@code toString()} on the argument. Equivalent to {@link
   * Object#toString() Object::toString}.
   *
   * @param <T> The type of the object on which to call {@code toString{}}.
   * @return The result of calling {@code toString()} on the argument
   */
  public static <T> Function<T, String> strval() {
    return Object::toString;
  }

  static {
    tmp.put(strval(), (arg, argName) -> ifNull(argName, simpleClassName(arg)) + ".toString()");
  }

  /**
   * Returns the length of a {@code CharSequence}. Equivalent to {@code CharSequence::length}.
   *
   * @return The length of a {@code CharSequence}
   */
  public static <T extends CharSequence> ToIntFunction<T> strlen() {
    return CharSequence::length;
  }

  static {
    tmp.put(strlen(), (arg, argName) -> ifNull(argName, simpleClassName(arg)) + ".length()");
  }

  /**
   * Returns the upper case version of the argument
   *
   * @return The upper case version of the argument
   */
  public static Function<String, String> toUpperCase() {
    return String::toUpperCase;
  }

  static {
    tmp.put(
        toUpperCase(), (arg, argName) -> ifNull(argName, simpleClassName(arg)) + ".toUpperCase()");
  }

  /**
   * Returns the lower case version of the argument
   *
   * @return The lower case version of the argument
   */
  public static Function<String, String> toLowerCase() {
    return String::toLowerCase;
  }

  static {
    tmp.put(
        toLowerCase(), (arg, argName) -> ifNull(argName, simpleClassName(arg)) + ".toLowerCase()");
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
    tmp.put(type(), (arg, argName) -> ifNull(argName, simpleClassName(arg)) + ".getClass()");
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
    tmp.put(
        constants(),
        (arg, argName) -> ifNull(argName, simpleClassName(arg)) + ".getEnumConstants()");
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
    tmp.put(enumName(), (arg, argName) -> ifNull(argName, ((Enum) arg).name()) + ".name()");
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
    tmp.put(ordinal(), (arg, argName) -> ifNull(argName, ((Enum) arg).name()) + ".ordinal()");
  }

  /**
   * Returns the length of an array argument. Equivalent to {@link Array#getLength(Object)
   * Array::getLength}.
   *
   * @param <T> The type of the array.
   * @return A {@code Function} that returns the length of an array
   */
  public static <T> ToIntFunction<T> length() {
    return Array::getLength;
  }

  static {
    tmp.put(length(), (arg, argName) -> ifNull(argName, simpleClassName(arg)) + ".length");
  }

  /**
   * Returns the size of a {@code Collection} argument. Equivalent to {@code Collection::size}.
   *
   * @param <C> The type of the {@code Collection}
   * @return The size of a {@code Collection} argument
   */
  public static <C extends Collection<?>> ToIntFunction<C> size() {
    return Collection::size;
  }

  static {
    tmp.put(size(), (arg, argName) -> ifNull(argName, simpleClassName(arg)) + ".size()");
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
    tmp.put(keySet(), (arg, argName) -> ifNull(argName, simpleClassName(arg)) + ".keySet()");
  }

  public static <K, V> Function<Map<K, V>, Collection<? extends V>> values() {
    return Map::values;
  }

  static {
    tmp.put(values(), (arg, argName) -> ifNull(argName, simpleClassName(arg)) + ".values()");
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
    tmp.put(listSize(), tmp.get(size())); // recycle
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
    tmp.put(setSize(), tmp.get(size())); // recycle
  }

  /**
   * Returns the absolute value of an {@code int} argument. Equivalent to {@link Math#abs(int)
   * Math::abs}.
   *
   * @return A {@code Function} that returns the absolute value of an integer
   */
  public static IntUnaryOperator abs() {
    return Math::abs;
  }

  static {
    tmp.put(
        abs(), (arg, argName) -> argName == null ? "absolute value" : "Math.abs(" + argName + ")");
  }

  /**
   * Returns the absolute value of a {@code Number}. Equivalent to {@link NumberMethods#abs(Number)
   * NumberMethods::abs}.
   *
   * @param <T> The type of the {@code Number}
   * @return The absolute value of a {@code Number}
   */
  public static <T extends Number> Function<T, T> ABS() {
    return NumberMethods::abs;
  }

  static {
    tmp.put(
        abs(),
        (arg, argName) ->
            String.format("absolute value of %s", ifNull(argName, simpleClassName(arg))));
  }

  /**
   * Returns the key of a {@code Map} entry. Equivalent to {@code Map.Entry::getKey}.
   *
   * @param <K> The type of the key of the entry
   * @param <V> The type of the value of the entry
   * @return The key of a {@code Map} entry
   */
  public static <K, V> Function<Map.Entry<K, V>, K> key() {
    return Map.Entry::getKey;
  }

  static {
    tmp.put(key(), (arg, argName) -> ifNull(argName, "entry") + ".getKey()");
  }

  /**
   * Returns the value of a {@code Map} entry. Equivalent to {@code Map.Entry::getValue}.
   *
   * @param <K> The type of the key of the entry
   * @param <V> The type of the value of the entry
   * @return A {@code Function} that returns the value of a {@code Map} entry
   */
  public static <K, V> Function<Map.Entry<K, V>, V> value() {
    return Map.Entry::getValue;
  }

  static {
    tmp.put(value(), (arg, argName) -> ifNull(argName, "entry") + ".getValue()");
  }

  /* +++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
  /*            End of getter definitions                    */
  /* +++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

  static String formatProperty(Object arg, String argName, Object getter, Class getterClass) {
    BiFunction<Object, String, String> fmt = NAMES.get(getter);
    if (fmt == null) {
      String s0 = getterClass == ToIntFunction.class ? "applyAsInt" : "apply";
      String s1 = ifNull(argName, simpleClassName(arg));
      return simpleClassName(getterClass) + "." + s0 + "(" + s1 + ")";
    }
    return fmt.apply(arg, argName);
  }

  static String formatProperty(int arg, String argName, Object getter, Class getterClass) {
    BiFunction<Object, String, String> fmt = NAMES.get(getter);
    if (fmt == null) {
      String s0 = getterClass == IntUnaryOperator.class ? "applyAsInt" : "apply";
      String s1 = ifNull(argName, "int");
      return simpleClassName(getterClass) + "." + s0 + "(" + s1 + ")";
    }
    return fmt.apply(arg, argName);
  }

  static {
    NAMES = Map.copyOf(tmp);
    tmp = null;
  }
}
