package nl.naturalis.common.check;

import nl.naturalis.common.IOMethods;
import nl.naturalis.common.collection.ArrayCloakList;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static nl.naturalis.common.check.CommonChecks.*;

import static nl.naturalis.common.ArrayMethods.pack;
import static org.junit.Assert.*;

public class MsgPredicateTest {

  @Test(expected = IllegalArgumentException.class)
  public void lambdaAsPredicate00() {
    String foo = null;
    Check.that(foo, "foo").is(s -> s != null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void methodReferenceAsPredicate00() {
    String foo = null;
    Check.that(foo, "foo").is(Objects::nonNull);
  }

  @Test
  public void predicate01() {
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

  @Test
  public void null00() {
    try {
      Check.on(indexOutOfBounds(), "???", "helium").is(NULL());
    } catch (IndexOutOfBoundsException e) {
      System.out.println(e.getMessage());
      assertEquals("helium must be null (was ???)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void null01() {
    try {
      Check.on(indexOutOfBounds(), null, "helium").isNot(NULL());
    } catch (IndexOutOfBoundsException e) {
      System.out.println(e.getMessage());
      assertEquals("helium must not be null", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notNull00() {
    try {
      Check.that(null, "plutonium").is(notNull());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("plutonium must not be null", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void notNull01() {
    try {
      Check.that("???", "plutonium").isNot(notNull());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("plutonium must be null (was ???)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void yes00() {
    try {
      Check.that(false, "oxygen").is(yes());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("oxygen must be true", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void yes01() {
    try {
      Check.that(true, "oxygen").isNot(yes());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("oxygen must not be true", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void no00() {
    try {
      Check.that(true, "carbon").is(no());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("carbon must be false", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void no01() {
    try {
      Check.that(false, "carbon").isNot(no());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("carbon must not be false", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void empty00() {
    try {
      Check.that(List.of(1F), "iron").is(empty());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("iron must be null or empty (was List12[1] of [1.0])", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void empty01() {
    try {
      Check.that("", "iron").isNot(empty());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("iron must not be null or empty (was \"\")", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void deepNotNull00() {
    try {
      Check.that(pack("foo", null, "bar"), "gold").is(deepNotNull());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "gold must not be null or contain null values (was String[3] of [foo, null, bar])",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void deepNotNull01() {
    try {
      Check.that(pack("foo", "bar"), "gold").isNot(deepNotNull());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("gold must be null or contain null values (was String[2] of [foo, bar])",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void deepNotEmpty00() {
    try {
      Check.that(pack("foo", "", "bar"), "silver").is(deepNotEmpty());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals(
          "silver must not be empty or contain empty values (was String[3] of [foo, \"\", bar])",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void deepNotEmpty01() {
    try {
      Check.that(pack("foo", "bar"), "silver").isNot(deepNotEmpty());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("silver must be empty or contain empty values (was String[2] of [foo, bar])",
          e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void blank00() {
    try {
      Check.that("foo", "nitrogen").is(blank());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("nitrogen must be null or blank (was foo)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void blank01() {
    try {
      Check.that("  ", "nitrogen").isNot(blank());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("nitrogen must not be null or blank (was \"  \")", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void integer00() {
    try {
      Check.that("foo", "calcium").is(integer());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("calcium must be parsable as integer (was foo)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void integer10() {
    try {
      Check.that("42", "calcium").isNot(integer());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("calcium must not be parsable as integer (was 42)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void array00() {
    try {
      Check.that("foo", "copper").is(array());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("copper must be an array (was java.lang.String)", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void array01() {
    try {
      Check.that(new double[8], "copper").isNot(array());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("copper must not be an array (was double[])", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void file00() throws IOException {
    File f = IOMethods.createTempDir();
    try {
      Check.that(f, "lithium").is(file());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("lithium must not be a directory (was " + f + ")", e.getMessage());
      return;
    } finally {
      f.delete();
    }
    fail();
  }

  @Test
  public void file01() throws IOException {
    File f = new File("/bla/bla/bar.foo");
    try {
      Check.that(f, "lithium").is(file());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("File lithium must exist (was /bla/bla/bar.foo)", e.getMessage());
      return;
    } finally {
      f.delete();
    }
    fail();
  }

  @Test
  public void file02() throws IOException {
    File f = IOMethods.createTempFile(getClass(), true);
    try {
      Check.that(f, "lithium").isNot(file());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("File lithium must not exist (was " + f + ")", e.getMessage());
      return;
    } finally {
      f.delete();
    }
    fail();
  }

  @Test
  public void directory00() throws IOException {
    File f = IOMethods.createTempFile();
    try {
      Check.that(f, "thorium").is(directory());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("thorium must not be a directory (was " + f + ")", e.getMessage());
      return;
    } finally {
      f.delete();
    }
    fail();
  }

  @Test
  public void directory01() throws IOException {
    File f = new File("/bla/bla/bar.foo");
    try {
      Check.that(f, "thorium").is(directory());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("directory thorium must exist (was /bla/bla/bar.foo)", e.getMessage());
      return;
    } finally {
      f.delete();
    }
    fail();
  }

  @Test
  public void directory02() throws IOException {
    File f = IOMethods.createTempDir();
    try {
      Check.that(f, "thorium").isNot(directory());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("directory thorium must not exist (was " + f + ")", e.getMessage());
      return;
    } finally {
      f.delete();
    }
    fail();
  }

  @Test
  public void fileExists00() throws IOException {
    File f = new File("/bla/foo/bla/bar");
    try {
      Check.that(f, "xenon").is(fileExists());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("xenon must exist (was " + f + ")", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void fileExists01() throws IOException {
    File f = IOMethods.createTempFile();
    try {
      Check.that(f, "xenon").isNot(fileExists());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("xenon must not exist (was " + f + ")", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void readable00() throws IOException {
    File f = new File("/bla/foo/bla/bar");
    try {
      Check.that(f, "krypton").is(readable());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("krypton must be readable (was " + f + ")", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void readable01() throws IOException {
    File f = IOMethods.createTempFile();
    try {
      Check.that(f, "krypton").isNot(readable());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("krypton must not be readable (was " + f + ")", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void readable02() throws IOException {
    File f = IOMethods.createTempDir();
    try {
      Check.that(f, "krypton").isNot(readable());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("krypton must not be readable (was " + f + ")", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void writable00() throws IOException {
    File f = new File("/bla/foo/bla/bar");
    try {
      Check.that(f, "argon").is(writable());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("argon must be writable (was " + f + ")", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void writable01() throws IOException {
    File f = IOMethods.createTempFile();
    try {
      Check.that(f, "argon").isNot(writable());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("argon must not be writable (was " + f + ")", e.getMessage());
      return;
    }
    fail();
  }

  @Test
  public void writable02() throws IOException {
    File f = IOMethods.createTempDir();
    try {
      Check.that(f, "argon").isNot(writable());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      assertEquals("argon must not be writable (was " + f + ")", e.getMessage());
      return;
    }
    fail();
  }

}
