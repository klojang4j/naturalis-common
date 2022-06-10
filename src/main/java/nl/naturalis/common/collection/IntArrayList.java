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
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.util.ResizeMethod.getMinIncrease;

/**
 * A {@code List} of {@code int} values.
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
   * Creates an {@code IntList} with the specified initial capacity and doubling it
   * each time it fills up.
   *
   * @param initialSize The initial capacity of the list
   */
  public IntArrayList(int initialSize) {
    this(initialSize, ResizeMethod.MULTIPLY, 2);
  }

  /**
   * Creates an {@code IntList} with the specified initial capacity and enlarging its
   * internal {@code int[]} array by the specified amount of elements each time it
   * fills up.
   */
  public IntArrayList(int initialCapacity, int resizeAmount) {
    this(initialCapacity, ResizeMethod.ADD, resizeAmount);
  }

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
   * Creates a new {@code IntList} containing the same integers as the specified
   * {@code IntList}.
   *
   * @param other The {@code IntList} to copy
   */
  public IntArrayList(IntList other) {
    Check.notNull(other, "IntList");
    if (other instanceof IntArrayList ial) {
      this.size = ial.size;
      this.resizeMethod = ial.resizeMethod;
      this.resizeAmount = ial.resizeAmount;
      this.buf = new int[size];
      arraycopy(ial.buf, 0, this.buf, 0, size);
    } else { // UnmodifiableIntList (IntList is sealed)
      this.buf = other.toArray();
      this.size = other.size();
      this.resizeMethod = ResizeMethod.MULTIPLY;
      this.resizeAmount = 2F;
    }
  }

  @Override
  public void add(int i) {
    if (size == buf.length) {
      increaseCapacity(1);
    }
    buf[size++] = i;
  }

  @Override
  public void addAll(IntList other) {
    Check.notNull(other);
    if (other instanceof IntArrayList ial) {
      int minIncrease = getMinIncrease(buf.length, size, ial.size);
      if (minIncrease > 0) {
        increaseCapacity(minIncrease);
      }
      arraycopy(ial.buf, 0, buf, size, ial.size);
      size += ial.size;
    } else { // UnmodifiableIntList
      addAll(((UnmodifiableIntList) other).buf);
    }
  }

  @Override
  public void addAll(int[] ints) {
    Check.notNull(ints);
    int len = ints.length;
    int minIncrease = getMinIncrease(buf.length, size, len);
    if (minIncrease > 0) {
      increaseCapacity(minIncrease);
    }
    arraycopy(ints, 0, buf, size, len);
    size += len;
  }

  @Override
  public int get(int index) {
    Check.on(ArrayIndexOutOfBoundsException::new, index)
        .is(gte(), 0)
        .is(lt(), buf.length);
    return buf[index];
  }

  @Override
  public void set(int index, int value) {
    Check.on(ArrayIndexOutOfBoundsException::new, index)
        .is(gte(), 0)
        .is(lt(), buf.length);
    buf[index] = value;
  }

  @Override
  public int size() {
    return size;
  }

  public int capacity() {
    return buf.length;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public void clear() {
    size = 0;
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

}
