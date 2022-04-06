package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.function.Function;

import static nl.naturalis.common.path.PathWalkerException.arrayIndexExpected;

final class ArraySegmentReader extends SegmentReader<Object[]> {

  ArraySegmentReader(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
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
