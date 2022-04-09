package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.Map;
import java.util.function.Function;

import static nl.naturalis.common.path.ErrorCode.OK;

@SuppressWarnings("rawtypes")
final class MapSegmentWriter extends SegmentWriter<Map> {

  MapSegmentWriter(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  ErrorCode write(Map map, Path path, Object value) {
    String segment = path.segment(-1);
    Object key = segment == null
        ? null
        : keyDeserializer() == null
            ? segment
            : keyDeserializer().apply(path);
    map.put(key, value);
    return OK;
  }

}
