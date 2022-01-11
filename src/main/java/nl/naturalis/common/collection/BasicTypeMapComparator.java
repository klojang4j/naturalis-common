package nl.naturalis.common.collection;

import java.util.Comparator;

import static nl.naturalis.common.ClassMethods.countAncestors;
import static nl.naturalis.common.ClassMethods.getAllInterfaces;

class BasicTypeMapComparator implements Comparator<Class<?>> {

  @Override
  public int compare(Class<?> c1, Class<?> c2) {
    if (c1 == c2) {
      return 0;
    }
    if (c1 == Object.class) {
      return 1;
    }
    if (c2 == Object.class) {
      return -1;
    }
    if (c1.isInterface()) {
      if (c2.isInterface()) {
        if (getAllInterfaces(c1).size() < getAllInterfaces(c2).size()) {
          return 1;
        }
        if (getAllInterfaces(c1).size() > getAllInterfaces(c2).size()) {
          return -1;
        }
      }
      return 1;
    }
    if (c2.isInterface()) {
      return -1;
    }
    if (countAncestors(c1) < countAncestors(c2)) {
      return 1;
    }
    if (countAncestors(c1) > countAncestors(c2)) {
      return -1;
    }
    if (getAllInterfaces(c1).size() < getAllInterfaces(c2).size()) {
      return 1;
    }
    if (getAllInterfaces(c1).size() > getAllInterfaces(c2).size()) {
      return -1;
    }
    if (c1.isArray() && c2.isArray()) {
      return compare(c1.getComponentType(), c2.getComponentType());
    }
    return c1.hashCode() - c2.hashCode();
  }
}
