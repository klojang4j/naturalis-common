package nl.naturalis.common;

import java.util.Map;

public class Generics {

  private Generics() {}

  @SuppressWarnings("unchecked")
  public static <K, V1, V2 extends V1> Map<K, V2> narrowValueType(Map<K, V1> m) {
    return (Map<K, V2>) m;
  }

  @SuppressWarnings("unchecked")
  public static <K, V1, V2 extends V1> Map<K, V1> widenValueType(Map<K, V2> m) {
    return (Map<K, V1>) m;
  }

  @SuppressWarnings("unchecked")
  public static <K, V> Map<K, V> imposeValueType(Map<K, ?> m) {
    return (Map<K, V>) m;
  }
}
