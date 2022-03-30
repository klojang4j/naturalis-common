package nl.naturalis.common.check;

import static nl.naturalis.common.check.MsgUtil.formatNegativeRelation;
import static nl.naturalis.common.check.MsgUtil.formatRelation;

final class MsgIntRelation {

  private MsgIntRelation() {}

  static PrefabMsgFormatter msgEq() {
    return formatRelation("equal", true, false);
  }

  static PrefabMsgFormatter msgNe() {
    return formatNegativeRelation("equal", false, true);
  }

  static PrefabMsgFormatter msgGt() {
    return formatRelation("be >", true);
  }

  static PrefabMsgFormatter msgGte() {
    return formatRelation("be >=", true);
  }

  static PrefabMsgFormatter msgLt() {
    return formatRelation("be <", true);
  }

  static PrefabMsgFormatter msgLte() {
    return formatRelation("be <=", true);
  }

  static PrefabMsgFormatter msgMultipleOf() {
    return formatRelation("be multiple of", true);
  }
}
