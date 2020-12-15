package nl.naturalis.common.check;

import java.util.function.IntPredicate;
import java.util.function.Predicate;
import nl.naturalis.common.function.IntObjRelation;
import nl.naturalis.common.function.IntRelation;
import nl.naturalis.common.function.ObjIntRelation;
import nl.naturalis.common.function.Relation;
import static java.lang.String.format;
import static nl.naturalis.common.check.Check.DEFAULT_ARG_NAME;
import static nl.naturalis.common.check.CommonChecks.nameOf;

/**
 * Thrown if a check on an argument cannot actually be applied to that argument, or if the check
 * fails for reasons not within its remit.
 *
 * @author Ayco Holleman
 */
@SuppressWarnings("rawtypes")
public class InvalidCheckException extends RuntimeException {

  private static final String ERR0 = "Error while checking %s: %s not applicable to %s";
  private static final String ERR1 = "Error while checking %s: %s cannot be subject of %s";

  static InvalidCheckException notApplicable(Predicate test, Object arg) {
    String msg = format(ERR0, DEFAULT_ARG_NAME, nameOf(test), arg.getClass().getName());
    return new InvalidCheckException(msg);
  }

  static InvalidCheckException notApplicable(IntPredicate test, Object arg) {
    String msg = format(ERR0, DEFAULT_ARG_NAME, nameOf(test), arg.getClass().getName());
    return new InvalidCheckException(msg);
  }

  static InvalidCheckException notApplicable(Relation test, Object arg) {
    String msg = format(ERR1, DEFAULT_ARG_NAME, arg.getClass().getName(), nameOf(test));
    return new InvalidCheckException(msg);
  }

  static InvalidCheckException notApplicable(IntRelation test, Object arg) {
    String msg = format(ERR1, DEFAULT_ARG_NAME, arg.getClass().getName(), nameOf(test));
    return new InvalidCheckException(msg);
  }

  static InvalidCheckException notApplicable(ObjIntRelation test, Object arg) {
    String msg = format(ERR1, DEFAULT_ARG_NAME, arg.getClass().getName(), nameOf(test));
    return new InvalidCheckException(msg);
  }

  static InvalidCheckException notApplicable(IntObjRelation test, Object arg) {
    String msg = format(ERR1, DEFAULT_ARG_NAME, arg.getClass().getName(), nameOf(test));
    return new InvalidCheckException(msg);
  }

  static InvalidCheckException notApplicable(Predicate test, Object arg, String argName) {
    String msg = format(ERR0, argName, nameOf(test), arg.getClass().getName());
    return new InvalidCheckException(msg);
  }

  static InvalidCheckException notApplicable(IntPredicate test, Object arg, String argName) {
    String msg = format(ERR0, argName, nameOf(test), arg.getClass().getName());
    return new InvalidCheckException(msg);
  }

  static InvalidCheckException notApplicable(Relation test, Object arg, String argName) {
    String msg = format(ERR1, argName, arg.getClass().getName(), nameOf(test));
    return new InvalidCheckException(msg);
  }

  static InvalidCheckException notApplicable(IntRelation test, Object arg, String argName) {
    String msg = format(ERR1, argName, arg.getClass().getName(), nameOf(test));
    return new InvalidCheckException(msg);
  }

  static InvalidCheckException notApplicable(ObjIntRelation test, Object arg, String argName) {
    String msg = format(ERR1, argName, arg.getClass().getName(), nameOf(test));
    return new InvalidCheckException(msg);
  }

  static InvalidCheckException notApplicable(IntObjRelation test, Object arg, String argName) {
    String msg = format(ERR1, argName, arg.getClass().getName(), nameOf(test));
    return new InvalidCheckException(msg);
  }

  InvalidCheckException(String message) {
    super(message);
  }
}
