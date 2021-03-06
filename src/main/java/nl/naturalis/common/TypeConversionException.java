package nl.naturalis.common;

import static java.lang.String.format;
import static nl.naturalis.common.ClassMethods.*;
import static nl.naturalis.common.StringMethods.ellipsis;
import static nl.naturalis.common.x.Constants.DECENT_TO_STRING;

/**
 * Thrown by various classes and methods that convert values of one type into values
 * of another type.
 *
 * @see Bool
 * @see Morph
 */
public final class TypeConversionException extends RuntimeException {

  static TypeConversionException inputTypeNotSupported(Object obj, Class<?> type) {
    return new TypeConversionException(obj, type, "input type not supported");
  }

  static TypeConversionException targetTypeNotSupported(Object obj, Class<?> type) {
    if (isPrimitiveNumber(type)) {
      String fmt = "primitive types not supported *** call ClassMethods.box to convert %s to %s";
      String c0 = type.getName();
      String c1 = box(type).getSimpleName();
      return new TypeConversionException(obj, type, fmt, c0, c1);
    } else if (isSubtype(type, Number.class)) {
      String c0 = type.getSimpleName();
      return new TypeConversionException(obj, type, "%s not supported", c0);
    }
    String msg = "target type must be subclass of Number";
    return new TypeConversionException(obj, type, msg);
  }

  private final Object inputValue;
  private final Class<?> targetType;

  public TypeConversionException(Object inputValue, Class<?> targetType) {
    super(defaultMessage(inputValue, targetType));
    this.inputValue = inputValue;
    this.targetType = targetType;
  }

  public TypeConversionException(Object inputValue,
      Class<?> targetType,
      String msg,
      Object... msgArgs) {
    super(defaultMessage(inputValue, targetType) + " *** " + format(msg, msgArgs));
    this.inputValue = inputValue;
    this.targetType = targetType;
  }

  public Object getInputValue() {
    return inputValue;
  }

  public Class<?> getTargetType() {
    return targetType;
  }

  private static String defaultMessage(Object obj, Class<?> type) {
    if (obj == null) {
      return format("cannot convert null to %s", className(type));
    } else if (obj instanceof String s) {
      return format("cannot convert \"%s\" to %s",
          ellipsis(obj, 30),
          className(type));
    } else if (DECENT_TO_STRING.contains(obj.getClass())) {
      return format("cannot convert (%s) %s to %s",
          simpleClassName(obj),
          ellipsis(obj, 30), className(type));
    }
    return format("cannot convert (%s) to %s", className(obj), className(type));
  }

}
