package nl.naturalis.common;

/**
 * Defines objects that can meaningfully be said to be empty, e&#46;g&#46; when all their fields are
 * null.
 *
 * @author Ayco Holleman
 */
public interface Emptyable {

  /** A generic empty object */
  public static Emptyable EMPTY = () -> true;

  /**
   * Returns whether or not the object can be said to be empty.
   *
   * @return
   */
  boolean isEmpty();
}
