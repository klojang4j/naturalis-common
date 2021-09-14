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
   * specified array. Calling {@link #contains(Object) contains} on it will return true if either
   * the specified type itself or any of its super types is present in the set, but if the type
   * itself is not present in the set, it will not be added to it.
   *
   * @param types The tyoes contained in the set
   * @return A {@code TypeSet} configured as described above
   */
  public static TypeSet withTypes(Class<?>... types) {
    return withTypes(false, types);
  }

  /**
   * Returns a {@code TypeSet} instance that will never contain any other types than the ones in the
   * specified array. Calling {@link #contains(Object) contains} on it will return true if either
   * the specified type itself or any of its super types is present in the set, but if the type
   * itself is not present in the set, it will not be added to it. The instance can also be
   * configured to return {@code true} if the corresponding wrapper type c.q. primitive type is
   * present in the set.
   *
   * @param autobox Enables/disables the "auto-boxing" and "auto-unboxing" feature
   * @param types The tyoes contained in the set
   * @return A {@code TypeSet} configured as described above
   */
  public static TypeSet withTypes(boolean autobox, Class<?>... types) {
    Check.notNull(types, "types");
    return withTypes(List.of(types), autobox);
  }

  /**
   * Returns a {@code TypeSet} instance that will never contain any other types than the ones in the
   * specified source collection. Calling {@link #contains(Object) contains} on it will return true
   * if either the specified type itself or any of its super types is present in the set, but if the
   * type itself is not present in the set, it will not be added to it.
   *
   * @param src The souurce collection from which to create the {@code TypeSet}.
   * @return A {@code TypeSet} configured as described above
   */
  public static TypeSet withTypes(Collection<Class<?>> src) {
    return withTypes(src, false);
  }

  /**
   * Returns a {@code TypeSet} instance that will never contain any other types than the ones in the
   * specified souurce collection. Calling {@link #contains(Object) contains} on it will return true
   * if either the specified type itself or any of its super types is present in the set, but if the
   * type itself is not present in the set, it will not be added to it. The instance can also be
   * configured to return {@code true} if the corresponding wrapper type c.q. primitive type is
   * present in the set.
   *
   * @param src The souurce collection from which to create the {@code TypeSet}.
   * @param autobox Enables/disables the "auto-boxing" and "auto-unboxing" feature
   * @return A {@code TypeSet} configured as described above
   */
  public static TypeSet withTypes(Collection<Class<?>> src, boolean autobox) {
    Check.notNull(src, "src");
    return new TypeSet(src, autobox);
  }

  /**
   * Returns a {@code TypeSet} instance that will grow as new subtypes are presented to the {@link
   * #contains(Object) method}. The set is expected to grow to about twice the length of the
   * specified array.
   *
   * @param types The types contained in the set
   * @return A {@code TypeSet} configured as described above
   */
  public static TypeSet extending(Class<?>... types) {
    return extending(false, types);
  }

  /**
   * Returns a {@code TypeSet} instance that will grow as new subtypes are presented to the {@link
   * #contains(Object) method}. The instance can also be configured to return {@code true} if the
   * corresponding wrapper type.
   *
   * @param expectedSize The expected size of the set
   * @param types
   * @return
   */
  public static TypeSet extending(int expectedSize, Class<?>... types) {
    return extending(expectedSize, false, types);
  }

  /**
   * Returns a {@code TypeSet} instance that will grow as new subtypes are presented to the {@link
   * #contains(Object) method}. The instance can also be configured to return {@code true} if the
   * corresponding wrapper type c.q. primitive type is present in the set.
   *
   * @param autobox
   * @param types
   * @return
   */
  public static TypeSet extending(boolean autobox, Class<?>... types) {
    return extending(0, autobox, types);
  }

  /**
   * @param expectedSize
   * @param autobox
   * @param types
   * @return
   */
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
