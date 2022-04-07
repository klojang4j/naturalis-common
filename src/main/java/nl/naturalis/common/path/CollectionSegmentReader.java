package nl.naturalis.common.path;

import nl.naturalis.common.NumberMethods;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.Collection;
import java.util.Iterator;
import java.util.OptionalInt;
import java.util.function.Function;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.PathWalkerException.*;

@SuppressWarnings("rawtypes")
final class CollectionSegmentReader extends SegmentReader<Collection> {

  CollectionSegmentReader(OnDeadEnd ode, Function<Path, Object> kds) {
    super(ode, kds);
  }

  @Override
  Object read(Collection collection, Path path) {
    String segment = path.segment(0);
    if (isEmpty(segment)) {
      return deadEnd(() -> emptySegment(path));
    }
    OptionalInt opt = NumberMethods.toPlainInt(segment);
    if (opt.isEmpty()) {
      return deadEnd(() -> arrayIndexExpected(path));
    }
    int idx = opt.getAsInt();
    if (idx < collection.size()) {
      Iterator iter = collection.iterator();
      for (; idx != 0 && iter.hasNext(); --idx, iter.next())
        ;
      if (iter.hasNext()) {
        return nextSegmentReader().read(iter.next(), path.shift());
      }
    }
    return deadEnd(() -> indexOutOfBounds(path));
  }

}
