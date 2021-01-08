package nl.naturalis.common.util;

/**
 * Defines ways to increment a number.
 *
 * @author Ayco Holleman
 */
public enum IncrementType {
  /** Increment a number by adding another (possibly negative) number. */
  TERM,
  /** Increment a number by multiplying it by another (possibly decimal) number. */
  FACTOR,
  /** Increment a number by a (possibly negative) percentage. */
  PERCENTAGE;
}
