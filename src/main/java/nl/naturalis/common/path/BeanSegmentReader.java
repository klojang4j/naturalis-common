package nl.naturalis.common.path;

import nl.naturalis.common.invoke.BeanReader;
import nl.naturalis.common.invoke.NoSuchPropertyException;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.function.Function;

import static nl.naturalis.common.path.PathWalkerException.noSuchProperty;
import static nl.naturalis.common.path.PathWalkerException.wrap;

final class BeanSegmentReader<T> extends SegmentReader<T> {

  BeanSegmentReader(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  @SuppressWarnings("unchecked")
  Object read(T bean, Path path) {
    BeanReader<T> reader = new BeanReader<>((Class<T>) bean.getClass());
    try {
      try {
        Object val = reader.read(bean, path.segment(0));
        return nextSegmentReader().read(val, path.shift());
      } catch (NoSuchPropertyException e) {
        return deadEnd(() -> noSuchProperty(path, e));
      }
    } catch (Throwable t) {
      return deadEnd(() -> wrap(t));
    }
  }

}
