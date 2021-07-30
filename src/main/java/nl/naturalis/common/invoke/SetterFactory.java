package nl.naturalis.common.invoke;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import nl.naturalis.common.ClassMethods;

public class SetterFactory {

  public static final SetterFactory INSTANCE = new SetterFactory();

  private final Map<Class<?>, Map<String, Setter>> cache;

  private SetterFactory() {
    cache = new HashMap<>();
  }

  public Map<String, Setter> getSetters(Class<?> beanClass) {
    Map<String, Setter> info = cache.get(beanClass);
    if (info == null) {
      info = new HashMap<>();
      for (Method m : ClassMethods.geSetters(beanClass)) {
        String prop = ClassMethods.getPropertyNameFromSetter(m);
        info.put(prop, new Setter(m, prop));
      }
      cache.put(beanClass, Map.copyOf(info));
    }
    return info;
  }
}
