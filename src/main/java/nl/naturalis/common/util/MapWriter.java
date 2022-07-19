package nl.naturalis.common.util;

import nl.naturalis.common.Result;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.path.Path;
import nl.naturalis.common.x.Param;

import java.util.*;

import static nl.naturalis.common.check.Check.fail;
import static nl.naturalis.common.check.CommonChecks.*;
import static java.util.AbstractMap.*;

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

  private static final String ERR_NULL_KEY = "Illegal null key in map at path \"${0}\"";
  private static final String ERR_BAD_KEY = "Illegal key type in map at path \"${0}\": ${type}";
  private static final String WRONG_EXIT = "expected to exit to ${arg} but arrived in ${obj}";

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
    Check.notNull(map, Param.MAP);
    this.map0 = new LinkedHashMap<>(map.size() + 10);
    this.root = Path.empty();
    this.parent = null;
    init(this, map);
  }

  private MapWriter(Path root, MapWriter parent) {
    this.root = root;
    this.map0 = new LinkedHashMap<>();
    this.parent = parent;
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
   * Returns the {@code MapWriter} for the map one level up from the map this
   * {@code MapWriter} writes to. An {@link IllegalStateException} is thrown when
   * trying to exit out of the root map. You must specify the name of the map you
   * expect to arrive in (that is, its key in the parent map). An
   * {@link IllegalArgumentException} is thrown if that is not the actual name of the
   * map.
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
   * @return the {@code MapWriter} for the map one level up from the map this
   *     {@code MapWriter} writes to
   */
  public MapWriter exit(String parentName) {
    Check.on(illegalState(), parent).is(notNull(), "already in root map");
    String actual = parent.root.segment(-1);
    Check.that(parentName).is(EQ(), actual, WRONG_EXIT);
    return parent;
  }

  /**
   * Returns the {@code MapWriter} for the root map.
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
   * <p>Sets the specified path to the specified value. It is not allowed to
   * overwrite the value of a path that has already been set, even if that value is
   * {@code null}. If necessary, use {@link #unset(String)} to unset the path's value
   * first.
   *
   * <p>It is not allowed to directly set the path to a value of type {@code Map}.
   * If you want to create a new {@code Map} at the specified path, use the
   * {@link #in(String) in} method. It is allowed, however, to set a path's value to
   * {@code null}.
   *
   * @param path the path at which to write the value
   * @param value the value
   * @return this {@code MapWriter}
   */
  public MapWriter set(String path, Object value) {
    Check.notNull(path, Param.PATH);
    set(this, Path.from(path), value);
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
   * Unsets the value of the specified path. This can be useful if you instantiated
   * the {@code MapWriter} with a pre-populated map. If any segment preceding the
   * last segment has a terminal value, or if it is not a key in the map at that
   * point the path, this method returns quietly. If the last segment <i>is</i> a
   * key, it will be removed.
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
   * modifiable and has retained the order in which the paths (now keys) were
   * written. You can continue to use the {@code MapWriter} after a call to this
   * method.
   *
   * @return the {@code Map} resulting from the write actions
   */
  public Map<String, Object> createMap() {
    return createMap(this);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static void init(MapWriter writer, Map map) {
    map.forEach((key, val) -> {
      Check.that(key)
          .is(notNull(), ERR_NULL_KEY, writer.root)
          .is(instanceOf(), String.class, ERR_BAD_KEY, writer.root);
      if (val instanceof Map nested) {
        Path root = writer.root.append(key.toString());
        MapWriter mw = new MapWriter(root, writer);
        writer.map0.put(key.toString(), mw);
        init(mw, nested);
      } else {
        writer.map0.put(key.toString(), val);
      }
    });
  }

  private static void set(MapWriter writer, Path path, Object value) {
    Check.that(value, Param.VALUE)
        .isNot(instanceOf(), Map.class)
        .isNot(instanceOf(), MapWriter.class);
    String key = path.segment(0);
    if (path.size() == 1) {
      if (!writer.map0.containsKey(key)) {
        writer.map0.put(key, value);
        return;
      }
      Path fullPath = writer.root.append(key);
      Object occupier = writer.map0.get(key);
      throw new PathOccupiedException(fullPath, occupier);
    } else {
      Path root = writer.root.append(key);
      Object val = writer.map0.computeIfAbsent(key,
          k -> new MapWriter(root, writer));
      if (val instanceof MapWriter mw) {
        set(mw, path.shift(), value);
        return;
      }
      throw new PathOccupiedException(root, val);
    }
  }

  private static Result<Object> get(MapWriter writer, Path path) {
    String key = path.segment(0);
    Object val = writer.map0.get(key);
    if (val instanceof MapWriter nested) {
      if (path.size() == 1) {
        return Result.of(nested.createMap());
      }
      return get(nested, path.shift());
    } else if (path.size() == 1 && (val != null || writer.map0.containsKey(key))) {
      return Result.of(val);
    }
    return Result.none();
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

  private static boolean isSet(MapWriter writer, Path path) {
    String key = path.segment(0);
    if (path.size() == 1) {
      return writer.map0.containsKey(key);
    } else if (writer.map0.get(key) instanceof MapWriter mw) {
      return isSet(mw, path.shift());
    }
    return false;
  }

  private static void unset(MapWriter writer, Path path) {
    String key = path.segment(0);
    if (path.size() == 1) {
      writer.map0.remove(key);
      return;
    }
    Object val = writer.map0.get(key);
    if (val instanceof MapWriter mw) {
      unset(mw, path.shift());
    }
  }

  private static Map<String, Object> createMap(MapWriter writer) {
    Map<String, Object> map = new LinkedHashMap<>();
    writer.map0.forEach((k, v) -> {
      if (v instanceof MapWriter mw) {
        map.put(k, createMap(mw));
      } else {
        map.put(k, v);
      }
    });
    return map;
  }

}
