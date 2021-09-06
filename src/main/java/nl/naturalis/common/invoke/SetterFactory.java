package nl.naturalis.common.invoke;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.empty;
import static nl.naturalis.common.invoke.NoPublicSettersException.noPublicSetters;

/**
 * Provides and caches {@link Setter setters} for classes.
 *
 * @author Ayco Holleman
 */
public class SetterFactory {

  public static final SetterFactory INSTANCE = new SetterFactory();

  private final Map<Class<?>, Map<String, Setter>> cache;

  private SetterFactory() {
    cache = new HashMap<>();
  }

  /**
   * Returns the public {@link Setter setters} for the specified class. The returned {@code Map}
   * maps maps property names to {@code Setter} instances.
   *
   * @param clazz The class for which to retrieve the public setters
   * @return The public setters of the specified class
   * @throws NoPublicSettersException If the specified class does not have any public setters
   */
  public Map<String, Setter> getSetters(Class<?> clazz) throws NoPublicSettersException {
    Map<String, Setter> setters = cache.get(clazz);
    if (setters == null) {
      setters = new HashMap<>();
      for (Method m : ClassMethods.geSetters(clazz)) {
        String prop = ClassMethods.getPropertyNameFromSetter(m);
        setters.put(prop, new Setter(m, prop));
      }
      Check.on(s -> noPublicSetters(clazz), setters).isNot(empty());
      cache.put(clazz, Map.copyOf(setters));
    }
    return setters;
  }
}
