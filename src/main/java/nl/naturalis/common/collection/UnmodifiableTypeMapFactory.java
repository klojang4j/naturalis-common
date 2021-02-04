package nl.naturalis.common.collection;

import java.util.Collections;
import java.util.Map;
import java.util.TreeSet;
import nl.naturalis.common.Tuple;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.notIn;
import static nl.naturalis.common.collection.TypeMap.comp3;
/**
 * A factory for unmodfiable {@code TypeMap} instances. This class will let you add types without
 * having to pay attention to the order which you add them (see {@link TypeMap}.
 *
 * @author Ayco Holleman
 */
public final class UnmodifiableTypeMapFactory {

  public static final class Builder<V> {
    private final TreeSet<Tuple<Class<?>, V>> tempStorage = new TreeSet<>(comp3);

    private Builder() {}

    public Builder<V> with(Class<?> key, V value) {
      Check.notNull(key, "key");
      Check.notNull(value, "value");
      Tuple<Class<?>, V> tuple = Tuple.of(key, value);
      Check.that(tuple).is(notIn(), tempStorage).then(tempStorage::add);
      return this;
    }

    public Map<Class<?>, V> freeze() {
      TypeMap<V> tm = new TypeMap<>(tempStorage);
      return Collections.unmodifiableMap(tm);
    }
  }

  public static <V> Builder<V> build() {
    return new Builder<>();
  }
}
