package nl.naturalis.common.check;

import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;

import static nl.naturalis.common.check.CommonChecks.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ObjHasObjTest {

  private record Person(String firstName, LocalDate birtDate) {}

  @Test
  public void hasNamePredicate00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p, "person").has(Person::firstName, "firstName", notNull());
  }

  @Test
  public void notHasNamePredicate00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p, "person").notHas(Person::firstName, "firstName", blank());
  }

  @Test
  public void hasNamePredicate01() {
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
  public void notHasNamePredicate01() {
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
  public void hasNamePredicateCustomMsg00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person").has(Person::firstName, NULL(), "Bad stuff");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Bad stuff", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notHasNamePredicateCustomMsg00() {
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
  public void hasNameRelation00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p, "person").has(Person::firstName, "firstName", EQ(), "john");
    Check.that(p, "person").has(Person::birtDate, "birtDate", LT(), LocalDate.of(2000, 1, 1));
  }

  @Test
  public void notHasNameRelation00() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p, "person").notHas(Person::firstName, "firstName", EQ(), "jim");
    Check.that(p, "person").notHas(Person::birtDate, "birtDate", LT(), LocalDate.of(1900, 1, 1));
  }

  @Test
  public void hasNameRelation01() {
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
  public void notHasNameRelation01() {
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
  public void hasRelationCustomMsg00() {
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
  public void hasRelationCustomMsg01() {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    try {
      Check.that(p, "person").notHas(Person::firstName, EQ(), "john", "Bad person: ${arg}");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("Bad person: Person[firstName=john, birtDate=1966-04-22]", e.getMessage());
      return;
    }
    fail();
  }

  @Test(expected = IOException.class)
  public void hasRelationCustomExc00() throws IOException {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p, "person")
        .has(Person::birtDate, GT(), LocalDate.of(2000, 1, 1), () -> new IOException());
  }

  @Test(expected = IOException.class)
  public void hasRelationCustomExc01() throws IOException {
    Person p = new Person("john", LocalDate.of(1966, 04, 22));
    Check.that(p, "person").notHas(Person::firstName, EQ(), "john", () -> new IOException());
  }
}
