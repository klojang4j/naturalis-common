package nl.naturalis.common.collection;

import nl.naturalis.common.ArrayInfo;
import nl.naturalis.common.Tuple2;

import java.util.*;

import static nl.naturalis.common.ClassMethods.*;
import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.ObjectMethods.ifNull;

abstract sealed class AbstractTypeNode permits TypeNode, LinkedTypeNode {

  final Class<?> type;
  final Object value;

  AbstractTypeNode(Class<?> type, final Object value) {
    this.type = type;
    this.value = value;
  }

  @SuppressWarnings({"unchecked"})
  <T> T value() {
    return (T) value;
  }

  <T> T get(Class<?> type) {
    return type.isInterface() ? findInterface(type) : findClass(type);
  }

  @SuppressWarnings({"unchecked"})
  <T> T getPrimitive(Class<?> type) {
    Object val;
    if ((val = findClassExact(type)) == null) {
      val = findClass(type, subclasses());
    }
    return (T) val;
  }

  //  Class<?> findSupertype(Class<?> type, boolean autobox) {
  //    if (value != null) {
  //      return Object.class;
  //    }
  //    Class<?> boxed = getBoxedType(type);
  //    if (boxed != null) {
  //      if (autobox) {
  //        for (var node : subclasses()) {
  //          if (isSupertype(node.type, type) || isSupertype(node.type, boxed)) {
  //            return node.type;
  //          }
  //        }
  //        return null;
  //      } else {
  //        return findSupertype(type, subclasses());
  //      }
  //    } else if (!type.isInterface()) {
  //      Class<?> c = findSupertype(type, subclasses());
  //      if (c != null) {
  //        return c;
  //      }
  //    }
  //    return findSupertype(type, subinterfaces());
  //  }

  void collectTypes(List<Class<?>> bucket) {
    for (var node : subclasses()) {
      bucket.add(node.type);
      node.collectTypes(bucket);
    }
    for (var node : subinterfaces()) {
      bucket.add((node.type));
      node.collectTypes(bucket);
    }
  }

  void collectTypesBreadthFirst(List<Class<?>> bucket) {
    for (var node : subclasses()) {
      bucket.add(node.type);
    }
    for (var node : subinterfaces()) {
      bucket.add((node.type));
    }
    for (var node : subclasses()) {
      node.collectTypesBreadthFirst(bucket);
    }
    for (var node : subinterfaces()) {
      node.collectTypesBreadthFirst(bucket);
    }
  }

  <E> void collectValues(Set<E> bucket) {
    for (var node : subclasses()) {
      bucket.add(node.value());
      node.collectValues(bucket);
    }
    for (var node : subinterfaces()) {
      bucket.add(node.value());
      node.collectValues(bucket);
    }
  }

  <E> void collectEntries(List<Map.Entry<Class<?>, E>> bucket) {
    for (var node : subclasses()) {
      bucket.add(new AbstractMap.SimpleImmutableEntry<>(node.type, node.value()));
      node.collectEntries(bucket);
    }
    for (var node : subinterfaces()) {
      bucket.add(new AbstractMap.SimpleImmutableEntry<>(node.type, node.value()));
      node.collectEntries(bucket);
    }
  }

  @SuppressWarnings({"unchecked"})
  private <T> T findClass(Class<?> type) {
    if (!isSupertype(this.type, type)) {
      return null;
    }
    Object val;
    if ((val = findClassExact(type)) == null) {
      if ((val = findAsSubclass(type)) == null) {
        if ((val = findAsImpl(type)) == null) {
          val = this.value;
        }
      }
    }
    return (T) val;
  }

  @SuppressWarnings({"unchecked"})
  private <T> T findInterface(Class<?> type) {
    if (!isSupertype(this.type, type)) {
      return null;
    }
    Object val;
    if ((val = findInterfaceExact(type)) == null) {
      if ((val = findAsExtension(type)) == null) {
        val = this.value;
      }
    }
    return (T) val;
  }

  abstract Collection<? extends AbstractTypeNode> subclasses();

  abstract Collection<? extends AbstractTypeNode> subinterfaces();

  // Will be overridden by TypeNode, as it can first do a
  // quick and easy Map lookup before we start iterating over
  // the values in the Map. There is no such shortcut for
  // LinkedTypeNode (so we assume the JVM will compile this
  // method away in no time for LinkedTypeGraph).
  Object findClassExact(Class<?> type) {
    return null;
  }

  Object findInterfaceExact(Class<?> type) {
    return null;
  }

  private Object findAsSubclass(Class<?> type) {
    return findClass(type, subclasses());
  }

  private Object findAsImpl(Class<?> type) {
    return findClass(type, subinterfaces());
  }

  private Object findAsExtension(Class<?> type) {
    for (AbstractTypeNode node : subinterfaces()) {
      Object val = node.findInterface(type);
      if (val != null) {
        return val;
      }
    }
    return null;
  }

  private static Class<?> findSupertype(Class<?> type,
      Collection<? extends AbstractTypeNode> nodes) {
    for (var node : nodes) {
      if (isSupertype(node.type, type)) {
        return node.type;
      }
    }
    return null;
  }

  private static Object findClass(Class<?> type,
      Collection<? extends AbstractTypeNode> nodes) {
    for (AbstractTypeNode node : nodes) {
      Object val = node.findClass(type);
      if (val != null) {
        return val;
      }
    }
    return null;
  }

  static Class<?> getBoxedType(Class<?> type) {
    if (type.isPrimitive()) {
      return box(type);
    } else if (isPrimitiveArray(type)) {
      ArrayInfo.forClass(type).box();
    }
    return null;
  }

}
