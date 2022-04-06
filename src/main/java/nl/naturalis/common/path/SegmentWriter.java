package nl.naturalis.common.path;

import java.util.function.Function;
import java.util.function.Supplier;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

abstract sealed class SegmentWriter<T> permits ArraySegmentWriter, BeanSegmentWriter,
    ListSegmentWriter, MapSegmentWriter, PrimitiveArraySegmentWriter {

  OnDeadEnd dea;
  Function<Path, Object> kds;

  SegmentWriter(OnDeadEnd deadEndAction, Function<Path, Object> keyDeserializer) {
    this.dea = deadEndAction;
    this.kds = keyDeserializer;
  }

  abstract boolean write(T obj, Path path, Object value);

  boolean deadEnd(Supplier<PathWalkerException> e) {
    if (dea == OnDeadEnd.THROW_EXCEPTION) {
      throw e.get();
    }
    return false;
  }

}
