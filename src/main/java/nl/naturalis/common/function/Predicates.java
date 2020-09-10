package nl.naturalis.common.function;

import java.util.Objects;
import java.util.function.Predicate;
import nl.naturalis.common.ObjectMethods;

/**
 * Various prefab {@link Predicate} implementations.
 *
 * @author Ayco Holleman
 */
public class Predicates {

  private Predicates() {}

  /**
   * Same as {@link Objects#isNull(Object) Objects::isNull} but more concise as static import.
   *
   * @param <T> The type of the object being tested
   * @return An instance of {@code Predicate}
   */
  public static <T> Predicate<T> isNull() {
    return Objects::isNull;
  }

  /**
   * Same as {@link Objects#nonNull(Object) Objects::nonNull} but more concise as static import.
   *
   * @param <T> The type of the object being tested
   * @return An instance of {@code Predicate}
   */
  public static <T> Predicate<T> isNotNull() {
    return Objects::nonNull;
  }

  /**
   * Same as {@link ObjectMethods#isEmpty(Object) ObjectMethods::isEmpty} but more concise as static
   * import.
   *
   * @param <T> The type of the object being tested
   * @return An instance of {@code Predicate}
   */
  public static <T> Predicate<T> isEmpty() {
    return ObjectMethods::isEmpty;
  }

  /**
   * Same as {@link ObjectMethods#isNotEmpty(Object) ObjectMethods::isNotEmpty} but more concise as
   * static import.
   *
   * @param <T> The type of the object being tested
   * @return An instance of {@code Predicate}
   */
  public static <T> Predicate<T> isNotEmpty() {
    return ObjectMethods::isNotEmpty;
  }

  /**
   * Same as {@link ObjectMethods#isNoneNull(Object) ObjectMethods::isNoneNull} but more concise as
   * static import.
   *
   * @param <T> The type of the object being tested
   * @return An instance of {@code Predicate}
   */
  public static <T> Predicate<T> isNoneNull() {
    return ObjectMethods::isNoneNull;
  }

  /**
   * Same as {@link ObjectMethods#isDeepNotNull(Object) ObjectMethods::isDeepNotNull} but more
   * concise as static import.
   *
   * @param <T> The type of the object being tested
   * @return An instance of {@code Predicate}
   */
  public static <T> Predicate<T> isDeepNotNull() {
    return ObjectMethods::isDeepNotNull;
  }

  /**
   * Same as {@link ObjectMethods#isDeepNotEmpty(Object) ObjectMethods::isDeepNotEmpty} but more
   * concise as static import.
   *
   * @param <T> The type of the object being tested
   * @return An instance of {@code Predicate}
   */
  public static <T> Predicate<T> isDeepNotEmpty() {
    return ObjectMethods::isDeepNotEmpty;
  }
}
