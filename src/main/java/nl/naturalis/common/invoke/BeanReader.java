package nl.naturalis.common.invoke;

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
 * Reads properties from a predetermined type of JavaBean. This class uses the {@code
 * java.lang.invoke} package in stead of reflection to read bean properties. Yet it still uses
 * reflection to identify getter methods on the bean class. Therefore if you use this class from
 * within a Java module you must still open the module to the naturalis-common module.
 *
 * <p>This class caches relevant data about the bean class such that after the first time you create
 * an instance of a {@code BeanReader} for a particular bean class, subsequent instantiations are
 * essentially for free (no matter which constructor you use).
 *
 * @author Ayco Holleman
 * @param <T> The type of the bean
 */
public class BeanReader<T> {

  private final Class<? super T> beanClass;
  private final Map<String, Getter> getters;

  /**
   * Creates a {@code BeanReader} for the specified properties of the specified class. Strict naming
   * conventions will be applied to what qualifies as a getter. See {@link
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
   * make the {@code BeanReader} more efficient. Otherwise you may specify {@code null} or a
   * zero-length array to indicate that you intend to read all properties.
   *
   * <p>Specifying non-existent properties (names that do not correspond to getter methods) will
   * <i>not</i> result in an exception being thrown.has no effect. Instead they will just be
   * silently ignored.
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
      this.getters = GetterFactory.INSTANCE.getGetters(beanClass, strictNaming);
    } else {
      GetterFactory gf = GetterFactory.INSTANCE;
      Map<String, Getter> copy = new HashMap<>(gf.getGetters(beanClass, strictNaming));
      if (exclude) {
        copy.keySet().removeAll(Set.of(properties));
      } else {
        copy.keySet().retainAll(Set.of(properties));
      }
      this.getters = Map.copyOf(copy);
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
    Check.on(s -> noSuchProperty(bean, property), property).is(keyIn(), getters);
    try {
      return (U) getters.get(property).getMethod().invoke(bean);
    } catch (Throwable t) {
      throw ExceptionMethods.uncheck(t);
    }
  }

  /**
   * Returns the type of the objects this {@code BeanReader} can read.
   *
   * @return The type of the objects {@code BeanReader} can read
   */
  public Class<? super T> getBeanClass() {
    return beanClass;
  }

  /**
   * Returns the bean properties that will actually be read by this {@code BeanReader}.
   *
   * @return The bean properties that will actually be read by this {@code BeanReader}
   */
  public Set<String> getUsedProperties() {
    return getters.keySet();
  }

  /**
   * Returns the {@link Getter getters} used by the {@code BeanReader} to read bean properties. The
   * returned {@code Map} maps the name of a property to the {@code Getter} used to read it.
   *
   * @return All getters used to read bean properties.
   */
  public Map<String, Getter> getUsedGetters() {
    return getters;
  }
}
