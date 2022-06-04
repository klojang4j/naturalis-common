package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.IntRelation;

import java.lang.ref.Cleaner;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * <p>A decorator for the {@link WiredList} class that you can use when
 * multiple threads will be updating a {@code WiredList} concurrently. A {@code
 * SynchronizedWiredList} ensures proper synchronization and then simply forwards to
 * an internally managed {@link WiredList}. An exception to this are the methods
 * returning an {@code Iterator}, so be sure to read their description.
 *
 * <p>{@code SynchronizedWiredList} preserves the fluent API of {@code WiredList}.
 * However, if you intend to create long call chains using the fluent API, you are
 * probably better off using {@code WiredList} itself and manually synchronize around
 * the entire call chain.
 *
 * @param <E> The type of the elements in the list
 */
public final class SynchronizedWiredList<E> implements List<E> {

  private static final Cleaner CLEANER = Cleaner.create();

  final class CloseableWiredIterator implements WiredIterator<E> {

    private final WiredIterator<E> itr;
    private final boolean readOnly;

    CloseableWiredIterator(WiredIterator<E> itr, boolean readOnly) {
      this.itr = itr;
      this.readOnly = readOnly;
    }

    @Override
    @SuppressWarnings({"unused"})
    public void close() {
      (readOnly ? getReadLock() : getWriteLock()).unlock();
    }

    @Override
    public boolean hasNext() {
      return itr.hasNext();
    }

    @Override
    public E next() {
      return itr.next();
    }

    @Override
    public void remove() {
      itr.remove();
    }

    @Override
    public void set(E newVal) {
      itr.set(newVal);
    }

    @Override
    public E peek() {
      return itr.peek();
    }

    @Override
    public WiredIterator<E> turn() {
      return itr.turn();
    }

  }

  /**
   * Forwards to {@link WiredList#of()}.
   */
  public static <E> SynchronizedWiredList<E> of() {
    return new SynchronizedWiredList<>();
  }

  /**
   * Forwards to {@link WiredList#of(Object)}.
   */
  public static <E> SynchronizedWiredList<E> of(E e) {
    return new SynchronizedWiredList<>(WiredList.of(e));
  }

  /**
   * Forwards to {@link WiredList#of(Object, Object)}.
   */
  public static <E> SynchronizedWiredList<E> of(E e0, E e1) {
    return new SynchronizedWiredList<>(WiredList.of(e0, e1));
  }

  /**
   * Forwards to {@link WiredList#of(Object, Object, Object, Object[])}.
   */
  @SafeVarargs
  public static <E> SynchronizedWiredList<E> of(E e0, E e1, E e2, E... moreElems) {
    return new SynchronizedWiredList<>(WiredList.of(e0, e1, e2, moreElems));
  }

  /**
   * Forwards to {@link WiredList#ofElements(Object[])}.
   */
  public static <E> SynchronizedWiredList<E> ofElements(E[] elements) {
    return new SynchronizedWiredList<>(WiredList.ofElements(elements));
  }

  /**
   * Forwards to {@link WiredList#concat(List)}.
   */
  public static <E> SynchronizedWiredList<E> stitch(List<SynchronizedWiredList<E>> lists) {
    SynchronizedWiredList<E> wl = new SynchronizedWiredList<>();
    Check.notNull(lists).ok().forEach(wl::stitch);
    return wl;
  }

  private final Object lock;
  private final WiredList<E> wl;

  /**
   * Creates a new {@code SynchronizedWiredList} with a locking strategy that is
   * optimized for write-intensive use. Write-intensive usage means that you expect
   * the number of writes (or writer threads) to be comparable to the number of reads
   * (or reader threads).
   *
   * @see #SynchronizedWiredList(boolean)
   */
  public SynchronizedWiredList() {
    this(false);
  }

  /**
   * Creates a new {@code SynchronizedWiredList} with a locking strategy based on the
   * {@code readIntensive} parameter. Read-intensive usage means that you expect the
   * number of reads (or reader threads) to be substantially higher than the number
   * of writes (or writer threads), <b>and</b> that you expect the list to become
   * large. If not both conditions are met, opt for write-intensive usage.
   *
   * @param readIntensive Whether usage of the list will be read-intensive or
   *     write-intensive
   * @see ReentrantLock
   * @see ReentrantReadWriteLock
   */
  public SynchronizedWiredList(boolean readIntensive) {
    this.lock = readIntensive ? new ReentrantReadWriteLock() : new ReentrantLock();
    this.wl = new WiredList<>();
  }

  /**
   * Creates a new {@code SynchronizedWiredList} initialized with the values from the
   * specified {@code Collection}. The locking strategy will be optimized from
   * write-intensive use.
   *
   * @param c The collection to initialize this instance with
   * @see #SynchronizedWiredList(boolean)
   */
  public SynchronizedWiredList(Collection<? extends E> c) {
    lock = new ReentrantLock();
    wl = new WiredList<>(c);
  }

  private SynchronizedWiredList(WiredList<E> wl) {
    this.lock = new ReentrantLock();
    this.wl = wl;
  }

  private SynchronizedWiredList(Object lock, WiredList<E> wl) {
    this.lock = lock instanceof ReentrantReadWriteLock
        ? new ReentrantReadWriteLock()
        : new ReentrantLock();
    this.wl = wl;
  }

  /**
   * Forwards to {@link WiredList#size()}.
   */
  @Override
  public int size() {
    Lock l;
    (l = getReadLock()).lock();
    try {
      return wl.size();
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#isEmpty()}.
   */
  @Override
  public boolean isEmpty() {
    Lock l;
    (l = getReadLock()).lock();
    try {
      return wl.isEmpty();
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#contains(Object)}.
   */
  @Override
  public boolean contains(Object o) {
    Lock l;
    (l = getReadLock()).lock();
    try {
      return wl.contains(o);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#containsAll(Collection)}.
   */
  @Override
  public boolean containsAll(Collection<?> c) {
    Lock l;
    (l = getReadLock()).lock();
    try {
      if (c instanceof SynchronizedWiredList swl) {
        Lock l2;
        (l2 = swl.getReadLock()).lock();
        try {
          return wl.containsAll(swl.wl);
        } finally {
          l2.unlock();
        }
      }
      return wl.containsAll(c);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#get(int)}.
   */
  @Override
  public E get(int index) {
    Lock l;
    (l = getReadLock()).lock();
    try {
      return wl.get(index);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#indexOf(Object)}.
   */
  @Override
  public int indexOf(Object o) {
    Lock l;
    (l = getReadLock()).lock();
    try {
      return wl.indexOf(o);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#lastIndexOf(Object)}.
   */
  @Override
  public int lastIndexOf(Object o) {
    Lock l;
    (l = getReadLock()).lock();
    try {
      return wl.lastIndexOf(o);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#copy()}.
   */
  public SynchronizedWiredList<E> copy() {
    Lock l;
    (l = getReadLock()).lock();
    try {
      return new SynchronizedWiredList<>(wl.copy());
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#copySegment(int, int)}.
   */
  public SynchronizedWiredList<E> copySegment(int fromIndex, int toIndex) {
    Lock l;
    (l = getReadLock()).lock();
    try {
      return new SynchronizedWiredList<>(wl.copySegment(fromIndex, toIndex));
    } finally {
      l.unlock();
    }
  }

  /**
   * Returns an {@code Iterator} that operates on a copy of the internally managed
   * {@code WiredList}. This is reasonable since this iterator does not support
   * updates (all list-altering methods throw an {@code UnsupportedOperationException}).
   * Since the copy is only accessible to the iterator, no synchronization is needed
   * while the iteration is in progress. The downside is that, for large lists,
   * getting hold of one of these iterators in the first place is rather expensive.
   *
   * @return An {@code Iterator} that operates on a copy of the internally managed
   *     {@code WiredList}
   */
  public Iterator<E> iterator() {
    Lock l;
    (l = getReadLock()).lock();
    try {
      return wl.copy().iterator();
    } finally {
      l.unlock();
    }
  }

  /**
   * Returns a backwards traversing {@code Iterator} that operates on a copy of the
   * internally managed {@code WiredList}. This is reasonable since this iterator
   * does not support updates (all list-altering methods throw an {@code
   * UnsupportedOperationException}). Since the copy is only accessible to the
   * iterator, no synchronization is needed while the iteration is in progress. The
   * downside is that, for large lists, getting hold of one of these iterators in the
   * first place is rather expensive.
   *
   * @return A backwards traversing {@code Iterator} that operates on a copy of the
   *     internally managed {@code WiredList}
   */
  public Iterator<E> reverseIterator() {
    Lock l;
    (l = getReadLock()).lock();
    try {
      return wl.copy().reverseIterator();
    } finally {
      l.unlock();
    }
  }

  /**
   * Returns a {@code ListIterator} that operates on a copy of the internally managed
   * {@code WiredList}. This is reasonable since this iterator does not support
   * updates (all list-altering methods throw an {@code UnsupportedOperationException}).
   * Since the copy is only accessible to the iterator, no synchronization is needed
   * while the iteration is in progress. The downside is that, for large lists,
   * getting hold of one of these iterators in the first place is rather expensive.
   *
   * @return A {@code ListIterator} that operates on a copy of the internally managed
   *     {@code WiredList}
   */
  public ListIterator<E> listIterator() {
    Lock l;
    (l = getReadLock()).lock();
    try {
      return wl.copy().listIterator();
    } finally {
      l.unlock();
    }
  }

  /**
   * Returns a {@code ListIterator} that operates on a copy of the internally managed
   * {@code WiredList}. This is reasonable since this iterator does not support
   * updates (all list-altering methods throw an {@code UnsupportedOperationException}).
   * Since the copy is only accessible to the iterator, no synchronization is needed
   * while the iteration is in progress. The downside is that, for large lists,
   * getting hold of one of these iterators in the first place is rather expensive.
   *
   * @param index Index of the first element to be returned from the list
   *     iterator (by a call to next)
   * @return A {@code ListIterator} that operates on a copy of the internally managed
   *     {@code WiredList}
   */
  public ListIterator<E> listIterator(int index) {
    Lock l;
    (l = getReadLock()).lock();
    try {
      return wl.copy().listIterator(index);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#wiredIterator()}. The returned iterator will assume
   * that you are going to use it to update the list.
   */
  public WiredIterator<E> wiredIterator() {
    return wiredIterator(false);
  }

  /**
   * Forwards to {@link WiredList#wiredIterator()}. The returned iterator will assume
   * that you are going to use it to update the list.
   */
  public WiredIterator<E> wiredIterator(boolean reverse) {
    return wiredIterator(reverse, false);
  }

  /**
   * <p>Returns a {@code WiredIterator} that directly operates on the list itself.
   * <i>This iterator locks the list while the iteration is in progress</i>. When
   * you instantiated the list as a read-intensive list, you can specify that the
   * iterator is only going to read from the list. If so, other threads can keep
   * reading from the list. Only writing to it will be prohibited. For
   * write-intensive lists, instantiated via the no-arg constructor, the {@code
   * readOnly} parameter has no effect. The list will be locked both for reading and
   * for writing.
   *
   * <p>Note that the {@link WiredIterator} interface not only extends
   * {@link Iterator} but also {@link AutoCloseable}. You <b>SHOULD ALWAYS</b> use a
   * try-with-resources block to obtain a {@code WiredIterator} from a {@code
   * SynchronizedWiredList}. The {@code close} method will release lock acquired at
   * the start of the iteration. If you don't call the {@code close} method, the lock
   * will still be released eventually, but this will only happen when the iterator
   * gets garbage-collected. (NB for a regular {@link WiredList} there is no need to
   * start a try-with-resources block.)
   *
   * <blockquote>
   * <pre>{@code
   * SynchronizedWiredList<String> swl = new SynchronizedWiredList<>();
   * // add some elements ...
   * try(WiredIterator<E> itr = swl.wiredIterator()) {
   *  while(itr.hasNext()) {
   *    // do stuff ...
   *  }
   * }
   * }</pre>
   * </blockquote>
   *
   * @param reverse Whether to traverse the list backwards
   * @param readOnly Whether you intend to use the iterator to read from the list
   *     only
   * @return A {@code WiredIterator} that traverses the list from the first element
   *     to the last, or the other way round
   */
  public WiredIterator<E> wiredIterator(boolean reverse, boolean readOnly) {
    Lock l;
    (l = readOnly ? getReadLock() : getWriteLock()).lock();
    WiredIterator<E> itr = wl.wiredIterator(reverse);
    WiredIterator<E> wrapper = new CloseableWiredIterator(itr, readOnly);
    CLEANER.register(wrapper, l::unlock);
    return wrapper;
  }

  /**
   * Returns {@code true} if the specified object is a {@code SynchronizedWiredList}
   * and the underlying {@code WiredList} instances are equal.
   *
   * @param o The object to compare this instance with
   * @return Whether it equals this instance
   */
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    Lock l;
    (l = getReadLock()).lock();
    try {
      if (o instanceof SynchronizedWiredList swl) {
        Lock l2;
        (l2 = swl.getReadLock()).lock();
        try {
          return wl.equals(swl.wl);
        } finally {
          l2.unlock();
        }
      }
      return wl.equals(o);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#hashCode()}.
   */
  public int hashCode() {
    Lock l;
    (l = getReadLock()).lock();
    try {
      return wl.hashCode();
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#toString()}.
   */
  public String toString() {
    Lock l;
    (l = getReadLock()).lock();
    try {
      return wl.toString();
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#set(int, Object)}.
   */
  @Override
  public E set(int index, E value) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return wl.set(index, value);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#setIf(int, Predicate, Object)}.
   */
  public E setIf(int index, Predicate<? super E> test, E value) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return wl.setIf(index, test, value);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#add(Object)}.
   */
  @Override
  public boolean add(E value) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return wl.add(value);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#add(int, Object)}.
   */
  @Override
  public void add(int index, E value) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      wl.add(index, value);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#addAll(Collection)}.
   */
  @Override
  @SuppressWarnings({"unchecked"})
  public boolean addAll(Collection<? extends E> values) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      if (values instanceof SynchronizedWiredList swl) {
        Lock l2;
        (l2 = swl.getReadLock()).lock();
        try {
          return wl.addAll(swl.wl);
        } finally {
          l2.unlock();
        }
      }
      return wl.addAll(values);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#addAll(int, Collection)}.
   */
  @Override
  @SuppressWarnings({"unchecked"})
  public boolean addAll(int index, Collection<? extends E> values) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      if (values instanceof SynchronizedWiredList swl) {
        Lock l2;
        (l2 = swl.getReadLock()).lock();
        try {
          return wl.addAll(index, swl.wl);
        } finally {
          l2.unlock();
        }
      }
      return wl.addAll(index, values);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#remove(int)}.
   */
  @Override
  public E remove(int index) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return wl.remove(index);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#remove(Object)}.
   */
  @Override
  public boolean remove(Object o) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return wl.remove(o);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#removeIf(Predicate)}.
   */
  @Override
  public boolean removeIf(Predicate<? super E> test) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return wl.removeIf(test);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#removeAll(Collection)}.
   */
  @Override
  public boolean removeAll(Collection<?> c) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      if (c instanceof SynchronizedWiredList swl) {
        Lock l2;
        (l2 = swl.getReadLock()).lock();
        try {
          return wl.removeAll(swl.wl);
        } finally {
          l2.unlock();
        }
      }
      return wl.removeAll(c);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#prepend(Object)}.
   */
  public SynchronizedWiredList<E> prepend(E value) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      wl.prepend(value);
      return this;
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#append(Object)}.
   */
  public SynchronizedWiredList<E> append(E value) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      wl.append(value);
      return this;
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#insert(int, Object)}.
   */
  public SynchronizedWiredList<E> insert(int index, E value) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      wl.insert(index, value);
      return this;
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#deleteFirst()}.
   */
  public E deleteFirst() {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return wl.deleteFirst();
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#deleteLast()}.
   */
  public E deleteLast() {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return wl.deleteLast();
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#prependAll(Collection)}.
   */
  @SuppressWarnings({"unchecked"})
  public SynchronizedWiredList<E> prependAll(Collection<? extends E> values) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      if (values instanceof SynchronizedWiredList swl) {
        Lock l2;
        (l2 = swl.getReadLock()).lock();
        try {
          wl.prependAll(swl.wl);
        } finally {
          l2.unlock();
        }
      } else {
        wl.prependAll(values);
      }
      return this;
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#appendAll(Collection)}.
   */
  @SuppressWarnings({"unchecked"})
  public SynchronizedWiredList<E> appendAll(Collection<? extends E> values) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      if (values instanceof SynchronizedWiredList swl) {
        Lock l2;
        (l2 = swl.getReadLock()).lock();
        try {
          wl.appendAll(swl.wl);
        } finally {
          l2.unlock();
        }
      } else {
        wl.appendAll(values);
      }
      return this;
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#insertAll(int, Collection)}.
   */
  @SuppressWarnings({"unchecked"})
  public SynchronizedWiredList<E> insertAll(int index,
      Collection<? extends E> values) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      if (values instanceof SynchronizedWiredList swl) {
        Lock l2;
        (l2 = swl.getReadLock()).lock();
        try {
          wl.insertAll(index, swl.wl);
        } finally {
          l2.unlock();
        }
      } else {
        wl.insertAll(index, values);
      }
      return this;
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#replaceAll(int, int, Collection)}.
   */
  @SuppressWarnings({"unchecked"})
  public SynchronizedWiredList<E> replaceAll(int fromIndex,
      int toIndex,
      Collection<? extends E> values) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      if (values instanceof SynchronizedWiredList swl) {
        Lock l2;
        (l2 = swl.getReadLock()).lock();
        try {
          wl.replaceAll(fromIndex, toIndex, swl.wl);
        } finally {
          l2.unlock();
        }
      } else {
        wl.replaceAll(fromIndex, toIndex, values);
      }
      return this;
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#replaceSegment(int, int, WiredList)}.
   */
  public SynchronizedWiredList<E> replaceSegment(int fromIndex,
      int toIndex,
      SynchronizedWiredList<? extends E> other) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      Lock l2;
      (l2 = other.getWriteLock()).lock();
      try {
        wl.replaceSegment(fromIndex, toIndex, other.wl);
      } finally {
        l2.unlock();
      }
      return this;
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#deleteSegment(int, int)}.
   */
  public SynchronizedWiredList<E> deleteSegment(int fromIndex, int toIndex) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return new SynchronizedWiredList<>(lock, wl.deleteSegment(fromIndex, toIndex));
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#embed(int, WiredList)}.
   */
  public SynchronizedWiredList<E> embed(int index,
      SynchronizedWiredList<? extends E> other) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      Lock l2;
      (l2 = other.getWriteLock()).lock();
      try {
        wl.embed(index, other.wl);
        return this;
      } finally {
        l2.unlock();
      }
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#concat(WiredList)}.
   */
  public SynchronizedWiredList<E> stitch(SynchronizedWiredList<? extends E> other) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      Lock l2;
      (l2 = other.getWriteLock()).lock();
      try {
        wl.concat(other.wl);
        return this;
      } finally {
        l2.unlock();
      }
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#excise(int, WiredList, int, int)}.
   */
  public SynchronizedWiredList<E> excise(SynchronizedWiredList<? extends E> other,
      int itsFromIndex,
      int itsToIndex) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      Lock l2;
      (l2 = other.getWriteLock()).lock();
      try {
        wl.excise(other.wl, itsFromIndex, itsToIndex);
        return this;
      } finally {
        l2.unlock();
      }
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#excise(int, WiredList, int, int)}.
   */
  public SynchronizedWiredList<E> excise(int myIndex,
      SynchronizedWiredList<? extends E> other,
      int itsFromIndex,
      int itsToIndex) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      Lock l2;
      (l2 = other.getWriteLock()).lock();
      try {
        wl.excise(myIndex, other.wl, itsFromIndex, itsToIndex);
        return this;
      } finally {
        l2.unlock();
      }
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#defragment(List)}.
   */
  public SynchronizedWiredList<E> defragment(List<Predicate<? super E>> criteria) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return new SynchronizedWiredList<>(lock, wl.defragment(criteria));
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#group(List)}.
   */
  public SynchronizedWiredList<SynchronizedWiredList<E>> group(List<Predicate<? super E>> criteria) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      WiredList<WiredList<E>> wls = wl.group(criteria);
      SynchronizedWiredList<SynchronizedWiredList<E>> groups = new SynchronizedWiredList<>();
      Iterator<WiredList<E>> itr = wls.iterator();
      for (int i = 0; i < wls.size() - 1; ++i) {
        groups.add(new SynchronizedWiredList<>(lock, itr.next()));
      }
      groups.add(this);
      return groups;
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#partition(int)}.
   */
  public SynchronizedWiredList<SynchronizedWiredList<E>> partition(int size) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      WiredList<WiredList<E>> wls = wl.partition(size);
      SynchronizedWiredList<SynchronizedWiredList<E>> partitions = new SynchronizedWiredList<>();
      Iterator<WiredList<E>> itr = wls.iterator();
      for (int i = 0; i < wls.size() - 1; ++i) {
        partitions.add(new SynchronizedWiredList<>(lock, itr.next()));
      }
      partitions.add(this);
      return partitions;
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#split(int)}.
   */
  public SynchronizedWiredList<SynchronizedWiredList<E>> split(int count) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      WiredList<WiredList<E>> wls = wl.split(count);
      SynchronizedWiredList<SynchronizedWiredList<E>> partitions = new SynchronizedWiredList<>();
      Iterator<WiredList<E>> itr = wls.iterator();
      for (int i = 0; i < wls.size() - 1; ++i) {
        partitions.add(new SynchronizedWiredList<>(lock, itr.next()));
      }
      partitions.add(this);
      return partitions;
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#lchop(Predicate)}.
   */
  public SynchronizedWiredList<E> lchop(Predicate<? super E> condition) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      WiredList<E> result = wl.lchop(condition);
      if (result == wl) {
        return this;
      }
      return new SynchronizedWiredList<>(lock, result);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#rchop(Predicate)}.
   */
  public SynchronizedWiredList<E> rchop(Predicate<? super E> condition) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      WiredList<E> result = wl.rchop(condition);
      if (result == wl) {
        return this;
      }
      return new SynchronizedWiredList<>(lock, result);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#reverse()}.
   */
  public SynchronizedWiredList<E> reverse() {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      wl.reverse();
      return this;
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#move(int, int, int)}.
   */
  public SynchronizedWiredList<E> move(int fromIndex,
      int toIndex,
      int newFromIndex) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      wl.move(fromIndex, toIndex, newFromIndex);
      return this;
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#retainAll(Collection)}.
   */
  public boolean retainAll(Collection<?> c) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      if (c instanceof SynchronizedWiredList swl) {
        Lock l2;
        (l2 = swl.getReadLock()).lock();
        try {
          return wl.retainAll(swl.wl);
        } finally {
          l2.unlock();
        }
      }
      return wl.retainAll(c);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#toArray()}.
   */
  public Object[] toArray() {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return wl.toArray();
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#toArray(Object[])}.
   */
  public <T> T[] toArray(T[] a) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return wl.toArray(a);
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#clear()}.
   */
  public void clear() {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      wl.clear();
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#subList(int, int)}.
   */
  public List<E> subList(int fromIndex, int toIndex) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return wl.subList(fromIndex, toIndex);
    } finally {
      l.unlock();
    }
  }

  private Lock getReadLock() {
    return lock instanceof ReentrantReadWriteLock x
        ? x.readLock()
        : (ReentrantLock) lock;
  }

  private Lock getWriteLock() {
    return lock instanceof ReentrantReadWriteLock x
        ? x.writeLock()
        : (ReentrantLock) lock;
  }

}
