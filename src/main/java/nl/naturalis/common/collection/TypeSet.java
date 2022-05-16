package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.*;

import static nl.naturalis.common.check.CommonChecks.sameAs;
import static nl.naturalis.common.check.CommonGetters.type;

abstract class TypeSet<M extends TypeMap<Object>> implements Set<Class<?>> {

  static final Object FOO = new Object();

  static Map<Class<?>, Object> toMap(Collection<? extends Class<?>> types) {
    Map<Class<?>, Object> m = new LinkedHashMap<>(types.size());
    types.forEach(x -> m.put(x, FOO));
    return m;
  }

  final M map;

  TypeSet(M map) {
    this.map = map;
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
    Check.notNull(o).has(type(), sameAs(), Class.class);
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
  public <T> T[] toArray(T[] a) {
    Check.notNull(a);
    return map.keySet().toArray(a);
  }

  @Override
  public boolean add(Class<?> e) {
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
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
    return map.keySet().equals(obj);
  }

  public List<String> typeNames() {
    return map.typeNames();
  }

  public List<String> simpleTypeNames() {
    return map.simpleTypeNames();
  }

  @Override
  public String toString() {
    return map.keySet().toString();
  }

}
