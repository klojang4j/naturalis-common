package nl.naturalis.common.path;

import java.util.Map;
import nl.naturalis.common.path.PathWalker.DeadEndAction;

class MapSegmentReader extends SegmentReader<Map<String, Object>> {

  MapSegmentReader(Path path, DeadEndAction deadEndAction) {
    super(path, deadEndAction);
  }

  @Override
  Object read(Map<String, Object> obj) {
    return null;
  }
}
