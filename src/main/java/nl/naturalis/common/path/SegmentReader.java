package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.function.Function;
import java.util.function.Supplier;

abstract sealed class SegmentReader<T> permits ArraySegmentReader, BeanSegmentReader,
    CollectionSegmentReader, MapSegmentReader, PrimitiveArraySegmentReader {

  final Function<Path, Object> kds;

  private final OnDeadEnd ode;

  SegmentReader(OnDeadEnd ode, Function<Path, Object> kds) {
    this.ode = ode;
    this.kds = kds;
  }

  abstract Object read(T obj, Path path);

  Object deadEnd(DeadEnd deadEnd, Supplier<DeadEndException> e) {
    return switch (ode) {
      case RETURN_NULL -> null;
      case RETURN_DEAD -> deadEnd;
      case THROW_EXCEPTION -> throw e.get();
    };
  }

  ObjectReader nextSegmentReader() {
    return new ObjectReader(ode, kds);
  }

}
