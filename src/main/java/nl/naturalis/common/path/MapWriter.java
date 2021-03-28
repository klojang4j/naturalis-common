package nl.naturalis.common.path;

import java.util.LinkedHashMap;
import java.util.Map;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.instanceOf;
import static nl.naturalis.common.check.CommonChecks.notEmpty;
import static nl.naturalis.common.check.CommonChecks.notInstanceOf;
import static nl.naturalis.common.check.CommonChecks.notNull;
import static nl.naturalis.common.path.Path.EMPTY_PATH;

/**
 * Provides a convenient way of writing <i>maps-within-maps</i> (<code>Map&lt;String, Object&gt;
 * </code>) objects. It lets you write deeply nested values without having to worry about whether
 * all the intermediate (<code>Map&lt;String, Object&gt;</code>) objects have been created. If they
 * are not, they will be tacitly created.
 *
 * <h4>Example 1:</h4>
 *
 * <pre>
 * MapWriter mw = new MapWriter();
 * mw.write("person.address.street", "12 Revolutionay Rd.")
 *  .write("person.address.state", "CA")
 *  .write("person.firstName", "John")
 *  .write("person.lastName", "Smith")
 *  .write("person.born", LocalDate.of(1967, 4, 4));
 * Map<String, Object> map = mw.getMap();
 * </pre>
 *
 * <p>
 *
 * <h4>Example 2:</h4>
 *
 * <pre>
 * MapWriter mw = new MapWriter();
 * mw.in("person")
 *  .write("firstName", "John")
 *  .write("lastName", "Smith")
 *  .write("born", LocalDate.of(1967, 4, 4))
 *  .in("address")
 *  .write("street", "12 Revolutionay Rd.")
 *  .write("state", "CA");
 * Map<String, Object> map = mw.getMap();
 * </pre>
 *
 * @author Ayco Holleman
 */
public class MapWriter {

  private static final String ERR_NULL_KEY = "Illegal null key in map at path [%s]";
  private static final String ERR_BAD_KEY = "Illegal key type in map at path [%s]: %s";

  /**
   * Thrown if you try to write to a path that extends beyond a path already containing a terminal
   * value (i&#46;e&#46; a non-Map value).
   *
   * @author Ayco Holleman
   */
  public static class PathBlockedException extends IllegalArgumentException {
    private PathBlockedException(Path path, Object value) {
      super(String.format("Key %s already written: %s", path, value));
    }
  }

  private final Path root;
  private final Map<String, Object> index;

  public MapWriter() {
    this(EMPTY_PATH);
  }

  public MapWriter(Map<String, Object> map) {
    Check.notNull(map);
    this.root = EMPTY_PATH;
    this.index = new LinkedHashMap<>(map.size());
    init(this, map);
  }

  private MapWriter(Path root) {
    this(root, new LinkedHashMap<>());
  }

  private MapWriter(Path root, Map<String, Object> index) {
    this.root = root;
    this.index = index;
  }

  public MapWriter in(String path) {
    Check.that(path, "path").is(notEmpty());
    return in(this, new Path(path));
  }

  public MapWriter write(String path, Object value) {
    return write(new Path(path), value);
  }

  public MapWriter write(Path path, Object value) {
    Check.notNull(path);
    write(this, path, value);
    return this;
  }

  public boolean wrote(String path) {
    return wrote(this, new Path(path));
  }

  public Map<String, Object> getMap() {
    return getMap(this);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static void init(MapWriter writer, Map map) {
    map.forEach(
        (k, v) -> {
          Check.that(k)
              .is(notNull(), ERR_NULL_KEY, writer.root)
              .is(instanceOf(), String.class, ERR_BAD_KEY, writer.root, k.getClass());
          String key = (String) k;
          if (v instanceof Map) {
            Map map0 = (Map) v;
            Path root = writer.root.append(key);
            MapWriter mw = new MapWriter(root, new LinkedHashMap(map0.size()));
            writer.index.put(key, mw);
            init(mw, map0);
          } else {
            writer.index.put(key, v);
          }
        });
  }

  private static void write(MapWriter writer, Path relPath, Object value) {
    if (value != null) {
      Check.that(value, "value").is(notInstanceOf(), Map.class);
    }
    if (relPath.size() == 1) {
      writer.index.put(relPath.toString(), value);
    } else {
      String key = relPath.segment(0);
      Path root = writer.root.append(key);
      Object val = writer.index.computeIfAbsent(key, k -> new MapWriter(root));
      if (val.getClass() != MapWriter.class) {
        throw new PathBlockedException(root, val);
      }
      write((MapWriter) val, relPath.shift(), value);
    }
  }

  private static MapWriter in(MapWriter writer, Path relPath) {
    if (relPath.isEmpty()) {
      return writer;
    }
    String key = relPath.segment(0);
    Path root = writer.root.append(key);
    Object val = writer.index.computeIfAbsent(key, k -> new MapWriter(root));
    if (val.getClass() != MapWriter.class) {
      throw new PathBlockedException(root, val);
    }
    return in((MapWriter) val, relPath.shift());
  }

  private static boolean wrote(MapWriter writer, Path relPath) {
    String key = relPath.segment(0);
    if (relPath.size() == 1) {
      return writer.index.containsKey(key);
    }
    Object val = writer.index.get(key);
    if (val.getClass() != MapWriter.class) {
      return true;
    }
    return wrote((MapWriter) val, relPath.shift());
  }

  private static Map<String, Object> getMap(MapWriter writer) {
    Map<String, Object> map = new LinkedHashMap<>(writer.index.size());
    writer.index.forEach(
        (k, v) -> {
          if (v != null && v.getClass() == MapWriter.class) {
            map.put(k, getMap((MapWriter) v));
          } else {
            map.put(k, v);
          }
        });
    return map;
  }
}
