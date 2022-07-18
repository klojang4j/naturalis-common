package nl.naturalis.common;

import nl.naturalis.common.util.EnumParser;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
final class MorphToEnum {

  private MorphToEnum() {
    throw new UnsupportedOperationException();
  }

  private static final Map<Class, EnumParser> parsers = new HashMap<>();

  static <T extends Enum<T>> T morph(Object obj, Class enumClass) {
    return (T) parsers.computeIfAbsent(enumClass, EnumParser::new).parse(obj);
  }

}
