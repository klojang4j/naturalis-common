package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.util.Map;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.keyIn;
import static nl.naturalis.common.invoke.NoSuchPropertyException.noSuchProperty;

/**
 * Reads properties of any type of bean. This makes {@code AnyBeanReader} more versatile than the
 * {@link BeanReader} class. The {@code BeanReader}, on the other hand, allows you to specify
 * up-front the bean properties you are going to read, making it slightly more efficient. Also
 * {@code AnyBeanReader} becomes slightly less efficient if you read from a wide variety of
 * continuously changing bean types. Note, however, that the performance difference should be
 * marginal.
 *
 * <p>Although this class uses {@link MethodHandle} instances to extract values from the bean, it
 * still uses reflection to identify the getter methods on the bean class. Therefore if you use this
 * class from within a Java module you must still open the module to the naturalis-common module.
 *
 * <p>This class is not thread-safe.
 *
 * @author Ayco Holleman
 */
public class AnyBeanReader {

  private final boolean strict;

  private Class<?> mruClass;
  private Map<String, Getter<?>> mruGetters;

  /**
   * Creates a new {@code AnyBeanReader}. Strict naming conventions will be applied to what
   * qualifies as a getter. See {@link
   * ClassMethods#getPropertyNameFromGetter(java.lang.reflect.Method, boolean)}.
   */
  public AnyBeanReader() {
    this(true);
  }

  /**
   * Creates a new {@code AnyBeanReader}.
   *
   * @param strictNaming hether or not to apply strict naming conventions for what counts as a
   *     getter. See {@link ClassMethods#getPropertyNameFromGetter(java.lang.reflect.Method,
   *     boolean)}.
   */
  public AnyBeanReader(boolean strictNaming) {
    this.strict = strictNaming;
  }

  /**
   * Returns the value of the specified property on the specified bean. If the property does not
   * exist a {@link NoSuchPropertyException} is thrown.
   *
   * @param bean The bean instance
   * @param property The property
   * @return Its value
   * @throws NoSuchPropertyException If the specified property does not exist
   */
  public <U> U read(Object bean, String property) throws NoSuchPropertyException {
    Check.notNull(bean, "bean");
    Check.notNull(property, "property");
    Class<?> clazz = bean.getClass();
    if (clazz != mruClass) {
      mruClass = clazz;
      mruGetters = GetterFactory.INSTANCE.getGetters(clazz, strict);
    }
    Check.on(s -> noSuchProperty(bean, property), property).is(keyIn(), mruGetters);
    try {
      return (U) mruGetters.get(property).getMethod().invoke(bean);
    } catch (Throwable t) {
      throw ExceptionMethods.uncheck(t);
    }
  }

  /**
   * Returns the most recently used type of objects processed by the {@link #read(Object, String)}
   * method.
   *
   * @return The most recently used type
   */
  public Class<?> getMRUClass() {
    return mruClass;
  }
}
