package nl.naturalis.common.collection;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A specialized {@link Set} implementation for {@code Class} objects. It returns {@code true} from
 * its {@link Set#contains(Object) contains} method if the set contains the specified type <i>or any
 * of its super types</i>. As with {@link TypeHashMap} and {@link TypeTreeMap}, you can optionally
 * enable "autoboxing" and auto-expansion. See {@link MultiPassTypeMap} for a description of these
 * features. A {@code TypeSet} is unmodifiable and does not allow {@code null} values. It is backed
 * by a {@link TypeHashMap}.
 *
 * @author Ayco Holleman
 * @see TypeHashMap
 * @see TypeSet
 * @see TypeTreeMap
 */
public class TypeSet extends AbstractTypeSet {

  /**
   * Returns an auto-expanding, autoboxing {@code TypeSet} for the specified types.
   *
   * @param types The types to add to the {@code Set}.
   * @return A non-auto-expanding, autoboxing {@code TypeSet} for the specified types
   */
  public static TypeSet of(Class<?>... types) {
    return copyOf(List.of(types));
  }

  /**
   * Returns an auto-expanding, autoboxing {@code TypeSet} for the specified types.
   *
   * @param expectedSize An estimate of the final size of the auto-expanding set.See {@link
   *     TypeHashMap.Builder#autoExpand(int) TypeMap} for more information about this parameter.
   * @param types The types to add to the {@code Set}.
   * @return An autoboxing {@code TypeSet} for the specified types
   */
  public static TypeSet of(int expectedSize, Class<?>... types) {
    return of(expectedSize, true, types);
  }

  /**
   * Returns an autoboxing {@code TypeSet} for the specified types.
   *
   * @param autoExpand Whether to enable auto-expansion. See {@link
   *     TypeHashMap.Builder#autoExpand(boolean) TypeMap} for more information about this
   *     parameter.
   * @param types The types to add to the {@code Set}.
   * @return An autoboxing {@code TypeSet} for the specified types
   */
  public static TypeSet of(boolean autoExpand, Class<?>... types) {
    return copyOf(List.of(types), autoExpand);
  }

  /**
   * Returns a {@code TypeSet} for the specified types.
   *
   * @param expectedSize An estimate of the final size of the auto-expanding set.See {@link
   *     TypeHashMap.Builder#autoExpand(int) TypeMap} for more information about this parameter.
   * @param autobox Whether to enable "autoboxing"
   * @param types The types to add to the {@code Set}.
   * @return A {@code TypeSet} for the specified types
   */
  public static TypeSet of(int expectedSize, boolean autobox, Class<?>... types) {
    return copyOf(List.of(types), expectedSize, autobox);
  }

  /**
   * Returns a {@code TypeSet} for the specified types.
   *
   * @param autoExpand Whether to enable auto-expansion. See {@link
   *     TypeHashMap.Builder#autoExpand(boolean) TypeMap} for more information about this
   *     parameter.
   * @param autobox Whether to enable "autoboxing"
   * @param types The types to add to the {@code Set}.
   * @return A {@code TypeSet} for the specified types
   */
  public static TypeSet of(boolean autoExpand, boolean autobox, Class<?>... types) {
    return copyOf(List.of(types), autoExpand, autobox);
  }

  /**
   * Converts the specified {@code Collection} to a non-auto-expanding, autoboxing {@code TypeSet}.
   *
   * @param src The {@code Collection} to convert
   * @return A non-auto-expanding, autoboxing {@code TypeSet}
   */
  public static TypeSet copyOf(Collection<Class<?>> src) {
    return copyOf(src, 2, true);
  }

  /**
   * Converts the specified {@code Collection} to an auto-expanding, autoboxing {@code TypeSet}.
   *
   * @param src The {@code Collection} to convert
   * @param expectedSize An estimate of the final size of the auto-expanding set.See {@link
   *     TypeHashMap.Builder#autoExpand(int) TypeMap} for more information about this parameter.
   * @return An auto-expanding, autoboxing {@code TypeSet}
   */
  public static TypeSet copyOf(Collection<Class<?>> src, int expectedSize) {
    return copyOf(src, expectedSize, true);
  }

  /**
   * Converts the specified {@code Collection} to an auto-expanding, autoboxing {@code TypeSet}.
   *
   * @param src The {@code Collection} to convert
   * @param autoExpand Whether to enable auto-expansion. See {@link
   *     TypeHashMap.Builder#autoExpand(boolean) TypeMap} for more information about this
   *     parameter.
   * @return An auto-expanding, autoboxing {@code TypeSet}
   */
  public static TypeSet copyOf(Collection<Class<?>> src, boolean autoExpand) {
    return copyOf(src, autoExpand, true);
  }

  /**
   * Converts the specified {@code Collection} to a {@code TypeSet}.
   *
   * @param src The {@code Collection} to convert
   * @param expectedSize An estimate of the final size of the auto-expanding set. See {@link
   *     TypeHashMap.Builder#autoExpand(int) TypeMap} for more information about this parameter.
   * @param autobox Whether to enable "autoboxing"
   * @return A {@code TypeSet} containing the types in the specified collection
   */
  public static TypeSet copyOf(Collection<Class<?>> src, int expectedSize, boolean autobox) {
    return new TypeSet(src, expectedSize, autobox);
  }

  /**
   * Converts the specified {@code Collection} to a {@code TypeSet}.
   *
   * @param src The {@code Collection} to convert
   * @param autoExpand Whether to enable auto-expansion. See {@link
   *     TypeHashMap.Builder#autoExpand(boolean) TypeMap} for more information about this
   *     parameter.
   * @param autobox Whether to enable "autoboxing"
   * @return A {@code TypeSet} containing the types in the specified collection
   */
  public static TypeSet copyOf(Collection<Class<?>> src, boolean autoExpand, boolean autobox) {
    if (autoExpand) {
      return copyOf(src, 2, autobox);
    } else if (src.getClass() == TypeSet.class) {
      TypeSet ts = (TypeSet) src;
      if (ts.map.autobox == autobox) {
        return ts;
      }
    }
    return new TypeSet(src, autobox);
  }

  private TypeSet(Collection<? extends Class<?>> s, boolean autobox) {
    super(new TypeHashMap<>(toMap(s), autobox));
  }

  private TypeSet(Collection<? extends Class<?>> s, int sz, boolean autobox) {
    super(new TypeHashMap<>(toMap(s), sz, autobox));
  }

}
