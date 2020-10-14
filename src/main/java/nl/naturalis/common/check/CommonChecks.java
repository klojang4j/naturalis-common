package nl.naturalis.common.check;

import java.io.File;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import nl.naturalis.common.ObjectMethods;
import nl.naturalis.common.Sizeable;
import nl.naturalis.common.StringMethods;
import nl.naturalis.common.Tuple;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;
import static nl.naturalis.common.check.Messages.*;

/**
 * Defines various common tests for arguments. These tests have short, informative error messages
 * associated with them in case the argument does not pass the test. Many of them are plain,
 * unadorned, method references and they <i>only</i> check what they advertise to be checking.
 * <b>None of them do a preliminary null-check on the argument</b>, except those dedicated to this
 * task (like {@link #notNull()}). They rely upon being embedded within in chain of checks on a
 * {@link Check} object, the first of which should be a <i>not-null</i> check.
 *
 * @author Ayco Holleman
 */
public class CommonChecks {

  static final IdentityHashMap<Object, Function<Object[], String>> messages;

  private static ArrayList<Tuple<Object, Function<Object[], String>>> temp = new ArrayList<>();

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

  /* ++++++++++++++ Predicate ++++++++++++++ */

  /**
   * Verifies that the argument is null. Equivalent to {@link Objects#isNull(Object)
   * Objects::isNull}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> nullRef() {
    return Objects::isNull;
  }

  static {
    add(nullRef(), msgNullRef());
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

  static {
    add(notNull(), msgNotNull());
  }

  /**
   * Verifies that the argument is empty. Equivalent to {@link ObjectMethods#isEmpty(Object)
   * ObjectMethods::isEmpty}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> empty() {
    return ObjectMethods::isEmpty;
  }

  static {
    add(empty(), msgEmpty());
  }

  /**
   * Verifies that the argument is not empty. Equivalent to {@link ObjectMethods#isNotEmpty(Object)
   * ObjectMethods::isNotEmpty}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> notEmpty() {
    return ObjectMethods::isNotEmpty;
  }

  static {
    add(notEmpty(), msgNotEmpty());
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

  static {
    add(noneNull(), msgNoneNull());
  }

  /**
   * Verifies that the argument is recursively non-empty as per {@link
   * ObjectMethods#isDeepNotEmpty(Object) ObjectMethods.isDeepNotEmpty}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> deepNotEmpty() {
    return ObjectMethods::isDeepNotEmpty;
  }

  static {
    add(deepNotEmpty(), msgDeepNotEmpty());
  }

  /**
   * Verifies that a {@code String} argument is not null and not blank. Equivalent to {@link
   * StringMethods#isNotBlank(Object) StringMethods::isNotBlank}.
   *
   * @return A {@code Predicate}
   */
  public static Predicate<String> notBlank() {
    return StringMethods::isNotBlank;
  }

  static {
    add(notBlank(), msgNotBlank());
  }

  /**
   * Verifies that the argument is an existing, regular file. Equivalent to {@link File#isFile()
   * File::isFile}.
   *
   * @returnn A {@code Predicate}
   */
  public static Predicate<File> fileExists() {
    return File::isFile;
  }

  static {
    add(fileExists(), msgFileExists());
  }

  /**
   * Verifies that the argument is an existing directory. Equivalent to {@link File#isDirectory()
   * File::isDirectory}.
   *
   * @returnn A {@code Predicate}
   */
  public static Predicate<File> directoryExists() {
    return File::isDirectory;
  }

  static {
    add(directoryExists(), msgDirectoryExists());
  }

  /**
   * Verifies that the argument is not a file of any type.
   *
   * @returnn A {@code Predicate}
   */
  public static Predicate<File> fileNotExists() {
    return f -> !f.exists();
  }

  static {
    add(fileNotExists(), msgFileNotExists());
  }

  /**
   * Verifies that the argument is a readable file (implies that the file exists). Equivalent to
   * {@link File#canRead() File::canRead}.
   *
   * @returnn A {@code Predicate}
   */
  public static Predicate<File> readable() {
    return File::canRead;
  }

  static {
    add(readable(), msgReadable());
  }

  /**
   * Verifies that the argument is a writable file (implies that the file exists). Equivalent to
   * {@link File#canWrite() File::canWrite}.
   *
   * @return
   */
  public static Predicate<File> writable() {
    return File::canWrite;
  }

  static {
    add(writable(), msgWritable());
  }

  /* ++++++++++++++ IntPredicate ++++++++++++++ */

  /**
   * Verifies that the argument is an even number.
   *
   * @return An {@code IntPredicate}
   */
  public static IntPredicate even() {
    return x -> x % 2 == 0;
  }

  static {
    add(even(), msgIsEven());
  }

  /**
   * Verifies that the argument is an odd number.
   *
   * @return An {@code IntPredicate}
   */
  public static IntPredicate odd() {
    return x -> x % 2 == 1;
  }

  static {
    add(odd(), msgIsOdd());
  }

  /**
   * Verifies that the argument is positive.
   *
   * @return An {@code IntPredicate}
   */
  public static IntPredicate positive() {
    return x -> x > 0;
  }

  static {
    add(positive(), msgPositive());
  }

  /**
   * Verifies that the argument is zero or negative.
   *
   * @return An {@code IntPredicate}
   */
  public static IntPredicate notPositive() {
    return x -> x <= 0;
  }

  static {
    add(notPositive(), msgNotPositive());
  }

  /**
   * Verifies that the argument is negative.
   *
   * @return An {@code IntPredicate}
   */
  public static IntPredicate negative() {
    return x -> x < 0;
  }

  static {
    add(negative(), msgNegative());
  }

  /**
   * Verifies that the argument is zero or positive.
   *
   * @return An {@code IntPredicate}
   */
  public static IntPredicate notNegative() {
    return x -> x >= 0;
  }

  static {
    add(notNegative(), msgNotNegative());
  }

  /* ++++++++++++++ Relation ++++++++++++++ */

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

  static {
    add(instanceOf(), msgInstanceOf());
  }

  /**
   * Verifies that the argument is an array.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> array() {
    return x -> x.getClass().isArray();
  }

  static {
    add(array(), msgAnArray());
  }

  /**
   * Verifies that a {@code Collection} contains a particular value. Equivalent to {@link
   * Collection#contains(Object) Collection::contains}.
   *
   * @param <E> The type of the elements in the {@code Collection}
   * @param <C> The type of the argument
   * @return A {@code Relation}
   */
  public static <E, C extends Collection<? super E>> Relation<C, E> contains() {
    return Collection::contains;
  }

  static {
    add(contains(), msgContains());
  }

  /**
   * Verifies that a {@code Collection} does not contain a particular value.
   *
   * @param <E> The type of the elements in the {@code Collection}
   * @param <C> The type of the argument
   * @return A {@code Relation}
   */
  public static <E, C extends Collection<? super E>> Relation<C, E> notContains() {
    return (x, y) -> !x.contains(y);
  }

  static {
    add(notContains(), msgNotContains());
  }

  /**
   * Verifies that the argument is an element of a {@code Collection}.
   *
   * @param <E> The type of the argument
   * @param <C> The type of the {@code Collection}
   * @return A {@code Relation}
   */
  public static <E, C extends Collection<? super E>> Relation<E, C> in() {
    return (x, y) -> y.contains(x);
  }

  static {
    add(in(), msgIn());
  }

  /**
   * Verifies that the argument is not an element of a {@code Collection}.
   *
   * @param <E> The type of the argument
   * @param <C> The type of the {@code Collection}
   * @return A {@code Relation}
   */
  public static <E, C extends Collection<? super E>> Relation<E, C> notIn() {
    return (x, y) -> !y.contains(x);
  }

  static {
    add(notIn(), msgNotIn());
  }

  /**
   * Verifies that a {@code Map} contains a key. Equivalent to {@link Map#containsKey(Object)
   * Map::containsKey}.
   *
   * @param <K> The type of the keys within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <K, M extends Map<? super K, ?>> Relation<M, K> hasKey() {
    return Map::containsKey;
  }

  static {
    add(hasKey(), msgHasKey());
  }

  /**
   * Verifies that a {@code Map} does not contain a key.
   *
   * @param <K> The type of the keys within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <K, M extends Map<? super K, ?>> Relation<M, K> notHasKey() {
    return (x, y) -> !x.containsKey(y);
  }

  static {
    add(notHasKey(), msgNotHasKey());
  }

  /**
   * Verifies that a {@code Map} contains a value. Equivalent to {@link Map#containsValue(Object)
   * Map::containsValue}.
   *
   * @param <V> The type of the values within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <V, M extends Map<?, ? super V>> Relation<M, V> hasValue() {
    return Map::containsValue;
  }

  static {
    add(hasValue(), msgHasValue());
  }

  /**
   * Verifies that a {@code Map} does not contain a value.
   *
   * @param <V> The type of the values within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <V, M extends Map<?, ? super V>> Relation<M, V> notHasValue() {
    return (x, y) -> !x.containsValue(y);
  }

  static {
    add(hasValue(), msgNotHasValue());
  }

  /**
   * Verifies that the argument is equal to a particular value. Equivalent to {@link
   * Objects#equals(Object, Object) Objects::equals}.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  public static <X> Relation<X, X> equalTo() {
    return Objects::equals;
  }

  static {
    add(equalTo(), msgEqualTo());
  }

  /**
   * Verifies that the argument is not equal to a particular value.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  public static <X> Relation<X, X> notEqualTo() {
    return (x, y) -> !Objects.equals(x, y);
  }

  static {
    add(notEqualTo(), msgNotEqualTo());
  }

  /**
   * Verifies that the argument references the same object as some other reference.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  public static <X> Relation<X, X> sameAs() {
    return (x, y) -> x == y;
  }

  static {
    add(sameAs(), msgSameAs());
  }

  /**
   * Verifies that the argument does not reference the same object than some other reference.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  public static <X> Relation<X, X> notSameAs() {
    return (x, y) -> x != y;
  }

  static {
    add(notSameAs(), msgNotSameAs());
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

  static {
    add(nullOr(), msgNullOr());
  }

  /**
   * Verifies that the argument is greater than a particular value, widening both to {@code double}
   * before comparing them.
   *
   * @param <X> The type of the argument
   * @param <Y> The type of the value to compare the argument to
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> greaterThan() {
    return (x, y) -> x.doubleValue() > y.doubleValue();
  }

  static {
    // Can recycle messages for int comparisons!
    add(greaterThan(), msgGt());
  }

  /**
   * Verifies that the argument is greater than or equal to a particular value, widening both to
   * {@code double} before comparing them.
   *
   * @param <X> The type of the argument
   * @param <Y> The type of the value to compare the argument to
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> atLeast() {
    return (x, y) -> x.doubleValue() >= y.doubleValue();
  }

  static {
    add(atLeast(), msgGte());
  }

  /**
   * Verifies that the argument is less than a particular value, widening both to {@code double}
   * before comparing them.
   *
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> lessThan() {
    return (x, y) -> x.doubleValue() < y.doubleValue();
  }

  static {
    add(lessThan(), msgLt());
  }

  /**
   * Verifies that the argument is less than or equal to a particular value, widening both to {@code
   * double} before comparing them.
   *
   * @param <X> The type of the argument
   * @param <Y> The type of the value to compare the argument to
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> atMost() {
    return (x, y) -> x.doubleValue() <= y.doubleValue();
  }

  static {
    add(atMost(), msgLte());
  }

  /**
   * Verifies that the argument ends with a particular character sequence. Equivalent to {@link
   * String#endsWith(String) String::endsWith}.
   *
   * @return A {@code Relation}
   */
  public static Relation<String, String> endsWith() {
    return String::endsWith;
  }

  static {
    add(endsWith(), msgEndsWith());
  }

  /**
   * Verifies that the argument does not end with a particular character sequence.
   *
   * @return A {@code Relation}
   */
  public static Relation<String, String> notEndsWith() {
    return (x, y) -> !x.endsWith(y);
  }

  static {
    add(notEndsWith(), msgNotEndsWith());
  }

  /**
   * Verifies that the argument contains a particular character sequence. Equivalent to {@link
   * String#contains(CharSequence) String::contains}.
   *
   * @return A {@code Relation}
   */
  public static <T extends CharSequence> Relation<String, T> hasSubstr() {
    return String::contains;
  }

  static {
    add(hasSubstr(), msgHasSubstr());
  }

  /**
   * Verifies that the argument does not contain a particular character sequence.
   *
   * @return A {@code Relation}
   */
  public static <T extends CharSequence> Relation<String, T> notHasSubstr() {
    return (x, y) -> !x.contains(y);
  }

  static {
    add(notHasSubstr(), msgNotHasSubstr());
  }

  /* ++++++++++++++ ObjIntRelation ++++++++++++++ */

  /**
   * Verifies that the argument's length or size is equal to a particular value. The argument must
   * be an array, {@code CharSequence}, {@code Collection}, {@code Map} or {@link Sizeable}. For any
   * other type of argument this method will throw an {@code UnsupportedOperationException}.
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
      throw notSupported("sizeEquals", x);
    };
  }

  static {
    add(sizeEquals(), msgSizeEquals());
  }

  /**
   * Verifies that the argument's length or size is not equal to a particular value. The argument
   * must be an array, {@code CharSequence}, {@code Collection}, {@code Map} or {@link Sizeable}.
   * For any other type of argument this method will throw an {@code UnsupportedOperationException}.
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
      throw notSupported("sizeNotEquals", x);
    };
  }

  static {
    add(sizeNotEquals(), msgSizeNotEquals());
  }

  /**
   * Verifies that the argument's length or size is greater than a particular value. The argument
   * must be an array, {@code CharSequence}, {@code Collection}, {@code Map} or {@link Sizeable}.
   * For any other type of argument this method will throw an {@code UnsupportedOperationException}.
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
      throw notSupported("sizeGreaterThan", x);
    };
  }

  static {
    add(sizeGreaterThan(), msgSizeGreaterThan());
  }

  /**
   * Verifies that the argument's length or size is greater than or equal to a particular value. The
   * argument must be an array, {@code CharSequence}, {@code Collection}, {@code Map} or {@link
   * Sizeable}. For any other type of argument this method will throw an {@code
   * UnsupportedOperationException}.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  @SuppressWarnings("rawtypes")
  public static <X> ObjIntRelation<X> sizeAtLeast() {
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
      throw notSupported("sizeAtLeast", x);
    };
  }

  static {
    add(sizeAtLeast(), msgSizeAtLeast());
  }

  /**
   * Verifies that the argument's length or size is less than a particular value. The argument must
   * be an array, {@code CharSequence}, {@code Collection}, {@code Map} or {@link Sizeable}. For any
   * other type of argument this method will throw an {@code UnsupportedOperationException}.
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
      throw notSupported("sizeLessThan", x);
    };
  }

  static {
    add(sizeLessThan(), msgSizeLessThan());
  }

  /**
   * Verifies that the argument's length or size is less than or equal to a particular value. The
   * argument must be an array, {@code CharSequence}, {@code Collection}, {@code Map} or {@link
   * Sizeable}. For any other type of argument this method will throw an {@code
   * UnsupportedOperationException}.
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
      throw notSupported("sizeAtMost", x);
    };
  }

  static {
    add(sizeAtMost(), msgSizeAtMost());
  }

  /* ++++++++++++++ IntObjRelation ++++++++++++++ */

  /**
   * Verifies that the argument is a valid index for a {@code List} operation like {@link
   * List#get(int) get} or {@link List#set(int, Object) set}. In other words, that the argument is
   * greater than or equal to zero and less than the size of the list.
   *
   * @param <E> The type of the elements in the {@code List}
   * @param <L> The type of the {@code List}
   * @return An {@code IntObjRelation}
   */
  public static <E, L extends List<? super E>> IntObjRelation<L> indexOf() {
    return (x, y) -> x >= 0 && x < y.size();
  }

  static {
    add(indexOf(), msgIndexOf());
  }

  /**
   * Verifies that the argument is a valid "to" index for a {@code List} operation like {@link
   * List#subList(int, int) List.sublist}. In other words, that the argument is greater than or
   * equal to zero and less than or equal to the size of the list.
   *
   * @return An {@code IntObjRelation}
   */
  public static <E, L extends List<? super E>> IntObjRelation<L> toIndexOf() {
    return (x, y) -> x >= 0 && x <= y.size();
  }

  static {
    add(toIndexOf(), msgToIndexOf());
  }

  /* ++++++++++++++ IntRelation ++++++++++++++ */

  /**
   * Verifies that the argument is equal to a particular value.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation eq() {
    return (x, y) -> x == y;
  }

  static {
    add(eq(), msgEq());
  }

  /**
   * Verifies that the argument is not equal to a particular value.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation ne() {
    return (x, y) -> x != y;
  }

  static {
    add(ne(), msgNe());
  }

  /**
   * Verifies that the argument is greater than a particular value.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation gt() {
    return (x, y) -> x > y;
  }

  static {
    add(gt(), msgGt());
  }

  /**
   * Verifies that the argument is greater than or equal to a particular value.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation gte() {
    return (x, y) -> x >= y;
  }

  static {
    add(gte(), msgGte());
  }

  /**
   * Verifies that the argument is less than a particular value.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation lt() {
    return (x, y) -> x < y;
  }

  static {
    add(lt(), msgLt());
  }

  /**
   * Verifies that the argument is less than or equal to a particular value.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation lte() {
    return (x, y) -> x <= y;
  }

  static {
    add(lte(), msgLte());
  }

  /**
   * Verifies that the argument is a whole mulitple of a particular integer.
   *
   * @return An {@code IntRelation}
   */
  public static IntRelation multipleOf() {
    return (x, y) -> x % y == 0;
  }

  static {
    add(multipleOf(), msgMultipleOf());
  }

  static {
    messages = new IdentityHashMap<>(temp.size());
    temp.forEach(t -> t.addTo(messages));
    temp = null;
  }

  private static UnsupportedOperationException notSupported(String test, Object obj) {
    String fmt = "Test \"%s\" not applicable to %s";
    String msg = String.format(fmt, test, obj.getClass().getName());
    return new UnsupportedOperationException(msg);
  }

  private static void add(Object test, Function<Object[], String> message) {
    temp.add(new Tuple<>(test, message));
  }
}
