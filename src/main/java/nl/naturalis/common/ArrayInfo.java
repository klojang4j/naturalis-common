package nl.naturalis.common;

import nl.naturalis.common.check.Check;

import java.util.concurrent.atomic.AtomicReference;

import static nl.naturalis.common.ClassMethods.simpleClassName;
import static nl.naturalis.common.check.CommonChecks.array;
import static nl.naturalis.common.check.CommonChecks.positive;

/**
 * Provides metadata about an array type.
 *
 * @param baseType The lowest-level component type. So for {@code int[][][]} that
 *     would be {@code int} and not {@code int[][]}.
 * @param dimensions The number of dimensions
 * @author Ayco Holleman
 */
public record ArrayInfo(Class<?> baseType, int dimensions) {

  private static Tuple2<Class<?>, ArrayInfo> FOO = Tuple2.of(int.class,
      new ArrayInfo(int.class, 1));

  private static final AtomicReference<Tuple2<Class<?>, ArrayInfo>> cache =
      new AtomicReference<>(FOO);

  /**
   * Returns an {@code ArrayInfo} instance for the specified array type. An {@link
   * IllegalArgumentException} is thrown if the provided object is not an array
   * type.
   *
   * @param arrayClass The array type
   * @return The {@code ArrayInfo} instance
   */
  public static ArrayInfo forClass(Class<?> arrayClass) {
    Check.notNull(arrayClass).is(array());
    return getArrayInfo(arrayClass);
  }

  /**
   * Returns an {@code ArrayInfo} instance for the specified array. An {@link
   * IllegalArgumentException} is thrown if the provided object is not an array.
   *
   * @param array The array
   * @return The {@code ArrayInfo} instance
   */
  public static ArrayInfo forArray(Object array) {
    Check.notNull(array).is(array());
    return getArrayInfo(array.getClass());
  }

  public ArrayInfo(Class<?> baseType, int dimensions) {
    this.baseType = Check.notNull(baseType, "baseType").ok();
    this.dimensions = Check.that(dimensions, "dimensions").is(positive()).ok();
  }

  /**
   * Returns an array type to based on this {@code ArrayInfo} instance.
   *
   * @return An array type to based on this {@code ArrayInfo} instance
   */
  public Class<?> getArrayType() {
    Class<?> c = baseType;
    for (int i = 0; i < dimensions; ++i) {
      c = c.arrayType();
    }
    return c;
  }

  /**
   * Returns an array type with the specified base type and with this instance's
   * number of dimensions.
   *
   * @return An array type for the specified base type and with this instance's
   *     number of dimensions
   */
  public Class<?> getArrayType(Class<?> baseType) {
    Class<?> c = Check.notNull(baseType).ok();
    for (int i = 0; i < dimensions; ++i) {
      c = c.arrayType();
    }
    return c;
  }

  /**
   * Returns an array type for the boxed version of this instance's base type and
   * with this instance's number of dimensions.
   *
   * @return An array type for the boxed version of this instance's base type and
   *     with this instance's number of dimensions
   * @see ClassMethods#box(Class)
   */
  public Class<?> box() {
    return getArrayType(ClassMethods.box(baseType));
  }

  /**
   * Returns an array type for the unboxed version of this instance's base type and
   * with this instance's number of dimensions.
   *
   * @return An array type for the boxed version of this instance's base type and
   *     with this instance's number of dimensions
   * @see ClassMethods#unbox(Class)
   */
  public Class<?> unbox() {
    return getArrayType(ClassMethods.unbox(baseType));
  }

  /**
   * Returns an array type based on the base type of this {@code ArrayInfo} instance
   * and with the specified number of dimensions.
   *
   * @return An array type based on the base type of this {@code ArrayInfo} instance
   *     and with the specified number of dimensions
   */
  public Class<?> getArrayType(int dimensions) {
    Check.that(dimensions, "dimensions").is(positive());
    Class<?> c = baseType;
    for (int i = 0; i < dimensions; ++i) {
      c = c.arrayType();
    }
    return c;
  }

  private static ArrayInfo getArrayInfo(Class<?> arrayClass) {
    return cache.updateAndGet(tuple -> {
      if (tuple.first() == arrayClass) {
        return tuple;
      }
      var c = arrayClass.getComponentType();
      int i = 1;
      for (; c.isArray(); c = c.getComponentType()) {
        ++i;
      }
      return Tuple2.of(arrayClass, new ArrayInfo(c, i));
    }).second();
  }

}
