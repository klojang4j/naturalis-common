package nl.naturalis.common.invoke;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BeanReaderTest {

  @Test
  public void test00() {

    Person person = new Person();
    person.setId(10);
    person.setFirstName("John");
    person.setLastName("Smith");

    BeanReader<Person> br = new BeanReader<>(Person.class);

    int i = br.read(person, "id");
    assertEquals(10, i);
    String s = br.read(person, "firstName");
    assertEquals("John", s);
    s = br.read(person, "lastName");
    assertEquals("Smith", s);
  }
}
