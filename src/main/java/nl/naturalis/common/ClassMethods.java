package nl.naturalis.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import nl.naturalis.common.check.Check;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.stream.Collectors.joining;
import static nl.naturalis.common.ArrayMethods.isOneOf;
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

  public static final boolean isPrimitiveNumberClass(Class<?> classToTest) {
    return isOneOf(
        classToTest, int.class, double.class, long.class, byte.class, float.class, short.class);
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

  public static boolean isPrimitiveClassOf(Class<?> primitiveClass, Class<?> classToTest) {
    Check.notNull(primitiveClass);
    Check.notNull(classToTest);
    return p2w.get(primitiveClass) == classToTest;
  }

  public static boolean isWrapperClassOf(Class<?> wrapperClass, Class<?> classToTest) {
    Check.notNull(wrapperClass);
    Check.notNull(classToTest);
    return w2p.get(wrapperClass) == classToTest;
  }

  public static Class<?> getWrapperClass(Class<?> primitiveClass) {
    return Check.notNull(primitiveClass)
        .is(keyIn(), p2w, "Not a primitive class: %s", primitiveClass)
        .ok(p2w::get);
  }

  public static Class<?> getPrimitiveClass(Class<?> wrapperClass) {
    return Check.that(wrapperClass)
        .is(keyIn(), w2p, "Not a wrapper class: %s", wrapperClass)
        .ok(w2p::get);
  }

  /**
   * Returns {@link #getArrayTypeName(Object)} if the argument is an array, else {@code
   * obj.getClass().getName()}.
   *
   * @param obj The object whose class name to return
   * @return The class name
   */
  public static String getPrettyClassName(Object obj) {
    Check.notNull(obj);
    if (obj.getClass() == Class.class) {
      return getPrettyClassName((Class<?>) obj);
    }
    return getPrettyClassName(obj.getClass());
  }

  /**
   * Returns {@link #getArrayTypeName(Object)} if the argument is an array type, else {@code
   * clazz.getName()}.
   *
   * @param clazz The class whose name to return
   * @return The class name
   */
  public static String getPrettyClassName(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isArray() ? getArrayTypeName(clazz) : clazz.getName();
  }

  /**
   * Returns {@link #getArrayTypeSimpleName(Object)} if the argument is an array, else {@link
   * Class#getSimpleName()}.
   *
   * @param obj The object whose class name to return
   * @return The class name
   */
  public static String getPrettySimpleClassName(Object obj) {
    Check.notNull(obj);
    if (obj.getClass() == Class.class) {
      return getSimpleClassName((Class<?>) obj);
    }
    return getSimpleClassName(obj.getClass());
  }

  public static String getSimpleClassName(Class<?> clazz) {
    Check.notNull(clazz);
    return clazz.isArray() ? getArrayTypeSimpleName(clazz) : clazz.getSimpleName();
  }

  /**
   * Returns a less arcane description of the type of the specified array. For example {@code
   * getArrayTypeName(new String[0][0])} will return {@code java.lang.String[][]}, which is a lot
   * friendlier than what comes back from {@code (new String[0][0]).getClass().getName()}.
   *
   * @param array The array
   * @return An intuitive description of the array's type
   */
  public static String getArrayTypeName(Object array) {
    return Check.notNull(array).ok(a -> getArrayTypeName(a.getClass()));
  }

  /**
   * Returns a less arcane description of the type of the specified array type.
   *
   * @param arrayClass The array type
   * @return An intuitive description of the array type
   */
  public static String getArrayTypeName(Class<?> arrayClass) {
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
   * Returns a less arcane description of the type of the specified array. For example {@code
   * getArrayTypeName(new String[0][0])} will return {@code String[][]}.
   *
   * @param array The array
   * @return The simple name of the array type
   */
  public static String getArrayTypeSimpleName(Object array) {
    Check.notNull(array).is(array());
    return array.getClass().getComponentType().getSimpleName() + "[]";
  }

  /**
   * Returns a less arcane description of the type of the specified array type.
   *
   * @param arrayClass The array type
   * @return An intuitive description of the array type
   */
  public static String getArrayTypeSimpleName(Class<?> arrayClass) {
    Check.notNull(arrayClass).is(array());
    return arrayClass.getClass().getComponentType().getSimpleName() + "[]";
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
    String rt = getSimpleClassName(m.getReturnType());
    String clazz = getPrettyClassName(m.getDeclaringClass());
    String params =
        Arrays.stream(m.getParameterTypes())
            .map(ClassMethods::getSimpleClassName)
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
