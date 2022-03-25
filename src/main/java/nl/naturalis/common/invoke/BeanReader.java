package nl.naturalis.common.invoke;

import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.invoke.IncludeExclude.INCLUDE;
import static nl.naturalis.common.invoke.InvokeException.typeMismatch;
import static nl.naturalis.common.invoke.NoSuchPropertyException.noSuchProperty;

/**
 * A dynamic bean reader class. This class uses the {@code java.lang.invoke} package instead of
 * reflection to read bean properties. Yet it still uses reflection to identify the getter methods
 * of the bean class. Therefore, if you use this class from within a Java module you must still open
 * the module to the naturalis-common module.
 *
 * @param <T> The type of the bean
 * @author Ayco Holleman
 */
public final class BeanReader<T> {

  private final Class<? super T> beanClass;
  private final Map<String, Getter> getters;

  /**
   * Creates a {@code BeanReader} for the specified properties of the specified class. You can
   * optionally specify an array of properties that you intend to read.  If you specify a
   * zero-length array all properties will be readable. Strict naming conventions will be applied
   * regarding what which methods qualify as getters.
   *
   * @param beanClass The bean class
   * @param properties The properties to be included/excluded
   */
  public BeanReader(Class<? super T> beanClass, String... properties) {
    this(beanClass, true, INCLUDE, properties);
  }

  /**
   * Creates a {@code BeanReader} for the specified properties of the specified class. You can
   * optionally specify an array of properties that you intend to read.  If you specify a
   * zero-length array all properties will be readable. Strict naming conventions will be applied
   * regarding what which methods qualify as getters.
   *
   * @param beanClass The bean class
   * @param includeExclude Whether to include or exclude the specified properties
   * @param properties The properties to be included/excluded
   */
  public BeanReader(Class<? super T> beanClass,
      IncludeExclude includeExclude,
      String... properties) {
    this(beanClass, true, includeExclude, properties);
  }

  /**
   * Creates a {@code BeanReader} for the specified properties of the specified class. You can
   * optionally specify an array of properties that you intend to read. If you specify a zero-length
   * array all properties will be readable. If you intend to use this {@code BeanReader} to
   * repetitively read just one or two properties from bulky bean types, explicitly specifying the
   * properties you intend to read might make the {@code BeanReader} more efficient.
   *
   * <p><i>Specifying one or more non-existent properties will not cause an exception to be
   * thrown.</i> They will be quietly ignored.
   *
   * @param beanClass The bean class
   * @param strictNaming If {@code false} all methods with a zero-length parameter list and a
   *     non-{@code void} return type will be regarded as getters; otherwise strict naming
   *     conventions will be applied regarding what which methods qualify as getters.
   * @param includeExclude Whether to include or exclude the specified properties
   * @param properties The properties to be included/excluded
   */
  public BeanReader(
      Class<? super T> beanClass,
      boolean strictNaming,
      IncludeExclude includeExclude,
      String... properties) {
    Check.notNull(beanClass);
    Check.notNull(includeExclude);
    Check.that(properties, "properties").is(deepNotNull());
    this.beanClass = beanClass;
    this.getters = getGetters(strictNaming, includeExclude, properties);
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
  @SuppressWarnings("unchecked")
  public <U> U read(T bean, String property) throws NoSuchPropertyException {
    Check.notNull(bean, "bean").is(instanceOf(), beanClass, () -> typeMismatch(this, bean));
    Check.notNull(property, "property");
    Getter getter = getters.get(property);
    Check.that(getter).is(notNull(), () -> noSuchProperty(bean, property));
    try {
      return (U) getter.read(bean);
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
  public Set<String> getIncludedProperties() {
    return getters.keySet();
  }

  /**
   * Returns the {@link Getter getters} used by the {@code BeanReader} to read bean properties. The
   * returned {@code Map} maps the name of a property to the {@code Getter} used to read it.
   *
   * @return All getters used to read bean properties.
   */
  public Map<String, Getter> getIncludedGetters() {
    return getters;
  }

  private Map<String, Getter> getGetters(boolean strictNaming, IncludeExclude ie, String[] props) {
    Map<String, Getter> tmp = GetterFactory.INSTANCE.getGetters(beanClass, strictNaming);
    if (props.length == 0) {
      return tmp;
    }
    Map<String, Getter> copy = new HashMap<>(tmp);
    if (ie.isExclude()) {
      copy.keySet().removeAll(Set.of(props));
    } else {
      copy.keySet().retainAll(Set.of(props));
    }
    return Map.copyOf(copy);
  }

}
