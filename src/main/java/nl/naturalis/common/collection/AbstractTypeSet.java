package nl.naturalis.common.collection;

import java.util.*;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.sameAs;
import static nl.naturalis.common.check.CommonGetters.type;

/**
 * A {@code Set} implementation built on the same logic as, and is in fact back by a {@link
 * TypeMap}.
 *
 * @see TypeMap
 * @author Ayco Holleman
 */
abstract class AbstractTypeSet implements Set<Class<?>> {

  @FunctionalInterface
  static interface TypeMapFactory2 {
    AbstractTypeMap<Object> create(Map<Class<?>, Object> src, boolean autoExpand, boolean autobox);
  }

  private static final Object FOO = new Object();

  static Map<Class<?>, Object> toMap(Collection<? extends Class<?>> types) {
    Map<Class<?>, Object> m = new LinkedHashMap<>(types.size());
    types.forEach(x -> m.put(x, FOO));
    return m;
  }

  final AbstractTypeMap<Object> map;

  AbstractTypeSet(AbstractTypeMap<Object> map) {
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
  public <T> T[] toArray(T[] a) {
    Check.notNull(a);
    return map.keySet().toArray(a);
  }

  @Override
  public boolean add(Class<?> e) {
    throw notModifiable();
  }

  @Override
  public boolean remove(Object o) {
    throw notModifiable();
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    Check.notNull(c);
    return c.stream().filter(map::containsKey).count() == c.size();
  }

  @Override
  public boolean addAll(Collection<? extends Class<?>> c) {
    throw notModifiable();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw notModifiable();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw notModifiable();
  }

  @Override
  public void clear() {
    throw notModifiable();
  }

  @Override
  public int hashCode() {
    return map.keySet().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return map.keySet().equals(obj);
  }

  public List<String> prettyTypeNames() {
    return map.typeNames();
  }

  public List<String> prettySimpleTypeNames() {
    return map.simpleTypeNames();
  }

  @Override
  public String toString() {
    return map.keySet().toString();
  }

  private static UnsupportedOperationException notModifiable() {
    return new UnsupportedOperationException("TypeSet not modifiable");
  }
}
