package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingIntConsumer;

import java.util.Arrays;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static nl.naturalis.common.check.CommonChecks.gte;
import static nl.naturalis.common.check.CommonChecks.lt;

final class UnmodifiableIntList implements IntList {

  final int[] buf;

  UnmodifiableIntList(int[] buf) {
    this.buf = buf;
  }

  @Override
  public int get(int index) {
    Check.on(ArrayIndexOutOfBoundsException::new, index)
        .is(gte(), 0)
        .is(lt(), buf.length);
    return buf[index];
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
    stream().forEach(Check.notNull(action).ok());
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
      return Arrays.equals(buf, uil.buf);
    } else if (obj instanceof IntArrayList ial) {
      return size() == ial.size()
          && Arrays.equals(buf, 0, size(), ial.buf, 0, ial.size);
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
    return stream().mapToObj(String::valueOf)
        .collect(Collectors.joining(", ", "[", "]"));
  }

}
