package nl.naturalis.common.path;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.PathWalkerException.*;
import static nl.naturalis.common.path.ErrorCode.*;

import java.util.function.Function;

import nl.naturalis.common.invoke.BeanWriter;
import nl.naturalis.common.invoke.NoPublicSettersException;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

final class BeanSegmentWriter extends SegmentWriter<Object> {

  BeanSegmentWriter(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  @SuppressWarnings("unchecked")
  ErrorCode write(Object bean, Path path, Object value) {
    String segment = path.segment(-1);
    if (isEmpty(segment)) {
      return error(EMPTY_SEGMENT, () -> emptySegment(path));
    }
    BeanWriter bw;
    try {
      bw = new BeanWriter(bean.getClass());
    } catch (NoPublicSettersException e) {
      return error(TERMINAL_VALUE, () -> terminalValue(path));
    }
    try {
      bw.set(bean, segment, value);
      return OK;
    } catch (Throwable t) {
      return error(EXCEPTION, () -> readError(path, t));
    }
  }

}
