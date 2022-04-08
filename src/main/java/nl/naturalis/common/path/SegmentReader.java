package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.function.Function;
import java.util.function.Supplier;

abstract sealed class SegmentReader<T> permits ArraySegmentReader, BeanSegmentReader,
    CollectionSegmentReader, MapSegmentReader, PrimitiveArraySegmentReader {

  static DeadEnd deadEnd(OnDeadEnd ode, DeadEnd deadEnd, Supplier<DeadEndException> exc) {
    return switch (ode) {
      case RETURN_NULL -> null;
      case RETURN_DEAD -> deadEnd;
      case THROW_EXCEPTION -> throw exc.get();
    };
  }

  final Function<Path, Object> kds;

  private final OnDeadEnd ode;

  SegmentReader(OnDeadEnd ode, Function<Path, Object> kds) {
    this.ode = ode;
    this.kds = kds;
  }

  abstract Object read(T obj, Path path);

  DeadEnd deadEnd(DeadEnd deadEnd, Supplier<DeadEndException> exc) {
    return deadEnd(ode, deadEnd, exc);
  }

  ObjectReader nextSegmentReader() {
    return new ObjectReader(ode, kds);
  }

}
