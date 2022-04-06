package nl.naturalis.common.path;

import static nl.naturalis.common.path.PathWalkerException.noSuchKey;

import java.util.Map;
import java.util.function.Function;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

@SuppressWarnings("rawtypes")
final class MapSegmentReader extends SegmentReader<Map<String, Object>> {

  MapSegmentReader(OnDeadEnd deadEndAction, Function<Path, Object> keyDeserializer) {
    super(deadEndAction, keyDeserializer);
  }

  @Override
  Object read(Map map, Path path) {
    String segment = path.segment(0);
    Object key = kds == null
        ? segment
        : kds.apply(path);
    if (map.containsKey(key)) {
      return nextSegmentReader().read(map.get(key), path.shift());
    }
    return deadEnd(() -> noSuchKey(path));
  }

}
