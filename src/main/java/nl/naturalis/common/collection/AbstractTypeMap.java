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
 * A specialized {@link Map} implementation used to map types to values. Its main feature is that,
 * if the requested type is not present, but one of its super types is, it will return the value
 * associated with the super type. A {@code TypeMap} does not allow {@code null} keys or values. If
 * the map contains {@code Object.class}, it is guaranteed to always return a non-null value. Note
 * that this is actually a deviation from Java's type hierarchy since primitive types do not extend
 * {@code Object.class}. However, the main goal of the {@code TypeMap} class is to elegantly provide
 * default values for groups of types through their common ancestor, and we want {@code
 * Object.class} to give us the ultimate, last-resort, fall-back value.
 *
 * <h4>Autoboxing</h4>
 *
 * <p>The map is configured by default to "autobox" (and unbox) types: if the requested type is a
 * primitive type, and there is no entry for it in the map, but there is one for the corresponding
 * wrapper type, then the map will return the value associated with the wrapper type (and vice
 * versa). You can {@link #autobox disable} the autoboxing feature.
 *
 * <h4>Auto-expansion</h4>
 *
 * <p>A {@code TypeMap} unmodifiable. All map-altering methods will throw an {@link
 * UnsupportedOperationException}. However, the map can be configured to automatically absorb
 * subtypes of types that already are in the map. Thus, when the subtype is requested again, it will
 * result in a direct hit. Auto-expansion is disabled by default.
 *
 * <h4>Type-lookup Logic</h4>
 *
 * <p>When looking for a super type of the requested type, the map will first climb the type's class
 * hierarchy up to, but not including {@code Object.class}; then it will climb up the type's
 * interfaces (if any); and finally it will check to see if it contains an entry for {@code
 * Object.class}.
 *
 * @see TypeMap
 * @see TypeTreeMap
 * @param <V> The type of the values in the {@code Map}
 * @author Ayco Holleman
 */
public abstract class AbstractTypeMap<V> implements Map<Class<?>, V> {

  private interface AnyNumber {}

  private interface AnyNumberArray {}

  /**
   * Special type constant. If this type is present in the map, it will provide the default value
   * for any {@link ClassMethods#isNumerical(Class) type of number}. This allows you to keep the map
   * very small (especially with "autoboxing" and auto-expansion disabled) while still having all
   * numerical types covered.
   */
  public static final Class<?> ANY_NUMBER = AnyNumber.class;

  /**
   * Special type constant. If this type is present in the map, it will provide the default value
   * for any type of {@link ClassMethods#isNumericalArray(Class) numerical array}. See also {@link
   * #ANY_NUMBER}.
   */
  public static final Class<?> ANY_NUMBER_ARRAY = AnyNumberArray.class;

  static final String ERR_NULL_KEY = "Source map must not contain null keys";
  static final String ERR_NULL_VAL = "Illegal null value for type ${0}";

  final boolean autoExpand;
  final boolean autobox;

  AbstractTypeMap(boolean autoExpand, boolean autobox) {
    this.autoExpand = autoExpand;
    this.autobox = autobox;
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
  public V put(Class<?> key, V value) {
    throw notModifiable();
  }

  @Override
  public void putAll(Map<? extends Class<?>, ? extends V> m) {
    throw notModifiable();
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
  public V remove(Object key) {
    throw notModifiable();
  }

  @Override
  public void clear() {
    throw notModifiable();
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

  public List<String> typeNames() {
    return keySet().stream().map(ClassMethods::className).collect(toUnmodifiableList());
  }

  public List<String> simpleTypeNames() {
    return keySet().stream().map(ClassMethods::simpleClassName).collect(toUnmodifiableList());
  }

  private UnsupportedOperationException notModifiable() {
    return new UnsupportedOperationException(getClass().getSimpleName() + " not modifiable");
  }
}
