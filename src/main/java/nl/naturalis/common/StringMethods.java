package nl.naturalis.common;

import nl.naturalis.common.check.Check;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.lang.Character.toLowerCase;
import static nl.naturalis.common.ArrayMethods.END_INDEX;
import static nl.naturalis.common.ArrayMethods.START_INDEX;
import static nl.naturalis.common.ObjectMethods.ifNull;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * Methods for working with strings. Many of them are geared towards printing
 * strings. Instead of accepting a {@code String} argument, they will take an {@code
 * Object}. The parameter name will then be "{@code subject}". If the object is
 * {@code null}, these methods will usually return an empty string, else they will
 * call {@code toString()} on the object and process the resulting {@code String}. In
 * other words, these methods are null-safe with respect to the string to be
 * processed, and they will never return {@code null} themselves. For ease of reading
 * the {@code subject} parameter will still be referred to as a {@code String}.
 */
public final class StringMethods {

  /**
   * The empty string.
   */
  public static final String EMPTY = "";

  private StringMethods() {}

  /**
   * Appends the specified value to the specified {@code StringBuilder} and returns
   * the {@code StringBuilder}.
   *
   * @param sb The {@code StringBuilder} to append the value to
   * @param val The value to append
   * @return The {@code StringBuilder}
   */
  public static StringBuilder append(StringBuilder sb, Object val) {
    return Check.notNull(sb, "sb").ok().append(val);
  }

  /**
   * Appends the specified values to the specified {@code StringBuilder} and returns
   * the {@code StringBuilder}.
   *
   * @param sb The {@code StringBuilder} to append the values to
   * @param val0 A value
   * @param val1 Another value
   * @return The {@code StringBuilder}
   */
  public static StringBuilder append(StringBuilder sb, Object val0, Object val1) {
    return Check.notNull(sb, "sb").ok().append(val0).append(val1);
  }

  /**
   * Appends the specified values to the specified {@code StringBuilder} and returns
   * the {@code StringBuilder}.
   *
   * @param sb The {@code StringBuilder} to append the values to
   * @param val0 A value
   * @param val1 Another value
   * @param val2 Another value
   * @return The {@code StringBuilder}
   */
  public static StringBuilder append(StringBuilder sb,
      Object val0,
      Object val1,
      Object val2) {
    return Check.notNull(sb, "sb").ok().append(val0).append(val1).append(val2);
  }

  /**
   * Appends the specified values to the specified {@code StringBuilder} and returns
   * the {@code StringBuilder}.
   *
   * @param sb The {@code StringBuilder} to append the values to
   * @param val0 A value
   * @param val1 Another value
   * @param val2 Another value
   * @param val3 Another value
   * @return The {@code StringBuilder}
   */
  public static StringBuilder append(
      StringBuilder sb, Object val0, Object val1, Object val2, Object val3) {
    return Check.notNull(sb, "sb")
        .ok()
        .append(val0)
        .append(val1)
        .append(val2)
        .append(val3);
  }

  /**
   * Appends the specified values to the specified {@code StringBuilder} and returns
   * the {@code StringBuilder}.
   *
   * @param sb The {@code StringBuilder} to append the values to
   * @param val0 A value
   * @param val1 Another value
   * @param val2 Another value
   * @param val3 Another value
   * @param val4 Another value
   * @return The {@code StringBuilder}
   */
  public static StringBuilder append(
      StringBuilder sb,
      Object val0,
      Object val1,
      Object val2,
      Object val3,
      Object val4) {

    return Check.notNull(sb, "sb")
        .ok()
        .append(val0)
        .append(val1)
        .append(val2)
        .append(val3)
        .append(val4);
  }

  /**
   * Appends the specified values to the specified {@code StringBuilder} and returns
   * the {@code StringBuilder}.
   *
   * @param sb The {@code StringBuilder} to append the values to
   * @param val0 A value
   * @param val1 Another value
   * @param val2 Another value
   * @param val3 Another value
   * @param val4 Another value
   * @param val5 Another value
   * @return The {@code StringBuilder}
   */
  public static StringBuilder append(
      StringBuilder sb,
      Object val0,
      Object val1,
      Object val2,
      Object val3,
      Object val4,
      Object val5) {

    return Check.notNull(sb, "sb")
        .ok()
        .append(val0)
        .append(val1)
        .append(val2)
        .append(val3)
        .append(val4)
        .append(val5);
  }

  /**
   * Appends the specified values to the specified {@code StringBuilder} and returns
   * the {@code StringBuilder}.
   *
   * @param sb The {@code StringBuilder} to append the values to
   * @param val0 A value
   * @param val1 Another value
   * @param val2 Another value
   * @param val3 Another value
   * @param val4 Another value
   * @param val5 Another value
   * @param val6 Another value
   * @return The {@code StringBuilder}
   */
  public static StringBuilder append(
      StringBuilder sb,
      Object val0,
      Object val1,
      Object val2,
      Object val3,
      Object val4,
      Object val5,
      Object val6) {

    return Check.notNull(sb, "sb")
        .ok()
        .append(val0)
        .append(val1)
        .append(val2)
        .append(val3)
        .append(val4)
        .append(val5)
        .append(val6);
  }

  /**
   * Appends the specified values to the specified {@code StringBuilder} and returns
   * the {@code StringBuilder}.
   *
   * @param sb The {@code StringBuilder} to append the values to
   * @param val0 A value
   * @param val1 Another value
   * @param val2 Another value
   * @param val3 Another value
   * @param val4 Another value
   * @param val5 Another value
   * @param val6 Another value
   * @param val7 Another value
   * @return The {@code StringBuilder}
   */
  public static StringBuilder append(
      StringBuilder sb,
      Object val0,
      Object val1,
      Object val2,
      Object val3,
      Object val4,
      Object val5,
      Object val6,
      Object val7) {

    return Check.notNull(sb, "sb")
        .ok()
        .append(val0)
        .append(val1)
        .append(val2)
        .append(val3)
        .append(val4)
        .append(val5)
        .append(val6)
        .append(val7);
  }

  /**
   * Appends the specified values to the specified {@code StringBuilder} and returns
   * the {@code StringBuilder}.
   *
   * @param sb The {@code StringBuilder} to append the values to
   * @param val0 A value
   * @param val1 Another value
   * @param val2 Another value
   * @param val3 Another value
   * @param val4 Another value
   * @param val5 Another value
   * @param val6 Another value
   * @param val7 Another value
   * @param val8 Another value
   * @return The {@code StringBuilder}
   */
  public static StringBuilder append(
      StringBuilder sb,
      Object val0,
      Object val1,
      Object val2,
      Object val3,
      Object val4,
      Object val5,
      Object val6,
      Object val7,
      Object val8) {

    return Check.notNull(sb, "sb")
        .ok()
        .append(val0)
        .append(val1)
        .append(val2)
        .append(val3)
        .append(val4)
        .append(val5)
        .append(val6)
        .append(val7)
        .append(val8);
  }

  /**
   * Appends the specified values to the specified {@code StringBuilder} and returns
   * the {@code StringBuilder}.
   *
   * @param sb The {@code StringBuilder} to append the values to
   * @param val0 A value
   * @param val1 Another value
   * @param val2 Another value
   * @param val3 Another value
   * @param val4 Another value
   * @param val5 Another value
   * @param val6 Another value
   * @param val7 Another value
   * @param val8 Another value
   * @param val9 Another value
   * @param moreData More values
   * @return The {@code StringBuilder}
   */
  public static StringBuilder append(
      StringBuilder sb,
      Object val0,
      Object val1,
      Object val2,
      Object val3,
      Object val4,
      Object val5,
      Object val6,
      Object val7,
      Object val8,
      Object val9,
      Object... moreData) {

    Check.notNull(sb, "sb")
        .ok()
        .append(val0)
        .append(val1)
        .append(val2)
        .append(val3)
        .append(val4)
        .append(val5)
        .append(val6)
        .append(val7)
        .append(val8)
        .append(val9);
    Check.notNull(moreData, "val").ok(Arrays::stream).forEach(sb::append);
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

  /**
   * Counts the number of occurrences of {@code substr} within {@code subject}.
   * Returns 0 (zero) if {@code subject} is {@code null}.
   *
   * @param subject The string to search
   * @param substr The substring to search for (must not be {@code null} or
   *     empty)
   * @return The number of occurrences of {@code substr} within {@code subject}
   */
  public static int count(Object subject, String substr) {
    return count(subject, substr, false);
  }

  /**
   * Counts the number of occurrences of {@code substr} within {@code subject}.
   * Returns 0 (zero) if {@code subject} is {@code null}.
   *
   * @param subject The string to search
   * @param substr The substring to search for (must not be {@code null} or
   *     empty)
   * @param ignoreCase Whether to ignore case while comparing substrings
   * @return The number of occurrences of {@code substr} within {@code subject}
   */
  public static int count(Object subject, String substr, boolean ignoreCase) {
    return count(subject, substr, ignoreCase, 0);
  }

  /**
   * Counts the number of occurrences of {@code substr} within {@code subject}.
   * Returns 0 (zero) if {@code subject} is {@code null}.
   *
   * @param subject The string to search
   * @param substr The substring to search for (must not be {@code null} or
   *     empty)
   * @param ignoreCase Whether to ignore case while comparing substrings
   * @param limit The maximum number of occurrences the count. You may specify 0
   *     (zero) for "no maximum".
   * @return The number of occurrences of {@code substr} within {@code subject} (will
   *     not exceed {@code limit})
   */
  public static int count(Object subject,
      String substr,
      boolean ignoreCase,
      int limit) {
    Check.that(substr, "substr").isNot(empty());
    Check.that(limit, "limit").is(gte(), 0);
    String str;
    if (subject == null || (str = subject.toString()).length() < substr.length()) {
      return 0;
    }
    if (substr.length() == 1) {
      return count(str, substr.charAt(0), ignoreCase, limit);
    }
    int count = 0;
    for (int i = 0; i <= str.length() - substr.length(); ++i) {
      if (str.regionMatches(ignoreCase, i, substr, 0, substr.length())) {
        if (++count == limit) {
          break;
        }
      }
    }
    return count;
  }

  /**
   * Counts the number of non-overlapping occurrences of {@code substr} within {@code
   * subject}. The string to search for must not be null or empty and is not treated
   * as a regular expression. Returns 0 (zero) if {@code subject} is {@code null}.
   *
   * @param subject The string to search
   * @param substr The substring to search for
   * @return The number of non-overlapping occurrences of {@code substr} within
   *     {@code subject}
   */
  public static int countDiscrete(Object subject, String substr) {
    return countDiscrete(subject, substr, false, 0);
  }

  /**
   * Counts the number of non-overlapping occurrences of {@code substr} within {@code
   * subject}. The string to search for must not be null or empty and is not treated
   * as a regular expression. Returns 0 (zero) if {@code subject} is {@code null}.
   *
   * @param subject The string to search
   * @param substr The substring to search for
   * @param ignoreCase Whether to ignore case while comparing substrings
   * @return The number of non-overlapping occurrences of {@code substr} within
   *     {@code subject}
   */
  public static int countDiscrete(Object subject,
      String substr,
      boolean ignoreCase) {
    return countDiscrete(subject, substr, ignoreCase, 0);
  }

  /**
   * Counts the number of non-overlapping occurrences of {@code substr} within {@code
   * subject}. Returns 0 (zero) if {@code subject} is {@code null}.
   *
   * @param subject The string to search
   * @param substr The substring to search for
   * @param ignoreCase Whether to ignore case while comparing substrings
   * @param limit The maximum number of occurrences the count. You may specify 0
   *     (zero) for "no maximum".
   * @return The number of non-overlapping occurrences of {@code substr} within
   *     {@code subject} (will not exceed {@code limit})
   */
  public static int countDiscrete(Object subject,
      String substr,
      boolean ignoreCase,
      int limit) {
    Check.that(substr, "substr").isNot(empty());
    Check.that(limit, "limit").is(gte(), 0);
    String str;
    if (subject == null || (str = subject.toString()).length() < substr.length()) {
      return 0;
    }
    if (substr.length() == 1) {
      return count(str, substr.charAt(0), ignoreCase, limit);
    }
    int count = 0;
    int i = 0;
    do {
      if (str.regionMatches(ignoreCase, i, substr, 0, substr.length())) {
        if (++count == limit) {
          break;
        }
        i += substr.length();
      } else {
        i += 1;
      }
    } while (i <= str.length() - substr.length());
    return count;
  }

  private static int count(String str, char c, boolean ignoreCase, int limit) {
    int count = 0;
    if (ignoreCase) {
      char c0 = toLowerCase(c);
      for (int i = 0; i < str.length(); ++i) {
        if (toLowerCase(str.charAt(i)) == c0 && ++count == limit) {
          break;
        }
      }
    } else {
      for (int i = 0; i < str.length(); ++i) {
        if (str.charAt(i) == c && ++count == limit) {
          break;
        }
      }
    }
    return count;
  }

  /**
   * Returns {@code subject.toString()} if its length does not exceed {@code
   * maxWidth}, else truncates the string and appends "...", such that the new
   * string's length does not exceed {@code maxWidth}.
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
    Check.that(maxWidth, "maxWidth").is(gt(), 3);
    if (subject == null) {
      return EMPTY;
    }
    String str = subject.toString();
    if (str.length() <= maxWidth) {
      return str;
    }
    int to = Math.max(0, maxWidth - 3);
    return str.substring(0, to) + "...";
  }

  /**
   * Whether the specified string ends with any of the specified suffixes.
   *
   * @param subject The string to test
   * @param ignoreCase Whether to ignore case
   * @param suffixes The suffixes to test
   * @return The first suffix found to be equal to the end of the string, or null if
   *     the string ended in none of the specified suffixes
   */
  public static String endsWith(Object subject,
      boolean ignoreCase,
      Collection<String> suffixes) {
    Check.notNull(suffixes, "suffixes");
    return endsWith(subject, ignoreCase, suffixes.toArray(String[]::new));
  }

  /**
   * Whether {@code subject} ends with any of the specified suffixes. Returns the
   * first suffix found to be equal to the end of the string, or null if the string
   * ended in none of the specified suffixes.
   *
   * @param subject The string to test
   * @param ignoreCase Whether to ignore case
   * @param suffixes The suffixes to test
   * @return The first suffix found to be equal to the end of the string, or null if
   *     the string ended in none of the specified suffixes
   */
  public static String endsWith(Object subject,
      boolean ignoreCase,
      String... suffixes) {
    Check.that(suffixes, "suffixes").is(deepNotEmpty());
    String str;
    if (subject == null || (str = subject.toString()).isEmpty()) {
      return null;
    }
    return endsWith0(str, ignoreCase, suffixes);
  }

  private static String endsWith0(String str,
      boolean ignoreCase,
      String[] suffixes) {
    for (String suf : suffixes) {
      if (str.regionMatches(ignoreCase,
          str.length() - suf.length(),
          suf,
          0,
          suf.length())) {
        return suf;
      }
    }
    return null;
  }

  /**
   * Prefixes to specified prefix to {@code subject} if it did not already start with
   * that prefix. Returns {@code prefix} if {@code subject} is null,
   *
   * @param subject The {@code String} to which to append the prefix
   * @param prefix The prefix (must not be {@code null})
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
   * Appends to specified suffix to {@code subject} if it did not already have that
   * suffix. If {@code subject} is null, {@code suffix} is returned.
   *
   * @param subject The {@code String} to which to append the suffix
   * @param suffix The suffix (must not be {@code null})
   * @return A string that is guaranteed to end with {@code suffix}
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
   * Whether the specified string is null or blank.
   *
   * @param subject The string
   * @return Whether it is null or blank
   */
  public static boolean isBlank(Object subject) {
    return subject == null || subject.toString().isBlank();
  }

  /**
   * Whether the specified string neither null nor blank.
   *
   * @param object The string to check
   * @return Whether it is neither null nor blank
   */
  public static boolean isNotBlank(Object object) {
    return !isBlank(object);
  }

  /**
   * Returns the 1st argument if it is not a whitespace-only string, else the 2nd
   * argument.
   *
   * @param subject The string to return if not null
   * @param dfault The replacement string
   * @see ObjectMethods#ifNull(Object, Object)
   */
  public static String ifBlank(Object subject, String dfault) {
    return isBlank(subject) ? dfault : subject.toString();
  }

  /**
   * Removes all occurrences of the specified prefixes from the start of a string.
   * The returned string will no longer start with any of the specified prefixes.
   *
   * @param subject The string to remove the prefixes from
   * @param prefixes The prefixes to remove
   */
  public static String lchop(Object subject, String... prefixes) {
    return lchop(subject, false, prefixes);
  }

  /**
   * Removes all occurrences of the specified prefixes from the start of a string.
   * The returned string will no longer start with any of the specified prefixes.
   *
   * @param subject The string to remove the prefixes from
   * @param ignoreCase Whether to ignore case
   * @param prefixes The prefixes to remove
   */
  public static String lchop(Object subject,
      boolean ignoreCase,
      String... prefixes) {
    Check.that(prefixes, "prefixes").is(deepNotEmpty());
    String str;
    if (subject == null || (str = subject.toString()).isEmpty()) {
      return EMPTY;
    }
    boolean found;
    int offset = 0;
    do {
      found = false;
      for (String prefix : prefixes) {
        if (str.regionMatches(ignoreCase, offset, prefix, 0, prefix.length())) {
          offset += prefix.length();
          found = true;
        }
      }
    } while (found);
    return str.substring(offset);
  }

  /**
   * Removes all occurrences of the specified suffixes from the end of a string. The
   * returned string will no longer end with any of the specified suffixes.
   *
   * @param subject The string to manipulate
   * @param suffixes The suffixes to chop off the right of the string
   * @return A String that does not end with any of the specified suffixes
   */
  public static String rchop(Object subject, String... suffixes) {
    return rchop(subject, false, suffixes);
  }

  /**
   * Removes all occurrences of the specified suffixes from the end of a string. The
   * returned string will no longer end with any of the specified suffixes.
   *
   * @param subject The string to manipulate
   * @param ignoreCase Whether to ignore case while chopping off suffixes
   * @param suffixes A String that does not end with any of the specified
   *     suffixes
   */
  public static String rchop(Object subject,
      boolean ignoreCase,
      String... suffixes) {
    Check.that(suffixes, "suffixes").is(deepNotEmpty());
    String str;
    if (subject == null || (str = subject.toString()).isEmpty()) {
      return EMPTY;
    }
    boolean found;
    int offset = str.length();
    do {
      found = false;
      for (String suffix : suffixes) {
        int sl = suffix.length();
        if (str.regionMatches(ignoreCase, offset - sl, suffix, 0, sl)) {
          offset -= sl;
          found = true;
        }
      }
    } while (found);
    return str.substring(0, offset);
  }

  /**
   * Ensures that the first character of the specified string is not a lowercase
   * character.
   *
   * @param subject The string
   * @return The same string except that the first character is not a lowercase
   *     character
   */
  public static String firstToUpper(Object subject) {
    String s;
    if (subject == null || (s = subject.toString()).isEmpty()) {
      return EMPTY;
    }
    if (Character.isLowerCase(s.charAt(0))) {
      return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
    return s;
  }

  /**
   * Ensures that the first character of the specified string is not an uppercase
   * character.
   *
   * @param subject The string
   * @return The same string except that the first character is not an uppercase
   *     character
   */
  public static String firstToLower(Object subject) {
    String s;
    if (subject == null || (s = subject.toString()).isEmpty()) {
      return EMPTY;
    }
    if (Character.isUpperCase(s.charAt(0))) {
      return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
    return s;
  }

  /**
   * Left-pads a string to the specified width using the space character (' ').
   *
   * @param obj An object whose {@code toString()} method produces the string to
   *     be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *     wider than the specified width, the string is returned without padding.
   * @return The left-padded string
   */
  public static String lpad(Object obj, int width) {
    return lpad(obj, width, ' ', EMPTY);
  }

  /**
   * Left-pads a string to the specified width using the specified padding
   * character.
   *
   * @param obj An object whose {@code toString()} method produces the string to
   *     be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *     wider than the specified width, the string is returned without padding.
   * @param padChar The character used to left-pad the string
   * @return The left-padded string
   */
  public static String lpad(Object obj, int width, char padChar) {
    return lpad(obj, width, padChar, EMPTY);
  }

  /**
   * Left-pads a string to the specified width using the specified padding character
   * and then appends the specified terminator.
   *
   * @param subject An object whose {@code toString()} method produces the string
   *     to be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *     wider than the specified width, the string is returned without padding.
   * @param padChar The character used to left-pad the string
   * @param delimiter A delimiter to append to the padded string. Specify null or
   *     an empty string to indicate that no delimiter should be appended.
   * @return The left-padded string
   * @throws IllegalArgumentException If {@code terminator} is null
   */
  public static String lpad(Object subject,
      int width,
      char padChar,
      String delimiter) {
    Check.that(width, "width").is(gte(), 0);
    String s = subject == null ? EMPTY : subject.toString();
    String d = ifNull(delimiter, EMPTY);
    if (s.length() >= width) {
      return s + d;
    }
    return new StringBuilder(width + d.length())
        .append(String.valueOf(padChar).repeat(width - s.length()))
        .append(s)
        .append(d)
        .toString();
  }

  /**
   * Centers (left- and right-pads) a string within the specified width using the
   * space character.
   *
   * @param subject An object whose {@code toString()} method produces the string
   *     to be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *     wider than the specified width, the string is printed without padding.
   * @return The left- and right-padded string plus the terminator
   */
  public static String pad(Object subject, int width) {
    return pad(subject, width, ' ', null);
  }

  /**
   * Centers (left- and right-pads) a string within the specified width using the
   * specified padding character.
   *
   * @param subject An object whose {@code toString()} method produces the string
   *     to be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *     wider than the specified width, the string is printed without padding.
   * @param padChar The character used to left- and right-pad the string.
   * @return The left- and right-padded string plus the terminator
   */
  public static String pad(Object subject, int width, char padChar) {
    return pad(subject, width, padChar, null);
  }

  /**
   * Centers (left- and right-pads) a string within the specified width using the
   * specified padding character and then appends the specified delimiter.
   *
   * @param subject An object whose {@code toString()} method produces the string
   *     to be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *     wider than the specified width, the string is printed without padding.
   * @param padChar The character used to left- and right-pad the string.
   * @param delimiter A delimiter to append to the padded string. Specify null or
   *     an empty string to indicate that no delimiter should be appended.
   * @return The left- and right-padded string plus the terminator
   */
  public static String pad(Object subject,
      int width,
      char padChar,
      String delimiter) {
    Check.that(width, "width").is(gte(), 0);
    String s = subject == null ? EMPTY : subject.toString();
    String d = ifNull(delimiter, EMPTY);
    if (s.length() >= width) {
      return s + d;
    }
    StringBuilder sb = new StringBuilder(width + d.length());
    int left = (width - s.length()) / 2;
    int right = width - left - s.length();
    sb.append(String.valueOf(padChar).repeat(left));
    sb.append(s);
    sb.append(String.valueOf(padChar).repeat(Math.max(0, right)));
    sb.append(d);
    return sb.toString();
  }

  /**
   * Right-pads a string to the specified width using the space character (' ').
   *
   * @param subject An object whose {@code toString()} method produces the string
   *     to be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *     wider than the specified width, the string is returned without padding.
   * @return The right-padded string
   */
  public static String rpad(Object subject, int width) {
    return rpad(subject, width, ' ', EMPTY);
  }

  /**
   * Right-pads a string to the specified width using the specified padding
   * character.
   *
   * @param subject An object whose {@code toString()} method produces the string
   *     to be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *     wider than the specified width, the string is returned without padding.
   * @param padChar The character used to left-pad the string.
   * @return The right-padded string
   */
  public static String rpad(Object subject, int width, char padChar) {
    return rpad(subject, width, padChar, EMPTY);
  }

  /**
   * Right-pads a string to the specified width using the specified padding character
   * and appends the specified suffix.
   *
   * @param subject An object whose {@code toString()} method produces the string
   *     to be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *     wider than the specified width, the string is printed without padding.
   * @param padChar The character used to right-pad the string.
   * @param suffix A suffix to append to the padded string.
   * @return The right-padded string
   */
  public static String rpad(Object subject, int width, char padChar, String suffix) {
    Check.that(width, "width").is(gte(), 0);
    Check.notNull(suffix, "delimiter");
    String str = subject == null ? EMPTY : subject.toString();
    if (str.length() >= width) {
      return str + suffix;
    }
    StringBuilder sb = new StringBuilder(width + suffix.length());
    String padding = String.valueOf(padChar);
    return append(sb, str, padding.repeat(width - str.length()), suffix).toString();
  }

  /**
   * Left-trims all characters contained in {@code chars} from the specified string.
   * The resulting string will not start with any of the characters contained in
   * {@code chars}.
   *
   * @param subject The {@code String} to trim
   * @param chars The character to trim off the {@code String}
   * @return The left-trimmed {@code String} or the input string if it did not start
   *     with any of the specified characters
   */
  public static String ltrim(Object subject, String chars) {
    Check.that(chars, "chars").isNot(empty());
    if (subject == null) {
      return EMPTY;
    }
    String str = subject.toString();
    int i = 0;
    LOOP:
    for (; i < str.length(); ++i) {
      for (int j = 0; j < chars.length(); ++j) {
        if (str.charAt(i) == chars.charAt(j)) {
          continue LOOP;
        }
      }
      break;
    }
    return i == 0 ? str : str.substring(i);
  }

  /**
   * Right-trims all characters contained in {@code chars} from the specified string.
   * The resulting string will not end with any of the characters contained in {@code
   * chars}.
   *
   * @param subject The {@code String} to trim
   * @param chars The character to trim off the {@code String} (must not be
   *     {@code null} or empty)
   * @return The right-trimmed {@code String} or the input string if it did not end
   *     with any of the specified characters
   */
  public static String rtrim(Object subject, String chars) {
    Check.that(chars, "chars").isNot(empty());
    if (subject == null) {
      return EMPTY;
    }
    String str = subject.toString();
    int i = str.length() - 1;
    LOOP:
    for (; i >= 0; --i) {
      for (int j = 0; j < chars.length(); ++j) {
        if (str.charAt(i) == chars.charAt(j)) {
          continue LOOP;
        }
      }
      break;
    }
    return i == str.length() - 1 ? str : str.substring(0, i + 1);
  }

  /**
   * Left and right-trims the specified string. The resulting string will neither
   * start nor end with any of the specified characters.
   *
   * @param subject The {@code String} to trim
   * @param chars The character to trim off the {@code String} (must not be
   *     {@code null} or empty)
   * @return The trimmed {@code String}.
   */
  public static String trim(Object subject, String chars) {
    return rtrim(ltrim(subject, chars), chars);
  }

  /**
   * Substring method that facilitates substring retrieval relative to the end of a
   * string.
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
  public static String substr(String str, int from) {
    Check.notNull(str, "str");
    int sz = str.length();
    if (from < 0) {
      from = sz + from;
    }
    Check.that(from, "from").is(gte(), 0).is(lte(), sz);
    return str.substring(from);
  }

  /**
   * Substring method that facilitates substring retrieval relative to the end of a
   * string as well as substring retrieval in the opposite direction. The {@code
   * from} and {@code length} arguments works as follows:
   *
   * <p>
   *
   * <ol>
   *   <li>If {@code from} is negative, it is taken relative to the end of the string.
   *   <li>If {@code length} is negative, the substring is taken in the opposite direction, with the
   *       character at {@code from} now becoming the <i>last</i> character of the substring.
   * </ol>
   *
   * @param str The {@code String} to extract a substring from. <i>Must not be
   *     null.</i>
   * @param from The start index within {@code string} (may be negative)
   * @param length The desired length of the substring
   * @return The substring
   * @see CollectionMethods#sublist(List, int, int)
   */
  public static String substr(String str, int from, int length) {
    Check.notNull(str, "str");
    int sz = str.length();
    int start;
    if (from < 0) {
      start = from + sz;
      Check.that(start, START_INDEX).is(gte(), 0);
    } else {
      start = from;
      Check.that(start, START_INDEX).is(lte(), sz);
    }
    int end;
    if (length >= 0) {
      end = start + length;
    } else {
      end = start + 1;
      start = end + length;
      Check.that(start, START_INDEX).is(gte(), 0);
    }
    Check.that(end, END_INDEX).is(lte(), sz);
    return str.substring(start, end);
  }

  /**
   * Returns the index of the nth occurrence of the specified substring within {@code
   * subject}. If {@code subject} is {@code null}, or if there is no nth occurrence
   * of the specified substring, the return value will be -1. You can specify a
   * negative occurrence to search backwards from the end of the string.
   *
   * @param subject The string to search
   * @param substr The substring to search for (must not be null or empty)
   * @param occurrence The occurrence number of the substring (1 means: get index
   *     of 1st occurrence)
   * @return The index of the nth occurrence of the specified substring
   */
  public static int indexOf(Object subject, String substr, int occurrence) {
    Check.that(substr, "substr").isNot(empty());
    Check.that(occurrence, "occurrence").is(ne(), 0);
    String str;
    if (subject == null || (str = subject.toString()).length() < substr.length()) {
      return -1;
    }
    if (substr.length() == 1) {
      return occurrence > 0
          ? charPosIndexOf(str, substr.charAt(0), occurrence)
          : charNegIndexOf(str, substr.charAt(0), occurrence);
    }
    return occurrence > 0
        ? strPosIndexOf(str, substr, occurrence)
        : strNegIndexOf(str, substr, occurrence);
  }

  private static int charPosIndexOf(String str, char c, int occurrence) {
    for (int i = 0; i < str.length(); ++i) {
      if (str.charAt(i) == c && --occurrence == 0) {
        return i;
      }
    }
    return -1;
  }

  private static int charNegIndexOf(String str, char c, int occurrence) {
    for (int i = str.length() - 1; i >= 0; --i) {
      if (str.charAt(i) == c && ++occurrence == 0) {
        return i;
      }
    }
    return -1;
  }

  private static int strPosIndexOf(String str, String substr, int occurrence) {
    for (int i = 0; i <= str.length() - substr.length(); ++i) {
      if (str.regionMatches(i, substr, 0, substr.length()) && --occurrence == 0) {
        return i;
      }
    }
    return -1;
  }

  private static int strNegIndexOf(String str, String substr, int occurrence) {
    for (int i = str.length() - substr.length(); i >= 0; --i) {
      if (str.regionMatches(i, substr, 0, substr.length()) && ++occurrence == 0) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns everything up to (not including) the nth occurrence of the specified
   * substring, or the entire string if there is no nth occurrence of the substring.
   * If the type of {@code subject} is {@code String}, you can do a reference
   * comparison between input and output string to ascertain whether the substring
   * was found. Returns an empty string if {@code subject} is {@code null}. You can
   * specify a negative occurrence to search backwards from the end of the string.
   *
   * @param subject The string to search
   * @param substr The substring to search for
   * @param occurrence The occurrence count of the substring
   * @return Everything to (not including) the nth occurrence of the specified
   *     substring
   */
  public static String substrBefore(Object subject, String substr, int occurrence) {
    int idx = indexOf(subject, substr, occurrence);
    if (idx == -1) {
      return subject == null ? EMPTY : subject.toString();
    }
    return subject.toString().substring(0, idx);
  }

  /**
   * Returns everything up to, <i>and including</i> the nth occurrence of the
   * specified substring, or the entire string if there is no nth occurrence of the
   * substring. If the type of {@code subject} is {@code String}, you can do a
   * reference comparison between input and output string to ascertain whether the
   * substring was found. Returns an empty string if {@code subject} is {@code null}.
   * You can specify a negative occurrence to search backwards from the end of the
   * string.
   *
   * @param subject The string to search
   * @param substr The substring to search for
   * @param occurrence The occurrence count of the substring
   * @return Everything up to, and including the nth occurrence of the specified
   *     substring
   */
  public static String substrTo(Object subject, String substr, int occurrence) {
    int idx = indexOf(subject, substr, occurrence);
    if (idx == -1) {
      return subject == null ? EMPTY : subject.toString();
    }
    if (subject.getClass() == String.class) {
      String s = subject.toString();
      return idx + substr.length() == s.length()
          ? new String(s)
          : s.substring(0, idx + substr.length());
    }
    return subject.toString().substring(0, idx + substr.length());
  }

  /**
   * Returns everything from (inclusive) the nth occurrence of the specified
   * substring, or the entire string if there is no nth occurrence of the
   * substring.If the type of {@code subject} is {@code String}, you can do a
   * reference comparison between input and output string to ascertain whether the
   * substring was found. Returns an empty string if {@code subject} is {@code null}.
   * You can specify a negative occurrence to search backwards from the end of the
   * string.
   *
   * @param subject The string to search
   * @param substr The substring to search for
   * @param occurrence The occurrence count of the substring
   * @return Everything from (inclusive) the nth occurrence of the specified
   *     substring
   */
  public static String substrFrom(Object subject, String substr, int occurrence) {
    int idx = indexOf(subject, substr, occurrence);
    if (idx == -1) {
      return subject == null ? EMPTY : subject.toString();
    }
    if (subject.getClass() == String.class) {
      String s = subject.toString();
      return idx == 0 ? new String(s) : s.substring(idx);
    }
    return subject.toString().substring(idx);
  }

  /**
   * Returns everything after (not including) the nth occurrence of the specified
   * substring, or the entire string there is no nth occurrence of the substring. If
   * the type of {@code subject} is {@code String}, you can do a reference comparison
   * between input and output string to ascertain whether the substring was found.
   * Returns an empty string if {@code subject} is {@code null}. You can specify a
   * negative occurrence to search backwards from the end of the string.
   *
   * @param subject The string to search
   * @param substr The substring to search for
   * @param occurrence The occurrence count of the substring
   * @return Everything after (not including) the nth occurrence of the specified
   *     substring
   */
  public static String substrAfter(Object subject, String substr, int occurrence) {
    int idx = indexOf(subject, substr, occurrence);
    if (idx == -1) {
      return subject == null ? EMPTY : subject.toString();
    }
    return subject.toString().substring(idx + substr.length());
  }

  /**
   * Returns the line number and column number of the character at the specified
   * index, given the system-defined line separator.
   *
   * @param str The string to search
   * @param index The string index to determine the line and column number of
   * @return A two-element array containing the line number and column number of the
   *     character at the specified index
   */
  public static int[] getLineAndColumn(String str, int index) {
    return getLineAndColumn(str, index, System.lineSeparator());
  }

  /**
   * Returns the line number and column number of the character at the specified
   * index, given the specified line separator.
   *
   * @param str The string to search
   * @param index The string index to determine the line and column number of
   * @param lineSep The line separator
   * @return A two-element array containing the line number and column number of the
   *     character at the specified index
   */
  public static int[] getLineAndColumn(String str, int index, String lineSep) {
    Check.notNull(str, "str");
    Check.that(index).is(stringIndexOf(), str, indexOutOfBounds(index));
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

}
