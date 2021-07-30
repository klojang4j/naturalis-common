package nl.naturalis.common.invoke;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import org.junit.Test;

public class SaveBeanReaderTest {
  @Test
  public void test00() throws NoSuchMethodException {
    FooBean fb = new FooBean();
    fb.setId(10);
    fb.setFirstName("John");
    fb.setLastName("Smith");
    LocalDate now = LocalDate.now();
    fb.setDate(now);
    SaveBeanReader<FooBean> br =
        SaveBeanReader.configure(FooBean.class)
            .with("id", int.class)
            .with("firstName", String.class)
            .with("lastName", String.class)
            .withGetter("lastModified", LocalDate.class)
            .freeze();
    int i = br.read(fb, "id");
    assertEquals(10, i);
    String s = br.read(fb, "firstName");
    assertEquals("John", s);
    s = br.read(fb, "lastName");
    assertEquals("Smith", s);
    assertEquals(now, br.read(fb, "lastModified"));
  }
}
