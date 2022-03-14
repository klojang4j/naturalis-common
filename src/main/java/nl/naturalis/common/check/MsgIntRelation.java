package nl.naturalis.common.check;

import static nl.naturalis.common.check.MsgUtil.formatNegativeRelation;
import static nl.naturalis.common.check.MsgUtil.formatRelation;

final class MsgIntRelation {

  private MsgIntRelation() {}

  static Formatter msgEq() {
    return formatRelation("equal", true, false);
  }

  static Formatter msgNe() {
    return formatNegativeRelation("equal", false, true);
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
