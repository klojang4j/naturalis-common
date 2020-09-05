package nl.naturalis.common.function;

@FunctionalInterface
public interface Relation<T, U> {

  boolean exists(T t, U u);
}
