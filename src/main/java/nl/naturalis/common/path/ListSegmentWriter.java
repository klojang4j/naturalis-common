package nl.naturalis.common.path;

import static nl.naturalis.common.StringMethods.isEmpty;
import static nl.naturalis.common.path.PathWalkerException.arrayIndexExpected;
import static nl.naturalis.common.path.PathWalkerException.emptySegment;
import static nl.naturalis.common.path.PathWalkerException.invalidType;

import java.util.List;
import java.util.function.Function;
import nl.naturalis.common.path.PathWalker.DeadEndAction;

@SuppressWarnings("rawtypes")
class ListSegmentWriter extends SegmentWriter<List> {

  ListSegmentWriter(DeadEndAction deadEndAction, Function<Path, Object> keyDeserializer) {
    super(deadEndAction, keyDeserializer);
  }

  @Override
  boolean write(List list, Path path, Object value) {
    String segment = path.segment(-1);
    if (isEmpty(segment)) {
      return deadEnd(() -> emptySegment(path));
    } else if (!Path.isArrayIndex(segment)) {
      return deadEnd(() -> arrayIndexExpected(path));
    }
    int idx = Integer.parseInt(segment);
    if (idx > 0 && idx < list.size()) {
      try {
        list.set(idx, value);
        return true;
      } catch (ClassCastException e) {
        return deadEnd(() -> invalidType(path, e));
      }
    }
    return deadEnd(() -> arrayIndexExpected(path));
  }
}
