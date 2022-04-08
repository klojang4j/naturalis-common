package nl.naturalis.common.path;

import nl.naturalis.common.invoke.BeanReader;
import nl.naturalis.common.invoke.InvokeException;
import nl.naturalis.common.invoke.NoSuchPropertyException;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.function.Function;

import static nl.naturalis.common.path.DeadEnd.*;
import static nl.naturalis.common.path.DeadEndException.*;

final class BeanSegmentReader<T> extends SegmentReader<T> {

  BeanSegmentReader(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  @SuppressWarnings("unchecked")
  Object read(T bean, Path path) {
    BeanReader<T> reader;
    try {
      reader = new BeanReader<>((Class<T>) bean.getClass());
    } catch (InvokeException e) {
      return deadEnd(TERMINAL_VALUE, () -> terminalValue(path));
    }
    try {
      Object val = reader.read(bean, path.segment(0));
      return nextSegmentReader().read(val, path.shift());
    } catch (NoSuchPropertyException e) {
      return deadEnd(NO_SUCH_PROPERTY, () -> noSuchProperty(path));
    } catch (Throwable t) {
      return deadEnd(READ_ERROR, () -> readError(path, t));
    }

  }

}
