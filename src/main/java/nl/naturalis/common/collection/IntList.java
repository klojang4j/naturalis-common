package nl.naturalis.common.collection;

import static nl.naturalis.common.ArrayMethods.asPrimitiveArray;
import static nl.naturalis.common.check.CommonChecks.notNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingIntConsumer;

/**
 * Yes, we have one, too, while we await Valhalla. Note that {@code IntList} is a
 * sealed interface with just two implementations: one ({@link IntArrayList}) allows
 * mutations on the list and the other is immutable. Instance of the immutable
 * variant can only be obtained through the static factory methods on this
 * interface.
 */
public sealed interface IntList permits IntArrayList, UnmodifiableIntList {

  IntList EMPTY = new UnmodifiableIntList(new int[0]);

  /**
   * Returns an unmodifiable {@code IntList} containing the integers in the specified
   * collection.
   *
   * @param c The {@code Integer} collection to extract the integers from
   * @return An unmodifiable {@code IntList}
   */
  static IntList copyOf(Collection<Integer> c) {
    Check.notNull(c);
    if (c.isEmpty()) {
      return EMPTY;
    }
    int[] buf = new int[c.size()];
    int idx = 0;
    for (Integer i : c) {
      Check.that(i).is(notNull(), "collection must not contain null values", '\0');
      buf[idx++] = i;
    }
    return new UnmodifiableIntList(buf);
  }

  /**
   * Returns an unmodifiable copy of the specified {@code IntList}.
   *
   * @param other The {@code IntList} to extract the integers from
   * @return An unmodifiable {@code IntList}
   */
  static IntList copyOf(IntList other) {
    Check.notNull(other);
    if (other == EMPTY || other.isEmpty()) {
      return EMPTY;
    } else if (other.getClass() == UnmodifiableIntList.class) {
      return other;
    }
    return new UnmodifiableIntList(other.toArray());
  }

  /**
   * Returns an unmodifiable, empty {@code IntList}.
   *
   * @return An unmodifiable, empty {@code IntList}
   */
  static IntList of() {
    return EMPTY;
  }

  /**
   * Returns an unmodifiable {@code IntList} containing the provided element.
   *
   * @param e0 The one and only element in the list
   * @return An unmodifiable {@code IntList} containing the provided element
   */
  static IntList of(int e0) {
    int[] buf = new int[] {e0};
    return new UnmodifiableIntList(buf);
  }

  /**
   * Returns an unmodifiable {@code IntList} containing the provided elements.
   *
   * @param e0 The 1st element
   * @param e1 The 2nd element
   * @return An unmodifiable {@code IntList} containing the provided elements
   */
  static IntList of(int e0, int e1) {
    int[] buf = new int[] {e0, e1};
    return new UnmodifiableIntList(buf);
  }

  /**
   * Returns an unmodifiable {@code IntList} containing the provided elements.
   *
   * @param e0 The 1st element
   * @param e1 The 2nd element
   * @param e2 The 3rd element
   * @return An unmodifiable {@code IntList} containing the provided elements
   */
  static IntList of(int e0, int e1, int e2) {
    int[] buf = new int[] {e0, e1, e2};
    return new UnmodifiableIntList(buf);
  }

  /**
   * Returns an unmodifiable {@code IntList} containing the provided elements.
   *
   * @param e0 The 1st element
   * @param e1 The 2nd element
   * @param e2 The 3rd element
   * @param e3 The 4th element
   * @return An unmodifiable {@code IntList} containing the provided elements
   */
  static IntList of(int e0, int e1, int e2, int e3) {
    int[] buf = new int[] {e0, e1, e2, e3};
    return new UnmodifiableIntList(buf);
  }

  /**
   * Returns an unmodifiable {@code IntList} containing the provided elements.
   *
   * @param e0 The 1st element
   * @param e1 The 2nd element
   * @param e2 The 3rd element
   * @param e3 The 4th element
   * @param e4 The 5th element
   * @param moreElems More elements to include in the list
   * @return An unmodifiable {@code IntList} containing the provided elements
   */
  static IntList of(int e0, int e1, int e2, int e3, int e4, int... moreElems) {
    Check.notNull(moreElems, "array");
    int[] buf = new int[5 + moreElems.length];
    buf[0] = e0;
    buf[1] = e1;
    buf[2] = e2;
    buf[3] = e3;
    buf[4] = e4;
    System.arraycopy(moreElems, 0, buf, 5, moreElems.length);
    return new UnmodifiableIntList(buf);
  }

  /**
   * Returns an unmodifiable {@code IntList} containing the specified values.
   *
   * @param values An {@code int} array containing the values for the {@code
   *     IntList}
   * @return An unmodifiable {@code IntList} containing the specified values
   */
  static IntList of(int[] values) {
    Check.notNull(values, "array");
    if (values.length == 0) {
      return EMPTY;
    }
    int[] buf = Arrays.copyOf(values, values.length);
    return new UnmodifiableIntList(buf);
  }

  /**
   * Returns the value at the specified index.
   *
   * @param index The list index
   * @return The value at the specified index
   */
  int get(int index);

  /**
   * Sets the value at the specified index.
   *
   * @param index The list index
   * @param value The value
   */
  void set(int index, int value);

  /**
   * Returns an {@link OptionalInt} containing the array index of the first
   * occurrence of the specified value, or an empty {@link OptionalInt} if the value
   * is not present.
   *
   * @param value The value to search for
   * @return An {@link OptionalInt} containing the array index of the first
   *     occurrence of the specified value, or an empty {@link OptionalInt} if the
   *     value is not present
   */
  OptionalInt indexOf(int value);

  OptionalInt lastIndexOf(int value);

  /**
   * Appends the specified integer.
   *
   * @param value The integer to append
   */
  void add(int value);

  /**
   * Inserts the specified integer.
   *
   * @param index The index at which to insert the integer
   * @param value The integer to insert
   */
  void add(int index, int value);

  /**
   * Appends the integers in the specified list to this list.
   *
   * @param other An {@code IntList} containing the integers to append
   */
  void addAll(IntList other);

  /**
   * Appends the specified integer array.
   *
   * @param values The integers to append
   */
  void addAll(int[] values);

  /**
   * Inserts the integers in the specified list into this list.
   *
   * @param index The index at which to insert the integers
   * @param other The integers to insert
   */
  void addAll(int index, IntList other);

  /**
   * Inserts the specified integer array.
   *
   * @param index The index at which to insert the integers
   * @param values The integers to insert
   */
  void addAll(int index, int[] values);

  /**
   * Removes the element at the specified index
   *
   * @param index The index of the element to remove
   */
  void removeByIndex(int index);

  /**
   * Removes the first occurrence of the specified value in this {@code IntList}.
   *
   * @param value The value to remove
   * @return {@code true} if the value was present; {@code false} otherwise
   */
  boolean removeByValue(int value);

  /**
   * Removes the values contained in the specified {@code IntList} from this {@code
   * IntList}
   *
   * @param list The {@code IntList} containing the values to remove
   * @return Whether the list changed
   */
  boolean removeAll(IntList list);

  /**
   * Removes the specified values from this {@code IntList}.
   *
   * @param values The values to remove
   * @return Whether the list changed
   */
  boolean removeAll(int... values);

  /**
   * Removes the values contained in the specified {@code Collection} from this
   * {@code IntList}.
   *
   * @param c The {@code Collection IntList} containing the values to remove
   * @return Whether the list changed
   */
  boolean removeAll(Collection<?> c);

  /**
   * Removes all values that are not in the specified {@code IntList}.
   *
   * @param list An {@code IntList} containing the values to keep
   * @return Whether the list changed
   */
  boolean retainAll(IntList list);

  /**
   * Removes all values that are not in the specified {@code IntList}.
   *
   * @param values An array containing the values to keep
   * @return Whether the list changed
   */
  boolean retainAll(int... values);

  /**
   * Removes all values that are not in the specified {@code IntList}.
   *
   * @param c A {@code Collection} containing the values to keep
   * @return Whether the list changed
   */
  boolean retainAll(Collection<?> c);

  /**
   * Returns the current size of the list.
   *
   * @return The current size of the list.
   */
  int size();

  /**
   * Returns whether the list is empty.
   *
   * @return Whether the list is empty.
   */
  boolean isEmpty();

  /**
   * Clears the list. Note that this leaves the backing array untouched. It just
   * resets the internal cursor.
   */
  void clear();

  /**
   * Trims the list to the specified size. Note that this leaves the backing array
   * untouched. It just moves the internal cursor backwards.
   *
   * @param newSize The desired new size of the list (must be less than or equal
   *     to its current size)
   * @see #setCapacity(int)
   */
  void trim(int newSize);

  /**
   * Returns the current capacity of the list (the length of the backing array).
   *
   * @return The current capacity of the list
   */
  int capacity();

  /**
   * Resizes the backing array. The new capacity is allowed to be less than the
   * current capacity, and even less than the size of the list. So this method can
   * also be used as a truncate or a trim-to-size method.
   *
   * @param newCapacity The desired length of the backing array
   * @see #trim(int)
   */
  void setCapacity(int newCapacity);

  /**
   * Converts this {@code IntList} to an unmodifiable {@code List<Integer>}.
   *
   * @return An unmodifiable {@code List&lt;Integer&gt;}
   */
  List<Integer> toGenericList();

  /**
   * Converts the list to an {@code int[]} array
   *
   * @return An {@code int[]} array containing the values in this list
   */
  int[] toArray();

  /**
   * Returns an {@code IntStream} of the elements in this list.
   *
   * @return An {@code IntStream} of the elements in this list
   */
  IntStream stream();

  /**
   * Carries out the specified action for each of the elements in the list.
   *
   * @param action The action to carry out for each of the elements
   */
  void forEach(IntConsumer action);

  /**
   * Carries out the specified action for each of the elements in the list.
   *
   * @param action The action to carry out for each of the elements
   * @param <E> The type of the exception that the consumer is allowed to throw.
   * @throws E A (possibly checked) exception thrown from within the consumer
   */
  <E extends Throwable> void forEachThrowing(ThrowingIntConsumer<E> action) throws E;

}
