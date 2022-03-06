package nl.naturalis.common.check;

import java.io.File;

import static java.lang.String.format;
import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.check.Messages.*;

class MsgPredicate {

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
      if (args.negated()) { // idem
        String fmt = "%s must be empty or contain or one or more empty values (was %s)";
        return format(fmt, args.argName(), toStr(args.arg()));
      }
      return format(
          "%s must not be empty or contain empty values (was %s)",
          args.typeAndName(), toStr(args.arg()));
    };
  }

  static Formatter msgBlank() {
    return args -> {
      if (args.negated()) {
        return format("%s must not be null or blank (was %s)", args.argName(), toStr(args.arg()));
      }
      return format("%s must be null or blank (was %s)", args.argName(), toStr(args.arg()));
    };
  }

  static Formatter msgInteger() {
    return args -> {
      if (args.negated()) {
        return format("%s must not be an integer (was %s)", args.argName(), args.arg());
      }
      String fmt = "%s must be an integer (was %s)";
      return format(fmt, args.argName(), args.arg(), className(args.arg()));
    };
  }

  static Formatter msgFile() {
    return args -> {
      File f = (File) args.arg();
      if (args.negated()) {
        return format("File %s already exists", args.arg());
      } else if (f.isDirectory()) {
        // File exists, but is a directory
        return format("%s must not be a directory (was %s)", args.argName(), f);
      }
      // File not present at all
      return format("No such file: %s", args.arg());
    };
  }

  static Formatter msgDirectory() {
    return args -> {
      File f = (File) args.arg();
      if (args.negated()) {
        return format("Directory %s already exists", args.arg());
      } else if (f.isFile()) {
        return format("%s must not be a file (was %s)", args.argName(), f);
      }
      return format("No such directory: %s", args.arg());
    };
  }

  static Formatter msgOnFileSystem() {
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
