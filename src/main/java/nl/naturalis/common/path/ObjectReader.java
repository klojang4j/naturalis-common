package nl.naturalis.common.path;

import static nl.naturalis.common.ClassMethods.isPrimitiveArray;
import static nl.naturalis.common.path.PathWalker.DEAD;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

@SuppressWarnings("rawtypes")
final class ObjectReader {

  OnDeadEnd ode;
  Function<Path, Object> kds;

  ObjectReader(OnDeadEnd ode, Function<Path, Object> kds) {
    this.ode = ode;
    this.kds = kds;
  }

  Object read(Object obj, Path path) {
    if (path.isEmpty() || obj == null || obj instanceof DeadEnd) {
      return obj;
    } else if (obj instanceof Collection) {
      return new CollectionSegmentReader(ode, kds).read((Collection) obj, path);
    } else if (obj instanceof Object[]) {
      return new ArraySegmentReader(ode, kds).read((Object[]) obj, path);
    } else if (obj instanceof Map) {
      return new MapSegmentReader(ode, kds).read((Map) obj, path);
    } else if (isPrimitiveArray(obj)) {
      return new PrimitiveArraySegmentReader(ode, kds).read(obj, path);
    } else {
      return new BeanSegmentReader<>(ode, kds).read(obj, path);
    }
  }

}
