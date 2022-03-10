package nl.naturalis.common.check;

import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.collection.ArrayCloakList;
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;

import static java.time.DayOfWeek.*;
import static nl.naturalis.common.ArrayMethods.*;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.*;

@SuppressWarnings({"rawtypes"})
public class CheckTest {

  @Test(expected = IllegalArgumentException.class)
  public void predicate00() {
    String foo = null;
    Check.that(foo, "foo").is(notNull());
  }

  @Test(expected = IllegalArgumentException.class)
  public void predicate01() {
    String foo = null;
    Check.that(foo, "foo").isNot(NULL());
  }

  @Test(expected = IllegalArgumentException.class)
  public void lambdaAsPredicate00() {
    String foo = null;
    // Check.that(foo, "foo").is((s -> s != null); // WON'T COMPILE!
    // Option 1: provide type of lambda parameter
    Check.that(foo, "foo").is((String s) -> s != null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void lambdaAsPredicate01() {
    String foo = null;
    // Option 2: use CommonChecks.asObj
    Check.that(foo, "foo").is(asObj(s -> s != null));
  }

  @Test(expected = IllegalArgumentException.class)
  public void lambdaAsPredicate02() {
    String foo = null;
    // Option 2: hard-cast the lambda
    Check.that(foo, "foo").is((Predicate<String>) s -> s != null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void methodReferenceAsPredicate00() {
    String foo = null;
    // Check.that(foo, "foo").is(Objects::nonNull); // Won't compile!
    // Option 1:  use CommonChecks.asObj
    Check.that(foo, "foo").is(asObj(Objects::nonNull));
  }

  @Test(expected = IllegalArgumentException.class)
  public void methodReferenceAsPredicate01() {
    String foo = null;
    // Option 2: hard-cast the method reference
    Check.that(foo, "foo").is((Predicate<String>) Objects::nonNull);
  }

  @Test
  public void predicate02() {
    Check.that(List.of("foo")).isNot(empty());
    Check.that(List.of()).is(empty());
    Check.that(List.of()).is(deepNotNull());
    Check.that(List.of()).isNot(deepNotEmpty());
    Check.that(List.of(1, 2, 3)).isNot(empty());
    Check.that(List.of(1, 2, 3)).is(deepNotNull());
    Check.that(List.of(1, 2, 3)).is(deepNotEmpty());
    Check.that(ArrayCloakList.create(String.class, null, null, null)).isNot(empty());
    Check.that(ArrayCloakList.create(String.class, null, null, null)).isNot(deepNotNull());
    Check.that(ArrayCloakList.create(String.class, null, null, null)).isNot(deepNotEmpty());
    Check.that("foo").isNot(blank());
    Check.that("   ").is(blank());
    Check.that(List.of().isEmpty()).is(yes());
    Check.that(List.of(1, 2, 3).isEmpty()).is(no());
    Check.that(true).isNot(no());
    Check.that(Boolean.TRUE).is(yes());
    Check.that(Boolean.FALSE).isNot(yes());
    Check.that("foo").isNot(integer());
    Check.that("123").is(integer());
    Check.that("abc").isNot((String s) -> s.endsWith("xyz"));
    Check.that(new int[10]).is(array());
    Check.that(float[].class).is(array());
    Check.that("foo").isNot(array());
    Check.that(List.class).isNot(array());
  }

  @Test(expected = IllegalArgumentException.class)
  public void relation00() {
    Check.that(Float.valueOf(7.5F)).is(GT(), 16F);
  }

  @Test(expected = IllegalArgumentException.class)
  public void lambdaAsRelation00() {
    Check.that("Foo").is(objObj((x, y) -> x.contains(y)), "Bar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void methodReferenceAsRelation00() {
    Check.that("Foo").is(objObj(String::contains), "Bar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void lambdaAsObjIntRelation00() {
    Check.that("Foo").is(objInt((x, y) -> x.length() > y), 7);
  }

  @Test
  public void hasPredicate00() {
    Check.that(List.of(1, 2, 3, 4)).has(l -> l.isEmpty(), no());
    Check.that(List.of(1, 2, 3, 4)).notHas(List::isEmpty, asObj(x -> false));
  }

  @Test(expected = IllegalArgumentException.class)
  public void hasPredicate01() {
    Check.that(List.of(1, 2, 3, 4)).has(l -> l.isEmpty(), yes());
  }

  @Test
  public void hasIntPredicate00() {
    Check.that(List.of(1, 2, 3, 4)).has(size(), even());
    Check.that(List.of(1, 2, 3, 4)).has(List::size, even());
    // Check.that(List.of(1, 2, 3, 4)).has(List::size, x -> x % 2 == 0); Ambiguous method call
    Check.that(List.of(1, 2, 3, 4)).has(List::size, asInt(x -> x % 2 == 0));
    Check.that(List.of(1, 2, 3, 4)).has(size(), x -> x % 2 == 0);
    Check.that(List.of(1, 2, 3, 4)).notHas(size(), odd());
    Check.that(List.of()).has(size(), zero());
    Check.that(List.of(1, 2, 3, 4)).notHas(size(), negative());
    Check.that("FOO").has(strlen(), positive());
    Check.that("").has(strlen(), zero());
  }

  @Test(expected = IllegalArgumentException.class)
  public void hasIntPredicate01() {
    Check.that(List.of(1, 2, 3, 4)).has(size(), odd());
  }

  @Test
  public void hasRelation00() {
    Check.that(List.of(1, 2, 3, 4)).has(l -> l.subList(1, 3), EQ(), List.of(2, 3));
    Check.that(MONDAY).has(strval(), startsWith(), "MON");
    Check.that(MONDAY).has(strval(), (x, y) -> x.startsWith(y), "MON");
    Check.that("Foo").notHas(type(), EQ(), int.class);
    // TODO, Houston, we have a problem here
    // Check.that(42).has(type(), equalTo(), int.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void hasRelation01() {
    Check.that(MONDAY).has(strval(), startsWith(), "TUE");
  }

  @Test
  public void hasIntRelation00() {
    Check.that(List.of(1, 2, 3, 4)).has(size(), eq(), 4);
    Check.that(List.of(1, 2, 3, 4)).notHas(size(), lt(), 1);
    Check.that(ArrayMethods.pack(1, 2, 3, 4, 5)).has(length(), gt(), 3);
    Check.that(-42).has(abs(), gt(), 40);
    Check.that(SUNDAY).has(ordinal(), eq(), 6);
  }

  @Test(expected = IllegalArgumentException.class)
  public void hasIntRelation01() {
    Check.that(List.of(1, 2, 3, 4)).has(size(), eq(), 5);
  }

  @Test
  public void hasIntObjRelation00() {
    Check.that(pack("foo", "bar", "baz")).notHas(length(), intElementOf(), ints(2, 4, 6));
    Check.that(List.of(1, 2, 3, 4)).has(size(), intElementOf(), ints(2, 4, 6));
  }

  @Test(expected = IllegalArgumentException.class)
  public void hasIntObjRelation01() {
    Check.that(List.of(1, 2, 3)).has(size(), intElementOf(), ints(2, 4, 6));
  }

  @Test
  public void hasObjIntRelation00() {
    Check.that("foo").has(s -> s.substring(1), strlenEQ(), 2);
    Check.that("foo").has(toUpperCase(), strlenGT(), 2);
    Check.that("foo").has(toLowerCase(), strlenGTE(), 3);
    Check.that("foo").notHas(toUpperCase(), strlenLTE(), 2);
    Check.that("foo").notHas(toUpperCase(), strlenLT(), 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void hasObjIntRelation01() {
    Check.that("foo").has(s -> s.substring(1), strlenEQ(), 6);
  }
}
