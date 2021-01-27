package nl.naturalis.common.path;

import java.util.Map;
import java.util.function.Function;
import nl.naturalis.common.path.PathWalker.DeadEndAction;
import static nl.naturalis.common.path.PathWalkerException.invalidType;

@SuppressWarnings("rawtypes")
class MapSegmentWriter extends SegmentWriter<Map> {

  MapSegmentWriter(DeadEndAction deadEndAction, Function<Path, Object> keyDeserializer) {
    super(deadEndAction, keyDeserializer);
  }

  @Override
  boolean write(Map map, Path path, Object value) {
    String segment = path.segment(-1);
    Object key = segment == null ? null : kds == null ? segment : kds.apply(path);
    try {
      map.put(key, value);
      return true;
    } catch (ClassCastException e) {
      return deadEnd(() -> invalidType(path, e));
    }
  }
}
