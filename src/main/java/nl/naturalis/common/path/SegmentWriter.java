package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnError;

import java.util.function.Function;
import java.util.function.Supplier;

abstract sealed class SegmentWriter<T> permits ArraySegmentWriter, BeanSegmentWriter,
    ListSegmentWriter, MapSegmentWriter, PrimitiveArraySegmentWriter {

  static ErrorCode error(OnError oe, ErrorCode code, Supplier<PathWalkerException> exc) {
    return switch (oe) {
      case RETURN_NULL -> null;
      case RETURN_CODE -> code;
      case THROW_EXCEPTION -> throw exc.get();
    };
  }

  private final OnError oe;
  private final Function<Path, Object> kd;

  SegmentWriter(OnError oe, Function<Path, Object> kd) {
    this.oe = oe;
    this.kd = kd;
  }

  abstract ErrorCode write(T obj, Path path, Object value);

  ErrorCode error(ErrorCode code, Supplier<PathWalkerException> exc) {
    return error(oe, code, exc);
  }

  Function<Path, Object> keyDeserializer() {
    return kd;
  }

}
