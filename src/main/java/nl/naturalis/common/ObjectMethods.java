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
@SuppressWarnings("rawtypes")
public class ObjectMethods {

  private ObjectMethods() {}

  /**
   * Returns whether or not the provided object is empty. Returns {@code true} if
   * the object is
   * <ul>
   * <li>{@code null}
   * <li>an empty <code>String</code>
   * <li>an empty <code>Collection</code>
   * <li>an empty <code>Map</code>
   * <li>a zero-length array
   * <li>a zero-size {@link Sizeable}
   * <li>an empty {@link Emptyable}
   * </ul>
   * 
   * @param obj The object being tested
   * @return Whether or not it is empty
   */
  public static boolean isEmpty(Object obj) {
    return obj == null
        || obj instanceof String && ((String) obj).isEmpty()
        || obj instanceof Collection && ((Collection) obj).isEmpty()
        || obj instanceof Map && ((Map) obj).isEmpty()
        || obj.getClass().isArray() && Array.getLength(obj) == 0
        || obj instanceof Sizeable && ((Sizeable) obj).size() == 0
        || obj instanceof Emptyable && ((Emptyable) obj).isEmpty();
  }

  /**
   * Returns the reverse of {@link #isEmpty(Object) isEmpty}.
   * 
   * @param obj The object being tested
   * @return Whether or not it is empty
   */
  public static boolean notEmpty(Object obj) {
    return !isEmpty(obj);
  }

  /**
   * Returns {@code true} if the provided object is not null and, if it is an
   * array or collection, none of its elements are null. Otherwise this method
   * returns {@code false}.
   * 
   * @param obj The object being tested
   * @return Whether or not it is not null recursively
   */
  public static boolean notNullRecursive(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof Object[]) {
      return Arrays.stream((Object[]) obj).allMatch(ObjectMethods::notNullRecursive);
    } else if (obj instanceof Collection) {
      return ((Collection) obj).stream().allMatch(ObjectMethods::notNullRecursive);
    }
    return true;
  }

  /**
   * Returns {@code true} if the provided object is not empty and, if it is an
   * array or collection, none of its elements are empty. Otherwise this method
   * returns {@code false}.
   * 
   * @param obj The object being tested
   * @return Whether or not it is not empty recursively
   */
  public static boolean notEmptyRecursive(Object obj) {
    if (isEmpty(obj)) {
      return false;
    } else if (obj instanceof Object[]) {
      return Arrays.stream((Object[]) obj).allMatch(ObjectMethods::notEmptyRecursive);
    } else if (obj instanceof Collection) {
      return ((Collection) obj).stream().allMatch(ObjectMethods::notEmptyRecursive);
    }
    return true;
  }

  /**
   * Returns {@code null} if the argument is {@link #isEmpty(Object) empty}, else
   * the argument itself.
   * 
   * @param <T>
   * @param obj
   * @return
   */
  public static <T> T emptyToNull(T obj) {
    return isEmpty(obj) ? null : obj;
  }

  /**
   * Tests the provided objects for equality using <i>empty-equals-null</i>
   * semantics. This is more or less equivalent to:
   * 
   * <pre>
   * Objects.equals(emptyToNull(obj1), emptyToNull(obj2))
   * </pre>
   * 
   * The following applies:
   * <ol>
   * <li>{@code null} equals an empty <code>String</code>
   * <li>{@code null} equals an empty <code>List</code>
   * <li>{@code null} equals an empty <code>Set</code>
   * <li>{@code null} equals an empty <code>Map</code>
   * <li>{@code null} equals a zero-length array
   * <li>{@code null} equals an empty {@link Emptyable}
   * <li>{@code null} equals a zero-size {@link Sizeable}
   * <li>An empty/zero-length <code>String</code>, <code>List</code>,
   * <code>Set</code>, <code>Map</code>, array, <code>Emptyable</code> and
   * <code>Sizeable</code> are <b>not</b> equal to each other
   * <li>For any other pair of objects this method returns the result of
   * <code>Objects.equals<code>
   * </ol>
   * 
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
   * Recursively tests the provided objects for equality using
   * <i>empty-equals-null</i> semantics. In other words, if the provided arguments
   * are non-empty arrays, lists or sets, their elements are also compared using
   * {@code e2nEqualsRecursive}. If the provided arguments are non-empty maps,
   * then keys are compared normally (using {@code equals}) while values are
   * compared using {@code e2nEqualsRecursive}.
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
   * Generates a hash code for the provided object using using
   * <i>empty-equals-null</i> semantics. That is: null and {@link #isEmpty(Object)
   * ObjectMethods.isEmpty empty objects}) all have hash code zero. The hash code
   * is not calculated recursively for arrays, collections and maps. Therefore
   * maps and sets relying on <i>empty-equals-null</i> semantics to find keys and
   * elements will likely have to fall back more often on <code>e2nEquals</code>
   * or {@code e2nEqualsRecursive}.
   * 
   * @param obj
   * @return
   */
  public static int e2nHashCode(Object obj) {
    return isEmpty(obj) ? 0 : obj.hashCode();
  }

  /**
   * Generates a hash code for the provided arguments using using
   * <i>empty-equals-null</i> semantics. See {@link #hashCode()}.
   * 
   * @param objs
   * @return
   */
  public static int e2nHash(Object... objs) {
    if (objs == null || objs.length == 0) {
      return 0;
    }
    int hash = 0;
    for (Object obj : objs) {
      hash = hash * 31 + e2nHashCode(obj);
    }
    return hash;
  }

  /**
   * Returns {@code dfault} if {@code value} is {@code null}, else {@code value}.
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
   * Returns value supplied by the {@code Supplier} if {@code value} is
   * {@code null}, else {@code value}.
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
   * Applies the provided transformation to {@code value} if it is not
   * {@code null}, else returns {@code null}.
   * 
   * @param <T> The type of the first argument
   * @param <U> The return type
   * @param value The value to check
   * @param then The transformation to apply to the value if it is not null
   * @return
   */
  public static <T, U> U ifNotNull(T value, Function<T, U> then) {
    return value != null ? then.apply(value) : null;
  }

  /**
   * Applies the provided transformation to {@code value} if it is not
   * {@code null}, else returns {@code dfault}.
   * 
   * @param <T> The type of the first argument
   * @param <U> The return type
   * @param value The value to check
   * @param then The transformation to apply to the value if it is not null
   * @param dfault The value to return in case the provided value is null
   * @return
   */
  public static <T, U> U ifNotNull(T value, Function<T, U> then, U dfault) {
    return value != null ? then.apply(value) : dfault;
  }

  /**
   * Applies the provided transformation to {@code value} if it is not
   * {@code null}, else returns the value supplied by the {@code Supplier}.
   * 
   * @param <T> The type of the first argument
   * @param <U> The return type
   * @param value The value to check
   * @param then The transformation to apply to the value if it is not null
   * @param otherwise A supplier providing the default value
   * @return
   */
  public static <T, U> U ifNotNull(T value, Function<T, U> then, Supplier<U> otherwise) {
    return value != null ? then.apply(value) : otherwise.get();
  }

  /**
   * Returns {@code dfault} if {@code value} is empty, else {@code value}.
   * 
   * @see StringMethods#ifEmpty(String, String)
   * @see StringMethods#ifBlank(Object, String)
   * 
   * @param <T>
   * @param value
   * @param dfault
   * @return
   */
  public static <T> T ifEmpty(T value, T dfault) {
    return isEmpty(value) ? dfault : value;
  }

  /**
   * Returns value provided by the {@code Supplier} if {@code value} is empty,
   * else {@code value}.
   * 
   * @param <T>
   * @param value The value to check and return if not null
   * @param then The <code>Supplier</code> supplying the value if null
   * @return
   */
  public static <T> T ifEmpty(T value, Supplier<T> then) {
    return isEmpty(value) ? then.get() : value;
  }

  /**
   * Applies the provided transformation to {@code value} if it is not empty, else
   * returns {@code null}.
   * 
   * @param <T> The type of the first argument
   * @param <U> The return type
   * @param value The value to check
   * @param then The transformation to apply to the value if it is not null
   * @return
   */
  public static <T, U> U ifNotEmpty(T value, Function<T, U> then) {
    return notEmpty(value) ? then.apply(value) : null;
  }

  /**
   * Applies the provided transformation to {@code value} if it is not empty else
   * returns {@code dfault}.
   * 
   * @param <T> The type of the first argument
   * @param <U> The return type
   * @param value The value to check
   * @param then The transformation to apply to the value if it is not null
   * @param dfault The value to return in case the provided value is null
   * @return
   */
  public static <T, U> U ifNotEmpty(T value, Function<T, U> then, U dfault) {
    return notEmpty(value) ? then.apply(value) : dfault;
  }

  /**
   * Applies the provided transformation to {@code value} if it is not empty else
   * returns value supplied by the {@code Supplier}.
   * 
   * @param <T> The type of the first argument
   * @param <U> The return type
   * @param value The value to check
   * @param then The transformation to apply to the value if it is not null
   * @param otherwise A supplier providing the default value
   * @return
   */
  public static <T, U> U ifNotEmpty(T value, Function<T, U> then, Supplier<U> otherwise) {
    return notEmpty(value) ? then.apply(value) : otherwise.get();
  }

  /**
   * Applies the provided transformation to {@code value} if the condition
   * evaluates to {@code true}, else returns {@code value} unaltered. For example:
   *
   * <pre>
   * String s = ifTrue(ignoreCase, String::toLowerCase, name);
   * </pre>
   * 
   * @param <T> The return type
   * 
   * @param condition The condition to evaluate
   * @param then The transformation to apply if the condition evaluates to
   *        {@code true}
   * @param value The value value to return or to apply the transformation to
   * @return
   */
  public static <T> T ifTrue(boolean condition, UnaryOperator<T> then, T value) {
    return condition ? then.apply(value) : value;
  }

  /**
   * Applies the provided transformation to {@code value} if the condition
   * evaluates to {@code false}, else returns {@code value} unaltered.
   * 
   * @param <T> The return type
   * @param condition The condition to evaluate
   * @param then The transformation to apply if the condition evaluates to
   *        {@code false}
   * @param value The value value to return or to apply the transformation to
   * @return
   */
  public static <T> T ifFalse(boolean condition, UnaryOperator<T> then, T value) {
    return !condition ? then.apply(value) : value;
  }

  private static boolean eq(Object obj1, Object obj2) {
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
    return false;
  }

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
          s2.remove(obj1);
          break;
        }
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }

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
    return empty1 == null // can always compare null to any other type of empty object
        || empty2 == null
        || empty1.getClass() == empty2.getClass()
        || empty1 instanceof List && empty2 instanceof List
        || empty1 instanceof Set && empty2 instanceof Set
        || empty1 instanceof Map && empty2 instanceof Map
        || empty1 instanceof Object[] && empty2 instanceof Object[];
  }

}
