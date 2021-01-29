package nl.naturalis.common.collection;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public interface IntList {

  void add(int i);

  void addAll(IntList other);

  void addAll(int... ints);

  int get(int index);

  void set(int index, int value);

  int size();

  boolean isEmpty();

  void clear();

  int[] toArray();

  IntStream stream();

  void forEach(IntConsumer action);
}
