package nl.naturalis.common.collection;

public class TypeGraphMap<V> {

  private TypeNode<V> root = new TypeNode();

  public void put(Class<?> type, V value) {
    if (type == Object.class) {
      root.value = value;
    }
  }

}
