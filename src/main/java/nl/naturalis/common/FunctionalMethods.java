package nl.naturalis.common;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/** Methods assisting in functional programming */
public class FunctionalMethods {

  /**
   * Converts the specified function from one that computes a result from its input into one that
   * computes an {@code Optional} of that result.
   *
   * @param <T> The input type
   * @param <R> The return type of specified function and the contents of the {@code Optional}.
   * @param fnc The function
   * @return A new function that wraps the result of the specified function into an {@code Optional}
   */
  public static <T, R> Function<T, Optional<R>> asOptional(Function<? super T, ? extends R> fnc) {
    return x -> Optional.of(fnc.apply(x));
  }

  /**
   * Converts the specified function from one that computes a result from its input into one that
   * computes an {@code OptionalInt} of that result.
   *
   * @param <T> The input type
   * @param fnc The function
   * @return A new function that wraps the result of the specified function into an {@code
   *     OptionalInt}
   */
  public static <T> Function<T, OptionalInt> asOptionalInt(ToIntFunction<? super T> fnc) {
    return x -> OptionalInt.of(fnc.applyAsInt(x));
  }

  private FunctionalMethods() {}
}
