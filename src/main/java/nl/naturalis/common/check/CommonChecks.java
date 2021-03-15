package nl.naturalis.common.check;

import java.io.File;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import nl.naturalis.common.*;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;
import static nl.naturalis.common.ClassMethods.prettySimpleClassName;
import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.check.InvalidCheckException.notApplicable;
import static nl.naturalis.common.check.Messages.*;

/**
 * Defines various common tests for arguments. These tests have short, informative error messages
 * associated with them in case the argument does not pass the test. Many of them are plain,
 * unadorned, method references and only check what they advertise to be checking. <b>None of them
 * do a preliminary null-check on the argument</b>, except those dedicated to this task (like {@link
 * #notNull()}). They rely upon being embedded within in chain of checks on a {@link Check} object,
 * the first of which should be a <i>not-null</i> check.
 *
 * @author Ayco Holleman
 */
public class CommonChecks {

  // Stores the error messages associated with the checks (or rather message
  // generators)
  static final IdentityHashMap<Object, Formatter> messages;
  // Stores the names of the checks
  static final IdentityHashMap<Object, String> names;

  private static ArrayList<Tuple<Object, Formatter>> tmp0 = new ArrayList<>(50);
  private static ArrayList<Tuple<Object, String>> tmp1 = new ArrayList<>(50);

  private CommonChecks() {}

  /* ++++++++++++++ Predicate ++++++++++++++ */

  /**
   * Verifies that the argument is null. Equivalent to {@link Objects#isNull(Object)
   * Objects::isNull}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> nullPointer() {
    return Objects::isNull;
  }

  static {
    addMessage(nullPointer(), msgNullPointer());
    addName(nullPointer(), "nullPointer");
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
    addMessage(notNull(), msgNotNull());
    addName(notNull(), "notNull");
  }

  /**
   * Verifies that the argument is true. If the argument is a Boolean rather than a boolean and the
   * argument might be null, you should not use this check. Use {@link #nullOr() nullOr(true)}.
   *
   * @return A {@code Predicate}
   */
  public static Predicate<Boolean> yes() {
    return x -> x;
  }

  static {
    addMessage(yes(), msgYes());
    addName(yes(), "yes");
  }

  /**
   * Verifies that the argument is true. If the argument is a Boolean rather than a boolean and the
   * argument might be null, you should not use this check. Use {@link #nullOr() nullOr(false)}.
   *
   * @return A {@code Predicate}
   */
  public static Predicate<Boolean> no() {
    return x -> !x;
  }

  static {
    addMessage(no(), msgNo());
    addName(no(), "no");
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
    addMessage(empty(), msgEmpty());
    addName(empty(), "empty");
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
    addMessage(notEmpty(), msgNotEmpty());
    addName(notEmpty(), "notEmpty");
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
    addMessage(noneNull(), msgNoneNull());
    addName(noneNull(), "noneNull");
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
    addMessage(deepNotEmpty(), msgDeepNotEmpty());
    addName(deepNotEmpty(), "deepNotEmpty");
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
    addMessage(notBlank(), msgNotBlank());
    addName(notBlank(), "notBlank");
  }

  /**
   * Verifies that a {@code String} argument is a valid integer. Equivalent to {@link
   * NumberMethods#isInteger(String) NumberMethods::isInteger}.
   *
   * @return A {@code Predicate}
   */
  public static Predicate<String> integer() {
    return NumberMethods::isInteger;
  }

  static {
    addMessage(integer(), msgInteger());
    addName(integer(), "integer");
  }

  /**
   * Verifies that a {@code String} argument is a valid port number. More precisely: that it can be
   * included as the port segment of a URL. That is: the string must not be blank, it must not start
   * with '+' or '-' and it must be a short (max 65535). This method implicitly checks for null, so
   * need not be preceeded by a {@link #notNull()} check.
   *
   * @return A {@code Predicate}
   */
  public static Predicate<String> validPortNumber() {
    return str -> {
      if (StringMethods.isBlank(str)) {
        return false;
      }
      char c = str.charAt(0);
      if (c == '+' || c == '-') {
        return false;
      }
      return NumberMethods.isShort(str);
    };
  }

  static {
    addMessage(validPortNumber(), msgValidPortNumber());
    addName(validPortNumber(), "validPortNumber");
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
    addMessage(fileExists(), msgFileExists());
    addName(fileExists(), "fileExists");
  }

  /**
   * Verifies that the argument is an existing directory. Equivalent to {@link File#isDirectory()
   * File::isDirectory}.
   *
   * @returnn A {@code Predicate}
   */
  public static Predicate<File> directory() {
    return File::isDirectory;
  }

  static {
    addMessage(directory(), msgDirectory());
    addName(directory(), "directory");
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
    addMessage(fileNotExists(), msgFileNotExists());
    addName(fileNotExists(), "fileNotExists");
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
    addMessage(readable(), msgReadable());
    addName(readable(), "readable");
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
    addMessage(writable(), msgWritable());
    addName(writable(), "writable");
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
    addMessage(even(), msgEven());
    addName(even(), "even");
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
    addMessage(odd(), msgOdd());
    addName(odd(), "odd");
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
    addMessage(positive(), msgPositive());
    addName(positive(), "positive");
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
    addMessage(negative(), msgNegative());
    addName(negative(), "negative");
  }

  /* ++++++++++++++ Relation ++++++++++++++ */

  /**
   * If the argument is a {@link Class} object, this method verifies that it is a subclass or
   * implementation of the specified class; otherwise that the argument is an intance of it.
   *
   * @param <X> The type of the argument
   * @param <Y> The type of the object of the relationship
   * @return A {@code Relation}
   */
  public static <X> Relation<X, Class<?>> instanceOf() {
    return (x, y) -> {
      if (x.getClass() == Class.class) {
        return ClassMethods.isA((Class<?>) x, y);
      }
      return y.isInstance(x);
    };
  }

  static {
    addMessage(instanceOf(), msgInstanceOf());
    addName(instanceOf(), "instanceOf");
  }

  /**
   * If the argument is a {@link Class} object, this method verifies that it is not a subclass or
   * implementation of the specified class; otherwise that the argument is not an intance of it.
   *
   * @param <X> The type of the argument
   * @param <Y> The type of the object of the relationship
   * @return A {@code Relation}
   */
  public static <X> Relation<X, Class<?>> notInstanceOf() {
    return (x, y) -> !y.isInstance(x);
  }

  static {
    addMessage(notInstanceOf(), msgInstanceOf());
    addName(notInstanceOf(), "notInstanceOf");
  }

  /**
   * If the argument is a {@link Class} object, verifies that {@code argument.isArray()} is {code
   * true}, else verifies that {@code argument.getClass().isArray()} is {@code true}.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> array() {
    return x -> x.getClass() == Class.class ? ((Class<?>) x).isArray() : x.getClass().isArray();
  }

  static {
    addMessage(array(), msgArray());
    addName(array(), "array");
  }

  /**
   * Verifies that a {@code Collection} contains a particular value. Equivalent to {@link
   * Collection#contains(Object) Collection::contains}.
   *
   * @param <E> The type of the elements in the {@code Collection}
   * @param <C> The type of the argument
   * @return A {@code Relation}
   */
  public static <E, C extends Collection<? super E>> Relation<C, E> containing() {
    return Collection::contains;
  }

  static {
    addMessage(containing(), msgContaining());
    addName(containing(), "containing");
  }

  /**
   * Verifies that a {@code Collection} does not contain a particular value.
   *
   * @param <E> The type of the elements in the {@code Collection}
   * @param <C> The type of the argument
   * @return A {@code Relation}
   */
  public static <E, C extends Collection<? super E>> Relation<C, E> notContaining() {
    return (x, y) -> !x.contains(y);
  }

  static {
    addMessage(notContaining(), msgNotContaining());
    addName(notContaining(), "notContaining");
  }

  /**
   * Verifies the presence of an element within a {@code Collection}.
   *
   * @param <E> The type of the argument
   * @param <C> The type of the {@code Collection}
   * @return A {@code Relation}
   */
  public static <E, C extends Collection<? super E>> Relation<E, C> in() {
    return (x, y) -> y.contains(x);
  }

  static {
    addMessage(in(), msgIn());
    addName(in(), "in");
  }

  /**
   * Verifies the absence of an element within a {@code Collection}.
   *
   * @param <E> The type of the argument
   * @param <C> The type of the {@code Collection}
   * @return A {@code Relation}
   */
  public static <E, C extends Collection<? super E>> Relation<E, C> notIn() {
    return (x, y) -> !y.contains(x);
  }

  static {
    addMessage(notIn(), msgNotIn());
    addName(notIn(), "notIn");
  }

  /**
   * Verifies that a {@code Collection} argument is a subset of another {@code Collection}.
   * Equivalent to {@link Collection#containsAll(Collection) Collection::containsAll}.
   *
   * @param <E> The type of the elements in the {@code Collection}
   * @param <C0> The type of the argument (the subject of the {@code Relation})
   * @param <C1> The type of the object of the {@code Relation}
   * @return A {@code Relation}
   */
  public static <E, C0 extends Collection<? super E>, C1 extends Collection<? super E>>
      Relation<C0, C1> supersetOf() {
    return Collection::containsAll;
  }

  static {
    addMessage(supersetOf(), msgSupersetOf());
    addName(supersetOf(), "supersetOf");
  }

  /**
   * Verifies that a {@code Collection} argument is a subset of another {@code Collection}.
   *
   * @param <E> The type of the elements in the {@code Collection}
   * @param <C0> The type of the argument (the subject of the {@code Relation})
   * @param <C1> The type of the object of the {@code Relation}
   * @return A {@code Relation}
   */
  public static <E, C0 extends Collection<? super E>, C1 extends Collection<? super E>>
      Relation<C0, C1> subsetOf() {
    return (x, y) -> y.containsAll(x);
  }

  static {
    addMessage(subsetOf(), msgSubsetOf());
    addName(subsetOf(), "subsetOf");
  }

  /**
   * Verifies that a {@code Map} contains a key. Equivalent to {@link Map#containsKey(Object)
   * Map::containsKey}.
   *
   * @param <K> The type of the keys within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <K, M extends Map<? super K, ?>> Relation<M, K> containingKey() {
    return Map::containsKey;
  }

  static {
    addMessage(containingKey(), msgContainingKey());
    addName(containingKey(), "containingKey");
  }

  /**
   * Verifies that a {@code Map} does not contain a key.
   *
   * @param <K> The type of the keys within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <K, M extends Map<? super K, ?>> Relation<M, K> notContainingKey() {
    return (x, y) -> !x.containsKey(y);
  }

  static {
    addMessage(notContainingKey(), msgNotContainingKey());
    addName(notContainingKey(), "notContainingKey");
  }

  /**
   * Verifies the presence of a key within a {@code Map}.
   *
   * @param <K> The type of the keys within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <K, M extends Map<? super K, ?>> Relation<K, M> keyIn() {
    return (x, y) -> y.containsKey(x);
  }

  static {
    addMessage(keyIn(), msgKeyIn());
    addName(keyIn(), "keyIn");
  }

  /**
   * Verifies the absence of a key withinin a {@code Map}.
   *
   * @param <K> The type of the keys within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <K, M extends Map<? super K, ?>> Relation<K, M> notKeyIn() {
    return (x, y) -> !y.containsKey(x);
  }

  static {
    addMessage(notKeyIn(), msgNotKeyIn());
    addName(notKeyIn(), "notKeyIn");
  }

  /**
   * Verifies that a {@code Map} contains a value. Equivalent to {@link Map#containsValue(Object)
   * Map::containsValue}.
   *
   * @param <V> The type of the values within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <V, M extends Map<?, ? super V>> Relation<M, V> containingValue() {
    return Map::containsValue;
  }

  static {
    addMessage(containingValue(), msgContainingValue());
    addName(containingValue(), "containingValue");
  }

  /**
   * Verifies that a {@code Map} does not contain a value.
   *
   * @param <V> The type of the values within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <V, M extends Map<?, ? super V>> Relation<M, V> notContainingValue() {
    return (x, y) -> !x.containsValue(y);
  }

  static {
    addMessage(notContainingValue(), msgNotContainingValue());
    addName(notContainingValue(), "notContainingValue");
  }

  /**
   * Verifies the presence of a value within a {@code Map}.
   *
   * @param <K> The type of the keys within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <K, M extends Map<? super K, ?>> Relation<K, M> valueIn() {
    return (x, y) -> y.containsValue(x);
  }

  static {
    addMessage(valueIn(), msgValueIn());
    addName(valueIn(), "valueIn");
  }

  /**
   * Verifies the absence of a value withinin a {@code Map}.
   *
   * @param <K> The type of the keys within the map
   * @param <M> The Type of the {@code Map}
   * @return A {@code Relation}
   */
  public static <K, M extends Map<? super K, ?>> Relation<K, M> notValueIn() {
    return (x, y) -> !y.containsValue(x);
  }

  static {
    addMessage(notValueIn(), msgNotValueIn());
    addName(notValueIn(), "notValueIn");
  }

  /**
   * Verifies the presence of an element within an array. Equivalent to {@link
   * ArrayMethods#inArray(Object, Object...) ArrayMethods::inArray}.
   *
   * @param <X> The type of the argument
   * @param <Y> The component type of the array
   * @return A {@code Relation}
   */
  public static <Y, X extends Y> Relation<X, Y[]> inArray() {
    return ArrayMethods::inArray;
  }

  static {
    addMessage(inArray(), msgIn()); // recycle message
    addName(inArray(), "inArray");
  }

  /**
   * Verifies the absence of an element within an array.
   *
   * @param <X> The type of the argument
   * @param <Y> The component type of the array
   * @return A {@code Relation}
   */
  public static <Y, X extends Y> Relation<X, Y[]> notInArray() {
    return (x, y) -> !ArrayMethods.inArray(x, y);
  }

  static {
    addMessage(notInArray(), msgNotIn());
    addName(notInArray(), "notInArray");
  }

  /**
   * Verifies that the argument is equal to a particular value. Equivalent to {@link
   * Objects#equals(Object) Objects::equals}.
   *
   * @param <X> The type of the argument
   * @return A {@code Relation}
   */
  public static <X> Relation<X, X> equalTo() {
    return Objects::equals;
  }

  static {
    addMessage(equalTo(), msgEqualTo());
    addName(equalTo(), "equalTo");
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
    addMessage(notEqualTo(), msgNotEqualTo());
    addName(notEqualTo(), "notEqualTo");
  }

  public static Relation<String, List<String>> equalsIgnoreCase() {
    return (x, y) ->
        y.stream().filter(notNull()).filter(s -> s.equalsIgnoreCase(x)).findAny().isPresent();
  }

  static {
    addMessage(equalsIgnoreCase(), msgEqualsIgnoreCase());
    addName(equalsIgnoreCase(), "equalsIgnoreCase");
  }

  /**
   * Verifies that the argument references the same object as some other reference.
   *
   * @param <X> The type of the argument (the subject of the {@code Relation})
   * @param <Y> The type of object of the {@code Relation}
   * @return A {@code Relation}
   */
  public static <X, Y> Relation<X, Y> sameAs() {
    return (x, y) -> x == y;
  }

  static {
    addMessage(sameAs(), msgSameAs());
    addName(sameAs(), "sameAs");
  }

  /**
   * Verifies that the argument does not reference the same object than some other reference.
   *
   * @param <X> The type of the argument (the subject of the {@code Relation})
   * @param <Y> The type of object of the {@code Relation}
   * @return A {@code Relation}
   */
  public static <X, Y> Relation<X, Y> notSameAs() {
    return (x, y) -> x != y;
  }

  static {
    addMessage(notSameAs(), msgNotSameAs());
    addName(notSameAs(), "notSameAs");
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
    addMessage(nullOr(), msgNullOr());
    addName(nullOr(), "nullOr");
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
    addMessage(greaterThan(), msgGt()); // recycle message
    addName(greaterThan(), "greaterThan");
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
    addMessage(atLeast(), msgGte()); // recycle message
    addName(atLeast(), "atLeast");
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
    addMessage(lessThan(), msgLt()); // recycle message
    addName(lessThan(), "lessThan");
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
    addMessage(atMost(), msgLte()); // recycle message
    addName(atMost(), "atMost");
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
    addMessage(endsWith(), msgEndsWith());
    addName(endsWith(), "endsWith");
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
    addMessage(notEndsWith(), msgNotEndsWith());
    addName(notEndsWith(), "notEndsWith");
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
    addMessage(hasSubstr(), msgHasSubstr());
    addName(hasSubstr(), "hasSubstr");
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
    addMessage(notHasSubstr(), msgNotHasSubstr());
    addName(notHasSubstr(), "notHasSubstr");
  }

  /* ++++++++++++++ ObjIntRelation ++++++++++++++ */

  /**
   * Verifies that the argument's length or size is equal to a particular value. The type of
   * argument must be one of:
   *
   * <p>
   *
   * <ul>
   *   <li>an array
   *   <li>a {@link CharSequence}
   *   <li>a {@link Collection}
   *   <li>a {@link Map}
   *   <li>a {@link Sizeable}
   * </ul>
   *
   * <p>For any other type of argument this method throws an {@link InvalidCheckException}.
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
      throw notApplicable(sizeEquals(), x);
    };
  }

  static {
    addMessage(sizeEquals(), msgSizeEquals());
    addName(sizeEquals(), "sizeEquals");
  }

  /**
   * Verifies that the argument's length or size is not equal to a particular value. The type of
   * argument must be one of:
   *
   * <p>
   *
   * <ul>
   *   <li>an array
   *   <li>a {@link CharSequence}
   *   <li>a {@link Collection}
   *   <li>a {@link Map}
   *   <li>a {@link Sizeable}
   * </ul>
   *
   * <p>For any other type of argument this method throws an {@link InvalidCheckException}.
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
      throw notApplicable(sizeNotEquals(), x);
    };
  }

  static {
    addMessage(sizeNotEquals(), msgSizeNotEquals());
    addName(sizeNotEquals(), "sizeNotEquals");
  }

  /**
   * Verifies that the argument's length or size is greater than a particular value. The type of
   * argument must be one of:
   *
   * <p>
   *
   * <ul>
   *   <li>an array
   *   <li>a {@link CharSequence}
   *   <li>a {@link Collection}
   *   <li>a {@link Map}
   *   <li>a {@link Sizeable}
   * </ul>
   *
   * <p>For any other type of argument this method throws an {@link InvalidCheckException}.
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
      throw notApplicable(sizeGreaterThan(), x);
    };
  }

  static {
    addMessage(sizeGreaterThan(), msgSizeGreaterThan());
    addName(sizeGreaterThan(), "sizeGreaterThan");
  }

  /**
   * Verifies that the argument's length or size is greater than or equal to a particular value. The
   * type of argument must be one of:
   *
   * <p>
   *
   * <ul>
   *   <li>an array
   *   <li>a {@link CharSequence}
   *   <li>a {@link Collection}
   *   <li>a {@link Map}
   *   <li>a {@link Sizeable}
   * </ul>
   *
   * <p>For any other type of argument this method throws an {@link InvalidCheckException}.
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
      throw notApplicable(sizeAtLeast(), x);
    };
  }

  static {
    addMessage(sizeAtLeast(), msgSizeAtLeast());
    addName(sizeAtLeast(), "sizeAtLeast");
  }

  /**
   * Verifies that the argument's length or size is less than a particular value. The type of
   * argument must be one of:
   *
   * <p>
   *
   * <ul>
   *   <li>an array
   *   <li>a {@link CharSequence}
   *   <li>a {@link Collection}
   *   <li>a {@link Map}
   *   <li>a {@link Sizeable}
   * </ul>
   *
   * <p>For any other type of argument this method throws an {@link InvalidCheckException}.
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
      throw notApplicable(sizeLessThan(), x);
    };
  }

  static {
    addMessage(sizeLessThan(), msgSizeLessThan());
    addName(sizeLessThan(), "sizeLessThan");
  }

  /**
   * Verifies that the argument's length or size is less than a particular value. The type of
   * argument must be one of:
   *
   * <p>
   *
   * <ul>
   *   <li>an array
   *   <li>a {@link CharSequence}
   *   <li>a {@link Collection}
   *   <li>a {@link Map}
   *   <li>a {@link Sizeable}
   * </ul>
   *
   * <p>For any other type of argument this method throws an {@link InvalidCheckException}.
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
      throw notApplicable(sizeGreaterThan(), x);
    };
  }

  static {
    addMessage(sizeAtMost(), msgSizeAtMost());
    addName(sizeAtMost(), "sizeAtMost");
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
    addMessage(indexOf(), msgIndexOf());
    addName(indexOf(), "indexOf");
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
    addMessage(toIndexOf(), msgToIndexOf());
    addName(toIndexOf(), "toIndexOf");
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
    addMessage(eq(), msgEq());
    addName(eq(), "eq");
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
    addMessage(ne(), msgNe());
    addName(ne(), "ne");
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
    addMessage(gt(), msgGt());
    addName(gt(), "gt");
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
    addMessage(gte(), msgGte());
    addName(gte(), "gte");
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
    addMessage(lt(), msgLt());
    addName(lt(), "lt");
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
    addMessage(lte(), msgLte());
    addName(lte(), "lte");
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
    addMessage(multipleOf(), msgMultipleOf());
    addName(multipleOf(), "multipleOf");
  }

  /* ++++++++++++++ Miscellaneous ++++++++++++++ */

  /**
   * Converts the specified {@code Predicate} into an {@code IntPredicate}. Can be used to force the
   * compiler to interpret a lambda as an {@code IntPredicate} rather than a {@code Predicate}.
   *
   * @param predicate A {@code Predicate}, supposedly in the form of a lambda
   * @return The {@code IntPredicate} version of the {@code Predicate}
   */
  public static IntPredicate asInt(Predicate<Integer> predicate) {
    return FunctionalMethods.asInt(predicate);
  }

  /**
   * Equivalent to {@link IllegalStateException#IllegalStateException(String)
   * IllegalStateException::new} but more concise when statically imported.
   *
   * @return A {@code Function} produces an {@code IllegalStateException}
   */
  public static Function<String, IllegalStateException> illegalState() {
    return IllegalStateException::new;
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
    return FunctionalMethods.asObj(predicate);
  }

  /* ++++++++++++++ END OF CHECKS ++++++++++++++ */

  static {
    messages = new IdentityHashMap<>(tmp0.size());
    names = new IdentityHashMap<>(tmp1.size());
    tmp0.forEach(tuple -> tuple.insertInto(messages));
    tmp1.forEach(tuple -> tuple.insertInto(names));
    tmp0 = null;
    tmp1 = null;
  }

  private static final String suffix = "()";

  static String nameOf(Predicate<?> test) {
    return ifNotNull(names.get(test), name -> name + suffix, prettySimpleClassName(test));
  }

  static String nameOf(IntPredicate test) {
    return ifNotNull(names.get(test), name -> name + suffix, prettySimpleClassName(test));
  }

  static String nameOf(Relation<?, ?> test) {
    return ifNotNull(names.get(test), name -> name + suffix, prettySimpleClassName(test));
  }

  static String nameOf(IntRelation test) {
    return ifNotNull(names.get(test), name -> name + suffix, prettySimpleClassName(test));
  }

  static String nameOf(ObjIntRelation<?> test) {
    return ifNotNull(names.get(test), name -> name + suffix, prettySimpleClassName(test));
  }

  static String nameOf(IntObjRelation<?> test) {
    return ifNotNull(names.get(test), name -> name + suffix, prettySimpleClassName(test));
  }

  private static void addMessage(Object test, Formatter message) {
    tmp0.add(Tuple.of(test, message));
  }

  private static void addName(Object test, String name) {
    tmp1.add(Tuple.of(test, name));
  }
}
