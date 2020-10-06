package nl.naturalis.common.check;

/**
 * An {@code int} tuple specifying a range.
 *
 * @author Ayco Holleman
 */
public class Range {

  /**
   * Returns a {@code Range} with the specified lower bound and the specified upper bound.
   *
   * @param lowerBound The lower bound of the range
   * @param upperBound The upper bound of the range
   * @return A {@code Range} instance
   */
  public static Range of(int lowerBound, int upperBound) {
    return new Range(lowerBound, upperBound);
  }

  private final int lowerBound;
  private final int upperBound;

  private Range(int x, int y) {
    this.lowerBound = x;
    this.upperBound = y;
  }

  public int getLowerBound() {
    return lowerBound;
  }

  public int getUpperBound() {
    return upperBound;
  }
}
