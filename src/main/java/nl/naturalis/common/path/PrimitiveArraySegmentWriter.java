package nl.naturalis.common.path;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.path.PathWalker.OnError;

import java.util.OptionalInt;
import java.util.function.Function;

import static java.lang.invoke.MethodHandles.arrayElementSetter;
import static java.lang.invoke.MethodHandles.arrayLength;
import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.ErrorCode.*;
import static nl.naturalis.common.path.PathWalkerException.*;

final class PrimitiveArraySegmentWriter extends SegmentWriter<Object> {

  PrimitiveArraySegmentWriter(OnError oe, Function<Path, Object> kd) {
    super(oe, kd);
  }

  @Override
  ErrorCode write(Object array, Path path, Object value) throws Throwable {
    String segment = path.segment(-1);
    if (isEmpty(segment)) {
      return error(EMPTY_SEGMENT, () -> emptySegment(path));
    }
    OptionalInt opt = NumberMethods.toPlainInt(segment);
    if (opt.isEmpty()) {
      return error(INDEX_EXPECTED, () -> indexExpected(path));
    }
    int idx = opt.getAsInt();
    if (idx < (int) arrayLength(array.getClass()).invoke(array)) {
      arrayElementSetter(array.getClass()).invoke(array, idx, value);
      return error(OK, null);
    }
    return error(INDEX_OUT_OF_BOUNDS, () -> indexOutOfBounds(path));
  }

}
