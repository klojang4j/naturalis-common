package nl.naturalis.common.util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import nl.naturalis.common.Result;
import org.junit.Test;
import nl.naturalis.common.util.MapWriter.PathOccupiedException;

import static org.junit.Assert.*;

public class MapWriterTest {

  @Test
  public void set00() {
    MapWriter mw = new MapWriter();
    mw.set("person.address.street", "12 Revolutionary Rd.")
        .set("person.address.state", "CA")
        .set("person.firstName", "John")
        .set("person.lastName", "Smith")
        .set("person.born", LocalDate.of(1967, 4, 4));
    String expected =
        "{person={address={street=12 Revolutionary Rd., state=CA}, firstName=John, lastName=Smith,"
            + " born=1967-04-04}}";
    assertEquals(expected, mw.createMap().toString());
  }

  @Test // Are we OK with null values?
  public void set01() {
    MapWriter mw = new MapWriter();
    mw
        .set("person.address.street", "12 Revolutionary Rd.")
        .set("person.address.state", null)
        .set("person.firstName", "John")
        .set("person.lastName", null)
        .set("person.born", LocalDate.of(1967, 4, 4));
    String expected =
        "{person={address={street=12 Revolutionary Rd., state=null}, firstName=John, "
            + "lastName=null, born=1967-04-04}}";
    assertEquals(expected, mw.createMap().toString());
  }

  @Test(expected = PathOccupiedException.class)
  public void set02() {
    MapWriter mw = new MapWriter();
    mw
        .set("person.address.street", "12 Revolutionary Rd.")
        .set("person.address.street.foo", "bar");
  }

  @Test(expected = PathOccupiedException.class)
  public void set03() {
    MapWriter mw = new MapWriter();
    mw
        .set("person.address.street", null)
        .set("person.address.street", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void set04() {
    MapWriter mw = new MapWriter();
    mw.set("person.address.street", new HashMap<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void set05() {
    MapWriter mw = new MapWriter();
    mw.set("person.address.street", new MapWriter());
  }

  @Test(expected = PathOccupiedException.class)
  public void set06() {
    MapWriter mw = new MapWriter();
    mw
        .set("person.address", "foo")
        .set("person.address.street", "Sunset Blvd");
  }

  @Test
  public void get00() {
    MapWriter mw = new MapWriter();
    mw.set("person.address.street", "foo");
    assertEquals("foo", mw.get("person.address.street").get());
    assertEquals(Map.of("street", "foo"), mw.get("person.address").get());
    assertEquals(Map.of("address", Map.of("street", "foo")), mw.get("person").get());
    assertEquals(Result.none(), mw.get("person.address.street.teapot.coffee"));
    assertEquals(Result.none(), mw.get("person.address.street.teapot"));
    assertEquals(Result.none(), mw.get("person.address.teapot"));
    assertEquals(Result.none(), mw.get("person.teapot"));
    assertEquals(Result.none(), mw.get("teapot"));
  }

  @Test
  public void in00() {
    MapWriter mw = new MapWriter();
    mw.in("person")
        .set("firstName", "John")
        .set("lastName", "Smith")
        .set("born", LocalDate.of(1967, 4, 4))
        .in("address")
        .set("street", "12 Revolutionary Rd.")
        .set("state", "CA");
    String expected =
        "{person={firstName=John, lastName=Smith, born=1967-04-04, address={street=12 "
            + "Revolutionary Rd., state=CA}}}";
    assertEquals(expected, mw.createMap().toString());
  }

  @Test
  public void exit00() {
    MapWriter mw = new MapWriter();
    mw.in("person.address")
        .set("street", "Sunset Blvd")
        .up("person")
        .set("firstName", "John");
    assertEquals("{person={address={street=Sunset Blvd}, firstName=John}}",
        mw.createMap().toString());
  }

  @Test
  public void exit01() {
    MapWriter mw = new MapWriter();
    mw.in("person.address")
        .set("street", "Sunset Blvd")
        .up("person")
        .set("firstName", "John");
    assertEquals("{person={address={street=Sunset Blvd}, firstName=John}}",
        mw.createMap().toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void exit02() {
    MapWriter mw = new MapWriter();
    mw.in("person.address")
        .set("street", "Sunset Blvd")
        .up("teapot");
  }

  @Test(expected = IllegalStateException.class)
  public void exit03() {
    MapWriter mw = new MapWriter();
    mw.up("teaport");

  }

  @Test
  public void exit04() {
    MapWriter mw = new MapWriter();
    mw.in("department.manager.address")
        .set("street", "Sunset Blvd")
        .up("manager")
        .up("department")
        .set("foo", "bar");
    Map<String, Object> expected = Map.of(
        "department",
        Map.of("foo",
            "bar",
            "manager",
            Map.of("address", Map.of("street", "Sunset Blvd")))
    );
    assertEquals(expected, mw.createMap());
  }

  @Test
  public void exit05() {
    MapWriter mw = new MapWriter();
    mw.in("department.manager.address")
        .set("street", "Sunset Blvd")
        .up("manager")
        .up("department")
        .up(null)
        .set("foo", "bar");
    Map<String, Object> expected = Map.of(
        "foo", "bar",
        "department",
        Map.of("manager",
            Map.of("address", Map.of("street", "Sunset Blvd")))
    );
    assertEquals(expected, mw.createMap());
  }

  @Test
  public void exit06() {
    MapWriter mw = new MapWriter();
    mw.in("department.manager.address")
        .set("street", "Sunset Blvd")
        .up("manager")
        .up("department")
        .up(null)
        .set("foo", "bar");
    Map<String, Object> expected = Map.of(
        "foo", "bar",
        "department",
        Map.of("manager",
            Map.of("address", Map.of("street", "Sunset Blvd")))
    );
    assertEquals(expected, mw.createMap());
  }

  @Test
  public void reset00() {
    MapWriter mw = new MapWriter();
    mw.in("person.address")
        .set("street", "Sunset Blvd")
        .reset()
        .set("firstName", "John");
    assertEquals("{person={address={street=Sunset Blvd}}, firstName=John}",
        mw.createMap().toString());
  }

  @Test(expected = IllegalStateException.class)
  public void reset01() {
    MapWriter mw = new MapWriter();
    mw.reset();
  }

  @Test
  public void isSet00() {
    MapWriter mw = new MapWriter();
    mw.set("person.address.street", "foo");
    assertTrue(mw.isSet("person.address.street"));
    assertTrue(mw.isSet("person.address"));
    assertTrue(mw.isSet("person"));
    assertFalse(mw.isSet("teapot"));
    assertFalse(mw.isSet("person.teapot"));
    assertFalse(mw.isSet("person.address.teapot"));
    assertFalse(mw.isSet("person.address.street.teapot"));
  }

  @Test
  public void unset00() {
    MapWriter mw = new MapWriter();
    mw.set("person.address.street", "foo");
    assertTrue(mw.isSet("person.address.street"));
    mw.unset("person.address.street");
    assertFalse(mw.isSet("person.address.street"));
    mw.set("person.address.street", "foo");
    assertTrue(mw.isSet("person.address.street"));
    mw.unset("person.address");
    assertFalse(mw.isSet("person.address"));
    assertFalse(mw.isSet("person.address.street"));
  }

  @Test
  public void sourceMap00() {
    Map<String, Object> source = Map.of("foo",
        Map.of("teapot", "coffee"),
        "bar",
        true);
    MapWriter mw = new MapWriter(source);
    assertEquals(source, mw.createMap());
    mw.set("ping", 1).set("pong", false);
    Map<String, Object> expected = Map.of("foo",
        Map.of("teapot", "coffee"),
        "bar",
        true, "ping", 1, "pong", false);
    assertEquals(expected, mw.createMap());
  }

}
