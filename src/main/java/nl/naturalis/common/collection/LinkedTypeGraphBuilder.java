package nl.naturalis.common.collection;

import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.collection.LinkedTypeGraph.LinkedTypeNode;

import java.util.Arrays;

import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.collection.LinkedTypeGraph.NO_SUBTYPES;

/**
 * A builder class for {@link LinkedTypeGraph} instances.
 *
 * @param <V> The type of the values in the {@code LinkedTypeGraph}
 * @author Ayco Holleman
 */
public final class LinkedTypeGraphBuilder<V> {

  // ================================================================== //
  // ======================= [ WritableTypeNode ] ===================== //
  // ================================================================== //

  private static class WritableTypeNode {

    private final Class<?> type;
    private Object value;

    private final WiredList<WritableTypeNode> subclasses = new WiredList<>();
    private final WiredList<WritableTypeNode> subinterfaces = new WiredList<>();

    WritableTypeNode(Class<?> type, Object val) {
      this.type = type;
      this.value = val;
    }

    LinkedTypeNode toTypeNode() {
      var subclasses = this.subclasses.stream()
          .map(WritableTypeNode::toTypeNode)
          .toArray(LinkedTypeNode[]::new);
      if (subclasses.length == 0) {
        subclasses = NO_SUBTYPES;
      }
      var subinterfaces = this.subinterfaces.stream()
          .map(WritableTypeNode::toTypeNode)
          .toArray(LinkedTypeNode[]::new);
      if (subinterfaces.length == 0) {
        subinterfaces = NO_SUBTYPES;
      }
      return new LinkedTypeNode(type, value, subclasses, subinterfaces);
    }

    void addChild(WritableTypeNode node) {
      if (node.type.isInterface()) {
        addSubinterface(node);
      } else {
        addSubclass(node);
      }
    }

    void addSubclass(WritableTypeNode node) {
      Check.that(node.type)
          .isNot(sameAs(), type, () -> new DuplicateKeyException(type));
      WiredIterator<WritableTypeNode> itr = subclasses.wiredIterator();
      while (itr.hasNext()) {
        var subclass = itr.next();
        if (ClassMethods.isSubtype(subclass.type, node.type)) {
          itr.remove();
          node.addSubclass(subclass);
        } else if (ClassMethods.isSubtype(node.type, subclass.type)) {
          subclass.addSubclass(node);
          return;
        }
      }
      itr = subinterfaces.wiredIterator();
      while (itr.hasNext()) {
        var subinterface = itr.next();
        if (ClassMethods.isSubtype(node.type, subinterface.type)) {
          subinterface.addSubclass(node);
          return;
        }
      }
      subclasses.append(node);
    }

    void addSubinterface(WritableTypeNode node) {
      Check.that(node.type)
          .isNot(sameAs(), type, () -> new DuplicateKeyException(type));
      WiredIterator<WritableTypeNode> itr = subinterfaces.wiredIterator();
      while (itr.hasNext()) {
        var subinterface = itr.next();
        if (ClassMethods.isSubtype(subinterface.type, node.type)) {
          itr.remove();
          node.addSubinterface(subinterface);
        } else if (ClassMethods.isSubtype(node.type, subinterface.type)) {
          subinterface.addSubinterface(node);
          return;
        }
      }
      subinterfaces.append(node);
    }

  }

  // ================================================================== //
  // ===================== [ TypeGraphMapBuilder ] ==================== //
  // ================================================================== //

  private final Class<V> valueType;
  private final WritableTypeNode root;

  private int size;
  private boolean autobox = true;

  LinkedTypeGraphBuilder(Class<V> valueType) {
    this.valueType = valueType;
    this.root = new WritableTypeNode(Object.class, null);
  }

  /**
   * Whether to enable the "autoboxing" feature. See {@link TypeMap} for an
   * explanation of this feature. By default, autoboxing is enabled.
   *
   * @return This {@code Builder} instance
   */
  public LinkedTypeGraphBuilder<V> autobox(boolean autobox) {
    this.autobox = autobox;
    return this;
  }

  /**
   * Associates the specified type with the specified value.
   *
   * @param type The type
   * @param value The value
   * @return This {@code Builder} instance
   */
  public LinkedTypeGraphBuilder<V> add(Class<?> type, V value) {
    Check.notNull(type, "type");
    Check.notNull(value, "value").is(instanceOf(), valueType);
    if (type == Object.class) {
      if (root.value == null) {
        root.value = value;
        ++size;
      } else {
        throw new DuplicateKeyException(Object.class);
      }
    } else {
      root.addChild(new WritableTypeNode(type, value));
      ++size;
    }
    return this;
  }

  /**
   * Associates the specified value with the specified types.
   *
   * @param value The value
   * @param types The types to associate the value with
   * @return This {@code Builder} instance
   */
  public LinkedTypeGraphBuilder<V> addMultiple(V value, Class<?>... types) {
    Check.notNull(value, "value").is(instanceOf(), valueType);
    Check.notNull(types, "types");
    Arrays.stream(types).forEach(t -> add(t, value));
    return this;
  }

  /**
   * Returns an unmodifiable {@code TypeMap} with the configured types and
   * behaviour.
   *
   * @return A new {@code TypeMap} instance with the configured types and behaviour
   */
  public LinkedTypeGraph<V> freeze() {
    return new LinkedTypeGraph<>(root.toTypeNode(), size, autobox);
  }

}
