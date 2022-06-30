package nl.naturalis.common.collection;

import java.util.Set;

/**
 * The {@code TypeSet} interface is the {@link Set} counterpart to the {@link
 * TypeMap} interface. As with {@link TypeMap}, the {@code TypeSet} does not specify
 * any methods of its own. It is the behavior required of implementations that take
 * it beyond the {@code Set} interface. See the class comments for {@link TypeMap}
 * for a detailed explanation. The behavior of the {@code contains} method must
 * follow the same logic as the {@code containKey} method of {@code TypeMap}. In
 * practice all implementations are backed by a {@code TypeMap} instance.
 */
public sealed interface TypeSet extends Set<Class<?>> permits AbstractTypeSet {
}
