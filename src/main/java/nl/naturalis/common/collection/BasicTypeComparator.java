package nl.naturalis.common.collection;

import java.util.Comparator;

import static nl.naturalis.common.ClassMethods.countAncestors;
import static nl.naturalis.common.ClassMethods.getAllInterfaces;

class BasicTypeComparator implements Comparator<Class<?>> {

  @Override
  public int compare(Class<?> c1, Class<?> c2) {
    if (c1 == c2) {
      return 0;
    }
    return subBeforeSuper(c1, c2);
  }

  int subBeforeSuper(Class<?> c1, Class<?> c2) {
    if (c1.isInterface()) {
      if (c2.isInterface()) {
        int i = getAllInterfaces(c2).size() - getAllInterfaces(c1).size();
        if (i != 0) {
          return i;
        }
      }
      return 1;
    }
    if (c2.isInterface()) {
      return -1;
    }
    int i = countAncestors(c2) - countAncestors(c1);
    if (i != 0) {
      return i;
    }
    i = getAllInterfaces(c2).size() - getAllInterfaces(c1).size();
    if (i != 0) {
      return i;
    }
    if (c1.isArray() && c2.isArray()) {
      if (c1.getComponentType() == Object.class) {
        return 1;
      }
      if (c2.getComponentType() == Object.class) {
        return -1;
      }
      return compare(c1.getComponentType(), c2.getComponentType());
    }
    return c1.hashCode() - c2.hashCode();
  }
}
