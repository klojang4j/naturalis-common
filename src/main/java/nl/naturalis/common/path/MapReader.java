package nl.naturalis.common.path;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import nl.naturalis.common.Check;
import nl.naturalis.common.ClassMethods;
import static nl.naturalis.common.path.InvalidPathException.missingArrayIndex;
import static nl.naturalis.common.path.Path.isArrayIndex;
import static java.util.function.Predicate.*;
import static nl.naturalis.common.ClassMethods.*;

public class MapReader {

  /**
   * A special value indicating that a path was not present in the map that is
   * currently being read.
   */
  public static final Object DEAD_PATH = new Object();

  private final List<Path> paths;
  private final boolean nullIfDead;

  public MapReader(List<Path> paths) {
    this(paths, true);
  }

  /**
   * Creates a {@code MapReader} for the specified paths. If {@code nullIfDead}
   * equals {@code true}, then the value for a path that does not exist within the
   * map currently being read will be null, else the value will be
   * {@link #DEAD_PATH}. You must use a reference comparison to find out if you
   * are dealing with a dead path.
   *
   * @param paths
   * @param nullIfDead
   */
  public MapReader(List<Path> paths, boolean nullIfDead) {
    this.paths = Check.notEmpty(paths);
    paths.forEach(p -> Check.argument(p, not(Path::isEmpty), "empty path not allowed"));
    this.nullIfDead = nullIfDead;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public Object read(Map<String, Object> map, Path path) {
    Object val;
    for (int i = 0; i < path.size(); ++i) {
      String segment = path.segment(i);
      if ((val = map.get(segment)) == null) {
        if (map.containsKey(segment) || nullIfDead) {
          return null;
        }
        return DEAD_PATH;
      } else if (i == path.size() - 1) {
        return val;
      } else if (val instanceof Collection) {
        Check.that(isArrayIndex(path.segment(i + 1)), () -> missingArrayIndex(path, segment));
        Collection coll = (Collection) val;
        int idx = Integer.parseInt(path.segment(i + 1));
        if (idx >= coll.size()) {
          return nullIfDead ? null : DEAD_PATH;
        }
        val = coll.toArray()[idx];
        if (++i == path.size() - 1) {
          return val;
        }
      } else if (val instanceof Object[]) {
        Check.that(isArrayIndex(path.segment(i + 1)), () -> missingArrayIndex(path, segment));
        Object[] objs = (Object[]) val;
        int idx = Integer.parseInt(path.segment(i + 1));
        if (idx >= objs.length) {
          return nullIfDead ? null : DEAD_PATH;
        }
        val = objs[idx];
        if (++i == path.size() - 1) {
          return val;
        }
      } else if (isPrimitiveArray(val)) {
        // ... TODO
      }
      map = (Map<String, Object>) val;
    }
    throw new AssertionError(); // should not get here
  }

}
