package nl.naturalis.common.path;

public enum DeadEnd {

  /**
   * The {@code PathWalker} had arrived on a list or array, so expected the next segment in the
   * {@code Path} to be an array index.
   */
  INDEX_EXPECTED,
  /**
   * The {@code PathWalker} encountered an array index in the {@code Path}, but it was not
   * processing a list or array.
   */
  INDEX_NOT_ALLOWED,
  /**
   * The {@code PathWalker} encountered an array index that was out of bounds for the list or array
   * it was processing.
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
   * Thrown if the {@code PathWalker} encounters a value in the host object that it cannot read or
   * write.
   */
  TYPE_NOT_SUPPORTED,
  /**
   * Thrown if the  {@code PathWalker} trapped an exception from underlying code while
   * reading/writing a value.
   */
  READ_ERROR,
  /**
   * The {@code PathWalker} successfully set the property corresponding to a {@code Path} (only
   * returned when writing values).
   */
  OK;

}
