package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.*;

import static nl.naturalis.common.check.CommonChecks.deepNotNull;
import static nl.naturalis.common.check.CommonChecks.instanceOf;

/**
 * A subclass of {@link AbstractTypeMap} that is internally backed by a {@link TreeMap}.
 *
 * @author Ayco Holleman
 * @param <V> The type of the values in the {@code Map}
 */
public final class TypeTreeMap<V> extends AbstractTypeMap<V> {

  static final Class<?>[] EMPTY_TYPE_ARRAY = new Class[0];

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
    private Class<?>[] bumped = EMPTY_TYPE_ARRAY;

    private Builder(Class<U> valueType) {
      this.valueType = valueType;
    }

    /**
     * Whether to enable auto-expansion. By default auto-expansion is disabled.
     *
     * @return This {@code Builder} instance
     */
    public Builder<U> autoExpand(boolean autoExpand) {
      this.autoExpand = autoExpand;
      return this;
    }

    /**
     * Whether to enable the "autoboxing" feature. See description above. By default autoboxing is
     * enabled.
     *
     * @return This {@code Builder} instance
     */
    public Builder<U> autobox(boolean autobox) {
      this.autobox = autobox;
      return this;
    }

    /**
     * Bumps the specified types to the head of the key set. This can be used to make the {@code
     * TypeTreeMap} quickly find ubiquitous types like {@code String.class} and {@code int.class},
     * but it will negatively impact general retrieval time as it deepens the tree. Also note that
     * this may break the ordering from less abstract to more abstract types.
     *
     * @param types The types to bump to the head of the head of the key set.
     * @return This {@code Builder} instance
     */
    public Builder<U> bump(Class<?>... types) {
      this.bumped = Check.that(types, "types").is(deepNotNull()).ok();
      return this;
    }

    /**
     * Associates the specified type with the specified value.
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
     * Associates the specified value with the specified types.
     *
     * @param value The value
     * @param types The types to associate the value with
     * @return This {@code Builder} instance
     */
    public Builder<U> addMultiple(U value, Class<?>... types) {
      Check.notNull(value, "value").is(instanceOf(), valueType);
      Check.that(types, "types").is(deepNotNull());
      Arrays.stream(types).forEach(t -> tmp.put(t, value));
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
        throw new IllegalStateException("Bumped types must also be added to the map");
      }
      return (TypeTreeMap<W>) new TypeTreeMap<>(tmp, autoExpand, autobox, bumped);
    }
  }

  /////////////////////////////////////////////////////////////////////
  // END BUILDER CLASS
  /////////////////////////////////////////////////////////////////////

  /**
   * Converts the specified {@code Map} to a non-auto-expanding, autoboxing {@code TypeMap} .
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @return A non-auto-expanding, autoboxing {@code TypeTreeMap}
   */
  public static <U> TypeTreeMap<U> copyOf(Map<Class<?>, U> src) {
    return copyOf(src, true, false);
  }

  /**
   * Converts the specified {@code Map} to a {@code TypeTreeMap} .
   *
   * @param <U> The type of the values in the {@code Map}
   * @param src The {@code Map} to convert
   * @param autoExpand Whether to enable the auto-expand feature (see class comments)
   * @param autobox Whether to enable the "autoboxing" feature (see class comments)
   * @return A {@code TypeTreeMap} instance that will grow as new types are being requested from it
   */
  public static <U> TypeTreeMap<U> copyOf(
      Map<Class<?>, U> src, boolean autoExpand, boolean autobox) {
    Check.notNull(src, "src");
    if (src.getClass() == TypeTreeMap.class) {
      TypeTreeMap<U> ttm = (TypeTreeMap<U>) src;
      if (ttm.autobox == autobox && ttm.autoExpand == autoExpand) {
        return ttm;
      }
    }
    return new TypeTreeMap<>(src, autoExpand, autobox, EMPTY_TYPE_ARRAY);
  }

  /**
   * Returns a {@code Builder} instance that lets you configure a {@code TypeTreeMap}
   *
   * @param <U> The type of the values in the {@code TypeTreeMap} to be built
   * @param valueType The class object corresponding to the type of the values in the {@code
   *     TypeTreeMap}
   * @return A {@code Builder} instance that lets you configure a {@code TypeTreeMap}
   */
  public static <U> Builder<U> build(Class<U> valueType) {
    Check.notNull(valueType);
    return new Builder<>(valueType);
  }

  private final TreeMap<Class<?>, V> backend;

  TypeTreeMap(
      Map<Class<?>, ? extends V> m, boolean autoExpand, boolean autobox, Class<?>[] bumped) {
    super(autoExpand, autobox);
    Comparator<Class<?>> cmp = TypeComparatorFactory.getComparator(bumped);
    TreeMap<Class<?>, V> tmp = new TreeMap<>(cmp);
    tmp.putAll(m);
    this.backend = tmp;
  }

  // Special-purpose constructor for TypeTreeSet.prettySort
  TypeTreeMap(Map<Class<?>, ? extends V> m) {
    super(false, false);
    TreeMap<Class<?>, V> tmp = new TreeMap<>(new PrettyTypeComparator());
    tmp.putAll(m);
    this.backend = tmp;
  }

  @Override
  Map<Class<?>, V> backend() {
    return backend;
  }
}
