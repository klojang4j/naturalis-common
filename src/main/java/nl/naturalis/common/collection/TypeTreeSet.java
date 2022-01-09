package nl.naturalis.common.collection;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import nl.naturalis.common.check.Check;

/**
 * A {@code Set} implementation that orders the {@code Class} objects it contains in ascending order
 * of abstraction. That is, for any two elements in the set, the one that comes first will never be
 * a super type of the one that comes second and, conversely, the one that comes second will never
 * be a subtype of the one that comes first. If the set contains {@code Object.class}, that will be
 * the last element in the set. A {@code TypeTreeSet} can also be configured to return true from its
 * {@link Set#contains(Object) contains} method if the specified type is a primitive type (e.g.
 * {@code boolean.class}) and the set either contains the primitive type itself or its corresponding
 * wrapper type ({@code Boolean.class}). This will then also work the other way round.
 *
 * <p>This {@code Set} implementation is backed by a {@link TypeTreeMap}.
 *
 * @see TypeTreeMap
 * @author Ayco Holleman
 */
public class TypeTreeSet extends AbstractTypeSet {

  public static TypeTreeSet withTypes(Class<?>... types) {
    return withTypes(false, types);
  }

  public static TypeTreeSet withTypes(boolean autobox, Class<?>... types) {
    Check.notNull(types, "types");
    return withTypes(List.of(types), autobox);
  }

  public static TypeTreeSet withTypes(Collection<Class<?>> src) {
    return withTypes(src, false);
  }

  public static TypeTreeSet withTypes(Collection<Class<?>> src, boolean autobox) {
    Check.notNull(src, "src");
    return new TypeTreeSet(src, false, autobox);
  }

  public static TypeTreeSet extending(Class<?>... types) {
    return extending(false, types);
  }

  public static TypeTreeSet extending(boolean autobox, Class<?>... types) {
    Check.notNull(types, "types");
    return extending(Set.of(types), autobox);
  }

  public static TypeTreeSet extending(Collection<Class<?>> types) {
    return extending(types, true);
  }

  public static TypeTreeSet extending(Collection<Class<?>> types, boolean autobox) {
    return new TypeTreeSet(types, true, autobox);
  }

  private TypeTreeSet(Collection<? extends Class<?>> s, boolean autoExpand, boolean autobox) {
    super(new TypeTreeMap<>(toMap(s), autoExpand, autobox, TypeTreeMap.EMPTY_TYPE_ARRAY));
  }
}
