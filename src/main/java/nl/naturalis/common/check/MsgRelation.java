package nl.naturalis.common.check;

import nl.naturalis.common.Pair;

import java.util.Collection;

import static java.lang.String.format;
import static java.lang.System.identityHashCode;
import static nl.naturalis.common.check.Messages.*;

class MsgRelation {

  static Formatter msgEqualsIgnoreCase() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be equal ignoring case to %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be equal ignoring case to %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgSameAs() {
    return args -> {
      String scnObj = simpleClassName(args.object());
      int hcObj = identityHashCode(args.object());
      String idObj = args.object() == null ? "null" : scnObj + '@' + hcObj;
      if (args.negated()) {
        String fmt = "%s must not be %s";
        return format(fmt, args.argName(), idObj);
      }
      String scnArg = simpleClassName(args.arg());
      int hcArg = identityHashCode(args.arg());
      String idArg = args.arg() == null ? "null" : scnArg + '@' + hcArg;
      String fmt = "%s must be %s (was %s)";
      return format(fmt, args.argName(), idObj, idArg);
    };
  }

  static Formatter msgSizeEquals() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s.size() must not be equal to %s";
        return format(fmt, args.argName(), args.object());
      }
      String fmt = "%s.size() must be equal to %s (was %s)";
      return format(fmt, args.argName(), args.object(), ((Collection) args.arg()).size());
    };
  }

  static Formatter msgSizeGT() {
    return args -> {
      if (args.negated()) {
        return msgSizeLTE().apply(args.flip());
      }
      String fmt = "%s.size() must be > %s (was %s)";
      return format(fmt, args.argName(), args.object(), ((Collection) args.arg()).size());
    };
  }

  static Formatter msgSizeGTE() {
    return args -> {
      if (args.negated()) {
        return msgSizeLT().apply(args.flip());
      }
      String fmt = "%s.size() must be >= %s (was %s)";
      return format(fmt, args.argName(), args.object(), ((Collection) args.arg()).size());
    };
  }

  static Formatter msgSizeLT() {
    return args -> {
      if (args.negated()) {
        return msgSizeGTE().apply(args.flip());
      }
      String fmt = "%s.size() must be < %s (was %s)";
      return format(fmt, args.argName(), args.object(), ((Collection) args.arg()).size());
    };
  }

  static Formatter msgSizeLTE() {
    return args -> {
      if (args.negated()) {
        return msgSizeGT().apply(args.flip());
      }
      String fmt = "%s.size() must be <= %s (was %s)";
      return format(fmt, args.argName(), args.object(), ((Collection) args.arg()).size());
    };
  }

  static Formatter msgInRangeFrom() {
    return args -> {
      Pair pair = (Pair) args.object();
      String fmt;
      if (args.negated()) {
        fmt = "%s must be < %s or >= %s (was %s)";
      } else {
        fmt = "%s must be >= %s and < %s (was %s)";
      }
      return format(fmt, args.argName(), pair.one(), pair.two(), args.arg());
    };
  }

  static Formatter msgInRangeClosed() {
    return args -> {
      Pair pair = (Pair) args.object();
      String fmt;
      if (args.negated()) {
        fmt = "%s must be < %s or > %s (was %s)";
      } else {
        fmt = "%s must be >= %s and <= %s (was %s)";
      }
      return format(fmt, args.argName(), pair.one(), pair.two(), args.arg());
    };
  }

  static Formatter msgNullOr() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be null or %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be null or %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgInstanceOf() {
    return args -> {
      String cnObj = className(args.object());
      if (args.negated()) {
        String fmt = "%s must not be instance of %s (was %s)";
        String arg = toStr(args.arg());
        return format(fmt, args.argName(), cnObj, arg);
      }
      String fmt = "%s must be instance of %s (was %s)";
      String cnArg = className(args.arg());
      return format(fmt, args.argName(), cnObj, cnArg);
    };
  }

  static Formatter msgExtending() {
    return args -> {
      Class cArg = (Class) args.arg();
      Class cObj = (Class) args.object();
      String verb = cArg.isInterface() || !cObj.isInterface() ? "extend" : "implement";
      String cnObj = className(cObj);
      String cnArg = className(cArg);
      if (args.negated()) {
        if (cArg == cObj) {
          String fmt = "%s must not %s %s";
          return format(fmt, args.argName(), verb, cnObj);
        }
        String fmt = "%s must not %s %s (was %s)";
        return format(fmt, args.argName(), verb, cnObj, cnArg);
      }
      String fmt = "%s must %s %s (was %s)";
      return format(fmt, args.argName(), verb, cnObj, cnArg);
    };
  }

  static Formatter msgContains() {
    return args -> {
      if (args.negated()) {
        return format("%s must not contain %s", args.argName(), toStr(args.object()));
      }
      String fmt = "%s must contain %s";
      return format(fmt, args.argName(), toStr(args.object()));
    };
  }

  static Formatter msgHasKey() {
    return args -> {
      if (args.negated()) {
        return format("%s must not contain key %s", args.argName(), toStr(args.object()));
      }
      return format("%s must contain key %s", args.argName(), toStr(args.object()));
    };
  }

  static Formatter msgHasValue() {
    return args -> {
      if (args.negated()) {
        return format("%s must not contain value %s", args.argName(), toStr(args.object()));
      }
      return format("%s must contain value %s", args.argName(), toStr(args.object()));
    };
  }

  static Formatter msgIn() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be element of %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be element of %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgKeyIn() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be key in %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be key in %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgValueIn() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be value in %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be value in %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgSupersetOf() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be superset %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be superset %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgSubsetOf() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be subset of %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be subset of %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgSubstringOf() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be substring of \"%s\" (was \"%s\")";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be substring of \"%s\" (was \"%s\")";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgStartsWith() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not start with \"%s\" (was \"%s\")";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must start with \"%s\" (was \"%s\")";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgEndsWith() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not end with \"%s\" (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must end with \"%s\" (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }
}
