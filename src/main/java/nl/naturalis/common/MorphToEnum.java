package nl.naturalis.common;

import nl.naturalis.common.util.EnumParser;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
final class MorphToEnum {

  private static MorphToEnum INSTANCE;

  static MorphToEnum getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new MorphToEnum();
    }
    return INSTANCE;
  }

  private MorphToEnum() {}

  private final Map<Class, EnumParser> table = new HashMap<>();

  <T extends Enum<T>> T morph(Object obj, Class enumClass) {
    return (T) table.computeIfAbsent(enumClass, EnumParser::new).parse(obj);
  }

}
