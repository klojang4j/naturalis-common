package nl.naturalis.common.collection;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A specialized {@link Set} implementation for {@code Class} objects. It returns {@code true} from
 * its {@link Set#contains(Object) contains} method if the set contains the specified type <i>or any
 * of its super types</i>.
 *
 * @author Ayco Holleman
 * @see TypeMap
 * @see TypeGraphMap
 */
public class TypeGraphSet extends TypeSet<TypeGraphMap<Object>> {

  /**
   * Returns a {@code TypeGraphSet} containing the specified types. Autoboxing will bee enabled.
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
   * Converts the specified {@code Collection} to a {@code TypeGraphSet}. Autoboxing will be
   * enabled
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
    return new TypeGraphSet(src, autobox);
  }

  private TypeGraphSet(Collection<? extends Class<?>> c, boolean autobox) {
    super(buildMap(c, autobox));
  }

  private static TypeGraphMap<Object> buildMap(Collection<? extends Class<?>> c, boolean autobox) {
    TypeGraphMapBuilder<Object> builder = TypeGraphMap.build(Object.class);
    builder.autobox(autobox);
    for (Class<?> clazz : c) {
      builder.add(clazz, FOO);
    }
    return builder.freeze();
  }

}
