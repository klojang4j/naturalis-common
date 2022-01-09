package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.*;

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
    private final LinkedHashMap<Class<?>, U> tmp = new LinkedHashMap<>();
    private boolean autoExpand = false;
    private boolean autobox = false;
    private Class<?>[] bumped = EMPTY;

    private Builder(Class<U> valueType) {
      this.valueType = valueType;
    }

    /**
     * Enables the automatic addition of new subtypes. Note that by default auto-expansion is
     * disabled.
     *
     * @return This {@code Builder} instance
     */
    public Builder<U> autoExpand() {
      this.autoExpand = true;
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
     * Bumps the specified types to the head of the key set. This can be used to make the {@code
     * TypeTreeMap} quickly find ubiquitous types like {@code String.class} and {@code int.class}.
     * Note though that this may break the ordering from less abstract to more abstract types.
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
      if (bumped.length > 0 && !tmp.keySet().containsAll(Set.of(bumped))) {
        throw new IllegalStateException("Bumped classes must have been added too");
      }
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
    Comparator<Class<?>> cmp = new TypeTreeMapHelper(bumped).getComparator();
    TreeMap<Class<?>, V> tmp = new TreeMap<>(cmp);
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
}
