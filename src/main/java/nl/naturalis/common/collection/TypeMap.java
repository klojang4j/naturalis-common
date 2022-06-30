package nl.naturalis.common.collection;

import java.util.Map;

/**
 * <p>A specialisation of the {@link Map} interface, aimed at providing natural
 * default values for groups of Java types through a common ancestor. Type maps can
 * be especially useful to bind actions or operations (in the form of lambdas) to the
 * Java types to which they are applicable. The {@code TypeMap} interface does not
 * specify any methods of its own. However, it <i>does</i> specify behavior that
 * takes it beyond the {@code Map} interface.
 *
 * <p>Implementation of {@code TypeMap} must behave as follows: if a type,
 * requested via {@link #get(Object) get} or {@link #containsKey(Object)
 * containsKey}, is not present in the map, but one of its supertypes is, then it
 * will return the value associated with the supertype (or {@code true} in the case
 * of {@code containsKey}). If the requested type is a class (rather than an
 * interface), its class hierarchy takes precedence over its interface hierarchy. The
 * behaviour for annotation types is not undefined.
 *
 * <p>A {@code TypeMap} is not modifiable. All map-altering methods throw an
 * {@link UnsupportedOperationException}. {@link #getOrDefault(Object, Object)} also
 * throws an {@code UnsupportedOperationException} as it sidesteps the {@code
 * TypeMap} paradigm. Neither keys nor values are allowed to be {@code null}. If the
 * map contains key {@code Object.class}, it is guaranteed to always return a
 * non-null value. Note that this is, in fact, a deviation from Java's type hierarchy
 * since primitive types do not extend {@code Object.class}. However, the point of
 * the {@code TypeMap} interface is to provide natural default values for groups of
 * types through a common ancestor, and {@code Object.class} is the obvious candidate
 * for providing the ultimate, last-resort, fall-back value.
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
 * type. This applies not just to primitive types, but also to arrays of a primitive
 * type. Thus, with autoboxing enabled, {@code int[]} will be "autoboxed" to {@code
 * Integer[]}.
 *
 * <p>Note that, irrespective of whether autoboxing is enabled or disabled, the
 * presence of {@code Object.class} in the map guarantees that a non-null value will
 * be returned for whatever type is requested, even primitive types.
 *
 * @param <V> The type of the values in the {@code Map}
 * @author Ayco Holleman
 * @see TypeGraph
 * @see LinkedTypeGraph
 * @see TypeHashMap
 */
public sealed interface TypeMap<V> extends Map<Class<?>, V> permits
    AbstractTypeMap {}
