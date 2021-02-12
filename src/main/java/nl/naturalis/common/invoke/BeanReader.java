package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.keyIn;
import static nl.naturalis.common.check.CommonChecks.noneNull;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.invoke.NoSuchPropertyException.*;
import static nl.naturalis.common.invoke.InvokeException.*;

/**
 * A bean reader class that uses the {@code java.lang.invoke} package in stead of reflection to read
 * bean properties. Although this class uses {@link MethodHandle} instances to extract values from
 * the bean, it still uses reflection to identify the getter methods on the bean class. Therefore if
 * you use this class within a Java module you must open the module to the naturalis-common module.
 *
 * @author Ayco Holleman
 * @param <T> The type of the bean
 */
public class BeanReader<T> {

  private final Class<? super T> beanClass;
  private final Map<String, ReadInfo> readInfo;

  /**
   * Creates a {@code BeanReader} for the specified class.
   *
   * @param beanClass The bean class
   */
  public BeanReader(Class<? super T> beanClass) {
    this.beanClass = Check.notNull(beanClass, "beanClass").ok();
    this.readInfo = ReadInfoFactory.INSTANCE.getReadInfo(beanClass);
  }

  /**
   * Creates a {@code BeanReader} for the specified class and the specified properties of that
   * class. If you intend to use this {@code BeanReader} to repetitively read just one or two
   * properties from a lot of bean instances, this makes the {@code BeanReader} more efficient.
   *
   * @param beanClass The bean class
   * @param properties The properties you are interested in
   */
  public BeanReader(Class<? super T> beanClass, String... properties) {
    this(beanClass, false, properties);
  }

  /**
   * Creates a {@code BeanReader} for the specified class and the specified properties of that
   * class. If you intend to use this {@code BeanReader} to repetitively read just one or two
   * properties from a lot of bean instances, this makes the {@code BeanReader} more efficient.
   *
   * @param beanClass The bean class
   * @param exclude Whether to exclude or include the specified properties
   * @param properties The properties you are, or are not interested in
   */
  public BeanReader(Class<? super T> beanClass, boolean exclude, String... properties) {
    this.beanClass = Check.notNull(beanClass, "beanClass").ok();
    Check.that(properties, "properties").is(noneNull());
    Map<String, ReadInfo> info = new HashMap<>(ReadInfoFactory.INSTANCE.getReadInfo(beanClass));
    if (exclude) {
      info.keySet().removeAll(Set.of(properties));
    } else {
      info.keySet().retainAll(Set.of(properties));
    }
    this.readInfo = Map.copyOf(info);
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
  public Object read(T bean, String property) throws NoSuchPropertyException {
    Check.notNull(bean, "bean");
    Check.notNull(property, "property");
    Check.with(s -> typeMismatch(this, bean), bean).is(instanceOf(), beanClass);
    Check.with(s -> noSuchProperty(property), property).is(keyIn(), readInfo);
    try {
      return readInfo.get(property).getter.invoke(bean);
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
}
