package nl.naturalis.common.check;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import nl.naturalis.common.Sizeable;

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
   * Returns the {@code Class} of an object.
   *
   * @param <T> The type of the object
   * @return Its class
   */
  @SuppressWarnings("unchecked")
  public static <T> Function<T, Class<T>> type() {
    return x -> (Class<T>) x.getClass();
  }

  /**
   * Returns the length of a {@link CharSequence} or array argument. For any other type of argument
   * this method throws an {@link UnsupportedOperationException}.
   *
   * @param <T> The type of the argument (must be either a {@code CharSequence} or an array)
   * @return Its length
   */
  public static <T> ToIntFunction<T> length() {
    return x -> {
      if (x instanceof CharSequence) {
        return ((CharSequence) x).length();
      } else if (x.getClass().isArray()) {
        return Array.getLength(x);
      }
      throw notApplicable("length", x);
    };
  }

  /**
   * Returns the size of a {@link Collection} ,{@link Map} or {@link Sizeable} argument. For any
   * other type of argument this method throws an {@link UnsupportedOperationException}.
   *
   * @param <T> The type of the {@code Collection}
   * @return Its size
   */
  @SuppressWarnings("rawtypes")
  public static <T> ToIntFunction<T> size() {
    return x -> {
      if (x instanceof Collection) {
        return ((Collection) x).size();
      } else if (x instanceof Map) {
        return ((Map) x).size();
      } else if (x instanceof Sizeable) {
        return ((Sizeable) x).size();
      }
      throw notApplicable("size", x);
    };
  }

  private static UnsupportedOperationException notApplicable(String getter, Object obj) {
    String fmt = "%s() not applicable to %s";
    String msg = String.format(fmt, getter, obj.getClass().getName());
    return new UnsupportedOperationException(msg);
  }
}
