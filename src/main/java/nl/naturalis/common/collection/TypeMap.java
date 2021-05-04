package nl.naturalis.common.collection;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import nl.naturalis.common.Tuple;
import static nl.naturalis.common.ClassMethods.isA;

/**
 * A {@code Map} extension that returns a non-null value for a type if either the type itself or any
 * of its super types is present in the map. This allows you to define fall-back values (or actions
 * if the value type is a function-like object) for types that have not been explicitly added to the
 * map.
 *
 * <p>Contrary to ordinary maps it is not permitted to add strictly duplicate keys. In other words,
 * once a type is in the map, you cannot overwrite its value any longer. Also, it is not permitted
 * to add a type if any of its super types has already been added to the map. Therefore you must add
 * the lowest-level classes first and base classes and interfaces last. {@code null} keys and {@code
 * null} values are not allowed. It is permitted to add {@code Object.class} but note that {@link
 * Map#containsKey(Object) containsKey()} will then always return true.
 *
 * <p>Contrary to the {@link TreeMap} class (the super class of {@link ModifiableTypeMap} and {@link
 * UnmodifiableTypeMap}) you cannot specify your own {@link Comparator} for sorting the key set. You
 * can, however, specify sort options that will be "appended" to an internally created {@code
 * Comparator}.
 *
 * @author Ayco Holleman
 * @param <V> The type of the values in the {@code}
 */
interface TypeMap<V> extends NavigableMap<Class<?>, V> {

  static final Comparator<Map.Entry<Class<?>, ?>> ENTRY_COMPARATOR =
      (e1, e2) -> {
        return compare(e1.getKey(), e2.getKey());
      };

  static final Comparator<Tuple<Class<?>, ?>> TUPLE_COMPARATOR =
      (t1, t2) -> {
        return compare(t1.getLeft(), t2.getLeft());
      };

  private static int compare(Class<?> class1, Class<?> class2) {
    if (class1 == class2) {
      return 0;
    } else if (isA(class1, class2)) {
      return Integer.MIN_VALUE;
    } else if (isA(class2, class1)) {
      return Integer.MAX_VALUE;
    }
    return class1.hashCode() - class2.hashCode();
  }
}
