package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.keyIn;
import static nl.naturalis.common.check.CommonChecks.noneNull;
import static nl.naturalis.common.check.CommonChecks.notNull;

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

  private final Map<String, ReadInfo> readInfo;

  /**
   * Creates a {@code BeanReader} for the specified class.
   *
   * @param beanClass The bean class
   */
  public BeanReader(Class<T> beanClass) {
    readInfo = Check.notNull(beanClass).ok(ReadInfoFactory.INSTANCE::getReadInfo);
  }

  /**
   * Creates a {@code BeanReader} for the specified class and the specified properties of that
   * class. If you intend to use this {@code BeanReader} to repetitively read just one or two
   * properties from a lot of bean instances, this makes the {@code BeanReader} more efficient.
   *
   * @param beanClass The bean class
   * @param properties The properties you are interested in
   */
  public BeanReader(Class<T> beanClass, String... properties) {
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
  public BeanReader(Class<T> beanClass, boolean exclude, String... properties) {
    Check.notNull(beanClass, "beanClass");
    Check.that(properties, "properties").is(noneNull());
    Map<String, ReadInfo> copy = new HashMap<>(ReadInfoFactory.INSTANCE.getReadInfo(beanClass));
    if (exclude) {
      copy.keySet().removeAll(Set.of(properties));
    } else {
      copy.keySet().retainAll(Set.of(properties));
    }
    this.readInfo = Map.copyOf(copy);
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
    ReadInfo ri =
        Check.with(s -> new NoSuchPropertyException(property), property)
            .is(notNull())
            .is(keyIn(), readInfo)
            .ok(readInfo::get);
    try {
      return Check.notNull(bean, "bean").ok(ri.getter::invoke);
    } catch (Throwable t) {
      throw ExceptionMethods.uncheck(t);
    }
  }
}
