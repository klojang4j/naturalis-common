package nl.naturalis.common.path;

import java.util.function.Function;
import nl.naturalis.common.path.PathWalker.DeadEndAction;
import static nl.naturalis.common.path.PathWalkerException.arrayIndexExpected;

class ArraySegmentReader extends SegmentReader<Object[]> {

  ArraySegmentReader(DeadEndAction deadEndAction, Function<Path, Object> keyDeserializer) {
    super(deadEndAction, keyDeserializer);
  }

  @Override
  Object read(Object[] array, Path path) {
    String segment = path.segment(0);
    if (Path.isArrayIndex(segment)) {
      int idx = Integer.parseInt(segment);
      if (idx < array.length) {
        return nextSegmentReader().read(array[idx], path.shift());
      }
    }
    return deadEnd(() -> arrayIndexExpected(path));
  }
}