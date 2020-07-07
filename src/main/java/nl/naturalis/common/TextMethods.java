package nl.naturalis.common;

import java.util.Collection;
import java.util.Map;

/**
 * Basic methods for working with text.
 * 
 * @author Ayco Holleman
 *
 */
public class TextMethods {

  private TextMethods() {}

  /**
   * Really basic method for creating the plural of a noun. Useful as a static import in the message argument list of a log message, or
   * within the argument list of <code>String.format</code>. Returns the unchanged noun if the map's size is 1, else the plural of the noun.
   * See {@link #plural(String, int)}.
   * 
   * @param noun
   * @param m
   * @return
   */
  public static String plural(String noun, Map<?, ?> m) {
    return plural(noun, m.size());
  }

  /**
   * Really basic method for creating the plural of a noun. Useful as a static import in the message argument list of a log message, or
   * within the argument list of <code>String.format</code>. Returns the unchanged noun if the collection's size is 1, else the plural of
   * the noun. See {@link #plural(String, int)}.
   * 
   * @param noun
   * @param c
   * @return
   */
  public static String plural(String noun, Collection<?> c) {
    return plural(noun, c.size());
  }

  /**
   * Really basic method for creating the plural of a noun. Useful as a static import in the message argument list of a log message, or
   * within the argument list of <code>String.format</code>. Returns the unchanged noun if the provided integer is 1, else the plural of the
   * noun. There is no linguistic sophistication in the generation of the plural and it's only useful for English messages.
   * <p>
   * Examples:
   * <table style="border:1px #000;">
   * <tr>
   * <td>countr<b>y</b></td>
   * <td>countr<b>ies</b></td>
   * </tr>
   * <tr>
   * <td>default</td>
   * <td>default<b>s</b></td>
   * </tr>
   * </table>
   * 
   * @param noun
   * @param count
   * @return
   */
  public static String plural(String noun, int count) {
    if (count == 1) {
      return noun;
    } else if (noun.charAt(noun.length() - 1) == 'y') {
      return noun.substring(0, noun.length() - 1) + "ies";
    }
    return noun + 's';
  }
}
