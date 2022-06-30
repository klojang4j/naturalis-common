package nl.naturalis.common.collection;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * An implementation of {@link TypeSet} that is internally backed by a {@link
 * TypeHashMap}.
 *
 * @author Ayco Holleman
 * @see TypeMap
 * @see TypeSet
 * @see TypeGraphSet
 */
public final class TypeHashSet extends AbstractTypeSet<TypeHashMap<Object>> {

  /**
   * Returns a {@code TypeHashSet} containing the specified types. Autoboxing will be
   * enabled. Auto-expansion will be disabled.
   *
   * @param types The types to include
   * @return A {@code TypeHashSet} containing the specified types
   */
  public static TypeHashSet of(Class<?>... types) {
    return copyOf(List.of(types));
  }

  /**
   * Returns a {@code TypeHashSet} for the specified types.
   *
   * @param autoExpand Whether to enable auto-expansion (see {@link
   *     TypeHashMap})
   * @param autobox Whether to enable "autoboxing" (see {@link TypeMap})
   * @param types The types to include
   * @return A {@code TypeHashSet} containing the specified types
   */
  public static TypeHashSet of(boolean autoExpand,
      boolean autobox,
      Class<?>... types) {
    return copyOf(List.of(types), autoExpand, autobox);
  }

  /**
   * Converts the specified {@code Collection} to a {@code TypeHashSet}.
   *
   * @param src The {@code Collection} to convert
   * @return a {@code TypeHashSet}.
   */
  public static TypeHashSet copyOf(Collection<Class<?>> src) {
    return copyOf(src, false, true);
  }

  /**
   * Converts the specified {@code Collection} to a {@code TypeHashSet}.
   *
   * @param src The {@code Collection} to convert
   * @param autoExpand Whether to enable auto-expansion. See {@link
   *     TypeHashMapBuilder#autoExpand(boolean) TypeMap} for more information about
   *     this parameter.
   * @param autobox Whether to enable "autoboxing"
   * @return A {@code TypeSet} containing the types in the specified collection
   */
  public static TypeHashSet copyOf(Collection<Class<?>> src,
      boolean autoExpand,
      boolean autobox) {
    if (autoExpand) {
      return new TypeHashSet(src, 2, autobox);
    } else if (src instanceof TypeHashSet sts && sts.map.autobox == autobox) {
      return sts;
    }
    return new TypeHashSet(src, 0, autobox);
  }

  private TypeHashSet(Collection<? extends Class<?>> s,
      int expectedSize,
      boolean autobox) {
    super(new TypeHashMap<>(toMap(s), expectedSize, autobox));
  }

}
