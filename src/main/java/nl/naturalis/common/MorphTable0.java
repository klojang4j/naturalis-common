package nl.naturalis.common;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static nl.naturalis.common.x.invoke.InvokeUtils.newInstance;

/*
 * Used to morph objects into {@code Collection} instances. We maintain a small table of
 * object-to-collection functions for ubiquitous {@code Collection} classes. Others are created on
 * demand. It's pointless to try and use generics here. It will fight you. Too much dynamic stuff
 * going on.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
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
    tmp.put(Iterable.class, MorphTable0::toList);
    tmp.put(Collection.class, MorphTable0::toList);
    tmp.put(List.class, obj -> toCollection1(obj, ArrayList::new));
    tmp.put(ArrayList.class, obj -> toCollection1(obj, ArrayList::new));
    tmp.put(LinkedList.class, obj -> toCollection2(obj, LinkedList::new));
    tmp.put(Set.class, obj -> toCollection1(obj, HashSet::new));
    tmp.put(HashSet.class, obj -> toCollection1(obj, HashSet::new));
    tmp.put(LinkedHashSet.class, obj -> toCollection2(obj, LinkedHashSet::new));
    tmp.put(SortedSet.class, obj -> toCollection2(obj, TreeSet::new));
    tmp.put(TreeSet.class, obj -> toCollection2(obj, TreeSet::new));
    table = Map.copyOf(tmp);
  }

  <T extends Collection> T morph(Object obj, Class toType) {
    Function<Object, Collection> converter = table.get(toType);
    if (converter == null) {
      return toSpecialCollection(obj, (Class<T>) toType);
    }
    return (T) converter.apply(obj);
  }

  private <T extends Collection> T toSpecialCollection(Object obj, Class<T> toType) {
    T collection;
    try {
      // If the Collection subclass has a no-arg constructor, we're good.
      // Otherwise we give up
      collection = newInstance(toType);
    } catch (Throwable t) {
      throw new TypeConversionException(obj, toType, t.toString());
    }
    return (T) toCollection2(obj, () -> collection);
  }

  private static Collection toList(Object obj) {
    Collection trg;
    if (obj.getClass().isArray()) {
      trg = new ArrayList(Array.getLength(obj));
      copyArrayElements(obj, trg);
    } else {
      trg = Collections.singletonList(obj);
    }
    return trg;
  }

  private static Collection toCollection1(Object obj, IntFunction<Collection> constructor) {
    Collection trg;
    if (obj instanceof Collection) {
      Collection src = (Collection) obj;
      trg = constructor.apply(src.size());
      trg.addAll(src);
    } else if (obj.getClass().isArray()) {
      trg = constructor.apply(Array.getLength(obj));
      copyArrayElements(obj, trg);
    } else {
      trg = constructor.apply(1);
      trg.add(obj);
    }
    return trg;
  }

  private static Collection toCollection2(Object obj, Supplier<Collection> supplier) {
    Collection trg = supplier.get();
    if (obj instanceof Collection) {
      trg = supplier.get();
      trg.addAll((Collection) obj);
    } else if (obj.getClass().isArray()) {
      copyArrayElements(obj, trg);
    } else {
      trg.add(obj);
    }
    return trg;
  }

  private static void copyArrayElements(Object obj, Collection c) {
    for (int i = 0; i < Array.getLength(obj); ++i) {
      c.add(Array.get(obj, i));
    }
  }
}
