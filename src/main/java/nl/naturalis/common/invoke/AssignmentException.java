package nl.naturalis.common.invoke;

import nl.naturalis.common.ClassMethods;

import static nl.naturalis.common.ClassMethods.simpleClassName;
import static nl.naturalis.common.ObjectMethods.ifNotNull;

/**
 * Thrown from a {@link BeanWriter} if there is a type mismatch between the value to set and the property to be set.
 */
public class AssignmentException extends InvokeException {

  private final Class<?> beanClass;
  private final String propertyName;
  private final Class<?> propertyType;
  private final Object value;

  AssignmentException(Class<?> beanClass, String propertyName, Class<?> propertyType, Object value) {
    super(createMessage(beanClass, propertyName, propertyType, value));
    this.beanClass = beanClass;
    this.propertyName = propertyName;
    this.propertyType = propertyType;
    this.value = value;
  }

  private static String createMessage(Class<?> beanClass, String propertyName, Class<?> propertyType, Object value) {
    String s = ifNotNull(value, ClassMethods::simpleClassName, "null");
    return "cannot assign " + s + " to " + simpleClassName(beanClass) + "." + propertyName + " (" + simpleClassName(
            propertyType) + ")";
  }

  public Class<?> getBeanClass() {
    return beanClass;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public Class<?> getPropertyType() {
    return propertyType;
  }

  public Object getValue() {
    return value;
  }


}
