package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.Map;
import java.util.function.Function;

import static nl.naturalis.common.path.DeadEnd.NO_SUCH_KEY;
import static nl.naturalis.common.path.DeadEndException.noSuchKey;

@SuppressWarnings("rawtypes")
final class MapSegmentReader extends SegmentReader<Map<String, Object>> {

  MapSegmentReader(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
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
    return deadEnd(NO_SUCH_KEY, () -> noSuchKey(path));
  }

}
