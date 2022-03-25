package nl.naturalis.common.invoke;

import nl.naturalis.common.Morph;
import nl.naturalis.common.TypeConversionException;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingBiFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static nl.naturalis.common.check.CommonChecks.empty;
import static nl.naturalis.common.check.CommonChecks.notNull;
import static nl.naturalis.common.invoke.IncludeExclude.INCLUDE;
import static nl.naturalis.common.invoke.NoSuchPropertyException.noPropertiesSelected;
import static nl.naturalis.common.invoke.NoSuchPropertyException.noSuchProperty;

/**
 * A dynamic bean writer class. This class uses the {@code java.lang.invoke} package instead of
 * reflection to read bean properties. Yet it still uses reflection to identify the getter methods
 * of the bean class. Therefore, if you use this class from within a Java module you must still open
 * the module to the naturalis-common module.
 *
 * @param <T> The type of the bean
 * @author Ayco Holleman
 */
public final class BeanWriter<T> {

  /**
   * Returns a {@code BeanWriter} that allows for "loose typing" of the values to be assigned to the
   * bean's properties. A {@link Morph} object will be used to adapt (if necessary) input values to
   * the type of the destination property. You can optionally specify an array of properties that
   * you intend to write. Specifying a zero-length array means all properties will be writable.
   *
   * @param <U> The type of the bean
   * @param beanClass The bean class
   * @param properties The properties you allow to be written
   * @return A {@code BeanWriter} with "loose typing" behavior
   */
  public static <U> BeanWriter<U> getTolerantWriter(Class<U> beanClass, String... properties) {
    return new BeanWriter<>(beanClass,
        (setter, value) -> Morph.convert(value, setter.getParamType()),
        INCLUDE,
        properties);
  }

  private final Class<T> beanClass;
  private final ThrowingBiFunction<Setter, Object, Object, Throwable> converter;
  private final Map<String, Setter> setters;

  /**
   * Creates a {@code BeanWriter} for the specified class. You can optionally specify an array of
   * properties that you intend to write. If you specify a zero-length array all properties will be
   * writable.
   *
   * @param beanClass The bean class
   * @param properties The properties you allow to be written
   */
  public BeanWriter(Class<T> beanClass, String... properties) {
    this(beanClass, INCLUDE, properties);
  }

  /**
   * Creates a {@code BeanWriter} for the specified class. You can optionally specify an array of
   * properties that you intend to write. If you specify a zero-length array all properties will be
   * writable. Input values will first be converted by the specified conversion function before
   * being assigned to properties.
   *
   * @param beanClass The bean class
   * @param converter A conversion function for input values. The conversion is given the {@link
   *     Setter} for the property to be set as the first argument, and the input value as the second
   *     argument. The return value should be the actual value to assign to the property. The {@code
   *     Setter} should only be used to get the {@link Setter#getProperty() name} and {@link
   *     Setter#getParamType() type} of the property to be set. You <i>should not</i> use it to
   *     actually {@link Setter#write(Object, Object) write} the property, as this will happen
   *     anyhow once the conversion function returns. Unless the conversion fails for extraordinary
   *     reasons, it should throw an {@link IllegalAssignmentException} upon failure. You can again
   *     use the {@code Setter} to {@link Setter#illegalAssignment(Object) generate} the exception.
   * @param properties The properties you allow to be written
   */
  public BeanWriter(
      Class<T> beanClass,
      ThrowingBiFunction<Setter, Object, Object, Throwable> converter,
      String... properties) {
    this(beanClass, converter, INCLUDE, properties);
  }

  /**
   * Creates a {@code BeanWriter} for the specified class. You can optionally specify an array of
   * properties that you intend or do <i>not</i> intend to write. If you specify a zero-length array
   * all properties will be writable.
   *
   * @param beanClass The bean class
   * @param includeExclude Whether to include or exclude the specified properties
   * @param properties The properties to be included/excluded
   */
  public BeanWriter(Class<T> beanClass, IncludeExclude includeExclude, String... properties) {
    this.beanClass = Check.notNull(beanClass, "beanClass").ok();
    this.converter = null;
    Check.notNull(includeExclude, "includeExclude");
    Check.notNull(properties, "properties");
    this.setters = getSetters(includeExclude, properties);
  }

  /**
   * Creates a {@code BeanWriter} for the specified class. You can optionally specify an array of
   * properties that you intend or do <i>not</i> intend to write. If you specify a zero-length array
   * all properties will be writable. If you intend to use this {@code BeanWriter} to repetitively
   * write just one or two properties from bulky bean types, explicitly specifying the properties
   * you intend to write might make the {@code BeanWriter} more efficient. Input values will first
   * be converted by the specified conversion function before being assigned to properties.
   *
   * <p><i>Specifying one or more non-existent properties will not cause an exception to be
   * thrown.</i> They will be quietly ignored.
   *
   * @param beanClass The bean class
   * @param converter A conversion function for input values. The conversion is given the {@link
   *     Setter} for the property to be set as the first argument, and the input value as the second
   *     argument. The return value should be the actual value to assign to the property. The {@code
   *     Setter} should only be used to get the {@link Setter#getProperty() name} and {@link
   *     Setter#getParamType() type} of the property to be set. You <i>should not</i> use it to
   *     actually {@link Setter#write(Object, Object) write} the property, as this will happen
   *     anyhow once the conversion function returns. Unless the conversion fails for extraordinary
   *     reasons, it should throw an {@link IllegalAssignmentException} upon failure. You can again
   *     use the {@code Setter} to {@link Setter#illegalAssignment(Object) generate} the exception.
   * @param includeExclude Whether to include or exclude the specified properties
   * @param properties The properties to be included/excluded
   */
  public BeanWriter(
      Class<T> beanClass,
      ThrowingBiFunction<Setter, Object, Object, Throwable> converter,
      IncludeExclude includeExclude,
      String... properties) {
    this.beanClass = Check.notNull(beanClass, "beanClass").ok();
    this.converter = Check.notNull(converter, "converter").ok();
    Check.notNull(includeExclude, "includeExclude");
    Check.notNull(properties, "properties");
    this.setters = getSetters(includeExclude, properties);
  }

  /**
   * Sets the value of the specified property on the specified bean.
   *
   * @param bean The bean instance
   * @param property The property
   * @param value The value to set it to
   * @throws IllegalAssignmentException If the value cannot be cast to the type of the property,
   *     or if the value is {@code null} and the property has a primitive type. This is a {@link
   *     RuntimeException}, but you might still want to catch it as it can often be handled in a
   *     meaningful way.
   * @throws Throwable The {@code Throwable} thrown from inside the {@code java.lang.invoke}
   *     package
   */
  public void set(T bean, String property, Object value) throws Throwable {
    Check.notNull(bean, "bean");
    Setter setter = Check.notNull(property, "property").ok(setters::get);
    Check.that(setter).is(notNull(), () -> noSuchProperty(bean, property));
    set(bean, setter, value);
  }

  /**
   * Overwrites all properties in the second bean with the values they have in the first bean. This
   * can potentially nullify non-null values in the second bean.
   *
   * @param fromBean The bean from which to copy the values.
   * @param toBean The bean to which to copy the values.
   * @throws Throwable The {@code Throwable} thrown from inside the {@code java.lang.invoke}
   *     package
   */
  public void copy(T fromBean, T toBean) throws Throwable {
    BeanReader<T> reader = getBeanReader();
    for (Setter setter : setters.values()) {
      set(toBean, setter, reader.read(fromBean, setter.getProperty()));
    }
  }

  /**
   * Copies only non-null values from the first bean to the second bean.
   *
   * @param fromBean The bean from which to copy the values.
   * @param toBean The bean to which to copy the values.
   * @throws Throwable The {@code Throwable} thrown from inside the {@code java.lang.invoke}
   *     package
   */
  public void copyNonNull(T fromBean, T toBean) throws Throwable {
    BeanReader<T> reader = getBeanReader();
    for (Setter setter : setters.values()) {
      Object v = reader.read(fromBean, setter.getProperty());
      if (v != null) {
        set(toBean, setter, v);
      }
    }
  }

  /**
   * Overwrites all properties in the second bean whose value is {@codd null} with the values they
   * have in the first bean. Non-null properties in the second bean are left alone.
   *
   * @param fromBean The bean from which to copy the values.
   * @param toBean The bean to which to copy the values.
   * @throws Throwable The {@code Throwable} thrown from inside the {@code java.lang.invoke}
   *     package
   */
  public void enrich(T fromBean, T toBean) throws Throwable {
    BeanReader<T> reader = getBeanReader();
    for (Setter setter : setters.values()) {
      Object v = reader.read(fromBean, setter.getProperty());
      if (v != null && reader.read(toBean, setter.getProperty()) == null) {
        set(toBean, setter, v);
      }
    }
  }

  /**
   * Overwrites all properties in the specified bean with the corresponding values in the specified
   * map. This can potentially nullify non-null values in the target bean.
   *
   * @param fromMap The {@code Map} providing the data for the JavaBean
   * @param toBean The JavaBean to populate
   * @throws IllegalAssignmentException If a value cannot be cast or converted to the type of
   *     the destination property, or if the value is {@code null} and the destination property has
   *     a primitive type. This is a {@link RuntimeException}, but you might still want to catch it
   *     as it can often be handled in a meaningful way.
   * @throws Throwable The {@code Throwable} thrown from inside the {@code java.lang.invoke}
   *     package
   */
  public void copy(Map<String, ?> fromMap, T toBean) throws IllegalAssignmentException, Throwable {
    Check.notNull(fromMap, "fromMap");
    Check.notNull(toBean, "toBean");
    for (Map.Entry<String, ?> e : fromMap.entrySet()) {
      if (e.getKey() != null) {
        Setter setter = setters.get(e.getKey());
        if (setter != null) {
          set(toBean, setter, e.getValue());
        }
      }
    }
  }

  /**
   * Copies all non-null values from the specified map to the specified bean. Map keys that do not
   * correspond to bean properties are quietly ignored.
   *
   * @param fromMap The {@code Map} providing the data for the JavaBean
   * @param toBean The JavaBean to populate
   * @throws IllegalAssignmentException If a value cannot be cast or converted to the type of
   *     the destination property, or if the value is {@code null} and the destination property has
   *     a primitive type. This is a {@link RuntimeException}, but you might still want to catch it
   *     as it can often be handled in a meaningful way.
   * @throws Throwable The {@code Throwable} thrown from inside the {@code java.lang.invoke}
   *     package
   */
  public void copyNonNull(Map<String, ?> fromMap, T toBean) throws Throwable {
    Check.notNull(fromMap, "fromMap");
    Check.notNull(toBean, "toBean");
    for (Map.Entry<String, ?> e : fromMap.entrySet()) {
      if (e.getValue() != null && e.getKey() != null) {
        Setter setter = setters.get(e.getKey());
        if (setter != null) {
          set(toBean, setter, e.getValue());
        }
      }
    }
  }

  /**
   * Overwrites all properties in the specified bean whose value is {@codd null} with the
   * corresponding values in the specified map. Non-null properties in the target bean are left
   * alone.
   *
   * @param fromMap The {@code Map} providing the data for the JavaBean
   * @param toBean The JavaBean to populate
   * @throws IllegalAssignmentException If a value cannot be cast or converted to the type of
   *     the destination property, or if the value is {@code null} and the destination property has
   *     a primitive type. This is a {@link RuntimeException}, but you might still want to catch it
   *     as it can often be handled in a meaningful way.
   * @throws Throwable The {@code Throwable} thrown from inside the {@code java.lang.invoke}
   *     package
   */
  public void enrich(Map<String, ?> fromMap, T toBean)
      throws IllegalAssignmentException, Throwable {
    Check.notNull(fromMap, "fromMap");
    Check.notNull(toBean, "toBean");
    BeanReader<T> reader = getBeanReader();
    for (Map.Entry<String, ?> e : fromMap.entrySet()) {
      if (e.getValue() != null && e.getKey() != null) {
        Setter setter = setters.get(e.getKey());
        if (setter != null && reader.read(toBean, e.getKey()) == null) {
          set(toBean, setter, e.getValue());
        }
      }
    }
  }

  /**
   * Returns the type of the objects this {@code BeanWriter} can write to.
   *
   * @return The type of the objects this {@code BeanWriter} can write to
   */
  public Class<? super T> getBeanClass() {
    return beanClass;
  }

  /**
   * Returns the bean properties that can be set by this {@code BeanWriter}. This may be a subset of
   * all writable properties of the bean class, as the constructor allows you to {@link
   * BeanWriter#BeanWriter(Class, String...) exclude} properties from being set by this {@code
   * BeanWriter}.
   *
   * @return The bean properties that can be set by this {@code BeanWriter}
   */
  public Set<String> getIncludedProperties() {
    return setters.keySet();
  }

  /**
   * Returns the {@link Setter setters} used by the {@code BeanWriter} to write bean properties. The
   * returned {@code Map} maps the name of a property to the {@code Setter} used to write it.
   *
   * @return The {@link Setter setters} used by the {@code BeanWriter} to write bean properties.
   */
  public Map<String, Setter> getIncludedSetters() {
    return setters;
  }

  private Map<String, Setter> getSetters(IncludeExclude ie, String[] props) {
    Map<String, Setter> tmp = SetterFactory.INSTANCE.getSetters(beanClass);
    if (props.length == 0) {
      return tmp;
    }
    Map<String, Setter> copy = new HashMap<>(tmp);
    if (ie.isExclude()) {
      copy.keySet().removeAll(Set.of(props));
    } else {
      copy.keySet().retainAll(Set.of(props));
    }
    Check.that(props).isNot(empty(), () -> noPropertiesSelected(beanClass, ie, props));
    return Map.copyOf(copy);

  }

  private void set(T bean, Setter setter, Object value) throws Throwable {
    if (converter == null) {
      setter.write(bean, value);
    } else {
      Object val;
      try {
        val = converter.apply(setter, value);
      } catch (TypeConversionException e) {
        throw setter.illegalAssignment(value);
      }
      setter.write(bean, val);
    }
  }

  private BeanReader<T> beanReader;

  private BeanReader<T> getBeanReader() {
    if (beanReader == null) {
      beanReader = new BeanReader<>(beanClass, setters.keySet().toArray(String[]::new));
    }
    return beanReader;
  }

}
