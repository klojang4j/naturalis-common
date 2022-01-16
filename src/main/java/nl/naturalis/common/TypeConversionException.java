package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import static java.lang.String.format;
import static nl.naturalis.common.ClassMethods.className;
import static nl.naturalis.common.ClassMethods.simpleClassName;

public class TypeConversionException extends RuntimeException {

  private final Object objectToConvert;
  private final Class<?> targetType;

  public TypeConversionException(Object obj, Class<?> targetType) {
    super(defaultMessage(obj, targetType));
    this.objectToConvert = obj;
    this.targetType = targetType;
  }

  public TypeConversionException(Object obj, Class<?> targetType, String msg, Object... msgArgs) {
    super(defaultMessage(obj, targetType) + ". " + format(msg, msgArgs));
    this.objectToConvert = obj;
    this.targetType = targetType;
  }

  static String defaultMessage(Object obj, Class<?> type) {
    if (obj == null) {
      return format("Cannot convert null to %s", className(type));
    }
    return format("Cannot convert %s to %s", className(obj), className(type));
  }

  public Object getObjectToConvert() {
    return objectToConvert;
  }

  public Class<?> getTargetType() {
    return targetType;
  }
}
