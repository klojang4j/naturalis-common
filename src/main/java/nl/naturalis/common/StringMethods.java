package nl.naturalis.common;

import java.util.Collection;
import java.util.regex.Pattern;
import static nl.naturalis.common.ObjectMethods.ifTrue;

/**
 * Methods for working with strings. This class tries to avoid duplicating the
 * functionality already present in Apache Commons and Google Guava, but since
 * the naturalis-common library as a whole is designed to be self-contained,
 * that is not always possible. All methods are null-safe for the
 * <code>String</code> being inspected or manipulated (usually named
 * {@code subject}). They also never <i>return</i> null unless otherwise
 * indicated. The string to be inspected or manipulated is always passed in as
 * an argument of type {@link Object}. If the argument is null, an empty string
 * is returned (in most cases). Otherwise inspection or manipulation is done on
 * the string resulting from {@link Object#toString() Object.toString}.
 */
public final class StringMethods {

  /**
   * The empty string.
   */
  public static final String EMPTY = "";

  private StringMethods() {}

  /**
   * Appends to provided suffix to {@code subject} if it did not already have that
   * suffix. If {@code subject} is null, <code>suffix</code> is returned.
   *
   * @param subject
   * @param suffix
   * @return
   */
  public static String appendIfAbsent(Object subject, String suffix) {
    if (subject == null) {
      return suffix;
    }
    String str = subject.toString();
    return str.endsWith(suffix) ? str : str + suffix;
  }

  /**
   * Appends to provided suffix to {@code str} if it did not already have that
   * suffix.
   *
   * @param subject
   * @param suffix
   * @return
   */
  public static String appendIfAbsent(Object subject, char suffix) {
    return appendIfAbsent(subject, String.valueOf(suffix));
  }

  /**
   * Equivalent to <code>count(subject, substr, false)</code>.
   *
   * @param subject
   * @param substr
   * @return
   */
  public static int count(Object subject, String substr) {
    return count(subject, substr, false);
  }

  /**
   * Counts the number of occurrences of <code>substr</code> within
   * {@code subject}.
   *
   * @param subject The string to search
   * @param substr The substring to search for
   * @param ignoreCase Whether or not to ignore case while matching
   *        <code>substr</code> against {@code subject}
   * @return
   */
  public static int count(Object subject, String substr, boolean ignoreCase) {
    Check.notNull(substr, "substr");
    if (subject == null) {
      return 0;
    }
    String str = ifTrue(ignoreCase, subject.toString(), String::toLowerCase);
    substr = ifTrue(ignoreCase, substr, String::toLowerCase);
    return (int) Pattern.compile(Pattern.quote(substr)).matcher(str).results().count();
  }

  /**
   * Whether or not the provided string ends with any of the provided suffixes.
   * They suffixes must not be null. They may be empty, but {@code endsWith}
   * always returns {@code false} for them.
   *
   * @param subject The string to test
   * @param ignoreCase Whether or not to ignore case
   * @param suffixes The suffixes to test
   * @return
   */
  public static String endsWith(Object subject, boolean ignoreCase, Collection<String> suffixes) {
    Check.notNull(suffixes, "suffixes");
    return endsWith(subject, ignoreCase, suffixes.toArray(new String[suffixes.size()]));
  }

  /**
   * Whether or not {@code subject} ends with any of the provided suffixes.
   * Returns the first suffix found to be equals to the end of the string, or null
   * if thr string ended in none of the provided suffixes.
   *
   * @param subject
   * @param ignoreCase
   * @param suffixes
   * @return
   */
  public static String endsWith(Object subject, boolean ignoreCase, String... suffixes) {
    Check.notNull(suffixes, "suffixes");
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
   * Whether or not the provided string is null or blank.
   *
   * @param subject The string to check
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
   * Returns the 1st argument if it is not a whitespace-only string, else the 2nd
   * argument.
   *
   * @see ObjectMethods#ifNull(Object, Object)
   *
   * @param subject
   * @param dfault
   * @return
   */
  public static String ifBlank(Object subject, String dfault) {
    return isBlank(subject) ? dfault : subject.toString();
  }

  /**
   * Returns the 1st argument if it is not an empty string, else the 2nd argument.
   *
   * @see ObjectMethods#ifEmpty(Object, Object)
   *
   * @param str
   * @param dfault
   * @return
   */
  public static String ifEmpty(String str, String dfault) {
    return isEmpty(str) ? dfault : str;
  }

  /**
   * Removes all occurrences of the provided prefixes from the start of a string.
   * The returned string will no longer start with any of the provided prefixes.
   *
   * @param subject
   * @param ignoreCase
   * @param prefixes
   * @return
   */
  public static String lchop(Object subject, boolean ignoreCase, Collection<String> prefixes) {
    return lchop(subject, ignoreCase, prefixes.toArray(new String[prefixes.size()]));
  }

  /**
   * Removes all occurrences of the provided prefixes from the start of a string.
   * The returned string will no longer start with any of the provided prefixes.
   *
   * @param subject
   * @param ignoreCase
   * @param prefixes
   * @return
   */
  public static String lchop(Object subject, boolean ignoreCase, String... prefixes) {
    Check.noneNull(prefixes, "prefixes");
    if (subject == null) {
      return EMPTY;
    }
    String s0 = subject.toString();
    boolean changed;
    do {
      changed = false;
      for (String prefix : prefixes) {
        if (!prefix.isEmpty() && s0.regionMatches(ignoreCase, 0, prefix, 0,
            prefix.length())) {
          s0 = s0.substring(prefix.length());
          changed = true;
        }
      }
    } while (changed);
    return s0;
  }

  /**
   * Left-pads a string to the specified width using the space character (' ') and
   * then appends the specified terminator.
   *
   * @param obj An object whose {@code toString()} method produces the string to
   *        be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *        wider than the specified width, the string is printed without padding.
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
   * @param obj An object whose {@code toString()} method produces the string to
   *        be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *        wider than the specified width, the string is printed without padding.
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
   *        be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *        wider than the specified width, the string is printed without padding.
   * @param padChar The character used to left-pad the string
   * @return The left-padded string
   */
  public static String lpad(Object obj, int width, char padChar) {
    return lpad(obj, width, padChar, EMPTY);
  }

  /**
   * Left-pads a string to the specified width using the specified padding
   * character and then appends the specified terminator.
   *
   * @param obj An object whose {@code toString()} method produces the string to
   *        be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *        wider than the specified width, the string is printed without padding.
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
   * Left-trims the provided string. The resulting string will not start with the
   * specified character.
   *
   * @param subject The {@code String} to trim
   * @param c The character to trim off the {@code String}
   *
   * @return The left-trimmed {@code String} or the input string if it did not
   *         start with the specified character
   *
   */
  public static String ltrim(Object subject, char c) {
    return ltrim(subject, String.valueOf(c));
  }

  /**
   * Left-trims all characters contained in {@code chars} from the provided
   * provided string. The resulting string will not start with any of the
   * charachters contained in {@code chars}.
   *
   * @param subject The {@code String} to trim
   * @param chars The character to trim off the {@code String}
   * @return The left-trimmed {@code String} or the input string if it did not
   *         start with any of the specified characters
   */
  public static String ltrim(Object subject, String chars) {
    Check.notEmpty(chars, "chars");
    if (subject == null) {
      return EMPTY;
    }
    String str0 = subject.toString();
    int i = 0;
    LOOP: for (; i < str0.length(); ++i) {
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
   * Centers (left- and right-pads) a string within the specified width using the
   * space character.
   *
   * @param obj An object whose {@code toString()} method produces the string to
   *        be padded. Null is treated as the empty string.
   * @param width
   * @return
   */
  public static String pad(Object obj, int width) {
    return pad(obj, width, ' ', EMPTY);
  }

  /**
   * Centers (left- and right-pads) a string within the specified width using the
   * specified padding character.
   *
   * @param obj An object whose {@code toString()} method produces the string to
   *        be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *        wider than the specified width, the string is printed without padding.
   * @param padChar The character used to left- and right-pad the string.
   * @return The left- and right-padded string plus the terminator
   */
  public static String pad(Object obj, int width, char padChar) {
    return pad(obj, width, padChar, EMPTY);
  }

  /**
   * Centers (left- and right-pads) a string within the specified width using the
   * specified padding character and then appends the specified terminator.
   *
   * @param subject An object whose {@code toString()} method produces the string
   *        to be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *        wider than the specified width, the string is printed without padding.
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
   * Prefixes {@code prefix} to {@code str} if {@code str} did not already start
   * with it.
   *
   * @param str
   * @param prefix
   * @return
   */
  public static String prefixIfAbsent(String str, String prefix) {
    return str.startsWith(prefix) ? str : prefix + str;
  }

  /**
   * Appends to provided suffix to {@code str} if it did not already have that
   * suffix.
   *
   * @param str
   * @param suffix
   * @return
   */
  public static String prefixIfAbsent(String str, char suffix) {
    return appendIfAbsent(str, String.valueOf(suffix));
  }

  /**
   * Removes all occurrences of the provided suffixes from the end of a string.
   * The returned string will no longer end with any of the provided suffixes.
   *
   * @param subject
   * @param ignoreCase
   * @param suffixes
   * @return
   */
  public static String rchop(Object subject, boolean ignoreCase, Collection<String> suffixes) {
    return rchop(subject, ignoreCase, suffixes.toArray(new String[suffixes.size()]));
  }

  /**
   * Removes all occurrences of the provided suffixes from the end of a string.
   * The returned string will no longer end with any of the provided suffixes.
   *
   * @param subject
   * @param ignoreCase
   * @param suffixes
   * @return
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
   * Right-pads a string to the specified width using the space character (' ')
   * and then appends the specified terminator.
   *
   * @param obj An object whose {@code toString()} method produces the string to
   *        be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *        wider than the specified width, the string is printed without padding.
   * @param terminator The delimiter to append to the right-padded string.
   * @return The right-padded string
   */
  public static String rpad(Object obj, int width, String terminator) {
    return rpad(obj, width, ' ', terminator);
  }

  /**
   * Right-pads a string to the specified width using the space character (' ').
   *
   * @param obj An object whose {@code toString()} method produces the string to
   *        be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *        wider than the specified width, the string is printed without padding.
   * @return The right-padded string
   */
  public static String rpad(Object obj, int width) {
    return rpad(obj, width, ' ', EMPTY);
  }

  /**
   * Right-pads a string to the specified width using the specified padding
   * character.
   *
   * @param obj An object whose {@code toString()} method produces the string to
   *        be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *        wider than the specified width, the string is printed without padding.
   * @param padChar The character used to left-pad the string.
   * @return The right-padded string
   */
  public static String rpad(Object obj, int width, char padChar) {
    return rpad(obj, width, padChar, EMPTY);
  }

  /**
   * Right-pads a string to the specified width using the specified padding
   * character and then appends the specified terminator.
   *
   * @param obj An object whose {@code toString()} method produces the string to
   *        be padded. Null is treated as the empty string.
   * @param width The total length of the padded string. If the string itself is
   *        wider than the specified width, the string is printed without padding.
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
   * Trims the specified character off both sides of the specified {@code String}.
   * This method returns an empty String if the specified {@code String} is null.
   *
   * @param subject The {@code String} to trim
   * @param c The character to trim off the {@code String}
   *
   * @return The trimmed {@code String}.
   *
   */
  public static String trim(Object subject, char c) {
    return ltrim(rtrim(subject, c), c);
  }

  /**
   * Right-trims the provided string. The resulting string will not end with the
   * specified character.
   *
   * @param subject The {@code String} to trim
   * @param c The character to trim off the {@code String}
   *
   * @return The right-trimmed {@code String} or the input string if it did not
   *         end with the specified character
   *
   */
  public static String rtrim(Object subject, char c) {
    return rtrim(subject, String.valueOf(c));
  }

  /**
   * Right-trims all characters contained in {@code chars} from the provided
   * provided string. The resulting string will not end with any of the
   * charachters contained in {@code chars}.
   *
   * @param subject The {@code String} to trim
   * @param chars The character to trim off the {@code String}
   * @return The right-trimmed {@code String} or the input string if it did not
   *         end with any of the specified characters
   */

  public static String rtrim(Object subject, String chars) {
    Check.notEmpty(chars, "chars");
    if (subject == null) {
      return EMPTY;
    }
    String str0 = subject.toString();
    int i = str0.length() - 1;
    LOOP: for (; i >= 0; --i) {
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
   * Substring method that allows substring retrieval relative to the end of a
   * string. See {@link #substr(String, int, int)}.
   *
   * @param subject The {@code String} to extract a substring from
   * @param from The start index within {@code string} (may be negative)
   * @return The substring
   */
  public static String substr(Object subject, int from) {
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
   * Substring method that allows for substring retrieval relative to the end of a
   * string.<br>
   * <br>
   * <table>
   * <tr>
   * <td style='padding-right:50px;'>substr(null, 1, 5)</td>
   * <td>"" (empty string)</td>
   * </tr>
   * <tr>
   * <td style='padding-right:50px;'>substr("Hello", 200, 3)</td>
   * <td>"" (empty string)</td>
   * </tr>
   * <tr>
   * <td style='padding-right:50px;'>substr("Hello", 2, 0)</td>
   * <td>"" (empty string)</td>
   * </tr>
   * <tr>
   * <td style='padding-right:50px;'>substr("Hello", 2, -2)</td>
   * <td>"" (empty string)</td>
   * </tr>
   * <tr>
   * <td style='padding-right:50px;'>substr("Hello", -2, 100)</td>
   * <td>"lo"</td>
   * </tr>
   * <tr>
   * <td style='padding-right:50px;'>substr("Hello", -4, 3)</td>
   * <td>"ell"</td>
   * </tr>
   * <tr>
   * <td style='padding-right:50px;'>substr("Hello", -400, 3)</td>
   * <td>"Hel"</td>
   * </tr>
   * <tr>
   * <td style='padding-right:50px;'>substr("Hello", 1, 3)</td>
   * <td>"ell"</td>
   * </tr>
   * <tr>
   * <td style='padding-right:50px;'>substr("Hello", 1, 300)</td>
   * <td>"ello"</td>
   * </tr>
   * </table>
   *
   * @param subject The {@code String} to extract a substring from
   * @param from The start index within {@code string} (may be negative)
   * @param length The length the substring
   * @return The substring
   */
  public static String substr(Object subject, int from, int length) {
    String str;
    if (subject == null || from > (str = subject.toString()).length() || length < 1) {
      return EMPTY;
    }
    if (from < 0) {
      from = Math.max(0, str.length() + from);
    }
    int to = Math.min(str.length(), from + length);
    return str.substring(from, to);
  }

  /**
   * Returns everything up to (not including) the first or last occurence of
   * {@code until} within the string, or the entire string if it does not contain
   * {@code until}.
   *
   * @param subject The string to take a substring from
   * @param until The string up until which to take the substring
   * @param first Whether to use the first of last occurrence of {@code until}
   * @return The substring
   */
  public static String substr(Object subject, String until, boolean first) {
    if (subject == null) {
      return EMPTY;
    }
    String str = subject.toString();
    int i = first ? str.indexOf(until) : str.lastIndexOf(until);
    return i == -1 ? str : str.substring(0, i);
  }

  /**
   * Returns everything up to (not including) the first or last occurence of
   * {@code until} within the string, or the entire string if it does not contain
   * {@code until}.
   *
   * @param subject The string to take a substring from
   * @param until The string up until which to take the substring
   * @param first Whether to use the first of last occurrence of {@code until}
   * @return The substring
   */
  public static String substr(Object subject, char until, boolean first) {
    if (subject == null) {
      return EMPTY;
    }
    String str = subject.toString();
    int i = first ? str.indexOf(until) : str.lastIndexOf(until);
    return i == -1 ? str : str.substring(0, i);
  }

}
