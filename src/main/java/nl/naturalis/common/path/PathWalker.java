package nl.naturalis.common.path;

import nl.naturalis.common.check.Check;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.length;
import static nl.naturalis.common.check.CommonGetters.size;
import static nl.naturalis.common.path.ErrorCode.OK;
import static nl.naturalis.common.path.PathWalker.OnError.RETURN_NULL;
import static nl.naturalis.common.path.PathWalker.OnError.THROW_EXCEPTION;

/**
 * A {@code PathWalker} lets you read and write deeply nested values using {@link Path} objects. The
 * {@code PathWalker} class is useful for reading large batches of sparsely populated objects. For
 * some of these objects, the {@code PathWalker} may not be able to follow the path all the way to
 * the end, for example because it hit a {@code null} value before it reached the last path segment,
 * or because it hit a terminal value like a primitive or a {@code String}. By default, this will
 * not cause the {@code PathWalker} to throw an exception. It will just return {@code null}.
 *
 * @author Ayco Holleman
 */
@SuppressWarnings({"unchecked"})
public final class PathWalker {

  /**
   * Symbolic constants for what to do if a read/write action fails.
   */
  public enum OnError {
    /**
     * Instructs the {@code PathWalker} to return {@code null}. This is the default behaviour.
     */
    RETURN_NULL,
    /**
     * Instructs the {@code PathWalker} to return an {@link ErrorCode}.
     */
    RETURN_CODE,
    /**
     * Instructs the {@code PathWalker} to throw a {@link PathWalkerException}, from which you can
     * retrieve the {@link ErrorCode} again.
     */
    THROW_EXCEPTION
  }

  private final Path[] paths;
  private final OnError oe;
  private final Function<Path, Object> kd;

  /**
   * Creates a {@code PathWalker} for the specified paths.
   *
   * @param paths The paths to walk through the provided host objects
   */
  public PathWalker(Path... paths) {
    Check.that(paths, "paths").isNot(empty()).is(deepNotNull());
    this.paths = Arrays.copyOf(paths, paths.length);
    this.oe = RETURN_NULL;
    this.kd = null;
  }

  /**
   * Creates a {@code PathWalker} for the specified paths.
   *
   * @param paths The paths to walk through the provided host objects
   */
  public PathWalker(String... paths) {
    Check.that(paths, "paths").isNot(empty()).is(deepNotNull());
    this.paths = Arrays.stream(paths).map(Path::new).toArray(Path[]::new);
    this.oe = RETURN_NULL;
    this.kd = null;
  }

  /**
   * Creates a {@code PathWalker} for the specified paths.
   *
   * @param paths The paths to walk through the provided host objects
   */
  public PathWalker(List<Path> paths) {
    this(paths, RETURN_NULL);
  }

  /**
   * Creates a {@code PathWalker} for the specified paths.
   *
   * @param paths The action to take if a path could not be read or written
   * @param onError The action to take if the {@code PathWalker} hits a dead end
   */
  public PathWalker(List<Path> paths, OnError onError) {
    Check.notNull(paths, "paths").has(size(), positive());
    this.paths = paths.toArray(Path[]::new);
    this.oe = Check.notNull(onError, "onError").ok();
    this.kd = null;
  }

  /**
   * Creates a {@code PathWalker} for the specified paths.
   *
   * @param paths The paths to walk
   * @param onError The action to take if a path could not be read or written
   * @param keyDeserializer A function that converts path segments to map keys. You need to
   *     provide this if the host objects are, or contain, {@link Map} instances with non-string
   *     keys. The function is given the path up to, and including the path segment that
   *     <i>represents</i> the key, and it should return the <i>actual</i> key
   */
  public PathWalker(List<Path> paths, OnError onError, Function<Path, Object> keyDeserializer) {
    Check.notNull(paths, "paths").has(size(), positive());
    this.paths = paths.toArray(Path[]::new);
    this.oe = Check.notNull(onError, "onError").ok();
    this.kd = Check.notNull(keyDeserializer, "keyDeserializer").ok();
  }

  // For internal use
  PathWalker(Path path, OnError oe, Function<Path, Object> kd) {
    this.paths = new Path[] {path};
    this.oe = oe;
    this.kd = kd;
  }

  /**
   * Returns the values of all paths within the specified host object.
   *
   * @param host The object to read the path values from
   * @return The values of all paths within the specified host object
   * @throws PathWalkerException
   */
  public Object[] readValues(Object host) throws PathWalkerException {
    ObjectReader reader = new ObjectReader(oe, kd);
    return Arrays.stream(paths).map(p -> reader.read(host, p)).toArray();
  }

  /**
   * Reads the values of all paths and places them in the provided output array. The length of the
   * output array must be greater than or equal to the number of paths specified through the
   * constructor.
   *
   * @param host The object from which to read the values
   * @param output An array into which to place the values
   * @throws PathWalkerException
   */
  public void readValues(Object host, Object[] output) throws PathWalkerException {
    Check.notNull(output, "output").has(length(), gte(), paths.length);
    ObjectReader reader = new ObjectReader(oe, kd);
    for (int i = 0; i < paths.length; ++i) {
      output[i] = reader.read(host, paths[i]);
    }
  }

  /**
   * Reads the values of all paths and inserts them into the provided path-to-value map.
   *
   * @param host The object from which to read the values
   * @param output The {@code Map} into which to put the values
   * @throws PathWalkerException
   */
  public void readValues(Object host, Map<Path, Object> output) throws PathWalkerException {
    Check.notNull(output, "output");
    ObjectReader reader = new ObjectReader(oe, kd);
    Arrays.stream(paths).forEach(p -> output.put(p, reader.read(host, p)));
  }

  /**
   * Returns the value of the first path within the specified host object.
   *
   * @param <T> The type of the value being returned
   * @param host The object to walk
   * @return The value referenced by the first path
   * @throws PathWalkerException
   */
  public <T> T read(Object host) {
    return (T) new ObjectReader(oe, kd).read(host, paths[0]);
  }

  /**
   * Writes the specified values to the paths specified through the constructor. The number of
   * values must equal the number of paths. If {@link OnError} is {@link OnError#RETURN_CODE
   * RETURN_CODE}, this method turns into a fail-fast method, returning the {@link ErrorCode} of the
   * first failed assignment, or {@link ErrorCode#OK} if all assignments succeeded. If {@link
   * OnError} is {@code OnDeadEnd#RETURN_NULL RETURN_NULL}, this method always returns {@code null}
   * and a write failure will not cause the method to be cut short.
   *
   * @param host The object to which to write the values
   * @param values The values to write
   * @return {@code null} ot the {@code ErrorCode} of the first failed assignment
   */
  public ErrorCode writeValues(Object host, Object... values) {
    Check.notNull(values, "values").has(length(), eq(), paths.length);
    ObjectWriter writer = new ObjectWriter(oe, kd);
    if (oe == RETURN_NULL || oe == THROW_EXCEPTION) {
      for (int i = 0; i < paths.length; ++i) {
        writer.write(host, paths[i], values[i]);
      }
      return null;
    }
    for (int i = 0; i < paths.length; ++i) {
      ErrorCode code = writer.write(host, paths[i], values[i]);
      if (code != OK) {
        return code;
      }
    }
    return OK;
  }

  /**
   * Writes the specified values to the paths specified through the constructor. The number of
   * values must equal the number of paths. A write failure will never cause this method to be cut
   * short. If {@link OnError} is {@code OnDeadEnd #RETURN_NULL RETURN_NULL}, this method returns
   * {@code null}. If it is {@code OnDeadEnd#RETURN_CODE RETURN_CODE}, an array of {@link ErrorCode
   * error codes} is returned.
   *
   * @param host The object to which to write the values
   * @param values The values to write
   * @return {@code null} or an array of {@code ErrorCode} instances, depending on the value of
   *     {@code OnDeadEnd}
   */
  public ErrorCode[] writeAll(Object host, Object... values) {
    Check.notNull(values, "values").has(length(), eq(), paths.length);
    ObjectWriter writer = new ObjectWriter(oe, kd);
    if (oe == RETURN_NULL || oe == THROW_EXCEPTION) {
      for (int i = 0; i < paths.length; ++i) {
        writer.write(host, paths[i], values[i]);
      }
      return null;
    }
    ErrorCode[] codes = new ErrorCode[paths.length];
    for (int i = 0; i < paths.length; ++i) {
      codes[i] = writer.write(host, paths[i], values[i]);
    }
    return codes;
  }

  /**
   * Sets the value of the first path.
   *
   * @param host The object into which to write the value
   * @param value The value to write
   * @return {@code null} or an  {@code ErrorCode}, depending on the value of {@code OnDeadEnd}
   */
  public ErrorCode write(Object host, Object value) {
    return new ObjectWriter(oe, kd).write(host, paths[0], value);
  }

}
