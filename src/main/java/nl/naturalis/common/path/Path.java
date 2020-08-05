package nl.naturalis.common.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import nl.naturalis.common.*;
import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;
import static java.util.function.Predicate.not;

/**
 *
 * Specifies a path to a within a {@code Map} or object. Path segments are
 * spearated by '.' (dot). For example: {@code address.streetName}. Array
 * indices are specified as separate path segments. For example:
 * {@code employees.3.address.streetName} (the street that the 4th employee
 * lives on).
 *
 *
 * @author Ayco Holleman
 *
 */
public final class Path implements Comparable<Path>, Sizeable, Emptyable {

  public static Path EMPTY_PATH = new Path();

  private final String[] elems;

  // Caches toString()
  private String str;
  // Caches hashCode()
  private int hash;

  /**
   * Creates a new empty {@code Path}.
   */
  private Path() {
    elems = new String[0];
  }

  /**
   * Creates a new {@code Path} from the specified path string.
   *
   * @param path
   */
  public Path(String path) {
    Check.notNull(path, "path");
    elems = split(str = path);
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
    this(Check.notNull(other, "other").elems);
  }

  /**
   * Returns the path segment at the specified index. Specify a negative index to
   * count back from the last segment of the {@code Path}.
   *
   * @param index
   * @return
   */
  public String segment(int index) {
    int i = index < 0 ? elems.length - index : index;
    Check.index(i, elems.length);
    return elems[i];
  }

  /**
   * Returns all segments from the specified index as a new {@code Path}. Specify
   * a negative index to count back from the last segment of the {@code Path}.
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
   * Returns all segments from the specified index as a new {@code Path}. Specify
   * a negative index to count back from the last segment of the {@code Path}.
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
    return new Path(Arrays.stream(elems).filter(not(this::isInteger)).toArray(String[]::new));
  }

  /**
   * Returns a new {@code Path} consisting of the segments of this {@code Path}
   * plus the segments of the specified {@code Path}. NB {@code Path} being an
   * immutable class, both this {@code Path} and the specified {@code Path} remain
   * unchanged.
   *
   * @param path
   * @return
   */
  public Path append(String path) {
    Check.notNull(path, "path");
    return append(new Path(split(path)));
  }

  /**
   * Returns a new {@code Path} consisting of the segments of this {@code Path}
   * plus the segments of the specified {@code Path}. NB {@code Path} being an
   * immutable class, both this {@code Path} and the specified {@code Path} remain
   * unchanged.
   *
   * @param other
   * @return
   */
  public Path append(Path other) {
    Check.notNull(other, "other");
    return new Path(ArrayMethods.concat(elems, other.elems));
  }

  /**
   * Returns a new {@code Path} with the path segment at the specified index set
   * to the new value.
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
   * Returns a new {@code Path} with the segments of this {@code Path} minus its
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
   * Returns the number of path segments in this {@code Path}.
   *
   * @return
   */
  @Override
  public int size() {
    return elems.length;
  }

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
      str = Arrays.stream(elems).collect(Collectors.joining("."));
    }
    return str;
  }

  private static String[] split(String path) {
    ArrayList<String> elems = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < path.length(); i++) {
      if (path.charAt(i) == '.') {
        elems.add(sb.toString());
        sb.setLength(0);
      } else {
        sb.append(path.charAt(i));
      }
    }
    if (sb.length() > 0) {
      elems.add(sb.toString());
    } else if (path.endsWith(".")) {
      elems.add(StringMethods.EMPTY);
    }
    return elems.toArray(new String[elems.size()]);
  }

  private boolean isInteger(String s) {
    return !s.isEmpty() && s.codePoints().allMatch(i -> Character.isDigit(i));
  }

}
