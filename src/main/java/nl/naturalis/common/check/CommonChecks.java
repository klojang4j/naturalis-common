package nl.naturalis.common.check;

import nl.naturalis.common.*;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import static nl.naturalis.common.ArrayMethods.isOneOf;
import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.check.Messages.*;

/**
 * Defines various common tests for arguments. These tests have short, informative error messages
 * associated with them so you don't have to invent them yourself. Many of them are plain,
 * unadorned, method references and only check what they advertise to be checking. Unless documented
 * otherwise, they <i>will not</i> do a preliminary null check. The will throw a raw, unprocessed
 * {@link NullPointerException} if the argument is null. They rely upon being embedded within in
 * chain of checks, the first of which should be a null check (most likely {@link #notNull()}).
 *
 * @author Ayco Holleman
 */
public class CommonChecks {

  static final IdentityHashMap<Object, Formatter> MESSAGE_PATTERNS;
  static final IdentityHashMap<Object, String> NAMES;

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
  public static <T> Predicate<T> NULL() {
    return Objects::isNull;
  }

  static {
    setMessagePattern(NULL(), msgNull());
    setName(NULL(), "NULL");
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
    setMessagePattern(notNull(), msgNotNull());
    setName(notNull(), "notNull");
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
    setMessagePattern(yes(), msgYes());
    setName(yes(), "yes");
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
    setMessagePattern(no(), msgNo());
    setName(no(), "no");
  }

  /**
   * Verifies that the argument is empty. See {@link ObjectMethods#isEmpty(Object)
   * ObjectMethods::isEmpty} for what counts as an empty object.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> empty() {
    return ObjectMethods::isEmpty;
  }

  static {
    setMessagePattern(empty(), msgEmpty());
    setName(empty(), "empty");
  }

  /**
   * Verifies that the argument is not null and, if it is an array or {@code Collection}, does not
   * contain any null values. The array or {@code Collection} may still be zero-sized though. Useful
   * for validating varargs arguments. Use {@link #deepNotEmpty()} if you want the array or {@code
   * Collection} to contain at least one element.
   *
   * @see ObjectMethods#isDeepNotNull(Object)
   * @param <T> The type of the argument
   * @return A {@code Predicate} implementing the check described above
   */
  public static <T> Predicate<T> deepNotNull() {
    return ObjectMethods::isDeepNotNull;
  }

  static {
    setMessagePattern(deepNotNull(), msgDeepNotNull());
    setName(deepNotNull(), "deepNotNull");
  }

  /**
   * Verifies that the argument is recursively non-empty as per {@link
   * ObjectMethods#isDeepNotEmpty(Object) ObjectMethods.isDeepNotEmpty}.
   *
   * @see ObjectMethods#isDeepNotEmpty(Object)
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> deepNotEmpty() {
    return ObjectMethods::isDeepNotEmpty;
  }

  static {
    setMessagePattern(deepNotEmpty(), msgDeepNotEmpty());
    setName(deepNotEmpty(), "deepNotEmpty");
  }

  /**
   * Verifies that a {@code String} argument is null or contains whitespace only. Mainly meant to be
   * called from the {code isNot} methods of the {@code Check} class, in which case it performs an
   * implicit null check.
   *
   * @return A {@code Predicate}
   */
  public static Predicate<String> blank() {
    return s -> s == null || s.isBlank();
  }

  static {
    setMessagePattern(blank(), msgBlank());
    setName(blank(), "blank");
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
    setMessagePattern(integer(), msgInteger());
    setName(integer(), "integer");
  }

  /**
   * If the argument is a {@code Class} object, verify that it either extends {@link Number} or is
   * one of the primitive number types. Otherwise verify that {@code argument.getClass()} either
   * extends {@link Number} or is one of the primitive number types.
   *
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> number() {
    return x -> {
      Class<?> c = x.getClass() == Class.class ? (Class<?>) x : x.getClass();
      return x instanceof Number
          || isOneOf(c, int.class, long.class, byte.class, double.class, float.class, short.class);
    };
  }

  static {
    setMessagePattern(number(), msgNumber());
    setName(number(), "number");
  }

  /**
   * Verifies that a {@code String} argument is a valid port number. More precisely: that it can be
   * included as the port segment of a URL. That is: the string must not be blank, it must not start
   * with '+' or '-' and it must be a positive {@code short} value (max 65535). This test performs a
   * preliminary null check.
   *
   * @return A {@code Predicate}
   */
  public static Predicate<String> validPort() {
    return str -> {
      if (StringMethods.isBlank(str)) {
        return false;
      }
      char c = str.charAt(0);
      if (c == '+' || c == '-') {
        return false;
      }
      return NumberMethods.isPlainShort(str);
    };
  }

  static {
    setMessagePattern(validPort(), msgValidPort());
    setName(validPort(), "validPort");
  }

  /**
   * Verifies that the argument is an existing, regular file. Equivalent to {@link File#isFile()
   * File::isFile}.
   *
   * @return A {@code Predicate}
   */
  public static Predicate<File> file() {
    return File::isFile;
  }

  static {
    setMessagePattern(file(), msgFile());
    setName(file(), "file");
  }

  /**
   * Verifies that the argument is an existing directory. Equivalent to {@link File#isDirectory()
   * File::isDirectory}.
   *
   * @return A {@code Predicate}
   */
  public static Predicate<File> directory() {
    return File::isDirectory;
  }

  static {
    setMessagePattern(directory(), msgDirectory());
    setName(directory(), "directory");
  }

  /**
   * Verifies that the argument is an existing file of any type.
   *
   * @return A {@code Predicate}
   */
  public static Predicate<File> present() {
    return f -> !f.exists();
  }

  static {
    setMessagePattern(present(), msgPresent());
    setName(present(), "present");
  }

  /**
   * Verifies that the argument is a readable file (implies that the file exists). Equivalent to
   * {@link File#canRead() File::canRead}.
   *
   * @return A {@code Predicate}
   */
  public static Predicate<File> readable() {
    return File::canRead;
  }

  static {
    setMessagePattern(readable(), msgReadable());
    setName(readable(), "readable");
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
    setMessagePattern(writable(), msgWritable());
    setName(writable(), "writable");
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
    setMessagePattern(even(), msgEven());
    setName(even(), "even");
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
    setMessagePattern(odd(), msgOdd());
    setName(odd(), "odd");
  }

  /**
   * Verifies that the argument is greater than zero.
   *
   * @return An {@code IntPredicate}
   */
  public static IntPredicate positive() {
    return x -> x > 0;
  }

  static {
    setMessagePattern(positive(), msgPositive());
    setName(positive(), "positive");
  }

  /**
   * Verifies that the argument is greater than zero.
   *
   * @return An {@code IntPredicate}
   */
  public static IntPredicate negative() {
    return x -> x < 0;
  }

  static {
    setMessagePattern(negative(), msgNegative());
    setName(negative(), "negative");
  }

  /* ++++++++++++++ Relation ++++++++++++++ */

  /**
   * If the argument is a {@link Class} object, this method verifies that it is a subclass or
   * implementation of the specified class; otherwise that the argument is an instance of it.
   *
   * @param <X> The type of the argument
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
    setMessagePattern(instanceOf(), msgInstanceOf());
    setName(instanceOf(), "instanceOf");
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
    setMessagePattern(array(), msgArray());
    setName(array(), "array");
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

  public static <E, C extends Collection<? super E>> Relation<C, E> containing(E element) {
    return Collection::contains;
  }

  static {
    setMessagePattern(containing(), msgContaining());
    setName(containing(), "containing");
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
    setMessagePattern(in(), msgIn());
    setName(in(), "in");
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
    setMessagePattern(supersetOf(), msgSupersetOf());
    setName(supersetOf(), "supersetOf");
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
    setMessagePattern(subsetOf(), msgSubsetOf());
    setName(subsetOf(), "subsetOf");
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
    setMessagePattern(containingKey(), msgContainingKey());
    setName(containingKey(), "containingKey");
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
    setMessagePattern(keyIn(), msgKeyIn());
    setName(keyIn(), "keyIn");
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
    setMessagePattern(containingValue(), msgContainingValue());
    setName(containingValue(), "containingValue");
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
    setMessagePattern(valueIn(), msgValueIn());
    setName(valueIn(), "valueIn");
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
    setMessagePattern(inArray(), msgIn()); // recycle message
    setName(inArray(), "inArray");
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
    setMessagePattern(equalTo(), msgEqualTo());
    setName(equalTo(), "equalTo");
  }

  /**
   * Verifies that a {@code String} is present, ignoring case, in a {@code List} of strings.
   *
   * @return
   */
  public static Relation<String, List<String>> equalsIgnoreCase() {
    return (x, y) -> y.stream().anyMatch(s -> s.equalsIgnoreCase(x));
  }

  static {
    setMessagePattern(equalsIgnoreCase(), msgEqualsIgnoreCase());
    setName(equalsIgnoreCase(), "equalsIgnoreCase");
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
    setMessagePattern(sameAs(), msgSameAs());
    setName(sameAs(), "sameAs");
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
    setMessagePattern(nullOr(), msgNullOr());
    setName(nullOr(), "nullOr");
  }

  /**
   * Verifies that a {@code Number} is greater than another {@code Number}, widening both to {@code
   * Double} before comparing them. Use when testing any type of numbers besides {@code int} or
   * {@code Integer}.
   *
   * @see #gt()
   * @param <X> The type of the argument
   * @param <Y> The type of the value to compare the argument to
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> greaterThan() {
    return (x, y) -> x.doubleValue() > y.doubleValue();
  }

  static {
    setMessagePattern(greaterThan(), msgGreaterThan()); // recycle message
    setName(greaterThan(), "greaterThan");
  }

  /**
   * Verifies that a {@code Number} is greater than or equal to another {@code Number}, widening
   * both to {@code Double} before comparing them. Use when testing any type of numbers besides
   * {@code int} or {@code Integer}.
   *
   * @see #gte()
   * @param <X> The type of the argument
   * @param <Y> The type of the value to compare the argument to
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> atLeast() {
    return (x, y) -> x.doubleValue() >= y.doubleValue();
  }

  static {
    setMessagePattern(atLeast(), msgAtLeast()); // recycle message
    setName(atLeast(), "atLeast");
  }

  /**
   * Verifies that a {@code Number} is less than another {@code Number}, widening both to {@code
   * Double} before comparing them. Use when testing any type of numbers besides {@code int} or
   * {@code Integer}.
   *
   * @see #lt()
   * @param <X> The type of the argument
   * @param <Y> The type of the value to compare the argument to
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> lessThan() {
    return (x, y) -> x.doubleValue() < y.doubleValue();
  }

  static {
    setMessagePattern(lessThan(), msgLessThan()); // recycle message
    setName(lessThan(), "lessThan");
  }

  /**
   * Verifies that a {@code Number} is less than or equal to another {@code Number}, widening both
   * to {@code Double} before comparing them. Use when testing any type of numbers besides {@code
   * int} or {@code Integer}.
   *
   * @see #lte()
   * @param <X> The type of the argument
   * @param <Y> The type of the value to compare the argument to
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> atMost() {
    return (x, y) -> x.doubleValue() <= y.doubleValue();
  }

  static {
    setMessagePattern(atMost(), msgAtMost()); // recycle message
    setName(atMost(), "atMost");
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
    setMessagePattern(endsWith(), msgEndsWith());
    setName(endsWith(), "endsWith");
  }

  /**
   * Verifies that a {@code String} contains a {@code CharSequence}. Equivalent to {@link
   * String#contains(CharSequence) String::contains}.
   *
   * @return A {@code Relation}
   */
  public static <T extends CharSequence> Relation<String, T> hasSubstr() {
    return String::contains;
  }

  static {
    setMessagePattern(hasSubstr(), msgHasSubstr());
    setName(hasSubstr(), "hasSubstr");
  }

  /* ++++++++++++++ ObjIntRelation ++++++++++++++ */

  /**
   * Verifies that that {@code Collection} has a size equal to some integer value. Mainly meant to
   * be called from the {@code has} methods of the {@code Check} class. In other words, when testing
   * a {@code Collection}-type property of the argument rather than the argument itself. If the
   * argument is itself a {@code Collection}, you can test this more concicely using {@code
   * Check.that(myCollection).has(size(), eq(), 42)}.
   *
   * @see CommonGetters#size()
   * @see #eq()
   * @param <E> The type of the elements of the collection
   * @param <C> The type of the collection
   * @return An {@code ObjIntRelation} that expresses the described test
   */
  public static <E, C extends Collection<? super E>> ObjIntRelation<C> sizeEquals() {
    return (x, y) -> x.size() == y;
  }

  static {
    setMessagePattern(sizeEquals(), msgSizeEquals());
    setName(sizeEquals(), "sizeEquals");
  }

  /**
   * Verifies that that {@code Collection} has a size greater than some integer value. Mainly meant
   * to be called from the {@code has} methods of the {@code Check} class. In other words, when
   * testing a {@code Collection}-type property of the argument rather than the argument itself. If
   * the argument is itself a {@code Collection}, you can test this more concicely using {@code
   * Check.that(myCollection).has(size(), gt(), 42)}.
   *
   * @see CommonGetters#size()
   * @see #gt()
   * @param <E> The type of the elements of the collection
   * @param <C> The type of the collection
   * @return An {@code ObjIntRelation} that expresses the described test
   */
  public static <E, C extends Collection<? super E>> ObjIntRelation<C> sizeGT() {
    return (x, y) -> x.size() > y;
  }

  static {
    setMessagePattern(sizeGT(), msgSizeGT());
    setName(sizeGT(), "sizeGT");
  }

  /**
   * Verifies that that {@code Collection} has a size equal to, or greater than some integer value.
   * Mainly meant to be called from the {@code has} methods of the {@code Check} class. In other
   * words, when testing a {@code Collection}-type property of the argument rather than the argument
   * itself. If the argument is itself a {@code Collection}, you can test this more concicely using
   * {@code Check.that(myCollection).has(size(), gte(), 42)}.
   *
   * @see CommonGetters#size()
   * @see #gte()
   * @param <E> The type of the elements of the collection
   * @param <C> The type of the collection
   * @return An {@code ObjIntRelation} that expresses the described test
   */
  public static <E, C extends Collection<? super E>> ObjIntRelation<C> sizeGTE() {
    return (x, y) -> x.size() >= y;
  }

  static {
    setMessagePattern(sizeGTE(), msgSizeGTE());
    setName(sizeGTE(), "sizeGTE");
  }

  /**
   * Verifies that that {@code Collection} has a size less than some integer value. Mainly meant to
   * be called from the {@code has} methods of the {@code Check} class. In other words, when testing
   * a {@code Collection}-type property of the argument rather than the argument itself. If the
   * argument is itself a {@code Collection}, you can test this more concicely using {@code
   * Check.that(myCollection).has(size(), lt(), 42)}.
   *
   * @see CommonGetters#size()
   * @see #lt()
   * @param <E> The type of the elements of the collection
   * @param <C> The type of the collection
   * @return An {@code ObjIntRelation} that expresses the described test
   */
  public static <E, C extends Collection<? super E>> ObjIntRelation<C> sizeLT() {
    return (x, y) -> x.size() < y;
  }

  static {
    setMessagePattern(sizeLT(), msgSizeLT());
    setName(sizeLT(), "sizeLT");
  }

  /**
   * Verifies that that {@code Collection} has a size less than, or equal to some integer value.
   * Mainly meant to be called from the {@code has} methods of the {@code Check} class. In other
   * words, when testing a {@code Collection}-type property of the argument rather than the argument
   * itself. If the argument is itself a {@code Collection}, you can test this more concicely using
   * {@code Check.that(myCollection).has(size(), eq(), 42)}.
   *
   * @see CommonGetters#size()
   * @see #lte()
   * @param <E> The type of the elements of the collection
   * @param <C> The type of the collection
   * @return An {@code ObjIntRelation} that expresses the described test
   */
  public static <E, C extends Collection<? super E>> ObjIntRelation<C> sizeLTE() {
    return (x, y) -> x.size() <= y;
  }

  static {
    setMessagePattern(sizeLTE(), msgSizeLTE());
    setName(sizeLTE(), "sizeLTE");
  }

  /* ++++++++++++++ IntObjRelation ++++++++++++++ */

  /**
   * Verifies that a number lies between two other numbers, the first one included, the second one
   * excluded. If the type of the numbers in the {@code Pair} is different from the type of the
   * number to be tested, all numbers are first converted to double instances, unless one of the
   * types is {@link BigDecimal}, in which case the comparison is scaled up to a {@code BigDecimal}
   * comparison.
   *
   * @return A {@code Relation} that implements the test described above
   */
  public static <T extends Number, U extends Number> Relation<T, Pair<U>> between() {
    return (x, y) -> {
      if (x.getClass() != y.one().getClass()) {
        if (x.getClass() == BigDecimal.class || y.one().getClass() == BigDecimal.class) {
          BigDecimal bd0 = NumberMethods.convert(x, BigDecimal.class);
          BigDecimal bd1 = NumberMethods.convert(y.one(), BigDecimal.class);
          BigDecimal bd2 = NumberMethods.convert(y.two(), BigDecimal.class);
          return bd0.compareTo(bd1) >= 0 && bd0.compareTo(bd2) < 0;
        }
        double d0 = x.doubleValue();
        double d1 = y.one().doubleValue();
        double d2 = y.two().doubleValue();
        return d0 >= d1 && d0 < d2;
      }
      if (x.getClass() == Integer.class) {
        int n = (Integer) x;
        return n >= (Integer) y.one() && n < (Integer) y.two();
      } else if (x.getClass() == Double.class) {
        double n = (Double) x;
        return n >= (Double) y.one() && n < (Double) y.two();
      } else if (x.getClass() == Long.class) {
        long n = (Long) x;
        return n >= (Long) y.one() && n < (Long) y.two();
      } else if (x.getClass() == Byte.class) {
        byte n = (Byte) x;
        return n >= (Byte) y.one() && n < (Float) y.two();
      } else if (x.getClass() == Float.class) {
        float n = (Float) x;
        return n >= (Float) y.one() && n < (Float) y.two();
      } else if (x.getClass() == Short.class) {
        short n = (Short) x;
        return n >= (Short) y.one() && n < (Short) y.two();
      } else if (x.getClass() == BigDecimal.class) {
        BigDecimal n = (BigDecimal) x;
        return n.compareTo((BigDecimal) y.one()) >= 0 && n.compareTo((BigDecimal) y.two()) < 0;
      }
      return Check.fail("Ouch, a new type of number: %s", x.getClass());
    };
  }

  static {
    setMessagePattern(between(), msgBetween());
    setName(between(), "between");
  }

  /**
   * Verifies that a number lies between two other numbers, both included.
   *
   * @return A {@code Relation} that implements the test described above
   */
  public static <T extends Number> Relation<T, Pair<T>> inRangeClosed() {
    return (x, y) -> {
      if (x.getClass() != y.one().getClass()) {
        if (x.getClass() == BigDecimal.class || y.one().getClass() == BigDecimal.class) {
          BigDecimal bd0 = NumberMethods.convert(x, BigDecimal.class);
          BigDecimal bd1 = NumberMethods.convert(y.one(), BigDecimal.class);
          BigDecimal bd2 = NumberMethods.convert(y.two(), BigDecimal.class);
          return bd0.compareTo(bd1) >= 0 && bd0.compareTo(bd2) <= 0;
        }
        double d0 = x.doubleValue();
        double d1 = y.one().doubleValue();
        double d2 = y.two().doubleValue();
        return d0 >= d1 && d0 <= d2;
      }
      if (x.getClass() == Integer.class) {
        int n = (Integer) x;
        return n >= (Integer) y.one() && n <= (Integer) y.two();
      } else if (x.getClass() == Double.class) {
        double n = (Double) x;
        return n >= (Double) y.one() && n <= (Double) y.two();
      } else if (x.getClass() == Long.class) {
        long n = (Long) x;
        return n >= (Long) y.one() && n <= (Long) y.two();
      } else if (x.getClass() == Byte.class) {
        byte n = (Byte) x;
        return n >= (Byte) y.one() && n <= (Float) y.two();
      } else if (x.getClass() == Float.class) {
        float n = (Float) x;
        return n >= (Float) y.one() && n <= (Float) y.two();
      } else if (x.getClass() == Short.class) {
        short n = (Short) x;
        return n >= (Short) y.one() && n <= (Short) y.two();
      } else if (x.getClass() == BigDecimal.class) {
        BigDecimal n = (BigDecimal) x;
        return n.compareTo((BigDecimal) y.one()) >= 0 && n.compareTo((BigDecimal) y.two()) <= 0;
      }
      return Check.fail("Ouch, a new type of number: %s", x.getClass());
    };
  }

  static {
    setMessagePattern(inRangeClosed(), msgInRangeClosed());
    setName(inRangeClosed(), "inRangeClosed");
  }

  /**
   * Verifies that the argument is greater than or equal to zero and less than the size of the
   * specified {@code List} or array. The object of the relationship is deliberately weakly typed to
   * allow it to be either a {@code List} or an array. Specifying any other type of object will
   * cause an {@link InvalidCheckException} to be thrown though.
   *
   * @return An {@code IntObjRelation} expressing this requirement
   */
  @SuppressWarnings("rawtypes")
  public static <T> IntObjRelation<T> indexOf() {
    return (x, y) -> {
      if (x < 0) {
        return false;
      } else if (y instanceof List) {
        return x < ((List) y).size();
      } else if (y.getClass().isArray()) {
        return x < Array.getLength(y);
      }
      throw new InvalidCheckException("Object of \"indexOf\" relation must be List or array");
    };
  }

  static {
    setMessagePattern(indexOf(), msgIndexOf());
    setName(indexOf(), "indexOf");
  }

  /**
   * Verifies that the argument is a valid "from" index for a {@code List} operation like {@link
   * List#subList(int, int) List.sublist}. In the case the index may actually be one position past
   * the end of the {@code List}.
   *
   * @return An {@code IntObjRelation}
   */
  public static <E, L extends List<? super E>> IntObjRelation<L> validFromIndex() {
    return (x, y) -> x >= 0 && x <= y.size();
  }

  static {
    setMessagePattern(validFromIndex(), msgValidFromIndex());
    setName(validFromIndex(), "msgValidFromIndex");
  }

  /**
   * Verifies that the argument is a valid "to" index for a {@code List} operation like {@link
   * List#subList(int, int) List.sublist}. This is, in fact, the same test as {@link
   * #validFromIndex()}, but it codes more intuitively when testing "to" indices.
   *
   * @return An {@code IntObjRelation}
   */
  public static <E, L extends List<? super E>> IntObjRelation<L> validToIndex() {
    return validFromIndex();
  }

  static {
    setMessagePattern(validToIndex(), msgValidFromIndex()); // recycle message
    setName(validToIndex(), "validToIndex");
  }

  /* ++++++++++++++ IntPredicate ++++++++++++++ */

  /**
   * Verifies that the argument is 0 (zero).
   *
   * @return An {@code IntPredicate} establishing that the argument is equal to zero
   */
  public static IntPredicate zero() {
    return (x) -> x == 0;
  }

  static {
    setMessagePattern(eq(), msgZero());
    setName(zero(), "zero");
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
    setMessagePattern(eq(), msgEq());
    setName(eq(), "eq");
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
    setMessagePattern(ne(), msgNe());
    setName(ne(), "ne");
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
    setMessagePattern(gt(), msgGreaterThan());
    setName(gt(), "gt");
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
    setMessagePattern(gte(), msgAtLeast());
    setName(gte(), "gte");
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
    setMessagePattern(lt(), msgLessThan());
    setName(lt(), "lt");
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
    setMessagePattern(lte(), msgAtMost());
    setName(lte(), "lte");
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
    setMessagePattern(multipleOf(), msgMultipleOf());
    setName(multipleOf(), "multipleOf");
  }

  /* ++++++++++++++ Miscellaneous ++++++++++++++ */

  /**
   * (Not a check) Converts the specified {@code Predicate} into an {@code IntPredicate}. Can be
   * used to force the compiler to interpret a lambda as an {@code IntPredicate} rather than a
   * {@code Predicate}.
   *
   * @param predicate A {@code Predicate}, supposedly in the form of a lambda
   * @return The {@code IntPredicate} version of the {@code Predicate}
   */
  public static IntPredicate asInt(Predicate<Integer> predicate) {
    return FunctionalMethods.asInt(predicate);
  }

  /**
   * (Not a check) Simply returns the argument. Can be used to force the compiler to interpret a
   * lambda as a {@code Predicate} rather than an {@code IntPredicate}.
   *
   * @param <T> The type of the argument being tested
   * @param predicate A {@code Predicate}, supposedly in the form of a lambda
   * @return The argument
   */
  public static <T> Predicate<T> asObj(Predicate<T> predicate) {
    return FunctionalMethods.asObj(predicate);
  }

  /**
   * (Not a check) Shortcut for {@link IllegalStateException#IllegalStateException(String)
   * IllegalStateException::new}. Can be used in combination with {@link Check#on(Function, Object)
   * Check.on(...)}. For example: <code>Check.on(illegalState(), out.isClosed()).is(no())</code>.
   *
   * @return A {@code Function} that produces an {@code IllegalStateException}
   */
  public static Function<String, IllegalStateException> illegalState() {
    return IllegalStateException::new;
  }

  /**
   * (Not a check) Shortcut for {@link IndexOutOfBoundsException#IndexOutOfBoundsException(String)
   * IndexOutOfBoundsException::new}.
   *
   * @return A {@code Function} that produces an {@code IndexOutOfBoundsException}
   */
  public static Function<String, IndexOutOfBoundsException> indexOutOfBounds() {
    return IndexOutOfBoundsException::new;
  }

  /**
   * (Not a check) Shortcut for {@link
   * UnsupportedOperationException#UnsupportedOperationException(String)
   * UnsupportedOperationException::new}.
   *
   * @return A {@code Function} that produces an {@code UnsupportedOperationException}
   */
  public static Function<String, UnsupportedOperationException> unsupportedOperation() {
    return UnsupportedOperationException::new;
  }

  /* ++++++++++++++ END OF CHECKS ++++++++++++++ */

  static {
    MESSAGE_PATTERNS = new IdentityHashMap<>(tmp0.size());
    NAMES = new IdentityHashMap<>(tmp1.size());
    tmp0.forEach(tuple -> tuple.insertInto(MESSAGE_PATTERNS));
    tmp1.forEach(tuple -> tuple.insertInto(NAMES));
    tmp0 = null;
    tmp1 = null;
  }

  private static final String suffix = "()";

  static String nameOf(Predicate<?> test) {
    return ifNotNull(NAMES.get(test), name -> name + suffix, Predicate.class.getSimpleName());
  }

  static String nameOf(IntPredicate test) {
    return ifNotNull(NAMES.get(test), name -> name + suffix, IntPredicate.class.getSimpleName());
  }

  static String nameOf(Relation<?, ?> test) {
    return ifNotNull(NAMES.get(test), name -> name + suffix, Relation.class.getSimpleName());
  }

  static String nameOf(IntRelation test) {
    return ifNotNull(NAMES.get(test), name -> name + suffix, IntRelation.class.getSimpleName());
  }

  static String nameOf(ObjIntRelation<?> test) {
    return ifNotNull(NAMES.get(test), name -> name + suffix, ObjIntRelation.class.getSimpleName());
  }

  static String nameOf(IntObjRelation<?> test) {
    return ifNotNull(NAMES.get(test), name -> name + suffix, IntObjRelation.class.getSimpleName());
  }

  private static void setMessagePattern(Object test, Formatter message) {
    tmp0.add(Tuple.of(test, message));
  }

  private static void setName(Object test, String name) {
    tmp1.add(Tuple.of(test, name));
  }
}
