package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.Map;
import java.util.function.Function;

import static nl.naturalis.common.path.DeadEnd.OK;

@SuppressWarnings("rawtypes")
final class MapSegmentWriter extends SegmentWriter<Map> {

  MapSegmentWriter(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  DeadEnd write(Map map, Path path, Object value) {
    String segment = path.segment(-1);
    Object key = segment == null
        ? null
        : kds == null
            ? segment
            : kds.apply(path);
    map.put(key, value);
    return OK;
  }

}
