package nl.naturalis.common.check;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.CollectionMethods;
import nl.naturalis.common.StringMethods;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;
import static java.lang.String.format;
import static nl.naturalis.common.ArrayMethods.asArray;
import static nl.naturalis.common.StringMethods.ellipsis;
import static nl.naturalis.common.check.CommonChecks.MESSAGE_PATTERNS;

@SuppressWarnings({"rawtypes", "unchecked"})
class Messages {

  private static final String ERR_INVALID_VALUE = "Invalid value for %s: %s";

  static String createMessage(Predicate test, boolean negated, String argName, Object argValue) {
    MessageData md = new MessageData(test, negated, argName, argValue);
    return message(md);
  }

  static String createMessage(IntPredicate test, boolean negated, String argName, int argValue) {
    return message(new MessageData(test, negated, argName, argValue));
  }

  static String createMessage(
      Relation relation, boolean negated, String argName, Object subject, Object object) {
    return message(new MessageData(relation, negated, argName, subject, object));
  }

  static String createMessage(
      IntRelation relation, boolean negated, String argName, int subject, int object) {
    return message(new MessageData(relation, negated, argName, subject, object));
  }

  static String createMessage(
      ObjIntRelation relation, boolean negated, String argName, Object subject, int object) {
    return message(new MessageData(relation, negated, argName, subject, object));
  }

  static String createMessage(
      IntObjRelation relation, boolean negated, String argName, int subject, Object object) {
    return message(new MessageData(relation, negated, argName, subject, object));
  }

  private static String message(MessageData md) {
    Formatter formatter = MESSAGE_PATTERNS.get(md.getCheck());
    if (formatter != null) {
      return formatter.apply(md);
    }
    return String.format(ERR_INVALID_VALUE, md.argName(), toStr(md.argument()));
  }

  static Formatter msgNullPointer() {
    return md -> {
      if (md.negated()) {
        return msgNotNull().apply(md);
      }
      return format("%s must be null (was %s)", md.argName(), toStr(md.argument()));
    };
  }

  static Formatter msgNotNull() {
    return md -> {
      if (md.negated()) {
        return msgNullPointer().apply(md);
      }
      return format("%s must not be null", md.argName());
    };
  }

  static Formatter msgYes() {
    return md -> {
      if (md.negated()) {
        return msgNo().apply(md);
      }
      return format("%s must be true (was false)", md.argName());
    };
  }

  static Formatter msgNo() {
    return md -> {
      if (md.negated()) {
        return msgYes().apply(md);
      }
      return format("%s must be false (was true)", md.argName());
    };
  }

  static Formatter msgNoneNull() {
    return md -> {
      if (md.negated()) { // Negation is bullshit, but OK
        format("%s must be null or contain one or more null values", md.argName());
      }
      return format("%s must not be null or contain null values", md.argName());
    };
  }

  static Formatter msgDeepNotEmpty() {
    return md -> {
      if (md.negated()) {
        String fmt = "%s must be empty or contain or contain or more empty values";
        return format(fmt, md.argName());
      }
      return format("%s must not be empty or contain empty values", md.argName());
    };
  }

  static Formatter msgEmpty() {
    return md -> {
      if (md.negated()) {
        return format("%s must not be null or empty", md.argName());
      }
      return format("%s must be empty (was %s)", md.argName(), toStr(md.argument()));
    };
  }

  static Formatter msgBlank() {
    return md -> format("%s must %sbe null or whitespace-only", not(md), md.argName());
  }

  static Formatter msgInteger() {
    String fmt = "%s must %sbe an integer (was %s)";
    return md -> format(fmt, md.argName(), not(md), md.argument());
  }

  static Formatter msgValidPortNumber() {
    String fmt = "%s must %sbe a valid TCP/UDP port number (was %s)";
    return md -> format(fmt, md.argName(), not(md), md.argument());
  }

  static Formatter msgEqualTo() {
    String fmt = "%s must %sbe equal to %s (was %s)";
    return md -> format(fmt, md.argName(), not(md), toStr(md.object()), toStr(md.argument()));
  }

  static Formatter msgEqualsIgnoreCase() {
    return md -> {
      String fmt = "%s must %sbe equal ignoring case to any of %s (was %s)";
      return format(fmt, md.argName(), not(md), toStr(md.object()), toStr(md.argument()));
    };
  }

  static Formatter msgSameAs() {
    String fmt = "%s must %sreference same object as %s (was %s)";
    return md -> {
      String id0 = className(md.argument()) + '@' + System.identityHashCode(md.argument());
      String id1 = className(md.object()) + '@' + System.identityHashCode(md.object());
      return format(fmt, md.argName(), not(md), id1, id0);
    };
  }

  static Formatter msgSizeEquals() {
    String fmt = "%s must %sbe equal to %s (was %s)";
    return md -> format(fmt, argSize(md), not(md), md.object(), md.argument());
  }

  static Formatter msgSizeGT() {
    String fmt = "%s must be > %s (was %s)";
    return md -> {
      if (md.negated()) {
        return msgSizeLTE().apply(md);
      }
      return format(fmt, argSize(md), md.object(), md.argument());
    };
  }

  static Formatter msgSizeGTE() {
    String fmt = "%s must be >= %s (was %s)";
    return md -> {
      if (md.negated()) {
        return msgSizeLT().apply(md);
      }
      return format(fmt, argSize(md), md.object(), md.argument());
    };
  }

  static Formatter msgSizeLT() {
    String fmt = "%s must be < %s (was %s)";
    return md -> {
      return format(fmt, argSize(md), not(md), md.object(), md.argument());
    };
  }

  static Formatter msgSizeLTE() {
    String fmt = "%s must %sbe <= %s (was %s)";
    return md -> format(fmt, argSize(md), not(md), md.object(), md.argument());
  }

  static Formatter msgIndexOf() {
    String fmt = "%s must %sbe >= 0 and < %s (was %s)";
    return md -> {
      int i = ((List<?>) md.object()).size();
      return format(fmt, md.argName(), not(md), i, md.argument());
    };
  }

  static Formatter msgToIndexOf() {
    String fmt = "%s must %sbe >= 0 and <= %s (was %s)";
    return md -> {
      int i = ((List<?>) md.object()).size();
      return format(fmt, md.argName(), not(md), i, md.argument());
    };
  }

  static Formatter msgFile() {
    String fmt = "%s must %sbe an existing file (was %s)";
    return md -> format(fmt, md.argName(), not(md), md.argument());
  }

  static Formatter msgDirectory() {
    String fmt = "%s must %sbe an existing directory (was %s)";
    return md -> format(fmt, md.argName(), not(md), md.argument());
  }

  static Formatter msgPresent() {
    String fmt = "%s must %sbe an existing file or directory (was %s)";
    return md -> format(fmt, md.argName(), not(md), md.argument());
  }

  static Formatter msgReadable() {
    String fmt = "%s must %sbe readable (was %s)";
    return md -> format(fmt, md.argName(), not(md), ((File) md.argument()).getAbsolutePath());
  }

  static Formatter msgWritable() {
    String fmt = "%s (%s) must %sbe writable";
    return md -> format(fmt, md.argName(), not(md), ((File) md.argument()).getAbsolutePath());
  }

  static Formatter msgEven() {
    return md -> format("%s must %sbe even (was %d)", md.argName(), not(md), md.argument());
  }

  static Formatter msgOdd() {
    return md -> format("%s must %sbe odd (was %d)", md.argName(), not(md), md.argument());
  }

  static Formatter msgPositive() {
    return md -> format("%s must %sbe positive (was %d)", md.argName(), not(md), md.argument());
  }

  static Formatter msgNegative() {
    return md -> format("%s must %sbe negative (was %d)", md.argName(), not(md), md.argument());
  }

  static Formatter msgNullOr() {
    String fmt = "%s must %sbe null or %s (was (%s)";
    return md -> format(fmt, md.argName(), not(md), toStr(md.object()), toStr(md.argument()));
  }

  static Formatter msgContaining() {
    return md -> format("%s must %scontain %s", md.argName(), not(md), toStr(md.object()));
  }

  static Formatter msgIn() {
    String fmt = "%s must be in %s (was %s)";
    return md -> format(fmt, md.argName(), not(md), toStr(md.object()), toStr(md.argument()));
  }

  static Formatter msgSupersetOf() {
    return md -> {
      Set set = Set.copyOf((Collection) md.object());
      set.removeAll((Collection) md.argument());
      return format("%s must contain all of %s", md.argName(), toStr(set));
    };
  }

  static Formatter msgSubsetOf() {
    return md -> {
      Set set = Set.copyOf((Collection) md.argument());
      set.removeAll((Collection) md.object());
      return format("%s must contain all of %s", md.argName(), toStr(set));
    };
  }

  static Formatter msgContainingKey() {
    return md -> format("%s must contain key %s", md.argName(), toStr(md.object()));
  }

  static Formatter msgNotContainingKey() {
    return md -> format("%s must not contain key %s", md.argName(), toStr(md.object()));
  }

  static Formatter msgKeyIn() {
    return md ->
        format(
            "%s must be key in %s (was %s)",
            md.argName(), toStr(md.object()), toStr(md.argument()));
  }

  static Formatter msgNotKeyIn() {
    return md ->
        format(
            "%s must not be key in %s (was %s)",
            md.argName(), toStr(md.object()), toStr(md.argument()));
  }

  static Formatter msgContainingValue() {
    return md -> format("%s must not contain value %s", md.argName(), toStr(md.object()));
  }

  static Formatter msgNotContainingValue() {
    return md -> format("%s must not contain value %s", md.argName(), toStr(md.object()));
  }

  static Formatter msgValueIn() {
    return md ->
        format(
            "%s must be value in %s (was %s)",
            md.argName(), toStr(md.object()), toStr(md.argument()));
  }

  static Formatter msgNotValueIn() {
    return md ->
        format(
            "%s must not be value in %s (was %s)",
            md.argName(), toStr(md.object()), toStr(md.argument()));
  }

  static Formatter msgEq() {
    return md ->
        format("%s must be equal to %d (was %s)", md.argName(), md.object(), md.argument());
  }

  static Formatter msgNe() {
    return md -> format("%s must not be equal to %s", md.argName(), md.object());
  }

  static Formatter msgGt() {
    return md -> format("%s must be > %s (was %s)", md.argName(), md.object(), md.argument());
  }

  static Formatter msgGte() {
    return md -> format("%s must be >= %s (was %s)", md.argName(), md.object(), md.argument());
  }

  static Formatter msgLt() {
    return md -> format("%s must be < %s (was %s)", md.argName(), md.object(), md.argument());
  }

  static Formatter msgLte() {
    return md -> format("%s must be <= %s (was %s)", md.argName(), md.object(), md.argument());
  }

  static Formatter msgEndsWith() {
    return md ->
        format("%s must end with \"%s\" (was %s)", md.argName(), md.object(), md.argument());
  }

  static Formatter msgNotEndsWith() {
    return md ->
        format("%s must not end with \"%s\" (was %s)", md.argName(), md.object(), md.argument());
  }

  static Formatter msgHasSubstr() {
    return md ->
        format("%s must contain \"%s\" (was %s)", md.argName(), md.object(), md.argument());
  }

  static Formatter msgNotHasSubstr() {
    return md ->
        format("%s must not contain \"%s\" (was %s)", md.argName(), md.object(), md.argument());
  }

  static Formatter msgInstanceOf() {
    return md ->
        format(
            "%s must %sbe instance of %s (was %s)",
            md.argName(),
            not(md),
            ClassMethods.prettyClassName((Class) md.object()),
            ClassMethods.prettyClassName(md.argument()));
  }

  static Formatter msgArray() {
    String fmt = "%s must %sbe an array (was %s)";
    return md -> format(fmt, md.argName(), not(md), className(md.argument()));
  }

  static Formatter msgMultipleOf() {
    String fmt = "%s must %sbe multiple of %d (was %d)";
    return md -> format(fmt, md.argName(), not(md), md.object(), md.argument());
  }

  private static String argSize(MessageData md) {
    if (md.argument() instanceof CharSequence) {
      return md.argName() + ".length()";
    } else if (md.argument().getClass().isArray()) {
      return md.argName() + ".length";
    }
    return md.argName() + ".size()";
  }

  private static String toStr(Object val) {
    if (val == null) {
      return "null";
    } else if (val instanceof Number) {
      return val.toString();
    } else if (val == Boolean.class) {
      return val.toString();
    } else if (val == Character.class) {
      return val.toString();
    } else if (val instanceof Enum) {
      return val.toString();
    } else if (val instanceof CharSequence) {
      return StringMethods.ellipsis(val.toString(), 40);
    } else if (val instanceof Collection) {
      String s = CollectionMethods.implode((Collection) val, ", ", 10);
      return basicToStr(val) + ": [" + ellipsis(s, 40) + "]";
    } else if (val.getClass().isArray()) {
      String s = ArrayMethods.implode(asArray(val), ", ", 10);
      return basicToStr(val) + ": [" + ellipsis(s, 40) + "]";
    } else if (val.getClass() != Object.class) {
      try {
        // If the class has its own toString() method, it's probably informative
        val.getClass().getDeclaredMethod("toString");
        return val.toString();
      } catch (Exception e) {
      }
    }
    return basicToStr(val);
  }

  private static String basicToStr(Object val) {
    return ClassMethods.prettySimpleClassName(val) + '@' + System.identityHashCode(val);
  }

  private static String className(Object obj) {
    return ClassMethods.prettyClassName(obj);
  }

  private static String not(MessageData md) {
    return md.negated() ? "not " : StringMethods.EMPTY;
  }
}
