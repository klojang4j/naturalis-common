package nl.naturalis.common;

import java.util.Collections;
import java.util.Map;

/**
 * Generic, immutable Tuple class.
 * 
 * @author Ayco Holleman
 *
 * @param <LEFT> The type of the first element (or key) of the tuple
 * @param <RIGHT> The type of the second element (or value) of the tuple
 */
public final class Tuple<LEFT, RIGHT> {

  /**
   * Creates a tuple from the provided two values. Use as static import to concisely create tuple instances.
   * 
   * @param <K> The type of the first element (or key) of the tuple
   * @param <V> The type of the second element (or value) of the tuple
   * @param left The first element (or key) of the tupl
   * @param right The second element (or value) of the tuple
   * @return
   */
  public static <K, V> Tuple<K, V> tuple(K left, V right) {
    return new Tuple<K, V>(left, right);
  }

  private final LEFT left;
  private final RIGHT right;

  /**
   * Creates a tuple containing the provided values.
   * 
   * @param left The first element of the tuple
   * @param right The second element of the tuple
   */
  public Tuple(LEFT left, RIGHT right) {
    this.left = left;
    this.right = right;
  }

  /**
   * Returns the first element of the tuple.
   * 
   * @return
   */
  public LEFT getLeft() {
    return left;
  }

  /**
   * Returns the second element of the tuple.
   * 
   * @return
   */
  public RIGHT getRight() {
    return right;
  }

  /**
   * Returns a singleton map with the left element as the key and the right element as the value.
   * 
   * @return
   */
  public Map<LEFT, RIGHT> toMap() {
    return Collections.singletonMap(left, right);
  }

  /**
   * Converts the tuple to a map entry.
   * 
   * @return
   */
  public Map.Entry<LEFT, RIGHT> toEntry() {
    return new Map.Entry<LEFT, RIGHT>() {
      private LEFT k = Tuple.this.left;
      private RIGHT v = Tuple.this.right;

      @Override
      public LEFT getKey() {
        return k;
      }

      @Override
      public RIGHT getValue() {
        return v;
      }

      @Override
      public RIGHT setValue(RIGHT value) {
        RIGHT old = this.v;
        this.v = value;
        return old;
      }
    };
  }

}
