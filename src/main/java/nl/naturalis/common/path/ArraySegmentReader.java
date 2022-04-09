package nl.naturalis.common.path;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.OptionalInt;
import java.util.function.Function;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.PathWalkerException.*;
import static nl.naturalis.common.path.ErrorCode.*;

final class ArraySegmentReader extends SegmentReader<Object[]> {

  ArraySegmentReader(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  Object read(Object[] array, Path path) {
    String segment = path.segment(0);
    if (isEmpty(segment)) {
      return deadEnd(EMPTY_SEGMENT, () -> emptySegment(path));
    }
    OptionalInt opt = NumberMethods.toPlainInt(segment);
    if (opt.isEmpty()) {
      return deadEnd(INDEX_EXPECTED, () -> indexExpected(path));
    }
    int idx = opt.getAsInt();
    if (idx < array.length) {
      return nextSegmentReader().read(array[idx], path.shift());
    }
    return deadEnd(INDEX_OUT_OF_BOUNDS, () -> indexOutOfBounds(path));
  }

}
