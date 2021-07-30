package nl.naturalis.common.util;

import static nl.naturalis.common.check.CommonChecks.GTE;
import static nl.naturalis.common.check.CommonChecks.LTE;

import nl.naturalis.common.check.Check;

/**
 * Defines ways to increment a number.
 *
 * @author Ayco Holleman
 */
public enum AugmentationType {
  /** Increment a number by adding another (possibly negative) number. */
  ADD,
  /** Increment a number by multiplying it by another (possibly decimal) number. */
  MULTIPLY,
  /** Increment a number by a (possibly negative) percentage. */
  PERCENTAGE;

  /**
   * Augments the specified value by the specified amount using {@code this} augmentation type with
   * a minimum amount of 1.
   *
   * @param value The value to increase
   * @param amount The amount by which to increase it
   * @return The increased value
   */
  public int augment(int value, double amount) {
    return augment(value, amount, 1);
  }

  /**
   * Augments the specified value by the specified amount using {@code this} augmentation type with
   * a minimum amount of {@code minAmount}. This method will throw an {@link
   * IllegalArgumentException} if the increased value goes beyond {@link Integer#MAX_VALUE} or
   * {@link Integer#MIN_VALUE}.
   *
   * @param value The value to increase
   * @param amount The amount by which to increase it
   * @param minAmount The amount by which to increase it
   * @return The increased value
   */
  public int augment(int value, double amount, int minAmount) {
    return Check.that(augment((double) value, amount, (double) minAmount), "New value")
        .is(LTE(), Integer.MAX_VALUE)
        .is(GTE(), Integer.MIN_VALUE)
        .ok(Double::intValue);
  }

  /**
   * Augments the specified value by the specified amount using {@code this} augmentation type with
   * a minimum amount of {@code minAmount}.
   *
   * @param value The value to increase
   * @param amount The amount by which to increase it
   * @param minAmount The amount by which to increase it
   * @return The increased value
   */
  public double augment(double value, double amount, double minAmount) {
    switch (this) {
      case ADD:
        return value + Math.max(amount, minAmount);
      case MULTIPLY:
        return Math.max(value * amount, value + minAmount);
      case PERCENTAGE:
      default:
        return Math.max(value * ((100 + amount) / 100), value + minAmount);
    }
  }
}
