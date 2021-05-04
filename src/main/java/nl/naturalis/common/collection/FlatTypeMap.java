package nl.naturalis.common.collection;

import java.util.HashMap;
import java.util.Map;

public class FlatTypeMap<V> extends HashMap<Class<?>, V> {

  public FlatTypeMap() {}

  public FlatTypeMap(int initialCapacity) {
    super(initialCapacity);
  }

  public FlatTypeMap(Map<? extends Class<?>, ? extends V> m) {
    super(m);
  }

  public FlatTypeMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  @Override
  public V get(Object key) {
    return containsKey(key) ? super.get(key) : null;
  }

  @Override
  public boolean containsKey(Object key) {
    if (super.containsKey(key)) {
      return true;
    }
    Class<?> clazz = (Class<?>) key;
    if (clazz != Object.class) {
      Class<?> c0 = clazz.getSuperclass();
      do {
        if (super.containsKey(c0)) {
          super.put(clazz, super.get(c0));
          return true;
        }
        for (Class<?> c1 : clazz.getInterfaces()) {
          if (super.containsKey(c1)) {
            super.put(clazz, super.get(c1));
            return true;
          }
        }
        c0 = clazz.getSuperclass();
      } while (c0 != Object.class);
    }
    return false;
  }
}
