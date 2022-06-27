package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.check.CommonChecks;
import nl.naturalis.common.function.ThrowingSupplier;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;
import static nl.naturalis.common.ArrayMethods.isElementOf;
import static nl.naturalis.common.ClassMethods.isPrimitiveArray;
import static nl.naturalis.common.check.CommonChecks.notNull;
import static nl.naturalis.common.check.CommonChecks.sameAs;

/**
 * General methods applicable to objects of any type.
 *
 * <h3>Container objects</h3>
 *
 * <p>Some methods in this class apply special logic (documented on the method
 * itself) when passed a
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
@SuppressWarnings({"rawtypes", "unchecked"})
public class ObjectMethods {

  private static final String ERR_NULL_OPTIONAL = "Optional must not be null";

  private static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = Map.of(int.class,
      0,
      boolean.class,
      Boolean.FALSE,
      double.class,
      0D,
      long.class,
      0L,
      float.class,
      0F,
      short.class,
      (short) 0,
      byte.class,
      (byte) 0,
      char.class,
      '\0');

  private ObjectMethods() {
    throw new UnsupportedOperationException();
  }

  /**
   * Whether the specified {@code String} is null or empty.
   *
   * @param arg The {@code String} to check
   * @return Whether it is null or empty
   */
  public static boolean isEmpty(String arg) {
    return arg == null || arg.isEmpty();
  }

  /**
   * Returns whether the specified {@code Collection} is null or empty.
   *
   * @param arg The {@code Collection} to check
   * @return Whether it is null or empty
   */
  public static boolean isEmpty(Collection arg) {
    return arg == null || arg.isEmpty();
  }

  /**
   * Returns whether the specified {@code Map} is null or empty.
   *
   * @param arg The {@code Map} to check
   * @return Whether it is null or empty
   */
  public static boolean isEmpty(Map arg) {
    return arg == null || arg.isEmpty();
  }

  /**
   * Returns whether the specified {@code Optional} is empty or contains an empty
   * object. This is the only {@code isNotEmpty} method that will throw an {@code
   * IllegalArgumentException} if the argument is null as {@code Optional} objects
   * should never be null.
   *
   * @param arg The {@code Optional} to check
   * @return Whether it is empty or contains an empty object
   */
  public static <T> boolean isEmpty(Optional<T> arg) {
    Check.that(arg).is(notNull(), ERR_NULL_OPTIONAL);
    return arg.isEmpty() || isEmpty(arg.get());
  }

  /**
   * Returns whether the specified array is null or empty.
   *
   * @param arg The array to check
   * @return Whether it is null or empty
   */
  public static boolean isEmpty(Object[] arg) {
    return arg == null || arg.length == 0;
  }

  /**
   * Returns whether the specified argument is null or empty. This method is (and can
   * be) used for broad-stroke methods like {@link #ifEmpty(Object, Object)} and
   * {@link CommonChecks#empty()}. Returns {@code true} if <i>any</i> of the
   * following applies:
   *
   * <p>
   *
   * <ul>
   *   <li>{@code arg} is {@code null}
   *   <li>{@code arg} is an empty {@link CharSequence}
   *   <li>{@code arg} is an empty {@link Collection}
   *   <li>{@code arg} is an empty {@link Map}
   *   <li>{@code arg} is a zero-length array
   *   <li>{@code arg} is an empty {@link Optional} <b>or</b> an {@code Optional} containing an
   *       empty object
   * </ul>
   *
   * <p>Otherwise this method returns {@code false}.
   *
   * @param arg The argument to check
   * @return Whether it is null or empty
   */
  public static boolean isEmpty(Object arg) {
    return arg == null
        || arg instanceof CharSequence cs && cs.length() == 0
        || arg instanceof Collection c && c.isEmpty()
        || arg instanceof Map m && m.isEmpty()
        || arg instanceof Object[] x && x.length == 0
        || isPrimitiveArray(arg) && Array.getLength(arg) == 0
        || arg instanceof Optional o && (o.isEmpty() || isEmpty(o.get()));
  }

  /**
   * Returns whether the specified {@code String} is neither null nor empty.
   *
   * @param arg The {@code String} to check
   * @return Whether it is neither null nor empty
   */
  public static boolean isNotEmpty(String arg) {
    return !isEmpty(arg);
  }

  /**
   * Returns whether the specified {@code Collection} is neither null nor empty.
   *
   * @param arg The {@code Collection} to check
   * @return Whether it is neither null nor empty
   */
  public static boolean isNotEmpty(Collection arg) {
    return !isEmpty(arg);
  }

  /**
   * Returns whether the specified {@code Map} is neither null nor empty.
   *
   * @param arg The {@code Map} to check
   * @return Whether it is neither null nor empty
   */
  public static boolean isNotEmpty(Map arg) {
    return !isEmpty(arg);
  }

  /**
   * Returns whether the specified {@code Optional} is neither empty nor contains an
   * empty object. This is the only {@code isNotEmpty} method that will throw an
   * {@code IllegalArgumentException} if the argument is null as {@code Optional}
   * objects should never be null.
   *
   * @param arg The {@code Optional} to check
   * @return Whether it is neither empty nor contains an empty object
   * @throws IllegalArgumentException If the argument is null
   */
  public static boolean isNotEmpty(Optional arg) throws IllegalArgumentException {
    return !isEmpty(arg);
  }

  /**
   * Returns whether the specified {@code Optional} is neither null nor empty.
   *
   * @param arg The {@code String} to check
   * @return Whether it is neither null nor empty
   */
  public static boolean isNotEmpty(Object[] arg) {
    return !isEmpty(arg);
  }

  /**
   * Verifies that the argument is neither null nor empty. Returns {@code true} if
   * <i>any</i> of the following applies:
   *
   * <p>
   *
   * <ul>
   *   <li>{@code obj} is a non-empty {@link CharSequence}
   *   <li>{@code obj} is a non-empty {@link Collection}
   *   <li>{@code obj} is a non-empty {@link Map}
   *   <li>{@code obj} is a non-zero-length array
   *   <li>{@code obj} is a non-empty {@link Optional} containing a non-empty object
   *   <li>{@code obj} is a non-null object of any other type
   * </ul>
   *
   * @param arg The object to be tested
   * @return Whether it is empty
   */
  public static boolean isNotEmpty(Object arg) {
    return !isEmpty(arg);
  }

  /**
   * Verifies that the argument is recursively non-empty. Returns {@code true} if
   * <i>any</i> of the following applies:
   *
   * <p>
   *
   * <ul>
   *   <li>{@code obj} is a non-empty {@link CharSequence}
   *   <li>{@code obj} is a non-empty {@link Collection} containing only <i>deep-not-empty</i>
   *       elements
   *   <li>{@code obj} is a non-empty {@link Map} containing only <i>deep-not-empty</i> keys and
   *       values
   *   <li>{@code obj} is a non-zero-length {@code Object[]} containing only <i>deep-not-empty</i>
   *       elements
   *   <li>{@code obj} is a non-zero-length array of primitives
   *   <li>{@code obj} is a non-empty {@link Optional} containing a <i>deep-not-empty</i> object
   *   <li>{@code obj} is a non-null object of any other type
   * </ul>
   *
   * @param obj The object to be tested
   * @return Whether it is recursively non-empty
   * @see CommonChecks#deepNotEmpty()
   */
  public static boolean isDeepNotEmpty(Object obj) {
    return obj != null
        && (!(obj instanceof CharSequence cs) || cs.length() > 0)
        && (!(obj instanceof Collection c) || dne(c))
        && (!(obj instanceof Map m) || dne(m))
        && (!(obj instanceof Object[] x) || dne(x))
        && (!(obj instanceof Optional o) || dne(o))
        && (!isPrimitiveArray(obj) || Array.getLength(obj) > 0);
  }

  private static boolean dne(Collection coll) {
    return !coll.isEmpty() && coll.stream().allMatch(ObjectMethods::isDeepNotEmpty);
  }

  private static boolean dne(Map map) {
    return !map.isEmpty() && map.entrySet()
        .stream()
        .allMatch(ObjectMethods::entryDeepNotEmpty);
  }

  private static boolean entryDeepNotEmpty(Object obj) {
    Map.Entry e = (Map.Entry) obj;
    return isDeepNotEmpty(e.getKey()) && isDeepNotEmpty(e.getValue());
  }

  private static boolean dne(Object[] arr) {
    return arr.length > 0 && Arrays.stream(arr)
        .allMatch(ObjectMethods::isDeepNotEmpty);
  }

  private static boolean dne(Optional opt) {
    return opt.isPresent() && isDeepNotEmpty(opt.get());
  }

  /**
   * Verifies that the argument is not null and, if it is array, {@link Collection}
   * or {@link Map}, does not contain any null values. It may still be an empty
   * array, {@code Collection} or {@code Map}. NB for maps, both keys and values are
   * tested for {@code null}.
   *
   * @param arg The object to be tested
   * @return Whether it is not null and does not contain any null values
   */
  public static boolean isDeepNotNull(Object arg) {
    if (arg == null) {
      return false;
    } else if (arg instanceof Object[] x) {
      return Arrays.stream(x).allMatch(notNull());
    } else if (arg instanceof Collection c) {
      return c.stream().allMatch(notNull());
    } else if (arg instanceof Map<?, ?> m) {
      return m.entrySet().stream()
          .allMatch(e -> e.getKey() != null && e.getValue() != null);
    }
    return true;
  }

  /**
   * Tests the provided arguments for equality using <i>empty-equals-null</i>
   * semantics. This is roughly equivalent to {@code Objects.equals(e2n(arg0),
   * e2n(arg1))}, except that {@code e2nEquals} <i>does</i> take the types of the two
   * arguments into account. So an empty {@code String} is not equal to an empty
   * {@code ArrayList} and an empty {@code ArrayList} is not equal to an empty {@code
   * HashSet}. (An empty {@code HashSet} <i>is</i> equal to an empty {@code TreeSet},
   * but that is just behaviour specified by the Collections Framework.)
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
   *   <li>A empty instance of one type is not equal to a empty instance of another non-comparable
   *       type
   * </ol>
   *
   * @param arg0 The 1st of the pair of objects to compare
   * @param arg1 The 2nd of the pair of objects to compare
   * @return Whether the provided arguments are equal using empty-equals-null
   *     semantics
   */
  public static boolean e2nEquals(Object arg0, Object arg1) {
    return (arg0 == arg1)
        || (arg0 == null && isEmpty(arg1))
        || (arg1 == null && isEmpty(arg0))
        || Objects.equals(arg0, arg1);
  }

  /**
   * Recursively tests the arguments for equality using <i>empty-equals-null</i>
   * semantics.
   *
   * @param arg0 The 1st of the pair of objects to compare
   * @param arg1 The 2nd of the pair of objects to compare
   * @return Whether the provided arguments are deeply equal using empty-equals-null
   *     semantics
   */
  public static boolean e2nDeepEquals(Object arg0, Object arg1) {
    return (arg0 == arg1)
        || (arg0 == null && isEmpty(arg1))
        || (arg1 == null && isEmpty(arg0))
        || eq(arg0, arg1);
  }

  /**
   * Generates a hash code for the provided object using <i>empty-equals-null</i>
   * semantics. Empty objects (whatever their type and including {@code null}) all
   * have the same hash code: 0 (zero)! If the argument is any array, this method is
   * recursively applied to the array's elements. Therefore {@code e2nHashCode} in
   * effect generates a "deep" hash code.
   *
   * @param obj The object to generate a hash code for
   * @return The hash code
   */
  public static int e2nHashCode(Object obj) {
    if (isEmpty(obj)) {
      return 0;
    }
    int hash;
    if (obj instanceof Collection<?> c) {
      hash = 1;
      for (Object o : c) {
        hash = hash * 31 + e2nHashCode(o);
      }
    } else if (obj instanceof Map<?, ?> m) {
      hash = 1;
      for (Object o : m.entrySet()) {
        for (Map.Entry e : m.entrySet()) {
          hash = hash * 31 + e2nHashCode(e.getKey());
          hash = hash * 31 + e2nHashCode(e.getValue());
        }
      }
    } else if (obj instanceof Object[] a) {
      hash = 1;
      for (Object o : a) {
        hash = hash * 31 + e2nHashCode(o);
      }
    } else if (obj.getClass().isArray()) {
      hash = ArrayMethods.hashCode(obj);
    } else {
      hash = obj.hashCode();
    }
    return hash;
  }

  /**
   * <p>Generates a hash code for the provided object using <i>empty-equals-null</i>
   * semantics. This variant of {@code hashCode()} includes the type of the argument
   * in the computation of the hash code. As with {@link #e2nEquals(Object, Object)},
   * this ensures that an empty {@code String} will not have the same hash code as an
   * empty {@code ArrayList}, and an empty {@code ArrayList} will not have the same
   * hash code as an empty {@code HashSet}. An empty {@code HashSet} <i>will</i> have
   * the same hash code as an empty {@code TreeSet}, because for Collection Framework
   * classes it is hash code of the base type that is included in the computation of
   * the hash code (i.e. {@code List.class}, {@code Set.class} and {@code
   * Map.class}).
   *
   * <p>Thus, using {@code e2nTypedHashCode} will lead to fewer hash collisions
   * than {@code e2nHashcode}. However, it may, in some circumstances break the
   * contract for {@link Object#hashCode()}. A class could define an {@code equals}
   * method that allows instances of it to be equal to instances of its superclass.
   * However, {@code e2nTypedHashCode} precludes this as the hash code for the class
   * and the superclass will be different.
   *
   * <p>Also note that this anyhow only becomes relevant if the collection of
   * objects you work with is remarkably heterogeneous: strings, empty strings, sets,
   * empty sets, empty lists, empty arrays, etc.
   *
   * @param obj The object to generate a hash code for
   * @return The hash code
   */
  public static int e2nTypedHashCode(Object obj) {
    if (obj == null) {
      return 0;
    }
    int hash;
    if (obj instanceof String s) {
      hash = String.class.hashCode();
      if (!s.isEmpty()) {
        hash = hash * 31 + s.hashCode();
      }
    } else if (obj instanceof List x) {
      hash = List.class.hashCode();
      for (Object o : x) {
        hash = hash * 31 + e2nTypedHashCode(o);
      }
    } else if (obj instanceof Set x) {
      hash = Set.class.hashCode();
      for (Object o : x) {
        hash = hash * 31 + e2nTypedHashCode(o);
      }
    } else if (obj instanceof Map<?, ?> x) {
      hash = Map.class.hashCode();
      for (Map.Entry e : x.entrySet()) {
        hash = hash * 31 + e2nTypedHashCode(e.getKey());
        hash = hash * 31 + e2nTypedHashCode(e.getValue());
      }
    } else if (obj instanceof Object[] x) {
      hash = x.getClass().hashCode();
      for (Object o : x) {
        hash = hash * 31 + e2nTypedHashCode(o);
      }
    } else if (obj.getClass().isArray()) {
      hash = obj.getClass().hashCode();
      if (Array.getLength(obj) != 0) {
        hash = ArrayMethods.hashCode(obj);
      }
    } else if (obj instanceof Optional o) {
      hash = Optional.class.hashCode();
      if (o.isPresent() && !isEmpty(o.get())) {
        hash = hash * 31 + e2nTypedHashCode(o.get());
      }
    } else {
      hash = obj.hashCode();
    }
    return hash;
  }

  /**
   * Generates a hash code for the provided arguments using using
   * <i>empty-equals-null</i> semantics. See {@link #hashCode()}.
   *
   * @param objs The objects to generate a hash code for
   * @return The hash code
   */
  public static int e2nHash(Object... objs) {
    if (objs == null) {
      return 0;
    }
    int hash = 1;
    for (Object obj : objs) {
      hash = hash * 31 + e2nHashCode(obj);
    }
    return hash;
  }

  /**
   * Returns the first argument if it is not null, else the second argument.
   *
   * @param value The value to return if not null
   * @param defVal The value to return if the first argument is {@code null}
   * @return A non-null value
   */
  public static <T> T ifNull(T value, T defVal) {
    return value == null ? defVal : value;
  }

  /**
   * Returns the first argument if it is not {@code null}, else the value produced by
   * the specified {@code Supplier}.
   *
   * @param <T> The type of the arguments and the return value
   * @param <E> The type of the exception that can potentially be thrown by the
   *     {@code Supplier}
   * @param value The value to return if it is not {@code null}
   * @param supplier The supplier of a default value
   * @return a non-null value
   */
  public static <T, E extends Exception> T ifNull(T value,
      ThrowingSupplier<? extends T, E> supplier) throws E {
    return value == null ? Check.notNull(supplier, "supplier").ok().get() : value;
  }

  /**
   * Returns the first argument if it is not empty (as per {@link #isEmpty(Object)}),
   * else the second argument.
   *
   * @param <T> The type of the arguments and the return value
   * @param value The value to return if it is not empty
   * @param defVal The value to return if the first argument is empty
   * @return a non-empty value
   */
  public static <T> T ifEmpty(T value, T defVal) {
    return isEmpty(value) ? defVal : value;
  }

  /**
   * Returns the first argument if it is not empty (as per {@link #isEmpty(Object)}),
   * else the value produced by the specified {@code Supplier} (which may be empty as
   * well).
   *
   * @param value The value to return if not empty
   * @param supplier The supplier of a default value if {@code value} is null
   * @param <T> The input and return type
   * @param <E> The exception potentially being thrown by the supplier as it
   * @return a non-empty value
   */
  public static <T, E extends Exception> T ifEmpty(T value,
      ThrowingSupplier<? extends T, E> supplier) throws E {
    return isEmpty(value) ? Check.notNull(supplier, "supplier").ok().get() : value;
  }

  /**
   * Returns this first argument if it is among the allowed values, else {@code
   * null}.
   *
   * @param value The value to test
   * @param allowedValues The values it is allowed to have
   * @param <T> The type of the involved values
   * @return The first argument or {@code null}
   */
  @SuppressWarnings("unchecked")
  public static <T> T nullUnless(T value, T... allowedValues) {
    return isElementOf(value, allowedValues) ? value : null;
  }

  /**
   * Returns {@code null} if the first argument is among the forbidden values, else
   * the first argument itself.
   *
   * @param <T> The type of the involved values
   * @param value The value to test
   * @param forbiddenValues The values it must <i>not</i> have
   * @return The first argument or {@code null}
   */
  @SuppressWarnings("unchecked")
  public static <T> T nullIf(T value, T... forbiddenValues) {
    return isElementOf(value, forbiddenValues) ? null : value;
  }

  /**
   * Returns the result of passing the specified argument to the specified {@code
   * Function} if the argument is not {@code null}, else returns {@code null}. For
   * example:
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
   * Returns the result of passing the specified argument to the specified {@code
   * Funtion} if the argument is not null, else a default value. For example:
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
   * Returns the result of passing the specified argument to the specified {@code
   * Funtion} if the argument is not {@link #isEmpty(Object) empty}, else returns
   * null.
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
   * Returns the result of passing the specified argument to the specified {@code
   * Funtion} if the argument is not {@link #isEmpty(Object) empty}, else a default
   * value.
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
   * Returns the primitive default for primitive types; {@code null} for any other
   * type.
   *
   * @param <T> The type of the class
   * @param type The class for which to retrieve the default value
   * @return The default value
   */
  @SuppressWarnings("unchecked")
  public static <T> T getTypeDefault(Class<T> type) {
    return Check.notNull(type, "type").isNot(sameAs(), void.class).ok().isPrimitive()
        ? (T) PRIMITIVE_DEFAULTS.get(type)
        : null;
  }

  /**
   * Empty-to-null: returns {@code null} if the argument is empty (as per {@link
   * #isEmpty(Object), else the argument itself.
   *
   * @param <T> The type of the argument
   * @param arg The argument
   * @return The argument itself if not empty, else {@code null}
   */
  public static <T> T e2n(T arg) {
    return isEmpty(arg) ? null : arg;
  }

  /**
   * Null-to-empty: returns an empty {@code String} if the argument is null, else the
   * argument itself.
   *
   * @param arg An argument of type {@code String}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static String n2e(String arg) {
    return ifNull(arg, StringMethods.EMPTY);
  }

  /**
   * Null-to-empty: returns {@link Collections#emptyList()} if the argument is null,
   * else the argument itself.
   *
   * @param arg An argument of type {@code List}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static <T> List<T> n2e(List<T> arg) {
    return ifNull(arg, Collections.emptyList());
  }

  /**
   * Null-to-empty: returns {@link Collections#emptySet()} if the argument is null,
   * else the argument itself.
   *
   * @param arg An argument of type {@code List}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static <T> Set<T> n2e(Set<T> arg) {
    return ifNull(arg, Collections.emptySet());
  }

  /**
   * Null-to-empty: returns {@link Collections#emptyMap()} if the argument is null,
   * else the argument itself.
   *
   * @param arg An argument of type {@code List}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static <K, V> Map<K, V> n2e(Map<K, V> arg) {
    return ifNull(arg, Collections.emptyMap());
  }

  /**
   * Returns zero if the argument is null, else the argument itself.
   *
   * @param arg An argument of type {@code Integer}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Integer n2e(Integer arg) {
    return ifNull(arg, 0);
  }

  /**
   * Returns zero the argument is null, else the argument itself.
   *
   * @param arg An argument of type {@code Double}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Double n2e(Double arg) {
    return ifNull(arg, 0D);
  }

  /**
   * Returns zero if the argument is null, else the argument itself.
   *
   * @param arg An argument of type {@code Long}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Long n2e(Long arg) {
    return ifNull(arg, 0L);
  }

  /**
   * Returns zero if the argument is null, else the argument itself.
   *
   * @param arg An argument of type {@code Float}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Float n2e(Float arg) {
    return ifNull(arg, 0F);
  }

  /**
   * Returns zero if the argument is null, else the argument itself.
   *
   * @param arg An argument of type {@code Short}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Short n2e(Short arg) {
    return ifNull(arg, (short) 0);
  }

  /**
   * Returns zero if the argument is null, else the argument itself.
   *
   * @param arg An argument of type {@code Byte}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Byte n2e(Byte arg) {
    return ifNull(arg, (byte) 0);
  }

  /**
   * Returns zero if the argument is null, else the argument itself.
   *
   * @param arg An argument of type {@code Byte}
   * @return The argument or the default value of the corresponding primitive type
   */
  public static Character n2e(Character arg) {
    return ifNull(arg, '\0');
  }

  /**
   * Returns {@link Boolean#FALSE} if the argument is null, else the argument
   * itself.
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
