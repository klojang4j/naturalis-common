package nl.naturalis.common.path;

import nl.naturalis.common.collection.TypeSet;
import nl.naturalis.common.path.PathWalker.OnError;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static nl.naturalis.common.ClassMethods.isPrimitiveArray;
import static nl.naturalis.common.path.ErrorCode.*;
import static nl.naturalis.common.path.PathWalkerException.*;

final class ObjectWriter {

  /*
   * We don't really need to maintain a list of non-writable classes b/c the BeanWriter used
   * to set the property would fail graciously. But error reporting is clearer if we do. We only
   * include the most obvious class though. An exhaustive list would be impossible anyhow.
   */
  private static final TypeSet NOT_WRITABLE = TypeSet.of(String.class, Number.class, Set.class);

  private final OnError oe;
  private final Function<Path, Object> kd;

  ObjectWriter(OnError onError, Function<Path, Object> keyDeserializer) {
    this.oe = onError;
    this.kd = keyDeserializer;
  }

  ErrorCode write(Object host, Path path, Object value) {
    Object obj;
    if (path.size() == 1) {
      obj = host;
    } else {
      PathWalker pw = new PathWalker(path.parent(), oe, kd);
      obj = pw.read(host);
    }
    if (obj == null || obj instanceof ErrorCode) {
      return error(oe, TERMINAL_VALUE, () -> terminalValue(path));
    }
    try {
      if (obj instanceof List l) {
        return new ListSegmentWriter(oe, kd).write(l, path, value);
      } else if (obj instanceof Map m) {
        return new MapSegmentWriter(oe, kd).write(m, path, value);
      } else if (obj instanceof Object[] o) {
        return new ArraySegmentWriter(oe, kd).write(o, path, value);
      } else if (isPrimitiveArray(obj)) {
        return new PrimitiveArraySegmentWriter(oe, kd).write(obj, path, value);
      } else if (isWritable(obj)) {
        return new BeanSegmentWriter(oe, kd).write(obj, path, value);
      }
    } catch (PathWalkerException e) {
      throw e;
    } catch (Throwable t) {
      return error(oe, EXCEPTION, () -> exception(path, t));
    }
    return error(oe, TYPE_NOT_SUPPORTED, () -> typeNotSupported(obj));
  }

  private static boolean isWritable(Object obj) {
    return !NOT_WRITABLE.contains(obj.getClass());
  }

}
