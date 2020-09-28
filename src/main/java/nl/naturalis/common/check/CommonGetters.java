package nl.naturalis.common.check;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * Lists some commonly used getter-type (no-arg) methods. When used in combination with the {@link
 * Check} class these would be called on the argument in order to check the value of one of its
 * properties. Most of the getters defined here are plain, unadorned method references and <i>none
 * do a null-check on the argument.</i> This is supposed to have already been done (e.g. using
 * {@link CommonChecks#notNull() Checks.notNull}).
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
    tmp.put(type(), "class");
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
    tmp.put(enumConstants(), "enumConstants");
  }

  /**
   * A {@code Function} that returns the length of a {@code String}. Equivalent to {@code
   * String::length}.
   *
   * @return A {@code Function} that returns the length of a {@code String}
   */
  public static ToIntFunction<String> length() {
    return String::length;
  }

  static {
    tmp.put(length(), "length");
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
    tmp.put(arrayLength(), "length");
  }

  /**
   * A {@code Function} that returns the size of a {@code Collection}. Equivalent to {@code
   * Collection::size}.
   *
   * @param <T> The type of the elements in the {@code Collection}
   * @return A {@code Function} that returns the size of a {@code Collection}
   */
  public static <T> ToIntFunction<Collection<T>> size() {
    return Collection::size;
  }

  static {
    tmp.put(size(), "size");
  }

  /**
   * A {@code Function} that returns the size of a {@code Map}. Equivalent to {@code Map::size}.
   *
   * @param <T> The type of the elements in the {@code Map}
   * @return A {@code Function} that returns the size of a {@code Map}
   */
  public static <K, V> ToIntFunction<Map<K, V>> mapSize() {
    return Map::size;
  }

  static {
    tmp.put(mapSize(), "size");
  }

  /**
   * A {@code Function} that returns the size of a {@code List}. Equivalent to {@code List::size}.
   * Can be used in case using the {@link #size()} causes a name clash.
   *
   * @param <T> The type of the elements in the {@code List}
   * @return A {@code Function} that returns the size of a {@code List}
   */
  public static <T> ToIntFunction<List<T>> listSize() {
    return List::size;
  }

  static {
    tmp.put(listSize(), "size");
  }

  /**
   * A {@code Function} that returns the size of a {@code Set}. Equivalent to {@code Set::size}. Can
   * be used in case using the {@link #size()} causes a name clash.
   *
   * @param <T> The type of the elements in the {@code Set}
   * @return A {@code Function} that returns the size of a {@code Set}
   */
  public static <T> ToIntFunction<Set<T>> setSize() {
    return Set::size;
  }

  static String getGetterName(Object getter) {
    return names.getOrDefault(getter, getter.toString());
  }

  static {
    tmp.put(setSize(), "size");
  }

  static {
    names = new IdentityHashMap<>(tmp.size());
    names.putAll(tmp);
  }
}
