package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static nl.naturalis.common.ClassMethods.isPrimitiveArray;

final class ObjectReader {

  private final OnDeadEnd ode;
  private final Function<Path, Object> kds;

  ObjectReader(OnDeadEnd ode, Function<Path, Object> kds) {
    this.ode = ode;
    this.kds = kds;
  }

  Object read(Object obj, Path path) {
    if (path.isEmpty() || obj == null || obj instanceof ErrorCode) {
      return obj;
    } else if (obj instanceof Collection c) {
      return new CollectionSegmentReader(ode, kds).read(c, path);
    } else if (obj instanceof Object[] o) {
      return new ArraySegmentReader(ode, kds).read(o, path);
    } else if (obj instanceof Map m) {
      return new MapSegmentReader(ode, kds).read(m, path);
    } else if (isPrimitiveArray(obj)) {
      return new PrimitiveArraySegmentReader(ode, kds).read(obj, path);
    } else {
      return new BeanSegmentReader(ode, kds).read(obj, path);
    }
  }

}
