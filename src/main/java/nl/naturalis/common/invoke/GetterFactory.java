package nl.naturalis.common.invoke;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import nl.naturalis.common.ClassMethods;

public class GetterFactory {

  private static final Map<Class<?>, Map<String, Getter<?>>> cache = new HashMap<>();

  static final GetterFactory INSTANCE = new GetterFactory();

  private GetterFactory() {}

  Map<String, Getter<?>> getGetters(Class<?> beanClass, boolean strict) {
    Map<String, Getter<?>> info = cache.get(beanClass);
    if (info == null) {
      info = new HashMap<>();
      for (Method m : ClassMethods.getGetters(beanClass, strict)) {
        String property = ClassMethods.getPropertyNameFromGetter(m, strict);
        info.put(property, new Getter<>(m));
      }
      info = Map.copyOf(info);
      cache.put(beanClass, info);
    }
    return info;
  }
}
