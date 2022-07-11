package nl.naturalis.common.collection;

import java.util.List;

import static nl.naturalis.common.CollectionMethods.implode;

/**
 * Indicates that insertion into a {@link java.util.Map}, {@link java.util.Set} or
 * some other uniqueness-enforcing data structure failed because the value to be
 * inserted was a duplicate.
 */
public class DuplicateValueException extends RuntimeException {

  /**
   * Symbolic constants for what the value was a duplicate of.
   */
  public enum Category {
    /**
     * The value was a duplicate of a {@code Map} key.
     */
    KEY,
    /**
     * The value was a duplicate of a {@code Set} element.
     */
    ELEMENT,
    /**
     * The value was a duplicate due to some other uniqueness-enforcing mechanism or
     * data structure.
     */
    VALUE;

    private String description() {
      return name().toLowerCase();
    }
  }

  /**
   * Default constructor.
   */
  public DuplicateValueException() {
    super();
  }

  /**
   * Creates a new {@code DuplicateValueException} for the specified value.
   *
   * @param duplicate The duplicate value.
   */
  public DuplicateValueException(Category category, Object duplicate) {
    super("Duplicate " + category.description() + ": " + duplicate);
  }

  /**
   * Creates a new {@code DuplicateValueException} for the specified values.
   *
   * @param duplicates The duplicate values.
   */
  public DuplicateValueException(Category category, List<Object> duplicates) {
    super("Duplicate " + category.description() + "(s): " + implode(duplicates));
  }

}
