package nl.naturalis.common.path;

import static nl.naturalis.common.ClassMethods.isPrimitiveArray;
import static nl.naturalis.common.check.CommonChecks.gt;
import static nl.naturalis.common.path.PathWalker.DEAD;
import static nl.naturalis.common.path.PathWalkerException.cannotWrite;
import static nl.naturalis.common.path.PathWalkerException.cannotWriteToDeadEnd;
import static nl.naturalis.common.path.PathWalkerException.cannotWriteToNullObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

@SuppressWarnings("rawtypes")
final class ObjectWriter {

  private static final Map<Path, WeakReference<PathWalker>> pwCache = new HashMap<>();

  /*
   * We don't really need to maintain a list of unsupported classes b/c the BeanWriter that is used
   * as a last resort would fail rather graciously when trying to set a property of instances of
   * them. But error reporting becomes a bit easier if we do.
   */
  private static final List<Class<?>> NOT_WRITABLE = List.of(Set.class);

  private OnDeadEnd dea;
  private Function<Path, Object> kds;

  ObjectWriter(OnDeadEnd deadEndAction, Function<Path, Object> keyDeserializer) {
    this.dea = deadEndAction;
    this.kds = keyDeserializer;
  }

  boolean write(Object host, Path path, Object value) {
    Check.notNull(path, "path").has(Path::size, gt(), 0);
    Object obj;
    if (path.size() == 1) {
      obj = host;
    } else {
      PathWalker pw = new PathWalker(path.parent(), dea, kds);
      obj = pw.read(host);
    }
    if (obj == null) {
      return deadEnd(() -> cannotWriteToNullObject());
    } else if (obj == DEAD) {
      return deadEnd(() -> cannotWriteToDeadEnd(path));
    } else if (obj instanceof List) {
      return new ListSegmentWriter(dea, kds).write((List) obj, path, value);
    } else if (obj instanceof Map) {
      return new MapSegmentWriter(dea, kds).write((Map) obj, path, value);
    } else if (obj instanceof Object[]) {
      return new ArraySegmentWriter(dea, kds).write((Object[]) obj, path, value);
    } else if (isPrimitiveArray(obj)) {
      return new PrimitiveArraySegmentWriter(dea, kds).write(obj, path, value);
    } else if (isWritable(obj)) {
      return new BeanSegmentWriter<>(dea, kds).write(obj, path, value);
    }
    return deadEnd(() -> cannotWrite(obj));
  }

  private WeakReference<PathWalker> createPathWalker(Path p) {
    return new WeakReference<>(new PathWalker(List.of(p), dea, kds));
  }

  private boolean deadEnd(Supplier<PathWalkerException> e) {
    if (dea == OnDeadEnd.THROW_EXCEPTION) {
      throw e.get();
    }
    return false;
  }

  private static boolean isWritable(Object obj) {
    return NOT_WRITABLE.stream().noneMatch(c -> c.isInstance(obj));
  }

}
