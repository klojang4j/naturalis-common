package nl.naturalis.common.check;

import static java.lang.String.format;
import static java.lang.System.identityHashCode;
import static nl.naturalis.common.ObjectMethods.ifNotNull;
import static nl.naturalis.common.check.MsgUtil.*;

@SuppressWarnings("rawtypes")
final class MsgRelation {

  private MsgRelation() {}

  static PrefabMsgFormatter msgSameAs() {
    return args -> {
      String idObj = args.obj() == null
          ? "null"
          : simpleClassName(args.obj()) + '@' + identityHashCode(args.obj());
      if (args.negated()) {
        String fmt = "%s must not be reference to %s";
        return format(fmt, args.name(), idObj);
      }
      String idArg = args.arg() == null
          ? "null"
          : simpleClassName(args.arg()) + '@' + identityHashCode(args.arg());
      String fmt = "%s must be reference to %s (was %s)";
      return format(fmt, args.name(), idObj, idArg);
    };
  }

  static PrefabMsgFormatter msgNullOr() {
    return formatRelation("be null or", true);
  }

  static PrefabMsgFormatter msgInstanceOf() {
    return args -> {
      String cnObj = className(args.obj());
      if (args.negated() && args.arg().getClass() == args.obj()) {
        String fmt = "%s must not be instance of %s (was %s)";
        return format(fmt, args.name(), cnObj, toStr(args.arg()));
      }
      String fmt = "%s must %sbe instance of %s (was %s)";
      String cnArg = className(args.arg());
      return format(fmt, args.name(), args.not(), cnObj, cnArg);
    };
  }

  static PrefabMsgFormatter msgSubtypeOf() {
    return formatRelation("extend/implement", true);
  }

  static PrefabMsgFormatter msgSupertypeOf() {
    return formatRelation("be supertype of", true);
  }

  static PrefabMsgFormatter msgContains() {
    return formatRelation("contain", false);
  }

  static PrefabMsgFormatter msgHasKey() {
    return formatRelation("contain key", false);
  }

  static PrefabMsgFormatter msgHasValue() {
    return formatRelation("contain value", false);
  }

  static PrefabMsgFormatter msgIn() {
    return formatRelation("be element of", true);
  }

  static PrefabMsgFormatter msgKeyIn() {
    return formatRelation("be key in", true);
  }

  static PrefabMsgFormatter msgValueIn() {
    return formatRelation("be value in", true);
  }

  static PrefabMsgFormatter msgSupersetOf() {
    return formatRelation("be superset of", true);
  }

  static PrefabMsgFormatter msgSubsetOf() {
    return formatRelation("be subset of", true);
  }

  static PrefabMsgFormatter msgHasSubstring() {
    return formatRelation("contain", true);
  }

  static PrefabMsgFormatter msgSubstringOf() {
    return formatRelation("be substring of", true);
  }

  static PrefabMsgFormatter msgEqualsIgnoreCase() {
    return formatRelation("be equal ignoring case to", true);
  }

  static PrefabMsgFormatter msgStartsWith() {
    return formatRelation("start with", true);
  }

  static PrefabMsgFormatter msgEndsWith() {
    return formatRelation("end with", true);
  }
}
