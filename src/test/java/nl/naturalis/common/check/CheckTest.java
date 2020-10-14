package nl.naturalis.common.check;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.size;
import static nl.naturalis.common.check.CommonGetters.stringLength;

/**
 * NB A lot of these tests don't make any assertion, but just verify that we can code them as we do
 * in the first place without the compiler warning about ambiguities. The "that" static factory
 * methods and the "and" instance methods are so heavily overloaded that we had to help the compiler
 * determining the type of the lambdas. (E.g. That's why we have the "andAsInt" methods.)
 */
@SuppressWarnings({"rawtypes"})
public class CheckTest {

  @Test(expected = IOException.class)
  public void that01() throws IOException {
    Check.that(3 > 5, () -> new IOException());
  }

  @Test
  public void that02() throws IOException {
    Check.that(5 > 3, () -> new IOException());
    assertTrue(true);
  }

  @Test
  public void that03() {
    Check check = Check.that(3, "fooArg").is(notNull());
    assertEquals(IntCheck.class, check.getClass());
  }

  @Test
  public void that04() {
    Check check = Check.that(5, "fooArg");
    assertEquals(IntCheck.class, check.getClass());
  }

  @Test
  public void that05() {
    Check check = Check.that(Integer.valueOf(9), "fooArg");
    assertEquals(ObjectCheck.class, check.getClass());
  }

  // greaterThan() works with Number instances, not ints, but the compiler is fine with it
  @Test(expected = IllegalArgumentException.class)
  public void greaterThan01() {
    Check.that(2, "fooArg").is(greaterThan(), 4);
    assertTrue(true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void greaterThan02() {
    Check.that(2, "fooArg").is(greaterThan(), Integer.valueOf(4));
    assertTrue(true);
  }

  // gt() works with ints, not Number instances, but the compiler is fine with it
  @Test
  public void gt01() {
    Check.that(Integer.valueOf(6), "fooArg").is(gt(), Integer.valueOf(4));
    assertTrue(true);
  }

  @Test
  public void gt02() {
    Check.that(Integer.valueOf(6), "fooArg").is(gt(), 4);
    assertTrue(true);
  }

  /*
   * Gotcha: intstanceOf() takes an object so forces the int argument to be boxed. In other words
   * it's pointless and misleading to do an instanceOf check on an int argument.
   */
  @Test(expected = IllegalArgumentException.class)
  public void instanceOf01() {
    Check.that(9, "fooArg").is(instanceOf(), int.class);
  }

  @Test
  public void instanceOf02() {
    Check.that(9, "fooArg").is(instanceOf(), Integer.class);
    assertTrue(true);
  }

  /*
   * The sizeXXX checks are special because they work on weakly typed arguments (Object) and
   * validate them programmatically
   */
  @Test(expected = UnsupportedOperationException.class)
  public void size01() {
    Check.that(new Object(), "fooArg").is(sizeGreaterThan(), 5);
  }

  @Test
  public void size02() {
    Check.that("Hello, World!", "fooArg").is(sizeGreaterThan(), 6);
    assertTrue(true);
  }

  @Test
  public void size03() {
    Check.that("Hello, World!", "fooArg").is(sizeGreaterThan(), Integer.valueOf(6));
    assertTrue(true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void size04() {
    Check.that("Hello, World!", "fooArg").is(sizeGreaterThan(), 100);
    assertTrue(true);
  }

  @Test
  public void size05() {
    Check.that(List.of("a", "b", "c", "d", "e"), "fooArg").is(sizeAtLeast(), 3);
    assertTrue(true);
  }

  @Test
  public void size06() {
    Check.that("Hello, World!", "fooArg").is(sizeAtLeast(), 3);
  }

  @Test
  public void size07() {
    Check.that("Hello, World!", "fooArg").has(stringLength(), gt(), 3);
    assertTrue(true);
  }

  @Test
  public void size08() {
    Check.that(List.of("a", "b", "c", "d", "e"), "fooArg").has(size(), lte(), 10);
    assertTrue(true);
  }

  @Test
  public void size09() {
    Collection<String> c = List.of("a", "b", "c", "d", "e");
    Check.that(c, "fooArg").has(size(), lte(), 10);
    assertTrue(true);
  }

  /**
   * The point here is that we can write these tests down in the first place without compilation
   * errors. Note the parameter typing in one of the lambdas, which we were forced to do by the
   * compiler.
   */
  @Test
  public void has01() {
    Employee employee = new Employee(3, "John Smith", 43, "Skating", "Scoccer");
    Check.notNull(employee, "employee")
        .has(Employee::getId, "id", gte(), 0)
        .has(Employee::getId, "id", (int x, int y) -> x > y, 0)
        .has(Employee::getHobbies, "hobbies", (x, y) -> x.contains(y), "Skating")
        .has(Employee::getHobbies, Collection::contains, "Scoccer", "Scoccer required hobby")
        .has(Employee::getHobbies, (x, y) -> x.contains(y), "Skating", "Skating is not optional")
        .has(Employee::getFullName, "fullName", s -> s.length() < 200)
        .has(Employee::getAge, gte(), 16, "Employee must be at least %d", 16)
        .has(Employee::getAge, atLeast(), 16, "Employee must be at least %d", 16)
        .ok();
    assertTrue(true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void has02() {
    Employee employee = new Employee();
    employee.setAge(12);
    Check.notNull(employee, "employee").has(Employee::getAge, "age", gte(), 16).ok();
    assertTrue(true);
  }

  @Test(expected = IOException.class)
  public void has03() throws IOException {
    Employee employee = new Employee();
    employee.setHobbies(List.of("Skating", "Scuba diving"));
    Check.notNull(employee, "employee", IOException::new)
        .has(Employee::getHobbies, Collection::contains, "Scoccer", "Scoccer required hobby")
        .ok();
    assertTrue(true);
  }

  @Test(expected = IOException.class)
  public void has04() throws IOException {
    Employee employee = new Employee();
    employee.setId(-23);
    Check.notNull(employee, "employee", IOException::new)
        .has(Employee::getId, gt(), 0, "Id must not be negative")
        .ok();
    assertTrue(true);
  }

  @Test
  public void has05() {
    Employee employee = new Employee();
    employee.setJustSomeNumbers(new float[] {3.2F, 103.2F, 0.8F});
    Check.notNull(employee, "employee")
        .has(Employee::getJustSomeNumbers, "justSomeLuckyNumbers", array());
    assertTrue(true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void has06() {
    Employee employee = new Employee();
    employee.setId(7);
    Check.notNull(employee, "employee").has(Employee::getId, "id", array());
    assertTrue(true);
  }

  @Test
  public void has07() {
    Employee[] employees = new Employee[10];
    Check.notNull(employees, "employees").has(Array::getLength, "length", lt(), 100);
    assertTrue(true);
  }

  @Test
  public void has08() {
    Employee employee = new Employee();
    employee.setJustSomeNumbers(new float[] {3.2F, 103.2F, 0.8F});
    Check.notNull(employee, "employee")
        .has(Employee::getJustSomeNumbers, "justSomeLuckyNumbers", sizeLessThan(), 100)
        .has(Employee::getJustSomeNumbers, "justSomeLuckyNumbers", x -> x.length != 100);
    assertTrue(true);
  }

  /*
   * Again note the forced typing of lambda parameters. It doesn't even really matter whether you
   * choose int or Integer. The compiler must apparently just get a starting point from where it can
   * start boxing/unboxing,
   */
  @Test
  public void hasInt() {
    Employee employee = new Employee(3, "John Smith", 43, "Skating", "Scoccer");
    Check.notNull(employee, "employee")
        .has(Employee::getId, "id", (Integer x) -> x != 2)
        .has(Employee::getId, (Integer x) -> x != 2, "id must not be 2")
        .has(Employee::getId, notEqualTo(), 2, "id must not be 2")
        .has(Employee::getId, (int x) -> x > 0, "Id must be positive")
        .has(Employee::getId, "id", positive())
        .has(Employee::getId, "id", notEqualTo(), 2);
  }
}
