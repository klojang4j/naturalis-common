package nl.naturalis.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import nl.naturalis.common.check.Check;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.stream.Collectors.joining;
import static nl.naturalis.common.check.CommonChecks.array;
/**
 * Methods for inspecting types.
 *
 * @author Ayco Holleman
 */
public class ClassMethods {

  private ClassMethods() {}

  /**
   * Tests whether the 1st argument extends or implements the 2nd argument. In case you keep
   * forgetting who should be the caller and who the callee with <code>Class.isAssignableFrom</code>
   * method.
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
            || c == byte[].class
            || c == double[].class
            || c == char[].class
            || c == long[].class
            || c == float[].class
            || c == boolean[].class
            || c == short[].class);
  }

  /**
   * Returns {@link #getArrayTypeName(Object)} if the argument is an array, else {@code
   * obj.getClass().getName()}.
   *
   * @param obj The object whose class name to return
   * @return The class name
   */
  public static String prettyClassName(Object obj) {
    Check.notNull(obj);
    if (obj.getClass() == Class.class) {
      return prettyClassName((Class<?>) obj);
    }
    return prettyClassName(obj.getClass());
  }

  public static String prettyClassName(Class<?> clazz) {
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
  public static String prettySimpleClassName(Object obj) {
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
   * Returns a friendly description of the type of the specified array. For example {@code
   * getArrayTypeName(new String[0][0])} will return {@code java.lang.String[][]}.
   *
   * @param array The array
   * @return The simple name of the array type
   */
  public static String getArrayTypeName(Object array) {
    return Check.notNull(array).ok(a -> getArrayTypeName(a.getClass()));
  }

  public static String getArrayTypeName(Class<?> clazz) {
    Check.notNull(clazz).is(array());
    Class<?> c = clazz.getComponentType();
    int i = 0;
    for (; c.isArray(); c = c.getComponentType()) {
      ++i;
    }
    StringBuilder sb = new StringBuilder(c.getName());
    IntStream.rangeClosed(0, i).forEach(x -> sb.append("[]"));
    return sb.toString();
  }

  /**
   * Returns a friendly description of the type of the specified array. For example {@code
   * getArrayTypeName(new String[0][0])} will return {@code String[][]}.
   *
   * @param array The array
   * @return The simple name of the array type
   */
  public static String getArrayTypeSimpleName(Object array) {
    Check.notNull(array).is(array());
    return array.getClass().getComponentType().getSimpleName() + "[]";
  }

  public static String getArrayTypeSimpleName(Class<?> arrayClass) {
    Check.notNull(arrayClass).is(array());
    return arrayClass.getClass().getComponentType().getSimpleName() + "[]";
  }

  /**
   * Returns the "getter" methods of the specified bean class.
   *
   * @param beanClass The class from which to retrieve the getters.
   * @param strict Whether or not to include only methods with JavaBean compliant names ({@code
   *     isXyz()} for boolean methods; {@code getXyz()} for other methods). If {@code strict} is
   *     false, all public methods that take no parameters are included in the returned {@code
   *     List}.
   * @return
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
    String fmt = "Method %s %s(%s) in class %s is not a getter";
    String returnType = getSimpleClassName(m.getReturnType());
    String declaringClass = prettyClassName(m.getDeclaringClass());
    String params =
        Arrays.stream(m.getParameterTypes())
            .map(ClassMethods::getSimpleClassName)
            .collect(joining(", "));
    String msg = String.format(fmt, returnType, n, params, declaringClass);
    throw new IllegalArgumentException(msg);
  }

  public static String getPropertyNameFromSetter(Method m) {
    String n = m.getName();
    if (m.getParameterCount() == 1
        && m.getReturnType() == void.class
        && n.length() > 3
        && n.startsWith("set")
        && isUpperCase(n.charAt(3))) {
      return extractName(n, 3);
    }
    String fmt = "Method %s %s(%s) in class %s is not a getter";
    String returnType = getSimpleClassName(m.getReturnType());
    String declaringClass = prettyClassName(m.getDeclaringClass());
    String params =
        Arrays.stream(m.getParameterTypes())
            .map(ClassMethods::getSimpleClassName)
            .collect(joining(", "));
    String msg = String.format(fmt, returnType, n, params, declaringClass);
    throw new IllegalArgumentException(msg);
  }

  private static String extractName(String n, int from) {
    StringBuilder sb = new StringBuilder(n.length() - 3);
    sb.append(n.substring(from));
    sb.setCharAt(0, toLowerCase(sb.charAt(0)));
    return sb.toString();
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
