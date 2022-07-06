package nl.naturalis.common.collection;

import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingIntConsumer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static nl.naturalis.common.ArrayMethods.box;
import static nl.naturalis.common.ArrayMethods.implodeInts;
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
  public void set(int index, int value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public OptionalInt indexOf(int value) {
    return ArrayMethods.indexOf(buf, value);
  }

  @Override
  public OptionalInt lastIndexOf(int value) {
    return ArrayMethods.lastIndexOf(buf, value);
  }

  @Override
  public void add(int value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(int index, int value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addAll(IntList other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addAll(int[] values) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addAll(int index, IntList other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addAll(int index, int[] values) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void removeByIndex(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeByValue(int value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(IntList list) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(int... values) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(IntList list) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(int... values) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
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
  public void trim(int newSize) {
    throw new UnsupportedOperationException();

  }

  @Override
  public int capacity() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setCapacity(int newCapacity) {
    throw new UnsupportedOperationException();

  }

  @Override
  public void sort() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void sortDescending() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int[] toArray() {
    int[] b = new int[buf.length];
    System.arraycopy(buf, 0, b, 0, buf.length);
    return b;
  }

  public List<Integer> toGenericList() {
    return List.of(ArrayMethods.box(buf));
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
    } else if (obj instanceof UnmodifiableIntList uil) {
      return Arrays.equals(buf, uil.buf);
    } else if (obj instanceof IntArrayList ial) {
      return Arrays.equals(buf, 0, size(), ial.buf, 0, size());
    }
    return false;
  }

  private int hash;
  private String str;

  public int hashCode() {
    if (hash == 0) {
      Arrays.hashCode(buf);
    }
    return hash;
  }

  public String toString() {
    if (str == null) {
      str = '[' + implodeInts(buf) + ']';
    }
    return str;
  }

}
