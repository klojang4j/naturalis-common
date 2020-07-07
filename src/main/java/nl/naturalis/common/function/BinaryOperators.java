package nl.naturalis.common.function;

import java.util.Collection;
import java.util.function.BinaryOperator;

/**
 * A dictionary of methods that can be used in places where a {@link BinaryOperator} is required. The methods do not necessarily provide new
 * functionality. Generally they just use existing classes and methods (in naturalis-common or elsewhere) to implement a
 * {@code BinaryOperator}.
 */
public class BinaryOperators {

  public static final class CollectionConcatenator<E, T extends Collection<E>> implements BinaryOperator<T> {
    @Override
    public T apply(T arg0, T arg1) {
      return concat(arg0, arg1);
    }
  }

  /**
   * Adds all elements of {@code collection2} to {@code collection1} and returns {@code collection1}.
   * 
   * @param collection1
   * @param collection2
   * @return
   */
  public static <E, T extends Collection<E>> T concat(T collection1, T collection2) {
    collection1.addAll(collection2);
    return collection1;
  }

}
