package nl.naturalis.common.path;

import static nl.naturalis.common.path.PathWalkerException.arrayIndexExpected;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

@SuppressWarnings("rawtypes")
final class CollectionSegmentReader extends SegmentReader<Collection> {

  CollectionSegmentReader(OnDeadEnd deadEndAction, Function<Path, Object> keyDeserializer) {
    super(deadEndAction, keyDeserializer);
  }

  @Override
  Object read(Collection collection, Path path) {
    String segment = path.segment(0);
    if (Path.isArrayIndex(segment)) {
      int idx = Integer.parseInt(segment);
      if (idx < collection.size()) {
        Iterator iter = collection.iterator();
        for (; idx != 0 && iter.hasNext(); --idx, iter.next())
          ;
        if (iter.hasNext()) {
          return nextSegmentReader().read(iter.next(), path.shift());
        }
        return deadEnd(() -> arrayIndexExpected(path));
      }
    }
    return deadEnd(() -> arrayIndexExpected(path));
  }

}
