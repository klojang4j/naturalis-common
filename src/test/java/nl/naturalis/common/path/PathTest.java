package nl.naturalis.common.path;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
    assertEquals("01", 4, path.size());
    assertEquals("02", "identifications", path.segment(0));
    assertEquals("03", "awk^", path.segment(1));
    assertEquals("04", "ward", path.segment(2));
    assertEquals("05", "scientificName", path.segment(3));
  }

  @Test
  public void parse03() {
    Path path = new Path("identifications.^^^..scientificName");
    assertEquals("01", 3, path.size());
    assertEquals("02", "identifications", path.segment(0));
    assertEquals("03", "^.", path.segment(1));
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
    assertEquals("01", 3, path.size());
    assertEquals("02", "identifications", path.segment(0));
    assertEquals("03", "^awk^^ward^", path.segment(1));
    assertEquals("04", "scientificName", path.segment(2));
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
    assertNull("03", path.segment(1));
    assertEquals("04", "scientificName", path.segment(2));
  }

  @Test
  public void parse08() {
    Path path = new Path("identifications.^^^0.scientificName");
    assertEquals("01", 3, path.size());
    assertEquals("02", "identifications", path.segment(0));
    assertEquals("03", "^^0", path.segment(1));
    assertEquals("04", "scientificName", path.segment(2));
  }

  @Test
  public void parse09() {
    Path path = new Path("identifications.^^^0.^0");
    assertEquals("01", 3, path.size());
    assertEquals("02", "identifications", path.segment(0));
    assertEquals("03", "^^0", path.segment(1));
    assertNull("04", path.segment(2));
  }

  @Test
  public void getPurePath() {
    Path path = new Path("identifications.0.scientificName.fullScientificName");
    assertEquals("01", "identifications.scientificName.fullScientificName", path.getPurePath().toString());
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

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void subpath01() {
    Path p = new Path("identifications.0.scientificName");
    p.subpath(3);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void subpath02() {
    Path p = new Path("identifications.0.scientificName");
    p.subpath(2, 4);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void subpath03() {
    Path p = new Path("identifications.0.scientificName");
    p.subpath(-1, 3);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void subpath04() {
    Path p = new Path("identifications.0.scientificName");
    p.subpath(3, 3);
  }

  public void subpath05() {
    Path p = new Path("identifications.0.scientificName");
    assertEquals("01", new Path("scientificName"), p.subpath(2, 3));
    assertEquals("02", new Path("0.scientificName"), p.subpath(1, 3));
    assertEquals("03", new Path("identifications.0.scientificName"), p.subpath(0, 3));
    assertEquals("04", new Path("0.scientificName"), p.subpath(1));
  }

}
