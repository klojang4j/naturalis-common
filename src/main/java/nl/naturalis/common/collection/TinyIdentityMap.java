package nl.naturalis.common.collection;

/**
 * A fixed-size, modifiable, null-repellent {@code Map} implementation meant to contain just a few
 * entries. Keys are inserted and searched for using their identity rather than using {@code equals}
 * semantics.
 *
 * @author Ayco Holleman
 */
public class TinyIdentityMap<K, V> extends AbstractTinyMap<K, V> {

  public TinyIdentityMap(int size) {
    super(size);
  }

  int indexOf(Object key) {
    for (int i = 0; i < sz; ++i) {
      if (key == entries[i][0]) {
        return i;
      }
    }
    return -1;
  }
}
