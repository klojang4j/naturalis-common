package nl.naturalis.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectMethodsTest {

  @Test
  public void testEquals() {
    assertTrue("01", ObjectMethods.equals("", null));
    assertTrue("02", ObjectMethods.equals(null, ""));
    assertTrue("03", ObjectMethods.equals(null, new Enum[0]));
    assertTrue("04", ObjectMethods.equals(new int[0], null));
    assertTrue("05", ObjectMethods.equals(new String[0], null));
    assertTrue("06", ObjectMethods.equals(new String[0], null));
    assertTrue("07", ObjectMethods.equals(Collections.emptyList(), null));
    assertTrue("08", ObjectMethods.equals(null, new HashSet<>()));
    assertTrue("09", ObjectMethods.equals(null, null));
    assertTrue("10", ObjectMethods.equals("", ""));
    assertTrue("11", ObjectMethods.equals(List.of(1, 2, 3, 4, 5), List.of(1, 2, 3, 4, 5)));
    assertTrue("12", ObjectMethods.equals(new String[] {"To", "be", "or", "not"}, new String[] {"To", "be", "or", "not"}));
    assertTrue("13", ObjectMethods.equals(new int[] {1, 2, 3, 4, 5}, new int[] {1, 2, 3, 4, 5}));
    assertFalse("14", ObjectMethods.equals(new int[0], new HashSet<>()));
    assertFalse("15", ObjectMethods.equals("", new HashSet<>()));
  }

  @Test // implicitly also tests ObjectMethods.hashCode
  public void testHash() {
    assertEquals("01",
        ObjectMethods.hash(null, 5, "hallo", null),
        ObjectMethods.hash(null, 5, "hallo", null));
    assertEquals("02",
        ObjectMethods.hash(null, 5, "hallo", null),
        ObjectMethods.hash(Collections.emptyList(), 5, "hallo", Collections.emptySet()));
    assertEquals("03",
        ObjectMethods.hash(null, 5, "hallo", null),
        ObjectMethods.hash(new int[0], 5, "hallo", new Enum[0]));
    // Ouch, surprise (but true):
    assertEquals("04",
        ObjectMethods.hash(Collections.emptyList(), 5, "hallo", Collections.emptySet()),
        ObjectMethods.hash(new int[0], 5, "hallo", new Enum[0]));
    assertEquals("05", 0, ObjectMethods.hash());
    assertEquals("06", 0, ObjectMethods.hash((Object[]) null));
  }

}
