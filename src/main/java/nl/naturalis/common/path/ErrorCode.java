package nl.naturalis.common.path;

/**
 * Symbolic constants for read/write failures.
 */
public enum ErrorCode {

  /**
   * The {@code PathWalker} had arrived on a list or array, so expected the next segment in the
   * {@code Path} to be an array index.
   */
  INDEX_EXPECTED,
  /**
   * The {@code PathWalker} encountered an array index in the {@code Path}, but the value of the
   * preceding path segment was something other than a list or array.
   */
  INDEX_NOT_ALLOWED,
  /**
   * The {@code PathWalker} encountered an array index in the {@code Path} that was out of bounds
   * for the list or array it was processing.
   */
  INDEX_OUT_OF_BOUNDS,
  /**
   * The {@code PathWalker} encountered a segment that did not correspond to any property of the
   * JavaBean it was processing.
   */
  NO_SUCH_PROPERTY,
  /**
   * The {@code PathWalker} encountered a segment that did not correspond to any key of the {@code
   * Map} it was processing.
   */
  NO_SUCH_KEY,
  /**
   * The {@code PathWalker} encountered a {@link Path#NULL_SEGMENT null} or empty segment in the
   * {@code Path} while not processing a {@code Map}. (Null and empty path segments can only
   * possibly be valid as map keys.)
   */
  EMPTY_SEGMENT,
  /**
   * The {@code Path} extended beyond a terminal value ({@code null}, a primitive, a {@code String},
   * or some other type of value that the {@code PathWalker} cannot descend into).
   */
  TERMINAL_VALUE,
  /**
   * Thrown if the {@code PathWalker} encounters a value that it doesn't know how to read or write.
   */
  TYPE_NOT_SUPPORTED,
  /**
   * Thrown if the  {@code PathWalker} trapped an exception from underlying code.
   */
  EXCEPTION,
  /**
   * Indicates that a write action was successful. This code is only returned by the {@code write}
   * methods of the {@code PathWalker} class. The {@code read} methods just return the value that
   * was read.
   */
  OK;

}
