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
    return new TypeTreeSet(src, autobox);
  }

  public static TypeTreeSet extending(Class<?>... types) {
    return extending(false, types);
  }

  public static TypeTreeSet extending(int expectedSize, Class<?>... types) {
    return extending(expectedSize, false, types);
  }

  public static TypeTreeSet extending(boolean autobox, Class<?>... types) {
    return extending(0, autobox, types);
  }

  public static TypeTreeSet extending(int expectedSize, boolean autobox, Class<?>... types) {
    Check.notNull(types, "types");
    return extending(Set.of(types), expectedSize, autobox);
  }

  public static TypeTreeSet extending(Collection<Class<?>> types) {
    return extending(types, 0);
  }

  public static TypeTreeSet extending(Collection<Class<?>> types, int expectedSize) {
    return extending(types, expectedSize, false);
  }

  public static TypeTreeSet extending(Collection<Class<?>> types, boolean autobox) {
    return extending(types, 0, autobox);
  }

  public static TypeTreeSet extending(
      Collection<Class<?>> types, int expectedSize, boolean autobox) {
    Check.notNull(types, "types");
    return new TypeTreeSet(types, expectedSize, autobox);
  }

  private TypeTreeSet(Collection<Class<?>> s, boolean autobox) {
    super(TypeTreeMap::new, s, autobox);
  }

  private TypeTreeSet(Collection<? extends Class<?>> s, int sz, boolean autobox) {
    super(TypeTreeMap::new, s, sz, autobox);
  }
}
