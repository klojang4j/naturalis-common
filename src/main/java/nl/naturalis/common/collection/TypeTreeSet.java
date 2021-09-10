package nl.naturalis.common.collection;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import nl.naturalis.common.check.Check;

/**
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
