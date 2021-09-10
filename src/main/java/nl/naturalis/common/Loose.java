package nl.naturalis.common;

import java.util.Arrays;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.invoke.BeanWriter;
import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.ClassMethods.isPrimitiveNumberClass;
import static nl.naturalis.common.ClassMethods.isAutoBoxedAs;
import static nl.naturalis.common.ObjectMethods.PRIMITIVE_DEFAULTS;

/**
 * Performs a wide variety of type and value conversions, more or less akin to loosely-typed
 * languages. However this class is not intended as an exact emulation of the type conversions
 * happening there. Rather it brings together separate conversion classes and methods in
 * naturalis-common in order to convert as much as possible into as much as possible. This class is
 * used by the {@link BeanWriter} class to perform type conversions on the values passed to its
 * {@link BeanWriter#set(Object, String, Object) set} method.
 *
 * @see NumberMethods#convert(Number, Class)
 * @see NumberMethods#parse(String, Class)
 * @see Bool
 * @author Ayco Holleman
 * @param <T>
 */
public class Loose<T> {

  public static <U> U convert(Object obj, Class<U> targetType) {
    return new Loose<>(targetType).convert(obj);
  }

  private final Class<T> targetType;

  public Loose(Class<T> targetType) {
    this.targetType = Check.notNull(targetType).ok();
  }

  @SuppressWarnings("unchecked")
  public T convert(Object obj) {
    if (obj == null) {
      return targetType.isPrimitive() ? (T) PRIMITIVE_DEFAULTS.get(targetType) : null;
    } else if (targetType.isInstance(obj)) {
      return (T) obj;
    } else if (targetType == String.class) {
      return (T) obj.toString();
    } else if (targetType == boolean.class || targetType == Boolean.class) {
      return (T) Bool.from(obj);
    } else if (isAutoBoxedAs(targetType, obj.getClass())) {
      return (T) obj;
    } else if (isPrimitiveNumberClass(targetType)) {
      Class<? extends Number> c = (Class<? extends Number>) box(targetType);
      if (isA(obj.getClass(), Number.class)) {
        return (T) new NumberConverter<>(c).convert((Number) obj);
      }
      return (T) new NumberParser<>(c).parse(obj.toString());
    } else if (isA(targetType, Number.class)) {
      Class<? extends Number> c = (Class<? extends Number>) targetType;
      if (isA(obj.getClass(), Number.class)) {
        return (T) new NumberConverter<>(c).convert((Number) obj);
      }
      return (T) new NumberParser<>(c).parse(obj.toString());
    } else if (isA(targetType, Enum.class)) {
      if (isA(obj.getClass(), Number.class)) {
        Integer i = new NumberConverter<>(Integer.class).convert((Number) obj);
        if (i < 0 || i >= targetType.getEnumConstants().length) {
          String fmt = "Invalid ordinal value for enum %s: %d";
          throw new TypeConversionException(obj, targetType, fmt, targetType.getName(), i);
        }
        return targetType.getEnumConstants()[i];
      }
      String s = obj.toString();
      return (T)
          Arrays.stream(targetType.getEnumConstants())
              .map(Enum.class::cast)
              .filter(c -> c.name().equals(s) || c.toString().equals(s))
              .findFirst()
              .orElseThrow(() -> new TypeConversionException(obj, targetType));
    }
    throw new TypeConversionException(obj, targetType);
  }
}
