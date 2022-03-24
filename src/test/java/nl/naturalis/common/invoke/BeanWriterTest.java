package nl.naturalis.common.invoke;

import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static nl.naturalis.common.invoke.IncludeExclude.INCLUDE;
import static org.junit.Assert.*;

public class BeanWriterTest {

  @Test
  public void set00() throws Throwable {
    Person person = new Person();
    BeanWriter writer = new BeanWriter(Person.class);
    writer.set(person, "firstName", "John");
    assertEquals("John", person.getFirstName());
  }

  @Test
  public void set01() throws Throwable {
    Person person = new Person();
    BeanWriter writer = new BeanWriter(Person.class);
    writer.set(person, "someNumber", 3);
    assertEquals(person.getSomeNumber().getClass(), Integer.class);
    assertEquals(3, person.getSomeNumber());
  }

  @Test
  public void set02() throws Throwable {
    Person person = new Person();
    BeanWriter writer = new BeanWriter(Person.class);
    writer.set(person, "someNumber", 3.5);
    assertEquals(person.getSomeNumber().getClass(), Double.class);
    assertEquals(3.5, person.getSomeNumber());
  }

  @Test
  public void set03() throws Throwable {
    Person person = new Person();
    BeanWriter writer = new BeanWriter(Person.class);
    try {
      writer.set(person, "someShort", 3.5);
    } catch (IllegalAssignmentException e) {
      assertEquals(Person.class, e.getBeanClass());
      assertEquals(e.getPropertyName(), "someShort");
      assertEquals(e.getPropertyType(), short.class);
      assertEquals(e.getValue(), 3.5);
      return;
    }
    fail();
  }

  @Test(expected = IllegalAssignmentException.class)
  public void set04() throws Throwable {
    Person person = new Person();
    BeanWriter writer = new BeanWriter(Person.class);
    writer.set(person, "someShort", null);
  }

  @Test
  public void set05() throws Throwable {
    Person person = new Person();
    person.setLastName("Smith");
    BeanWriter writer = new BeanWriter(Person.class);
    writer.set(person, "lastName", null);
    assertNull(person.getLastName());
  }

  @Test
  public void set06() throws Throwable {
    Person person = new Person();
    BeanWriter writer = new BeanWriter(Person.class);
    writer.set(person, "someInt", (byte) 125);
    assertEquals(125, person.getSomeInt());
  }

  @Test
  public void set07() throws Throwable {
    Person person = new Person();
    BeanWriter writer = new BeanWriter(Person.class);
    writer.set(person, "someNumber", (byte) 125);
    assertEquals((byte) 125, person.getSomeNumber());
  }

  @Test
  public void copy00() throws Throwable {
    Person person0 = new Person();
    person0.setId(100);
    person0.setFirstName("John");
    person0.setLastName("Smith");
    person0.setHobbies(null);
    person0.setLastModified(LocalDate.of(2022, 04, 03));
    person0.setSomeCharSequence("Hello World");

    Person person1 = new Person();
    person1.setId(80);
    person1.setFirstName("Patrick");
    person1.setLastName("Steward");
    person1.setHobbies(List.of("Tennis"));
    person1.setLastModified(LocalDate.of(2021, 04, 03));
    person1.setSomeCharSequence("Hi There");
    person1.setSomeChar('A');

    BeanWriter writer = new BeanWriter(Person.class, INCLUDE,
        "id",
        "firstName",
        "lastName",
        "hobbies",
        "lastModified",
        "someCharSequence");

    writer.copy(person0, person1);
    assertEquals(person0.getId(), person1.getId());
    assertEquals(person0.getFirstName(), person1.getFirstName());
    assertEquals(person0.getLastName(), person1.getLastName());
    assertEquals(person0.getHobbies(), person1.getHobbies());
    assertEquals(person0.getSomeCharSequence(), person1.getSomeCharSequence());
    assertEquals('A', person1.getSomeChar());
  }

}
