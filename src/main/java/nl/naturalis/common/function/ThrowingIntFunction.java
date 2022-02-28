package nl.naturalis.common.function;

import java.util.function.IntFunction;

/**
 * An alternative to Java's {@link IntFunction} interface where the {@code apply} method is allowed
 * to throw a checked exception.
 *
 * @param <R> The type of the return value
 * @param <E> The type of the exception potentially being thrown
 * @author Ayco Holleman
 */
@FunctionalInterface
public interface ThrowingIntFunction<R, E extends Throwable> {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    R apply(int value);

}
