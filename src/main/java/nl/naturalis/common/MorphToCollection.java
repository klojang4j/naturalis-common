package nl.naturalis.common;

import nl.naturalis.common.x.invoke.InvokeUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static nl.naturalis.common.x.invoke.InvokeUtils.*;

/*
 * Used to morph objects into {@code Collection} instances. We maintain a small table
 * of object-to-collection functions for ubiquitous {@code Collection} classes.
 * Others are created on demand. It's pointless to try and use generics here. It will
 * fight you. Too much dynamic stuff going on.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
final class MorphToCollection {

  private static MorphToCollection INSTANCE;

  static MorphToCollection getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new MorphToCollection();
    }
    return INSTANCE;
  }

  private final Map<Class, Function<Object, Collection>> table;

  private MorphToCollection() {
    Map<Class, Function<Object, Collection>> tmp = new HashMap<>();
    tmp.put(Iterable.class, MorphToCollection::toList);
    tmp.put(Collection.class, MorphToCollection::toList);
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
      // If toType has a no-arg constructor, we're good. Otherwise we give up.
      collection = InvokeUtils.newInstance(toType);
    } catch (Throwable t) {
      throw new TypeConversionException(obj, toType, t.toString());
    }
    return (T) toCollection2(obj, () -> collection);
  }

  private static Collection toList(Object obj) {
    Collection collection;
    if (obj.getClass().isArray()) {
      collection = new ArrayList(Array.getLength(obj));
      copyArrayElements(obj, collection);
    } else {
      collection = Collections.singletonList(obj);
    }
    return collection;
  }

  private static Collection toCollection1(Object obj,
      IntFunction<Collection> constructor) {
    Collection collection;
    if (obj instanceof Collection src) {
      collection = constructor.apply(src.size());
      collection.addAll(src);
    } else if (obj.getClass().isArray()) {
      collection = constructor.apply(Array.getLength(obj));
      copyArrayElements(obj, collection);
    } else {
      collection = constructor.apply(1);
      collection.add(obj);
    }
    return collection;
  }

  private static Collection toCollection2(Object obj,
      Supplier<Collection> supplier) {
    Collection collection = supplier.get();
    if (obj instanceof Collection) {
      collection = supplier.get();
      collection.addAll((Collection) obj);
    } else if (obj.getClass().isArray()) {
      copyArrayElements(obj, collection);
    } else {
      collection.add(obj);
    }
    return collection;
  }

}
