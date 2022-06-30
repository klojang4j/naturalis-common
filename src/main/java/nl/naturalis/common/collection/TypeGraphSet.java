package nl.naturalis.common.collection;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * An implementation of {@link TypeSet} that is internally backed by a {@link
 * TypeGraph}.
 *
 * @author Ayco Holleman
 * @see TypeMap
 * @see TypeSet
 * @see TypeHashSet
 */
public final class TypeGraphSet extends AbstractTypeSet<TypeGraph<Object>> {

  /**
   * Returns a {@code TypeGraphSet} containing the specified types. Autoboxing will
   * be enabled.
   *
   * @param types The types to include
   * @return A {@code TypeGraphSet} containing the specified types
   */
  public static TypeGraphSet of(Class<?>... types) {
    return copyOf(List.of(types));
  }

  /**
   * Returns {@code TypeGraphSet} containing the specified types.
   *
   * @param autobox Whether to enable "autoboxing" (see {@link TypeMap})
   * @param types The types to include
   * @return An {@code TypeGraphSet} containing the specified types
   */
  public static TypeGraphSet of(boolean autobox, Class<?>... types) {
    return copyOf(List.of(types), autobox);
  }

  /**
   * Converts the specified {@code Collection} to a {@code TypeGraphSet}. Autoboxing
   * will be enabled
   *
   * @param src The {@code Collection} to convert
   * @return A {@code TypeGraphSet}
   */
  public static TypeGraphSet copyOf(Collection<Class<?>> src) {
    return copyOf(src, true);
  }

  /**
   * Converts the specified {@code Collection} to a {@code TypeSet}.
   *
   * @param src The {@code Collection} to convert
   * @param autobox Whether to enable "autoboxing" (see {@link TypeMap})
   * @return A {@code TypeSet} containing the types in the specified collection
   */
  public static TypeGraphSet copyOf(Collection<Class<?>> src, boolean autobox) {
    if (src instanceof TypeGraphSet tgs && tgs.map.autobox == autobox) {
      return tgs;
    }
    return new TypeGraphSet(src, autobox);
  }

  private TypeGraphSet(Collection<? extends Class<?>> c, boolean autobox) {
    super(buildMap(c, autobox));
  }

  private static TypeGraph<Object> buildMap(Collection<? extends Class<?>> c,
      boolean autobox) {
    TypeGraphBuilder<Object> builder = TypeGraph.build(Object.class);
    builder.autobox(autobox);
    for (Class<?> clazz : c) {
      builder.add(clazz, WHATEVER);
    }
    return builder.freeze();
  }

}
