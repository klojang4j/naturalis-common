package nl.naturalis.common.collection;

import nl.naturalis.common.ClassMethods;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * A {@code TypeMap} is used to map Java types to values or actions (in the form of
 * lambdas). Its main feature is that if a type, requested via {@link #get(Object)
 * get} or {@link #containsKey(Object) containsKey}, is not present in the map, but
 * one of its supertypes is, then it will return the value associated with the
 * supertype. The requested type's class hierarchy takes precedence over its
 * interface hierarchy.
 *
 * <p>A {@code TypeMap} is not modifiable. All map-altering methods throw an
 * {@link UnsupportedOperationException}. {@link #getOrDefault(Object, Object)} will
 * also throw an {@code UnsupportedOperationException} as it sidesteps the {@code
 * TypeMap} paradigm. Neither keys nor values are allowed to be {@code null}. If the
 * map contains {@code Object.class}, it is guaranteed to always return a non-null
 * value. Note that this is, in fact, a deviation from Java's type hierarchy since
 * primitive types do not extend {@code Object.class}. However, the point of the
 * {@code TypeMap} interface is to provide natural default values for groups of types
 * through a common ancestor, and {@code Object.class} is the obvious candidate for
 * delivering the ultimate, last-resort, fall-back value.
 *
 * <h4>Autoboxing</h4>
 *
 * <p>A {@code TypeMap} can be configured to "autobox" the types requested by the
 * client. For example, if the client makes a request for {@code double.class}, but
 * the map only contains an entry for {@code Double.class}, then the value associated
 * with {@code Double.class} is returned. If there is no entry for {@code
 * Double.class} either, but there is one for {@code Number.class}, then the value
 * associated with {@code Number.class} is returned. Thus, with autoboxing enabled,
 * you need (and should) only add the primitive wrapper types to the map, unless you
 * want the primitive type to be associated with a different value than the wrapper
 * type.
 *
 * @param <V> The type of the values in the {@code Map}
 * @author Ayco Holleman
 * @see AbstractTypeMap
 * @see TypeHashMap
 */
public sealed interface TypeMap<V> extends Map<Class<?>, V> permits AbstractTypeMap {

  /**
   * Returns the fully-qualified names of the types in the map.
   *
   * @return The fully-qualified names of the types in the map
   */
  default List<String> typeNames() {
    return keySet().stream()
        .map(ClassMethods::className)
        .collect(toUnmodifiableList());
  }

  /**
   * Returns the simple names of the types in the map.
   *
   * @return The simple names of the types in the map
   */
  default List<String> simpleTypeNames() {
    return keySet().stream()
        .map(ClassMethods::simpleClassName)
        .collect(toUnmodifiableList());
  }

}
