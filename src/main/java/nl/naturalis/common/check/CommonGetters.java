package nl.naturalis.common.check;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import nl.naturalis.common.NumberMethods;
import static nl.naturalis.common.ObjectMethods.*;

/**
 * Defines various commonly used getter-type (no-arg) methods that can be used to retrieve a
 * property of the argument. They are meant to be used in conjunction with the various {@code
 * has(...)} methods of the {@link Check} class. Each getter is associated with the name of the
 * property it exposes, so you can choose the leanest of the {@code has(...)} methods (those that
 * don't let you specify a property name yourself). For example:
 *
 * <p>
 *
 * <pre>
 * Check.notNull(stampCollection, "stampCollection").has(size(), greaterThan(), 100);
 * // "stampCollection.size() must be &gt; 100 (was 22)"
 * </pre>
 *
 * <p>Most of the getters defined here are plain, unadorned method references. <b>None of them do a
 * preliminary null-check on the argument.</b> They rely upon being embedded within in chain of
 * checks on a {@link Check} object, the first of which should be a <i>not-null</i> check.
 *
 * <p>NB some getters defined here are actually not no-arg methods on the argument but rather unary
 * operations on it, disguised as a {@link Function} so they can be passed in as the first argument
 * to the {@code has (...)} methods.
 *
 * @author Ayco Holleman
 */
public class CommonGetters {

  private CommonGetters() {}

  private static final HashMap<Object, String> tmp = new HashMap<>();
  private static final IdentityHashMap<Object, String> names;

  /**
   * A {@code Function} that returns the {@code Class} of an object.
   *
   * @param <T> The type of the object
   * @return A {@code Function} that returns the {@code Class} of an object
   */
  @SuppressWarnings("unchecked")
  public static <T> Function<T, Class<T>> type() {
    return x -> (Class<T>) x.getClass();
  }

  static {
    tmp.put(type(), "%s.getClass()");
  }

  /**
   * A {@code Function} that returns all enum constants of an {@code Enum} class.
   *
   * @param <T> The enum class
   * @return A {@code Function} that returns all enum constants of an {@code Enum} class
   */
  public static <T extends Enum<T>> Function<Class<T>, T[]> enumConstants() {
    return x -> x.getEnumConstants();
  }

  static {
    tmp.put(enumConstants(), "%s.getEnumConstants()");
  }

  /**
   * A {@code Function} that returns the length of a {@code String}. Equivalent to {@code
   * String::length}.
   *
   * @return A {@code Function} that returns the length of a {@code String}
   */
  public static ToIntFunction<String> stringLength() {
    return String::length;
  }

  static {
    tmp.put(stringLength(), "%s.length()");
  }

  /**
   * A {@code Function} that returns the length of an array.
   *
   * @param <T> The type of the elements in the array
   * @return A {@code Function} that returns the length of an array
   */
  public static <T> ToIntFunction<T[]> arrayLength() {
    return x -> x.length;
  }

  static {
    tmp.put(arrayLength(), "%s.length");
  }

  /**
   * A {@code Function} that returns the length of an {@code int} array.
   *
   * @return A {@code Function} that returns the length of an array
   */
  public static ToIntFunction<int[]> intArrayLength() {
    return x -> x.length;
  }

  static {
    tmp.put(intArrayLength(), "%s.length");
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
    tmp.put(mapSize(), "%s.size()");
  }

  /**
   * A {@code Function} that returns the size of a {@code List}. Equivalent to {@code List::size}.
   *
   * @param <L> The type of the {@code List}
   * @return A {@code Function} that returns the size of a {@code List}
   */
  public static <L extends List<?>> ToIntFunction<L> listSize() {
    return List::size;
  }

  static {
    tmp.put(listSize(), "%s.size()");
  }

  /**
   * A {@code Function} that returns the size of a {@code Set}. Equivalent to {@code Set::size}.
   *
   * @param <S> The type of the {@code Set}.
   * @return A {@code Function} that returns the size of a {@code Set}
   */
  public static <S extends Set<?>> ToIntFunction<S> setSize() {
    return Set::size;
  }

  static {
    tmp.put(setSize(), "%s.size()");
  }

  /**
   * A {@code Function} that returns the absolute value of a {@code Number}. Equivalent to {@link
   * NumberMethods#abs(Number) NumberMethods::abs}.
   *
   * @param <T> The type of the {@code Number}
   * @return A {@code Function} that returns the absolute value of a {@code Number}
   */
  public static <T extends Number> Function<T, T> abs() {
    return NumberMethods::abs;
  }

  static {
    tmp.put(abs(), "abs(%s)");
  }

  /**
   * A {@code Function} that returns the value supplied by a {@code Supplier}. Equivalent to {@link
   * Supplier#get() Supplier::get}.
   *
   * @param <T> The type of the object supplied by the {@code Supplier}
   * @param <U> The type of the {@code Supplier}
   * @return A {@code Function} that returns the value supplied by a {@code Supplier}
   */
  public static <T, U extends Supplier<T>> Function<U, T> supplied() {
    return Supplier::get;
  }

  static {
    tmp.put(supplied(), "%s.get()");
  }

  /* +++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
  /*            End of getter definitions                    */
  /* +++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

  static String getGetterDescription(String argName, Object getter) {
    return ifNotNull(names.get(getter), fmt -> String.format(fmt, argName), () -> argName + ".?");
  }

  static {
    names = new IdentityHashMap<>(tmp.size());
    names.putAll(tmp);
  }
}
