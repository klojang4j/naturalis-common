package nl.naturalis.common.check;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static nl.naturalis.common.check.Checks.atLeast;
import static nl.naturalis.common.check.Checks.greaterThan;

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
    Check.notNull(employee, "employee")
        .and(Employee::getId, "id", atLeast(), 0)
        .and(Employee::getFullName, s -> s.length() < 200, "Full name too large")
        .and(Employee::getHobbies, Collection::contains, "Scoccer", "Scoccer required hobby")
        .and(Employee::getAge, atLeast(), 16, "Employee must be at least 16")
        .ok();
  }

  @Test(expected = IllegalArgumentException.class)
  public void and02() {
    Employee employee = new Employee(3, "John Smith", 12, "Skating", "Scoccer");
    Check.notNull(employee, "employee")
        .and(Employee::getId, greaterThan(), 0, "Id must not be negative")
        .and(Employee::getFullName, s -> s.length() < 200, "Full name too large")
        .and(Employee::getHobbies, Collection::contains, "Scoccer", "Scoccer required hobby")
        .and(Employee::getAge, "age", atLeast(), 16)
        .ok();
  }

  @Test(expected = IOException.class)
  public void and03() throws IOException {
    Employee employee = new Employee(3, "John Smith", 44, "Skating", "Scuba diving");
    Check.notNull(employee, "employee", IOException::new)
        .and(Employee::getId, greaterThan(), 0, "Id must not be negative")
        .and(Employee::getFullName, s -> s.length() < 200, "Full name too large")
        .and(Employee::getHobbies, Collection::contains, "Scoccer", "Scoccer required hobby")
        .and(Employee::getAge, atLeast(), 16, "Employee must be at least 16")
        .ok();
  }

  @Test(expected = IOException.class)
  public void and04() throws IOException {
    Employee employee = new Employee(-23, "John Smith", 44, "Skating", "Scuba diving");
    Check.notNull(employee, "employee", IOException::new)
        .and(Employee::getId, greaterThan(), 0, "Id must not be negative")
        .and(Employee::getFullName, s -> s.length() < 200, "Full name too large")
        .and(Employee::getHobbies, Collection::contains, "Scoccer", "Scoccer required hobby")
        .and(Employee::getAge, atLeast(), 16, "Employee must be at least 16")
        .ok();
  }
}
