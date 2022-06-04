package nl.naturalis.common.collection;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * A one-way-only iterator that, in practice, still provides the same functionality
 * as a {@link ListIterator}. A {@code WiredIterator} lets you reverse the direction
 * of the iteration through the {@link #turn()} method. <i>The</i> {@code next()}
 * <i>and</i> {@code hasNext()} <i>methods are always relative to the
 * direction of the traversal.</i>
 *
 * @param <E> The type of the elements being iterated over
 * @see
 */
public sealed interface WiredIterator<E> extends Iterator<E>, AutoCloseable permits
    SynchronizedWiredList.CloseableWiredIterator, WiredList.ForwardWiredIterator,
    WiredList.ReverseWiredIterator {

  /**
   * Sets the value of the element arrived at by the last call to {@link #next()
   * next()}. An {@link IllegalStateException} is thrown if {@link #next()} has not
   * been called yet.
   *
   * @param newVal The new value for the element.
   */
  void set(E newVal);

  /**
   * Returns the value that would be returned by a call to {@link #next()} without
   * actually moving towards the next element. A {@link java.util.NoSuchElementException}
   * is thrown if the iterator has arrived at the last element.
   *
   * @return The value that would be returned by a call to{@code #next()}.
   */
  E peek();

  /**
   * Flips the direction of the iteration. The returned {@code Iterator} is
   * initialized to be at the same element as this {@code Iterator}. An {@link
   * IllegalStateException} is thrown if {@link #next()} has not been called yet.
   *
   * @return A {@code WiredIterator} that the traverses the list in the opposite
   *     direction.
   */
  WiredIterator<E> turn();

  /**
   * Provides a hook for implementations returned by {@link SynchronizedWiredList} to
   * release the {@link java.util.concurrent.locks.Lock Lock} acquired at the start
   * of the iteration. You <b>SHOULD ALWAYS</b> use a try-with-resources block to
   * obtain a {@code WiredIterator} from a {@code SynchronizedWiredList}. This is
   * unnecessary for a regular {@code WiredList}.
   */
  default void close() {}

}
