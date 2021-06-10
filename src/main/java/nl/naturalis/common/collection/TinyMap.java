package nl.naturalis.common.collection;

/**
 * A fixed-size, modifiable, null-repellent {@code Map} implementation meant to contain just a few
 * entries.
 *
 * @author Ayco Holleman
 */
public class TinyMap<K, V> extends AbstractTinyMap<K, V> {

  public TinyMap(int size) {
    super(size);
  }

  int indexOf(Object key) {
    for (int i = 0; i < sz; ++i) {
      if (key.equals(entries[i][0])) {
        return i;
      }
    }
    return -1;
  }
}
