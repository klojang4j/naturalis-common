package nl.naturalis.common.path;

import nl.naturalis.common.check.Check;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.length;
import static nl.naturalis.common.check.CommonGetters.size;
import static nl.naturalis.common.path.ErrorCode.OK;
import static nl.naturalis.common.path.PathWalker.OnDeadEnd.RETURN_NULL;

/**
 * A {@code PathWalker} lets you read and write deeply nested values using {@link Path} objects. The
 * {@code PathWalker} class is useful for reading large batches of sparsely populated objects. For
 * some of these objects, the {@code PathWalker} may not be able to follow the path all the way to
 * the end, for example because it hit a {@code null} value before it reached the last path segment,
 * or some other kind of terminal value: a primitive, a {@code String}, etc. By default, this will
 * not cause the {@code PathWalker} to throw an exception. It will just return {@code null}.
 *
 * @author Ayco Holleman
 */
@SuppressWarnings({"unchecked"})
public final class PathWalker {

  /**
   * Symbolic constants for what to do if a read/write action fails.
   */
  public static enum OnDeadEnd {
    /**
     * Instructs the {@code PathWalker} to just return {@code null}. This is the default behaviour.
     */
    RETURN_NULL,
    /**
     * Instructs the {@code PathWalker} to return an {@link ErrorCode}.
     */
    RETURN_CODE,
    /**
     * Instructs the {@code PathWalker} to throw a {@link PathWalkerException}.
     */
    THROW_EXCEPTION;
  }

  private final Path[] paths;
  private final OnDeadEnd ode;
  private final Function<Path, Object> kds;

  /**
   * Creates a {@code PathWalker} for the specified paths.
   *
   * @param paths The paths to walk through the provided host objects
   */
  public PathWalker(Path... paths) {
    Check.that(paths, "paths").isNot(empty()).is(deepNotNull());
    this.paths = Arrays.copyOf(paths, paths.length);
    this.ode = RETURN_NULL;
    this.kds = null;
  }

  /**
   * Creates a {@code PathWalker} for the specified paths.
   *
   * @param paths The paths to walk through the provided host objects
   */
  public PathWalker(String... paths) {
    Check.that(paths, "paths").isNot(empty()).is(deepNotNull());
    this.paths = Arrays.stream(paths).map(Path::new).toArray(Path[]::new);
    this.ode = RETURN_NULL;
    this.kds = null;
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
   * @param paths The paths to walk through the provided host objects
   * @param onDeadEnd The action to take if the {@code PathWalker} hits a dead end
   */
  public PathWalker(List<Path> paths, OnDeadEnd onDeadEnd) {
    Check.notNull(paths, "paths").has(size(), positive());
    this.paths = paths.toArray(Path[]::new);
    this.ode = Check.notNull(onDeadEnd, "onDeadEnd").ok();
    this.kds = null;
  }

  /**
   * Creates a {@code PathWalker} for the specified paths.
   *
   * @param paths The paths to walk
   * @param onDeadEnd The action to take if a path could not be walked all the way to the end.
   * @param keyDeserializer A function that converts path segments to map keys. You need to
   *     provide this if the host objects are, or contain, {@link Map} instances with non-string
   *     keys. The function is given the path up to, and including the path segment that
   *     <i>represents</i> the key, and it should return the <i>actual</i> key
   */
  public PathWalker(List<Path> paths, OnDeadEnd onDeadEnd, Function<Path, Object> keyDeserializer) {
    Check.notNull(paths, "paths").has(size(), positive());
    this.paths = paths.toArray(Path[]::new);
    this.ode = Check.notNull(onDeadEnd, "onDeadEnd").ok();
    this.kds = Check.notNull(keyDeserializer, "keyDeserializer").ok();
  }

  PathWalker(Path path, OnDeadEnd onDeadEnd, Function<Path, Object> keyDeserializer) {
    this.paths = new Path[] {path};
    this.ode = onDeadEnd;
    this.kds = keyDeserializer;
  }

  /**
   * Returns the values of all paths within the specified host object.
   *
   * @param host The object to read the path values from
   * @return The values of all paths within the specified host object
   * @throws PathWalkerException
   */
  public Object[] readValues(Object host) throws PathWalkerException {
    return IntStream.range(0, paths.length).mapToObj(i -> readObj(host, paths[i])).toArray();
  }

  /**
   * Reads the values of all paths within the specified host object and places them in the provided
   * output array. The values will be in the same order as the paths specified through the
   * constructors. The length of the output array must be greater than or equal to the number of
   * paths specified through the constructor.
   *
   * @param host The object from which to read the values
   * @param output An array into which to place the values
   * @throws PathWalkerException
   */
  public void readValues(Object host, Object[] output) throws PathWalkerException {
    Check.notNull(output, "output").has(length(), gte(), paths.length);
    IntStream.range(0, paths.length).forEach(i -> output[i] = readObj(host, paths[i]));
  }

  /**
   * Reads the values of all paths within the specified host object and places them in the provided
   * path-to-value map.
   *
   * @param host The object from which to read the values
   * @param output The {@code Map} into which to put the values
   * @throws PathWalkerException
   */
  public void readValues(Object host, Map<Path, Object> output) throws PathWalkerException {
    Check.notNull(output, "output");
    Arrays.stream(paths).forEach(p -> output.put(p, readObj(host, p)));
  }

  /**
   * Returns the value of the first path within the specified host object . Useful if the {@code
   * PathWalker} was created with just one path.
   *
   * @param <T> The type of the value being returned
   * @param host The object to walk
   * @return The value referenced by the first path
   * @throws PathWalkerException
   */
  public <T> T read(Object host) {
    return (T) readObj(host, paths[0]);
  }

  /**
   * Sets the values of all configured paths to the specified values. This method returns {@code
   * true} if <i>all</i> values were successfully written. If {@link OnDeadEnd} is {@code
   * THROW_EXCEPTION}, a {@link PathWalkerException} detailing the error is thrown, otherwise this
   * method returns {@code false}.
   *
   * @param host The object into which to write the values
   * @param values The values to write
   * @return Whether all values were successfully written.
   */
  public boolean writeValues(Object host, Object... values) {
    Check.notNull(values, "values").has(length(), gte(), paths.length);
    boolean success = true;
    for (int i = 0; i < paths.length; ++i) {
      success = success && OK == write(host, paths[i], values[i]);
    }
    return success;
  }

  /**
   * Sets the value of the first path within the provided object to the specified value. Useful if
   * the {@code PathWalker} was created with just one path. This method will return {@code true} if
   * the value was successfully written to the host object. If {@link OnDeadEnd} is {@code
   * THROW_EXCEPTION}, a {@link PathWalkerException} detailing the error is thrown, otherwise this
   * method will return false.
   *
   * @param host The object into which to write the value
   * @param value The value to write
   * @return Whether the value was successfully written
   */
  public ErrorCode write(Object host, Object value) {
    return write(host, paths[0], value);
  }

  private Object readObj(Object obj, Path path) {
    return new ObjectReader(ode, kds).read(obj, path);
  }

  private ErrorCode write(Object host, Path path, Object value) {
    return new ObjectWriter(ode, kds).write(host, path, value);
  }

}
