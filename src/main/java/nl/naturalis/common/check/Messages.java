package nl.naturalis.common.check;

import java.io.File;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;
import nl.naturalis.common.Tuple;
import static java.lang.String.format;
import static nl.naturalis.common.ArrayMethods.pack;
import static nl.naturalis.common.ClassMethods.getArrayTypeName;
import static nl.naturalis.common.ClassMethods.getArrayTypeSimpleName;
import static nl.naturalis.common.StringMethods.substr;
import static nl.naturalis.common.Tuple.tuple;
import static nl.naturalis.common.check.Checks.*;

class Messages {

  private static final String ERR_INVALID_VALUE = "Invalid value for %s: %s";

  /* Returns messages associated with predefined Predicate instances. */
  static String get(Object test, Object arg, String argName) {
    Function<Object[], String> fnc = msgs.get(test);
    if (fnc != null) {
      return fnc.apply(pack(arg, argName));
    }
    return String.format(ERR_INVALID_VALUE, argName, str(arg));
  }

  /* Returns messages associated with predefined Relation instances. */
  static String get(Object test, Object arg, String argName, Object target) {
    Function<Object[], String> fnc = msgs.get(test);
    if (fnc != null) {
      return fnc.apply(pack(arg, argName, target));
    }
    return String.format(ERR_INVALID_VALUE, argName, str(arg));
  }

  /*
   * Maps predefined Predicate And Relation instances to message suppliers, which take an array of
   * message arguments and return a complete error message. The 1st message argument must be the
   * argument itself, the 2nd must be the name of the argument and, for Relation instances only, the
   * 3rd message argument must be the object at the other end of the relationship.
   */
  private static final IdentityHashMap<Object, Function<Object[], String>> msgs = createLookups();

  private static IdentityHashMap<Object, Function<Object[], String>> createLookups() {

    List<Tuple<Object, Function<Object[], String>>> tmp = new ArrayList<>();

    /* NULL & EMPTY */
    tmp.add(tuple(isNull(), x -> format("%s must be null (was %s)", arg(x), str(x[0]))));
    tmp.add(tuple(notNull(), x -> format("%s must not be null", arg(x))));
    tmp.add(tuple(isEmpty(), x -> format("%s must be empty (was %s)", arg(x), str(x[0]))));
    tmp.add(tuple(notEmpty(), x -> format("%s must not be empty", arg(x))));
    tmp.add(tuple(noneNull(), x -> format("%s must not contain null values", arg(x))));

    /* STRING PREDICATES */
    tmp.add(tuple(notBlank(), x -> format("%s must not be blank", arg(x))));

    /* FILE PREDICATES */
    tmp.add(tuple(isFile(), x -> msgIsFile(x)));
    tmp.add(tuple(isDirectory(), x -> msgIsDirectory(x)));
    tmp.add(tuple(fileNotExists(), x -> msgFileNotExists(x)));
    tmp.add(tuple(readable(), x -> msgReadable(x)));
    tmp.add(tuple(writable(), x -> msgWritable(x)));

    /* INT PREDICATES */
    tmp.add(tuple(isEven(), x -> format("%s must be even (was %d)", arg(x), x[0])));
    tmp.add(tuple(isOdd(), x -> format("%s must be odd (was %d)", arg(x), x[0])));
    tmp.add(tuple(positive(), x -> format("%s must be positive (was %d)", arg(x), x[0])));
    tmp.add(
        tuple(notPositive(), x -> format("%s must be zero or negative (was %d)", arg(x), x[0])));
    tmp.add(tuple(negative(), x -> format("%s must be negative (was %d)", arg(x), x[0])));
    tmp.add(
        tuple(notNegative(), x -> format("%s must be zero or positive (was %d)", arg(x), x[0])));

    /* COLLECTION RELATIONS */
    tmp.add(tuple(contains(), x -> msgContains(x)));
    tmp.add(tuple(notContains(), x -> msgNotContains(x)));
    tmp.add(tuple(elementOf(), x -> msgElementOf(x)));
    tmp.add(tuple(notElementOf(), x -> msgNotElementOf(x)));
    tmp.add(tuple(containsKey(), x -> msgContainsKey(x)));
    tmp.add(tuple(notContainsKey(), x -> msgNotContainsKey(x)));
    tmp.add(tuple(containsValue(), x -> msgContainsValue(x)));
    tmp.add(tuple(notContainsValue(), x -> msgNotContainsValue(x)));

    tmp.add(tuple(objEquals(), x -> format("%s must be equal to %s (was %s)", arg(x), x[2], x[0])));
    tmp.add(tuple(objNotEquals(), x -> format("%s must be not be equal to %s", arg(x), x[2])));

    tmp.add(tuple(numGreaterThan(), x -> format("%s must be > %s (was %s)", arg(x), x[2], x[0])));
    tmp.add(tuple(numAtLeast(), x -> format("%s must be >= %s (was %s)", arg(x), x[2], x[0])));
    tmp.add(tuple(numLessThan(), x -> format("%s must be < %s (was %s)", arg(x), x[2], x[0])));
    tmp.add(tuple(numAtMost(), x -> format("%s must be <= %s (was %s)", arg(x), x[2], x[0])));

    tmp.add(tuple(sizeEquals(), x -> format("%s must be equal to %s (was %s)", sz(x), x[2], x[0])));
    tmp.add(tuple(sizeNotEquals(), x -> format("%s must be not be equal to %s", sz(x), x[2])));
    tmp.add(tuple(sizeGreaterThan(), x -> format("%s must be > %s (was %s)", sz(x), x[2], x[0])));
    tmp.add(tuple(sizeAtLeast(), x -> format("%s must be >= %s (was %s)", sz(x), x[2], x[0])));
    tmp.add(tuple(sizeLessThan(), x -> format("%s must be < %s (was %s)", sz(x), x[2], x[0])));
    tmp.add(tuple(sizeAtMost(), x -> format("%s must be <= %s (was %s)", sz(x), x[2], x[0])));

    tmp.add(tuple(instanceOf(), x -> msgInstanceOf(x)));
    tmp.add(tuple(isArray(), x -> format("%s must be an array (was %s)", arg(x), cname(x[0]))));

    /* INT RELATIONS */
    tmp.add(
        tuple(equalTo(), x -> format("%s must not be equal to %d (was %d)", arg(x), x[2], x[0])));
    tmp.add(tuple(notEqualTo(), x -> format("%s must not be equal to %d", arg(x), x[2])));
    tmp.add(tuple(greaterThan(), x -> format("%s must be > %d (was %d)", arg(x), x[2], x[0])));
    tmp.add(tuple(atLeast(), x -> format("%s must be >= %s (was %s)", arg(x), x[2], x[0])));
    tmp.add(tuple(lessThan(), x -> format("%s must be < %s (was %s)", arg(x), x[2], x[0])));
    tmp.add(tuple(atMost(), x -> format("%s must be <= %s (was %s)", arg(x), x[2], x[0])));
    tmp.add(tuple(multipleOf(), x -> msgMultipleOf(x)));

    IdentityHashMap<Object, Function<Object[], String>> map = new IdentityHashMap<>(tmp.size());

    tmp.forEach(t -> t.addTo(map));

    return map;
  }

  private static String msgIsFile(Object[] x) {
    return format("No such file (%s): %s", x[1], ((File) x[0]).getAbsolutePath());
  }

  private static String msgIsDirectory(Object[] x) {
    return format("No such directory (%s): %s", x[1], ((File) x[0]).getAbsolutePath());
  }

  private static String msgFileNotExists(Object[] x) {
    return format("File/directory already exists (%s): %s", x[1], ((File) x[0]).getAbsolutePath());
  }

  private static String msgReadable(Object[] x) {
    return format("File/directory (%s) not reable: %s", x[1], ((File) x[0]).getAbsolutePath());
  }

  private static String msgWritable(Object[] x) {
    return format("File/directory (%s) not writable: %s", x[1], ((File) x[0]).getAbsolutePath());
  }

  private static String msgContains(Object[] x) {
    return format("%s must contain %s", arg(x), str(x[2]));
  }

  private static String msgNotContains(Object[] x) {
    return format("%s must not contain %s", arg(x), str(x[2]));
  }

  private static String msgElementOf(Object[] x) {
    return format("%s must be element of %s (was %s)", arg(x), str(x[2]), str(x[0]));
  }

  private static String msgNotElementOf(Object[] x) {
    return format("%s must be not element of %s (was %s)", arg(x), str(x[2]), str(x[0]));
  }

  private static String msgContainsKey(Object[] x) {
    return format("%s must contain key %s", arg(x), str(x[2]));
  }

  private static String msgNotContainsKey(Object[] x) {
    return format("%s must not contain key %s", arg(x), str(x[2]));
  }

  private static String msgContainsValue(Object[] x) {
    return format("%s must not contain value %s", arg(x), str(x[2]));
  }

  private static String msgNotContainsValue(Object[] x) {
    return format("%s must not contain value %s", arg(x), str(x[2]));
  }

  private static String msgInstanceOf(Object[] x) {
    String fmt = "%s must be instance of %s (was %s)";
    return format(fmt, arg(x), ((Class<?>) x[2]).getName(), cname(x[0]));
  }

  private static String msgMultipleOf(Object[] x) {
    return format("%s must be multiple of %d (was %d)", arg(x), x[2], x[0]);
  }

  private static String sz(Object[] x) {
    if (x[0] instanceof CharSequence) {
      return "(" + arg(x) + ").length()";
    } else if (x[0].getClass().isArray()) {
      return "(" + arg(x) + ").length";
    }
    return "(" + arg(x) + ").size()";
  }

  private static String arg(Object[] x) {
    if (x[0] == null) {
      return x[1].toString();
    }
    return "(" + sname(x[0]) + ") " + x[1];
  }

  private static String str(Object arg) {
    if (arg == null) {
      return "null";
    } else if (arg instanceof Number) {
      return arg.toString();
    } else if (arg instanceof Enum) {
      return arg.toString();
    } else if (arg instanceof CharSequence) {
      String s = arg.toString();
      if (s.length() > 20) {
        return '"' + substr(s, 0, 20) + "[...]\"";
      }
      return '"' + s + '"';
    } else if (arg.getClass().isArray()) {
      return getArrayTypeSimpleName(arg) + '@' + System.identityHashCode(arg);
    }
    return sname(arg) + '@' + System.identityHashCode(arg);
  }

  private static String cname(Object obj) {
    if (obj.getClass().isArray()) {
      return getArrayTypeName(obj);
    }
    return obj.getClass().getName();
  }

  private static String sname(Object obj) {
    if (obj.getClass().isArray()) {
      return getArrayTypeSimpleName(obj);
    }
    return obj.getClass().getSimpleName();
  }
}
