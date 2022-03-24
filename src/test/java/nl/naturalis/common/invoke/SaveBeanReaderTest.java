package nl.naturalis.common.invoke;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SaveBeanReaderTest {
  @Test
  public void test00() throws NoSuchMethodException {
    Person fb = new Person();
    fb.setId(10);
    fb.setFirstName("John");
    fb.setLastName("Smith");
    LocalDate now = LocalDate.now();
    fb.setLastModified(now);
    List<String> hobbies = Arrays.asList("Tennis", "Scoccer");
    fb.setHobbies(hobbies);
    SaveBeanReader<Person> br =
        SaveBeanReader.configure(Person.class)
            .withInt("id")
            .withString("firstName", "lastName")
            .with(List.class, "hobbies")
            .with(LocalDate.class, "lastModified")
            .freeze();
    assertEquals(10, (int) br.read(fb, "id"));
    assertEquals("John", br.read(fb, "firstName"));
    assertEquals("Smith", br.read(fb, "lastName"));
    assertEquals(now, br.read(fb, "lastModified"));
    assertEquals(hobbies, br.read(fb, "hobbies"));
  }
}
