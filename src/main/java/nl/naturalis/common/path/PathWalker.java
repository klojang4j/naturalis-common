package nl.naturalis.common.path;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.invoke.NoSuchPropertyException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.length;
import static nl.naturalis.common.path.PathWalker.DeadEndAction.RETURN_NULL;

/**
 * A {@code PathWalker} lets you read and write deeply nested values within almost any kind of
 * object by means of {@link Path} objects. The {@code PathWalker} class is useful for reading large
 * batches of sparsely populated objects or maps. For some of these objects or maps, the {@code
 * PathWalker} may not be able to follow a path all the way to the end. This can be due to any of
 * the following reasons:
 *
 * <p>
 *
 * <ul>
 *   <li>One of the intermediate path segments references a null value (the {@code PathWalker}
 *       literally hits a dead end)
 *   <li>The path is invalid given the type of the object to read/write
 *   <li>An array index within the path was out of bounds
 *   <li>The path continued after having reached a terminal value within the object (a primitive)
 * </ul>
 *
 * <p>By default {@code PathWalker} will return {@code null} in all of these cases. If you need to
 * distinguish between true nulls and the "dead ends" described above, you can instruct the {@code
 * PathWalker} to return a special value, {@link #DEAD_END}, in stead of null. You can also instruct
 * to throw a {@link NoSuchPropertyException}. See {@link DeadEndAction}.
 *
 * <p>If the {@code PathWalker} is on a segment that references a {@code Collection} or an array,
 * the next path segment must be an array index (unless it is the {@code Collection} or array itself
 * that you want to retrieve). If it is not, it will silently assume you want the first element of
 * the {@code Collection} or array.
 *
 * @author Ayco Holleman
 */
@SuppressWarnings({"unchecked"})
public final class PathWalker {

  /**
   * Symbolic formatters for what to do if the {@code PathWalker} hits a dead end for a particular
   * {@code Path}.
   */
  public static enum DeadEndAction {
    /** Instructs the {@code PathWalker} to return {@code null} if it reaches a dead end. */
    RETURN_NULL,
    /**
     * Instructs the {@code PathWalker} to return the special value {@link PathWalker#DEAD_END} if
     * it reaches a dead end. Use a reference comparison to check for this value.
     */
    RETURN_DEAD_END,
    /**
     * Instructs the {@code PathWalker} to throw a {@link NoSuchPropertyException} if it reaches a
     * dead end.
     */
    THROW_EXCEPTION;
  }

  /**
   * A special value indicating that a path could not be walked all the way to the end for the
   * object currently being read.
   */
  public static final Object DEAD_END = new Object();

  private final Path[] paths;
  private final DeadEndAction dea;
  private final Function<Path, Object> kds;

  /**
   * Creates a {@code PathWalker} for the specified paths, setting the value for paths that could
   * not be walked all the way to the end to null.
   *
   * @param paths
   */
  public PathWalker(Path... paths) {
    Check.that(paths, "paths").isNot(empty()).is(neverNull());
    this.paths = Arrays.copyOf(paths, paths.length);
    this.dea = RETURN_NULL;
    this.kds = null;
  }

  /**
   * Creates a {@code PathWalker} for the specified paths, setting the value for paths that could
   * not be walked all the way to the end to null.
   *
   * @param paths
   */
  public PathWalker(String... paths) {
    Check.that(paths, "paths").isNot(empty()).is(neverNull());
    this.paths = Arrays.stream(paths).map(Path::new).toArray(Path[]::new);
    this.dea = RETURN_NULL;
    this.kds = null;
  }

  /**
   * Creates a {@code PathWalker} for the specified paths, setting the value for paths that code not
   * be walked all the way to the end to null.
   *
   * @param paths
   */
  public PathWalker(List<Path> paths) {
    this(paths, RETURN_NULL, null);
  }

  /**
   * Creates a {@code PathWalker} for the specified paths. If {@code useDeadEndValue} equals {@code
   * true}, then the value for a path that could not be walked will be {@link #DEAD_END}, else null.
   * If it is important to distinguish between "real" null values and dead ends, pass {@code true}.
   *
   * @param paths
   * @param deadEndAction
   */
  public PathWalker(List<Path> paths, DeadEndAction deadEndAction) {
    this(paths, deadEndAction, null);
  }

  /**
   * Creates a {@code PathWalker} for the specified paths. If {@code useDeadEndValue} equals {@code
   * true}, then the value for a path that could not be walked will be {@link #DEAD_END}, else
   * {@code null}. If it is important to distinguish between "real" null values and dead ends, pass
   * {@code true}. If you need to read from or write to maps with non-string keys, you must provide
   * a function that deserializes a path segment into a map key.
   *
   * @param paths The paths to walk
   * @param deadEndAction The action to take if a path could not be walked all the way to the end.
   * @param mapKeyDeserializer A function that converts strings to map keys (may be null if no
   *     deserialization is required)
   */
  public PathWalker(
      List<Path> paths, DeadEndAction deadEndAction, Function<Path, Object> mapKeyDeserializer) {
    Check.that(paths, "paths").isNot(empty()).is(neverNull());
    this.paths = paths.toArray(Path[]::new);
    this.dea = deadEndAction;
    this.kds = mapKeyDeserializer;
  }

  /**
   * Returns the values of all paths within the provided object in the same order as the paths
   * specified through the constructor.
   *
   * @param host The object to read the path values from
   * @return
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
   * true} if <i>all</i> values were successfully writen. If the configured {@link DeadEndAction} is
   * {@code THROW_EXCEPTION}, this method will throw a {@link PathWalkerException} detailing the
   * error of the write action that failed, otherwise this method returns {@code false}.
   *
   * @param host The object into which to write the values
   * @param values The values to write
   * @return Whether or not all values were successfully written.
   */
  public boolean writeValues(Object host, Object... values) {
    Check.notNull(values, "values").has(length(), gte(), paths.length);
    boolean success = true;
    for (int i = 0; i < paths.length; ++i) {
      success = success && write(host, paths[i], values[i]);
    }
    return success;
  }

  /**
   * Sets the value of the first path within the provided object to the specified value. Useful if
   * the {@code PathWalker} was created with just one path. This method will return {@code true} if
   * the value was successfully written to the host object. If the configured {@link DeadEndAction}
   * is {@code THROW_EXCEPTION}, this method will throw a {@link PathWalkerException} detailing the
   * error, otherwise this method will return false.
   *
   * @param host The object into which to write the value
   * @param value The value to write
   * @return Whether or not the value was successfully written
   */
  public boolean write(Object host, Object value) {
    return write(host, paths[0], value);
  }

  private Object readObj(Object obj, Path path) {
    return new ObjectReader(dea, kds).read(obj, path);
  }

  private boolean write(Object host, Path path, Object value) {
    return new ObjectWriter(dea, kds).write(host, path, value);
  }
}
