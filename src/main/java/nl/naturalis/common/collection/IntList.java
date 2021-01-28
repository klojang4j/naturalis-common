package nl.naturalis.common.collection;

import java.util.Arrays;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.util.AugmentationType;
import static nl.naturalis.common.check.CommonChecks.greaterThan;
import static nl.naturalis.common.check.CommonChecks.gt;
import static nl.naturalis.common.check.CommonChecks.gte;
import static nl.naturalis.common.check.CommonChecks.lt;

/**
 * A {@code List} of {@code int} values. The backing array is exposed via the {@link #toArray()}
 * method, which returns the backing array itself, rather than a copy of it.
 *
 * @author Ayco Holleman
 */
public class IntList {

  private final float eb;
  private final AugmentationType at;

  private int[] buf;
  private int cnt;

  private int hash;

  /**
   * Creates an {@code IntList} with an initial capacity of 10 and doubling it each time it fills
   * up.
   */
  public IntList() {
    this(10);
  }

  /**
   * Creates an {@code IntList} with the specified initial capacity and doubling it each time it
   * fills up.
   */
  public IntList(int capacity) {
    this(capacity, 2, AugmentationType.MULTIPLY);
  }

  /**
   * Creates an {@code IntList} with the specified initial capacity and enlarging it by the
   * specified amount of elements each time it fills up.
   */
  public IntList(int capacity, int enlargeBy) {
    this(capacity, enlargeBy, AugmentationType.ADD);
  }

  public IntList(int capacity, float enlargeBy, AugmentationType incrementType) {
    this.buf = Check.that(capacity, "capacity").is(gt(), 0).ok(int[]::new);
    this.eb = Check.that(enlargeBy, "incrementBy").is(greaterThan(), 0).ok();
    this.at = Check.notNull(incrementType, "incrementType").ok();
  }

  /**
   * Creates a new {@code IntList} containing the same integers as the specified {@code IntList}.
   * The initial capacity of the {@code IntList} is tightly sized to contain just those integers.
   *
   * @param other The {@code IntList} to copy
   */
  public IntList(IntList other) {
    this.eb = other.eb;
    this.at = other.at;
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
    Check.notNull(other).then(o -> addAll(o.buf));
  }

  public void addAll(int... integers) {
    Check.notNull(integers);
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

  public void clear() {
    cnt = 0;
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
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
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
    int capacity = at.augment(buf.length, eb, minIncrease);
    int[] newBuf = new int[capacity];
    System.arraycopy(buf, 0, newBuf, 0, cnt);
    buf = newBuf;
  }
}
