package nl.naturalis.common.path;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import nl.naturalis.common.path.MapWriter.PathBlockedException;
import org.junit.Test;

public class MapWriterTest {

  @Test
  public void testWrite00() {
    MapWriter mw = new MapWriter();
    mw.write("person.address.street", "12 Revolutionay Rd.")
        .write("person.address.state", "CA")
        .write("person.firstName", "John")
        .write("person.lastName", "Smith")
        .write("person.born", LocalDate.of(1967, 4, 4));
    String expected =
        "{person={address={street=12 Revolutionay Rd., state=CA}, firstName=John, lastName=Smith, born=1967-04-04}}";
    assertEquals(expected, mw.getMap().toString());
  }

  @Test // Are we OK with null values?
  public void testWrite01() {
    MapWriter mw = new MapWriter();
    mw.write("person.address.street", "12 Revolutionay Rd.")
        .write("person.address.state", null)
        .write("person.firstName", "John")
        .write("person.lastName", null)
        .write("person.born", LocalDate.of(1967, 4, 4));
    String expected =
        "{person={address={street=12 Revolutionay Rd., state=null}, firstName=John, lastName=null, born=1967-04-04}}";
    assertEquals(expected, mw.getMap().toString());
  }

  @Test(expected = PathBlockedException.class)
  public void testWrite02() {
    MapWriter mw = new MapWriter();
    mw.write("person.address.street", "12 Revolutionay Rd.")
        .write("person.address.street.foo", "bar");
  }

  @Test
  public void testAt00() {
    MapWriter mw = new MapWriter();
    mw.in("person")
        .write("firstName", "John")
        .write("lastName", "Smith")
        .write("born", LocalDate.of(1967, 4, 4))
        .in("address")
        .write("street", "12 Revolutionay Rd.")
        .write("state", "CA");
    String expected =
        "{person={firstName=John, lastName=Smith, born=1967-04-04, address={street=12 Revolutionay Rd., state=CA}}}";
    assertEquals(expected, mw.getMap().toString());
  }
}
