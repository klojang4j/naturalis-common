package nl.naturalis.common.check;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.*;

/**
 * NB A lot of these tests don't make any assertion, but just verify that we can code them as we do
 * in the first place without the compiler warning about ambiguities. The "that" static factory
 * methods and the "and" instance methods are so heavily overloaded that we had to help the compiler
 * determining the type of the lambdas. (E.g. That's why we have the "andAsInt" methods.)
 */
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

  @Test
  public void that03() {
    Check check = Check.that(3, "fooArg").is(notNull());
    assertEquals(IntCheck.class, check.getClass());
  }

  @Test // numGreaterThan() forces ObjectCheck
  public void that04() {
    Check check = Check.that(5, "fooArg").is(nGreaterThan(), 4);
    assertEquals(IntCheck.class, check.getClass());
  }

  @Test
  public void that05() {
    Check<Integer, IllegalArgumentException> check = Check.that(9, "fooArg");
    assertEquals(IntCheck.class, check.getClass());
  }

  @Test
  public void that06() {
    Check<Integer, IllegalArgumentException> check = Check.that(Integer.valueOf(9), "fooArg");
    assertEquals(ObjectCheck.class, check.getClass());
  }

  @Test(expected = IllegalArgumentException.class)
  public void that07() {
    Check.that(2, "fooArg").is(nGreaterThan(), 4);
  }

  @Test
  public void that08() {
    Check.that(Integer.valueOf(5), "fooArg").is(nGreaterThan(), 3);
  }

  @Test
  public void that09() {
    IntCheck<IllegalArgumentException> check = (IntCheck) Check.that(9, "fooArg");
    check.is(nGreaterThan(), 4);
  }

  @Test
  public void that10() {
    IntCheck<IllegalArgumentException> check = (IntCheck) Check.that(9, "fooArg");
    check.is(greaterThan(), Integer.valueOf(4));
  }

  @Test
  public void that11() {
    // Gotcha here : intstanceOf() takes an object so forces the int argument to be boxed.
    IntCheck<IllegalArgumentException> check = (IntCheck) Check.that(9, "fooArg");
    check.is(instanceOf(), Integer.class);
  }

  @Test
  public void that12() {
    // numGreaterThan() works with Number instances, not ints, but the compiler
    // can make sense of it
    Check.that(9, "fooArg").is(nGreaterThan(), 8);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void size01() {
    Check.that(new File("bla"), "fooArg").is(sizeGreaterThan(), 5);
  }

  @Test
  public void size02() {
    Check.that("Hello, World!", "fooArg").is(sizeGreaterThan(), 6);
  }

  @Test
  public void size03() {
    Check.that("Hello, World!", "fooArg").is(sizeGreaterThan(), Integer.valueOf(6));
  }

  @Test(expected = IllegalArgumentException.class)
  public void size04() {
    Check.that("Hello, World!", "fooArg").is(sizeGreaterThan(), 100);
  }

  @Test
  public void size05() {
    Check.that(List.of("a", "b", "c", "d", "e"), "fooArg").is(sizeAtLeast(), 3);
  }

  @Test
  public void size06() {
    Check.that("Hello, World!", "fooArg").is(sizeAtLeast(), 3);
  }

  @Test
  public void size07() {
    Check.that("Hello, World!", "fooArg").has(stringLength(), greaterThan(), 3);
  }

  @Test
  public void size08() {
    Check.that(List.of("a", "b", "c", "d", "e"), "fooArg").has(size(), atMost(), 10);
  }

  @Test
  public void size09() {
    Collection<String> c = List.of("a", "b", "c", "d", "e");
    Check.that(c, "fooArg").has(size(), atMost(), 10);
  }

  @Test // Does this all "work"? (i.e. no compile errors)
  public void and01() {
    Employee employee = new Employee(3, "John Smith", 43, "Skating", "Scoccer");
    Check.notNull(employee, "employee")
        .has(Employee::getId, "id", atLeast(), 0)
        .has(Employee::getId, "id", (int x, int y) -> x > y, 0)
        .has(Employee::getHobbies, "hobbies", (x, y) -> x.contains(y), "Skating")
        .has(Employee::getHobbies, Collection::contains, "Scoccer", "Scoccer required hobby")
        .has(Employee::getHobbies, (x, y) -> x.contains(y), "Skating", "Skating is not optional")
        .has(Employee::getFullName, "fullName", s -> s.length() < 200)
        .has(Employee::getAge, atLeast(), 16, "Employee must be at least %d", 16)
        .has(Employee::getAge, nAtLeast(), 16, "Employee must be at least %d", 16)
        .ok();
  }

  @Test(expected = IllegalArgumentException.class)
  public void and02() {
    Employee employee = new Employee();
    employee.setAge(12);
    Check.notNull(employee, "employee").has(Employee::getAge, "age", atLeast(), 16).ok();
  }

  @Test(expected = IOException.class)
  public void and03() throws IOException {
    Employee employee = new Employee();
    employee.setHobbies(List.of("Skating", "Scuba diving"));
    Check.notNull(employee, "employee", IOException::new)
        .has(Employee::getHobbies, Collection::contains, "Scoccer", "Scoccer required hobby")
        .ok();
  }

  @Test(expected = IOException.class)
  public void and04() throws IOException {
    Employee employee = new Employee();
    employee.setId(-23);
    Check.notNull(employee, "employee", IOException::new)
        .has(Employee::getId, greaterThan(), 0, "Id must not be negative")
        .ok();
  }

  @Test
  public void and05() {
    Employee employee = new Employee();
    employee.setJustSomeNumbers(new float[] {3.2F, 103.2F, 0.8F});
    Check.notNull(employee, "employee")
        .has(Employee::getJustSomeNumbers, "justSomeLuckyNumbers", anArray());
  }

  @Test(expected = IllegalArgumentException.class)
  public void and06() {
    Employee employee = new Employee();
    employee.setId(7);
    Check.notNull(employee, "employee").has(Employee::getId, "id", anArray());
  }

  @Test
  public void and07() {
    Employee[] employees = new Employee[10];
    Check.notNull(employees, "employees").has(Array::getLength, "length", lessThan(), 100);
  }

  @Test
  public void and08() {
    Employee employee = new Employee();
    employee.setJustSomeNumbers(new float[] {3.2F, 103.2F, 0.8F});
    Check.notNull(employee, "employee")
        .has(Employee::getJustSomeNumbers, "justSomeLuckyNumbers", sizeLessThan(), 100)
        .has(Employee::getJustSomeNumbers, "justSomeLuckyNumbers", x -> x.length != 100);
  }

  @Test
  public void asInt01() {
    Employee employee = new Employee(3, "John Smith", 43, "Skating", "Scoccer");
    Check.notNull(employee, "employee")
        .has(Employee::getId, "id", (Integer x) -> x != 2)
        .has(Employee::getId, (Integer x) -> x != 2, "id must not be 2")
        .has(Employee::getId, objNotEquals(), 2, "id must not be 2")
        .has(Employee::getId, "id", objNotEquals(), 2);
  }

  @Test
  public void asInt02() {
    Employee employee = new Employee(3, "John Smith", 43, "Skating", "Scoccer");
    Check.notNull(employee, "employee")
        .has(Employee::getId, (int x) -> x > 0, "Id must be positive")
        .has(Employee::getId, "id", positive());
  }
}
