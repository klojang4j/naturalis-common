package nl.naturalis.common.collection;

import nl.naturalis.common.ClassMethods;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * Base class for {@link TypeGraphMap}, {@link TypeHashMap} and  {@link TypeTreeMap}. A {@code
 * TypeMap} is a specialized {@link Map} implementation used to map types to values. Its main
 * feature is that, if a type (requested via the {@link #get(Object) get} or {@link
 * #containsKey(Object) containsKey} method) is not present in the map, but one of its super types
 * is, then it will return the value associated with the super type. A {@code TypeMap} is not
 * modifiable and does not allow {@code null} keys and {@code null} values. If the map contains
 * {@code Object.class}, it is guaranteed to always return a non-null value. Note that this is
 * actually a deviation from Java's type hierarchy since primitive types do not extend {@code
 * Object.class}. However, the point of the {@code TypeMap} class is to elegantly provide default
 * values for groups of types through their common ancestor, and we want {@code Object.class} to be
 * associated with the ultimate, last-resort, fall-back value.
 *
 * <p>The requested type's class hierarchy takes precedence over its interface hierarchy.
 *
 * <h4>Autoboxing</h4>
 *
 * <p>A {@code TypeMap} can be configured to "autobox" the types requested by the client. That
 * is, if, for example, the client makes a request for {@code double.class}, but the map only
 * contains an entry for {@code Double.class}, then that is the type being served (i.e. {@code
 * containsKey} returns {@code true} and {@code get} returns the value associated with {@code
 * Double.class}). If there is no entry for {@code Double.class} either, but there is one for {@code
 * Number.class}, then that is the type being served. Thus, with autoboxing enabled, you need (and
 * should) only add the wrapper types to the map, unless you want the primitive type to be
 * associated with a different value than the wrapper type.
 *
 * @param <V> The type of the values in the {@code Map}
 * @author Ayco Holleman
 * @see TypeMap
 * @see TypeHashMap
 * @see TypeTreeMap
 */
public abstract sealed class TypeMap<V> implements Map<Class<?>, V> permits MultiPassTypeMap,
    TypeGraphMap {

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

  final boolean autobox;

  public TypeMap(boolean autobox) {
    this.autobox = autobox;
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
  public V remove(Object key) {
    throw notModifiable();
  }

  @Override
  public void clear() {
    throw notModifiable();
  }

  public List<String> typeNames() {
    return keySet().stream().map(ClassMethods::className).collect(toUnmodifiableList());
  }

  public List<String> simpleTypeNames() {
    return keySet().stream().map(ClassMethods::simpleClassName).collect(toUnmodifiableList());
  }

  private UnsupportedOperationException notModifiable() {
    return new UnsupportedOperationException();
  }

  private interface AnyNumber {}

  private interface AnyNumberArray {}

}
