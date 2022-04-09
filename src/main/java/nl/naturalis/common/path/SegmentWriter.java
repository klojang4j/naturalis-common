package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.function.Function;
import java.util.function.Supplier;

abstract sealed class SegmentWriter<T> permits ArraySegmentWriter, BeanSegmentWriter,
    ListSegmentWriter, MapSegmentWriter, PrimitiveArraySegmentWriter {

  static ErrorCode error(OnDeadEnd ode, ErrorCode deadEnd, Supplier<PathWalkerException> exc) {
    return switch (ode) {
      case RETURN_NULL -> null;
      case RETURN_CODE -> deadEnd;
      case THROW_EXCEPTION -> throw exc.get();
    };
  }

  private final OnDeadEnd ode;
  private final Function<Path, Object> kds;

  SegmentWriter(OnDeadEnd ode, Function<Path, Object> kds) {
    this.ode = ode;
    this.kds = kds;
  }

  abstract ErrorCode write(T obj, Path path, Object value);

  ErrorCode error(ErrorCode code, Supplier<PathWalkerException> exc) {
    return error(ode, code, exc);
  }

  Function<Path, Object> keyDeserializer() {
    return kds;
  }

}
