package nl.naturalis.common.collection;

import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.x.invoke.InvokeUtils;

import java.util.*;

import static nl.naturalis.common.ArrayMethods.EMPTY_OBJECT_ARRAY;
import static nl.naturalis.common.check.Check.fail;
import static nl.naturalis.common.check.CommonChecks.deepNotNull;
import static nl.naturalis.common.check.CommonChecks.lt;

public final class ArraySet<E> extends ImmutableSet<E> {

  private static final ArraySet EMPTY = new ArraySet(EMPTY_OBJECT_ARRAY);

  public static <F> ArraySet<F> of(F[] elements) {
    return Check.that(elements).is(deepNotNull()).ok(ArraySet::new);
  }

  public static <F> ArraySet<F> of(boolean verify, F[] elements) {
    if (Check.notNull(elements, "array").ok().length == 0) {
      return EMPTY;
    }
    if (verify && new HashSet<>(Arrays.asList(elements)).size() != elements.length) {
      return fail("Array must not contain duplicate values");
    }
    return new ArraySet<>(elements);
  }

  public static <F> ArraySet<F> copyOf(Set<F> c) {
    return Check.that(c).is(deepNotNull()).ok(ArraySet::new);
  }

  private final Object[] elems;

  private ArraySet(Collection<? extends E> c) {
    if (c.isEmpty()) {
      elems = EMPTY_OBJECT_ARRAY;
    } else {
      elems = c.toArray();
    }
  }

  private ArraySet(E[] elems) {
    this.elems = elems;
  }

  @Override
  public int size() {
    return elems.length;
  }

  @Override
  public boolean isEmpty() {
    return elems.length == 0;
  }

  @Override
  public boolean contains(Object o) {
    return ArrayMethods.isElementOf(o, elems);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return new HashSet<>(this).containsAll(c);
  }

  @Override
  public Iterator<E> iterator() {
    return new Iterator<>() {
      private int i = 0;

      @Override
      public boolean hasNext() {
        return i < elems.length;
      }

      @Override
      public E next() {
        Check.that(i).is(lt(), size(), NoSuchElementException::new);
        return (E) elems[i++];
      }
    };
  }

  @Override
  public Object[] toArray() {
    if (isEmpty()) {
      return EMPTY_OBJECT_ARRAY;
    }
    Object[] objs = new Object[elems.length];
    System.arraycopy(elems, 0, objs, 0, elems.length);
    return objs;
  }

  @Override
  public <T> T[] toArray(T[] a) {
    int sz = elems.length;
    Check.notNull(a);
    if (a.length < sz) {
      a = InvokeUtils.newArray(a.getClass(), sz);
    }
    int i = 0;
    Object[] result = a;
    for (E val : this) {
      result[i++] = val;
    }
    if (a.length > sz) {
      a[sz] = null;
    }
    return a;
  }

}
