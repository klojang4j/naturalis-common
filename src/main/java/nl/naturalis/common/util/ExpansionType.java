package nl.naturalis.common.util;

import static nl.naturalis.common.check.CommonChecks.atLeast;
import static nl.naturalis.common.check.CommonChecks.atMost;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.unsafe.UnsafeByteArrayOutputStream;
import nl.naturalis.common.unsafe.UnsafeList;

/**
 * Defines ways to increase the capacity of data structures that automatically grow as they fill up
 * (for example list-like data structures).
 *
 * @see UnsafeList
 * @see UnsafeByteArrayOutputStream
 * @author Ayco Holleman
 */
public enum ExpansionType {
  /** Inxrease the capacity by a fixed amount each time the data structure reaches full capacity. */
  ADD,
  /** Increase the capacity by multiplying by a fixed amount. */
  MULTIPLY,
  /** Increase the capacity with a fixed percentage of the current capacity. */
  PERCENTAGE;

  /**
   * Augments the specified curCapacity by the specified amount using {@code this} augmentation type
   * with a minimum amount of 1.
   *
   * @param curCapacity The current capacity
   * @param amount The amount by which to increase it
   * @return The value to used for the new capacity
   */
  public int increaseCapacity(int curCapacity, double amount) {
    return increaseCapacity(curCapacity, amount, 1);
  }

  /**
   * Augments the specified curCapacity by the specified amount using {@code this} augmentation type
   * with a minimum amount of {@code minAmount}. This method will throw an {@link
   * IllegalArgumentException} if the increased curCapacity goes beyond {@link Integer#MAX_VALUE} or
   * {@link Integer#MIN_VALUE}.
   *
   * @param curCapacity The current capacity
   * @param amount The amount by which to increase it
   * @param minAmount The amount by which to increase it
   * @return The value to used for the new capacity
   */
  public int increaseCapacity(int curCapacity, double amount, int minAmount) {
    return Check.that(
            increaseCapacity((double) curCapacity, amount, (double) minAmount), "New capacity")
        .is(atMost(), Integer.MAX_VALUE)
        .is(atLeast(), Integer.MIN_VALUE)
        .ok(Double::intValue);
  }

  /**
   * Augments the specified curCapacity by the specified amount using {@code this} augmentation type
   * with a minimum amount of {@code minAmount}.
   *
   * @param curCapacity The current capacity
   * @param amount The amount by which to increase it
   * @param minAmount The minimum amount by which to increase it
   * @return The value to used for the new capacity
   */
  public double increaseCapacity(double curCapacity, double amount, double minAmount) {
    switch (this) {
      case ADD:
        return curCapacity + Math.max(amount, minAmount);
      case MULTIPLY:
        return Math.max(curCapacity * amount, curCapacity + minAmount);
      case PERCENTAGE:
      default:
        return Math.max(curCapacity * ((100 + amount) / 100), curCapacity + minAmount);
    }
  }
}
