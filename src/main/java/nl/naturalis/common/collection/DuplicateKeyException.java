package nl.naturalis.common.collection;

import java.util.List;

import static nl.naturalis.common.CollectionMethods.implode;

/**
 * Indicates that insertion into a {@link java.util.Map}, {@link java.util.Set} or
 * other uniqueness-enforcing data structure failed because the value to be inserted
 * was a duplicate.
 */
public class DuplicateKeyException extends RuntimeException {

  /**
   * Default constructor.
   */
  public DuplicateKeyException() {
    super();
  }

  /**
   * Creates a new {@code DuplicateKeyException} for the specified keys.
   *
   * @param key The duplicate key.
   */
  public DuplicateKeyException(Object key) {
    super("Duplicate key: " + key);
  }

  /**
   * Creates a new {@code DuplicateKeyException} for the specified keys.
   *
   * @param keys The duplicate keys.
   */
  public DuplicateKeyException(List<Object> keys) {
    super("Duplicate keys: " + implode(keys));
  }

}
