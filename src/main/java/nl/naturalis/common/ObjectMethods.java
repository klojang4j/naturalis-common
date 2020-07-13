package nl.naturalis.common;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * General methods applicable to objects of any type.
 *
 * @author Ayco Holleman
 */
public class ObjectMethods {

  private ObjectMethods() {}

  /**
   * Tests the provided objects for equality using empty-equals-null semantics. If one of the
   * arguments is null and the other is an empty <code>String</code>, array or
   * <code>Collection</code>, this method returns <code>true</code>. Otherwise this method delegates
   * to {@link Objects#deepEquals(Object, Object) Objects.deepEquals}. This method does not
   * recursively test the elements of arrays and collections: an array with an empty
   * <code>String</code> as the 1st element is not equal to an array with null as the 1st element.
   * Also, an empty array will not be equal to an empty <code>String</code> or an empty
   * <code>Collection</code>.
   * 
   * @see StringMethods#isEmpty(Object)
   * @see ArrayMethods#isEmpty(Object)
   * @see CollectionMethods#isEmpty(Collection)
   * 
   * @param obj1
   * @param obj2
   * @return
   */
  @SuppressWarnings("rawtypes")
  public static boolean equals(Object obj1, Object obj2) {
    if (obj1 == obj2) {
      return true;
    }
    if (obj1 == null) {
      if (obj2 instanceof String && ((String) obj2).isEmpty() ||
          obj2 instanceof Collection && ((Collection) obj2).isEmpty() ||
          obj2.getClass().isArray() && Array.getLength(obj2) == 0) {
        return true;
      }
    } else if (obj2 == null) {
      if (obj1 instanceof String && ((String) obj1).isEmpty() ||
          obj1 instanceof Collection && ((Collection) obj1).isEmpty() ||
          obj1.getClass().isArray() && Array.getLength(obj1) == 0) {
        return true;
      }
    }
    return Objects.deepEquals(obj1, obj2);
  }

  /**
   * Generates a hash code for the provided object using using empty-equals-null semantics. That is:
   * null, empty strings, arrays and collections all return 0 (in other words, they all have the same
   * hash code!). The hash code of non-empty arrays will be calculated using
   * {@link Arrays#deepHashCode(Object[])}. For any other type of object this method simply calls
   * hashCode() on it.
   * 
   * @see StringMethods#isEmpty(Object)
   * @see ArrayMethods#isEmpty(Object)
   * @see CollectionMethods#isEmpty(Collection)
   * 
   * @param objs
   * @return
   */
  @SuppressWarnings("rawtypes")
  public static int hashCode(Object obj) {
    if (obj == null ||
        obj instanceof String && ((String) obj).isEmpty() ||
        obj instanceof Collection && ((Collection) obj).isEmpty() ||
        obj.getClass().isArray() && Array.getLength(obj) == 0) {
      return 0;
    }
    return obj.hashCode();
  }

  /**
   * Generates a hash code for the provided array of object using using empty-equals-null semantics.
   * See {@link #hashCode()}.
   * 
   * @see StringMethods#isEmpty(Object)
   * @see ArrayMethods#isEmpty(Object)
   * @see CollectionMethods#isEmpty(Collection)
   * 
   * @param objs
   * @return
   */
  public static int hash(Object... objs) {
    if (objs == null || objs.length == 0) {
      return 0;
    }
    int hash = 17;
    for (Object obj : objs) {
      hash = hash * 31 + hashCode(obj);
    }
    return hash;
  }

  /**
   * Returns the first non-null argument or null if all arguments are null.
   * 
   * @param <T>
   * @param arg0
   * @param arg1
   * @param moreArgs
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T firstNonNull(T arg0, T arg1, T... moreArgs) {
    if (arg0 != null) {
      return arg0;
    } else if (arg1 != null) {
      return arg1;
    } else {
      for (T arg : moreArgs) {
        if (arg != null) {
          return arg;
        }
      }
    }
    return null;
  }

  /**
   * Returns the 1st argument if it is not null, else the 2nd argument.
   * 
   * @see StringMethods#ifEmpty(String, String)
   * @see StringMethods#ifBlank(Object, String)
   * 
   * @param <T>
   * @param value
   * @param dfault
   * @return
   */
  public static <T> T ifNull(T value, T dfault) {
    return value == null ? dfault : value;
  }

  /**
   * Returns the 1st argument if it is not null, else the value supplied by the <code>Supplier</code>.
   * 
   * @param <T>
   * @param value The value to check and return if not null
   * @param then The <code>Supplier</code> supplying the value if null
   * @return
   */
  public static <T> T ifNull(T value, Supplier<T> then) {
    return value == null ? then.get() : value;
  }

  /**
   * Converts the 1st argument into an instance of type <code>U</code> if it is not null.
   * 
   * @param <T> The type of the first argument
   * @param <U> The return type
   * @param value The value to check
   * @param then The function to call if value is not null
   * @return The result of apply the function to the value
   */
  public static <T, U> U ifNotNull(T value, Function<T, U> then) {
    return value == null ? null : then.apply(value);
  }

  /**
   * Processes the 1st argument using the provided <code>Consumer</code> if it is not null.
   * 
   * @param <T>
   * @param value
   * @param then
   */
  public static <T> void doIfNotNull(T value, Consumer<T> then) {
    if (value != null) {
      then.accept(value);
    }
  }

  /**
   * Executes the specified <code>Runnable</code> if the specified condition evaluates to
   * <code>true</code>.
   * 
   * @param condition
   * @param then
   */
  public static void doIf(boolean condition, Runnable then) {
    if (condition) {
      then.run();
    }
  }

  /**
   * Applies the provided operation on <code>value</code> if the condition equals <code>true</code>,
   * else returns <code>value</code>.
   * 
   * @param <T>
   * @param condition
   * @param then
   * @param value
   * @return
   */
  public static <T> T ifTrue(boolean condition, UnaryOperator<T> then, T value) {
    return condition ? then.apply(value) : value;
  }

  /**
   * Applies the provided operation on <code>value</code> if the condition equals <code>false</code>,
   * else returns <code>value</code>.
   * 
   * @param <T>
   * @param condition
   * @param then
   * @param value
   * @return
   */
  public static <T> T ifFalse(boolean condition, UnaryOperator<T> then, T value) {
    return !condition ? then.apply(value) : value;
  }

}
