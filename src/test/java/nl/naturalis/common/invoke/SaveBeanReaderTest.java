package nl.naturalis.common.invoke;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
    List<String> hobbies = Arrays.asList("Tennis", "Scoccer");
    fb.setHobbies(hobbies);
    SaveBeanReader<FooBean> br =
        SaveBeanReader.configure(FooBean.class)
            .withInt("id")
            .withString("firstName", "lastName")
            .with(List.class, "hobbies")
            .withGetter(LocalDate.class, "lastModified")
            .freeze();
    assertEquals(10, (int) br.read(fb, "id"));
    assertEquals("John", br.read(fb, "firstName"));
    assertEquals("Smith", br.read(fb, "lastName"));
    assertEquals(now, br.read(fb, "lastModified"));
    assertEquals(hobbies, br.read(fb, "hobbies"));
  }
}
