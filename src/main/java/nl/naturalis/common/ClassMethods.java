package nl.naturalis.common;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.check.CommonChecks;

import java.util.*;
import java.util.stream.IntStream;

import static nl.naturalis.common.CollectionMethods.swapAndFreeze;
import static nl.naturalis.common.check.CommonChecks.array;
import static nl.naturalis.common.check.CommonChecks.asObj;

/**
 * Methods for inspecting types.
 *
 * @author Ayco Holleman
 */
public final class ClassMethods {

  // primitive-to-wrapper
  private static final Map<Class<?>, Class<?>> P2W = Map.of(double.class,
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
   * @return Whether the 1st argument is an instance of the 2nd argument
   */
  public static boolean isA(Object instance, Class<?> type) {
    Check.notNull(instance, "instance");
    Check.notNull(type, "type");
    return type.isInstance(instance);
  }

  /**
   * Tests whether the first class extends or implements of the second class. In
   * other words, whether it extends or implements the second class. In case you keep
   * forgetting what "assignable from" even means. Equivalent to
   * <code>class1.isAssignableFrom(class0)</code>.
   *
   * @param class0 The class or interface you are interested in
   * @param class1 The class or interface to compare it against
   * @return {@code true} if the first class is a subtype of the second class; {@code
   *     false} otherwise
   * @see CommonChecks#subtypeOf()
   */
  public static boolean isSubtype(Class<?> class0, Class<?> class1) {
    Check.notNull(class0, "class0");
    Check.notNull(class1, "class1");
    return class1.isAssignableFrom(class0);
  }

  /**
   * Tests whether the first class is a supertype of the second class. In other
   * words, whether it is extended or implemented by the second class. Equivalent to
   * <code>class0.isAssignableFrom(class1)</code>.
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
   * @param clazz The class to test
   * @return Whether the specified class is one of the primitive number classes
   */
  public static boolean isPrimitiveNumber(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isPrimitive() && clazz != boolean.class && clazz != char.class;
  }

  /**
   * Returns whether the specified class is a {@link #isPrimitiveNumber primitive
   * number} or extends {@link Number} (including {@code Number} itself).
   *
   * @param clazz The class to test
   * @return Whether the specified class is a {@link #isPrimitiveNumber primitive
   *     number} or extends {@code Number}
   */
  public static boolean isNumerical(Class<?> clazz) {
    Check.notNull(clazz);
    return Number.class.isAssignableFrom(clazz) || (clazz.isPrimitive()
                                                        && clazz != boolean.class
                                                        && clazz != char.class);
  }

  /**
   * Returns whether the specified object is a primitive array or a {@code Class}
   * object representing a primitive array. Defers to {@link
   * #isPrimitiveArray(Class)} if the specified object is a {@code Class} object.
   *
   * @param obj The object to test
   * @return Whether it is a primitive array class
   */
  public static boolean isPrimitiveArray(Object obj) {
    Check.notNull(obj);
    if (obj.getClass() == Class.class) {
      return isPrimitiveArray((Class<?>) obj);
    }
    return isPrimitiveArray(obj.getClass());
  }

  /**
   * Returns whether the class is a primitive array class.
   *
   * @param clazz The class to test
   * @return Whether it is a primitive array class
   */
  public static boolean isPrimitiveArray(Class<?> clazz) {
    return clazz.isArray() && clazz.getComponentType().isPrimitive();
  }

  /**
   * Returns whether the specified object is a primitive number array or a {@code
   * Class} object representing a primitive number array. Defers to {@link
   * #isPrimitiveNumberArray(Class)} if the specified object is a {@code Class}
   * object.
   *
   * @param obj The object to test
   * @return Whether it is a primitive array class
   */
  public static boolean isPrimitiveNumberArray(Object obj) {
    Check.notNull(obj);
    if (obj.getClass() == Class.class) {
      return isPrimitiveArray((Class<?>) obj);
    }
    return isPrimitiveArray(obj.getClass());
  }

  /**
   * Returns whether the specified class represents an array of primitive numbers.
   *
   * @param clazz The class to test
   * @return Whether it is a primitive array class
   */
  public static boolean isPrimitiveNumberArray(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isArray()
        && clazz.getComponentType().isPrimitive()
        && clazz.getComponentType() != boolean.class
        && clazz.getComponentType() != char.class;
  }

  /**
   * Returns whether the specified object is an array of a {@link #isNumerical
   * numerical type} or a {@code Class} object representing an array of a numerical
   * type.
   *
   * @param obj The object to test
   * @return Whether it is an array of a {@code #isNumerical numerical type} or a
   *     {@code Class} object representing an array of a numerical type
   */
  public static boolean isNumericalArray(Object obj) {
    Check.notNull(obj);
    if (obj.getClass() == Class.class) {
      return isNumericalArray((Class<?>) obj);
    }
    return isNumericalArray(obj.getClass());
  }

  /**
   * Returns whether the specified class represents an array of a {@link #isNumerical
   * numerical type}.
   *
   * @param clazz The class to test
   * @return whether it represents an array of a numerical type
   */
  public static boolean isNumericalArray(Class<?> clazz) {
    Check.notNull(clazz);
    Class<?> c;
    return clazz.isArray()
        && (Number.class.isAssignableFrom(c = clazz.getComponentType())
                || (c.isPrimitive() && c != boolean.class && c != char.class));
  }

  /**
   * Returns whether the specified is one of the primitive wrapper classes.
   *
   * @param clazz The class to test
   * @return Whether the specified is one of the primitive wrapper classes
   */
  public static boolean isWrapper(Class<?> clazz) {
    return W2P.containsKey(clazz);
  }

  /**
   * Returns whether instances of the first class will be auto-unboxed into instances
   * of the second class. This method does not check whether the first class actually
   * is a wrapper class and the second a primitive class. If either is not true, the
   * method will return {@code false}.
   *
   * @param classToTest The class to test
   * @param primitiveClass Supposedly a primitively class
   * @return Whether instances of the first class will be auto-unboxed into instances
   *     of the second class
   */
  public static boolean isAutoUnboxedAs(Class<?> classToTest,
      Class<?> primitiveClass) {
    Check.notNull(classToTest);
    Check.notNull(primitiveClass);
    return P2W.get(primitiveClass) == classToTest;
  }

  /**
   * Returns whether instances of the first class will be auto-unboxed into instances
   * of the second class. This method does not check whether the first class actually
   * is a wrapper class and the second a primitive class. If either is not true, the
   * method will return {@code false}.
   *
   * @param classToTest The class to test
   * @param wrapperClass Supposedly a wrapper class
   * @return Whether instances of the first class will be auto-unboxed into instances
   *     of the second class
   */
  public static boolean isAutoBoxedAs(Class<?> classToTest, Class<?> wrapperClass) {
    Check.notNull(classToTest);
    Check.notNull(wrapperClass);
    return W2P.get(wrapperClass) == classToTest;
  }

  /**
   * If the specified class is a primitive type, returns the corresponding primitive
   * wrapper class, else the specified class itself.
   *
   * @param clazz The (primitive) class
   * @return The corresponding wrapper class
   */
  public static Class<?> box(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isPrimitive() ? P2W.get(clazz) : clazz;
  }

  /**
   * If the specified class is a primitive wrapper class, returns the corresponding
   * primitive type, else the specified class itself.
   *
   * @param clazz The (wrapper) class
   * @return The corresponding primitive class
   */
  public static Class<?> unbox(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isPrimitive() ? clazz : W2P.getOrDefault(clazz, clazz);
  }

  /**
   * Returns the superclasses of the specified class up to, and including {@code
   * Object.class}.
   *
   * @param clazz The class for which to get the superclasses
   * @return The superclasses of the specified class.
   */
  public static List<Class<?>> getAncestors(Class<?> clazz) {
    Check.notNull(clazz).isNot(asObj(Class::isInterface),
        "Cannot get ancestors for interface types");
    List<Class<?>> l = new ArrayList<>(5);
    for (Class<?> x = clazz.getSuperclass(); x != null; x = x.getSuperclass()) {
      l.add(x);
    }
    return l;
  }

  public static int countAncestors(Class<?> clazz) {
    Check.notNull(clazz).isNot(asObj(Class::isInterface),
        "Cannot get ancestors for interface types");
    int i = 0;
    for (Class<?> x = clazz.getSuperclass(); x != null; x = x.getSuperclass()) {
      ++i;
    }
    return i;
  }

  /**
   * Returns the entire interface hierarchy, both "horizontal" amd "vertical",
   * associated with specified class or interface. Returns an empty set if the
   * argument is a top-level interface, or if the class is a regular class that does
   * not implement any interface (either directly, or indirectly)and neither directly
   * nor indirectly through its superclasses).
   *
   * @param clazz The {@code Class} object for which to retrieve the interface
   *     hierarchy
   * @return The interface hierarchy for the specified {@code Class} object
   */
  public static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
    Check.notNull(clazz);
    Set<Class<?>> interfaces = new LinkedHashSet<>();
    collectInterfaces(interfaces, clazz);
    for (Class<?> c = clazz.getSuperclass(); c != null; c = c.getSuperclass()) {
      collectInterfaces(interfaces, c);
    }
    return interfaces;
  }

  private static void collectInterfaces(Set<Class<?>> interfaces, Class<?> clazz) {
    Class<?>[] myInterfaces = clazz.getInterfaces();
    interfaces.addAll(List.of(myInterfaces));
    for (Class<?> c : myInterfaces) {
      collectInterfaces(interfaces, c);
    }
  }

  /**
   * Returns a prettified version of the fully-qualified class name. If the argument
   * is an array, {@link #arrayClassName(Object)} is returned, else {@code
   * obj.getClass().getName()}.
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
   * is an array class, {@link #arrayClassName(Object)} is returned, else {@code
   * obj.getClass().getName()}.
   *
   * @param clazz The class whose name to return
   * @return The class name
   */
  public static String className(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isArray() ? arrayClassName(clazz) : clazz.getName();
  }

  /**
   * Returns a prettified version of the simple class name. If the argument is an
   * array, {@link #arrayClassSimpleName(Object)} is returned, else {@code
   * obj.getClass().getSimpleName()}.
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
   * array class, {@link #arrayClassSimpleName(Class)} is returned, else {@code
   * obj.getClass().getSimpleName()}.
   *
   * @param clazz The class whose ame to return
   * @return The class name
   */
  public static String simpleClassName(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isArray() ? arrayClassSimpleName(clazz) : clazz.getSimpleName();
  }

  /**
   * Returns a description for the specified array that is a bit easier on they eye
   * than what you get from {@link Class#getName()}. For example {@code
   * arrayClassName(new String[0][0])} will return {@code java.lang.String[][]}.
   *
   * @param array The array
   * @return A more intuitive description of the array's type
   * @throws IllegalArgumentException If the argument is not an array
   */
  public static String arrayClassName(Object array) {
    return Check.notNull(array).ok(a -> arrayClassName(a.getClass()));
  }

  /**
   * Returns a description for the specified array class that is a bit easier on they
   * eye than what you get from {@link Class#getName()}.
   *
   * @param arrayClass The array type
   * @return A more intuitive description of the array type
   * @throws IllegalArgumentException If the argument is not an array class
   */
  public static String arrayClassName(Class<?> arrayClass) {
    Check.notNull(arrayClass).is(array());
    Class<?> c = arrayClass.getComponentType();
    int i = 0;
    for (; c.isArray(); c = c.getComponentType()) {
      ++i;
    }
    StringBuilder sb = new StringBuilder(c.getName());
    IntStream.rangeClosed(0, i).forEach(x -> sb.append("[]"));
    return sb.toString();
  }

  /**
   * Returns a short description for the specified array that is a bit easier on they
   * eye than what you get from {@link Class#getSimpleName()}. For example {@code
   * arrayClassName(new String[0][0])} will return {@code String[][]}.
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
    Check.notNull(arrayClass).is(array());
    Class<?> c = arrayClass.getComponentType();
    int i = 0;
    for (; c.isArray(); c = c.getComponentType()) {
      ++i;
    }
    StringBuilder sb = new StringBuilder(c.getSimpleName());
    IntStream.rangeClosed(0, i).forEach(x -> sb.append("[]"));
    return sb.toString();
  }

}
