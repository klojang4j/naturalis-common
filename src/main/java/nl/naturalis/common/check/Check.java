package nl.naturalis.common.check;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.StringMethods;
import nl.naturalis.common.function.*;

import java.io.IOException;
import java.util.function.*;
import java.util.stream.IntStream;

import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.check.CommonChecks.NAMES;
import static nl.naturalis.common.check.CommonChecks.eq;
import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.Messages.createMessage;

/**
 * Facilitates precondition and postcondition checking. See {@linkplain nl.naturalis.common.check
 * package description}.
 *
 * @param <T> The type of the object being validated
 * @param <E> The type of exception thrown if the argument fails a test
 * @author Ayco Holleman
 */
public abstract class Check<T, E extends Exception> {

    static final String DEF_ARG_NAME = "argument";

    private static final Function<String, IllegalArgumentException> DEF_EXC_FACTORY =
            IllegalArgumentException::new;

    /**
     * Static factory method. Returns a new {@code Check} instance suitable for testing integers.
     *
     * @param arg The argument
     * @return A {@code Check} object suitable for testing integers
     */
    public static IntCheck<IllegalArgumentException> that(int arg) {
        return new IntCheck<>(arg, null, DEF_EXC_FACTORY);
    }

    /**
     * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
     * argument.
     *
     * @param <U> The type of the argument
     * @param arg The argument
     * @return A {@code Check} object suitable for testing the provided argument
     */
    public static <U> ObjectCheck<U, IllegalArgumentException> that(U arg) {
        return new ObjectCheck<>(arg, null, DEF_EXC_FACTORY);
    }

    /**
     * Static factory method. Returns a new {@code Check} instance suitable for testing integers.
     *
     * @param arg     The argument
     * @param argName The name of the argument
     * @return A new {@code Check} object
     */
    public static IntCheck<IllegalArgumentException> that(int arg, String argName) {
        return new IntCheck<>(arg, argName, DEF_EXC_FACTORY);
    }

    /**
     * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
     * argument.
     *
     * @param <U>     The type of the argument
     * @param arg     The argument
     * @param argName The name of the argument
     * @return A new {@code Check} object
     */
    public static <U> ObjectCheck<U, IllegalArgumentException> that(U arg, String argName) {
        return new ObjectCheck<>(arg, argName, DEF_EXC_FACTORY);
    }

    /**
     * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
     * argument. The argument will have already passed the {@link CommonChecks#notNull() notNull}
     * test.
     *
     * @param <U> The type of the argument
     * @param arg The argument
     * @return A new {@code Check} object
     */
    public static <U> ObjectCheck<U, IllegalArgumentException> notNull(U arg)
            throws IllegalArgumentException {
        if (arg == null) {
            String msg = createMessage(CommonChecks.notNull(), false, DEF_ARG_NAME, null);
            throw DEF_EXC_FACTORY.apply(msg);
        }
        return new ObjectCheck<>(arg, null, DEF_EXC_FACTORY);
    }

    /**
     * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
     * argument. The argument will have already passed the {@link CommonChecks#notNull() notNull}
     * test.
     *
     * @param <U>     The type of the argument
     * @param arg     The argument
     * @param argName The name of the argument
     * @return A new {@code Check} object
     */
    public static <U> ObjectCheck<U, IllegalArgumentException> notNull(U arg, String argName)
            throws IllegalArgumentException {
        if (arg == null) {
            String msg = createMessage(CommonChecks.notNull(), false, argName, null);
            throw DEF_EXC_FACTORY.apply(msg);
        }
        return new ObjectCheck<>(arg, argName, DEF_EXC_FACTORY);
    }

    /**
     * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
     * argument. The argument will have already passed the {@link CommonChecks#notNull() notNull}
     * test.
     *
     * @param excFactory A {@code Function} that takes a {@code String} (the error message) and
     *                   returns an {@code Exception}
     * @param arg        The argument
     * @param <U>        The type of the argument
     * @param <X>        The type of {@code Exception} thrown if the argument fails to pass a test
     * @return A new {@code Check} object
     * @throws X If the argument fails to pass the {@code notNull} test or any subsequent tests
     *           called on the returned {@code Check} object
     */
    public static <U, X extends Exception> ObjectCheck<U, X> notNull(Function<String, X> excFactory, U arg)
            throws X {
        if (arg == null) {
            String msg = createMessage(CommonChecks.notNull(), false, DEF_ARG_NAME, null);
            throw excFactory.apply(msg);
        }
        return new ObjectCheck<>(arg, null, excFactory);
    }

    /**
     * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
     * argument. The argument will have already passed the {@link CommonChecks#notNull() notNull}
     * test.
     *
     * @param excFactory A {@code Function} that will produce the exception if a test fails. The
     *                   {@code Function} takes a {@code String} (the error message) and returns the
     *                   {@code Exception}
     * @param arg        The argument
     * @param argName    The name of the argument
     * @param <U>        The type of the argument
     * @param <X>        The type of {@code Exception} thrown if the argument fails to pass a test
     * @return A new {@code Check} object
     * @throws X If the argument fails to pass the {@code notNull} test or any subsequent tests
     *           called on the returned {@code Check} object
     */
    public static <U, X extends Exception> ObjectCheck<U, X> notNull(
            Function<String, X> excFactory, U arg, String argName) throws X {
        if (arg == null) {
            String msg = createMessage(CommonChecks.notNull(), false, argName, null);
            throw excFactory.apply(msg);
        }
        return new ObjectCheck<>(arg, argName, excFactory);
    }

    /**
     * Static factory method. Returns a new {@code Check} instance suitable for testing integers.
     *
     * @param excFactory A {@code Function} that will produce the exception if a test fails. The
     *                   {@code Function} will be passed a {@code String} (the error message) and
     *                   must return the {@code Exception} to be thrown
     * @param arg        The argument
     * @param <X>        The type of {@code Exception} thrown if the argument fails to pass a test
     * @return A {@code Check} object suitable for testing {@code int} arguments
     */
    public static <X extends Exception> IntCheck<X> on(
            Function<String, X> excFactory, int arg) {
        return new IntCheck<>(arg, null, excFactory);
    }

    /**
     * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
     * argument.
     *
     * @param <U>        The type of the argument
     * @param <X>        The type of {@code Exception} thrown if the argument fails to pass a test
     * @param excFactory A {@code Function} that will produce the exception if a test fails. The
     *                   {@code Function} will be passed a {@code String} (the error message) and
     *                   must return the {@code Exception} to be thrown
     * @param arg        The argument
     * @return A {@code Check} object suitable for testing the provided argument
     */
    public static <U, X extends Exception> ObjectCheck<U, X> on(Function<String, X> excFactory, U arg) {
        return new ObjectCheck<>(arg, null, excFactory);
    }

    /**
     * Static factory method. Returns a new {@code Check} instance suitable for testing integers.
     *
     * @param excFactory A {@code Function} that will produce the exception if a test fails. The
     *                   {@code Function} will be passed a {@code String} (the error message) and
     *                   must return the {@code Exception} to be thrown
     * @param arg        The argument
     * @param argName    The name of the argument
     * @param <X>        The type of {@code Exception} thrown if the argument fails to pass a test
     * @return A new {@code Check} object
     */
    public static <X extends Exception> IntCheck<X> on(
            Function<String, X> excFactory, int arg, String argName) {
        return new IntCheck<>(arg, argName, excFactory);
    }

    /**
     * Static factory method. Returns a new {@code Check} instance suitable for testing the provided
     * argument.
     *
     * @param <U>        The type of the argument
     * @param <X>        The type of {@code Exception} thrown if the argument fails to pass a test
     * @param excFactory A {@code Function} that will produce the exception if a test fails. The
     *                   {@code Function} will be passed a {@code String} (the error message) and
     *                   must return the {@code Exception} to be thrown
     * @param arg        The argument
     * @param argName    The name of the argument
     * @return A new {@code Check} object
     */
    public static <U, X extends Exception> ObjectCheck<U, X> on(
            Function<String, X> excFactory, U arg, String argName) {
        return new ObjectCheck<>(arg, argName, excFactory);
    }

    public static void offsetAndLength(byte[] array, int off, int len) throws IOException {
        if (array == null) {
            throw new IOException("byte array must not be null");
        } else if (off < 0) {
            throw new IndexOutOfBoundsException("offset must not be negative");
        } else if (len < 0) {
            throw new IndexOutOfBoundsException("length must not be negative");
        } else if (off + len > array.length) {
            throw new IndexOutOfBoundsException("offset + length must be <= " + array.length);
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} with the specified message and message arguments.
     * The method is still declared to return a value of type &lt;U&gt; so it can be used as the
     * expression for a {@code return statement}.
     *
     * @param <U>     The type of the object that would have been returned if it had passed the
     *                checks
     * @param msg     The message
     * @param msgArgs The message argument
     * @return Nothing, but allows {@code fail} to be used as the expression in a {@code return}
     * statement
     */
    public static <U> U fail(String msg, Object... msgArgs) {
        return fail(DEF_EXC_FACTORY, msg, msgArgs);
    }

    /**
     * Throws the exception created by the specified exception factory.
     *
     * @param <U>        The type of the object that would have been returned if it had passed the
     *                   checks
     * @param <X>        The type of the exception
     * @param excFactory The exception supplier
     * @return Nothing, but allows {@code fail} to be used as the expression in a {@code return}
     * statement
     * @throws X The exception that is thrown
     */
    public static <U, X extends Exception> U fail(Function<String, X> excFactory) throws X {
        return fail(excFactory, StringMethods.EMPTY);
    }

    /**
     * Throws an exception created by the specified exception factory with the specified message and
     * message arguments.
     *
     * @param <U>     The type of the object that would have been returned if it had passed the
     *                checks
     * @param <X>     The type of the exception
     * @param msg     The message
     * @param msgArgs The message argument
     * @return Nothing, but allows {@code fail} to be used as the expression in a {@code return}
     * statement
     * @throws X The exception that is thrown
     */
    public static <U, X extends Exception> U fail(
            Function<String, X> excFactory, String msg, Object... msgArgs) throws X {
        return Check.on(excFactory, 1).is(eq(), 0, msg, msgArgs).ok(i -> null);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // Instance fields / methods start here
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    final String argName;
    final Function<String, E> excFactory;

    Check(String argName, Function<String, E> exceptionFactory) {
        this.argName = argName;
        this.excFactory = exceptionFactory;
    }

    /**
     * Verifies that the argument passes the test expressed through the specified {@code Predicate}.
     * Although not strictly required, this method is meant to be used with a {@code Predicate} from
     * the {@link CommonChecks} class so that an informative error message is generated if the
     * argument fails the test.
     *
     * <p>When providing a lambda or method reference, you will have to make it clear to the
     * compiler that it actually <i>is</i> a {@code Predicate}, because the compiler will not be
     * able to distinguish it from an {@code IntPredicate}. It will complain about an <b>Ambiguous
     * method call</b>. You can circumvent this in three ways:
     *
     * <ol>
     *   <li>Specify the type of the lambda parameter (not an option when providing a method
     *       reference)
     *   <li>Use the {@link CommonChecks#asObj asObj} utility method from the {@code CommonChecks}
     *       class
     *   <li>Cast the lambda or method reference to an {@code IntPredicate}
     * </ol>
     *
     * <blockquote>
     *
     * <pre>{@code
     * Check.that("abc").is(s -> s.endsWith("xyz")); // WON'T COMPILE !
     * Check.that("abc").is((String s) -> s.endsWith("xyz")); // Will compile
     * Check.that("abc").is(asObj(s -> s.endsWith("xyz"))); // Will compile
     * Check.that("abc").is((Predicate<String>) (s -> s.endsWith("xyz"))); // Verbose, but compiles
     * }</pre>
     *
     * <p>Note that you will never have to do any of this when using the checks from the {@code
     * CommonChecks} class.
     *
     * @param test A {@code Predicate} expressing the test
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public Check<T, E> is(Predicate<T> test) throws E {
        if (test.test(ok())) {
            return this;
        }
        String msg = createMessage(test, false, getArgName(ok()), ok());
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that the argument passes the test expressed through the specified {@code
     * IntPredicate}. Although not strictly required, this method is meant to be used with an {@code
     * IntPredicate} from the {@link CommonChecks} class so that an informative error message is
     * generated if the argument fails the test.
     *
     * <p>When providing a lambda or method reference, you will have to make it clear to the
     * compiler that it actually <i>is</i> a {@code Predicate}, because the compiler will not be
     * able to distinguish it from an {@code IntPredicate}. It will complain about an <b>Ambiguous
     * method call</b>. You can circumvent this in three ways:
     *
     * <ol>
     *   <li>Specify the type of the lambda parameter (not an option when providing a method
     *       reference)
     *   <li>Use the {@link CommonChecks#asInt(IntPredicate)} asInt} utility method from the {@code
     *       CommonChecks} class
     *   <li>Cast the lambda or method reference to an {@code IntPredicate}
     * </ol>
     *
     * <blockquote>
     *
     * <pre>{@code
     * Check.that(8).is(i -> i > 5); // WON'T COMPILE !
     * Check.that(8).is((int i) -> i > 5); // Will compile
     * Check.that(8).is(asInt(i -> i > 5)); // Will compile
     * Check.that(8).is((IntPredicate) (i -> i > 5)); // Verbose, but compiles
     * }</pre>
     *
     * <p>Note that you will never have to do any of this when using the checks from the {@code
     * CommonChecks} class.
     *
     * @param test An {@code IntPredicate} expressing the test
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public Check<T, E> is(IntPredicate test) throws E {
        if (test.test(intValue())) {
            return this;
        }
        String msg = createMessage(test, false, getArgName(intValue()), ok());
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that the argument ducks the test expressed through the specified {@code Predicate}.
     * Although not strictly required, this method is meant to be used with a {@code Predicate} from
     * the {@link CommonChecks} class so that an informative error message is generated if the
     * argument fails the test.
     *
     * @param test A {@code Predicate} expressing the test
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public Check<T, E> isNot(Predicate<T> test) throws E {
        if (!test.test(ok())) {
            return this;
        }
        String msg = createMessage(test, true, getArgName(ok()), ok());
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that the argument ducks the test expressed through the specified {@code Predicate}.
     * Although not strictly required, this method is meant to be used with a {@code Predicate} from
     * the {@link CommonChecks} class so that an informative error message is generated if the
     * argument fails the test.
     *
     * @param test A {@code Predicate} expressing the test
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public Check<T, E> isNot(IntPredicate test) throws E {
        if (!test.test(intValue())) {
            return this;
        }
        String msg = createMessage(test, true, argName, ok());
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that the argument passes the test expressed through the specified {@code Predicate}.
     * Allows you to provide a custom error message.
     *
     * @param test    A {@code Predicate} expressing the test
     * @param message The error message
     * @param msgArgs The message arguments
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public Check<T, E> is(Predicate<T> test, String message, Object... msgArgs) throws E {
        if (test.test(ok())) {
            return this;
        }
        throw exception(test, message, msgArgs);
    }

    /**
     * Verifies that the argument passes the test expressed through the specified {@code Predicate}.
     * Allows you to provide a custom error message.
     *
     * @param test    An {@code IntPredicate} expressing the test
     * @param message The error message
     * @param msgArgs The message arguments
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public Check<T, E> is(IntPredicate test, String message, Object... msgArgs) throws E {
        if (test.test(intValue())) {
            return this;
        }
        throw exception(test, message, msgArgs);
    }

    /**
     * Verifies that the argument ducks the test expressed through the specified {@code Predicate}.
     * Allows you to provide a custom error message.
     *
     * @param test    A {@code Predicate} expressing the test
     * @param message The error message
     * @param msgArgs The message arguments
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public Check<T, E> isNot(Predicate<T> test, String message, Object... msgArgs) throws E {
        return is(test.negate(), message, msgArgs);
    }

    /**
     * Verifies that the argument ducks the test expressed through the specified {@code Predicate}.
     * Allows you to provide a custom error message.
     *
     * @param test    An {@code IntPredicate} expressing the test
     * @param message The error message
     * @param msgArgs The message arguments
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public Check<T, E> isNot(IntPredicate test, String message, Object... msgArgs) throws E {
        return is(test.negate(), message, msgArgs);
    }

    /**
     * Verifies that the argument passes the test expressed through the specified {@code Relation}.
     * Although not strictly required, this method is meant to be used with a {@code Relation} from
     * the {@link CommonChecks} class so that an informative error message is generated if the
     * argument fails the test.
     *
     * <p>When providing a lambda or method reference, you will have to make it clear to the
     * compiler that it actually <i>is</i> a {@code Relation}, because the compiler will not be able
     * to distinguish its sister interfaces ({@link IntRelation}, etc.). It will complain about an
     * <b>Ambiguous method call</b>. You can circumvent this in three ways:
     *
     * <ol>
     *   <li>Specify the types of the lambda parameters (not an option when providing a method
     *       reference)
     *   <li>Use the {@link CommonChecks#objObj(Relation)} objObj} utility method from the {@code
     *       CommonChecks} class
     *   <li>Cast the lambda or method reference to a {@code Relation}
     * </ol>
     *
     * <blockquote>
     *
     * <pre>{@code
     * Check.that(map).is((x, y) -> x.containsKey(y), "Hello World"); // WON'T COMPILE !
     * Check.that(map).is(Map::containsKey, "Greeting");// WON'T COMPILE !
     * // use objObj method:
     * Check.that(map).is(objObj((x, y) -> x.containsKey(y)), "Greeting");
     * Check.that(map).is(objObj(Map::containsKey), "Greeting");
     * // Specify the type of the lambda parameters (verbose, but compiles):
     * Check.that(map).is((Map<String, Object> x, String y) -> x.containsKey(y), "Greeting");
     * // Cast the entire lambda or method reference (verbose, but compiles):
     * Check.that(map).is((Relation<Map<String, Object>, String>) Map::containsKey, "Greeting");
     * }</pre>
     *
     * <p>Note that you will never have to do any of this when using the checks from the {@code
     * CommonChecks} class.
     *
     * @param <U>    The type of the object of the relationship
     * @param test   The relation to verify between the argument (as the subject of the
     *               relationship) and the specified value (as the object of the relationship)
     * @param object The object of the relationship
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public <U> Check<T, E> is(Relation<T, U> test, U object) throws E {
        if (test.exists(ok(), object)) {
            return this;
        }
        String msg = createMessage(test, false, getArgName(ok()), ok(), object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that the argument ducks the test expressed through the specified {@code Relation}.
     * Although not strictly required, this method is meant to be used with a {@code Relation} from
     * the {@link CommonChecks} class so that an informative error message is generated if the
     * argument fails the test.
     *
     * @param <U>    The type of the object of the relationship
     * @param test   The relation to verify between the argument (as the subject of the
     *               relationship) and the specified value (as the object of the relationship)
     * @param object The object of the relationship
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public <U> Check<T, E> isNot(Relation<T, U> test, U object) throws E {
        if (!test.exists(ok(), object)) {
            return this;
        }
        String msg = createMessage(test, true, getArgName(ok()), ok(), object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that the argument passes the test expressed through the specified {@code Relation}.
     * Allows you to provide a custom error message.
     *
     * @param <U>     The type of the object of the relationship
     * @param test    The relation to verify between the argument (as the subject of the
     *                relationship) and the specified value (as the object of the relationship)
     * @param object  The object of the relationship
     * @param message The error message
     * @param msgArgs The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> is(Relation<T, U> test, U object, String message, Object... msgArgs)
            throws E {
        if (test.exists(ok(), object)) {
            return this;
        }
        throw exception(test, object, message, msgArgs);
    }

    /**
     * Verifies that the argument ducks the test expressed through the specified {@code Relation}.
     * Allows you to provide a custom error message.
     *
     * @param <U>     The type of the object of the relationship
     * @param test    The relation to verify between the argument (as the subject of the
     *                relationship) and the specified value (as the object of the relationship)
     * @param object  The object of the relationship
     * @param message The error message
     * @param msgArgs The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> isNot(Relation<T, U> test, U object, String message, Object... msgArgs)
            throws E {
        return is(test.negate(), object, message, msgArgs);
    }

    /**
     * Verifies that the argument passes the test expressed through the specified {@code
     * ObjIntRelation}. Although not strictly required, this method is meant to be used with an
     * {@code ObjIntRelation} from the {@link CommonChecks} class so that an informative error
     * message is generated if the argument fails the test.
     *
     * <p>When providing a lambda or method reference, you will have to make it clear to the
     * compiler that it actually <i>is</i> an {@code ObjIntRelation}, because the compiler will not
     * be able to distinguish its sister interfaces ({@link Relation}, etc.). It will complain about
     * an
     * <b>Ambiguous method call</b>. You can circumvent this in three ways:
     *
     * <ol>
     *   <li>Specify the types of the lambda parameters (not an option when providing a method
     *       reference)
     *   <li>Use the {@link CommonChecks#objInt(ObjIntRelation)} objInt} utility method from the
     *       {@code CommonChecks} class
     *   <li>Cast the lambda or method reference to an {@code ObjIntRelation}
     * </ol>
     *
     * <p>Note that you will never have to do any of this when using the checks from the {@code
     * CommonChecks} class.
     *
     * @param test   The relation to verify between the argument (as the subject of the
     *               relationship) and the specified value (as the object of the relationship)
     * @param object The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public Check<T, E> is(ObjIntRelation<T> test, int object) throws E {
        if (test.exists(ok(), object)) {
            return this;
        }
        String msg = createMessage(test, false, getArgName(ok()), ok(), object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that the argument ducks the test expressed through the specified {@code
     * ObjIntRelation}. Although not strictly required, this method is meant to be used with an
     * {@code ObjIntRelation} from the {@link CommonChecks} class so that an informative error
     * message is generated if the argument fails the test.
     *
     * @param test   The relation to verify between the argument (as the subject of the
     *               relationship) and the specified value (as the object of the relationship)
     * @param object The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public Check<T, E> isNot(ObjIntRelation<T> test, int object) throws E {
        if (!test.exists(ok(), object)) {
            return this;
        }
        String msg = createMessage(test, true, getArgName(ok()), ok(), object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that the argument passes the test expressed through the specified {@code
     * ObjIntRelation}. Allows you to provide a custom error message.
     *
     * @param test    The relation to verify between the argument (as the subject of the
     *                relationship) and the specified value (as the object of the relationship)
     * @param object  The object of the relationship
     * @param message The error message
     * @param msgArgs The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public Check<T, E> is(ObjIntRelation<T> test, int object, String message, Object... msgArgs)
            throws E {
        if (test.exists(ok(), object)) {
            return this;
        }
        throw exception(test, object, message, msgArgs);
    }

    /**
     * Verifies that the argument ducks the test expressed through the specified {@code
     * ObjIntRelation}. Allows you to provide a custom error message.
     *
     * @param test    The relation to verify between the argument (as the subject of the
     *                relationship) and the specified value (as the object of the relationship)
     * @param object  The object of the relationship
     * @param message The error message
     * @param msgArgs The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public Check<T, E> isNot(ObjIntRelation<T> test, int object, String message, Object... msgArgs)
            throws E {
        return is(test.negate(), object, message, msgArgs);
    }

    /**
     * Verifies that the argument passes the test expressed through the specified {@code
     * IntObjRelation}. Although not strictly required, this method is meant to be used with an
     * {@code IntObjRelation} from the {@link CommonChecks} class so that an informative error
     * message is generated if the argument fails the test.
     *
     * <p>When providing a lambda or method reference, you will have to make it clear to the
     * compiler that it actually <i>is</i> an {@code IntObjRelation}, because the compiler will not
     * be able to distinguish its sister interfaces ({@link Relation}, etc.). It will complain about
     * an
     * <b>Ambiguous method call</b>. You can circumvent this in three ways:
     *
     * <ol>
     *   <li>Specify the types of the lambda parameters (not an option when providing a method
     *       reference)
     *   <li>Use the {@link CommonChecks#intObj(IntObjRelation)} intObj} utility method from the
     *       {@code CommonChecks} class
     *   <li>Cast the lambda or method reference to an {@code IntObjRelation}
     * </ol>
     *
     * <p>Note that you will never have to do any of this when using the checks from the {@code
     * CommonChecks} class.
     *
     * @param <U>    The type of the object of the relationship
     * @param test   The relation to verify between the argument (as the subject of the
     *               relationship) and the specified value (as the object of the relationship)
     * @param object The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public abstract <U> Check<T, E> is(IntObjRelation<U> test, U object) throws E;

    /**
     * Verifies that the argument ducks the test expressed through the specified {@code
     * IntObjRelation}. Although not strictly required, this method is meant to be used with an
     * {@code IntObjRelation} from the {@link CommonChecks} class so that an informative error
     * message is generated if the argument fails the test.
     *
     * @param <U>    The type of the object of the relationship
     * @param test   The relation to verify between the argument (as the subject of the
     *               relationship) and the specified value (as the object of the relationship)
     * @param object The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public abstract <U> Check<T, E> isNot(IntObjRelation<U> test, U object) throws E;

    /**
     * Verifies that the argument passes the test expressed through the specified {@code
     * IntObjRelation}. Allows you to provide a custom error message.
     *
     * @param <U>     The type of the object of the relationship
     * @param test    The relation to verify between the argument (as the subject of the
     *                relationship) and the specified value (as the object of the relationship)
     * @param object  The object of the relationship
     * @param message The error message
     * @param msgArgs The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public abstract <U> Check<T, E> is(
            IntObjRelation<U> test, U object, String message, Object... msgArgs) throws E;

    /**
     * Verifies that the argument ducks the test expressed through the specified {@code
     * IntObjRelation}. Allows you to provide a custom error message.
     *
     * @param <U>     The type of the object of the relationship
     * @param test    The relation to verify between the argument (as the subject of the
     *                relationship) and the specified value (as the object of the relationship)
     * @param object  The object of the relationship
     * @param message The error message
     * @param msgArgs The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> isNot(IntObjRelation<U> test, U object, String message, Object... msgArgs)
            throws E {
        return is(test.negate(), object, message, msgArgs);
    }

    /**
     * Verifies that the argument passes the test expressed through the specified {@code
     * IntRelation}. Although not strictly required, this method is meant to be used with an {@code
     * IntRelation} from the {@link CommonChecks} class so that an informative error message is
     * generated if the argument fails the test.
     *
     * <p>When providing a lambda or method reference, you will have to make it clear to the
     * compiler that it actually <i>is</i> an {@code IntRelation}, because the compiler will not be
     * able to distinguish its sister interfaces ({@link Relation}, etc.). It will complain about
     * an
     * <b>Ambiguous method call</b>. You can circumvent this in three ways:
     *
     * <ol>
     *   <li>Specify the types of the lambda parameters (not an option when providing a method
     *       reference)
     *   <li>Use the {@link CommonChecks#intInt(IntRelation)} intInt} utility method from the {@code
     *       CommonChecks} class
     *   <li>Cast the lambda or method reference to an {@code IntRelation}
     * </ol>
     *
     * <p>Note that you will never have to do any of this when using the checks from the {@code
     * CommonChecks} class.
     *
     * @param test   The relation to verify between the argument (as the subject of the
     *               relationship) and the specified value (as the object of the relationship)
     * @param object The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public abstract Check<T, E> is(IntRelation test, int object) throws E;

    /**
     * Verifies that the argument ducks the test expressed through the specified {@code
     * IntRelation}. Although not strictly required, this method is meant to be used with an {@code
     * IntRelation} from the {@link CommonChecks} class so that an informative error message is
     * generated if the argument fails the test.
     *
     * @param test   The relation to verify between the argument (as the subject of the
     *               relationship) and the specified value (as the object of the relationship)
     * @param object The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public abstract Check<T, E> isNot(IntRelation test, int object) throws E;

    /**
     * Verifies that the argument passes the test expressed through the specified {@code
     * IntRelation}. Allows you to provide a custom error message.
     *
     * @param test    The relation to verify between the argument (as the subject of the
     *                relationship) and the specified value (as the object of the relationship)
     * @param object  The object of the relationship
     * @param message The error message
     * @param msgArgs The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public abstract Check<T, E> is(IntRelation test, int object, String message, Object... msgArgs)
            throws E;

    /**
     * Verifies that the argument ducks the test expressed through the specified {@code
     * IntRelation}. Allows you to provide a custom error message.
     *
     * @param test    The relation to verify between the argument (as the subject of the
     *                relationship) and the specified value (as the object of the relationship)
     * @param object  The object of the relationship
     * @param message The error message
     * @param msgArgs The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public Check<T, E> isNot(IntRelation test, int object, String message, Object... msgArgs)
            throws E {
        return is(test.negate(), object, message, msgArgs);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * passes the test expressed through the specified {@code Predicate}. Although not strictly
     * required, this method is meant to be used with a {@code Predicate} from the {@link
     * CommonChecks} class so that an informative error message is generated if the argument fails
     * the test.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param name     The name of the property
     * @param test     A {@code Predicate} expressing the test
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public <U> Check<T, E> has(Function<T, U> property, String name, Predicate<U> test) throws E {
        U value = property.apply(ok());
        if (test.test(value)) {
            return this;
        }
        String msg = createMessage(test, false, fqn(name), value);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * ducks the test expressed through the specified {@code Predicate}. Although not strictly
     * required, this method is meant to be used with a {@code Predicate} from the {{@link
     * CommonChecks} class so that an informative error message is generated if the argument fails
     * the test.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param name     The name of the property
     * @param test     A {@code Predicate} expressing the test
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public <U> Check<T, E> notHas(Function<T, U> property, String name, Predicate<U> test) throws E {
        U value = property.apply(ok());
        if (!test.test(value)) {
            return this;
        }
        String msg = createMessage(test, true, fqn(name), value);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * passes the test expressed through the specified {@code Predicate}. Although not strictly
     * required, this method is meant to be used with a {@code Predicate} from the {@link
     * CommonChecks} class <i>and</i> a {@code Function} from the {@link CommonGetters} class so
     * that an informative error message is generated if the argument fails the test.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     A {@code Predicate} expressing the test
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public <U> Check<T, E> has(Function<T, U> property, Predicate<U> test) throws E {
        U value = property.apply(ok());
        if (test.test(value)) {
            return this;
        }
        String name = formatProperty(getArgName(ok()), property);
        String msg = createMessage(test, false, name, value);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * ducks the test expressed through the specified {@code Predicate}. Although not strictly
     * required, this method is meant to be used with a {@code Predicate} from the {@link
     * CommonChecks} class <i>and</i> a {@code Function} from the {@link CommonGetters} class so
     * that an informative error message is generated if the argument fails the test.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     A {@code Predicate} expressing the test
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public <U> Check<T, E> notHas(Function<T, U> property, Predicate<U> test) throws E {
        U value = property.apply(ok());
        if (!test.test(value)) {
            return this;
        }
        String name = formatProperty(getArgName(ok()), property);
        String msg = createMessage(test, true, name, value);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * passes the test expressed through the specified {@code Predicate}. Allows you to provide a
     * custom error message.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param message  The error message
     * @param msgArgs  The message arguments
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public <U> Check<T, E> has(
            Function<T, U> property, Predicate<U> test, String message, Object... msgArgs) throws E {
        U value = property.apply(ok());
        if (test.test(value)) {
            return this;
        }
        throw exception(test, message, msgArgs);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * ducks the test expressed through the specified {@code Predicate}. Allows you to provide a
     * custom error message.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param message  The error message
     * @param msgArgs  The message arguments
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public <U> Check<T, E> notHas(
            Function<T, U> property, Predicate<U> test, String message, Object... msgArgs) throws E {
        return has(property, test.negate(), message, msgArgs);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code
     * ToIntFunction}, passes the test expressed through the specified {@code Predicate}. Although
     * not required this method is meant to be used with an {@code IntPredicate} from the {@link
     * CommonChecks} class so that an informative error message is generated if the argument fails
     * the test.
     *
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param name     The name of the property
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public Check<T, E> has(ToIntFunction<T> property, String name, IntPredicate test) throws E {
        int value = property.applyAsInt(ok());
        if (test.test(value)) {
            return this;
        }
        String msg = createMessage(test, false, fqn(name), value);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code
     * ToIntFunction}, ducks the test expressed through the specified {@code Predicate}. Although
     * not required this method is meant to be used with an {@code IntPredicate} from the {@link
     * CommonChecks} class so that an informative error message is generated if the argument fails
     * the test.
     *
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param name     The name of the property
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public Check<T, E> notHas(ToIntFunction<T> property, String name, IntPredicate test) throws E {
        int value = property.applyAsInt(ok());
        if (!test.test(value)) {
            return this;
        }
        String msg = createMessage(test, true, fqn(name), value);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code
     * ToIntFunction}, passes the test expressed through the specified {@code IntPredicate}.
     * Although not required this method is meant to be used with an {@code IntPredicate} from the
     * {@link CommonChecks} class <i>and</i> a {@code ToIntFunction} from the {@link CommonGetters}
     * class so that an informative error message is generated if the argument fails the test.
     *
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public Check<T, E> has(ToIntFunction<T> property, IntPredicate test) throws E {
        int value = property.applyAsInt(ok());
        if (test.test(value)) {
            return this;
        }
        String name = formatProperty(getArgName(intValue()), property);
        String msg = createMessage(test, false, name, value);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code
     * ToIntFunction}, ducks the test expressed through the specified {@code IntPredicate}. Although
     * not required this method is meant to be used with an {@code IntPredicate} from the {@link
     * CommonChecks} class <i>and</i> a {@code ToIntFunction} from the {@link CommonGetters} class
     * so that an informative error message is generated if the argument fails the test.
     *
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public Check<T, E> notHas(ToIntFunction<T> property, IntPredicate test) throws E {
        int value = property.applyAsInt(ok());
        if (!test.test(value)) {
            return this;
        }
        String name = formatProperty(getArgName(intValue()), property);
        String msg = createMessage(test, true, name, value);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code
     * ToIntFunction}, passes the test expressed through the specified {@code Predicate}. Allows you
     * to provide a custom error message.
     *
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param message  The error message
     * @param msgArgs  The message arguments
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public Check<T, E> has(
            ToIntFunction<T> property, IntPredicate test, String message, Object... msgArgs) throws E {
        int value = property.applyAsInt(ok());
        if (test.test(value)) {
            return this;
        }
        throw exception(test, message, msgArgs);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code
     * ToIntFunction}, ducks the test expressed through the specified {@code Predicate}. Allows you
     * to provide a custom error message.
     *
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param message  The error message
     * @param msgArgs  The message arguments
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public Check<T, E> notHas(
            ToIntFunction<T> property, IntPredicate test, String message, Object... msgArgs) throws E {
        return has(property, test.negate(), message, msgArgs);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * passes the test expressed through the specified {@code Predicate}. Although not strictly
     * required, this method is meant to be used with a {@code Relation} from the {@link
     * CommonChecks} class so that an informative error message is generated if the argument fails
     * the test.
     *
     * @param <U>      The type of the property
     * @param <V>      The type of the object of the relationship
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param name     The name of the property
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U, V> Check<T, E> has(Function<T, U> property, String name, Relation<U, V> test, V object)
            throws E {
        U value = property.apply(ok());
        if (test.exists(value, object)) {
            return this;
        }
        String msg = createMessage(test, false, fqn(name), value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * ducks the test expressed through the specified {@code Relation}. Although not strictly
     * required, this method is meant to be used with a {@code Relation} from the {@link
     * CommonChecks} class so that an informative error message is generated if the argument fails
     * the test.
     *
     * @param <U>      The type of the property
     * @param <V>      The type of the object of the relationship
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param name     The name of the property
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U, V> Check<T, E> notHas(
            Function<T, U> property, String name, Relation<U, V> test, V object) throws E {
        U value = property.apply(ok());
        if (!test.exists(value, object)) {
            return this;
        }
        String msg = createMessage(test, true, fqn(name), value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * passes the test expressed through the specified {@code Relation}. Although not strictly
     * required, this method is meant to be used with a {@code Relation} from the {@link
     * CommonChecks} class <i>and</i> a {@code Function} from the {@link CommonGetters} class so
     * that an informative error message is generated if the argument fails the test.
     *
     * @param <U>      The type of the property
     * @param <V>      The type of the object of the relationship
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U, V> Check<T, E> has(Function<T, U> property, Relation<U, V> test, V object) throws E {
        U value = property.apply(ok());
        if (test.exists(value, object)) {
            return this;
        }
        String name = formatProperty(getArgName(ok()), property);
        String msg = createMessage(test, false, name, value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * ducks the test expressed through the specified {@code Relation}. Although not strictly
     * required, this method is meant to be used with a {@code Relation} from the {@link
     * CommonChecks} class <i>and</i> a {@code Function} from the {@link CommonGetters} class so
     * that an informative error message is generated if the argument fails the test.
     *
     * @param <U>      The type of the property
     * @param <V>      The type of the object of the relationship
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U, V> Check<T, E> notHas(Function<T, U> property, Relation<U, V> test, V object)
            throws E {
        U value = property.apply(ok());
        if (!test.exists(value, object)) {
            return this;
        }
        String name = formatProperty(getArgName(ok()), property);
        String msg = createMessage(test, true, name, value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * passes the test expressed through the specified {@code Predicate}. Allows you to provide a
     * custom error message.
     *
     * @param <U>      The type of the property
     * @param <V>      The type of the object of the relationship
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @param message  The error message
     * @param msgArgs  The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U, V> Check<T, E> has(
            Function<T, U> property, Relation<U, V> test, V object, String message, Object... msgArgs)
            throws E {
        U value = property.apply(ok());
        if (test.exists(value, object)) {
            return this;
        }
        throw exception(test, object, message, msgArgs);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * ducks the test expressed through the specified {@code Predicate}. Allows you to provide a
     * custom error message.
     *
     * @param <U>      The type of the property
     * @param <V>      The type of the object of the relationship
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @param message  The error message
     * @param msgArgs  The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U, V> Check<T, E> notHas(
            Function<T, U> property, Relation<U, V> test, V object, String message, Object... msgArgs)
            throws E {
        return has(property, test.negate(), object, message, msgArgs);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * passes the test expressed through the specified {@code ObjIntRelation}. Although not strictly
     * required, this method is meant to be used with the {@link CommonChecks} class so that an
     * informative error message is generated if the argument fails the test.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param name     The name of the property
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> has(
            Function<T, U> property, String name, ObjIntRelation<U> test, int object) throws E {
        U value = property.apply(ok());
        if (test.exists(value, object)) {
            return this;
        }
        String msg = createMessage(test, false, fqn(name), value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * ducks the test expressed through the specified {@code ObjIntRelation}. Although not strictly
     * required, this method is meant to be used with the {@link CommonChecks} class so that an
     * informative error message is generated if the argument fails the test.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param name     The name of the property
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> notHas(
            Function<T, U> property, String name, ObjIntRelation<U> test, int object) throws E {
        U value = property.apply(ok());
        if (!test.exists(value, object)) {
            return this;
        }
        String msg = createMessage(test, true, fqn(name), value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * passes the test expressed through the specified {@code ObjIntRelation}. Although not strictly
     * required, this method is meant to be used with a {@code ObjIntRelation} from the {@link
     * CommonChecks} class <i>and</i> a {@code Function} from the {@link CommonGetters} class so
     * that an informative error message is generated if the argument fails the test.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> has(Function<T, U> property, ObjIntRelation<U> test, int object) throws E {
        U value = property.apply(ok());
        if (test.exists(value, object)) {
            return this;
        }
        String name = formatProperty(getArgName(ok()), property);
        String msg = createMessage(test, false, name, value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * ducks the test expressed through the specified {@code ObjIntRelation}. Although not strictly
     * required, this method is meant to be used with a {@code ObjIntRelation} from the {@link
     * CommonChecks} class <i>and</i> a {@code Function} from the {@link CommonGetters} class so
     * that an informative error message is generated if the argument fails the test.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> notHas(Function<T, U> property, ObjIntRelation<U> test, int object)
            throws E {
        U value = property.apply(ok());
        if (!test.exists(value, object)) {
            return this;
        }
        String name = formatProperty(getArgName(ok()), property);
        String msg = createMessage(test, true, name, value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified function, passes
     * the test expressed through the specified {@code ObjIntRelation}. Allows you to provide a
     * custom error message.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @param message  The error message
     * @param msgArgs  The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> has(
            Function<T, U> property,
            ObjIntRelation<U> test,
            int object,
            String message,
            Object... msgArgs)
            throws E {
        U value = property.apply(ok());
        if (test.exists(value, object)) {
            return this;
        }
        throw exception(test, object, message, msgArgs);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified function, ducks the
     * test expressed through the specified {@code ObjIntRelation}. Allows you to provide a custom
     * error message.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @param message  The error message
     * @param msgArgs  The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> notHas(
            Function<T, U> property,
            ObjIntRelation<U> test,
            int object,
            String message,
            Object... msgArgs)
            throws E {
        return has(property, test, object, message, msgArgs);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * passes the test expressed through the specified {@code ObjIntRelation}. Although not strictly
     * required, this method is meant to be used with the {@link CommonChecks} class so that an
     * informative error message is generated if the argument fails the test.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param name     The name of the property
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> has(
            ToIntFunction<T> property, String name, IntObjRelation<U> test, U object) throws E {
        int value = property.applyAsInt(ok());
        if (test.exists(value, object)) {
            return this;
        }
        String msg = createMessage(test, false, fqn(name), value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * ducks the test expressed through the specified {@code ObjIntRelation}. Although not strictly
     * required, this method is meant to be used with the {@link CommonChecks} class so that an
     * informative error message is generated if the argument fails the test.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param name     The name of the property
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> notHas(
            ToIntFunction<T> property, String name, IntObjRelation<U> test, U object) throws E {
        int value = property.applyAsInt(ok());
        if (!test.exists(value, object)) {
            return this;
        }
        String msg = createMessage(test, true, fqn(name), value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * passes the test expressed through the specified {@code ObjIntRelation}. Although not strictly
     * required, this method is meant to be used with a {@code ObjIntRelation} from the {@link
     * CommonChecks} class <i>and</i> a {@code Function} from the {@link CommonGetters} class so
     * that an informative error message is generated if the argument fails the test.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> has(ToIntFunction<T> property, IntObjRelation<U> test, U object) throws E {
        int value = property.applyAsInt(ok());
        if (test.exists(value, object)) {
            return this;
        }
        String name = formatProperty(getArgName(ok()), property);
        String msg = createMessage(test, false, name, value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code Function},
     * ducks the test expressed through the specified {@code ObjIntRelation}. Although not strictly
     * required, this method is meant to be used with a {@code ObjIntRelation} from the {@link
     * CommonChecks} class <i>and</i> a {@code Function} from the {@link CommonGetters} class so
     * that an informative error message is generated if the argument fails the test.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> notHas(ToIntFunction<T> property, IntObjRelation<U> test, U object)
            throws E {
        int value = property.applyAsInt(ok());
        if (!test.exists(value, object)) {
            return this;
        }
        String name = formatProperty(getArgName(ok()), property);
        String msg = createMessage(test, true, name, value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified function, passes
     * the test expressed through the specified {@code ObjIntRelation}. Allows you to provide a
     * custom error message.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @param message  The error message
     * @param msgArgs  The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> has(
            ToIntFunction<T> property,
            IntObjRelation<U> test,
            U object,
            String message,
            Object... msgArgs)
            throws E {
        int value = property.applyAsInt(ok());
        if (test.exists(value, object)) {
            return this;
        }
        throw exception(test, object, message, msgArgs);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified function, ducks the
     * test expressed through the specified {@code ObjIntRelation}. Allows you to provide a custom
     * error message.
     *
     * @param <U>      The type of the property
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @param message  The error message
     * @param msgArgs  The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public <U> Check<T, E> notHas(
            ToIntFunction<T> property,
            IntObjRelation<U> test,
            U object,
            String message,
            Object... msgArgs)
            throws E {
        return has(property, test, object, message, msgArgs);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code
     * ToIntFunction}, passes the test expressed through the specified {@code IntRelation}. Although
     * not required this method is meant to be used with the {@link CommonChecks} class so that an
     * informative error message is generated if the argument fails the test.
     *
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param name     The name of the property
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public Check<T, E> has(ToIntFunction<T> property, String name, IntRelation test, int object)
            throws E {
        int value = property.applyAsInt(ok());
        if (test.exists(value, object)) {
            return this;
        }
        String msg = createMessage(test, false, fqn(name), value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code
     * ToIntFunction}, ducks the test expressed through the specified {@code IntRelation}. Although
     * not required this method is meant to be used with the {@link CommonChecks} class so that an
     * informative error message is generated if the argument fails the test.
     *
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param name     The name of the property
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public Check<T, E> notHas(ToIntFunction<T> property, String name, IntRelation test, int object)
            throws E {
        int value = property.applyAsInt(ok());
        if (!test.exists(value, object)) {
            return this;
        }
        String msg = createMessage(test, true, fqn(name), value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code
     * ToIntFunction}, passes the test expressed through the specified {@code IntRelation}. Although
     * not required this method is meant to be used with a {@code IntRelation} from the {@link
     * CommonChecks} class <i>and</i> a {@code ToIntFunction} from the {@link CommonGetters} class
     * so that an informative error message is generated if the argument fails the test.
     *
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public Check<T, E> has(ToIntFunction<T> property, IntRelation test, int object) throws E {
        int value = property.applyAsInt(ok());
        if (test.exists(value, object)) {
            return this;
        }
        String name = formatProperty(getArgName(intValue()), property);
        String msg = createMessage(test, false, name, value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified {@code
     * ToIntFunction}, ducks the test expressed through the specified {@code IntRelation}. Although
     * not required this method is meant to be used with a {@code IntRelation} from the {@link
     * CommonChecks} class <i>and</i> a {@code ToIntFunction} from the {@link CommonGetters} class
     * so that an informative error message is generated if the argument fails the test.
     *
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public Check<T, E> notHas(ToIntFunction<T> property, IntRelation test, int object) throws E {
        int value = property.applyAsInt(ok());
        if (!test.exists(value, object)) {
            return this;
        }
        String name = formatProperty(getArgName(intValue()), property);
        String msg = createMessage(test, true, name, value, object);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified function, passes
     * the test expressed through the specified {@code IntRelation}. Allows you to provide a custom
     * error message.
     *
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @param message  The error message
     * @param msgArgs  The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public Check<T, E> has(
            ToIntFunction<T> property, IntRelation test, int object, String message, Object... msgArgs)
            throws E {
        int value = property.applyAsInt(ok());
        if (test.exists(value, object)) {
            return this;
        }
        String msg = String.format(message, msgArgs);
        throw excFactory.apply(msg);
    }

    /**
     * Verifies that a property of the argument, retrieved through the specified function, ducks the
     * test expressed through the specified {@code IntRelation}. Allows you to provide a custom
     * error message.
     *
     * @param property A function which is given the argument as input and returns the value to be
     *                 tested
     * @param test     The relation to verify between the property (as the subject of the
     *                 relationship) and the specified value (as the object of the relationship)
     * @param object   The object of the relationship
     * @param message  The error message
     * @param msgArgs  The message arguments
     * @return This {@code Check} object
     * @throws E If the specified test does not exist between subject and object
     */
    public Check<T, E> notHas(
            ToIntFunction<T> property, IntRelation test, int object, String message, Object... msgArgs)
            throws E {
        return has(property, test.negate(), object, message, msgArgs);
    }

    /**
     * Returns the argument. To be used as the last call after a chain of checks. For example:
     *
     * <blockquote>
     *
     * <pre>{@code
     * int age = Check.that(person).has(Person::getAge, "age", lt(), 50).ok().getAge();
     * }</pre>
     *
     * </blockquote>
     *
     * @return The argument
     */
    public abstract T ok();

    /**
     * Passes the argument to the specified {@code Function} and returns the value it computes. To
     * be used as the last call after a chain of checks. For example:
     *
     * <blockquote>
     *
     * <pre>{@code
     * int age = Check.that(person).has(Person::getAge, "age", lt(), 50).ok(Person::getAge);
     * }</pre>
     *
     * </blockquote>
     *
     * @param <U>         The type of the returned value
     * @param transformer A {@code Function} that transforms the argument into some other value
     * @return The value computed by the {@code Function}
     * @throws F The exception potentially thrown by the {@code Function}
     */
    public <U, F extends Throwable> U ok(ThrowingFunction<T, U, F> transformer) throws F {
        return transformer.apply(ok());
    }

    /**
     * Passes the validated argument to the specified {@code Consumer}. To be used as the last call
     * after a chain of checks.
     *
     * @param consumer The {@code Consumer}
     */
    public <F extends Throwable> void then(ThrowingConsumer<T, F> consumer) throws F {
        consumer.accept(ok());
    }

    /**
     * Returns the argument being tested as an {@code int}. To be used as the last call after a
     * chain of checks. If the argument being tested actually is an {@code int} (rather than an
     * {@code Integer}), this method saves the cost of a boxing-unboxing round trip incurred by
     * {@link #ok()}.
     *
     * @return The argument cast or converted to an {@code int}
     * @see NumberMethods#fitsInto(Number, Class)
     */
    public abstract int intValue() throws E;

    /**
     * Passes the validated argument to the specified {@code Function} and returns the value
     * computed by the {@code Function}. To be used as the last call after a chain of checks.
     *
     * @param transformer An {@code IntFunction} that transforms the argument into some other value
     */
    public <U> U intValue(IntFunction<U> transformer) throws E {
        return transformer.apply(intValue());
    }

    E exception(Object test, String msg, Object[] msgArgs) {
        return exception(test, null, msg, msgArgs);
    }

    E exception(Object test, Object object, String msg, Object[] msgArgs) {
        return exception(test, ok(), object, msg, msgArgs);
    }

    E exception(Object test, Object subject, Object object, String pattern, Object[] msgArgs) {
        if (pattern == null) {
            throw new InvalidCheckException("message must not be null");
        }
        if (msgArgs == null) {
            throw new InvalidCheckException("message arguments must not be null");
        }
        String fmt = FormatNormalizer.normalize(pattern);
        Object[] all = new Object[msgArgs.length + 5];
        all[0] = NAMES.getOrDefault(test, test.getClass().getSimpleName());
        all[1] = Messages.toStr(subject);
        all[2] = ifNotNull(subject, Check::className);
        all[3] = argName;
        all[4] = Messages.toStr(object);
        System.arraycopy(msgArgs, 0, all, 5, msgArgs.length);
        return excFactory.apply(String.format(fmt, all));
    }

    String getArgName(Object arg) {
        return argName != null ? argName : arg != null ? className(arg) : DEF_ARG_NAME;
    }

//    String getArgName(int arg) {
//        return argName != null ? argName : int.class.getSimpleName();
//    }

    private static String className(Object obj) {
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            Class<?> c = clazz.getComponentType();
            int i = 0;
            for (; c.isArray(); c = c.getComponentType()) {
                ++i;
            }
            StringBuilder sb = new StringBuilder(c.getSimpleName());
            IntStream.rangeClosed(0, i).forEach(x -> sb.append("[]"));
            return sb.toString();
        }
        return clazz.getSimpleName();
    }

    /* Returns fully-qualified name of the property with the specified name */
    private String fqn(String name) {
        return argName + "." + name;
    }
}
