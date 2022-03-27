package nl.naturalis.common.invoke;

import static java.lang.System.identityHashCode;
import static nl.naturalis.common.check.CommonChecks.keyIn;
import static nl.naturalis.common.invoke.NoSuchPropertyException.noSuchProperty;

import java.lang.invoke.MethodHandle;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.x.invoke.InvokeUtils;

/**
 * Reads properties of any type of bean. This makes {@code AnyBeanReader} easier to use than the
 * {@link BeanReader} class. The {@code BeanReader}, on the other hand, allows you to specify
 * up-front the bean properties you are going to read, making it slightly more efficient. Then
 * again, {@code AnyBeanReader} will create an internally maintained cache of oft-read properties,
 * if you specify it to do so. This can improve performance if you expect the same few properties to
 * be read over and over again from the same few beans.
 *
 * <p>Although this class uses {@link MethodHandle} instances to extract values from the bean, it
 * still uses reflection to identify the getter methods on the bean class. Therefore if you use this
 * class from within a Java module you must still open the module to the naturalis-common module.
 *
 * <p>This class caches relevant data about the bean class such that after the first time you
 * create
 * an instance of a {@code BeanReader} for a particular bean class, subsequent instantiations are
 * essentially for free (no matter which constructor you use).
 *
 * @author Ayco Holleman
 */
public class AnyBeanReader {

  private final boolean strict;
  private final LinkedHashMap<int[], Object> valueCache;

  /**
   * Creates a new {@code AnyBeanReader}. Strict naming conventions will be applied to what
   * qualifies as a getter. See
   * {@link InvokeUtils#getPropertyNameFromGetter(java.lang.reflect.Method,
   * boolean)}.
   */
  public AnyBeanReader() {
    this(true);
  }

  /**
   * Creates a new {@code AnyBeanReader}.
   *
   * @param strictNaming hether or not to apply strict naming conventions for what counts as a
   *     getter. See {@link InvokeUtils#getPropertyNameFromGetter(java.lang.reflect.Method,
   *     boolean)}.
   */
  public AnyBeanReader(boolean strictNaming) {
    this.valueCache = null;
    this.strict = strictNaming;
  }

  /**
   * Creates a new {@code AnyBeanReader} that maintains a small cache of values read previously.
   * Only getters that strictly conform to the JavaBeans naming conventions can and will be read.
   *
   * @param valueCacheSize
   */
  public AnyBeanReader(int valueCacheSize) {
    this(valueCacheSize, true);
  }

  /**
   * Creates a new {@code AnyBeanReader} that maintains a small cache of values read previously.
   * This is useful if you expect the same few properties to be read over and over again from the
   * same few beans. The cache size should be small in order to be more effective than not using
   * cache at all. That cache is queried using reference comparisons only. Using {@code equals}
   * comparisons would also nullify the benefits of using a cache at all.
   *
   * @param valueCacheSize The size of the value cache
   * @param strictNaming hether or not to apply strict naming conventions for what counts as a
   *     getter. See {@link InvokeUtils#getPropertyNameFromGetter(java.lang.reflect.Method,
   *     boolean)}
   */
  public AnyBeanReader(int valueCacheSize, boolean strictNaming) {
    this.valueCache =
        new LinkedHashMap<>((valueCacheSize * 4) / 3 + 1, .75F, true) {

          @Override
          protected boolean removeEldestEntry(Entry<int[], Object> eldest) {
            return size() > valueCacheSize;
          }
        };
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
    if (valueCache != null) {
      int[] key = new int[] {identityHashCode(bean), identityHashCode(property)};
      return (U) valueCache.computeIfAbsent(key, k -> doRead(bean, property));
    }
    return doRead(bean, property);
  }

  @SuppressWarnings("unchecked")
  private <U> U doRead(Object bean, String prop) {
    Map<String, Getter> getters = GetterFactory.INSTANCE.getGetters(bean.getClass(), strict);
    Check.on(x -> noSuchProperty(bean, prop), prop).is(keyIn(), getters);
    try {
      return (U) getters.get(prop).read(bean);
    } catch (Throwable exc) {
      throw ExceptionMethods.uncheck(exc);
    }
  }
}
