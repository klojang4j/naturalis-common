package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.collection.LinkedTypeGraphMap.LinkedTypeNode;

import java.util.Arrays;
import java.util.Iterator;

import static nl.naturalis.common.ClassMethods.isA;
import static nl.naturalis.common.check.CommonChecks.deepNotNull;
import static nl.naturalis.common.check.CommonChecks.instanceOf;

public final class LinkedTypeGraphMapBuilder<V> {

  // ================================================================== //
  // ======================= [ WritableTypeNode ] ===================== //
  // ================================================================== //

  private static class WritableTypeNode {

    private final WiredList<WritableTypeNode> subtypes = new WiredList<>();
    private final Class<?> type;

    private Object value;

    WritableTypeNode(Class<?> type, Object val) {
      this.type = type;
      this.value = val;
    }

    LinkedTypeNode toTypeNode() {
      LinkedTypeNode[] children = new LinkedTypeNode[subtypes.size()];
      WiredList<WritableTypeNode> classes =
          subtypes.lchop(node -> node.type.isInterface()).reverse();
      classes.toArray(children);

      return new LinkedTypeNode(type, value, children);
    }

    void addChild(WritableTypeNode node) {
      if (node.type == this.type) {
        throw new DuplicateKeyException(node.type);
      }
      Iterator<WritableTypeNode> itr = subtypes.wiredIterator();
      boolean inserted = false;
      while (itr.hasNext()) {
        var child = itr.next();
        if (isA(child.type, node.type)) {
          itr.remove();
          node.addChild(child);
        } else if (isA(node.type, child.type)) {
          child.addChild(node);
          inserted = true;
        }
      }
      if (!inserted) {
        if (node.type.isInterface()) {
          subtypes.append(node);
        } else {
          subtypes.prepend(node);
        }
      }
    }

  }

  // ================================================================== //
  // ===================== [ TypeGraphMapBuilder ] ==================== //
  // ================================================================== //

  private final Class<V> valueType;
  private final WritableTypeNode root;

  private int size;
  private boolean autobox = true;

  LinkedTypeGraphMapBuilder(Class<V> valueType) {
    this.valueType = valueType;
    this.root = new WritableTypeNode(Object.class, null);
  }

  /**
   * Whether to enable the "autoboxing" feature. See {@link TypeMap for an explanation of this
   * feature. By default, autoboxing is enabled.
   *
   * @return This {@code Builder} instance
   */
  public LinkedTypeGraphMapBuilder<V> autobox(boolean autobox) {
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
  public LinkedTypeGraphMapBuilder<V> add(Class<?> type, V value) {
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
  public LinkedTypeGraphMapBuilder<V> addMultiple(V value, Class<?>... types) {
    Check.notNull(value, "value").is(instanceOf(), valueType);
    Check.that(types, "types").is(deepNotNull());
    Arrays.stream(types).forEach(t -> add(t, value));
    return this;
  }

  /**
   * Returns an unmodifiable {@code TypeMap} with the configured types and behaviour.
   *
   * @param <W> The type of the values in the returned {@code TypeMap}
   * @return S new {@code TypeMap} instance with the configured types and behaviour
   */
  public <W> LinkedTypeGraphMap<W> freeze() {
    return new LinkedTypeGraphMap<>(root.toTypeNode(), size, autobox);
  }

}
