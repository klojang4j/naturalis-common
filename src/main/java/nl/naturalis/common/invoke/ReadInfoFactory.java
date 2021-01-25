package nl.naturalis.common.invoke;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import nl.naturalis.common.ClassMethods;

class ReadInfoFactory {

  private static final Map<Class<?>, Map<String, ReadInfo>> cache = new HashMap<>();
  private static final Boolean STRICT_NAMING = true;

  static final ReadInfoFactory INSTANCE = new ReadInfoFactory();

  private ReadInfoFactory() {}

  Map<String, ReadInfo> getReadInfo(Class<?> beanClass) {
    Map<String, ReadInfo> info = cache.get(beanClass);
    if (info == null) {
      info = new HashMap<>();
      for (Method m : ClassMethods.getGetters(beanClass, STRICT_NAMING)) {
        String property = ClassMethods.getPropertyNameFromGetter(m, STRICT_NAMING);
        info.put(property, new ReadInfo(m));
      }
      cache.put(beanClass, Map.copyOf(info));
    }
    return info;
  }
}
