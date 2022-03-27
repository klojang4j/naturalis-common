package nl.naturalis.common.invoke;

import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.check.Check;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.Entry;
import static java.util.Map.entry;
import static nl.naturalis.common.ClassMethods.getPropertyNameFromGetter;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * Provides and caches {@link Getter getters} for classes.
 *
 * @author Ayco Holleman
 */
public final class GetterFactory {

  public static final GetterFactory INSTANCE = new GetterFactory();

  private final Map<Class<?>, Map<String, Getter>> cache = new HashMap<>();

  private GetterFactory() {}

  /**
   * Returns the public {@link Getter getters} for the specified class. The returned {@code Map}
   * maps property names to {@code Getter} instances.
   *
   * @param clazz The class for which to retrieve the public getters
   * @param strict Whether to apply strict JavaBeans naming conventions as to what qualifies as
   *     a getter. See {@link ClassMethods#getGetters(Class, boolean)}.
   * @return The public getters of the specified class
   * @throws IllegalAssignmentException If the does not have any public getters
   */
  public Map<String, Getter> getGetters(Class<?> clazz, boolean strict) {
    Map<String, Getter> getters = cache.get(clazz);
    if (getters == null) {
      List<Method> methods = ClassMethods.getGetters(clazz, strict);
      Check.that(methods).isNot(empty(), "class ${0} does not have any public getters", clazz);
      List<Entry<String, Getter>> entries = new ArrayList<>(methods.size());
      for (Method m : methods) {
        String prop = getPropertyNameFromGetter(m, strict);
        entries.add(entry(prop, new Getter(m, prop)));
      }
      getters = Map.ofEntries(entries.toArray(Entry[]::new));
      cache.put(clazz, getters);
    }
    return getters;
  }
}
