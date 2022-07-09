package nl.naturalis.common.path;

import nl.naturalis.common.collection.TypeHashSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.naturalis.common.ClassMethods.isPrimitiveArray;
import static nl.naturalis.common.path.ErrorCode.TYPE_NOT_SUPPORTED;
import static nl.naturalis.common.path.PathWalkerException.nullValue;
import static nl.naturalis.common.path.PathWalkerException.typeNotSupported;

final class ObjectWriter {

  private final boolean se;
  private final KeyDeserializer kd;

  ObjectWriter(boolean suppressExceptions, KeyDeserializer keyDeserializer) {
    this.se = suppressExceptions;
    this.kd = keyDeserializer;
  }

  boolean write(Object host, Path path, Object value) {
    Object writeTo;
    int segment;
    if (path.size() == 1) {
      writeTo = host;
      segment = 0;
    } else {
      Path parent = path.parent();
      PathWalker pw = new PathWalker(path.parent(), se, kd);
      writeTo = pw.read(host);
      segment = parent.size() - 1;
    }
    if (writeTo == null) {
      return deadEnd(nullValue(path, segment));
    }
    if (writeTo instanceof List l) {
      return new ListSegmentWriter(se, kd).write(l, path, value);
    } else if (writeTo instanceof Map m) {
      return new MapSegmentWriter(se, kd).write(m, path, value);
    } else if (writeTo instanceof Object[] o) {
      return new ArraySegmentWriter(se, kd).write(o, path, value);
    } else if (isPrimitiveArray(writeTo)) {
      return new PrimitiveArraySegmentWriter(se, kd).write(writeTo, path, value);
    }
    return new BeanSegmentWriter(se, kd).write(writeTo, path, value);
  }

  boolean deadEnd(PathWalkerException.Factory excFactory) {
    if (se) {
      return false;
    }
    throw excFactory.get();
  }

}
