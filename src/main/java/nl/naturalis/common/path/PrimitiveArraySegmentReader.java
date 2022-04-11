package nl.naturalis.common.path;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.path.PathWalker.OnError;

import java.util.OptionalInt;
import java.util.function.Function;

import static java.lang.invoke.MethodHandles.arrayElementGetter;
import static java.lang.invoke.MethodHandles.arrayLength;
import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.ErrorCode.*;
import static nl.naturalis.common.path.PathWalkerException.*;

final class PrimitiveArraySegmentReader extends SegmentReader<Object> {

  PrimitiveArraySegmentReader(OnError oe, Function<Path, Object> kd) {
    super(oe, kd);
  }

  @Override
  Object read(Object array, Path path) throws Throwable {
    if (path.size() == 1) { // primitive *must* be the end of the trail
      String segment = path.segment(0);
      if (isEmpty(segment)) {
        return error(EMPTY_SEGMENT, () -> emptySegment(path));
      }
      OptionalInt opt = NumberMethods.toPlainInt(segment);
      if (opt.isEmpty()) {
        return error(INDEX_EXPECTED, () -> indexExpected(path));
      }
      int idx = opt.getAsInt();
      if (idx < (int) arrayLength(array.getClass()).invoke(array)) {
        Object val = arrayElementGetter(array.getClass()).invoke(array, idx);
        return nextSegmentReader().read(val, path.shift());
      }
      return error(INDEX_OUT_OF_BOUNDS, () -> indexOutOfBounds(path));
    }
    return error(TERMINAL_VALUE, () -> terminalValue(path));
  }

}
