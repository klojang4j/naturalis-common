package nl.naturalis.common;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import static java.util.stream.Collectors.toSet;

/**
 * General methods applicable to objects of any type.
 *
 * @author Ayco Holleman
 */
public class ObjectMethods {

  private ObjectMethods() {}

  /**
   * Returns whether or not the provided object is empty. Returns <code>true</code> if ths object:
   * <ul>
   * <li>is <code>null</code>
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
   * Returns the reverse of {@link #isEmpty(Object) isEmpty}.
   * 
   * @param obj
   * @return
   */
  public static boolean notEmpty(Object obj) {
    return !isEmpty(obj);
  }

  /**
   * Returns <code>null</code> if the argument is empty, else the argument itself.
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
   * Tests the provided objects for equality using empty-equals-null semantics. The following applies:
   * </p>
   * <p>
   * <ol>
   * <li><code>null</code> equals an empty <code>String</code>
   * <li><code>null</code> equals an empty <code>List</code>
   * <li><code>null</code> equals an empty <code>Set</code>
   * <li><code>null</code> equals an empty <code>Map</code>
   * <li><code>null</code> equals a zero-length array
   * <li><code>null</code> equals an empty {@link Emptyable}
   * <li><code>null</code> equals a zero-size {@link Sizeable}
   * <li>An empty/zero-size <code>String</code>, <code>List</code>, <code>Set</code>,
   * <code>Map</code>, array, <code>Emptyable</code> and <code>Sizeable</code> are <b>not</b> equal to
   * each other
   * <li>For any other combination of objects this method returns the result of
   * <code>Objects.equals<code>
   * </ol>
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
  public static <T> boolean e2nEquals(T obj1, T obj2) {
    if (obj1 == obj2) {
      return true;
    } else if (isEmpty(obj1)) {
      return isEmpty(obj2) ? canCompare(obj1, obj2) : false;
    }
    return isEmpty(obj2) ? false : Objects.equals(obj1, obj2);
  }

  /**
   * Recursively tests the provided objects for equality using empty-equals-null semantics. In other
   * words, for non-empty arrays, lists and sets the elements within them are also compared using
   * <code>e2nEqualsRecursive</code>. For non-empty maps only the values are compared using
   * <code>e2nEqualsRecursive</code>; the keys are compared normally (using their <code>equals</code>
   * method.
   * 
   * @param obj1
   * @param obj2
   * @return
   */
  public static <T> boolean e2nEqualsRecursive(T obj1, T obj2) {
    if (obj1 == obj2) {
      return true;
    } else if (isEmpty(obj1)) {
      return isEmpty(obj2) ? canCompare(obj1, obj2) : false;
    }
    return isEmpty(obj2) ? false : eq(obj1, obj2);
  }

  /**
   * Generates a hash code for the provided object using using empty-equals-null semantics. That is:
   * null and empty objects (as per {@link #isEmpty(Object) isEmpty}) all have a zero hash code. In
   * other words, they all have the same hash code! The hash code is also not calculated recursively
   * for arrays, list and sets. Therefore maps and sets relying on this method to find keys and
   * elements will likely have to fall back more often on <code>e2nEquals</code> or
   * <code>e2nEqualsRecursive</code>.
   * 
   * @see StringMethods#isEmpty(Object)
   * @see ArrayMethods#isEmpty(Object)
   * @see CollectionMethods#isEmpty(Collection)
   * 
   * @param objs
   * @return
   */
  @SuppressWarnings("rawtypes")
  public static int e2nHashCode(Object obj) {
    if (obj == null
        || obj instanceof String && ((String) obj).isEmpty()
        || obj instanceof Collection && ((Collection) obj).isEmpty()
        || obj instanceof Map && ((Map) obj).isEmpty()
        || obj.getClass().isArray() && Array.getLength(obj) == 0
        || obj instanceof Emptyable && ((Emptyable) obj).isEmpty()
        || obj instanceof Sizeable && ((Sizeable) obj).size() == 0) {
      return 0;
    }
    return obj.hashCode();
  }

  /**
   * Generates a hash code for the provided arguments using using empty-equals-null semantics. See
   * {@link #hashCode()}.
   * 
   * @see StringMethods#isEmpty(Object)
   * @see ArrayMethods#isEmpty(Object)
   * @see CollectionMethods#isEmpty(Collection)
   * 
   * @param objs
   * @return
   */
  public static int e2nHash(Object... objs) {
    if (objs == null || objs.length == 0) {
      return 0;
    }
    int hash = 17;
    for (Object obj : objs) {
      hash = hash * 31 + e2nHashCode(obj);
    }
    return hash;
  }

  /**
   * Returns the 1st argument if it is not <code>null</code>, else the 2nd argument.
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
   * Returns the 1st argument if it is not <code>null</code>, else the value supplied by the
   * <code>Supplier</code>.
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
   * Returns <code>null</code> if the 1st argument is <code>null</code>, else the result of applying
   * the provided <code>Function</code> to the 1st argument.
   * 
   * @param <T> The type of the first argument
   * @param <U> The return type
   * @param value The value to check
   * @param then The transformation to apply to the value if it is not null
   * @return
   */
  public static <T, U> U ifNotNull(T value, Function<T, U> then) {
    return value == null ? null : then.apply(value);
  }

  /**
   * Returns the 3rd argument if the 1st argument is <code>null</code>, else the result of applying
   * the provided <code>Function</code> to the 1st argument.
   * 
   * @param <T> The type of the first argument
   * @param <U> The return type
   * @param value The value to check
   * @param then The transformation to apply to the value if it is not null
   * @param dfault The value to return in case the provided value is null
   * @return
   */
  public static <T, U> U ifNotNull(T value, Function<T, U> then, U dfault) {
    return value == null ? dfault : then.apply(value);
  }

  /**
   * Returns the 3rd argument if the condition evaluates to <code>true</code>, else the result of
   * applying the provided operation on the 3rd argument.
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
   * Returns the 3rd argument if the condition evaluates to <code>false</code>, else the result of
   * applying the provided operation on the 3rd argument.
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

  @SuppressWarnings("rawtypes")
  private static <T> boolean eq(T obj1, T obj2) {
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

  private static boolean arraysEqual(Object[] objs1, Object[] objs2) {
    if (objs1.length == objs2.length) {
      for (int i = 0; i < objs1.length; ++i) {
        if (!e2nEqualsRecursive(objs1[i], objs2[i])) {
          return false;
        }
      }
      return true;
    }
    return true;
  }

  @SuppressWarnings("rawtypes")
  private static boolean listsEqual(List list1, List list2) {
    if (list1.size() == list2.size()) {
      Iterator it1 = list1.iterator();
      Iterator it2 = list2.iterator();
      while (it1.hasNext()) {
        if (!it2.hasNext() || !e2nEqualsRecursive(it1.next(), it2.next())) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  @SuppressWarnings({"rawtypes"})
  private static boolean setsEqual(Set set1, Set set2) {
    Set s1 = (Set) set1.stream().map(ObjectMethods::emptyToNull).collect(toSet());
    Set s2 = (Set) set2.stream().map(ObjectMethods::emptyToNull).collect(toSet());
    if (s1.size() != s2.size()) {
      return false;
    } else if (s1.equals(s2)) {
      return true;
    }
    for (Object obj1 : s1) {
      boolean found = false;
      for (Object obj2 : s2) {
        if (e2nEqualsRecursive(obj1, obj2)) {
          found = true;
          break;
        }
      }
      if (!found) {
        return false;
      }
      s2.remove(obj1);
    }
    return true;
  }

  @SuppressWarnings({"rawtypes"})
  private static boolean mapsEqual(Map map1, Map map2) {
    if (map1.size() == map2.size()) {
      for (Object k : map1.keySet()) {
        if (!map2.containsKey(k) || !e2nEqualsRecursive(map1.get(k), map2.get(k))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  private static boolean canCompare(Object empty1, Object empty2) {
    return empty1 == null // can always compare null to any type of empty object
        || empty2 == null
        || empty1.getClass() == empty2.getClass()
        || empty1 instanceof List && empty2 instanceof List
        || empty1 instanceof Set && empty2 instanceof Set
        || empty1 instanceof Map && empty2 instanceof Map
        || empty1 instanceof Object[] && empty2 instanceof Object[];
  }

}
