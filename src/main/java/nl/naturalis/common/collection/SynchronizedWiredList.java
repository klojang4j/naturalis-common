package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.lang.ref.Cleaner;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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

  final class CloseableWiredIterator implements WiredIterator<E> {

    private final WiredIterator<E> itr;
    private final boolean readOnly;

    CloseableWiredIterator(WiredIterator<E> itr, boolean readOnly) {
      this.itr = itr;
      this.readOnly = readOnly;
    }

    @Override
    public void close() {
      Lock l;
      (l = readOnly ? getReadLock() : getWriteLock()).unlock();
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
   * Forwards to {@link WiredList#stitch(List)}.
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
   * Forwards to {@link WiredList#iterator()}.
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
   * Forwards to {@link WiredList#set(int, Object)}.
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
   * Forwards to {@link WiredList#listIterator(int)}.
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
   * Forwards to {@link WiredList#listIterator(int)}.
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
      return new SynchronizedWiredList<>(lock, wl.prepend(value));
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
      return new SynchronizedWiredList<>(lock, wl.append(value));
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
  public SynchronizedWiredList<E> prependAll(Collection<? extends E> values) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return new SynchronizedWiredList<>(lock, wl.prependAll(values));
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#appendAll(Collection)}.
   */
  public SynchronizedWiredList<E> appendAll(Collection<? extends E> values) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return new SynchronizedWiredList<>(lock, wl.appendAll(values));
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#insertAll(int, Collection)}.
   */
  public SynchronizedWiredList<E> insertAll(int index,
      Collection<? extends E> values) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return new SynchronizedWiredList<>(lock, wl.insertAll(index, values));
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
  public SynchronizedWiredList<E> embed(int myIndex,
      SynchronizedWiredList<? extends E> other) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return new SynchronizedWiredList<>(lock, wl.embed(myIndex, other.wl));
    } finally {
      l.unlock();
    }
  }

  /**
   * Forwards to {@link WiredList#stitch(WiredList)}.
   */
  public SynchronizedWiredList<E> stitch(SynchronizedWiredList<? extends E> other) {
    Lock l;
    (l = getWriteLock()).lock();
    try {
      return new SynchronizedWiredList<>(lock, wl.stitch(other.wl));
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
      return new SynchronizedWiredList<>(lock,
          wl.excise(other.wl, itsFromIndex, itsToIndex));
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
      return new SynchronizedWiredList<>(lock,
          wl.excise(myIndex, other.wl, itsFromIndex, itsToIndex));
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
      return new SynchronizedWiredList<>(lock, wl.reverse());
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
      return new SynchronizedWiredList<>(lock,
          wl.move(fromIndex, toIndex, newFromIndex));
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
   * Forwards to {@link WiredList#wiredIterator()}.
   */
  public WiredIterator<E> wiredIterator(boolean readOnly) {
    Lock l;
    (l = readOnly ? getReadLock() : getWriteLock()).lock();
    WiredIterator<E> itr = wl.wiredIterator();
    WiredIterator<E> wrapper = new CloseableWiredIterator(itr, readOnly);
    CLEANER.register(wrapper, () -> l.unlock());
    return wrapper;
  }

  /**
   * Forwards to {@link WiredList#wiredIterator(boolean)}.
   */
  public WiredIterator<E> wiredIterator(boolean readOnly, boolean reverse) {
    Lock l;
    (l = readOnly ? getReadLock() : getWriteLock()).lock();
    WiredIterator<E> itr = wl.wiredIterator(reverse);
    WiredIterator<E> wrapper = new CloseableWiredIterator(itr, readOnly);
    CLEANER.register(wrapper, () -> l.unlock());
    return wrapper;
  }

  /**
   * Forwards to {@link WiredList#subList(int, int)} .
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
