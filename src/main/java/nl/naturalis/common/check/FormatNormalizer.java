package nl.naturalis.common.check;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.TypeConversionException;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.Map.entry;

class FormatNormalizer {

  private static boolean USE_REGEX = false;

  private static final String REGEX = "\\$\\{(test|arg|type|name|obj|\\d)}";
  private static final Pattern PATTERN = Pattern.compile(REGEX);

  private static final Map<String, String> VARS_REGEX = Map.ofEntries(entry("test", "%1\\$s"),
      entry("arg", "%2\\$s"),
      entry("type", "%3\\$s"),
      entry("name", "%4\\$s"),
      entry("obj", "%5\\$s"),
      entry("0", "%6\\$s"),
      entry("1", "%7\\$s"),
      entry("2", "%8\\$s"),
      entry("3", "%9\\$s"),
      entry("4", "%10\\$s"),
      entry("5", "%11\\$s"),
      entry("6", "%12\\$s"),
      entry("7", "%13\\$s"),
      entry("8", "%14\\$s"),
      entry("9", "%15\\$s"));

  private static final Map<String, String> VARS = Map.ofEntries(entry("test", "%1$s"),
      entry("arg", "%2$s"),
      entry("type", "%3$s"),
      entry("name", "%4$s"),
      entry("obj", "%5$s"),
      entry("0", "%6$s"),
      entry("1", "%7$s"),
      entry("2", "%8$s"),
      entry("3", "%9$s"),
      entry("4", "%10$s"),
      entry("5", "%11$s"),
      entry("6", "%12$s"),
      entry("7", "%13$s"),
      entry("8", "%14$s"),
      entry("9", "%15$s"));

  static String normalize(String fmt) {
    return USE_REGEX ? normalizeRegex(fmt) : normalizeNoRegex(fmt);
  }

  static String format(String fmt, Object[] msgArgs) {
    return formatNoRegex(fmt, msgArgs);
  }

  static String normalizeRegex(String fmt) {
    return PATTERN.matcher(fmt).replaceAll(r -> {
      String var = VARS_REGEX.get(r.group(1));
      return var == null ? "${" + r.group(1) + "}" : var;
    });
  }

  private static String normalizeNoRegex(String fmt) {
    StringBuilder out = new StringBuilder(fmt.length());
    StringBuilder tmp = new StringBuilder(4);
    int EOL = fmt.length() - 1;
    int i = 0;
    boolean assembling = false;
    while (i < fmt.length()) {
      char c = fmt.charAt(i);
      if (assembling) {
        if (c == '}') {
          String assembled = tmp.toString();
          String s = VARS.get(assembled);
          if (s == null) {
            out.append("${").append(assembled).append('}');
          } else {
            out.append(s);
          }
          assembling = false;
          tmp.setLength(0);
          ++i;
        } else if (i == EOL) {
          out.append("${").append(tmp).append(c);
          break;
        } else {
          tmp.append(c);
          ++i;
        }
      } else if (c == '$') {
        if (i == EOL || fmt.charAt(i + 1) != '{') {
          out.append('$');
          ++i;
        } else if (i + 1 == EOL) {
          out.append("${");
          break;
        } else {
          assembling = true;
          i += 2;
        }
      } else {
        out.append(c);
        ++i;
      }
    }
    return out.toString();
  }

  private static String formatNoRegex(String fmt, Object[] msgArgs) {
    StringBuilder out = new StringBuilder(fmt.length());
    StringBuilder tmp = new StringBuilder(4);
    int EOL = fmt.length() - 1;
    int i = 0;
    boolean assembling = false;
    while (i < fmt.length()) {
      char c = fmt.charAt(i);
      if (assembling) {
        if (c == '}') {
          out.append(lookup(tmp.toString(), msgArgs));
          assembling = false;
          tmp.setLength(0);
          ++i;
        } else if (i == EOL) {
          out.append("${").append(tmp).append(c);
          break;
        } else {
          tmp.append(c);
          ++i;
        }
      } else if (c == '$') {
        if (i == EOL || fmt.charAt(i + 1) != '{') {
          out.append('$');
          ++i;
        } else if (i + 1 == EOL) {
          out.append("${");
          break;
        } else {
          assembling = true;
          i += 2;
        }
      } else {
        out.append(c);
        ++i;
      }
    }
    return out.toString();
  }

  private static String lookup(String arg, Object[] args) {
    switch (arg) {
      case "test":
        return (String) args[0];
      case "arg":
        return (String) args[1];
      case "type":
        return (String) args[2];
      case "name":
        return (String) args[3];
      case "obj":
        return (String) args[4];
      default:
        try {
          int i = NumberMethods.parseInt(arg);
          if (i + 5 < args.length) {
            return Objects.toString(args[i + 5]);
          }
        } catch (TypeConversionException e) {
        }
        return "${" + arg + "}";
    }
  }
}
