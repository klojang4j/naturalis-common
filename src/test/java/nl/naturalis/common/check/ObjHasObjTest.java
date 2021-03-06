package nl.naturalis.common.check;

import org.junit.Test;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;

import static nl.naturalis.common.check.CommonChecks.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static nl.naturalis.common.check.CommonGetters.*;

public class ObjHasObjTest {

  private record Person(String firstName, LocalDate birtDate) {}

  @Test
  public void vanilla00() throws IOException {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p, "person").has(Person::firstName, notNull());
    Check.that(p, "person").has(Person::firstName, "firstName", notNull());
    Check.that(p, "person").has(Person::firstName, notNull(), "Any message you like bro");
    Check.that(p, "person").has(Person::firstName, notNull(), () -> new IOException());
    Check.that(p, "person").has(type(), EQ(), Person.class);
    Check.that(p, "person").has(type(), "class", EQ(), Person.class);
    Check.that(p, "person").has(type(), EQ(), Person.class, "Any message you like bro");
    Check.that(p, "person").has(type(), EQ(), Person.class, () -> new IOException());
    Check.that(p, "person").has(Person::firstName, EQ(), "john");
    Check.that(p, "person").has(Person::birtDate, LT(), LocalDate.of(2000, 1, 1));
    Check.that(DayOfWeek.class).has(constants(), lenEQ(), 7);
  }

  @Test
  public void vanilla01() throws IOException {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p, "person").notHas(Person::firstName, blank());
    Check.that(p, "person").notHas(Person::firstName, "class", blank());
    Check.that(p, "person").notHas(Person::firstName, blank(), "Any message you like ${0}", "bro");
    Check.that(p, "person").notHas(Person::firstName, blank(), () -> new IOException());
    Check.that(p, "person").notHas(type(), EQ(), String.class);
    Check.that(p, "person").notHas(type(), "class", EQ(), String.class);
    Check.that(p, "person").notHas(type(), EQ(), String.class, "Any message you like ${0}", "bro");
    Check.that(p, "person").notHas(type(), EQ(), String.class, () -> new IOException());
    Check.that(p, "person").notHas(Person::firstName, EQ(), "jim");
    Check.that(p, "person").notHas(Person::birtDate, LT(), LocalDate.of(1900, 1, 1));
  }

  @Test
  public void hasPredicate00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person").has(Person::firstName, NULL());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Function.apply(person) must be null (was john)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHasPredicate00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person").notHas(Person::firstName, notNull());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Function.apply(person) must be null (was john)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void has_Name_Predicate00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person").has(Person::firstName, "firstName", NULL());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("person.firstName must be null (was john)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHas_Name_Predicate00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person").notHas(Person::firstName, "firstName", notNull());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("person.firstName must be null (was john)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void has_Predicate_CustomMsg00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p).has(Person::firstName, NULL(), "Bad stuff");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Bad stuff", e.getMessage());
      return;
    }
    fail();
  }

  @Test(expected = IOException.class)
  public void notHas_Predicate_CustomMsg00() throws IOException {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p).notHas(Person::firstName, notNull(), () -> new IOException());
  }

  @Test(expected = IOException.class)
  public void has_Predicate_CustomExc00() throws IOException {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p, "person").has(Person::firstName, NULL(), () -> new IOException());
  }

  @Test
  public void notHas_Predicate_CustomExc00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person").notHas(Person::firstName, notNull(), "Failed test ${test}");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Failed test notNull", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void has_Relation00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person").has(Person::birtDate, GT(), LocalDate.of(2000, 1, 1));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Function.apply(person) must be > 2000-01-01 (was 1966-04-22)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHas_Relation00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person").notHas(Person::firstName, EQ(), "john");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Function.apply(person) must not equal john", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void has_Name_Relation00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person").has(Person::birtDate, "birtDate", GT(), LocalDate.of(2000, 1, 1));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("person.birtDate must be > 2000-01-01 (was 1966-04-22)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHas_Name_Relation00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person").notHas(Person::firstName, "firstName", EQ(), "john");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("person.firstName must not equal john", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void has_Relation_CustomMsg00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person")
          .has(Person::birtDate, GT(), LocalDate.of(2000, 1, 1), "Bad birth date: ${obj}");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Bad birth date: 2000-01-01", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHas_Relation_CustomMsg00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person").notHas(Person::firstName, EQ(), "john", "Bad person: ${arg}");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Bad person: john", e.getMessage());
      return;
    }
    fail();
  }

  @Test(expected = IOException.class)
  public void has_Relation_CustomExc00() throws IOException {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p, "person")
        .has(Person::birtDate, GT(), LocalDate.of(2000, 1, 1), () -> new IOException());
  }

  @Test(expected = IOException.class)
  public void notHas_Relation_CustomExc01() throws IOException {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p, "person").notHas(Person::firstName, EQ(), "john", () -> new IOException());
  }

  @Test
  public void has_ObjIntRelation00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person").has(Person::firstName, strlenGT(), 100);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Function.apply(person) must be > 100 (was john)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHas_ObjIntRelation00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that("1234567890", "person").notHas(s -> s.substring(2), strlenGT(), 3);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Function.apply(person) must not be > 3 (was 34567890)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void has_Name_ObjIntRelation00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person").has(Person::firstName, "firstName", strlenGT(), 100);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("person.firstName must be > 100 (was john)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHas_Name_ObjIntRelation00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that("1234567890", "person").notHas(s -> s.substring(2), "substring2", strlenGT(), 3);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("person.substring2 must not be > 3 (was 34567890)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void has_ObjIntRelation_CustomMsg00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p).has(Person::firstName, strlenGT(), 100, "First name = ${arg}");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("First name = john", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHas_ObjIntRelation_CustomMsg00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p).notHas(Person::firstName, strlenLT(), 100, "Min length = ${obj}");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Min length = 100", e.getMessage());
      return;
    }
    fail();
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void has_ObjIntRelation_CustomExc00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p).has(Person::firstName, strlenGT(), 100, () -> new IndexOutOfBoundsException());
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void notHas_ObjIntRelation_CustomExc00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p).notHas(Person::firstName, strlenLT(), 100, () -> new IndexOutOfBoundsException());
  }

  @Test
  public void testLambdas() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p).has(x -> x.firstName(), objObj((x, y) -> x.equals(y)), "john");
    Check.that(p).has(x -> x.firstName().length(), intInt((x, y) -> x == y), 4);
    Check.that(p).has(toInt(x -> x.firstName().length()), intInt((x, y) -> x == y), 4);
    Check.that(p).has(toInt(x -> x.firstName().length()), (x, y) -> x == y, 4);
    Check.that(p).has(objToObj(x -> x.firstName()), objObj((x, y) -> x.equals(y)), "john");
    Check.that(p).has(objToObj(x -> x.firstName()), (x, y) -> x.equals(y), "john");
    Check.that(p).has(Person::firstName, (x, y) -> x.equals(y), "john");
  }
}
