package nl.naturalis.common.check;

import java.io.File;

import static java.lang.String.format;
import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.check.MsgUtil.*;

final class MsgPredicate {

  private MsgPredicate() {}

  static Formatter msgNull() {
    return formatPredicate("be null", true, false);
  }

  static Formatter msgNotNull() {
    return formatDeniedPredicate("be null", false, true);
  }

  static Formatter msgYes() {
    return formatPredicate("be true", false);
  }

  static Formatter msgNo() {
    return formatPredicate("be false", false);
  }

  static Formatter msgEmpty() {
    return formatPredicate("be null or empty", true);
  }

  static Formatter msgDeepNotNull() {
    return formatDeniedPredicate("be null or contain null values", true);
  }

  static Formatter msgDeepNotEmpty() {
    return formatDeniedPredicate("be empty or contain empty values", true);
  }

  static Formatter msgBlank() {
    return formatPredicate("be null or blank", true);
  }

  static Formatter msgInteger() {
    return formatPredicate("be parsable as integer", true);
  }

  static Formatter msgArray() {
    return args ->
        format(MSG_PREDICATE_WAS, args.name(), args.not(), "be an array", className(args.type()));
  }

  static Formatter msgFile() {
    return args -> {
      File f = (File) args.arg();
      if (f.isDirectory()) {
        return format("%s must not be a directory (was %s)", args.name(), f);
      }
      return format(MSG_PREDICATE_WAS, args.typeAndName(), args.not(), "exist", args.arg());
    };
  }

  static Formatter msgDirectory() {
    return args -> {
      File f = (File) args.arg();
      if (f.isFile()) {
        return format("%s must not be a directory (was %s)", args.name(), f);
      }
      var fmt = "Directory %s must%s exist (was %s)";
      return format(fmt, args.name(), args.not(), args.arg());
    };
  }

  static Formatter msgFileExists() {
    return args -> format(MSG_PREDICATE_WAS, args.typeAndName(), args.not(), "exist", args.arg());
  }

  static Formatter msgReadable() {
    return args -> {
      File f = (File) args.arg();
      if (!f.exists()) {
        return format("No such file/directory: %s", f);
      }
      String s = f.isDirectory() ? "Directory" : File.class.getSimpleName();
      return format("%s %s must%s be readable (was %s)", s, args.name(), args.not(), f);
    };
  }

  static Formatter msgWritable() {
    return args -> {
      File f = (File) args.arg();
      if (!f.exists()) {
        return format("No such file/directory: %s", f);
      }
      String s = f.isDirectory() ? "Directory" : File.class.getSimpleName();
      return format("%s %s must%s be writable (was %s)", s, args.name(), args.not(), f);
    };
  }
}
