package nl.naturalis.common;

import nl.naturalis.common.check.Check;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static nl.naturalis.common.check.CommonChecks.yes;

/**
 * Simple value container. The value is explicitly allowed to be {@code null}. This
 * class is primarily meant to be used as the return value of methods that would
 * otherwise return {@code null} as the result of a computation, but also if the
 * computation yielded no result. A typical scenario would be iterating over an array
 * and returning a particular element, if found. If the element can legitimately be
 * {@code null}, it is not clear any longer what a returning {@code null} actually
 * means: not present or "really" {@code null}. Using {@code Result}, you would
 * return a {@code Result} containing {@code null} if the element was {@code null}.
 * If the element was not present, you would return {@link Result#none()}. Note that
 * re-using, for example, {@link AtomicReference} for this purpose would be awkward
 * and confusing.
 *
 * @param <T> the type of the value
 */
public final class Result<T> {

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static final Result NONE = new Result(null);

  /**
   * Returns a {@code Result} containing the specified value (possibly {@code
   * null}).
   *
   * @param value The value
   * @param <U> The type of the value
   * @return a {@code Result} containing the specified value
   */
  public static <U> Result<U> of(U value) {
    return new Result<>(value);
  }

  /**
   * Returns a {@code Result} signifying the absence of a result.
   *
   * @return a {@code Result} signifying the absence of a result
   */
  @SuppressWarnings("unchecked")
  public static <U> Result<U> none() {
    return (Result<U>) NONE;
  }

  private final T value;

  private Result(T value) {
    this.value = value;
  }

  /**
   * Returns the value.
   *
   * @return the value
   * @throws NoSuchElementException if this {@code Result} does not contain a
   *     proper result value
   */
  public T get() {
    Check.that(isPresent()).is(yes(),
        () -> new NoSuchElementException("no result available"));
    return value;
  }

  /**
   * Returns whether this {@code Result} represents a proper result.
   *
   * @return whether this {@code Result} represents a proper result
   */
  public boolean isPresent() {
    return this != NONE;
  }

  /**
   * Returns the value if it is a proper result, else the provided default value.
   *
   * @param defaultValue the default value
   * @return the value if it is a proper result, else the provided default value
   */
  public T orElse(T defaultValue) {
    return isPresent() ? value : defaultValue;
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj
        || (obj instanceof Result<?> other && Objects.equals(value, other.value));
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return value != null ? String.format("Result[%s]", value) : "Result.none";
  }

}
