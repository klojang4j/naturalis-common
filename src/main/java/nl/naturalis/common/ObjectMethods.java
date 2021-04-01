package nl.naturalis.common;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.check.CommonChecks;
import nl.naturalis.common.function.Relation;
import nl.naturalis.common.function.ThrowingSupplier;
import static java.util.stream.Collectors.toSet;
import static nl.naturalis.common.ClassMethods.isPrimitiveArray;
import static nl.naturalis.common.check.CommonChecks.*;

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

  private static final String ERR_NULL_OPTIONAL = "Optional must not be null";

  private ObjectMethods() {}

  /**
   * Whether or not the specified {@code String} is null or empty.
   *
   * @param arg The {@code String} to check
   * @return Whether or not it is null or empty
   */
  public static boolean isEmpty(String arg) {
    return arg == null || arg.isEmpty();
  }

  /**
   * Returns whether or not the specified {@code Collection} is null or empty.
   *
   * @param arg The {@code Collection} to check
   * @return Whether or not it is null or empty
   */
  public static boolean isEmpty(Collection arg) {
    return arg == null || arg.isEmpty();
  }

  /**
   * Returns whether or not the specified {@code Map} is null or empty.
   *
   * @param arg The {@code Map} to check
   * @return Whether or not it is null or empty
   */
  public static boolean isEmpty(Map arg) {
    return arg == null || arg.isEmpty();
  }

  /**
   * Returns whether or not the specified {@code Optional} is empty or contains an empty object.
   * This is the only {@code isNotEmpty} method that will actually throw an {@code
   * IllegalArgumentException} if the argument is null as {@code Optional} objects should never be
   * null.
   *
   * @param arg The {@code Optional} to check
   * @return Whether or not it is empty or contains an empty object
   */
  public static boolean isEmpty(Optional arg) {
    if (arg == null) {
      throw new IllegalArgumentException(ERR_NULL_OPTIONAL);
    }
    return arg.isEmpty() || isEmpty(arg.get());
  }

  /**
   * Returns whether or not the specified array is null or empty.
   *
   * @param arg The array to check
   * @return Whether or not it is null or empty
   */
  public static boolean isEmpty(Object[] arg) {
    return arg == null || arg.length == 0;
  }

  /**
   * Returns whether or not the specified argument is null or empty. This method is (and can be)
   * used for broad-stroke methods like {@link #ifEmpty(Object, Object)} and {@link
   * CommonChecks#empty()}. Returns {@code true} if <i>any</i> of the following applies:
   *
   * <p>
   *
   * <ul>
   *   <li>{@code arg} is {@code null}
   *   <li>{@code arg} is an empty {@link CharSequence}
   *   <li>{@code arg} is an empty {@link Collection}
   *   <li>{@code arg} is an empty {@link Map}
   *   <li>{@code arg} is a zero-length array
   *   <li>{@code arg} is an empty {@link Optional} or an {@code Optional} containing an empty
   *       object
   *   <li>{@code arg} is a zero-size {@link Sizeable}
   *   <li>{@code arg} is an empty {@link Emptyable}
   * </ul>
   *
   * <p>Otherwise this method returns {@code false}.
   *
   * @param arg The argument to check
   * @return Whether or not it is null or empty
   */
  public static boolean isEmpty(Object arg) {
    return arg == null
        || arg instanceof CharSequence && ((CharSequence) arg).length() == 0
        || arg instanceof Collection && ((Collection) arg).isEmpty()
        || arg instanceof Map && ((Map) arg).isEmpty()
        || arg instanceof Object[] && ((Object[]) arg).length == 0
        || isPrimitiveArray(arg) && Array.getLength(arg) == 0
        || arg instanceof Optional
            && (((Optional) arg).isEmpty() || isEmpty(((Optional) arg).get()))
        || arg instanceof Sizeable && ((Sizeable) arg).size() == 0
        || arg instanceof Emptyable && ((Emptyable) arg).isEmpty();
  }

  /**
   * Returns whether or not the specified {@code String} is neither null nor empty.
   *
   * @param arg The {@code String} to check
   * @return Whether or not it is neither null nor empty
   */
  public static boolean isNotEmpty(String arg) {
    return !isEmpty(arg);
  }

  /**
   * Returns whether or not the specified {@code Collection} is neither null nor empty.
   *
   * @param arg The {@code Collection} to check
   * @return Whether or not it is neither null nor empty
   */
  public static boolean isNotEmpty(Collection arg) {
    return !isEmpty(arg);
  }

  /**
   * Returns whether or not the specified {@code Map} is neither null nor empty.
   *
   * @param arg The {@code Map} to check
   * @return Whether or not it is neither null nor empty
   */
  public static boolean isNotEmpty(Map arg) {
    return !isEmpty(arg);
  }

  /**
   * Returns whether or not the specified {@code Optional} neither empty nor contains an empty
   * object. This is the only {@code isNotEmpty} method that will actually throw an {@code
   * IllegalArgumentException} if the argument is null as {@code Optional} objects should never be
   * null.
   *
   * @param arg The {@code Optional} to check
   * @return Whether or not it is neither empty nor contains an empty object
   * @throws IllegalArgumentException If the argument is null
   */
  public static boolean isNotEmpty(Optional arg) throws IllegalArgumentException {
    return !isEmpty(arg);
  }

  /**
   * Returns whether or not the specified {@code Optional} is neither null nor empty.
   *
   * @param arg The {@code String} to check
   * @return Whether or not it is neither null nor empty
   */
  public static boolean isNotEmpty(Object[] arg) {
    return !isEmpty(arg);
  }
  /**
   * Verifies that the argument is neither null nor empty. This method is (and can be) used for
   * broad-stroke methods like {@link #ifNotEmpty(Object, Object)} and {@link
   * CommonChecks#notEmpty()}. Returns {@code true} if <i>any</i> of the following applies:
   *
   * <p>
   *
   * <ul>
   *   <li>{@code obj} is a non-empty {@link CharSequence}
   *   <li>{@code obj} is a non-empty {@link Collection}
   *   <li>{@code obj} is a non-empty {@link Map}
   *   <li>{@code obj} is a non-zero-length array
   *   <li>{@code obj} is a non-empty {@link Optional} containing a non-empty object
   *   <li>{@code obj} is a non-zero-size {@link Sizeable}
   *   <li>{@code obj} is a non-empty {@link Emptyable}
   *   <li>{@code obj} is a non-null object of any other type
   * </ul>
   *
   * @param arg The object to be tested
   * @return Whether or not it is empty
   */
  public static boolean isNotEmpty(Object arg) {
    return !isEmpty(arg);
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
   *   <li>{@code obj} is a non-zero-length {@code Object[]} containing only <i>deep-not-empty</i>
   *       elements
   *   <li>{@code obj} is a non-zero-length array of primitives
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
   * @param arg The object to be tested
   * @return Whether or not it is not null and does not contain any null values
   */
  public static boolean isNoneNull(Object arg) {
    if (arg == null) {
      return false;
    } else if (arg instanceof Object[]) {
      return Arrays.stream((Object[]) arg).allMatch(notNull());
    } else if (arg instanceof Collection) {
      return ((Collection) arg).stream().allMatch(notNull());
    } else if (arg instanceof Map) {
      return ((Map) arg).values().stream().allMatch(notNull());
    }
    return true;
  }

  /**
   * Empty-to-null: returns {@code null} if the argument is {@link #isEmpty(Object) empty}, else the
   * argument itself.
   *
   * @param <T> The type of the argument
   * @param arg The argument
   * @return The argument itself if not empty or {@code null}
   */
  public static <T> T e2n(T arg) {
    return isEmpty(arg) ? null : arg;
  }

  /**
   * Tests the provided arguments for equality using <i>empty-equals-null</i> semantics. This is
   * roughly equivalent to {@code Objects.equals(e2n(arg0), e2n(arg1))}, except that empty (but
   * non-null) instances of different types (e.g. {@code String} and {@code Set}) are generally not
   * equal to each other. (An empty {@code HashSet}, however, is equal to an empty {@code TreeSet}.)
   *
   * <p>
   *
   * <ol>
   *   <li>{@code null} equals an empty {@link CharSequence}
   *   <li>{@code null} equals an empty {@link Collection}
   *   <li>{@code null} equals an empty {@link Map}
   *   <li>{@code null} equals an empty array
   *   <li>{@code null} equals an empty {@link Optional} or an {@link Optional} containing an empty
   *       object
   *   <li>{@code null} equals an empty {@link Emptyable}
   *   <li>{@code null} equals a zero-size {@link Sizeable}
   *   <li>A empty intance of one type is not equal to a empty instance of another non-comparable
   *       type
   * </ol>
   *
   * @param arg0 The 1st of the pair of objects to compare
   * @param arg1 The 2nd of the pair of objects to compare
   * @return Whether or not the provided arguments are equal using empty-equals-null semantics
   */
  public static boolean e2nEquals(Object arg0, Object arg1) {
    return arg0 == null ? isEmpty(arg1) : arg1 == null ? isEmpty(arg0) : Objects.equals(arg0, arg1);
  }

  /**
   * Recursively tests the arguments for equality using <i>empty-equals-null</i> semantics.
   *
   * @param arg0 The 1st of the pair of objects to compare
   * @param arg1 The 2nd of the pair of objects to compare
   * @return Whether or not the provided arguments are deeply equal using empty-equals-null
   *     semantics
   */
  public static boolean e2nDeepEquals(Object arg0, Object arg1) {
    return arg0 == null ? isEmpty(arg1) : arg1 == null ? isEmpty(arg0) : eq(arg0, arg1);
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
    return value == null ? Check.notNull(dfault, "dfault").ok() : value;
  }

  /**
   * Returns the first argument if it is not null, else the value supplied by the specified {@code
   * Supplier}. The value supplied by the {@code Supplier} is guaranteed to be non-null, or else an
   * {@link IllegalArgumentException} is thrown.
   *
   * @param <T> The input and return type
   * @param <E> The exception potentially being thrown by the supplier as it produces a default
   *     value
   * @param value The value to return if not null
   * @param supplier The supplier of a default value if {@code value} is null
   * @return a non-null value
   */
  public static <T, E extends Exception> T ifNull(T value, ThrowingSupplier<T, E> supplier)
      throws E {
    Check.notNull(supplier, "supplier");
    if (value == null) {
      return Check.that(supplier.get()).is(notNull(), "Supplier must not supply null").ok();
    }
    return value;
  }

  /**
   * Returns {@code dfault} if {@code value} is {@link #isEmpty(Object) empty}, else {@code value}.
   * This method will throw an {@link IllegalArgumentException} if the second argument is empty, so
   * it is guaranteed to return a non-empty value.
   *
   * @param <T> The input and return type
   * @param value The value to test
   * @param dfault The value to return if {@code value} is null
   * @return a non-empty value
   */
  public static <T> T ifEmpty(T value, T dfault) {
    return isEmpty(value) ? Check.that(dfault, "dfault").isNot(empty()).ok() : value;
  }

  /**
   * Returns the value supplied by {@code supplier} if {@code value} is {@link #isEmpty(Object)
   * empty}, else {@code value}. The value supplied by the {@code Supplier} is guaranteed to be
   * non-empty, or else an {@link IllegalArgumentException} is thrown.
   *
   * @param <T> The input and return type
   * @param <E> The exception potentially being thrown by the supplier as it produces a default
   *     value
   * @param value The value to return if not empty
   * @param supplier The supplier of a default value if {@code value} is null
   * @return a non-empty value
   */
  public static <T, E extends Exception> T ifEmpty(T value, ThrowingSupplier<T, E> supplier)
      throws E {
    Check.notNull(supplier, "supplier");
    if (isEmpty(value)) {
      return Check.that(supplier.get()).isNot(empty(), "Supplier must not supply empty value").ok();
    }
    return value;
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
   * Returns {@code alternative} if the specified relation exists between {@code subject} and {@code
   * object}, else {@code subject}.
   *
   * @param <T> The type of the objects involved
   * @param subject The value to test and possibly return
   * @param relation The test
   * @param object The value to to test against
   * @param alternative The value to return if the
   * @return either {@code subject} or {@code alternative}
   */
  public static <T> T ifTrue(T subject, Relation<T, T> relation, T object, T alternative) {
    return relation.exists(subject, object) ? alternative : subject;
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
   * Returns null if {@code arg0} is equal to any of the specified values, else {@code arg0}. For
   * example:
   *
   * <p>
   *
   * <pre>
   *  this.operator = nullIf(operator, Operator.AND);
   * </pre>
   *
   * @param <T> The input and return type
   * @param arg0 The value to test
   * @param values The value it must not have in order to be returned
   * @return {@code value} or null
   */
  @SuppressWarnings("unchecked")
  public static <T> T nullIf(T arg0, T... values) {
    return nullIf(arg0, inArray(), values);
  }

  /**
   * Returns null unless {@code arg0} equals one of the specified values. For example:
   *
   * <p>
   *
   * <pre>
   *  this.operator = nullUnless(operator, Operator.OR);
   * </pre>
   *
   * @param <T> The input and return type
   * @param arg0 The value to test
   * @param values The values {@code arg0} may have in order to be returned
   * @return {@code arg0} or null
   */
  @SuppressWarnings("unchecked")
  public static <T> T nullUnless(T arg0, T... values) {
    return (T) nullUnless(arg0, inArray().negate(), values);
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
    return ifNotNull(arg, then, null);
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
   * argument is not {@link #isEmpty(Object) empty}, else returns null.
   *
   * @param <T> The type of the value to transform
   * @param <U> The return type
   * @param arg The value to transform
   * @param then The function to apply to the value if it is not null
   * @return The result produced by the {@code Function} or a default value
   */
  public static <T, U> U ifNotEmpty(T arg, Function<T, U> then) {
    return ifNotEmpty(arg, then, null);
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

  /**
   * Null-to-empty: returns an empty {@code String} if the argument is null, else the argument
   * itself.
   *
   * @param arg An argument of type {@code String}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static String n2e(String arg) {
    return ifNull(arg, StringMethods.EMPTY);
  }

  /**
   * Null-to-empty: returns {@link Collections#emptyList()} if the argument is null, else the
   * argument itself.
   *
   * @param arg An argument of type {@code List}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static <T> List<T> n2e(List<T> arg) {
    return ifNull(arg, Collections.emptyList());
  }

  /**
   * Null-to-empty: returns {@link Collections#emptySet()} if the argument is null, else the
   * argument itself.
   *
   * @param arg An argument of type {@code List}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static <T> Set<T> n2e(Set<T> arg) {
    return ifNull(arg, Collections.emptySet());
  }

  /**
   * Null-to-empty: returns {@link Collections#emptyMap()} if the argument is null, else the
   * argument itself.
   *
   * @param arg An argument of type {@code List}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static <K, V> Map<K, V> n2e(Map<K, V> arg) {
    return ifNull(arg, Collections.emptyMap());
  }

  /**
   * Returns {@link NumberMethods#ZERO_INT} if the argument is null, else the argument itself.
   *
   * @param arg An argument of type {@code Integer}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Integer n2e(Integer arg) {
    return ifNull(arg, NumberMethods.ZERO_INT);
  }

  /**
   * Returns {@link NumberMethods#ZERO_DOUBLE} the argument is null, else the argument itself.
   *
   * @param arg An argument of type {@code Double}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Double n2e(Double arg) {
    return ifNull(arg, NumberMethods.ZERO_DOUBLE);
  }

  /**
   * Returns {@link NumberMethods#ZERO_LONG} if the argument is null, else the argument itself.
   *
   * @param arg An argument of type {@code Long}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Long n2e(Long arg) {
    return ifNull(arg, NumberMethods.ZERO_LONG);
  }

  /**
   * Returns {@link NumberMethods#ZERO_FLOAT} if the argument is null, else the argument itself.
   *
   * @param arg An argument of type {@code Float}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Float n2e(Float arg) {
    return ifNull(arg, NumberMethods.ZERO_FLOAT);
  }

  /**
   * Returns {@link NumberMethods#ZERO_SHORT} if the argument is null, else the argument itself.
   *
   * @param arg An argument of type {@code Short}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Short n2e(Short arg) {
    return ifNull(arg, NumberMethods.ZERO_SHORT);
  }

  /**
   * Returns {@link NumberMethods#ZERO_BYTE} if the argument is null, else the argument itself.
   *
   * @param arg An argument of type {@code Byte}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Byte n2e(Byte arg) {
    return ifNull(arg, NumberMethods.ZERO_BYTE);
  }

  /**
   * Returns {@link Boolean#FALSE} if the argument is null, else the argument itself.
   *
   * @param arg An argument of type {@code Byte}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Boolean n2e(Boolean arg) {
    return ifNull(arg, Boolean.FALSE);
  }

  private static boolean eq(Object arg0, Object arg1) {
    if (arg0 instanceof Object[] && arg1 instanceof Object[]) {
      return arraysEqual((Object[]) arg0, (Object[]) arg1);
    } else if (arg0 instanceof List && arg1 instanceof List) {
      return listsEqual((List) arg0, (List) arg1);
    } else if (arg0 instanceof Set && arg1 instanceof Set) {
      return setsEqual((Set) arg0, (Set) arg1);
    } else if (arg0 instanceof Map && arg1 instanceof Map) {
      return mapsEqual((Map) arg0, (Map) arg1);
    }
    return Objects.deepEquals(arg0, arg1);
  }

  private static boolean arraysEqual(Object[] arr0, Object[] arr1) {
    if (arr0.length == arr1.length) {
      for (int i = 0; i < arr0.length; ++i) {
        if (!e2nDeepEquals(arr0[i], arr1[i])) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  private static boolean listsEqual(List list0, List list1) {
    if (list0.size() == list1.size()) {
      Iterator it0 = list0.iterator();
      Iterator it1 = list1.iterator();
      while (it0.hasNext()) {
        if (!it1.hasNext() || !e2nDeepEquals(it0.next(), it1.next())) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  private static boolean setsEqual(Set set0, Set set1) {
    Set s0 = (Set) set0.stream().map(ObjectMethods::e2n).collect(toSet());
    Set s1 = (Set) set1.stream().map(ObjectMethods::e2n).collect(toSet());
    if (s0.size() != s1.size()) {
      return false;
    } else if (s0.equals(s1)) {
      return true;
    }
    for (Object obj0 : s0) {
      boolean found = false;
      for (Object obj1 : s1) {
        if (e2nDeepEquals(obj0, obj1)) {
          found = true;
          s1.remove(obj0);
          break;
        }
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }

  private static boolean mapsEqual(Map map0, Map map1) {
    if (map0.size() == map1.size()) {
      for (Object k : map0.keySet()) {
        if (!map1.containsKey(k) || !e2nDeepEquals(map0.get(k), map1.get(k))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
