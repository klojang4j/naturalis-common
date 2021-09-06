package nl.naturalis.common.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.empty;
import static nl.naturalis.common.check.CommonChecks.yes;
import static nl.naturalis.common.invoke.InvokeException.classNotPublic;
import static nl.naturalis.common.invoke.NoPublicGettersException.noPublicGetters;

/**
 * Provides and caches {@link Getter getters} for classes.
 *
 * @author Ayco Holleman
 */
public class GetterFactory {

  public static final GetterFactory INSTANCE = new GetterFactory();

  private final Map<Class<?>, Map<String, Getter>> cache = new HashMap<>();

  private GetterFactory() {}

  /**
   * @param clazz The class for which to retrieve the public getters
   * @param strict Wheter or not to apply strict JavaBeans naming conventions to what qualifies as a
   *     getter. See {@link ClassMethods#getPropertyNameFromGetter(java.lang.reflect.Method,
   *     boolean)}. Note that {@code getClass()} will anyhow never be included as a getter.
   * @return The public setters of the specified class
   * @throws NoPublicGettersException If the specified class does not have any public getters
   */
  public Map<String, Getter> getGetters(Class<?> clazz, boolean strict)
      throws NoPublicGettersException {
    Check.on(classNotPublic(clazz), clazz.getModifiers()).has(Modifier::isPublic, yes());
    Map<String, Getter> getters = cache.get(clazz);
    if (getters == null) {
      getters = new HashMap<>();
      for (Method m : ClassMethods.getGetters(clazz, strict)) {
        if (!m.getName().equals("getClass")) {
          String property = ClassMethods.getPropertyNameFromGetter(m, strict);
          getters.put(property, new Getter(m, property));
        }
      }
      Check.on(s -> noPublicGetters(clazz), getters).isNot(empty());
      getters = Map.copyOf(getters);
      cache.put(clazz, getters);
    }
    return getters;
  }
}
