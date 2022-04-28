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
 * index-based searches, but it can be very efficient and deleting and moving around relatively
 * large chunks of list elements (the larger the chunks the bigger the gain, compared to, for
 * example, {@link ArrayList}). This {@code List} implementation does <i>not</i> support the {@link
 * List#subList(int, int) subList} method. Its specification requires an administration that would
 * almost completely undo again the efficiency of the operations that linked lists are supposed to
 * be good at.
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
      return curr == null || curr != tail;
    }

    @Override
    public E next() {
      if (curr == null) {
        return (curr = node(idx)).val;
      }
      Check.that(curr).isNot(sameAs(), tail, NO_SUCH_ELEMENT);
      return (curr = curr.next).val;
    }

    @Override
    public boolean hasPrevious() {
      return curr == null || curr != head;
    }

    @Override
    public E previous() {
      if (curr == null) {
        return (curr = node(idx)).val;
      }
      Check.that(curr).isNot(sameAs(), head, NO_SUCH_ELEMENT);
      return (curr = curr.prev).val;
    }

    /**
     * <b>Not supported by this implementation of {@code ListIterator}.</b>
     */
    @Override
    public int nextIndex() {
      throw new UnsupportedOperationException();
    }

    /**
     * <b>Not supported by this implementation of {@code ListIterator}.</b>
     */
    @Override
    public int previousIndex() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {

    }

    @Override
    public void set(E e) {
      Check.on(illegalState(), curr).isNot(NULL(), "previous/next not called yet", null);
      curr.val = e;
    }

    @Override
    public void add(E e) {
      push(e);
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
  private static final String ERR_AUTO_EMBED = "list cannot be embedded within itself";

  private static final Supplier<NoSuchElementException> NO_SUCH_ELEMENT =
      NoSuchElementException::new;

  public static <E> WiredList<E> of(E e) {
    WiredList<E> wl = new WiredList<>();
    wl.push(e);
    return wl;
  }

  public static <E> WiredList<E> of(E e0, E e1) {
    WiredList<E> wl = new WiredList<>();
    wl.push(e0);
    wl.push(e1);
    return wl;
  }

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

  public WiredList() {}

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
    Node<E> n = new Node<>(null);
    n.next = head;
    for (int i = 0; i < sz; ++i) {
      if (Objects.equals(o, (n = n.next).val)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int lastIndexOf(Object o) {
    Node<E> n = new Node<>(null);
    n.prev = tail;
    for (int i = sz - 1; i != 0; --i) {
      if (Objects.equals(o, (n = n.prev).val)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Inserts the specified value at the end of the list.
   *
   * @param value The value to insert
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
    return delete0(sz, 1).head.val;
  }

  /**
   * Removes the first element from the list.
   *
   * @return The value of the removed element
   */
  public E shift() {
    return delete0(0, 1).head.val;
  }

  /**
   * Inserts the specified value at the start of the list.
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
  public void prepend(Collection<? extends E> values) {
    insertAll(0, values);
  }

  /**
   * Inserts the specified values at the end of the list.
   *
   * @param values The values to insert
   */
  public void append(Collection<? extends E> values) {
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
   * Embeds the specified list in this list. This method is very efficient, but <b>the provided list
   * will empty afterwards</b>. If you don't want this to happen, use {@link #insertAll(int,
   * Collection) insertAll}.
   *
   * @param index The index at which to embed the list
   * @param other The list to embed
   */
  public void embed(int index, WiredList<? extends E> other) {
    checkInclusive(index);
    Check.notNull(other, "list");
    if (!other.isEmpty()) {
      insert(index, new Chain(other.head, other.tail, other.sz));
      other.clear();
    }
  }

  /**
   * Removes a segment from the specified list and embeds it in this list.
   *
   * @param index The index at which to embed the list
   * @param other The list to remove the segment from
   * @param fromIndex The start index of the segment (inclusive)
   * @param toIndex The end index of the segment (exclusive)
   */
  public void transfer(int index, WiredList<? extends E> other, int fromIndex, int toIndex) {
    checkInclusive(index);
    int length = Check.fromTo(other, fromIndex, toIndex);
    transfer0(index, other, fromIndex, length);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void transfer0(int index, WiredList<? extends E> other, int offset, int length) {
    Check.that(other, "list").isNot(sameAs(), this, ERR_AUTO_EMBED, null);
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
   * Moves a list segment forward or backward in the list. Use a negative value for the {@code
   * positions} argument to make the segment move towards the start of the list.
   *
   * @param fromIndex The start index of the segment (inclusive)
   * @param toIndex The end index of the segment (exclusive)
   * @param positions The number of positions to move the segment forward (positive) or
   *     backwards (negative)
   */
  public void move(int fromIndex, int toIndex, int positions) {
    moveForward(fromIndex, toIndex, positions);
  }

  private void moveForward(int from, int to, int pos) {
    int len = Check.fromTo(sz, from, to);
    Node<E> oldFirst = node(from);
    Node<E> oldLast = node(oldFirst, from, len);
    Node<E> newFirst = (len == pos + 1) ? oldLast : node(oldFirst, from, pos + 1);
    Node<E> newLast = node(oldLast, to - 1, pos + 1);
    if (from == 0) { // oldFirst == head
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

  /**
   * Removes an element from the list, left-shifting the elements following it.
   *
   * @param index The index of the element to remove
   * @return The value of the removed element
   */
  public E remove(int index) {
    checkIndex(index);
    return delete0(index, 1).head.val;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean remove(Object o) {
    for (Node<E> n = head; ; n = n.next) {
      if (Objects.equals(o, n.val)) {
        deleteNode(n);
        return true;
      } else if (n == tail) {
        return false;
      }
    }
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
   * Removes a segment with the specified length, starting at the specified offset.
   *
   * @param offset The offset from the start of the {@code WiredList}
   * @param length The length of the segment
   * @return The deleted segment
   */
  public WiredList<E> removeSegment(int offset, int length) {
    Check.offsetLength(sz, offset, length);
    return delete0(offset, length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean removeIf(Predicate<? super E> test) {
    Check.notNull(test, TEST);
    return deleteIf0(0, sz, test);
  }

  /**
   * Removes all elements of the list that have the specified relation to the test value.
   *
   * @param test The relation
   * @param testValue The value to test the list elements against
   * @param <O> The type of the test value
   * @return {@code true} if any elements were removed
   */
  public <O> boolean removeIf(Relation<E, O> test, O testValue) {
    Check.notNull(test, TEST);
    return deleteIf0(0, sz, test, testValue);
  }

  /**
   * Removes all elements between {@code fromIndex} (inclusive) and {@code toIndex} (exclusive) that
   * satisfy the given predicate.
   *
   * @param test A predicate which returns true for elements to be removed
   * @param fromIndex The start of the list segment to consider
   * @param toIndex The end of the list segment to consider
   * @return {@code true} if any elements were removed
   */
  public boolean removeIf(Predicate<? super E> test, int fromIndex, int toIndex) {
    Check.notNull(test, TEST);
    int length = Check.fromTo(sz, fromIndex, toIndex);
    return deleteIf0(fromIndex, length, test);
  }

  public <O> boolean removeIf(int fromIndex, int toIndex, Relation<E, O> test, O testValue) {
    Check.notNull(test, TEST);
    int length = Check.fromTo(sz, fromIndex, toIndex);
    return deleteIf0(fromIndex, length, test, testValue);
  }

  private boolean deleteIf0(int fromIndex, int length, Predicate<? super E> test) {
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

  private <O> boolean deleteIf0(int fromIndex, int length, Relation<E, O> test, O testValue) {
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

  @Override
  public int size() {
    return sz;
  }

  @Override
  public boolean isEmpty() {
    return sz == 0;
  }

  @Override
  public boolean contains(Object o) {
    for (Node<E> n = head; ; n = n.next) {
      if (Objects.equals(o, n.val)) {
        return true;
      } else if (n == tail) {
        return false;
      }
    }
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    Check.notNull(c, "collection");
    return new HashSet<>(this).containsAll(c);
  }

  @Override
  public Object[] toArray() {
    if (sz == 0) {
      return EMPTY_OBJECT_ARRAY;
    }
    Object[] result = new Object[sz];
    int i = 0;
    for (Node<E> n = head; ; n = n.next) {
      result[i++] = n.val;
      if (n == tail) {
        break;
      }
    }
    return result;
  }

  @Override
  public <T> T[] toArray(T[] a) {
    Check.notNull(a, "array");
    if (a.length < sz) {
      a = InvokeUtils.newArray(a.getClass(), sz);
    }
    int i = 0;
    Object[] result = a;
    for (Node<E> n = head; ; n = n.next) {
      result[i++] = n.val;
      if (n == tail) {
        break;
      }
    }
    if (a.length > sz) {
      a[sz] = null;
    }
    return a;
  }

  @Override
  public boolean add(E e) {
    push(e);
    return true;
  }

  @Override
  public void add(int index, E element) {
    insert(index, element);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    insertAll(sz, c);
    return true;
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    insertAll(index, c);
    return !c.isEmpty();
  }

  @Override
  public void clear() {
    head = tail = null;
    sz = 0;
  }

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
        Check.that(curr).isNot(sameAs(), tail, NO_SUCH_ELEMENT);
        return (curr = curr.next).val;
      }

      private Node<E> justBeforeHead() {
        Node<E> n = new Node<>(null);
        n.next = head;
        return n;
      }
    };
  }

  @Override
  public ListIterator<E> listIterator() {
    return sz == 0 ? emptyListIterator() : new ListItr();
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    return checkIndex(index).ok(ListItr::new);
  }

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

  @Override
  public int hashCode() {
    int hash = 1;
    for (var n = head; ; n = n.next) {
      hash = 31 * hash + (n.val == null ? 0 : n.val.hashCode());
      if (n == tail) {
        break;
      }
    }
    return hash;
  }

  public String toString() {
    return '[' + CollectionMethods.implode(this) + ']';
  }

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

  private IntCheck<IndexOutOfBoundsException> checkIndex(int index) {
    return Check.on(indexOutOfBounds(), index, INDEX).is(listIndexOf(), this);
  }

  private IntCheck<IndexOutOfBoundsException> checkInclusive(int index) {
    return Check.on(indexOutOfBounds(), index, INDEX).is(gte(), 0).is(lte(), sz);
  }

}
