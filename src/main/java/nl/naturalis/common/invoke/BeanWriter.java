package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.keyIn;
import static nl.naturalis.common.check.CommonChecks.neverNull;
import static nl.naturalis.common.check.CommonChecks.notNull;

/**
 * A bean writer class that uses the {@code java.lang.invoke} package in stead of reflection to set
 * bean properties. Although this class uses {@link MethodHandle} instances to extract values from
 * the bean, it still uses reflection to identify the getter methods on the bean class. Therefore if
 * you use this class within a Java module you must open the module to the naturalis-common module.
 *
 * @author Ayco Holleman
 * @param <T> The type of the bean
 */
public class BeanWriter<T> {

  private final Map<String, WriteInfo> writeInfo;

  /**
   * Creates a {@code BeanReader} for the specified class.
   *
   * @param beanClass The bean class
   */
  public BeanWriter(Class<T> beanClass) {
    writeInfo = Check.notNull(beanClass).ok(WriteInfoFactory.INSTANCE::getWriteInfo);
  }

  /**
   * Creates a {@code BeanReader} for the specified class and the specified properties of that
   * class. If you intend to use this {@code BeanReader} to repetitively read just one or two
   * properties from the provided bean instances, this makes the {@code BeanReader} more efficient.
   *
   * @param beanClass The bean class
   * @param properties The properties you are interested in
   */
  public BeanWriter(Class<T> beanClass, String... properties) {
    this(beanClass, false, properties);
  }

  /**
   * Creates a {@code BeanReader} for the specified class and the specified properties of that
   * class. If you intend to use this {@code BeanReader} to repetitively read just one or two
   * properties from the provided bean instances, this makes the {@code BeanReader} more efficient.
   *
   * @param beanClass The bean class
   * @param exclude Whether to exclude or include the specified properties
   * @param properties The properties you are, or ar not interested in
   */
  public BeanWriter(Class<T> beanClass, boolean exclude, String... properties) {
    Check.notNull(beanClass, "beanClass");
    Check.that(properties, "properties").is(neverNull());
    Map<String, WriteInfo> copy = new HashMap<>(WriteInfoFactory.INSTANCE.getWriteInfo(beanClass));
    if (exclude) {
      copy.keySet().removeAll(Set.of(properties));
    } else {
      copy.keySet().retainAll(Set.of(properties));
    }
    this.writeInfo = Map.copyOf(copy);
  }

  /**
   * Sets the value of the specified property on the specified bean.
   *
   * @param bean The bean instance
   * @param property The property @Param value The value to set it to
   * @throws Throwable Any {@code Throwable} thrown from inside the {@code java.lang.invoke} package
   */
  public void set(T bean, String property, Object value) throws Throwable {
    WriteInfo wi =
        Check.on(s -> new NoSuchPropertyException(property), property)
            .is(notNull())
            .is(keyIn(), writeInfo)
            .ok(writeInfo::get);
    if (wi != null) {
      Check.notNull(bean, "bean");
      wi.setter.invoke(bean, value);
    }
  }
}
