package nl.naturalis.common;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.ClassMethods.isPrimitiveArray;

/**
 * General methods applicable to objects of any type.
 *
 * @author Ayco Holleman
 */
public class ObjectMethods {

  private ObjectMethods() {}

  /**
   * Returns whether or not the provided object
   * <ul>
   * <li>is null
   * <li><b>or</b> an empty <code>Collection</code>
   * <li><b>or</b> a zero-size {@link Sizeable}
   * <li><b>or</b> an empty {@link Emptyable}
   * <li><b>or</b> a zero-length array
   * </ul>
   * 
   * @param obj
   * @return
   */
  @SuppressWarnings("rawtypes")
  public static boolean isEmpty(Object obj) {
    return obj == null ||
        obj instanceof CharSequence && ((CharSequence) obj).length() == 0 ||
        obj instanceof Collection && ((Collection) obj).isEmpty() ||
        obj instanceof Sizeable && ((Sizeable) obj).size() == 0 ||
        obj instanceof Emptyable && ((Emptyable) obj).isEmpty() ||
        obj.getClass().isArray() && Array.getLength(obj) == 0;
  }

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
  public static <T> boolean equals(T obj1, T obj2) {
    if (obj1 == obj2) {
      return true;
    } else if (!isEmpty(obj1)) {
      if (!isEmpty(obj2)) {
        if (!isA(obj1.getClass(), obj2.getClass()) && !isA(obj2.getClass(), obj1.getClass())) {
          return false;
        } else if (isPrimitiveArray(obj1)) {
          return Objects.deepEquals(obj1, obj2);
        } else if (obj1 instanceof Object[]) {
          return arraysEqual(obj1, obj2);
        } else if (obj1 instanceof Collection) {
          return collectionsEqual(obj1, obj2);
        }
      }
    } else if (isEmpty(obj2)) {
      return true;
    }
    return false;
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
   * Executes the specified <code>Runnable</code> if the specified condition is true.
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
   * Returns <code>value</code> if the provided condition is true, else the outcome of the provided
   * operation on <code>value</code>.
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
   * Returns <code>value</code> if the provided condition is false, else the outcome of the provided
   * operation on <code>value</code>.
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

  private static <T> boolean arraysEqual(T obj1, T obj2) {
    Object[] objs1 = (Object[]) obj1;
    Object[] objs2 = (Object[]) obj2;
    if (objs1.length == objs2.length) {
      for (int i = 0; i < objs1.length; ++i) {
        if (!equals(objs1[i], objs2[i])) {
          return false;
        }
      }
    }
    return true;
  }

  @SuppressWarnings("rawtypes")
  private static <T> boolean collectionsEqual(T obj1, T obj2) {
    Collection c1 = (Collection) obj1;
    Collection c2 = (Collection) obj2;
    if (c1.equals(c2)) {
      return true;
    } else if (c1.size() == c2.size()) {
      // We'll rely on the iterators, but these may not yield elements in the same order!
      Iterator it1 = c1.iterator();
      Iterator it2 = c2.iterator();
      while (it1.hasNext()) {
        if (!it2.hasNext()) { // just in case the iterator doesn't even yield all elements
          return false;
        } else if (!equals(it1, it2)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

}
