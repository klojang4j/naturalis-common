package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.function.Function;
import java.util.function.Supplier;

import static nl.naturalis.common.path.PathWalker.DEAD;

abstract sealed class SegmentReader<T> permits ArraySegmentReader, BeanSegmentReader,
    CollectionSegmentReader, MapSegmentReader, PrimitiveArraySegmentReader {

  OnDeadEnd dea;
  Function<Path, Object> kds;

  SegmentReader(OnDeadEnd deadEndAction, Function<Path, Object> keyDeserializer) {
    this.dea = deadEndAction;
    this.kds = keyDeserializer;
  }

  abstract Object read(T obj, Path path);

  Object deadEnd(Supplier<PathWalkerException> e) {
    switch (dea) {
      case RETURN_NULL:
        return null;
      case RETURN_DEAD:
        return DEAD;
      case THROW_EXCEPTION:
      default:
        throw e.get();
    }
  }

  ObjectReader nextSegmentReader() {
    return new ObjectReader(dea, kds);
  }

}
