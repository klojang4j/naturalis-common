package nl.naturalis.common.collection;

public interface Treparator<T> {

  public static enum Position {
    EQUAL,
    UNRELATED,
    PARENT,
    CHILD,
    LEFT,
    RIGHT;
  }

  Position compare(T obj1, T obj2);
}
