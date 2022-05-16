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
 * @see TypeSet
 * @see TypeMap
 * @see SimpleTypeMap
 */
public class SimpleTypeSet extends TypeSet<SimpleTypeMap<Object>> {

  /**
   * Returns a {@code SimpleTypeSet} containing the specified types. Autoboxing will be enabled.
   * Auto-expansion will be disabled.
   *
   * @param types The types to include
   * @return A {@code SimpleTypeSet} containing the specified types
   */
  public static SimpleTypeSet of(Class<?>... types) {
    return copyOf(List.of(types));
  }

  /**
   * Returns a {@code SimpleTypeSet} for the specified types.
   *
   * @param autoExpand Whether to enable auto-expansion (see {@link SimpleTypeMap})
   * @param autobox Whether to enable "autoboxing" (see {@link TypeMap})
   * @param types The types to include
   * @return A {@code SimpleTypeSet} containing the specified types
   */
  public static SimpleTypeSet of(boolean autoExpand, boolean autobox, Class<?>... types) {
    return copyOf(List.of(types), autoExpand, autobox);
  }

  /**
   * Converts the specified {@code Collection} to a {@code SimpleTypeSet}.
   *
   * @param src The {@code Collection} to convert
   * @Return a {@code SimpleTypeSet}.
   */
  public static SimpleTypeSet copyOf(Collection<Class<?>> src) {
    return copyOf(src, false, true);
  }

  /**
   * Converts the specified {@code Collection} to a {@code SimpleTypeSet}.
   *
   * @param src The {@code Collection} to convert
   * @param autoExpand Whether to enable auto-expansion. See {@link
   *     SimpleTypeMapBuilder#autoExpand(boolean) TypeMap} for more information about this
   *     parameter.
   * @param autobox Whether to enable "autoboxing"
   * @return A {@code TypeSet} containing the types in the specified collection
   */
  public static SimpleTypeSet copyOf(Collection<Class<?>> src,
      boolean autoExpand,
      boolean autobox) {
    if (autoExpand) {
      return new SimpleTypeSet(src, 2, autobox);
    } else if (src instanceof SimpleTypeSet sts && sts.map.autobox == autobox) {
      return sts;
    }
    return new SimpleTypeSet(src, 0, autobox);
  }

  private SimpleTypeSet(Collection<? extends Class<?>> s, int expectedSize, boolean autobox) {
    super(new SimpleTypeMap<>(toMap(s), expectedSize, autobox));
  }

}
