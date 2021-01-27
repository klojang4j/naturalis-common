package nl.naturalis.common.path;

import java.lang.reflect.Array;
import java.util.function.Function;
import nl.naturalis.common.path.PathWalker.DeadEndAction;
import static nl.naturalis.common.path.PathWalkerException.arrayIndexExpected;
import static nl.naturalis.common.path.PathWalkerException.arrayIndexOutOfBounds;
import static nl.naturalis.common.path.PathWalkerException.nullInvalidForPrimitiveArray;

class PrimitiveArraySegmentWriter extends SegmentWriter<Object> {

  PrimitiveArraySegmentWriter(DeadEndAction deadEndAction, Function<Path, Object> keyDeserializer) {
    super(deadEndAction, keyDeserializer);
  }

  @Override
  boolean write(Object obj, Path path, Object value) {
    if (!Path.isArrayIndex(path.segment(-1))) {
      return deadEnd(() -> arrayIndexExpected(path));
    }
    if (value == null) {
      return deadEnd(() -> nullInvalidForPrimitiveArray(path, obj));
    }
    int idx = Integer.parseInt(path.segment(-1));
    if (idx > 0 && idx < Array.getLength(obj)) {
      Array.set(obj, idx, value);
    }
    return deadEnd(() -> arrayIndexOutOfBounds(path));
  }
}
