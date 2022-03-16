package nl.naturalis.common.check;

import org.junit.Test;

import java.util.List;
import java.util.Set;

import static nl.naturalis.common.ArrayMethods.ints;
import static nl.naturalis.common.check.CommonChecks.*;

public class CheckObjIntRelationTest {

  /*
   * There is currently no need to test the messages being generated if the checks fail, because
   * all checks of type ObjIntRelation borrow there messages from other checks: eq(), gt(), gte(),
   * lt() and lte().
   */

  @Test
  public void objIntRelation00() {
    Check.that("1234567890", "strlen").is(strlenEQ(), 10);
    Check.that("1234567890", "strlen").is(strlenGT(), 9);
    Check.that("1234567890", "strlen").is(strlenGTE(), 10);
    Check.that("1234567890", "strlen").is(strlenLTE(), 10);
    Check.that("1234567890", "strlen").is(strlenLT(), 11);
  }

  @Test
  public void not_objIntRelation00() {
    Check.that("1234567890", "strlen").isNot(strlenEQ(), 100);
    Check.that("1234567890", "strlen").isNot(strlenGT(), 15);
    Check.that("1234567890", "strlen").isNot(strlenGTE(), 15);
    Check.that("1234567890", "strlen").isNot(strlenLTE(), 9);
    Check.that("1234567890", "strlen").isNot(strlenLT(), 9);
  }

  @Test
  public void objIntRelation01() {
    Check.that(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "size").is(sizeEQ(), 10);
    Check.that(Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "size").is(sizeGT(), 9);
    Check.that(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "size").is(sizeGTE(), 10);
    Check.that(Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "size").is(sizeLTE(), 10);
    Check.that(Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "size").is(sizeLT(), 11);
  }

  @Test
  public void not_objIntRelation01() {
    Check.that(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "size").isNot(sizeEQ(), 17);
    Check.that(Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "size").isNot(sizeGT(), 15);
    Check.that(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "size").isNot(sizeGTE(), 16);
    Check.that(Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "size").isNot(sizeLTE(), 7);
    Check.that(Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "size").isNot(sizeLT(), 6);
  }

  @Test
  public void objIntRelation02() {
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "length").is(lenEQ(), 10);
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "length").is(lenGT(), 9);
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "length").is(lenGTE(), 10);
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "length").is(lenLTE(), 10);
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "length").is(lenLT(), 11);
  }

  @Test
  public void not_objIntRelation02() {
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "length").isNot(lenEQ(), 0);
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "length").isNot(lenGT(), 27);
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "length").isNot(lenGTE(), 14);
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "length").isNot(lenLTE(), 4);
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "length").isNot(lenLT(), 4);
  }
}
