package nl.naturalis.common.collection;

import nl.naturalis.common.ArrayType;
import nl.naturalis.common.Tuple2;
import nl.naturalis.common.check.Check;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.naturalis.common.ClassMethods.*;
import static nl.naturalis.common.ObjectMethods.ifNull;
import static nl.naturalis.common.check.CommonChecks.instanceOf;

/*
 * Currently only extended by TypeHashMap, but could be base class for any TypeMap
 * that relies on a regular map to do the lookups.
 */
abstract sealed class MultiPassTypeMap<V> extends AbstractTypeMap<V> permits
    TypeHashMap {

  final boolean autoExpand;

  MultiPassTypeMap(boolean autoExpand, boolean autobox) {
    super(autobox);
    this.autoExpand = autoExpand;
  }

  abstract Map<Class<?>, V> backend();

  @Override
  public V get(Object key) {
    Class<?> type = Check.notNull(key)
        .is(instanceOf(), Class.class)
        .ok(Class.class::cast);
    Tuple2<Class<?>, V> entry = find(type);
    if (entry.second() == NULL) {
      return null;
    }
    if (autoExpand && type != entry.first()) {
      backend().put(type, entry.second());
    }
    return entry.second();
  }

  @Override
  public boolean containsKey(Object key) {
    Class<?> type = Check.notNull(key)
        .is(instanceOf(), Class.class)
        .ok(Class.class::cast);
    Tuple2<Class<?>, V> entry = find(type);
    if (entry.second() == NULL) {
      return false;
    }
    if (autoExpand && type != entry.first()) {
      backend().put(type, entry.second());
    }
    return true;
  }

  private Tuple2<Class<?>, V> find(Class<?> type) {
    V val;
    if ((val = backend().get(type)) != null) {
      return Tuple2.of(type, val);
    }
    Tuple2<Class<?>, V> result = null;
    if (type.isArray()) {
      ArrayType at = ArrayType.forClass(type);
      if (at.baseType().isPrimitive()) {
        if (autobox) {
          result = find(at.box());
        }
      } else if (at.baseType().isInterface()) {
        result = findInterfaceArray(at);
      } else if ((result = findSuperClassArray(at)) == null) {
        result = findInterfaceArray(at);
      }
    } else if (type.isPrimitive()) {
      if (autobox) {
        result = find(box(type));
      }
    } else if (type.isInterface()) {
      result = findInterface(type);
    } else if ((result = findSuperClass(type)) == null) {
      result = findInterface(type);
    }
    if (result == null) {
      return defaultValue();
    }
    return result;
  }

  private Tuple2<Class<?>, V> findSuperClass(Class<?> type) {
    List<Class<?>> supertypes = getAncestors(type);
    for (Class<?> c : supertypes) {
      if (c == Object.class) {
        break; // don't resort to that one just yet
      }
      V val = backend().get(c);
      if (val != null) {
        return Tuple2.of(c, val);
      }
    }
    return null;
  }

  private Tuple2<Class<?>, V> findInterface(Class<?> type) {
    Set<Class<?>> supertypes = getAllInterfaces(type);
    for (Class<?> c : supertypes) {
      V val = backend().get(c);
      if (val != null) {
        return Tuple2.of(c, val);
      }
    }
    return null;
  }

  private Tuple2<Class<?>, V> findSuperClassArray(ArrayType arrayType) {
    List<Class<?>> supertypes = getAncestors(arrayType.baseType());
    for (Class<?> c : supertypes) {
      // Now we do want to go up all the way to Object.class, because
      // what we are actually going to ask for is Object[].class (or
      // whatever the dimensionality of the array type)
      Class<?> arrayClass = arrayType.toClass(c);
      V val = backend().get(arrayClass);
      if (val != null) {
        return Tuple2.of(arrayClass, val);
      }
    }
    return null;
  }

  private Tuple2<Class<?>, V> findInterfaceArray(ArrayType arrayType) {
    Set<Class<?>> supertypes = getAllInterfaces(arrayType.baseType());
    for (Class<?> c : supertypes) {
      Class<?> arrayClass = arrayType.toClass(c);
      V val = backend().get(arrayClass);
      if (val != null) {
        return Tuple2.of(arrayClass, val);
      }
    }
    return null;
  }

  @SuppressWarnings({"unchecked"})
  private final V NULL = (V) new Object();
  private Tuple2<Class<?>, V> defVal;

  private Tuple2<Class<?>, V> defaultValue() {
    if (defVal == null) {
      V val = ifNull(backend().get(Object.class), NULL);
      defVal = new Tuple2<>(Object.class, val);
    }
    return defVal;
  }

  @Override
  public int size() {
    return backend().size();
  }

  @Override
  public boolean isEmpty() {
    return backend().isEmpty();
  }

  @Override
  public boolean containsValue(Object value) {
    return backend().containsValue(value);
  }

  @Override
  public int hashCode() {
    return backend().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return backend().equals(obj);
  }

  @Override
  public String toString() {
    return backend().toString();
  }

}
