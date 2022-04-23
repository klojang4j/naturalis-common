package nl.naturalis.common.collection;

import nl.naturalis.common.CollectionMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.check.IntCheck;
import nl.naturalis.common.x.invoke.InvokeUtils;

import java.util.*;
import java.util.function.Supplier;

import static java.util.Collections.*;
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
  // ====================== [ Chain ] ====================== //
  // ======================================================= //

  private class Chain {

    final Node<E> head;
    final Node<E> tail;
    final int length;

    Chain(Node<E> head, Node<E> tail, int length) {
      this.head = head;
      this.tail = tail;
      this.length = length;
    }

  }

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
      Check.on(illegalState(), curr).isNot(NULL(), "previousIndex/nextIndex not called yet");
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

  private static final Supplier<NoSuchElementException> NO_SUCH_ELEMENT =
      NoSuchElementException::new;

  public static <E> WiredList<E> of(E e) {
    WiredList<E> wl = new WiredList<>();
    wl.append(e);
    return wl;
  }

  public static <E> WiredList<E> of(E e0, E e1) {
    WiredList<E> wl = new WiredList<>();
    wl.append(e0);
    wl.append(e1);
    return wl;
  }

  @SafeVarargs
  public static <E> WiredList<E> of(E e0, E e1, E e2, E... moreElems) {
    Check.notNull(moreElems, "moreElems");
    WiredList<E> wl = new WiredList<>();
    wl.append(e0);
    wl.append(e1);
    wl.append(e2);
    if (moreElems.length != 0) {
      wl.insert(3, wl.chain(moreElems));
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

  /**
   * Inserts the specified value at the start of the list, right-shifting the elements currently in
   * the list.
   *
   * @param value The value to insert
   */
  public void prepend(E value) {
    insert(0, value);
  }

  /**
   * Inserts the specified values at the start of the list, right-shifting the elements currently in
   * the list.
   *
   * @param values The values to insert
   */
  public void prependAll(Collection<? extends E> values) {
    insertAll(0, values);
  }

  /**
   * Inserts the specified value at the end of the list.
   *
   * @param value The value to insert
   */
  public void append(E value) {
    insert(sz, value);
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
   * Inserts the specified value at the specified location, right-shifting the elements at and after
   * that location.
   *
   * @param value The value to insert
   */
  @SuppressWarnings({"unchecked"})
  public void insert(int index, E value) {
    insert(index, chain((E[]) new Object[] {value}));
  }

  /**
   * Inserts the specified values at the specified location, right-shifting the elements at and
   * following that location.
   *
   * @param values The values to insert
   */
  public void insertAll(int index, Collection<? extends E> values) {
    Check.notNull(values, VALUES);
    if (!values.isEmpty()) {
      insert(index, chain(values));
    }
  }

  /**
   * Embeds the specified list within this {@code WiredList}. This method is very efficient, but
   * <b>the provided list will empty afterwards</b>. If you don't want this to happen, use
   * {@link #insertAll(int, Collection) insertAll}.
   *
   * @param index The index at which to embed the {@code WiredList}
   * @param other The {@code WiredList} to embed
   */
  public void embed(int index, WiredList<? extends E> other) {
    embed0(index, other);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void embed0(int index, WiredList other) {
    Check.notNull(other, "other").isNot(sameAs(), this, "list cannot be embedded within itself");
    if (!other.isEmpty()) {
      if (this.isEmpty()) {
        head = other.head;
        tail = other.tail;
      } else if (index == 0) {
        join(other.tail, head);
        head = other.head;
      } else if (index == sz) {
        join(tail, other.head);
        tail = other.tail;
      } else {
        var node = checkToIndex(index).ok(this::node);
        join(node.prev, other.head);
        join(other.tail, node);
      }
      sz += other.sz;
      other.clear();
    }
  }

  /**
   * Removes the first element from the list, left-shifting the elements following it.
   *
   * @return The value of the first element
   */
  public E deleteFirst() {
    return delete0(0, 1).head.val;
  }

  /**
   * Removes the last element from the list.
   *
   * @return The value of the last element
   */
  public E deleteLast() {
    return delete0(sz, 1).head.val;
  }

  /**
   * Removes the element at the specified location, left-shifting the elements at and following that
   * location.
   *
   * @param index The index of the element to remove
   * @return The value of the removed element
   */
  public E delete(int index) {
    return delete0(index, 1).head.val;
  }

  public WiredList<E> deleteRegion(int fromIndex, int toIndex) {
    int length = Check.fromTo(sz, fromIndex, toIndex);
    return delete0(fromIndex, length);
  }

  public WiredList<E> deleteSegment(int offset, int length) {
    Check.offsetLength(sz, offset, length);
    return delete0(offset, length);
  }

  public WiredList<E> copy(int offset, int length) {
    Check.offsetLength(sz, offset, length);
    return delete0(offset, length);
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
  public Iterator<E> iterator() {
    return sz == 0 ? emptyIterator() : new Iterator<E>() {

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
        Node<E> n = new Node(null);
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
    append(e);
    return true;
  }

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
  public boolean removeAll(Collection<?> c) {
    Check.notNull(c, "collection");
    return new HashSet<>(this).removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    Check.notNull(c, "collection");
    return new HashSet<>(this).retainAll(c);
  }

  @Override
  public void clear() {
    head = tail = null;
    sz = 0;
  }

  @Override
  public E get(int index) {
    return checkIndex(index).ok(this::node).val;
  }

  @Override
  public E set(int index, E element) {
    var node = checkIndex(index).ok(this::node);
    E old = node.val;
    node.val = element;
    return old;
  }

  @Override
  public void add(int index, E element) {
    insert(index, element);
  }

  @Override
  public E remove(int index) {
    return delete(index);
  }

  @Override
  public int indexOf(Object o) {
    return 0;
  }

  @Override
  public int lastIndexOf(Object o) {
    return 0;
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException();
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

  private void insert(int index, Chain chain) {
    if (sz == 0) {
      head = chain.head;
      tail = chain.tail;
    } else if (index == 0) {
      join(chain.tail, head);
      head = chain.head;
    } else if (index == sz) {
      join(tail, chain.head);
      tail = chain.tail;
    } else {
      var node = checkToIndex(index).ok(this::node);
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
    WiredList<E> wl;
    if (len == 0) {
      wl = new WiredList<>();
    } else if (len == sz) {
      wl = new WiredList<>(head, tail, sz);
      head = tail = null;
    } else {
      var first = node(off);
      var last = node(first, off, len);
      if (first == head) {
        makeHead(last.next);
      } else if (last == tail) {
        makeTail(first.prev);
      } else {
        join(first.prev, last.next);
      }
      wl = new WiredList<>(first, last, len);
    }
    sz -= len;
    return wl;
  }

  private E deleteNode(Node<E> node) {
    E val = node.val;
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
    return val;
  }

  private Chain chain(Collection<? extends E> values) {
    if (values instanceof WiredList wl) {
      return copy(wl.head, wl.size());
    }
    Iterator<? extends E> itr = values.iterator();
    var firstNode = new Node<>((E) itr.next());
    var lastNode = firstNode;
    while (itr.hasNext()) {
      lastNode = new Node<>(lastNode, itr.next());
    }
    return new Chain(firstNode, lastNode, values.size());
  }

  private Chain chain(E[] values) {
    var firstNode = new Node<>(values[0]);
    var lastNode = firstNode;
    for (int i = 1; i < values.length; ++i) {
      lastNode = new Node<>(lastNode, values[i]);
    }
    return new Chain(firstNode, lastNode, values.length);
  }

  private Chain copy(Node<E> node, int len) {
    var firstNode = new Node<>(node.val);
    var lastNode = firstNode;
    for (int i = 1; i < len; ++i) {
      lastNode = new Node<>(lastNode, (node = node.next).val);
    }
    return new Chain(firstNode, lastNode, len);
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
    if (len < ((sz - startIndex) >> 1)) {
      Node<E> node = startNode;
      for (int i = 1; i < len; ++i) {
        node = node.next;
      }
      return node;
    } else {
      Node<E> node = tail;
      for (int i = sz; i > startIndex + len; --i) {
        node = node.prev;
      }
      return node;
    }
  }

  private void makeHead(Node<E> node) {
    node.prev = null;
    head = node;
  }

  private void makeTail(Node<E> node) {
    node.next = null;
    tail = node;
  }

  private static void join(Node prev, Node next) {
    prev.next = next;
    next.prev = prev;
  }

  private IntCheck<IndexOutOfBoundsException> checkIndex(int index) {
    return Check.on(indexOutOfBounds(), index, INDEX).is(listIndexOf(), this);
  }

  private IntCheck<IndexOutOfBoundsException> checkToIndex(int index) {
    return Check.on(indexOutOfBounds(), index, INDEX).is(gte(), 0).is(lte(), sz);
  }

}
