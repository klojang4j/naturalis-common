package nl.naturalis.common.collection;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.check.CommonChecks.containingKey;
import static nl.naturalis.common.check.CommonChecks.containingValue;
import static nl.naturalis.common.check.CommonChecks.keyIn;

/**
 * A modifiable {@link TypeMap} implementation. Although instances of this class are modifiable, the
 * only way to get data into them is via the constructor or via de {@link #put(Class, Object) put}
 * method. All other methods that would modify the map through an {@link
 * UnsupportedOperationException}.
 *
 * @author Ayco Holleman
 * @param <V> The value type
 */
public class ModifiableTypeMap<V> extends TreeMap<Class<?>, V> implements TypeMap<V> {

  static final String ERR_DUPLICATE =
      "Class %s or one of its superclasses or interfaces has already been added "
          + "to the map. Overwriting not permitted. Add lowest-level classes first, "
          + "base classes and interfaces last";

  static Comparator<Class<?>> createComparator(List<Predicate<Class<?>>> sortOptions) {
    return (c1, c2) -> {
      if (isA(c1, c2)) {
        return 0;
      }
      int min = Integer.MIN_VALUE;
      int max = Integer.MAX_VALUE;
      // Prefer values associated with superclasses over values with interfaces
      if (c1.isInterface() && !c2.isInterface()) {
        return max--;
      } else if (c1 == Object.class && c2 != Object.class) {
        return max--;
      } else if (c1.isInterface()
          && c2.isInterface()
          && c1.getInterfaces().length == 0
          && c2.getInterfaces().length != 0) {
        return max--;
      } else {
        for (Predicate<Class<?>> p : sortOptions) {
          if (p.test(c1) && !p.test(c2)) {
            return max--;
          }
          if (!p.test(c1) && p.test(c2)) {
            return min++;
          }
        }
      }
      return c1.hashCode() - c2.hashCode();
    };
  }

  public ModifiableTypeMap() {
    this(Collections.emptyList());
  }

  public ModifiableTypeMap(List<Predicate<Class<?>>> sortOptions) {
    super(createComparator(sortOptions));
  }

  public ModifiableTypeMap(Map<Class<?>, V> source) {
    this(source, Collections.emptyList());
  }

  /**
   * Creates a new {@code TypeMap} from the entries in the specified source map. The source map's
   * entries are first copied and sorted such that the lowest-level classes enter this {@code Map}
   * first while base classes and interfaces come last. The {@code sortOptions} argument allows you
   * to apply extra sorting to the map keys beyond the sorting imposed by an internally created
   * {@link Comparator}. The predicates are passed on to the {@code Comparator} such that if the
   * {@code Predicate} evaluates to {@code true} for the first argument to {@link
   * Comparator#compare(Object, Object) Comparator.compare(class1, class2}, and false for the second
   * argument, then {@code class1} will be pushed to the front and {@code class2} to the back.
   *
   * @param source
   * @param sortOptions
   */
  public ModifiableTypeMap(Map<Class<?>, V> source, List<Predicate<Class<?>>> sortOptions) {
    super(createComparator(Check.notNull(sortOptions, "sortOptions").ok()));
    Check.notNull(source, "source");
    if (source instanceof TypeMap && sortOptions.isEmpty()) {
      source.forEach((k, v) -> ModifiableTypeMap.super.put(k, v));
    } else {
      Check.that(source, "source").isNot(containingKey(), null).isNot(containingValue(), null);
      Set<Map.Entry<Class<?>, V>> temp = new TreeSet<>(TypeMap.ENTRY_COMPARATOR);
      temp.addAll(source.entrySet());
      temp.forEach(e -> ModifiableTypeMap.super.put(e.getKey(), e.getValue()));
    }
  }

  @Override
  public V put(Class<?> key, V value) {
    Check.notNull(key, "key").isNot(keyIn(), this, ERR_DUPLICATE, key.getName());
    Check.notNull(value, "value");
    return super.put(key, value);
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
}
