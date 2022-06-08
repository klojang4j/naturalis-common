package nl.naturalis.common.collection;

import static nl.naturalis.common.check.CommonChecks.notNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingIntConsumer;

/**
 * Yes, we have one, too, while we await Valhalla.
 */
public interface IntList {

  /**
   * Returns an unmodifiable {@code IntList} containing the integers in the specified
   * collection.
   *
   * @param c The {@code Integer} collection to extract the integers from
   * @return An unmodifiable {@code IntList}
   */
  static IntList copyOf(Collection<Integer> c) {
    Check.notNull(c);
    int[] buf = new int[c.size()];
    int idx = 0;
    for (Integer val : c) {
      buf[idx++] = Check.that(val)
          .is(notNull(), "Illegal null value at index %d", idx - 1)
          .ok();
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
    if (other.getClass() == UnmodifiableIntList.class) {
      return other;
    } else if (other.getClass() == IntArrayList.class) {
      // With IntArrayList we know for a fact that toArray() always returns a
      // fresh copy of its internal int array; no need to copy it again.
      return new UnmodifiableIntList(other.toArray());
    }
    int[] buf = new int[other.size()];
    System.arraycopy(other.toArray(), 0, buf, 0, buf.length);
    return new UnmodifiableIntList(buf);
  }

  /**
   * Returns an unmodifiable {@code IntList} wrapping the specified {@code int}
   * array.
   *
   * @param ints The integer array to wrap
   * @return An unmodifiable {@code IntList} wrapping the specified {@code int} array
   */
  static IntList of(int... ints) {
    Check.notNull(ints);
    int[] buf = Arrays.copyOf(ints, ints.length);
    return new UnmodifiableIntList(buf);
  }

  /**
   * Appends the specified integer to this list.
   *
   * @param i The integer to append
   */
  void add(int i);

  /**
   * Appends the integers in the specified list to this list.
   *
   * @param other An {@code IntList} containing the integers to append
   */
  void addAll(IntList other);

  /**
   * Appends the specified integers to this list.
   *
   * @param ints The integers to append
   */
  void addAll(int... ints);

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
   * Clears the list.
   */
  void clear();

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
