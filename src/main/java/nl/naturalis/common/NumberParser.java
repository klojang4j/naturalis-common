package nl.naturalis.common;

import java.math.BigDecimal;

class NumberParser<T extends Number> {

  private final Class<T> targetType;

  NumberParser(Class<T> targetType) {
    this.targetType = targetType;
  }

  T parse(String s) {
    BigDecimal bd;
    try {
      bd = new BigDecimal(s);
    } catch (NumberFormatException e) {
      throw new TypeConversionException(s, targetType);
    }
    return new NumberConverter<>(targetType).convert(bd);
  }
}
