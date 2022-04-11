package nl.naturalis.common.path;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.PathWalkerException.*;
import static nl.naturalis.common.path.ErrorCode.*;

import java.util.function.Function;

import nl.naturalis.common.invoke.BeanWriter;
import nl.naturalis.common.invoke.NoPublicSettersException;
import nl.naturalis.common.path.PathWalker.OnError;

@SuppressWarnings({"rawtypes", "unchecked"})
final class BeanSegmentWriter extends SegmentWriter<Object> {

  BeanSegmentWriter(OnError oe, Function<Path, Object> kd) {
    super(oe, kd);
  }

  @Override
  ErrorCode write(Object bean, Path path, Object value) throws Throwable {
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
    bw.set(bean, segment, value);
    return error(OK, null);
  }

}
