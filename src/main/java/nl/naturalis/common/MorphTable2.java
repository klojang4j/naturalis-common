package nl.naturalis.common;

import nl.naturalis.common.util.EnumParser;

import java.util.HashMap;
import java.util.Map;

/*
 * Dedicated to enum classes.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
class MorphTable2 {

  private static MorphTable2 INSTANCE;

  static MorphTable2 getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new MorphTable2();
    }
    return INSTANCE;
  }

  private MorphTable2() {}

  private final Map<Class, EnumParser> table = new HashMap<>();

  <T extends Enum<T>> T morph(Object obj, Class enumClass) {
    return (T) table.computeIfAbsent(enumClass, EnumParser::new).parse(obj);
  }
}
