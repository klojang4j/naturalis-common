package nl.naturalis.common.check;

import java.io.File;

import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.check.MsgUtil.*;

final class MsgPredicate {

  private MsgPredicate() {}

  static PrefabMsgFormatter msgNull() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "null"
        : x.name() + MUST_BE + "null" + was2(x);
  }

  static PrefabMsgFormatter msgNotNull() {
    return x -> x.negated()
        ? x.name() + MUST_BE + "null" + was2(x)
        : x.name() + MUST_NOT_BE + "null";
  }

  static PrefabMsgFormatter msgYes() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "true"
        : x.name() + MUST_BE + "true";
  }

  static PrefabMsgFormatter msgNo() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "false"
        : x.name() + MUST_BE + "false";
  }

  static PrefabMsgFormatter msgEmpty() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "null or empty" + was2(x)
        : x.name() + MUST_BE + "null or empty" + was2(x);
  }

  static PrefabMsgFormatter msgDeepNotNull() {
    return x -> x.negated()
        ? x.name() + MUST_BE + "null or contain null values" + was2(x)
        : x.name() + MUST_NOT_BE + "null or contain null values" + was2(x);
  }

  static PrefabMsgFormatter msgDeepNotEmpty() {
    return x -> x.negated()
        ? x.name() + MUST_BE + "empty or contain empty values" + was2(x)
        : x.name() + MUST_NOT_BE + "empty or contain empty values" + was2(x);
  }

  static PrefabMsgFormatter msgBlank() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "null or blank" + was2(x)
        : x.name() + MUST_BE + "null or blank" + was2(x);
  }

  static PrefabMsgFormatter msgInteger() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "parsable as integer" + was1(x)
        : x.name() + MUST_BE + "parsable as integer" + was1(x);
  }

  static PrefabMsgFormatter msgArray() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "an array" + was(className(x.arg()))
        : x.name() + MUST_BE + "an array" + was(className(x.arg()));
  }

  static PrefabMsgFormatter msgFile() {
    return x -> {
      File f = (File) x.arg();
      if (f.isDirectory()) {
        return x.name() + MUST_NOT_BE + "a directory" + was(f);
      }
      return x.negated()
          ? x.typeAndName() + MUST_NOT + "exist" + was2(x)
          : x.typeAndName() + MUST + "exist" + was2(x);
    };
  }

  static PrefabMsgFormatter msgDirectory() {
    return x -> {
      File f = (File) x.arg();
      if (f.isFile()) {
        return x.name() + MUST_NOT_BE + "a directory" + was(f);
      }
      return x.negated()
          ? "directory " + x.name() + MUST_NOT + "exist" + was2(x)
          : "directory " + x.name() + MUST + "exist" + was2(x);
    };
  }

  static PrefabMsgFormatter msgFileExists() {
    return x -> x.negated()
        ? x.name() + MUST_NOT + "exist" + was2(x)
        : x.name() + MUST + "exist" + was2(x);
  }

  static PrefabMsgFormatter msgReadable() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "readable" + was2(x)
        : x.name() + MUST_BE + "readable" + was2(x);
  }

  static PrefabMsgFormatter msgWritable() {
    return x -> x.negated()
        ? x.name() + MUST_NOT_BE + "writable" + was2(x)
        : x.name() + MUST_BE + "writable" + was2(x);
  }

}
