package nl.naturalis.common.path;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.OptionalInt;
import java.util.function.Function;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.ErrorCode.*;
import static nl.naturalis.common.path.PathWalkerException.*;

final class ArraySegmentWriter extends SegmentWriter<Object[]> {

  ArraySegmentWriter(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  ErrorCode write(Object[] array, Path path, Object value) {
    String segment = path.segment(-1);
    if (isEmpty(segment)) {
      return error(EMPTY_SEGMENT, () -> emptySegment(path));
    }
    OptionalInt opt = NumberMethods.toPlainInt(segment);
    if (opt.isEmpty()) {
      return error(INDEX_EXPECTED, () -> indexExpected(path));
    }
    int idx = opt.getAsInt();
    if (idx < array.length) {
      array[idx] = value;
      return OK;
    }
    return error(INDEX_OUT_OF_BOUNDS, () -> indexOutOfBounds(path));
  }

}
