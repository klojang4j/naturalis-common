package nl.naturalis.common.check;

import nl.naturalis.common.StringMethods;

import java.io.File;

import static java.lang.String.format;
import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.check.Messages.toStr;

final class MsgPredicate {

  private static final String DISCARD = StringMethods.EMPTY;
  private static final String MSG_PLAIN = "%s must%s be %s";
  private static final String MSG_STD = "%s must%s be %s (was %s)";

  private static final String MSG_NULL = "%s must be null (was %s)";
  private static final String MSG_NOT_NULL = "%s must not be null";
  private static final String MSG_TRUE = "%s must be true (was false)";
  private static final String MSG_FALSE = "%s must be false (was true)";

  private MsgPredicate() {}

  static Formatter msgNull() {
    return args ->
        args.negated()
            ? format(MSG_NOT_NULL, args.name())
            : format(MSG_NULL, args.name(), toStr(args.arg()));
  }

  static Formatter msgNotNull() {
    return args ->
        args.negated()
            ? format(MSG_NULL, args.name(), toStr(args.arg()))
            : format(MSG_NOT_NULL, args.name());
  }

  static Formatter msgYes() {
    return args -> args.negated() ? format(MSG_FALSE, args.name()) : format(MSG_TRUE, args.name());
  }

  static Formatter msgNo() {
    return args -> args.negated() ? format(MSG_TRUE, args.name()) : format(MSG_FALSE, args.name());
  }

  static Formatter msgEmpty() {
    return args -> format(MSG_STD, args.name(), args.not(), "null or empty", toStr(args.arg()));
  }

  static Formatter msgDeepNotNull() {
    return args ->
        format(
            MSG_STD, args.name(), args.notNot(), "null or contain null values", toStr(args.arg()));
  }

  static Formatter msgDeepNotEmpty() {
    return args ->
        format(
            MSG_STD,
            args.name(),
            args.notNot(),
            "empty or contain empty values",
            toStr(args.arg()));
  }

  static Formatter msgBlank() {
    return args -> format(MSG_STD, args.name(), args.not(), "null or blank", toStr(args.arg()));
  }

  static Formatter msgInteger() {
    return args -> {
      var fmt = "%s must%s be parsable as integer (was %s)";
      return format(fmt, args.name(), args.not(), args.arg());
    };
  }

  static Formatter msgArray() {
    return args -> {
      String fmt = "%s must%s be an array (was %s)";
      return format(fmt, args.name(), args.not(), className(args.arg()));
    };
  }

  static Formatter msgFile() {
    return args -> {
      File f = (File) args.arg();
      if (f.isDirectory()) {
        return format("%s must not be a directory (was %s)", args.name(), f);
      }
      var fmt = "%s must%s exist (was %s)";
      return format(fmt, args.typeAndName(), args.not(), args.arg());
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
    return args -> {
      File f = (File) args.arg();
      if (args.negated()) {
        if (f.isDirectory()) {
          return format("Directory already exists: %s", f.getAbsolutePath());
        }
        return format("File already exists: %s", f.getAbsolutePath());
      }
      return format("Missing file/directory: %s", f.getAbsolutePath());
    };
  }

  static Formatter msgReadable() {
    return args -> {
      File f = (File) args.arg();
      if (!f.exists()) {
        return format("No such file/directory: %s", f.getAbsolutePath());
      }
      if (args.negated()) {
        if (f.isDirectory()) {
          return format("Directory must not be readable: %s", f.getAbsolutePath());
        }
        return format("File must not be readable: %s", f.getAbsolutePath());
      }
      if (f.isDirectory()) {
        return format("Directory must be readable: %s", f.getAbsolutePath());
      }
      return format("File must be readable: %s", f.getAbsolutePath());
    };
  }

  static Formatter msgWritable() {
    return args -> {
      File f = (File) args.arg();
      if (!f.exists()) {
        return format("No such file/directory: %s", f.getAbsolutePath());
      }
      if (args.negated()) {
        if (f.isDirectory()) {
          return format("Directory must not be writable: %s", f.getAbsolutePath());
        }
        return format("File must not be writable: %s", f.getAbsolutePath());
      }
      if (f.isDirectory()) {
        return format("Directory must be writable: %s", f.getAbsolutePath());
      }
      return format("File must be writable: %s", f.getAbsolutePath());
    };
  }
}
