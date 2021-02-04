package nl.naturalis.common.collection;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeMapTest {

  @Test
  public void test00() {
    TypeMap<String> map = new TypeMap<>();
    map.put(Integer.class, "integer");
    map.put(Number.class, "number");
    // map.put(Short.class, "short");
    // String s = map.get(Short.class);
    System.out.println(map);
  }
}
