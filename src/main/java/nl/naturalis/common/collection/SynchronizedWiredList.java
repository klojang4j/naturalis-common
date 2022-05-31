package nl.naturalis.common.collection;

import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;

import java.lang.ref.Cleaner;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

/**
 * A decorator for the {@link WiredList} class that you can use if there is a
 * significant chance that multiple threads will update a {@code WiredList}
 * concurrently. Its {@link #iterator()}, {@link #reverseIterator()} and {@link
 * #listIterator()} methods operate on a copy of the internally managed {@code
 * WiredList}. This is reasonable since none of these iterators supports any kind of
 * update of the {@code WiredList}. Consequently, no synchronization is needed while
 * the iteration is in progress. The downside is that, for large lists, getting hold
 * of one of these iterators is itself rather expensive. The {@link #wiredIterator()}
 * method returns a {@link WiredIterator} that directly operates on the list itself.
 * However, <b>this iterator locks out all writer threads while the iteration is in
 * progress</b>. All threads that want to update the list will have to wait until the
 * iteration is over. The returned iterator not only implements {@link
 * WiredIterator}, but also {@link AutoCloseable}. <b>You SHOULD start a
 * try-with-resources block when using this iterator.</b> The {@code close} method
 * will release lock acquired at the start of the iteration. If you don't call the
 * {@code close} method, the lock will still be released eventually, but this may
 * (and probably will) cause the other threads to wait longer than necessary.
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
 * @param <E> The type of the elements in the list
 */
public final class SynchronizedWiredList<E> implements List<E> {

  private static final Cleaner CLEANER = Cleaner.create();

  final class CloseableWiredIterator implements WiredIterator<E>, AutoCloseable {

    private final WiredIterator<E> itr;

    CloseableWiredIterator(WiredIterator<E> itr) {
      getWriteLock(lock).lock();
      CLEANER.register(this, () -> getWriteLock(lock).unlock());
      this.itr = itr;
    }

    @Override
    public void close() {
      getWriteLock(lock).unlock();
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
    public WiredIterator<E> reverse() {
      return itr.reverse();
    }

  }

  /**
   * Forwards to {@link WiredList#of()}. Access to the underlying {@code WiredList}
   * is synchronized using a {@link ReentrantLock}. See {@link
   * #SynchronizedWiredList(boolean)}.
   */
  public static <E> SynchronizedWiredList<E> of() {
    return new SynchronizedWiredList<>();
  }

  /**
   * Forwards to {@link WiredList#of(Object)}. Access to the underlying {@code
   * WiredList} is synchronized using a {@link ReentrantLock}. See {@link
   * #SynchronizedWiredList(boolean)}.
   */
  public static <E> SynchronizedWiredList<E> of(E e) {
    return new SynchronizedWiredList<>(WiredList.of(e));
  }

  /**
   * Forwards to {@link WiredList#of(Object, Object)}. Access to the underlying
   * {@code WiredList} is synchronized using a {@link ReentrantLock}. See {@link
   * #SynchronizedWiredList(boolean)}.
   */
  public static <E> SynchronizedWiredList<E> of(E e0, E e1) {
    return new SynchronizedWiredList<>(WiredList.of(e0, e1));
  }

  /**
   * Forwards to {@link WiredList#of(Object, Object, Object, Object[])}. Access to
   * the underlying {@code WiredList} is synchronized using a {@link ReentrantLock}.
   * See {@link #SynchronizedWiredList(boolean)}.
   */
  public static <E> SynchronizedWiredList<E> of(E e0, E e1, E e2, E... moreElems) {
    return new SynchronizedWiredList<>(WiredList.of(e0, e1, e2, moreElems));
  }

  /**
   * Forwards to {@link WiredList#ofElements(Object[])}. Access to the underlying
   * {@code WiredList} is synchronized using a {@link ReentrantLock}. See {@link
   * #SynchronizedWiredList(boolean)}.
   */
  public static <E> SynchronizedWiredList<E> ofElements(E[] elements) {
    return new SynchronizedWiredList<>(WiredList.ofElements(elements));
  }

  /**
   * Forwards to {@link WiredList#stitch(List)}. Access to the underlying {@code
   * WiredList} is synchronized using a {@link ReentrantLock}. See {@link
   * #SynchronizedWiredList(boolean)}.
   */
  public static <E> SynchronizedWiredList<E> stitch(List<SynchronizedWiredList<E>> lists) {
    SynchronizedWiredList<E> wl = new SynchronizedWiredList<>();
    Check.notNull(lists).ok().forEach(wl::stitch);
    return wl;
  }

  private final Object lock;
  private final WiredList<E> wl;

  /**
   * Creates a new {@code SynchronizedWiredList} that synchronizes access to the
   * underlying {@code WiredList} using a {@link ReentrantLock}.
   */
  public SynchronizedWiredList() {
    this(false);
  }

  /**
   * Creates a new {@code SynchronizedWiredList} that synchronizes access to the
   * underlying {@code WiredList} using either a {@link ReentrantReadWriteLock} or a
   * {@link ReentrantLock}, depending on whether {@code writeIntensive} is {@code
   * true} or {@code false}, respectively. If you expect the list to become large
   * <i>and</i> you expect the number writes and/or writer threads to be
   * substantially lower than the number of reads and/or reader threads, choose the
   * former, else stick to the latter. See the class comments for {@link
   * ReentrantReadWriteLock} and {@link java.util.concurrent.locks.ReadWriteLock}.
   *
   * @param writeIntensive Whether you expect the number of writes to be
   *     substantially lower than the number of reads.
   */
  public SynchronizedWiredList(boolean writeIntensive) {
    this.lock = writeIntensive ? new ReentrantReadWriteLock() : new ReentrantLock();
    this.wl = new WiredList<>();
  }

  /**
   * Creates a new {@code SynchronizedWiredList} initialized with the values from the
   * specified {@code Collection}. Access to the underlying {@code WiredList} is
   * synchronized using a {@link ReentrantLock}. See {@link
   * #SynchronizedWiredList(boolean)}.
   *
   * @param c The collection to initialize this instance with
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
    return callReader(() -> wl.size());
  }

  /**
   * Forwards to {@link WiredList#isEmpty()}.
   */
  @Override
  public boolean isEmpty() {
    return callReader(() -> wl.isEmpty());
  }

  /**
   * Forwards to {@link WiredList#contains(Object)}.
   */
  @Override
  public boolean contains(Object o) {
    return callReader(() -> wl.contains(o));
  }

  /**
   * Forwards to {@link WiredList#containsAll(Collection)}.
   */
  @Override
  public boolean containsAll(Collection<?> c) {
    return callReader(() -> wl.containsAll(c));
  }

  /**
   * Forwards to {@link WiredList#get(int)}.
   */
  @Override
  public E get(int index) {
    return callReader(() -> wl.get(index));
  }

  /**
   * Forwards to {@link WiredList#set(int, Object)}.
   */
  @Override
  public E set(int index, E value) {
    return callWriter(() -> wl.set(index, value));
  }

  /**
   * Forwards to {@link WiredList#setIf(int, Predicate, Object)}.
   */
  public E setIf(int index, Predicate<? super E> test, E value) {
    return callWriter(() -> wl.setIf(index, test, value));
  }

  /**
   * Forwards to {@link WiredList#add(Object)}.
   */
  @Override
  public boolean add(E value) {
    return callWriter(() -> wl.add(value));
  }

  /**
   * Forwards to {@link WiredList#add(int, Object)}.
   */
  @Override
  public void add(int index, E value) {
    runWriter(() -> wl.add(index, value));
  }

  /**
   * Forwards to {@link WiredList#indexOf(Object)}.
   */
  @Override
  public int indexOf(Object o) {
    return callReader(() -> wl.indexOf(o));
  }

  /**
   * Forwards to {@link WiredList#lastIndexOf(Object)}.
   */
  @Override
  public int lastIndexOf(Object o) {
    return callReader(() -> wl.lastIndexOf(o));
  }

  /**
   * Forwards to {@link WiredList#addAll(Collection)}.
   */
  @Override
  public boolean addAll(Collection<? extends E> values) {
    return callWriter(() -> wl.addAll(values));
  }

  /**
   * Forwards to {@link WiredList#addAll(int, Collection)}.
   */
  @Override
  public boolean addAll(int index, Collection<? extends E> values) {
    return callWriter(() -> wl.addAll(index, values));
  }

  /**
   * Forwards to {@link WiredList#remove(int)}.
   */
  @Override
  public E remove(int index) {
    return callWriter(() -> wl.remove(index));
  }

  /**
   * Forwards to {@link WiredList#remove(Object)}.
   */
  @Override
  public boolean remove(Object o) {
    return callWriter(() -> wl.remove(o));
  }

  /**
   * Forwards to {@link WiredList#removeIf(Predicate)}.
   */
  @Override
  public boolean removeIf(Predicate<? super E> test) {
    return callWriter(() -> wl.removeIf(test));
  }

  /**
   * Forwards to {@link WiredList#removeAll(Collection)}.
   */
  @Override
  public boolean removeAll(Collection<?> c) {
    return callWriter(() -> wl.removeAll(c));
  }

  /**
   * Forwards to {@link WiredList#prepend(Object)}.
   */
  public SynchronizedWiredList<E> prepend(E value) {
    return callFluentWriter(() -> wl.prepend(value));
  }

  /**
   * Forwards to {@link WiredList#append(Object)}.
   */
  public SynchronizedWiredList<E> append(E value) {
    return callFluentWriter(() -> wl.append(value));
  }

  /**
   * Forwards to {@link WiredList#deleteFirst()}.
   */
  public SynchronizedWiredList<E> deleteFirst() {
    return callFluentWriter(() -> wl.deleteFirst());
  }

  /**
   * Forwards to {@link WiredList#deleteLast()}.
   */
  public SynchronizedWiredList<E> deleteLast() {
    return callFluentWriter(() -> wl.deleteLast());
  }

  /**
   * Forwards to {@link WiredList#prependAll(Collection)}.
   */
  public SynchronizedWiredList<E> prependAll(Collection<? extends E> values) {
    return callFluentWriter(() -> wl.prependAll(values));
  }

  /**
   * Forwards to {@link WiredList#appendAll(Collection)}.
   */
  public SynchronizedWiredList<E> appendAll(Collection<? extends E> values) {
    return callFluentWriter(() -> wl.appendAll(values));
  }

  /**
   * Forwards to {@link WiredList#insertAll(int, Collection)}.
   */
  public SynchronizedWiredList<E> insertAll(int index, Collection<?
      extends E> values) {
    return callFluentWriter(() -> wl.insertAll(index, values));
  }

  /**
   * Forwards to {@link WiredList#copy()}.
   */
  public SynchronizedWiredList<E> copy() {
    return callReader(() -> new SynchronizedWiredList<>(lock, wl.copy()));
  }

  /**
   * Forwards to {@link WiredList#copySegment(int, int)}.
   */
  public SynchronizedWiredList<E> copySegment(int fromIndex, int toIndex) {
    return callReader(
        () -> new SynchronizedWiredList<>(lock, wl.copySegment(fromIndex, toIndex)));
  }

  /**
   * Forwards to {@link WiredList#deleteSegment(int, int)}.
   */
  public SynchronizedWiredList<E> deleteSegment(int fromIndex, int toIndex) {
    return callFluentWriter(() -> wl.deleteSegment(fromIndex, toIndex));
  }

  /**
   * Forwards to {@link WiredList#embed(int, WiredList)}.
   */
  public SynchronizedWiredList<E> embed(int myIndex,
      SynchronizedWiredList<? extends E> other) {
    return callFluentWriter(() -> wl.embed(myIndex, other.wl));
  }

  /**
   * Forwards to {@link WiredList#stitch(WiredList)}.
   */
  public SynchronizedWiredList<E> stitch(SynchronizedWiredList<? extends E> other) {
    return embed(size(), other);
  }

  /**
   * Forwards to {@link WiredList#excise(int, WiredList, int, int)}.
   */
  public SynchronizedWiredList<E> excise(SynchronizedWiredList<? extends E> other,
      int itsFromIndex,
      int itsToIndex) {
    return callFluentWriter(() -> excise(size(),
        other,
        itsFromIndex,
        itsToIndex));
  }

  /**
   * Forwards to {@link WiredList#excise(int, WiredList, int, int)}.
   */
  public SynchronizedWiredList<E> excise(int myIndex,
      SynchronizedWiredList<? extends E> other,
      int itsFromIndex,
      int itsToIndex) {
    return callFluentWriter(() -> wl.excise(myIndex,
        other.wl,
        itsFromIndex,
        itsToIndex));
  }

  /**
   * Forwards to {@link WiredList#defragment(List)}.
   */
  public SynchronizedWiredList<E> defragment(List<Predicate<? super E>> criteria) {
    return callFluentWriter(() -> wl.defragment(criteria));
  }

  /**
   * Forwards to {@link WiredList#group(List)}.
   */
  public SynchronizedWiredList<SynchronizedWiredList<E>> group(List<Predicate<? super E>> criteria) {
    final Object lock = this.lock;
    final Lock writeLock = getWriteLock(lock);
    try {
      WiredList<WiredList<E>> wls = wl.group(criteria);
      SynchronizedWiredList<SynchronizedWiredList<E>> groups =
          new SynchronizedWiredList<>();
      Iterator<WiredList<E>> itr = wls.iterator();
      for (int i = 0; i < wls.size() - 1; ++i) {
        groups.add(new SynchronizedWiredList<>(lock, itr.next()));
      }
      groups.add(this);
      return groups;
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#partition(int)}.
   */
  public SynchronizedWiredList<SynchronizedWiredList<E>> partition(int size) {
    final Object lock = this.lock;
    final Lock writeLock = getWriteLock(lock);
    try {
      WiredList<WiredList<E>> wls = wl.partition(size);
      SynchronizedWiredList<SynchronizedWiredList<E>> partitions =
          new SynchronizedWiredList<>();
      Iterator<WiredList<E>> itr = wls.iterator();
      for (int i = 0; i < wls.size() - 1; ++i) {
        partitions.add(new SynchronizedWiredList<>(lock, itr.next()));
      }
      partitions.add(this);
      return partitions;
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#split(int)}.
   */
  public SynchronizedWiredList<SynchronizedWiredList<E>> split(int count) {
    final Object lock = this.lock;
    final Lock writeLock = getWriteLock(lock);
    try {
      WiredList<WiredList<E>> wls = wl.split(count);
      SynchronizedWiredList<SynchronizedWiredList<E>> partitions =
          new SynchronizedWiredList<>();
      Iterator<WiredList<E>> itr = wls.iterator();
      for (int i = 0; i < wls.size() - 1; ++i) {
        partitions.add(new SynchronizedWiredList<>(lock, itr.next()));
      }
      partitions.add(this);
      return partitions;
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#lchop(Predicate)}.
   */
  public SynchronizedWiredList<E> lchop(Predicate<? super E> condition) {
    return callFluentWriter(() -> wl.lchop(condition));
  }

  /**
   * Forwards to {@link WiredList#rchop(Predicate)}.
   */
  public SynchronizedWiredList<E> rchop(Predicate<? super E> condition) {
    return callFluentWriter(() -> wl.rchop(condition));
  }

  /**
   * Forwards to {@link WiredList#reverse()}.
   */
  public SynchronizedWiredList<E> reverse() {
    return callFluentWriter(() -> wl.reverse());
  }

  /**
   * Forwards to {@link WiredList#move(int, int, int)}.
   */
  public SynchronizedWiredList<E> move(int fromIndex,
      int toIndex,
      int newFromIndex) {
    return callFluentWriter(() -> wl.move(fromIndex, toIndex, newFromIndex));
  }

  /**
   * Forwards to {@link WiredList#retainAll(Collection)}.
   */
  public boolean retainAll(Collection<?> c) {
    return callWriter(() -> wl.retainAll(c));
  }

  /**
   * Forwards to {@link WiredList#toArray()}.
   */
  public Object[] toArray() {
    return callReader(() -> wl.toArray());
  }

  /**
   * Forwards to {@link WiredList#toArray(Object[])}.
   */
  public <T> T[] toArray(T[] a) {
    return callReader(() -> wl.toArray(a));
  }

  /**
   * Forwards to {@link WiredList#clear()}.
   */
  public void clear() {
    runWriter(() -> wl.clear());
  }

  /**
   * Forwards to {@link WiredList#iterator()}.
   */
  public Iterator<E> iterator() {
    return callReader(() -> wl.copy().iterator());
  }

  /**
   * Forwards to {@link WiredList#set(int, Object)}.
   */
  public Iterator<E> reverseIterator() {
    return callReader(() -> wl.copy().reverseIterator());
  }

  /**
   * Forwards to {@link WiredList#wiredIterator()}.
   */
  public WiredIterator<E> wiredIterator() {
    return new CloseableWiredIterator(wl.wiredIterator());
  }

  /**
   * Forwards to {@link WiredList#wiredIterator(boolean)}.
   */
  public WiredIterator<E> wiredIterator(boolean reverse) {
    return new CloseableWiredIterator(wl.wiredIterator(reverse));
  }

  /**
   * Forwards to {@link WiredList#listIterator(int)}.
   */
  public ListIterator<E> listIterator() {
    return callReader(() -> wl.copy().listIterator());
  }

  /**
   * Forwards to {@link WiredList#listIterator(int)}.
   */
  public ListIterator<E> listIterator(int index) {
    return callReader(() -> wl.copy().listIterator(index));
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
    if (o instanceof SynchronizedWiredList<?> swl) {
      Lock l0 = getReadLock(lock);
      try {
        Lock l1 = getReadLock(swl.lock);
        try {
          return wl.equals(swl.wl);
        } finally {
          l1.unlock();
        }
      } finally {
        l0.unlock();
      }
    }
    return false;
  }

  /**
   * Forwards to {@link WiredList#hashCode()}.
   */
  public int hashCode() {
    return callReader(() -> wl.hashCode());
  }

  /**
   * Forwards to {@link WiredList#toString()}.
   */
  public String toString() {
    return callReader(() -> wl.toString());
  }

  /**
   * Forwards to {@link WiredList#subList(int, int)} .
   */
  public List<E> subList(int fromIndex, int toIndex) {
    return wl.subList(fromIndex, toIndex);
  }

  private void runReader(Runnable method) {
    Lock l;
    (l = getReadLock(lock)).lock();
    try {
      method.run();
    } finally {
      l.unlock();
    }
  }

  private void runWriter(Runnable method) {
    Lock l;
    (l = getWriteLock(lock)).lock();
    try {
      method.run();
    } finally {
      l.unlock();
    }
  }

  private <R> R callReader(Callable<R> method) {
    Lock l;
    (l = getReadLock(lock)).lock();
    try {
      return method.call();
    } catch (Exception e) {
      throw ExceptionMethods.uncheck(e);
    } finally {
      l.unlock();
    }
  }

  private <R> R callWriter(Callable<R> method) {
    Lock l;
    (l = getWriteLock(lock)).lock();
    try {
      return method.call();
    } catch (Exception e) {
      throw ExceptionMethods.uncheck(e);
    } finally {
      l.unlock();
    }
  }

  private SynchronizedWiredList<E> callFluentWriter(Runnable method) {
    Lock l;
    (l = getWriteLock(lock)).lock();
    try {
      method.run();
      return this;
    } finally {
      l.unlock();
    }
  }

  private static Lock getReadLock(Object lock) {
    return lock instanceof ReentrantReadWriteLock x
        ? x.readLock()
        : (ReentrantLock) lock;
  }

  private static Lock getWriteLock(Object lock) {
    return lock instanceof ReentrantReadWriteLock x
        ? x.writeLock()
        : (ReentrantLock) lock;
  }

}
