package nl.naturalis.common;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.IntFunction;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.invoke.BeanWriter;
import static nl.naturalis.common.ArrayMethods.isOneOf;
import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.ClassMethods.isAutoBoxedAs;
import static nl.naturalis.common.ClassMethods.isPrimitiveNumber;
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
 *   <li>If the incoming value is an array and the target type is a {@link Collection}, the array
 *       will be wrapped into an instance of the appropriate subtype of {@code Collection}.
 *       Otherwise, the first element of the array is returned (recursively converted to the
 *       appropriate type), or {@code null} in case of a zero-length array.
 *   <li>If the incoming value a {@code Collection} and the target type is an array. The {@code
 *       Collection} will be unwrapped into an array. Otherwise the first element of the {@code
 *       Collection} will be returned (recursively converted to the appropriate type), or {@code
 *       null} in case of a zero-size {@code Collection}.
 *   <li>If the target type is {@code String.class}, {@code obj.toString()} is returned.
 * </ol>
 *
 * @see NumberMethods#convert(Number, Class)
 * @see NumberMethods#parse(String, Class)
 * @see Bool
 * @author Ayco Holleman
 * @param <T> The type to which incoming values will be converted
 */
@SuppressWarnings({"unchecked", "rawtypes"})
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

  public T convert(Object obj) {
    if (obj == null) {
      return targetType.isPrimitive() ? (T) PRIMITIVE_DEFAULTS.get(targetType) : null;
    } else if (targetType.isInstance(obj)) {
      return (T) obj;
    } else if (targetType == boolean.class || targetType == Boolean.class) {
      return (T) Bool.from(obj);
    } else if (isAutoBoxedAs(targetType, obj.getClass())) {
      return (T) obj;
    } else if (isPrimitiveNumber(targetType)) {
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
      return toEnum(obj);
    } else if (obj.getClass().isArray()) {
      if (isA(obj.getClass().getComponentType(), targetType)) {
        return Array.getLength(obj) == 0 ? null : convert(Array.get(obj, 0), targetType);
      } else if (isOneOf(targetType, List.class, Collection.class, ArrayList.class)) {
        return (T) arrayToCollection(obj, ArrayList::new);
      } else if (targetType == LinkedList.class) {
        return (T) arrayToCollection(obj, new LinkedList());
      } else if (targetType == Set.class || targetType == LinkedHashSet.class) {
        return (T) arrayToCollection(obj, LinkedHashSet::new);
      } else if (targetType == Set.class || targetType == HashSet.class) {
        return (T) arrayToCollection(obj, HashSet::new);
      } else if (targetType == Set.class || targetType == TreeSet.class) {
        return (T) arrayToCollection(obj, new TreeSet());
      }
    } else if (isA(obj.getClass(), Collection.class)) {
      Collection c = (Collection) obj;
      if (targetType.isArray()) {
        T array = (T) Array.newInstance(targetType.getComponentType(), c.size());
        int i = 0;
        for (Object o : c) {
          Array.set(array, i++, convert(o, targetType.getComponentType()));
        }
        return array;
      } else if (c.isEmpty()) {
        return null;
      }
      return convert(c.iterator().next(), targetType);
    } else if (targetType == String.class) {
      return (T) obj.toString();
    }
    throw new TypeConversionException(obj, targetType);
  }

  private T toEnum(Object obj) {
    if (isA(obj.getClass(), Number.class)) {
      Integer i = new NumberConverter<>(Integer.class).convert((Number) obj);
      return checkOrdinal(i, obj);
    }
    String s = obj.toString();
    try {
      int i = NumberMethods.parseInt(s);
      return checkOrdinal(i, obj);
    } catch (IllegalArgumentException e) {
    }
    return (T)
        Arrays.stream(targetType.getEnumConstants())
            .map(Enum.class::cast)
            .filter(c -> c.name().equals(s) || c.toString().equals(s))
            .findFirst()
            .orElseThrow(() -> new TypeConversionException(obj, targetType));
  }

  private T checkOrdinal(int ordinal, Object obj) {
    if (ordinal < 0 || ordinal >= targetType.getEnumConstants().length) {
      String fmt = "Invalid ordinal value for enum %s: %d";
      throw new TypeConversionException(obj, targetType, fmt, targetType.getName(), ordinal);
    }
    return targetType.getEnumConstants()[ordinal];
  }

  private static Collection arrayToCollection(Object obj, IntFunction<Collection> constructor) {
    return arrayToCollection(obj, constructor.apply(Array.getLength(obj)));
  }

  private static Collection arrayToCollection(Object obj, Collection c) {
    for (int i = 0; i < Array.getLength(obj); ++i) {
      c.add(Array.get(obj, i));
    }
    return c;
  }
}
