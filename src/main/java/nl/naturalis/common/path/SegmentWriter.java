package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.function.Function;
import java.util.function.Supplier;

abstract sealed class SegmentWriter<T> permits ArraySegmentWriter, BeanSegmentWriter,
    ListSegmentWriter, MapSegmentWriter, PrimitiveArraySegmentWriter {

  static DeadEnd deadEnd(OnDeadEnd ode, DeadEnd deadEnd, Supplier<DeadEndException> exc) {
    return switch (ode) {
      case RETURN_NULL -> null;
      case RETURN_DEAD -> deadEnd;
      case THROW_EXCEPTION -> throw exc.get();
    };
  }

  final Function<Path, Object> kds;

  private final OnDeadEnd ode;

  SegmentWriter(OnDeadEnd ode, Function<Path, Object> kds) {
    this.ode = ode;
    this.kds = kds;
  }

  abstract DeadEnd write(T obj, Path path, Object value);

  DeadEnd deadEnd(DeadEnd deadEnd, Supplier<DeadEndException> exc) {
    return deadEnd(ode, deadEnd, exc);
  }

}
