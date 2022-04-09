package nl.naturalis.common.path;

import nl.naturalis.common.invoke.BeanReader;
import nl.naturalis.common.invoke.InvokeException;
import nl.naturalis.common.invoke.NoPublicGettersException;
import nl.naturalis.common.invoke.NoSuchPropertyException;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.function.Function;

import static nl.naturalis.common.path.ErrorCode.*;
import static nl.naturalis.common.path.PathWalkerException.*;

final class BeanSegmentReader extends SegmentReader<Object> {

  BeanSegmentReader(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  @SuppressWarnings("unchecked")
  Object read(Object bean, Path path) {
    BeanReader reader;
    try {
      reader = new BeanReader(bean.getClass());
    } catch (NoPublicGettersException e) {
      return deadEnd(TERMINAL_VALUE, () -> terminalValue(path));
    }
    try {
      Object val = reader.read(bean, path.segment(0));
      return nextSegmentReader().read(val, path.shift());
    } catch (NoSuchPropertyException e) {
      return deadEnd(NO_SUCH_PROPERTY, () -> noSuchProperty(path));
    } catch (Throwable t) {
      return deadEnd(EXCEPTION, () -> readError(path, t));
    }

  }

}
