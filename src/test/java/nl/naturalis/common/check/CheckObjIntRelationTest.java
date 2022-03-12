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
    Check.that("1234567890", "john").is(strlenEQ(), 10);
    Check.that("1234567890", "john").is(strlenGT(), 9);
    Check.that("1234567890", "john").is(strlenGTE(), 10);
    Check.that("1234567890", "john").is(strlenLTE(), 10);
    Check.that("1234567890", "john").is(strlenLT(), 11);
  }

  @Test
  public void objIntRelation01() {
    Check.that(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "fred").is(sizeEQ(), 10);
    Check.that(Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "fred").is(sizeGT(), 9);
    Check.that(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "fred").is(sizeGTE(), 10);
    Check.that(Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "fred").is(sizeLTE(), 10);
    Check.that(Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "fred").is(sizeLT(), 11);
  }

  @Test
  public void objIntRelation02() {
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "michael").is(lenEQ(), 10);
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "michael").is(lenGT(), 9);
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "michael").is(lenGTE(), 10);
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "michael").is(lenLTE(), 10);
    Check.that(ints(1, 2, 3, 4, 5, 6, 7, 8, 9, 0), "michael").is(lenLT(), 11);
  }
}
