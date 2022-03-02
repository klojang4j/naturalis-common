package nl.naturalis.common.check;

import java.util.regex.Pattern;

class FormatNormalizer {

  static final String REGEX = "\\$\\{(check|arg|type|name|obj|\\d+)}";
  static final Pattern PATTERN = Pattern.compile(REGEX);

  static String normalize(String fmt) {
    return PATTERN.matcher(fmt).replaceAll(r -> switch (r.group(1)) {
      case "check" -> "%1\\$s";
      case "arg" -> "%2\\$s";
      case "type" -> "%3\\$s";
      case "name" -> "%4\\$s";
      case "obj" -> "%5\\$s";
      default -> "%" + (Integer.valueOf(r.group(1)) + 6) + "\\$s";
    });
  }
}
