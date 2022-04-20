package nl.naturalis.common.collection;

import nl.naturalis.common.CollectionMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.check.IntCheck;
import nl.naturalis.common.x.invoke.InvokeUtils;

import java.util.*;
import java.util.function.Supplier;

import static java.util.Collections.emptyListIterator;
import static nl.naturalis.common.ArrayMethods.EMPTY_OBJECT_ARRAY;
import static nl.naturalis.common.check.CommonChecks.*;

/**
 * A doubly-linked list, much like the JDK's {@link LinkedList}, but more focused on list
 * manipulation, and less on its queue-like aspects.
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

    Node(F val, Node<F> next) {
      this.val = val;
      this.next = next;
      next.prev = this;
    }

    Node(Node<F> prev, F val, Node<F> next) {
      this.val = val;
      this.prev = prev;
      this.next = next;
      prev.next = next.prev = this;
    }

  }

  // ======================================================= //
  // ======================= [ Itr ] ======================= //
  // ======================================================= //

  private class Itr implements Iterator<E> {

    private Node<E> curr;

    Itr() {
      curr = new Node<>(null);
      curr.next = head;
    }

    @Override
    public boolean hasNext() {
      return curr != tail;
    }

    @Override
    public E next() {
      Check.that(curr).isNot(sameAs(), tail, NO_SUCH_ELEMENT);
      return (curr = curr.next).val;
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

    }

  }

  // ======================================================= //
  // ==================== [ WiredList ] ==================== //
  // ======================================================= //

  // Ubiquitous parameter names within this class
  private static final String INDEX = "index";
  private static final String VALUES = "values";

  // Error messages
  private static final String ALREADY_EMBEDDED = "Cannot embed the same list twice";

  private static final Supplier<NoSuchElementException> NO_SUCH_ELEMENT =
      () -> new NoSuchElementException();

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
    WiredList<E> wl = new WiredList<>();
    wl.append(e0);
    wl.append(e1);
    wl.append(e2);
    wl.insertAll(3, wl.asNodes(moreElems));
    return wl;
  }

  private int sz;
  private boolean embedded;

  private Node<E> head;
  private Node<E> tail;

  public WiredList() {}

  public WiredList(Collection c) {
    insertAll(0, c);
  }

  private WiredList(Node<E> head, Node<E> tail, int sz) {
    this.head = head;
    this.tail = tail;
    this.sz = sz;
  }

  public void prepend(E value) {
    insert(0, value);
  }

  public void prependAll(Collection<? extends E> values) {
    insertAll(0, values);
  }

  public void append(E value) {
    insert(sz, value);
  }

  public void appendAll(Collection<? extends E> values) {
    insertAll(sz, values);
  }

  public void insert(int index, E value) {
    if (sz == 0) {
      head = tail = new Node<>(value);
    } else if (index == 0) {
      head.prev = new Node<>(value, head);
      head = head.prev;
    } else if (index == sz) {
      tail.next = new Node<>(tail, value);
      tail = tail.next;
    } else {
      Check.that(index, INDEX).is(gte(), 0).is(lte(), sz);
      var node = node(index);
      new Node<>(node.prev, value, node);
    }
    ++sz;
  }

  public void insertAll(int index, Collection<? extends E> values) {
    var nodes = Check.notNull(values, VALUES).ok(this::asNodes);
    insertAll(index, nodes);
  }

  /**
   * Embeds the specified {@code WiredList} within this {@code WiredList}. This method is very
   * efficient, but it will modify the provided list. Lists that have already been embedded within
   * another list will be rejected. Changes made afterwards to the provided list will be reflected
   * in this {@code WiredList} and vice versa.
   *
   * @param index The index at which to embed the {@code WiredList}
   * @param other The {@code WiredList} to embed
   */
  public void embed(int index, WiredList<E> other) {
    Check.notNull(other, "other");
    if (!other.isEmpty()) {
      if (this.isEmpty()) {
        head = other.head;
        tail = other.tail;
      } else if (index == 0) {
        other.tail.next = head;
        head.prev = other.tail;
        head = other.head;
      } else if (index == sz) {
        tail.next = other.head;
        other.head.prev = tail;
      } else {
        Check.that(index, INDEX).is(gte(), 0).is(lte(), sz);
        var node = node(index);
        node.next.prev = other.tail;
        other.tail.next = node.next;
        node.next = other.head;
        other.head.prev = node;
      }
      sz += other.sz;
    }
  }

  public E cut(int index) {
    var node = checkIndex(index).ok(this::node);
    if (sz == 1) {
      if (embedded) {
        if (head.prev != null) {
          head.prev.next = tail.next;
        }
        if (tail.next != null) {
          tail.next.prev = head.prev;
        }
        embedded = false;
      }
      head = tail = null;
    } else if (node == head) {
      head = node.next;
      if (!embedded) {
        head.prev = null;  // don't hog the heap
      }
    } else if (node == tail) {
      tail = node.prev;
      if (!embedded) {
        tail.next = null;
      }
    } else {
      node.prev.next = node.next;
      node.next.prev = node.prev;
    }
    --sz;
    return node.val;
  }

  public WiredList<E> delete(int fromIndex, int toIndex) {
    Check.fromTo(this, fromIndex, toIndex);
    return cut0(fromIndex, fromIndex - toIndex);
  }

  public WiredList<E> cut(int offset, int length) {
    Check.offsetLength(sz, offset, length);
    return cut0(offset, length);
  }

  public WiredList<E> copy(int offset, int length) {
    Check.offsetLength(sz, offset, length);
    return cut0(offset, length);
  }

  private WiredList<E> cut0(int offset, int len) {
    WiredList<E> wl;
    if (len == 0) {
      wl = new WiredList<>();
    } else if (len == sz) {
      wl = new WiredList<>(head, tail, sz);
      if (embedded) {
        if (head.prev != null) {
          head.prev.next = tail.next;
        }
        if (tail.next != null) {
          tail.next.prev = head.prev;
        }
        embedded = false;
      }
      head = tail = null;
    } else {
      var start = node(offset);
      var end = node(start, offset, len);
      if (start == head) {
        head = end.next;
        if (!embedded) {
          head.prev = null;
        }
      } else if (end == tail) {
        tail = start.prev;
        if (!embedded) {
          tail.next = null;
        }
      } else {
        start.prev.next = end.next;
        end.next.prev = start.prev;
      }
      wl = new WiredList<>(start, end, len);
    }
    sz -= len;
    return wl;
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
    return false;
  }

  @Override
  public Iterator<E> iterator() {
    return sz == 0 ? emptyListIterator() : new Itr();
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
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    appendAll(c);
    return true;
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    insertAll(index, c);
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return false;
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
    return cut(index);
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
    if (fromIndex == 0 && toIndex == sz) {
      return this;
    }
    Check.fromTo(this, fromIndex, toIndex);
    return null;
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

  private void insertAll(int index, Node[] nodes) {
    if (nodes.length > 0) {
      if (sz == 0) {
        head = nodes[0];
        tail = nodes[nodes.length - 1];
      } else if (index == 0) {
        nodes[nodes.length - 1].next = head;
        head.prev = nodes[nodes.length - 1];
        head = nodes[0];
      } else if (index == sz) {
        tail.next = nodes[0];
        nodes[0].prev = tail;
        tail = nodes[nodes.length - 1];
      } else {
        Check.that(index, INDEX).is(gte(), 0).is(lte(), sz);
        var node = node(index);
        nodes[0].prev = node.prev;
        nodes[nodes.length - 1].next = node;
        node.prev = nodes[nodes.length - 1];
      }
      sz += nodes.length;
    }
  }

  private static final Node[] ZERO_NODES = new Node[0];

  private Node[] asNodes(Collection<? extends E> values) {
    if (values.size() == 0) {
      return ZERO_NODES;
    }
    Node[] nodes = new Node[values.size()];
    Iterator<? extends E> iter = values.iterator();
    nodes[0] = new Node<>(iter.next());
    for (int i = 1; i < nodes.length; ++i) {
      nodes[i] = new Node<>(nodes[i - 1], iter.next());
    }
    return nodes;
  }

  private Node[] asNodes(E[] values) {
    if (values.length == 0) {
      return ZERO_NODES;
    }
    Node[] nodes = new Node[values.length];
    nodes[0] = new Node<>(values[0]);
    for (int i = 1; i < values.length; ++i) {
      nodes[i] = new Node<>(nodes[i - 1], values[i]);
    }
    return nodes;
  }

  private Node<E> node(int index) {
    if (index < (sz / 2)) {
      Node<E> n = head;
      for (int i = 0; i < index; i++) {
        n = n.next;
      }
      return n;
    } else {
      Node<E> n = tail;
      for (int i = sz - 1; i > index; i--) {
        n = n.prev;
      }
      return n;
    }
  }

  // Will only be called if len > 0
  private Node<E> node(Node<E> startNode, int startIndex, int len) {
    if (len < ((sz - startIndex) / 2)) {
      Node<E> node = startNode;
      for (int i = 1; i < len; i++) {
        node = node.next;
      }
      return node;
    } else {
      Node<E> node = tail;
      for (int i = sz; i > startIndex + len; i--) {
        node = node.prev;
      }
      return node;
    }
  }

  private IntCheck<IndexOutOfBoundsException> checkIndex(int index) {
    return Check.on(indexOutOfBounds(), index, INDEX).is(listIndexOf(), this);
  }

}
