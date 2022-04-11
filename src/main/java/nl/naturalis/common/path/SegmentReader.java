package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnError;

import java.util.function.Function;
import java.util.function.Supplier;

abstract sealed class SegmentReader<T> permits ArraySegmentReader, BeanSegmentReader,
    CollectionSegmentReader, MapSegmentReader, PrimitiveArraySegmentReader {

  private final OnError oe;
  private final Function<Path, Object> kd;

  SegmentReader(OnError oe, Function<Path, Object> kd) {
    this.oe = oe;
    this.kd = kd;
  }

  abstract Object read(T obj, Path path) throws Throwable;

  ErrorCode error(ErrorCode code, Supplier<PathWalkerException> exc) {
    return PathWalkerException.error(oe, code, exc);
  }

  ObjectReader nextSegmentReader() {
    return new ObjectReader(oe, kd);
  }

  Function<Path, Object> keyDeserializer() {
    return kd;
  }

}
