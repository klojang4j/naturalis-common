package nl.naturalis.common.invoke;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import nl.naturalis.common.ClassMethods;

class GetInvokerFactory {

  private static final Map<Class<?>, Map<String, GetInvoker>> cache = new HashMap<>();

  static final GetInvokerFactory INSTANCE = new GetInvokerFactory();

  private GetInvokerFactory() {}

  Map<String, GetInvoker> getInvokers(Class<?> beanClass, boolean strict) {
    Map<String, GetInvoker> info = cache.get(beanClass);
    if (info == null) {
      info = new HashMap<>();
      for (Method m : ClassMethods.getGetters(beanClass, strict)) {
        String property = ClassMethods.getPropertyNameFromGetter(m, strict);
        info.put(property, new GetInvoker(m));
      }
      info = Map.copyOf(info);
      cache.put(beanClass, info);
    }
    return info;
  }
}
