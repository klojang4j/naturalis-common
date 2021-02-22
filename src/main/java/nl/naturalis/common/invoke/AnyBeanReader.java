package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.util.Map;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.keyIn;
import static nl.naturalis.common.invoke.NoSuchPropertyException.noSuchProperty;

/**
 * Reads properties of any type of bean. Slightly less efficient than {@link BeanReader} if the
 * {@link #read(Object, String)} method is provided with continuously changing types of objects.
 * This class uses the {@code java.lang.invoke} package in stead of reflection to read bean
 * properties. Although this class uses {@link MethodHandle} instances to extract values from the
 * bean, it still uses reflection to identify the getter methods on the bean class. Therefore if you
 * use this class from within a Java module you must still open the module to the naturalis-common
 * module.
 *
 * @author Ayco Holleman
 * @param <T> The type of the bean
 */
public class AnyBeanReader {

  private Class<?> mruClass;
  private Map<String, ReadInfo> mruInfo;

  /** Creates a new {@code AnyBeanReader} */
  public AnyBeanReader() {}

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
      mruInfo = ReadInfoFactory.INSTANCE.getReadInfo(clazz);
    }
    Check.on(s -> noSuchProperty(bean, property), property).is(keyIn(), mruInfo);
    try {
      return (U) mruInfo.get(property).getter.invoke(bean);
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
