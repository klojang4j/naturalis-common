package nl.naturalis.common.collection;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * A one-way-only iterator that, in practice, still provides the same functionality as a {@link
 * ListIterator}. It is resistant against same-thread list modifications outside the iterator, and
 * it makes some light-weight attempts to protect itself against the consequences concurrent
 * modifications of the list. A {@code WiredIterator} lets you reverse the direction of the
 * iteration midway. Its {@code next} and {@code hasNext} methods are always relative to the
 * direction of the traversal. (Thus, {@code next()} may move the {@code Iterator} closer to the
 * start of the list.) You obtain a {@code WiredIterator} by calling {@link
 * WiredList#wiredIterator(boolean) WiredList.wiredIterator}.
 *
 * @param <E> The type of the elements being iterated over
 */
public sealed interface WiredIterator<E> extends Iterator<E> permits WiredList.ForwardWiredIterator,
    WiredList.ReverseWiredIterator {

  /**
   * Sets the value of the element arrived at by the last call to {@link #next() next()}.
   *
   * @param newVal The new value for the element.
   */
  void set(E newVal);

  /**
   * Reverses the direction of the iteration. The returned {@code Iterator} is initialized to be at
   * the same element as this {@code Iterator} (that is, at the element arrived at by the last call
   * to {@code next()}.
   *
   * @return A {@code WiredIterator} that the traverses the list in the opposite direction.
   */
  WiredIterator<E> reverse();

}
