package nl.naturalis.common.check;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static nl.naturalis.common.check.Checks.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CheckTest {

  @Test(expected = IOException.class)
  public void that01() throws IOException {
    Check.that(3 > 5, () -> new IOException());
  }

  @Test
  public void that02() throws IOException {
    Check.that(5 > 3, () -> new IOException());
  }

  @Test // notNull() forces ObjectCheck
  public void test01() {
    Check check = Check.that(3, "fooArg", notNull());
    assertEquals(ObjectCheck.class, check.getClass());
  }

  @Test // two ints silently converted to Integer
  public void test02() {
    Check check = Check.that(5, "fooArg", numGreaterThan(), 4);
    assertEquals(ObjectCheck.class, check.getClass());
  }

  @Test(expected = IllegalArgumentException.class)
  public void test03() {
    Check.argument(2, "fooArg").and(numGreaterThan(), 4);
  }

  @Test
  public void test04() {
    Check.that(Integer.valueOf(5), "fooArg", numGreaterThan(), 3);
  }

  @Test
  public void test05() {
    IntCheck<IllegalArgumentException> check = (IntCheck) Check.argument(9, "fooArg");
    check.and(numGreaterThan(), 4);
  }

  @Test
  public void test06() {
    IntCheck<IllegalArgumentException> check = (IntCheck) Check.argument(9, "fooArg");
    check.and(numGreaterThan(), Integer.valueOf(4));
  }

  @Test
  public void test07() {
    IntCheck<IllegalArgumentException> check = (IntCheck) Check.argument(9, "fooArg");
    check.and(greaterThan(), Integer.valueOf(4));
  }

  @Test // Gotcha: intstanceOf() takes an object so force the int argument to be boxed.
  public void test09() {
    IntCheck<IllegalArgumentException> check = (IntCheck) Check.argument(9, "fooArg");
    check.and(instanceOf(), Integer.class);
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
    Employee employee = new Employee();
    employee.setAge(12);
    Check.notNull(employee, "employee").and(Employee::getAge, "age", atLeast(), 16).ok();
  }

  @Test(expected = IOException.class)
  public void and03() throws IOException {
    Employee employee = new Employee();
    employee.setHobbies(List.of("Skating", "Scuba diving"));
    Check.notNull(employee, "employee", IOException::new)
        .and(Employee::getHobbies, Collection::contains, "Scoccer", "Scoccer required hobby")
        .ok();
  }

  @Test(expected = IOException.class)
  public void and04() throws IOException {
    Employee employee = new Employee();
    employee.setId(-23);
    Check.notNull(employee, "employee", IOException::new)
        .and(Employee::getId, greaterThan(), 0, "Id must not be negative")
        .ok();
  }

  @Test
  public void and05() {
    Employee employee = new Employee();
    employee.setJustSomeNumbers(new float[] {3.2F, 103.2F, 0.8F});
    Check.notNull(employee, "employee")
        .and(Employee::getJustSomeNumbers, "justSomeLuckyNumbers", isArray());
  }

  @Test(expected = IllegalArgumentException.class)
  public void and06() {
    Employee employee = new Employee();
    employee.setId(7);
    Check.notNull(employee, "employee").and(Employee::getId, "id", isArray());
  }

  @Test
  public void and07() {
    Employee[] employees = new Employee[10];
    Check.notNull(employees, "employees").and(Array::getLength, "length", lessThan(), 100);
  }
}
