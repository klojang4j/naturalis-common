package nl.naturalis.common.path;

import static nl.naturalis.common.ClassMethods.isPrimitiveArray;
import static nl.naturalis.common.path.PathWalker.DEAD_END;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import nl.naturalis.common.path.PathWalker.DeadEndAction;

@SuppressWarnings("rawtypes")
final class ObjectReader {

  DeadEndAction dea;
  Function<Path, Object> kds;

  ObjectReader(DeadEndAction deadEndAction, Function<Path, Object> keyDeserializer) {
    this.dea = deadEndAction;
    this.kds = keyDeserializer;
  }

  Object read(Object obj, Path path) {
    if (path.isEmpty() || obj == null || obj == DEAD_END) {
      return obj;
    } else if (obj instanceof Collection) {
      return new CollectionSegmentReader(dea, kds).read((Collection) obj, path);
    } else if (obj instanceof Object[]) {
      return new ArraySegmentReader(dea, kds).read((Object[]) obj, path);
    } else if (obj instanceof Map) {
      return new MapSegmentReader(dea, kds).read((Map) obj, path);
    } else if (isPrimitiveArray(obj)) {
      return new PrimitiveArraySegmentReader(dea, kds).read(obj, path);
    } else {
      return new BeanSegmentReader<>(dea, kds).read(obj, path);
    }
  }

}
