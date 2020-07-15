package nl.naturalis.common;

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
   * Tests whether the provided object is an array of primitives.
   * 
   * @param obj
   * @return
   */
  public static boolean isPrimitiveArray(Object obj) {
    return Check.notNull(obj, "obj") instanceof int[]
        || obj instanceof double[]
        || obj instanceof long[]
        || obj instanceof byte[]
        || obj instanceof char[]
        || obj instanceof float[]
        || obj instanceof short[];
  }

}
