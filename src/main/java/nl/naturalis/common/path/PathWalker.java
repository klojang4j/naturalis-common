package nl.naturalis.common.path;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.invoke.BeanReader;
import nl.naturalis.common.invoke.BeanWriter;
import nl.naturalis.common.invoke.NoSuchPropertyException;
import static nl.naturalis.common.ClassMethods.getArrayTypeSimpleName;
import static nl.naturalis.common.ClassMethods.isPrimitiveArray;
import static nl.naturalis.common.check.Check.badArgument;
import static nl.naturalis.common.check.CommonChecks.gte;
import static nl.naturalis.common.check.CommonChecks.noneNull;
import static nl.naturalis.common.check.CommonChecks.notEmpty;
import static nl.naturalis.common.check.CommonGetters.length;
import static nl.naturalis.common.path.PathWalkerException.invalidPath;
import static nl.naturalis.common.path.PathWalkerException.nullSegmentNotAllowed;
import static nl.naturalis.common.path.PathWalkerException.readWriteError;
import static nl.naturalis.common.path.PathWalker.DeadEndAction.*;

/**
 * Reads/writes objects using {@link Path} objects. The {@code PathWalker} class is useful for
 * reading large batches of sparsely populated objects or maps. For some of these objects or maps,
 * the {@code PathWalker} will not be able to follow a path all the way to the end. This can be due
 * to any of the following reasons:
 *
 * <p>
 *
 * <ul>
 *   <li>One of the intermediate path segments references a null value
 *   <li>The path is invalid given the type of the object to read/write
 *   <li>An array index within the path was out of bounds
 *   <li>The path continued after having reached a terminal value within the object (a primitive)
 * </ul>
 *
 * <p>By default {@code PathWalker} will return null in these cases. If you need to distinguish
 * between true nulls and the "dead ends" described above, you can instruct the {@code PathWalker}
 * to return a special value, {@link #DEAD_END}, in stead of null. You can also instruct to throw a
 * {@link NoSuchPropertyException}. See {@link DeadEndAction}.
 *
 * <p>If the {@code PathWalker} is on a segment that references a {@code Collection} or an array,
 * the next path segment must be an array index (unless it is the {@code Collection} or array itself
 * that you want to retrieve). If it is not, it will silently assume you want the first element of
 * the {@code Collection} or array.
 *
 * @author Ayco Holleman
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class PathWalker {

  /**
   * Symbolic constants for what to do if the {@code PathWalker} hits a dead end for a particular
   * {@code Path}.
   */
  public static enum DeadEndAction {
    RETURN_NULL,
    RETURN_DEAD_END,
    THROW_EXCEPTION;
  }

  /**
   * A special value indicating that a path could not be walked all the way to the end for the
   * object currently being read.
   */
  public static final Object DEAD_END = new Object();

  private final Path[] paths;
  private final DeadEndAction deadEndAction;
  private final Function<Path, Object> keyDeser;

  /**
   * Creates a {@code PathWalker} for the specified paths, setting the value for paths that could
   * not be walked all the way to the end to null.
   *
   * @param paths
   */
  public PathWalker(Path... paths) {
    Check.that(paths, "paths").is(notEmpty()).is(noneNull());
    this.paths = Arrays.copyOf(paths, paths.length);
    this.deadEndAction = RETURN_NULL;
    this.keyDeser = null;
  }

  /**
   * Creates a {@code PathWalker} for the specified paths, setting the value for paths that could
   * not be walked all the way to the end to null.
   *
   * @param paths
   */
  public PathWalker(String... paths) {
    Check.that(paths, "paths").is(notEmpty()).is(noneNull());
    this.paths = Arrays.stream(paths).map(Path::new).toArray(Path[]::new);
    this.deadEndAction = RETURN_NULL;
    this.keyDeser = null;
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
    Check.that(paths, "paths").is(notEmpty()).is(noneNull());
    this.paths = paths.toArray(Path[]::new);
    this.deadEndAction = deadEndAction;
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
   * @param host The object from which to read the values
   * @param output An array into which to place the values
   * @throws PathWalkerException
   */
  public void readValues(Object host, Object[] output) throws PathWalkerException {
    Check.notNull(output, "output").has(length(), gte(), paths.length);
    IntStream.range(0, paths.length).forEach(i -> output[i] = readObj(host, paths[i]));
  }

  /**
   * Reads the values of all paths within the provided object and places them in the provided
   * path-to-value map.
   *
   * @param host The object from which to read the values
   * @param output An {@code Map} into which to put the values
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
    Check.notNull(values, "values").has(length(), gte(), paths.length);
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
      return readBean(obj, path);
    }
  }

  private Object readMap(Map map, Path path) {
    String segment = path.segment(0);
    Object key = keyDeser == null ? segment : keyDeser.apply(path);
    if (map.containsKey(key)) {
      return readObj(map.get(key), path.shift());
    }
    return deadEnd(new NoSuchPropertyException(path.toString()));
  }

  private Object readElement(Collection collection, Path path) {
    String segment = path.segment(0);
    if (Path.isArrayIndex(segment)) {
      int idx = Integer.parseInt(segment);
      if (idx < collection.size()) {
        return readObj(collection.toArray()[idx], path.shift());
      }
    }
    return deadEnd(new NoSuchPropertyException(path.toString()));
  }

  private Object readElement(Object[] array, Path path) {
    String segment = path.segment(0);
    if (Path.isArrayIndex(segment)) {
      int idx = Integer.parseInt(segment);
      if (idx < array.length) {
        return readObj(array[idx], path.shift());
      }
    }
    return deadEnd(new NoSuchPropertyException(path.toString()));
  }

  private Object readPrimitiveElement(Object array, Path path) {
    if (path.size() == 1) { // primitive *must* be the end of the trail
      String segment = path.segment(0);
      if (Path.isArrayIndex(segment)) {
        int idx = Integer.parseInt(segment);
        if (idx < Array.getLength(array)) {
          return readObj(Array.get(array, idx), path.shift());
        }
      }
    }
    return deadEnd(new NoSuchPropertyException(path.toString()));
  }

  private <T> Object readBean(T bean, Path path) {
    Class<T> beanClass = (Class<T>) bean.getClass();
    String property = path.segment(0);
    BeanReader<T> reader = new BeanReader<>(beanClass, property);
    try {
      try {
        Object val = reader.get(bean, property);
        return readObj(val, path.shift());
      } catch (NoSuchPropertyException e) {
        return deadEnd(e);
      }
    } catch (Throwable e1) {
      throw readWriteError(e1, bean, path.segment(0));
    }
  }

  private void write(Object host, Path path, Object value) {
    Path parent = path.parent();
    Path child = path.subpath(-1);
    if (Path.isArrayIndex(child.segment(0))) {
      if (parent.isEmpty()) {
        throw invalidPath(path);
      }
      PathWalker pw = new PathWalker(parent);
      Object parval = pw.read(host);
      if (parval != null) {
        int idx = Integer.parseInt(child.toString());
        writeElement(parval, idx, value);
      }
    } else {
      PathWalker pw = new PathWalker(parent);
      Object parentObject = pw.read(host);
      if (parentObject != null) {
        if (parentObject instanceof Map) {
          Map map = (Map) parentObject;
          Object key;
          if (child.isNullSegment()) {
            key = null;
          } else if (keyDeser == null) {
            key = child.toString();
          } else {
            key = keyDeser.apply(child);
          }
          map.put(key, value);
        } else if (!child.isNullSegment()) {
          try {
            BeanWriter bw = new BeanWriter<>(parentObject.getClass());
            bw.set(parentObject, child.toString(), value);
          } catch (Throwable T) {
            throw readWriteError(T, parentObject, child.toString());
          }
        } else {
          throw nullSegmentNotAllowed(parentObject);
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
      Check.notNull(
          badArgument("Cannot assign null to element of %s", getArrayTypeSimpleName(parval)),
          value);
      if (idx < Array.getLength(parval)) {
        Array.set(parval, idx, value);
      }
    } else {
      String fmt = "Cannot set value in object of type %s using array index";
      String msg = String.format(fmt, parval.getClass().getName());
      throw new PathWalkerException(msg);
    }
  }

  private Object deadEnd(NoSuchPropertyException e) {
    switch (deadEndAction) {
      case RETURN_NULL:
        return null;
      case RETURN_DEAD_END:
        return DEAD_END;
      case THROW_EXCEPTION:
      default:
        throw e;
    }
  }
}
