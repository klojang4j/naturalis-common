package nl.naturalis.common.invoke;

import nl.naturalis.common.ClassMethods;

public class InvokeException extends RuntimeException {

  //  static InvokeException typeMismatch(BeanReader<?> reader, Object bean) {
  //    String fmt = "Cannot read beans of type %s (am BeanReader for ? extends %s)";
  //    String cn0 = ClassMethods.prettyClassName(bean);
  //    String cn1 = ClassMethods.prettyClassName(reader.getBeanClass());
  //    return new InvokeException(String.format(fmt, cn0, cn1));
  //  }

  private InvokeException(String message) {
    super(message);
  }
}
