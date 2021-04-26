package nl.naturalis.common.collection;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import nl.naturalis.common.Tuple;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.containingKey;
import static nl.naturalis.common.check.CommonChecks.containingValue;
import static nl.naturalis.common.check.CommonChecks.in;
import static nl.naturalis.common.collection.ModifiableTypeMap.createComparator;
/**
 * An unmodifiable {@link TypeMap} implementation.
 *
 * @author Ayco Holleman
 */
public final class UnmodifiableTypeMap<V> extends TreeMap<Class<?>, V> implements TypeMap<V> {

  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  //                      BUILDER CLASS
  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++

  /**
   * A {@code Builder} class that lets you add types one by one without having to pay attention to
   * the order in which you add them. Once you call {@link #freeze()} on the {@code Builder}, it
   * will make sure the types enter the {@code TypeMap} in the right order (lowest-level classes
   * first, base classes and interfaces last).
   *
   * @author Ayco Holleman
   * @param <V>
   */
  public static final class Builder<V> {
    private final TreeSet<Tuple<Class<?>, V>> tempStorage = new TreeSet<>(TypeMap.TUPLE_COMPARATOR);
    private final List<Predicate<Class<?>>> sortOptions = new ArrayList<>(5);

    private Builder() {}

    public Builder<V> add(Class<?> key, V value) {
      Check.notNull(key, "key");
      Check.notNull(value, "value");
      Tuple<Class<?>, V> tuple = Tuple.of(key, value);
      Check.that(tuple)
          .isNot(in(), tempStorage, "Key already added: %s", key)
          .then(tempStorage::add);
      return this;
    }

    public Builder<V> sortOn(Predicate<Class<?>> sortOption) {
      sortOptions.add(Check.notNull(sortOption).ok());
      return this;
    }

    public TypeMap<V> freeze() {
      UnmodifiableTypeMap<V> utm = new UnmodifiableTypeMap<>(sortOptions);
      tempStorage.forEach(t -> utm.putUnchecked(t.getLeft(), t.getRight()));
      return utm;
    }
  }

  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++

  public static <V> Builder<V> build() {
    return new Builder<>();
  }

  public static <V> TypeMap<V> copyOf(Map<Class<?>, V> source) {
    return copyOf(source, Collections.emptyList());
  }

  public static <V> TypeMap<V> copyOf(
      Map<Class<?>, V> source, List<Predicate<Class<?>>> sortOptions) {
    Check.notNull(source, "source");
    Check.notNull(sortOptions, "sortOptions");
    if (source instanceof TypeMap && sortOptions.isEmpty()) {
      UnmodifiableTypeMap<V> utm = new UnmodifiableTypeMap<>(sortOptions);
      source.forEach((k, v) -> utm.putUnchecked(k, v));
      return utm;
    }
    Check.that(source, "source").isNot(containingKey(), null).isNot(containingValue(), null);
    TreeSet<Map.Entry<Class<?>, V>> temp = new TreeSet<>(TypeMap.ENTRY_COMPARATOR);
    temp.addAll(source.entrySet());
    UnmodifiableTypeMap<V> utm = new UnmodifiableTypeMap<>(sortOptions);
    temp.forEach(e -> utm.putUnchecked(e.getKey(), e.getValue()));
    return utm;
  }

  private UnmodifiableTypeMap(List<Predicate<Class<?>>> sortOptions) {
    super(createComparator(sortOptions));
  }

  private void putUnchecked(Class<?> key, V value) {
    super.put(key, value);
  }

  @Override
  public V put(Class<?> key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V putIfAbsent(Class<?> key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V computeIfAbsent(Class<?> key, Function<? super Class<?>, ? extends V> mappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V computeIfPresent(
      Class<?> key, BiFunction<? super Class<?>, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V compute(
      Class<?> key, BiFunction<? super Class<?>, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V merge(
      Class<?> key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map<? extends Class<?>, ? extends V> map) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean replace(Class<?> key, V oldValue, V newValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V replace(Class<?> key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void replaceAll(BiFunction<? super Class<?>, ? super V, ? extends V> function) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }
}
