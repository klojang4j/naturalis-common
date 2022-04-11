package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnError;

import java.util.Map;
import java.util.function.Function;

import static nl.naturalis.common.path.ErrorCode.*;
import static nl.naturalis.common.path.PathWalkerException.exception;

@SuppressWarnings({"rawtypes", "unchecked"})
final class MapSegmentWriter extends SegmentWriter<Map> {

  MapSegmentWriter(OnError oe, Function<Path, Object> kd) {
    super(oe, kd);
  }

  @Override
  ErrorCode write(Map map, Path path, Object value) {
    String segment = path.segment(-1);
    Object k;
    if (segment == null) {
      k = null;
    } else if (keyDeserializer() == null) {
      k = segment;
    } else {
      try {
        k = keyDeserializer().apply(path);
      } catch (Throwable t) {
        return error(EXCEPTION, () -> exception(path, t));
      }
    }
    map.put(k, value);
    return error(OK, null);
  }

}
