package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.function.Function;
import java.util.function.Supplier;

abstract sealed class SegmentReader<T> permits ArraySegmentReader, BeanSegmentReader,
    CollectionSegmentReader, MapSegmentReader, PrimitiveArraySegmentReader {

  private final OnDeadEnd ode;
  private final Function<Path, Object> kds;

  SegmentReader(OnDeadEnd ode, Function<Path, Object> kds) {
    this.ode = ode;
    this.kds = kds;
  }

  abstract Object read(T obj, Path path);

  ErrorCode deadEnd(ErrorCode code, Supplier<PathWalkerException> exc) {
    return switch (ode) {
      case RETURN_NULL -> null;
      case RETURN_CODE -> code;
      case THROW_EXCEPTION -> throw exc.get();
    };
  }

  ObjectReader nextSegmentReader() {
    return new ObjectReader(ode, kds);
  }

  Function<Path, Object> keyDeserializer() {
    return kds;
  }

}
