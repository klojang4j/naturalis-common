package nl.naturalis.common.collection;

import java.util.Comparator;

class TypeComparatorFactory {

  private final Class<?>[] bumped;

  TypeComparatorFactory(Class<?>[] bumped) {
    this.bumped = bumped;
  }

  static Comparator<Class<?>> getComparator(Class<?>[] bumped) {
    if (bumped.length == 0) {
      return new BasicTypeComparator();
    }
    return (c1, c2) -> {
      if (c1 == c2) {
        return 0;
      }
      for (Class<?> c : bumped) {
        if (c1 == c) {
          return -1;
        }
        if (c2 == c) {
          return 1;
        }
      }
      return new BasicTypeComparator().subBeforeSuper(c1, c2);
    };
  }
}
