package nl.naturalis.common.path;

import nl.naturalis.common.invoke.NoSuchPropertyException;
import nl.naturalis.common.path.PathWalker.DeadEndAction;
import static nl.naturalis.common.path.PathWalker.DEAD_END;

abstract class SegmentReader<T> {

  final Path path;
  final DeadEndAction deadEndAction;

  SegmentReader(Path path, DeadEndAction deadEndAction) {
    this.path = path;
    this.deadEndAction = deadEndAction;
  }

  abstract Object read(T obj);

  protected Object deadEnd(NoSuchPropertyException e) {
    switch (deadEndAction) {
      case RETURN_NULL:
        return null;
      case RETURN_DEAD_END:
        return DEAD_END;
      case THROW_EXCEPTION:
      default:
        throw e;
    }
  }
}
