package nl.naturalis.common.collection;

import java.util.ArrayList;
import java.util.List;

import static nl.naturalis.common.ClassMethods.isA;

class TypeNode<V> {

  Class type;
  V value;

  private List<TypeNode<V>> children;

  void addNode(TypeNode<V> node) {
    if (children == null) {
      children = new ArrayList<>(4);
      children.add(node);
    } else {
      for (int i = 0; i < children.size(); ++i) {
        TypeNode<V> child = children.get(i);
        if (child.type == node.type) {
          child.value = node.value;
          return;
        } else if (isA(node.type, child.type)) {
          child.addNode(node);
          return;
        }
      }
      children.add(node);
    }
  }

}
