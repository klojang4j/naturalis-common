package nl.naturalis.common.path;

import nl.naturalis.common.*;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.x.Param;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;
import static java.util.function.Predicate.not;
import static nl.naturalis.common.ArrayMethods.EMPTY_STRING_ARRAY;
import static nl.naturalis.common.ArrayMethods.implode;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * Specifies a path to a value within an object. For example:
 * {@code employee.address.street}. Path segments are separated by the dot character
 * ('.'). Array indices are specified as separate path segments. For example:
 * {@code employees.3.address.street}. Non-numeric segments can be either bean
 * properties or map keys. Therefore the {@code Path} class does not impose any
 * constraints on what constitutes a valid path segment. A map key, after all, can be
 * anything - including {@code null} and the empty string. Of course, if the path
 * segment denotes a JavaBean property, it should be a valid Java identifier.
 *
 * <h4>Escaping</h4>
 * <p>These are the escaping rules when specifying path strings:
 * <ul>
 *   <li>If a path segment represents a map key that happens to contain the
 *      segment separator ('.'), it must be escaped using the circumflex
 *      character ('^'). So a map key with the value {@code "my.awkward.map.key"}
 *      should be escaped like this: {@code "my^.awkward^.map^.key"}.
 *  <li>The escape character ('^') itself must not be escaped. Thus, if the
 *      escape character is followed by anything but a dot or the zero character
 *      (see next rule), it is just that character.
 *  <li>If a segment needs to denote a map key with value {@code null}, use this
 *      escape sequence: {@code "^0"}. So the path {@code "lookups.^0.name"}
 *      references the {@code name} field of an object stored under key
 *      {@code null} in the {@code lookups} map.
 *  <li>If a segment needs to denote a map key whose value is the empty string,
 *      simply make it a zero-length segment: {@code "lookups..name"}. This also
 *      implies that a path string that ends with a dot in fact ends with an
 *      empty (zero-length) segment.
 * </ul>
 *
 * <p>If a path segment represents a map key that happens to contain the segment
 * separator ('.'), it must be escaped using the circumflex character ('^'). So a map
 * key with the unfortunate value of {@code "my.awkward.map.key"} should be escaped
 * like this: {@code "my^.awkward^.map^.key"}. The escape character itself must not
 * be escaped. If a segment needs to denote a map key with value {@code null}, use
 * this escape sequence: {@code "^0"}. So the path {@code "lookups.^0.name"}
 * references the {@code name} field of an object stored under key {@code null} in
 * the {@code lookups} map. In case you want a segment to denote a map key whose
 * value is the empty string, simply make it a zero-length segment:
 * {@code "lookups..name"}.
 *
 * You can let the {@link #escape(String) escape} method do the escaping for you.
 *
 * <p>Do not escape path segments when passing them individually (as a {@code
 * String} array) to the constructor. Only escape them when passing a complete path
 * string.
 *
 * @author Ayco Holleman
 */
public final class Path implements Comparable<Path>, Iterable<String>, Emptyable {

  /**
   * The segment separator within a path: '.' (dot).
   */
  public static final char SEP = '.';
  /**
   * The escape character: '^' (circumflex).
   */
  public static final char ESC = '^';
  /**
   * The character sequence to use for {@code null} keys: "^0"
   */
  public static final String NULL_SEGMENT = "^0";
  /**
   * The empty path (containing zero path segments).
   */
  public static Path EMPTY_PATH = new Path();

  /**
   * Static factory method. Returns a new {@code Path} instance for the specified
   * path string.
   *
   * @param path The path
   * @return a new {@code Path} instance for the specified path string
   */
  public static Path of(String path) {
    Check.notNull(path, Param.PATH);
    return new Path(path);
  }

  public static Path copyOf(Path other) {
    return Check.notNull(other).ok(Path::new);
  }

  /**
   * Applies escaping to a path segment. Can be used to construct complete path
   * strings. <i>Do not use</i> when passing a {@code String} array containing the
   * individual path segments to the {@link #Path(String[]) constructor}. Only use
   * this method to construct valid path strings from individual path segments. You
   * only need to use this method if the segment contains a dot ('.') or the escape
   * character ('^');
   *
   * @param segment The path segment to escape
   * @return The escaped version of the segment
   */
  public static String escape(String segment) {
    if (segment == null) {
      return NULL_SEGMENT;
    } else if (segment.indexOf(SEP) == -1) {
      return segment;
    }
    StringBuilder sb = new StringBuilder(segment.length() + 4);
    for (int i = 0; i < segment.length(); i++) {
      if (segment.charAt(i) == SEP) {
        sb.append(ESC).append(SEP);
      } else {
        sb.append(segment.charAt(i));
      }
    }
    return sb.toString();
  }

  private final String[] elems;

  private String str; // Caches toString()
  private int hash; // Caches hashCode()

  // Reserved for EMPTY PATH
  private Path() {
    elems = EMPTY_STRING_ARRAY;
  }

  /**
   * Creates a {@code Path} object from the specified path string.
   *
   * @param path The path string from which to create the {@code Path}
   */
  private Path(String path) {
    elems = parse(str = path);
  }

  /**
   * Creates a {@code Path} object from the specified path segments. <i>Do not escape
   * the individual path segments.</i>
   *
   * @param segments The path segments from which to create the {@code Path}
   */
  public Path(String[] segments) {
    Check.notNull(segments);
    elems = new String[segments.length];
    arraycopy(segments, 0, elems, 0, segments.length);
  }

  /**
   * Creates a new {@code Path} object from the specified {@code Path}.
   *
   * @param other The {@code Path} to initialize this {@code Path} with
   */
  public Path(Path other) {
    Check.notNull(other);
    // Since we are immutable we can happily share state
    this.elems = other.elems;
    this.str = other.str;
    this.hash = other.hash;
  }

  private static String[] parse(String path) {
    ArrayList<String> elems = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < path.length(); i++) {
      switch (path.charAt(i)) {
        case SEP:
          String s = sb.toString();
          elems.add(s.equals(NULL_SEGMENT) ? null : s);
          sb.setLength(0);
          break;
        case ESC:
          if (i < path.length() - 1 && path.charAt(i + 1) == SEP) {
            sb.append(SEP);
            ++i;
          } else {
            sb.append(ESC);
          }
          break;
        default:
          sb.append(path.charAt(i));
      }
    }
    if (sb.length() > 0) {
      String s = sb.toString();
      elems.add(s.equals(NULL_SEGMENT) ? null : s);
    } else if (path.endsWith(".")) {
      elems.add(StringMethods.EMPTY);
    }
    return elems.toArray(String[]::new);
  }

  /**
   * Returns the path segment at the specified index. Specify a negative index to
   * count back from the last segment of the {@code Path} (-1 returns the last path
   * segment).
   *
   * @param index The array index of the path segment
   * @return The path segment at the specified index.
   */
  public String segment(int index) {
    int i = index < 0
        ? elems.length + index
        : index;
    return Check.that(i).is(lt(), elems.length).ok(x -> elems[x]);
  }

  /**
   * Returns a new {@code Path} starting with the segment at the specified array
   * index. Specify a negative index to count back from the last segment of the
   * {@code Path} (-1 returns the last path segment).
   *
   * @param offset The index of the first segment of the new {@code Path}
   * @return A new {@code Path} starting with the segment at the specified array
   *     index
   */
  public Path subpath(int offset) {
    int from = offset < 0
        ? elems.length + offset
        : offset;
    Check.that(from).is(lt(), elems.length);
    return new Path(copyOfRange(elems, from, elems.length));
  }

  /**
   * Returns a new {@code Path} consisting of {@code length} segments starting with
   * segment {@code offset}. The first argument may be negative to indicate a
   * left-offset from the last segment.
   *
   * @param offset The index of the first segment of the new {@code Path}
   * @param length The number of segments in the new {@code Path}
   * @return A new {@code Path} consisting of {@code len} segments starting with
   *     segment {@code from}.
   */
  public Path subpath(int offset, int length) {
    int from = offset < 0
        ? elems.length + offset
        : offset;
    int to = Check.offsetLength(elems.length, from, length);
    return new Path(copyOfRange(elems, from, to));
  }

  /**
   * Return the parent of this {@code Path}. If this {@code Path} is empty, this
   * method returns null. If it consists of a single segment, and empty {@code Path}
   * is returned.
   *
   * @return The parent of this {@code Path}
   */
  public Path parent() {
    return elems.length == 0
        ? null
        : elems.length == 1
            ? EMPTY_PATH
            : new Path(copyOfRange(elems, 0, elems.length - 1));
  }

  /**
   * Returns a new {@code Path} containing only the segments of this {@code Path}
   * that are not array indices.
   *
   * @return A new {@code Path} without any array indices
   */
  public Path getCanonicalPath() {
    return new Path(stream().filter(not(NumberMethods::isInt))
        .toArray(String[]::new));
  }

  /**
   * Returns a new {@code Path} representing the concatenation of this {@code Path}
   * and the specified {@code Path}.
   *
   * @param path The path to append to this {@code Path}
   * @return A new {@code Path} representing the concatenation of this {@code Path}
   *     and the specified {@code Path}
   */
  public Path append(String path) {
    Check.notNull(path, "path");
    return append(new Path(parse(path)));
  }

  /**
   * Returns a new {@code Path} consisting of the segments of this {@code Path} plus
   * the segments of the specified {@code Path}.
   *
   * @param other The {@code Path} to append to this {@code Path}.
   * @return A new {@code Path} consisting of the segments of this {@code Path} plus
   *     the segments of the specified {@code Path}
   */
  public Path append(Path other) {
    Check.notNull(other, "other");
    return new Path(ArrayMethods.concat(elems, other.elems));
  }

  /**
   * Returns a new {@code Path} with the path segment at the specified array index
   * set to the new value.
   *
   * @param index The array index of the segment to replace
   * @param newValue The new segment
   * @return A new {@code Path} with the path segment at the specified array index
   *     set to the new value
   */
  public Path replace(int index, String newValue) {
    Check.that(index, "index").is(gte(), 0).is(lte(), elems.length);
    String[] copy = Arrays.copyOf(elems, elems.length);
    copy[index] = newValue;
    return new Path(copy);
  }

  /**
   * Returns a new {@code Path} with all segments of this {@code Path} except the
   * first segment.
   *
   * @return
   */
  public Path shift() {
    Check.on(illegalState(), elems.length).is(ne(), 0, "cannot shift on empty path");
    if (elems.length == 1) {
      return EMPTY_PATH;
    }
    String[] shifted = new String[elems.length - 1];
    arraycopy(elems, 1, shifted, 0, elems.length - 1);
    return new Path(shifted);
  }

  /**
   * Returns an {@code Iterator} over the path segments.
   *
   * @return
   */
  @Override
  public Iterator<String> iterator() {
    return new Iterator<>() {
      private int i;

      public boolean hasNext() {
        return i < elems.length;
      }

      public String next() {
        if (i < elems.length) {
          return elems[i++];
        }
        throw new ArrayIndexOutOfBoundsException(i);
      }
    };
  }

  /**
   * Returns a {@code Stream} of path segments.
   *
   * @return A {@code Stream} of path segments
   */
  public Stream<String> stream() {
    return Arrays.stream(elems);
  }

  /**
   * Returns the number of segments in this {@code Path}.
   *
   * @return The number of segments in this {@code Path}
   */
  public int size() {
    return elems.length;
  }

  public boolean isEmpty() {
    return elems.length == 0;
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj || (obj instanceof Path p && Arrays.deepEquals(elems,
        p.elems));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    if (hash == 0) {
      hash = Arrays.deepHashCode(elems);
    }
    return hash;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(Path other) {
    Check.notNull(other);
    return Arrays.compare(elems, other.elems);
  }

  /**
   * Returns this {@code Path} as a string, properly escaped.
   *
   * @return This {@code Path} as a string, properly escaped
   */
  @Override
  public String toString() {
    if (str == null) {
      str = implode(elems, Path::escape, ".", 0, elems.length);
    }
    return str;
  }

}
