package nl.naturalis.common.check;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import nl.naturalis.common.ObjectMethods;
import nl.naturalis.common.Sizeable;
import nl.naturalis.common.StringMethods;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;

/**
 * Defines various common tests for arguments. These tests have short, informative error messages
 * associated with them in case the argument does not pass the test. Many of them are plain,
 * unadorned, method references and they <i>only</i> check what they advertise to be checking.
 * <i>None of them do a preliminary null-check on the argument</i> (except of course those dedicated
 * to this task, like {@link #notNull()}). They rely upon being embedded within in chain of checks
 * on a {@link Check} object, the first of which should be a <i>not-null</i> check.
 *
 * @author Ayco Holleman
 */
public class CommonChecks {

  private CommonChecks() {}

  /**
   * Equivalent to {@link Predicate#not(Predicate) Predicate.not(test)}. Not associated with an
   * error message.
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
   * Relation#not(Relation) Relation.not(relation)}. Not associated with an error message.
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
   * IntRelation#not(Relation) IntRelation.not(relation)}. Not associated with an error message.
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
   * relation. Equivalent to {@link Relation#reverse(Relation) Relation.reverse(relation)}. Not
   * associated with an error message.
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
   * Not associated with an error message.
   *
   * @see IntRelation#not(Relation)
   * @param relation The {@code IntRelation} to return the negation of
   * @return The negated {@code IntRelation}
   */
  public static IntRelation reverse(IntRelation relation) {
    return IntRelation.not(relation);
  }

  /**
   * Verifies that the argument is null. Equivalent to {@link Objects#isNull(Object)
   * Objects::isNull}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> isNull() {
    return Objects::isNull;
  }

  /**
   * Verifies that the argument is not null. Equivalent to {@link Objects#nonNull(Object)
   * Objects::nonNull}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> notNull() {
    return Objects::nonNull;
  }

  /**
   * Verifies that the argument is empty as per {@link ObjectMethods#isEmpty(Object)
   * ObjectMethods::isEmpty}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> isEmpty() {
    return ObjectMethods::isEmpty;
  }

  /**
   * Verifies that the argument is not empty as per {@link ObjectMethods#isNotEmpty(Object)
   * ObjectMethods::isNotEmpty}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> notEmpty() {
    return ObjectMethods::isNotEmpty;
  }

  /**
   * Verifies the argument is not null and does not contain any null values. Equivalent to {@link
   * ObjectMethods#isNoneNull(Object) ObjectMethods::isNoneNull}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> noneNull() {
    return ObjectMethods::isNoneNull;
  }

  /**
   * Verifies that the argument is recursively non-empty as per {@link
   * ObjectMethods#isDeepNotEmpty(Object) ObjectMethods::isDeepNotEmpty}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> deepNotEmpty() {
    return ObjectMethods::isDeepNotEmpty;
  }

  /**
   * Verifies that a {@code String} argument is not null and not blank.
   *
   * @return A {@code Predicate}
   */
  public static Predicate<String> notBlank() {
    return StringMethods::isNotBlank;
  }

  /**
   * Verifies that the argument is an existing, regular file.
   *
   * @returnn A {@code Predicate}
   */
  public static Predicate<File> isFile() {
    return File::isFile;
  }

  /**
   * Verifies that the argument is an existing directory.
   *
   * @returnn A {@code Predicate}
   */
  public static Predicate<File> isDirectory() {
    return File::isDirectory;
  }

  /**
   * Verifies that the argument is not a file of any type.
   *
   * @returnn A {@code Predicate}
   */
  public static Predicate<File> fileNotExists() {
    return f -> !f.exists();
  }

  /**
   * Verifies that the argument is a readable file (implies that the file exists).
   *
   * @returnn A {@code Predicate}
   */
  public static Predicate<File> readable() {
    return File::canRead;
  }

  /**
   * Verifies that the argument is a writable file (implies that the file exists).
   *
   * @return
   */
  public static Predicate<File> writable() {
    return File::canWrite;
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
   * Verifies that a {@code Collection} contains a particular value. Equivalent to {@link
   * Collection#contains(Object) Collection::contains}.
   *
   * @param <E> The type of the elements in the {@code Collection}
   * @param <C> The type of the argument
   * @return A {@code Relation}
   */
  public static <E, C extends Collection<E>> Relation<C, E> contains() {
    return Collection::contains;
  }

  /**
   * Verifies that a {@code Collection} does not contain a particular value.
   *
   * @param <E> The type of the elements in the {@code Collection}
   * @param <C> The type of the argument
   * @return A {@code Relation}
   */
  public static <E, C extends Collection<E>> Relation<C, E> notContains() {
    return (x, y) -> !x.contains(y);
  }

  /**
   * Verifies that the argument is an element of a {@code Collection}.
   *
   * @param <E> The type of the argument
   * @param <C> The type of the {@code Collection}
   * @return A {@code Relation}
   */
  public static <E, C extends Collection<E>> Relation<E, C> elementOf() {
    return (x, y) -> y.contains(x);
  }

  /**
   * Verifies that the argument is not an element of a {@code Collection}.
   *
   * @param <E> The type of the argument
   * @param <C> The type of the {@code Collection}
   * @return A {@code Relation}
   */
  public static <E, C extends Collection<E>> Relation<E, C> notElementOf() {
    return (x, y) -> !y.contains(x);
  }

  /**
   * Verifies that a {@code Map} contains a key.
   *
   * @param <K> The type of the keys within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <K, M extends Map<K, ?>> Relation<M, K> containsKey() {
    return Map::containsKey;
  }

  /**
   * Verifies that a {@code Map} does not contain a key.
   *
   * @param <K> The type of the keys within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <K, M extends Map<K, ?>> Relation<M, K> notContainsKey() {
    return (x, y) -> !x.containsValue(y);
  }

  /**
   * Verifies that a {@code Map} contains a value.
   *
   * @param <V> The type of the values within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <V, M extends Map<?, V>> Relation<M, V> containsValue() {
    return Map::containsValue;
  }

  /**
   * Verifies that a {@code Map} does not contain a value.
   *
   * @param <V> The type of the values within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <V, M extends Map<?, V>> Relation<M, V> notContainsValue() {
    return (x, y) -> !x.containsValue(y);
  }

  /**
   * Verifies that the argument is equal to a particular value. Equivalent to {@link
   * Objects#equals(Object, Object) Objects::equals}.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  public static <X> Relation<X, X> objEquals() {
    return Objects::equals;
  }

  /**
   * Verifies that the argument is not equal to a particular value.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  public static <X> Relation<X, X> objNotEquals() {
    return (x, y) -> !Objects.equals(x, y);
  }

  /**
   * Verifies that the argument is either null or has a particular value.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  public static <X> Relation<X, X> nullOr() {
    return (x, y) -> x == null || x.equals(y);
  }

  /**
   * Verifies that the argument is greater than a particular value, widening or narrowing the type
   * of the argument to the type of the specified value.
   *
   * @param <X> The type of the argument
   * @param <Y> The type of the value to compare the argument to
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> nGreaterThan() {
    return (x, y) -> {
      if (y.getClass() == Integer.class) {
        return x.intValue() > y.intValue();
      } else if (y.getClass() == Long.class) {
        return x.longValue() > y.longValue();
      } else if (y.getClass() == Double.class) {
        return x.doubleValue() > y.doubleValue();
      } else if (y.getClass() == Float.class) {
        return x.floatValue() > y.floatValue();
      } else if (y.getClass() == Short.class) {
        return x.shortValue() > y.shortValue();
      }
      return x.byteValue() > y.byteValue();
    };
  }

  /**
   * Verifies that the argument is greater than or equal to a particular value, widening or
   * narrowing the type of the argument to the type of the specified value.
   *
   * @param <X> The type of the argument
   * @param <Y> The type of the value to compare the argument to
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> nAtLeast() {
    return (x, y) -> {
      if (y.getClass() == Integer.class) {
        return x.intValue() >= y.intValue();
      } else if (y.getClass() == Long.class) {
        return x.longValue() >= y.longValue();
      } else if (y.getClass() == Double.class) {
        return x.doubleValue() >= y.doubleValue();
      } else if (y.getClass() == Float.class) {
        return x.floatValue() >= y.floatValue();
      } else if (y.getClass() == Short.class) {
        return x.shortValue() >= y.shortValue();
      }
      return x.byteValue() >= y.byteValue();
    };
  }

  /**
   * Verifies that the argument is less than a particular value, widening or narrowing the type of
   * the argument to the type of the specified value.
   *
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> nLessThan() {
    return (x, y) -> !nAtLeast().exists(x, y);
  }

  /**
   * Verifies that the argument is less than or equal to a particular value, widening or narrowing
   * the type of the argument to the type of the specified value.
   *
   * @param <X> The type of the argument
   * @param <Y> The type of the value to compare the argument to
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> nAtMost() {
    return (x, y) -> !nGreaterThan().exists(x, y);
  }

  /**
   * Verifies that the argument's length or size is equal to a particular value. This method is
   * well-behaved if the argument is a {@code CharSequence}, {@code Collection}, {@code Map}, {@link
   * Sizeable} or array. For any other type of argument this method throws an {@code
   * IllegalArgumentException}.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  @SuppressWarnings("rawtypes")
  public static <X> ObjIntRelation<X> sizeEquals() {
    return (x, y) -> {
      if (x instanceof CharSequence) {
        return ((CharSequence) x).length() == y;
      } else if (x instanceof Collection) {
        return ((Collection) x).size() == y;
      } else if (x instanceof Map) {
        return ((Map) x).size() == y;
      } else if (x.getClass().isArray()) {
        return Array.getLength(x) == y;
      } else if (x instanceof Sizeable) {
        return ((Sizeable) x).size() == y;
      }
      throw notApplicable("sizeEquals", x);
    };
  }

  /**
   * Verifies that the argument's length or size is not equal to a particular value. This method is
   * well-behaved if the argument is a {@code CharSequence}, {@code Collection}, {@code Map}, {@link
   * Sizeable} or array. For any other type of argument this method throws an {@code
   * IllegalArgumentException}.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  @SuppressWarnings("rawtypes")
  public static <X> ObjIntRelation<X> sizeNotEquals() {
    return (x, y) -> {
      if (x instanceof CharSequence) {
        return ((CharSequence) x).length() != y;
      } else if (x instanceof Collection) {
        return ((Collection) x).size() != y;
      } else if (x instanceof Map) {
        return ((Map) x).size() != y;
      } else if (x.getClass().isArray()) {
        return Array.getLength(x) != y;
      } else if (x instanceof Sizeable) {
        return ((Sizeable) x).size() != y;
      }
      throw notApplicable("sizeNotEquals", x);
    };
  }

  /**
   * Verifies that the argument's length or size is greater than a particular value. This method is
   * well-behaved if the argument is a {@code CharSequence}, {@code Collection}, {@code Map}, {@link
   * Sizeable} or array. For any other type of argument this method throws an {@code
   * IllegalArgumentException}.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  @SuppressWarnings("rawtypes")
  public static <X> ObjIntRelation<X> sizeGreaterThan() {
    return (x, y) -> {
      if (x instanceof CharSequence) {
        return ((CharSequence) x).length() > y;
      } else if (x instanceof Collection) {
        return ((Collection) x).size() > y;
      } else if (x instanceof Map) {
        return ((Map) x).size() > y;
      } else if (x.getClass().isArray()) {
        return Array.getLength(x) > y;
      } else if (x instanceof Sizeable) {
        return ((Sizeable) x).size() > y;
      }
      throw notApplicable("sizeGreaterThan", x);
    };
  }

  /**
   * Verifies that the argument's length or size is greater than or equal to a particular value.
   * This method is well-behaved if the argument is a {@code CharSequence}, {@code Collection},
   * {@code Map}, {@link Sizeable} or array. For any other type of argument this method always
   * returns false.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  @SuppressWarnings("rawtypes")
  public static <X> Relation<X, Integer> sizeAtLeast() {
    return (x, y) -> {
      if (x instanceof CharSequence) {
        return ((CharSequence) x).length() >= y;
      } else if (x instanceof Collection) {
        return ((Collection) x).size() >= y;
      } else if (x instanceof Map) {
        return ((Map) x).size() >= y;
      } else if (x.getClass().isArray()) {
        return Array.getLength(x) >= y;
      } else if (x instanceof Sizeable) {
        return ((Sizeable) x).size() >= y;
      }
      throw notApplicable("sizeAtLeast", x);
    };
  }

  /**
   * Verifies that the argument's length or size is less than a particular value. This method is
   * well-behaved if the argument is a {@code CharSequence}, {@code Collection}, {@code Map}, {@link
   * Sizeable} or array. For any other type of argument this method throws an {@code
   * IllegalArgumentException}.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  @SuppressWarnings("rawtypes")
  public static <X> ObjIntRelation<X> sizeLessThan() {
    return (x, y) -> {
      if (x instanceof CharSequence) {
        return ((CharSequence) x).length() < y;
      } else if (x instanceof Collection) {
        return ((Collection) x).size() < y;
      } else if (x instanceof Map) {
        return ((Map) x).size() < y;
      } else if (x.getClass().isArray()) {
        return Array.getLength(x) < y;
      } else if (x instanceof Sizeable) {
        return ((Sizeable) x).size() < y;
      }
      throw notApplicable("sizeLessThan", x);
    };
  }

  /**
   * Verifies that the argument's length or size is less than or equal to a particular value. This
   * method is well-behaved if the argument is a {@code CharSequence}, {@code Collection}, {@code
   * Map}, {@link Sizeable} or array. For any other type of argument this method always returns
   * false.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  @SuppressWarnings("rawtypes")
  public static <X> ObjIntRelation<X> sizeAtMost() {
    return (x, y) -> {
      if (x instanceof CharSequence) {
        return ((CharSequence) x).length() <= y;
      } else if (x instanceof Collection) {
        return ((Collection) x).size() <= y;
      } else if (x instanceof Map) {
        return ((Map) x).size() <= y;
      } else if (x.getClass().isArray()) {
        return Array.getLength(x) <= y;
      } else if (x instanceof Sizeable) {
        return ((Sizeable) x).size() <= y;
      }
      throw notApplicable("sizeLessThan", x);
    };
  }

  /**
   * Verifies that the argument is an intance of a particular class or interface.
   *
   * @param <X> The type of the argument
   * @param <Y> The type of the object of the relationship
   * @return A {@code Relation}
   */
  public static <X, Y extends Class<?>> Relation<X, Y> instanceOf() {
    return (x, y) -> y.isInstance(x);
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
   * Verifies that the argument is equal to a particular value.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation equalTo() {
    return (x, y) -> x == y;
  }

  /**
   * Verifies that the argument is not equal to a particular value.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation notEquals() {
    return (x, y) -> x != y;
  }

  /**
   * Verifies that the argument is greater than a particular value.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation greaterThan() {
    return (x, y) -> x > y;
  }

  /**
   * Verifies that the argument is greater than or equal to a particular value.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation atLeast() {
    return (x, y) -> x >= y;
  }

  /**
   * Verifies that the argument is less than a particular value.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation lessThan() {
    return (x, y) -> x < y;
  }

  /**
   * Verifies that the argument is less than or equal to a particular value.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation atMost() {
    return (x, y) -> x <= y;
  }

  /**
   * Verifies that the argument is a whole mulitple of a particular integer.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation multipleOf() {
    return (x, y) -> x % y == 0;
  }

  private static IllegalArgumentException notApplicable(String test, Object obj) {
    String fmt = "Test \"%s\" not applicable to %s";
    String msg = String.format(fmt, test, obj.getClass().getName());
    return new IllegalArgumentException(msg);
  }
}
