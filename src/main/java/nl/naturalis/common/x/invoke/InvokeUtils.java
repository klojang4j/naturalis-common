package nl.naturalis.common.x.invoke;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.invoke.InvokeException;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;

import static java.lang.invoke.MethodHandles.publicLookup;
import static java.lang.invoke.MethodType.methodType;
import static nl.naturalis.common.check.CommonChecks.no;
import static nl.naturalis.common.invoke.InvokeException.cannotInstantiate;

public class InvokeUtils {

  private static final Map<Class<?>, MethodHandle> noArgsConstructors = new HashMap<>();

  @SuppressWarnings({"unchecked"})
  public static <T> T newInstance(Class<T> clazz) throws Throwable {
    return (T) getNoArgConstructor(clazz).invoke();
  }

  public static <T> MethodHandle getNoArgConstructor(Class<T> clazz) {
    Check.on(cannotInstantiate(clazz), clazz).has(Class::isInterface, no());
    return noArgsConstructors.computeIfAbsent(
        clazz,
        k -> {
          try {
            return publicLookup().findConstructor(k, methodType(void.class));
          } catch (NoSuchMethodException e) {
            throw InvokeException.missingNoArgConstructor(k);
          } catch (IllegalAccessException e) {
            throw InvokeException.wrap(e);
          }
        });
  }
}
