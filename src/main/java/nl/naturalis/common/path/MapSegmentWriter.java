package nl.naturalis.common.path;

import static nl.naturalis.common.path.PathWalkerException.invalidType;

import java.util.Map;
import java.util.function.Function;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

@SuppressWarnings("rawtypes")
final class MapSegmentWriter extends SegmentWriter<Map> {

  MapSegmentWriter(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  boolean write(Map map, Path path, Object value) {
    String segment = path.segment(-1);
    Object key = segment == null
        ? null
        : kds == null
            ? segment
            : kds.apply(path);
    try {
      map.put(key, value);
      return true;
    } catch (ClassCastException e) {
      return deadEnd(() -> invalidType(path, e));
    }
  }

}
