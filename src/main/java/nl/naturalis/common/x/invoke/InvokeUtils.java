package nl.naturalis.common.x.invoke;

import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.invoke.InvokeException;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;

import static java.lang.invoke.MethodHandles.arrayConstructor;
import static java.lang.invoke.MethodHandles.publicLookup;
import static java.lang.invoke.MethodType.methodType;

public class InvokeUtils {

  private static final Map<Class<?>, MethodHandle> noArgConstructors = new HashMap<>();
  private static final Map<Class<?>, MethodHandle> intArgConstructors = new HashMap<>();

  @SuppressWarnings({"unchecked"})
  public static <T> T newInstance(Class<T> clazz) throws Throwable {
    try {
      return (T) getNoArgConstructor(clazz).invoke();
    } catch (NoSuchMethodException e) {
      throw InvokeException.missingNoArgConstructor(clazz);
    } catch (Throwable t) {
      throw ExceptionMethods.uncheck(t);
    }
  }

  @SuppressWarnings({"unchecked"})
  public static <T> T newInstance(Class<T> clazz, int arg0) {
    try {
      return (T) getIntArgConstructor(clazz).invoke(arg0);
    } catch (NoSuchMethodException e) {
      throw InvokeException.noSuchConstructor(clazz, int.class);
    } catch (Throwable t) {
      throw ExceptionMethods.uncheck(t);
    }
  }

  @SuppressWarnings({"unchecked"})
  public static <T> T[] newArray(Class<?> clazz, int length) {
    MethodHandle mh = arrayConstructor(clazz);
    try {
      return (T[]) mh.invoke(length);
    } catch (Throwable t) {
      throw ExceptionMethods.uncheck(t);
    }
  }

  public static <T> MethodHandle getNoArgConstructor(Class<T> clazz)
      throws NoSuchMethodException, IllegalAccessException {
    MethodHandle mh = noArgConstructors.get(clazz);
    if (mh == null) {
      mh = publicLookup().findConstructor(clazz, methodType(void.class));
      noArgConstructors.put(clazz, mh);
    }
    return mh;
  }

  // Return MethodHandle for constructor taking a single argument of type int
  public static <T> MethodHandle getIntArgConstructor(Class<T> clazz)
      throws NoSuchMethodException, IllegalAccessException {
    MethodHandle mh = intArgConstructors.get(clazz);
    if (mh == null) {
      mh = publicLookup().findConstructor(clazz, methodType(void.class, int.class));
      intArgConstructors.put(clazz, mh);
    }
    return mh;
  }
}
