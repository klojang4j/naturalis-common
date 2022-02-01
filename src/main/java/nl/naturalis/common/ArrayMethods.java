package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.collection.ArrayCloakList;
import nl.naturalis.common.x.invoke.InvokeUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import static java.lang.System.arraycopy;
import static java.util.stream.Collectors.joining;
import static nl.naturalis.common.ObjectMethods.ifNull;
import static nl.naturalis.common.check.CommonChecks.*;

/** Methods for working with arrays. */
public final class ArrayMethods {

  private ArrayMethods() {}

  /** A zero-length Object array */
  public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

  /** A zero-length String array */
  public static final String[] EMPTY_STRING_ARRAY = new String[0];

  /**
   * Default separator for the various {@code implode} methods in this class and in {@link
   * CollectionMethods}: ", " (comma+space).
   */
  public static final String DEFAULT_IMPLODE_SEPARATOR = ", ";

  static final String START_INDEX = "Start index";
  static final String END_INDEX = "End index";

  /**
   * Appends the specified object to the specified array.
   *
   * @param array The array to append the object to
   * @param obj The object to append
   * @return A concatenation of {@code array} and {@code obj}
   */
  public static <T> T[] append(T[] array, T obj) {
    Check.notNull(array, "array");
    T[] arr = fromTemplate(array, array.length + 1);
    arraycopy(array, 0, arr, 0, array.length);
    arr[array.length] = obj;
    return arr;
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
    Check.notNull(moreObjs, "moreObjs");
    int sz = array.length + 2 + moreObjs.length;
    T[] arr = fromTemplate(array, sz);
    arraycopy(array, 0, arr, 0, array.length);
    arr[array.length] = obj0;
    arr[array.length + 1] = obj1;
    arraycopy(moreObjs, 0, arr, array.length + 2, moreObjs.length);
    return arr;
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
   * Returns {@code true} if the specified array contains the specified value, {@code false}
   * otherwise.
   *
   * @param value The value to search for
   * @param array The array to search
   * @return Whether the array contains the value
   */
  public static boolean inIntArray(int value, int... array) {
    return indexOf(array, value) != -1;
  }

  /**
   * Returns {@code true} if the specified array contains the specified value, {@code false}
   * otherwise.
   *
   * @param value The value to search for (may be null)
   * @param array The array to search
   * @return Whether the array contains the value
   */
  @SafeVarargs
  public static <T> boolean inArray(T value, T... array) {
    return indexOf(array, value) != -1;
  }

  /**
   * Returns {@code true} if the specified array contains the specified reference, {@code false}
   * otherwise.
   *
   * @param ref The reference to search for
   * @param array The array to search
   * @return Whether the array contains the specified reference
   */
  @SafeVarargs
  public static <T> boolean isOneOf(T ref, T... array) {
    return find(array, ref) != -1;
  }

  /**
   * Returns a new, empty array with the same element type and length as the specified array's
   * element type and length.
   *
   * @param <T> The type of the elements in the requested array
   * @param template An array with the same length and element type as the requested array
   */
  public static <T> T[] fromTemplate(T[] template) {
    return fromTemplate(template, template.length);
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
  public static <T> T[] fromTemplate(T[] template, int length) {
    Check.notNull(template, "template");
    Check.that(length, "length").is(gte(), 0);
    return InvokeUtils.newArray(template.getClass(), length);
  }

  /**
   * PHP-style implode method, concatenating the array elements using ", " (comma-space) as
   * separator. Optimized for {@code int[]} arrays.
   *
   * @param array The array to implode
   * @return A concatenation of the elements in the array.
   */
  public static String implodeInts(int[] array) {
    return implodeInts(array, DEFAULT_IMPLODE_SEPARATOR);
  }

  private static final IntFunction<String> INT_TO_STR = i -> "" + i;

  /**
   * PHP-style implode method, concatenating the array elements using the specified separator.
   * Optimized for {@code int[]} arrays.
   *
   * @param array The array to implode
   * @param separator The separator string
   * @return A concatenation of the elements in the array.
   */
  public static String implodeInts(int[] array, String separator) {
    return implodeInts(array, separator, -1);
  }

  /**
   * PHP-style implode method, concatenating at most {@code limit} array elements using ", "
   * (comma-space) as separator.
   *
   * @param array The array to implode
   * @param limit The maximum number of elements to collect. The specified number will be clamped to
   *     {@code array.length} (i.e. it's OK to specify a number greater than {@code array.length}).
   *     You can specify -1 as a shorthand for {@code array.length}.
   * @return A concatenation of the elements in the array.
   */
  public static String implodeInts(int[] array, int limit) {
    return implodeInts(array, DEFAULT_IMPLODE_SEPARATOR, limit);
  }

  /**
   * PHP-style implode method, concatenating at most {@code limit} array elements using ", "
   * (comma-space) as separator.
   *
   * @param array The array to implode
   * @param stringifier A {@code Function} that converts the array elements to strings
   * @return A concatenation of the elements in the array.
   */
  public static String implodeInts(int[] array, IntFunction<String> stringifier) {
    return implodeInts(array, stringifier, DEFAULT_IMPLODE_SEPARATOR, 0, -1);
  }

  /**
   * PHP-style implode method, concatenating at most {@code limit} array elements using the
   * specified separator.
   *
   * @param array The array to implode
   * @param separator The separator string
   * @param limit The maximum number of elements to collect. The specified number will be clamped to
   *     {@code array.length} (i.e. it's OK to specify a number greater than {@code array.length}).
   *     You can specify -1 as a shorthand for {@code array.length}.
   * @return A concatenation of the elements in the array.
   */
  public static String implodeInts(int[] array, String separator, int limit) {
    return implodeInts(array, INT_TO_STR, separator, 0, limit);
  }

  /**
   * PHP-style implode method, optimized for {@code int[]} arrays.
   *
   * @see CollectionMethods#implode(Collection, Function, String, int, int)
   * @param array The array to implode
   * @param stringifier A {@code Function} that converts the array elements to strings
   * @param separator The separator string
   * @param from The index of the element to begin the concatenation with (inclusive)
   * @param to The index of the element to end the concatenation with (exclusive). The specified
   *     number will be clamped to {@code array.length} (i.e. it's OK to specify a number greater
   *     than {@code array.length}). You can specify -1 as a shorthand for {@code array.length}.
   * @return A concatenation of the elements in the array.
   */
  public static String implodeInts(
      int[] array, IntFunction<String> stringifier, String separator, int from, int to) {
    Check.notNull(array, "array");
    Check.notNull(separator, "separator");
    Check.that(from, "from").is(gte(), 0).is(lte(), array.length);
    int x = to == -1 ? array.length : Math.min(to, array.length);
    Check.that(x, "to").is(gte(), from);
    return Arrays.stream(array, from, x).mapToObj(stringifier).collect(joining(separator));
  }

  /**
   * PHP-style implode method, concatenating the array elements using ", " (comma-space) as
   * separator. This method is primarily meant to implode primitive arrays, but you <i>can</i> use
   * it to implode any type of array. An {@link IllegalArgumentException} is thrown if {@code array}
   * is not an array.
   *
   * @see CollectionMethods#implode(Collection, String)
   * @param array The array to implode
   * @return A concatenation of the elements in the array.
   */
  public static String implodeAny(Object array) {
    return implodeAny(array, Objects::toString, DEFAULT_IMPLODE_SEPARATOR, 0, -1);
  }

  /**
   * PHP-style implode method, concatenating the array elements using the specified separator. This
   * method is primarily meant to implode primitive arrays, but you <i>can</i> use it to implode any
   * type of array. An {@link IllegalArgumentException} is thrown if {@code array} is not an array.
   *
   * @see CollectionMethods#implode(Collection, String)
   * @param array The array to implode
   * @param separator The separator string
   * @return A concatenation of the elements in the array.
   */
  public static String implodeAny(Object array, String separator) {
    return implodeAny(array, separator, -1);
  }

  /**
   * PHP-style implode method, concatenating the array elements using ", " (comma-space) as
   * separator. This method is primarily meant to implode primitive arrays, but you <i>can</i> use
   * it to implode any type of array. An {@link IllegalArgumentException} is thrown if {@code array}
   * is not an array.
   *
   * @see CollectionMethods#implode(Collection, String)
   * @param array The array to implode
   * @param stringifier A {@code Function} that converts the array elements to strings
   * @return A concatenation of the elements in the array.
   */
  public static String implodeAny(Object array, Function<Object, String> stringifier) {
    return implodeAny(array, stringifier, DEFAULT_IMPLODE_SEPARATOR, 0, -1);
  }

  /**
   * PHP-style implode method, concatenating at most {@code limit} array elements using ", "
   * (comma-space) as separator. This method is primarily meant to implode primitive arrays, but you
   * <i>can</i> use it to implode any type of array. An {@link IllegalArgumentException} is thrown
   * if {@code array} is not an array.
   *
   * @param array The array to implode
   * @param limit The maximum number of elements to collect. The specified number will be clamped to
   *     {@code array.length} (i.e. it's OK to specify a number greater than {@code array.length}).
   *     You can specify -1 as a shorthand for {@code array.length}.
   * @return A concatenation of the elements in the array.
   */
  public static String implodeAny(Object array, int limit) {
    return implodeAny(array, DEFAULT_IMPLODE_SEPARATOR, limit);
  }

  /**
   * PHP-style implode method, concatenating at most {@code limit} array elements using the
   * specified separator. This method is primarily meant to implode primitive arrays, but you
   * <i>can</i> use it to implode any type of array. An {@link IllegalArgumentException} is thrown
   * if {@code array} is not an array.
   *
   * @param array The array to implode
   * @param limit The maximum number of elements to collect. The specified number will be clamped to
   *     {@code array.length} (i.e. it's OK to specify a number greater than {@code array.length}).
   *     You can specify -1 as a shorthand for {@code array.length}.
   * @return A concatenation of the elements in the array.
   */
  public static String implodeAny(Object array, String separator, int limit) {
    return implodeAny(array, Objects::toString, separator, 0, limit);
  }

  /**
   * PHP-style implode method. This method is primarily meant to implode primitive arrays, but you
   * <i>can</i> use it to implode any type of array. An {@link IllegalArgumentException} is thrown
   * if {@code array} is not an array.
   *
   * @see CollectionMethods#implode(Collection, Function, String, int, int)
   * @param array The array to implode
   * @param stringifier A {@code Function} that converts the array elements to strings
   * @param separator The separator string
   * @param from The index of the element to begin the concatenation with (inclusive)
   * @param to The index of the element to end the concatenation with (exclusive). The specified
   *     number will be clamped to {@code array.length} (i.e. it's OK to specify a number greater
   *     than {@code array.length}). You can specify -1 as a shorthand for {@code array.length}.
   * @return A concatenation of the elements in the array.
   */
  public static String implodeAny(
      Object array, Function<Object, String> stringifier, String separator, int from, int to) {
    int len = Check.notNull(array, "array").is(array()).ok(Array::getLength);
    Check.notNull(separator, "separator");
    Check.that(from, "from").is(gte(), 0).is(lte(), len);
    int x = to == -1 ? len : Math.min(to, len);
    Check.that(x, "to").is(gte(), from);
    return IntStream.range(from, x)
        .mapToObj(i -> Array.get(array, i))
        .map(stringifier)
        .collect(joining(separator));
  }

  /**
   * PHP-style implode method, concatenating the array elements using ", " (comma-space) as
   * separator.
   *
   * @see CollectionMethods#implode(Collection)
   * @param array The collection to implode
   * @return A concatenation of the elements in the collection.
   */
  public static <T> String implode(T[] array) {
    return implode(array, DEFAULT_IMPLODE_SEPARATOR);
  }

  /**
   * PHP-style implode method, concatenating the array elements using the specified separator.
   *
   * @see CollectionMethods#implode(Collection, String)
   * @param array The array to implode
   * @param separator The separator string
   * @return A concatenation of the elements in the array.
   */
  public static <T> String implode(T[] array, String separator) {
    Check.notNull(array);
    return implode(array, Objects::toString, separator, 0, -1);
  }

  /**
   * PHP-style implode method, concatenating at most {@code limit} array elements using ", "
   * (comma+space) as separator.
   *
   * @see CollectionMethods#implode(Collection, int)
   * @param array The array to implode
   * @param limit The maximum number of elements to collect. The specified number will be clamped to
   *     {@code array.length} (i.e. it's OK to specify a number greater than {@code array.length}).
   *     You can specify -1 as a shorthand for {@code array.length}.
   * @return A concatenation of the elements in the array.
   */
  public static <T> String implode(T[] array, int limit) {
    return implode(array, DEFAULT_IMPLODE_SEPARATOR, limit);
  }

  /**
   * PHP-style implode method, concatenating at most {@code limit} array elements using ", "
   * (comma+space) as separator.
   *
   * @see CollectionMethods#implode(Collection, int)
   * @param array The array to implode
   * @param stringifier A {@code Function} that converts the array elements to strings
   * @return A concatenation of the elements in the array.
   */
  public static <T> String implode(T[] array, Function<T, String> stringifier) {
    return implode(array, stringifier, DEFAULT_IMPLODE_SEPARATOR, 0, -1);
  }

  /**
   * PHP-style implode method, concatenating at most {@code limit} array elements using the
   * specified separator.
   *
   * @see CollectionMethods#implode(Collection, String, int)
   * @param array The array to implode
   * @param separator The separator string
   * @param limit The maximum number of elements to collect. The specified number will be clamped to
   *     {@code array.length} (i.e. it's OK to specify a number greater than {@code array.length}).
   *     You can specify -1 as a shorthand for {@code array.length}.
   * @return A concatenation of the elements in the array.
   */
  public static <T> String implode(T[] array, String separator, int limit) {
    return implode(array, Objects::toString, separator, 0, limit);
  }

  /**
   * PHP-style implode method.
   *
   * @see CollectionMethods#implode(Collection, Function, String, int, int)
   * @param array The array to implode
   * @param stringifier A {@code Function} that converts the array elements to strings
   * @param separator The separator string
   * @param from The index of the element to begin the concatenation with (inclusive)
   * @param to The index of the element to end the concatenation with (exclusive). The specified
   *     number will be clamped to {@code array.length} (i.e. it's OK to specify a number greater
   *     than {@code array.length}). You can specify -1 as a shorthand for {@code array.length}.
   * @return A concatenation of the elements in the array.
   */
  public static <T> String implode(
      T[] array, Function<T, String> stringifier, String separator, int from, int to) {
    Check.notNull(array, "array");
    Check.notNull(stringifier, "stringifier");
    Check.notNull(separator, "separator");
    Check.that(from, "from").is(gte(), 0).is(lte(), array.length);
    int x = to == -1 ? array.length : Math.min(to, array.length);
    Check.that(x, "to").is(gte(), from);
    return Arrays.stream(array, from, x).map(stringifier).collect(joining(separator));
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
  private static <T> IntStream streamIndices(T[] array) {
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
    Check.notNull(array, "array");
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
    Check.notNull(moreObjs, "moreObjs");
    int sz = array.length + 2 + moreObjs.length;
    T[] res = fromTemplate(array, sz);
    res[0] = obj0;
    res[1] = obj1;
    arraycopy(moreObjs, 0, res, 2, moreObjs.length);
    arraycopy(array, 0, res, 2 + moreObjs.length, array.length);
    return res;
  }

  /**
   * Converts the specified {@code int} array into an unmodifiable {@code List<Integer>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Integer> asList(int[] values) {
    return List.of(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code float} array into an unmodifiable {@code List<Float>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Float> asList(float[] values) {
    return List.of(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code double} array into an unmodifiable {@code List<Double>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Double> asList(double[] values) {
    return List.of(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code long} array into an unmodifiable {@code List<Long>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Long> asList(long[] values) {
    return List.of(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code short} array into an unmodifiable {@code List<Short>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Short> asList(short[] values) {
    return List.of(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code byte} array into an unmodifiable {@code List<Byte>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Byte> asList(byte[] values) {
    return List.of(asWrapperArray(values));
  }

  /**
   * Converts the specified{@code char} array into an unmodifiable {@code List<Character>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Character> asList(char[] values) {
    return List.of(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code char} array into an unmodifiable {@code List<Character>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Boolean> asList(boolean[] values) {
    return List.of(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code int} array into an {@link ArrayCloakList<Integer>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Integer> cloak(int[] values) {
    return new ArrayCloakList<>(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code double} array into an {@link ArrayCloakList<Double>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Double> cloak(double[] values) {
    return new ArrayCloakList<>(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code long} array into an {@link ArrayCloakList<Long>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Long> cloak(long[] values) {
    return new ArrayCloakList<>(asWrapperArray(values));
  }
  /**
   * Converts the specified {@code float} array into an {@link ArrayCloakList<Float>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Float> cloak(float[] values) {
    return new ArrayCloakList<>(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code short} array into an {@link ArrayCloakList<Short>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Short> cloak(short[] values) {
    return new ArrayCloakList<>(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code byte} array into an {@link ArrayCloakList<Byte>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Byte> cloak(byte[] values) {
    return new ArrayCloakList<>(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code char} array into an {@link ArrayCloakList<Character>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Character> cloak(char[] values) {
    return new ArrayCloakList<>(asWrapperArray(values));
  }

  /**
   * Converts the specified {@code char} array into an {@link ArrayCloakList<Character>}.
   *
   * @param values the array elements.
   * @return a {@code List} containing the same elements in the same order
   */
  public static List<Boolean> cloak(boolean[] values) {
    return new ArrayCloakList<>(asWrapperArray(values));
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
    int[] arr = new int[values.length];
    IntStream.range(0, values.length).forEach(i -> arr[i] = ifNull(values[i], dfault));
    return arr;
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
    int[] arr = new int[values.length];
    for (int i = 0; i < values.length; ++i) {
      arr[i] = Check.that(values[i]).is(notNull(), ERR_NO_NULLS).ok();
    }
    return arr;
  }

  /**
   * Converts the specified {@code int} array to an {@code Integer} array.
   *
   * @param values The {@code int} array
   * @return The {@code Integer} array
   */
  public static Integer[] asWrapperArray(int[] values) {
    return Check.notNull(values).ok(Arrays::stream).boxed().toArray(Integer[]::new);
  }

  /**
   * Converts the specified {@code double} array to a {@code Double} array.
   *
   * @param values The {@code double} array
   * @return The {@code Double} array
   */
  public static Double[] asWrapperArray(double[] values) {
    return Check.notNull(values).ok(Arrays::stream).boxed().toArray(Double[]::new);
  }

  /**
   * Converts the specified {@code long} array to a {@code Long} array.
   *
   * @param values The {@code long} array
   * @return The {@code Long} array
   */
  public static Long[] asWrapperArray(long[] values) {
    return Check.notNull(values).ok(Arrays::stream).boxed().toArray(Long[]::new);
  }

  /**
   * Converts the specified {@code float} array to a {@code Float} array.
   *
   * @param values The {@code float} array
   * @return The {@code Float} array
   */
  public static Float[] asWrapperArray(float[] values) {
    Check.notNull(values);
    return IntStream.range(0, values.length).mapToObj(i -> values[i]).toArray(Float[]::new);
  }

  /**
   * Converts the specified {@code short} array to a {@code Short} array.
   *
   * @param values The {@code short} array
   * @return The {@code Short} array
   */
  public static Short[] asWrapperArray(short[] values) {
    Check.notNull(values);
    return IntStream.range(0, values.length).mapToObj(i -> values[i]).toArray(Short[]::new);
  }

  /**
   * Converts the specified {@code byte} array to a {@code Byte} array.
   *
   * @param values The {@code byte} array
   * @return The {@code Byte} array
   */
  public static Byte[] asWrapperArray(byte[] values) {
    Check.notNull(values);
    return IntStream.range(0, values.length).mapToObj(i -> values[i]).toArray(Byte[]::new);
  }

  /**
   * Converts the specified {@code char} array to a {@code Character} array.
   *
   * @param values The {@code char} array
   * @return The {@code Character} array
   */
  public static Character[] asWrapperArray(char[] values) {
    Check.notNull(values);
    return IntStream.range(0, values.length).mapToObj(i -> values[i]).toArray(Character[]::new);
  }

  /**
   * Converts the specified {@code boolean} array to a {@code Boolean} array.
   *
   * @param values The {@code boolean} array
   * @return The {@code Boolean} array
   */
  public static Boolean[] asWrapperArray(boolean[] values) {
    Check.notNull(values);
    return IntStream.range(0, values.length).mapToObj(i -> values[i]).toArray(Boolean[]::new);
  }
}
