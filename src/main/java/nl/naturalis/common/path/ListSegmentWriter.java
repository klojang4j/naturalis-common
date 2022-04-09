package nl.naturalis.common.path;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Function;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.ErrorCode.*;
import static nl.naturalis.common.path.PathWalkerException.*;

@SuppressWarnings("rawtypes")
final class ListSegmentWriter extends SegmentWriter<List> {

  ListSegmentWriter(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  ErrorCode write(List list, Path path, Object value) {
    String segment = path.segment(-1);
    if (isEmpty(segment)) {
      return error(EMPTY_SEGMENT, () -> emptySegment(path));
    }
    OptionalInt opt = NumberMethods.toPlainInt(segment);
    if (opt.isEmpty()) {
      return error(INDEX_EXPECTED, () -> indexExpected(path));
    }
    int idx = opt.getAsInt();
    if (idx < list.size()) {
      list.set(opt.getAsInt(), value);
      return OK;
    }
    return error(INDEX_OUT_OF_BOUNDS, () -> indexOutOfBounds(path));
  }

}
