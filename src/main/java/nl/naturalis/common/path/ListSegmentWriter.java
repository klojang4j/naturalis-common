package nl.naturalis.common.path;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.List;
import java.util.function.Function;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.PathWalkerException.*;

@SuppressWarnings("rawtypes")
final class ListSegmentWriter extends SegmentWriter<List> {

  ListSegmentWriter(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
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
