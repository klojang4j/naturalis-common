package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;

import java.util.*;

import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.length;

/**
 * A doubly-linked list, much like the JDK's {@link LinkedList}, but more focused on list
 * manipulation, and less on its queue-like aspects.
 *
 * @param <E> The type of the elements in the list
 */
public class WiredList<E> implements List<E> {

  // Ubiquitous parameter names within this class
  private static final String INDEX = "index";
  private static final String VALUES = "values";

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

  }

  private int sz;

  private Node<E> head;
  private Node<E> tail;

  public void prepend(E value) {
    if (sz == 0) {
      head = tail = new Node<>(value);
    } else if (sz == 1) {
      head = new Node<>(value, tail);
    } else {
      head = new Node<>(value, head);
    }
    ++sz;
  }

  public void prependAll(Collection<E> values) {
    var nodes = Check.notNull(values, VALUES).has(length(), gt(), 0).ok(this::asNodes);
    prependAll(nodes);
  }

  public void prependAll(E... values) {
    var nodes = Check.notNull(values, VALUES).has(length(), gt(), 0).ok(this::asNodes);
    prependAll(nodes);
  }

  public void append(E value) {
    if (sz == 0) {
      head = tail = new Node<>(value);
    } else if (sz == 1) {
      tail = new Node<>(head, value);
    } else {
      tail = new Node<>(tail, value);
    }
    ++sz;
  }

  public void appendAll(Collection<E> values) {
    var nodes = Check.notNull(values, VALUES).has(length(), gt(), 0).ok(this::asNodes);
    appendAll(nodes);
  }

  public void appendAll(E... values) {
    var nodes = Check.notNull(values, VALUES).has(length(), gt(), 0).ok(this::asNodes);
    appendAll(nodes);
  }

  public void insert(int index, E value) {
    if (index == 0) {
      prepend(value);
    } else if (index == sz) {
      append(value);
    } else {
      Check.that(index, INDEX).is(listIndexOf(), this);
      var x = new Node<>(value);
      var y = node(index);
      x.prev = y.prev;
      x.next = y;
      y.prev = x;
      ++sz;
    }
  }

  public void insertAll(int index, Collection<E> values) {
    if (index == 0) {
      prependAll(values);
    } else if (index == sz) {
      appendAll(values);
    } else {
      Check.that(index, INDEX).is(listIndexOf(), this);
      var nodes = Check.notNull(values, VALUES).has(length(), gt(), 0).ok(this::asNodes);
      insertAll(index, nodes);
    }
  }

  public void insertAll(int index, E... values) {
    if (index == 0) {
      prependAll(values);
    } else if (index == sz) {
      appendAll(values);
    } else {
      Check.that(index, INDEX).is(listIndexOf(), this);
      var nodes = Check.notNull(values, VALUES).has(length(), gt(), 0).ok(this::asNodes);
      insertAll(index, nodes);
    }
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
    return null;
  }

  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return null;
  }

  @Override
  public boolean add(E e) {
    return false;
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
    return false;
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
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
    return Check.that(index, INDEX).is(listIndexOf(), this).ok(this::node).val;
  }

  @Override
  public E set(int index, E element) {
    return null;
  }

  @Override
  public void add(int index, E element) {

  }

  @Override
  public E remove(int index) {
    return null;
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
  public ListIterator<E> listIterator() {
    return null;
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    return null;
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    return null;
  }

  private void prependAll(List<Node<E>> nodes) {
    if (head == null) {
      head = nodes.get(0);
      tail = nodes.get(nodes.size() - 1);
    } else {
      head.prev = nodes.get(nodes.size() - 1);
      head = nodes.get(0);
    }
    sz += nodes.size();
  }

  private void appendAll(List<Node<E>> nodes) {
    if (head == null) {
      head = nodes.get(0);
      tail = nodes.get(nodes.size() - 1);
    } else {
      tail.next = nodes.get(0);
      tail = nodes.get(nodes.size() - 1);
    }
    sz += nodes.size();
  }

  private void insertAll(int index, List<Node<E>> nodes) {
    var y = node(index);
    nodes.get(0).prev = y.prev;
    nodes.get(nodes.size() - 1).next = y;
    y.prev = nodes.get(nodes.size() - 1);
    sz += nodes.size();
  }

  private List<Node<E>> asNodes(Collection<E> values) {
    List<Node<E>> nodes = new ArrayList<>(values.size());
    Node<E> prev = null;
    for (E val : values) {
      var x = new Node<>(val);
      if (prev != null) {
        prev.next = x;
        x.prev = prev;
      }
      nodes.add(x);
      prev = x;
    }
    return nodes;
  }

  private List<Node<E>> asNodes(E[] values) {
    List<Node<E>> nodes = new ArrayList<>(values.length);
    Node<E> prev = null;
    for (E val : values) {
      var x = new Node<>(val);
      if (prev != null) {
        prev.next = x;
        x.prev = prev;
      }
      nodes.add(x);
      prev = x;
    }
    return nodes;
  }

  private Node<E> node(int index) {
    if (index < (sz >> 1)) {
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

}
