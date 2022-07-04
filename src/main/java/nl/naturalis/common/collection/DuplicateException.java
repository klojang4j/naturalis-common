package nl.naturalis.common.collection;

import java.util.List;

import static nl.naturalis.common.CollectionMethods.implode;

/**
 * Indicates that insertion into a {@link java.util.Map}, {@link java.util.Set} or
 * some other uniqueness-enforcing data structure failed because the value to be
 * inserted was a duplicate.
 */
public class DuplicateException extends RuntimeException {

  public static enum Category {
    KEY, ELEMENT, VALUE;

    String description() {
      return name().toLowerCase();
    }
  }

  /**
   * Default constructor.
   */
  public DuplicateException() {
    super();
  }

  /**
   * Creates a new {@code DuplicateKeyException} for the specified value.
   *
   * @param duplicate The duplicate value.
   */
  public DuplicateException(Category category, Object duplicate) {
    super("Duplicate " + category.description() + ": " + duplicate);
  }

  /**
   * Creates a new {@code DuplicateKeyException} for the specified values.
   *
   * @param duplicates The duplicate values.
   */
  public DuplicateException(Category category, List<Object> duplicates) {
    super("Duplicate " + category.description() + ": " + implode(duplicates));
  }

}
