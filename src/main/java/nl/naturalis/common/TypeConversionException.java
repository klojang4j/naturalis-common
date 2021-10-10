package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import static java.lang.String.format;
import static nl.naturalis.common.ClassMethods.className;

public class TypeConversionException extends RuntimeException {

  private final Object objectToConvert;
  private final Class<?> targetType;

  public TypeConversionException(Object objectToConvert, Class<?> targetType) {
    super(getDefaultMessage(objectToConvert, targetType));
    this.objectToConvert = objectToConvert;
    this.targetType = targetType;
  }

  public TypeConversionException(
      Object objectToConvert, Class<?> targetType, String message, Object... msgArgs) {
    super(format(message, msgArgs));
    this.objectToConvert = objectToConvert;
    this.targetType = targetType;
  }

  static String getDefaultMessage(Object obj, Class<?> type) {
    Check.notNull(type, "type");
    String cn0 = className(type);
    if (obj == null) {
      return format("Cannot convert null into instance of %s", cn0);
    }
    if (obj.getClass() == String.class) {
      return format("Cannot convert \"%s\" into instance of %s", obj, cn0);
    }
    if (Number.class.isInstance(obj)) {
      String fmt = "Cannot convert %s %s into instance of %s";
      return format(fmt, obj.getClass().getSimpleName(), obj, cn0);
    }
    String cn1 = className(obj.getClass());
    return format("Cannot convert instance of %s into instance of %s", cn1, cn0);
  }

  public Object getObjectToConvert() {
    return objectToConvert;
  }

  public Class<?> getTargetType() {
    return targetType;
  }
}
