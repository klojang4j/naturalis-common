package nl.naturalis.common.collection;

import nl.naturalis.common.ClassMethods;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableList;

abstract sealed class AbstractTypeMap<V> extends ImmutableMap<Class<?>, V> implements
    TypeMap<V> permits MultiPassTypeMap, TypeGraph, LinkedTypeGraph {

  final boolean autobox;

  AbstractTypeMap(boolean autobox) {
    this.autobox = autobox;
  }

  @Override
  public V getOrDefault(Object key, V defaultValue) {
    throw new UnsupportedOperationException();
  }

}
