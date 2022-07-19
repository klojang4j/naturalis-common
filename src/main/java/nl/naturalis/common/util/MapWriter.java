package nl.naturalis.common.util;

import nl.naturalis.common.StringMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.path.Path;
import nl.naturalis.common.x.Param;

import java.util.LinkedHashMap;
import java.util.Map;

import static nl.naturalis.common.check.Check.fail;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * <p>Provides a convenient way of writing {@code Map<String, Object>}
 * pseudo-objects. Useful when serializing the maps again to JSON or some other
 * hierarchical format as it guarantees well-formedness. A {@code MapWriter} lets you
 * write deeply nested values without having to create the intermediate maps first.
 * If they are missing, they will be tacitly created.
 *
 * <p>Internally, a {@code MapWriter} works with {@link Path} objects. See the
 * documentation for the {@code Path} class for how to specify a path.
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
 * Map<String, Object> map = mw.getMap();
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
 *  .getMap();
 * }</pre></blockquote>
 *
 * @author Ayco Holleman
 */
public final class MapWriter {

  private static final String ERR_NULL_KEY = "Illegal null key in map at path \"${0}\"";
  private static final String ERR_BAD_KEY = "Illegal key type in map at path \"${0}\": ${type}";
  private static final String WRONG_EXIT = "expected to exit to {arg} but arrived in {obj}";

  private final Map<String, Object> map0;
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
    this(map, StringMethods.EMPTY);
  }

  /**
   * Creates a {@code MapWriter} that starts out with the entries in the specified
   * map. The map is read, but not modified. All subsequent write actions will be
   * relative to the specified root path. That is, the path you pass to the
   * {@link #in(String) in} and {@link #set(String, Object)} methods are appended to
   * the root path. In other words you will be prevented from writing "above" the
   * root path. If there is no nested map yet at the specified path, it will be
   * created. If the specified map is already occupied by some other value, a
   * {@link PathOccupiedException} is thrown.
   *
   * <p>Do not end the root path with a dot ('.'), unless you really intend to write
   * to a nested map that is itself keyed on the empty string.
   *
   * @param map
   * @param rootPath
   */
  public MapWriter(Map<String, Object> map, String rootPath) {
    Check.notNull(map, Param.MAP);
    Check.notNull(rootPath, "root path");
    this.root = Path.from("rootPath");
    this.map0 = new LinkedHashMap<>(map.size() * 2);
    this.parent = null;
    init(this, map);
  }

  private MapWriter(Path root, MapWriter parent) {
    this(new LinkedHashMap<>(), root, parent);
  }

  private MapWriter(Map<String, Object> map, Path root, MapWriter parent) {
    this.root = root;
    this.map0 = map;
    this.parent = parent;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static void init(MapWriter writer, Map map) {
    map.forEach((key, val) -> {
      Check.that(key)
          .is(notNull(), ERR_NULL_KEY, writer.root)
          .is(instanceOf(), String.class, ERR_BAD_KEY, writer.root);
      if (val instanceof Map nested) {
        Path root = writer.root.append(key.toString());
        MapWriter mw = new MapWriter(new LinkedHashMap(nested.size()), root, writer);
        writer.map0.put(key.toString(), mw);
        init(mw, nested);
      } else {
        writer.map0.put(key.toString(), val);
      }
    });
  }

  private static void set(MapWriter writer, Path relPath, Object value) {
    Check.that(value, Param.VALUE)
        .isNot(instanceOf(), Map.class)
        .isNot(instanceOf(), MapWriter.class);
    if (relPath.size() == 1) {
      writer.map0.put(relPath.toString(), value);
    } else {
      String key = relPath.segment(0);
      Path root = writer.root.append(key);
      Object val = writer.map0.computeIfAbsent(key,
          k -> new MapWriter(root, writer));
      if (val.getClass() != MapWriter.class) {
        throw new PathOccupiedException(root, val);
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
    Object val = writer.map0.computeIfAbsent(key, k -> new MapWriter(root, writer));
    if (val.getClass() != MapWriter.class) {
      throw new PathOccupiedException(root, val);
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
      if (v instanceof MapWriter mw) {
        map.put(k, getMap(mw));
      } else {
        map.put(k, v);
      }
    });
    return map;
  }

  /**
   * Creates a new, nested map at the specified path. The path is taken to be
   * relative to the root path specified through the constructor. Intermediate maps
   * will be created as and when needed. If there already is a map at the specified
   * location, this method does nothing (the map is not replaced). If there already
   * is some other type of value at the specified location, a
   * {@link PathOccupiedException} is thrown. The returned {@code MapWriter} is not
   * <i>this</i> {@code MapWriter}, but a {@code MapWriter} for the map found or
   * created at the specified location.
   *
   * @param path the path, relative to the root path, at which to create a new,
   *     nested map
   * @return a {@code MapWriter} for the map found or created at the specified
   *     location
   */
  public MapWriter in(String path) {
    Check.notNull(path, Param.PATH);
    return in(this, Path.from(path));
  }

  /**
   * Returns a {@code MapWriter} for the map one level up from the map this
   * {@code MapWriter} writes to. An {@link IllegalStateException} is thrown when
   * trying to exit out of the root map. You must specify the name (i.e. key) of the
   * map you expect to arrive in. An {@link IllegalArgumentException} is thrown if
   * that is not the actual name of the map. This ensures you will not accidentally
   * start writing to the wrong map.
   *
   * <blockquote><pre>{@code
   * Map<String, Object> map = new MapWriter()
   *  .in("person")
   *    .write("firstName", "John")
   *    .write("lastName", "Smith")
   *    .in("address")
   *      .write("street", "12 Revolutionary Rd.")
   *      .write("state", "CA")
   *      .exit("person")
   *    .write("dateOfBirth", LocalDate.of(1967, 4, 4))
   *  .getMap();
   * }</pre></blockquote>
   *
   * @return a {@code MapWriter} for the map one level up from the map this
   *     {@code MapWriter} writes to
   */
  public MapWriter exit(String parentName) {
    Check.that(parent).is(notNull(),
        () -> new IllegalStateException("already in root map"));
    String actual = parent.root.segment(-1);
    Check.that(parentName).is(EQ(), actual, WRONG_EXIT);
    return parent;
  }

  /**
   * Returns a {@code MapWriter} for the root map. Note, though, that if the root map
   * writer was
   *
   * @return a {@code MapWriter} for the root map
   */
  public MapWriter reset() {
    MapWriter mw = this;
    while (mw.parent != null) {
      mw = mw.parent;
    }
    return mw;
  }

  /**
   * Sets the specified path to the specified value. The path is taken to be relative
   * to the root path specified through the constructor. It is not allowed to pass
   * values of type {@link Map} or {@code MapWriter}. Use the {@link #in(String) in}
   * method to start a new {@code Map}. It is allowed to set a path's value to
   * {@code null}.
   *
   * @param path the path at which to write the value
   * @param value the value
   * @return this {@code MapWriter}
   */
  public MapWriter set(String path, Object value) {
    Check.notNull(path, Param.PATH);
    return set(Path.from(path), value);
  }

  /**
   * Sets the specified path to the specified value. The path is taken to be relative
   * to the root path specified through the constructor. It is not allowed to pass
   * values of type {@link Map} or {@code MapWriter}. Use the {@link #in(String) in}
   * method to start a new {@code Map}. It is allowed to set a path's value to
   * {@code null}.
   *
   * @param path the path at which to write the value
   * @param value the value
   * @return this {@code MapWriter}
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
   * @param path the path
   * @return whether it is set to a terminal value
   */
  public boolean isSet(String path) {
    return isSet(this, Path.from(path));
  }

  /**
   * Returns the {@code Map} resulting from the write actions. The returned map is
   * modifiable and has retained the order in which the paths (now keys) were
   * written. You can continue to use the {@code MapWriter} after a call to this
   * method.
   *
   * @return the {@code Map} resulting from the write actions
   */
  public Map<String, Object> getMap() {
    return getMap(this);
  }

  /**
   * Thrown when attempting to write to a path that extends beyond a path with a
   * terminal value (anything that is not a {@code Map}).
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

}
