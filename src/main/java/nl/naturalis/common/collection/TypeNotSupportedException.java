package nl.naturalis.common.collection;

/**
 * An exception throw from {@link TypeMap#get(Object) TypeMap.get} and {@link
 * TypeTreeMap#get(Object) TypeMap.get} if the map does not contain a type equal to or extending
 * from the specified type.
 *
 * @author Ayco Holleman
 */
public class TypeNotSupportedException extends IllegalArgumentException {

  public TypeNotSupportedException(Class<?> type) {
    super("Type not supported: " + type.getName());
  }
}
