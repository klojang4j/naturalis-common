package nl.naturalis.common.check;

import static nl.naturalis.common.check.Range.BoundaryOperator.*;
import static nl.naturalis.common.check.Range.*;

/**
 * Interface for defining an integer range.
 */
public sealed interface Range permits From, Closed, Inside, Offset {

    /**
     * Symbolic constants for the operators to be used for bounds checking.
     */
    enum BoundaryOperator {
        LT("<"), LTE("<="), GT(">"), GTE(">=");

        private final String symbol;

        BoundaryOperator(String symbol) {
            this.symbol = symbol;
        }

        /**
         * Returns the Java symbol for the operator/
         *
         * @return The Java symbol for the operator
         */
        public String symbol() {
            return symbol;
        }

        /**
         * Returns the Java symbol for the operator/
         *
         * @return The Java symbol for the operator
         */
        public String toString() {
            return symbol;
        }
    }

    /**
     * Returns the lower bound of the range. The element sitting on this boundary may or may not
     * itself be part of the range, depending on how the boundaries are interpreted in this {@code
     * Range} implementation.
     *
     * @return The lower bound of the range
     */
    int getDeclaredLowerBound();

    /**
     * Returns the upper bound of the range. The element sitting on this boundary may or may not
     * itself be part of the range, depending on how the boundaries are interpreted this {@code
     * Range} implementation.
     *
     * @return The upper bound of the range
     */
    int getDeclaredUpperBound();

    /**
     * Returns the smallest integer that is still part of the range.
     *
     * @return The smallest integer that is still part of the range
     */
    int getLowerBoundInclusive();

    /**
     * Returns the highest integer that is still part of the range.
     *
     * @return The highest integer that is still part of the range
     */
    int getUpperBoundInclusive();

    /**
     * Returns the comparison operator that must in order to determine if a given integer belongs to
     * the range.
     *
     * @return The operator to be used for the lower bound
     */
    BoundaryOperator getLowerBoundOperator();

    /**
     * Returns the comparison operator that must in order to determine if a given integer belongs to
     * the range.
     *
     * @return The operator to be used for the upper bound
     */
    BoundaryOperator getUpperBoundOperator();

    /**
     * The quintessential method of this interface: does an integer belong to the range or does it
     * not, given how the boundaries are interpreted by this {@code Range} implementation.
     *
     * @param i The integer to test
     * @return Whether it belongs to this {@code Range}.
     */
    default boolean contains(int i) {

        int x = getDeclaredLowerBound();
        int y = getDeclaredUpperBound();

        boolean c1 = switch (getLowerBoundOperator()) {
            case LT -> i < x;
            case GT -> i > x;
            case LTE -> i <= x;
            case GTE -> i >= x;
        };

        boolean c2 = switch (getUpperBoundOperator()) {
            case LT -> i < y;
            case GT -> i > y;
            case LTE -> i <= y;
            case GTE -> i >= y;
        };

        return c1 && c2;
    }

    /**
     * Returns a {@code Range} implementation that encodes the convention whereby an integer is
     * considered to be part of a range if it sits at or above the lower bound and below the upper
     * bound. In other words, this is the "classic" way of defining a range.
     *
     * @param from The lower boundary (inclusive)
     * @param to   The upper boundary (exclusive)
     * @return A {@code Range} object
     */
    static Range from(int from, int to) {
        return new From(from, to);
    }

    /**
     * Returns a {@code Range} implementation that encodes the convention whereby an integer is
     * considered to be part of a range if it sits at or above the lower bound and at or below the
     * lower bound.
     *
     * @param from The lower boundary (inclusive)
     * @param to   The upper boundary (inclusive)
     * @return A {@code Range} object
     */
    static Range atOrInside(int from, int to) {
        return new Closed(from, to);
    }

    /**
     * Returns the same {@code Range} implementation as {@link #atOrInside(int, int) atOrInside},
     * but is more in line with Java naming conventions. E.g. {@link java.util.stream.IntStream#rangeClosed(int,
     * int) IntStream.rangeClosed}.
     *
     * @param from The lower boundary (inclusive)
     * @param to   The upper boundary (inclusive)
     * @return A {@code Range} object
     */
    static Range closed(int from, int to) {
        return new Closed(from, to);
    }

    /**
     * Returns a {@code Range} implementation that encodes the convention whereby an integer is
     * considered to be part of a range if it sits fully inside the lower and upper boundaries.
     *
     * @param from The lower boundary (exclusive)
     * @param to   The upper boundary (exclusive)
     * @return A {@code Range} object
     */
    static Range inside(int from, int to) {
        return new Inside(from, to);
    }

    /**
     * Returns a {@code Range} implementation that encodes the convention whereby an integer is
     * considered to be part of a range if it sits at or above the lower bound and below the lower
     * bound plus the given length of the range. This is how ranges are typically specified in
     * {@code java.io}; for example: {@code write(byte[] b, int off, int len)}.
     *
     * @param from The lower boundary (inclusive)
     * @param len  The length of the range
     * @return A {@code Range} object
     */
    static Range atOffset(int from, int len) {
        return new Offset(from, len);
    }

    record From(int from, int to) implements Range {
        public int getDeclaredLowerBound() {
            return from;
        }

        public int getDeclaredUpperBound() {
            return to;
        }

        public int getLowerBoundInclusive() {
            return from;
        }

        public int getUpperBoundInclusive() {
            return to - 1;
        }

        public BoundaryOperator getLowerBoundOperator() {
            return GTE;
        }

        public BoundaryOperator getUpperBoundOperator() {
            return LT;
        }

    }

    record Closed(int from, int to) implements Range {
        public int getDeclaredLowerBound() {
            return from;
        }

        public int getDeclaredUpperBound() {
            return to;
        }

        public int getLowerBoundInclusive() {
            return from;
        }

        public int getUpperBoundInclusive() {
            return to;
        }

        public BoundaryOperator getLowerBoundOperator() {
            return GTE;
        }

        public BoundaryOperator getUpperBoundOperator() {
            return LTE;
        }
    }

    record Inside(int from, int to) implements Range {
        public int getDeclaredLowerBound() {
            return from;
        }

        public int getDeclaredUpperBound() {
            return to;
        }

        public int getLowerBoundInclusive() {
            return from + 1;
        }

        public int getUpperBoundInclusive() {
            return to - 1;
        }

        public BoundaryOperator getLowerBoundOperator() {
            return GT;
        }

        public BoundaryOperator getUpperBoundOperator() {
            return LT;
        }
    }

    record Offset(int from, int len) implements Range {
        public int getDeclaredLowerBound() {
            return from;
        }

        public int getDeclaredUpperBound() {
            return from + len;
        }

        public int getLowerBoundInclusive() {
            return from;
        }

        public int getUpperBoundInclusive() {
            return from + len - 1;
        }

        public BoundaryOperator getLowerBoundOperator() {
            return GTE;
        }

        public BoundaryOperator getUpperBoundOperator() {
            return LT;
        }
    }
}
