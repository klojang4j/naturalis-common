package nl.naturalis.common;

import java.lang.reflect.Array;

import static java.lang.System.arraycopy;

/**
 * Methods for working with arrays.
 */
public class ArrayMethods {

  private ArrayMethods() {}

  /**
   * A zero-length Object array
   */
  public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

  /**
   * A zero-length String array
   */
  public static final String[] EMPTY_STRING_ARRAY = new String[0];

  /**
   * Appends the provided object to the provided array.
   *
   * @param array
   * @param obj
   * @return
   */
  public static <T> T[] append(T[] array, T obj) {
    T[] res = fromTemplate(array, array.length + 1);
    arraycopy(array, 0, res, 0, array.length);
    res[array.length] = obj;
    return res;
  }

  /**
   * Appends the provided objects to the provided array.
   *
   * @param array
   * @param obj1
   * @param obj2
   * @return
   */
  public static <T> T[] append(T[] array, T obj1, T obj2) {
    T[] res = fromTemplate(array, array.length + 2);
    arraycopy(array, 0, res, 0, array.length);
    res[array.length] = obj1;
    res[array.length + 1] = obj2;
    return res;
  }

  /**
   * Appends the provided objects to the provided array.
   *
   * @param array
   * @param obj1
   * @param obj2
   * @param obj3
   * @param moreObjs
   * @return
   */
  @SafeVarargs
  public static <T> T[] append(T[] array, T obj1, T obj2, T obj3, T... moreObjs) {
    int sz = array.length + 3 + moreObjs.length;
    T[] res = fromTemplate(array, sz);
    arraycopy(array, 0, res, 0, array.length);
    res[array.length] = obj1;
    res[array.length + 1] = obj2;
    res[array.length + 2] = obj3;
    arraycopy(moreObjs, 0, res, array.length + 3, moreObjs.length);
    return res;
  }

  /**
   * Syntactic sugar avoiding <code>new Object[] {obj}</code> notation. Use as static import for increased sugar content.
   * 
   * @param <T>
   * @param objs
   * @return
   */
  @SafeVarargs
  public static <T> T[] toArray(T... objs) {
    return objs;
  }

  /**
   * Creates and returns a new array containing all elements of the provided arrays.
   *
   * @param <T> The element type of the arrays
   * @param array1 The 1st array to go into the new array
   * @param array2 The 2nd array to go into the new array (adjacent to the 1st array)
   * @return A new array containing all elements of the provided arrays
   */
  public static <T> T[] concat(T[] array1, T[] array2) {
    int x = array1.length + array2.length;
    T[] all = fromTemplate(array1, x);
    x = 0;
    arraycopy(array1, 0, all, x, array1.length);
    x += array1.length;
    arraycopy(array2, 0, all, x, array2.length);
    return all;
  }

  /**
   * Returns {@code true} if the provided array contains the specfied value. {@code false} otherwise.
   * 
   * @param array The array to search
   * @param value The value to search for
   * @return Whether or not the array contans the value
   */
  public static boolean contains(int[] array, int value) {
    return indexOf(array, value) != -1;
  }

  /**
   * Creates and returns a new array containing all elements of the provided arrays.
   *
   * @param <T> The element type of the arrays.
   * @param array1
   * @param array2
   * @param array3
   * @param moreArrays
   * @return
   */
  @SafeVarargs
  public static <T> T[] concat(T[] array1, T[] array2, T[] array3, T[]... moreArrays) {
    int sz = array1.length + array2.length + array3.length;
    for (T[] arr : moreArrays) {
      sz += arr.length;
    }
    T[] all = fromTemplate(array1, sz);
    sz = 0;
    arraycopy(array1, 0, all, sz, array1.length);
    sz += array1.length;
    arraycopy(array2, 0, all, sz, array2.length);
    sz += array2.length;
    arraycopy(array3, 0, all, sz, array3.length);
    if (moreArrays.length != 0) {
      sz += array3.length;
      for (T[] arr : moreArrays) {
        arraycopy(arr, 0, all, sz, arr.length);
        sz += arr.length;
      }
    }
    return all;
  }

  /**
   * Returns an array for a generic type, using the provided array as a template. The returned array will have the same length and element
   * type as the provided array.
   * 
   * @param <T> The type of the elements in the requested array
   * @param template An array with the same length and element type as the requested array
   */
  public static <T> T[] fromTemplate(T[] template) {
    return fromTemplate(template, template.length);
  }

  /**
   * Returns an array for a generic type, using the provided array as a template. The returned array will have the same element type as the
   * provded array. The length of the provided array is specified through the {@code length} parameter.
   * 
   * @param <T> The type of the elements in the requested array
   * @param template An array with the same element type as the requested array
   * @param length The desired length of the new array
   * @return A new array with the same length and element type as the provided array
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] fromTemplate(T[] template, int length) {
    return (T[]) Array.newInstance(template.getClass().getComponentType(), length);
  }

  /**
   * Returns the array index of the first occurrence of {@code value} within the provided array. Returns -1 if the array does not contain
   * the value.
   * 
   * @param array The array to search
   * @param value The value to search for
   * @return The array index of the value
   */
  public static int indexOf(int[] array, int value) {
    for (int i = 0; i < array.length; ++i) {
      if (array[i] == value) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Whether or not the provided array is null or has a zero length. The {@code array} argument is of type {@code Object} so we don't need
   * to overload for primitive and non-primitive arrays. An {@link IllegalArgumentException} is thrown if the provided object is not in fact
   * an array.
   * 
   * @param array The array to check
   * @return Whether it is null or empty
   * @throws IllegalArgumentException if the provided object is not an array.
   */
  public static boolean isEmpty(Object array) {
    if (array == null) {
      return true;
    }
    Check.argument(array.getClass().isArray(), "not an array: " + array.getClass().getName());
    return Array.getLength(array) == 0;
  }

  /**
   * Whether or not the provided array is neither null nor has a zero length. The {@code array} argument is of type {@code Object} so we
   * don't need to overload for primitive and non-primitive arrays. An {@link IllegalArgumentException} is thrown if the provided object is
   * not in fact an array.
   * 
   * @param array The array to check
   * @return Whether or not the provided array is neither null nor has a zero length
   * @throws IllegalArgumentException if the provided object is not an array.
   */
  public static boolean notEmpty(Object array) {
    return !isEmpty(array);
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
    T[] res = fromTemplate(array, array.length + 1);
    res[0] = obj;
    arraycopy(array, 0, res, 1, array.length);
    return res;
  }

  /**
   * Prefixes the provided objects to the provided array.
   * 
   * @param <T>
   * @param array
   * @param obj1
   * @param obj2
   * @return
   */
  public static <T> T[] prefix(T[] array, T obj1, T obj2) {
    T[] res = fromTemplate(array, array.length + 2);
    res[0] = obj1;
    res[1] = obj2;
    arraycopy(array, 0, res, 2, array.length);
    return res;
  }

  /**
   * Prefixes the provided objects to the provided array.
   *
   * @param array
   * @param obj1
   * @param obj2
   * @param obj3
   * @param moreObjs
   * @return
   */
  @SafeVarargs
  public static <T> T[] prefix(T[] array, T obj1, T obj2, T obj3, T... moreObjs) {
    int sz = array.length + 3 + moreObjs.length;
    T[] res = fromTemplate(array, sz);
    res[0] = obj1;
    res[1] = obj2;
    res[2] = obj3;
    arraycopy(moreObjs, 0, res, 3, moreObjs.length);
    arraycopy(array, 0, res, 3 + moreObjs.length, array.length);
    return res;
  }

}
