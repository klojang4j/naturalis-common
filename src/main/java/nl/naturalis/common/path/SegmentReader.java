package nl.naturalis.common.path;

import java.util.function.Function;
import nl.naturalis.common.invoke.NoSuchPropertyException;
import nl.naturalis.common.path.PathWalker.DeadEndAction;
import static nl.naturalis.common.path.PathWalker.DEAD_END;

abstract class SegmentReader<T> {

  DeadEndAction dea;
  Function<Path, Object> kds;

  SegmentReader(DeadEndAction deadEndAction, Function<Path, Object> keyDeserializer) {
    this.dea = deadEndAction;
    this.kds = keyDeserializer;
  }

  abstract Object read(T obj, Path path);

  Object deadEnd(NoSuchPropertyException e) {
    switch (dea) {
      case RETURN_NULL:
        return null;
      case RETURN_DEAD_END:
        return DEAD_END;
      case THROW_EXCEPTION:
      default:
        throw e;
    }
  }

  ObjectReader nextSegmentReader() {
    return new ObjectReader(dea, kds);
  }
}
