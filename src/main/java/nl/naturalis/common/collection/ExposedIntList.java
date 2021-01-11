package nl.naturalis.common.collection;

import java.nio.BufferOverflowException;
import java.util.Arrays;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.util.IncrementType;
import static nl.naturalis.common.check.CommonChecks.atMost;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * A {@code List} of {@code int} values. The backing array is exposed via the {@link #toArray()}
 * method, which returns the backing array itself, rather than a copy of it.
 *
 * @author Ayco Holleman
 */
public class ExposedIntList {

  private final float ib;
  private final IncrementType it;

  private int[] buf;
  private int cnt;

  public ExposedIntList() {
    this(10);
  }

  public ExposedIntList(int size) {
    this(size, 2, IncrementType.FACTOR);
  }

  public ExposedIntList(int size, float incrementBy, IncrementType incrementType) {
    this.buf = Check.that(size, "size").is(gt(), 0).ok(int[]::new);
    this.ib = Check.that(incrementBy, "incrementBy").is(greaterThan(), 0).ok();
    this.it = Check.notNull(incrementType, "incrementType").ok();
  }

  public void add(int i) {
    if (cnt == buf.length) {
      increaseCapacity(1);
    }
    buf[cnt++] = i;
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
    return buf;
  }

  public IntStream stream() {
    return Arrays.stream(buf);
  }

  public void forEach(IntConsumer action) {
    stream().forEach(action);
  }

  private void increaseCapacity(int minIncrease) {
    long newSize;
    switch (it) {
      case TERM:
        newSize = buf.length + Math.max(minIncrease, (int) ib);
        break;
      case FACTOR:
        newSize = Math.max(buf.length + minIncrease, buf.length * (int) ib);
        break;
      case PERCENTAGE:
      default:
        newSize = Math.max(buf.length + minIncrease, buf.length * ((100 + (int) ib) / 100));
        break;
    }
    Check.with(s -> new BufferOverflowException(), newSize).is(atMost(), Integer.MAX_VALUE);
    int[] newBuf = new int[(int) newSize];
    System.arraycopy(buf, 0, newBuf, 0, cnt);
    buf = newBuf;
  }
}
