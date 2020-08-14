package nl.naturalis.common.path;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.IntStream;
import nl.naturalis.common.Check;
import nl.naturalis.common.ClassMethods;
import static nl.naturalis.common.ClassMethods.isPrimitiveArray;
import static nl.naturalis.common.path.Path.isArrayIndex;
import static nl.naturalis.common.path.PathWalkerException.illegalAccess;

/**
 * <p>
 * Walks one or more {@link Path paths} through a given object in order to
 * retrieve their values. No exceptions are thrown if a path could not be walked
 * all the way to the last segment due to:
 * <p>
 * <ul>
 * <li>a path segment did not correspond to any field or map key
 * <li>an array index was expected but not found in the path
 * <li>the array index was out of bounds
 * <li>the path continued after having reached a terminal value within the
 * object (a primitive)
 * </ul>
 * <p>
 * In all of these cases the path's value is set to null or {@link #DEAD_END}
 * (depending on your choice), but no exception is thrown. Only if a segment
 * <i>did</i> correspond to a field, but accessing the field caused an
 * {@link IllegalAccessException}, a {@link PathWalkerException} wrapping the
 * {@code IllegalAccessException} is thrown.
 * <p>
 * A path does not have to end at a terminal value. If it stops at a complex
 * object, then that object will be the value of the path, even though it may
 * contain any number of nested objects.
 *
 * @author Ayco Holleman
 *
 */
public final class PathWalker {

  /**
   * A special value indicating that a path could not be walked all the way to the
   * end for the object currently being read.
   */
  public static final Object DEAD_END = new Object();

  private final Path[] paths;
  private final boolean useDeadEnd;
  private final Function<Object, String> stringifier;

  /**
   * Creates a {@code MapReader} for the specified paths, setting the value for
   * paths that code not be walked all the way to the end to null.
   *
   * @param paths
   */
  public PathWalker(Path... paths) {
    Check.that(paths, "paths").notEmpty().noneNull();
    this.paths = Arrays.copyOf(paths, paths.length);
    this.useDeadEnd = false;
    this.stringifier = null;
  }

  /**
   * Creates a {@code MapReader} for the specified paths, setting the value for
   * paths that code not be walked all the way to the end to null.
   *
   * @param paths
   */
  public PathWalker(String... paths) {
    Check.that(paths, "paths").notEmpty().noneNull();
    this.paths = Arrays.stream(paths).map(Path::new).toArray(Path[]::new);
    this.useDeadEnd = false;
    this.stringifier = null;
  }

  /**
   * Creates a {@code MapReader} for the specified paths, setting the value for
   * paths that code not be walked all the way to the end to null.
   *
   * @param paths
   */
  public PathWalker(List<Path> paths) {
    this(paths, false, null);
  }

  /**
   * Creates a {@code MapReader} for the specified paths. If
   * {@code useDeadEndValue} equals {@code true}, then the value for a path that
   * could not be walked will be {@link #DEAD_END}, else null. If it is important
   * to distinguish between "real" null values and dead ends, pass {@code true}.
   *
   * @param paths
   * @param useDeadEndValue
   */
  public PathWalker(List<Path> paths, boolean useDeadEndValue) {
    this(paths, useDeadEndValue, null);
  }

  /**
   * Creates a {@code MapReader} for the specified paths. If
   * {@code useDeadEndValue} equals {@code true}, then the value for a path that
   * could not be walked will be {@link #DEAD_END}, else {@code null}. If it is
   * important to distinguish between "real" null values and dead ends, pass
   * {@code true}. If the objects to walk are or contain other than
   * string-to-object maps, you need to provide a function that stringifies the
   * map keys, such that they can be mapped to path segments.
   *
   * @param paths The paths to walk
   * @param useDeadEndValue Whether to use {@link #DEAD_END} or null for paths
   *        that could not be walked all the way to the end
   * @param mapKeyStringifier A function that converts map keys to strings (may be
   *        null if no stringification is required)
   */
  public PathWalker(List<Path> paths, boolean useDeadEndValue, Function<Object, String> mapKeyStringifier) {
    Check.that(paths, "paths").notEmpty().noneNull();
    this.paths = paths.toArray(Path[]::new);
    this.useDeadEnd = useDeadEndValue;
    this.stringifier = mapKeyStringifier;
  }

  /**
   * Walks all paths through to provided object and returns their values in the
   * same order as the paths specified through the constructor.
   *
   * @param obj The object to read the path values from
   * @return
   * @throws PathWalkerException
   */
  public Object[] readValues(Object obj) throws PathWalkerException {
    return IntStream.range(0, paths.length).mapToObj(i -> readObj(obj, paths[i])).toArray();
  }

  /**
   * Walks all paths through to provided object and places their values in the
   * provided output array. The values will be in the same order as the paths
   * specified through the constructors. The output array need not have the same
   * length as the path array.
   *
   * @param obj
   * @param output
   * @throws PathWalkerException
   */
  public void readValues(Object obj, Object[] output) throws PathWalkerException {
    int x = Math.min(paths.length, Check.notNull(output, "output").length);
    IntStream.range(0, x).forEach(i -> output[i] = readObj(obj, paths[i]));
  }

  /**
   * Walks all paths through to provided object and places their values in the
   * provided path-to-value map.
   *
   * @param obj
   * @param output
   * @throws PathWalkerException
   */
  public void readValues(Object obj, Map<Path, Object> output) throws PathWalkerException {
    Check.notNull(output, "output");
    Arrays.stream(paths).forEach(p -> output.put(p, readObj(obj, p)));
  }

  /**
   * Returns the value of the first path. Useful if the {@code PathWalker} was
   * created for just one path.
   *
   * @param <T> The type of the object that the path points to
   * @param obj The object to read the path values from
   * @return
   * @throws PathWalkerException
   */
  @SuppressWarnings("unchecked")
  public <T> T readValue(Object obj) {
    return (T) readObj(obj, paths[0]);
  }

  @SuppressWarnings({"rawtypes"})
  private Object readObj(Object obj, Path path) {
    if (path.isEmpty() || obj == null || obj == DEAD_END) {
      return obj;
    } else if (obj instanceof Collection) {
      return readElement((Collection) obj, path);
    } else if (obj instanceof Object[]) {
      return readElement((Object[]) obj, path);
    } else if (obj instanceof Map) {
      return readMap((Map) obj, path);
    } else if (isPrimitiveArray(obj)) {
      return readPrimitiveElement(obj, path);
    } else {
      return readAny(obj, path);
    }
  }

  @SuppressWarnings({"rawtypes"})
  private Object readMap(Map map, Path path) {
    String segment = path.segment(0);
    if (stringifier == null) {
      if (map.containsKey(segment)) {
        return readObj(map.get(segment), path.shift());
      }
    } else {
      for (Entry e : (Set<Entry>) map.entrySet()) {
        if (Objects.equals(stringifier.apply(e.getKey()), segment)) {
          return readObj(e.getValue(), path.shift());
        }
      }
    }
    return deadEnd();
  }

  @SuppressWarnings("rawtypes")
  private Object readElement(Collection collection, Path path) {
    String segment = path.segment(0);
    if (isArrayIndex(segment)) {
      int idx = Integer.parseInt(segment);
      if (idx < collection.size()) {
        return readObj(collection.toArray()[idx], path.shift());
      }
    }
    return deadEnd();
  }

  private Object readElement(Object[] array, Path path) {
    String segment = path.segment(0);
    if (isArrayIndex(segment)) {
      int idx = Integer.parseInt(segment);
      if (idx < array.length) {
        return readObj(array[idx], path.shift());
      }
    }
    return deadEnd();
  }

  private Object readPrimitiveElement(Object array, Path path) {
    if (path.size() == 1) { // primitive *must* be the end of the trail
      String segment = path.segment(0);
      if (isArrayIndex(segment)) {
        int idx = Integer.parseInt(segment);
        if (idx < Array.getLength(array)) {
          return readObj(Array.get(array, idx), path.shift());
        }
      }
    }
    return deadEnd();
  }

  private Object readAny(Object any, Path path) {
    String segment = path.segment(0);
    try {
      VarHandle vh = ClassMethods.getVarHandle(any, segment);
      if (vh == null) {
        return deadEnd();
      }
      return readObj(vh.get(any), path.shift());
    } catch (IllegalAccessException e) {
      throw illegalAccess(e, any, segment);
    }
  }

  private Object deadEnd() {
    return useDeadEnd ? DEAD_END : null;
  }

}
