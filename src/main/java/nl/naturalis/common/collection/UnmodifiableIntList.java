package nl.naturalis.common.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.gte;
import static nl.naturalis.common.check.CommonChecks.lt;

public final class UnmodifiableIntList implements IntList {

  public static UnmodifiableIntList of(int... ints) {
    Check.notNull(ints);
    int[] buf = Arrays.copyOf(ints, ints.length);
    return new UnmodifiableIntList(buf);
  }

  public static UnmodifiableIntList copyOf(IntList other) {
    if (other.getClass() == UnmodifiableIntList.class) {
      return new UnmodifiableIntList(((UnmodifiableIntList) other).buf);
    } else if (other.getClass() == IntArrayList.class) {
      // With IntArrayList we know for a fact that toArray() always returns a fresh copy of its
      // internal int array; no need to copy it again.
      return new UnmodifiableIntList(other.toArray());
    }
    int[] buf = new int[other.size()];
    System.arraycopy(other.toArray(), 0, buf, 0, buf.length);
    return new UnmodifiableIntList(buf);
  }

  public static UnmodifiableIntList copyOf(Collection<Integer> c) {
    int[] buf = new int[c.size()];
    int idx = 0;
    for (Integer val : c) {
      buf[idx++] = val;
    }
    return new UnmodifiableIntList(buf);
  }

  private final int[] buf;

  private UnmodifiableIntList(int[] buf) {
    this.buf = buf;
  }

  @Override
  public void add(int i) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addAll(IntList other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addAll(int... ints) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int get(int index) {
    Check.on(ArrayIndexOutOfBoundsException::new, index).is(gte(), 0).is(lt(), buf.length);
    return buf[index];
  }

  @Override
  public void set(int index, int value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size() {
    return buf.length;
  }

  @Override
  public boolean isEmpty() {
    return buf.length == 0;
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int[] toArray() {
    int[] b = new int[buf.length];
    System.arraycopy(buf, 0, b, 0, buf.length);
    return b;
  }

  @Override
  public IntStream stream() {
    return Arrays.stream(buf, 0, buf.length);
  }

  @Override
  public void forEach(IntConsumer action) {
    stream().forEach(action);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj.getClass() == UnmodifiableIntList.class) {
      return this == obj || Arrays.equals(buf, ((UnmodifiableIntList) obj).buf);
    } else if (obj.getClass() == IntArrayList.class) {
      return Arrays.equals(buf, ((IntArrayList) obj).buf);
    } else if (obj instanceof IntList) {
      IntList that = (IntList) obj;
      return size() == that.size() && Arrays.equals(buf, that.toArray());
    }
    return false;
  }

  private int hash;

  public int hashCode() {
    if (hash == 0) {
      hash = buf[0];
      for (int i = 1; i < buf.length; ++i) {
        hash = hash * 31 + buf[i];
      }
    }
    return hash;
  }

  public String toString() {
    return stream().mapToObj(String::valueOf).collect(Collectors.joining(", ", "[", "]"));
  }
}
