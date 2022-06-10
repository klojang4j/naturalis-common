package nl.naturalis.common.util;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.path.Path;

import java.util.LinkedHashMap;
import java.util.Map;

import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.path.Path.EMPTY_PATH;

/**
 * Provides a convenient way of writing <i>maps-within-maps</i> ({@code Map<String,
 * Object>} objects). It lets you write deeply nested values without having to create
 * the intermediate maps first. If they are missing, they will be tacitly created.
 *
 * <h4>Example 1:</h4>
 *
 * <blockquote>
 *
 * <pre>{@code
 * MapWriter mw = new MapWriter();
 * mw.write("person.address.street", "12 Revolutionary Rd.")
 *  .write("person.address.state", "CA")
 *  .write("person.firstName", "John")
 *  .write("person.lastName", "Smith")
 *  .write("person.dateOfBirth", LocalDate.of(1967, 4, 4));
 * Map<String, Object> map = mw.getMap();
 * }</pre>
 *
 * </blockquote>
 *
 * <p>
 *
 * <h4>Example 2:</h4>
 *
 * <blockquote>
 *
 * <pre>{@code
 * Map<String, Object> map = new MapWriter()
 *  .in("person")
 *    .write("firstName", "John")
 *    .write("lastName", "Smith")
 *    .write("dateOfBirth", LocalDate.of(1967, 4, 4))
 *    .in("address")
 *      .write("street", "12 Revolutionary Rd.")
 *      .write("state", "CA")
 *  .getMap();
 * }</pre>
 *
 * </blockquote>
 *
 * @author Ayco Holleman
 */
public final class MapWriter {

  private static final String ERR_NULL_KEY = "Illegal null key in map at path "
      + "\"${0}\"";
  private static final String ERR_BAD_KEY = "Illegal key type in map at path "
      + "\"${0}\": ${type}";

  /**
   * Thrown if you try to write to a path that extends beyond a path that is set to a
   * terminal value (a non-Map value).
   *
   * @author Ayco Holleman
   */
  public static class PathBlockedException extends IllegalArgumentException {

    private PathBlockedException(Path path, Object value) {
      super(String.format("Key %s already written: %s", path, value));
    }

  }

  private final Path root;
  private final Map<String, Object> map0;

  /**
   * Creates a {@code MapWriter} that lets you start with a clean slate.
   */
  public MapWriter() {
    this(EMPTY_PATH);
  }

  /**
   * Creates a {@code MapWriter} that lets you start with the entries in the
   * specified map. The map is read, but not modified.
   *
   * @param map The initial {@code Map}
   */
  public MapWriter(Map<String, Object> map) {
    Check.notNull(map);
    this.root = EMPTY_PATH;
    this.map0 = new LinkedHashMap<>(map.size());
    init(this, map);
  }

  /**
   * Creates a {@code MapWriter} that lets you start with a clean slate. All paths
   * passed to the {@link #in(String) in} and {@link #set(String, Object)} methods
   * are taken relative to specified root path.
   *
   * @param root The root path
   */
  private MapWriter(Path root) {
    this(new LinkedHashMap<>(), root);
  }

  /**
   * Creates a {@code MapWriter} that lets you start with the entries in the
   * specified map. The map is read, but not modified. All paths passed to the {@link
   * #in(String) in} and {@link #set(String, Object)} methods are taken relative to
   * specified root path.
   *
   * @param map The initial map
   * @param root The root path
   */
  private MapWriter(Map<String, Object> map, Path root) {
    this.root = root;
    this.map0 = map;
  }

  /**
   * Starts a new (nested) map at the specified path, relative to the root path
   * specified through the constructor. Intermediate maps will be created as and when
   * necessary.
   *
   * @param path A path relative to the root path specified through the
   *     constructor
   * @return This {@code MapWriter}
   */
  public MapWriter in(String path) {
    Check.that(path, "path").isNot(empty());
    return in(this, new Path(path));
  }

  /**
   * Sets the key corresponding to the specified path to the specified value.
   * Intermediate maps will be created as and when necessary. It is not allowed to
   * pass values of type {@link Map} or {@code MapWriter}. Use the {@link #in(String)
   * in} method to start a new {@code Map}. Null values are allowed.
   *
   * @param path The path at which to write the value
   * @param value The value
   * @return This {@code MapWriter}
   */
  public MapWriter set(String path, Object value) {
    return set(new Path(path), value);
  }

  /**
   * Sets the key corresponding to the specified path to the specified value.
   * Intermediate maps will be created as and when necessary. Values must not be
   * instances of {@link Map} or {@code MapWriter}. Use the {@link #in(String) in}
   * method to start a new {@code Map}. Null values are allowed.
   *
   * @param path The path at which to write the value
   * @param value The value
   * @return This {@code MapWriter}
   */
  public MapWriter set(Path path, Object value) {
    Check.notNull(path);
    set(this, path, value);
    return this;
  }

  /**
   * Returns whether the specified path is set to a terminal value (and hence cannot
   * be extended).
   *
   * @param path The path
   * @return Whether it is set to a terminal value
   */
  public boolean isSet(String path) {
    return isSet(this, new Path(path));
  }

  /**
   * Returns the {@code Map} resulting from the write actions. You can continue to
   * use the {@code MapWriter} after a call to this method.
   *
   * @return The {@code Map} resulting from the write actions
   */
  public Map<String, Object> getMap() {
    return getMap(this);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static void init(MapWriter writer, Map map) {
    map.forEach((k, v) -> {
      Check.that(k)
          .is(notNull(), ERR_NULL_KEY, writer.root)
          .is(instanceOf(), String.class, ERR_BAD_KEY, writer.root);
      String key = (String) k;
      if (v instanceof Map) {
        Map map0 = (Map) v;
        Path root = writer.root.append(key);
        MapWriter mw = new MapWriter(new LinkedHashMap(map0.size()), root);
        writer.map0.put(key, mw);
        init(mw, map0);
      } else {
        writer.map0.put(key, v);
      }
    });
  }

  private static void set(MapWriter writer, Path relPath, Object value) {
    if (value != null) {
      Check.that(value, "value")
          .isNot(instanceOf(), Map.class)
          .isNot(instanceOf(), MapWriter.class);
    }
    if (relPath.size() == 1) {
      writer.map0.put(relPath.toString(), value);
    } else {
      String key = relPath.segment(0);
      Path root = writer.root.append(key);
      Object val = writer.map0.computeIfAbsent(key, k -> new MapWriter(root));
      if (val.getClass() != MapWriter.class) {
        throw new PathBlockedException(root, val);
      }
      set((MapWriter) val, relPath.shift(), value);
    }
  }

  private static MapWriter in(MapWriter writer, Path relPath) {
    if (relPath.isEmpty()) {
      return writer;
    }
    String key = relPath.segment(0);
    Path root = writer.root.append(key);
    Object val = writer.map0.computeIfAbsent(key, k -> new MapWriter(root));
    if (val.getClass() != MapWriter.class) {
      throw new PathBlockedException(root, val);
    }
    return in((MapWriter) val, relPath.shift());
  }

  private static boolean isSet(MapWriter writer, Path relPath) {
    String key = relPath.segment(0);
    if (relPath.size() == 1) {
      return writer.map0.containsKey(key);
    }
    Object val = writer.map0.get(key);
    if (val.getClass() != MapWriter.class) {
      return true;
    }
    return isSet((MapWriter) val, relPath.shift());
  }

  private static Map<String, Object> getMap(MapWriter writer) {
    Map<String, Object> map = new LinkedHashMap<>(writer.map0.size());
    writer.map0.forEach((k, v) -> {
      if (v instanceof MapWriter) {
        map.put(k, getMap((MapWriter) v));
      } else {
        map.put(k, v);
      }
    });
    return map;
  }

}
