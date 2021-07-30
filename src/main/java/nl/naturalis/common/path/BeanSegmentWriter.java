package nl.naturalis.common.path;

import static nl.naturalis.common.StringMethods.isEmpty;
import static nl.naturalis.common.path.PathWalkerException.emptySegment;
import static nl.naturalis.common.path.PathWalkerException.wrap;

import java.util.function.Function;
import nl.naturalis.common.invoke.BeanWriter;
import nl.naturalis.common.path.PathWalker.DeadEndAction;

class BeanSegmentWriter<T> extends SegmentWriter<T> {

  BeanSegmentWriter(DeadEndAction deadEndAction, Function<Path, Object> keyDeserializer) {
    super(deadEndAction, keyDeserializer);
  }

  @Override
  @SuppressWarnings("unchecked")
  boolean write(T bean, Path path, Object value) {
    String segment = path.segment(-1);
    if (isEmpty(segment)) {
      return deadEnd(() -> emptySegment(path));
    }
    Class<T> beanClass = (Class<T>) bean.getClass();
    BeanWriter<T> bw = new BeanWriter<>(beanClass, segment);
    try {
      bw.set(bean, segment, value);
      return true;
    } catch (Throwable t) {
      return deadEnd(() -> wrap(t));
    }
  }
}
