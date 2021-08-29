package nl.naturalis.common.collection;

import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TypeSetTest {

  @Test
  public void test00() {
    TypeSet ts = new TypeSet(Integer.class, Short.class, Number.class, CharSequence.class);
    assertEquals(4, ts.size());
    assertTrue(ts.contains(Short.class));
    assertTrue(ts.contains(String.class));
    assertEquals(5, ts.size());
  }

  @Test
  public void test01() {
    Set<Class<?>> s = Set.of(Integer.class, Short.class, Number.class, CharSequence.class);
    TypeSet ts = new TypeSet(s, false);
    assertEquals(4, ts.size());
    assertTrue(ts.contains(Short.class));
    assertTrue(ts.contains(String.class));
    assertEquals(4, ts.size());
  }
}
