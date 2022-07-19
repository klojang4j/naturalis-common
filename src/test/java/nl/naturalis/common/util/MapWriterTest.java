package nl.naturalis.common.util;

import java.time.LocalDate;

import org.junit.Test;
import nl.naturalis.common.util.MapWriter.PathOccupiedException;

import static org.junit.Assert.assertEquals;

public class MapWriterTest {

  @Test
  public void testWrite00() {
    MapWriter mw = new MapWriter();
    mw.set("person.address.street", "12 Revolutionay Rd.")
        .set("person.address.state", "CA")
        .set("person.firstName", "John")
        .set("person.lastName", "Smith")
        .set("person.born", LocalDate.of(1967, 4, 4));
    String expected =
        "{person={address={street=12 Revolutionay Rd., state=CA}, firstName=John, lastName=Smith,"
            + " born=1967-04-04}}";
    assertEquals(expected, mw.getMap().toString());
  }

  @Test // Are we OK with null values?
  public void testWrite01() {
    MapWriter mw = new MapWriter();
    mw.set("person.address.street", "12 Revolutionay Rd.")
        .set("person.address.state", null)
        .set("person.firstName", "John")
        .set("person.lastName", null)
        .set("person.born", LocalDate.of(1967, 4, 4));
    String expected =
        "{person={address={street=12 Revolutionay Rd., state=null}, firstName=John, "
            + "lastName=null, born=1967-04-04}}";
    assertEquals(expected, mw.getMap().toString());
  }

  @Test(expected = PathOccupiedException.class)
  public void testWrite02() {
    MapWriter mw = new MapWriter();
    mw.set("person.address.street", "12 Revolutionay Rd.")
        .set("person.address.street.foo", "bar");
  }

  @Test
  public void testAt00() {
    MapWriter mw = new MapWriter();
    mw.in("person")
        .set("firstName", "John")
        .set("lastName", "Smith")
        .set("born", LocalDate.of(1967, 4, 4))
        .in("address")
        .set("street", "12 Revolutionay Rd.")
        .set("state", "CA");
    String expected =
        "{person={firstName=John, lastName=Smith, born=1967-04-04, address={street=12 "
            + "Revolutionay Rd., state=CA}}}";
    assertEquals(expected, mw.getMap().toString());
  }

}
