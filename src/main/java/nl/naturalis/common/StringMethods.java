package nl.naturalis.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ArrayMethods.END_INDEX;
import static nl.naturalis.common.ArrayMethods.START_INDEX;
import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.ObjectMethods.ifNull;
import static nl.naturalis.common.ObjectMethods.ifTrue;
import static nl.naturalis.common.check.Check.fail;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * Methods for working with strings. Most methods are friendly towards batch-wise print jobs, trying
 * to make the best of their input. They are are null-safe with respect to the string to be
 * manipulated and they never return null (unless indicated otherwise). The string to be manipulated
 * is named {@code subject} and is passed in as an {@link Object}. If the argument is null, an empty
 * string is returned. Otherwise manipulation is done on the string resulting from {@link
 * Object#toString() Object.toString}.
 */
public final class StringMethods {

  /** The empty string. */
  public static final String EMPTY = "";

  private StringMethods() {}

  public static StringBuilder append(StringBuilder sb, Object data0) {
    Check.notNull(sb, "sb");
    return sb.append(data0);
  }

  public static StringBuilder append(StringBuilder sb, Object data0, Object data1) {
    Check.notNull(sb, "sb");
    return sb.append(data0).append(data1);
  }

  public static StringBuilder append(StringBuilder sb, Object data0, Object data1, Object data2) {
    Check.notNull(sb, "sb");
    return sb.append(data0).append(data1).append(data2);
  }

  public static StringBuilder append(
      StringBuilder sb, Object data0, Object data1, Object data2, Object data3) {
    Check.notNull(sb, "sb");
    return sb.append(data0).append(data1).append(data2).append(data3);
  }

  public static StringBuilder append(
      StringBuilder sb, Object data0, Object data1, Object data2, Object data3, Object data4) {
    Check.notNull(sb, "sb");
    return sb.append(data0).append(data1).append(data2).append(data3).append(data4);
  }

  public static StringBuilder append(
      StringBuilder sb,
      Object data0,
      Object data1,
      Object data2,
      Object data3,
      Object data4,
      Object data5) {
    Check.notNull(sb, "sb");
    return sb.append(data0).append(data1).append(data2).append(data3).append(data4).append(data5);
  }

  public static StringBuilder append(
      StringBuilder sb,
      Object data0,
      Object data1,
      Object data2,
      Object data3,
      Object data4,
      Object data5,
      Object data6) {
    Check.notNull(sb, "sb");
    return sb.append(data0)
        .append(data1)
        .append(data2)
        .append(data3)
        .append(data4)
        .append(data5)
        .append(data6);
  }

  public static StringBuilder append(
      StringBuilder sb,
      Object data0,
      Object data1,
      Object data2,
      Object data3,
      Object data4,
      Object data5,
      Object data6,
      Object data7) {
    Check.notNull(sb, "sb");
    return sb.append(data0)
        .append(data1)
        .append(data2)
        .append(data3)
        .append(data4)
        .append(data5)
        .append(data6)
        .append(data7);
  }

  public static StringBuilder append(
      StringBuilder sb,
      Object data0,
      Object data1,
      Object data2,
      Object data3,
      Object data4,
      Object data5,
      Object data6,
      Object data7,
      Object data8) {
    Check.notNull(sb, "sb");
    return sb.append(data0)
        .append(data1)
        .append(data2)
        .append(data3)
        .append(data4)
        .append(data5)
        .append(data6)
        .append(data7)
        .append(data8);
  }

  public static StringBuilder append(
      StringBuilder sb,
      Object data0,
      Object data1,
      Object data2,
      Object data3,
      Object data4,
      Object data5,
      Object data6,
      Object data7,
      Object data8,
      Object data9,
      Object... moreData) {
    Check.notNull(sb, "sb");
    sb.append(data0)
        .append(data1)
        .append(data2)
        .append(data3)
        .append(data4)
        .append(data5)
        .append(data6)
        .append(data7)
        .append(data8)
        .append(data9);
    Check.notNull(moreData, "data").ok(Arrays::stream).forEach(sb::append);
    return sb;
  }

  /**
   * Concatenates the specified data.
   *
   * @param data The data to append (must not be null)
   * @return The concatenation of the data
   */
  public static String concat(Object... data) {
    Check.notNull(data);
    StringBuilder sb = new StringBuilder(10 * data.length);
    Arrays.stream(data).forEach(sb::append);
    return sb.toString();
  }

  public static int count(Object subject, char c, int stopAfter) {
    Check.that(stopAfter).is(positive());
    if (subject == null) {
      return 0;
    }
    String str = subject.toString();
    int x = 0;
    for (int i = 0; i < str.length() && x < stopAfter; ++i) {
      if (str.charAt(i) == c) {
        ++x;
      }
    }
    return x;
  }

  /**
   * Counts the number of occurrences of {@code substr} within {@code subject}. The string to search
   * for must not be null or empty and is not treated as a regular expression.
   *
   * @param subject The string to search
   * @param substr The substring to search for
   * @return The number of occurrences of {@code substr} within {@code subject}
   */
  public static int count(Object subject, String substr) {
    return count(subject, substr, false);
  }

  /**
   * Counts the number of occurrences of {@code substr} within {@code subject}. The string to search
   * for must not be null or empty and is not treated as a regular expression.
   *
   * @param subject The string to search
   * @param substr The substring to search for
   * @param ignoreCase Whether or not to ignore case while matching {@code substr} against {@code
   *     subject}
   * @return The number of occurrences of {@code substr} within {@code subject}
   */
  public static int count(Object subject, String substr, boolean ignoreCase) {
    Check.that(substr, "substr").isNot(empty());
    if (subject == null) {
      return 0;
    }
    String str = ifTrue(ignoreCase, subject.toString(), String::toLowerCase);
    substr = ifTrue(ignoreCase, substr, String::toLowerCase);
    int i = 0;
    int j = str.indexOf(substr);
    while (j != -1) {
      ++i;
      j = str.indexOf(substr, j + 1);
    }
    return i;
  }

  /**
   * Counts the number of non-overlapping occurrences of {@code substr} within {@code subject}. The
   * string to search for must not be null or empty and is not treated as a regular expression.
   *
   * @param subject The string to search
   * @param substr The substring to search for
   * @return The number of non-overlapping occurrences of {@code substr} within {@code subject}
   */
  public static int countDiscrete(Object subject, String substr) {
    return countDiscrete(subject, substr, false);
  }

  /**
   * Counts the number of non-overlapping occurrences of {@code substr} within {@code subject}. The
   * string to search for must not be null or empty and is not treated as a regular expression.
   *
   * @param subject The string to search
   * @param substr The substring to search for
   * @param ignoreCase Whether or not to ignore case while matching {@code substr} against {@code
   *     subject}
   * @return The number of non-overlapping occurrences of {@code substr} within {@code subject}
   */
  public static int countDiscrete(Object subject, String substr, boolean ignoreCase) {
    Check.that(substr, "substr").isNot(empty());
    if (subject == null) {
      return 0;
    }
    String str = ifTrue(ignoreCase, subject.toString(), String::toLowerCase);
    substr = ifTrue(ignoreCase, substr, String::toLowerCase);
    int i = 0;
    int j = str.indexOf(substr);
    while (j != -1) {
      ++i;
      j = str.indexOf(substr, j + substr.length());
    }
    return i;
  }

  /**
   * Returns {@code subject.toString()} if its length does not exceed {@code maxWidth}, else
   * truncates the string and appends "...", such that the new string's length does not exceed
   * {@code maxWidth}.
   *
   * <h4>Examples:</h4>
   *
   * <p>
   *
   * <pre>
   * String hello = "Hello World, how are you?";
   * assertEquals("Hello W...", ellipsis(hello, 10));
   * assertEquals("H...", ellipsis(hello, 4));
   * assertEquals(hello, ellipsis(hello, 100));
   * </pre>
   *
   * @param subject The string to abbreviate, if necessary
   * @param maxWidth The maximum width of the string (must be greater than 3)
   * @return The string itself or an abbreviated version, suffixed with "..."
   */
  public static String ellipsis(Object subject, int maxWidth) {
    String str = Check.notNull(subject, "subject").ok().toString();
    Check.that(maxWidth, "maxWidth").is(gt(), 3);
    if (str.length() <= maxWidth) {
      return str;
    }
    int to = Math.max(0, maxWidth - 3);
    return str.substring(0, to) + "...";
  }

  /**
   * Returns a human-friendly representation of the duration between the specified start and now.
   * Example: 540:00:12.630
   *
   * @param start The start time
   * @return A human-friendly representation of the duration between the specified start and now
   */
  public static String interval(long start) {
    return interval(start, System.currentTimeMillis());
  }

  /**
   * Returns a human-friendly representation of the duration of the specified time interval.
   * Example: 00:08:07.041
   *
   * @param start The start time
   * @param end The end time
   * @return A human-friendly representation of the duration of the specified time interval
   */
  public static String interval(long start, long end) {
    Check.that(end).is(atLeast(), start, "Negative time interval");
    return duration(end - start);
  }

  /**
   * Returns a human-friendly representation of the specified duration in milliseconds.
   *
   * @param millis The duration in millisecond
   * @return A human-friendly representation of the specified duration in milliseconds
   */
  public static String duration(long millis) {
    long h = millis / (60 * 60 * 1000);
    millis %= (60 * 60 * 1000);
    long m = millis / (60 * 1000);
    millis %= (60 * 1000);
    long s = millis / 1000;
    millis %= 1000;
    return append(
            new StringBuilder(12),
            lpad(h, 2, '0', ":"),
            lpad(m, 2, '0', ":"),
            lpad(s, 2, '0', "."),
            lpad(millis, 3, '0'))
        .toString();
  }

  /**
   * Whether or not the specified string ends with any of the specified suffixes. They suffixes must
   * not be null or empty.
   *
   * @param subject The string to test
   * @param ignoreCase Whether or not to ignore case
   * @param suffixes The suffixes to test
   * @return The first suffix found to be equal to the end of the string, or null if the string
   *     ended in none of the specified suffixes
   */
  public static String endsWith(Object subject, boolean ignoreCase, Collection<String> suffixes) {
    Check.notNull(suffixes, "suffixes");
    return endsWith(subject, ignoreCase, suffixes.toArray(new String[suffixes.size()]));
  }

  /**
   * Whether or not {@code subject} ends with any of the specified suffixes. They suffixes must not
   * be null or empty. Returns the first suffix found to be equal to the end of the string, or null
   * if the string ended in none of the specified suffixes.
   *
   * @param subject The string to test
   * @param ignoreCase Whether or not to ignore case
   * @param suffixes The suffixes to test
   * @return The first suffix found to be equal to the end of the string, or null if the string
   *     ended in none of the specified suffixes
   */
  public static String endsWith(Object subject, boolean ignoreCase, String... suffixes) {
    Check.that(suffixes, "suffixes").is(deepNotEmpty());
    if (subject != null) {
      String str = subject.toString();
      for (String suf : suffixes) {
        if (str.regionMatches(ignoreCase, str.length() - suf.length(), suf, 0, suf.length())) {
          return suf;
        }
      }
    }
    return null;
  }

  /**
   * Prefixes to specified prefix to {@code subject} if it did not already have that prefix. If
   * {@code subject} is null, {@code prefix} is returned.
   *
   * @param subject The {@code String} to which to append the suffix
   * @param prefix The prefix
   * @return A string that is guaranteed to start with {@code prefix}
   */
  public static String ensurePrefix(Object subject, String prefix) {
    Check.notNull(prefix, "prefix");
    if (subject == null) {
      return prefix;
    }
    String str = subject.toString();
    return str.startsWith(prefix) ? str : prefix + str;
  }

  /**
   * Prefixes to specified prefix to {@code subject} if it did not already have that prefix. If
   * {@code subject} is null, {@code prefix} is returned.
   *
   * @param subject The {@code String} to which to append the suffix
   * @param prefix The prefix
   */
  public static String ensurePrefix(Object subject, char prefix) {
    if (subject == null) {
      return String.valueOf(prefix);
    }
    String str = subject.toString();
    return str.charAt(0) == prefix ? subject.toString() : prefix + str;
  }

  /**
   * Appends to specified suffix to {@code subject} if it did not already have that suffix. If
   * {@code subject} is null, {@code suffix} is returned.
   *
   * @param subject The {@code String} to which to append the suffix
   * @param suffix The suffix
   */
  public static String ensureSuffix(Object subject, String suffix) {
    Check.notNull(suffix, "suffix");
    if (subject == null) {
      return suffix;
    }
    String str = subject.toString();
    return str.endsWith(suffix) ? str : str + suffix;
  }

  /**
   * Appends to specified suffix to {@code str} if it did not already have that suffix.
   *
   * @param subject The {@code String} to which to append the suffix
   * @param suffix The suffix
   */
  public static String ensureSuffix(Object subject, char suffix) {
    if (subject == null) {
      return String.valueOf(suffix);
    }
    String str = subject.toString();
    return str.charAt(str.length() - 1) == suffix ? str : str + suffix;
  }

  /**
   * Whether or not the specified string is null or empty.
   *
   * @param subject The string
   * @return Whether it is null or blank
   */
  public static boolean isEmpty(Object subject) {
    return subject == null || subject.toString().isEmpty();
  }

  /**
   * Whether or not the specified string is null or blank.
   *
   * @param subject The string
   * @return Whether it is null or blank
   */
  public static boolean isBlank(Object subject) {
    return subject == null || subject.toString().isBlank();
  }

  /**
   * Whether or not the specified string neither null nor empty.
   *
   * @param object The string to check
   * @return Whether it is neither null nor blank
   */
  public static boolean isNotEmpty(Object object) {
    return !isEmpty(object);
  }

  /**
   * Whether or not the specified string neither null nor blank.
   *
   * @param object The string to check
   * @return Whether it is neither null nor blank
   */
  public static boolean isNotBlank(Object object) {
    return !isBlank(object);
  }

  /**
   * Returns the 1st argument if it is not a whitespace-only string, else the 2nd argument.
   *
   * @see ObjectMethods#ifNull(Object, Object)
   * @param subject
   * @param dfault
   */
  public static String ifBlank(Object subject, String dfault) {
    return isBlank(subject) ? dfault : subject.toString();
  }

  /**
   * Removes all occurrences of the specified prefixes from the start of a string. The returned
   * string will no longer start with any of the specified prefixes.
   *
   * @param subject
   * @param ignoreCase
   * @param prefixes
   */
  public static String lchop(Object subject, Collection<String> prefixes) {
    return lchop(subject, false, prefixes);
  }

  /**
   * Removes all occurrences of the specified prefixes from the start of a string. The returned
   * string will no longer start with any of the specified prefixes.
   *
   * @param subject
   * @param ignoreCase
   * @param prefixes
   */
  public static String lchop(Object subject, boolean ignoreCase, Collection<String> prefixes) {
    return lchop(subject, ignoreCase, prefixes.toArray(String[]::new));
  }

  /**
   * Removes all occurrences of the specified prefixes from the start of a string. The returned
   * string will no longer start with any of the specified prefixes.
   *
   * @param subject The string to remove the prefixes from
   * @param prefixes The prefixes to remove
   */
  public static String lchop(Object subject, String... prefixes) {
    return lchop(subject, false, prefixes);
  }

  /**
   * Removes all occurrences of the specified prefixes from the start of a string. The returned
   * string will no longer start with any of the specified prefixes.
   *
   * @param subject The string to remove the prefixes from
   * @param ignoreCase Whether or not to ignore case
   * @param prefixes The prefixes to remove
   */
  public static String lchop(Object subject, boolean ignoreCase, String... prefixes) {
    Check.that(prefixes, "prefixes").is(neverNull());
    if (subject == null) {
      return EMPTY;
    }
    String s0 = subject.toString();
    boolean changed;
    do {
      changed = false;
      for (String prefix : prefixes) {
        if (!prefix.isEmpty() && s0.regionMatches(ignoreCase, 0, prefix, 0, prefix.length())) {
          s0 = s0.substring(prefix.length());
          changed = true;
        }
      }
    } while (changed);
    return s0;
  }

  /**
   * Returns the line number and column number of the character at the specified index, given the
   * system-defined line separator.
   *
   * @param str The string to search
   * @param index The string index to determine the line and column number of
   * @return A two-element array containing the line line number and column number of the character
   *     at the specified index
   */
  public static int[] getLineAndColumn(String str, int index) {
    return getLineAndColumn(str, index, System.lineSeparator());
  }

  /**
   * Returns the line number and column number of the character at the specified index, given the
   * specified line separator.
   *
   * @param str The string to search
   * @param index The string index to determine the line and column number of
   * @param lineSep The line separator
   * @return A two-element array containing the line number and column number of the character at
   *     the specified index
   */
  public static int[] getLineAndColumn(String str, int index, String lineSep) {
    Check.notNull(str, "str");
    Check.that(index, "index").is(gte(), 0).is(lt(), str.length());
    Check.that(lineSep, "lineSep").isNot(empty());
    if (index == 0) {
      return new int[] {0, 0};
    }
    int line = 0, pos = 0, i = str.indexOf(lineSep);
    while (i != -1 && i < index) {
      ++line;
      pos = i + lineSep.length();
      i = str.indexOf(lineSep, i + lineSep.length());
    }
    return new int[] {line, index - pos};
  }

  /**
   * PHP-style implode method, concatenating the collection elements with &#34;, &#34; as separator
   * string.
   *
   * @param collection The collection to implode
   * @return A concatenation of the elements in the collection.
   */
  public static String implode(Collection<?> collection) {
    return implode(collection, ", ");
  }

  /**
   * PHP-style implode method, concatenating the collection elements using the specified separator
   * string.
   *
   * @param collection The collection to implode
   * @param separator The separator string
   * @return A concatenation of the elements in the collection.
   */
  public static String implode(Collection<?> collection, String separator) {
    return implode(collection, separator, -1);
  }

  /**
   * PHP-style implode method, concatenating the collection elements using the specified separator
   * string.
   *
   * @param collection The collection to implode
   * @param separator The separator string
   * @param limit The maximum number of elements to collect. Specify -1 for no maximum. Any other
   *     negative integer results in an {@link IllegalArgumentException}.
   * @return A concatenation of the elements in the collection.
   */
  public static String implode(Collection<?> collection, String separator, int limit) {
    Check.notNull(collection, "collection");
    Check.notNull(separator, "separator");
    Check.that(limit, "limit").is(gte(), -1);
    Stream<?> stream = collection.stream();
    if (limit != -1 && limit < collection.size()) {
      stream = stream.limit(limit);
    }
    return stream.map(Objects::toString).collect(Collectors.joining(separator));
  }

  /**
   * Uppercases the first character of {@code subject}.
   *
   * @param subject An object to call {@code toString()} on
   * @return The resulting string with the first character converted to upper case
   */
  public static String initCap(Object subject) {
    Check.notNull(subject);
    String s = subject.toString();
    if (s.isBlank()) {
      return s;
    }
    char[] chars = s.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return String.valueOf(chars);
  }

  /**
   * Left-pads a string to the specified width using the space character (' ').
   *
   * @param obj An object whose {@code toString()} method produces the string to be padded. Null is
   *     treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is returned without padding.
   * @return The left-padded string
   */
  public static String lpad(Object obj, int width) {
    return lpad(obj, width, ' ', null);
  }

  /**
   * Left-pads a string to the specified width using the specified padding character.
   *
   * @param obj An object whose {@code toString()} method produces the string to be padded. Null is
   *     treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is returned without padding.
   * @param padChar The character used to left-pad the string
   * @return The left-padded string
   */
  public static String lpad(Object obj, int width, char padChar) {
    return lpad(obj, width, padChar, null);
  }

  /**
   * Left-pads a string to the specified width using the specified padding character and then
   * appends the specified terminator.
   *
   * @param subject An object whose {@code toString()} method produces the string to be padded. Null
   *     is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is returned without padding.
   * @param padChar The character used to left-pad the string
   * @param delimiter A delimiter to append to the padded string. Specify null or an empty string to
   *     indicate that no delimiter should be appended.
   * @return The left-padded string
   * @throws IllegalArgumentException If {@code terminator} is null
   */
  public static String lpad(Object subject, int width, char padChar, String delimiter) {
    Check.that(width, "width").is(gte(), 0);
    String s = ifNotNull(subject, Object::toString, EMPTY);
    String d = ifNull(delimiter, EMPTY);
    if (s.length() >= width) {
      return s + d;
    }
    StringBuilder sb = new StringBuilder(width + d.length());
    for (int i = s.length(); i < width; ++i) {
      sb.append(padChar);
    }
    sb.append(s);
    sb.append(d);
    return sb.toString();
  }

  /**
   * Left-trims the specified string. The resulting string will not start with the specified
   * character.
   *
   * @param subject The {@code String} to trim
   * @param c The character to trim off the {@code String}
   * @return The left-trimmed {@code String} or the input string if it did not start with the
   *     specified character
   */
  public static String ltrim(Object subject, char c) {
    return ltrim(subject, String.valueOf(c));
  }

  /**
   * Left-trims all characters contained in {@code chars} from the specified specified string. The
   * resulting string will not start with any of the charachters contained in {@code chars}.
   *
   * @param subject The {@code String} to trim
   * @param chars The character to trim off the {@code String}
   * @return The left-trimmed {@code String} or the input string if it did not start with any of the
   *     specified characters
   */
  public static String ltrim(Object subject, String chars) {
    Check.that(chars, "chars").isNot(empty());
    if (subject == null) {
      return EMPTY;
    }
    String str0 = subject.toString();
    int i = 0;
    LOOP:
    for (; i < str0.length(); ++i) {
      for (int j = 0; j < chars.length(); ++j) {
        if (str0.charAt(i) == chars.charAt(j)) {
          continue LOOP;
        }
      }
      break LOOP;
    }
    return i == 0 ? str0 : str0.substring(i);
  }

  /**
   * Centers (left- and right-pads) a string within the specified width using the space character.
   *
   * @param subject An object whose {@code toString()} method produces the string to be padded. Null
   *     is treated as the empty string.
   * @param width
   */
  public static String pad(Object subject, int width) {
    return pad(subject, width, ' ', null);
  }

  /**
   * Centers (left- and right-pads) a string within the specified width using the specified padding
   * character.
   *
   * @param subject An object whose {@code toString()} method produces the string to be padded. Null
   *     is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is printed without padding.
   * @param padChar The character used to left- and right-pad the string.
   * @return The left- and right-padded string plus the terminator
   */
  public static String pad(Object subject, int width, char padChar) {
    return pad(subject, width, padChar, null);
  }

  /**
   * Centers (left- and right-pads) a string within the specified width using the specified padding
   * character and then appends the specified delimiter.
   *
   * @param subject An object whose {@code toString()} method produces the string to be padded. Null
   *     is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is printed without padding.
   * @param padChar The character used to left- and right-pad the string.
   * @param delimiter A delimiter to append to the padded string. Specify null or an empty string to
   *     indicate that no delimiter should be appended.
   * @return The left- and right-padded string plus the terminator
   */
  public static String pad(Object subject, int width, char padChar, String delimiter) {
    Check.that(width, "width").is(gte(), 0);
    String s = ifNotNull(subject, Object::toString, EMPTY);
    String d = ifNull(delimiter, EMPTY);
    if (s.length() >= width) {
      return s + d;
    }
    StringBuilder sb = new StringBuilder(width + d.length());
    int left = (width - s.length()) / 2;
    int right = width - left - s.length();
    for (int i = 0; i < left; ++i) {
      sb.append(padChar);
    }
    sb.append(s);
    for (int i = 0; i < right; ++i) {
      sb.append(padChar);
    }
    sb.append(d);
    return sb.toString();
  }

  /**
   * Removes all occurrences of the specified suffixes from the end of a string. The returned string
   * will no longer end with any of the specified suffixes.
   *
   * @param subject
   * @param suffixes
   */
  public static String rchop(Object subject, Collection<String> suffixes) {
    return rchop(subject, false, suffixes);
  }

  /**
   * Removes all occurrences of the specified suffixes from the end of a string. The returned string
   * will no longer end with any of the specified suffixes.
   *
   * @param subject
   * @param ignoreCase
   * @param suffixes
   */
  public static String rchop(Object subject, boolean ignoreCase, Collection<String> suffixes) {
    return rchop(subject, ignoreCase, suffixes.toArray(String[]::new));
  }

  /**
   * Removes all occurrences of the specified suffixes from the end of a string. The returned string
   * will no longer end with any of the specified suffixes.
   *
   * @param subject
   * @param ignoreCase
   * @param suffixes
   */
  public static String rchop(Object subject, String... suffixes) {
    return rchop(subject, false, suffixes);
  }

  /**
   * Removes all occurrences of the specified suffixes from the end of a string. The returned string
   * will no longer end with any of the specified suffixes.
   *
   * @param subject
   * @param ignoreCase
   * @param suffixes
   */
  public static String rchop(Object subject, boolean ignoreCase, String... suffixes) {
    Check.that(suffixes, "suffixes").is(neverNull());
    if (subject == null) {
      return EMPTY;
    }
    String s0 = subject.toString();
    String suf;
    while (null != (suf = endsWith(s0, ignoreCase, suffixes))) {
      s0 = s0.substring(0, s0.length() - suf.length());
    }
    return s0;
  }

  /**
   * Right-pads a string to the specified width using the space character (' ').
   *
   * @param subject An object whose {@code toString()} method produces the string to be padded. Null
   *     is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is returned without padding.
   * @return The right-padded string
   */
  public static String rpad(Object subject, int width) {
    return rpad(subject, width, ' ', null);
  }

  /**
   * Right-pads a string to the specified width using the specified padding character.
   *
   * @param subject An object whose {@code toString()} method produces the string to be padded. Null
   *     is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is returned without padding.
   * @param padChar The character used to left-pad the string.
   * @return The right-padded string
   */
  public static String rpad(Object subject, int width, char padChar) {
    return rpad(subject, width, padChar, null);
  }

  /**
   * Right-pads a string to the specified width using the specified padding character and then
   * appends the specified delimiter.
   *
   * @param subject An object whose {@code toString()} method produces the string to be padded. Null
   *     is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is printed without padding.
   * @param padChar The character used to right-pad the string.
   * @param delimiter A delimiter to append to the padded string. Specify null or an empty string to
   *     indicate that no delimiter should be appended.
   * @return The right-padded string
   */
  public static String rpad(Object subject, int width, char padChar, String delimiter) {
    Check.that(width, "width").is(gte(), 0);
    String s = ifNotNull(subject, Object::toString, EMPTY);
    String d = ifNull(delimiter, EMPTY);
    if (s.length() >= width) {
      return s + d;
    }
    StringBuilder sb = new StringBuilder(width + d.length());
    sb.append(s);
    for (int i = s.length(); i < width; ++i) {
      sb.append(padChar);
    }
    sb.append(d);
    return sb.toString();
  }

  /**
   * Right-trims the specified string. The resulting string will not end with the specified
   * character.
   *
   * @param subject The {@code String} to trim
   * @param c The character to trim off the {@code String}
   * @return The right-trimmed {@code String} or the input string if it did not end with the
   *     specified character
   */
  public static String rtrim(Object subject, char c) {
    return rtrim(subject, String.valueOf(c));
  }

  /**
   * Right-trims all characters contained in {@code chars} from the specified specified string. The
   * resulting string will not end with any of the charachters contained in {@code chars}.
   *
   * @param subject The {@code String} to trim
   * @param chars The character to trim off the {@code String}
   * @return The right-trimmed {@code String} or the input string if it did not end with any of the
   *     specified characters
   */
  public static String rtrim(Object subject, String chars) {
    Check.that(chars, "chars").isNot(empty());
    if (subject == null) {
      return EMPTY;
    }
    String str0 = subject.toString();
    int i = str0.length() - 1;
    LOOP:
    for (; i >= 0; --i) {
      for (int j = 0; j < chars.length(); ++j) {
        if (str0.charAt(i) == chars.charAt(j)) {
          continue LOOP;
        }
      }
      break LOOP;
    }
    return i == str0.length() - 1 ? str0 : str0.substring(0, i + 1);
  }

  /**
   * Substring method that facilitates substring retrieval relative to the end of a string.
   *
   * <p>
   *
   * <ol>
   *   <li>If the input string is empty and {@code from} equals zero, an empty string is returned.
   *   <li>If {@code from} is negative, it is taken relative to the end of the string.
   * </ol>
   *
   * @param str The {@code String} to extract a substring from
   * @param from The start index within {@code string} (may be negative)
   * @return The substring
   */
  public static String substring(String str, int from) {
    Check.notNull(str, "str");
    int sz = str.length();
    if (from < 0) {
      from = sz + from;
    }
    Check.that(from, "from").is(gte(), 0).is(lte(), sz);
    return str.substring(from);
  }

  /**
   * Substring method that facilitates substring retrieval relative to the end of a string as well
   * as substring retrieval in the opposite direction. The {@code from} and {@code length} arguments
   * works as follows:
   *
   * <p>
   *
   * <ol>
   *   <li>If {@code from} is negative, it is taken relative to the end of the string.
   *   <li>If {@code length} is negative, the substring is taken in the opposite direction, with
   *       {@code from} now becoming the <i>last</i> character of the substring.
   * </ol>
   *
   * @param str The {@code String} to extract a substring from. <i>Must not be null.</i>
   * @param from The start index within {@code string} (may be negative)
   * @param length The desired length of the substring
   * @return The substring
   */
  public static String substring(String str, int from, int length) {
    Check.notNull(str, "str");
    int sz = str.length();
    if (from < 0) {
      from = Check.that(sz + from, START_INDEX).is(gte(), 0).intValue();
    } else {
      Check.that(from, START_INDEX).is(lte(), sz);
    }
    int to;
    if (length >= 0) {
      to = Check.that(from + length, END_INDEX).is(lte(), sz).intValue();
    } else {
      to = Check.that(from + 1, END_INDEX).is(lte(), sz).intValue();
      from = Check.that(to + length, START_INDEX).is(gte(), 0).intValue();
    }
    return str.substring(from, to);
  }

  /**
   * Returns everything from (and including) the first occurrence of the specified character
   * sequence, or the entire string if it does not contain that character sequence.
   *
   * @param str The string to take a substring from
   * @param from The character sequence from which to take the substring
   * @return The substring
   */
  public static String substrFrom(String str, String from) {
    return substrFrom(str, from, false);
  }

  /**
   * Returns everything from (and including) the first or last occurrence of the specified character
   * sequence, or the entire string if it does not contain that character sequence.
   *
   * @param str The string to take a substring from
   * @param from The character sequence from which to take the substring
   * @param last Whether to use the first of last occurrence of {@code from}
   * @return The substring
   */
  public static String substrFrom(String str, String from, boolean last) {
    Check.notNull(str, "str");
    Check.that(from, "from").isNot(empty());
    int i = last ? str.lastIndexOf(from) : str.indexOf(from);
    return i == -1 ? str : str.substring(i);
  }

  /**
   * Returns everything from (and including) the first occurrence of the specified character, or the
   * entire string if it does not contain that character.
   *
   * @param str The string to take a substring from
   * @param from The character up to which to take the substring
   * @return The substring
   */
  public static String substrFrom(String str, char from) {
    return substrFrom(str, from, false);
  }

  /**
   * Returns everything from (and including) the first or last occurrence of the specified
   * character, or the entire string if it does not contain that character.
   *
   * @param str The string to take a substring from
   * @param from The character up to which to take the substring
   * @param last Whether to use the first of last occurrence of {@code to}
   * @return The substring
   */
  public static String substrFrom(String str, char from, boolean last) {
    Check.notNull(str, "str");
    int i = last ? str.lastIndexOf(from) : str.indexOf(from);
    return i == -1 ? str : str.substring(i);
  }

  /**
   * Returns everything after (and not including) the first occurrence of the specified character
   * sequence, or the entire string if it does not contain that character sequence.
   *
   * @param str The string to take a substring from
   * @param after The character sequence after which to take the substring
   * @return The substring
   */
  public static String substrAfter(String str, String after) {
    return substrAfter(str, after, false);
  }

  /**
   * Returns everything from (and not including) the first or last occurrence of the specified
   * character sequence, or the entire string if it does not contain that character sequence.
   *
   * @param str The string to take a substring from
   * @param after The character sequence after which to take the substring (must not be null or
   *     empty)
   * @param last Whether to use the first of last occurrence of {@code after}
   * @return The substring
   */
  public static String substrAfter(String str, String after, boolean last) {
    Check.notNull(str, "str");
    Check.that(after, "from").isNot(empty());
    int i = last ? str.lastIndexOf(after) : str.indexOf(after);
    int j = after.length();
    return i + j == str.length() ? EMPTY : i == -1 ? str : str.substring(i + j);
  }

  /**
   * Returns everything from (and not including) the first occurrence of the specified character, or
   * the entire string if it does not contain that character.
   *
   * @param str The string to take a substring from
   * @param after The character after which to take the substring
   * @return The substring
   */
  public static String substrAfter(String str, char after) {
    return substrAfter(str, after, false);
  }

  /**
   * Returns everything after (and not including) the first or last occurrence of the specified
   * character, or the entire string if it does not contain that character.
   *
   * @param str The string to take a substring from
   * @param after The character after which to take the substring
   * @param last Whether to use the first of last occurrence of {@code after}
   * @return The substring
   */
  public static String substrAfter(String str, char after, boolean last) {
    Check.notNull(str, "str");
    int i = last ? str.lastIndexOf(after) : str.indexOf(after);
    return i == str.length() - 1 ? EMPTY : i == -1 ? str : str.substring(i + 1);
  }

  /**
   * Returns the substring up to (and not including) the first occurrence of the specified
   * character, or the entire string if it does not contain that character.
   *
   * @param str The string to take a substring from
   * @param to The character sequence up to which to take the substring
   * @return The substring
   */
  public static String substrTo(String str, String to) {
    return substrTo(str, to, false);
  }

  /**
   * Returns the substring up to (not including) the first or last occurrence of the specified
   * character, or the entire string if it does not contain that character.
   *
   * @param str The string to take a substring from
   * @param to The character sequence up to which to take the substring
   * @param last Whether to use the first of last occurrence of {@code to}
   * @return The substring
   */
  public static String substrTo(String str, String to, boolean last) {
    Check.notNull(str, "str");
    Check.that(to, "to").isNot(empty());
    int i = last ? str.lastIndexOf(to) : str.indexOf(to);
    return i == -1 ? str : str.substring(0, i);
  }

  /**
   * Returns everything up to (not including) the first occurrence of the specified character within
   * the string, or the entire string if it does not contain that character.
   *
   * @param str The string to take a substring from
   * @param to The character up to which to take the substring
   * @return The substring
   */
  public static String substrTo(String str, char to) {
    return substrTo(str, to, false);
  }

  /**
   * Returns everything up to (and not including) the first or last occurrence of the specified
   * character within the string, or the entire string if it does not contain that character.
   *
   * @param str The string to take a substring from
   * @param to The character up to which to take the substring
   * @param last Whether to use the first of last occurrence of {@code to}
   * @return The substring
   */
  public static String substrTo(String str, char to, boolean last) {
    Check.notNull(str, "str");
    int i = last ? str.lastIndexOf(to) : str.indexOf(to);
    return i == -1 ? str : str.substring(0, i);
  }

  public static String substrTo(String str, char c, int occurrence) {
    Check.notNull(str, "str");
    Check.that(occurrence, "occurrence").is(gt(), 0);
    int x = occurrence;
    for (int i = 0; i < str.length(); ++i) {
      if (str.charAt(i) == c && --x == 0) {
        return str.substring(0, i);
      }
    }
    return fail("Occurrences of '%s' in \"%s\": %d. Expected: %d.", c, str, x, occurrence);
  }

  /**
   * Left and right-trims the specified string. The resulting string will neither start nor end with
   * the specified character.
   *
   * @param subject The {@code String} to trim
   * @param c The character to trim off the {@code String}
   * @return The trimmed {@code String}.
   */
  public static String trim(Object subject, char c) {
    return rtrim(ltrim(subject, c), c);
  }

  /**
   * Left and right-trims the specified string. The resulting string will neither start nor end with
   * any of the specified characters.
   *
   * @param subject The {@code String} to trim
   * @param chars The character to trim off the {@code String}
   * @return The trimmed {@code String}.
   */
  public static String trim(Object subject, String chars) {
    return rtrim(ltrim(subject, chars), chars);
  }
}
