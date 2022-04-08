package nl.naturalis.common.path;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.DeadEndException.*;
import static nl.naturalis.common.path.DeadEnd.*;

import java.util.function.Function;

import nl.naturalis.common.invoke.BeanWriter;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

final class BeanSegmentWriter<T> extends SegmentWriter<T> {

  BeanSegmentWriter(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  @SuppressWarnings("unchecked")
  DeadEnd write(T bean, Path path, Object value) {
    String segment = path.segment(-1);
    if (isEmpty(segment)) {
      return deadEnd(EMPTY_SEGMENT, () -> emptySegment(path));
    }
    BeanWriter<T> bw = new BeanWriter<>((Class<T>) bean.getClass());
    try {
      bw.set(bean, segment, value);
      return OK;
    } catch (Throwable t) {
      return deadEnd(READ_ERROR, () -> readError(path, t));
    }
  }

}
