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
 * <p>The following type and value conversion will be performed:
 *
 * <p>
 *
 * <ol>
 *   <li>If the incoming value is {@code null} and the targetype is a primitive type (e.g. {@code
 *       float.class}, that type's default value will be returned ({@code 0F}), wrapped into the
 *       type's wrapper class ({@code Float.class}). Otherwise {@code null} will be returned.
 *   <li>If the incoming value is an instance of the target type, a simple cast of the object
 *       ({@code (T) obj} is returned.
 *   <li>If the target type is a primitive type and the incoming value has the corresponding wrapper
 *       type, a simple cast of the object ({@code (T) obj} is returned.
 *   <li>If the target type is {@code boolean.class} or {@code Boolean.class}, {@link
 *       Bool#from(Object) Bool.from(obj)} is returned.
 *   <li>If the target type is a primitive number type and the incoming value is a {@link Number}
 *       type other than the corresponding wrapper class, the result of {@link
 *       NumberMethods#convert(Number, Class) NumberMethods.convert(obj)} is returned. Otherwise the
 *       result of {@link NumberMethods#parse(String, Class) NumberMethods.parse(obj.toString()} is
 *       returned.
 *   <li>If the target type is a {@code Number} type and the incoming value has a different {@code
 *       Number} type, the result of {@link NumberMethods#convert(Number, Class)
 *       NumberMethods.convert(obj)} is returned.
 *   <li>If the target type is an {@code enum} class and the incoming value is a {@code Number}, the
 *       number will be converted to an {@code Integer} using {@link NumberMethods#convert(Number,
 *       Class) NumberMethods.convert(obj)} and {@code targetType.getEnumConstants()[integer]} is
 *       returned. Otherwise the incoming value is converted to a {@code String} and the enum
 *       constant whose name or {@code toString()} value is equal to that {@code String} is
 *       returned.
 *   <li>If the target type is {@code String.class}, {@code obj.toString()} is returned.
 * </ol>
 *
 * @see NumberMethods#convert(Number, Class)
 * @see NumberMethods#parse(String, Class)
 * @see Bool
 * @author Ayco Holleman
 * @param <T> The type to which incoming values will be converted
 */
public class Loose<T> {

  /**
   * Converts the specified object to the specified type.
   *
   * @param <U> The target type
   * @param obj The value to convert
   * @param targetType The {@code Class} object corresponding to the target type
   * @return The converted value
   */
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
    } else if (targetType == String.class) {
      return (T) obj.toString();
    }
    throw new TypeConversionException(obj, targetType);
  }
}
