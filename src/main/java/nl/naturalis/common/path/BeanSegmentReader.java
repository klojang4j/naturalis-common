package nl.naturalis.common.path;

import java.util.function.Function;
import nl.naturalis.common.invoke.BeanReader;
import nl.naturalis.common.invoke.NoSuchPropertyException;
import nl.naturalis.common.path.PathWalker.DeadEndAction;
import static nl.naturalis.common.path.PathWalkerException.readWriteError;

class BeanSegmentReader<T> extends SegmentReader<T> {

  BeanSegmentReader(DeadEndAction deadEndAction, Function<Path, Object> keyDeserializer) {
    super(deadEndAction, keyDeserializer);
  }

  @Override
  @SuppressWarnings("unchecked")
  Object read(T bean, Path path) {
    Class<T> beanClass = (Class<T>) bean.getClass();
    String property = path.segment(0);
    BeanReader<T> reader = new BeanReader<>(beanClass, property);
    try {
      try {
        Object val = reader.get(bean, property);
        return nextSegmentReader().read(val, path.shift());
      } catch (NoSuchPropertyException e) {
        return deadEnd(e);
      }
    } catch (Throwable e1) {
      throw readWriteError(e1, bean, path.segment(0));
    }
  }
}
