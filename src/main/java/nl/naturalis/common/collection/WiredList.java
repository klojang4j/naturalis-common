package nl.naturalis.common.collection;

import nl.naturalis.common.CollectionMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.check.CommonChecks;
import nl.naturalis.common.check.IntCheck;
import nl.naturalis.common.x.invoke.InvokeUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyIterator;
import static java.util.Collections.emptyListIterator;
import static nl.naturalis.common.ArrayMethods.EMPTY_OBJECT_ARRAY;
import static nl.naturalis.common.MathMethods.divUp;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * A doubly-linked list, much like {@link LinkedList}, but exclusively focused on
 * list manipulation while disregarding its queue-like capabilities. As with any
 * doubly-linked list, index-based retrieval is relatively costly compared to {@link
 * ArrayList}. It is very efficient, however, at inserting, deleting and moving
 * around chunks of list elements (i.e. structural changes). The larger the chunks
 * the bigger the gain compared to {@code ArrayList}.
 *
 * <p>This implementation of {@link List} <b>does not support</b> the
 * {@link List#subList(int, int) subList} method.
 *
 * <h4>Use in multi-threaded context</h4>
 *
 * <p>Since list manipulation nearly always happens in a destructive manner for
 * the underlying data structure, careless use of a {@code WiredList} in a
 * multi-threaded context can leave it in a seriously compromised state. {@code
 * WiredList} itself makes no attempt to protect itself against this. You should use
 * a {@link SynchronizedWiredList} if it is likely that multiple threads accessing
 * the same list concurrently will cause the list to get corrupted.
 *
 * <h4>Iteration</h4>
 *
 * <p>You should always use an {@code Iterator} to iterate over the elements in the
 * list (which you implicitly would when executing a {@code forEach} loop). Using an
 * index/get loop is possible, but performs poorly. The {@link Iterator} and {@link
 * ListIterator} implementations prescribed by the {@code List} interface are
 * no-frills iterators that throw an {@code UnsupportedOperationException} from all
 * methods designated as optional by the specification. In addition, the {@code
 * WiredList class} also features a {@link #reverseIterator()} and a {@link
 * #wiredIterator()} method. The latter returns an instance of the {@link
 * WiredIterator} interface. Unlike a {@link ListIterator}, this is a one-way-only
 * iterator, but it still provides the same functionality, and it <i>does</i>
 * implement the methods that are optional in the {@code ListIterator} interface.
 *
 * @param <E> The type of the elements in the list
 */
public final class WiredList<E> implements List<E> {

  // Ubiquitous parameter names within this class
  private static final String INDEX = "index";
  private static final String WIRED_LIST = "WiredList";
  private static final String TEST = "test";
  private static final String COLLECTION = "collection";

  private static Supplier<IllegalStateException> emptyListNotAllowed() {
    return () -> new IllegalStateException("operation not allowed on empty list");
  }

  private static Supplier<IllegalArgumentException> emptySegmentNotAllowed() {
    return () -> new IllegalArgumentException("zero-length segments not allowed");
  }

  private static Supplier<IllegalArgumentException> autoEmbedNotAllowed() {
    return () -> new IllegalArgumentException("list cannot be embedded within itself");
  }

  private static Supplier<IllegalStateException> callNextFirst() {
    return () -> new IllegalStateException("Iterator.next() must be called first");
  }

  private static Supplier<ConcurrentModificationException> concurrentModification() {
    return ConcurrentModificationException::new;
  }

  private static Supplier<NoSuchElementException> noSuchElement() {
    return NoSuchElementException::new;
  }

  // ======================================================= //
  // ======================= [ Node ] ====================== //
  // ======================================================= //

  // @VisibleForTesting
  private static class Node<F> {

    F val;
    Node<F> prev;
    Node<F> next;

    Node(F val) {this.val = val;}

    Node(Node<F> prev, F val) {
      this.prev = prev;
      this.val = val;
      prev.next = this;
    }

    F value() {
      return val;
    }

    public String toString() {
      return String.valueOf(val);
    }

  }

  // ======================================================= //
  // ====================== [ Chain ] ====================== //
  // ======================================================= //

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static class Chain {

    static <F> Chain of(F[] values) {
      var head = new Node(values[0]);
      var tail = head;
      for (int i = 1; i < values.length; ++i) {
        tail = new Node(tail, values[i]);
      }
      return new Chain(head, tail, values.length);
    }

    static <F> Chain of(Collection<F> values) {
      if (values instanceof WiredList wl) {
        return copyOf(wl.head, wl.size());
      }
      Iterator<F> itr = values.iterator();
      var head = new Node<>(itr.next());
      var tail = head;
      while (itr.hasNext()) {
        tail = new Node<>(tail, itr.next());
      }
      return new Chain(head, tail, values.size());
    }

    static Chain copyOf(Node node, int len) {
      var head = new Node(node.val);
      var tail = head;
      for (int i = 1; i < len; ++i) {
        tail = new Node(tail, (node = node.next).val);
      }
      return new Chain(head, tail, len);
    }

    final Node head;
    final Node tail;
    final int length;

    Chain(Node head, Node tail, int length) {
      this.head = head;
      this.tail = tail;
      this.length = length;
    }

  }
  // ======================================================= //
  // ================== [ WiredIterator ]  ================= //
  // ======================================================= //

  final class ForwardWiredIterator implements WiredIterator<E> {

    private Node<E> beforeHead;
    private Node<E> curr;

    private ForwardWiredIterator() {
      curr = beforeHead = justBeforeHead();
    }

    private ForwardWiredIterator(Node<E> curr) {
      this.curr = curr;
    }

    @Override
    public boolean hasNext() {
      return sz != 0 && curr != tail;
    }

    @Override
    public E peek() {
      Check.that(sz).is(ne(), 0, noSuchElement());
      Check.that(curr).isNot(sameAs(), tail, noSuchElement());
      return Check.that(curr.next).is(notNull(), noSuchElement()).ok(Node::value);
    }

    @Override
    public E next() {
      Check.that(sz).is(ne(), 0, noSuchElement());
      Check.that(curr).isNot(sameAs(), tail, noSuchElement());
      return Check.that(curr = curr.next)
          .is(notNull(), noSuchElement())
          .ok(Node::value);
    }

    @Override
    public void set(E newVal) {
      Check.that(sz).is(ne(), 0, emptyListNotAllowed());
      Check.that(curr).isNot(sameAs(), beforeHead, callNextFirst());
      curr.val = newVal;
    }

    @Override
    public void remove() {
      Check.that(sz).is(ne(), 0, emptyListNotAllowed());
      Node<E> node = this.curr;
      Check.that(node).isNot(sameAs(), beforeHead, callNextFirst());
      if (sz == 1) {
        destroy(node);
      } else if (node == head) {
        destroy(node);
        node = beforeHead = justBeforeHead();
      } else {
        Check.that(node = node.prev).is(notNull(), noSuchElement());
        destroy(node.next);
      }
      this.curr = node;
    }

    @Override
    public WiredIterator<E> reverse() {
      Check.that(sz).is(ne(), 0, emptyListNotAllowed());
      return Check.that(curr).isNot(sameAs(), beforeHead, callNextFirst()).ok(
          ReverseWiredIterator::new);
    }

  }

  final class ReverseWiredIterator implements WiredIterator<E> {

    private Node<E> afterTail;
    private Node<E> curr;

    private ReverseWiredIterator() {
      curr = afterTail = justAfterTail();
    }

    private ReverseWiredIterator(Node<E> curr) {
      this.curr = curr;
    }

    @Override
    public boolean hasNext() {
      return sz != 0 && curr != head;
    }

    @Override
    public E peek() {
      Check.that(sz).is(ne(), 0, noSuchElement());
      Check.that(curr).isNot(sameAs(), head, noSuchElement());
      return Check.that(curr.prev).is(notNull(), noSuchElement()).ok(Node::value);
    }

    @Override
    public E next() {
      Check.that(sz).is(ne(), 0, emptyListNotAllowed());
      Check.that(curr).isNot(sameAs(), head, noSuchElement());
      return Check.that(curr = curr.prev)
          .is(notNull(), noSuchElement())
          .ok(Node::value);
    }

    @Override
    public void set(E newVal) {
      Check.that(sz).is(ne(), 0, emptyListNotAllowed());
      Check.that(curr).isNot(sameAs(), afterTail, callNextFirst());
      curr.val = newVal;
    }

    @Override
    public void remove() {
      Check.that(sz).is(ne(), 0, emptyListNotAllowed());
      Node<E> node = this.curr;
      Check.that(node).isNot(sameAs(), afterTail, callNextFirst());
      if (sz == 1) {
        delete(node);
      } else if (node == tail) {
        delete(node);
        node = afterTail = justAfterTail();
      } else {
        Check.that(node = node.next)
            .is(notNull(), concurrentModification())
            .then(x -> delete(x.prev));
      }
      this.curr = node;
    }

    @Override
    public WiredIterator<E> reverse() {
      Check.that(sz).is(ne(), 0, emptyListNotAllowed());
      return Check.that(curr).isNot(sameAs(), afterTail, callNextFirst()).ok(
          ForwardWiredIterator::new);
    }

  }

  private class ListItr implements ListIterator<E> {

    Node<E> curr;
    Boolean forward;
    int idx;

    ListItr() {
      curr = justBeforeHead();
      idx = -1;
    }

    ListItr(int index) {
      curr = nodeAt(idx = index);
    }

    @Override
    public boolean hasNext() {
      return idx < sz - 1 && curr.next != null;
    }

    @Override
    public E next() {
      if (forward != FALSE) {
        Check.that(++idx).is(lt(), sz, noSuchElement());
        return Check.that(curr = curr.next).is(notNull(),
                noSuchElement()) // we are in deep space
            .ok(Node::value);
      }
      forward = TRUE;
      return curr.val;
    }

    @Override
    public boolean hasPrevious() {
      return idx > 0 && curr.prev != null;
    }

    @Override
    public E previous() {
      Check.that(forward).is(notNull(), noSuchElement());
      if (forward == FALSE) {
        Check.that(--idx).is(gte(), 0, noSuchElement());
        return Check.that(curr = curr.prev)
            .is(notNull(), noSuchElement())
            .ok(Node::value);
      }
      forward = FALSE;
      return curr.val;
    }

    @Override
    public int nextIndex() {
      return idx + 1;
    }

    @Override
    public int previousIndex() {
      return Math.min(-1, idx - 1);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings({"unused"})
    public void set(E value) {
      throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings({"unused"})
    public void add(E value) {
      throw new UnsupportedOperationException();
    }

  }

  // ======================================================= //
  // ==================== [ WiredList ] ==================== //
  // ======================================================= //

  /**
   * Returns a new, empty {@code WiredList}. Note that, although the {@code of(..)}
   * methods look like the {@code List.of(...)} methods, they return ordinary,
   * mutable, {@code null}-accepting {@code WiredList} instances.
   *
   * @param <E> The type of the elements in the list
   * @return A new, empty {@code WiredList}
   */
  public static <E> WiredList<E> of() {
    return new WiredList<>();
  }

  /**
   * Returns a new {@code WiredList} containing the specified element.
   *
   * @param e The element
   * @param <E> The type of the elements in the list
   * @return A new {@code WiredList} containing the specified elements
   */
  public static <E> WiredList<E> of(E e) {
    return new WiredList<E>().append(e);
  }

  /**
   * Returns a new {@code WiredList} containing the specified elements.
   *
   * @param e0 The first element in the list
   * @param e1 The second element in the list
   * @param <E> The type of the elements in the list
   * @return A new {@code WiredList} containing the specified elements
   */
  public static <E> WiredList<E> of(E e0, E e1) {
    return new WiredList<E>().append(e0).append(e1);
  }

  /**
   * Returns a new {@code WiredList} containing the specified elements.
   *
   * @param e0 The first element in the list
   * @param e1 The second element in the list
   * @param e2 The third element in the list
   * @param moreElems More elements to include in the list
   * @param <E> The type of the elements in the list
   * @return A new {@code WiredList} containing the specified elements
   */
  @SafeVarargs
  public static <E> WiredList<E> of(E e0, E e1, E e2, E... moreElems) {
    Check.notNull(moreElems, "moreElems");
    var wl = new WiredList<E>().append(e0).append(e1).append(e2);
    if (moreElems.length != 0) {
      wl.insert(3, Chain.of(moreElems));
    }
    return wl;
  }

  /**
   * Returns a new {@code WiredList} containing the specified elements.
   *
   * @param elements The elements to add to the list
   * @param <E> The type of the elements in the list
   * @return A new {@code WiredList} containing the specified elements
   */
  public static <E> WiredList<E> ofElements(E[] elements) {
    Check.notNull(elements);
    WiredList<E> wl = new WiredList<>();
    if (elements.length > 0) {
      wl.insert(0, Chain.of(elements));
    }
    return wl;
  }

  /**
   * Concatenates the provided {@code WiredList} instances. This is a destructive
   * operation for the argument; the instances will be empty when the method returns.
   * See {@link #stitch(WiredList)}.
   *
   * @param lists The {@code WiredList} instances to concatenate
   * @param <E> The type of the elements in the list
   * @return A new {@code WiredList} containing the elements in the individual {@code
   *     WiredList} instances
   */
  public static <E> WiredList<E> stitch(List<WiredList<E>> lists) {
    WiredList<E> wl = new WiredList<>();
    Check.notNull(lists).ok().forEach(wl::stitch);
    return wl;
  }

  private Node<E> head;
  private Node<E> tail;
  private int sz;

  /**
   * Creates a new, empty {@code WiredList}.
   */
  public WiredList() {}

  /**
   * Creates a new {@code WiredList} containing the elements in the specified {@code
   * Collection}.
   *
   * @param c The collection whose elements to copy to this {@code WiredList}
   */
  public WiredList(Collection<? extends E> c) {
    addAll(0, c);
  }

  @SuppressWarnings({"unchecked"})
  private WiredList(Chain chain) {
    head = chain.head;
    tail = chain.tail;
    sz = chain.length;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int size() {
    return sz;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isEmpty() {
    return sz == 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean contains(Object o) {
    return indexOf(o) != -1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsAll(Collection<?> c) {
    Check.notNull(c, COLLECTION);
    return new HashSet<>(this).containsAll(c);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int indexOf(Object o) {
    int i = 0;
    if (o == null) {
      for (var x = head; x != null; x = x.next) {
        if (x.val == null) {
          return i;
        }
        ++i;
      }
    } else {
      for (var x = head; x != null; x = x.next) {
        if (o.equals(x.val)) {
          return i;
        }
        ++i;
      }
    }
    return -1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int lastIndexOf(Object o) {
    int i = sz - 1;
    if (o == null) {
      for (var x = tail; x != null; x = x.prev) {
        if (x.val == null) {
          return i;
        }
        --i;
      }
    } else {
      for (var x = tail; x != null; x = x.prev) {
        if (o.equals(x.val)) {
          return i;
        }
        --i;
      }

    }
    return -1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public E get(int index) {
    return checkExclusive(index).ok(this::nodeAt).val;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public E set(int index, E value) {
    var node = checkExclusive(index).ok(this::nodeAt);
    E old = node.val;
    node.val = value;
    return old;
  }

  /**
   * Sets the element at the specified index to the specified value if the original
   * value passes the specified test. This method mitigates the relatively large cost
   * of index-based retrieval with linked lists, which would double if you had to
   * execute a get-compare-set sequence.
   *
   * @param index The index of the element to set
   * @param test The test that the original value has to pass in order to be
   *     replaced with the new value. The original value is passed to the predicate's
   *     {@code test} method.
   * @param value The value to set
   * @return The original value
   */
  public E setIf(int index, Predicate<? super E> test, E value) {
    Check.notNull(test, TEST);
    var node = checkExclusive(index).ok(this::nodeAt);
    E old = node.val;
    if (test.test(old)) {
      node.val = value;
    }
    return old;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean add(E value) {
    append(value);
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void add(int index, E value) {
    checkInclusive(index);
    insert(index, new Node<>(value));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean addAll(Collection<? extends E> values) {
    return addAll(sz, values);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean addAll(int index, Collection<? extends E> values) {
    checkInclusive(index);
    Check.notNull(values, COLLECTION);
    if (!values.isEmpty()) {
      insert(index, Chain.of(values));
    }
    return !values.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public E remove(int index) {
    Node<E> node = checkExclusive(index).ok(this::nodeAt);
    return destroy(node);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean remove(Object o) {
    if (o == null) {
      for (var x = head; x != null; ) {
        if (x.val == null) {
          delete(x);
          return true;
        }
        x = x.next;
      }
    } else {
      for (var x = head; x != null; ) {
        if (o.equals(x.val)) {
          destroy(x);
          return true;
        }
        x = x.next;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean removeIf(Predicate<? super E> test) {
    Check.notNull(test, TEST);
    int size = sz;
    for (var x = head; x != null; ) {
      if (test.test(x.val)) {
        var next = x.next;
        destroy(x);
        x = next;
      } else {
        x = x.next;
      }
    }
    return size != this.sz;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean removeAll(Collection<?> c) {
    Check.notNull(c, COLLECTION);
    int size = this.sz;
    removeIf(c::contains);
    return size != this.sz;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean retainAll(Collection<?> c) {
    Check.notNull(c, COLLECTION);
    int sz = this.sz;
    removeIf(e -> !c.contains(e));
    return sz != this.sz;
  }

  /**
   * Inserts the specified value at the start of the list, right-shifting the
   * original elements.
   *
   * @param value The value to insert
   */
  public WiredList<E> prepend(E value) {
    insert(0, new Node<>(value));
    return this;
  }

  /**
   * Appends the specified value to the end of the list. Equivalent to {@code
   * add(value)}.
   *
   * @param value The value to append to the list
   */
  public WiredList<E> append(E value) {
    insert(sz, new Node<>(value));
    return this;
  }

  /**
   * Appends the specified value to the end of the list. Equivalent to {@code
   * add(value)}.
   *
   * @param value The value to append to the list
   */
  public WiredList<E> insert(int index, E value) {
    checkInclusive(index);
    insert(index, new Node<>(value));
    return this;
  }

  /**
   * Removes the first element from the list, left-shifting the remaining elements.
   *
   * @return The value of the removed element
   */
  public E deleteFirst() {
    Check.that(sz).is(ne(), 0, emptyListNotAllowed());
    return destroy(head);
  }

  /**
   * Removes the last element from the list.
   *
   * @return The value of the removed element
   */
  public E deleteLast() {
    Check.that(sz).is(ne(), 0, emptyListNotAllowed());
    return destroy(tail);
  }

  /**
   * Inserts the specified collection at the start of this {@code WiredList},
   * right-shifting the original elements.
   */
  public WiredList<E> prependAll(Collection<? extends E> values) {
    Check.notNull(values, COLLECTION);
    if (!values.isEmpty()) {
      insert(0, Chain.of(values));
    }
    return this;
  }

  public WiredList<E> appendAll(Collection<? extends E> values) {
    Check.notNull(values, COLLECTION);
    if (!values.isEmpty()) {
      insert(sz, Chain.of(values));
    }
    return this;
  }

  /**
   * Inserts the specified collection at the specified index, right-shifting the
   * elements at and following the index.
   */
  public WiredList<E> insertAll(int index, Collection<? extends E> values) {
    checkInclusive(index);
    Check.notNull(values, COLLECTION);
    if (!values.isEmpty()) {
      insert(index, Chain.of(values));
    }
    return this;
  }

  /**
   * Returns a deep copy of this {@code WiredList}. Changes made to the copy will not
   * propagate to this instance, and vice versa.
   *
   * @return A deep copy of this {@code WiredList}
   */
  public WiredList<E> copy() {
    return sz > 0 ? new WiredList<>(Chain.copyOf(head, sz)) : WiredList.of();
  }

  /**
   * Returns a deep copy of the specified segment. Changes made to the copy will not
   * propagate to this instance, and vice versa.
   *
   * @param fromIndex The start index (inclusive) of the segment
   * @param toIndex The end index (exclusive) of the segment
   * @return A deep copy of the specified segment
   */
  public WiredList<E> copySegment(int fromIndex, int toIndex) {
    int len = Check.fromTo(this, fromIndex, toIndex);
    return len > 0
        ? new WiredList<>(Chain.copyOf(nodeAt(fromIndex), len))
        : WiredList.of();
  }

  /**
   * Removes and returns a segment from the list. The segment must contain at least
   * one element.
   *
   * @param fromIndex The start index (inclusive) of the segment to delete
   * @param toIndex The end index (exclusive) of the segment to delete
   * @return The deleted segment
   */
  public WiredList<E> deleteSegment(int fromIndex, int toIndex) {
    checkSegment(fromIndex, toIndex);
    return delete(fromIndex, toIndex);
  }

  /**
   * Embeds the specified list in this list. This method is very efficient, but it is
   * a destructive operation for the provided list - it will be empty afterwards. If
   * you don't want this to happen, use {@link #addAll(int, Collection) addAll}.
   *
   * @param index The index at which to embed the list
   * @param other The list to embed
   * @return this {@code WiredList}
   */
  public WiredList<E> embed(int index, WiredList<? extends E> other) {
    checkInclusive(index);
    Check.notNull(other, WIRED_LIST).isNot(sameAs(), this, autoEmbedNotAllowed());
    if (!other.isEmpty()) {
      insert(index, new Chain(other.head, other.tail, other.sz));
      // Reset but don't clear the embedded list, because that
      // would nullify the nodes we just embedded in this list
      other.head = null;
      other.tail = null;
      other.sz = 0;
    }
    return this;
  }

  /**
   * Appends the specified list to this list. This method is very efficient, but it
   * is a destructive operation for the provided list - it will be empty afterwards.
   * If you don't want this to happen, use {@link #addAll(Collection) addAll}.
   *
   * @param other The list to embed
   * @return this {@code WiredList}
   * @see #stitch(WiredList)
   */
  public WiredList<E> stitch(WiredList<? extends E> other) {
    return embed(sz, other);
  }

  /**
   * Removes a segment from the specified list and appends it to this list. The
   * segment must contain at least one element.
   *
   * @param other The list to remove the segment from
   * @param itsFromIndex The start index of the segment (inclusive)
   * @param itsToIndex The end index of the segment (exclusive)
   * @return This {@code WiredList}
   */
  public WiredList<E> excise(WiredList<? extends E> other,
      int itsFromIndex,
      int itsToIndex) {
    return excise(sz, other, itsFromIndex, itsToIndex);
  }

  /**
   * Removes a segment from the specified list and embeds it in this list. The
   * segment must contain at least one element.
   *
   * @param myIndex The index at which to insert segment
   * @param other The list to remove the segment from
   * @param itsFromIndex The start index of the segment (inclusive)
   * @param itsToIndex The end index of the segment (exclusive)
   * @return This {@code WiredList}
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public WiredList<E> excise(int myIndex,
      WiredList<? extends E> other,
      int itsFromIndex,
      int itsToIndex) {
    checkInclusive(myIndex);
    Check.notNull(other, WIRED_LIST);
    other.checkSegment(itsFromIndex, itsToIndex);
    Node first = other.nodeAt(itsFromIndex);
    Node last = other.nodeAfter(first, itsFromIndex, itsToIndex - 1);
    Chain chain = new Chain(first, last, itsToIndex - itsFromIndex);
    // deleteNode MUST precede insertNode
    other.delete(chain);
    insert(myIndex, chain);
    return this;
  }

  /**
   * Groups the elements in the list according to the provided criteria. Once the
   * operation is complete, the elements satisfying the first criterion (if any) will
   * come first in the list, the elements satisfying the second criterion (if any)
   * will come second, etc. The elements that did not satisfy any criterion will come
   * last.
   *
   * @param criteria The criteria used to group the elements
   * @return This {@code WiredList}
   */
  @SuppressWarnings({"rawtypes"})
  public WiredList<E> defragment(List<Predicate<? super E>> criteria) {
    Check.that(criteria).isNot(empty());
    Predicate[] predicates = criteria.toArray(Predicate[]::new);
    WiredList[] groups = createGroups(predicates);
    Chain rest = new Chain(head, tail, sz);
    sz = 0;
    for (WiredList wl : groups) {
      if (!wl.isEmpty()) {
        insert(sz, new Chain(wl.head, wl.tail, wl.sz));
      }
    }
    if (rest.length != 0) {
      insert(sz, rest);
    }
    return this;
  }

  /**
   * Groups the elements according to the provided criteria. For each criterion a
   * separate {@code WiredList} is created that will contain the elements satisfying
   * the criterion. This {@code WiredList} is left with all elements that did not
   * satisfy any criterion, and it will be the last element in the returned
   * list-of-lists. In other words, the size of the returned list-of-lists is the
   * number of criteria plus one. You can use the {@link #stitch(List)} method to
   * create a single "defragmented" list again. Elements will never be placed in more
   * than one group. As soon as an element is found to satisfy a criterion it is
   * placed in the corresponding group and the remaining criteria are skipped.
   *
   * @param criteria The criteria used to group the elements
   * @return A list of element groups
   * @see #stitch(WiredList)
   */
  @SuppressWarnings({"rawtypes"})
  public WiredList<WiredList<E>> group(List<Predicate<? super E>> criteria) {
    Check.that(criteria).isNot(empty());
    Predicate[] predicates = criteria.toArray(Predicate[]::new);
    WiredList<E>[] groups = createGroups(predicates);
    WiredList<WiredList<E>> result = WiredList.ofElements(groups);
    result.add(this);
    return result;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private WiredList<E>[] createGroups(Predicate[] predicates) {
    WiredList[] groups = new WiredList[predicates.length];
    for (int i = 0; i < predicates.length; ++i) {
      groups[i] = new WiredList();
    }
    for (Node node = head; node != null; ) {
      var next = node.next;
      for (int i = 0; i < predicates.length; ++i) {
        if (predicates[i].test(node.val)) {
          delete(node);
          WiredList wl = groups[i];
          wl.insert(wl.size(), node);
          break;
        }
      }
      node = next;
    }
    return groups;
  }

  /**
   * Splits this {@code WiredList} into multiple {@code WiredList} instances of the
   * specified size. This method is destructive for the {@code WiredList} it operates
   * on. The partitions are chopped off from the {@code WiredList} and then placed in
   * a separate {@code WiredList}. The last element in the returned list-of-lists is
   * the {@code WiredList} itself, and it may (now) contain less than {@code size}
   * elements.
   *
   * @param size The desired size of the partitions
   * @return A list of {@code WiredList} instances of the specified size
   */
  public WiredList<WiredList<E>> partition(int size) {
    Check.that(size, "partition size").is(gt(), 0);
    WiredList<WiredList<E>> partitions = new WiredList<>();
    while (sz > size) {
      Chain chain = new Chain(head, nodeAt(size - 1), size);
      delete(chain);
      partitions.append(new WiredList<>(chain));
    }
    partitions.append(this);
    return partitions;
  }

  /**
   * Splits this {@code WiredList} into the specified number of {@code WiredList}
   * instances. See {@link #partition(int)}.
   *
   * @param count The number of {@code WiredList} instances to split this {@code
   *     WiredList} into
   * @return A list containing the specified number of {@code WiredList} instances
   */
  public WiredList<WiredList<E>> split(int count) {
    Check.that(count).is(gt(), 0);
    return partition(divUp(sz, count));
  }

  /**
   * Removes and returns a segment from the start of the list. The segment includes
   * all elements satisfying the specified condition, up to the first element that
   * does <i>not</i> satisfy the condition. If the condition is never satisfied, this
   * list remains unchanged and an empty list is returned. If <i>all</i> elements
   * satisfy the condition, the list remains unchanged and is itself returned.
   *
   * @param condition The condition that the elements in the returned segment
   *     will satisfy
   * @return A {@code WiredList} containing all elements preceding the first element
   *     that does not satisfy the condition
   */
  public WiredList<E> lchop(Predicate<? super E> condition) {
    Check.notNull(condition);
    if (sz == 0) {
      return this;
    }
    Node<E> first = head;
    Node<E> last = justBeforeHead();
    int len = 0;
    for (; condition.test(last.next.val) && ++len != sz; last = last.next)
      ;
    if (len == sz) {
      return this;
    }
    Chain chain = new Chain(first, last, len);
    delete(chain);
    return new WiredList<>(chain);
  }

  /**
   * Removes and returns a segment from the end of the list. The segment includes all
   * elements satisfying the specified condition, following after the last element
   * that does <i>not</i> satisfy the condition. If the condition is never satisfied,
   * this list remains unchanged and an empty list is returned. If <i>all</i>
   * elements satisfy the condition, the list remains unchanged and is itself
   * returned.
   *
   * @param condition The condition that the elements in the returned segment
   *     will satisfy
   * @return A {@code WiredList} containing all elements after the last element that
   *     does not satisfy the condition
   */
  public WiredList<E> rchop(Predicate<? super E> condition) {
    Check.notNull(condition);
    if (sz == 0) {
      return this;
    }
    Node<E> last = tail;
    Node<E> first = justAfterTail();
    int len = 0;
    for (; condition.test(first.prev.val) && ++len != sz; first = first.prev)
      ;
    if (len == sz) {
      return this;
    }
    Chain chain = new Chain(first, last, len);
    delete(chain);
    return new WiredList<>(chain);
  }

  /**
   * Reverses the order of the elements in this {@code WiredList}.
   *
   * @return This {@code WiredList}
   */
  public WiredList<E> reverse() {
    if (sz > 1) {
      Node<E> next;
      for (var x = head; ; ) {
        next = x.next;
        x.next = x.prev;
        if ((x.prev = next) == null) {
          break;
        }
        x = next;
      }
      next = head;
      head = tail;
      tail = next;
    }
    return this;
  }

  /**
   * Moves a list segment forward or backwards through the list. The segment must
   * contain at least one element.
   *
   * @param fromIndex The start index of the segment (inclusive)
   * @param toIndex The end index of the segment (exclusive)
   * @param newFromIndex The desired start index of the segment. To move the
   *     segment to the very start of the list, specify 0 (zero). To move the segment
   *     to the very end of the list specify the {@link #size() size} of the list
   */
  public WiredList<E> move(int fromIndex, int toIndex, int newFromIndex) {
    Check.on(indexOutOfBounds(), fromIndex, "fromIndex").is(gte(), 0);
    Check.on(indexOutOfBounds(), toIndex, "toIndex").is(lte(), sz).is(gt(),
        fromIndex,
        emptySegmentNotAllowed());
    Check.on(indexOutOfBounds(), newFromIndex, "newFromIndex").is(gte(), 0).is(lte(),
        sz);
    if (newFromIndex > fromIndex) {
      moveToTail(fromIndex, toIndex, newFromIndex);
    } else if (newFromIndex < fromIndex) {
      moveToHead(fromIndex, toIndex, newFromIndex);
    }
    return this;
  }

  private void moveToTail(int from, int to, int newFrom) {
    int indexOfLast = to - 1;
    int steps = newFrom - from;
    Node<E> first = nodeAt(from);
    Node<E> last = nodeAfter(first, from, indexOfLast);
    Node<E> insertAfter = nodeAfter(last, indexOfLast, indexOfLast + steps);
    if (first == head) {
      makeHead(last.next);
    } else {
      join(first.prev, last.next);
    }
    if (insertAfter == tail) {
      join(insertAfter, first);
      makeTail(last);
    } else {
      join(last, insertAfter.next);
      join(insertAfter, first);
    }
  }

  private void moveToHead(int from, int to, int newFrom) {
    Node<E> first = nodeAt(from);
    Node<E> last = nodeAfter(first, from, to - 1);
    Node<E> insertBefore = nodeBefore(first, from, newFrom);
    if (last == tail) {
      makeTail(first.prev);
    } else {
      join(first.prev, last.next);
    }
    if (insertBefore == head) {
      join(last, insertBefore);
      makeHead(first);
    } else {
      join(insertBefore.prev, first);
      join(last, insertBefore);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] toArray() {
    if (sz == 0) {
      return EMPTY_OBJECT_ARRAY;
    }
    Object[] result = new Object[sz];
    int i = 0;
    for (E val : this) {
      result[i++] = val;
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T[] toArray(T[] a) {
    Check.notNull(a);
    if (a.length < sz) {
      a = InvokeUtils.newArray(a.getClass(), sz);
    }
    int i = 0;
    Object[] result = a;
    for (E val : this) {
      result[i++] = val;
    }
    if (a.length > sz) {
      a[sz] = null;
    }
    return a;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear() {
    for (var x = head; x != null; ) {
      var next = x.next;
      x.val = null;
      x.prev = null;
      x.next = null;
      x = next;
    }
    head = tail = null;
    sz = 0;
  }

  /**
   * Returns an {@code Iterator} that traverses the list from the first element to
   * the last. It is a no-frills {@code Iterator} designed to make the traversal go
   * as quickly as possible, under the assumption that the list is not (structurally)
   * modified while the elements are being iterated over.
   *
   * @return An {@code Iterator} that traverses the list's elements from first to the
   *     last
   */
  @Override
  public Iterator<E> iterator() {
    return sz == 0 ? emptyIterator() : new Iterator<>() {

      private Node<E> curr = justBeforeHead();

      @Override
      public boolean hasNext() {
        return curr != tail;
      }

      @Override
      public E next() {
        return Check.that(curr = curr.next).is(notNull(),
                noSuchElement()) // we are in deep space
            .ok(Node::value);
      }
    };
  }

  /**
   * Returns an {@code Iterator} that traverses the list from the last element to the
   * first. See also {@link #iterator()}.
   *
   * @return An {@code Iterator} that traverses the list from the last element to the
   *     first
   */
  public Iterator<E> reverseIterator() {
    return sz == 0 ? emptyIterator() : new Iterator<>() {

      private Node<E> curr = justAfterTail();

      @Override
      public boolean hasNext() {
        return curr != head;
      }

      @Override
      public E next() {
        return Check.that(curr = curr.prev)
            .is(notNull(), noSuchElement())
            .ok(Node::value);
      }
    };
  }

  /**
   * Returns a {@link WiredIterator} that traverses the list from the first element
   * to the last.
   *
   * @return A {@code WiredIterator} that traverses the list from the first element
   *     to the last
   */
  public WiredIterator<E> wiredIterator() {
    return new ForwardWiredIterator();
  }

  /**
   * Returns a {@link WiredIterator} that traverses the list from the first element
   * to the last, or the other way round, depending on the value of the argument
   *
   * @return A {@code WiredIterator} that traverses the list from the first element
   *     to the last, or the other way round
   */
  public WiredIterator<E> wiredIterator(boolean reverse) {
    return reverse ? new ReverseWiredIterator() : new ForwardWiredIterator();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ListIterator<E> listIterator() {
    return isEmpty() ? emptyListIterator() : new ListItr();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ListIterator<E> listIterator(int index) {
    return checkExclusive(index).ok(ListItr::new);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o instanceof List l) {
      if (sz == 0) {
        return l.size() == 0;
      } else if (sz == l.size()) {
        var x = head;
        for (Object obj : l) {
          if (!Objects.equals(obj, x.val)) {
            return false;
          }
          x = x.next;
        }
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int hash = 1;
    for (E val : this) {
      hash = 31 * hash + Objects.hashCode(val);
    }
    return hash;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return '[' + CollectionMethods.implode(this) + ']';
  }

  /**
   * <b>Not supported by this implementation.</b>
   */
  @Override
  @SuppressWarnings({"unused"})
  public List<E> subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException();
  }

  ////////////////////////////////////////////////////////////////
  // IMPORTANT: If you want to reuse nodes or chains, the order of
  // the operations matter. Always first delete the node or chain,
  // and then insert it.
  ////////////////////////////////////////////////////////////////

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void insert(int index, Node node) {
    if (sz == 0) {
      makeHead(node);
      makeTail(node);
    } else if (index == 0) {
      join(node, head);
      makeHead(node);
    } else if (index == sz) {
      join(tail, node);
      makeTail(node);
    } else {
      var x = nodeAt(index);
      join(x.prev, node);
      join(node, x);
    }
    ++sz;
  }

  @SuppressWarnings("unchecked")
  private void insert(int index, Chain chain) {
    if (sz == 0) {
      makeHead(chain.head);
      makeTail(chain.tail);
    } else if (index == 0) {
      join(chain.tail, head);
      makeHead(chain.head);
    } else if (index == sz) {
      join(tail, chain.head);
      makeTail(chain.tail);
    } else {
      var node = nodeAt(index);
      join(node.prev, chain.head);
      if (chain.length == 1) {
        join(chain.head, node);
      } else {
        join(chain.tail, node);
      }
    }
    sz += chain.length;
  }

  private WiredList<E> delete(int from, int to) {
    var first = nodeAt(from);
    var last = nodeAfter(first, from, to - 1);
    int len = to - from;
    Chain chain = new Chain(first, last, len);
    delete(chain);
    return new WiredList<>(chain);
  }

  private E destroy(Node<E> node) {
    E val = node.val;
    node.val = null; // help garbage collector
    delete(node);
    return val;
  }

  private void delete(Node<E> node) {
    if (sz == 1) {
      head = tail = null;
    } else if (node == head) {
      makeHead(head.next);
    } else if (node == tail) {
      makeTail(tail.prev);
    } else {
      join(node.prev, node.next);
    }
    --sz;
  }

  @SuppressWarnings("unchecked")
  private void delete(Chain chain) {
    if (chain.length == sz) {
      head = tail = null;
    } else if (chain.head == head) {
      makeHead(chain.tail.next);
    } else if (chain.tail == tail) {
      makeTail(chain.head.prev);
    } else {
      join(chain.head.prev, chain.tail.next);
    }
    sz -= chain.length;
  }

  // @VisibleForTesting
  private Node<E> nodeAt(int index) {
    if (index < (sz >> 1)) {
      Node<E> node = head;
      for (int i = 0; i < index; ++i) {
        node = node.next;
      }
      return node;
    } else {
      Node<E> n = tail;
      for (int i = sz - 1; i > index; --i) {
        n = n.prev;
      }
      return n;
    }
  }

  /*
   * Used to minimize the amount of pointers we need to chase, given that
   * we've already gotten hold of another node. The index argument is the
   * index of the node we are interested in.
   */
  // @VisibleForTesting
  private Node<E> nodeAfter(Node<E> startNode, int startIndex, int index) {
    Node<E> x;
    if (index < ((sz + startIndex) >> 1)) {
      x = startNode;
      while (startIndex++ < index) {
        x = x.next;
      }
    } else {
      x = tail;
      while (++index < sz) {
        x = x.prev;
      }
    }
    return x;
  }

  // @VisibleForTesting
  private Node<E> nodeBefore(Node<E> startNode, int startIndex, int index) {
    Node<E> x;
    if (index < (startIndex >> 1)) {
      x = head;
      while (index-- > 0) {
        x = x.next;
      }
    } else {
      x = startNode;
      while (index++ < startIndex) {
        x = x.prev;
      }
    }
    return x;
  }

  private void makeHead(Node<E> node) {
    node.prev = null;
    head = node;
  }

  private void makeTail(Node<E> node) {
    node.next = null;
    tail = node;
  }

  private static <T> void join(Node<T> prev, Node<T> next) {
    prev.next = next;
    next.prev = prev;
  }

  private Node<E> justBeforeHead() {
    Node<E> x = new Node<>(null);
    x.next = head;
    return x;
  }

  private Node<E> justAfterTail() {
    Node<E> x = new Node<>(null);
    x.prev = tail;
    return x;
  }

  private IntCheck<IndexOutOfBoundsException> checkExclusive(int index) {
    return Check.on(indexOutOfBounds(), index, INDEX).is(CommonChecks.indexOf(),
        this);
  }

  private IntCheck<IndexOutOfBoundsException> checkInclusive(int index) {
    return Check.on(indexOutOfBounds(), index, INDEX).is(indexInclusiveOf(), this);
  }

  private void checkSegment(int fromIndex, int toIndex) {
    Check.on(indexOutOfBounds(), fromIndex, "fromIndex").is(gte(), 0);
    Check.on(indexOutOfBounds(), toIndex, "toIndex").is(lte(), sz).is(gt(),
        fromIndex,
        emptySegmentNotAllowed());
  }

}
