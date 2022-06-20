package nl.naturalis.common;

import nl.naturalis.common.check.Check;

import java.util.concurrent.atomic.AtomicReference;

import static nl.naturalis.common.ClassMethods.simpleClassName;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * Provides metadata about an array type.
 *
 * @param baseType The lowest-level component type. So for {@code int[][][]} that
 *     would be {@code int} and not {@code int[][]}.
 * @param dimensions The number of dimensions
 * @author Ayco Holleman
 */
public record ArrayType(Class<?> baseType, int dimensions) {

  // Initialized with a nonsensical tuple that's guaranteed not to
  // match any realistic tuple (because void is not an array type)
  private static final AtomicReference<Tuple2<Class<?>, ArrayType>> cache =
      new AtomicReference<>(Tuple2.of(void.class, new ArrayType(int.class, 1)));

  /**
   * Returns an {@code ArrayType} instance for the specified array type. An {@link
   * IllegalArgumentException} is thrown if the provided object is not an array
   * type.
   *
   * @param arrayClass The array type
   * @return The {@code ArrayType} instance
   */
  public static ArrayType forClass(Class<?> arrayClass) {
    Check.notNull(arrayClass).is(array());
    return create(arrayClass);
  }

  /**
   * Returns an {@code ArrayType} instance for the specified array. An {@link
   * IllegalArgumentException} is thrown if the provided object is not an array.
   *
   * @param array The array
   * @return The {@code ArrayType} instance
   */
  public static ArrayType forArray(Object array) {
    Check.notNull(array).is(array());
    return create(array.getClass());
  }

  /**
   * Returns 0 (zero) for non-array types and the number of dimensions for array
   * types.
   *
   * @param c The type for which to get the number of dimensions
   * @return The dimensionality of the type.
   */
  public static int dimensions(Class<?> c) {
    Check.notNull(c);
    int x = 0;
    for (; c.isArray(); c = c.getComponentType()) {
      ++x;
    }
    return x;
  }

  /**
   * Creates a new {@code ArrayType} instance. The {@code baseType} argument is
   * allowed to be an array class, but the base type recorded by the instance will
   * then be the base type of <i>that</i> array class, and the number of dimensions
   * will be added to the provided number of dimensions. The provided number of
   * dimensions may then even be zero or negative (as long as the sum of the
   * dimensions remains positive).
   *
   * <blockquote><pre>{@code
   * ArrayType at = new ArrayType(float[][].class, -1);
   * Class<?> c = at.toClass(); // float[].class
   * }</pre></blockquote>
   *
   * @param baseType The base type of the array (i.e. the lowest-level component
   *     type)
   * @param dimensions The number of dimensions
   */
  public ArrayType(Class<?> baseType, int dimensions) {
    Check.notNull(baseType, "baseType")
        .isNot(sameAs(), void.class, "illegal base type: void", '\0');
    if (baseType.isArray()) {
      ArrayType tmp = forClass(baseType);
      this.baseType = tmp.baseType;
      int x = dimensions + tmp.dimensions;
      this.dimensions = Check.that(x, "dimensions").is(positive()).ok();
    } else {
      Check.that(dimensions, "dimensions").is(positive());
      this.baseType = baseType;
      this.dimensions = dimensions;
    }
  }

  /**
   * Returns the {@link Class} object corresponding to this {@code ArrayType}.
   *
   * @return the {@link Class} object corresponding to an array with the base type
   *     and dimensionality of this instance.
   */
  public Class<?> toClass() {
    return toClass(baseType, dimensions);
  }

  /**
   * Returns the {@link Class} object corresponding to an array with the specified
   * base type and with the number of dimensions this instance
   *
   * @return An array type with the specified base type and with the number of
   *     dimensions of this instance
   */
  public Class<?> toClass(Class<?> baseType) {
    Class<?> c = Check.notNull(baseType).ok();
    return toClass(baseType, dimensions);
  }

  /**
   * Returns the {@link Class} object corresponding to an array of the boxed version
   * of this instance's base type and with this instance's number of dimensions. So
   * for {@code int[][].class} it would return {@code Integer[][].class}.
   *
   * @return An array type for the boxed version of this instance's base type and
   *     with this instance's number of dimensions
   * @see ClassMethods#box(Class)
   */
  public Class<?> box() {
    return toClass(ClassMethods.box(baseType));
  }

  /**
   * Returns the {@link Class} object corresponding to an array of the unboxed
   * version of this instance's base type and with this instance's number of
   * dimensions.
   *
   * @return An array type for the boxed version of this instance's base type and
   *     with this instance's number of dimensions
   * @see ClassMethods#unbox(Class)
   */
  public Class<?> unbox() {
    return toClass(ClassMethods.unbox(baseType));
  }

  /**
   * Returns the {@link Class} object corresponding to an array with this instance's
   * dimensionality and the specified number of dimensions this instance
   *
   * @return The {@link Class} object corresponding to an array with this instance's
   *     dimensionality and the specified number of dimensions this instance
   */
  public Class<?> toClass(int dimensions) {
    Check.that(dimensions, "dimensions").is(positive());
    return toClass(baseType, dimensions);
  }

  /**
   * Returns the {@code ArrayType} for the boxed version of the base type.
   *
   * @return The {@code ArrayType} for the boxed version of the base type
   */
  public ArrayType boxed() {
    return new ArrayType(ClassMethods.box(baseType), dimensions);
  }

  /**
   * Returns the {@code ArrayType} for the unboxed version of the base type.
   *
   * @return The {@code ArrayType} for the unboxed version of the base type
   */
  public ArrayType unboxed() {
    return new ArrayType(ClassMethods.unbox(baseType), dimensions);
  }

  /**
   * Returns the simple class name of the array type encoded by this {@code
   * ArrayType}, in a format that is a bit easier on the eye than what you get from
   * {@link Class#getSimpleName()}. For example the returned value for {@code
   * int[][].class} would be "int[][]".
   *
   * @return The simple class name of the array type encoded by this {@code
   *     ArrayType}
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(baseType().getSimpleName());
    for (int i = 0; i < dimensions; ++i) {
      sb.append("[]");
    }
    return sb.toString();
  }

  /**
   * Returns the fully-qualified class name of the array type encoded by this {@code
   * ArrayType}, in a format that is a bit easier on the eye than what you get from
   * {@link Class#getName()}. For example, the returned value for {@code
   * String[][].class} would be "java.lang.String[][]".
   *
   * @return The fully-qualified class name of the array type encoded by this {@code
   *     ArrayType}
   */
  public String arrayClassName() {
    StringBuilder sb = new StringBuilder(baseType().getName());
    for (int i = 0; i < dimensions; ++i) {
      sb.append("[]");
    }
    return sb.toString();
  }

  private static ArrayType create(Class<?> arrayClass) {
    return cache.updateAndGet(tuple -> {
      if (tuple.first() == arrayClass) {
        return tuple;
      }
      var c = arrayClass.getComponentType();
      int i = 1;
      for (; c.isArray(); c = c.getComponentType()) {
        ++i;
      }
      return Tuple2.of(arrayClass, new ArrayType(c, i));
    }).second();
  }

  private static Class<?> toClass(Class<?> baseType, int dimensions) {
    Class<?> c = baseType;
    for (int i = 0; i < dimensions; ++i) {
      c = c.arrayType();
    }
    return c;
  }

}
