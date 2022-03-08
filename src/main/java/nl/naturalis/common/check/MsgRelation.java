package nl.naturalis.common.check;

import nl.naturalis.common.Pair;

import java.util.Collection;

import static java.lang.String.format;
import static nl.naturalis.common.ClassMethods.className;
import static java.lang.System.*;
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
      if (args.negated()) {
        String fmt = "%s must not be %s";
        String id0 =
            args.object() == null
                ? "null"
                : simpleClassName(args.object()) + '@' + identityHashCode(args.object());
        return format(fmt, args.argName(), id0);
      }
      String fmt = "%s must be %s (was %s)";
      String id0 =
          args.object() == null
              ? "null"
              : simpleClassName(args.object()) + '@' + identityHashCode(args.object());
      String id1 =
          args.arg() == null
              ? "null"
              : simpleClassName(args.arg()) + '@' + identityHashCode(args.arg());
      return format(fmt, args.argName(), id0, id1);
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

  static Formatter msgContains() {
    return args -> {
      if (args.negated()) {
        return format("%s must not contain %s", args.argName(), toStr(args.object()));
      }
      String fmt = "%s must contain %s";
      return format(fmt, args.argName(), toStr(args.object()));
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

  static Formatter msgContainsAll() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be superset of %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be superset of %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
    };
  }

  static Formatter msgAllIn() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be subset of %s (was %s)";
        return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
      }
      String fmt = "%s must be subset of %s (was %s)";
      return format(fmt, args.argName(), toStr(args.object()), toStr(args.arg()));
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

  static Formatter msgKeyIn() {
    return args -> {
      if (args.negated()) {
        String fmt = "%s must not be key in %s (parameter \"%s\")";
        return format(fmt, toStr(args.arg()), toStr(args.object()), args.argName());
      }
      String fmt = "%s must be key in %s (parameter \"%s\")";
      return format(fmt, toStr(args.arg()), toStr(args.object()), args.argName());
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
