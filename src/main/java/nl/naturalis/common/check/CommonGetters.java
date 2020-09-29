package nl.naturalis.common.check;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * Defines various commonly used getter-type (no-arg) methods. They can be passed as the first
 * argument to the appropriate {@code and(...)} methods in the {@link Check} class. Each getter is
 * associated with the name of the property it exposes, so you can choose the leanest of {@code
 * and()} methods (those that don't let you specify a property name yourself). For example:
 *
 * <p>
 *
 * <pre>
 * Check.notNull(stampCollection, "stampCollection").and(size(), greaterThan(), 100);
 * // "stampCollection.size must be > 100 (was 22)"
 * </pre>
 *
 * <p>Most of the getters defined here are plain, unadorned method references. <b>None of them do a
 * null-check on the argument.</b> They rely upon being embedded within in chain of checks on a
 * {@link Check} object, the first of which should be a <i>not-null</i> check.
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
   * Collection::size}. See also {@link #listSize()} and {@link #setSize()}.
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
   * @param <K> The key type
   * @param <V> The value typr
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
   * Note that the compiler will force you to match the declared type of the argument to the
   * argument of the getter function:
   *
   * <p>
   *
   * <pre>
   * Collection&lt;String&gt; c = List.of("Hello", "World");
   * Check.notNull(c, "c").and(size(), atMost(), 2);
   * List&lt;String&gt; l = List.of("Hello", "World");
   * Check.notNull(l, "l").and(listSize(), atMost(), 2); // size() wouldn't work here!
   * </pre>
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
   * A {@code Function} that returns the size of a {@code Set}. Equivalent to {@code Set::size}.
   * Note that the compiler will force you to match the declared type of the argument to the
   * argument of the getter function:
   *
   * <p>
   *
   * <pre>
   * Collection&lt;String&gt; c = List.of("Hello", "World");
   * Check.notNull(c, "c").and(size(), atMost(), 2);
   * Set&lt;String&gt; s = Set.of("Hello", "World");
   * Check.notNull(s, "s").and(listSize(), atMost(), 2); // size() wouldn't work here!
   * </pre>
   *
   * @param <T> The type of the elements in the {@code Set}
   * @return A {@code Function} that returns the size of a {@code Set}
   */
  public static <T> ToIntFunction<Set<T>> setSize() {
    return Set::size;
  }

  static {
    tmp.put(setSize(), "size");
  }

  static String getGetterName(Object getter) {
    return names.getOrDefault(getter, "?");
  }

  static {
    names = new IdentityHashMap<>(tmp.size());
    names.putAll(tmp);
  }
}
