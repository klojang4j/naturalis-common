package nl.naturalis.common;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import nl.naturalis.common.check.Check;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static nl.naturalis.common.function.IntRelation.*;

public class CheckTest {

  @Test(expected = IOException.class)
  public void that01() throws IOException {
    Check.that(3 > 5, () -> new IOException());
  }

  @Test
  public void that02() throws IOException {
    Check.that(5 > 3, () -> new IOException());
  }

  @Test
  public void that03() throws IOException {
    String s = Check.that(5 > 3, () -> "Hello, World", () -> new IOException());
    assertEquals("Hello, World", s);
  }

  @Test
  public void argument01() {
    try {
      Check.argument(3 > 5, "not true");
    } catch (IllegalArgumentException e) {
      assertEquals("not true", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void argument02() {
    try {
      Check.argument(3, x -> x > 5, "not true");
    } catch (IllegalArgumentException e) {
      assertEquals("not true", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void argument03() {
    int i = Check.argument(5, x -> x > 3, "not true");
    assertEquals(5, i);
  }

  @Test
  public void notNull02() {
    try {
      Check.notNull(null, "storage");
    } catch (IllegalArgumentException e) {
      assertEquals("storage must not be null", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notNull03() {
    try {
      Check.notNull(null, "The sky is blue", "");
    } catch (IllegalArgumentException e) {
      assertEquals("The sky is blue", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notNull04() {
    try {
      Check.notNull(null, "The sky is %s", "red");
    } catch (IllegalArgumentException e) {
      assertEquals("The sky is red", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notNull05() {
    try {
      Check.notNull(null, "The sky is %s, but blood is %s", "blue", "red");
    } catch (IllegalArgumentException e) {
      assertEquals("The sky is blue, but blood is red", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notNull06() {
    try {
      Check.notNull(null, "The sky is %s, grass is %s and blood is %s", "blue", "green", "red");
    } catch (IllegalArgumentException e) {
      assertEquals("The sky is blue, grass is green and blood is red", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notNull07() {
    Object obj0 = new Object();
    Object obj1 = Check.notNull(obj0, "foo");
    assertTrue(obj0 == obj1);
  }

  @Test
  public void greaterThan01() {
    try {
      Check.gt(3, 5, "counter");
    } catch (IllegalArgumentException e) {
      assertEquals("counter must be greater than 5", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void greaterThan02() {
    int i = Check.gt(5, 3, "counter");
    assertEquals(5, i);
  }

  @Test
  public void testCheckChaining() {
    Integer i = Check.that(Integer.valueOf(5), "numBirds").notNull().gte(2).lt(10).ok();
    assertEquals(Integer.valueOf(5), i);
  }

  class Employee {
    int id;
    String fullName;
    Integer age;
    List<String> hobbies;

    Employee(int id, String fullName, Integer age, String... hobbies) {
      super();
      this.id = id;
      this.fullName = fullName;
      this.age = age;
      this.hobbies = List.of(hobbies);
    }

    int getId() {
      return id;
    }

    String getFullName() {
      return fullName;
    }

    Integer getAge() {
      return age;
    }

    List<String> getHobbies() {
      return hobbies;
    }
  }

  @Test
  public void and01() {
    Employee employee = new Employee(3, "John Smith", 43, "Skating", "Scoccer");
    Check.that(employee, "employee")
        .notNull()
        .and(Employee::getId, GT, 0, "Id must not be negative")
        .and(Employee::getFullName, s -> s.length() < 200, "Full name too large")
        .and(Employee::getHobbies, Collection::contains, "Scoccer", "Scoccer required hobby")
        .and(Employee::getAge, GTE, 16, "Employee must be at least 16")
        .ok();
  }

  @Test(expected = IllegalArgumentException.class)
  public void and02() {
    Employee employee = new Employee(3, "John Smith", 12, "Skating", "Scoccer");
    Check.that(employee, "employee")
        .notNull()
        .and(Employee::getId, GT, 0, "Id must not be negative")
        .and(Employee::getFullName, s -> s.length() < 200, "Full name too large")
        .and(Employee::getHobbies, Collection::contains, "Scoccer", "Scoccer required hobby")
        .and(Employee::getAge, GTE, 16, "Employee must be at least 16")
        .ok();
  }

  @Test(expected = IOException.class)
  public void and03() throws IOException {
    Employee employee = new Employee(3, "John Smith", 44, "Skating", "Scuba diving");
    Check.that(employee, "employee", IOException::new)
        .notNull()
        .and(Employee::getId, GT, 0, "Id must not be negative")
        .and(Employee::getFullName, s -> s.length() < 200, "Full name too large")
        .and(Employee::getHobbies, Collection::contains, "Scoccer", "Scoccer required hobby")
        .and(Employee::getAge, GTE, 16, "Employee must be at least 16")
        .ok();
  }

  @Test(expected = IOException.class)
  public void and04() throws IOException {
    Employee employee = new Employee(-23, "John Smith", 44, "Skating", "Scuba diving");
    Check.that(employee, "employee", IOException::new)
        .notNull()
        .and(Employee::getId, GT, 0, "Id must not be negative")
        .and(Employee::getFullName, s -> s.length() < 200, "Full name too large")
        .and(Employee::getHobbies, Collection::contains, "Scoccer", "Scoccer required hobby")
        .and(Employee::getAge, GTE, 16, "Employee must be at least 16")
        .ok();
  }

  @Test(expected = IOException.class)
  public void and05() throws IOException {
    Employee employee = new Employee(-23, "John Smith", 44, "Skating", "Scuba diving");
    Check.that(employee, "employee", IOException::new)
        .notNull()
        .and(Employee::getId, GT, 0, "Id must not be negative")
        .and(Employee::getFullName, s -> s.length() < 5, "Full name too large")
        .and(Employee::getHobbies, Collection::contains, "Scoccer", "Scoccer required hobby")
        .and(Employee::getAge, GTE, 16, "Employee must be at least 16")
        .ok();
  }
}
