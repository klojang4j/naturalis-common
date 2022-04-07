package nl.naturalis.common.path;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.PathWalkerException.*;

import java.lang.reflect.Array;
import java.util.OptionalInt;
import java.util.function.Function;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

final class PrimitiveArraySegmentReader extends SegmentReader<Object> {

  PrimitiveArraySegmentReader(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  Object read(Object array, Path path) {
    if (path.size() == 1) { // primitive *must* be the end of the trail
      String segment = path.segment(0);
      if (isEmpty(segment)) {
        return deadEnd(() -> emptySegment(path));
      }
      OptionalInt opt = NumberMethods.toPlainInt(segment);
      if (opt.isEmpty()) {
        return deadEnd(() -> arrayIndexExpected(path));
      }
      int idx = opt.getAsInt();
      if (idx < Array.getLength(array)) {
        Object val = Array.get(array, idx);
        return nextSegmentReader().read(val, path.shift());
      }
      return deadEnd(() -> indexOutOfBounds(path));
    }
    return deadEnd(() -> pathExtendsBeyondPrimitive(path));
  }

}
