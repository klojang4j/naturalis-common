package nl.naturalis.common.invoke;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import nl.naturalis.common.ClassMethods;

class WriteInfoFactory {

  private static final Map<Class<?>, Map<String, WriteInfo>> cache = new HashMap<>();

  static final WriteInfoFactory INSTANCE = new WriteInfoFactory();

  private WriteInfoFactory() {}

  Map<String, WriteInfo> getWriteInfo(Class<?> beanClass) {
    Map<String, WriteInfo> info = cache.get(beanClass);
    if (info == null) {
      info = new HashMap<>();
      for (Method m : ClassMethods.geSetters(beanClass)) {
        String property = ClassMethods.getPropertyNameFromSetter(m);
        info.put(property, new WriteInfo(m));
      }
      cache.put(beanClass, Map.copyOf(info));
    }
    return info;
  }
}
