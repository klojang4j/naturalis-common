package nl.naturalis.common;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.IntStream;
import nl.naturalis.common.check.Check;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.stream.Collectors.joining;
import static nl.naturalis.common.CollectionMethods.swapAndFreeze;
import static nl.naturalis.common.check.CommonChecks.array;
import static nl.naturalis.common.check.CommonChecks.keyIn;
/**
 * Methods for inspecting types.
 *
 * @author Ayco Holleman
 */
public class ClassMethods {

  private ClassMethods() {}

  /**
   * Tests whether the 1st argument extends or implements the 2nd argument. In case you keep
   * forgetting what "assign from" even means. Equivalent to <code>
   * superOrInterface.isAssignableFrom(classToTest)</code>.
   *
   * @param classToTest The class to test
   * @param superOrInterface The class or interface to test the class against
   * @return Whether the 1st argument extends or implements the 2nd argument
   */
  public static boolean isA(Class<?> classToTest, Class<?> superOrInterface) {
    Check.notNull(classToTest, "classToTest");
    Check.notNull(superOrInterface, "superOrInterface");
    return superOrInterface.isAssignableFrom(classToTest);
  }

  /**
   * Tests whether the 1st argument is an instance of the 2nd argument. Equivalent to <code>
   * superOrInterface.isInstance(objectToTest)</code>. Note that, since this method is overloaded
   * with {@code Class} as the type of the first parameter, you cannot and should not use this
   * method to figure out if a {@code Class} object is an instance of some interface or super class.
   *
   * @param objectToTest The object to test
   * @param superOrInterface The class or interface to test the object against
   * @return Whether the 1st argument is an instance of the 2nd argument
   */
  public static boolean isA(Object objectToTest, Class<?> superOrInterface) {
    Check.notNull(objectToTest, "objectToTest");
    Check.notNull(superOrInterface, "superOrInterface");
    return superOrInterface.isInstance(objectToTest);
  }

  /**
   * Returns whether or not the specified class is a "regular" class. Returns {@code true} if the
   * specified class is Object.class or if has a super class, is not an enum and not an array.
   *
   * @param clazz The class to test
   * @return Whether or not the specified class is a "regular" class
   */
  public static boolean isRegular(Class<?> clazz) {
    return clazz == Object.class
        || (clazz.getSuperclass() != null && !isA(clazz, Enum.class) && !clazz.isArray());
  }

  public static final boolean isPrimitiveNumberClass(Class<?> classToTest) {
    return Set.of(int.class, double.class, long.class, byte.class, float.class, short.class)
        .contains(classToTest);
  }

  /**
   * Returns whether or not the argument is an array of primitives without using reflection.
   * Null-safe.
   *
   * @param arg The object to test
   * @return Whether or not it is an array of primitives
   */
  public static boolean isPrimitiveArray(Object arg) {
    Class<?> c;
    return arg != null
        && ((c = arg.getClass()) == int[].class
            || c == boolean[].class
            || c == byte[].class
            || c == double[].class
            || c == long[].class
            || c == float[].class
            || c == char[].class
            || c == short[].class);
  }

  // primitive-to-wrapper
  private static final Map<Class<?>, Class<?>> p2w =
      Map.of(
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

  // wrappper-to-primitve
  private static final Map<Class<?>, Class<?>> w2p = swapAndFreeze(p2w);

  public static boolean isPrimitive(Class<?> classToTest) {
    return classToTest.isPrimitive();
  }

  public static boolean isWrapper(Class<?> classToTest) {
    return w2p.keySet().contains(classToTest);
  }

  public static boolean isAutoUnboxedAs(Class<?> classToTest, Class<?> primitiveClass) {
    Check.notNull(classToTest);
    Check.notNull(primitiveClass);
    return p2w.get(primitiveClass) == classToTest;
  }

  public static boolean isAutoBoxedAs(Class<?> classToTest, Class<?> wrapperClass) {
    Check.notNull(classToTest);
    Check.notNull(wrapperClass);
    return w2p.get(wrapperClass) == classToTest;
  }

  public static Class<?> box(Class<?> primitiveClass) {
    return Check.notNull(primitiveClass)
        .is(keyIn(), p2w, "Not a primitive class: %s", primitiveClass)
        .ok(p2w::get);
  }

  public static Class<?> unbox(Class<?> wrapperClass) {
    return Check.that(wrapperClass)
        .is(keyIn(), w2p, "Not a wrapper class: %s", wrapperClass)
        .ok(w2p::get);
  }

  /**
   * Returns true if and only if both {@code Class} objects represent interfaces and the first
   * interface directly or indirectly extends the the second interface.
   *
   * @param classToTest The {@code Class} object to test
   * @param baseClass The {@code Class} object to test it against
   * @return Whether or not the first argument is an interface that directly or indirectly extends
   *     the second interface
   */
  public static boolean hasAncestor(Class<?> classToTest, Class<?> baseClass) {
    Check.notNull(classToTest, "classToTest");
    Check.notNull(baseClass, "baseClass");
    for (Class<?> c = classToTest.getSuperclass(); c != null; c = c.getSuperclass()) {
      if (c == baseClass) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if and only if both {@code Class} objects represent interfaces and the first
   * interface directly or indirectly extends the the second interface.
   *
   * @param classToTest The {@code Class} object to test
   * @param superInterface The {@code Class} object to test it against
   * @return Whether or not the first argument is an interface that directly or indirectly extends
   *     the second interface
   */
  public static boolean hasAncestorInterface(Class<?> classToTest, Class<?> superInterface) {
    Check.notNull(classToTest, "classToTest");
    Check.notNull(superInterface, "superInterface");
    if (!classToTest.isInterface()
        || !superInterface.isInterface()
        || classToTest.getInterfaces().length == 0) {
      return false;
    }
    return getAllInterfaces(classToTest).contains(superInterface);
  }

  public static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
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
   * Returns a prettified version of the fully-qualified class name. If the argument is an array,
   * {@link #arrayClassName(Object)} is returned, else {@code obj.getClass().getName()}.
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
   * Returns a prettified version of the fully-qualified class name. If the argument is an array
   * class, {@link #arrayClassName(Object)} is returned, else {@code obj.getClass().getName()}.
   *
   * @param clazz The class whose name to return
   * @return The class name
   */
  public static String className(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isArray() ? arrayClassName(clazz) : clazz.getName();
  }

  /**
   * Returns a prettified version of the simple class name. If the argument is an array, {@link
   * #arrayClassSimpleName(Object)} is returned, else {@code obj.getClass().getSimpleName()}.
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
   * Returns a prettified version of the simple class name. If the argument is an array class,
   * {@link #arrayClassSimpleName(Class)} is returned, else {@code obj.getClass().getSimpleName()}.
   *
   * @param obj The object whose class name to return
   * @return The class name
   */
  public static String simpleClassName(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isArray() ? arrayClassSimpleName(clazz) : clazz.getSimpleName();
  }

  /**
   * Returns a description for the specified array that is a bit easier on they eye than what you
   * get from {@link Class#getName()}. For example {@code arrayClassName(new String[0][0])} will
   * return {@code java.lang.String[][]}.
   *
   * @param array The array
   * @return A more intuitive description of the array's type
   * @throws IllegalArgumentException If the argument is not an array
   */
  public static String arrayClassName(Object array) {
    return Check.notNull(array).ok(a -> arrayClassName(a.getClass()));
  }

  /**
   * Returns a description for the specified array class that is a bit easier on they eye than what
   * you get from {@link Class#getName()}.
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
   * Returns a short description for the specified array that is a bit easier on they eye than what
   * you get from {@link Class#getSimpleName()}. For example {@code arrayClassName(new
   * String[0][0])} will return {@code String[][]}.
   *
   * @param array The array
   * @return The simple name of the array type
   * @throws IllegalArgumentException If the argument is not an array
   */
  public static String arrayClassSimpleName(Object array) {
    Check.notNull(array).is(array());
    return array.getClass().getComponentType().getSimpleName() + "[]";
  }

  /**
   * Returns a short description for the specified array class that is a bit easier on they eye than
   * what you get from {@link Class#getSimpleName()}.
   *
   * @param arrayClass The array type
   * @return An intuitive description of the array type
   * @throws IllegalArgumentException If the argument is not an array class
   */
  public static String arrayClassSimpleName(Class<?> arrayClass) {
    Check.notNull(arrayClass).is(array());
    return arrayClass.getComponentType().getSimpleName() + "[]";
  }

  /**
   * Returns all methods of the specified bean class than can be identified as setters. See {@link
   * #getPropertyNameFromGetter(Method, boolean)} for an explanation of the {@code strict}
   * parameter.
   *
   * @param beanClass The bean class from which to extract the getter methods
   * @return The getters on the specified bean class
   */
  public static List<Method> getGetters(Class<?> beanClass, boolean strict) {
    Check.notNull(beanClass, "beanClass");
    Method[] methods = beanClass.getMethods();
    List<Method> getters = new ArrayList<>();
    for (Method m : methods) {
      if (isStatic(m.getModifiers())) {
        continue;
      } else if (m.getParameterCount() != 0) {
        continue;
      } else if (m.getReturnType() == void.class) {
        continue;
      } else if (strict && !validGetterName(m)) {
        continue;
      }
      getters.add(m);
    }
    return getters;
  }

  /**
   * Returns all methods of the specified bean class that can be identified as setters.
   *
   * @see #getPropertyNameFromSetter(Method)
   * @param beanClass The bean class from which to extract the setter methods
   * @return The setters on the specified bean class
   */
  public static List<Method> geSetters(Class<?> beanClass) {
    Check.notNull(beanClass, "beanClass");
    Method[] methods = beanClass.getMethods();
    List<Method> setters = new ArrayList<>();
    for (Method m : methods) {
      if (isStatic(m.getModifiers())) {
        continue;
      } else if (m.getParameterCount() != 1) {
        continue;
      } else if (m.getReturnType() != void.class) {
        continue;
      } else if (!validSetterName(m)) {
        continue;
      }
      setters.add(m);
    }
    return setters;
  }

  /**
   * Returns the property name corresponding to the specified method, which is assumed to be a
   * getter. If the method cannot be identified as a getter, an {@link IllegalArgumentException} is
   * thrown. If {@code strict} equals {@code false}, any method that has a zero-length parameter
   * list and that does not return {@code void} is taken to be a getter. Otherwise the JavaBeans
   * naming conventions are followed strictly, with the exception that methods returning a {@link
   * Boolean} (rather than {@code boolean}) are allowed to have a name starting with "is".
   *
   * @param m The method from which to extract a property name
   * @param strict Whether or not to be strict as regards the method name
   * @return The name of the property corresponding to the method
   */
  public static String getPropertyNameFromGetter(Method m, boolean strict) {
    String n = m.getName();
    if (m.getParameterCount() == 0 && m.getReturnType() != void.class) {
      if ((m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class)
          && n.length() > 2
          && n.startsWith("is")
          && isUpperCase(n.charAt(2))) {
        return extractName(n, 2);
      } else if (n.length() > 3 && n.startsWith("get") && isUpperCase(n.charAt(3))) {
        return extractName(n, 3);
      }
      if (!strict) {
        return n;
      }
    }
    throw notAProperty(m, true);
  }

  /**
   * Returns the property name corresponding to the specified method, which is assumed to be a
   * setter. If the method cannot be identified as a setter, an {@link IllegalArgumentException} is
   * thrown.
   *
   * @param m The method from which to extract a property name
   * @return The name of the property corresponding to the method
   */
  public static String getPropertyNameFromSetter(Method m) {
    String n = m.getName();
    if (m.getParameterCount() == 1
        && m.getReturnType() == void.class
        && n.length() > 3
        && n.startsWith("set")
        && isUpperCase(n.charAt(3))) {
      return extractName(n, 3);
    }
    throw notAProperty(m, false);
  }

  private static String extractName(String n, int from) {
    StringBuilder sb = new StringBuilder(n.length() - 3);
    sb.append(n.substring(from));
    sb.setCharAt(0, toLowerCase(sb.charAt(0)));
    return sb.toString();
  }

  private static IllegalArgumentException notAProperty(Method m, boolean asGetter) {
    String fmt = "Method %s %s(%s) in class %s is not a %s";
    String rt = simpleClassName(m.getReturnType());
    String clazz = className(m.getDeclaringClass());
    String params =
        Arrays.stream(m.getParameterTypes())
            .map(ClassMethods::simpleClassName)
            .collect(joining(", "));
    String type = asGetter ? "getter" : "setter";
    String msg = String.format(fmt, rt, m.getName(), params, clazz, type);
    return new IllegalArgumentException(msg);
  }

  private static boolean validGetterName(Method m) {
    String n = m.getName();
    if (n.length() > 4 && n.startsWith("get") && isUpperCase(n.charAt(3))) {
      return true;
    }
    if (n.length() > 3 && n.startsWith("is") && isUpperCase(n.charAt(2))) {
      return m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class;
    }
    return false;
  }

  private static boolean validSetterName(Method m) {
    String n = m.getName();
    return n.length() > 3 && n.startsWith("set") && isUpperCase(n.charAt(3));
  }
}
