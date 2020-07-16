package nl.naturalis.common;

import java.lang.reflect.Array;
import java.util.*;
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
   * Returns whether or not the provided object is empty. Returns <code>true</code> if:
   * <ul>
   * <li>is null
   * <li><b>or</b> an empty <code>String</code>
   * <li><b>or</b> an empty <code>Collection</code>
   * <li><b>or</b> an empty <code>Map</code>
   * <li><b>or</b> a zero-length array
   * <li><b>or</b> a zero-size {@link Sizeable}
   * <li><b>or</b> an empty {@link Emptyable}
   * </ul>
   * 
   * @param obj
   * @return
   */
  @SuppressWarnings("rawtypes")
  public static boolean isEmpty(Object obj) {
    return obj == null ||
        obj instanceof String && ((String) obj).isEmpty() ||
        obj instanceof Collection && ((Collection) obj).isEmpty() ||
        obj instanceof Map && ((Map) obj).isEmpty() ||
        obj.getClass().isArray() && Array.getLength(obj) == 0 ||
        obj instanceof Sizeable && ((Sizeable) obj).size() == 0 ||
        obj instanceof Emptyable && ((Emptyable) obj).isEmpty();
  }

  /**
   * Returns <code>null</code> if the provided object is empty as per {@link #isEmpty(Object)
   * isEmpty}, else the provided object.
   * 
   * @param <T>
   * @param obj
   * @return
   */
  public static <T> T emptyToNull(T obj) {
    return isEmpty(obj) ? null : obj;
  }

  /**
   * <p>
   * Tests the provided objects for equality using empty-equals-null semantics.
   * </p>
   * <p>
   * The following applies:
   * <ul>
   * <li><code>null</code> equals an empty <code>String</code>
   * <li><code>null</code> equals an empty <code>List</code>
   * <li><code>null</code> equals an empty <code>Set</code>
   * <li><code>null</code> equals an empty <code>Map</code>
   * <li><code>null</code> equals a zero-length array
   * <li>An empty <code>String</code>, <code>List</code>, <code>Set</code>, <code>Map</code> and array
   * are <b>not</b> equal to each other
   * <li>Equality for non-empty arrays, lists and sets is determined recursively
   * <li>Maps are equal if they are the same size and they have the same key-value pairs. Only the
   * values are compared using this method. Keys must be equal in the strict sense
   * <li>For any other combination of objects this method the result of <code>Objects.deepEquals<code>
   * </ul>
   * </p>
   * 
   * @see #isEmpty(Object)
   * @see StringMethods#isEmpty(Object)
   * @see ArrayMethods#isEmpty(Object)
   * @see CollectionMethods#isEmpty(Collection)
   * 
   * @param obj1
   * @param obj2
   * @return
   */
  @SuppressWarnings("rawtypes")
  public static <T> boolean equals(T obj1, T obj2) {
    if (obj1 == obj2) {
      return true;
    } else if (!isEmpty(obj1)) {
      if (!isEmpty(obj2)) {
        if (obj1 instanceof Object[] && obj2 instanceof Object[]) {
          return arraysEqual((Object[]) obj1, (Object[]) obj2);
        } else if (obj1 instanceof List && obj2 instanceof List) {
          return listsEqual((List) obj1, (List) obj2);
        } else if (obj1 instanceof Set && obj2 instanceof Set) {
          return setsEqual((Set) obj1, (Set) obj2);
        } else if (obj1 instanceof Map && obj2 instanceof Map) {
          return mapsEqual((Map) obj1, (Map) obj2);
        }
        return Objects.deepEquals(obj1, obj2);
      }
    } else if (isEmpty(obj2)) {
      return canCompare(obj1, obj2);
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

  private static boolean arraysEqual(Object[] objs1, Object[] objs2) {
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
  private static boolean listsEqual(List list1, List list2) {
    if (list1.size() == list2.size()) {
      Iterator it1 = list1.iterator();
      Iterator it2 = list2.iterator();
      while (it1.hasNext()) {
        if (!it2.hasNext() || !it1.next().equals(it2.next())) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static boolean setsEqual(Set set1, Set set2) {
    Comparator cmp = (e1, e2) -> {
      if (equals(e1, e2)) {
        return 0;
      } else if (e1 == null) {
        return -e2.hashCode();
      } else if (e2 == null) {
        return e1.hashCode();
      }
      return e1.hashCode() - e2.hashCode();
    };
    TreeSet s1 = new TreeSet<>(cmp);
    TreeSet s2 = new TreeSet<>(cmp);
    set1.forEach(s1::add);
    set2.forEach(s2::add);
    return s1.equals(s2);
  }

  @SuppressWarnings({"rawtypes"})
  private static boolean mapsEqual(Map map1, Map map2) {
    if (map1.size() == map2.size()) {
      for (Object k : map1.keySet()) {
        if (!map2.containsKey(k) || !equals(map1.get(k), map2.get(k))) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean canCompare(Object obj1, Object obj2) {
    return obj1 == null
        || obj2 == null
        || obj1.getClass() == obj2.getClass() // Covers empty strings and zero-length primitive arrays
        || (obj1 instanceof List && obj2 instanceof List)
        || (obj1 instanceof Set && obj2 instanceof Set)
        || (obj1 instanceof Map && obj2 instanceof Map)
        || (obj1 instanceof Object[] && obj2 instanceof Object[]);
  }

}
