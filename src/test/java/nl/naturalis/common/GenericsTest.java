package nl.naturalis.common;

import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.assertSame;
import static nl.naturalis.common.ArrayMethods.pack;

public class GenericsTest {

  @Test
  public void test00() {
    Map<String, Object> m1 = Collections.singletonMap("foo", pack("Hello", ", ", "World!"));
    Map<String, String[]> m2 = Generics.narrowValueType(m1);
    assertSame(m1, m2);
  }

  @Test
  public void test01() {
    Map<String, String[]> m1 = Collections.singletonMap("foo", pack("Hello", ", ", "World!"));
    Map<String, Object> m2 = Generics.widenValueType(m1);
    assertSame(m1, m2);
  }
}
