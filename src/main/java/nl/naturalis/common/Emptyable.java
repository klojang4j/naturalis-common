package nl.naturalis.common;

/**
 * Defines objects that can meaningfully be said to be empty. For example when all their fields are
 * null. Classes implementing this interface get special treatment in methods like {@link
 * ObjectMethods#isEmpty(Object)}.
 *
 * @author Ayco Holleman
 */
public interface Emptyable {

  /** A generic empty object */
  public static final Emptyable EMPTY_INSTANCE = () -> true;

  /** A generic non-empty object */
  public static final Emptyable NON_EMPTY_INSTANCE = () -> false;

  /**
   * Returns whether the object can be said to be empty.
   *
   * @return Whether the object can be said to be empty
   */
  boolean isEmpty();
}
