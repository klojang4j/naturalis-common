package nl.naturalis.common.path;

@FunctionalInterface
public interface KeyDeserializer {

  Object deserialize(Path path, int segmentIndex) throws KeyDeserializationException;

}
