package nl.naturalis.common.collection;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import nl.naturalis.common.check.Check;

/**
 * A {@link Set} implementation that returns {@code true} from its {@link Set#contains(Object)
 * contains} method if the set either contains the specified type or any of its super types. It can
 * also be configured to return true if the specified type is a primitive type (e.g. {@code
 * boolean.class}) and the set either contains the primitive type itself or its corresponding
 * wrapper type ({@code Boolean.class}). This will then also work the other way round.
 *
 * <p>This {@code Set} implementation is backed by a {@link TypeMap}.
 *
 * @see TypeMap
 * @author Ayco Holleman
 */
public class TypeSet extends AbstractTypeSet {

  /**
   * Returns a {@code TypeSet} instance that will never contain any other types than the ones in the
   * specified array.
   *
   * @param types
   * @return
   */
  public static TypeSet withTypes(Class<?>... types) {
    return withTypes(false, types);
  }

  public static TypeSet withTypes(boolean autobox, Class<?>... types) {
    Check.notNull(types, "types");
    return withTypes(List.of(types), autobox);
  }

  public static TypeSet withTypes(Collection<Class<?>> src) {
    return withTypes(src, false);
  }

  public static TypeSet withTypes(Collection<Class<?>> src, boolean autobox) {
    Check.notNull(src, "src");
    return new TypeSet(src, autobox);
  }

  public static TypeSet extending(Class<?>... types) {
    return extending(false, types);
  }

  public static TypeSet extending(int expectedSize, Class<?>... types) {
    return extending(expectedSize, false, types);
  }

  public static TypeSet extending(boolean autobox, Class<?>... types) {
    return extending(0, autobox, types);
  }

  public static TypeSet extending(int expectedSize, boolean autobox, Class<?>... types) {
    Check.notNull(types, "types");
    return extending(List.of(types), expectedSize, autobox);
  }

  public static TypeSet extending(Collection<Class<?>> types) {
    return extending(types, 0);
  }

  public static TypeSet extending(Collection<Class<?>> types, int expectedSize) {
    return extending(types, expectedSize, false);
  }

  public static TypeSet extending(Collection<Class<?>> types, boolean autobox) {
    return extending(types, 0, autobox);
  }

  public static TypeSet extending(Collection<Class<?>> types, int expectedSize, boolean autobox) {
    Check.notNull(types, "types");
    return new TypeSet(types, expectedSize, autobox);
  }

  private TypeSet(Collection<? extends Class<?>> s, boolean autobox) {
    super(new TypeMap<>(toMap(s), autobox));
  }

  private TypeSet(Collection<? extends Class<?>> s, int sz, boolean autobox) {
    super(new TypeMap<>(toMap(s), sz, autobox));
  }
}
