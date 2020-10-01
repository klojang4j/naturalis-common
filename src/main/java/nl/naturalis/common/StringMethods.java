package nl.naturalis.common;

import java.util.Arrays;
import java.util.Collection;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ObjectMethods.ifTrue;
import static nl.naturalis.common.check.CommonChecks.deepNotEmpty;
import static nl.naturalis.common.check.CommonChecks.noneNull;
import static nl.naturalis.common.check.CommonChecks.notEmpty;

/**
 * Methods for working with strings. Most methods are friendly towards batch-wise print jobs, trying
 * "to make the best" of their input: all methods are null-safe and they never return null. The
 * string to be manipulated is named {@code subject} and is passed in as an {@link Object}. If the
 * argument is null, an empty string is returned. Otherwise manipulation is done on the string
 * resulting from {@link Object#toString() Object.toString}.
 */
public final class StringMethods {

  /** The empty string. */
  public static final String EMPTY = "";

  private StringMethods() {}

  /**
   * Appends data to the specified {@code StringBuilder}.
   *
   * @param sb The {@code StringBuilder} (must not be null)
   * @param data The data to append (must not be null)
   * @return The {@code StringBuilder} with the appended data
   */
  public static StringBuilder append(StringBuilder sb, Object... data) {
    Check.notNull(sb, "sb");
    Arrays.stream(Check.notNull(data, "data").ok()).forEach(sb::append);
    return sb;
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
    Check.that(substr, "substr", notEmpty());
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
    Check.that(substr, "substr", notEmpty());
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
   * Whether or not the provided string ends with any of the provided suffixes. They suffixes must
   * not be null or empty.
   *
   * @param subject The string to test
   * @param ignoreCase Whether or not to ignore case
   * @param suffixes The suffixes to test
   * @return The first suffix found to be equal to the end of the string, or null if the string
   *     ended in none of the provided suffixes
   */
  public static String endsWith(Object subject, boolean ignoreCase, Collection<String> suffixes) {
    Check.notNull(suffixes, "suffixes");
    return endsWith(subject, ignoreCase, suffixes.toArray(new String[suffixes.size()]));
  }

  /**
   * Whether or not {@code subject} ends with any of the provided suffixes. They suffixes must not
   * be null or empty. Returns the first suffix found to be equal to the end of the string, or null
   * if the string ended in none of the provided suffixes.
   *
   * @param subject The string to test
   * @param ignoreCase Whether or not to ignore case
   * @param suffixes The suffixes to test
   * @return The first suffix found to be equal to the end of the string, or null if the string
   *     ended in none of the provided suffixes
   */
  public static String endsWith(Object subject, boolean ignoreCase, String... suffixes) {
    Check.that(suffixes, "suffixes", deepNotEmpty());
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
   * Prefixes to provided prefix to {@code subject} if it did not already have that prefix. If
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
   * Prefixes to provided prefix to {@code subject} if it did not already have that prefix. If
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
   * Appends to provided suffix to {@code subject} if it did not already have that suffix. If {@code
   * subject} is null, {@code suffix} is returned.
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
   * Appends to provided suffix to {@code str} if it did not already have that suffix.
   *
   * @param subject The {@code String} to which to append the suffix
   * @param suffix The suffix
   */
  public static String ensureSuffix(Object subject, char suffix) {
    return subject == null ? String.valueOf(suffix) : subject.toString() + suffix;
  }

  /**
   * Whether or not the provided string is null or blank.
   *
   * @param subject The string
   * @return Whether it is null or blank
   */
  public static boolean isBlank(Object subject) {
    return subject == null || subject.toString().isBlank();
  }

  /**
   * Whether or not the provided string is null or empty.
   *
   * @param subject The string to check
   * @return Whether it is null or empty
   */
  public static boolean isEmpty(Object subject) {
    return subject == null || subject.toString().isEmpty();
  }

  /**
   * Whether or not the provided string neither null nor blank.
   *
   * @param object The string to check
   * @return Whether it is neither null nor blank
   */
  public static boolean isNotBlank(Object object) {
    return !isBlank(object);
  }

  /**
   * Whether or not the provided string neither null nor empty.
   *
   * @param subject The string to check
   * @return Whether it is neither null nor empty
   */
  public static boolean isNotEmpty(Object subject) {
    return !isEmpty(subject);
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
   * Returns the 1st argument if it is not an empty string, else the 2nd argument.
   *
   * @see ObjectMethods#ifEmpty(Object, Object)
   * @param str
   * @param dfault
   */
  public static String ifEmpty(String str, String dfault) {
    return isEmpty(str) ? dfault : str;
  }

  /**
   * Removes all occurrences of the provided prefixes from the start of a string. The returned
   * string will no longer start with any of the provided prefixes.
   *
   * @param subject
   * @param ignoreCase
   * @param prefixes
   */
  public static String lchop(Object subject, boolean ignoreCase, Collection<String> prefixes) {
    return lchop(subject, ignoreCase, prefixes.toArray(new String[prefixes.size()]));
  }

  /**
   * Removes all occurrences of the provided prefixes from the start of a string. The returned
   * string will no longer start with any of the provided prefixes.
   *
   * @param subject The string to remove the prefixes from
   * @param ignoreCase Whether or not to ignore case
   * @param prefixes The prefixes to remove
   */
  public static String lchop(Object subject, boolean ignoreCase, String... prefixes) {
    Check.that(prefixes, "prefixes", noneNull());
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
   * Left-pads a string to the specified width using the space character (' ') and then appends the
   * specified terminator.
   *
   * @param obj An object whose {@code toString()} method produces the string to be padded. Null is
   *     treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is printed without padding.
   * @param terminator The delimiter to append to the left-padded string.
   * @return The left-padded string
   * @throws IllegalArgumentException If {@code terminator} is null
   */
  public static String lpad(Object obj, int width, String terminator) {
    return lpad(obj, width, ' ', terminator);
  }

  /**
   * Left-pads a string to the specified width using the space character (' ').
   *
   * @param obj An object whose {@code toString()} method produces the string to be padded. Null is
   *     treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is printed without padding.
   * @return The left-padded string
   */
  public static String lpad(Object obj, int width) {
    return lpad(obj, width, ' ', EMPTY);
  }

  /**
   * Left-pads a string to the specified width using the specified padding character.
   *
   * @param obj An object whose {@code toString()} method produces the string to be padded. Null is
   *     treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is printed without padding.
   * @param padChar The character used to left-pad the string
   * @return The left-padded string
   */
  public static String lpad(Object obj, int width, char padChar) {
    return lpad(obj, width, padChar, EMPTY);
  }

  /**
   * Left-pads a string to the specified width using the specified padding character and then
   * appends the specified terminator.
   *
   * @param obj An object whose {@code toString()} method produces the string to be padded. Null is
   *     treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is printed without padding.
   * @param padChar The character used to left-pad the string
   * @param terminator The string to append to the left-padded string
   * @return The left-padded string
   * @throws IllegalArgumentException If {@code terminator} is null
   */
  public static String lpad(Object obj, int width, char padChar, String terminator) {
    Check.notNull(terminator, "terminator");
    String s;
    if (obj == null) {
      s = EMPTY;
    } else {
      if ((s = obj.toString()).length() >= width) {
        return s + terminator;
      }
    }
    StringBuilder sb = new StringBuilder(width + terminator.length());
    for (int i = s.length(); i < width; ++i) {
      sb.append(padChar);
    }
    sb.append(s);
    sb.append(terminator);
    return sb.toString();
  }

  /**
   * Left-trims the provided string. The resulting string will not start with the specified
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
   * Left-trims all characters contained in {@code chars} from the provided provided string. The
   * resulting string will not start with any of the charachters contained in {@code chars}.
   *
   * @param subject The {@code String} to trim
   * @param chars The character to trim off the {@code String}
   * @return The left-trimmed {@code String} or the input string if it did not start with any of the
   *     specified characters
   */
  public static String ltrim(Object subject, String chars) {
    Check.that(chars, "chars", notEmpty());
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
   * @param obj An object whose {@code toString()} method produces the string to be padded. Null is
   *     treated as the empty string.
   * @param width
   */
  public static String pad(Object obj, int width) {
    return pad(obj, width, ' ', EMPTY);
  }

  /**
   * Centers (left- and right-pads) a string within the specified width using the specified padding
   * character.
   *
   * @param obj An object whose {@code toString()} method produces the string to be padded. Null is
   *     treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is printed without padding.
   * @param padChar The character used to left- and right-pad the string.
   * @return The left- and right-padded string plus the terminator
   */
  public static String pad(Object obj, int width, char padChar) {
    return pad(obj, width, padChar, EMPTY);
  }

  /**
   * Centers (left- and right-pads) a string within the specified width using the specified padding
   * character and then appends the specified terminator.
   *
   * @param subject An object whose {@code toString()} method produces the string to be padded. Null
   *     is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is printed without padding.
   * @param padChar The character used to left- and right-pad the string.
   * @param terminator The string to append to the padded string
   * @return The left- and right-padded string plus the terminator
   */
  public static String pad(Object subject, int width, char padChar, String terminator) {
    String s;
    if (subject == null) {
      s = EMPTY;
    } else {
      s = subject.toString();
      if (s.length() >= width) {
        return s + terminator;
      }
    }
    int left = (width - s.length()) / 2;
    int right = width - left - s.length();
    StringBuilder sb = new StringBuilder(width + terminator.length());
    for (int i = 0; i < left; ++i) {
      sb.append(padChar);
    }
    sb.append(s);
    for (int i = 0; i < right; ++i) {
      sb.append(padChar);
    }
    sb.append(terminator);
    return sb.toString();
  }

  /**
   * Removes all occurrences of the provided suffixes from the end of a string. The returned string
   * will no longer end with any of the provided suffixes.
   *
   * @param subject
   * @param ignoreCase
   * @param suffixes
   */
  public static String rchop(Object subject, boolean ignoreCase, Collection<String> suffixes) {
    return rchop(subject, ignoreCase, suffixes.toArray(new String[suffixes.size()]));
  }

  /**
   * Removes all occurrences of the provided suffixes from the end of a string. The returned string
   * will no longer end with any of the provided suffixes.
   *
   * @param subject
   * @param ignoreCase
   * @param suffixes
   */
  public static String rchop(Object subject, boolean ignoreCase, String... suffixes) {
    Check.notNull(suffixes, "suffixes");
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
   * Right-pads a string to the specified width using the space character (' ') and then appends the
   * specified terminator.
   *
   * @param obj An object whose {@code toString()} method produces the string to be padded. Null is
   *     treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is printed without padding.
   * @param terminator The delimiter to append to the right-padded string.
   * @return The right-padded string
   */
  public static String rpad(Object obj, int width, String terminator) {
    return rpad(obj, width, ' ', terminator);
  }

  /**
   * Right-pads a string to the specified width using the space character (' ').
   *
   * @param obj An object whose {@code toString()} method produces the string to be padded. Null is
   *     treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is printed without padding.
   * @return The right-padded string
   */
  public static String rpad(Object obj, int width) {
    return rpad(obj, width, ' ', EMPTY);
  }

  /**
   * Right-pads a string to the specified width using the specified padding character.
   *
   * @param obj An object whose {@code toString()} method produces the string to be padded. Null is
   *     treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is printed without padding.
   * @param padChar The character used to left-pad the string.
   * @return The right-padded string
   */
  public static String rpad(Object obj, int width, char padChar) {
    return rpad(obj, width, padChar, EMPTY);
  }

  /**
   * Right-pads a string to the specified width using the specified padding character and then
   * appends the specified terminator.
   *
   * @param obj An object whose {@code toString()} method produces the string to be padded. Null is
   *     treated as the empty string.
   * @param width The total length of the padded string. If the string itself is wider than the
   *     specified width, the string is printed without padding.
   * @param padChar The character used to left-pad the string.
   * @param terminator The string to append to the left-padded string
   * @return The right-padded string
   */
  public static String rpad(Object obj, int width, char padChar, String terminator) {
    String s;
    if (obj == null) {
      s = EMPTY;
    } else {
      if ((s = obj.toString()).length() >= width) {
        return s + terminator;
      }
    }
    StringBuilder sb = new StringBuilder(width + terminator.length());
    sb.append(s);
    for (int i = s.length(); i < width; ++i) {
      sb.append(padChar);
    }
    sb.append(terminator);
    return sb.toString();
  }

  /**
   * Trims the specified character off both sides of the specified {@code String}. This method
   * returns an empty String if the specified {@code String} is null.
   *
   * @param subject The {@code String} to trim
   * @param c The character to trim off the {@code String}
   * @return The trimmed {@code String}.
   */
  public static String trim(Object subject, char c) {
    return ltrim(rtrim(subject, c), c);
  }

  /**
   * Right-trims the provided string. The resulting string will not end with the specified
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
   * Right-trims all characters contained in {@code chars} from the provided provided string. The
   * resulting string will not end with any of the charachters contained in {@code chars}.
   *
   * @param subject The {@code String} to trim
   * @param chars The character to trim off the {@code String}
   * @return The right-trimmed {@code String} or the input string if it did not end with any of the
   *     specified characters
   */
  public static String rtrim(Object subject, String chars) {
    Check.that(chars, "chars", notEmpty());
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
   * Substring method that allows substring retrieval relative to the end of a string. See {@link
   * #substring(Object, int, int)}.
   *
   * @param subject The {@code String} to extract a substring from
   * @param from The start index within {@code string} (may be negative)
   * @return The substring
   */
  public static String substring(Object subject, int from) {
    String str;
    if (subject == null || from > (str = subject.toString()).length()) {
      return EMPTY;
    }
    if (from < 0) {
      from = Math.max(0, str.length() + from);
    }
    return str.substring(from);
  }

  /**
   * Substring method that allows for substring retrieval relative to the end of a string. The
   * {@code length} argument will be clamped to remain with the bounds of the string.
   *
   * <p>
   *
   * <ol>
   *   <li>If the input string is null or empty, or if {@code length} is 0, en empty string is
   *       returned.
   *   <li>If {@code from} is negative, it is taken relative to the end of the string.
   *   <li>If {@code length} is negative, the substring is taken to the left of {@code from}
   *       (exclusive). So {@code from} in effect becomes the standard <i>to-exclusive</i> argument.
   *   <li>Both {@code from} and {@code length} are clamped to the start and/or end of the string.
   *       In other words you will never get an {@link ArrayIndexOutOfBoundsException}.
   * </ol>
   *
   * @param subject The {@code String} to extract a substring from
   * @param from The start index within {@code string} (may be negative)
   * @param length The length the substring
   * @return The substring
   */
  public static String substring(Object subject, int from, int length) {
    String str;
    if (subject == null || length == 0 || (str = subject.toString()).isEmpty()) {
      return EMPTY;
    }
    if (from < 0) {
      from = Math.max(0, str.length() + from);
    }
    if (length < 0) {
      length = Math.min(from, -length);
      from -= length;
    } else {
      from = Math.min(str.length(), from);
    }
    int to = Math.min(str.length(), from + length);
    return str.substring(from, to);
  }

  /**
   * Returns everything up to (not including) the last occurence of {@code until} within the string,
   * or the entire string if it does not contain {@code until}.
   *
   * @param subject The string to take a substring from
   * @param until The string up until which to take the substring
   * @return The substring
   */
  public static String substringUpTo(Object subject, String until) {
    return substringUpTo(subject, until, false);
  }

  /**
   * Returns everything up to (not including) the first or last occurence of {@code until} within
   * the string, or the entire string if it does not contain {@code until}.
   *
   * @param subject The string to take a substring from
   * @param until The string up until which to take the substring
   * @param firstOccurrence Whether to use the first of last occurrence of {@code until}
   * @return The substring
   */
  public static String substringUpTo(Object subject, String until, boolean firstOccurrence) {
    if (subject == null) {
      return EMPTY;
    }
    String str = subject.toString();
    int i = firstOccurrence ? str.indexOf(until) : str.lastIndexOf(until);
    return i == -1 ? str : str.substring(0, i);
  }

  /**
   * Returns everything up to (not including) the first or last occurence of {@code until} within
   * the string, or the entire string if it does not contain {@code until}.
   *
   * @param subject The string to take a substring from
   * @param until The string up until which to take the substring
   * @return The substring
   */
  public static String substringUpTo(Object subject, char until) {
    return substringUpTo(subject, until, false);
  }

  /**
   * Returns everything up to (not including) the first or last occurence of {@code until} within
   * the string, or the entire string if it does not contain {@code until}.
   *
   * @param subject The string to take a substring from
   * @param until The string up until which to take the substring
   * @param firstOccurrence Whether to use the first of last occurrence of {@code until}
   * @return The substring
   */
  public static String substringUpTo(Object subject, char until, boolean firstOccurrence) {
    if (subject == null) {
      return EMPTY;
    }
    String str = subject.toString();
    int i = firstOccurrence ? str.indexOf(until) : str.lastIndexOf(until);
    return i == -1 ? str : str.substring(0, i);
  }
}
