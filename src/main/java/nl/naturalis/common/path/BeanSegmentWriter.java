package nl.naturalis.common.path;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.PathWalkerException.emptySegment;
import static nl.naturalis.common.path.PathWalkerException.wrap;

import java.util.function.Function;

import nl.naturalis.common.invoke.BeanWriter;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

final class BeanSegmentWriter<T> extends SegmentWriter<T> {

  BeanSegmentWriter(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  @SuppressWarnings("unchecked")
  boolean write(T bean, Path path, Object value) {
    String segment = path.segment(-1);
    if (isEmpty(segment)) {
      return deadEnd(() -> emptySegment(path));
    }
    BeanWriter<T> bw = new BeanWriter<>((Class<T>) bean.getClass());
    try {
      bw.set(bean, segment, value);
      return true;
    } catch (Throwable t) {
      return deadEnd(() -> wrap(t));
    }
  }

}
