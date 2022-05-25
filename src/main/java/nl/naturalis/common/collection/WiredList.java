package nl.naturalis.common.collection;

import nl.naturalis.common.CollectionMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.check.IntCheck;
import nl.naturalis.common.function.Relation;
import nl.naturalis.common.internal.VisibleForTesting;
import nl.naturalis.common.x.invoke.InvokeUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Collections.emptyIterator;
import static nl.naturalis.common.ArrayMethods.EMPTY_OBJECT_ARRAY;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * A doubly-linked list, much like {@link LinkedList}, but focused on list manipulation rather than
 * queue-like behaviour. As with any doubly-linked list, iteration and index-based retrieval is
 * relatively costly compared to {@link ArrayList}. It can be very efficient, however, at applying
 * structural changes to the list. That is: inserting, deleting and moving around large chunks of
 * list elements. The larger the segments the bigger the gain compared to {@code ArrayList}.
 *
 * <p>This implementation of the {@link List} interface <b>does not support</b> the
 * {@link List#subList(int, int) subList} and {@link List#listIterator() listIterator} methods.
 *
 * <h4>Iteration</h4>
 *
 * <p>You should always use an {@code Iterator} to iterate over the elements in the list, either
 * explicitly or implicitly, via the {@code forEach} loop. Using an index/get loop is possible, but
 * performs poorly. As mentioned, a {@code WiredList} does not support the {@code listIterator}
 * method. However, besides the standard {@link Iterable#iterator() iterator} method, it also
 * provides a {@link #reverseIterator()} and a {@link #wiredIterator(boolean) wiredIterator} method.
 * The latter returns an instance of the {@link WiredIterator} interface. Unlike a {@link
 * ListIterator} this is a one-way-only iterator, but still provides the same functionality,
 *
 * <p>None of the provided iterators is resistant against concurrent modifications of the list. The
 * standard {@code Iterator} is not even designed to be resistant against same-thread modifications
 * happening outside the iterator. (Everything happening in the iterator's rear mirror is fine
 * though.) It is just there to iterate as quickly as possible over the elements.
 *
 * @param <E> The type of the elements in the list
 */
public final class WiredList<E> implements List<E> {

  // Ubiquitous parameter names within this class
  private static final String INDEX = "index";
  private static final String LIST = "list";
  private static final String TEST = "test";

  private static final Supplier<IllegalStateException> ERR_EMPTY_LIST =
      () -> new IllegalStateException("empty list");

  private static final Supplier<IllegalArgumentException> ERR_EMPTY_SEGMENT =
      () -> new IllegalArgumentException("zero-length segments not allowed");

  private static final Supplier<IllegalArgumentException> ERR_AUTO_EMBED =
      () -> new IllegalArgumentException("list cannot be embedded within itself");

  private static final Supplier<IllegalStateException> ERR_ITERATOR_NEXT =
      () -> new IllegalStateException("Iterator.next() must be called first");

  private static Supplier<IllegalStateException> callNextFirst() {
    return () -> new IllegalStateException("Iterator.next() must be called first");
  }

  // ======================================================= //
  // ======================= [ Node ] ====================== //
  // ======================================================= //

  @VisibleForTesting
  static class Node<F> {

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

    static <F> Chain ofSingle(F value) {
      Node n = new Node(value);
      return new Chain(n, n, 1);
    }

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

    private static final Supplier<ConcurrentModificationException> CME =
        ConcurrentModificationException::new;

    private Node<E> curr = justBeforeHead();

    private ForwardWiredIterator() {
      curr = justBeforeHead();
    }

    private ForwardWiredIterator(Node<E> curr) {
      this.curr = curr;
    }

    @Override
    public boolean hasNext() {
      return sz != 0 && curr != tail;
    }

    @Override
    public E next() {
      Check.that(sz).is(ne(), 0, CME);
      Check.that(curr).isNot(sameAs(), tail, CME);
      return Check.that(curr = curr.next).is(notNull(), CME).ok(Node::value);
    }

    @Override
    public WiredIterator<E> reverse() {
      Check.that(curr.next).isNot(sameAs(), head, callNextFirst());
      return new ReverseWiredIterator(curr);
    }

    @Override
    public void set(E newVal) {
      Check.that(curr.next).isNot(sameAs(), head, callNextFirst());
      curr.val = newVal;
    }

    @Override
    public void remove() {
      Check.that(sz).is(ne(), 0, CME);
      Node<E> x = this.curr;
      Check.that(x.next).isNot(sameAs(), head, callNextFirst());
      if (sz == 1) {
        deleteNode(x);
      } else if (x == head) {
        deleteNode(x);
        x = justBeforeHead();
      } else {
        Check.that(x = x.prev).is(notNull(), CME).then(y -> deleteNode(y.next));
      }
      this.curr = x;
    }

  }

  final class ReverseWiredIterator implements WiredIterator<E> {

    private static final Supplier<ConcurrentModificationException> CME =
        ConcurrentModificationException::new;

    private Node<E> curr = justAfterTail();

    private ReverseWiredIterator() {
      curr = justAfterTail();
    }

    private ReverseWiredIterator(Node<E> curr) {
      this.curr = curr;
    }

    @Override
    public boolean hasNext() {
      return sz != 0 && curr != head;
    }

    @Override
    public E next() {
      Check.that(sz).is(ne(), 0, CME);
      Check.that(curr).isNot(sameAs(), head, CME);
      return Check.that(curr = curr.prev).is(notNull(), CME).ok(Node::value);
    }

    @Override
    public void set(E newVal) {
      Check.that(curr.prev).isNot(sameAs(), tail, callNextFirst());
      curr.val = newVal;
    }

    @Override
    public WiredIterator<E> reverse() {
      Check.that(curr.prev).isNot(sameAs(), tail, callNextFirst());
      return new ForwardWiredIterator(curr);
    }

    @Override
    public void remove() {
      Check.that(sz).is(ne(), 0, CME);
      Node<E> x = this.curr;
      Check.that(x.prev).isNot(sameAs(), tail, callNextFirst());
      if (sz == 1) {
        deleteNode(x);
      } else if (x == tail) {
        deleteNode(x);
        x = justAfterTail();
      } else {
        Check.that(x = x.next).is(notNull(), CME).then(y -> deleteNode(y.prev));
      }
      this.curr = x;
    }

  }

  // ======================================================= //
  // ==================== [ WiredList ] ==================== //
  // ======================================================= //

  /**
   * Returns a new, empty {@code WiredList}. Note that, although the {@code of(..)} methods look
   * like the {@code List.of(...)} methods, they return ordinary, mutable, {@code null}-accepting
   * {@code WiredList} instances.
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
    WiredList<E> wl = new WiredList<>();
    wl.push(e);
    return wl;
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
    WiredList<E> wl = new WiredList<>();
    wl.push(e0);
    wl.push(e1);
    return wl;
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
    WiredList<E> wl = new WiredList<>();
    wl.push(e0);
    wl.push(e1);
    wl.push(e2);
    if (moreElems.length != 0) {
      wl.insert(3, Chain.of(moreElems));
    }
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
   * Creates a new {@code WiredList} containing the elements in the specified {@code Collection}.
   *
   * @param c The collection whose elements to copy to this {@code WiredList}
   */
  public WiredList(Collection<? extends E> c) {
    insertAll(0, c);
  }

  private WiredList(Node<E> head, Node<E> tail, int sz) {
    this.head = head;
    this.tail = tail;
    this.sz = sz;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public E get(int index) {
    return checkIndex(index).ok(this::nodeAt).val;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public E set(int index, E value) {
    var node = checkIndex(index).ok(this::nodeAt);
    E old = node.val;
    node.val = value;
    return old;
  }

  /**
   * Sets the element at the specified index to the specified value if the original value passes the
   * specified test. This method mitigates the relatively large cost of index-based retrieval with
   * linked lists, which would double if you had to execute a get-compare-set sequence.
   *
   * @param index The index of the element to set
   * @param test The test that the original value has to pass in order to be replaced with the
   *     new value. The original value is passed to the predicate's {@code test} method.
   * @param value The value to set
   * @return The original value
   */
  public E setIf(int index, Predicate<? super E> test, E value) {
    Check.notNull(test, TEST);
    var node = checkIndex(index).ok(this::nodeAt);
    E old = node.val;
    if (test.test(old)) {
      node.val = value;
    }
    return old;
  }

  /**
   * Inserts the specified value at the specified index, right-shifting the elements at, and
   * following the index.
   *
   * @param value The value to insert
   */
  public void insert(int index, E value) {
    insert(index, Chain.ofSingle(value));
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
      System.out.println("Here!");
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
   * Appends the specified value to the end of the list.
   *
   * @param value The value to append to the list
   */
  public void push(E value) {
    insert(sz, value);
  }

  /**
   * Removes the last element from the list.
   *
   * @return The value of the removed element
   */
  public E pop() {
    Check.that(sz).is(ne(), 0, ERR_EMPTY_LIST);
    return delete0(sz, 1).head.val;
  }

  /**
   * Removes the first element from the list, left-shifting the remaining elements.
   *
   * @return The value of the removed element
   */
  public E shift() {
    Check.that(sz).is(ne(), 0, ERR_EMPTY_LIST);
    return delete0(0, 1).head.val;
  }

  /**
   * Inserts the specified value at the start of the list, right-shifting the original elements.
   *
   * @param value The value to insert
   */
  public void unshift(E value) {
    insert(0, value);
  }

  /**
   * Inserts the specified values at the start of the list.
   *
   * @param values The values to insert
   */
  public void prependAll(Collection<? extends E> values) {
    insertAll(0, values);
  }

  /**
   * Inserts the specified values at the end of the list.
   *
   * @param values The values to insert
   */
  public void appendAll(Collection<? extends E> values) {
    insertAll(sz, values);
  }

  /**
   * Inserts the specified values at the specified location, right-shifting the elements at, and
   * following the index..
   *
   * @param values The values to insert
   */
  public void insertAll(int index, Collection<? extends E> values) {
    Check.notNull(values, "values");
    if (!values.isEmpty()) {
      insert(index, Chain.of(values));
    }
  }

  /**
   * Embeds the specified list in this list. This method is very efficient, but the provided list
   * will empty afterwards. If you don't want this to happen, use {@link #insertAll(int, Collection)
   * insertAll}.
   *
   * @param index The index at which to embed the list
   * @param other The list to embed
   * @return this {@code WiredList}
   */
  public WiredList<E> embed(int index, WiredList<? extends E> other) {
    checkIndexInclusive(index);
    Check.notNull(other, LIST).isNot(sameAs(), this, ERR_AUTO_EMBED);
    if (!other.isEmpty()) {
      insert(index, new Chain(other.head, other.tail, other.sz));
      // Disable, but don't clear the embedded list, because that would
      // nullify the nodes we just embedded in this list
      other.head = null;
      other.tail = null;
      other.sz = 0;
    }
    return this;
  }

  /**
   * Embeds this {@code WiredList} in the specified {@code WiredList}. This {@code WiredList} will
   * be empty afterwards.
   *
   * @param index The index at which to embed this {@code WiredList}
   * @param other The list in which to embed this list
   * @return this {@code WiredList}
   */
  public WiredList<E> embedIn(int index, WiredList<? super E> other) {
    Check.notNull(other, LIST).then(list -> list.embed(index, this));
    return this;
  }

  /**
   * Removes a segment from the specified list and embeds it in this list. The provided list will be
   * empty afterwards. The segment must contain at least one element.
   *
   * @param index The index at which to insert segment
   * @param other The list to remove the segment from
   * @param fromIndex The start index of the segment (inclusive)
   * @param toIndex The end index of the segment (exclusive)
   * @return This {@code WiredList}
   */
  public WiredList<E> transfer(int index,
      WiredList<? extends E> other,
      int fromIndex,
      int toIndex) {
    checkIndexInclusive(index);
    Check.notNull(other, LIST).isNot(sameAs(), this, ERR_AUTO_EMBED);
    Check.on(indexOutOfBounds(), fromIndex, "fromIndex")
        .is(gte(), 0)
        .is(lt(), toIndex, ERR_EMPTY_SEGMENT);
    Check.on(indexOutOfBounds(), toIndex, "toIndex").is(lte(), other.sz);
    Node first = other.nodeAt(fromIndex);
    Node last = other.nodeAfter(first, fromIndex, toIndex - 1);
    Chain chain = new Chain(first, last, toIndex - fromIndex);
    other.delete(chain);
    insert(index, chain);
    return this;
  }

  /**
   * Removes a segment from this list and embeds it in the specified list. This list will be empty
   * afterwards.
   *
   * @param fromIndex The start index of the segment (inclusive)
   * @param toIndex The end index of the segment (exclusive)
   * @param other The list into which to insert the segment
   * @param index The index at which to insert segment
   * @return This {@code WiredList}
   */
  public WiredList<E> transferTo(int fromIndex,
      int toIndex,
      WiredList<? super E> other,
      int index) {
    Check.notNull(other, LIST).then(list -> list.transfer(index, this, fromIndex, toIndex));
    return this;
  }

  /**
   * Trims off all elements from the start of the list that satisfy the specified condition. The
   * returned list will contain all elements preceding the first element that does not satisfy the
   * condition. If the condition is never satisfied, an empty list is returned and this list remains
   * unchanged.
   *
   * @param condition The test that the elements must pass in order to be trimmed off
   * @return A {@code WiredList} containing all elements preceding the first element that does not
   *     satisfy the condition
   */
  public WiredList<E> ltrim(Predicate<? super E> condition) {
    Check.notNull(condition, "condition");
    if (sz == 0) {
      return this;
    }
    Node<E> first = head;
    Node<E> last = justBeforeHead();
    int len = 0;
    for (; condition.test(last.next.val) && ++len != sz; last = last.next) ;
    if (len == sz) {
      return new WiredList<>();
    }
    WiredList<E> wl = new WiredList<>(first, last, len);
    delete(new Chain(first, last, len));
    return wl;
  }

  /**
   * Trims off all elements from the end of the list that satisfy the specified condition. The
   * returned list will contain all elements after the last element that does not satisfy the
   * condition. If the condition is never satisfied, an empty list is returned and this list remains
   * unchanged.
   *
   * @param condition The test that the elements must pass in order to be trimmed off
   * @return A {@code WiredList} containing all elements after the last element that does not
   *     satisfy the condition
   */
  public WiredList<E> rtrim(Predicate<? super E> condition) {
    Check.notNull(condition, "condition");
    if (sz == 0) {
      return this;
    }
    Node<E> last = tail;
    Node<E> first = justAfterTail();
    int len = 0;
    for (; condition.test(first.prev.val) && ++len != sz; first = first.prev) ;
    if (len == sz) {
      return new WiredList<>();
    }
    WiredList<E> wl = new WiredList<>(first, last, len);
    delete(new Chain(first, last, len));
    return wl;
  }

  /**
   * Reverses the order of the elements in this {@code WiredList}.
   *
   * @return This {@code WiredList}
   */
  public WiredList<E> reverse() {
    if (sz > 1) {
      Node<E> node = head;
      Node<E> temp = node.next;
      do {
        node.next = node.prev;
        node.prev = temp;
        if (node == tail) {
          break;
        }
        node = temp;
        temp = temp.next;
      } while (true);
      temp = head;
      head = tail;
      tail = temp;
    }
    return this;
  }

  /**
   * Moves a list segment forward or backward in the list. It is not allowed to specify a
   * zero-length segment ({@code fromIndex == toIndex}). The {@code newFromIndex} is allowed to be
   * equal to {@code fromIndex}, causing the segment to stay where it is.
   *
   * @param fromIndex The start index of the segment (inclusive)
   * @param toIndex The end index of the segment (exclusive)
   * @param newFromIndex The desired start index of the segment. To move the segment to the very
   *     start of the list, specify 0 (zero). To move the segment to the very end of the list
   *     specify the {@link #size() size} of the list
   */
  public void move(int fromIndex, int toIndex, int newFromIndex) {
    Check.on(indexOutOfBounds(), fromIndex, "fromIndex").is(gte(), 0);
    Check.on(indexOutOfBounds(), toIndex, "toIndex")
        .is(lte(), sz)
        .is(gt(), fromIndex, ERR_EMPTY_SEGMENT);
    Check.on(indexOutOfBounds(), newFromIndex, "newFromIndex").is(gte(), 0).is(lte(), sz);
    if (newFromIndex > fromIndex) {
      moveToTail(fromIndex, toIndex, newFromIndex);
    } else if (newFromIndex < fromIndex) {
      moveToHead(fromIndex, toIndex, newFromIndex);
    }
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
  public E remove(int index) {
    Check.on(indexOutOfBounds(), index, INDEX).is(listIndexOf(), this);
    return delete0(index, index + 1).head.val;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean remove(Object o) {
    Iterator<E> itr = iterator();
    while (itr.hasNext()) {
      if (Objects.equals(o, itr.next())) {
        itr.remove();
        return true;
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
    WiredIterator<E> itr = wiredIterator();
    while (itr.hasNext()) {
      if (test.test(itr.next())) {
        itr.remove();
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public boolean removeAll(Collection<?> c) {
    Check.notNull(c);
    int sz = this.sz;
    removeIf0(0, sz, in(), (Collection) c);
    return sz != this.sz;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public boolean retainAll(Collection<?> c) {
    Check.notNull(c);
    int sz = this.sz;
    removeIf0(0, sz, (Relation) in().negate(), (Collection) c);
    return sz != this.sz;
  }

  /**
   * Removes the elements between the {@code fromIndex} (inclusive) and {@code toIndex}
   * (exclusive).
   *
   * @param fromIndex The left boundary (inclusive) of the segment to delete
   * @param toIndex The right boundary (exclusive) of the segment to delete
   * @return The deleted segment
   */
  public WiredList<E> cut(int fromIndex, int toIndex) {
    Check.on(indexOutOfBounds(), fromIndex, "fromIndex").is(gte(), 0);
    Check.on(indexOutOfBounds(), toIndex, "toIndex")
        .is(lte(), sz)
        .is(gt(), fromIndex, ERR_EMPTY_SEGMENT);
    return delete0(fromIndex, toIndex);
  }

  private <O> boolean removeIf0(int fromIndex, int length, Relation<E, O> test, O testValue) {
    if (length > 0) {
      int sz = this.sz;
      Node<E> node = nodeAt(fromIndex);
      for (int i = 0; i < length; ++i) {
        if (test.exists(node.val, testValue)) {
          deleteNode(node);
        } else {
          node = node.next;
        }
      }
      return sz != this.sz;
    }
    return false;
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
    return indexOf(o) == -1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsAll(Collection<?> c) {
    return new HashSet<>(this).containsAll(Check.notNull(c).ok());
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
  public boolean add(E e) {
    push(e);
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void add(int index, E element) {
    insert(index, element);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean addAll(Collection<? extends E> c) {
    insertAll(sz, c);
    return !c.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    insertAll(index, c);
    return !c.isEmpty();
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
   * {@inheritDoc}
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
        if (curr != tail) {
          return (curr = curr.next).val;
        }
        throw new NoSuchElementException();
      }
    };
  }

  /**
   * Returns an {@code Iterator} that traverses the list's elements from last to first.
   *
   * @return An {@code Iterator} that traverses the list's elements from last to first
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
        if (curr != head) {
          return (curr = curr.next).val;
        }
        throw new NoSuchElementException();
      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o instanceof List l) {
      Iterator<?> itr = l.iterator();
      var node = head;
      while (itr.hasNext()) {
        if (!Objects.equals(node.val, itr.next())) {
          return false;
        } else if (node == tail) {
          break;
        }
        node = node.next;
      }
      return node == tail && !itr.hasNext();
    }
    return false;
  }

  /**
   * Returns a {@link WiredIterator} that traverses the list from the first element to the last
   *
   * @return
   */
  public WiredIterator<E> wiredIterator() {
    return new ForwardWiredIterator();
  }

  public WiredIterator<E> wiredIterator(boolean reverse) {
    return reverse ? new ReverseWiredIterator() : new ForwardWiredIterator();
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
  public ListIterator<E> listIterator() {
    throw new UnsupportedOperationException();
  }

  /**
   * <b>Not supported by this implementation.</b>
   */
  @Override
  public ListIterator<E> listIterator(int index) {
    throw new UnsupportedOperationException();
  }

  /**
   * <b>Not supported by this implementation.</b>
   */
  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException();
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
      var node = checkIndexInclusive(index).ok(this::nodeAt);
      join(node.prev, chain.head);
      if (chain.length == 1) {
        join(chain.head, node);
      } else {
        join(chain.tail, node);
      }
    }
    sz += chain.length;
  }

  private WiredList<E> delete0(int from, int to) {
    var first = nodeAt(from);
    var last = nodeAfter(first, from, to - 1);
    int len = to - from;
    delete(new Chain(first, last, len));
    return new WiredList<>(first, last, len);
  }

  private void deleteNode(Node<E> node) {
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

  // NB this method does not touch the chain
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

  @VisibleForTesting
  Node<E> nodeAt(int index) {
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
  @VisibleForTesting
  Node<E> nodeAfter(Node<E> startNode, int startIndex, int index) {
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

  @VisibleForTesting
  Node<E> nodeBefore(Node<E> startNode, int startIndex, int index) {
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

  private IntCheck<IndexOutOfBoundsException> checkIndex(int index) {
    return Check.on(indexOutOfBounds(), index, INDEX).is(listIndexOf(), this);
  }

  private IntCheck<IndexOutOfBoundsException> checkIndexInclusive(int index) {
    return Check.on(indexOutOfBounds(), index, INDEX).is(gte(), 0).is(lte(), sz);
  }

}
