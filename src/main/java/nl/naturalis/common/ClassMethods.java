package nl.naturalis.common;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Methods for inspecting types.
 *
 * @author Ayco Holleman
 */
public class ClassMethods {

  private ClassMethods() {}

  /**
   * Tests whether the 1st argument extends or implements the 2nd argument. In
   * case you keep forgetting who should be the caller and who the callee with
   * <code>Class.isAssignableFrom</code> method.
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
   * Tests whether the provided object is an array of primitives without using
   * reflection. Null-safe.
   *
   * @param obj
   * @return
   */
  public static boolean isPrimitiveArray(Object obj) {
    return obj != null && (obj instanceof int[]
        || obj instanceof double[]
        || obj instanceof long[]
        || obj instanceof byte[]
        || obj instanceof char[]
        || obj instanceof float[]
        || obj instanceof short[]);
  }

  @SuppressWarnings("rawtypes")
  private static final HashMap<Tuple<String, Class>, VarHandle> varHandles = new HashMap<>();

  /**
   * Returns a {@code VarHandle} object that lets you to retrieve the value of the
   * specified field within the specified object.
   *
   * @param obj
   * @param fieldName
   * @return
   * @throws IllegalAccessException
   */
  @SuppressWarnings("rawtypes")
  public static VarHandle getVarHandle(Object obj, String fieldName) throws IllegalAccessException {
    Tuple<String, Class> key = Tuple.tuple(fieldName, obj.getClass());
    VarHandle vh = varHandles.get(key);
    if (vh == null) {
      if (!varHandles.containsKey(key)) {
        for (Class c = obj.getClass(); c != Object.class; c = c.getSuperclass()) {
          Field fld = Arrays.stream(c.getDeclaredFields())
              .filter(f -> f.getName().equals(fieldName))
              .findFirst().orElse(null);
          if (fld != null) {
            Lookup lookup = MethodHandles.privateLookupIn(c, MethodHandles.lookup());
            vh = lookup.unreflectVarHandle(fld);
            break;
          }
        }
        varHandles.put(key, vh);
      }
    }
    return vh;
  }

}
