package nl.naturalis.common.x.invoke;

import nl.naturalis.common.invoke.InvokeException;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;

import static java.lang.invoke.MethodHandles.publicLookup;
import static java.lang.invoke.MethodType.methodType;

public class InvokeUtils {

  private static final Map<Class<?>, MethodHandle> noArgsConstructors = new HashMap<>();

  public static <T> T newInstance(Class<T> clazz) {
    try {
      return (T) getNoArgConstructor(clazz).invokeExact();
    } catch (Throwable e) {
      throw InvokeException.wrap(e);
    }
  }

  public static <T> MethodHandle getNoArgConstructor(Class<T> clazz) {
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
