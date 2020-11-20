package nl.naturalis.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.stream.IntStream;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.*;

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
   * @param classToTest
   * @param superOrInterface
   * @return
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
  public static String getClassName(Object obj) {
    Class<?> c = Check.notNull(obj).ok(Object::getClass);
    return c.isArray() ? getArrayTypeName(obj) : c.getName();
  }

  /**
   * Returns {@link #getArrayTypeSimpleName(Object)} if the argument is an array, else {@code
   * obj.getClass().getSimpleName()}.
   *
   * @param obj The object whose class name to return
   * @return The class name
   */
  public static String getSimpleClassName(Object obj) {
    Class<?> c = Check.notNull(obj).ok(Object::getClass);
    return c.isArray() ? getArrayTypeSimpleName(obj) : c.getSimpleName();
  }

  /**
   * Returns a friendly description of the type of the specified array. For example {@code
   * getArrayTypeName(new String[0][0])} will return {@code java.lang.String[][]}.
   *
   * @param array The array
   * @return The simple name of the array type
   */
  public static String getArrayTypeName(Object array) {
    Check.notNull(array).is(array());
    Class<?> c = array.getClass().getComponentType();
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

  @SuppressWarnings("rawtypes")
  private static final HashMap<Tuple<String, Class>, Field> fields = new HashMap<>();

  /**
   * Returns a {@code Field} object corresponding to the specified field name. If found. the field's
   * {@code accessible} flag will be set to {@code true}.
   *
   * @param fieldName The name of the field
   * @param obj An object whose class is supposed to declare or inherit the field
   * @return The {@code Field} or null if the specified field name does not correspond to a {@code
   *     Field} in the specified object's class
   */
  @SuppressWarnings("rawtypes")
  public static Field getField(String fieldName, Object obj) {
    Tuple<String, Class> key = Tuple.tuple(fieldName, obj.getClass());
    Field field = fields.get(key);
    if (field == null) {
      LOOP:
      for (Class c = obj.getClass(); c != Object.class; c = c.getSuperclass()) {
        for (Field f : c.getDeclaredFields()) {
          if (f.getName().equals(fieldName)) {
            f.setAccessible(true);
            fields.put(key, field = f);
            break LOOP;
          }
        }
      }
    }
    return field;
  }
}
