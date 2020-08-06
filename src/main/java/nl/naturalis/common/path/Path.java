package nl.naturalis.common.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.naturalis.common.*;
import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;
import static java.util.function.Predicate.not;

/**
 * Specifies a path to a within a {@code Map} or object. Path segments are
 * spearated by SEP (dot). For example: {@code employee.address.street}. Array
 * indices are specified as separate path segments. For example:
 * {@code employees.3.address.street} (the street that the 4th employee lives
 * on). The {@code Path} class is agnostic about whether a non-numeric path
 * segment denotes a map key or a field within an object. Therefore there are
 * zero constraints on what constitutes a valid path segment (a map key can be
 * anything, including null).
 *
 * @author Ayco Holleman
 *
 */
public final class Path implements Comparable<Path>, Iterable<String>, Sizeable, Emptyable {

  /**
   * The empty path (containing zero path segments).
   */
  public static Path EMPTY_PATH = new Path();

  /**
   * The segment separator within a path: &#39;&#46;&#39; (dot).
   */
  public static final char SEP = '.';
  /**
   * The escape character: &#39;^&#39; (circumflex). If a path segment contains
   * the segment separator, you must escape it with this character. Using the
   * backslash character as an escape character would have made it needlessly
   * complicated to write path strings in Java code. The escape character itself
   * can, but does not have to be escaped unless it is followed by the segment
   * separator. So {@code awk^ward.lastName} denotes the same path as
   * {@code awk^^ward.lastName}. Only escape a path string when passing it in its
   * entirety (as a {@code String}) to the {@link #Path(String) Path constructor}.
   * Do not escape individual path segments when passing them as a {@code String}
   * array to the {@link #Path(String[]) Path constructor}.
   */
  public static final char ESC = '^';
  /**
   * The character sequence to use for {@code null} keys: "^0". So
   * {@code lookups.^0} accesses the object with key {@code null} in the
   * {@code lookups} map.
   */
  public static final String NULL_SEGMENT = "^0";

  static boolean isArrayIndex(String segment) {
    return !segment.isEmpty() && segment.codePoints().allMatch(i -> Character.isDigit(i));
  }

  private final String[] elems;

  // Caches toString()
  private String str;
  // Caches hashCode()
  private int hash;

  /**
   * Creates a new empty {@code Path}.
   */
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
  public Path(String[] segments) {
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
   * Returns the path segment at the specified index. Specify a negative index to
   * count back from the last segment of the {@code Path} (-1 is the index of the
   * last path segment).
   *
   * @param index
   * @return
   */
  public String segment(int index) {
    int i = index < 0 ? elems.length + index : index;
    return elems[Check.index(i, elems.length)];
  }

  /**
   * Returns a new {@code Path} consisting of all segments starting with the
   * segment at the specified array index. The E.g.
   * <code>myPath.subpath(-2)</code> returns a new {@code Path} consisting of the
   * last two segments of {@code myPath}.
   *
   * @param from
   * @return
   */
  public Path subpath(int from) {
    int i = from < 0 ? elems.length - from : from;
    Check.index(i, elems.length);
    return new Path(copyOfRange(elems, i, elems.length));
  }

  /**
   * Returns a new {@code Path} consisting of the segments from the 1st array
   * index (inclusive) to the 2nd array index (exclusive). The 1st argument may be
   * negative to indicate an offset from the last segment.
   *
   * @param from
   * @return
   */
  public Path subpath(int from, int to) {
    int i = from < 0 ? elems.length - from : from;
    Check.index(i, elems.length);
    Check.index(to, i + 1, elems.length + 1);
    return new Path(copyOfRange(elems, i, to));
  }

  /**
   * Returns a new {@code Path} containing only the segments of this {@code Path}
   * that are not array indices.
   *
   * @return
   */
  public Path getPurePath() {
    return new Path(Arrays.stream(elems).filter(not(Path::isArrayIndex)).toArray(String[]::new));
  }

  /**
   * Returns a new {@code Path} consisting of the segments of this {@code Path}
   * plus the segments of the specified {@code Path}.
   *
   * @param path
   * @return
   */
  public Path append(String path) {
    Check.notNull(path, "path");
    return append(new Path(parse(path)));
  }

  /**
   * Returns a new {@code Path} consisting of the segments of this {@code Path}
   * plus the segments of the specified {@code Path}.
   *
   * @param other
   * @return
   */
  public Path append(Path other) {
    Check.notNull(other, "other");
    return new Path(ArrayMethods.concat(elems, other.elems));
  }

  /**
   * Returns a new {@code Path} with the path segment at the specified array index
   * set to the new value.
   *
   * @param index
   * @param newValue
   * @return
   */
  public Path replace(int index, String newValue) {
    Check.index(index, 0, elems.length);
    String[] copy = Arrays.copyOf(elems, elems.length);
    copy[index] = newValue;
    return new Path(copy);
  }

  /**
   * Returns a new {@code Path} with all segments of this {@code Path} except the
   * first element.
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
        return elems[Check.index(++i, elems.length)];
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

  /**
   * Returns whether or not this path contains any segments.
   */
  @Override
  public boolean isEmpty() {
    return elems.length == 0;
  }

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

  @Override
  public int hashCode() {
    if (hash == 0) {
      hash = Arrays.deepHashCode(elems);
    }
    return hash;
  }

  @Override
  public int compareTo(Path other) {
    return toString().compareTo(other.toString());
  }

  /**
   * Returns this {@code Path} as a string.
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
          if (i < path.length() - 1 && (path.charAt(i + 1) == ESC || path.charAt(i + 1) == SEP)) {
            sb.append(path.charAt(i + 1));
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

  private static String escape(String segment) {
    if (segment == null) {
      return NULL_SEGMENT;
    }
    StringBuilder sb = new StringBuilder(segment.length() << 4);
    for (int i = 0; i < segment.length(); i++) {
      switch (segment.charAt(i)) {
        case SEP:
          sb.append(ESC).append(SEP);
          break;
        case ESC:
          sb.append(ESC);
          if (i < segment.length() - 1 && segment.charAt(i + 1) == SEP) {
            sb.append(ESC);
          }
          break;
        default:
          sb.append(segment.charAt(i));
      }
    }
    return sb.toString();
  }

}
