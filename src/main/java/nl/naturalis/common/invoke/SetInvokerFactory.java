package nl.naturalis.common.invoke;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import nl.naturalis.common.ClassMethods;

class SetInvokerFactory {

  private static final Map<Class<?>, Map<String, SetInvoker>> cache = new HashMap<>();

  static final SetInvokerFactory INSTANCE = new SetInvokerFactory();

  private SetInvokerFactory() {}

  Map<String, SetInvoker> getInvokers(Class<?> beanClass) {
    Map<String, SetInvoker> info = cache.get(beanClass);
    if (info == null) {
      info = new HashMap<>();
      for (Method m : ClassMethods.geSetters(beanClass)) {
        String property = ClassMethods.getPropertyNameFromSetter(m);
        info.put(property, new SetInvoker(m));
      }
      cache.put(beanClass, Map.copyOf(info));
    }
    return info;
  }
}
