package nl.naturalis.common;

/**
 * Defines objects that can meaningfully be said to have a certain size or length. Classes
 * implementing this interface get special treatment in methods like {@link
 * ObjectMethods#isEmpty(Object)}.
 *
 * @author Ayco Holleman
 */
public interface Sizeable {

  /**
   * Returns the size of the object.
   *
   * @return The size of the object
   */
  int size();
}
