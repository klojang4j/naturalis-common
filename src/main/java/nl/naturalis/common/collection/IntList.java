package nl.naturalis.common.collection;

import static nl.naturalis.common.check.CommonChecks.notNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingIntConsumer;

public interface IntList {

  /**
   * Returns an unmodifiable {@code IntList} containing the integers in the specified collection.
   *
   * @param c The {@code Integer} collection to extract the integers from
   * @return An unmodifiable {@code IntList}
   */
  static IntList copyOf(Collection<Integer> c) {
    Check.notNull(c);
    int[] buf = new int[c.size()];
    int idx = 0;
    for (Integer val : c) {
      buf[idx++] = Check.that(val).is(notNull(), "Illegal null value at index %d", idx - 1).ok();
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
      // With IntArrayList we know for a fact that toArray() always returns a fresh copy of its
      // internal int array; no need to copy it again.
      return new UnmodifiableIntList(other.toArray());
    }
    int[] buf = new int[other.size()];
    System.arraycopy(other.toArray(), 0, buf, 0, buf.length);
    return new UnmodifiableIntList(buf);
  }

  /**
   * Returns an unmodifiable {@code IntList} wrapping the specified {@code int} array.
   *
   * @param ints The integer array to wrap
   * @return An unmodifiable {@code IntList} wrapping the specified {@code int} array
   */
  static IntList of(int... ints) {
    Check.notNull(ints);
    int[] buf = Arrays.copyOf(ints, ints.length);
    return new UnmodifiableIntList(buf);
  }

  void add(int i);

  void addAll(IntList other);

  void addAll(int... ints);

  int get(int index);

  void set(int index, int value);

  int size();

  boolean isEmpty();

  void clear();

  int[] toArray();

  IntStream stream();

  void forEach(IntConsumer action);

  <E extends Throwable> void forEachThrowing(ThrowingIntConsumer<E> action) throws E;
}
