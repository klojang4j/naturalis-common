package nl.naturalis.common.check;

import java.io.File;

import static java.lang.String.format;
import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.check.Messages.toStr;

final class MsgPredicate {

  private MsgPredicate() {}

  static Formatter msgNull() {
    return args -> {
      if (args.negated()) {
        return msgNotNull().apply(args.flip());
      }
      return format("%s must be null (was %s)", args.argName(), toStr(args.arg()));
    };
  }

  static Formatter msgNotNull() {
    return args -> {
      if (args.negated()) {
        return msgNull().apply(args.flip());
      }
      return format("%s must not be null", args.argName());
    };
  }

  static Formatter msgYes() {
    return args -> {
      if (args.negated()) {
        return msgNo().apply(args.flip());
      }
      return format("%s must be true (was false)", args.argName());
    };
  }

  static Formatter msgNo() {
    return args -> {
      if (args.negated()) {
        return msgYes().apply(args.flip());
      }
      return format("%s must be false (was true)", args.argName());
    };
  }

  static Formatter msgEmpty() {
    return args -> {
      if (args.negated()) {
        return format("%s must not be null or empty (was %s)", args.argName(), toStr(args.arg()));
      }
      return format("%s must be empty (was %s)", args.argName(), toStr(args.arg()));
    };
  }

  static Formatter msgDeepNotNull() {
    return args -> {
      if (args.negated()) { // Negation is total nonsense, but OK
        return format(
            "%s must be null or contain one or more null values (was %s)",
            args.argName(), toStr(args.arg()));
      }
      return format(
          "%s must not be null or contain null values (was %s)", args.argName(), toStr(args.arg()));
    };
  }

  static Formatter msgDeepNotEmpty() {
    return args -> {
      var fmt = "%s must%s be empty or contain empty values (was %s)";
      return format(fmt, args.argName(), args.notNot(), toStr(args.arg()));
    };
  }

  static Formatter msgBlank() {
    return args -> {
      var fmt = "%s must%s be null or blank (was %s)";
      return format(fmt, args.argName(), args.not(), toStr(args.arg()));
    };
  }

  static Formatter msgInteger() {
    return args -> {
      var fmt = "%s must%s be parsable as integer (was %s)";
      return format(fmt, args.argName(), args.not(), args.arg());
    };
  }

  static Formatter msgArray() {
    return args -> {
      String fmt = "%s must%s be an array (was %s)";
      return format(fmt, args.argName(), args.not(), className(args.arg()));
    };
  }

  static Formatter msgFile() {
    return args -> {
      File f = (File) args.arg();
      if (f.isDirectory()) {
        return format("%s must not be a directory (was %s)", args.argName(), f);
      }
      var fmt = "%s must%s exist (was %s)";
      return format(fmt, args.typeAndName(), args.not(), args.arg());
    };
  }

  static Formatter msgDirectory() {
    return args -> {
      File f = (File) args.arg();
      if (f.isFile()) {
        return format("%s must not be a file (was %s)", args.argName(), f);
      }
      var fmt = "Directory %s must%s exist (was %s)";
      return format(fmt, args.argName(), args.not(), args.arg());
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
