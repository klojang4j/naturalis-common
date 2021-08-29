package nl.naturalis.common.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.greaterThan;
import static nl.naturalis.common.check.CommonChecks.negative;
import static nl.naturalis.common.check.CommonChecks.sameAs;
import static nl.naturalis.common.check.CommonGetters.type;

/**
 * A {@code Set} implementation built on the same logic as, and is in fact back by a {@link
 * TypeMap}.
 *
 * @see TypeMap
 * @author Ayco Holleman
 */
public class TypeSet implements Set<Class<?>> {

  private static final Object FOO = new Object();

  private final TypeMap<Object> map;

  public TypeSet(Class<?>... types) {
    this(Set.of(types));
  }

  public TypeSet(int initialCapacity) {
    Check.that(initialCapacity, "initialCapacity").isNot(negative());
    map = new TypeMap<>(initialCapacity);
  }

  public TypeSet(Set<? extends Class<?>> s) {
    this(s, true);
  }

  public TypeSet(Set<? extends Class<?>> s, boolean autoExpand) {
    Check.notNull(s);
    map = new TypeMap<>(capacity(s, autoExpand), 0.75F, autoExpand);
    s.forEach(c -> map.put(c, FOO));
  }

  private static int capacity(Set<? extends Class<?>> s, boolean autoExpand) {
    int i = 1 + ((4 * s.size()) / 3);
    // If autoGrow, reserve some extra space for tacit additions
    return autoExpand ? (int) (1.25 * i) : i;
  }

  public TypeSet(int initialCapacity, float loadFactor, boolean autoExpand) {
    Check.that(initialCapacity, "initialCapacity").isNot(negative());
    Check.that(loadFactor, "loadFactor").is(greaterThan(), 0);
    map = new TypeMap<>(initialCapacity, loadFactor, autoExpand);
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public boolean contains(Object o) {
    Check.notNull(o, "o").has(type(), sameAs(), Class.class);
    return map.containsKey(o);
  }

  @Override
  public Iterator<Class<?>> iterator() {
    return map.keySet().iterator();
  }

  @Override
  public Object[] toArray() {
    return map.keySet().toArray();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] a) {
    Check.notNull(a);
    if (a.length >= map.size()) {
      System.arraycopy(map.keySet().toArray(), 0, a, 0, map.size());
    }
    return (T[]) map.keySet().toArray(new Class[map.size()]);
  }

  @Override
  public boolean add(Class<?> e) {
    Check.notNull(e);
    boolean b = !map.containsKey(e);
    map.put(e, FOO);
    return b;
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    Check.notNull(c);
    return c.stream().filter(map::containsKey).count() == c.size();
  }

  @Override
  public boolean addAll(Collection<? extends Class<?>> c) {
    Check.notNull(c);
    boolean b = false;
    for (Class<?> clazz : c) {
      b = add(clazz) || b;
    }
    return b;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    return map.keySet().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null || !Set.class.isInstance(obj)) {
      return false;
    }
    return map.keySet().equals(obj);
  }

  @Override
  public String toString() {
    return map.keySet().toString();
  }
}
