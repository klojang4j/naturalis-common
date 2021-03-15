package nl.naturalis.common.collection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import nl.naturalis.common.collection.Treparator.Position;
import static nl.naturalis.common.collection.Treparator.Position.*;
import static nl.naturalis.common.collection.Tree.Traversal.*;

// UNDER CONSTRUCTION. NOT FINISHED
public class Tree<K, V> {

  public static enum Traversal {
    DEPTH_FIRST,
    WIDTH_FIRST
  }

  private static class Node<KEY, VAL> {
    private final KEY key;
    private VAL val;
    private LinkedList<Node<KEY, VAL>> ancestors;
    private LinkedList<Node<KEY, VAL>> descendants;
    private LinkedList<Node<? super KEY, VAL>> siblings;

    Node(KEY key, VAL val) {
      this.key = key;
      this.val = val;
    }
  }

  private final Treparator<K> treparator;
  private final LinkedList<Node<K, V>> leaves;
  private Traversal putTraversal;
  private Traversal getTraversal;

  public Tree(Treparator<K> treparator) {
    this.treparator = treparator;
    this.leaves = new LinkedList<>();
  }

  public boolean put(K key, V val, LinkedList<Node<K, V>> leaves) {
    for (int i = 0; i < leaves.size(); ++i) {
      Node<K, V> n = leaves.get(i);
      Position p = treparator.compare(key, n.key);
      if (p == EQUAL) {
        n.val = val;
        return true;
      } else if (p == PARENT) {
        if (putTraversal == DEPTH_FIRST) {
          if (n.ancestors == null) {
            n.ancestors = new LinkedList<>();
            n.ancestors.add(new Node<>(key, val));
            return true;
          } else if (put(key, val, n.ancestors)) {
            return true;
          }
        }

        // ...
      } else if (p == LEFT) {
        leaves.add(i, new Node<>(key, val));
        return true;
      }
    }
    return false;
  }
}
