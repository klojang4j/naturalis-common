package nl.naturalis.common;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.invoke.BeanWriter;
import static nl.naturalis.common.ArrayMethods.isOneOf;
import static nl.naturalis.common.ArrayMethods.pack;
import static nl.naturalis.common.ClassMethods.box;
import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.ClassMethods.isAutoUnboxedAs;
import static nl.naturalis.common.ClassMethods.isPrimitiveNumber;
import static nl.naturalis.common.ObjectMethods.getDefaultValue;
import static nl.naturalis.common.check.CommonChecks.notNull;

/**
 * Performs a wide variety of type and value conversions, more or less akin to loosely-typed
 * languages. However this class is not intended as an exact emulation of the type conversions
 * happening there. Rather it brings together separate conversion classes and methods in
 * naturalis-common in order to convert as much as possible into as much as possible. This class is
 * used by the {@link BeanWriter} class to perform type conversions on the values passed to its
 * {@link BeanWriter#set(Object, String, Object) set} method.
 *
 * <p>The list of conversions performed by this class it too large to enumerate. Generally, however,
 * the following applies:
 *
 * @see NumberMethods#convert(Number, Class)
 * @see NumberMethods#parse(String, Class)
 * @see Bool
 * @author Ayco Holleman
 * @param <T> The type to which incoming values will be converted
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Morph<T> {

  /**
   * Converts the specified object to the specified type.
   *
   * @param <U> The target type
   * @param obj The value to convert
   * @param targetType The {@code Class} object corresponding to the target type
   * @return The converted value
   * @throws TypeConversionException If the conversion did not succeed
   */
  public static <U> U convert(Object obj, Class<U> targetType) {
    return new Morph<>(targetType).convert(obj);
  }

  private final Class<T> targetType;

  /**
   * Creates a new {@code Morph} instance that will convert values to the specified type.
   *
   * @param targetType The type to which to convert values
   */
  public Morph(Class<T> targetType) {
    this.targetType = Check.notNull(targetType).ok();
  }

  /**
   * Converts the specified object into an instance of the type specified through the constructor.
   *
   * @param obj The value to convert
   * @return An instance of the target type
   * @throws TypeConversionException If the conversion did not succeed
   */
  public T convert(Object obj) throws TypeConversionException {
    Class<T> toType = this.targetType;
    if (obj == null) {
      return getDefaultValue(toType);
    }
    Class myType = obj.getClass();
    if (toType.isInstance(obj) || isAutoUnboxedAs(myType, toType)) {
      return (T) obj;
    } else if (toType.isArray()) {
      return toArray(obj);
    } else if (isOneOf(toType, List.class, ArrayList.class, Collection.class)) {
      return toCollection1(obj, ArrayList::new);
    } else if (toType == Set.class || toType == HashSet.class) {
      return toCollection1(obj, HashSet::new);
    } else if (toType == LinkedList.class) {
      return toCollection2(obj, LinkedList::new);
    } else if (toType == LinkedHashSet.class) {
      return toCollection2(obj, LinkedHashSet::new);
    } else if (toType == TreeSet.class) {
      return toCollection2(obj, TreeSet::new);
    } else if (isA(toType, Collection.class)) {
      throw new TypeConversionException(obj, toType);
    }
    // If we get to this point we know for sure the target
    // type is not an array and not a Collection.
    else if (myType.isArray()) {
      return Array.getLength(obj) == 0
          ? (T) getDefaultValue(toType)
          : convert(Array.get(obj, 0), toType);
    } else if (isA(myType, Collection.class)) {
      Collection collection = (Collection) obj;
      return collection.size() == 0
          ? (T) getDefaultValue(toType)
          : convert(collection.iterator().next(), toType);
    } else if (toType == boolean.class || toType == Boolean.class) {
      return (T) Bool.from(obj);
    } else if (isPrimitiveNumber(toType)) {
      return toPrimitiveNumber(obj);
    } else if (isA(toType, Number.class)) {
      return toNumber(obj);
    } else if (toType.isEnum()) {
      return toEnum(obj);
    } else if (toType == char.class || toType == Character.class) {
      return toChar(obj);
    } else if (toType == String.class) {
      return (T) obj.toString();
    }
    throw new TypeConversionException(obj, toType);
  }

  private T toEnum(Object obj) {
    if (isA(obj.getClass(), Number.class)) {
      Integer i = new NumberConverter<>(Integer.class).convert((Number) obj);
      return checkOrdinal(i, obj);
    } else if (obj.getClass() == char.class || obj.getClass() == Character.class) {
      char c = (Character) obj;
      if (c >= '0' && c <= '9') {
        return checkOrdinal(c - 48, obj);
      }
    } else {
      String s = stringify(obj);
      try {
        int i = new NumberParser<>(Integer.class).parse(s);
        return checkOrdinal(i, obj);
      } catch (TypeConversionException e) {
      }
      for (Object o : targetType.getEnumConstants()) {
        Enum enum0 = (Enum) o;
        if (enum0.name().equals(s)) {
          return (T) enum0;
        }
        String s0 = enum0.toString();
        if (s0 != enum0.name() && s0 != null && s0.equals(s)) {
          return (T) enum0;
        }
      }
    }
    throw new TypeConversionException(obj, targetType);
  }

  private T checkOrdinal(int ordinal, Object obj) {
    Class<T> toType = this.targetType;
    if (ordinal < 0 || ordinal >= toType.getEnumConstants().length) {
      String fmt = "Invalid ordinal value for enum %s: %d";
      String msg = String.format(fmt, toType.getName(), ordinal);
      throw new TypeConversionException(obj, toType, msg);
    }
    return toType.getEnumConstants()[ordinal];
  }

  private T toPrimitiveNumber(Object obj) {
    Class myType = obj.getClass();
    Class toType = targetType;
    if (isA(myType, Number.class)) {
      return (T) new NumberConverter(box(toType)).convert((Number) obj);
    } else if (myType.isEnum()) {
      return (T) (Integer) ArrayMethods.find(myType.getEnumConstants(), obj);
    } else if (myType == Character.class) {
      return charToNumber(obj, box(toType));
    }
    return (T) new NumberParser(box(toType)).parse(stringify(obj));
  }

  private T toNumber(Object obj) {
    Class myType = obj.getClass();
    Class toType = targetType;
    if (isA(myType, Number.class)) {
      return (T) new NumberConverter(toType).convert((Number) obj);
    } else if (myType.isEnum()) {
      return (T) (Integer) ArrayMethods.find(myType.getEnumConstants(), obj);
    } else if (myType == Character.class) {
      return charToNumber(obj, box(toType));
    }
    return (T) new NumberParser(toType).parse(stringify(obj));
  }

  private T toChar(Object obj) {
    String s = stringify(obj);
    if (s.length() == 1) {
      return (T) (Character) s.charAt(0);
    }
    throw new TypeConversionException(obj, targetType);
  }

  private T toArray(Object obj) {
    if (obj.getClass().isArray()) {
      return arrayToArray(obj);
    } else if (isA(obj.getClass(), Collection.class)) {
      return collectionToArray((Collection) obj);
    }
    return (T) pack(convert(obj, targetType.getComponentType()));
  }

  private T arrayToArray(Object inputArray) {
    int sz = Array.getLength(inputArray);
    Class elementType = targetType.getComponentType();
    Object outputArray = Array.newInstance(elementType, sz);
    for (int i = 0; i < sz; ++i) {
      Object in = Array.get(inputArray, i);
      Object out = convert(in, elementType);
      Array.set(outputArray, i, out);
    }
    return (T) outputArray;
  }

  private T collectionToArray(Collection collection) {
    Class elementType = targetType.getComponentType();
    Object array = Array.newInstance(elementType, collection.size());
    int i = 0;
    for (Object in : collection) {
      Object out = convert(in, elementType);
      Array.set(array, i++, out);
    }
    return (T) array;
  }

  private T toCollection1(Object obj, IntFunction<Collection> constructor) {
    if (isA(obj.getClass(), Collection.class)) {
      return (T) new ArrayList((Collection) obj);
    } else if (obj.getClass().isArray()) {
      return (T) arrayToCollection(obj, constructor);
    }
    return (T) singletonCollection(obj, constructor);
  }

  private T toCollection2(Object obj, Supplier<Collection> c) {
    if (isA(obj.getClass(), Collection.class)) {
      return (T) new ArrayList((Collection) obj);
    } else if (obj.getClass().isArray()) {
      return (T) arrayToCollection(obj, c.get());
    }
    return (T) singletonCollection(obj, c.get());
  }

  private static Collection singletonCollection(Object obj, IntFunction<Collection> constructor) {
    return singletonCollection(obj, constructor.apply(1));
  }

  private static Collection singletonCollection(Object obj, Collection c) {
    c.add(obj);
    return c;
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

  private T charToNumber(Object obj, Class targetType) {
    char c = (Character) obj;
    if (c >= '0' && c <= '9') {
      return (T) new NumberConverter(targetType).convert(c - 48);
    }
    throw new TypeConversionException(obj, targetType);
  }

  private static String stringify(Object obj) {
    return Check.that(obj.toString()).is(notNull(), "obj.toString() must not return null").ok();
  }
}
