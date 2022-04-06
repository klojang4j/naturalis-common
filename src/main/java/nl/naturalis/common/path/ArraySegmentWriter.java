package nl.naturalis.common.path;

import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.path.PathWalkerException.arrayIndexExpected;
import static nl.naturalis.common.path.PathWalkerException.arrayIndexOutOfBounds;
import static nl.naturalis.common.path.PathWalkerException.emptySegment;
import static nl.naturalis.common.path.PathWalkerException.invalidType;

import java.util.function.Function;

import nl.naturalis.common.path.PathWalker.OnDeadEnd;

final class ArraySegmentWriter extends SegmentWriter<Object[]> {

  ArraySegmentWriter(OnDeadEnd deadEndAction, Function<Path, Object> keyDeserializer) {
    super(deadEndAction, keyDeserializer);
  }

  @Override
  boolean write(Object[] array, Path path, Object value) {
    String segment = path.segment(-1);
    if (isEmpty(segment)) {
      return deadEnd(() -> emptySegment(path));
    } else if (!Path.isArrayIndex(segment)) {
      return deadEnd(() -> arrayIndexExpected(path));
    }
    int idx = Integer.parseInt(segment);
    if (idx > 0 && idx < array.length) {
      try {
        array[idx] = value;
        return true;
      } catch (ClassCastException e) {
        Class<?> expected = array.getClass().getComponentType();
        Class<?> actual = value.getClass();
        return deadEnd(() -> invalidType(path, expected, actual));
      }
    }
    return deadEnd(() -> arrayIndexOutOfBounds(path));
  }

}
