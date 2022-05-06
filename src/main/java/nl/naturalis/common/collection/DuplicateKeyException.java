package nl.naturalis.common.collection;

public class DuplicateKeyException extends RuntimeException {

  DuplicateKeyException(Object key) {
    super("Duplicate key: " + key);
  }

}
