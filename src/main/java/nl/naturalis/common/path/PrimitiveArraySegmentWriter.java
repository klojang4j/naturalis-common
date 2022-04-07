package nl.naturalis.common.path;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.lang.reflect.Array;
import java.util.OptionalInt;
import java.util.function.Function;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.PathWalkerException.*;

final class PrimitiveArraySegmentWriter extends SegmentWriter<Object> {

  PrimitiveArraySegmentWriter(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  boolean write(Object obj, Path path, Object value) {
    String segment = path.segment(-1);
    if (isEmpty(segment)) {
      return deadEnd(() -> emptySegment(path));
    }
    OptionalInt opt = NumberMethods.toPlainInt(segment);
    if (opt.isEmpty()) {
      return deadEnd(() -> arrayIndexExpected(path));
    }
    int idx = opt.getAsInt();
    if (idx < Array.getLength(obj)) {
      Array.set(obj, idx, value);
      return true;
    }
    return deadEnd(() -> indexOutOfBounds(path));
  }

}
