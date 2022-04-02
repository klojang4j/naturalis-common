package nl.naturalis.common.check;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.TypeConversionException;

import java.util.Objects;
import java.util.regex.Pattern;

import static nl.naturalis.common.check.CommonChecks.NAMES;
import static nl.naturalis.common.check.MsgUtil.simpleClassName;
import static nl.naturalis.common.check.MsgUtil.toStr;

class CustomMsgFormatter {

  // We used to have a regex impl as well, but it was ever so slightly slower
  // than the hand-woven impl, and it fell by the wayside for maintenance
  // reasons. Nevertheless, let's keep this around.
  private static final boolean USE_REGEX = false;
  private static final String REGEX = "\\$\\{(test|arg|type|name|obj|\\d+)}";
  private static final Pattern PATTERN = Pattern.compile(REGEX);

  static String format(String fmt, Object[] msgArgs) {
    return formatNoRegex(fmt, msgArgs);
  }

  private static String formatNoRegex(String fmt, Object[] msgArgs) {
    int x = fmt.indexOf("${");
    if (x == -1 || x == fmt.length() - 2) { // fmt ending with "${"
      return fmt;
    }
    int EOL = fmt.length() - 1;
    StringBuilder out = new StringBuilder(fmt.length());
    StringBuilder tmp = new StringBuilder(4);
    boolean assembling = true;
    int i = x + 2;
    do {
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
    } while (i < fmt.length());
    return fmt.substring(0, x) + out.toString();
  }

  private static String lookup(String arg, Object[] args) {
    switch (arg) {
      case "test":
        return NAMES.getOrDefault(args[0], args[0].getClass().getSimpleName());
      case "arg":
        return toStr(args[1]);
      case "type":
        return args[2] == null
            ? args[1] == null ? null : simpleClassName(args[1])
            : simpleClassName(args[2]);
      case "name":
        return (String) args[3];
      case "obj":
        return toStr(args[4]);
      default:
        try {
          int i = NumberMethods.parseInt(arg);
          if (i + 5 < args.length) {
            return Objects.toString(args[i + 5]);
          }
        } catch (TypeConversionException ignored) {
        }
        return "${" + arg + "}";
    }
  }
}
