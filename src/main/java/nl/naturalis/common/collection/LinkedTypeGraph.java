package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.x.collection.ArraySet;

import java.util.*;

import static java.util.AbstractMap.SimpleImmutableEntry;
import static nl.naturalis.common.ArrayMethods.find;
import static nl.naturalis.common.ClassMethods.*;
import static nl.naturalis.common.check.CommonChecks.instanceOf;

/**
 * A {@link TypeMap} that stores its entries in a data structure similar to the one
 * used by {@link TypeGraph}, but is sensitive to the order in which the types are
 * inserted into the map. Thus, if you expect, for example, {@code String.class} to
 * be requested often compared to the other types, it pays to {@link
 * LinkedTypeGraphBuilder#add(Class, Object) add} that type first.
 *
 * <p>The {@link #keySet()} method returns a depth-first view of the type
 * hierarchy within the map. You can also request a {@link #keySetBreadthFirst()
 * breadth-first view} of the type hierarchy.
 *
 * @param <V> The type of the values in the {@code Map}
 * @see TypeGraph
 * @see LinkedTypeGraphBuilder
 * @see TypeHashMap
 */
public final class LinkedTypeGraph<V> extends NativeTypeMap<V, LinkedTypeNode> {

  /**
   * Converts the specified {@code Map} to a {@code TypeGraphMap}. Autoboxing will be
   * enabled.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @return A {@code TypeGraphMap}
   */
  public static <U> LinkedTypeGraph<U> copyOf(Class<U> valueType,
      Map<Class<?>, U> src) {
    return copyOf(valueType, true, src);
  }

  /**
   * Converts the specified {@code Map} to a {@code TypeGraphMap}.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param valueType
   * @param autobox Whether to enable "autoboxing" (see {@link AbstractTypeMap})
   * @param src The {@code Map} to convert
   * @return A {@code TypeGraphMap}
   */
  public static <U> LinkedTypeGraph<U> copyOf(Class<U> valueType,
      boolean autobox,
      Map<Class<?>, U> src) {
    Check.notNull(src, "source map");
    LinkedTypeGraphBuilder<U> builder = build(valueType);
    builder.autobox(autobox);
    src.forEach(builder::add);
    return builder.freeze();
  }

  /**
   * Returns a builder for {@code TypeHashMap} instances.
   *
   * @param <U> The type of the values in the map
   * @param valueType The class of the values in the map
   * @return A builder for {@code TypeHashMap} instances
   */
  public static <U> LinkedTypeGraphBuilder<U> build(Class<U> valueType) {
    return Check.notNull(valueType).ok(LinkedTypeGraphBuilder::new);
  }

  LinkedTypeGraph(LinkedTypeNode root, int size, boolean autobox) {
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
