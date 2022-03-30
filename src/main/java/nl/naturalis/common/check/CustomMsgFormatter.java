package nl.naturalis.common.check;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.TypeConversionException;

import java.util.Objects;
import java.util.regex.Pattern;

class CustomMsgFormatter {

  private static boolean USE_REGEX = false;

  private static final String REGEX = "\\$\\{(test|arg|type|name|obj|\\d+)}";
  private static final Pattern PATTERN = Pattern.compile(REGEX);

  static String format(String fmt, Object[] msgArgs) {
    return formatNoRegex(fmt, msgArgs);
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
