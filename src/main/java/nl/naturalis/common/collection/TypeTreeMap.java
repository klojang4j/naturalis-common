package nl.naturalis.common.collection;

import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ArrayMethods.isOneOf;
import static nl.naturalis.common.ClassMethods.*;
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

  static final Class<?>[] EMPTY = new Class[0];

  private static final Comparator<Class<?>> COMP =
      (c1, c2) -> {
        if (c1 == c2) {
          return 0;
        } else if (c1 == Object.class) {
          return 1;
        } else if (c2 == Object.class) {
          return -1;
        } else if (c1.isPrimitive()) {
          return -1;
        } else if (c2.isPrimitive()) {
          return 1;
        } else if (isWrapper(c1)) {
          return -1;
        } else if (isWrapper(c2)) {
          return 1;
        } else if (c1.isEnum()) {
          return -1;
        } else if (c2.isEnum()) {
          return 1;
        } else if (c1.isArray()) {
          return 1;
        } else if (c2.isArray()) {
          return -1;
        } else if (countAncestors(c1) > countAncestors(c2)) {
          return -1;
        } else if (Modifier.isAbstract(c1.getModifiers())) {
          return 1;
        } else if (Modifier.isAbstract(c2.getModifiers())) {
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

  /////////////////////////////////////////////////////////////////////
  // BEGIN BUILDER CLASS
  /////////////////////////////////////////////////////////////////////

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
    private boolean autobox = false;
    private Class<?>[] bumped = EMPTY;

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
     * Bumps the specified type to the head of the key set, so they will be found quickly. Could be
     * used to quickly find ubiquitous types like {@code String.class} and {@code int.class}. Note
     * though that this may break the ordering from less abstract to more abstract types.
     *
     * @param findFast The types to bump to the kead of the head of the key set.
     * @return This {@code Builder} instance
     */
    public Builder<U> bump(Class<?>... findFast) {
      this.bumped = Check.notNull(findFast).ok();
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
        return (TypeTreeMap<W>) new TypeTreeMap<>(tmp, autoExpand, autobox, bumped);
      }
      if (autobox) {
        tmp.forEach(
            (k, v) -> {
              if (k.isPrimitive() && !tmp.containsKey(box(k))) {
                tmp.put(box(k), v);
              } else if (isWrapper(k) && !tmp.containsKey(unbox(k))) {
                tmp.put(unbox(k), v);
              }
            });
      }
      return (TypeTreeMap<W>) new TypeTreeMap<>(tmp, autoExpand, autobox, bumped);
    }
  }

  /////////////////////////////////////////////////////////////////////
  // END BUILDER CLASS
  /////////////////////////////////////////////////////////////////////

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
    return new TypeTreeMap<>(src, false, autobox, EMPTY);
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
    return new TypeTreeMap<>(src, true, autobox, EMPTY);
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

  private final TreeMap<Class<?>, V> backend;

  TypeTreeMap(
      Map<? extends Class<?>, ? extends V> m,
      boolean autoExpand,
      boolean autobox,
      Class<?>[] bumped) {
    super(autoExpand, autobox);
    TreeMap<Class<?>, V> tmp = new TreeMap<>(createComparator(bumped));
    m.forEach(
        (k, v) -> {
          Check.that(k).is(notNull(), ERR_NULL_KEY);
          Check.that(v).is(notNull(), ERR_NULL_VAL, k.getName());
          tmp.put(k, v);
        });
    this.backend = tmp;
  }

  @Override
  Map<Class<?>, V> backend() {
    return backend;
  }

  private static Comparator<Class<?>> createComparator(Class<?>[] bumped) {
    if (bumped.length == 0) {
      return COMP;
    }
    return (c1, c2) -> isOneOf(c1, bumped) ? -1 : isOneOf(c2) ? 1 : COMP.compare(c1, c2);
  }

  private static int countAncestors(Class<?> c) {
    int i = 0;
    for (Class<?> x = c.getSuperclass(); x != null; x = x.getSuperclass()) {
      ++i;
    }
    return i;
  }
}
