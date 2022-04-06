package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.function.Function;
import java.util.function.Supplier;

import static nl.naturalis.common.path.PathWalker.OnDeadEnd.THROW_EXCEPTION;

abstract sealed class SegmentWriter<T> permits ArraySegmentWriter, BeanSegmentWriter,
    ListSegmentWriter, MapSegmentWriter, PrimitiveArraySegmentWriter {

  final Function<Path, Object> kds;

  private final OnDeadEnd ode;

  SegmentWriter(OnDeadEnd ode, Function<Path, Object> kds) {
    this.ode = ode;
    this.kds = kds;
  }

  abstract boolean write(T obj, Path path, Object value);

  boolean deadEnd(Supplier<PathWalkerException> e) {
    if (ode == THROW_EXCEPTION) {
      throw e.get();
    }
    return false;
  }

}
