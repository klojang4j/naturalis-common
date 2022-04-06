package nl.naturalis.common.path;

import static nl.naturalis.common.path.PathWalkerException.pathExtendsBeyondPrimitive;

import java.lang.reflect.Array;
import java.util.function.Function;

import nl.naturalis.common.path.PathWalker.DeadEndAction;

final class PrimitiveArraySegmentReader extends SegmentReader<Object> {

  PrimitiveArraySegmentReader(DeadEndAction deadEndAction, Function<Path, Object> keyDeserializer) {
    super(deadEndAction, keyDeserializer);
  }

  @Override
  Object read(Object array, Path path) {
    if (path.size() == 1) { // primitive *must* be the end of the trail
      String segment = path.segment(0);
      if (Path.isArrayIndex(segment)) {
        int idx = Integer.parseInt(segment);
        if (idx < Array.getLength(array)) {
          Object val = Array.get(array, idx);
          return nextSegmentReader().read(val, path.shift());
        }
      }
    }
    return deadEnd(() -> pathExtendsBeyondPrimitive(path));
  }

}
