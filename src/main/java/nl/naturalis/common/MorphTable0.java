package nl.naturalis.common;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.x.invoke.InvokeUtils.newInstance;

/**
 * Used to morph objects into {@code Collection} instances. We maintain a small table of
 * object-to-collection functions for ubiquitous {@code Collection} classes. Others we instantiate
 * dynamically under the assumption they have a no-arg constructor. If they don't, an {@lcode
 * InvokeException} will be thrown.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
// It's pointless to try and use generics here. It will fight you. Real hard.
// Too much dynamic stuff going on.
class MorphTable0 {

  private static MorphTable0 INSTANCE;

  static MorphTable0 getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new MorphTable0();
    }
    return INSTANCE;
  }

  private final Map<Class, Function<Object, Collection>> table;

  private MorphTable0() {
    Map<Class, Function<Object, Collection>> tmp = new HashMap<>();
    tmp.put(Collection.class, obj -> toCollection1(obj, ArrayList::new));
    tmp.put(List.class, obj -> toCollection1(obj, ArrayList::new));
    tmp.put(ArrayList.class, obj -> toCollection1(obj, ArrayList::new));
    tmp.put(LinkedList.class, obj -> toCollection2(obj, LinkedList::new));
    tmp.put(Set.class, obj -> toCollection1(obj, HashSet::new));
    tmp.put(HashSet.class, obj -> toCollection1(obj, HashSet::new));
    tmp.put(LinkedHashSet.class, obj -> toCollection2(obj, LinkedHashSet::new));
    tmp.put(TreeSet.class, obj -> toCollection2(obj, TreeSet::new));
    table = Map.copyOf(tmp);
  }

  <T extends Collection> T morph(Object obj, Class<T> toType) {
    return (T) table.getOrDefault(toType, createConverter(toType)).apply(obj);
  }

  private Function<Object, Collection> createConverter(Class<? extends Collection> toType) {
    return o -> toCollection2(o, () -> newInstance((toType)));
  }

  private static Collection toCollection1(Object obj, IntFunction<Collection> constructor) {
    if (isA(obj.getClass(), Collection.class)) {
      return new ArrayList((Collection) obj);
    } else if (obj.getClass().isArray()) {
      return arrayToCollection(obj, constructor);
    }
    return singletonCollection(obj, constructor);
  }

  private static Collection toCollection2(Object obj, Supplier<Collection> c) {
    if (isA(obj.getClass(), Collection.class)) {
      return new ArrayList((Collection) obj);
    } else if (obj.getClass().isArray()) {
      return arrayToCollection(obj, c.get());
    }
    return singletonCollection(obj, c.get());
  }

  private static Collection singletonCollection(Object obj, IntFunction<Collection> constructor) {
    return singletonCollection(obj, constructor.apply(1));
  }

  private static Collection singletonCollection(Object obj, Collection c) {
    c.add(obj);
    return c;
  }

  private static Collection arrayToCollection(Object obj, IntFunction<Collection> constructor) {
    return arrayToCollection(obj, constructor.apply(Array.getLength(obj)));
  }

  private static Collection arrayToCollection(Object obj, Collection c) {
    for (int i = 0; i < Array.getLength(obj); ++i) {
      c.add(Array.get(obj, i));
    }
    return c;
  }
}
