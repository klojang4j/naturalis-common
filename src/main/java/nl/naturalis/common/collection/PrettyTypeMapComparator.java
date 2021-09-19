package nl.naturalis.common.collection;

import java.util.Comparator;
import static nl.naturalis.common.ClassMethods.countAncestors;
import static nl.naturalis.common.ClassMethods.getAllInterfaces;
import static nl.naturalis.common.ClassMethods.isWrapper;

class PrettyTypeMapComparator implements Comparator<Class<?>> {

  @Override
  public int compare(Class<?> c1, Class<?> c2) {
    if (c1 == c2) {
      return 0;
    }
    if (c1.isPrimitive()) {
      return -1;
    }
    if (c2.isPrimitive()) {
      return 1;
    }
    if (isWrapper(c1)) {
      return -1;
    }
    if (isWrapper(c2)) {
      return 1;
    }
    if (c1.isEnum()) {
      return -1;
    }
    if (c2.isEnum()) {
      return 1;
    }
    if (c1.isArray()) {
      return 1;
    }
    if (c2.isArray()) {
      return -1;
    }
    if (c1.isInterface()) {
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
    return c1.hashCode() - c2.hashCode();
  }
}
