package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnError;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static nl.naturalis.common.ClassMethods.isPrimitiveArray;

final class ObjectReader {

  private final OnError oe;
  private final Function<Path, Object> kd;

  ObjectReader(OnError onError, Function<Path, Object> keyDeserializer) {
    this.oe = onError;
    this.kd = keyDeserializer;
  }

  Object read(Object obj, Path path) {
    if (path.isEmpty() || obj == null || obj instanceof ErrorCode) {
      return obj;
    } else if (obj instanceof Collection c) {
      return new CollectionSegmentReader(oe, kd).read(c, path);
    } else if (obj instanceof Object[] o) {
      return new ArraySegmentReader(oe, kd).read(o, path);
    } else if (obj instanceof Map m) {
      return new MapSegmentReader(oe, kd).read(m, path);
    } else if (isPrimitiveArray(obj)) {
      return new PrimitiveArraySegmentReader(oe, kd).read(obj, path);
    } else {
      return new BeanSegmentReader(oe, kd).read(obj, path);
    }
  }

}
