package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.check.CommonChecks;

import java.util.*;
import java.util.stream.IntStream;

import static nl.naturalis.common.CollectionMethods.swapAndFreeze;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * Methods for inspecting types.
 *
 * @author Ayco Holleman
 */
public final class ClassMethods {

  // primitive-to-wrapper
  private static final Map<Class<?>, Class<?>> P2W = Map.of(
      double.class,
      Double.class,
      float.class,
      Float.class,
      long.class,
      Long.class,
      int.class,
      Integer.class,
      char.class,
      Character.class,
      short.class,
      Short.class,
      byte.class,
      Byte.class,
      boolean.class,
      Boolean.class);

  // wrapper-to-primitive
  private static final Map<Class<?>, Class<?>> W2P = swapAndFreeze(P2W);

  private static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = Map.of(int.class,
      0,
      boolean.class,
      Boolean.FALSE,
      double.class,
      0D,
      long.class,
      0L,
      float.class,
      0F,
      short.class,
      (short) 0,
      byte.class,
      (byte) 0,
      char.class,
      '\0');

  private ClassMethods() {
    throw new UnsupportedOperationException();
  }

  /**
   * Performs a brute-force cast to {@code <R>} of the specified object. Mainly meant
   * to be used as a method reference. The argument is allowed to be {@code null}.
   *
   * @param obj The object whose type to cast
   * @param <T> The type of the object
   * @param <R> The type to case it to
   * @return An instance of type {@code <R>}
   */
  @SuppressWarnings({"unchecked"})
  public static <T, R> R cast(T obj) {
    return (R) obj;
  }

  /**
   * Alias for {@link Class#isInstance(Object)}.
   *
   * @param instance The object to test
   * @param type The class or interface to test the object against
   * @return whether the 1st argument is an instance of the 2nd argument
   */
  public static boolean isA(Object instance, Class<?> type) {
    Check.notNull(instance, "instance");
    Check.notNull(type, "type");
    return type.isInstance(instance);
  }

  /**
   * Tests whether the first class is the same as, or a subtype of the second class.
   * In other words, whether it extends or implements the second class. In case you
   * keep forgetting what "assignable from" even means. Equivalent to
   * <code>class1.isAssignableFrom(class0)</code>.
   *
   * @param class0 The class or interface you are interested in
   * @param class1 The class or interface to compare it against
   * @return {@code true} if the first class is a subtype of the second class;
   *     {@code false} otherwise
   * @see CommonChecks#subtypeOf()
   */
  public static boolean isSubtype(Class<?> class0, Class<?> class1) {
    Check.notNull(class0, "class0");
    Check.notNull(class1, "class1");
    return class1.isAssignableFrom(class0);
  }

  /**
   * Tests whether the first class is the same as, or a supertype of the second
   * class. In other words, whether it is extended or implemented by the second
   * class. Equivalent to <code>class0.isAssignableFrom(class1)</code>.
   *
   * @param class0 The class or interface you are interested in
   * @param class1 The class or interface to compare it against
   * @return {@code true} if the first class is a supertype of the second class;
   *     {@code false} otherwise
   * @see CommonChecks#supertypeOf()
   */
  public static boolean isSupertype(Class<?> class0, Class<?> class1) {
    Check.notNull(class0, "class0");
    Check.notNull(class1, "class1");
    return class0.isAssignableFrom(class1);
  }

  /**
   * Returns whether the specified class is one of the primitive number classes. Note
   * that this does not include {@code char.class}, just like {@link Character} does
   * not extend {@link Number}.
   *
   * @param clazz the class to test
   * @return whether the specified class is one of the primitive number classes
   * @see NumberMethods#isWrapper(Class)
   */
  public static boolean isPrimitiveNumber(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isPrimitive() && clazz != boolean.class && clazz != char.class;
  }

  /**
   * Returns {@code true} if the specified object is an array of a primitive type, or
   * a {@code Class} object representing an array of a primitive type.
   *
   * @param obj The object to test
   * @return {@code true} if the specified object is an array of a primitive type, or
   *     a {@code Class} object representing an array of a primitive type
   */
  public static boolean isPrimitiveArray(Object obj) {
    if (obj instanceof Class c) {
      return isPrimitiveArray(c);
    }
    return obj != null && isPrimitiveArray(obj.getClass());
  }

  /**
   * Returns {@code true} if the specified type represents an array of a primitive
   * type.
   *
   * @param clazz the class to test
   * @return {@code true} if the specified type represents an array of a primitive
   *     type
   */
  public static boolean isPrimitiveArray(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isArray() && clazz.getComponentType().isPrimitive();
  }

  /**
   * Returns {@code true} if the specified type represents an array with a primitive
   * type as its deepest-level component type. So this method will return
   * {@code true} not just for {@code int[]}, but also for {@code int[][]},
   * {@code int[][][]}, etc.
   *
   * @param clazz the class to test
   * @return {@code true} if the specified type represents an array with a primitive
   *     type as its deepest-level component type
   * @see ArrayType
   */
  public static boolean isDeeplyPrimitiveArray(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isArray() && ArrayType.forClass(clazz).baseType().isPrimitive();
  }

  /**
   * Returns {@code true} if the specified class is one of the primitive wrapper
   * classes. Use {@link NumberMethods#isWrapper(Class)} to establish whether the
   * class is a primitive <i>number</i> wrapper.
   *
   * @param clazz the class to test
   * @return {@code true} if the specified class is one of the primitive wrapper
   *     classes
   */
  public static boolean isWrapper(Class<?> clazz) {
    return W2P.containsKey(clazz);
  }

  /**
   * Returns {@code true} if first argument is a primitive wrapper type and the
   * second argument is the corresponding primitive type. If the first class is not a
   * wrapper class (like {@code Integer.class}), or the second class is not a
   * primitive type (like {@code int.class}), this method returns {@code false}.
   *
   * @param classToTest the class to test
   * @param primitiveClass the class to compare it with (supposedly, but not
   *     necessarily, a primitive type)
   * @return whether instances of the first class will be auto-unboxed into instances
   *     of the second class
   */
  public static boolean isAutoUnboxedAs(Class<?> classToTest,
      Class<?> primitiveClass) {
    Check.notNull(classToTest);
    Check.notNull(primitiveClass);
    return P2W.get(primitiveClass) == classToTest;
  }

  /**
   * Returns {@code true} if first argument is a primitive type and the second
   * argument is the corresponding wrapper class. If the first class is not a
   * primitive type (like {@code int.class}), or the second class is not a wrapper
   * class (like {@code Integer.class}), this method returns {@code false}.
   *
   * @param classToTest the class to test
   * @param wrapperClass the class to compare it with (supposedly, but not
   *     necessarily, a primitive wrapper type)
   * @return whether instances of the first class will be auto-unboxed into instances
   *     of the second class
   */
  public static boolean isAutoBoxedAs(Class<?> classToTest, Class<?> wrapperClass) {
    Check.notNull(classToTest);
    Check.notNull(wrapperClass);
    return W2P.get(wrapperClass) == classToTest;
  }

  /**
   * Returns the wrapper class corresponding to the specified class <i>if</i> it is a
   * primitive type; else the class itself is returned.
   *
   * @param clazz the (primitive) class
   * @return The corresponding wrapper class
   */
  public static Class<?> box(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isPrimitive() ? P2W.get(clazz) : clazz;
  }

  /**
   * Returns the primitive type corresponding to the specified class <i>if</i> it is
   * a wrapper class; else the class itself is returned.
   *
   * @param clazz the (wrapper) class
   * @return The corresponding primitive class
   */
  public static Class<?> unbox(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isPrimitive() ? clazz : W2P.getOrDefault(clazz, clazz);
  }

  /**
   * Returns the superclasses of the specified class up to, and including
   * {@code Object.class}.
   *
   * @param clazz the class for which to get the superclasses
   * @return The superclasses of the specified class.
   */
  public static List<Class<?>> getAncestors(Class<?> clazz) {
    Check.notNull(clazz).isNot(Class::isInterface,
        "Cannot get ancestors for interface type {0}", clazz);
    List<Class<?>> l = new ArrayList<>(5);
    for (Class<?> x = clazz.getSuperclass(); x != null; x = x.getSuperclass()) {
      l.add(x);
    }
    return l;
  }

  public static int countAncestors(Class<?> clazz) {
    Check.notNull(clazz)
        .isNot(
            Class::isInterface,
            "Cannot get ancestors for interface types");
    int i = 0;
    for (Class<?> x = clazz.getSuperclass(); x != null; x = x.getSuperclass()) {
      ++i;
    }
    return i;
  }

  /**
   * Returns the entire interface hierarchy, both "horizontal" and "vertical",
   * associated with specified class or interface. Returns an empty set if the
   * argument is a top-level interface, or if the class is a regular class that does
   * not implement any interface (directly, or indirectly via its superclass).
   *
   * @param clazz the {@code Class} object for which to retrieve the interface
   *     hierarchy
   * @return The interface hierarchy for the specified {@code Class} object
   */
  public static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
    Check.notNull(clazz);
    Set<Class<?>> bucket = new LinkedHashSet<>();
    collectInterfaces(clazz, bucket);
    for (Class<?> c = clazz.getSuperclass(); c != null; c = c.getSuperclass()) {
      collectInterfaces(c, bucket);
    }
    return bucket;
  }

  private static void collectInterfaces(Class<?> clazz,
      Set<Class<?>> bucket) {
    Class<?>[] myInterfaces = clazz.getInterfaces();
    bucket.addAll(Arrays.asList(myInterfaces));
    for (Class<?> c : myInterfaces) {
      collectInterfaces(c, bucket);
    }
  }

  /**
   * Returns a prettified version of the fully-qualified class name. If the argument
   * is an array, {@link #arrayClassName(Object)} is returned, else
   * {@code obj.getClass().getName()}.
   *
   * @param obj The object whose class name to return
   * @return The class name
   */
  public static String className(Object obj) {
    Check.notNull(obj);
    if (obj.getClass() == Class.class) {
      return className((Class<?>) obj);
    }
    return className(obj.getClass());
  }

  /**
   * Returns a prettified version of the fully-qualified class name. If the argument
   * is an array class, {@link #arrayClassName(Object)} is returned, else
   * {@code obj.getClass().getName()}.
   *
   * @param clazz the class whose name to return
   * @return The class name
   */
  public static String className(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isArray() ? arrayClassName(clazz) : clazz.getName();
  }

  /**
   * Returns a prettified version of the simple class name. If the argument is an
   * array, {@link #arrayClassSimpleName(Object)} is returned, else
   * {@code obj.getClass().getSimpleName()}.
   *
   * @param obj The object whose class name to return
   * @return The class name
   */
  public static String simpleClassName(Object obj) {
    Check.notNull(obj);
    if (obj.getClass() == Class.class) {
      return simpleClassName((Class<?>) obj);
    }
    return simpleClassName(obj.getClass());
  }

  /**
   * Returns a prettified version of the simple class name. If the argument is an
   * array class, {@link #arrayClassSimpleName(Class)} is returned, else
   * {@code obj.getClass().getSimpleName()}.
   *
   * @param clazz the class whose ame to return
   * @return The class name
   */
  public static String simpleClassName(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isArray() ? arrayClassSimpleName(clazz) : clazz.getSimpleName();
  }

  /**
   * Returns a description for the specified array that is a bit easier on the eye
   * than what you get from {@link Class#getName()}. For example
   * {@code arrayClassName(new String[0][0])} will return
   * {@code java.lang.String[][]}.
   *
   * @param array The array
   * @return A more intuitive description of the array's type
   * @throws IllegalArgumentException If the argument is not an array
   */
  public static String arrayClassName(Object array) {
    return Check.notNull(array).ok(a -> arrayClassName(a.getClass()));
  }

  /**
   * Returns a description for the specified array class that is a bit easier on the
   * eye than what you get from {@link Class#getName()}.
   *
   * @param arrayClass The array type
   * @return A more intuitive description of the array type
   * @throws IllegalArgumentException If the argument is not an array class
   */
  public static String arrayClassName(Class<?> arrayClass) {
    var info = ArrayType.forClass(arrayClass);
    var sb = new StringBuilder(info.baseType().getName());
    IntStream.range(0, info.dimensions()).forEach(x -> sb.append("[]"));
    return sb.toString();
  }

  /**
   * Returns a short description for the specified array that is a bit easier on they
   * eye than what you get from {@link Class#getSimpleName()}. For example
   * {@code arrayClassName(new String[0][0])} will return {@code String[][]}.
   *
   * @param array The array
   * @return The simple name of the array type
   * @throws IllegalArgumentException If the argument is not an array
   */
  public static String arrayClassSimpleName(Object array) {
    return Check.notNull(array).ok(a -> arrayClassSimpleName(a.getClass()));
  }

  /**
   * Returns a short description for the specified array class that is a bit easier
   * on they eye than what you get from {@link Class#getSimpleName()}.
   *
   * @param arrayClass The array type
   * @return An intuitive description of the array type
   * @throws IllegalArgumentException If the argument is not an array class
   */
  public static String arrayClassSimpleName(Class<?> arrayClass) {
    var info = ArrayType.forClass(arrayClass);
    var sb = new StringBuilder(info.baseType().getSimpleName());
    IntStream.range(0, info.dimensions()).forEach(x -> sb.append("[]"));
    return sb.toString();
  }

  /**
   * Returns zero, cast to the appropriate type, for primitive types; {@code null}
   * for any other type.
   *
   * @param <T> The type of the class
   * @param type The class for which to retrieve the default value
   * @return The default value
   */
  @SuppressWarnings("unchecked")
  public static <T> T getTypeDefault(Class<T> type) {
    return Check.notNull(type, "type").isNot(sameAs(), void.class).ok().isPrimitive()
        ? (T) PRIMITIVE_DEFAULTS.get(type)
        : null;
  }

}
