package nl.naturalis.common.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.naturalis.common.*;
import nl.naturalis.common.check.Check;
import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;
import static java.util.function.Predicate.not;
import static nl.naturalis.common.check.CommonChecks.gte;
import static nl.naturalis.common.check.CommonChecks.lt;
import static nl.naturalis.common.check.CommonChecks.lte;

/**
 * Specifies a path to a value within an object. Path segments are spearated by '.' (dot). For
 * example: {@code employee.address.street}. Array indices are specified as separate path segments.
 * For example: {@code employees.3.address.street}. Non-numeric segments can be either field names
 * or map keys. Therefore the {@code Path} class does not impose any constraints on what constitutes
 * a valid path segment, since a map key can be anything (including null or an empty string).
 *
 * <h4>Escaping</h4>
 *
 * <p>If a path segment contains the segment separator, it must be escaped using the circumflex
 * character ('^'). (Using the backslash character as an escape character would have made it
 * needlessly cumbersome to write path strings in Java code.) The escape character itself <i>must
 * not</i> be escaped. You can let the {@link #escape(String) escape} method do the escaping for
 * you. Do not escape path segments when passing them individually, in a {@code String} array, to
 * the constructor. Only escape them when passing a complete path string. So {@code
 * "some.awk^.ward.path^string"} could also be passed in as: {@code new String[] {"some",
 * "awk.ward", "path^string"}}.
 *
 * <p>In case you need to reference the {@code null} key of a {@code Map}, use {@code "^0"}. So
 * {@code "lookups.^0.name"} references the {@code name} field of an object stored under key {@code
 * null} in the {@code lookups} map. And you could also pass this in as: {@code new String[]
 * {"lookups", null, "name"}}.
 *
 * @author Ayco Holleman
 */
public final class Path implements Comparable<Path>, Iterable<String>, Sizeable, Emptyable {

  /** The empty path (containing zero path segments). */
  public static Path EMPTY_PATH = new Path();

  /** The segment separator within a path: &#39;&#46;&#39; (dot). */
  public static final char SEP = '.';
  /** The escape character: &#39;^&#39; (circumflex). */
  public static final char ESC = '^';
  /** The character sequence to use for {@code null} keys. */
  public static final String NULL_SEGMENT = "^0";

  /**
   * Applies escaping to a path segment. Can be used to construct complete path strings. Do not use
   * when passing individual path segments as a {@code String} array to the {@link #Path(String[])
   * constructor}.
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
      switch (segment.charAt(i)) {
        case SEP:
          sb.append(ESC).append(SEP);
          break;
        default:
          sb.append(segment.charAt(i));
      }
    }
    return sb.toString();
  }

  static boolean isArrayIndex(String segment) {
    return NumberMethods.isPlainInteger(segment);
  }

  private final String[] elems;

  // Caches toString()
  private String str;
  // Caches hashCode()
  private int hash;

  /** Creates a new empty {@code Path}. */
  public Path() {
    elems = new String[0];
  }

  /**
   * Creates a new {@code Path} from the specified path string.
   *
   * @param path
   */
  public Path(String path) {
    Check.notNull(path, "path");
    elems = parse(str = path);
  }

  /**
   * Creates a new {@code Path} from the specified path segments.
   *
   * @param segments
   */
  public Path(String... segments) {
    Check.notNull(segments, "segments");
    elems = new String[segments.length];
    arraycopy(segments, 0, elems, 0, segments.length);
  }

  /**
   * Copy constructor. Creates a new {@code Path} from the specified {@code Path}.
   *
   * @param other
   */
  public Path(Path other) {
    Check.notNull(other, "other");
    elems = new String[other.elems.length];
    arraycopy(other.elems, 0, elems, 0, other.elems.length);
  }

  /**
   * Returns true if this {@code Path} consists of a single segment and that segment is null
   * (meaning that this path can only possibly be valid for map objects that allow null keys).
   * Otherwise returns false.
   *
   * @return
   */
  public boolean isNullSegment() {
    return elems.length == 1 && elems[0] == null;
  }

  /**
   * Returns the path segment at the specified index. Specify a negative index to count back from
   * the last segment of the {@code Path} (-1 returns the last path segment).
   *
   * @param index
   * @return
   */
  public String segment(int index) {
    int i = index < 0 ? elems.length + index : index;
    return Check.that(i).is(lt(), elems.length).intValue(x -> elems[x]);
  }

  /**
   * Returns a new {@code Path} starting with the segment at the specified array index. Specify a
   * negative index to count back from the last segment of the {@code Path} (-1 returns the last
   * path segment).
   *
   * @param from
   * @return
   */
  public Path subpath(int from) {
    int i = from < 0 ? elems.length + from : from;
    Check.that(i).is(lt(), elems.length);
    return new Path(copyOfRange(elems, i, elems.length));
  }

  /**
   * Returns a new {@code Path} consisting of {@code len} segments starting with segment {@code
   * from}. The 1st argument may be negative to indicate a left-offset from the last segment.
   *
   * @param from
   * @return
   */
  public Path subpath(int from, int len) {
    int i = from < 0 ? elems.length + from : from;
    int j = i + len;
    Check.that(i, "from").is(lt(), elems.length);
    Check.that(j, "from+len").is(gte(), i).is(lte(), elems.length);
    return new Path(copyOfRange(elems, i, j));
  }

  /**
   * Return the parent of this {@code Path}. If this {@code Path} is empty, this method returns
   * null. If it consists of a single segment, and empty {@code Path} is returned.
   *
   * @return The parent of this {@code Path}
   */
  public Path parent() {
    return elems.length == 0
        ? null
        : elems.length == 1 ? EMPTY_PATH : new Path(copyOfRange(elems, 0, elems.length - 1));
  }

  /**
   * Returns a new {@code Path} containing only the segments of this {@code Path} that are not array
   * indices.
   *
   * @return A new {@code Path} without any array indices
   */
  public Path getCanonicalPath() {
    return new Path(stream().filter(not(Path::isArrayIndex)).toArray(String[]::new));
  }

  /**
   * Returns a new {@code Path} consisting of the segments of this {@code Path} plus the segments of
   * the specified {@code Path}.
   *
   * @param path
   * @return
   */
  public Path append(String path) {
    Check.notNull(path, "path");
    return append(new Path(parse(path)));
  }

  /**
   * Returns a new {@code Path} consisting of the segments of this {@code Path} plus the segments of
   * the specified {@code Path}.
   *
   * @param other
   * @return
   */
  public Path append(Path other) {
    Check.notNull(other, "other");
    return new Path(ArrayMethods.concat(elems, other.elems));
  }

  /**
   * Returns a new {@code Path} with the path segment at the specified array index set to the new
   * value.
   *
   * @param index
   * @param newValue
   * @return
   */
  public Path replace(int index, String newValue) {
    Check.that(index, "index").is(gte(), 0).is(lte(), elems.length);
    String[] copy = Arrays.copyOf(elems, elems.length);
    copy[index] = newValue;
    return new Path(copy);
  }

  /**
   * Returns a new {@code Path} with all segments of this {@code Path} except the first segment.
   *
   * @return
   */
  public Path shift() {
    Check.state(elems.length != 0, "cannot shift on empty path");
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
      private int i = 0;

      public boolean hasNext() {
        return i < elems.length;
      }

      public String next() {
        if (++i < elems.length) {
          return elems[i];
        }
        throw new ArrayIndexOutOfBoundsException(i);
      }
    };
  }

  /**
   * Returns a {@code Stream} of path segments.
   *
   * @return
   */
  public Stream<String> stream() {
    return Arrays.stream(elems);
  }

  /**
   * Returns the number of segments in this {@code Path}.
   *
   * @return
   */
  @Override
  public int size() {
    return elems.length;
  }

  /** Returns whether or not this path contains any segments. */
  @Override
  public boolean isEmpty() {
    return elems.length == 0;
  }

  /** Overrides {@link Object#equals(Object) Object.equals}. */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != Path.class) {
      return false;
    }
    return Arrays.deepEquals(this.elems, ((Path) obj).elems);
  }

  /** Overrides {@link Object#hashCode() Object.hashCode}. */
  @Override
  public int hashCode() {
    if (hash == 0) {
      hash = Arrays.deepHashCode(elems);
    }
    return hash;
  }

  /** Implements {@link Comparable#compareTo(Object) Comparable.comparaTo}. */
  @Override
  public int compareTo(Path other) {
    return toString().compareTo(other.toString());
  }

  /**
   * Returns this {@code Path} as a string, properly escaped.
   *
   * @return
   */
  @Override
  public String toString() {
    if (str == null) {
      str = stream().map(Path::escape).collect(Collectors.joining("."));
    }
    return str;
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
    return elems.toArray(new String[elems.size()]);
  }
}
