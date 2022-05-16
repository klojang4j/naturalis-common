package nl.naturalis.common.collection;

import nl.naturalis.common.CollectionMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.check.CommonChecks;
import nl.naturalis.common.check.IntCheck;
import nl.naturalis.common.function.Relation;
import nl.naturalis.common.x.invoke.InvokeUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Collections.emptyIterator;
import static java.util.Collections.emptyListIterator;
import static nl.naturalis.common.ArrayMethods.EMPTY_OBJECT_ARRAY;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * A doubly-linked list, much like {@link LinkedList}, but more focused on list manipulation, and
 * less on queue-like behaviour. As with any doubly-linked list, it is relatively inefficient at
 * index-based retrieval, but very efficient at inserting, deleting and moving around large chunks
 * of list elements (the larger the chunks the bigger the gain, compared to {@link ArrayList}).
 *
 * @param <E> The type of the elements in the list
 */
public class WiredList<E> implements List<E> {

  // ======================================================= //
  // ======================= [ Node ] ====================== //
  // ======================================================= //

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

    public String toString() {
      return String.valueOf(val);
    }

  }

  // ======================================================= //
  // ====================== [ Chain ] ====================== //
  // ======================================================= //

  @SuppressWarnings("unchecked,rawtypes")
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

  final class WiredIteratorImpl implements WiredIterator<E> {

    private Node<E> curr = justBeforeHead();

    @Override
    public boolean hasNext() {
      return sz != 0 && curr != tail;
    }

    @Override
    public E next() {
      // We don't protect ourselves against concurrent modifications,
      // but we must still check do a size check in case the same
      // thread has emptied the list from outside the iterator
      Check.that(sz).is(ne(), 0, NO_SUCH_ELEMENT);
      Check.that(curr).isNot(sameAs(), tail, NO_SUCH_ELEMENT);
      return (curr = curr.next).val;
    }

    @Override
    public void set(E newVal) {
      curr.val = newVal;
    }

    @Override
    public void remove() {
      Node<E> tmp = curr;
      Check.that(sz).is(ne(), 0, NO_SUCH_ELEMENT);
      if (tmp != tail) {
        curr = curr.next;
      }
      deleteNode(tmp);
    }

  }

  // ======================================================= //
  // ===================== [ ListItr ]  ==================== //
  // ======================================================= //

  private class ListItr implements ListIterator<E> {

    int idx;
    Node<E> curr;

    ListItr() {
      this(0);
    }

    ListItr(int index) {
      this.idx = index;
    }

    @Override
    public boolean hasNext() {
      return idx != sz || curr == null;
    }

    @Override
    public E next() {
      if (curr == null) {
        return (curr = node(idx)).val;
      }
      Check.that(idx).is(ne(), sz, NO_SUCH_ELEMENT);
      ++idx;
      return (curr = curr.next).val;
    }

    @Override
    public boolean hasPrevious() {
      return idx != 0 || curr == null;
    }

    @Override
    public E previous() {
      if (curr == null) {
        return (curr = node(idx)).val;
      }
      Check.that(idx).is(ne(), 0, NO_SUCH_ELEMENT);
      --idx;
      return (curr = curr.prev).val;
    }

    @Override
    public int nextIndex() {
      return idx + 1;
    }

    @Override
    public int previousIndex() {
      return idx - 1;
    }

    @Override
    public void remove() {
      Node<E> tmp = curr;
      Check.that(sz).is(ne(), 0, NO_SUCH_ELEMENT);
      if (tmp != tail) {
        curr = curr.next;
      }
      deleteNode(tmp);
    }

    @Override
    public void set(E e) {
      Check.on(illegalState(), curr).isNot(NULL(), "previous/next not called yet", null);
      curr.val = e;
    }

    @Override
    public void add(E e) {
      append(e);
    }

  }

  // ======================================================= //
  // ==================== [ WiredList ] ==================== //
  // ======================================================= //

  // Ubiquitous parameter names within this class
  private static final String INDEX = "index";
  private static final String VALUES = "values";
  private static final String TEST = "test";

  // Error messages
  private static final Supplier<IllegalArgumentException> ERR_AUTO_EMBED =
      () -> new IllegalArgumentException("list cannot be embedded within itself");

  private static final Supplier<IndexOutOfBoundsException> MOVE_BEYOND_BOUNDS =
      () -> new IndexOutOfBoundsException("cannot move segment beyond list boundary");

  private static final Supplier<NoSuchElementException> NO_SUCH_ELEMENT =
      NoSuchElementException::new;

  /**
   * Returns a new, empty {@code WiredList}.
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
    wl.append(e);
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
    wl.append(e0);
    wl.append(e1);
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
    wl.append(e0);
    wl.append(e1);
    wl.append(e2);
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

  @Override
  public E get(int index) {
    return checkIndex(index).ok(this::node).val;
  }

  @Override
  public E set(int index, E value) {
    var node = checkIndex(index).ok(this::node);
    E old = node.val;
    node.val = value;
    return old;
  }

  /**
   * Sets the element at the specified index to the specified value <i>if</i> the original value
   * passes the specified test. This method mitigates the relatively large cost of index-based
   * retrieval with linked lists, which would double if you had to execute a get-test-set sequence.
   * See also {@link CommonChecks}.
   *
   * @param index The index of the element to set
   * @param test The test that the original value has to pass in order to be replaced with the
   *     new value.
   * @param value The value to set
   * @return The original value
   */
  public E setIf(int index, Predicate<? super E> test, E value) {
    Check.notNull(test, TEST);
    var node = checkIndex(index).ok(this::node);
    E old = node.val;
    if (test.test(old)) {
      node.val = value;
    }
    return old;
  }

  /**
   * Sets the element at the specified index to the specified value <i>if</i> the original value has
   * the specified relation to that value. This method mitigates the relatively large cost of
   * index-based retrieval with linked lists, which would double if you had to execute a
   * get-test-set sequence. See also {@link CommonChecks}.
   *
   * @param index The index of the element to set
   * @param test The test that the original value has to pass in order to be replaced with the
   *     new value.
   * @param value The value to set
   * @return The original value
   */
  public E setIf(int index, Relation<E, E> test, E value) {
    Check.notNull(test, TEST);
    var node = checkIndex(index).ok(this::node);
    E old = node.val;
    if (test.exists(old, value)) {
      node.val = value;
    }
    return old;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int indexOf(Object o) {
    int i = 0;
    for (E val : this) {
      if (Objects.equals(o, val)) {
        return i;
      }
      ++i;
    }
    return -1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int lastIndexOf(Object o) {
    Iterator<E> itr = reverseIterator();
    int i = sz - 1;
    while (itr.hasNext()) {
      if (Objects.equals(o, itr.next())) {
        return i;
      }
      --i;
    }
    return -1;
  }

  /**
   * Appends the specified value to the end of the list. A.k.a. push.
   *
   * @param value The value to insert
   */
  public void append(E value) {
    insert(sz, value);
  }

  /**
   * Removes the last element from the list. A.k.a. pop.
   *
   * @return The value of the removed element
   */
  public E removeLast() {
    return delete0(sz, 1).head.val;
  }

  /**
   * Removes the first element from the list, left-shifting the remaining elements. A.k.a. shift.
   *
   * @return The value of the removed element
   */
  public E removeFirst() {
    return delete0(0, 1).head.val;
  }

  /**
   * Inserts the specified value at the start of the list, right-shifting the original elements.
   * A.k.a. unshift.
   *
   * @param value The value to insert
   */
  public void prepend(E value) {
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
   * Inserts the specified value at the specified location, right-shifting the elements following
   * it.
   *
   * @param value The value to insert
   */
  public void insert(int index, E value) {
    insert(index, Chain.ofSingle(value));
  }

  /**
   * Inserts the specified values at the specified location, right-shifting the elements following
   * it.
   *
   * @param values The values to insert
   */
  public void insertAll(int index, Collection<? extends E> values) {
    Check.notNull(values, VALUES);
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
    checkInclusive(index);
    Check.notNull(other, "list").isNot(sameAs(), this, ERR_AUTO_EMBED);
    if (!other.isEmpty()) {
      insert(index, new Chain(other.head, other.tail, other.sz));
      other.clear();
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
    Check.notNull(other, "list").then(list -> list.embed(index, this));
    return this;
  }

  /**
   * Removes a segment from the specified list and embeds it in this list.
   *
   * @param index The index at which to embed in this list
   * @param other The list to remove the segment from
   * @param fromIndex The start index of the segment (inclusive)
   * @param toIndex The end index of the segment (exclusive)
   */
  public void transfer(int index, WiredList<? extends E> other, int fromIndex, int toIndex) {
    checkInclusive(index);
    Check.notNull(other, "list").isNot(sameAs(), this, ERR_AUTO_EMBED);
    int length = Check.fromTo(other, fromIndex, toIndex);
    transfer0(index, other, fromIndex, length);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void transfer0(int index, WiredList<? extends E> other, int offset, int length) {
    if (length > 0) {
      Node first = other.node(offset);
      Node last = other.node(first, offset, length);
      other.delete(new Chain(first, last, length));
      insert(index, new Chain(first, last, length));
    }
  }

  /**
   * Exchanges list segments between this list and the specified list.
   *
   * @param myFrom The start index of the segment in this list (inclusive)
   * @param myTo The end index of the segment in this list (exclusive)
   * @param other The list with which to exchange the segments
   * @param itsFrom The start index of the segment in the other list (inclusive)
   * @param itsTo The end index of the segment in the other list (exclusive)
   */
  public void exchange(int myFrom, int myTo, WiredList<E> other, int itsFrom, int itsTo) {
    int myLen = Check.fromTo(this, myFrom, myTo);
    int itsLen = Check.fromTo(other, itsFrom, itsTo);
    if (myLen == 0) {
      if (itsLen == 0) {
        return;
      }
      transfer0(myFrom, other, itsFrom, itsLen);
    } else if (itsLen == 0) {
      other.transfer0(itsFrom, this, myFrom, myLen);
    } else {
      Node<E> myStart = node(myFrom);
      Node<E> myEnd = node(myStart, myFrom, myLen);
      Node<E> itsStart = other.node(itsFrom);
      Node<E> itsEnd = other.node(itsStart, itsFrom, itsLen);
      Node<E> tmp = myStart.prev;
      myStart.prev = itsStart.prev;
      itsStart.prev = tmp;
      tmp = myStart.next;
      myStart.next = itsStart.next;
      itsStart.next = tmp;
      tmp = myEnd.prev;
      myEnd.prev = itsEnd.prev;
      itsEnd.prev = tmp;
      tmp = myEnd.next;
      myEnd.next = itsEnd.next;
      itsEnd.next = tmp;
      if ((sz += itsLen - myLen) == 0) {
        head = tail = null;
      }
      if ((other.sz += myLen - itsLen) == 0) {
        other.head = other.tail = null;
      }
    }
  }

  /**
   * Chops off a segment consisting of all elements up to (but excluding) the first element that
   * satisfies the specified condition. In other words, none of the elements in the returned list
   * satisfy the condition and you are left with a list whose first element does satisy the
   * condition. If the condition was never satisfied, the list remains unaltered and will itself be
   * returned.
   *
   * @param condition The condition that the elements in the returned list must <i>not</i>
   *     satisfy
   * @return A {@code WiredList} containing all elements of this instance up to (but excluding) the
   *     first element that satisfies the specified condition
   */
  public WiredList<E> lchop(Predicate<? super E> condition) {
    return ltrim(condition, false);
  }

  /**
   * Chops off a segment consisting of all elements up to (but excluding) the first or last element
   * that satisfies the specified condition. In other words, none of the elements in the returned
   * list satisfy the condition and you are left with a list whose first element does satisy the
   * condition. If the condition was never satisfied, the list remains unaltered and will itself be
   * returned.
   *
   * @param condition The condition that the elements in the returned list must <i>not</i>
   *     satisfy
   * @param lastOccurrence Whether to split on the first or the last element satisfying the
   *     condition
   * @return A {@code WiredList} containing all elements of this instance up to (but excluding) the
   *     first element that satisfies the specified condition
   */
  public WiredList<E> ltrim(Predicate<? super E> condition, boolean lastOccurrence) {
    Check.notNull(condition, "condition");
    if (sz == 0) {
      return this;
    }
    Node<E> first = head;
    Node<E> last = justBeforeHead();
    int len = 0;
    for (; !condition.test(last.next.val) && ++len != sz; last = last.next) ;
    if (len == sz) {
      return this;
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
   * Moves a list segment forward or backward in the list. Use a negative value for the {@code
   * positions} argument to make the segment move towards the start of the list.
   *
   * @param fromIndex The start index of the segment (inclusive)
   * @param toIndex The end index of the segment (exclusive)
   * @param positions The number of positions to move the segment forward (positive) or
   *     backwards (negative)
   */
  public void move(int fromIndex, int toIndex, int positions) {
    int len = Check.fromTo(sz, fromIndex, toIndex);
    if ((len | positions) == 0) {
      // ...
    } else if (positions > 0) {
      Check.that(len + positions).is(lte(), sz, MOVE_BEYOND_BOUNDS);
      moveToTail(fromIndex, len, positions);
    } else {
      Check.that(fromIndex + positions).is(gte(), 0, MOVE_BEYOND_BOUNDS);
      moveToHead(fromIndex, len, positions);
    }
  }

  private void moveToTail(int off, int len, int pos) {
    // pos is the number of steps we must make, but we
    // need a to-index *exclusive*:
    pos += 1;
    Node<E> oldFirst = node(off);
    Node<E> oldLast = node(oldFirst, off, len);
    Node<E> newLast = node(oldLast, off + len - 1, pos);
    if (off == 0) { // oldFirst == head
      makeHead(oldLast.next);
    } else {
      join(oldFirst.prev, oldLast.next);
    }
    if (newLast == tail) {
      join(newLast, oldFirst);
      makeTail(oldLast);
    } else {
      join(oldLast, newLast.next);
      join(newLast, oldFirst);
    }
  }

  private void moveToHead(int off, int len, int pos) {
    int newFrom = off + pos;
    pos = 1 - pos;
    Node<E> newFirst = node(newFrom);
    Node<E> newLast = node(newFirst, newFrom, len);
    Node<E> oldFirst = (pos == len) ? newLast : node(newFirst, newFrom, pos);
    Node<E> oldLast = node(oldFirst, off, len);
    if (oldLast == tail) {
      makeTail(oldFirst.prev);
    } else {
      join(oldFirst.prev, oldLast.next);
    }
    if (newFrom == 0) {
      join(oldLast, newFirst);
      makeHead(oldFirst);
    } else {
      join(newFirst.prev, oldFirst);
      join(oldLast, newFirst);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public E remove(int index) {
    checkIndex(index);
    return delete0(index, 1).head.val;
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
  @SuppressWarnings({"rawtypes", "unchecked"})
  public boolean removeAll(Collection<?> c) {
    Check.notNull(c, "collection");
    int sz = this.sz;
    removeIf(0, sz, in(), (Collection) c);
    return sz != this.sz;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public boolean retainAll(Collection<?> c) {
    Check.notNull(c, "collection");
    int sz = this.sz;
    removeIf(0, sz, (Relation) in().negate(), (Collection) c);
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
  public WiredList<E> remove(int fromIndex, int toIndex) {
    int length = Check.fromTo(sz, fromIndex, toIndex);
    return delete0(fromIndex, length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean removeIf(Predicate<? super E> test) {
    Check.notNull(test, TEST);
    return removeIf0(0, sz, test);
  }

  /**
   * Removes all elements of the list that have the specified relation to the test value. Note that
   * the {@link CommonChecks} class contains some common relationships. For example, to remove all
   * dates before 1900-01-01 from a list of dates:
   *
   * <blockquote>
   * <pre>{@code
   * WireList<LocalDate> wl = ....
   * wl.removeIf(LT(), LocalDate.of(1900, 1, 1));
   * }</pre>
   * </blockquote>
   *
   * @param test The relation
   * @param testValue The value to test the list elements against
   * @param <O> The type of the test value
   * @return {@code true} if any elements were removed
   */
  public <O> boolean removeIf(Relation<E, O> test, O testValue) {
    Check.notNull(test, TEST);
    return removeIf0(0, sz, test, testValue);
  }

  /**
   * Removes all elements between {@code fromIndex} (inclusive) and {@code toIndex} (exclusive) that
   * satisfy the given predicate. Note that the {@link CommonChecks} class contains some common
   * predicates. For example, to remove all non-readable files from a list of files:
   *
   * <blockquote>
   * <pre>{@code
   * WireList<File> wl = ....
   * wl.removeIf(readable().negate());
   * }</pre>
   * </blockquote>
   *
   * @param fromIndex The start of the list segment to consider
   * @param toIndex The end of the list segment to consider
   * @param test A predicate which returns true for elements to be removed
   * @return {@code true} if any elements were removed
   */
  public boolean removeIf(int fromIndex, int toIndex, Predicate<? super E> test) {
    Check.notNull(test, TEST);
    int length = Check.fromTo(sz, fromIndex, toIndex);
    return removeIf0(fromIndex, length, test);
  }

  /**
   * Removes all elements between {@code fromIndex} (inclusive) and {@code toIndex} (exclusive) that
   * have the specified relation to the specified value. Note that the {@link CommonChecks} class
   * contains some common relationships.
   *
   * @param fromIndex The start of the list segment to consider
   * @param toIndex The end of the list segment to consider
   * @param test The relation
   * @param testValue The value to test the elements within the segment against
   * @param <O> The type of the object of the {@code Relation}
   * @return {@code true} if any elements were removed
   */
  public <O> boolean removeIf(int fromIndex, int toIndex, Relation<E, O> test, O testValue) {
    Check.notNull(test, TEST);
    int length = Check.fromTo(sz, fromIndex, toIndex);
    return removeIf0(fromIndex, length, test, testValue);
  }

  private boolean removeIf0(int fromIndex, int length, Predicate<? super E> test) {
    if (length > 0) {
      int sz = this.sz;
      Node<E> node = node(fromIndex);
      for (int i = 0; i < length; ++i) {
        if (test.test(node.val)) {
          deleteNode(node);
        } else {
          node = node.next;
        }
      }
      return sz != this.sz;
    }
    return false;
  }

  private <O> boolean removeIf0(int fromIndex, int length, Relation<E, O> test, O testValue) {
    if (length > 0) {
      int sz = this.sz;
      Node<E> node = node(fromIndex);
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
    Check.notNull(c, "collection");
    return new HashSet<>(this).containsAll(c);
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
    Check.notNull(a, "array");
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
    append(e);
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
  public ListIterator<E> listIterator() {
    return sz == 0 ? emptyListIterator() : new ListItr();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ListIterator<E> listIterator(int index) {
    return checkIndex(index).ok(ListItr::new);
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

  public WiredIterator<E> wiredIterator() {
    return new WiredIteratorImpl();
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
      var node = checkInclusive(index).ok(this::node);
      join(node.prev, chain.head);
      if (chain.length == 1) {
        join(chain.head, node);
      } else {
        join(chain.tail, node);
      }
    }
    sz += chain.length;
  }

  private WiredList<E> delete0(int off, int len) {
    if (len == 0) {
      return new WiredList<>();
    }
    var first = node(off);
    var last = node(first, off, len);
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

  private Node<E> node(int index) {
    if (index == 0) {
      return head;
    } else if (index == sz) {
      return tail;
    } else if (index < (sz >> 1)) {
      Node<E> n = head.next;
      for (int i = 1; i < index; ++i) {
        n = n.next;
      }
      return n;
    } else {
      Node<E> n = tail;
      for (int i = sz - 1; i > index; --i) {
        n = n.prev;
      }
      return n;
    }
  }

  // Will only be called if len > 0
  private Node<E> node(Node<E> startNode, int startIndex, int len) {
    Node<E> node;
    if (len < ((sz - startIndex) >> 1)) {
      node = startNode;
      for (int i = 1; i < len; ++i) {
        node = node.next;
      }
    } else {
      node = tail;
      for (int i = sz; i > startIndex + len; --i) {
        node = node.prev;
      }
    }
    return node;
  }

  private void makeHead(Node<E> node) {
    node.prev = null;
    head = node;
  }

  private void makeTail(Node<E> node) {
    node.next = null;
    tail = node;
  }

  @SuppressWarnings("rawtypes")
  private static void join(Node prev, Node next) {
    prev.next = next;
    next.prev = prev;
  }

  private Iterator<Node<E>> nodeIterator() {
    return sz == 0 ? emptyIterator() : new Iterator<>() {

      Node<E> node = justBeforeHead();

      @Override
      public boolean hasNext() {
        return node != tail;
      }

      @Override
      public Node<E> next() {
        return node = node.next;
      }
    };
  }

  private Node<E> justBeforeHead() {
    Node<E> n = new Node<>(null);
    n.next = head;
    return n;
  }

  private Node<E> justAfterTail() {
    Node<E> n = new Node<>(null);
    n.prev = tail;
    return n;
  }

  private IntCheck<IndexOutOfBoundsException> checkIndex(int index) {
    return Check.on(indexOutOfBounds(), index, INDEX).is(listIndexOf(), this);
  }

  private IntCheck<IndexOutOfBoundsException> checkInclusive(int index) {
    return Check.on(indexOutOfBounds(), index, INDEX).is(gte(), 0).is(lte(), sz);
  }

}
