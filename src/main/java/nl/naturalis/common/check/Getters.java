package nl.naturalis.common.check;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * Lists some commonly used getter-type (no-arg) methods in the form of method references. When used
 * in combination with the {@link Check} class these would be called on the argument in order to
 * check the value of one of its properties. Most of the getters defined here are plain, unadorned
 * method references and <i>none do a null-check on the argument.</i> This is sypposed to have
 * already been done (e.g. using {@link Checks#notNull() Checks.notNull}).
 *
 * @author Ayco Holleman
 */
public class Getters {

  private Getters() {}

  /**
   * Returns all enum constants of an {@code Enum} class.
   *
   * @param <T> The enum class
   * @return Its constants
   */
  public static <T extends Enum<T>> Function<Class<T>, T[]> enumConstants() {
    return x -> x.getEnumConstants();
  }

  /**
   * Returns all siblings of an enum constant, including that constant itself
   *
   * @param <T> The enum class
   * @return Its constants
   */
  @SuppressWarnings("unchecked")
  public static <T extends Enum<T>> Function<T, T[]> enumSiblings() {
    return x -> (T[]) x.getClass().getEnumConstants();
  }

  /**
   * Returns the size of a {@code Collection}.
   *
   * @param <T> The type of the {@code Collection}
   * @return Its size
   */
  public static <T extends Collection<?>> ToIntFunction<T> size() {
    return Collection::size;
  }

  /**
   * Returns the size of a {@code Map}.
   *
   * @param <T> The type of the {@code Map}
   * @return Its size
   */
  public static <T extends Map<?, ?>> ToIntFunction<T> mapSize() {
    return Map::size;
  }

  /**
   * Returns the size of a {@code String}.
   *
   * @param <T> The type of the {@code String}
   * @return Its length
   */
  public static ToIntFunction<Object> length() {
    return Array::getLength;
  }
}
