package nl.naturalis.common.check;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.TypeConversionException;

import java.util.Objects;
import java.util.regex.Pattern;

import static nl.naturalis.common.check.CommonChecks.NAMES;
import static nl.naturalis.common.check.MsgUtil.simpleClassName;
import static nl.naturalis.common.check.MsgUtil.toStr;

final class CustomMsgFormatter {

  private CustomMsgFormatter() {}

  // Can we squeeze out some extra performance by manually
  // parsing the message pattern? Yes, we consistently get
  // about +/- 50% better performance
  private static final boolean USE_REGEX = false;

  private static final String REGEX = "\\$\\{(test|arg|type|name|obj|\\d+)}";
  private static final Pattern PATTERN = Pattern.compile(REGEX);

  private static final String ARG_START = "${";
  private static final char ARG_END = '}';

  static String format(String fmt, Object[] msgArgs) {
    return USE_REGEX ? formatRegex(fmt, msgArgs) : formatNoRegex(fmt, msgArgs);
  }

  private static String formatRegex(String fmt, Object[] msgArgs) {
    return PATTERN.matcher(fmt).replaceAll(mr -> lookup(mr.group(1), msgArgs));
  }

  private static String formatNoRegex(String fmt, Object[] msgArgs) {
    int x = fmt.indexOf(ARG_START);
    if (x == -1) {
      return fmt;
    }
    StringBuilder out = new StringBuilder(fmt.length() + 20);
    int y = 0;
    do {
      out.append(fmt.substring(y, x));
      if ((y = fmt.indexOf(ARG_END, x += 2)) == -1) {
        return out.append(ARG_START).append(fmt.substring(x)).toString();
      }
      out.append(lookup(fmt.substring(x, y), msgArgs));
      if ((x = fmt.indexOf(ARG_START, y += 1)) == -1) {
        return out.append(fmt.substring(y)).toString();
      }
    } while (true);
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
        if (USE_REGEX) {
          return '\\' + ARG_START + arg + ARG_END;
        }
        return ARG_START + arg + ARG_END;
    }
  }

}
