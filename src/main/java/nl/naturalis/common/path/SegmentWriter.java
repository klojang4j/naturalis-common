package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnError;

import java.util.function.Function;
import java.util.function.Supplier;

abstract sealed class SegmentWriter<T> permits ArraySegmentWriter, BeanSegmentWriter,
    ListSegmentWriter, MapSegmentWriter, PrimitiveArraySegmentWriter {

  private final OnError oe;
  private final Function<Path, Object> kd;

  SegmentWriter(OnError oe, Function<Path, Object> kd) {
    this.oe = oe;
    this.kd = kd;
  }

  abstract ErrorCode write(T obj, Path path, Object value) throws Throwable;

  ErrorCode error(ErrorCode code, Supplier<PathWalkerException> exc) {
    return PathWalkerException.error(oe, code, exc);
  }

  Function<Path, Object> keyDeserializer() {
    return kd;
  }

}
