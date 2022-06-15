package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.invoke.BeanWriter;

import java.lang.reflect.Array;
import java.util.Collection;

import static nl.naturalis.common.ArrayMethods.pack;
import static nl.naturalis.common.ClassMethods.isAutoUnboxedAs;
import static nl.naturalis.common.ObjectMethods.getTypeDefault;
import static nl.naturalis.common.check.CommonChecks.notNull;

/**
 * Performs a wide variety of type conversions. The conversions try to strike a
 * balance between being lenient without becoming outlandish or contrived.This class
 * is optionally used by the {@link BeanWriter} class to perform type conversions on
 * the values passed to its {@link BeanWriter#set(Object, String, Object) set}
 * method.
 *
 * <p>The list of conversions performed by this class it too large to enumerate.
 * Generally, however, the following applies:
 *
 * @param <T> The type to which incoming values will be converted
 * @author Ayco Holleman
 * @see NumberMethods#convert(Number, Class)
 * @see NumberMethods#parse(String, Class)
 * @see Bool
 * @see nl.naturalis.common.util.EnumParser
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
   * Creates a new {@code Morph} instance that will convert values to the specified
   * type.
   *
   * @param targetType The type to which to convert values
   */
  public Morph(Class<T> targetType) {
    this.targetType = Check.notNull(targetType).ok();
  }

  /**
   * Converts the specified object into an instance of the type specified through the
   * constructor.
   *
   * @param obj The value to convert
   * @return An instance of the target type
   * @throws TypeConversionException If the conversion did not succeed
   */
  public T convert(Object obj) throws TypeConversionException {
    Class<T> toType = this.targetType;
    if (obj == null) {
      return getTypeDefault(toType);
    } else if (toType.isInstance(obj)) {
      return (T) obj;
    } else if (isAutoUnboxedAs(obj.getClass(), toType)) {
      return (T) obj;
    } else if (toType == String.class) {
      return (T) obj.toString();
    } else if (toType.isArray()) {
      return toArray(obj);
    } else if (ClassMethods.isSubtype(toType, Collection.class)) {
      return MorphTable0.getInstance().morph(obj, toType);
    }
    Class myType = obj.getClass();
    if (myType.isArray()) {
      boolean empty = Array.getLength(obj) == 0;
      return empty ? getTypeDefault(toType) : convert(Array.get(obj, 0), toType);
    } else if (ClassMethods.isSubtype(myType, Collection.class)) {
      Collection coll = (Collection) obj;
      boolean empty = coll.size() == 0;
      return empty ? getTypeDefault(toType) : convert(coll.iterator()
          .next(), toType);
    }
    Object out = MorphTable1.getInstance().morph(obj, toType);
    if (out != null) {
      return (T) out;
    } else if (toType.isEnum()) {
      return (T) MorphTable2.getInstance().morph(obj, toType);
    }
    throw new TypeConversionException(obj, toType);
  }

  private T toArray(Object obj) {
    if (obj.getClass().isArray()) {
      return arrayToArray(obj);
    } else if (ClassMethods.isSubtype(obj.getClass(), Collection.class)) {
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

  static String stringify(Object obj) {
    return Check.that(obj.toString())
        .is(notNull(), "obj.toString() must not return null")
        .ok();
  }

}
