package nl.naturalis.common.invoke;

import java.util.function.Function;
import nl.naturalis.common.ClassMethods;

public class InvokeException extends RuntimeException {

  static <T> InvokeException typeMismatch(BeanReader<? super T> reader, T bean) {
    String fmt = "Cannot read beans of type %s (am BeanReader for ? extends %s)";
    String name0 = ClassMethods.prettyClassName(bean);
    String name1 = ClassMethods.prettyClassName(reader.getBeanClass());
    return new InvokeException(fmt, name0, name1);
  }

  static Function<String, InvokeException> notPublic(Class<?> clazz) {
    return s -> new InvokeException("Class %s is not public", clazz.getName());
  }

  static Function<String, InvokeException> noPublicGetters(Class<?> clazz) {
    return s -> new InvokeException("Class %s does not have any public getters", clazz.getName());
  }

  private InvokeException(String message, Object... msgArgs) {
    super(msgArgs.length == 0 ? message : String.format(message, msgArgs));
  }
}
