package nl.naturalis.common.collection;

import java.lang.reflect.Array;
import java.util.*;
import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.gte;

/**
 * A fixed-size, mutable {@code List} that does not perform range checking in its {@code get} and
 * {@code set} methods. Useful for package-private or intra-modular list exchanges with a high
 * number if reads and/or writes on the list. Since this is a fixed-size list the {@code get} and
 * {@code set} methods will "work" straight-away (provided you specify a valid list index).
 *
 * @author Ayco Holleman
 * @param <E>
 */
public class UnsafeList<E> implements List<E>, RandomAccess {

  private final E[] data;

  /**
   * Creates a new {@code UnsafeList} for the specified element type and with the specified
   * <i>size</i> (not capacity). All elements in the list are null.
   *
   * @param clazz
   * @param size
   */
  @SuppressWarnings("unchecked")
  public UnsafeList(Class<E> clazz, int size) {
    Check.notNull(clazz, "clazz");
    Check.that(size, "size").is(gte(), 0);
    this.data = (E[]) Array.newInstance(clazz, size);
  }

  /**
   * Creates a new {@code UnsafeList} from the specified {@code Collection}.
   *
   * @param c
   */
  @SuppressWarnings("unchecked")
  public UnsafeList(Collection<? extends E> c) {
    Check.notNull(c);
    this.data = (E[]) c.toArray();
  }

  /**
   * Creates a new {@code UnsafeList} from a copy of the specified array.
   *
   * @param array
   */
  public UnsafeList(E[] array) {
    this.data = ArrayMethods.fromTemplate(array);
  }

  @Override
  public int size() {
    return data.length;
  }

  @Override
  public boolean isEmpty() {
    return data.length == 0;
  }

  @Override
  public boolean contains(Object o) {
    return Arrays.stream(data).anyMatch(obj -> Objects.deepEquals(obj, 0));
  }

  @Override
  public Iterator<E> iterator() {
    return new Iterator<>() {
      private int idx;

      @Override
      public boolean hasNext() {
        return idx < data.length;
      }

      @Override
      public E next() {
        if (idx < data.length) {
          return data[idx++];
        }
        throw new NoSuchElementException("No more elements in FastList");
      }
    };
  }

  @Override
  public Object[] toArray() {
    Object[] objs = new Object[data.length];
    System.arraycopy(data, 0, objs, 0, data.length);
    return objs;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] toArray(T[] a) {
    if (a.length < data.length) {
      return (T[]) Arrays.copyOf(data, data.length, a.getClass());
    }
    System.arraycopy(data, 0, a, 0, data.length);
    return a;
  }

  @Override
  public boolean remove(Object o) {
    for (int i = 0; i < data.length; ++i) {
      if (Objects.equals(data[i], o)) {
        data[i] = null;
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    MAIN_LOOP:
    for (Object obj0 : c) {
      for (Object obj1 : data) {
        if (Objects.equals(obj0, obj1)) {
          continue MAIN_LOOP;
        }
      }
      return false;
    }
    return true;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    boolean changed = false;
    for (Object obj : c) {
      changed = remove(obj) || changed;
    }
    return changed;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    boolean changed = false;
    for (Object obj : c) {
      for (int i = 0; i < data.length; ++i) {
        if (!Objects.equals(data[i], obj)) {
          data[i] = null;
          changed = true;
        }
      }
    }
    return changed;
  }

  @Override
  public void clear() {
    Arrays.fill(data, null);
  }

  @Override
  public E get(int index) {
    return data[index];
  }

  @Override
  public E set(int index, E element) {
    E e = data[index];
    data[index] = element;
    return e;
  }

  @Override
  public E remove(int index) {
    E e = data[index];
    data[index] = null;
    return e;
  }

  @Override
  public int indexOf(Object o) {
    for (int i = 0; i < data.length; ++i) {
      if (Objects.equals(data[i], o)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public int lastIndexOf(Object o) {
    for (int i = data.length - 1; i >= 0; --i) {
      if (Objects.equals(data[i], o)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    E[] elems = ArrayMethods.fromTemplate(data, toIndex - fromIndex);
    System.arraycopy(data, fromIndex, elems, 0, elems.length);
    return new UnsafeList<>(elems);
  }

  /**
   * Throws an {@code UnsupportedOperationException}. List manipulation must be done through the
   * {@link #set(int, Object) set} method only.
   */
  @Override
  public boolean add(E e) {
    throw new UnsupportedOperationException();
  }

  /**
   * Throws an {@code UnsupportedOperationException}. List manipulation must be done through the
   * {@link #set(int, Object) set} method only.
   */
  @Override
  public void add(int index, E element) {
    throw new UnsupportedOperationException();
  }

  /**
   * Throws an {@code UnsupportedOperationException}. List manipulation must be done through the
   * {@link #set(int, Object) set} method only.
   */
  @Override
  public boolean addAll(Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  /**
   * Throws an {@code UnsupportedOperationException}. List manipulation must be done through the
   * {@link #set(int, Object) set} method only.
   */
  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  /** Throws an {@code UnsupportedOperationException}. */
  @Override
  public ListIterator<E> listIterator() {
    throw new UnsupportedOperationException();
  }

  /** Throws an {@code UnsupportedOperationException}. */
  @Override
  public ListIterator<E> listIterator(int index) {
    throw new UnsupportedOperationException();
  }
}
