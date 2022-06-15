package nl.naturalis.common;

import static nl.naturalis.common.ArrayMethods.findReference;
import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.Morph.stringify;

/*
 * Used to morph objects into primitives and primitive wrapper types. Also used to convert to {@code
 * BigDecimal} and {@code BigInteger}. It's pointless to try and use generics here. It will fight
 * you. Too much dynamic stuff going on.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
class MorphTable1 {

  private static MorphTable1 INSTANCE;

  static MorphTable1 getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new MorphTable1();
    }
    return INSTANCE;
  }

  private MorphTable1() {}

  Object morph(Object obj, Class toType) {
    Class type = box(toType);
    if (type == Boolean.class) {
      return Bool.from(obj);
    } else if (type == Character.class) {
      return toChar(obj, toType);
    } else if (ClassMethods.isSubtype(type, Number.class)) {
      return toNumber(obj, type);
    }
    return null;
  }

  private static Number toNumber(Object obj, Class toType) {
    Class myType = obj.getClass();
    if (ClassMethods.isSubtype(myType, Number.class)) {
      return new NumberConverter(toType).convert((Number) obj);
    } else if (myType.isEnum()) {
      return findReference(myType.getEnumConstants(), obj);
    } else if (myType == Character.class) {
      return charToNumber(obj, box(toType));
    }
    return new NumberParser(toType).parse(stringify(obj));
  }

  private static Character toChar(Object obj, Class toType) {
    if (obj.getClass() == Boolean.class) {
      return (Boolean) obj ? '1' : '0';
    }
    String s = stringify(obj);
    if (s.length() == 1) {
      return s.charAt(0);
    }
    throw new TypeConversionException(obj, toType, "String length exceeds 1: %s", obj);
  }

  private static Number charToNumber(Object obj, Class targetType) {
    char c = (Character) obj;
    if (c >= '0' && c <= '9') {
      return new NumberConverter(targetType).convert(c - 48);
    }
    throw new TypeConversionException(obj, targetType);
  }

}
