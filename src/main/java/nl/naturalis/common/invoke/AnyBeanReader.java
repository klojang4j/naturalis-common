package nl.naturalis.common.invoke;

import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.IntPair;
import nl.naturalis.common.check.Check;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import static java.lang.System.identityHashCode;
import static nl.naturalis.common.check.CommonChecks.keyIn;
import static nl.naturalis.common.check.CommonChecks.notNull;
import static nl.naturalis.common.invoke.NoSuchPropertyException.noSuchProperty;

/**
 * A dynamic bean reader class for arbitrary types of beans.
 *
 * @author Ayco Holleman
 */
public final class AnyBeanReader {

  private final boolean strict;

  /**
   * Creates a new {@code AnyBeanReader}. JavaBeans naming conventions will be applied regarding
   * which methods qualify as getters.
   */
  public AnyBeanReader() {
    this(true);
  }

  /**
   * Creates a new {@code AnyBeanReader}.
   *
   * @param strictNaming If {@code false}, all methods with a zero-length parameter list and a
   *     non-{@code void} return type, except {@code getClass()}, {@code hashCode()} and {@code
   *     toString()}, will be regarded as getters. Otherwise JavaBeans naming conventions will be
   *     applied regarding which methods qualify as getters, with the exception that methods
   *     returning a {@link Boolean} are allowed to have a name starting with "is".
   */
  public AnyBeanReader(boolean strictNaming) {
    this.strict = strictNaming;
  }

  /**
   * Returns the value of the specified property on the specified bean. If the property does not
   * exist a {@link NoSuchPropertyException} is thrown.
   *
   * @param bean The bean instance
   * @param property The property
   * @return Its value
   * @throws NoSuchPropertyException If the specified property does not exist
   */
  @SuppressWarnings("unchecked")
  public <U> U read(Object bean, String property) throws NoSuchPropertyException {
    Check.notNull(bean, "bean");
    Check.notNull(property, "property");
    return doRead(bean, property);
  }

  @SuppressWarnings("unchecked")
  private <U> U doRead(Object bean, String prop) {
    Map<String, Getter> getters = GetterFactory.INSTANCE.getGetters(bean.getClass(), strict);
    Getter getter = getters.get(prop);
    Check.that(getter).is(notNull(), () -> noSuchProperty(bean, prop)).ok();
    try {
      return (U) getters.get(prop).read(bean);
    } catch (Throwable exc) {
      throw InvokeException.wrap(exc, bean, getter);
    }
  }
}
