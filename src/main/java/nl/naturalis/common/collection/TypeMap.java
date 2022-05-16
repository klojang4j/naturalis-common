package nl.naturalis.common.collection;

import nl.naturalis.common.ClassMethods;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * Base class for {@link TypeGraphMap}, {@link SimpleTypeMap} and  {@link TypeTreeMap}. A {@code
 * TypeMap} is a specialized {@link Map} implementation used to map types to values. Its main
 * feature is that, if a type requested via {@link #get(Object) get} or {@link #containsKey(Object)
 * containsKey} is not present in the map, but one of its supertypes is, then it will return the
 * value associated with the supertype. The requested type's class hierarchy takes precedence over
 * its interface hierarchy.
 *
 * <p>A {@code TypeMap} is not modifiable. All map-altering methods throw an
 * {@link UnsupportedOperationException}. Neither keys nor values are allowed to be {@code null}. If
 * the map contains {@code Object.class}, it is guaranteed to always return a non-null value. Note
 * that this is actually a deviation from Java's type hierarchy since primitive types do not extend
 * {@code Object.class}. However, the point of the {@code TypeMap} class is to provide natural
 * default values for groups of types through their common ancestor, and we want {@code
 * Object.class} to be associated with the ultimate, last-resort, fall-back value. The {@link
 * #getOrDefault(Object, Object)} also throws an {@code UnsupportedOperationException} as it
 * sidesteps the {@code TypeMap} paradigm.
 *
 * <h4>Autoboxing</h4>
 *
 * <p>A {@code TypeMap} can be configured to "autobox" the types requested by the client. That
 * is, if, for example, the client makes a request for {@code double.class}, but the map only
 * contains an entry for {@code Double.class}, then that is the type being served. If there is no
 * entry for {@code Double.class} either, but there is one for {@code Number.class}, then that is
 * the type being served. Thus, with autoboxing enabled, you need (and should) only add the wrapper
 * types to the map, unless you want the primitive type to be associated with a different value than
 * the wrapper type.
 *
 * @param <V> The type of the values in the {@code Map}
 * @author Ayco Holleman
 * @see TypeMap
 * @see SimpleTypeMap
 * @see TypeTreeMap
 */
public abstract sealed class TypeMap<V> extends ImmutableMap<Class<?>, V> permits MultiPassTypeMap,
    TypeGraphMap, LinkedTypeGraphMap {

  final boolean autobox;

  TypeMap(boolean autobox) {
    this.autobox = autobox;
  }

  /**
   * <b>Throws an {@code UnsupportedOperationException}.</b>
   */
  @Override
  public V getOrDefault(Object key, V defaultValue) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the fully-qualified names of the types in the map.
   *
   * @return The fully-qualified names of the types in the map
   */
  public List<String> typeNames() {
    return keySet().stream().map(ClassMethods::className).collect(toUnmodifiableList());
  }

  /**
   * Returns the simple names of the types in the map.
   *
   * @return The simple names of the types in the map
   */
  public List<String> simpleTypeNames() {
    return keySet().stream().map(ClassMethods::simpleClassName).collect(toUnmodifiableList());
  }

}
