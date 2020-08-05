package nl.naturalis.common.path;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
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
}
