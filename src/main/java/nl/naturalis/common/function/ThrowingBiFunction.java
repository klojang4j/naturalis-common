package nl.naturalis.common.function;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, E extends Exception> {

  R apply(T arg0, U arg1) throws E;

}
