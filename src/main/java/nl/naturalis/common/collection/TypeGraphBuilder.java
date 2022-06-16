package nl.naturalis.common.collection;

import nl.naturalis.common.check.Check;
import nl.naturalis.common.collection.TypeGraph.TypeNode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Map.Entry;
import static java.util.Map.entry;
import static nl.naturalis.common.ClassMethods.isSubtype;
import static nl.naturalis.common.ClassMethods.isSupertype;
import static nl.naturalis.common.check.CommonChecks.deepNotNull;
import static nl.naturalis.common.check.CommonChecks.instanceOf;

/**
 * A builder class for {@link TypeGraph} instances.
 *
 * @param <V> The type of the values in the {@code TypeGraph}
 * @author Ayco Holleman
 */
public final class TypeGraphBuilder<V> {

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

    // Split the types in interfaces and non-interfaces. Interfaces can only
    // extend other interfaces, so if the client passes an interface type to
    // get() or containsKey() we can significantly confine our search
    TypeNode toTypeNode() {
      List<List<WritableTypeNode>> mySubtypes = subtypes.group(subtype -> subtype.type.isInterface());
      var subinterfaces = Map.ofEntries(createEntries(mySubtypes.get(0)));
      var subclasses = Map.ofEntries(createEntries(mySubtypes.get(1)));
      return new TypeNode(type, value, subclasses, subinterfaces);
    }

    private Entry[] createEntries(List<WritableTypeNode> mySubtypes) {
      return mySubtypes.stream().map(this::toEntry).toArray(Entry[]::new);
    }

    private Entry<? extends Class<?>, TypeNode> toEntry(WritableTypeNode node) {
      return entry(node.type, node.toTypeNode());
    }

    void addChild(WritableTypeNode node) {
      if (node.type == this.type) {
        throw new DuplicateKeyException(node.type);
      }
      Iterator<WritableTypeNode> itr = subtypes.wiredIterator();
      boolean inserted = false;
      while (itr.hasNext()) {
        var child = itr.next();
        if (isSupertype(node.type, child.type)) {
          itr.remove();
          node.addChild(child);
        } else if (isSubtype(node.type, child.type)) {
          child.addChild(node);
          inserted = true;
          break;
        }
      }
      if (!inserted) {
        subtypes.add(node);
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

  TypeGraphBuilder(Class<V> valueType) {
    this.valueType = valueType;
    this.root = new WritableTypeNode(Object.class, null);
  }

  /**
   * Whether to enable the "autoboxing" feature. See {@link TypeMap} for an
   * explanation of this feature. By default, autoboxing is enabled.
   *
   * @return This {@code Builder} instance
   */
  public TypeGraphBuilder<V> autobox(boolean autobox) {
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
  public TypeGraphBuilder<V> add(Class<?> type, V value) {
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
  public TypeGraphBuilder<V> addMultiple(V value, Class<?>... types) {
    Check.that(types, "types").is(deepNotNull());
    Arrays.stream(types).forEach(t -> add(t, value));
    return this;
  }

  /**
   * Returns a new {@code TypeGraph} instance with the configured types and
   * behaviour.
   *
   * @return A new {@code TypeGraph} instance with the configured types and behaviour
   */
  public TypeGraph<V> freeze() {
    return new TypeGraph<>(root.toTypeNode(), size, autobox);
  }

}
