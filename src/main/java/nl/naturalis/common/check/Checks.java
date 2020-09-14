package nl.naturalis.common.check;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.ObjectMethods;
import nl.naturalis.common.Sizeable;
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
   * Returns a test that always succeeds. Can be used as constructor argument for the {@link Check}
   * class if the first test you need to execute needs to be accompanied by a custom error message.
   * In that case you will have to use the {@code and(...)} methods of {@code Check}.
   *
   * @return
   */
  public static <T> Predicate<T> valid() {
    return x -> true;
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
   * Same as {@link ObjectMethods#isDeepNotNull(Object) ObjectMethods::isDeepNotNull}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> deepNotNull() {
    return ObjectMethods::isDeepNotNull;
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
   * A heavily overloaded <i>greater-than</i> test applicable to multiple types of objects.
   *
   * <p>
   *
   * <ul>
   *   <li>If the argument is an instance of {@code Number}, it checks that it is greater than the
   *       {@code Number} to compare it against. For example like so: {@code ((Short)
   *       obj).shortValue() > number.shortValue()}
   *   <li>If the argument is an instance of {@link CharSequence}, it checks that the length of the
   *       {@code CharSequence} is greater than the {@code Number} to compare it against
   *   <li>If the argument is an instance of {@link Collection}, it checks that the size of the
   *       {@code Collection} is greater than the {@code Number} to compare it against
   *   <li>If the argument is an instance of {@link Map}, it checks that the size of the {@code Map}
   *       is greater than the {@code Number} to compare it against
   *   <li>If the argument is an array, it checks that its length is greater than the {@code Number}
   *       to compare it against
   *   <li>If the argument is an instance of {@link Sizeable}, it checks that the size of the {@code
   *       Sizeable} is greater than the {@code Number} to compare it against
   * </ul>
   *
   * @return A {@code Relation}
   */
  @SuppressWarnings("rawtypes")
  public static <X, Y extends Number> Relation<X, Y> objGreaterThan() {
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
      } else if (x instanceof CharSequence) {
        return ((CharSequence) x).length() > y.intValue();
      } else if (x instanceof Collection) {
        return ((Collection) x).size() > y.intValue();
      } else if (x instanceof Map) {
        return ((Map) x).size() > y.intValue();
      } else if (x.getClass().isArray()) {
        return Array.getLength(x) > y.intValue();
      } else if (x instanceof Sizeable) {
        return ((Sizeable) x).size() > y.intValue();
      }
      throw new UnsupportedOperationException();
    };
  }

  /**
   * A heavily overloaded <i>greater-than-or-equal-to</i> test applicable to multiple types of
   * objects. See {@link #greaterThan()}.
   *
   * @return A {@code Relation}
   */
  @SuppressWarnings("rawtypes")
  public static <X, Y extends Number> Relation<X, Y> objAtLeast() {
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
      } else if (x.getClass() == Byte.class) {
        return ((Byte) x).byteValue() >= y.byteValue();
      } else if (x instanceof CharSequence) {
        return ((CharSequence) x).length() >= y.intValue();
      } else if (x instanceof Collection) {
        return ((Collection) x).size() >= y.intValue();
      } else if (x instanceof Map) {
        return ((Map) x).size() >= y.intValue();
      } else if (x.getClass().isArray()) {
        return Array.getLength(x) >= y.intValue();
      } else if (x instanceof Sizeable) {
        return ((Sizeable) x).size() >= y.intValue();
      }
      throw new UnsupportedOperationException();
    };
  }

  /**
   * A heavily overloaded <i>less-than</i> test applicable to multiple types of objects. See {@link
   * #greaterThan()}.
   *
   * @return A {@code Relation}
   */
  public static <X, Y extends Number> Relation<X, Y> objLessThan() {
    return not(objAtLeast());
  }

  /**
   * A heavily overloaded <i>less-than-or-equal</i> test applicable to multiple types of objects.
   * See {@link #greaterThan()}.
   *
   * @return A {@code Relation}
   */
  public static <X, Y extends Number> Relation<X, Y> objAtMost() {
    return not(objGreaterThan());
  }

  /**
   * Verifies that a {@code CharSequence}, {@code Collection}, {@code Map}, array or {@code
   * Sizeable} has a certain length c.q. size. For any other type of object an {@link
   * UnsupportedOperationException} will be thrown.
   *
   * @param <X> The type of the argument
   * @param <Y> A {@code Number} type
   * @return A {@code Relation}
   */
  @SuppressWarnings("rawtypes")
  public static <X, Y extends Number> Relation<X, Y> hasSize() {
    return (x, y) -> {
      if (x instanceof CharSequence) {
        return ((CharSequence) x).length() == y.intValue();
      } else if (x instanceof Collection) {
        return ((Collection) x).size() == y.intValue();
      } else if (x instanceof Map) {
        return ((Map) x).size() == y.intValue();
      } else if (x.getClass().isArray()) {
        return Array.getLength(x) == y.intValue();
      } else if (x instanceof Sizeable) {
        return ((Sizeable) x).size() == y.intValue();
      }
      throw new UnsupportedOperationException();
    };
  }

  /**
   * Same as {@link ClassMethods#isA(Object, Class) ClassMethods::isA}.
   *
   * @param <X> The type of the argument
   * @param <Y> The type of the object of the relationship
   * @return A {@code Relation}
   */
  public static <X, Y extends Class<?>> Relation<X, Y> instanceOf() {
    return ClassMethods::isA;
  }

  /**
   * The {@code x == y} relation.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation equalTo() {
    return (x, y) -> x == y;
  }

  /**
   * The {@code x != y} relation.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation notEquals() {
    return (x, y) -> x != y;
  }

  /**
   * The {@code x > y} relation.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation greaterThan() {
    return (x, y) -> x > y;
  }

  /**
   * The {@code x >= y} relation.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation atLeast() {
    return (x, y) -> x >= y;
  }

  /**
   * The {@code x < y} relation.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation lessThan() {
    return (x, y) -> x < y;
  }

  /**
   * The {@code x <= y} relation.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation atMost() {
    return (x, y) -> x <= y;
  }
}
