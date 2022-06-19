package nl.naturalis.common.collection;

import nl.naturalis.common.x.collection.ImmutableMap;

abstract sealed class AbstractTypeMap<V> extends ImmutableMap<Class<?>, V> implements
    TypeMap<V> permits MultiPassTypeMap, NativeTypeMap {

  final boolean autobox;

  AbstractTypeMap(boolean autobox) {
    this.autobox = autobox;
  }

  @Override
  public V getOrDefault(Object key, V defaultValue) {
    throw new UnsupportedOperationException();
  }

}
