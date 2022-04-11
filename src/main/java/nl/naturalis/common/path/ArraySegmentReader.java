package nl.naturalis.common.path;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.path.PathWalker.OnError;

import java.util.OptionalInt;
import java.util.function.Function;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.PathWalkerException.*;
import static nl.naturalis.common.path.ErrorCode.*;

final class ArraySegmentReader extends SegmentReader<Object[]> {

  ArraySegmentReader(OnError oe, Function<Path, Object> kd) {
    super(oe, kd);
  }

  @Override
  Object read(Object[] array, Path path) {
    String segment = path.segment(0);
    if (isEmpty(segment)) {
      return error(EMPTY_SEGMENT, () -> emptySegment(path));
    }
    OptionalInt opt = NumberMethods.toPlainInt(segment);
    if (opt.isEmpty()) {
      return error(INDEX_EXPECTED, () -> indexExpected(path));
    }
    int idx = opt.getAsInt();
    if (idx < array.length) {
      return nextSegmentReader().read(array[idx], path.shift());
    }
    return error(INDEX_OUT_OF_BOUNDS, () -> indexOutOfBounds(path));
  }

}
