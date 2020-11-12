package nl.naturalis.common;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.Relation;
import static java.util.stream.Collectors.toSet;
import static nl.naturalis.common.ClassMethods.isPrimitiveArray;
import static nl.naturalis.common.check.CommonChecks.equalTo;
import static nl.naturalis.common.check.CommonChecks.notEmpty;
import static nl.naturalis.common.check.CommonChecks.notEqualTo;
import static nl.naturalis.common.check.CommonChecks.notNull;
import static nl.naturalis.common.check.CommonGetters.supplied;

/**
 * General methods applicable to objects of any type.
 *
 * <h3>Container objects</h3>
 *
 * <p>Some methods in this class apply special logic (documented on the method itself) when passed a
 * <i>container object</i>. A container object is one of the following
 *
 * <p>
 *
 * <ul>
 *   <li>an instance of {@code Object[]}
 *   <li>an instance of {@link Collection}
 *   <li>an instance of {@link Map}
 *   <li>an array of primitive values
 * </ul>
 *
 * <p>For {@code Map} objects the logic will always only applied to their values, not their keys.
 * Use {@link Map#keySet() Map.keySet()} if you want to apply the logic to their keys as well.
 *
 * @author Ayco Holleman
 */
@SuppressWarnings("rawtypes")
public class ObjectMethods {

  private ObjectMethods() {}

  /**
   * Verifies that the object is empty. Returns {@code true} if <i>any</i> of the following applies:
   *
   * <p>
   *
   * <ul>
   *   <li>{@code obj} is {@code null}
   *   <li>{@code obj} is an empty {@link CharSequence}
   *   <li>{@code obj} is an empty {@link Collection}
   *   <li>{@code obj} is an empty {@link Map}
   *   <li>{@code obj} is an empty array
   *   <li>{@code obj} is an empty {@link Optional} or an {@code Optional} containing an {@code
   *       isEmpty} object
   *   <li>{@code obj} is a zero-size {@link Sizeable}
   *   <li>{@code obj} is an empty {@link Emptyable}
   * </ul>
   *
   * <p>Otherwise this method returns {@code false}.
   *
   * @param obj The object to be tested
   * @return Whether or not it is empty
   */
  public static boolean isEmpty(Object obj) {
    return obj == null
        || obj instanceof CharSequence && ((CharSequence) obj).length() == 0
        || obj instanceof Collection && ((Collection) obj).isEmpty()
        || obj instanceof Map && ((Map) obj).isEmpty()
        || obj instanceof Object[] && ((Object[]) obj).length == 0
        || isPrimitiveArray(obj) && Array.getLength(obj) == 0
        || obj instanceof Optional
            && (((Optional) obj).isEmpty() || isEmpty(((Optional) obj).get()))
        || obj instanceof Sizeable && ((Sizeable) obj).size() == 0
        || obj instanceof Emptyable && ((Emptyable) obj).isEmpty();
  }

  /**
   * Verifies that the argument is not empty. Returns {@code true} if <i>any</i> of the following
   * applies:
   *
   * <p>
   *
   * <ul>
   *   <li>{@code obj} is a non-empty {@link CharSequence}
   *   <li>{@code obj} is a non-empty {@link Collection}
   *   <li>{@code obj} is a non-empty {@link Map}
   *   <li>{@code obj} is a non-empty array
   *   <li>{@code obj} is a non-empty {@link Optional} containing a non-empty object
   *   <li>{@code obj} is a non-zero-size {@link Sizeable}
   *   <li>{@code obj} is a non-empty {@link Emptyable}
   *   <li>{@code obj} is a non-null object of any other type
   * </ul>
   *
   * @param obj The object to be tested
   * @return Whether or not it is empty
   */
  public static boolean isNotEmpty(Object obj) {
    return !isEmpty(obj);
  }

  /**
   * Verifies that the argument is recursively non-empty. Returns {@code true} if <i>any</i> of the
   * following applies:
   *
   * <p>
   *
   * <ul>
   *   <li>{@code obj} is a non-empty {@link CharSequence}
   *   <li>{@code obj} is a non-empty {@link Collection} containing only <i>deep-not-empty</i>
   *       elements
   *   <li>{@code obj} is a non-empty {@link Map} containing only <i>deep-not-empty</i> values (keys
   *       are not considered)
   *   <li>{@code obj} is a non-empty {@code Object[]} containing only <i>deep-not-empty</i>
   *       elements
   *   <li>{@code obj} is a non-empty primitive array
   *   <li>{@code obj} is a non-empty {@link Optional} containing a <i>deep-not-empty</i> object
   *   <li>{@code obj} is a non-empty {@link Emptyable}
   *   <li>{@code obj} is a non-zero-size {@link Sizeable}
   *   <li>{@code obj} is a non-null object of any other type
   * </ul>
   *
   * @param obj The object to be tested
   * @return Whether or not it is recursively non-empty
   */
  public static boolean isDeepNotEmpty(Object obj) {
    return obj != null
        && (!(obj instanceof CharSequence) || ((CharSequence) obj).length() > 0)
        && (!(obj instanceof Collection) || dne((Collection) obj))
        && (!(obj instanceof Map) || dne((Map) obj))
        && (!(obj instanceof Object[]) || dne((Object[]) obj))
        && (!(obj instanceof Optional) || dne((Optional) obj))
        && (!isPrimitiveArray(obj) || Array.getLength(obj) > 0)
        && (!(obj instanceof Sizeable) || ((Sizeable) obj).size() > 0)
        && (!(obj instanceof Emptyable) || !((Emptyable) obj).isEmpty());
  }

  private static boolean dne(Collection coll) {
    return !coll.isEmpty() && coll.stream().allMatch(ObjectMethods::isDeepNotEmpty);
  }

  private static boolean dne(Map map) {
    return !map.isEmpty() && map.values().stream().allMatch(ObjectMethods::isDeepNotEmpty);
  }

  private static boolean dne(Object[] arr) {
    return arr.length > 0 && Arrays.stream(arr).allMatch(ObjectMethods::isDeepNotEmpty);
  }

  private static boolean dne(Optional opt) {
    return opt.isPresent() && isDeepNotEmpty(opt.get());
  }

  /**
   * Verifies that the argument is not null and, if it is a {@link Collection}, {@link Map} or
   * {@code Object[]}, does not contain any null values. It may still be an empty collection, map or
   * array, however.
   *
   * @param obj The object to be tested
   * @return Whether or not it is not null and does not contain any null values
   */
  public static boolean isNoneNull(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj instanceof Object[]) {
      return Arrays.stream((Object[]) obj).allMatch(notNull());
    } else if (obj instanceof Collection) {
      return ((Collection) obj).stream().allMatch(notNull());
    } else if (obj instanceof Map) {
      return ((Map) obj).values().stream().allMatch(notNull());
    }
    return true;
  }

  /**
   * Returns {@code null} if the argument is {@link #isEmpty(Object) empty}, else the argument
   * itself.
   *
   * @param <T> The type of the argument
   * @param obj The argument
   * @return The argument itself or {@code null}
   */
  public static <T> T emptyToNull(T obj) {
    return isEmpty(obj) ? null : obj;
  }

  /**
   * Tests the provided arguments for equality using <i>empty-equals-null</i> semantics. This is
   * equivalent to {@code Objects.equals(emptyToNull(obj1), emptyToNull(obj2))}, except that an
   * empty instance of one type (e.g. {@code String}) is <b>not</b> equal to an empty instance of
   * another non-comparable type (e.g. {@code Set}). An empty {@code HashSet}, however, is equal to
   * an empty {@code TreeSet}. So:
   *
   * <p>
   *
   * <ol>
   *   <li>{@code null} equals an empty {@link CharSequence}
   *   <li>{@code null} equals an empty {@link Collection}
   *   <li>{@code null} equals an empty {@link Map}
   *   <li>{@code null} equals an empty array
   *   <li>{@code null} equals an empty {@link Emptyable}
   *   <li>{@code null} equals a zero-size {@link Sizeable}
   *   <li>An empty intance of one type never equals an empty instance of another non-comparable
   *       type
   * </ol>
   *
   * @param obj1 The 1st of the pair of objects to compare
   * @param obj2 The 2nd of the pair of objects to compare
   * @return Whether or not the provided arguments are equal using empty-equals-null semantics
   */
  public static boolean e2nEquals(Object obj1, Object obj2) {
    if (obj1 == obj2) {
      return true;
    } else if (isEmpty(obj1)) {
      return isEmpty(obj2) ? e2nComparable(obj1, obj2) : false;
    }
    return isEmpty(obj2) ? false : Objects.equals(obj1, obj2);
  }

  /**
   * Recursively tests the arguments for equality using <i>empty-equals-null</i> semantics.
   *
   * @param obj1 The 1st of the pair of objects to compare
   * @param obj2 The 2nd of the pair of objects to compare
   * @return Whether or not the provided arguments are deeply equal using empty-equals-null
   *     semantics
   */
  public static boolean e2nDeepEquals(Object obj1, Object obj2) {
    if (obj1 == obj2) {
      return true;
    } else if (isEmpty(obj1)) {
      return isEmpty(obj2) ? e2nComparable(obj1, obj2) : false;
    }
    return isEmpty(obj2) ? false : eq(obj1, obj2);
  }

  /**
   * Generates a hash code for the provided object using using <i>empty-equals-null</i> semantics.
   * Null and {@link #isEmpty(Object) empty} objects (whatever their type) all have the same hash
   * code: 0 (zero). Therefore a {@link TreeMap} or {@link TreeSet} using on
   * <i>empty-equals-null</i> semantics may have to fall fall back more often on {@link
   * #e2nEquals(Object, Object) e2nEquals} or {@link #e2nDeepEquals(Object, Object) e2nDeepEquals}.
   *
   * @param obj The object to generate a hash code for
   * @return The hash code
   */
  public static int e2nHashCode(Object obj) {
    return isEmpty(obj) ? 0 : obj.hashCode();
  }

  /**
   * Generates a hash code for the provided arguments using using <i>empty-equals-null</i>
   * semantics. See {@link #hashCode()}.
   *
   * @param objs The objects to generate a hash code for
   * @return The hash code
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
   * Returns the first argument if it is not null, else the second argument. This method will throw
   * an {@link IllegalArgumentException} if the second argument is null, so it is guaranteed to
   * return a non-null value.
   *
   * @param <T> The input and return type
   * @param value The value to return if not null
   * @param dfault The value to return if the first argument is null
   * @return A non-null value
   */
  public static <T> T ifNull(T value, T dfault) {
    Check.notNull(dfault, "dfault");
    return value == null ? dfault : value;
  }

  /**
   * Returns the first argument if it is not null, else the value supplied by the specified {@code
   * Supplier}. The value supplied by the {@code Supplier} is guaranteed to be non-null, or else an
   * {@link IllegalArgumentException} is thrown.
   *
   * @param <T> The input and return type
   * @param value The value to return if not null
   * @param supplier A {@code Supplier} supplying the default value if {@code value} is null
   * @return a non-null value
   */
  public static <T> T ifNull(T value, Supplier<T> supplier) {
    Check.notNull(supplier, "supplier").has(supplied(), notNull());
    return value == null ? supplier.get() : value;
  }

  /**
   * Returns {@code dfault} if {@code value} is {@link #isEmpty(Object) empty}, else {@code value}.
   *
   * @param <T> The input and return type
   * @param value The value to test
   * @param dfault The value to return if {@code value} is null
   * @return a non-empty value
   */
  public static <T> T ifEmpty(T value, T dfault) {
    Check.that(dfault, "dfault").is(notEmpty());
    return isEmpty(value) ? dfault : value;
  }

  /**
   * Returns the value supplied by {@code supplier} if {@code value} is {@link #isEmpty(Object)
   * empty}, else {@code value}.
   *
   * @param <T> The input and return type
   * @param value The value to return if not empty
   * @param supplier A {@code Supplier} supplying a default value if {@code value} is empty
   * @return a non-empty value
   */
  public static <T> T ifEmpty(T value, Supplier<T> supplier) {
    return isEmpty(value) ? supplier.get() : value;
  }

  /**
   * Returns the result of the specified operation on {@code value} if the condition evaluates to
   * {@code true}, else {@code value} itself. For example:
   *
   * <pre>
   * String s = ifTrue(ignoreCase, name, String::toLowerCase);
   * </pre>
   *
   * @param <T> The input and return type
   * @param condition The condition to evaluate
   * @param value The value value to return or to apply the transformation to
   * @param then The operation to apply if the condition evaluates to {@code true}
   * @return {@code value}, possibly transformed by the unary operator
   */
  public static <T> T ifTrue(boolean condition, T value, UnaryOperator<T> then) {
    return condition ? then.apply(value) : value;
  }

  /**
   * Returns the result of the specified operation on {@code value} if the condition evaluates to
   * {@code false}, else {@code value} itself. For example:
   *
   * @param <T> The return type
   * @param condition The condition to evaluate
   * @param value The value value to return or to apply the transformation to
   * @param then The operation to apply if the condition evaluates to {@code false}
   * @return {@code value}, possibly transformed by the unary operator
   */
  public static <T> T ifFalse(boolean condition, T value, UnaryOperator<T> then) {
    return !condition ? then.apply(value) : value;
  }

  /**
   * Returns null if {@code arg0} is equal to {@code arg1}, else {@code arg0}.
   *
   * @param <T> The input and return type
   * @param arg0 The value to test
   * @param arg1 The value it must not have in order to be returned
   * @return {@code value} or null
   */
  public static <T> T nullIf(T arg0, T arg1) {
    return nullIf(arg0, equalTo(), arg1);
  }

  /**
   * Returns null unless {@code arg0} equals {@code arg1}, else {@code arg0}.
   *
   * @param <T> The input and return type
   * @param arg0 The value to test
   * @param arg1 The value {@code arg0} must have in order to be returned
   * @return {@code arg0} or null
   */
  public static <T> T nullUnless(T arg0, T arg1) {
    return nullUnless(arg0, notEqualTo(), arg1);
  }

  /**
   * Returns null if {@code arg0} has the specified {@link Relation} to {@code arg1}, else {@code
   * arg0}.
   *
   * @param <T> The input and return type
   * @param <U> The type of target of the relation
   * @param arg0 The value to test and return
   * @param relation The required {@code Relation} between {@code arg 0} and {@code arg1}
   * @param arg1 The value to test {@code arg0} against
   * @return {@code arg0} or null
   */
  public static <T, U> T nullIf(T arg0, Relation<T, U> relation, U arg1) {
    return relation.exists(arg0, arg1) ? null : arg0;
  }

  /**
   * Returns null unless {@code arg0} has the specified {@link Relation} to {@code arg1} {@code
   * arg1}, else {@code arg0}.
   *
   * @param arg0 The value to test and return
   * @param relation The required {@code Relation} between {@code arg 0} and {@code arg1}
   * @param arg1 The target of the relationship
   * @param <T> The input and return type
   * @param <U> The type of target of the relation
   * @return {@code value} or null
   */
  public static <T, U> T nullUnless(T arg0, Relation<T, U> relation, U arg1) {
    return relation.exists(arg0, arg1) ? arg0 : null;
  }

  /**
   * Returns the result of passing the specified argument to the specified {@code Funtion} if the
   * argument is not null, else returns null. For example:
   *
   * <pre>
   * String[] strs = ifNotNull("Hello World", s -> s.split(" "));
   * </pre>
   *
   * @param <T> The type of the first argument
   * @param <U> The return type
   * @param arg The value to test
   * @param then The transformation to apply to the value if it is not null
   * @return {@code value} or null
   */
  public static <T, U> U ifNotNull(T arg, Function<T, U> then) {
    return arg != null ? then.apply(arg) : null;
  }

  /**
   * Returns the result of passing the specified argument to the specified {@code Funtion} if the
   * argument is not null, else a default value. For example:
   *
   * <pre>
   * String[] strs = ifNotNull("Hello World", s -> s.split(" "), new String[0]);
   * </pre>
   *
   * @param <T> The type of the first value to transform
   * @param <U> The return type
   * @param arg The value to transform
   * @param then The transformation to apply to the value if it is not null
   * @param dfault A default value to return if the argument is null
   * @return The result produced by the {@code Function} or by the {@code Supplier}
   */
  public static <T, U> U ifNotNull(T arg, Function<T, U> then, U dfault) {
    return arg != null ? then.apply(arg) : dfault;
  }

  /**
   * Returns the result of passing the specified argument to the specified {@code Funtion} if the
   * argument is not {@link #isEmpty(Object) empty}, else a default value.
   *
   * @param <T> The type of the value to transform
   * @param <U> The return type
   * @param arg The value to transform
   * @param then The function to apply to the value if it is not null
   * @param dfault A default value to return if the argument is empty
   * @return The result produced by the {@code Function} or a default value
   */
  public static <T, U> U ifNotEmpty(T arg, Function<T, U> then, U dfault) {
    return isNotEmpty(arg) ? then.apply(arg) : dfault;
  }

  /**
   * Executes the specified {@code Runnable} if the specified condition evaluates to {@code true},
   * else does nothing.
   *
   * @param condition The condition to evaluate
   * @param then The action to execute
   */
  public static void doIf(boolean condition, Runnable then) {
    if (condition) {
      then.run();
    }
  }

  /**
   * Executes the first {@code Runnable} if the specified condition evaluates to {@code true}, else
   * the second.
   *
   * @param condition The condition to evaluate
   * @param then The action to execute if the condition to evaluates to {@code true}
   * @param otherwise The action to execute if the condition to evaluates to {@code false}
   */
  public static void doIf(boolean condition, Runnable then, Runnable otherwise) {
    if (condition) {
      then.run();
    } else {
      otherwise.run();
    }
  }

  /**
   * Passes the specified argument to the specified {@code consumer} if not null, else does nothing.
   *
   * @param <T> The type of the object to test
   * @param arg The value to test
   * @param consumer The {@code Consumer} whose {@code accewpt} method to call
   */
  public static <T> void doIfNotNull(T arg, Consumer<T> consumer) {
    if (arg != null) {
      consumer.accept(arg);
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

  private static boolean e2nComparable(Object obj1, Object obj2) {
    return obj1 == null
        || obj2 == null
        || obj1.getClass() == obj2.getClass()
        || obj1 instanceof List && obj2 instanceof List
        || obj1 instanceof Set && obj2 instanceof Set
        || obj1 instanceof Map && obj2 instanceof Map
        || obj1 instanceof Object[] && obj2 instanceof Object[];
  }
}
