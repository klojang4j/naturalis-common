package nl.naturalis.common.collection;

import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingIntConsumer;
import nl.naturalis.common.util.ResizeMethod;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static nl.naturalis.common.ArrayMethods.implode;
import static nl.naturalis.common.ArrayMethods.implodeInts;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * A {@code List} of {@code int} values.
 *
 * @author Ayco Holleman
 */
public final class IntArrayList implements IntList {

  private final float resizeAmount;
  private final ResizeMethod resizeMethod;

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
   */
  /**
   * Creates an {@code IntList} with the specified initial capacity and doubling it
   * each time it fills up.
   *
   * @param capacity
   */
  public IntArrayList(int capacity) {
    this(capacity, ResizeMethod.MULTIPLY, 2);
  }

  /**
   * Creates an {@code IntList} with the specified initial capacity and enlarging its
   * internal {@code int[]} array by the specified amount of elements each time it
   * fills up.
   */
  public IntArrayList(int capacity, int resizeAmount) {
    this(capacity, ResizeMethod.ADD, resizeAmount);
  }

  public IntArrayList(int capacity, ResizeMethod resizeMethod, float resizeAmount) {
    this.buf = Check.that(capacity, "capacity").is(gt(), 0).ok(int[]::new);
    this.resizeMethod = Check.notNull(resizeMethod, "resizeMethod").ok();
    this.resizeAmount = Check.that(resizeAmount, "resizeAmount").is(GT(), 0F).ok();
  }

  /**
   * Creates a new {@code IntList} containing the same integers as the specified
   * {@code IntList}. The initial capacity of the {@code IntList} is tightly sized to
   * contain just those integers.
   *
   * @param other The {@code IntList} to copy
   */
  public IntArrayList(IntList other) {
    if (other.getClass() == IntArrayList.class) {
      IntArrayList that = (IntArrayList) other;
      this.resizeAmount = that.resizeAmount;
      this.resizeMethod = that.resizeMethod;
      this.size = that.size;
      this.buf = new int[size];
      System.arraycopy(that.buf, 0, this.buf, 0, size);
    } else {
      this.resizeAmount = 2F;
      this.resizeMethod = ResizeMethod.MULTIPLY;
      this.size = other.size();
      if (other.getClass() == UnmodifiableIntList.class) {
        this.buf = other.toArray();
      } else {
        this.buf = new int[other.size()];
        System.arraycopy(other.toArray(), 0, buf, 0, buf.length);
      }
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
    if (other.getClass() == IntArrayList.class) {
      addAll(((IntArrayList) other).buf);
    } else {
      addAll(other.toArray());
    }
  }

  @Override
  public void addAll(int... integers) {
    Check.notNull(integers);
    int len = integers.length;
    if (size + len > buf.length) {
      increaseCapacity(size + len - buf.length);
    }
    System.arraycopy(integers, 0, buf, size, len);
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
    System.arraycopy(buf, 0, b, 0, size);
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
  @SuppressWarnings("unlikely-arg-type")
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (obj.getClass() == UnmodifiableIntList.class) {
      return obj.equals(this);
    } else if (obj.getClass() == IntArrayList.class) {
      IntArrayList that = (IntArrayList) obj;
      return size == that.size && Arrays.equals(buf, 0, size, that.buf, 0, size);
    } else if (obj instanceof IntList that) {
      return size() == that.size()
          && Arrays.equals(buf, 0, size, that.toArray(), 0, size);
    }
    return false;
  }

  private int hash;

  public int hashCode() {
    if (hash == 0) {
      hash = buf[0];
      for (int i = 1; i < size; ++i) {
        hash = hash * 31 + buf[i];
      }
    }
    return hash;
  }

  public String toString() {
    return '[' + implodeInts(buf) + ']';
  }

  private void increaseCapacity(int minIncrease) {
    int capacity = resizeMethod.resize(buf.length, resizeAmount, minIncrease);
    int[] newBuf = new int[capacity];
    System.arraycopy(buf, 0, newBuf, 0, size);
    buf = newBuf;
  }

}
