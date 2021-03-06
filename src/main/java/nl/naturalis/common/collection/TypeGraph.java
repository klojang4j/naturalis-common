package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A {@link TypeMap} that stores its entries in a data structure that reflects the
 * Java type hierarchy. While {@link TypeHashMap} climbs the requested type's class
 * and interface hierarchy, a {@code TypeGraph} comes in from the top of the type
 * hierarchy and works its way down until it encounters the requested type.
 * Consequently, it exhibits a pointed lopsidedness in the performance of {@link
 * #containsKey(Object)} and {@link #get(Object) get}. {@code containsKey} can return
 * ({@code true}) as soon as it encounters a supertype of the requested type (which
 * would be practically immediate if the map contains {@code Object.class}). The
 * {@code get} method, on the other hand, needs to descend into the type hierarchy
 * until it finds the requested type, or knows for sure it is absent.
 *
 * <p>The {@link #keySet()} method returns a depth-first view of the type
 * hierarchy within the map. You can also request a {@link #keySetBreadthFirst()
 * breadth-first view} of the type hierarchy.
 *
 * @param <V> The type of the values in the  {@code Map}
 * @see TypeGraphBuilder
 * @see LinkedTypeGraph
 * @see TypeHashMap
 */
public final class TypeGraph<V> extends NativeTypeMap<V, TypeNode> {

  /**
   * Converts the specified {@code Map} to a {@code TypeGraph} with "autoboxing"
   * enabled.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param valueType The class of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @return A {@code TypeGraph} built from the entries in the provided map
   */
  public static <U> TypeGraph<U> copyOf(Class<U> valueType, Map<Class<?>, U> src) {
    return copyOf(valueType, true, src);
  }

  /**
   * Converts the specified {@code Map} to a {@code TypeGraph}.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param valueType The class of the values in the {@code Map}
   * @param autobox Whether to enable "autoboxing" (see {@link AbstractTypeMap})
   * @param src The {@code Map} to convert
   * @return A {@code TypeGraph} built from the entries in the provided map
   */
  public static <U> TypeGraph<U> copyOf(Class<U> valueType,
      boolean autobox,
      Map<Class<?>, U> src) {
    Check.notNull(src, "source map");
    TypeGraphBuilder<U> builder = build(valueType);
    builder.autobox(autobox);
    src.forEach(builder::add);
    return builder.freeze();
  }

  /**
   * Returns a builder for {@code TypeGraph} instances.
   *
   * @param <U> The type of the values in the map
   * @param valueType The class of the values in the map
   * @return A builder for {@code TypeHashMap} instances
   */
  public static <U> TypeGraphBuilder<U> build(Class<U> valueType) {
    return Check.notNull(valueType).ok(TypeGraphBuilder::new);
  }

  TypeGraph(TypeNode root, int size, boolean autobox) {
    super(root, size, autobox);
  }

  /**
   * Returns a breadth-first view of the type hierarchy within this {@code Map}.
   *
   * @return A breadth-first view of the type hierarchy within this {@code Map}
   */
  public Set<Class<?>> keySetBreadthFirst() {
    return super.keySetBreadthFirst();
  }

}
