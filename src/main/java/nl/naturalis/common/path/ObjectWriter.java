package nl.naturalis.common.path;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.path.PathWalker.OnDeadEnd;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static nl.naturalis.common.ClassMethods.isPrimitiveArray;
import static nl.naturalis.common.check.CommonChecks.gt;
import static nl.naturalis.common.path.DeadEnd.TERMINAL_VALUE;
import static nl.naturalis.common.path.DeadEnd.TYPE_NOT_SUPPORTED;
import static nl.naturalis.common.path.DeadEndException.terminalValue;
import static nl.naturalis.common.path.DeadEndException.typeNotSupported;

@SuppressWarnings("rawtypes")
final class ObjectWriter {

  /*
   * We don't really need to maintain a list of unsupported classes b/c the BeanWriter that is used
   * as a last resort would fail rather graciously when trying to set a property of instances of
   * them. But error reporting becomes a bit easier if we do.
   */
  private static final List<Class<?>> NOT_WRITABLE = List.of(Set.class);

  private OnDeadEnd ode;
  private Function<Path, Object> kds;

  ObjectWriter(OnDeadEnd deadEndAction, Function<Path, Object> keyDeserializer) {
    this.ode = deadEndAction;
    this.kds = keyDeserializer;
  }

  DeadEnd write(Object host, Path path, Object value) {
    Check.notNull(path, "path").has(Path::size, gt(), 0);
    Object obj;
    if (path.size() == 1) {
      obj = host;
    } else {
      PathWalker pw = new PathWalker(path.parent(), ode, kds);
      obj = pw.read(host);
    }
    if (obj == null || obj instanceof DeadEnd) {
      return SegmentWriter.deadEnd(ode, TERMINAL_VALUE, () -> terminalValue(path));
    } else if (obj instanceof List) {
      return new ListSegmentWriter(ode, kds).write((List) obj, path, value);
    } else if (obj instanceof Map) {
      return new MapSegmentWriter(ode, kds).write((Map) obj, path, value);
    } else if (obj instanceof Object[]) {
      return new ArraySegmentWriter(ode, kds).write((Object[]) obj, path, value);
    } else if (isPrimitiveArray(obj)) {
      return new PrimitiveArraySegmentWriter(ode, kds).write(obj, path, value);
    } else if (isWritable(obj)) {
      return new BeanSegmentWriter<>(ode, kds).write(obj, path, value);
    }
    return SegmentWriter.deadEnd(ode, TYPE_NOT_SUPPORTED, () -> typeNotSupported(obj));
  }

  private static boolean isWritable(Object obj) {
    return NOT_WRITABLE.stream().noneMatch(c -> c.isInstance(obj));
  }

}
