package nl.naturalis.common.check;

import java.util.Collection;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import nl.naturalis.common.ObjectMethods;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.Relation;

/**
 * Defines various common tests for arguments and variables.
 *
 * @author Ayco Holleman
 */
public class Checks {

  private Checks() {}

  /**
   * Returns a test that always succeeds. Can be used as argument for the static factory methods of
   * {@link Check}, in case the very first test needs the functionality of the {@code and(...)}
   * methods.
   *
   * @return A {@code Predicate} that always returns {@code true}.
   */
  public static <T> Predicate<T> objValid() {
    return x -> true;
  }

  /**
   * Returns an integer test that always succeeds. Can be used as argument for the static factory
   * methods of {@link Check}, in case the very first test needs the functionality of the {@code
   * and(...)} methods.
   *
   * @return An {@code IntPredicate} that always returns {@code true}.
   */
  public static IntPredicate intValid() {
    return x -> true;
  }

  /**
   * Equivalent to {@link Predicate#not(Predicate) Predicate.not(test)}.
   *
   * @param <X> The type of the argument
   * @param test The test
   * @return The negation of the specified {@code Predicate}.
   */
  public static <X> Predicate<X> not(Predicate<X> test) {
    return Predicate.not(test);
  }

  /**
   * Returns the negation of the specified {@code Relation}. Equivalent to {@link
   * Relation#not(Relation) Relation.not(relation)}.
   *
   * @see Relation#not(Relation)
   * @param <X> The type of the subject of the original relation
   * @param <Y> The type of the object of the original relation
   * @param relation The {@code Relation} to return the negation of
   * @return The negated {@code Relation}
   */
  public static <X, Y> Relation<X, Y> not(Relation<X, Y> relation) {
    return Relation.not(relation);
  }

  /**
   * Returns the negation of the specified {@code IntRelation}. Equivalent to {@link
   * IntRelation#not(Relation) IntRelation.not(relation)}.
   *
   * @see IntRelation#not(Relation)
   * @param relation The {@code IntRelation} to return the negation of
   * @return The negated {@code IntRelation}
   */
  public static IntRelation not(IntRelation relation) {
    return IntRelation.not(relation);
  }

  /**
   * Returns the reverse of the specified {@code Relation}, swapping subject and object of the
   * relation. Equivalent to {@link Relation#reverse(Relation) Relation.reverse(relation)}.
   *
   * @see Relation#reverse(Relation)
   * @param <X> The type of the subject of the original relation
   * @param <Y> The type of the object of the original relation
   * @param relation The {@code Relation} to return the reverse of
   * @return The reverse {@code Relation}
   */
  public static <X, Y> Relation<Y, X> reverse(Relation<X, Y> relation) {
    return Relation.reverse(relation);
  }

  /**
   * Returns the reverse of the specified {@code IntRelation}, swapping subject and object of the
   * relation. Equivalent to {@link IntRelation#reverse(Relation) IntRelation.reverse(relation)}.
   *
   * @see IntRelation#not(Relation)
   * @param relation The {@code IntRelation} to return the negation of
   * @return The negated {@code IntRelation}
   */
  public static IntRelation reverse(IntRelation relation) {
    return IntRelation.not(relation);
  }

  /**
   * Same as {@link Objects#isNull(Object) Objects::isNull}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static Predicate<Object> isNull() {
    return Objects::isNull;
  }

  /**
   * Same as {@link Objects#nonNull(Object) Objects::nonNull}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> notNull() {
    return Objects::nonNull;
  }

  /**
   * Same as {@link ObjectMethods#isEmpty(Object) ObjectMethods::isEmpty}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static Predicate<Object> isEmpty() {
    return ObjectMethods::isEmpty;
  }

  /**
   * Same as {@link ObjectMethods#isNotEmpty(Object) ObjectMethods::isNotEmpty}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> notEmpty() {
    return ObjectMethods::isNotEmpty;
  }

  /**
   * Same as {@link ObjectMethods#isNoneNull(Object) ObjectMethods::isNoneNull}
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> noneNull() {
    return ObjectMethods::isNoneNull;
  }

  /**
   * Same as {@link ObjectMethods#isDeepNotEmpty(Object) ObjectMethods::isDeepNotEmpty}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> deepNotEmpty() {
    return ObjectMethods::isDeepNotEmpty;
  }

  /**
   * Verifies that the argument is an array.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> isArray() {
    return x -> x.getClass().isArray();
  }

  /**
   * Verifies that the argument is an even number.
   *
   * @return A {@code Predicate}
   */
  public static IntPredicate isEven() {
    return x -> x % 2 == 0;
  }

  /**
   * Verifies that the argument is an odd number.
   *
   * @return A {@code Predicate}
   */
  public static IntPredicate isOdd() {
    return x -> x % 2 == 1;
  }

  /**
   * Verifies that the argument is positive.
   *
   * @return A {@code Predicate}
   */
  public static IntPredicate positive() {
    return x -> x > 0;
  }

  /**
   * Verifies that the argument is zero or negative.
   *
   * @return A {@code Predicate}
   */
  public static IntPredicate notPositive() {
    return x -> x <= 0;
  }

  /**
   * Verifies that the argument is negative.
   *
   * @return A {@code Predicate}
   */
  public static IntPredicate negative() {
    return x -> x < 0;
  }

  /**
   * Verifies that the argument is zero or positive.
   *
   * @return A {@code Predicate}
   */
  public static IntPredicate notNegative() {
    return x -> x >= 0;
  }

  /**
   * Same as {@link Collection#contains(Object) Collection::contains}.
   *
   * @param <E> The type of the elements in the {@code Collection}
   * @param <C> The type of the argument
   * @return A {@code Relation}
   */
  public static <E, C extends Collection<E>> Relation<C, E> contains() {
    return Collection::contains;
  }

  /**
   * Returns the {@link Relation#reverse(Relation) reverse} of {@link #contains()}.
   *
   * @param <E> The type of the argument
   * @param <C> The type of the {@code Collection}
   * @return A {@code Relation}
   */
  public static <E, C extends Collection<E>> Relation<E, C> elementOf() {
    return reverse(contains());
  }

  /**
   * Same as {@link Objects#equals(Object, Object) Objects::equals}. (NB the name <i>objEquals</i>
   * allows the method to be statically imported while avoiding a name clash with the ever-present
   * {@link Object#equals(Object) Object.equals} method.)
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  public static <X> Relation<X, X> objEquals() {
    return Objects::equals;
  }

  /**
   * Same as {@code not(objEquals())}.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  public static <X> Relation<X, X> objNotEquals() {
    return not(objEquals());
  }

  /**
   * Implements the <i>greater-than</i> relation for {@code Number} instances, forcing the
   * right-hand side of the relation to be type-compatible with the left-hand side. For example:
   *
   * <p>
   *
   * <pre>
   *    // If x instance of Short
   * return ((Short) x).shortValue() > y.shortValue();
   * </pre>
   *
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> objGreaterThan() {
    return (x, y) -> {
      if (x.getClass() == Integer.class) {
        return ((Integer) x).intValue() > y.intValue();
      } else if (x.getClass() == Long.class) {
        return ((Long) x).longValue() > y.longValue();
      } else if (x.getClass() == Double.class) {
        return ((Double) x).doubleValue() > y.doubleValue();
      } else if (x.getClass() == Float.class) {
        return ((Float) x).floatValue() > y.floatValue();
      } else if (x.getClass() == Short.class) {
        return ((Short) x).shortValue() > y.shortValue();
      } else if (x.getClass() == Byte.class) {
        return ((Byte) x).byteValue() > y.byteValue();
      }
      throw new UnsupportedOperationException();
    };
  }

  /**
   * Implements the <i>less-than</i> relation for {@code Number} instances.
   *
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> objAtLeast() {
    return (x, y) -> {
      if (x.getClass() == Integer.class) {
        return ((Integer) x).intValue() >= y.intValue();
      } else if (x.getClass() == Long.class) {
        return ((Long) x).longValue() >= y.longValue();
      } else if (x.getClass() == Double.class) {
        return ((Double) x).doubleValue() >= y.doubleValue();
      } else if (x.getClass() == Float.class) {
        return ((Float) x).floatValue() >= y.floatValue();
      } else if (x.getClass() == Short.class) {
        return ((Short) x).shortValue() >= y.shortValue();
      }
      return ((Byte) x).byteValue() >= y.byteValue();
    };
  }

  /**
   * Implements the <i>less-than</i> relation for {@code Number} instances.
   *
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> objLessThan() {
    return not(objAtLeast());
  }

  /**
   * Implements the <i>less-than-or-equal-to</i> relation for {@code Number} instances.
   *
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> objAtMost() {
    return not(objGreaterThan());
  }

  /**
   * Reverse of {@link Class#isInstance(Object) Class::isInstance} (Y is on the left-hand side of
   * the relation).
   *
   * @param <X> The type of the argument
   * @param <Y> The type of the object of the relationship
   * @return A {@code Relation}
   */
  public static <X, Y extends Class<?>> Relation<X, Y> instanceOf() {
    return (x, y) -> y.isInstance(x);
  }

  /**
   * Returns the <i>X == Y</i> relation.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation equalTo() {
    return (x, y) -> x == y;
  }

  /**
   * Returns the <i>X != Y</i> relation.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation notEqualTo() {
    return (x, y) -> x != y;
  }

  /**
   * Returns the <i>X &lt; Y</i> relation.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation greaterThan() {
    return (x, y) -> x > y;
  }

  /**
   * Returns the <i>X &gt;= Y</i> relation.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation atLeast() {
    return (x, y) -> x >= y;
  }

  /**
   * Returns the <i>X &lt; Y</i> relation.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation lessThan() {
    return (x, y) -> x < y;
  }

  /**
   * Returns the <i>X &lt;= Y</i> relation.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation atMost() {
    return (x, y) -> x <= y;
  }

  /**
   * Returns the <i>X multiple-of Y</i> relation.
   *
   * @return
   */
  public static IntRelation multipleOf() {
    return (x, y) -> x % y == 0;
  }
}
