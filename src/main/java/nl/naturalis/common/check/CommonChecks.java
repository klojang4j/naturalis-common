package nl.naturalis.common.check;

import nl.naturalis.common.*;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.check.Check.fail;
import static nl.naturalis.common.check.Messages.*;

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
 * <p>
 * The {@code CommonChecks} also contains a few static exception factories that you can use in
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
public class CommonChecks {

    static final IdentityHashMap<Object, Formatter> MESSAGE_PATTERNS;
    static final IdentityHashMap<Object, String> NAMES;

    private static ArrayList<Tuple<Object, Formatter>> tmp0 = new ArrayList<>(50);
    private static ArrayList<Tuple<Object, String>> tmp1 = new ArrayList<>(50);

    private CommonChecks() {
    }

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
     * Verifies that the argument is {@code true}. If the argument is a {@code Boolean} rather than
     * a {@code boolean} and the argument might be null, use {@link #nullOr() nullOr(true)}.
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
     * Verifies that the argument is {@code false}. If the argument is a {@code Boolean} rather than
     * a {@code boolean} and the argument might be null, use {@link #nullOr() nullOr(false)}.
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
     * <p>This check performs an implicit null check, so can be safely executed without (or instead
     * of) executing the {@link #notNull()} check first.
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
     * Verifies that the argument is not null and, if it is an array, {@link Map} or {@link
     * Collection}, that it does not contain any null values. This check still allows the array, map
     * or collection to be empty (zero-sized). Use {@link #deepNotEmpty()} if you want to disallow
     * this, too.
     *
     * <p>This check performs an implicit null check, so can be safely executed without (or instead
     * of) executing the {@link #notNull()} check first.
     *
     * @param <T> The type of the argument
     * @return A {@code Predicate} implementing the check described above
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
     * {@link Collection}, that it does not contain any null or empty values. See {@link
     * ObjectMethods#isEmpty(Object) ObjectMethods::isEmpty} for what counts as an empty object.
     * This check does not test for circular references, so will probably crash your JVM (with a
     * {@link StackOverflowError}) if there are any.
     *
     * <p>This check performs an implicit null check, so can be safely executed without (or instead
     * of) executing the {@link #notNull()} check first.
     *
     * @param <T> The type of the argument
     * @return A {@code Predicate}
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
     * useful when called from an {@link Check#isNot(Predicate) isNot} method.
     *
     * <p>This check performs an implicit null check, so can be safely executed without (or instead
     * of) executing the {@link #notNull()} check first.
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
     * Verifies that a {@code String} argument represents a valid integer. Equivalent to {@link
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
     * Verifies that the argument is an array or, if the argument is a {@code Class} object, that is
     * an array type.
     *
     * @param <T> The type of the argument
     * @return A {@code Predicate} implementing the test described above
     */
    public static <T> Predicate<T> array() {
        return x -> x.getClass() == Class.class ? ((Class) x).isArray() : x.getClass().isArray();
    }

    static {
        setMessagePattern(array(), msgArray());
        setName(array(), "array");
    }

    /**
     * Verifies that the argument is an existing, regular file. Equivalent to {@link File#isFile()
     * File::isFile}.
     *
     * @return A {@code Predicate} implementing the test described above
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
     * @return A {@code Predicate} implementing the test described above
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
     * @return A {@code Predicate}
     */
    public static Predicate<File> onFileSystem() {
        return File::exists;
    }

    static {
        setMessagePattern(onFileSystem(), msgOnFileSystem());
        setName(onFileSystem(), "onFileSystem");
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
     * @return A {@code Predicate} implementing the test described above
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
     * @return A {@code Predicate} implementing the test described above
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
     * @return A {@code Predicate} implementing the test described above
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
     * @return A {@code Predicate} implementing the test described above
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
     * @return A {@code Predicate} implementing the test described above
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
     * @return A {@code Predicate} implementing the test described above establishing that the
     * argument is equal to zero
     */
    public static IntPredicate zero() {
        return x -> x == 0;
    }

    static {
        setMessagePattern(eq(), msgZero());
        setName(zero(), "zero");
    }

    /* ++++++++++++++ Relation ++++++++++++++ */

    /**
     * Verifies that the argument is an instance of the specified class or, if the argument is
     * itself a {@code Class} object, that it is a subclass or implementation of the specified
     * class.
     *
     * @param <X> The type of the argument
     * @return A {@code Relation} implementing the test described above
     */
    public static <X> Relation<X, Class<?>> instanceOf() {
        return (x, y) -> x.getClass() == Class.class ? y.isAssignableFrom(
                (Class<?>) x) : y.isInstance(x);
    }

    static {
        setMessagePattern(instanceOf(), msgInstanceOf());
        setName(instanceOf(), "instanceOf");
    }

    /**
     * Verifies that the specified {@code Collection} contains the specified value. Equivalent to
     * {@link Collection#contains(Object) Collection::contains}.
     *
     * @param <E> The type of the elements in the {@code Collection}
     * @param <C> The type of the collection
     * @return A {@code Relation} implementing the test described above
     */
    public static <E, C extends Collection<? super E>> Relation<C, E> containing() {
        return Collection::contains;
    }

    static {
        setMessagePattern(containing(), msgContaining());
        setName(containing(), "containing");
    }

    /**
     * Verifies the specified argument is contained in the specified {@code Collection}.
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
     * Verifies that the specified {@code Collection} argument is a superset of another {@code
     * Collection}. Equivalent to {@link Collection#containsAll(Collection)
     * Collection::containsAll}. Note that the collections <i>need not be</i> instances of the
     * {@link Set} interface:
     *
     * <blockquote>
     *
     * <pre>{@code
     * Check.that(Set.of(1, 2, 3).is(supersetOf(), List.of(1, 2)); // valid and true
     * Check.that(List.of(1, 2).is(supersetOf(), Set.of(1, 2, 3)); // valid but false
     * }</pre>
     *
     * </blockquote>
     *
     * @param <E>  The type of the elements in the {@code Collection}
     * @param <C0> The type of the argument (the subject of the {@code Relation})
     * @param <C1> The type of the object of the {@code Relation}
     * @return A {@code Relation}
     */
    public static <E, C0 extends Collection<? super E>, C1 extends Collection<? super E>> Relation<C0, C1> supersetOf() {
        return Collection::containsAll;
    }

    static {
        setMessagePattern(supersetOf(), msgSupersetOf());
        setName(supersetOf(), "supersetOf");
    }

    /**
     * Verifies that a {@code Collection} argument is a subset of another {@code Collection}.
     *
     * @param <E>  The type of the elements in the {@code Collection}
     * @param <C0> The type of the argument (the subject of the {@code Relation})
     * @param <C1> The type of the object of the {@code Relation}
     * @return A {@code Relation}
     */
    public static <E, C0 extends Collection<? super E>, C1 extends Collection<? super E>> Relation<C0, C1> subsetOf() {
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
     * Verifies that the argument is an element of the specified array. Equivalent to {@link
     * ArrayMethods#isElementOf(Object, Object[]) ArrayMethods::isElementOf}.
     *
     * @param <X> The type of the argument
     * @param <Y> The component type of the array
     * @return A {@code Relation}
     */
    public static <Y, X extends Y> Relation<X, Y[]> elementOf() {
        return ArrayMethods::isElementOf;
    }

    static {
        setMessagePattern(elementOf(), msgIn()); // recycle message
        setName(elementOf(), "elementOf");
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
     * @return A {@code Relation} implementing the test described above
     */
    public static Relation<String, String> equalsIgnoreCase() {
        return String::equalsIgnoreCase;
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
     * Verifies that a {@code Number} is greater than another {@code Number}, widening both to
     * {@code Double} before comparing them. Use when testing any type of numbers besides {@code
     * int} or {@code Integer}.
     *
     * @param <X> The type of the argument
     * @param <Y> The type of the value to compare the argument to
     * @return A {@code Relation}
     * @see #gt()
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
     * @param <X> The type of the argument
     * @param <Y> The type of the value to compare the argument to
     * @return A {@code Relation}
     * @see #gte()
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
     * @param <X> The type of the argument
     * @param <Y> The type of the value to compare the argument to
     * @return A {@code Relation}
     * @see #lt()
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
     * @param <X> The type of the argument
     * @param <Y> The type of the value to compare the argument to
     * @return A {@code Relation}
     * @see #lte()
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
    public static Relation<String, String> startsWith() {
        return String::startsWith;
    }

    static {
        setMessagePattern(startsWith(), msgStartsWith());
        setName(startsWith(), "startsWith");
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
    public static <T extends CharSequence> Relation<String, T> contains() {
        return String::contains;
    }

    static {
        setMessagePattern(contains(), msgContains());
        setName(contains(), "contains");
    }

    // Lookup for nRangeFrom()
    private static final Map<Class, Relation<?, Pair<?>>> m0 = Map.of(Integer.class,
                                                                      (x, y) -> (int) x >= (int) y.one() && (int) x < (int) y.two(),
                                                                      Long.class,
                                                                      (x, y) -> (long) x >= (long) y.one() && (long) x < (long) y.two(),
                                                                      Double.class,
                                                                      (x, y) -> (double) x >= (double) y.one() && (double) x < (double) y.two(),
                                                                      Float.class,
                                                                      (x, y) -> (float) x >= (float) y.one() && (float) x < (float) y.two(),
                                                                      Byte.class,
                                                                      (x, y) -> (byte) x >= (byte) y.one() && (byte) x < (byte) y.two(),
                                                                      Short.class,
                                                                      (x, y) -> (short) x >= (short) y.one() && (short) x < (short) y.two(),
                                                                      BigInteger.class, (x, y) -> {
                BigInteger bi0 = (BigInteger) x;
                BigInteger bi1 = (BigInteger) y.one();
                BigInteger bi2 = (BigInteger) y.two();
                return bi0.compareTo(bi1) >= 0 && bi0.compareTo(bi2) < 0;
            }, BigDecimal.class, (x, y) -> {
                BigDecimal bd0 = (BigDecimal) x;
                BigDecimal bd1 = (BigDecimal) y.one();
                BigDecimal bd2 = (BigDecimal) y.two();
                return bd0.compareTo(bd1) >= 0 && bd0.compareTo(bd2) < 0;
            });

    // Lookup for inRangeClosed()
    private static final Map<Class, Relation<?, Pair<?>>> m1 = Map.of(Integer.class,
                                                                      (x, y) -> (int) x >= (int) y.one() && (int) x <= (int) y.two(),
                                                                      Long.class,
                                                                      (x, y) -> (long) x >= (long) y.one() && (long) x <= (long) y.two(),
                                                                      Double.class,
                                                                      (x, y) -> (double) x >= (double) y.one() && (double) x <= (double) y.two(),
                                                                      Float.class,
                                                                      (x, y) -> (float) x >= (float) y.one() && (float) x <= (float) y.two(),
                                                                      Byte.class,
                                                                      (x, y) -> (byte) x >= (byte) y.one() && (byte) x <= (byte) y.two(),
                                                                      Short.class,
                                                                      (x, y) -> (short) x >= (short) y.one() && (short) x <= (short) y.two(),
                                                                      BigInteger.class, (x, y) -> {
                BigInteger bi0 = (BigInteger) x;
                BigInteger bi1 = (BigInteger) y.one();
                BigInteger bi2 = (BigInteger) y.two();
                return bi0.compareTo(bi1) >= 0 && bi0.compareTo(bi2) <= 0;
            }, BigDecimal.class, (x, y) -> {
                BigDecimal bd0 = (BigDecimal) x;
                BigDecimal bd1 = (BigDecimal) y.one();
                BigDecimal bd2 = (BigDecimal) y.two();
                return bd0.compareTo(bd1) >= 0 && bd0.compareTo(bd2) <= 0;
            });

    /**
     * Verifies that a number lies between two other numbers, the first one inclusive, the second
     * one exclusive.
     *
     * @return A {@code Relation} that implements the test described above
     */
    public static <T extends Number, U extends Number> Relation<T, Pair<T>> inRangeFrom() {
        return (x, y) -> ((Relation) m0.get(x.getClass())).exists(x, y);
    }

    static {
        setMessagePattern(inRangeFrom(), msgInRangeFrom());
        setName(inRangeFrom(), "inRangeFrom");
    }

    /**
     * Verifies that a number lies between two other numbers, both inclusive.
     *
     * @return A {@code Relation} that implements the test described above
     */
    public static <T extends Number, U extends Number> Relation<T, Pair<T>> inRangeClosed() {
        return (x, y) -> ((Relation) m1.get(x.getClass())).exists(x, y);
    }

    static {
        setMessagePattern(inRangeClosed(), msgInRangeClosed());
        setName(inRangeClosed(), "inRangeClosed");
    }

    /* ++++++++++++++ ObjIntRelation ++++++++++++++ */

    public static <T extends CharSequence> ObjIntRelation<T> strlenEquals() {
        return (x, y) -> x.length() == y;
    }

    static {
        setMessagePattern(strlenEquals(), msgEq()); // Recycle message
        setName(strlenEquals(), "strlenEquals");
    }

    public static <T extends CharSequence> ObjIntRelation<T> strlenNotEquals() {
        return (x, y) -> x.length() != y;
    }

    static {
        setMessagePattern(strlenNotEquals(), msgNe()); // Recycle message
        setName(strlenNotEquals(), "strlenNotEquals");
    }

    public static <T extends CharSequence> ObjIntRelation<T> strlenGreaterThan() {
        return (x, y) -> x.length() > y;
    }

    static {
        setMessagePattern(strlenGreaterThan(), msgGreaterThan()); // Recycle message
        setName(strlenGreaterThan(), "strlenGreaterThan");
    }

    public static <T extends CharSequence> ObjIntRelation<T> strlenAtLeast() {
        return (x, y) -> x.length() >= y;
    }

    static {
        setMessagePattern(strlenAtLeast(), msgGreaterThan()); // Recycle message
        setName(strlenAtLeast(), "strlenAtLeast");
    }

    public static <T extends CharSequence> ObjIntRelation<T> strlenLessThan() {
        return (x, y) -> x.length() < y;
    }

    static {
        setMessagePattern(strlenLessThan(), msgLessThan()); // Recycle message
        setName(strlenLessThan(), "strlenLessThan");
    }

    public static <T extends CharSequence> ObjIntRelation<T> strlenAtMost() {
        return (x, y) -> x.length() <= y;
    }

    static {
        setMessagePattern(strlenAtMost(), msgAtMost()); // Recycle message
        setName(strlenAtMost(), "strlenAtMost");
    }

    /* ++++++++++++++ IntObjRelation ++++++++++++++ */

    private static final String ERR_INDEX_OF = "Object of indexOf(), fromIndexOf() and toIndexOf() must be a List, a String or an array";

    /**
     * Verifies that the argument can be used as index into the specified array, string or list,
     * respectively. The object of the relationship is deliberately weakly typed to allow it to be
     * either a {@code List} or an array. or a {@code String} Specifying any other type of object
     * will, however, cause an {@link InvalidCheckException} to be thrown though.
     *
     * @return An {@code IntObjRelation} expressing this requirement
     */
    @SuppressWarnings("rawtypes")
    public static <T> IntObjRelation<T> indexOf() {
        return (x, y) -> {
            if (y instanceof List l) {
                return x >= 0 && x < l.size();
            } else if (y instanceof String s) {
                return x >= 0 && x < s.length();
            } else if (y instanceof Object[] s) {
                return x >= 0 && x < s.length;
            } else if (y.getClass().isArray()) {
                return x >= 0 && x < Array.getLength(y);
            }
            return fail(InvalidCheckException::new, ERR_INDEX_OF);
        };
    }

    static {
        setMessagePattern(indexOf(), msgIndexOf());
        setName(indexOf(), "indexOf");
    }

    /**
     * Verifies that the argument is a valid {@code from} index for operations like {@code
     * List.subList} and {@code String.substring}. For this type of operations the {@code from}
     * index may actually be one position past the end of the  {@code List}, {@code String}, etc.
     *
     * @return An {@code IntObjRelation}
     */
    public static <T> IntObjRelation<T> fromIndexOf() {
        return (x, y) -> {
            if (y instanceof List l) {
                return x >= 0 && x <= l.size();
            } else if (y instanceof String s) {
                return x >= 0 && x <= s.length();
            } else if (y instanceof Object[] s) {
                return x >= 0 && x <= s.length;
            } else if (y.getClass().isArray()) {
                return x >= 0 && x <= Array.getLength(y);
            }
            return fail(InvalidCheckException::new, ERR_INDEX_OF);
        };
    }

    static {
        setMessagePattern(fromIndexOf(), msgFromIndexOf());
        setName(fromIndexOf(), "fromIndexOf");
    }

    /**
     * Verifies that the argument is a valid {@code to} index for operations like {@code
     * List.subList} and {@code String.substring}. For this type of operations the {@code to} index
     * may actually be one position past the end of the  {@code List}, {@code String}, etc. This
     * method really just returns {@link #fromIndexOf()}, but you might find it more intuitive to
     * use when testing "to" indices.
     *
     * @return An {@code IntObjRelation}
     */
    public static <T> IntObjRelation<T> toIndexOf() {
        return fromIndexOf();
    }

    static {
        setMessagePattern(toIndexOf(), msgFromIndexOf()); // recycle message
        setName(toIndexOf(), "toIndexOf");
    }

    /**
     * Verifies that the argument is present in the specified array.
     *
     * @return
     */
    public static IntObjRelation<int[]> intElementOf() {
        return ArrayMethods::isElementOf;
    }

    static {
        setMessagePattern(intElementOf(), msgIn()); // recycle message
        setName(intElementOf(), "intElementOf");
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
     * Verifies that the argument is a whole multiple of a particular integer.
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
     * (Not a check) Shortcut for {@link IllegalStateException#IllegalStateException(String)
     * IllegalStateException::new}. Can be used in combination with {@link Check#on(Function,
     * Object) Check.on(...)}. For example: <code>Check.on(illegalState(),
     * out.isClosed()).is(no())</code>.
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
     * (Not a check) Shortcut for {@link UnsupportedOperationException#UnsupportedOperationException(String)
     * UnsupportedOperationException::new}.
     *
     * @return A {@code Function} that produces an {@code UnsupportedOperationException}
     */
    public static Function<String, UnsupportedOperationException> unsupportedOperation() {
        return UnsupportedOperationException::new;
    }

    /**
     * (Not a check) Simply returns the specified {@code IntPredicate}. You can use this method when
     * passing a lambda or method reference to the {@code Check.is()} and {@code Check.isNot()}
     * methods. The compiler will not be able to establish whether the lambda is supposed to be a
     * {@code Predicate} or {@code IntPredicate}. This method clears that up for the compiler. You
     * could also hard-cast the lambda to an {@code IntPredicate}, but this method, when statically
     * imported, is less verbose. Note that you will never have to use this method for any of the
     * checks in this class.
     *
     * @param lambdaOrMethodReference A lambda or method reference
     * @return The same {@code IntPredicate}
     * @see Check#is(Predicate)
     * @see Check#is(IntPredicate)
     */
    public static IntPredicate asInt(IntPredicate lambdaOrMethodReference) {
        return lambdaOrMethodReference;
    }

    /**
     * (Not a check) Simply returns the specified {@code Predicate}. You can use this method when
     * passing a lambda or method reference to the {@code Check.is()} and {@code Check.isNot()}
     * methods. The compiler will not be able to establish whether the lambda is supposed to be a
     * {@code Predicate} or {@code IntPredicate}. This method clears that up for the compiler. You
     * could also hard-cast the lambda to a {@code Predicate}, but this method, when statically
     * imported, is less verbose. Note that you will never have to use this method for any of the
     * checks in this class.
     *
     * @param lambdaOrMethodReference A lambda or method reference
     * @param <T>                     The type of the object being tested
     * @return The same {@code Predicate}
     * @see Check#is(Predicate)
     * @see Check#is(IntPredicate)
     */
    public static <T> Predicate<T> asObj(Predicate<T> lambdaOrMethodReference) {
        return lambdaOrMethodReference;
    }

    /**
     * (Not a check) Simply returns the specified {@code Relation}. You can use this method when
     * passing a lambda or method reference to the {@code Check.is()} and {@code Check.isNot()}
     * methods. The compiler will not be able to establish whether the lambda is supposed to be a
     * {@code Relation} or one of its sister interfaces. This method clears that up for the
     * compiler. You could also hard-cast the lambda to a {@code Relation}, but this method, when
     * statically imported, is less verbose. Note that you will never have to use this method for
     * any of the checks in this class.
     *
     * @param lambdaOrMethodReference A lambda or method reference
     * @param <T>                     The type of the subject of the {@code Relation}
     * @param <U>                     The type of the object of the {@code Relation}
     * @return The same {@code Relation}
     */
    public static <T, U> Relation<T, U> objObj(Relation<T, U> lambdaOrMethodReference) {
        return lambdaOrMethodReference;
    }

    /**
     * (Not a check) Simply returns the specified {@code ObjIntRelation}. You can use this method
     * when passing a lambda or method reference to the {@code Check.is()} and {@code Check.isNot()}
     * methods. The compiler will not be able to establish whether the lambda is supposed to be a
     * {@code Relation} or one of its sister interfaces. You could also hard-cast the lambda to an
     * {@code ObjIntRelation}, but this method, when statically imported, is less verbose. This
     * method clears that up for the compiler. Note that you will never have to use this method for
     * any of the checks in this class.
     *
     * @param lambdaOrMethodReference A lambda or method reference
     * @param <T>                     The type of the subject of the {@code Relation}
     * @return The same {@code ObjIntRelation}
     */
    public static <T> ObjIntRelation<T> objInt(ObjIntRelation<T> lambdaOrMethodReference) {
        return lambdaOrMethodReference;
    }

    /**
     * (Not a check) Simply returns the specified {@code IntObjRelation}. You can use this method
     * when passing a lambda or method reference to the {@code Check.is()} and {@code Check.isNot()}
     * methods. The compiler will not be able to establish whether the lambda is supposed to be a
     * {@code Relation} or one of its sister interfaces. This method clears that up for the
     * compiler. You could also hard-cast the lambda to an {@code IntObjRelation}, but this method,
     * when statically imported, is less verbose. Note that you will never have to use this method
     * for any of the checks in this class.
     *
     * @param lambdaOrMethodReference A lambda or method reference
     * @param <T>                     The type of the object of the {@code Relation}
     * @return The same {@code IntObjRelation}
     */
    public static <T> IntObjRelation<T> intObj(IntObjRelation<T> lambdaOrMethodReference) {
        return lambdaOrMethodReference;
    }

    /**
     * (Not a check) Simply returns the specified {@code IntObjRelation}. You can use this method
     * when passing a lambda or method reference to the {@code Check.is()} and {@code Check.isNot()}
     * methods. The compiler will not be able to establish whether the lambda is supposed to be a
     * {@code Relation} or one of its sister interfaces. This method clears that up for the
     * compiler. You could also hard-cast the lambda to an {@code IntRelation}, but this method,
     * when statically imported, is less verbose. Note that you will never have to use this method
     * for any of the checks in this class.
     *
     * @param lambdaOrMethodReference A lambda or method reference
     * @return The same {@code IntRelation}
     */
    public static IntRelation intInt(IntRelation lambdaOrMethodReference) {
        return lambdaOrMethodReference;
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
        return ifNotNull(NAMES.get(test), name -> name + suffix,
                         IntPredicate.class.getSimpleName());
    }

    static String nameOf(Relation<?, ?> test) {
        return ifNotNull(NAMES.get(test), name -> name + suffix, Relation.class.getSimpleName());
    }

    static String nameOf(IntRelation test) {
        return ifNotNull(NAMES.get(test), name -> name + suffix, IntRelation.class.getSimpleName());
    }

    static String nameOf(ObjIntRelation<?> test) {
        return ifNotNull(NAMES.get(test), name -> name + suffix,
                         ObjIntRelation.class.getSimpleName());
    }

    static String nameOf(IntObjRelation<?> test) {
        return ifNotNull(NAMES.get(test), name -> name + suffix,
                         IntObjRelation.class.getSimpleName());
    }

    private static void setMessagePattern(Object test, Formatter message) {
        tmp0.add(Tuple.of(test, message));
    }

    private static void setName(Object test, String name) {
        tmp1.add(Tuple.of(test, name));
    }
}
