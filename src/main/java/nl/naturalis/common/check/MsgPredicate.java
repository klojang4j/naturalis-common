package nl.naturalis.common.check;

import java.io.File;

import static java.lang.String.format;
import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.check.MsgUtil.*;

final class MsgPredicate {

  private static final String MSG_NULL = "%s must be null (was %s)";
  private static final String MSG_NOT_NULL = " must not be null";

  private MsgPredicate() {}

  static Formatter msgNull() {
    return args ->
        args.negated()
            ? args.name() + MSG_NOT_NULL
            : format(MSG_NULL, args.name(), toStr(args.arg()));
  }

  static Formatter msgNotNull() {
    return args ->
        args.negated()
            ? format(MSG_NULL, args.name(), toStr(args.arg()))
            : args.name() + MSG_NOT_NULL;
  }

  static Formatter msgYes() {
    return args -> format(MSG_PREDICATE, args.name(), args.not(), "be true");
  }

  static Formatter msgNo() {
    return args -> format(MSG_PREDICATE, args.name(), args.not(), "be false");
  }

  static Formatter msgEmpty() {
    return args ->
        format(MSG_PREDICATE_WAS, args.name(), args.not(), "be null or empty", toStr(args.arg()));
  }

  static Formatter msgDeepNotNull() {
    return args ->
        format(
            MSG_PREDICATE_WAS,
            args.name(),
            args.notNot(),
            "be null or contain null values",
            toStr(args.arg()));
  }

  static Formatter msgDeepNotEmpty() {
    return args ->
        format(
            MSG_PREDICATE_WAS,
            args.name(),
            args.notNot(),
            "be empty or contain empty values",
            toStr(args.arg()));
  }

  static Formatter msgBlank() {
    return args ->
        format(MSG_PREDICATE_WAS, args.name(), args.not(), "be null or blank", toStr(args.arg()));
  }

  static Formatter msgInteger() {
    return args ->
        format(
            MSG_PREDICATE_WAS,
            args.name(),
            args.not(),
            "be parsable as integer",
            toStr(args.arg()));
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
