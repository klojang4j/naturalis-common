package nl.naturalis.common;

/**
 * Methods for inspecting types.
 *
 * @author Ayco Holleman
 */
public class ClassMethods {

  private ClassMethods() {}

  /**
   * Tests whether <code>what</code> <b>is a</b> subclass or implementation of <code> superOrInterface</code>. Useful if you keep forgetting
   * who should be the caller and who the callee with the <code>Class.isAssignableFrom</code> method.
   * 
   * @param what
   * @param superOrInterface
   * @return
   */
  public static boolean isA(Class<?> what, Class<?> superOrInterface) {
    return superOrInterface.isAssignableFrom(what);
  }

  /**
   * Tests whether <code>obj</code> <b>is a</b> instance of <code>superOrInterface</code>.
   * 
   * @param obj
   * @param superOrInterface
   * @return
   */
  public static boolean isA(Object obj, Class<?> superOrInterface) {
    return superOrInterface.isAssignableFrom(obj.getClass());
  }

}
