package nl.naturalis.common.path;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ClassMethods.getArrayTypeSimpleName;
import static nl.naturalis.common.ClassMethods.isPrimitiveArray;
import static nl.naturalis.common.path.Path.isArrayIndex;
import static nl.naturalis.common.path.PathWalkerException.illegalAccess;
import static nl.naturalis.common.check.Checks.*;
import static nl.naturalis.common.check.Check.*;

/**
 * Reads/writes values within a given Object using {@link Path} objects. The {@code PathWalker}
 * class is especially useful for reading large batches of sparsely populated objects or maps. It
 * will simply return null if a path could not be walked all the way to the last path segment, due
 * to any of the following reasons:
 *
 * <p>
 *
 * <ul>
 *   <li>one of the intermediate path segments referenced a null value
 *   <li>the path is invalid given the type of the object to read/write
 *   <li>an array index within the path was out of bounds
 *   <li>the path continued after having reached a terminal value within the object (a primitive)
 * </ul>
 *
 * <p>If you need to distinguish between true null values and the "dead ends" described above, you
 * can {@link #PathWalker(List, boolean) request} to return a special value, {@link #DEAD_END},
 * instead.
 *
 * <p>If the {@code PathWalker} is on a segment that references a {@code Collection} or an array,
 * the next path segment is supposed to be an array index (unless it is the {@code Collection} or
 * array itself that you are interested in). If it is not, it will silently assume you want the
 * first element of the {@code Collection} c.q. array.
 *
 * <p>If the {@code PathWalker} is on a segment that references a map key, and the map to be
 * read/written uses non-{@code String} keys, you must instruct the {@code PathWalker} how to
 * deserialize the path segment into an appropriately typed key. This is done by passing in a {@code
 * Function} takes a {@code Path} and returns the key. The first path segment of the {@code Path} is
 * the segment specifying the key. You still get the entire path following that segment so you know
 * where you are within the object you are reading.
 *
 * @author Ayco Holleman
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class PathWalker {

  /**
   * A special value indicating that a path could not be walked all the way to the end for the
   * object currently being read.
   */
  public static final Object DEAD_END = new Object();

  private final Path[] paths;
  private final boolean useDeadEnd;
  private final Function<Path, Object> keyDeser;

  /**
   * Creates a {@code PathWalker} for the specified paths, setting the value for paths that could
   * not be walked all the way to the end to null.
   *
   * @param paths
   */
  public PathWalker(Path... paths) {
    Check.that(paths, "paths", notEmpty()).and(noneNull());
    this.paths = Arrays.copyOf(paths, paths.length);
    this.useDeadEnd = false;
    this.keyDeser = null;
  }

  /**
   * Creates a {@code PathWalker} for the specified paths, setting the value for paths that could
   * not be walked all the way to the end to null.
   *
   * @param paths
   */
  public PathWalker(String... paths) {
    Check.that(paths, "paths", notEmpty()).and(noneNull());
    this.paths = Arrays.stream(paths).map(Path::new).toArray(Path[]::new);
    this.useDeadEnd = false;
    this.keyDeser = null;
  }

  /**
   * Creates a {@code PathWalker} for the specified paths, setting the value for paths that code not
   * be walked all the way to the end to null.
   *
   * @param paths
   */
  public PathWalker(List<Path> paths) {
    this(paths, false, null);
  }

  /**
   * Creates a {@code PathWalker} for the specified paths. If {@code useDeadEndValue} equals {@code
   * true}, then the value for a path that could not be walked will be {@link #DEAD_END}, else null.
   * If it is important to distinguish between "real" null values and dead ends, pass {@code true}.
   *
   * @param paths
   * @param useDeadEndValue
   */
  public PathWalker(List<Path> paths, boolean useDeadEndValue) {
    this(paths, useDeadEndValue, null);
  }

  /**
   * Creates a {@code PathWalker} for the specified paths. If {@code useDeadEndValue} equals {@code
   * true}, then the value for a path that could not be walked will be {@link #DEAD_END}, else
   * {@code null}. If it is important to distinguish between "real" null values and dead ends, pass
   * {@code true}. If you need to read from or write to maps with non-string keys, you must provide
   * a function that converts path segments to map keys.
   *
   * @param paths The paths to walk
   * @param useDeadEndValue Whether to use {@link #DEAD_END} or null for paths that could not be
   *     walked all the way to the end
   * @param mapKeyDeserializer A function that converts strings to map keys (may be null if no
   *     deserialization is required)
   */
  public PathWalker(
      List<Path> paths, boolean useDeadEndValue, Function<Path, Object> mapKeyDeserializer) {
    Check.that(paths, "paths", notEmpty()).and(noneNull());
    this.paths = paths.toArray(Path[]::new);
    this.useDeadEnd = useDeadEndValue;
    this.keyDeser = mapKeyDeserializer;
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
   * Reads the values of all paths within the provided object and places them in the provided output
   * array. The values will be in the same order as the paths specified through the constructors.
   * The length of the output array must be greater than or equal to the number of paths specified
   * through the constructor.
   *
   * @param host
   * @param values
   * @throws PathWalkerException
   */
  public void readValues(Object host, Object[] values) throws PathWalkerException {
    Check.notNull(values, "output").and(Array::getLength, "length", atLeast(), paths.length);
    IntStream.range(0, paths.length).forEach(i -> values[i] = readObj(host, paths[i]));
  }

  /**
   * Reads the values of all paths within the provided object and places them in the provided
   * path-to-value map.
   *
   * @param host
   * @param output
   * @throws PathWalkerException
   */
  public void readValues(Object host, Map<Path, Object> output) throws PathWalkerException {
    Check.notNull(output, "output");
    Arrays.stream(paths).forEach(p -> output.put(p, readObj(host, p)));
  }

  /**
   * Returns the value of the first path. Useful if the {@code PathWalker} was created with just one
   * path.
   *
   * @param <T> The type of the object that the path points to
   * @param obj The object to read the path values from
   * @return
   * @throws PathWalkerException
   */
  public <T> T read(Object obj) {
    return (T) readObj(obj, paths[0]);
  }

  /**
   * Sets the values of all paths within the provided object to the specified values. The number of
   * values must be greater than or equal to the length as the number of {@code Path} objects
   * specified through the constructor.
   *
   * @param host
   * @param values
   */
  public void writeValues(Object host, Object... values) {
    Check.notNull(values, "values").and(Array::getLength, "length", atLeast(), paths.length);
    for (int i = 0; i < paths.length; ++i) {
      write(host, paths[i], values[i]);
    }
  }

  /**
   * Sets the value of the first path within the provided object to the specified value. Useful if
   * the {@code PathWalker} was created for just one path. This method will not throw an exception
   * if path's parent does not exist within the provided object, or if the value of the path's
   * parent is null. It will also not throw an exception when trying to set a value at an index that
   * is out of bounds for the provided host object. It may throw a {@link ClassCastException},
   * however.
   *
   * @param host
   * @param value
   */
  public void write(Object host, Object value) {
    write(host, paths[0], value);
  }

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

  private Object readMap(Map map, Path path) {
    String segment = path.segment(0);
    Object key = keyDeser == null ? segment : keyDeser.apply(path);
    if (map.containsKey(key)) {
      return readObj(map.get(key), path.shift());
    }
    return deadEnd();
  }

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
      Field f = ClassMethods.getField(any, segment);
      if (f == null) {
        return deadEnd();
      }
      return readObj(f.get(any), path.shift());
    } catch (IllegalAccessException e) {
      throw illegalAccess(e, any, segment);
    }
  }

  /*
   * NB setting the value of a path actually means setting a value in the object referenced
   * by the parent of that path. E.g. if you want to set employee.name to "John", you actually
   * invoke a setter of the Employee class.
   */
  private void write(Object host, Path path, Object value) {
    Path parent = path.parent();
    Path target = path.subpath(-1);
    if (target.isArrayIndex()) {
      if (parent.isEmpty()) {
        throw new PathWalkerException("Invalid path: " + path);
      }
      PathWalker pw = new PathWalker(parent);
      Object parval = pw.read(host);
      if (parval != null) {
        int idx = Integer.parseInt(target.toString());
        writeElement(parval, idx, value);
      }
    } else {
      PathWalker pw = new PathWalker(parent);
      Object parval = pw.read(host);
      if (parval != null) {
        if (parval instanceof Map) {
          Map map = (Map) parval;
          Object key;
          if (target.isNullSegment()) {
            key = null;
          } else if (keyDeser == null) {
            key = target.toString();
          } else {
            key = keyDeser.apply(target);
          }
          map.put(key, value);
        } else if (!target.isNullSegment()) {
          try {
            Field f = ClassMethods.getField(parval, target.toString());
            f.set(parval, value);
          } catch (IllegalAccessException e) {
            throw illegalAccess(e, parval, target.toString());
          }
        } else {
          throw new PathWalkerException(
              "Null segment can only be used to write map value with key null");
        }
      }
    }
  }

  private static void writeElement(Object parval, int idx, Object value) {
    if (parval instanceof List) {
      List list = (List) parval;
      if (idx < list.size()) {
        list.set(idx, value);
      }
    } else if (parval instanceof Object[]) {
      Object[] arr = (Object[]) parval;
      if (idx < arr.length) {
        arr[idx] = value;
      }
    } else if (isPrimitiveArray(parval)) {
      Check.that(
          value != null,
          badArgument("Cannot assign null to element of %s[]", getArrayTypeSimpleName(parval)));
      if (idx < Array.getLength(parval)) {
        Array.set(parval, idx, value);
      }
    } else {
      String fmt = "Cannot set value in object of type %s using array index";
      String msg = String.format(fmt, parval.getClass().getName());
      throw new PathWalkerException(msg);
    }
  }

  private Object deadEnd() {
    return useDeadEnd ? DEAD_END : null;
  }
}
