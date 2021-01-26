package nl.naturalis.common.path;

import java.util.Map;
import java.util.function.Function;
import nl.naturalis.common.invoke.NoSuchPropertyException;
import nl.naturalis.common.path.PathWalker.DeadEndAction;

@SuppressWarnings("rawtypes")
class MapSegmentReader extends SegmentReader<Map<String, Object>> {

  MapSegmentReader(DeadEndAction deadEndAction, Function<Path, Object> keyDeserializer) {
    super(deadEndAction, keyDeserializer);
  }

  @Override
  Object read(Map map, Path path) {
    String segment = path.segment(0);
    Object key = kds == null ? segment : kds.apply(path);
    if (map.containsKey(key)) {
      return nextSegmentReader().read(map.get(key), path.shift());
    }
    return deadEnd(new NoSuchPropertyException(path.toString()));
  }
}
