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
import static nl.naturalis.common.CollectionMethods.swapUnique;
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
   * superOrInterface.isInstance(objectToTest)</code>.
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
   * Returns {@code true} if both arguments are classes and the first class <i>indirectly</i>
   * extends the second class. Equivalent to <code>classToTest.getSuperclass() == superClass</code>.
   *
   * @param classToTest The class to test
   * @param superClass The parent class (or not) of the class to test
   * @return Whether the 1st argument directly extends the 2nd argument
   */
  public static boolean isSubclass(Class<?> classToTest, Class<?> superClass) {
    return classToTest.getSuperclass() == superClass;
  }

  /**
   * Returns {@code true} if both arguments are classes and the first class <i>indirectly</i>
   * extends the second class. In other words, the first class is not a direct child of the second
   * class, but it is a descendant.
   *
   * @param classToTest The class to test
   * @param baseClass The ancestor (or not) of the class to test
   * @return Whether the 1st argument indirectly extends the 2nd argument
   */
  public static boolean isDescendant(Class<?> classToTest, Class<?> baseClass) {
    Check.notNull(classToTest, "classToTest");
    Check.notNull(baseClass, "baseClass");
    if (baseClass.isInterface() || baseClass.isPrimitive() || baseClass.isAnnotation()) {
      return false;
    }
    for (Class<?> c = classToTest.getSuperclass(); c != null; c = c.getSuperclass()) {
      if (c == baseClass) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns {@code true} if the first argument is a class and the second an interface, and the
   * class has the interface among its declared interfaces.
   *
   * @param classToTest The class to test
   * @param interfaceClass The interface directly implemented (or not) by the class to test
   * @return Whether or not the 1st argument directly implements the 2nd argument
   */
  public static boolean implementsDirectly(Class<?> classToTest, Class<?> interfaceClass) {
    Check.notNull(classToTest, "classToTest");
    Check.notNull(interfaceClass, "interfaceClass");
    return implementsDirectly0(classToTest, interfaceClass);
  }

  /**
   * Returns {@code true} if any of the ancestors of the specified class directly implements the
   * specified interface.
   *
   * @param classToTest The class to test
   * @param interfaceClass The interface directly implemented (or not) by the class to test
   * @return Whether or not the 1st argument indirectly implements the 2nd argument
   */
  public static boolean implementsIndirectly(Class<?> classToTest, Class<?> interfaceClass) {
    Check.notNull(classToTest, "classToTest");
    Check.notNull(interfaceClass, "interfaceClass");
    for (Class<?> c = classToTest.getSuperclass(); c != null; c = c.getSuperclass()) {
      if (implementsDirectly0(classToTest, interfaceClass)) {
        return true;
      }
    }
    return false;
  }

  private static boolean implementsDirectly0(Class<?> c0, Class<?> c1) {
    if (c0.isInterface()
        || c0.isPrimitive()
        || c0.isAnnotation()
        || c0.isEnum()
        || !c1.isInterface()) {
      return false;
    }
    for (Class<?> c : c0.getInterfaces()) {
      if (c == c1) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns {@code true} if the specified class implements the specified interface by virtue of it
   * implementing a more specific interface.
   *
   * @param classToTest The class to test
   * @param ancestorInterface The ancestor interface (or not) of one of the interfaces directly
   *     implemented by the class to test
   * @return Whether or not the specified class implements the specified interface by virtue of it
   *     implementing a more specific interface
   */
  public static boolean implementsByDefault(Class<?> classToTest, Class<?> ancestorInterface) {
    Check.notNull(classToTest, "classToTest");
    Check.notNull(ancestorInterface, "ancestorInterface");
    return implementsByDefault0(classToTest, ancestorInterface);
  }

  /**
   * Returns {@code true} if the specified class implements the specified interface by virtue of one
   * of its ancestors implementing a more specific interface.
   *
   * @param classToTest The class to test
   * @param ancestorInterface The ancestor interface (or not) of an interface implemented by one of
   *     the ancestors of the specified class
   * @return Whether or not the specified class implements the specified interface by virtue of one
   *     of its ancestors implementing a more specific interface
   */
  public static boolean indirectlyImplementsByDefault(
      Class<?> classToTest, Class<?> ancestorInterface) {
    Check.notNull(classToTest, "classToTest");
    Check.notNull(ancestorInterface, "ancestorInterface");
    for (Class<?> c = classToTest.getSuperclass(); c != null; c = c.getSuperclass()) {
      if (implementsByDefault0(c, ancestorInterface)) {
        return true;
      }
    }
    return false;
  }

  private static boolean implementsByDefault0(Class<?> c0, Class<?> c1) {
    if (c0.isInterface()
        || c0.isPrimitive()
        || c0.isAnnotation()
        || c0.isEnum()
        || !c1.isInterface()) {
      return false;
    }
    for (Class<?> c : c0.getInterfaces()) {
      if (isDescendantInterface(c, c1)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns {@code true} if both arguments are interfaces and the 2nd argument is a direct parent
   * of the 1st argument. By implication this method returns <i>false</i> if the two arguments are
   * identical.
   *
   * @param interfaceToTest The interface to test
   * @param parentInterface The parent interface (or not) of the interface to test
   * @return Whether or not the 2nd argument is a direct parent of the 1st argument
   */
  public static boolean isSubInterface(Class<?> interfaceToTest, Class<?> parentInterface) {
    Check.notNull(interfaceToTest, "interfaceToTest");
    Check.notNull(parentInterface, "parentInterface");
    if (!interfaceToTest.isInterface() || !parentInterface.isInterface()) {
      return false;
    }
    for (Class<?> c : interfaceToTest.getInterfaces()) {
      if (c == parentInterface) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns {@code true} if both arguments are interfaces and the 2nd argument is an ancestor of
   * the 1st argument. By implication method this returns <i>false</i> if the two arguments are
   * identical.
   *
   * @param interfaceToTest The interface to test
   * @param parentInterface The ancestor interface (or not) of the interface to test
   * @return Whether or not the 2nd argument is an ancestor of the 1st argument
   */
  public static boolean isDescendantInterface(
      Class<?> interfaceToTest, Class<?> ancestorInterface) {
    return interfaceToTest.isInterface()
        && ancestorInterface.isInterface()
        && interfaceToTest != ancestorInterface
        && isA(interfaceToTest, ancestorInterface);
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

  // primitive-to-wrappper
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
  private static final Map<Class<?>, Class<?>> w2p = Map.copyOf(swapUnique(p2w));

  public static Class<?> getWrapperClass(Class<?> primitiveClass) {
    return Check.that(primitiveClass)
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
   * Returns all methods of the specified bean class than can be identified as setters.
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
    String clazz = prettyClassName(m.getDeclaringClass());
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
