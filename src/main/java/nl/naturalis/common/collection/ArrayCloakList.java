package nl.naturalis.common.collection;

import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.check.CommonChecks;
import nl.naturalis.common.x.invoke.InvokeUtils;

import java.util.*;
import java.util.stream.Stream;

import static nl.naturalis.common.ArrayMethods.EMPTY_OBJECT_ARRAY;
import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * <p>A fixed-size, mutable {@code List} implementation intended for situations
 * where an array temporarily needs to take on the guise of a {@code List} before
 * being processed as an array again. This {@code List} implementation is much like
 * the one you get from {@link Arrays#asList(Object[]) Arrays.asList} in that it
 * swallows rather than copies the array. However, it will also spit out the original
 * array (see {@link #uncloak()}), thus saving you one array copy when continuing
 * with the array again. All {@code add}-like methods throw an {@link
 * UnsupportedOperationException}. The {@code remove} methods, however,  have been
 * repurposed to nullify list elements, allowing them to be easily used as method
 * references. This {@code List} implementation accepts and may return {@code null}
 * values.
 *
 * <p>Although this is a generic type, heap pollution through conversion to its
 * raw type is scrupulously guarded against, as there is no way to instantiate it
 * without the instance knowing and enforcing the exact type of its elements.
 *
 * @param <E> The type of the list elements
 * @author Ayco Holleman
 */
public class ArrayCloakList<E> implements List<E>, RandomAccess {

  /**
   * Returns an {@code ArrayCloakList} wrapping the specified array.
   *
   * @param array The array to wrap
   * @param <F> The type of the array elements
   * @return An {@code ArrayCloakList} wrapping the specified array
   */
  public static <F> ArrayCloakList<F> cloak(F[] array) {
    return new ArrayCloakList<>(array);
  }

  /**
   * Creates a new {@code ArrayCloakList} from the specified elements
   *
   * @param elementType The class of the elements
   * @param elems The elements
   * @param <F> The type of the array elements
   * @return A new {@code ArrayCloakList} containing the specified elements
   */
  @SafeVarargs
  public static <F> ArrayCloakList<F> of(Class<F> elementType, F... elems) {
    Check.notNull(elementType, "elementType");
    Check.notNull(elems, "elems");
    for (F f : elems) {
      if (f != null) {
        Check.that(f, "element").is(instanceOf(), elementType);
      }
    }
    return new ArrayCloakList<>(elems);
  }

  private final Class<E> type;
  private final E[] data;

  /**
   * Creates a new {@code ArrayCloakList} for the specified element type and with the
   * specified size (and capacity).
   *
   * @param elementType The class of the list elements
   * @param size The desired size of the list
   */
  public ArrayCloakList(Class<E> elementType, int size) {
    Check.that(size, "size").is(gte(), 0);
    this.type = Check.notNull(elementType, "elementType").ok();
    this.data = InvokeUtils.newArray(elementType.arrayType(), size);
  }

  /**
   * Creates a new {@code ArrayCloakList} from the specified array.
   *
   * @param array The array from which to create the {@code ArrayCloakList}
   */
  @SuppressWarnings({"unchecked"})
  public ArrayCloakList(E[] array) {
    this.data = Check.notNull(array).ok();
    this.type = (Class<E>) array.getClass().getComponentType();
  }

  @Override
  public E get(int index) {
    checkIndex(index);
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
    checkIndex(index);
    if (element != null) {
      Check.that(element, "element").is(instanceOf(), type);
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
        throw new NoSuchElementException();
      }
    };
  }

  /**
   * Repurposed to nullify the element at the specified index.
   */
  @Override
  public E remove(int index) {
    return set(index, null);
  }

  /**
   * Repurposed to nullify the first list element that equals specified object.
   */
  @Override
  public boolean remove(Object o) {
    int idx = indexOf(o);
    if (idx == -1) {
      return false;
    }
    data[idx] = null;
    return true;
  }

  /**
   * Repurposed to nullify all list elements whose value is present in the specified
   * {@code Collection}.
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
   * Repurposed to nullify all list elements whose value is <i>not</i> present in the
   * specified {@code Collection}.
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

  /**
   * Repurposed to nullify all elements in the list.
   */
  @Override
  public void clear() {
    Arrays.fill(data, null);
  }

  @Override
  public int indexOf(Object o) {
    return (o == null || isA(o, type)) ? ArrayMethods.indexOf(data, o) : -1;
  }

  @Override
  public int lastIndexOf(Object o) {
    return (o == null || isA(o, type)) ? ArrayMethods.lastIndexOf(data, o) : -1;
  }

  /**
   * Returns the segment identified by the specified {@code from} and {@code to}
   * index. <i>The returned {@code List} is not backed by the original list.</i>
   *
   * @param fromIndex The start index of the segment (inclusive)
   * @param toIndex The end index of the segment (exclusive)
   * @return The segment identified by the specified {@code from} and {@code to}
   *     index
   */
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
    } else if (obj instanceof ArrayCloakList<?> acl) {
      return Arrays.deepEquals(data, acl.data);
    } else if (obj instanceof List other && size() == other.size()) {
      int i = 0;
      for (Object o : other) {
        if (!Objects.deepEquals(o, data[i++])) {
          return false;
        }
      }
      return true;
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
  public <T> T[] toArray(T[] a) {
    Check.notNull(a, "array");
    var data = this.data;
    var sz = data.length;
    if (a.length < sz) {
      a = InvokeUtils.newArray(a.getClass(), sz);
    }
    Object[] result = a;
    System.arraycopy(data, 0, result, 0, sz);
    if (a.length > sz) {
      a[sz] = null;
    }
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
   * Throws an {@code UnsupportedOperationException}. List manipulation must be done
   * through the {@link #set(int, Object) set} method only.
   */
  @Override
  public boolean add(E e) {
    throw new UnsupportedOperationException();
  }

  /**
   * Throws an {@code UnsupportedOperationException}. List manipulation must be done
   * through the {@link #set(int, Object) set} method only.
   */
  @Override
  public void add(int index, E element) {
    throw new UnsupportedOperationException();
  }

  /**
   * Throws an {@code UnsupportedOperationException}. List manipulation must be done
   * through the {@link #set(int, Object) set} method only.
   */
  @Override
  public boolean addAll(Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  /**
   * Throws an {@code UnsupportedOperationException}. List manipulation must be done
   * through the {@link #set(int, Object) set} method only.
   */
  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  /**
   * Throws an {@code UnsupportedOperationException}.
   */
  @Override
  public ListIterator<E> listIterator() {
    throw new UnsupportedOperationException();
  }

  /**
   * Throws an {@code UnsupportedOperationException}.
   */
  @Override
  public ListIterator<E> listIterator(int index) {
    throw new UnsupportedOperationException();
  }

  private void checkIndex(int index) {
    Check.on(indexOutOfBounds(), index, "index").is(CommonChecks.arrayIndexOf(),
        data);
  }

}
