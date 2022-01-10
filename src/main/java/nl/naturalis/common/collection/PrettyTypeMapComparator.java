package nl.naturalis.common.collection;

import java.util.Comparator;

import static nl.naturalis.common.ClassMethods.*;

class PrettyTypeMapComparator implements Comparator<Class<?>> {

  @Override
  public int compare(Class<?> c1, Class<?> c2) {
    if (c1 == c2) {
      return 0;
    }
    if(c1.isPrimitive() && c2.isPrimitive()) {
      return c1.getSimpleName().compareTo(c2.getSimpleName());
    }
    if (c1.isPrimitive()) {
      return -1;
    }
    if (c2.isPrimitive()) {
      return 1;
    }
    if(isWrapper(c1) && isWrapper(c2)) {
      return c1.getSimpleName().compareTo(c2.getSimpleName());
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
    if (c1 == Object.class) {
      return 1;
    }
    if (c2 == Object.class) {
      return -1;
    }
    if (c1.isArray() && c2.isArray()) {
      return compare(c1.getComponentType(), c2.getComponentType());
    }
    if (c1.isArray()) {
      return 1;
    }
    if (c2.isArray()) {
      return -1;
    }
    if (c1.isInterface() && c2.isInterface()) {
      if (getAllInterfaces(c1).size() < getAllInterfaces(c2).size()) {
        return 1;
      }
      if (getAllInterfaces(c1).size() > getAllInterfaces(c2).size()) {
        return -1;
      }
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
    // Compare the number of directly or directly implemented interfaces
    // for regular classes. Thus, classes not implementing any interface
    // are regarded as more primitive and should come first
    if (getAllInterfaces(c1).size() < getAllInterfaces(c2).size()) {
      return 1;
    }
    if (getAllInterfaces(c1).size() > getAllInterfaces(c2).size()) {
      return -1;
    }
    return c1.getSimpleName().compareTo(c2.getSimpleName());
  }
}
