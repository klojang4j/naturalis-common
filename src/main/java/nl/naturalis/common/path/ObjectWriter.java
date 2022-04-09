package nl.naturalis.common.path;

import nl.naturalis.common.collection.TypeSet;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static nl.naturalis.common.ClassMethods.isPrimitiveArray;
import static nl.naturalis.common.path.ErrorCode.TERMINAL_VALUE;
import static nl.naturalis.common.path.ErrorCode.TYPE_NOT_SUPPORTED;
import static nl.naturalis.common.path.PathWalkerException.terminalValue;
import static nl.naturalis.common.path.PathWalkerException.typeNotSupported;

final class ObjectWriter {

  /*
   * We don't really need to maintain a list of non-writable classes b/c the BeanWriter used
   * to set the property would fail graciously. But error reporting is clearer if we do. We only
   * include the most obvious class though. An exhaustive list would be impossible anyhow.
   */
  private static final TypeSet NOT_WRITABLE = TypeSet.of(String.class, Number.class, Set.class);

  private final OnDeadEnd ode;
  private final Function<Path, Object> kds;

  ObjectWriter(OnDeadEnd deadEndAction, Function<Path, Object> keyDeserializer) {
    this.ode = deadEndAction;
    this.kds = keyDeserializer;
  }

  ErrorCode write(Object host, Path path, Object value) {
    Object obj;
    if (path.size() == 1) {
      obj = host;
    } else {
      PathWalker pw = new PathWalker(path.parent(), ode, kds);
      obj = pw.read(host);
    }
    if (obj == null || obj instanceof ErrorCode) {
      return SegmentWriter.error(ode, TERMINAL_VALUE, () -> terminalValue(path));
    } else if (obj instanceof List l) {
      return new ListSegmentWriter(ode, kds).write(l, path, value);
    } else if (obj instanceof Map m) {
      return new MapSegmentWriter(ode, kds).write(m, path, value);
    } else if (obj instanceof Object[] o) {
      return new ArraySegmentWriter(ode, kds).write(o, path, value);
    } else if (isPrimitiveArray(obj)) {
      return new PrimitiveArraySegmentWriter(ode, kds).write(obj, path, value);
    } else if (isWritable(obj)) {
      return new BeanSegmentWriter(ode, kds).write(obj, path, value);
    }
    return SegmentWriter.error(ode, TYPE_NOT_SUPPORTED, () -> typeNotSupported(obj));
  }

  private static boolean isWritable(Object obj) {
    return !NOT_WRITABLE.contains(obj.getClass());
  }

}
