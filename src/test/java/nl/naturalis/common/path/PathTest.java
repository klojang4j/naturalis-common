package nl.naturalis.common.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PathTest {

  @Test
  public void path() {
    Path path = new Path("identifications");
    assertEquals("01", 1, path.size());
    path = new Path("identifications.0.scientificName.fullScientificName");
    assertEquals("02", 4, path.size());
    assertEquals("03", "identifications", path.segment(0));
    assertEquals("04", "0", path.segment(1));
    assertEquals("05", "scientificName", path.segment(2));
    assertEquals("06", "fullScientificName", path.segment(3));
  }

  @Test
  public void parse01() {
    Path path = new Path("identifications.awk^.ward.scientificName");
    assertEquals("01", 3, path.size());
    assertEquals("02", "identifications", path.segment(0));
    assertEquals("03", "awk.ward", path.segment(1));
    assertEquals("04", "scientificName", path.segment(2));
  }

  @Test
  public void parse02() {
    Path path = new Path("identifications.awk^^.ward.scientificName");
    assertEquals("01", 3, path.size());
    assertEquals("02", "identifications", path.segment(0));
    assertEquals("03", "awk^.ward", path.segment(1));
    assertEquals("04", "scientificName", path.segment(2));
  }

  @Test
  public void parse03() {
    Path path = new Path("identifications.^^^..scientificName");
    assertEquals("01", 3, path.size());
    assertEquals("02", "identifications", path.segment(0));
    assertEquals("03", "^^.", path.segment(1));
    assertEquals("04", "scientificName", path.segment(2));
  }

  @Test
  public void parse04() {
    Path path = new Path("identifications.awk^ward.scientificName");
    assertEquals("01", 3, path.size());
    assertEquals("02", "identifications", path.segment(0));
    assertEquals("03", "awk^ward", path.segment(1));
    assertEquals("04", "scientificName", path.segment(2));
  }

  @Test
  public void parse05() {
    Path path = new Path("identifications.^awk^^^^ward^^.scientificName");
    assertEquals("01", 2, path.size());
    assertEquals("02", "identifications", path.segment(0));
    assertEquals("03", "^awk^^^^ward^.scientificName", path.segment(1));
  }

  @Test
  public void parse06() {
    Path path = new Path("identifications.^0.scientificName");
    assertEquals("01", 3, path.size());
    assertEquals("02", "identifications", path.segment(0));
    assertNull("03", path.segment(1));
    assertEquals("04", "scientificName", path.segment(2));
  }

  @Test
  public void parse07() {
    Path path = new Path("identifications.^^0.scientificName");
    assertEquals("01", 3, path.size());
    assertEquals("02", "identifications", path.segment(0));
    assertEquals("03", "^^0", path.segment(1));
    assertEquals("04", "scientificName", path.segment(2));
  }

  @Test
  public void parse08() {
    Path path = new Path("identifications.^^^0.scientificName");
    assertEquals("01", 3, path.size());
    assertEquals("02", "identifications", path.segment(0));
    assertEquals("03", "^^^0", path.segment(1));
    assertEquals("04", "scientificName", path.segment(2));
  }

  @Test
  public void parse09() {
    Path path = new Path("identifications.^^^0.^0");
    assertEquals("01", 3, path.size());
    assertEquals("02", "identifications", path.segment(0));
    assertEquals("03", "^^^0", path.segment(1));
    assertNull("04", path.segment(2));
  }

  @Test
  public void escape() {
    String segment = "identifications";
    assertTrue("01", segment == Path.escape(segment));
    assertEquals("02", "identifications^.", Path.escape("identifications."));
    assertEquals("03", "i^.dentifications", Path.escape("i.dentifications"));
    assertEquals("04", "^.identifications", Path.escape(".identifications"));
    assertEquals("05", "^.^identifications", Path.escape(".^identifications"));
    assertEquals("06", "^^.identifications", Path.escape("^.identifications"));
    assertEquals("07", "^0", Path.escape(null));
  }

  @Test
  public void getPurePath() {
    Path path = new Path("identifications.0.scientificName.fullScientificName");
    assertEquals(
            "01",
            "identifications.scientificName.fullScientificName",
            path.getCanonicalPath().toString());
  }

  @Test
  public void append() {
    Path path = new Path("identifications.0");
    assertEquals("01", new Path("identifications.0.scientificName"), path.append("scientificName"));
  }

  @Test
  public void shift() {
    Path path = new Path("identifications.0.scientificName.fullScientificName");
    assertEquals("01", new Path("0.scientificName.fullScientificName"), (path = path.shift()));
    assertEquals("02", new Path("scientificName.fullScientificName"), (path = path.shift()));
    assertEquals("03", new Path("fullScientificName"), (path = path.shift()));
    assertTrue("04", (path = path.shift()) == Path.EMPTY_PATH);
  }

  @Test(expected = IllegalArgumentException.class)
  public void subpath01() {
    Path p = new Path("identifications.0.scientificName");
    p.subpath(3);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void subpath02() {
    Path p = new Path("identifications.0.scientificName");
    p.subpath(2, 4);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void subpath03() {
    Path p = new Path("identifications.0.scientificName");
    p.subpath(-1, 5);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void subpath04() {
    Path p = new Path("identifications.0.scientificName");
    p.subpath(3, 3);
  }

  @Test
  public void subpath05() {
    Path p = new Path("identifications.0.scientificName");
    assertEquals("01", new Path("scientificName"), p.subpath(2, 1));
    assertEquals("02", new Path("0.scientificName"), p.subpath(1, 2));
    assertEquals("03", new Path("identifications.0.scientificName"), p.subpath(0, 3));
    assertEquals("04", new Path("0.scientificName"), p.subpath(1));
  }

  @Test
  public void subpath06() {
    Path p = new Path("identifications.0.scientificName");
    assertEquals("01", new Path("0.scientificName"), p.subpath(-2));
    assertEquals("02", new Path("0"), p.subpath(-2, 1));
    assertEquals("03", Path.EMPTY_PATH, p.subpath(-2, 0));
  }

  @Test
  public void parent01() {
    assertEquals("01", null, Path.EMPTY_PATH.parent());
    Path p = new Path("identifications.0.scientificName");
    assertEquals("02", new Path("identifications.0"), p.parent());
    assertEquals("03", new Path("identifications"), p.parent().parent());
    assertEquals("04", Path.EMPTY_PATH, p.parent().parent().parent());
    assertEquals("05", null, p.parent().parent().parent().parent());
  }
}
