package nl.naturalis.common;

public final class Pair<T> {

  public static <U> Pair<U> of(U first, U second) {
    return new Pair<>(first, second);
  }

  private final T one;
  private final T two;

  private Pair(T first, T second) {
    one = first;
    two = second;
  }

  public T getFirst() {
    return one;
  }

  public T getSecond() {
    return two;
  }
}
