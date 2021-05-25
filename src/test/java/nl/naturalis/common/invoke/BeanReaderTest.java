package nl.naturalis.common.invoke;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class BeanReaderTest {

  @Test
  public void test00() {
    FooBean fb = new FooBean();
    fb.setId(10);
    fb.setFirstName("John");
    fb.setLastName("Smith");
    BeanReader<FooBean> br = new BeanReader<>(FooBean.class);
    int i = br.read(fb, "id");
    assertEquals(10, i);
    String s = br.read(fb, "firstName");
    assertEquals("John", s);
    s = br.read(fb, "lastName");
    assertEquals("Smith", s);
  }
}
