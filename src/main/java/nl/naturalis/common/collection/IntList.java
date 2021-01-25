package nl.naturalis.common.collection;

import java.nio.BufferOverflowException;
import java.util.Arrays;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.util.IncrementType;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * A {@code List} of {@code int} values. The backing array is exposed via the {@link #toArray()}
 * method, which returns the backing array itself, rather than a copy of it.
 *
 * @author Ayco Holleman
 */
public class IntList {

  private final float ib;
  private final IncrementType it;

  private int[] buf;
  private int cnt;

  private int hash;

  /** Creates an {@code IntList} with an initial capacity of 10. */
  public IntList() {
    this(10);
  }

  public IntList(int capacity) {
    this(capacity, 2, IncrementType.FACTOR);
  }

  public IntList(int capacity, float incrementBy, IncrementType incrementType) {
    this.buf = Check.that(capacity, "capacity").is(gt(), 0).ok(int[]::new);
    this.ib = Check.that(incrementBy, "incrementBy").is(greaterThan(), 0).ok();
    this.it = Check.notNull(incrementType, "incrementType").ok();
  }

  /**
   * Creates a new {@code IntList} containing the same integers as the specified {@code IntList}.
   * The initial capacity of the {@code IntList} is tightly sized to contain just those integers.
   *
   * @param other The {@code IntList} to copy
   */
  public IntList(IntList other) {
    this.ib = other.ib;
    this.it = other.it;
    this.cnt = other.cnt;
    this.buf = new int[cnt];
    System.arraycopy(other.buf, 0, this.buf, 0, cnt);
  }

  public void add(int i) {
    if (cnt == buf.length) {
      increaseCapacity(1);
    }
    buf[cnt++] = i;
  }

  public void addAll(IntList other) {
    addAll(other.buf);
  }

  public void addAll(int... integers) {
    Check.notNull(integers, "integers");
    int len = integers.length;
    if (cnt + len > buf.length) {
      increaseCapacity(cnt + len - buf.length);
    }
    System.arraycopy(integers, 0, buf, cnt, len);
    cnt += len;
  }

  public int get(int index) {
    return Check.that(index, "index").is(gte(), 0).is(lt(), buf.length).ok(i -> buf[i]);
  }

  public int size() {
    return cnt;
  }

  public int capacity() {
    return buf.length;
  }

  public boolean isEmpty() {
    return cnt == 0;
  }

  public int[] toArray() {
    return Arrays.copyOfRange(buf, 0, cnt);
  }

  public IntStream stream() {
    return Arrays.stream(buf, 0, cnt);
  }

  public void forEach(IntConsumer action) {
    stream().forEach(action);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    IntList other = (IntList) obj;
    return cnt == other.cnt && Arrays.equals(buf, 0, cnt, other.buf, 0, cnt);
  }

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
    long capacity = calculateCapacity(minIncrease);
    Check.with(s -> new BufferOverflowException(), capacity).is(atMost(), Integer.MAX_VALUE);
    int[] newBuf = new int[(int) capacity];
    System.arraycopy(buf, 0, newBuf, 0, cnt);
    buf = newBuf;
  }

  private long calculateCapacity(int minIncrease) {
    switch (it) {
      case TERM:
        return buf.length + Math.max(minIncrease, (int) ib);
      case FACTOR:
        return Math.max(buf.length + minIncrease, buf.length * (int) ib);
      case PERCENTAGE:
      default:
        return Math.max(buf.length + minIncrease, buf.length * ((100 + (int) ib) / 100));
    }
  }
}
