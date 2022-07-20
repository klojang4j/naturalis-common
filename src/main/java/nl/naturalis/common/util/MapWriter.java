package nl.naturalis.common.util;

import nl.naturalis.common.Result;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.path.Path;
import nl.naturalis.common.x.Param;

import java.util.LinkedHashMap;
import java.util.Map;

import static nl.naturalis.common.ObjectMethods.*;
import static nl.naturalis.common.check.Check.fail;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.strlen;

/**
 * <p>Provides a convenient way of writing {@code Map<String, Object>}
 * pseudo-objects. Useful when serializing the maps again to JSON or some other
 * hierarchical format as it guarantees well-formedness. A {@code MapWriter} lets you
 * write deeply nested values without having to create the intermediate maps first.
 * If they are missing, they will be tacitly created.
 *
 * <p>Internally, a {@code MapWriter} works with {@link Path} objects. See the
 * documentation for the {@code Path} class for how to specify path strings.
 *
 * <h4>Example 1:</h4>
 *
 * <blockquote><pre>{@code
 * MapWriter mw = new MapWriter();
 * mw.write("person.address.street", "12 Revolutionary Rd.")
 *  .write("person.address.state", "CA")
 *  .write("person.firstName", "John")
 *  .write("person.lastName", "Smith")
 *  .write("person.dateOfBirth", LocalDate.of(1967, 4, 4));
 * Map<String, Object> map = mw.createMap();
 * }</pre></blockquote>
 *
 * <p>
 *
 * <h4>Example 2:</h4>
 *
 * <blockquote><pre>{@code
 * Map<String, Object> map = new MapWriter()
 *  .in("person")
 *    .write("firstName", "John")
 *    .write("lastName", "Smith")
 *    .write("dateOfBirth", LocalDate.of(1967, 4, 4))
 *    .in("address")
 *      .write("street", "12 Revolutionary Rd.")
 *      .write("state", "CA")
 *  .createMap();
 * }</pre></blockquote>
 *
 * @author Ayco Holleman
 */
public final class MapWriter {

  /**
   * Thrown when attempting to write to a path that has already been set, or that is
   * unwritable because one of its path segments has been already been set to a
   * terminal value.
   *
   * @author Ayco Holleman
   */
  public static class PathOccupiedException extends IllegalArgumentException {

    private PathOccupiedException(Path path, Object value) {
      super(createMessage(path, value));
    }

    private static String createMessage(Path path, Object value) {
      String fmt = "path \"%s\" occupied by terminal value %s";
      if (value instanceof String s) {
        value = '"' + s + '"';
      }
      return String.format(fmt, path, value);
    }

  }

  //  static final String ERR_ILLEGAL_VALUE = "Illegal value for path \"${0}\": ${arg}";
  //  static final String ERR_ILLEGAL_VALUE_IN_SOURCE_MAP = "Illegal value in source map for path \"${0}\": ${arg}";

  private static final String NULL = new String();

  private static final String ERR_EMPTY_SEGMENT = "illegal null or empty segment in path \"${0}\"";
  private static final String ERR_HOME_ALREADY = "already in root map";

  private final Map<String, Object> map;
  private final Path root;
  private final MapWriter parent;

  /**
   * Creates a new {@code MapWriter}.
   */
  public MapWriter() {
    this(new LinkedHashMap<>());
  }

  /**
   * Creates a {@code MapWriter} that starts out with the entries in the specified
   * map. The map is read, but not modified.
   *
   * @param map The initial {@code Map}
   */
  public MapWriter(Map<String, Object> map) {
    Check.notNull(map, Param.MAP);
    this.map = new LinkedHashMap<>(map.size() + 10);
    this.root = Path.empty();
    this.parent = null;
    init(this, map);
  }

  private MapWriter(Path root, MapWriter parent) {
    this.root = root;
    this.map = new LinkedHashMap<>();
    this.parent = parent;
  }

  /**
   * Returns a {@code MapWriter} for the map at the specified path. All paths you
   * specify will from now on be interpreted as relative to the specified path. If
   * there is no map yet at the specified path, it will be created. Ancestral maps
   * will be created as well, as and when needed. If any of the segments in the path
   * (including the last segment) has already been set to a terminal value, a
   * {@link PathOccupiedException} is thrown. A terminal value is anything that is
   * <i>not</i> a map.
   *
   * @param path the path to be used as the root path. The path must itself be
   *     specified relative to the current root path
   * @return a {@code MapWriter} for the map found or created at the specified path
   */
  public MapWriter in(String path) {
    Check.notNull(path, Param.PATH);
    return in(this, Path.from(path));
  }

  /**
   * <p>Returns a {@code MapWriter} for the parent map of the map currently being
   * written to. All paths you specify will from now on be interpreted as relative to
   * the parent map's path. An {@link IllegalStateException} is thrown when trying to
   * exit out of the root map. The argument to the {@code up} method must be the last
   * segment of the parent map's path. An {@link IllegalArgumentException} is thrown
   * if it isn't.
   *
   * <blockquote><pre>{@code
   * Map<String, Object> map = new MapWriter()
   *  .in("person")
   *    .write("firstName", "John")
   *    .write("lastName", "Smith")
   *    .in("address")
   *      .write("street", "12 Revolutionary Rd.")
   *      .write("state", "CA")
   *      .up("person")
   *    .write("dateOfBirth", LocalDate.of(1967, 4, 4))
   *  .createMap();
   * }</pre></blockquote>
   *
   * <p> You can chain {@code exit} calls. To exit from a map directly under the
   * root map, specify {@code null} or {@code ""} (an empty string):
   *
   * <blockquote><pre>{@code
   * MapWriter mw = new MapWriter();
   *  .in("department.manager.address")
   *    .set("street", "Sunset Blvd")
   *    .up("manager")
   *    .up("department")
   *    .up(null)
   *  .set("foo", "bar");
   * }</pre></blockquote>
   *
   * @return a {@code MapWriter} for the parent map of the map currently being
   *     written to
   */
  public MapWriter up(String parentSegment) {
    Check.on(illegalState(), parent).is(notNull(), ERR_HOME_ALREADY);
    if (root.size() == 1) {
      Check.that(parentSegment).is(empty(),
          "specify null or \"\" to exit to root map");
    } else {
      String actual = parent.root.segment(-1);
      Check.that(parentSegment).is(EQ(), actual,
          "expected segment: \"${obj}\"; provided segment: \"${arg}\"");
    }
    return parent;
  }

  /**
   * Takes you back to the root map. All paths you specify will from now on be
   * interpreted as absolute paths again.
   *
   * @return a {@code MapWriter} for the root map
   */
  public MapWriter reset() {
    Check.on(illegalState(), parent).is(notNull(), ERR_HOME_ALREADY);
    MapWriter mw = parent;
    while (mw.parent != null) {
      mw = mw.parent;
    }
    return mw;
  }

  /**
   * <p>Sets the specified path to the specified value. It is not allowed to
   * overwrite the value of a path that has already been set, even if set to
   * {@code null}. If necessary, use {@link #unset(String)} to unset the path's value
   * first.
   *
   * <p>It is not allowed to directly set the path to a value of type {@code Map}.
   * Use the {@link #in(String) in} method to create a new map at the specified path.
   * It is allowed to set a path's value to {@code null}.
   *
   * @param path the path at which to write the value
   * @param value the value
   * @return this {@code MapWriter}
   */
  public MapWriter set(String path, Object value) {
    Check.notNull(path, Param.PATH);
    set(this, Path.from(path), ifNull(value, NULL));
    return this;
  }

  /**
   * Returns a {@link Result} object containing the value of the specified path, or
   * {@link Result#none} if the path is not set.
   *
   * @param path the path
   * @return a {@link Result} object containing the value of the specified path, or
   *     {@link Result#none} if the path is not set
   * @see #isSet(String)
   */
  public Result<Object> get(String path) {
    Check.notNull(path, Param.PATH);
    return get(this, Path.from(path));
  }

  /**
   * Returns whether the specified path is set to a terminal value (and hence cannot
   * be extended).
   *
   * @param path the path
   * @return whether it is set to a terminal value
   */
  public boolean isSet(String path) {
    Check.notNull(path);
    return isSet(this, Path.from(path));
  }

  /**
   * Unsets the value of the specified path. If any segment preceding the last
   * segment has a terminal value, or if it is not a key in the map at that point the
   * path, this method returns quietly. If the last segment <i>is</i> a key, it will
   * be removed.
   *
   * @param path the path to unset.
   * @return this {@code MapWriter}
   */
  public MapWriter unset(String path) {
    Check.notNull(path);
    unset(this, Path.from(path));
    return this;
  }

  /**
   * Returns the {@code Map} resulting from the write actions. The returned map is
   * modifiable and retains the order in which the paths (now keys) were written. You
   * can continue to use the {@code MapWriter} after a call to this method.
   *
   * @return the {@code Map} resulting from the write actions
   */
  public Map<String, Object> createMap() {
    return createMap(this);
  }

  @Override
  public String toString() {
    return createMap().toString();
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static void init(MapWriter writer, Map map) {
    map.forEach((key, val) -> processEntry(writer, key, val));
  }

  private static void processEntry(MapWriter writer, Object key, Object val) {
    Check.that(key)
        .isNot(NULL(), "illegal null key in source map")
        .isNot(empty(), "illegal empty key in source map")
        .is(instanceOf(), String.class, "illegal key type in source map: ${type}");
    String k = key.toString();
    if (val instanceof Map nested) {
      Path path = writer.root.append(k);
      MapWriter mw = new MapWriter(path, writer);
      writer.map.put(k, mw);
      init(mw, nested);
    } else {
      writer.map.put(k, val);
    }
  }

  private static void set(MapWriter writer, Path path, Object value) {
    Check.that(value, Param.VALUE)
        .isNot(instanceOf(), Map.class)
        .isNot(instanceOf(), MapWriter.class);
    String key = firstSegment(path);
    if (path.size() == 1) {
      if (writer.map.containsKey(key)) {
        throw overwriteNotAllowed(writer, key);
      }
      writer.map.put(key, value);
    } else {
      MapWriter mw = getNestedWriter(writer, key);
      set(mw, path.shift(), value);
    }
  }

  private static Result<Object> get(MapWriter writer, Path path) {
    String key = path.segment(0);
    Object val = writer.map.get(key);
    if (val instanceof MapWriter nested) {
      if (path.size() == 1) {
        return Result.of(nested.createMap());
      }
      return get(nested, path.shift());
    } else if (path.size() == 1 && (val != null || writer.map.containsKey(key))) {
      return Result.of(nullIf(val, NULL));
    }
    return Result.none();
  }

  private static MapWriter in(MapWriter writer, Path path) {
    if (path.isEmpty()) {
      return writer;
    }
    String key = firstSegment(path);
    Path root = writer.root.append(key);
    Object val = writer.map.computeIfAbsent(key, k -> new MapWriter(root, writer));
    if (val.getClass() != MapWriter.class) {
      throw new PathOccupiedException(root, val);
    }
    return in((MapWriter) val, path.shift());
  }

  private static boolean isSet(MapWriter writer, Path path) {
    String key = firstSegment(path);
    Object val = writer.map.get(key);
    if (val == null) {
      return false;
    } else if (path.size() == 1 || !(val instanceof MapWriter)) {
      return true;
    }
    return isSet((MapWriter) val, path.shift());
  }

  private static void unset(MapWriter writer, Path path) {
    String key = firstSegment(path);
    if (path.size() == 1) {
      writer.map.remove(key);
    } else {
      unset(getNestedWriter(writer, key), path.shift());
    }
  }

  private static Map<String, Object> createMap(MapWriter writer) {
    Map<String, Object> m = new LinkedHashMap<>();
    writer.map.forEach((k, v) -> {
      if (v instanceof MapWriter mw) {
        m.put(k, createMap(mw));
      } else {
        m.put(k, nullIf(v, NULL));
      }
    });
    return m;
  }

  private static MapWriter getNestedWriter(MapWriter writer, String key) {
    Path root = writer.root.append(key);
    Object val = writer.map.computeIfAbsent(key, k -> new MapWriter(root, writer));
    if (val instanceof MapWriter mw) {
      return mw;
    }
    throw new PathOccupiedException(root, val);
  }

  private static PathOccupiedException overwriteNotAllowed(MapWriter writer,
      String key) {
    Path absPath = writer.root.append(key);
    Object curVal = writer.map.get(key);
    return new PathOccupiedException(absPath, curVal);
  }

  private static String firstSegment(Path path) {
    return segment(path, 0);
  }

  private static String segment(Path path, int index) {
    return Check.that(path.segment(index))
        .isNot(NULL(), "illegal null segment in path \"${0}\"", path)
        .has(strlen(), gt(), 0, "illegal empty segment in path \"${0}\"", path)
        .ok();
  }

}
