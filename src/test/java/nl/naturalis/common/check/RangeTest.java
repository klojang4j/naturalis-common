package nl.naturalis.common.check;

import org.junit.Test;

import static org.junit.Assert.*;

public class RangeTest {

    @Test
    public void from00() {
        Range r = Range.from(8, 8);
        assertFalse(r.contains(8));
        assertEquals(8, r.getDeclaredLowerBound());
        assertEquals(8, r.getDeclaredUpperBound());
        r = Range.from(8, 9);
        assertTrue(r.contains(8));
        assertEquals(8, r.getDeclaredLowerBound());
        assertEquals(9, r.getDeclaredUpperBound());
    }

    @Test
    public void closed00() {
        Range r = Range.closed(8, 8);
        assertTrue(r.contains(8));
        assertEquals(8, r.getDeclaredLowerBound());
        assertEquals(8, r.getDeclaredUpperBound());
    }

    @Test
    public void inside00() {
        Range r = Range.inside(8, 8);
        assertFalse(r.contains(8));
        assertEquals(8, r.getDeclaredLowerBound());
        assertEquals(8, r.getDeclaredUpperBound());
        r = Range.inside(8, 9);
        assertFalse(r.contains(8));
        assertEquals(8, r.getDeclaredLowerBound());
        assertEquals(9, r.getDeclaredUpperBound());
        r = Range.inside(7, 9);
        assertTrue(r.contains(8));
        assertEquals(7, r.getDeclaredLowerBound());
        assertEquals(9, r.getDeclaredUpperBound());
    }
}
