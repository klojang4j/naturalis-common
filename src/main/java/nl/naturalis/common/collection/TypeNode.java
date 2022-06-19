package nl.naturalis.common.collection;

import java.util.*;

import static nl.naturalis.common.ClassMethods.*;
import static nl.naturalis.common.ObjectMethods.ifNotNull;

final class TypeNode extends AbstractTypeNode {

  private final Map<Class<?>, TypeNode> subclasses;
  private final Map<Class<?>, TypeNode> subinterfaces;

  TypeNode(Class<?> type,
      Object value,
      Map<Class<?>, TypeNode> subclasses,
      Map<Class<?>, TypeNode> subinterfaces) {
    super(type, value);
    this.subclasses = subclasses;
    this.subinterfaces = subinterfaces;
  }

  @Override
  Collection<? extends AbstractTypeNode> subclasses() {
    return subclasses.values();
  }

  @Override
  Collection<? extends AbstractTypeNode> subinterfaces() {
    return subinterfaces.values();
  }

  @Override
  Object findClassExact(Class<?> type) {
    return ifNotNull(subclasses.get(type), TypeNode::value);
  }

  @Override
  Object findInterfaceExact(Class<?> type) {
    return ifNotNull(subinterfaces.get(type), TypeNode::value);
  }

}
