package nl.naturalis.common.check;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.function.*;

import java.util.function.*;
import java.util.stream.IntStream;

import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.check.CommonChecks.NAMES;
import static nl.naturalis.common.check.CommonGetters.formatProperty;
import static nl.naturalis.common.check.Messages.createMessage;
import static nl.naturalis.common.check.Check.*;


public final class IntCheck<E extends Exception> {

    private final int arg;
    private final String argName;
    private final Function<String, E> excFactory;


    IntCheck(int arg, String argName, Function<String, E> excFactory) {
        this.arg = arg;
        this.argName = argName;
        this.excFactory = excFactory;
    }


    public <U> IntCheck<E> is(IntObjRelation<U> test, U object) throws E {
        if (test.exists(arg, object)) {
            return this;
        }
        String msg = createMessage(test, false, getArgName(arg), arg, object);
        throw excFactory.apply(msg);
    }


    public <U> IntCheck<E> isNot(IntObjRelation<U> test, U object) throws E {
        if (!test.exists(arg, object)) {
            return this;
        }
        String msg = createMessage(test, true, argName, getArgName(arg), object);
        throw excFactory.apply(msg);
    }


    public <U> IntCheck<E> is(
            IntObjRelation<U> test, U object, String message, Object... msgArgs) throws E {
        if (test.exists(arg, object)) {
            return this;
        }
        String msg = String.format(message, msgArgs);
        throw excFactory.apply(msg);
    }


    public IntCheck<E> is(IntRelation test, int object) throws E {
        if (test.exists(arg, object)) {
            return this;
        }
        String msg = createMessage(test, false, getArgName(arg), arg, object);
        throw excFactory.apply(msg);
    }


    public IntCheck<E> isNot(IntRelation test, int object) throws E {
        if (!test.exists(arg, object)) {
            return this;
        }
        String msg = createMessage(test, true, getArgName(arg), arg, object);
        throw excFactory.apply(msg);
    }


    public IntCheck<E> is(IntRelation test, int object, String message, Object... msgArgs) throws E {
        if (test.exists(arg, object)) {
            return this;
        }
        String msg = String.format(message, msgArgs);
        throw excFactory.apply(msg);
    }


    public Integer ok() {
        return arg;
    }


    public int intValue() {
        return arg;
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
    public IntCheck<E> is(IntPredicate test) throws E {
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
    public IntCheck<E> isNot(IntPredicate test) throws E {
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
     * @param test    An {@code IntPredicate} expressing the test
     * @param message The error message
     * @param msgArgs The message arguments
     * @return This {@code Check} object
     * @throws E If the test fails
     */
    public IntCheck<E> is(IntPredicate test, String message, Object... msgArgs) throws E {
        if (test.test(intValue())) {
            return this;
        }
        throw exception(test, message, msgArgs);
    }

    public <U> IntCheck<E> is(Relation<Integer, U> test, U object) throws E {
        if (test.exists(arg, object)) {
            return this;
        }
        String msg = createMessage(test, false, getArgName(intValue()), ok(), object);
        throw excFactory.apply(msg);
    }


    public <U> IntCheck<E> isNot(Relation<Integer, U> test, U object) throws E {
        if (!test.exists(arg, object)) {
            return this;
        }
        String msg = createMessage(test, true, getArgName(intValue()), ok(), object);
        throw excFactory.apply(msg);
    }

    public <U> IntCheck<E> is(Relation<Integer, U> test, U object, String message, Object... msgArgs) throws E {
        if (test.exists(arg, object)) {
            return this;
        }
        throw exception(test, message, msgArgs);
    }

    public <U> IntCheck<E> isNot(Relation<Integer, U> test, U object, String message, Object... msgArgs) throws E {
        return is(test.negate(), object, message, msgArgs);
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
    public IntCheck<E> isNot(IntPredicate test, String message, Object... msgArgs) throws E {
        return is(test.negate(), message, msgArgs);
    }

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
    public <U> IntCheck<E> isNot(IntObjRelation<U> test, U object, String message, Object... msgArgs)
            throws E {
        return is(test.negate(), object, message, msgArgs);
    }

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
    public IntCheck<E> isNot(IntRelation test, int object, String message, Object... msgArgs)
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
    public <U> IntCheck<E> has(IntFunction<U> property, String name, Predicate<U> test) throws E {
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
    public <U> IntCheck<E> notHas(IntFunction<U> property, String name, Predicate<U> test) throws E {
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
    public <U> IntCheck<E> has(IntFunction<U> property, Predicate<U> test) throws E {
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
    public <U> IntCheck<E> notHas(IntFunction<U> property, Predicate<U> test) throws E {
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
    public <U> IntCheck<E> has(
            IntFunction<U> property, Predicate<U> test, String message, Object... msgArgs) throws E {
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
    public <U> IntCheck<E> notHas(
            IntFunction<U> property, Predicate<U> test, String message, Object... msgArgs) throws E {
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
    public IntCheck<E> has(IntUnaryOperator property, String name, IntPredicate test) throws E {
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
    public IntCheck<E> notHas(IntUnaryOperator property, String name, IntPredicate test) throws E {
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
    public IntCheck<E> has(IntUnaryOperator property, IntPredicate test) throws E {
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
    public IntCheck<E> notHas(IntUnaryOperator property, IntPredicate test) throws E {
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
    public IntCheck<E> has(
            IntUnaryOperator property, IntPredicate test, String message, Object... msgArgs) throws E {
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
    public IntCheck<E> notHas(
            IntUnaryOperator property, IntPredicate test, String message, Object... msgArgs) throws E {
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
    public <U, V> IntCheck<E> has(IntFunction<U> property, String name, Relation<U, V> test, V object)
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
    public <U, V> IntCheck<E> notHas(
            IntFunction<U> property, String name, Relation<U, V> test, V object) throws E {
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
    public <U, V> IntCheck<E> has(IntFunction<U> property, Relation<U, V> test, V object) throws E {
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
    public <U, V> IntCheck<E> notHas(IntFunction<U> property, Relation<U, V> test, V object)
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
    public <U, V> IntCheck<E> has(
            IntFunction<U> property, Relation<U, V> test, V object, String message, Object... msgArgs)
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
    public <U, V> IntCheck<E> notHas(
            IntFunction<U> property, Relation<U, V> test, V object, String message, Object... msgArgs)
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
    public <U> IntCheck<E> has(
            IntFunction<U> property, String name, ObjIntRelation<U> test, int object) throws E {
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
    public <U> IntCheck<E> notHas(
            IntFunction<U> property, String name, ObjIntRelation<U> test, int object) throws E {
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
    public <U> IntCheck<E> has(IntFunction<U> property, ObjIntRelation<U> test, int object) throws E {
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
    public <U> IntCheck<E> notHas(IntFunction<U> property, ObjIntRelation<U> test, int object)
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
    public <U> IntCheck<E> has(
            IntFunction<U> property,
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
    public <U> IntCheck<E> notHas(
            IntFunction<U> property,
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
    public <U> IntCheck<E> has(
            IntUnaryOperator property, String name, IntObjRelation<U> test, U object) throws E {
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
    public <U> IntCheck<E> notHas(
            IntUnaryOperator property, String name, IntObjRelation<U> test, U object) throws E {
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
    public <U> IntCheck<E> has(IntUnaryOperator property, IntObjRelation<U> test, U object) throws E {
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
    public <U> IntCheck<E> notHas(IntUnaryOperator property, IntObjRelation<U> test, U object)
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
    public <U> IntCheck<E> has(
            IntUnaryOperator property,
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
    public <U> IntCheck<E> notHas(
            IntUnaryOperator property,
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
    public IntCheck<E> has(IntUnaryOperator property, String name, IntRelation test, int object)
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
    public IntCheck<E> notHas(IntUnaryOperator property, String name, IntRelation test, int object)
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
    public IntCheck<E> has(IntUnaryOperator property, IntRelation test, int object) throws E {
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
    public IntCheck<E> notHas(IntUnaryOperator property, IntRelation test, int object) throws E {
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
    public IntCheck<E> has(
            IntUnaryOperator property, IntRelation test, int object, String message, Object... msgArgs)
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
    public IntCheck<E> notHas(
            IntUnaryOperator property, IntRelation test, int object, String message, Object... msgArgs)
            throws E {
        return has(property, test.negate(), object, message, msgArgs);
    }

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
    public <U, F extends Throwable> U ok(ThrowingIntFunction<U, F> transformer) throws F {
        return transformer.apply(ok());
    }

    /**
     * Passes the validated argument to the specified {@code Consumer}. To be used as the last call
     * after a chain of checks.
     *
     * @param consumer The {@code Consumer}
     */
    public <F extends Throwable> void then(ThrowingIntConsumer<F> consumer) throws F {
        consumer.accept(ok());
    }

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
        all[2] = ifNotNull(subject, IntCheck::className);
        all[3] = argName;
        all[4] = Messages.toStr(object);
        System.arraycopy(msgArgs, 0, all, 5, msgArgs.length);
        return excFactory.apply(String.format(fmt, all));
    }

    String getArgName(Object arg) {
        return argName != null ? argName : arg != null ? className(arg) : DEF_ARG_NAME;
    }

    String getArgName(int arg) {
        return argName != null ? argName : int.class.getSimpleName();
    }

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
