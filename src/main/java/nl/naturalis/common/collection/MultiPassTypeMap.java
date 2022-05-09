package nl.naturalis.common.collection;

import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.Tuple2;
import nl.naturalis.common.check.Check;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.toUnmodifiableList;
import static nl.naturalis.common.ClassMethods.*;
import static nl.naturalis.common.check.CommonChecks.sameAs;
import static nl.naturalis.common.check.CommonGetters.type;

/**
 * Base class for {@link TypeHashMap} and {@link TypeTreeMap}. These two classes are backed by a
 * regular {@code Map} (a {@link HashMap} and a {@link TreeMap}, respectively). The type lookup
 * mechanism may involve multiple queries against the backing map. If the requested type is not
 * itself present in the backing map, a {@code MultiPassTypeMap} will first climb the type's class
 * hierarchy up to, but not including {@code Object.class}. If none of the classes in the type's
 * class hierarchy were found in the backing map, it will climb up the type's interfaces (if any);
 * and finally it will check to see if the backing map contains an entry for {@code Object.class}.
 *
 * <h4>Auto-expansion</h4>
 *
 * <p>A {@code TypeMap} unmodifiable. All map-altering methods will throw an {@link
 * UnsupportedOperationException}. However, {@code TypeHashMap} and {@code TypeTreeMap} can be
 * configured to automatically absorb subtypes of types that already are in the map. That is, with
 * auto-expansion enabled, if a type is not present in the map, but one of its supertypes is, the
 * {@link #get(Object) get} and {@link #containsKey(Object) containsKey} will tacitly add the
 * subtype to the map, associating it with the same value as the supertype. Thus, when the subtype
 * is requested again, it will result in a direct hit. (NB auto-expansion is not a feature of the
 * {@link TypeGraphMap} class as it would actually be detrimental to its performance.)
 *
 * @param <V> The type of the values in the {@code Map}
 * @author Ayco Holleman
 * @see TypeHashMap
 * @see TypeTreeMap
 */
public abstract sealed class MultiPassTypeMap<V> extends TypeMap<V> permits TypeHashMap,
    TypeTreeMap {

  final boolean autoExpand;

  MultiPassTypeMap(boolean autoExpand, boolean autobox) {
    super(autobox);
    this.autoExpand = autoExpand;
  }

  abstract Map<Class<?>, V> backend();

  @Override
  public V get(Object key) {
    Check.notNull(key, "key").has(type(), sameAs(), Class.class);
    Class<?> type = (Class<?>) key;
    Tuple2<Class<?>, V> entry = find(type);
    if (entry == null) {
      return null;
    }
    if (autoExpand && type != entry.one()) {
      backend().put(type, entry.two());
    }
    return entry.two();
  }

  @Override
  public boolean containsKey(Object key) {
    Check.notNull(key, "key").has(type(), sameAs(), Class.class);
    Class<?> type = (Class<?>) key;
    Tuple2<Class<?>, V> entry = find(type);
    if (entry == null) {
      return false;
    }
    if (autoExpand && type != entry.one()) {
      backend().put(type, entry.two());
    }
    return true;
  }

  private Tuple2<Class<?>, V> find(Class<?> k) {
    V v = backend().get(k);
    if (v != null) {
      return Tuple2.of(k, v);
    }
    if (k.isArray()) {
      return findArrayType(k);
    }
    return findSimpleType(k);
  }

  private Tuple2<Class<?>, V> findSimpleType(Class<?> k) {
    if (k.isInterface()) {
      Tuple2<Class<?>, V> t = climbInterfaces(k, false);
      return t == null ? defaultValue() : t;
    }
    // We don't want to search for Object.class just yet.
    // It should be our last resort.
    for (Class<?> c = k.getSuperclass(); c != null && c != Object.class; c = c.getSuperclass()) {
      V v = backend().get(c);
      if (v != null) {
        return Tuple2.of(c, v);
      }
    }
    for (Class<?> c = k; c != null && c != Object.class; c = c.getSuperclass()) {
      Tuple2<Class<?>, V> t = climbInterfaces(c, false);
      if (t != null) {
        return t;
      }
    }
    if (autobox) {
      if (k.isPrimitive()) {
        return find(box(k));
      }
      if (isWrapper(k)) {
        Class<?> c = unbox(k);
        V v = backend().get(c);
        if (v != null) {
          return Tuple2.of(c, v);
        }
      }
    }
    if (isNumerical(k) && defaultNumberValue() != null) {
      return defaultNumberValue();
    }
    return defaultValue();
  }

  private Tuple2<Class<?>, V> findArrayType(Class<?> k) {
    Class<?> elementType = k.componentType();
    if (elementType.isInterface()) {
      Tuple2<Class<?>, V> t = climbInterfaces(elementType, true);
      return t == null ? defaultValue() : t;
    }
    for (Class<?> c = elementType.getSuperclass(); c != null; c = c.getSuperclass()) {
      Class<?> arrayType = c.arrayType();
      V v = backend().get(arrayType);
      if (v != null) {
        return Tuple2.of(arrayType, v);
      }
    }
    for (Class<?> c = elementType; c != null; c = c.getSuperclass()) {
      Tuple2<Class<?>, V> t = climbInterfaces(c, true);
      if (t != null) {
        return t;
      }
    }
    if (autobox) {
      if (elementType.isPrimitive()) {
        return find(box(elementType).arrayType());
      }
      if (isWrapper(elementType)) {
        Class<?> c = unbox(elementType).arrayType();
        V v = backend().get(c);
        if (v != null) {
          return Tuple2.of(c, v);
        }
      }
    }
    if (isNumerical(elementType) && defaultNumArrayValue() != null) {
      return defaultNumArrayValue();
    }
    return defaultValue();
  }

  private AtomicReference<Tuple2<Class<?>, V>> defVal;
  private AtomicReference<Tuple2<Class<?>, V>> defNumberVal;
  private AtomicReference<Tuple2<Class<?>, V>> defNumArrayVal;

  private Tuple2<Class<?>, V> defaultValue() {
    if (defVal == null) {
      V v = backend().get(Object.class);
      Tuple2<Class<?>, V> t = v == null ? null : Tuple2.of(Object.class, v);
      defVal = new AtomicReference<>(t);
    }
    return defVal.getPlain();
  }

  private Tuple2<Class<?>, V> defaultNumberValue() {
    if (defNumberVal == null) {
      V v = backend().get(ANY_NUMBER);
      Tuple2<Class<?>, V> t = v == null ? null : Tuple2.of(ANY_NUMBER, v);
      defNumberVal = new AtomicReference<>(t);
    }
    return defNumberVal.getPlain();
  }

  private Tuple2<Class<?>, V> defaultNumArrayValue() {
    if (defNumArrayVal == null) {
      V v = backend().get(ANY_NUMBER_ARRAY);
      Tuple2<Class<?>, V> t = v == null ? null : Tuple2.of(ANY_NUMBER_ARRAY, v);
      defNumArrayVal = new AtomicReference<>(t);
    }
    return defNumArrayVal.getPlain();
  }

  private Tuple2<Class<?>, V> climbInterfaces(Class<?> clazz, boolean array) {
    for (Class<?> c : clazz.getInterfaces()) {
      Class<?> c0 = array ? c.arrayType() : c;
      V v = backend().get(c0);
      if (v != null) {
        return Tuple2.of(c0, v);
      }
    }
    for (Class<?> c : clazz.getInterfaces()) {
      Tuple2<Class<?>, V> t = climbInterfaces(c, array);
      if (t != null) {
        return t;
      }
    }
    return null;
  }

  @Override
  public int size() {
    return backend().size();
  }

  @Override
  public boolean isEmpty() {
    return backend().isEmpty();
  }

  @Override
  public boolean containsValue(Object value) {
    return backend().containsValue(value);
  }

  @Override
  public Set<Class<?>> keySet() {
    return Collections.unmodifiableSet(backend().keySet());
  }

  @Override
  public Collection<V> values() {
    return Collections.unmodifiableCollection(backend().values());
  }

  @Override
  public Set<Entry<Class<?>, V>> entrySet() {
    return Collections.unmodifiableSet(backend().entrySet());
  }

  @Override
  public int hashCode() {
    return backend().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return backend().equals(obj);
  }

  @Override
  public String toString() {
    return backend().toString();
  }

}
