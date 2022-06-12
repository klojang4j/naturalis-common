package nl.naturalis.common.collection;

import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingIntConsumer;
import nl.naturalis.common.util.ResizeMethod;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static java.lang.System.arraycopy;
import static nl.naturalis.common.ArrayMethods.implodeInts;
import static nl.naturalis.common.check.Check.fail;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.util.ResizeMethod.*;

/**
 * A mutable {@code List} of {@code int} values.
 *
 * @author Ayco Holleman
 */
public final class IntArrayList implements IntList {

  private final ResizeMethod resizeMethod;
  private final float resizeAmount;

  int[] buf;
  int size;

  /**
   * Creates an {@code IntList} with an initial capacity of 10.
   */
  public IntArrayList() {
    this(10);
  }

  /**
   * Creates an {@code IntList} with the specified initial capacity. Each time the
   * backing array reaches full capacity, it is resized to twice its length.
   * (However, see {@link ResizeMethod}.)
   *
   * @param initialCapacity The initial capacity of the list
   */
  public IntArrayList(int initialCapacity) {
    this(initialCapacity, MULTIPLY, 2);
  }

  /**
   * Creates an {@code IntList} with the specified initial capacity. Each time the
   * backing array reaches full capacity, it is enlarged by the specified amount.
   *
   * @param initialCapacity The initial capacity of the list
   * @param resizeAmount The (fixed) amount by which to enlarge it when it fills
   *     up
   */
  public IntArrayList(int initialCapacity, int resizeAmount) {
    this(initialCapacity, ADD, resizeAmount);
  }

  /**
   * Creates an {@code IntList} with the specified initial capacity. Each time the
   * backing array reaches full capacity, it is resized by applying the specified
   * {@link ResizeMethod} to the specified resize amount.
   *
   * @param initialCapacity The initial capacity of the list
   * @param resizeMethod The method to use for resizing the backing array
   * @param resizeAmount The resize amount
   */
  public IntArrayList(int initialCapacity,
      ResizeMethod resizeMethod,
      float resizeAmount) {
    Check.that(initialCapacity, "initialCapacity").is(gt(), 0);
    Check.notNull(resizeMethod, "resizeMethod");
    this.buf = new int[initialCapacity];
    this.resizeMethod = resizeMethod;
    this.resizeAmount = resizeAmount;
  }

  /**
   * Copy constructor. Creates a new {@code IntList} containing the same values as
   * the specified {@code IntList}.
   *
   * @param other The {@code IntList} to copy
   */
  public IntArrayList(IntList other) {
    Check.notNull(other, "IntList");
    if (other instanceof IntArrayList ial) {
      this.size = ial.size;
      this.resizeMethod = ial.resizeMethod;
      this.resizeAmount = ial.resizeAmount;
      this.buf = new int[Math.min(Integer.MAX_VALUE, size + 10)];
      arraycopy(ial.buf, 0, this.buf, 0, size);
    } else { // UnmodifiableIntList (IntList is sealed)
      this.buf = other.toArray();
      this.size = other.size();
      this.resizeMethod = MULTIPLY;
      this.resizeAmount = 2F;
    }
  }

  public void add(int value) {
    add(size, value);
  }

  public void add(int index, int value) {
    Check.that(index).is(indexInclusiveOf(), this);
    if (size == buf.length) {
      increaseCapacity(1);
    }
    if (index != size) {
      arraycopy(buf, index, buf, index + 1, size - index);
    }
    buf[index] = value;
    ++size;
  }

  public void addAll(IntList other) {
    addAll(size, other);
  }

  public void addAll(int[] values) {
    addAll(size, values);
  }

  public void addAll(int index, IntList other) {
    Check.on(indexOutOfBounds(), index, "index").is(indexInclusiveOf(), this);
    Check.notNull(other);
    int minIncrease = getMinIncrease(buf.length, size, other.size());
    if (minIncrease > 0) {
      increaseCapacity(minIncrease);
    }
    if (index != size) {
      arraycopy(buf, index, buf, index + other.size(), size - index);
    }
    arraycopy(getBuffer(other), 0, buf, index, other.size());
    size += other.size();
  }

  public void addAll(int index, int[] values) {
    Check.on(indexOutOfBounds(), index, "index").is(indexInclusiveOf(), this);
    Check.notNull(values);
    int minIncrease = getMinIncrease(buf.length, size, values.length);
    if (minIncrease > 0) {
      increaseCapacity(minIncrease);
    }
    if (index != size) {
      arraycopy(buf, index, buf, index + values.length, size - index);
    }
    arraycopy(values, 0, buf, index, values.length);
    size += values.length;
  }

  @Override
  public int get(int index) {
    Check.that(index, "index").is(indexOf(), this);
    return buf[index];
  }

  public void set(int index, int value) {
    Check.that(index, "index").is(indexOf(), this);
    buf[index] = value;
  }

  @Override
  public int size() {
    return size;
  }

  /**
   * Returns the current capacity of the list (the length of the backing array).
   *
   * @return The current capacity of the list
   */
  public int capacity() {
    return buf.length;
  }

  /**
   * Allows you to manually resize the backing array. The new capacity is allowed to
   * be less than the current capacity, and even less than the size of the list. So
   * this method can also be used as a truncate or a trim-to-size method.
   *
   * @param newCapacity The desired length of the backing array (may be less than
   *     its current length)
   * @see #trim(int)
   */
  public void resize(int newCapacity) {
    if (newCapacity != buf.length) {
      Check.that(newCapacity, "new capacity")
          .is(gte(), 0)
          .is(lte(), Integer.MAX_VALUE);
      size = Math.min(size, newCapacity);
      int[] newBuf = new int[newCapacity];
      arraycopy(buf, 0, newBuf, 0, size);
      buf = newBuf;
    }
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Clears the list. Note that this leaves the backing array untouched. It just
   * resets the internal cursor.
   */
  public void clear() {
    size = 0;
  }

  /**
   * Trims the list to the specified size. Note that this leaves the backing array
   * untouched. It just moves the internal cursor backwards.
   *
   * @param newSize The desired new size of the list (must be less than or equal
   *     to its current size)
   * @see #resize(int)
   */
  public void trim(int newSize) {
    size = Check.that(newSize, "new size").is(gte(), 0).is(lte(), size).ok();
  }

  @Override
  public int[] toArray() {
    int[] b = new int[size];
    arraycopy(buf, 0, b, 0, size);
    return b;
  }

  public List<Integer> toGenericList() {
    return ArrayMethods.asList(buf);
  }

  @Override
  public IntStream stream() {
    return Arrays.stream(buf, 0, size);
  }

  @Override
  public void forEach(IntConsumer action) {
    stream().forEach(action);
  }

  @Override
  public <E extends Throwable> void forEachThrowing(ThrowingIntConsumer<E> action)
      throws E {
    for (int i : buf) {
      action.accept(i);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (obj instanceof UnmodifiableIntList uil) {
      return size == uil.size() && Arrays.equals(buf, 0, size, uil.buf, 0, size);
    } else if (obj instanceof IntArrayList ial) {
      return size == ial.size && Arrays.equals(buf, 0, size, ial.buf, 0, size);
    }
    return false;
  }

  public int hashCode() {
    int hash = buf[0];
    for (int i = 1; i < size; ++i) {
      hash = hash * 31 + buf[i];
    }
    return hash;
  }

  public String toString() {
    return '[' + implodeInts(buf, size) + ']';
  }

  private void increaseCapacity(int minIncrease) {
    int capacity = resizeMethod.resize(buf.length, resizeAmount, minIncrease);
    int[] newBuf = new int[capacity];
    arraycopy(buf, 0, newBuf, 0, size);
    buf = newBuf;
  }

  private static int[] getBuffer(IntList other) {
    // IntList is sealed, and as far as we know only permits
    // IntArrayList and UnmodifiableIntList
    return other instanceof IntArrayList ial
        ? ial.buf
        : other instanceof UnmodifiableIntList uil
            ? uil.buf
            : fail(AssertionError::new);
  }

}
