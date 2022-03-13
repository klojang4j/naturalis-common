package nl.naturalis.common.check;

import static java.lang.String.format;
import static nl.naturalis.common.check.MsgUtil.*;

final class MsgIntRelation {

  private MsgIntRelation() {}

  static Formatter msgEq() {
    return formatRelation("equal", true, false);
  }

  static Formatter msgNe() {
    return formatDeniedRelation("equal", true, false);
  }

  static Formatter msgGt() {
    return formatRelation("be >", true);
  }

  static Formatter msgGte() {
    return formatRelation("be >=", true);
  }

  static Formatter msgLt() {
    return formatRelation("be <", true);
  }

  static Formatter msgLte() {
    return formatRelation("be <=", true);
  }

  static Formatter msgMultipleOf() {
    return formatRelation("be multiple of", true);
  }
}
