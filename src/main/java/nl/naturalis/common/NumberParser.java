package nl.naturalis.common;

import nl.naturalis.common.x.invoke.BigDecimalConverter;

import java.math.BigDecimal;

final class NumberParser<T extends Number> {

  private final Class<T> targetType;

  NumberParser(Class<T> targetType) {
    this.targetType = targetType;
  }

  T parse(String s) {
    BigDecimal bd;
    try {
      return new BigDecimalConverter(new BigDecimal(s)).convertTo(targetType);
    } catch (NumberFormatException | TypeConversionException e) {
      throw new TypeConversionException(s, targetType);
    }
  }

}
