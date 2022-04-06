package nl.naturalis.common.path;

import static nl.naturalis.common.path.PathWalkerException.arrayIndexExpected;

import java.util.function.Function;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

final class ArraySegmentReader extends SegmentReader<Object[]> {

  ArraySegmentReader(OnDeadEnd deadEndAction, Function<Path, Object> keyDeserializer) {
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
