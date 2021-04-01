package nl.naturalis.common.collection;

import java.util.Arrays;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.util.AugmentationType;
import static nl.naturalis.common.check.CommonChecks.GT;
import static nl.naturalis.common.check.CommonChecks.gt;
import static nl.naturalis.common.check.CommonChecks.gte;
import static nl.naturalis.common.check.CommonChecks.lt;

/**
 * A {@code List} of {@code int} values. The backing array is exposed via the {@link #toArray()}
 * method, which returns the backing array itself, rather than a copy of it.
 *
 * @author Ayco Holleman
 */
public class IntArrayList implements IntList {

  private final float eb;
  private final AugmentationType at;

  int[] buf;
  int cnt;

  /**
   * Creates an {@code IntList} with an initial capacity of 10 and doubling it each time it fills
   * up.
   */
  public IntArrayList() {
    this(10);
  }

  /**
   * Creates an {@code IntList} with the specified initial capacity and doubling it each time it
   * fills up.
   */
  public IntArrayList(int capacity) {
    this(capacity, 2, AugmentationType.MULTIPLY);
  }

  /**
   * Creates an {@code IntList} with the specified initial capacity and enlarging it by the
   * specified amount of elements each time it fills up.
   */
  public IntArrayList(int capacity, int enlargeBy) {
    this(capacity, enlargeBy, AugmentationType.ADD);
  }

  public IntArrayList(int capacity, float enlargeBy, AugmentationType incrementType) {
    this.buf = Check.that(capacity, "capacity").is(gt(), 0).ok(int[]::new);
    this.eb = Check.that(enlargeBy, "incrementBy").is(GT(), 0).ok();
    this.at = Check.notNull(incrementType, "incrementType").ok();
  }

  /**
   * Creates a new {@code IntList} containing the same integers as the specified {@code IntList}.
   * The initial capacity of the {@code IntList} is tightly sized to contain just those integers.
   *
   * @param other The {@code IntList} to copy
   */
  public IntArrayList(IntList other) {
    if (other.getClass() == IntArrayList.class) {
      IntArrayList that = (IntArrayList) other;
      this.eb = that.eb;
      this.at = that.at;
      this.cnt = that.cnt;
      this.buf = new int[cnt];
      System.arraycopy(that.buf, 0, this.buf, 0, cnt);
    } else {
      this.eb = 2F;
      this.at = AugmentationType.MULTIPLY;
      this.cnt = other.size();
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
    if (cnt == buf.length) {
      increaseCapacity(1);
    }
    buf[cnt++] = i;
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
    if (cnt + len > buf.length) {
      increaseCapacity(cnt + len - buf.length);
    }
    System.arraycopy(integers, 0, buf, cnt, len);
    cnt += len;
  }

  @Override
  public int get(int index) {
    Check.on(ArrayIndexOutOfBoundsException::new, index).is(gte(), 0).is(lt(), buf.length);
    return buf[index];
  }

  @Override
  public void set(int index, int value) {
    Check.on(ArrayIndexOutOfBoundsException::new, index).is(gte(), 0).is(lt(), buf.length);
    buf[index] = value;
  }

  @Override
  public int size() {
    return cnt;
  }

  public int capacity() {
    return buf.length;
  }

  @Override
  public boolean isEmpty() {
    return cnt == 0;
  }

  @Override
  public void clear() {
    cnt = 0;
  }

  @Override
  public int[] toArray() {
    // ImmutableIntArray relies on this class always returning a fresh copy!
    int[] b = new int[cnt];
    System.arraycopy(buf, 0, b, 0, cnt);
    return b;
  }

  @Override
  public IntStream stream() {
    return Arrays.stream(buf, 0, cnt);
  }

  @Override
  public void forEach(IntConsumer action) {
    stream().forEach(action);
  }

  @Override
  @SuppressWarnings("unlikely-arg-type")
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj.getClass() == UnmodifiableIntList.class) {
      return ((UnmodifiableIntList) obj).equals(this);
    } else if (obj.getClass() == IntArrayList.class) {
      if (this == obj) return true;
      IntArrayList that = (IntArrayList) obj;
      return cnt == that.cnt && Arrays.equals(buf, 0, cnt, that.buf, 0, cnt);
    } else if (obj instanceof IntList) {
      IntList that = (IntList) obj;
      return size() == that.size() && Arrays.equals(buf, 0, cnt, that.toArray(), 0, cnt);
    }
    return false;
  }

  private int hash;

  public int hashCode() {
    if (hash == 0) {
      hash = buf[0];
      for (int i = 1; i < cnt; ++i) {
        hash = hash * 31 + buf[i];
      }
    }
    return hash;
  }

  public String toString() {
    return stream().mapToObj(String::valueOf).collect(Collectors.joining(", ", "[", "]"));
  }

  private void increaseCapacity(int minIncrease) {
    int capacity = at.augment(buf.length, eb, minIncrease);
    int[] newBuf = new int[capacity];
    System.arraycopy(buf, 0, newBuf, 0, cnt);
    buf = newBuf;
  }
}
