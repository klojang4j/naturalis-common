package nl.naturalis.common.collection;

import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.x.invoke.InvokeUtils;

import java.util.*;
import java.util.stream.Stream;

import static nl.naturalis.common.ArrayMethods.EMPTY_OBJECT_ARRAY;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.type;

/**
 * A fixed-size, mutable {@code List} implementation intended for situations where an array
 * temporarily needs to take on the guise of a {@code List} before being processed as an array
 * again. This {@code List} implementation is much like the one you get from {@link
 * Arrays#asList(Object[]) Arrays.asList} in that it swallows rather than copies the array. However,
 * it also <i>exposes</i> the original array through the {@link #uncloak()} method, thus saving you
 * one array copy when continuing with the array again. The {@code set} and {@code get} methods
 * don't perform bounds-checking and all {@code add}-like methods throw an {@link
 * UnsupportedOperationException}. The {@code remove} methods have been repurposed to nullify list
 * elements, making them attractive candidates for a method references. This {@code List}
 * implementation accepts and may return {@code null} values.
 *
 * @param <E> The type of the list elements
 * @author Ayco Holleman
 */
public class ArrayCloakList<E> implements List<E>, RandomAccess {

  /**
   * Creates a new {@code ArrayCloakList} from the specified elements
   *
   * @param elementType The class of the elements
   * @param elems The elements
   * @return A new {@code ArrayCloakList} containing the specified elements
   */
  public static <F> ArrayCloakList<F> create(Class<F> elementType, F... elems) {
    Check.notNull(elementType, "elementType");
    Check.notNull(elems, "elems");
    for (F f : elems) {
      if (f != null) {
        Check.that(f, "element").is(instanceOf(), elementType);
      }
    }
    return new ArrayCloakList<>(elems);
  }

  private final Class<E> elementType;
  private final E[] data;

  /**
   * Creates a new {@code ArrayCloakList} for the specified element type and with the specified size
   * (and capacity).
   *
   * @param elementType The class of the list elements
   * @param size The desired size of the list
   */
  public ArrayCloakList(Class<E> elementType, int size) {
    Check.that(size, "size").is(gte(), 0);
    this.elementType = Check.notNull(elementType, "elementType").ok();
    this.data = InvokeUtils.newArray(elementType.arrayType(), size);
  }

  /**
   * Creates a new {@code ArrayCloakList} from the specified array.
   *
   * @param array The array from which to create the {@code ArrayCloakList}
   */
  public ArrayCloakList(E[] array) {
    this.data = Check.notNull(array).ok();
    this.elementType = (Class<E>) array.getClass().getComponentType();
  }

  /**
   * Returns the element at the specified index.
   *
   * @param index The array index
   * @return The element at the specified index
   */
  @Override
  public E get(int index) {
    return data[index];
  }

  /**
   * Sets the array element at the specified index to the specified value.
   *
   * @param index The array index
   * @param element The value
   * @return The original value at the specified index
   */
  @Override
  public E set(int index, E element) {
    if (element != null) {
      Check.that(element, "element").is(instanceOf(), elementType);
    }
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
   * throw an {@link UnsupportedOperationException} if the type parameter for this {@code
   * ArrayCloakList} is {@code Integer}, because Java's auto-boxing/auto-unboxing feature would make
   * the method indistinguishable from {@code remove(Object o)}.
   */
  @Override
  public E remove(int index) {
    if (elementType == Integer.class) {
      String msg =
          "Method remove(int) not supported for ArrayCloakList<Integer>. "
              + "Auto-boxing makes it indistinguishable from remove(Object)";
      throw new UnsupportedOperationException(msg);
    }
    if (size() == 0) {
      return null;
    }
    return set(index, null);
  }

  /**
   * Repurposed to nullify the first list element that equals specified object. Note, however, that
   * this method will throw an {@link UnsupportedOperationException} if the type parameter for this
   * {@code ArrayCloakList} is {@code Integer}, because Java's auto-boxing/auto-unboxing feature
   * would make the method indistinguishable from {@code remove(int index)}.
   */
  @Override
  public boolean remove(Object o) {
    if (elementType == Integer.class) {
      String msg =
          "Method remove(Object) not supported for ArrayCloakList<Integer>. "
              + "Auto-unboxing makes it indistinguishable from remove(int)";
      throw new UnsupportedOperationException(msg);
    }
    if (size() == 0) {
      return false;
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
    return new ArrayCloakList<>(elems);
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
    } else if (obj.getClass() == ArrayCloakList.class) {
      return Arrays.deepEquals(data, ((ArrayCloakList) obj).data);
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
   * Returns the backing array of this {@code ArrayCloakList}.
   *
   * @return The backing array of this {@code ArrayCloakList}
   */
  public E[] uncloak() {
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
