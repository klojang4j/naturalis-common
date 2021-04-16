package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.instanceOf;
import static nl.naturalis.common.check.CommonChecks.keyIn;
import static nl.naturalis.common.check.CommonChecks.neverNull;
import static nl.naturalis.common.invoke.InvokeException.typeMismatch;
import static nl.naturalis.common.invoke.NoSuchPropertyException.noSuchProperty;

/**
 * Read properties from a predetermined type of Java beans. This class uses the {@code
 * java.lang.invoke} package in stead of reflection to read bean properties. Although this class
 * uses {@link MethodHandle} instances to extract values from the bean, it still uses reflection to
 * identify the getter methods on the bean class. Therefore if you use this class from within a Java
 * module you must still open the module to the naturalis-common module.
 *
 * @author Ayco Holleman
 * @param <T> The type of the bean
 */
public class BeanReader<T> {

  private final Class<? super T> beanClass;
  private final Map<String, GetInvoker> readInfo;

  /**
   * Creates a {@code BeanReader} for the specified class. The specified properties will be included
   * rather than excluded from the list of properties you intend to read. Strict naming conventions
   * will be applied to what qualifies as a getter. See {@link
   * ClassMethods#getPropertyNameFromGetter(java.lang.reflect.Method, boolean)}.
   *
   * @param beanClass The bean class
   * @param properties The properties you intend to read (may be {@code null} or zero-length)
   */
  public BeanReader(Class<? super T> beanClass, String... properties) {
    this(beanClass, true, false, properties);
  }

  /**
   * Creates a {@code BeanReader} for the specified class. Strict naming conventions will be applied
   * to what qualifies what counts as a getter. See {@link
   * ClassMethods#getPropertyNameFromGetter(java.lang.reflect.Method, boolean)}.
   *
   * @param beanClass The bean class
   * @param exclude Whether to exclude or include the specified properties
   * @param properties The properties you intend to read (may be {@code null} or zero-length)
   */
  public BeanReader(Class<? super T> beanClass, boolean exclude, String... properties) {
    this(beanClass, true, exclude, properties);
  }

  /**
   * Creates a {@code BeanReader} for the specified class and the specified properties on that
   * class. If you intend to use this {@code BeanReader} to repetitively to read just one or two
   * properties from bulky bean types, explicitly specifying the properties you intend to read might
   * make the {@code BeanReader} slightly more efficient. Otherwise you may specify {@code null} or
   * a zero-length array to indicate that you intend to read all properties.
   *
   * <p>Specifying non-existent properties (names that cannot be traced back to getters) has no
   * effect. It will not cause an exception to be thrown. Instead they will be ignored silently.
   *
   * @param beanClass The bean class
   * @param strictNaming Whether or not to apply strict naming conventions to what qualifies as a
   *     getter. See {@link ClassMethods#getPropertyNameFromGetter(java.lang.reflect.Method,
   *     boolean)}.
   * @param exclude Whether to exclude or include the specified properties
   * @param properties The properties you intend to read (may be {@code null} or zero-length)
   */
  public BeanReader(
      Class<? super T> beanClass, boolean strictNaming, boolean exclude, String... properties) {
    this.beanClass = Check.notNull(beanClass, "beanClass").ok();
    Check.that(properties, "properties").is(neverNull());
    if (properties == null || properties.length == 0) {
      this.readInfo = GetInvokerFactory.INSTANCE.getInvokers(beanClass, strictNaming);
    } else {
      GetInvokerFactory gif = GetInvokerFactory.INSTANCE;
      Map<String, GetInvoker> info = new HashMap<>(gif.getInvokers(beanClass, strictNaming));
      if (exclude) {
        info.keySet().removeAll(Set.of(properties));
      } else {
        info.keySet().retainAll(Set.of(properties));
      }
      this.readInfo = Map.copyOf(info);
    }
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
  public <U> U read(T bean, String property) throws NoSuchPropertyException {
    Check.notNull(bean, "bean");
    Check.notNull(property, "property");
    Check.on(s -> typeMismatch(this, bean), bean).is(instanceOf(), beanClass);
    Check.on(s -> noSuchProperty(bean, property), property).is(keyIn(), readInfo);
    try {
      return (U) readInfo.get(property).getter.invoke(bean);
    } catch (Throwable t) {
      throw ExceptionMethods.uncheck(t);
    }
  }

  /**
   * Returns which type of this {@code BeanReader} can read.
   *
   * @return Which type of this {@code BeanReader} can read
   */
  public Class<? super T> getBeanClass() {
    return beanClass;
  }

  /**
   * Returns all properties of the bean class that will actually be read by this {@code BeanReader}.
   *
   * @return All properties of the bean class that will actually be read by this {@code BeanReader}
   */
  public Set<String> getUsedProperties() {
    return readInfo.keySet();
  }
}
