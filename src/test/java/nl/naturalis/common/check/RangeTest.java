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
import static org.junit.Assert.*;

public class RangeTest {

  @Test
  public void from00() {
    Range.Reader r = (Range.Reader) Range.from(8, 8);
    assertFalse(r.isInRange(8));
    assertEquals(8, r.getBounds().one());
    assertEquals(7, r.getBounds().two());
    r = (Range.Reader) Range.from(8, 9);
    assertTrue(r.isInRange(8));
    assertEquals(8, r.getBounds().one());
    assertEquals(8, r.getBounds().two());
  }

  @Test
  public void closed00() {
    Range.Reader r = (Range.Reader) Range.closed(8, 8);
    assertTrue(r.isInRange(8));
    assertEquals(8, r.getBounds().one());
    assertEquals(8, r.getBounds().two());
  }

  @Test
  public void inside00() {
    Range.Reader r = (Range.Reader) Range.inside(8, 8);
    assertFalse(r.isInRange(8));
    assertEquals(9, r.getBounds().one());
    assertEquals(7, r.getBounds().two());
    r = (Range.Reader) Range.inside(8, 9);
    assertFalse(r.isInRange(8));
    assertEquals(9, r.getBounds().one());
    assertEquals(8, r.getBounds().two());
    r = (Range.Reader) Range.inside(7, 9);
    assertTrue(r.isInRange(8));
    assertEquals(8, r.getBounds().one());
    assertEquals(8, r.getBounds().two());
  }
}
