package nl.naturalis.common.collection;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * A forward-only iterator, allowing it to operate more efficiently than a {@link ListIterator}, but
 * still with more versatile than a regular {@link Iterator}. Returned by {@link
 * WiredList#wiredIterator()}.
 *
 * @param <E> The type of the elements being iterated over
 */
public sealed interface WiredIterator<E> extends Iterator<E> permits WiredList.WiredIteratorImpl {

  void set(E newVal);

}
