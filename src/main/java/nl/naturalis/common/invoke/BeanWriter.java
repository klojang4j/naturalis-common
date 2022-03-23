package nl.naturalis.common.invoke;

import nl.naturalis.common.Morph;
import nl.naturalis.common.TypeConversionException;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingBiFunction;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.invoke.NoSuchPropertyException.noPropertiesSelected;
import static nl.naturalis.common.invoke.NoSuchPropertyException.noSuchProperty;

/**
 * A bean writer class that uses the {@code java.lang.invoke} package in stead of reflection to set bean properties.
 * Although this class uses {@link MethodHandle} instances to extract values from the bean, it still uses reflection to
 * identify the getter methods on the bean class. Therefore if you use this class within a Java module you must open the
 * module to the naturalis-common module.
 *
 * @param <T> The type of the bean
 * @author Ayco Holleman
 */
public class BeanWriter<T> {

  /**
   * Returns a {@code BeanWriter} that allows for "loose typing" of the values to be assigned to the bean's properties.
   * A {@link Morph} object will be used to morph incoming values to the type of the destination property.
   *
   * @param <U> The type of the bean
   * @param beanClass The bean class
   * @return A @code BeanWriter} for the specified class that uses a {@link Morph} to convert values to the type of the
   * property for which they are destined
   */
  public static <U> BeanWriter<U> getTolerantWriter(Class<U> beanClass, String... properties) {
    return new BeanWriter<>(beanClass, Morph::convert, false, properties);
  }

  private final Class<T> beanClass;
  private final ThrowingBiFunction<Object, Class<?>, Object, Throwable> converter;
  private final Map<String, Setter> setters;

  /**
   * Creates a {@code BeanWriter} for the class. You can optionally specify an array of properties that you intend to
   * write. Specifying {@code null} or a zero-length array will allow you to write <i>all</i> of the bean's properties.
   *
   * @param beanClass The bean class
   * @param properties The properties you intend to write (may be {@code null} or zero-length)
   */
  public BeanWriter(Class<T> beanClass, String... properties) {
    this(beanClass, false, properties);
  }

  public BeanWriter(
          Class<T> beanClass,
          ThrowingBiFunction<Object, Class<?>, Object, Throwable> converter,
          String... properties) {
    this(beanClass, converter, true, properties);
  }

  /**
   * Creates a {@code BeanWriter} for the specified class. You can optionally specify an array of properties that you
   * intend or do <i>not</i> intend to write. Specifying {@code null} or a zero-length array will allow you to write
   * <i>all</i> of the bean's properties.
   *
   * @param beanClass The bean class
   * @param exclude Whether to exclude or include the specified properties
   * @param properties The properties you intend to read (may be {@code null} or zero-length)
   */
  public BeanWriter(Class<T> beanClass, boolean exclude, String... properties) {
    this(beanClass, (obj, type) -> obj, exclude, properties);
  }

  /**
   * Creates a {@code BeanWriter} for the specified class. You can optionally specify an array of properties that you
   * intend or do <i>not</i> intend to write. Specifying {@code null} or a zero-length array will allow you to write
   * <i>all</i> of the bean's properties. If you intend to use this {@code BeanWriter} to repetitively to write just
   * one or two properties from bulky bean types, explicitly specifying the properties you intend to write might make
   * the {@code BeanWriter} more efficient. In addition you can also specify a function that converts the values passed
   * to the {@link #set(Object, String, Object) set} method to the type of the property for which they are destined.
   *
   * @param beanClass The bean class
   * @param converter A function that takes the value passed to the {@link #set(Object, String, Object) set} method
   * (first parameter) and the target type (second parameter) and produces the value that is actually going to be
   * assigned to the property
   * @param exclude Whether to exclude or include the specified properties
   * @param properties The properties you intend to read (may be {@code null} or zero-length)
   * @see Morph
   */
  public BeanWriter(
          Class<T> beanClass,
          ThrowingBiFunction<Object, Class<?>, Object, Throwable> converter,
          boolean exclude,
          String... properties) {
    this.beanClass = Check.notNull(beanClass, "beanClass").ok();
    this.converter = Check.notNull(converter, "converter").ok();
    Map<String, Setter> tmp = SetterFactory.INSTANCE.getSetters(beanClass);
    if (properties == null || properties.length == 0) {
      this.setters = tmp;
    } else {
      Check.that(properties, "properties").is(deepNotEmpty());
      Map<String, Setter> copy = new HashMap<>(tmp);
      if (exclude) {
        copy.keySet().removeAll(Set.of(properties));
      } else {
        copy.keySet().retainAll(Set.of(properties));
      }
      Check.on(noPropertiesSelected(beanClass, exclude, properties), copy).isNot(empty());
      this.setters = Map.copyOf(copy);
    }
  }

  /**
   * Sets the value of the specified property on the specified bean.
   *
   * @param bean The bean instance
   * @param property The property @Param value The value to set it to
   * @throws Throwable Any {@code Throwable} thrown from inside the {@code java.lang.invoke} package or a {@link
   * TypeConversionException} if loose typing was specified through the constructor and one or more values in the {@code
   * Map} could not be converted to the appropriate type
   */
  public void set(T bean, String property, Object value) throws Throwable {
    Check.notNull(bean, "bean");
    Check.notNull(property, "property").is(keyIn(), setters, () -> noSuchProperty(bean, property));
    set(bean, setters.get(property), value);
  }

  /**
   * Copies all values, including null-values, from the first bean to the second bean.
   *
   * @param fromBean The bean from which to copy the values.
   * @param toBean The bean to which to copy the values.
   * @throws Throwable Any {@code Throwable} thrown from inside the {@code java.lang.invoke} package
   */
  public void copy(T fromBean, T toBean) throws Throwable {
    BeanReader<T> reader = new BeanReader<>(beanClass);
    for (Setter setter : setters.values()) {
      set(toBean, setter, reader.read(fromBean, setter.getProperty()));
    }
  }

  /**
   * Copies only non-null values from the first bean to the second bean.
   *
   * @param fromBean The bean from which to copy the values.
   * @param toBean The bean to which to copy the values.
   * @throws Throwable Any {@code Throwable} thrown from inside the {@code java.lang.invoke} package
   */
  public void copyNonNull(T fromBean, T toBean) throws Throwable {
    BeanReader<T> reader = new BeanReader<>(beanClass);
    for (Setter setter : setters.values()) {
      Object v = reader.read(fromBean, setter.getProperty());
      if (v != null) {
        set(toBean, setter, v);
      }
    }
  }

  /**
   * Copies all values from the first bean to the second bean, but only if the value in the second bean is {@code
   * null}.
   *
   * @param fromBean The bean from which to copy the values.
   * @param toBean The bean to which to copy the values.
   * @throws Throwable Any {@code Throwable} thrown from inside the {@code java.lang.invoke} package or a {@link
   * TypeConversionException} if loose typing was specified through the constructor and one or more values in the {@code
   * Map} could not be converted to the appropriate type
   */
  public void enrich(T fromBean, T toBean) throws Throwable {
    BeanReader<T> reader = new BeanReader<>(beanClass);
    for (Setter setter : setters.values()) {
      Object v = reader.read(fromBean, setter.getProperty());
      if (v != null && reader.read(toBean, setter.getProperty()) == null) {
        set(toBean, setter, v);
      }
    }
  }

  /**
   * Copies all values, including null-values, from the specified map to the specified bean. Map keys that do not
   * correspond to bean properties are quietly ignored.
   *
   * @param fromMap The {@code Map} providing the data for the JavaBean
   * @param toBean The JavaBean to populate
   * @throws Throwable Any {@code Throwable} thrown from inside the {@code java.lang.invoke} package or a {@link
   * TypeConversionException} if loose typing was specified through the constructor and one or more values in the {@code
   * Map} could not be converted to the appropriate type
   */
  public void copy(Map<String, ?> fromMap, T toBean) throws Throwable {
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
   * Copies all non-null values from the specified map to the specified bean. Map keys that do not correspond to bean
   * properties are quietly ignored.
   *
   * @param fromMap The {@code Map} providing the data for the JavaBean
   * @param toBean The JavaBean to populate
   * @throws Throwable Any {@code Throwable} thrown from inside the {@code java.lang.invoke} package or a {@link
   * TypeConversionException} if loose typing was specified through the constructor and one or more values in the {@code
   * Map} could not be converted to the appropriate type
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
   * Copies all values from the specified map to the specified bean, but only if the value in the bean is {@code null}.
   * Map keys that do not correspond to bean properties are quietly ignored.
   *
   * @param fromMap The {@code Map} providing the data for the JavaBean
   * @param toBean The JavaBean to populate
   * @throws Throwable Any {@code Throwable} thrown from inside the {@code java.lang.invoke} package or a {@link
   * TypeConversionException} if loose typing was specified through the constructor and one or more values in the {@code
   * Map} could not be converted to the appropriate type
   */
  public void enrich(Map<String, ?> fromMap, T toBean) throws Throwable {
    Check.notNull(fromMap, "fromMap");
    Check.notNull(toBean, "toBean");
    BeanReader<T> reader = new BeanReader<>(beanClass);
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
   * Returns the type of the objects this {@code BeanReader} can read.
   *
   * @return The type of the objects this {@code BeanReader} can read
   */
  public Class<? super T> getBeanClass() {
    return beanClass;
  }

  /**
   * Returns the bean properties that can be set by this {@code BeanWriter}, which may not be all of the bean's
   * properties, depending on which constructor was used to instantiate the {@code BeanWriter}.
   *
   * @return The bean properties that can be set by this {@code BeanWriter}
   */
  public Set<String> getIncludedProperties() {
    return setters.keySet();
  }

  /**
   * Returns the {@link Setter setters} used by the {@code BeanWriter} to write bean properties. The returned {@code
   * Map} maps the name of a property to the {@code Setter} used to write it.
   *
   * @return The {@link Setter setters} used by the {@code BeanWriter} to write bean properties.
   */
  public Map<String, Setter> getIncludedSetters() {
    return setters;
  }

  private void set(T bean, Setter setter, Object value) throws Throwable {
    if (converter == null) {
      try {
        setter.write(bean, value);
      } catch (ClassCastException e) {
        throw new AssignmentException(beanClass, setter.getProperty(), setter.getParamType(), value);
      }
    } else {
      try {
        setter.write(bean, converter.apply(value, setter.getParamType()));
      } catch (TypeConversionException e) {
        throw new AssignmentException(beanClass, setter.getProperty(), setter.getParamType(), value);
      }
    }
  }

}
