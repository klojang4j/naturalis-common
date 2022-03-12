package nl.naturalis.common.check;

import static java.lang.String.format;
import static java.lang.System.identityHashCode;
import static nl.naturalis.common.check.MsgUtil.*;

final class MsgRelation {

  private MsgRelation() {}

  static Formatter msgSameAs() {
    return args -> {
      String idObj =
          args.obj() == null
              ? "null"
              : simpleClassName(args.obj()) + '@' + identityHashCode(args.obj());
      if (args.negated()) {
        String fmt = "%s must not be reference to %s";
        return format(fmt, args.name(), idObj);
      }
      String idArg =
          args.arg() == null
              ? "null"
              : simpleClassName(args.arg()) + '@' + identityHashCode(args.arg());
      String fmt = "%s must be reference to %s (was %s)";
      return format(fmt, args.name(), idObj, idArg);
    };
  }

  static Formatter msgNullOr() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be null or %s (was %s)";
        return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
      }
      String fmt = "%s must be null or %s (was %s)";
      return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
    };
  }

  static Formatter msgInstanceOf() {
    return args -> {
      String cnObj = className(args.obj());
      if (args.negated()) {
        String fmt = "%s must not be instance of %s (was %s)";
        String arg = toStr(args.arg());
        return format(fmt, args.name(), cnObj, arg);
      }
      String fmt = "%s must be instance of %s (was %s)";
      String cnArg = className(args.arg());
      return format(fmt, args.name(), cnObj, cnArg);
    };
  }

  static Formatter msgSubtypeOf() {
    return args -> {
      Class cArg = (Class) args.arg();
      Class cObj = (Class) args.obj();
      String verb = cArg.isInterface() || !cObj.isInterface() ? "extend" : "implement";
      String cnObj = className(cObj);
      String cnArg = className(cArg);
      if (args.negated()) {
        String fmt = "%s must not %s %s (was %s)";
        return format(fmt, args.name(), verb, cnObj, cnArg);
      }
      String fmt = "%s must %s %s (was %s)";
      return format(fmt, args.name(), verb, cnObj, cnArg);
    };
  }

  static Formatter msgSupertypeOf() {
    return args -> {
      Class cArg = (Class) args.arg();
      Class cObj = (Class) args.obj();
      String cnObj = className(cObj);
      String cnArg = className(cArg);
      if (args.negated()) {
        String fmt = "%s must not be supertype of %s (was %s)";
        return format(fmt, args.name(), cnObj, cnArg);
      }
      String fmt = "%s must be supertype of %s (was %s)";
      return format(fmt, args.name(), cnObj, cnArg);
    };
  }

  static Formatter msgContains() {
    return args -> {
      if (args.negated()) {
        return format("%s must not contain %s", args.name(), toStr(args.obj()));
      }
      String fmt = "%s must contain %s";
      return format(fmt, args.name(), toStr(args.obj()));
    };
  }

  static Formatter msgHasKey() {
    return args -> {
      if (args.negated()) {
        return format("%s must not contain key %s", args.name(), toStr(args.obj()));
      }
      return format("%s must contain key %s", args.name(), toStr(args.obj()));
    };
  }

  static Formatter msgHasValue() {
    return args -> {
      if (args.negated()) {
        return format("%s must not contain value %s", args.name(), toStr(args.obj()));
      }
      return format("%s must contain value %s", args.name(), toStr(args.obj()));
    };
  }

  static Formatter msgIn() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be element of %s (was %s)";
        return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
      }
      String fmt = "%s must be element of %s (was %s)";
      return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
    };
  }

  static Formatter msgKeyIn() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be key in %s (was %s)";
        return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
      }
      String fmt = "%s must be key in %s (was %s)";
      return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
    };
  }

  static Formatter msgValueIn() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be value in %s (was %s)";
        return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
      }
      String fmt = "%s must be value in %s (was %s)";
      return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
    };
  }

  static Formatter msgSupersetOf() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be superset %s (was %s)";
        return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
      }
      String fmt = "%s must be superset %s (was %s)";
      return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
    };
  }

  static Formatter msgSubsetOf() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be subset of %s (was %s)";
        return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
      }
      String fmt = "%s must be subset of %s (was %s)";
      return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
    };
  }

  static Formatter msgHasSubstring() {
    return args -> {
      if (args.negated()) {
        return format(
            "%s must not contain %s (was %s)", args.name(), toStr(args.obj()), toStr(args.arg()));
      }
      String fmt = "%s must contain %s (was %s)";
      return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
    };
  }

  static Formatter msgSubstringOf() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be substring of %s (was %s)";
        return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
      }
      String fmt = "%s must be substring of %s (was %s)";
      return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
    };
  }

  static Formatter msgEqualsIgnoreCase() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be equal ignoring case to %s";
        return format(fmt, args.name(), toStr(args.obj()));
      }
      String fmt = "%s must be equal ignoring case to %s (was %s)";
      return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
    };
  }

  static Formatter msgStartsWith() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not start with %s (was %s)";
        return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
      }
      String fmt = "%s must start with %s (was %s)";
      return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
    };
  }

  static Formatter msgEndsWith() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not end with %s (was %s)";
        return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
      }
      String fmt = "%s must end with %s (was %s)";
      return format(fmt, args.name(), toStr(args.obj()), toStr(args.arg()));
    };
  }
}
