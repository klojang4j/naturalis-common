package nl.naturalis.common;

import static java.lang.String.format;
import static nl.naturalis.common.ClassMethods.className;

/**
 * Thrown by various methods that convert values of one type into values of another type.
 *
 * @see Bool
 * @see Morph
 */
public class TypeConversionException extends RuntimeException {

  private final Object inputValue;
  private final Class<?> targetType;

  public TypeConversionException(Object inputValue, Class<?> targetType) {
    super(defaultMessage(inputValue, targetType));
    this.inputValue = inputValue;
    this.targetType = targetType;
  }

  public TypeConversionException(Object inputValue, Class<?> targetType, String msg, Object... msgArgs) {
    super(defaultMessage(inputValue, targetType) + ". " + format(msg, msgArgs));
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
      return format("Cannot convert null to %s", className(type));
    }
    return format("Cannot convert %s to %s", className(obj), className(type));
  }

}
