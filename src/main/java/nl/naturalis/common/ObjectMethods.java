package nl.naturalis.common;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.*;
import static java.util.stream.Collectors.toSet;
import static nl.naturalis.common.ClassMethods.isPrimitiveArray;

/**
 * General methods applicable to objects of any type.
 *
 * @author Ayco Holleman
 */
@SuppressWarnings("rawtypes")
public class ObjectMethods {

  private ObjectMethods() {}

  /**
   * <p>
   * Returns whether or not the provided object is empty. Returns {@code true} if
   * the argument is:
   * <p>
   * <ul>
   * <li>{@code null}
   * <li>an empty <code>String</code>
   * <li>an empty <code>Collection</code>
   * <li>an empty <code>Map</code>
   * <li>a zero-length array
   * <li>a zero-size {@link Sizeable}
   * <li>an empty {@link Emptyable}
   * </ul>
   * <p>
   * Otherwise this method returns {@code false}.
   *
   * @param obj The object to be tested
   * @return Whether or not it is empty
   */
  public static boolean isEmpty(Object obj) {
    return obj == null
        || obj instanceof String && ((String) obj).isEmpty()
        || obj instanceof Collection && ((Collection) obj).isEmpty()
        || obj instanceof Map && ((Map) obj).isEmpty()
        || obj instanceof Object[] && ((Object[]) obj).length == 0
        || isPrimitiveArray(obj) && Array.getLength(obj) == 0
        || obj instanceof Sizeable && ((Sizeable) obj).size() == 0
        || obj instanceof Emptyable && ((Emptyable) obj).isEmpty();
  }

  /**
   * Returns the inverse of {@link #isEmpty(Object) isEmpty}.
   *
   * @param obj The object to be tested
   * @return Whether or not it is non-empty
   */
  public static boolean notEmpty(Object obj) {
    return !isEmpty(obj);
  }

  /**
   * <p>
   * Returns whether or not the argument is recursively non-empty. More
   * specifically, this method returns {@code true} if:
   * <p>
   * <ul>
   * <li>{@code obj} is a non-empty (non-primitive) array with only
   * {@code deepNotEmpty} elements
   * <li>{@code obj} is a non-empty {@code Collection} with only
   * {@code deepNotEmpty} elements
   * <li>{@code obj} is a non-empty {@code Map}, with only {@code deepNotEmpty}
   * values (NB map keys are not checked for empty-ness)
   * <li>{@code obj} is a non-empty object of any other type
   * </ul>
   * <p>
   * Otherwise this method returns {@code false}.
   *
   * @param obj The object to be tested
   * @return Whether or not it is recursively non-empty
   */
  public static boolean deepNotEmpty(Object obj) {
    return obj != null
        && (!(obj instanceof String) || ((String) obj).length() > 0)
        && (!(obj instanceof Collection) || ((Collection) obj).size() > 0
            && ((Collection) obj).stream().allMatch(ObjectMethods::deepNotEmpty))
        && (!(obj instanceof Map) || ((Map) obj).size() > 0
            && ((Map) obj).values().stream().allMatch(ObjectMethods::deepNotEmpty))
        && (!(obj instanceof Object[]) || ((Object[]) obj).length > 0
            && Arrays.stream((Object[]) obj).allMatch(ObjectMethods::deepNotEmpty))
        && (!isPrimitiveArray(obj) || Array.getLength(obj) > 0)
        && (!(obj instanceof Sizeable) || ((Sizeable) obj).size() > 0)
        && (!(obj instanceof Emptyable) || !((Emptyable) obj).isEmpty());
  }

  /**
   * <p>
   * Returns whether or not the argument is recursively non-null. More
   * specifically, this method returns {@code true} if:
   * <p>
   * <ul>
   * <li>{@code obj} is not null
   * <li>{@code obj} is an empty array or a non-empty array with only non-null
   * elements
   * <li>{@code obj} is an empty {@code Collection} or a non-empty
   * {@code Collection} with only non-null elements
   * <li>{@code obj} is an empty {@code Map} non-empty {@code Map} with only
   * non-null values (NB Map keys are not checked for empty-ness.)
   * </ul>
   * <p>
   * Otherwise this method returns {@code false}. Contrary to
   * {@link #deepNotEmpty(Object) deepNotEmpty}, this method returns {@code true}
   * for empty arrays, collections and maps. It only checks that the values they
   * do contain are non-null.
   *
   * @param obj The object to be tested
   * @return Whether or not it is recursively non-null
   */
  public static boolean deepNotNull(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof Object[]) {
      return Arrays.stream((Object[]) obj).allMatch(ObjectMethods::deepNotNull);
    } else if (obj instanceof Collection) {
      return ((Collection) obj).stream().allMatch(ObjectMethods::deepNotNull);
    } else if (obj instanceof Map) {
      return ((Map) obj).values().stream().allMatch(ObjectMethods::deepNotNull);
    }
    return true;
  }

  /**
   * Returns {@code null} if the argument is {@link #isEmpty(Object) empty}, else
   * the argument itself.
   *
   * @param <T> The type of the argument
   * @param obj The argument
   * @return The argument itself or {@code null}
   */
  public static <T> T emptyToNull(T obj) {
    return isEmpty(obj) ? null : obj;
  }

  /**
   * <p>
   * Tests the provided arguments for equality using <i>empty-equals-null</i>
   * semantics. This is more or less equivalent to:
   *
   * <pre>
   * Objects.equals(emptyToNull(obj1), emptyToNull(obj2))
   * </pre>
   *
   * More specifically:
   * <p>
   * <ol>
   * <li>{@code null} equals an empty <code>String</code>
   * <li>{@code null} equals an empty <code>Collection</code>
   * <li>{@code null} equals an empty <code>Map</code>
   * <li>{@code null} equals a zero-length array
   * <li>{@code null} equals an empty {@link Emptyable}
   * <li>{@code null} equals a zero-size {@link Sizeable}
   * <li>An empty/zero-size <code>String</code>, <code>List</code>,
   * <code>Set</code>, <code>Map</code>, array, <code>Emptyable</code> and
   * <code>Sizeable</code> are <b>not</b> equal to each other
   * <li>For any other pair of arguments this method returns the result of
   * <code>Objects.equals(obj1, obj2)<code>
   * </ol>
   *
   *
   * @param obj1 The 1st of the pair of objects to compare
   * @param obj2 The 2nd of the pair of objects to compare
   * @return
   */
  public static boolean e2nEquals(Object obj1, Object obj2) {
    if (obj1 == obj2) {
      return true;
    } else if (isEmpty(obj1)) {
      return isEmpty(obj2) ? canCompare(obj1, obj2) : false;
    }
    return isEmpty(obj2) ? false : Objects.equals(obj1, obj2);
  }

  /**
   * Recursively tests the arguments for equality using <i>empty-equals-null</i>
   * semantics. In other words, for arrays, collections and maps, elements c.q.
   * values are also compared using {@code e2nDeepEquals}.
   *
   * @param obj1 The 1st of the pair of objects to compare
   * @param obj2 The 2nd of the pair of objects to compare
   * @return
   */
  public static boolean e2nDeepEquals(Object obj1, Object obj2) {
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
   * empty} objects all have hash code zero. Therefore maps and sets relying on
   * <i>empty-equals-null</i> semantics to find keys and elements will likely have
   * to fall back more often on {@link #e2nEquals(Object, Object) e2nEquals} or
   * {@link #e2nDeepEquals(Object, Object) e2nDeepEquals}.
   *
   * @param obj The object to generate a hash code for
   * @return
   */
  public static int e2nHashCode(Object obj) {
    return isEmpty(obj) ? 0 : obj.hashCode();
  }

  /**
   * Generates a hash code for the provided arguments using using
   * <i>empty-equals-null</i> semantics. See {@link #hashCode()}.
   *
   * @param objs The objects to generate a hash code for
   * @return
   */
  public static int e2nHash(Object... objs) {
    if (objs == null) {
      return 0;
    }
    int hash = 0;
    for (Object obj : objs) {
      hash = hash * 31 + e2nHashCode(obj);
    }
    return hash;
  }

  /**
   * Returns {@code dfault} if {@code value} is null, else {@code value}.
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
   * Returns the value supplied by {@code supplier} if {@code value} is null, else
   * {@code value}.
   *
   * @param <T>
   * @param value The value to test
   * @param supplier A {@code Supplier} supplying a default value if {@code value}
   *        is null
   * @return
   */
  public static <T> T ifNull(T value, Supplier<T> supplier) {
    return value == null ? supplier.get() : value;
  }

  /**
   * Returns 0 if the argument is null, else the unboxed argument itself.
   *
   * @param i
   * @return
   */
  public static int ifNull(Integer i) {
    return ifNull(i, 0);
  }

  /**
   * Returns 0 if the argument is null, else the unboxed argument itself.
   *
   * @param d
   * @return
   */
  public static double ifNull(Double d) {
    return ifNull(d, 0D);
  }

  /**
   * Returns 0 if the argument is null, else the unboxed argument itself.
   *
   * @param l
   * @return
   */
  public static long ifNull(Long l) {
    return ifNull(l, 0L);
  }

  /**
   * Returns {@code false} if the argument is null, else the unboxed argument
   * itself.
   *
   * @param b
   * @return
   */
  public static boolean ifNull(Boolean b) {
    return ifNull(b, false);
  }

  /**
   * Returns {@code dfault} if {@code value} is {@link #isEmpty(Object) empty},
   * else {@code value}.
   *
   * @param <T>
   * @param value The value to test
   * @param dfault The value to return if {@code value} is null
   * @return
   */
  public static <T> T ifEmpty(T value, T dfault) {
    return isEmpty(value) ? dfault : value;
  }

  /**
   * Returns the value supplied by {@code supplier} if {@code value} is
   * {@link #isEmpty(Object) empty}, else {@code value}.
   *
   * @param <T>
   * @param value The value to test
   * @param supplier The <code>Supplier</code> supplying {@code value} is null
   * @return
   */
  public static <T> T ifEmpty(T value, Supplier<T> supplier) {
    return isEmpty(value) ? supplier.get() : value;
  }

  /**
   * Returns {@code value} if it is equal to {@code mustBe}, else null.
   *
   * @param <T> The input and return type
   * @param value The value to test
   * @param mustBe The value it must have in order to be returned
   * @return {@code value} or null
   */
  public static <T> T ifEquals(T value, T mustBe) {
    return ifTrue(Objects::equals, value, mustBe);
  }

  /**
   * Returns {@code value} only if it is not equal to {@code mustNotBe}, else
   * null.
   *
   * @param <T> The input and return type
   * @param value The value to test
   * @param mustBe The value it should have
   * @return {@code value} or null
   */
  public static <T> T ifNotEquals(T value, T mustNotBe) {
    return ifFalse(Objects::equals, value, mustNotBe);
  }

  /**
   * Returns {@code value} if the condition evaluates to {@code true}, else the
   * result of the specified operation on {@code value}. For example:
   *
   * <pre>
   * String s = ifTrue(ignoreCase, name, String::toLowerCase);
   * </pre>
   *
   * @param <T> The input and return type
   * @param condition The condition to evaluate
   * @param value The value value to return or to apply the transformation to
   * @param then The operation to apply if the condition evaluates to {@code true}
   * @return
   */
  public static <T> T ifTrue(boolean condition, T value, UnaryOperator<T> then) {
    return condition ? then.apply(value) : value;
  }

  /**
   * Returns {@code arg0} if the {@code comparison} function returns {@code true}
   * for arguments {@code arg0} and {@code arg1}, else null.
   *
   * @param <T> The input and return type
   * @param comparison A function that compares {@code value} and {@code mustBe}
   *        and returns a {@code Boolean}
   * @param arg0 The value to test
   * @param arg1 The value to compare it to
   * @return {@code value} or null
   */
  public static <T> T ifTrue(BiFunction<Object, Object, Boolean> comparison, T arg0, T arg1) {
    return comparison.apply(arg0, arg1) ? arg0 : null;
  }

  /**
   * Returns {@code value} if the condition evaluates to {@code false}, else the
   * result of the specified operation on {@code value}. For example:
   *
   * @param <T> The return type
   * @param condition The condition to evaluate
   * @param value The value value to return or to apply the transformation to
   * @param then The operation to apply if the condition evaluates to
   *        {@code false}
   * @return
   */
  public static <T> T ifFalse(boolean condition, T value, UnaryOperator<T> then) {
    return !condition ? then.apply(value) : value;
  }

  /**
   * Returns {@code arg0} if the {@code comparison} function returns {@code false}
   * for arguments {@code arg0} and {@code arg1}, else null.
   *
   * @param <T> The input and return type
   * @param comparison A function that compares {@code value} and {@code mustBe}
   *        and returns a {@code Boolean}
   * @param value The value to test
   * @param mustNotBe The value to compare it to
   * @return {@code value} or null
   */
  public static <T> T ifFalse(BiFunction<Object, Object, Boolean> comparison, T value, T mustNotBe) {
    return comparison.apply(value, mustNotBe) ? null : value;
  }

  /**
   * Returns the result of passing {@code value} to {@code then} if {@code value}
   * is not null, else the value supplied by {@code otherwise}. For example:
   *
   * <pre>
   * String[] strs = ifNotNull("Hello World", s -> s.split(" "));
   * </pre>
   *
   * @param <T> The type of the first argument
   * @param <U> The return type
   * @param value The value to test
   * @param then The transformation to apply to the value if it is not null
   * @return
   */
  public static <T, U> U ifNotNull(T value, Function<T, U> then) {
    return value != null ? then.apply(value) : null;
  }

  /**
   * Returns the result of passing {@code value} to {@code then} if {@code value}
   * is not null, else the value supplied by {@code otherwise}. For example:
   *
   * <pre>
   * String[] strs = ifNotNull("Hello World", s -> s.split(" "), () -> new String[0]);
   * </pre>
   *
   * @param <T> The type of the first argument
   * @param <U> The return type
   * @param value The value to test
   * @param then The transformation to apply to the value if it is not null
   * @param otherwise A supplier providing the default value
   * @return
   */
  public static <T, U> U ifNotNull(T value, Function<T, U> then, Supplier<U> otherwise) {
    return value != null ? then.apply(value) : otherwise.get();
  }

  /**
   * Returns the result of passing {@code value} to {@code then} if {@code value}
   * is not empty, else null.
   *
   * @param <T> The type of the first argument
   * @param <U> The return type
   * @param value The value to test
   * @param then The transformation to apply to the value if it is not null
   * @return
   */
  public static <T, U> U ifNotEmpty(T value, Function<T, U> then) {
    return notEmpty(value) ? then.apply(value) : null;
  }

  /**
   * Returns the result of passing {@code value} to {@code then} if {@code value}
   * is not empty, else the value supplied by {@code otherwise}.
   *
   * @param <T> The type of the first argument
   * @param <U> The return type
   * @param value The value to test
   * @param then The transformation to apply to the value if it is not null
   * @param otherwise A supplier providing the default value
   * @return
   */
  public static <T, U> U ifNotEmpty(T value, Function<T, U> then, Supplier<U> otherwise) {
    return notEmpty(value) ? then.apply(value) : otherwise.get();
  }

  /**
   * Executes the {@code Runnable} if the condition evaluates to {@code true},
   * else does nothing.
   *
   * @param condition The condition to evaluate
   * @param then The action to execute
   */
  public static void when(boolean condition, Runnable then) {
    if (condition) {
      then.run();
    }
  }

  /**
   * Executes the {@code Runnable} if {@code value} is null, else does nothing.
   *
   * @param value The value to test
   * @param then The action to execute
   */
  public static void whenNull(Object value, Runnable then) {
    if (value == null) {
      then.run();
    }
  }

  /**
   * Executes the {@code Runnable} if {@code value} is empty, else does nothing.
   *
   * @param value The value to test
   * @param then The action to execute
   */
  public static void whenEmpty(Object value, Runnable then) {
    if (isEmpty(value)) {
      then.run();
    }
  }

  /**
   * Executes the {@code Runnable} if the condition evaluates to {@code false},
   * else does nothing.
   *
   * @param condition The condition to evaluate
   * @param then The action to execute
   */
  public static void whenNot(boolean condition, Runnable then) {
    if (!condition) {
      then.run();
    }
  }

  /**
   * Passes {@code value} to {@code consumer} if not null, else does nothing.
   *
   * @param <T> The type of the object to test
   * @param value The value to test
   * @param consumer The {@code Consumer} whose {@code accewpt} method to call
   */
  public static <T> void whenNotNull(T value, Consumer<T> consumer) {
    if (value != null) {
      consumer.accept(value);
    }
  }

  /**
   * Passes {@code value} to {@code consumer} if not {@link #notEmpty(Object)
   * empty}, else does nothing.
   *
   * @param <T> The type of the object to test
   * @param value The object to test
   * @param consumer The {@code Consumer} whose {@code accept} method to call
   */
  public static <T> void whenNotEmpty(T value, Consumer<T> consumer) {
    if (notEmpty(value)) {
      consumer.accept(value);
    }
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
        if (!e2nDeepEquals(objs1[i], objs2[i])) {
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
        if (!it2.hasNext() || !e2nDeepEquals(it1.next(), it2.next())) {
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
        if (e2nDeepEquals(obj1, obj2)) {
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
        if (!map2.containsKey(k) || !e2nDeepEquals(map1.get(k), map2.get(k))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  private static boolean canCompare(Object obj1, Object obj2) {
    return obj1 == null // can always compare null to any other type of empty object
        || obj2 == null
        || obj1.getClass() == obj2.getClass() // String, Emptyable, Sizeable & primitive arrays
        || obj1 instanceof List && obj2 instanceof List
        || obj1 instanceof Set && obj2 instanceof Set
        || obj1 instanceof Map && obj2 instanceof Map
        || obj1 instanceof Object[] && obj2 instanceof Object[];
  }

}
