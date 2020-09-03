package nl.naturalis.common;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import static java.lang.System.arraycopy;

/** Methods for working with arrays. */
public class ArrayMethods {

  private ArrayMethods() {}

  /** A zero-length Object array */
  public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

  /** A zero-length String array */
  public static final String[] EMPTY_STRING_ARRAY = new String[0];

  /**
   * Appends the provided object to the provided array.
   *
   * @param array The array to append the object to
   * @param obj The object to append
   * @return A concatenation of {@code array} and {@code obj}
   */
  public static <T> T[] append(T[] array, T obj) {
    Check.notNull(array, "array");
    T[] res = fromTemplate(array, array.length + 1);
    arraycopy(array, 0, res, 0, array.length);
    res[array.length] = obj;
    return res;
  }

  /**
   * Appends the provided objects to the provided array.
   *
   * @param array The array to append the objects to
   * @param obj1 The 1st bject to append
   * @param obj2 The 2nd object to append
   * @param moreObjs More objects to append
   * @return A concatenation of {@code array}, {@code obj1}, {@code obj2} and {@code moreObjs}
   */
  @SafeVarargs
  public static <T> T[] append(T[] array, T obj1, T obj2, T... moreObjs) {
    Check.notNull(array, "array");
    Check.notNull(moreObjs, "moreObjs");
    int sz = array.length + 2 + moreObjs.length;
    T[] res = fromTemplate(array, sz);
    arraycopy(array, 0, res, 0, array.length);
    res[array.length] = obj1;
    res[array.length + 1] = obj2;
    arraycopy(moreObjs, 0, res, array.length + 2, moreObjs.length);
    return res;
  }

  /**
   * Returns a new array containing all elements of the provided arrays.
   *
   * @param <T> The element type of the arrays
   * @param array1 The 1st array to go into the new array
   * @param array2 The 2nd array to go into the new array
   * @return A new array containing all elements of the provided arrays
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] concat(T[] array1, T[] array2) {
    return (T[]) concat(array1, array2, EMPTY_OBJECT_ARRAY);
  }

  /**
   * Returns a new array containing all elements of the provided arrays.
   *
   * @param <T> The element type of the arrays.
   * @param array1 The 1st array to go into the new array
   * @param array2 The 2nd array to go into the new array
   * @param array3 The 3rd array to go into the new array
   * @param moreArrays More arrays to concatenate
   * @return A new array containing all elements of the provided arrays
   */
  @SafeVarargs
  public static <T> T[] concat(T[] array1, T[] array2, T[] array3, T[]... moreArrays) {
    long x =
        Check.notNull(array1, "array1").length
            + Check.notNull(array2, "array2").length
            + Check.notNull(array3, "array3").length
            + Arrays.stream(Check.noneNull(moreArrays, "moreArrays"))
                .flatMap(Arrays::stream)
                .count();
    Check.that(
        x <= Integer.MAX_VALUE, () -> new IllegalArgumentException("Concatenated array too large"));
    int i = (int) x;
    T[] all = fromTemplate(array1, i);
    i = 0;
    arraycopy(array1, 0, all, i, array1.length);
    i += array1.length;
    arraycopy(array2, 0, all, i, array2.length);
    i += array2.length;
    arraycopy(array3, 0, all, i, array3.length);
    if (moreArrays.length != 0) {
      i += array3.length;
      for (T[] arr : moreArrays) {
        arraycopy(arr, 0, all, i, arr.length);
        i += arr.length;
      }
    }
    return all;
  }

  /**
   * Returns {@code true} if the provided array contains the specfied value, {@code false}
   * otherwise.
   *
   * @param array The array to search
   * @param value The value to search for
   * @return Whether or not the array contans the value
   */
  public static boolean contains(int[] array, int value) {
    Check.notNull(array, "array");
    return indexOf(array, value) != -1;
  }

  /**
   * Returns a new, empty array with the same element type and length as the provided array's
   * element type and length.
   *
   * @param <T> The type of the elements in the requested array
   * @param template An array with the same length and element type as the requested array
   */
  public static <T> T[] fromTemplate(T[] template) {
    return fromTemplate(template, template.length);
  }

  /**
   * Returns a new, empty array with the same element type as the provided array's element type. The
   * length of the returned array is specified through the {@code length} parameter.
   *
   * @param <T> The type of the elements in the requested array
   * @param template An array with the same element type as the requested array
   * @param length The desired length of the new array
   * @return A new array with the same length and element type as the provided array
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] fromTemplate(T[] template, int length) {
    Check.notNull(template, "template");
    Check.gte(length, 0, "length");
    return (T[]) Array.newInstance(template.getClass().getComponentType(), length);
  }

  /**
   * Returns the array index of the first occurrence of {@code value} within the provided array.
   * Returns -1 if the array does not contain the value.
   *
   * @param array The array to search
   * @param value The value to search for
   * @return The array index of the value
   */
  public static int indexOf(int[] array, int value) {
    Check.notNull(array, "array");
    for (int i = 0; i < array.length; ++i) {
      if (array[i] == value) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns the array index of the first occurrence of {@code value} within the provided array.
   * Returns -1 if the array does not contain the value.
   *
   * @param array The array to search
   * @param value The value to search for
   * @return The array index of the value
   */
  public static int indexOf(Object[] array, Object value) {
    Check.notNull(array, "array");
    for (int i = 0; i < array.length; ++i) {
      if (Objects.deepEquals(array[i], value)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Whether or not the provided array is null or has a zero length. The {@code array} argument is
   * of type {@code Object} so we don't need to overload for primitive and non-primitive arrays. An
   * {@link IllegalArgumentException} is thrown if the provided object is not in fact an array.
   *
   * @param array The array to check
   * @return Whether it is null or empty
   * @throws IllegalArgumentException if the provided object is not an array.
   */
  public static boolean isEmpty(Object array) {
    if (array == null) {
      return true;
    }
    Check.argument(array.getClass().isArray(), "Array type required");
    return Array.getLength(array) == 0;
  }

  /**
   * Whether or not the provided array is neither null nor has a zero length. The {@code array}
   * argument is of type {@code Object} so we don't need to overload for primitive and non-primitive
   * arrays. An {@link IllegalArgumentException} is thrown if the provided object is not in fact an
   * array.
   *
   * @param array The array to check
   * @return Whether or not the provided array is neither null nor has a zero length
   * @throws IllegalArgumentException if the provided object is not an array.
   */
  public static boolean isNotEmpty(Object array) {
    return !isEmpty(array);
  }

  /**
   * Returns the provided array. Syntactic sugar avoiding code bloat. In stead of <code>
   * new Object[] {obj1, obj2, obj3, etc}</code> write <code>pack(obj1, obj2, obj3)</code>.
   *
   * @param <T>
   * @param objs
   * @return
   */
  @SafeVarargs
  public static <T> T[] pack(T... objs) {
    return objs;
  }

  /**
   * Prefixes the provided object to the provided array.
   *
   * @param <T> The type of the array elements and the object to be prefixed.
   * @param array The array to be prefixed.
   * @param obj The object to prefix.
   * @return A new array containing the provided object and the elements of the provided array.
   */
  public static <T> T[] prefix(T[] array, T obj) {
    Check.notNull(array, "array");
    T[] res = fromTemplate(array, array.length + 1);
    res[0] = obj;
    arraycopy(array, 0, res, 1, array.length);
    return res;
  }

  /**
   * Prefixes the provided objects to the provided array.
   *
   * @param array
   * @param obj1
   * @param obj2
   * @param moreObjs
   * @return
   */
  @SafeVarargs
  public static <T> T[] prefix(T[] array, T obj1, T obj2, T... moreObjs) {
    Check.notNull(array, "array");
    Check.notNull(moreObjs, "moreObjs");
    int sz = array.length + 2 + moreObjs.length;
    T[] res = fromTemplate(array, sz);
    res[0] = obj1;
    res[1] = obj2;
    arraycopy(moreObjs, 0, res, 2, moreObjs.length);
    arraycopy(array, 0, res, 2 + moreObjs.length, array.length);
    return res;
  }
}
