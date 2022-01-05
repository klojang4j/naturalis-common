package nl.naturalis.common.invoke;

import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.Character.toUpperCase;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.invoke.InvokeException.typeMismatch;
import static nl.naturalis.common.invoke.NoSuchPropertyException.noSuchProperty;

/**
 * A bean reader that does not use reflection at all in order to read properties off JavaBeans. This
 * is in contrast to {@link BeanReader} and {@link AnyBeanReader}. The advantage is that you can use
 * this class from within a Java module without having to "open" up the module to the
 * naturalis-common module. The disadvantage is that you get to specify yourself what the names and
 * types are of the properties that you want to read. Thus it takes more work to set up an instance
 * of this class.
 *
 * <p>This class does not use any caching to speed up the instantiation process, so you might want
 * to do some caching yourself.
 *
 * @author Ayco Holleman
 * @param <T> The type of the JavaBeans to be read by the {@code SaveBeanReader}
 */
public class SaveBeanReader<T> {

  /**
   * A {@code Builder} class for {@code SaveBeanReader} instances. You obtain an instance of the
   * {@code Builder} class through
   *
   * @author Ayco Holleman
   * @param <U> The type of the JavaBean for which to create a reader
   */
  public static class Builder<U> {

    private static final String ERR_DUPLICATE = "\"${arg}\" already added";

    private final Map<String, Getter> getters = new HashMap<>();

    private final Class<U> beanClass;

    private Builder(Class<U> beanClass) {
      this.beanClass = beanClass;
    }

    /**
     * Equivalent to {@link #with(Class, String...) with(String.class, properties}.
     *
     * @param properties The property names
     * @return This {@code Builder}
     */
    public Builder<U> withString(String... properties) throws NoSuchMethodException {
      return with(String.class, properties);
    }

    /**
     * Equivalent to {@link #with(Class, String...) with(String.class, properties}.
     *
     * @param properties The property names
     * @return This {@code Builder}
     */
    public Builder<U> withInt(String... properties) throws NoSuchMethodException {
      return with(int.class, properties);
    }

    /**
     * Adds the specified properties, all sharing the specified data type, to the list of properties
     * to be read by the {SaveBeanReader}. Strict JavaBeans naming conventions are applied to
     * construct the name of the corresponding getter method from the property name. Thus, if {@code
     * type} equals {@code boolean.class}, then a property named "active" is assumed to correspond
     * to a getter named "isActive"; in <i>any</i> other case the property is assumed to correspond
     * to a getter named "getActive()".
     *
     * @param type The type of the property (the return type of the corresponding getter)
     * @param properties The property names
     * @return This {@code Builder}
     */
    public Builder<U> with(Class<?> type, String... properties) throws NoSuchMethodException {
      Check.notNull(type, "type");
      Check.notNull(properties, "properties");
      for (String p : properties) {
        add(type, p);
      }
      return this;
    }

    private void add(Class<?> type, String property) throws NoSuchMethodException {
      Check.that(property, "property").isNot(empty()).isNot(keyIn(), getters, ERR_DUPLICATE);
      String name;
      if (type == boolean.class) {
        name = "is" + toUpperCase(property.charAt(0)) + property.substring(1);
      } else {
        name = "get" + toUpperCase(property.charAt(0)) + property.substring(1);
      }
      getters.put(property, new Getter(beanClass, name, type, property));
    }

    /**
     * Adds the getter with the specified name and type to the list of properties to be read by the
     * {SaveBeanReader} produced by this {@code Builder}. This enables you to add methods that
     * functionally behave like getters (they have a non-void return type and zero parameters), but
     * don't obey JavaBeans naming conventions.
     *
     * @param type The return type of the method
     * @param name The verbatim name of the method you want to include (must have a non-void return
     *     type and zero parameters)
     * @return This {@code Builder}
     * @throws NoSuchMethodException If the combination of {@code name} and {@code type} did not
     *     correspond to any method on the bean class.
     */
    public Builder<U> withGetter(Class<?> type, String name) throws NoSuchMethodException {
      Check.that(name).isNot(empty()).isNot(keyIn(), getters, ERR_DUPLICATE);
      Getter getter = new Getter(beanClass, name, type, name);
      getters.put(name, getter);
      return this;
    }

    /**
     * Creates a {@code SaveBeanReader} for the specified bean class.
     *
     * @return A {@code SaveBeanReader} for the specified bean class
     */
    public SaveBeanReader<U> freeze() {
      return new SaveBeanReader<>(beanClass, getters);
    }
  }

  /**
   * Returns a {@code Builder} instance that lets you configure a {@code SaveBeanReader} for
   * instances of the specified bean class.
   *
   * @param <U> The type of the JavaBean for which to configure a {@code SaveBeanReader}
   * @param beanClass The bean class
   * @return A {@code Builder} instance that lets you configure a {@code SaveBeanReader} for
   *     instances of the specified bean class
   */
  public static <U> Builder<U> configure(Class<U> beanClass) {
    return new Builder<>(beanClass);
  }

  private final Class<? super T> beanClass;
  private final Map<String, Getter> getters;

  private SaveBeanReader(Class<? super T> beanClass, Map<String, Getter> getters) {
    this.beanClass = beanClass;
    this.getters = Map.copyOf(getters);
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
    Check.notNull(bean, "bean");
    Check.notNull(property, "property");
    Check.on(s -> typeMismatch(this, bean), bean).is(instanceOf(), beanClass);
    Check.on(s -> noSuchProperty(bean, property), property).is(keyIn(), getters);
    try {
      return (U) getters.get(property).read(bean);
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
}
