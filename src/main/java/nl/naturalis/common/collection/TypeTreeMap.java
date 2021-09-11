package nl.naturalis.common.collection;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ClassMethods.getAllInterfaces;
import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.ClassMethods.isWrapper;
import static nl.naturalis.common.check.CommonChecks.instanceOf;
import static nl.naturalis.common.check.CommonChecks.notNull;

/**
 * A {@code Map} implementation that behaves exactly like the {@link TypeMap} class, but is
 * internally backed by a {@link TreeMap}. The {@code TreeMap} is instantiated with a {@link
 * Comparator} that is specifically tuned to navigate type hierarchies. As a bonus the {@link
 * #keySet() key set} of a {@code TypeTreeMap} is sorted such that for any two keys, the one that
 * comes first will never be a supertype of the one following it. This is the main feature if the
 * {@link TypeTreeSet} class.
 *
 * @see TypeMap
 * @author Ayco Holleman
 * @param <V> The type of the values in the {@code Map}
 */
public class TypeTreeMap<V> extends AbstractTypeMap<V> {

  private static final Comparator<Class<?>> COMP =
      (c1, c2) -> {
        if (c1 == c2) {
          return 0;
        } else if (c1 == Object.class) {
          return 1;
        } else if (c2 == Object.class) {
          return -1;
        } else if (c1.isArray()) {
          return 1;
        } else if (c2.isArray()) {
          return -1;
        } else if (c1.isEnum()) {
          return -1;
        } else if (c2.isEnum()) {
          return 1;
        } else if (c1.isPrimitive()) {
          return -1;
        } else if (c2.isPrimitive()) {
          return 1;
        } else if (countAncestors(c1) > countAncestors(c2)) {
          return -1;
        } else if (c1.isInterface()) {
          if (!c2.isInterface()) {
            return 1;
          } else if (getAllInterfaces(c1).size() > getAllInterfaces(c2).size()) {
            return -1;
          }
        } else if (isA(c1, c2)) {
          return -1;
        }
        return 1;
      };

  private static final Comparator<Class<?>> AUTOBOX_COMP =
      (c1, c2) -> {
        if (c1 == c2) {
          return 0;
        } else if (c1 == Object.class) {
          return 1;
        } else if (c2 == Object.class) {
          return -1;
        } else if (c1.isArray()) {
          return 1;
        } else if (c2.isArray()) {
          return -1;
        } else if (c1.isEnum()) {
          return -1;
        } else if (c2.isEnum()) {
          return 1;
        } else if (c1.isPrimitive()) {
          return -1;
        } else if (c2.isPrimitive()) {
          return 1;
        } else if (isWrapper(c1)) {
          return -1;
        } else if (isWrapper(c2)) {
          return 1;
        } else if (countAncestors(c1) > countAncestors(c2)) {
          return -1;
        } else if (c1.isInterface()) {
          if (!c2.isInterface()) {
            return 1;
          } else if (getAllInterfaces(c1).size() > getAllInterfaces(c2).size()) {
            return -1;
          }
        } else if (isA(c1, c2)) {
          return -1;
        }
        return 1;
      };

  /**
   * A builder class for {@code TypeTreeMap} instances.
   *
   * @author Ayco Holleman
   * @param <U> The type of the values in the {@code TypeTreeMap} to be built
   */
  public static final class Builder<U> {
    private final Class<U> valueType;
    private final HashMap<Class<?>, U> tmp = new HashMap<>();
    private boolean autoExpand = true;
    private boolean autobox;

    private Builder(Class<U> valueType) {
      this.valueType = valueType;
    }

    /**
     * Disables the automatic addition of new subtypes. Note that by default auto-expansion is
     * enabled.
     *
     * @return This {@code Builder} instance
     */
    public Builder<U> noAutoExpand() {
      this.autoExpand = false;
      return this;
    }

    /**
     * Enables the "auto-boxing" and "auto-unboxing" feature.
     *
     * @return This {@code Builder} instance
     */
    public Builder<U> autobox() {
      this.autobox = true;
      return this;
    }

    /**
     * Associates the specified type with the specified value
     *
     * @param type The type
     * @param value The value
     * @return This {@code Builder} instance
     */
    public Builder<U> add(Class<?> type, U value) {
      Check.notNull(type, "type");
      Check.notNull(value, "value").is(instanceOf(), valueType);
      tmp.put(type, value);
      return this;
    }

    /**
     * Returns a new {@code TypeTreeMap} instance with the configured types and behaviour.
     *
     * @param <W> The type of the values in the returned {@code TypeTreeMap}
     * @return S new {@code TypeTreeMap} instance with the configured types and behaviour
     */
    @SuppressWarnings("unchecked")
    public <W> TypeTreeMap<W> freeze() {
      if (autoExpand) {
        return (TypeTreeMap<W>) new TypeTreeMap<>(tmp, 0, autobox);
      }
      return (TypeTreeMap<W>) new TypeTreeMap<>(tmp, autobox);
    }
  }

  /**
   * Returns a {@code TypeTreeMap} instance that will never contain any other keys or values than
   * the ones in the specified map. See class description above.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to turn into a {@code TypeTreeMap}
   * @return A {@code TypeTreeMap} instance that will never grow beyond the size of the specified
   *     map
   */
  public static <U> TypeTreeMap<U> withTypes(Map<Class<?>, U> src) {
    return withTypes(src, false);
  }

  /**
   * Returns a {@code TypeTreeMap} instance that will never contain any other keys or values than
   * the ones in the specified map. See class description above.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to turn into a {@code TypeTreeMap}
   * @param autobox Specifying {@code true} causes the following behaviour: if the value for a
   *     primitive type (e.g. {@code int.class}) is requested and there is no entry for the
   *     primitive type, then, if present, the value for the corresponding wrapper type ({@code
   *     Integer.class}) will be returned - <i>and vice versa</i>.
   * @return A {@code TypeTreeMap} instance that will never grow beyond the size of the specified
   *     map
   */
  public static <U> TypeTreeMap<U> withTypes(Map<Class<?>, U> src, boolean autobox) {
    Check.notNull(src);
    return new TypeTreeMap<>(src, autobox);
  }

  /**
   * Returns a {@code TypeTreeMap} instance that will never contain any other values than the ones
   * in the specified map (as per {@link Map#values()}), and that will never contain any types
   * (keys) that are not equal to, or extending from the types already present in the specified map,
   * but that <i>will</i> grow as new subtypes are being associated with pre-existing values.
   * Equivalent to {@code withValues(src, 0)}.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to turn into a {@code TypeTreeMap}
   * @return A {@code TypeTreeMap} instance that will grow as new types are being requested from it
   */
  public static <U> TypeTreeMap<U> withValues(Map<Class<?>, U> src) {
    return withValues(src, false);
  }

  /**
   * Returns a {@code TypeTreeMap} instance that will never contain any other values than the ones
   * in the specified map (as per {@link Map#values()}), and that will never contain any types
   * (keys) that are not equal to, or extending from the types already present in the specified map,
   * but that <i>will</i> grow as new subtypes are being associated with pre-existing values.
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to turn into a {@code TypeTreeMap}
   * @param autobox Specifying {@code true} causes the following behaviour: if the value for a
   *     primitive type (e.g. {@code int.class}) is requested and there is no entry for the
   *     primitive type, then, if present, the value for the corresponding wrapper type ({@code
   *     Integer.class}) will be returned - <i>and vice versa</i>.
   * @return A {@code TypeTreeMap} instance that will grow as new types are being requested from it
   */
  public static <U> TypeTreeMap<U> withValues(Map<Class<?>, U> src, boolean autobox) {
    Check.notNull(src, "src");
    return new TypeTreeMap<>(src, 0, autobox);
  }

  /**
   * Returns a {@code Builder} instance that lets you configure a {@code TypeTreeMap}
   *
   * @param <U> The type of the values in the {@code TypeTreeMap} to be built
   * @param valueType The class object corresponding to the type of the values in the {@code
   *     TypeTreeMap} to be built
   * @return A {@code Builder} instance that lets you configure a {@code TypeTreeMap}
   */
  public static <U> Builder<U> build(Class<U> valueType) {
    Check.notNull(valueType);
    return new Builder<>(valueType);
  }

  TypeTreeMap(Map<? extends Class<?>, ? extends V> m, boolean autobox) {
    super(m, autobox);
  }

  /*
   * NB even though TreeMap has no use for a size parameter, we must still carefully distinguish
   * between the constructor that does, and the constructor that does not have the "expectedSize"
   * parameter. The use of the latter signals that an auto-expanding instance is requested. So the
   * static factory methods of TypeTreeMap will pass an arbitrary value (0) for expectedSize in
   * order to produce an auto-expanding instance.
   */
  TypeTreeMap(Map<? extends Class<?>, ? extends V> m, int expectedSize, boolean autobox) {
    super(m, expectedSize, autobox);
  }

  @Override
  Map<Class<?>, V> createBackend(Map<? extends Class<?>, ? extends V> m, boolean autobox) {
    TreeMap<Class<?>, V> backend = new TreeMap<>(autobox ? AUTOBOX_COMP : COMP);
    m.forEach(
        (k, v) -> {
          Check.that(k).is(notNull(), ERR_NULL_KEY);
          Check.that(v).is(notNull(), ERR_NULL_VAL, k.getName());
          backend.put(k, v);
        });
    return backend;
  }

  @Override
  Map<Class<?>, V> createBackend(Map<? extends Class<?>, ? extends V> m, int sz, boolean autobox) {
    return createBackend(m, autobox);
  }

  private static int countAncestors(Class<?> c) {
    int i = 0;
    for (Class<?> x = c.getSuperclass(); x != null; x = x.getSuperclass()) {
      ++i;
    }
    return i;
  }
}
