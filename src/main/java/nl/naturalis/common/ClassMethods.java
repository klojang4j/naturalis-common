package nl.naturalis.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.Checks.*;

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
   * Tests whether the 1st argument is an instance of the 2nd argument.
   *
   * @param objectToTest
   * @param superOrInterface
   * @return
   */
  public static boolean isA(Object objectToTest, Class<?> superOrInterface) {
    Check.notNull(objectToTest, "objectToTest");
    Check.notNull(superOrInterface, "superOrInterface");
    return superOrInterface.isAssignableFrom(objectToTest.getClass());
  }

  /**
   * Tests whether the provided object is an array of primitives without using reflection.
   * Null-safe.
   *
   * @param obj
   * @return
   */
  public static boolean isPrimitiveArray(Object obj) {
    return obj != null
        && (obj instanceof int[]
            || obj instanceof double[]
            || obj instanceof boolean[]
            || obj instanceof byte[]
            || obj instanceof long[]
            || obj instanceof char[]
            || obj instanceof float[]
            || obj instanceof short[]);
  }

  /**
   * Returns a description of the type of the specified array, including the fully-qualified name of
   * the array's component type. Meant to be a bit less harsh than the description produced by
   * {@code array.toString()}
   *
   * @param array The array
   * @return The simple name of the array type
   */
  public static String getArrayTypeName(Object array) {
    Check.notNull(array, "obj").and(isArray());
    return array.getClass().getComponentType().getName() + "[]";
  }

  /**
   * Returns a description of the type of the specified array, including the simple name of the
   * array's component type. Meant to be a bit less harsh than the description produced by {@code
   * array.toString()}
   *
   * @param array The array
   * @return The simple name of the array type
   */
  public static String getArrayTypeSimpleName(Object array) {
    Check.notNull(array, "obj").and(isArray());
    return array.getClass().getComponentType().getSimpleName() + "[]";
  }

  @SuppressWarnings("rawtypes")
  private static final HashMap<Tuple<String, Class>, Field> fields = new HashMap<>();

  /**
   * Returns a {@code Field} object corresponding to the provided field name within the provided
   * object's type. The fields {@code accessible} flag will be set to {@code true}.
   *
   * @param obj
   * @param fieldName
   * @return
   */
  @SuppressWarnings("rawtypes")
  public static Field getField(Object obj, String fieldName) {
    Tuple<String, Class> key = Tuple.tuple(fieldName, obj.getClass());
    Field field = fields.get(key);
    if (field == null) {
      if (!fields.containsKey(key)) {
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
    }
    return field;
  }
}
