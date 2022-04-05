package nl.naturalis.common.check;

import nl.naturalis.common.*;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.*;

import static nl.naturalis.common.check.MsgIntObjRelation.*;
import static nl.naturalis.common.check.MsgIntPredicate.*;
import static nl.naturalis.common.check.MsgIntRelation.*;
import static nl.naturalis.common.check.MsgPredicate.*;
import static nl.naturalis.common.check.MsgRelation.*;

/**
 * Defines various common tests for arguments. The tests have short, informative error messages
 * associated with them so you don't have to invent them yourself. Unless specified otherwise, they
 * <i>only</i> test what they advertise to be testing. They <b>will not</b> do a preliminary null
 * check! If the argument might be {@code null}, always precede them with the {@link #notNull()}
 * check. Otherwise, a raw, unprocessed {@link NullPointerException} will be thrown from the test
 * <i>itself</i>, rather than the application code.
 *
 * <blockquote>
 *
 * <pre>{@code
 * Check.notNull(file, "file").is(readable());
 * // Or:
 * Check.that(file, "file").is(notNull()).is(readable());
 * }</pre>
 *
 * </blockquote>
 *
 * <p>The {@code CommonChecks} also contains a few static exception factories that you can use in
 * combination with {@code Check.on(...)}:
 *
 * <blockquote>
 *
 * <pre>{@code
 * Check.on(illegalState(), file, "file").is(notNull()).is(readable());
 * }</pre>
 *
 * </blockquote>
 *
 * @author Ayco Holleman
 */
@SuppressWarnings("rawtypes")
public final class CommonChecks {

  static final Map<Object, PrefabMsgFormatter> MESSAGE_PATTERNS;
  static final Map<Object, String> NAMES;

  private static Map<Object, PrefabMsgFormatter> tmp0 = new HashMap<>(64);
  private static Map<Object, String> tmp1 = new HashMap<>(64);

  private CommonChecks() {
  }

  //////////////////////////////////////////////////////////////////////////////////
  // Predicate
  //////////////////////////////////////////////////////////////////////////////////

  /**
   * Verifies that the argument is null. Equivalent to {@link Objects#isNull(Object)
   * Objects::isNull}.
   *
   * @param <T> The type of the argument
   * @return A function implementing the test described above
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
   * @return A function implementing the test described above
   */
  public static <T> Predicate<T> notNull() {
    return Objects::nonNull;
  }

  static {
    setMessagePattern(notNull(), msgNotNull());
    setName(notNull(), "notNull");
  }

  /**
   * Verifies that the argument is {@code true}. If the argument is a {@code Boolean} rather than a
   * {@code boolean} and the argument might be null, use {@link #nullOr() nullOr()}.
   *
   * <blockquote>
   *
   * <pre>{@code
   * Check.that(list.isEmpty()).is(yes());
   * Check.that(list).has(List::isEmpty, yes());
   * }</pre>
   *
   * </blockquote>
   *
   * @return A function implementing the test described above
   */
  public static Predicate<Boolean> yes() {
    return x -> x;
  }

  static {
    setMessagePattern(yes(), msgYes());
    setName(yes(), "yes");
  }

  /**
   * Verifies that the argument is {@code false}. If the argument is a {@code Boolean} rather than a
   * {@code boolean} and the argument might be null, use {@link #nullOr() nullOr()}.
   *
   * @return A function implementing the test described above
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
   * <p>This check performs an implicit null check, so can be safely executed without (or instead
   * of) executing the {@link #notNull()} check first.
   *
   * @param <T> The type of the argument
   * @return A function implementing the test described above
   */
  public static <T> Predicate<T> empty() {
    return ObjectMethods::isEmpty;
  }

  static {
    setMessagePattern(empty(), msgEmpty());
    setName(empty(), "empty");
  }

  /**
   * Verifies that the argument is not null and, if it is an array, {@link Map} or {@link
   * Collection}, that it does not contain any null values. This check still allows the array, map
   * or collection to be empty (zero-sized). Use {@link #deepNotEmpty()} if you want to disallow
   * this, too.
   *
   * <p>This check performs an implicit null check, so can be safely executed without (or instead
   * of) executing the {@link #notNull()} check first.
   *
   * @param <T> The type of the argument
   * @return A function implementing the test described above
   * @see ObjectMethods#isDeepNotNull(Object)
   */
  public static <T> Predicate<T> deepNotNull() {
    return ObjectMethods::isDeepNotNull;
  }

  static {
    setMessagePattern(deepNotNull(), msgDeepNotNull());
    setName(deepNotNull(), "deepNotNull");
  }

  /**
   * Verifies that the argument is not null, not empty, and, if it is an array, {@link Map} or
   * {@link Collection}, that it does not contain any null or empty values. Equivalent to {@link
   * ObjectMethods#isDeepNotEmpty(Object) ObjectMethods::isDeepNotEmpty}.
   *
   * <p>This check performs an implicit null check, so can be safely executed without (or instead
   * of) executing the {@link #notNull()} check first.
   *
   * @param <T> The type of the argument
   * @return A function implementing the test described above
   * @see ObjectMethods#isDeepNotEmpty(Object)
   */
  public static <T> Predicate<T> deepNotEmpty() {
    return ObjectMethods::isDeepNotEmpty;
  }

  static {
    setMessagePattern(deepNotEmpty(), msgDeepNotEmpty());
    setName(deepNotEmpty(), "deepNotEmpty");
  }

  /**
   * Verifies that a {@code String} argument is null or contains whitespace only. Probably more
   * useful when called from an {@code isNot} method.
   *
   * <p>This check performs an implicit null check, so can be safely executed without (or instead
   * of) executing the {@link #notNull()} check first.
   *
   * @return A function implementing the test described above
   */
  public static Predicate<String> blank() {
    return StringMethods::isBlank;
  }

  static {
    setMessagePattern(blank(), msgBlank());
    setName(blank(), "blank");
  }

  /**
   * Verifies that a {@code String} argument represents a valid integer. Equivalent to {@link
   * NumberMethods#isInteger(String) NumberMethods::isInteger}.
   *
   * @return A function implementing the test described above
   */
  public static Predicate<String> integer() {
    return NumberMethods::isInteger;
  }

  static {
    setMessagePattern(integer(), msgInteger());
    setName(integer(), "integer");
  }

  /**
   * Verifies that the argument is an array or, if the argument is a {@code Class} object, that is
   * an array type.
   *
   * @param <T> The type of the argument
   * @return A function implementing the test described above
   */
  public static <T> Predicate<T> array() {
    return x -> x.getClass() == Class.class
        ? ((Class) x).isArray()
        : x.getClass().isArray();
  }

  static {
    setMessagePattern(array(), msgArray());
    setName(array(), "array");
  }

  /**
   * Verifies that the argument is an existing, regular file. Equivalent to {@link File#isFile()
   * File::isFile}.
   *
   * @return A function implementing the test described above
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
   * @return A function implementing the test described above
   */
  public static Predicate<File> directory() {
    return File::isDirectory;
  }

  static {
    setMessagePattern(directory(), msgDirectory());
    setName(directory(), "directory");
  }

  /**
   * Verifies that the argument is an existing file of any type.Equivalent to {@link File#exists()
   * File::exists}.
   *
   * @return A function implementing the test described above
   */
  public static Predicate<File> fileExists() {
    return File::exists;
  }

  static {
    setMessagePattern(fileExists(), msgFileExists());
    setName(fileExists(), "fileExists");
  }

  /**
   * Verifies that the argument is a readable file (implies that the file exists). Equivalent to
   * {@link File#canRead() File::canRead}.
   *
   * @return A function implementing the test described above
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
   * @return A function implementing the test described above
   */
  public static Predicate<File> writable() {
    return File::canWrite;
  }

  static {
    setMessagePattern(writable(), msgWritable());
    setName(writable(), "writable");
  }

  //////////////////////////////////////////////////////////////////////////////////
  // IntPredicate
  //////////////////////////////////////////////////////////////////////////////////

  /**
   * Verifies that the argument is an even number.
   *
   * @return A function implementing the test described above
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
   * @return A function implementing the test described above
   */
  public static IntPredicate odd() {
    return x -> x % 2 == 1;
  }

  static {
    setMessagePattern(odd(), msgOdd());
    setName(odd(), "odd");
  }

  /**
   * Verifies that the argument is greater than zero. If the argument is an {@code int}, you are
   * better off using the {@link #gt()} check, performancewise.
   *
   * @return A function implementing the test described above
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
   * @return A function implementing the test described above
   */
  public static IntPredicate negative() {
    return x -> x < 0;
  }

  static {
    setMessagePattern(negative(), msgNegative());
    setName(negative(), "negative");
  }

  /**
   * Verifies that the argument is 0 (zero).
   *
   * @return A function implementing the test described above
   */
  public static IntPredicate zero() {
    return x -> x == 0;
  }

  static {
    setMessagePattern(zero(), msgZero());
    setName(zero(), "zero");
  }

  //////////////////////////////////////////////////////////////////////////////////
  // IntRelation
  //////////////////////////////////////////////////////////////////////////////////

  /**
   * Verifies that the argument is equal to a particular value.
   *
   * @return A function implementing the test described above
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
   * @return A function implementing the test described above
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
   * @return A function implementing the test described above
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
   * @return A function implementing the test described above
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
   * @return A function implementing the test described above
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
   * @return A function implementing the test described above
   */
  public static IntRelation lte() {
    return (x, y) -> x <= y;
  }

  static {
    setMessagePattern(lte(), msgLte());
    setName(lte(), "lte");
  }

  /**
   * Verifies that the argument is a whole multiple of a particular integer.
   *
   * @return A function implementing the test described above
   */
  public static IntRelation multipleOf() {
    return (x, y) -> x % y == 0;
  }

  static {
    setMessagePattern(multipleOf(), msgMultipleOf());
    setName(multipleOf(), "multipleOf");
  }

  //////////////////////////////////////////////////////////////////////////////////
  // Relation
  //////////////////////////////////////////////////////////////////////////////////

  /**
   * Verifies that the argument equals the specified value. Equivalent to {@link
   * Objects#equals(Object) Objects::equals}.
   *
   * @param <X> The type of the argument
   * @return A function implementing the test described above
   */
  public static <X, Y> Relation<X, Y> EQ() {
    return Objects::equals;
  }

  static {
    setMessagePattern(EQ(), msgEq()); // Recycle message
    setName(EQ(), "EQ");
  }

  /**
   * Verifies that the argument is greater than the specified value. Especially useful for checking
   * values of primitive wrappers like {@code Byte}, {@code Integer} or {@code Double}, but can be
   * used to check any value that is an instance of {@link Comparable}.
   *
   * @param <X> The type of the values being compared
   * @return A function implementing the test described above
   * @see #gt()
   */
  public static <X extends Comparable<X>> Relation<X, X> GT() {
    return (x, y) -> x.compareTo(y) > 0;
  }

  static {
    setMessagePattern(GT(), msgGt()); // Recycle message
    setName(GT(), "GT");
  }

  /**
   * Verifies that the argument is less than the specified value. Especially useful for checking
   * values of primitive wrappers like {@code Byte}, {@code Integer} or {@code Double}, but can be
   * used to check any value that is an instance of {@link Comparable}.
   *
   * @param <X> The type of the values being compared
   * @return A function implementing the test described above
   * @see #lt()
   */
  public static <X extends Comparable<X>> Relation<X, X> LT() {
    return (x, y) -> x.compareTo(y) < 0;
  }

  static {
    setMessagePattern(LT(), msgLt()); // Recycle message
    setName(LT(), "LT");
  }

  /**
   * Verifies that the argument is greater than or equal to the specified value. Especially useful
   * for checking values of primitive wrappers like {@code Byte}, {@code Integer} or {@code Double},
   * but can be used to check any value that is an instance of {@link Comparable}.
   *
   * @param <X> The type of the values being compared
   * @return A function implementing the test described above
   * @see #gte()
   */
  public static <X extends Comparable<X>> Relation<X, X> GTE() {
    return (x, y) -> x.compareTo(y) >= 0;
  }

  static {
    setMessagePattern(GTE(), msgGte()); // Recycle message
    setName(GTE(), "GTE");
  }

  /**
   * Verifies that the argument is less than or equal to the specified value. Especially useful for
   * checking values of primitive wrappers like {@code Byte}, {@code Integer} or {@code Double}, but
   * can be used to check any value that is an instance of {@link Comparable}.
   *
   * @param <X> The type of the values being compared
   * @return A function implementing the test described above
   * @see #lte()
   */
  public static <X extends Comparable<X>> Relation<X, X> LTE() {
    return (x, y) -> x.compareTo(y) <= 0;
  }

  static {
    setMessagePattern(LTE(), msgLte()); // Recycle message
    setName(LTE(), "LTE");
  }

  /**
   * Verifies that the argument references the same object as the specified value
   *
   * @param <X> The type of the argument (the subject of the {@code Relation})
   * @param <Y> The type of the value to compare it with (the object of the {@code Relation})
   * @return A function implementing the test described above
   */
  public static <X, Y> Relation<X, Y> sameAs() {
    return (x, y) -> x == y;
  }

  static {
    setMessagePattern(sameAs(), msgSameAs());
    setName(sameAs(), "sameAs");
  }

  /**
   * Verifies that the argument is either null or equals the specified value.
   *
   * @param <X> The type of the argument
   * @return A function implementing the test described above
   */
  public static <X> Relation<X, X> nullOr() {
    return (x, y) -> x == null || x.equals(y);
  }

  static {
    setMessagePattern(nullOr(), msgNullOr());
    setName(nullOr(), "nullOr");
  }

  /**
   * Verifies that the argument is an instance of the specified class.
   *
   * @param <X> The type of the argument
   * @return A function implementing the test described above
   */
  public static <X> Relation<X, Class<?>> instanceOf() {
    return (x, y) -> y.isInstance(x);
  }

  static {
    setMessagePattern(instanceOf(), msgInstanceOf());
    setName(instanceOf(), "instanceOf");
  }

  /**
   * Verifies that the argument is a supertype of the specified class. Equivalent to {@link
   * Class#isAssignableFrom(Class) Class::isAssignableFrom}.
   *
   * @return A function implementing the test described above
   */
  public static <T, U> Relation<Class<T>, Class<U>> supertypeOf() {
    return Class::isAssignableFrom;
  }

  static {
    setMessagePattern(supertypeOf(), msgSupertypeOf());
    setName(supertypeOf(), "supertypeOf");
  }

  /**
   * Verifies that the argument extends or implements the specified class / interface.
   *
   * @return A function implementing the test described above
   */
  public static <T, U> Relation<Class<T>, Class<U>> subtypeOf() {
    return (x, y) -> y.isAssignableFrom(x);
  }

  static {
    setMessagePattern(subtypeOf(), msgSubtypeOf());
    setName(subtypeOf(), "subtypeOf");
  }

  /**
   * Verifies that a {@code Collection} argument contains the specified value. Equivalent to {@link
   * Collection#contains(Object) Collection::contains}.
   *
   * @param <E> The type of the elements in the {@code Collection}
   * @param <C> The type of the collection
   * @return A function implementing the test described above
   */
  public static <E, C extends Collection<? super E>> Relation<C, E> contains() {
    return Collection::contains;
  }

  static {
    setMessagePattern(contains(), msgContains());
    setName(contains(), "contains");
  }

  /**
   * Verifies that a {@code Map} argument contains the specified key. Equivalent to {@link
   * Map#containsKey(Object) Map::containsKey}.
   *
   * @param <K> The type of the keys within the map
   * @param <M> The Type of the {@code Map}
   * @return A function implementing the test described above
   */
  public static <K, M extends Map<? super K, ?>> Relation<M, K> hasKey() {
    return Map::containsKey;
  }

  static {
    setMessagePattern(hasKey(), msgHasKey());
    setName(hasKey(), "hasKey");
  }

  /**
   * Verifies that a {@code Map} argument contains the specified value. Equivalent to {@link
   * Map#containsValue(Object) Map::containsValue}.
   *
   * @param <V> The type of the values within the map
   * @param <M> The Type of the {@code Map}
   * @return A function implementing the test described above
   */
  public static <V, M extends Map<?, ? super V>> Relation<M, V> hasValue() {
    return Map::containsValue;
  }

  static {
    setMessagePattern(hasValue(), msgHasValue());
    setName(hasValue(), "msgHasValue");
  }

  /**
   * Verifies that the argument is an element of the specified {@code Collection}.
   *
   * @param <E> The type of the argument
   * @param <C> The type of the {@code Collection}
   * @return A function implementing the test described above
   */
  public static <E, C extends Collection<? super E>> Relation<E, C> in() {
    return (x, y) -> y.contains(x);
  }

  static {
    setMessagePattern(in(), msgIn());
    setName(in(), "in");
  }

  /**
   * Verifies that the argument is a key in the specified {@code Map}.
   *
   * @param <K> The type of the keys within the map
   * @param <M> The Type of the {@code Map}
   * @return A function implementing the test described above
   */
  public static <K, M extends Map<? super K, ?>> Relation<K, M> keyIn() {
    return (x, y) -> y.containsKey(x);
  }

  static {
    setMessagePattern(keyIn(), msgKeyIn());
    setName(keyIn(), "keyIn");
  }

  /**
   * Verifies the presence of a value within a {@code Map}.
   *
   * @param <K> The type of the keys within the map
   * @param <M> The Type of the {@code Map}
   * @return A function implementing the test described above
   */
  public static <K, M extends Map<? super K, ?>> Relation<K, M> valueIn() {
    return (x, y) -> y.containsValue(x);
  }

  static {
    setMessagePattern(valueIn(), msgValueIn());
    setName(valueIn(), "valueIn");
  }

  /**
   * Verifies that the argument is an element of the specified array. Equivalent to {@link
   * ArrayMethods#isElementOf(Object, Object[]) ArrayMethods::isElementOf}.
   *
   * @param <X> The type of the argument
   * @param <Y> The component type of the array
   * @return A function implementing the test described above
   */
  public static <Y, X extends Y> Relation<X, Y[]> elementOf() {
    return ArrayMethods::isElementOf;
  }

  static {
    setMessagePattern(elementOf(), msgIn()); // Recycle message
    setName(elementOf(), "elementOf");
  }

  /**
   * Verifies that a {@code Collection} argument contains all the elements of the specified
   * collection. Equivalent to {@link Collection#containsAll(Collection) Collection::containsAll}.
   * Note that neither collection needs to be a {@link Set}:
   *
   * <blockquote>
   *
   * <pre>{@code
   * Check.that(List.of(1,2,3)).is(supersetOf(), Set.of(1,2); // valid and true
   * Check.that(List.of(1,2)).is(supersetOf(), Set.of(1,2,3); // valid but false
   * }</pre>
   *
   * </blockquote>
   *
   * @param <E> The type of the elements in the {@code Collection}
   * @param <C0> The type of the argument (the subject of the {@code Relation})
   * @param <C1> The type of the object of the {@code Relation}
   * @return A function implementing the test described above
   */
  public static <E, C0 extends Collection<? super E>, C1 extends Collection<E>> Relation<C0, C1> supersetOf() {
    return Collection::containsAll;
  }

  static {
    setMessagePattern(supersetOf(), msgSupersetOf());
    setName(supersetOf(), "supersetOf");
  }

  /**
   * Verifies that a {@code Collection} argument is a subset or sublist of another {@code
   * Collection}. Note that neither collection needs to be a {@link Set}:
   *
   * <blockquote>
   *
   * <pre>{@code
   * Check.that(List.of(1,2,3)).is(subsetOf(), Set.of(1,2); // valid but false
   * Check.that(List.of(1,2)).is(subsetOf(), Set.of(1,2,3); // valid and true
   * }</pre>
   *
   * </blockquote>
   *
   * @param <E> The type of the elements in the {@code Collection}
   * @param <C0> The type of the argument (the subject of the {@code Relation})
   * @param <C1> The type of the object of the {@code Relation}
   * @return A function implementing the test described above
   */
  public static <E, C0 extends Collection<E>, C1 extends Collection<? super E>> Relation<C0, C1> subsetOf() {
    return (x, y) -> y.containsAll(x);
  }

  static {
    setMessagePattern(subsetOf(), msgSubsetOf());
    setName(subsetOf(), "subsetOf");
  }

  /**
   * Verifies that a {@code String} argument contains the specified substring. Equivalent to {@link
   * String#contains(CharSequence) String::contains}.
   *
   * @return A function implementing the test described above
   */
  public static Relation<String, CharSequence> hasSubstring() {
    return String::contains;
  }

  static {
    setMessagePattern(hasSubstring(), msgHasSubstring());
    setName(hasSubstring(), "hasSubstring");
  }

  /**
   * Verifies that the argument is a substring of the specified string.
   *
   * @return A function implementing the test described above
   */
  public static Relation<String, String> substringOf() {
    return (x, y) -> y.contains(x);
  }

  static {
    setMessagePattern(substringOf(), msgSubstringOf());
    setName(substringOf(), "substringOf");
  }

  /**
   * Verifies that the argument equals ignoring case the specified string.
   *
   * @return A function implementing the test described above
   */
  public static Relation<String, String> equalsIgnoreCase() {
    return String::equalsIgnoreCase;
  }

  static {
    setMessagePattern(equalsIgnoreCase(), msgEqualsIgnoreCase());
    setName(equalsIgnoreCase(), "equalsIgnoreCase");
  }

  /**
   * Verifies that a {@code String} argument starts with the specified substring. Equivalent to
   * {@link String#startsWith(String) String::startsWith}.
   *
   * @return A function implementing the test described above
   */
  public static Relation<String, String> startsWith() {
    return String::startsWith;
  }

  static {
    setMessagePattern(startsWith(), msgStartsWith());
    setName(startsWith(), "startsWith");
  }

  /**
   * Verifies that a {@code String} argument ends with the specified substring. Equivalent to {@link
   * String#endsWith(String) String::endsWith}.
   *
   * @return A function implementing the test described above
   */
  public static Relation<String, String> endsWith() {
    return String::endsWith;
  }

  static {
    setMessagePattern(endsWith(), msgEndsWith());
    setName(endsWith(), "endsWith");
  }

  //////////////////////////////////////////////////////////////////////////////////
  // ObjIntRelation
  //////////////////////////////////////////////////////////////////////////////////

  /**
   * Verifies that the length of a {@code String} argument has the specified value. Although you can
   * use this check with the {@link ObjectCheck#is(ObjIntRelation, int) is()} and {@link
   * ObjectCheck#isNot(ObjIntRelation, int) isNot()} method, it is specifically meant to be used
   * with the {@link ObjectCheck#has(Function, ObjIntRelation, int) has()} and {@link
   * ObjectCheck#notHas(Function, ObjIntRelation, int) notHas()} methods as there is no API for
   * validating properties of properties:
   *
   * <blockquote>
   *
   * <pre>{@code
   * // Validate the length of a string argument (not preferred):
   * Check.that("FOO").is(strlenEQ(), 3);
   *
   * // Validate the length of a string argument (preferred):
   * Check.that("FOO").has(strlen(), eq(), 3);
   *
   * // Validate the length property of the lastName property of the employee argument:
   * Check.that(employee).has(Employee::getLastName, strlenEQ(), 3);
   * }</pre>
   *
   * </blockquote>
   *
   * @return A function implementing the test described above
   */
  public static ObjIntRelation<String> strlenEQ() {
    return (x, y) -> x.length() == y;
  }

  static {
    setMessagePattern(strlenEQ(), msgEq()); // Recycle message
    setName(strlenEQ(), "strlenEQ");
  }

  /**
   * Verifies that the length of a {@code String} argument is greater than the specified value.
   * Although you can use this check with the {@link ObjectCheck#is(ObjIntRelation, int) is()} and
   * {@link ObjectCheck#isNot(ObjIntRelation, int) isNot()} method, it is specifically meant to be
   * used with the {@link ObjectCheck#has(Function, ObjIntRelation, int) has()} and {@link
   * ObjectCheck#notHas(Function, ObjIntRelation, int) notHas()} methods as there is no API for
   * validating properties of properties.
   *
   * <blockquote>
   *
   * <pre>{@code
   * // Validate the length property of a string argument (not preferred):
   * Check.that("FOO").is(strlenGT(), 2);
   *
   * // Validate the length property of a string argument (preferred):
   * Check.that("FOO").has(strlen(), gt(), 2);
   *
   * // Validate the length property of the lastName property of the employee argument:
   * Check.that(employee).has(Employee::getLastName, strlenGT(), 2);
   * }</pre>
   *
   * </blockquote>
   *
   * @return A function implementing the test described above
   */
  public static ObjIntRelation<String> strlenGT() {
    return (x, y) -> x.length() > y;
  }

  static {
    setMessagePattern(strlenGT(), msgGt()); // Recycle message
    setName(strlenGT(), "strlenGT");
  }

  /**
   * Verifies that the length of a {@code String} argument is greater than or equal to the specified
   * value. Although you can use this check with the {@link ObjectCheck#is(ObjIntRelation, int)
   * is()} and {@link ObjectCheck#isNot(ObjIntRelation, int) isNot()} method, it is specifically
   * meant to be used with the {@link ObjectCheck#has(Function, ObjIntRelation, int) has()} and
   * {@link ObjectCheck#notHas(Function, ObjIntRelation, int) notHas()} methods as there is no API
   * for validating properties of properties.
   *
   * @return A function implementing the test described above
   * @see #strlenGT()
   */
  public static ObjIntRelation<String> strlenGTE() {
    return (x, y) -> x.length() >= y;
  }

  static {
    setMessagePattern(strlenGTE(), msgGte()); // Recycle message
    setName(strlenGTE(), "strlenGTE");
  }

  /**
   * Verifies that the length of a {@code String} argument is less than the specified value.
   * Although you can use this check with the {@link ObjectCheck#is(ObjIntRelation, int) is()} and
   * {@link ObjectCheck#isNot(ObjIntRelation, int) isNot()} method, it is specifically meant to be
   * used with the {@link ObjectCheck#has(Function, ObjIntRelation, int) has()} and {@link
   * ObjectCheck#notHas(Function, ObjIntRelation, int) notHas()} methods as there is no API for
   * validating properties of properties.
   *
   * @return A function implementing the test described above
   * @see #strlenGT()
   */
  public static ObjIntRelation<String> strlenLT() {
    return (x, y) -> x.length() < y;
  }

  static {
    setMessagePattern(strlenLT(), msgLt()); // Recycle message
    setName(strlenLT(), "strlenLT");
  }

  /**
   * Verifies that the length of a {@code String} argument is less than or equal to the specified
   * value. Although you can use this check with the {@link ObjectCheck#is(ObjIntRelation, int)
   * is()} and {@link ObjectCheck#isNot(ObjIntRelation, int) isNot()} method, it is specifically
   * meant to be used with the {@link ObjectCheck#has(Function, ObjIntRelation, int) has()} and
   * {@link ObjectCheck#notHas(Function, ObjIntRelation, int) notHas()} methods as there is no API
   * for validating properties of properties.
   *
   * @return A function implementing the test described above
   * @see #strlenGT()
   */
  public static ObjIntRelation<String> strlenLTE() {
    return (x, y) -> x.length() <= y;
  }

  static {
    setMessagePattern(strlenLTE(), msgLte()); // Recycle message
    setName(strlenLTE(), "strlenLTE");
  }

  /**
   * Verifies that the size of a {@code Collection} argument has the specified value. Although you
   * can use this check with the {@link ObjectCheck#is(ObjIntRelation, int) is()} and {@link
   * ObjectCheck#isNot(ObjIntRelation, int) isNot()} method, it is specifically meant to be used
   * with the {@link ObjectCheck#has(Function, ObjIntRelation, int) has()} and {@link
   * ObjectCheck#notHas(Function, ObjIntRelation, int) notHas()} methods as there is no API for
   * validating properties of properties:
   *
   * <blockquote>
   *
   * <pre>{@code
   * // Validate the size property of a collection argument (not preferred):
   * Check.that(List.of("A", "B", "C")).is(sizeEQ(), 3);
   *
   * // Validate the size property of a collection argument (preferred):
   * Check.that(List.of("A", "B", "C")).has(size(), eq(), 3);
   *
   * // Validate the size property of the employees property of the company argument:
   * Check.that(company).has(Company::getEmployees, sizeEQ(), 3);
   * }</pre>
   *
   * </blockquote>
   *
   * @return A function implementing the test described above
   */
  public static <E, C extends Collection<E>> ObjIntRelation<C> sizeEQ() {
    return (x, y) -> x.size() == y;
  }

  static {
    setMessagePattern(sizeEQ(), msgEq()); // Recycle message
    setName(sizeEQ(), "sizeEQ");
  }

  /**
   * Verifies that the size of a {@code Collection} argument is greater than the specified value.
   * Although you can use this check with the {@link ObjectCheck#is(ObjIntRelation, int) is()} and
   * {@link ObjectCheck#isNot(ObjIntRelation, int) isNot()} method, it is specifically meant to be
   * used with the {@link ObjectCheck#has(Function, ObjIntRelation, int) has()} and {@link
   * ObjectCheck#notHas(Function, ObjIntRelation, int) notHas()} methods as there is no API for
   * validating properties of properties:
   *
   * <blockquote>
   *
   * <pre>{@code
   * // Validate the size property of a collection argument (not preferred):
   * Check.that(List.of("A", "B", "C")).is(sizeGT(), 2);
   *
   * // Validate the size property of a collection argument (preferred):
   * Check.that(List.of("A", "B", "C")).has(size(), gt(), 2);
   *
   * // Validate the size property of the employees property of the company argument:
   * Check.that(company).has(Company::getEmployees, sizeGT(), 2);
   * }</pre>
   *
   * </blockquote>
   *
   * @return A function implementing the test described above
   */
  public static <E, C extends Collection<E>> ObjIntRelation<C> sizeGT() {
    return (x, y) -> x.size() > y;
  }

  static {
    setMessagePattern(sizeGT(), msgGt()); // Recycle message
    setName(sizeGT(), "sizeGT");
  }

  /**
   * Verifies that the size of a {@code Collection} argument is greater than or equal to the
   * specified value. Although you can use this check with the {@link ObjectCheck#is(ObjIntRelation,
   * int) is()} and {@link ObjectCheck#isNot(ObjIntRelation, int) isNot()} method, it is
   * specifically meant to be used with the {@link ObjectCheck#has(Function, ObjIntRelation, int)
   * has()} and {@link ObjectCheck#notHas(Function, ObjIntRelation, int) notHas()} methods as there
   * is no API for validating properties of properties.
   *
   * @return A function implementing the test described above
   * @see #sizeGT()
   */
  public static <E, C extends Collection<E>> ObjIntRelation<C> sizeGTE() {
    return (x, y) -> x.size() >= y;
  }

  static {
    setMessagePattern(sizeGTE(), msgGte()); // Recycle message
    setName(sizeGTE(), "sizeGTE");
  }

  /**
   * Verifies that the size of a {@code Collection} argument is less than the specified value.
   * Although you can use this check with the {@link ObjectCheck#is(ObjIntRelation, int) is()} and
   * {@link ObjectCheck#isNot(ObjIntRelation, int) isNot()} method, it is specifically meant to be
   * used with the {@link ObjectCheck#has(Function, ObjIntRelation, int) has()} and {@link
   * ObjectCheck#notHas(Function, ObjIntRelation, int) notHas()} methods as there is no API for
   * validating properties of properties.
   *
   * @return A function implementing the test described above
   * @see #sizeGT()
   */
  public static <E, C extends Collection<E>> ObjIntRelation<C> sizeLT() {
    return (x, y) -> x.size() < y;
  }

  static {
    setMessagePattern(sizeLT(), msgLt()); // Recycle message
    setName(sizeLT(), "sizeLT");
  }

  /**
   * Verifies that the size of a {@code Collection} argument is less than or equal to the specified
   * value. Although you can use this check with the {@link ObjectCheck#is(ObjIntRelation, int)
   * is()} and {@link ObjectCheck#isNot(ObjIntRelation, int) isNot()} method, it is specifically
   * meant to be used with the {@link ObjectCheck#has(Function, ObjIntRelation, int) has()} and
   * {@link ObjectCheck#notHas(Function, ObjIntRelation, int) notHas()} methods as there is no API
   * for validating properties of properties.
   *
   * @return A function implementing the test described above
   * @see #sizeGT()
   */
  public static <E, C extends Collection<E>> ObjIntRelation<C> sizeLTE() {
    return (x, y) -> x.size() <= y;
  }

  static {
    setMessagePattern(sizeLTE(), msgLte()); // Recycle message
    setName(sizeLTE(), "sizeLTE");
  }

  /**
   * Verifies that the size of an array argument is equal to the specified value. No preliminary
   * check is done to determine if the argument actually <i>is</i> an array. Execute the {@link
   * #array()} check first if there is any doubt about this. Although you can use this check with
   * the {@link ObjectCheck#is(ObjIntRelation, int) is()} and
   * {@link ObjectCheck#isNot(ObjIntRelation,
   * int) isNot()} method, it is specifically meant to be used with the {@link
   * ObjectCheck#has(Function, ObjIntRelation, int) has()} and {@link ObjectCheck#notHas(Function,
   * ObjIntRelation, int) notHas()} methods as there is no API for validating properties of
   * properties:
   *
   * <blockquote>
   *
   * <pre>{@code
   * // Validate the length of an array argument (not preferred):
   * Check.that(new int[10]).is(lenEQ(), 10);
   *
   * // Validate the length of an array argument (preferred):
   * Check.that(new int[10]).has(length(), eq(), 10);
   *
   *
   * // Validate the length property of the employees array of the company argument:
   * Check.that(company).has(Company::getEmployees, lenEQ(), 25);
   * }</pre>
   *
   * </blockquote>
   *
   * @return A function implementing the test described above
   */
  public static <T> ObjIntRelation<T> lenEQ() {
    return (x, y) -> Array.getLength(x) == y;
  }

  static {
    setMessagePattern(lenEQ(), msgEq()); // Recycle message
    setName(lenEQ(), "lenEQ");
  }

  /**
   * Verifies that the size of an array argument is greater than the specified value. No preliminary
   * check is done to determine if the argument actually <i>is</i> an array. Execute the {@link
   * #array()} check first if there is any doubt about this. Although you can use this check with
   * the {@link ObjectCheck#is(ObjIntRelation, int) is()} and
   * {@link ObjectCheck#isNot(ObjIntRelation,
   * int) isNot()} method, it is specifically meant to be used with the {@link
   * ObjectCheck#has(Function, ObjIntRelation, int) has()} and {@link ObjectCheck#notHas(Function,
   * ObjIntRelation, int) notHas()} methods as there is no API for validating properties of
   * properties.
   *
   * @return A function implementing the test described above
   */
  public static <T> ObjIntRelation<T> lenGT() {
    return (x, y) -> Array.getLength(x) > y;
  }

  static {
    setMessagePattern(lenGT(), msgGt()); // Recycle message
    setName(lenGT(), "lenGT");
  }

  /**
   * Verifies that the size of an array argument is greater than or equal to the specified value. No
   * preliminary check is done to determine if the argument actually <i>is</i> an array. Execute the
   * {@link #array()} check first if there is any doubt about this. Although you can use this check
   * with the {@link ObjectCheck#is(ObjIntRelation, int) is()} and {@link
   * ObjectCheck#isNot(ObjIntRelation, int) isNot()} method, it is specifically meant to be used
   * with the {@link ObjectCheck#has(Function, ObjIntRelation, int) has()} and {@link
   * ObjectCheck#notHas(Function, ObjIntRelation, int) notHas()} methods as there is no API for
   * validating properties of properties.
   *
   * @return A function implementing the test described above
   */
  public static <T> ObjIntRelation<T> lenGTE() {
    return (x, y) -> Array.getLength(x) >= y;
  }

  static {
    setMessagePattern(lenGTE(), msgGte()); // Recycle message
    setName(lenGTE(), "lenGTE");
  }

  /**
   * Verifies that the size of an array argument is less than the specified value. No preliminary
   * check is done to determine if the argument actually <i>is</i> an array. Execute the {@link
   * #array()} check first if there is any doubt about this. Although you can use this check with
   * the {@link ObjectCheck#is(ObjIntRelation, int) is()} and
   * {@link ObjectCheck#isNot(ObjIntRelation,
   * int) isNot()} method, it is specifically meant to be used with the {@link
   * ObjectCheck#has(Function, ObjIntRelation, int) has()} and {@link ObjectCheck#notHas(Function,
   * ObjIntRelation, int) notHas()} methods as there is no API for validating properties of
   * properties.
   *
   * @return A function implementing the test described above
   */
  public static <T> ObjIntRelation<T> lenLT() {
    return (x, y) -> Array.getLength(x) < y;
  }

  static {
    setMessagePattern(lenLT(), msgLt()); // Recycle message
    setName(lenLT(), "lenLT");
  }

  /**
   * Verifies that the size of an array argument is less than or equal to the specified value. No
   * preliminary check is done to determine if the argument actually <i>is</i> an array. Execute the
   * {@link #array()} check first if there is any doubt about this. Although you can use this check
   * with the {@link ObjectCheck#is(ObjIntRelation, int) is()} and {@link
   * ObjectCheck#isNot(ObjIntRelation, int) isNot()} method, it is specifically meant to be used
   * with the {@link ObjectCheck#has(Function, ObjIntRelation, int) has()} and {@link
   * ObjectCheck#notHas(Function, ObjIntRelation, int) notHas()} methods as there is no API for
   * validating properties of properties.
   *
   * @return A function implementing the test described above
   */
  public static <T> ObjIntRelation<T> lenLTE() {
    return (x, y) -> Array.getLength(x) <= y;
  }

  static {
    setMessagePattern(lenLTE(), msgLte()); // Recycle message
    setName(lenLTE(), "lenLTE");
  }

  //////////////////////////////////////////////////////////////////////////////////
  // IntObjRelation
  //////////////////////////////////////////////////////////////////////////////////

  /**
   * Verifies that the argument can be used as index into the specified array. No preliminary check
   * is done to ascertain that the provided object actually is an array. Execute the {@link
   * #array()} check first if there is any doubt about this.
   *
   * <blockquote>
   *
   * <pre>{@code
   * Check.that(4).is(indexOf, new String[5]); // true
   * Check.that(5).is(indexOf, new String[5]); // false
   * Check.that(-1).is(indexOf, new String[5]); // false
   * }</pre>
   *
   * </blockquote>
   *
   * @param <T> The type of the array
   * @return A function implementing the test described above
   */
  public static <T> IntObjRelation<T> indexOf() {
    return (x, y) -> x >= 0 && x < Array.getLength(y);
  }

  static {
    setMessagePattern(indexOf(), msgIndexOf());
    setName(indexOf(), "indexOf");
  }

  /**
   * Verifies that the argument can be used as index into the specified list.
   *
   * @return A function implementing the test described above
   */
  public static <E, L extends List<E>> IntObjRelation<L> listIndexOf() {
    return (x, y) -> x >= 0 && x < y.size();
  }

  static {
    setMessagePattern(listIndexOf(), msgListIndexOf());
    setName(listIndexOf(), "listIndexOf");
  }

  /**
   * Verifies that the argument can be safely passed to {@link String#charAt(int) String.charAt}.
   *
   * @return A function implementing the test described above
   */
  public static IntObjRelation<String> strIndexOf() {
    return (x, y) -> x >= 0 && x < y.length();
  }

  static {
    setMessagePattern(strIndexOf(), msgStrIndexOf());
    setName(strIndexOf(), "strIndexOf");
  }

  /**
   * Verifies that the argument is present in the specified integer array. Equivalent to {@link
   * ArrayMethods#isElementOf(int, int[]) ArrayMethods::isElementOf}.
   *
   * @return A function implementing the test described above
   */
  public static IntObjRelation<int[]> intElementOf() {
    return ArrayMethods::isElementOf;
  }

  static {
    setMessagePattern(intElementOf(), msgIn()); // Recycle message
    setName(intElementOf(), "intElementOf");
  }

  /**
   * Verifies that the argument is greater than or equal to the first integer of the specified
   * {@link IntPair} and less than the second.
   *
   * @return A function implementing the test described above
   */
  public static IntObjRelation<IntPair> inRange() {
    return (x, y) -> x >= y.one() && x < y.two();
  }

  static {
    setMessagePattern(inRange(), msgInRange());
    setName(inRange(), "inRange");
  }

  /**
   * Verifies that the argument is greater than or equal to the first integer of the specified
   * {@link IntPair} and less than or equal to the second.
   *
   * @return A function implementing the test described above
   */
  public static IntObjRelation<IntPair> inRangeClosed() {
    return (x, y) -> x >= y.one() && x <= y.two();
  }

  static {
    setMessagePattern(inRangeClosed(), msgInRangeClosed());
    setName(inRangeClosed(), "inRangeClosed");
  }

  /* ++++++++++++++ Miscellaneous ++++++++++++++ */

  /**
   * (Not a check) Shortcut for {@link IllegalStateException#IllegalStateException(String)
   * IllegalStateException::new}. Can be used in combination with the {@link Check#on(Function,
   * Object) Check.on()} static factory method. For example:
   * <code>Check.on(illegalState(), out.isClosed()).is(no())</code>.
   *
   * @return A {@code Function} that takes a {@code String} (the exception message) and produces an
   *     {@code IllegalStateException}
   */
  public static Function<String, IllegalStateException> illegalState() {
    return IllegalStateException::new;
  }

  /**
   * (Not a check) Shortcut for {@link IndexOutOfBoundsException#IndexOutOfBoundsException(String)
   * IndexOutOfBoundsException::new}.
   *
   * @return A {@code Function} that takes a {@code String} (the exception message) and produces an
   *     {@code IndexOutOfBoundsException}
   */
  public static Function<String, IndexOutOfBoundsException> indexOutOfBounds() {
    return IndexOutOfBoundsException::new;
  }

  /**
   * (Not a check) Shortcut for
   * {@link UnsupportedOperationException#UnsupportedOperationException(String)
   * UnsupportedOperationException::new}.
   *
   * @return A {@code Function} that takes a {@code String} (the exception message) and produces an
   *     {@code UnsupportedOperationException}
   */
  public static Function<String, UnsupportedOperationException> unsupportedOperation() {
    return UnsupportedOperationException::new;
  }

  /**
   * (Not a check) Shortcut for {@link IOException#IOException(String) IOException::new}.
   *
   * @return A {@code Function} that takes a {@code String} (the exception message) and produces an
   *     {@code IOException}
   */
  public static Function<String, IOException> io() {
    return IOException::new;
  }

  /**
   * (Not a check) Shortcut for {@link NullPointerException#NullPointerException(String)
   * NullPointerException::new}. Could be used if you prefer illegal {@code null} values to cause a
   * {@code NullPointerException} rather than an {@cod IllegalArgumentException} (as is the
   * default).
   *
   * <blockquote>
   * <pre>{@code
   * Check.on(nullPointer(), foo, "foo").isNot(NULL());
   * }</pre>
   * </blockquote>
   *
   * @return A {@code Function} that takes a {@code String} (the exception message) and produces a
   *     {@code NullPointerException}
   */
  public static Function<String, NullPointerException> nullPointer() {
    return NullPointerException::new;
  }

  /**
   * (Not a check) Simply returns the specified {@code IntPredicate}. Use when passing a lambda or
   * method reference to the {@code Check.has()} and {@code Check.notHas()} methods. Because these
   * methods are heavily overloaded, the compiler may not be able to establish whether the lambda is
   * supposed to be a {@code Predicate} or {@code IntPredicate} (it will complain about an
   * <b>Ambiguous method call</b>). This method clears that up for the compiler.
   *
   * <p>NB this method is meant to wrap the {@code test} argument of a {@code Check.has()} or
   * {@code Check.notHas()} method, not the {@code property} argument. You only need to wrap one of
   * the arguments to disambiguate the call. It is a matter of taste which one you prefer.
   *
   * @param lambdaOrMethodReference A lambda or method reference
   * @return The same {@code IntPredicate}
   */
  public static IntPredicate asInt(IntPredicate lambdaOrMethodReference) {
    return lambdaOrMethodReference;
  }

  /**
   * (Not a check) Simply returns the specified {@code Predicate}. Use when passing a lambda or
   * method reference to the {@code Check.has()} and {@code Check.notHas()} methods. Because these
   * methods are heavily overloaded, the compiler may not be able to establish whether the lambda is
   * supposed to be a {@code Predicate} or {@code IntPredicate} (it will complain about an
   * <b>Ambiguous method call</b>). This method clears that up for the compiler.
   *
   * <p>NB this method is meant to wrap the {@code test} argument of a {@code Check.has()} or
   * {@code Check.notHas()} method, not the {@code property} argument. You only need to wrap one of
   * the arguments to disambiguate the call. It is a matter of taste which one you prefer.
   *
   * @param lambdaOrMethodReference A lambda or method reference
   * @param <T> The type of the object being tested
   * @return The same {@code Predicate}
   */
  public static <T> Predicate<T> asObj(Predicate<T> lambdaOrMethodReference) {
    return lambdaOrMethodReference;
  }

  /**
   * (Not a check) Simply returns the specified {@code Relation}. Use when passing a lambda or
   * method reference to the {@code Check.has()} and {@code Check.notHas()} methods. Because these
   * methods are heavily overloaded, the compiler may not be able to establish whether the lambda is
   * supposed to be a {@code Relation} or one of its sister interfaces (it will complain about an
   * <b>Ambiguous method call</b>). This method clears that up for the compiler.
   *
   * <p>NB this method is meant to wrap the {@code test} argument of a {@code Check.has()} or
   * {@code Check.notHas()} method, not the {@code property} argument. You only need to wrap one of
   * the arguments to disambiguate the call. It is a matter of taste which one you prefer.
   *
   * @param lambdaOrMethodReference A lambda or method reference
   * @param <T> The type of the subject of the {@code Relation}
   * @param <U> The type of the object of the {@code Relation}
   * @return The same {@code Relation}
   */
  public static <T, U> Relation<T, U> objObj(Relation<T, U> lambdaOrMethodReference) {
    return lambdaOrMethodReference;
  }

  /**
   * (Not a check) Simply returns the specified {@code ObjIntRelation}. Use when passing a lambda or
   * method reference to the {@code Check.has()} and {@code Check.notHas()} methods. Because these
   * methods are heavily overloaded, the compiler may not be able to establish whether the lambda is
   * supposed to be an {@code ObjIntRelation} or one of its sister interfaces (it will complain
   * about an <b>Ambiguous method call</b>). This method clears that up for the compiler.
   *
   * <p>NB this method is meant to wrap the {@code test} argument of a {@code Check.has()} or
   * {@code Check.notHas()} method, not the {@code property} argument. You only need to wrap one of
   * the arguments to disambiguate the call. It is a matter of taste which one you prefer.
   *
   * @param lambdaOrMethodReference A lambda or method reference
   * @param <T> The type of the subject of the {@code Relation}
   * @return The same {@code ObjIntRelation}
   */
  public static <T> ObjIntRelation<T> objInt(ObjIntRelation<T> lambdaOrMethodReference) {
    return lambdaOrMethodReference;
  }

  /**
   * (Not a check) Simply returns the specified {@code IntObjRelation}. Use when passing a lambda or
   * method reference to the {@code Check.has()} and {@code Check.notHas()} methods. Because these
   * methods are heavily overloaded, the compiler may not be able to establish whether the lambda is
   * supposed to be an {@code IntObjRelation} or one of its sister interfaces (it will complain
   * about an <b>Ambiguous method call</b>). This method clears that up for the compiler.
   *
   * <p>NB this method is meant to wrap the {@code test} argument of a {@code Check.has()} or
   * {@code Check.notHas()} method, not the {@code property} argument. You only need to wrap one of
   * the arguments to disambiguate the call. It is a matter of taste which one you prefer.
   *
   * @param lambdaOrMethodReference A lambda or method reference
   * @param <T> The type of the object of the {@code Relation}
   * @return The same {@code IntObjRelation}
   */
  public static <T> IntObjRelation<T> intObj(IntObjRelation<T> lambdaOrMethodReference) {
    return lambdaOrMethodReference;
  }

  /**
   * (Not a check) Simply returns the specified {@code IntRelation}. Use when passing a lambda or
   * method reference to the {@code Check.has()} and {@code Check.notHas()} methods. Because these
   * methods are heavily overloaded, the compiler may not be able to establish whether the lambda is
   * supposed to be an {@code IntRelation} or one of its sister interfaces (it will complain about
   * an <b>Ambiguous method call</b>). This method clears that up for the compiler.
   *
   * <p>NB this method is meant to wrap the {@code test} argument of a {@code Check.has()} or
   * {@code Check.notHas()} method, not the {@code property} argument. You only need to wrap one of
   * the arguments to disambiguate the call. It is a matter of taste which one you prefer.
   *
   * @param lambdaOrMethodReference A lambda or method reference
   * @return The same {@code IntRelation}
   */
  public static IntRelation intInt(IntRelation lambdaOrMethodReference) {
    return lambdaOrMethodReference;
  }

  /**
   * (Not a check) Simply returns the specified {@code ToIntFunction}. Use when passing a lambda or
   * method reference to the {@code Check.has()} and {@code Check.notHas()} methods. Because these
   * methods are heavily overloaded, the compiler may not be able to establish whether the lambda is
   * supposed to be an {@code ToIntFunction} or some other kind of function (it will complain about
   * an <b>Ambiguous method call</b>). This method clears that up for the compiler.
   *
   * <p>NB this method is meant to wrap the {@code property} argument of a {@code Check.has()} or
   * {@code Check.notHas()} method, not the {@code test} argument. You only need to wrap one of the
   * arguments to disambiguate the call. It is a matter of taste which one you prefer.
   *
   * @param lambdaOrMethodReference A lambda or method reference
   * @param <T> The type of the subject of the {@code Relation}
   * @return The same {@code ObjIntRelation}
   */
  public static <T> ToIntFunction<T> toInt(ToIntFunction<T> lambdaOrMethodReference) {
    return lambdaOrMethodReference;
  }

  /**
   * (Not a check) Simply returns the specified {@code IntFunction}. Use when passing a lambda or
   * method reference to the {@code Check.has()} and {@code Check.notHas()} methods. Because these
   * methods are heavily overloaded, the compiler may not be able to establish whether the lambda is
   * supposed to be an {@code IntFunction} or some other kind of function (it will complain about
   * an
   * <b>Ambiguous method call</b>). This method clears that up for the compiler.
   *
   * <p>NB this method is meant to wrap the {@code property} argument of a {@code Check.has()} or
   * {@code Check.notHas()} method, not the {@code test} argument. You only need to wrap one of the
   * arguments to disambiguate the call. It is a matter of taste which one you prefer.
   *
   * @param lambdaOrMethodReference A lambda or method reference
   * @param <T> The type of the subject of the {@code Relation}
   * @return The same {@code ObjIntRelation}
   */
  public static <T> IntFunction<T> toObj(IntFunction<T> lambdaOrMethodReference) {
    return lambdaOrMethodReference;
  }

  /**
   * (Not a check) Simply returns the specified {@code Function}. Use when passing a lambda or
   * method reference to the {@code Check.has()} and {@code Check.notHas()} methods. Because these
   * methods are heavily overloaded, the compiler may not be able to establish whether the lambda is
   * supposed to be an {@code Function} or, for example, an {@code IntFunction} (it will complain
   * about an <b>Ambiguous method call</b>). This method clears that up for the compiler.
   *
   * <p>NB this method is meant to wrap the {@code property} argument of a {@code Check.has()} or
   * {@code Check.notHas()} method, not the {@code test} argument. You only need to wrap one of the
   * arguments to disambiguate the call. It is a matter of taste which one you prefer.
   *
   * @param lambdaOrMethodReference A lambda or method reference
   * @param <T> The type of the subject of the {@code Relation}
   * @return The same {@code ObjIntRelation}
   */
  public static <T, R> Function<T, R> objToObj(Function<T, R> lambdaOrMethodReference) {
    return lambdaOrMethodReference;
  }

  /**
   * (Not a check) Simply returns the specified {@code IntUnaryOperator}. Use when passing a lambda
   * or method reference to the {@code Check.has()} and {@code Check.notHas()} methods. Because
   * these methods are heavily overloaded, the compiler may not be able to establish whether the
   * lambda is supposed to be an {@code IntUnaryOperator} or some other kind of function (it will
   * complain about an <b>Ambiguous method call</b>). This method clears that up for the compiler.
   *
   * <p>NB this method is meant to wrap the {@code property} argument of a {@code Check.has()} or
   * {@code Check.notHas()} method, not the {@code test} argument. You only need to wrap one of the
   * arguments to disambiguate the call. It is a matter of taste which one you prefer.
   *
   * @param lambdaOrMethodReference A lambda or method reference
   * @return The same {@code ObjIntRelation}
   */
  public static IntUnaryOperator intToInt(IntUnaryOperator lambdaOrMethodReference) {
    return lambdaOrMethodReference;
  }

  /* ++++++++++++++ END OF CHECKS ++++++++++++++ */

  static {
    MESSAGE_PATTERNS = Map.copyOf(tmp0);
    NAMES = Map.copyOf(tmp1);
    tmp0 = null;
    tmp1 = null;
  }

  private static void setMessagePattern(Object test, PrefabMsgFormatter message) {
    tmp0.put(test, message);
  }

  private static void setName(Object test, String name) {
    tmp1.put(test, name);
  }

}
