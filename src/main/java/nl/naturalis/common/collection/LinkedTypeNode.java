package nl.naturalis.common.collection;

import java.util.*;

import static nl.naturalis.common.ArrayMethods.find;
import static nl.naturalis.common.ClassMethods.*;

final class LinkedTypeNode extends AbstractTypeNode {

  private final List<LinkedTypeNode> subclasses;
  private final List<LinkedTypeNode> subinterfaces;

  LinkedTypeNode(Class<?> type,
      Object value,
      List<LinkedTypeNode> subclasses,
      List<LinkedTypeNode> subinterfaces) {
    super(type, value);
    this.subclasses = subclasses;
    this.subinterfaces = subinterfaces;
  }

  @Override
  Collection<? extends AbstractTypeNode> subclasses() {
    return subclasses;
  }

  @Override
  Collection<? extends AbstractTypeNode> subinterfaces() {
    return subinterfaces;
  }

}
