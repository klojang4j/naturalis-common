package nl.naturalis.common;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.*;

/** Methods assisting in functional programming */
public class FunctionalMethods {

  /**
   * Converts the specified {@code Predicate} into an {@code IntPredicate}. Can be used to force the
   * compiler to interpret a lambda as an {@code IntPredicate} rather than a {@code Predicate}.
   *
   * @param predicate A {@code Predicate}, supposedly in the form of a lambda
   * @return The {@code IntPredicate} version of the {@code Predicate}
   */
  public static IntPredicate asInt(Predicate<Integer> predicate) {
    return x -> predicate.test(Integer.valueOf(x));
  }

  /**
   * Simply returns the argument. Can be used to force the compiler to interpret a lambda as a
   * {@code Predicate} rather than an {@code IntPredicate}.
   *
   * @param <T> The type of the argument being tested
   * @param predicate A {@code Predicate}, supposedly in the form of a lambda
   * @return The argument
   */
  public static <T> Predicate<T> asObj(Predicate<T> predicate) {
    return predicate;
  }

  /**
   * Converts the specified function from one that computes a result into one that computes an
   * {@code Optional} of that result.
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
   * Converts the specified function from one that computes a result into one that computes an
   * {@code OptionalInt} of that result.
   *
   * @param <T> The input type
   * @param fnc The function
   * @return A new function that wraps the result of the specified function into an {@code
   *     OptionalInt}
   */
  public static <T> Function<T, OptionalInt> asOptionalInt(ToIntFunction<? super T> fnc) {
    return x -> OptionalInt.of(fnc.applyAsInt(x));
  }

  /**
   * Converts the specified {@code Function} to a {@code BiFunction} whose second argument is
   * ignored.
   *
   * @param <T> The type of the (first) argument of the {@code Function} and {@code Bifunction}
   * @param <U> The type of the second argument of the {@code Bifunction}
   * @param <R> The return type
   * @param fnc The {@code Function} to be converted
   * @return The resulting {@code BiFunction}
   */
  public static <T, U, R> BiFunction<T, U, R> toBiFunction(Function<T, R> fnc) {
    return (arg0, arg1) -> fnc.apply(arg0);
  }

  private FunctionalMethods() {}
}
