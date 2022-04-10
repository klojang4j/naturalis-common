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

  abstract Object read(T obj, Path path);

  ErrorCode deadEnd(ErrorCode code, Supplier<PathWalkerException> exc) {
    return switch (oe) {
      case RETURN_NULL -> null;
      case RETURN_CODE -> code;
      case THROW_EXCEPTION -> throw exc.get();
    };
  }

  ObjectReader nextSegmentReader() {
    return new ObjectReader(oe, kd);
  }

  Function<Path, Object> keyDeserializer() {
    return kd;
  }

}
