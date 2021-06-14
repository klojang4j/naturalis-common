package nl.naturalis.common.invoke;

import java.lang.invoke.MethodHandle;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.Tuple;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.keyIn;
import static nl.naturalis.common.invoke.NoSuchPropertyException.noSuchProperty;

/**
 * Reads properties of any type of bean. This makes {@code AnyBeanReader} more versatile than the
 * {@link BeanReader} class. The {@code BeanReader}, on the other hand, allows you to specify
 * up-front the bean properties you are going to read, possibly making it slightly more efficient.
 *
 * <p>Although this class uses {@link MethodHandle} instances to extract values from the bean, it
 * still uses reflection to identify the getter methods on the bean class. Therefore if you use this
 * class from within a Java module you must still open the module to the naturalis-common module.
 *
 * @author Ayco Holleman
 */
public class AnyBeanReader {

  private final boolean strict;
  private final LinkedHashMap<Tuple<Object, String>, Object> valueCache;

  /**
   * Creates a new {@code AnyBeanReader}. Strict naming conventions will be applied to what
   * qualifies as a getter. See {@link
   * ClassMethods#getPropertyNameFromGetter(java.lang.reflect.Method, boolean)}.
   */
  public AnyBeanReader() {
    this(true);
  }

  /**
   * Creates a new {@code AnyBeanReader}.
   *
   * @param strictNaming hether or not to apply strict naming conventions for what counts as a
   *     getter. See {@link ClassMethods#getPropertyNameFromGetter(java.lang.reflect.Method,
   *     boolean)}
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
   * same (as per {@code equals}}) few beans. The cache size should be small in order to be more
   * effective than not using cache at all, but large enough to contain all the properties of all
   * the bean you intend to read.
   *
   * @param valueCacheSize The size of the value cache
   * @param strictNaming hether or not to apply strict naming conventions for what counts as a
   *     getter. See {@link ClassMethods#getPropertyNameFromGetter(java.lang.reflect.Method,
   *     boolean)}
   */
  public AnyBeanReader(int valueCacheSize, boolean strictNaming) {
    this.valueCache =
        new LinkedHashMap<>(valueCacheSize + 2, 1F) {

          @Override
          protected boolean removeEldestEntry(Entry<Tuple<Object, String>, Object> eldest) {
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
    Tuple<Object, String> t = Tuple.of(bean, property);
    if (valueCache != null) {
      return (U) valueCache.computeIfAbsent(t, this::doRead);
    }
    return doRead(t);
  }

  private <U> U doRead(Tuple<Object, String> t) {
    Object bean = t.getLeft();
    String prop = t.getRight();
    Map<String, Getter> getters = GetterFactory.INSTANCE.getGetters(bean.getClass(), strict);
    Check.on(x -> noSuchProperty(bean, prop), prop).is(keyIn(), getters);
    try {
      return (U) getters.get(prop).getMethod().invoke(bean);
    } catch (Throwable exc) {
      throw ExceptionMethods.uncheck(exc);
    }
  }
}
