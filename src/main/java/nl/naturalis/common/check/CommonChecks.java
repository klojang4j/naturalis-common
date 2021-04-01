package nl.naturalis.common.check;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import nl.naturalis.common.*;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;
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

  // Associates checks with String.format message patterns
  static final IdentityHashMap<Object, Formatter> MESSAGE_PATTERNS;
  // Stores the names of the checks
  static final IdentityHashMap<Object, String> CHECK_NAMES;

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
    setMessagePattern(nullPointer(), msgNullPointer());
    setName(nullPointer(), "nullPointer");
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
   * Verifies that the argument is not null and does not contain any null values. Equivalent to
   * {@link ObjectMethods#isNoneNull(Object) ObjectMethods::isNoneNull}. Especially useful for
   * validating varargs arguments.
   *
   * @param <T> The type of the argument
   * @return A {@code Predicate}
   */
  public static <T> Predicate<T> noneNull() {
    return ObjectMethods::isNoneNull;
  }

  static {
    setMessagePattern(noneNull(), msgNoneNull());
    setName(noneNull(), "noneNull");
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
   * Verifies that a {@code String} argument is a valid port number. More precisely: that it can be
   * included as the port segment of a URL. That is: the string must not be blank, it must not start
   * with '+' or '-' and it must be a positive {@code short} value (max 65535). This test performs a
   * preliminary null check.
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
    setMessagePattern(validPortNumber(), msgValidPortNumber());
    setName(validPortNumber(), "validPortNumber");
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
   * Verifies that the argument is positive.
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
   * Verifies that the argument is negative.
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
   * implementation of the specified class; otherwise that the argument is an intance of it.
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
   * Verifies that a {@code Number} is greater than another {@code Number} , widening both to {@code
   * Double} before comparing them. Use when testing any type of numbers besides {@code int} or
   * {@code Integer}.
   *
   * @see #gt()
   * @param <X> The type of the argument
   * @param <Y> The type of the value to compare the argument to
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> GT() {
    return (x, y) -> x.doubleValue() > y.doubleValue();
  }

  static {
    setMessagePattern(GT(), msgGt()); // recycle message
    setName(GT(), "GT");
  }

  /**
   * Verifies that the argument is greater than or equal to a particular value, widening both to
   * {@code double} before comparing them.
   *
   * @param <X> The type of the argument
   * @param <Y> The type of the value to compare the argument to
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> GTE() {
    return (x, y) -> x.doubleValue() >= y.doubleValue();
  }

  static {
    setMessagePattern(GTE(), msgGte()); // recycle message
    setName(GTE(), "GTE");
  }

  /**
   * Verifies that the argument is less than a particular value, widening both to {@code double}
   * before comparing them.
   *
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> LT() {
    return (x, y) -> x.doubleValue() < y.doubleValue();
  }

  static {
    setMessagePattern(LT(), msgLt()); // recycle message
    setName(LT(), "LT");
  }

  /**
   * Verifies that the argument is less than or equal to a particular value, widening both to {@code
   * double} before comparing them.
   *
   * @param <X> The type of the argument
   * @param <Y> The type of the value to compare the argument to
   * @return A {@code Relation}
   */
  public static <X extends Number, Y extends Number> Relation<X, Y> LTE() {
    return (x, y) -> x.doubleValue() <= y.doubleValue();
  }

  static {
    setMessagePattern(LTE(), msgLte()); // recycle message
    setName(LTE(), "LTE");
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
    setMessagePattern(indexOf(), msgIndexOf());
    setName(indexOf(), "indexOf");
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
    setMessagePattern(toIndexOf(), msgToIndexOf());
    setName(toIndexOf(), "toIndexOf");
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
    setMessagePattern(gt(), msgGt());
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
    setMessagePattern(gte(), msgGte());
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
    setMessagePattern(lt(), msgLt());
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
    setMessagePattern(lte(), msgLte());
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
    MESSAGE_PATTERNS = new IdentityHashMap<>(tmp0.size());
    CHECK_NAMES = new IdentityHashMap<>(tmp1.size());
    tmp0.forEach(tuple -> tuple.insertInto(MESSAGE_PATTERNS));
    tmp1.forEach(tuple -> tuple.insertInto(CHECK_NAMES));
    tmp0 = null;
    tmp1 = null;
  }

  private static final String suffix = "()";

  static String nameOf(Predicate<?> test) {
    return ifNotNull(CHECK_NAMES.get(test), name -> name + suffix, Predicate.class.getSimpleName());
  }

  static String nameOf(IntPredicate test) {
    return ifNotNull(
        CHECK_NAMES.get(test), name -> name + suffix, IntPredicate.class.getSimpleName());
  }

  static String nameOf(Relation<?, ?> test) {
    return ifNotNull(CHECK_NAMES.get(test), name -> name + suffix, Relation.class.getSimpleName());
  }

  static String nameOf(IntRelation test) {
    return ifNotNull(
        CHECK_NAMES.get(test), name -> name + suffix, IntRelation.class.getSimpleName());
  }

  static String nameOf(ObjIntRelation<?> test) {
    return ifNotNull(
        CHECK_NAMES.get(test), name -> name + suffix, ObjIntRelation.class.getSimpleName());
  }

  static String nameOf(IntObjRelation<?> test) {
    return ifNotNull(
        CHECK_NAMES.get(test), name -> name + suffix, IntObjRelation.class.getSimpleName());
  }

  private static void setMessagePattern(Object test, Formatter message) {
    tmp0.add(Tuple.of(test, message));
  }

  private static void setName(Object test, String name) {
    tmp1.add(Tuple.of(test, name));
  }
}
