package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnError;

import java.util.function.Function;
import java.util.function.Supplier;

import static nl.naturalis.common.path.ErrorCode.OK;
import static nl.naturalis.common.path.PathWalker.OnError.RETURN_CODE;
import static nl.naturalis.common.path.PathWalker.OnError.RETURN_NULL;

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
    if (oe == RETURN_CODE) {
      return code;
    } else if (oe == RETURN_NULL || code == OK) {
      return null;
    }
    throw exc.get();
  }

  ObjectReader nextSegmentReader() {
    return new ObjectReader(oe, kd);
  }

  Function<Path, Object> keyDeserializer() {
    return kd;
  }

}
