package nl.naturalis.common.unsafe;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ArrayMethods.EMPTY_OBJECT_ARRAY;
import static nl.naturalis.common.check.CommonChecks.gte;

/**
 * A fixed-size {@code List} implementation that does not perform range checking and exposes its
 * backing array. Useful for package-private and/or intra-modular list exchanges with a high number
 * if reads and/or writes on the list. Since this is a fixed-size list, you can immediately {@code
 * get} and {@code set} values, provided you specify a valid list index. All {@code add} methods
 * throw an {@code UnsupportedOperationException}; list manipulation <i>must</i> be done via the
 * {@code set} method. The {@code remove} methods, however, have been repurposed to nullify list
 * elements.
 *
 * @param <E> The type of the list elements
 * @author Ayco Holleman
 */
public class UnsafeList<E> implements List<E>, RandomAccess {

  private final E[] data;

  /**
   * Creates a new {@code UnsafeList} for the specified element type and with the specified
   * <i>size</i> (not capacity). All elements in the list are null.
   *
   * @param clazz The class of the list elements
   * @param size The desired size of the list
   */
  @SuppressWarnings("unchecked")
  public UnsafeList(Class<E> clazz, int size) {
    Check.notNull(clazz, "clazz");
    Check.that(size, "size").is(gte(), 0);
    this.data = (E[]) Array.newInstance(clazz, size);
  }

  /**
   * Creates a new {@code UnsafeList} using the specified function the create a backing array of the
   * specified size.
   *
   * @param constructor A function that produces the backing array
   * @param size The size of the backing array
   */
  public UnsafeList(IntFunction<E[]> constructor, int size) {
    Check.notNull(constructor, "constructor");
    Check.that(size, "size").is(gte(), 0);
    this.data = constructor.apply(size);
  }

  /**
   * Creates a new {@code UnsafeList} from the specified {@code Collection}.
   *
   * @param c The collection from which to created the {@code UnsafeList}
   */
  @SuppressWarnings("unchecked")
  public UnsafeList(Collection<? extends E> c) {
    Check.notNull(c);
    this.data = (E[]) c.toArray();
  }

  /**
   * Creates a new {@code UnsafeList} from the specified array. The array is "swallowed" by the
   * {@code UnsafeList}, so any externally performed updates on the array are visible to the {@code
   * UnsafeList}.
   *
   * @param array The array from which to create the {@code UnsafeList}
   */
  public UnsafeList(E[] array) {
    this.data = array;
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
  public int size() {
    return data.length;
  }

  @Override
  public boolean isEmpty() {
    return data.length == 0;
  }

  @Override
  public boolean contains(Object o) {
    return indexOf(o) != -1;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    for (Object obj : Check.notNull(c).ok()) {
      if (!contains(obj)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Iterator<E> iterator() {
    return new Iterator<>() {
      private int idx = 0;

      @Override
      public boolean hasNext() {
        return idx < data.length;
      }

      @Override
      public E next() {
        if (idx < data.length) {
          return data[idx++];
        }
        throw new NoSuchElementException("Cannot move beyond end of list");
      }
    };
  }

  /**
   * Repurposed to nullify the element at the specified index. Note, however, that this method will
   * throw an {@link UnsupportedOperationException} if the type parameter for this {@UnsafeList} is
   * {@code Integer}, because Java's auto-boxing/auto-unboxing feature would make the method
   * indistinguishable from {@code remove(Object o)}.
   */
  @Override
  public E remove(int index) {
    if (size() == 0) {
      return null;
    }
    if (data[0].getClass() == Integer.class) {
      String msg =
          "Method remove(int index) not supported for UnsafeList<Integer>. "
              + "Auto-boxing makes it indistinguishable from remove(Object o)";
      throw new UnsupportedOperationException(msg);
    }
    E e = data[index];
    data[index] = null;
    return e;
  }

  /**
   * Repurposed to nullify the first list element that equals specified object. Note, however, that
   * this method will throw an {@link UnsupportedOperationException} if the type parameter for this
   * {@UnsafeList} is {@code Integer}, because Java's auto-boxing/auto-unboxing would make the
   * method indistinguishable from {@code remove(int index)}.
   */
  @Override
  public boolean remove(Object o) {
    if (size() == 0) {
      return false;
    }
    if (data[0].getClass() == Integer.class) {
      String msg =
          "Method remove(int index) not supported for UnsafeList<Integer>. "
              + "Auto-unboxing makes it indistinguishable from remove(Object o)";
      throw new UnsupportedOperationException(msg);
    }
    Check.notNull(o);
    for (int i = 0; i < data.length; ++i) {
      if (Objects.equals(data[i], o)) {
        data[i] = null;
        return true;
      }
    }
    return false;
  }

  /**
   * Repurposed to nullify all list elements whose value is present in the specified {@code
   * Collection}.
   */
  @Override
  public boolean removeAll(Collection<?> c) {
    Check.notNull(c);
    boolean changed = false;
    for (Object obj : c) {
      changed = remove(obj) || changed;
    }
    return changed;
  }

  /**
   * Repurposed to nullify all list elements whose value is <i>not</i> present in the specified
   * {@code Collection}.
   */
  @Override
  public boolean retainAll(Collection<?> c) {
    Check.notNull(c);
    boolean changed = false;
    MAIN_LOOP:
    for (int i = 0; i < data.length; ++i) {
      for (Object obj : c) {
        if (Objects.equals(data[i], obj)) {
          continue MAIN_LOOP;
        }
      }
      data[i] = null;
      changed = true;
    }
    return changed;
  }

  /** Repurposed to nullify all elements in the list. */
  @Override
  public void clear() {
    Arrays.fill(data, null);
  }

  @Override
  public int indexOf(Object o) {
    E[] data = this.data;
    if (o == null) {
      for (int i = 0; i < data.length; ++i) {
        if (data[i] == null) {
          return i;
        }
      }
    } else {
      for (int i = 0; i < data.length; ++i) {
        if (o.equals(data[i])) {
          return i;
        }
      }
    }
    return -1;
  }

  @Override
  public int lastIndexOf(Object o) {
    if (o == null) {
      for (int i = data.length - 1; i >= 0; --i) {
        if (data[i] == null) {
          return i;
        }
      }
    } else {
      for (int i = data.length - 1; i >= 0; --i) {
        if (o.equals(data[i])) {
          return i;
        }
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

  @Override
  public Stream<E> stream() {
    return Arrays.stream(data);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (obj.getClass() == UnsafeList.class) {
      return Arrays.deepEquals(data, ((UnsafeList) obj).data);
    } else if (obj instanceof List) {
      List other = (List) obj;
      if (size() == other.size()) {
        int i = 0;
        for (Object e : other) {
          if (!Objects.equals(e, data[i++])) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the backing array of this {@code UnsafeList}.
   *
   * @return The backing array of this {@code UnsafeList}
   */
  public E[] getBackingArray() {
    return data;
  }

  @Override
  public Object[] toArray() {
    E[] data = this.data;
    if (data.length == 0) {
      return EMPTY_OBJECT_ARRAY;
    }
    Object[] objs = new Object[data.length];
    System.arraycopy(data, 0, objs, 0, data.length);
    return objs;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] a) {
    E[] data = this.data;
    if (a.length < data.length) {
      return (T[]) Arrays.copyOf(data, data.length, a.getClass());
    }
    System.arraycopy(data, 0, a, 0, data.length);
    return a;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.deepHashCode(data);
    return result;
  }

  public String toString() {
    return "[" + ArrayMethods.implode(data) + "]";
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
