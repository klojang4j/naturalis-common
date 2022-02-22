package nl.naturalis.common.check;

import nl.naturalis.common.collection.ArrayCloakList;
import nl.naturalis.common.function.Relation;
import org.junit.Test;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import static java.time.DayOfWeek.*;
import static nl.naturalis.common.ArrayMethods.pack;
import static nl.naturalis.common.ArrayMethods.packInts;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.check.CommonGetters.*;
import static nl.naturalis.common.check.Range.*;

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
    public void intPredicate00() {
        Check.that(3, "foo").is(even());
    }

    @Test(expected = IllegalArgumentException.class)
    public void lambdaAsIntPredicate00() {
        Check.that(3).is(asInt(i -> i != 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void lambdaAsIntPredicate01() {
        Check.that(3, "foo").is((IntPredicate) i -> i != 3);
    }

    private static boolean notEqualsThree(int i) {
        return i != 3;
    }

    @Test(expected = IllegalArgumentException.class)
    public void methodReferenceAsIntPredicate00() {
        Check.that(3, "foo").is(asInt(CheckTest::notEqualsThree));
    }

    @Test(expected = IllegalArgumentException.class)
    public void methodReferenceAsIntPredicate01() {
        Check.that(3, "foo").is((IntPredicate) CheckTest::notEqualsThree);
    }

    @Test
    public void intPredicate01() {
        Check.that(4).is(even());
        Check.that(4).isNot(odd());
        Check.that(4).is(positive());
        Check.that(0).isNot(positive());
        Check.that(0).isNot(negative());
        Check.that(-3).is(negative());
        Check.that(-3).isNot(positive());
        Check.that(0).is(zero());
        Check.that(1).isNot(zero());
    }

    @Test(expected = IllegalArgumentException.class)
    public void relation00() {
        Check.that(Float.valueOf(7.5F)).is(greaterThan(), Short.valueOf((short) 16));
    }

    @Test(expected = IllegalArgumentException.class)
    public void lambdaAsRelation00() {
        Check.that("Foo").is(objObj((x, y) -> x.contains(y)), "Bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void methodReferenceAsRelation00() {
        Check.that("Foo").is(objObj(String::contains), "Bar");
    }

    @Test
    public void relation01() {
        Map<String, Object> map = new HashMap<>();
        map.put("Greeting", "HelloWorld");
        // Check.that(map).is((x, y) -> x.containsKey(y), "Hello World"); // WON'T COMPILE !
        // Check.that(map).is(Map::containsKey, "Greeting"); // WON'T COMPILE !
        Check.that(map).is(containingKey(), "Greeting");
        Check.that(map).is((Map<String, Object> x, String y) -> x.containsKey(y), "Greeting");
        Check.that(map).is(objObj((x, y) -> x.containsKey(y)), "Greeting");
        Check.that(map).is(objObj(Map::containsKey), "Greeting");
        Check.that(map).is((Relation<Map<String, Object>, String>) Map::containsKey, "Greeting");
    }

    @Test
    public void relation02() {
        Check.that(String.class).is(instanceOf(), CharSequence.class);
        Check.that(String.class).is(instanceOf(), CharSequence.class);
        Check.that(CharSequence.class).isNot(instanceOf(), String.class);
        Check.that(42).isNot(instanceOf(), String.class);
        Check.that(Set.of("1", "2", "3")).is(containing(), "2");
        Check.that(Set.of("1", "2", "3")).isNot(containing(), "4");
        Check.that(2).is(in(), List.of(1, 2, 3));
        Check.that(4).isNot(in(), List.of(1, 2, 3));
        Check.that(Set.of("1", "2", "3")).is(supersetOf(), List.of("1", "2"));
        Check.that(Set.of("1", "4", "5")).isNot(supersetOf(), List.of("1", "2"));
        Check.that(Set.of(MONDAY, TUESDAY, WEDNESDAY))
             .is(subsetOf(), List.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY));
        Check.that(Set.of(MONDAY, TUESDAY, SATURDAY))
             .isNot(subsetOf(), List.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY));
        Map<Integer, Integer> map = Map.of(1, 1, 2, 4, 3, 6, 4, 8, 5, 10);
        Check.that(map).is(containingKey(), 1);
        Check.that(map).isNot(containingKey(), 11);
        Check.that(map).is(containingValue(), 4);
        Check.that(map).isNot(containingValue(), 7);
        Check.that(5).is(keyIn(), map);
        Check.that(7).isNot(valueIn(), map);
        Check.that(7).is(elementOf(), pack(1, 7, 10));
        Check.that("Hello").is(equalTo(), new String("Hello"));
        Check.that("Hello").isNot(sameAs(), new String("Hello"));
        Check.that("Hello").is(equalsIgnoreCase(), "HELLO");
        Check.that(null).is(nullOr(), Boolean.TRUE);
        Check.that(true).is(nullOr(), Boolean.TRUE);
        Check.that(7.23F).is(greaterThan(), (byte) 2);
        Check.that(7.230F).is(atMost(), 7.230F);
        Check.that((short) 17).is(lessThan(), (byte) 31);
        Check.that((short) 17).is(atLeast(), (byte) 17);
        Check.that("hello").isNot(startsWith(), "foo");
        Check.that("hello").is(endsWith(), "lo");
        Check.that("hello").is(contains(), "lo");
    }


    @Test(expected = IllegalArgumentException.class)
    public void lambdaAsObjIntRelation00() {
        Check.that("Foo").is(objInt((x, y) -> x.length() > y), 7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void lambdaAsIntObjRelation00() {
        Check.that(7).is(intObj((x, y) -> y.length() > x), "Foo");
    }

    public void intObjRelation00() {
        Check.that(7).is(intElementOf(), packInts(3, 5, 7, 9));
        Check.that(7).isNot(inRange(), from(3, 7));
        Check.that(7).is(inRange(), closed(7, 7));
        Check.that(7).is(inRange(), inside(6, 8));
    }

    @Test(expected = IllegalArgumentException.class)
    public void lambdaAsIntRelation() {
        Check.that(7).is(intInt((x, y) -> x > y), 9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void intRelation00() {
        Check.that(7).is(gt(), 9);
    }

    @Test
    public void intRelation01() {
        Check.that(9).is(gt(), 7);
        Check.that(9).is(gte(), 9);
        Check.that(9).is(eq(), 9);
        Check.that(9).is(ne(), 11);
        Check.that(9).is(lt(), 11);
        Check.that(11).is(lte(), 11);
    }

    @Test
    public void hasPredicate00() {
        Check.that(List.of(1, 2, 3, 4)).has(l -> l.isEmpty(), no());
        Check.that(List.of(1, 2, 3, 4)).notHas(List::isEmpty, asObj(x -> false));
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

    @Test
    public void hasRelation00() {
        Check.that(List.of(1, 2, 3, 4)).has(l -> l.subList(1, 3), equalTo(), List.of(2, 3));
        Check.that(MONDAY).has(stringValue(), startsWith(), "MON");
        Check.that(MONDAY).has(stringValue(), (x, y) -> x.startsWith(y), "MON");
        Check.that("Foo").notHas(type(), equalTo(), int.class);
        // TODO, Houston, we have a problem here
        // Check.that(42).has(type(), equalTo(), int.class);
    }

    @Test
    public void hasIntRelation00() {
        Check.that(List.of(1, 2, 3, 4)).has(size(), eq(), 4);
        Check.that(List.of(1, 2, 3, 4)).notHas(size(), lt(), 1);
        Check.that(packInts(1, 2, 3, 4, 5)).has(length(), gt(), 3);
        Check.that(-42).has(abs(), gt(), 40);
        Check.that(SUNDAY).has(ordinal(), eq(), 6);
    }

    @Test
    public void hasIntObjRelation00() {
        Check.that(List.of(1, 2, 3, 4)).has(size(), intElementOf(), packInts(2, 4, 6));
        Check.that(List.of(1, 2, 3, 4)).has(size(), inRange(), from(3, 10));
    }

    @Test
    public void hasObjIntRelation00() {
        Check.that("foo").has(s -> s.substring(1), strlenEquals(), 2);
        Check.that("foo").has(s -> s + s, strlenNotEquals(), 3);
        Check.that("foo").has(toUpperCase(), strlenGreaterThan(), 2);
        Check.that("foo").has(toLowerCase(), strlenAtLeast(), 3);
    }
}
