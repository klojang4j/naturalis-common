package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.unsafe.UnsafeList;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.lang.System.arraycopy;
import static java.util.stream.Collectors.joining;
import static nl.naturalis.common.ObjectMethods.ifNull;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.length;

/** Methods for working with arrays. */
public class ArrayMethods {

  private ArrayMethods() {}

  /** A zero-length Object array */
  public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

  /** A zero-length String array */
  public static final String[] EMPTY_STRING_ARRAY = new String[0];

  static final String START_INDEX = "Start index";
  static final String END_INDEX = "End index";
  static final String DEFAULT_IMPLODE_SEPARATOR = ", ";

  // Maximum length of an array
  private static final int MAX_SIZE = Integer.MAX_VALUE;

  /**
   * Appends the specified object to the specified array.
   *
   * @param array The array to append the object to
   * @param obj The object to append
   * @return A concatenation of {@code array} and {@code obj}
   */
  public static <T> T[] append(T[] array, T obj) {
    Check.notNull(array, "array").has(length(), lt(), MAX_SIZE);
    T[] res = fromTemplate(array, array.length + 1);
    arraycopy(array, 0, res, 0, array.length);
    res[array.length] = obj;
    return res;
  }

  /**
   * Appends the specified objects to the specified array.
   *
   * @param array The array to append the objects to
   * @param obj0 The 1st object to append
   * @param obj1 The 2nd object to append
   * @param moreObjs More objects to append
   * @return A concatenation of {@code array}, {@code obj1}, {@code obj2} and {@code moreObjs}
   */
  @SafeVarargs
  public static <T> T[] append(T[] array, T obj0, T obj1, T... moreObjs) {
    Check.notNull(array, "array");
    Check.notNull(moreObjs, "moreObjs").has(length(), x -> MAX_SIZE - array.length - 2 - x >= 0);
    int sz = array.length + 2 + moreObjs.length;
    T[] res = fromTemplate(array, sz);
    arraycopy(array, 0, res, 0, array.length);
    res[array.length] = obj0;
    res[array.length + 1] = obj1;
    arraycopy(moreObjs, 0, res, array.length + 2, moreObjs.length);
    return res;
  }

  /**
   * Returns the specified value <i>in</i> or <i>as</i> an array.
   *
   * <p>
   *
   * <ul>
   *   <li>If the value is {@code null}, a one-element array is returned with {@code null} as the
   *       value of the element
   *   <li>If the value is a {@code Collection}, the collection is converted to an array using
   *       {@link Collection#toArray()}
   *   <li>If the value is an array of a non-primitive type, it is returned as-is
   *   <li>If the value is an array of a primitive type, an array of the corresponding wrapper type
   *       is returned
   *   <li>Otherwise a one-element array is returned with the specified value as the value of the
   *       element
   * </ul>
   *
   * @param <T> The type of the elements in the returned array
   * @param val The value to convert
   * @return The pecified value <i>in</i> or <i>as</i> an array
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static <T> T[] asArray(Object val) {
    T[] objs;
    if (val == null) {
      objs = (T[]) new Object[1];
    } else if (val instanceof Collection) {
      objs = (T[]) ((Collection) val).toArray();
    } else if (val instanceof Object[]) {
      objs = (T[]) val;
    } else if (val.getClass() == int[].class) {
      objs = (T[]) asWrapperArray((int[]) val);
    } else if (val.getClass() == double[].class) {
      objs = (T[]) asWrapperArray((double[]) val);
    } else if (val.getClass() == byte[].class) {
      objs = (T[]) asWrapperArray((byte[]) val);
    } else if (val.getClass() == short[].class) {
      objs = (T[]) asWrapperArray((short[]) val);
    } else if (val.getClass() == float[].class) {
      objs = (T[]) asWrapperArray((float[]) val);
    } else if (val.getClass() == char[].class) {
      objs = (T[]) asWrapperArray((char[]) val);
    } else if (val.getClass() == boolean[].class) {
      objs = (T[]) asWrapperArray((boolean[]) val);
    } else {
      objs = (T[]) new Object[] {val};
    }
    return objs;
  }

  /**
   * Returns a new array containing all elements of the specified arrays.
   *
   * @param <T> The element type of the arrays
   * @param arr0 The 1st array to go into the new array
   * @param arr1 The 2nd array to go into the new array
   * @return A new array containing all elements of the specified arrays
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] concat(T[] arr0, T[] arr1) {
    return (T[]) concat(arr0, arr1, new Object[0][0]);
  }

  /**
   * Returns a new array containing all elements of the specified arrays.
   *
   * @param <T> The element type of the arrays.
   * @param arr0 The 1st array to go into the new array
   * @param arr1 The 2nd array to go into the new array
   * @param arr2 The 3rd array to go into the new array
   * @param moreArrays More arrays to concatenate
   * @return A new array containing all elements of the specified arrays
   */
  @SafeVarargs
  public static <T> T[] concat(T[] arr0, T[] arr1, T[] arr2, T[]... moreArrays) {
    Check.notNull(arr0, "arr0");
    Check.notNull(arr1, "arr1");
    Check.notNull(arr2, "arr2");
    Check.notNull(moreArrays, "moreArrays");
    long x = Arrays.stream(moreArrays).flatMap(Arrays::stream).count();
    long y = arr0.length + arr1.length + arr2.length + x;
    Check.that(y).is(atMost(), Integer.MAX_VALUE, "Concatenated array too large");
    int i = (int) y;
    T[] all = fromTemplate(arr0, i);
    i = 0;
    arraycopy(arr0, 0, all, i, arr0.length);
    i += arr0.length;
    arraycopy(arr1, 0, all, i, arr1.length);
    i += arr1.length;
    arraycopy(arr2, 0, all, i, arr2.length);
    if (moreArrays.length != 0) {
      i += arr2.length;
      for (T[] arr : moreArrays) {
        Check.that(arr).is(notNull(), "moreArrays must not contain null values");
        arraycopy(arr, 0, all, i, arr.length);
        i += arr.length;
      }
    }
    return all;
  }

  /**
   * Returns {@code true} if the specified array contains the specfied value, {@code false}
   * otherwise.
   *
   * @param value The value to search for
   * @param array The array to search
   * @return Whether or not the array contans the value
   */
  public static boolean inArray(int value, int... array) {
    return indexOf(array, value) != -1;
  }

  /**
   * Returns {@code true} if the specified array contains the specfied value, {@code false}
   * otherwise.
   *
   * @param value The value to search for (may be null)
   * @param array The array to search
   * @return Whether or not the array contans the value
   */
  @SafeVarargs
  public static <T> boolean inArray(T value, T... array) {
    return indexOf(array, value) != -1;
  }

  /**
   * Returns {@code true} if the specified array contains the specfied reference, {@code false}
   * otherwise.
   *
   * @param ref The reference to search for
   * @param array The array to search
   * @return Whether or not the array contans the specfied referenc
   */
  @SafeVarargs
  public static <T> boolean isOneOf(T ref, T... array) {
    return Arrays.stream(Check.notNull(array, "array").ok()).anyMatch(t -> t == ref);
  }

  /**
   * Returns a new, empty array with the same element type and length as the specified array's
   * element type and length.
   *
   * @param <T> The type of the elements in the requested array
   * @param template An array with the same length and element type as the requested array
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] fromTemplate(T[] template) {
    Check.notNull(template, "template");
    return (T[]) Array.newInstance(template.getClass().getComponentType(), template.length);
  }

  /**
   * Returns a new, empty array with the same element type as the specified array's element type.
   * The length of the returned array is specified through the {@code length} parameter.
   *
   * @param <T> The type of the elements in the requested array
   * @param template An array with the same element type as the requested array
   * @param length The desired length of the new array
   * @return A new array with the same length and element type as the specified array
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] fromTemplate(T[] template, int length) {
    Check.notNull(template, "template");
    Check.that(length, "length").is(gte(), 0);
    return (T[]) Array.newInstance(template.getClass().getComponentType(), length);
  }

  public static String implode(Object array) {
    return implode(array, ", ");
  }

  public static String implode(Object array, String separator) {
    Check.notNull(array, "array").is(array());
    Check.notNull(separator, "separator");
    int sz = Array.getLength(array);
    StringBuilder sb = new StringBuilder(sz * 8);
    for (int i = 0; i < sz; ++i) {
      if (i != 0) {
        sb.append(separator);
      }
      sb.append(Array.get(array, i));
    }
    return sb.toString();
  }

  /**
   * PHP-style implode method, concatenating the array elements with ", " (comma+space) as separator
   * string.
   *
   * @param array The collection to implode
   * @return A concatenation of the elements in the collection.
   */
  public static <T> String implode(T[] array) {
    return implode(array, ", ");
  }

  /**
   * PHP-style implode method, concatenating the array elements with ", " (comma+space) as separator
   * string.
   *
   * @param array The array to implode
   * @param stringifier A function converting the array elements to strings
   * @return A concatenation of the elements in the collection.
   */
  public static <T> String implode(T[] array, Function<T, String> stringifier) {
    Check.notNull(array);
    return implode(array, stringifier, DEFAULT_IMPLODE_SEPARATOR, 0, array.length);
  }

  /**
   * PHP-style implode method, concatenating the array elements using the specified separator
   * string.
   *
   * @param array The array to implode
   * @param separator The separator string
   * @return A concatenation of the elements in the array.
   */
  public static <T> String implode(T[] array, String separator) {
    Check.notNull(array);
    return implode(array, Objects::toString, separator, 0, array.length);
  }

  /**
   * PHP-style implode method, concatenating the array elements using the specified separator
   * string.
   *
   * @param array The array to implode
   * @param stringifier A function converting the array elements to strings
   * @param separator The separator string
   * @return A concatenation of the elements in the array.
   */
  public static <T> String implode(T[] array, Function<T, String> stringifier, String separator) {
    Check.notNull(array);
    return implode(array, stringifier, separator, 0, array.length);
  }

  /**
   * PHP-style implode method, concatenating the array elements using the specified separator
   * string.
   *
   * @param array The array to implode
   * @param limit The maximum number of elements to collect. Specify -1 for no maximum. Specifying a
   *     number greater than the length of the array is OK. It will be clamped to the array length.
   * @return A concatenation of the elements in the array.
   */
  public static <T> String implode(T[] array, int limit) {
    Check.notNull(array, "array");
    Check.that(limit, "limit").is(gte(), -1);
    int x = limit == -1 ? array.length : Math.min(limit, array.length);
    return implode(array, Objects::toString, DEFAULT_IMPLODE_SEPARATOR, 0, x);
  }

  /**
   * PHP-style implode method, concatenating the array elements using the specified separator
   * string.
   *
   * @param array The array to implode
   * @param stringifier A function converting the array elements to strings
   * @param separator The separator string
   * @param from The index of the element to begin the concatenation with (inclusive)
   * @param to The index of the element to end the concatenation with (exclusive)
   * @return A concatenation of the elements in the array.
   */
  public static <T> String implode(
      T[] array, Function<T, String> stringifier, String separator, int from, int to) {
    Check.notNull(array, "array");
    Check.notNull(stringifier, "stringifier");
    Check.notNull(separator, "separator");
    Check.that(from, "from").is(gte(), 0).is(lte(), array.length);
    Check.that(to, "to").is(gte(), from).is(lte(), array.length);
    return Arrays.stream(array, from, to).map(stringifier).collect(joining(separator));
  }

  /**
   * Returns the array index of the first occurrence of the specified value within the specified
   * array. Returns -1 if the array does not contain the value.
   *
   * @param array The array to search
   * @param value The value to search for
   * @return The array index of the value
   */
  public static int indexOf(int[] array, int value) {
    Check.notNull(array, "array");
    return IntStream.range(0, array.length).filter(i -> array[i] == value).findFirst().orElse(-1);
  }

  /**
   * Returns the array index of the first occurrence of the specified value within the specified
   * array, using {@link Objects#deepEquals(Object, Object) Objects.deepEquals} to identify the
   * occurrence. Returns -1 if the array does not contain the value. Searching for null is allowed.
   *
   * @param <T> The type of the elements within the array
   * @param array The array to search
   * @param value The value to search for (may be null)
   * @return The array index of the value
   */
  public static <T> int indexOf(T[] array, T value) {
    Check.notNull(array, "array");
    return streamIndices(array)
        .filter(i -> Objects.deepEquals(array[i], value))
        .findFirst()
        .orElse(-1);
  }

  /**
   * Returns the array index of the first occurrence of the specified value within the specified
   * array, using reference comparison to identify the occurrence. Returns -1 if the array does not
   * contain the value. Searching for null is <i>not</i> allowed.
   *
   * @param array The array to search
   * @param reference The reference to search for (must not be null)
   * @return The array index of the reference
   */
  public static int find(Object[] array, Object reference) {
    Check.notNull(array, "array");
    Check.notNull(reference, "reference");
    return streamIndices(array).filter(i -> array[i] == reference).findFirst().orElse(-1);
  }

  /**
   * Returns an {@code IntStream} of the indices of the specified array.
   *
   * @param <T> The component type of the array
   * @param array The array
   * @return a {@code Stream} of its indices
   */
  public static <T> IntStream streamIndices(T[] array) {
    return Check.notNull(array).ok(x -> IntStream.range(0, x.length));
  }

  /**
   * Simply returns the specified array, but allows for leaner code.
   *
   * <blockquote>
   *
   * <pre>{@code
   * String[] words0 = new String[] {"Hello", "world"};
   * String[] words1 = pack("Hello", "world");
   * }</pre>
   *
   * </blockquote>
   *
   * @param <T> The type of the objects to pack
   * @param objs The objects to pack
   * @return The packed objects
   */
  @SafeVarargs
  public static <T> T[] pack(T... objs) {
    return objs;
  }

  /**
   * Prefixes the specified object to the specified array.
   *
   * @param <T> The type of the array elements and the object to be prefixed
   * @param array The array to be prefixed
   * @param obj The object to prefix
   * @return A new array containing the specified object and the elements of the specified array
   */
  public static <T> T[] prefix(T[] array, T obj) {
    Check.notNull(array, "array").has(length(), lt(), MAX_SIZE);
    T[] res = fromTemplate(array, array.length + 1);
    res[0] = obj;
    arraycopy(array, 0, res, 1, array.length);
    return res;
  }

  /**
   * Prefixes the specified object to the specified array.
   *
   * @param <T> The type of the array elements and the object to be prefixed
   * @param array The array to be prefixed
   * @param obj0 The 1st object to prefix
   * @param obj1 The 2nd object to prefix
   * @param moreObjs More objects to prefix
   * @return A new array containing the specified objects and the elements of the specified array
   */
  @SafeVarargs
  public static <T> T[] prefix(T[] array, T obj0, T obj1, T... moreObjs) {
    Check.notNull(array, "array");
    Check.notNull(moreObjs, "moreObjs").has(length(), x -> MAX_SIZE - array.length - 2 - x >= 0);
    int sz = array.length + 2 + moreObjs.length;
    T[] res = fromTemplate(array, sz);
    res[0] = obj0;
    res[1] = obj1;
    arraycopy(moreObjs, 0, res, 2, moreObjs.length);
    arraycopy(array, 0, res, 2 + moreObjs.length, array.length);
    return res;
  }

  /**
   * Converts the specified {@code int} array into a fixed-size, but mutable {@code List<Integer>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Integer> asList(int[] values) {
    return Arrays.asList(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code float} array into a fixed-size, but mutable {@code List<Float>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Float> asList(float[] values) {
    return Arrays.asList(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code double} array into a fixed-size, but mutable {@code
   * List<Double>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Double> asList(double[] values) {
    return Arrays.asList(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code long} array into a fixed-size, but mutable {@code List<Long>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Long> asList(long[] values) {
    return Arrays.asList(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code short} array into a fixed-size, but mutable {@code List<Short>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Short> asList(short[] values) {
    return Arrays.asList(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code byte} array into a fixed-size, but mutable {@code List<Byte>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Byte> asList(byte[] values) {
    return Arrays.asList(asWrapperArray(values));
  }

  /**
   * Converts the specified{@code char} array into a fixed-size, but mutable {@code
   * List<Character>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Character> asList(char[] values) {
    return Arrays.asList(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code char} array into a fixed-size, but mutable {@code
   * List<Character>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Boolean> asList(boolean[] values) {
    return Arrays.asList(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code int} array into an {@link UnsafeList<Integer>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Integer> asUnsafeList(int[] values) {
    return new UnsafeList<>(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code float} array into an {@link UnsafeList<Float>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Float> asUnsafeList(float[] values) {
    return new UnsafeList<>(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code double} array into an {@link UnsafeList<Double>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Double> asUnsafeList(double[] values) {
    return new UnsafeList<>(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code long} array into an {@link UnsafeList<Long>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Long> asUnsafeList(long[] values) {
    return new UnsafeList<>(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code short} array into an {@link UnsafeList<Short>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Short> asUnsafeList(short[] values) {
    return new UnsafeList<>(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code byte} array into an {@link UnsafeList<Byte>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Byte> asUnsafeList(byte[] values) {
    return new UnsafeList<>(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code char} array into an {@link UnsafeList<Character>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Character> asUnsafeList(char[] values) {
    return new UnsafeList<>(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code char} array into an {@link UnsafeList<Character>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Boolean> asUnsafeList(boolean[] values) {
    return new UnsafeList<>(asWrapperArray(values));
  }

  private static final String ERR_NO_NULLS = "Array must not contain null values";

  /**
   * Converts the specified {@code Integer} array to an {@code int} array, substituting the
   * specified default value for {@code null} values in the source array.
   *
   * @param values The {@code Integer} array
   * @param dfault The {@code int} value to use for {@code null} values in the {@code Integer} array
   * @return The {@code int} array
   */
  public static int[] asPrimitiveArray(Integer[] values, int dfault) {
    Check.notNull(values);
    int[] vals = new int[values.length];
    IntStream.range(0, values.length).forEach(i -> vals[i] = ifNull(values[i], dfault));
    return vals;
  }

  /**
   * Converts the specified {@code Integer} array to an {@code int} array. The {@code Integer} array
   * must not contain null values.
   *
   * @param values The {@code Integer} array
   * @return The {@code int} array
   */
  public static int[] asPrimitiveArray(Integer[] values) {
    Check.notNull(values);
    int[] vals = new int[values.length];
    for (int i = 0; i < values.length; ++i) {
      vals[i] = Check.that(values[i]).is(notNull(), ERR_NO_NULLS).ok();
    }
    return vals;
  }

  /**
   * Converts the specified specified {@code int} array to an {@code Integer} array.
   *
   * @param values The {@code int} array
   * @return The {@code Integer} array
   */
  public static Integer[] asWrapperArray(int[] values) {
    Check.notNull(values);
    Integer[] vals = new Integer[values.length];
    IntStream.range(0, values.length).forEach(i -> vals[i] = values[i]);
    return vals;
  }

  /**
   * Converts the specified {@code float} array to a {@code Float} array.
   *
   * @param values The {@code float} array
   * @return The {@code Float} array
   */
  public static Float[] asWrapperArray(float[] values) {
    Check.notNull(values);
    Float[] vals = new Float[values.length];
    IntStream.range(0, values.length).forEach(i -> vals[i] = values[i]);
    return vals;
  }

  /**
   * Converts the specified {@code double} array to a {@code Double} array.
   *
   * @param values The {@code double} array
   * @return The {@code Double} array
   */
  public static Double[] asWrapperArray(double[] values) {
    Check.notNull(values);
    Double[] vals = new Double[values.length];
    IntStream.range(0, values.length).forEach(i -> vals[i] = values[i]);
    return vals;
  }

  /**
   * Converts the specified {@code long} array to a {@code Long} array.
   *
   * @param values The {@code long} array
   * @return The {@code Long} array
   */
  public static Long[] asWrapperArray(long[] values) {
    Check.notNull(values);
    Long[] vals = new Long[values.length];
    IntStream.range(0, values.length).forEach(i -> vals[i] = values[i]);
    return vals;
  }

  /**
   * Converts the specified {@code short} array to a {@code Short} array.
   *
   * @param values The {@code short} array
   * @return The {@code Short} array
   */
  public static Short[] asWrapperArray(short[] values) {
    Check.notNull(values);
    Short[] vals = new Short[values.length];
    IntStream.range(0, values.length).forEach(i -> vals[i] = values[i]);
    return vals;
  }

  /**
   * Converts the specified {@code byte} array to a {@code Byte} array.
   *
   * @param values The {@code byte} array
   * @return The {@code Byte} array
   */
  public static Byte[] asWrapperArray(byte[] values) {
    Check.notNull(values);
    Byte[] vals = new Byte[values.length];
    IntStream.range(0, values.length).forEach(i -> vals[i] = values[i]);
    return vals;
  }

  /**
   * Converts the specified {@code char} array to a {@code Character} array.
   *
   * @param values The {@code char} array
   * @return The {@code Character} array
   */
  public static Character[] asWrapperArray(char[] values) {
    Check.notNull(values);
    Character[] vals = new Character[values.length];
    IntStream.range(0, values.length).forEach(i -> vals[i] = values[i]);
    return vals;
  }

  /**
   * Converts the specified {@code boolean} array to a {@code Boolean} array.
   *
   * @param values The {@code boolean} array
   * @return The {@code Boolean} array
   */
  public static Boolean[] asWrapperArray(boolean[] values) {
    Check.notNull(values);
    Boolean[] vals = new Boolean[values.length];
    IntStream.range(0, values.length).forEach(i -> vals[i] = values[i]);
    return vals;
  }
}
