package nl.naturalis.common.check;

import nl.naturalis.common.IntPair;

/**
 * Interface for defining an integer range. Meant to be used in conjunction
 * with the {@link CommonChecks#inRange() inRange} check.
 */
public sealed interface Range permits Range.From, Range. Closed, Range.Inside {

    /**
     * Returns a {@code Range} object with the first integer (@code from} as the lower boundary
     * (inclusive) and the second integer ({@code to}) as the upper boundary (exclusive).
     *
     * @param from The lower boundary (inclusive)
     * @param to The upper boundary (exclusive)
     * @return A {@code Range} object
     */
    public static Range from(int from, int to) {
        return new From(from, to);
    }

    /**
     * Returns a {@code Range} object with the first integer (@code from} as the lower boundary
     * (inclusive) and the second integer ({@code to}) as the upper boundary (inclusive).
     *
     * @param from The lower boundary (inclusive)
     * @param to The upper boundary (inclusive)
     * @return A {@code Range} object
     */
    public static Range closed(int from, int to) {
        return new Closed(from, to);
    }

    /**
     * Returns a {@code Range} object with the first integer (@code from} as the lower boundary
     * (exclusive) and the second integer ({@code to}) as the upper boundary (exclusive).
     *
     * @param from The lower boundary (exclusive)
     * @param to The upper boundary (exclusive)
     * @return A {@code Range} object
     */
    public static Range inside(int from, int to) {
        return new Inside(from,to);
    }

    static sealed interface Reader permits Range.From, Range. Closed, Range.Inside {
        default boolean isInRange(int i) {
            IntPair ip = getBounds();
            return i >= ip.one() && i <= ip.two();
        }
        IntPair getBounds();
    }

    static record From(int from, int to) implements Range, Reader {
      public IntPair getBounds() { return IntPair.of(from, to - 1); }
    }

    static record Closed(int from, int to) implements Range, Reader {
      public IntPair getBounds() { return IntPair.of(from, to); }
    }

    static record Inside(int from, int to)  implements Range, Reader {
      public IntPair getBounds() { return IntPair.of(from + 1, to - 1); }
    }
}
