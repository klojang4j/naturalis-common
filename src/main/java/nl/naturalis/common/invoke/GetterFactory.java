package nl.naturalis.common.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.empty;
import static nl.naturalis.common.check.CommonChecks.yes;
import static nl.naturalis.common.invoke.InvokeException.*;

public class GetterFactory {

  public static final GetterFactory INSTANCE = new GetterFactory();

  private final Map<Class<?>, Map<String, Getter>> cache = new HashMap<>();

  private GetterFactory() {}

  public Map<String, Getter> getGetters(Class<?> clazz, boolean strict) {
    Check.on(notPublic(clazz), clazz.getModifiers()).has(Modifier::isPublic, yes());
    Map<String, Getter> info = cache.get(clazz);
    if (info == null) {
      info = new HashMap<>();
      for (Method m : ClassMethods.getGetters(clazz, strict)) {
        if (!m.getName().equals("getClass")) {
          String property = ClassMethods.getPropertyNameFromGetter(m, strict);
          info.put(property, new Getter(m));
        }
      }
      Check.on(noPublicGetters(clazz), info).isNot(empty());
      info = Map.copyOf(info);
      cache.put(clazz, info);
    }
    return info;
  }
}
