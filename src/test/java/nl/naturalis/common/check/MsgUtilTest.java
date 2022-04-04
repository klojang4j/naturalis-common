package nl.naturalis.common.check;

import org.junit.Test;

import static nl.naturalis.common.check.MsgUtil.*;
import static org.junit.Assert.assertEquals;

public class MsgUtilTest {

  @Test
  public void formatPredicate_simple_00() {
    MsgArgs args = new MsgArgs("integer", false, "robert1", "78ab45", null, null);
    PrefabMsgFormatter formatter = formatPredicate("be parsable as integer", true);
    System.out.println(formatter.apply(args));
    assertEquals("robert1 must be parsable as integer (was 78ab45)", formatter.apply(args));
  }

  @Test
  public void formatPredicate_simple_01() {
    MsgArgs args = new MsgArgs("integer", false, "robert2", "78ab45", null, null);
    PrefabMsgFormatter formatter = formatPredicate("be parsable as integer", false);
    System.out.println(formatter.apply(args));
    assertEquals("robert2 must be parsable as integer", formatter.apply(args));
  }

  @Test
  public void formatPredicate_simple_02() {
    MsgArgs args = new MsgArgs("integer", true, "robert3", "78ab45", null, null);
    PrefabMsgFormatter formatter = formatPredicate("be parsable as integer", true);
    System.out.println(formatter.apply(args));
    assertEquals("robert3 must not be parsable as integer (was 78ab45)", formatter.apply(args));
  }

  @Test
  public void formatPredicate_simple_03() {
    MsgArgs args = new MsgArgs("integer", true, "robert4", "78ab45", null, null);
    PrefabMsgFormatter formatter = formatPredicate("be parsable as integer", false);
    System.out.println(formatter.apply(args));
    assertEquals("robert4 must not be parsable as integer", formatter.apply(args));
  }

  @Test
  public void formatPredicate_affirmative_00() {
    MsgArgs args = new MsgArgs("integer", false, "mariah1", "78ab45", null, null);
    PrefabMsgFormatter formatter = formatPredicate("be parsable as integer", true, true);
    System.out.println(formatter.apply(args));
    assertEquals("mariah1 must be parsable as integer (was 78ab45)", formatter.apply(args));
  }

  @Test
  public void formatPredicate_affirmative_01() {
    MsgArgs args = new MsgArgs("integer", false, "mariah2", "78ab45", null, null);
    PrefabMsgFormatter formatter = formatPredicate("be parsable as integer", true, false);
    System.out.println(formatter.apply(args));
    assertEquals("mariah2 must be parsable as integer (was 78ab45)", formatter.apply(args));
  }

  @Test
  public void formatPredicate_affirmative_02() {
    MsgArgs args = new MsgArgs("integer", false, "mariah3", "78ab45", null, null);
    PrefabMsgFormatter formatter = formatPredicate("be parsable as integer", false, true);
    System.out.println(formatter.apply(args));
    assertEquals("mariah3 must be parsable as integer", formatter.apply(args));
  }

  @Test
  public void formatPredicate_affirmative_03() {
    MsgArgs args = new MsgArgs("integer", false, "mariah4", "78ab45", null, null);
    PrefabMsgFormatter formatter = formatPredicate("be parsable as integer", false, false);
    System.out.println(formatter.apply(args));
    assertEquals("mariah4 must be parsable as integer", formatter.apply(args));
  }

  @Test
  public void formatPredicate_negated_00() {
    MsgArgs args = new MsgArgs("integer", true, "eve1", "78ab45", null, null);
    PrefabMsgFormatter formatter = formatPredicate("be parsable as integer", true, true);
    System.out.println(formatter.apply(args));
    assertEquals("eve1 must not be parsable as integer (was 78ab45)", formatter.apply(args));
  }

  @Test
  public void formatPredicate_negated_01() {
    MsgArgs args = new MsgArgs("integer", true, "eve2", "78ab45", null, null);
    PrefabMsgFormatter formatter = formatPredicate("be parsable as integer", true, false);
    System.out.println(formatter.apply(args));
    assertEquals("eve2 must not be parsable as integer", formatter.apply(args));
  }

  @Test
  public void formatPredicate_negated_02() {
    MsgArgs args = new MsgArgs("integer", true, "eve3", "78ab45", null, null);
    PrefabMsgFormatter formatter = formatPredicate("be parsable as integer", false, true);
    System.out.println(formatter.apply(args));
    assertEquals("eve3 must not be parsable as integer (was 78ab45)", formatter.apply(args));
  }

  @Test
  public void formatPredicate_negated_03() {
    MsgArgs args = new MsgArgs("integer", true, "eve4", "78ab45", null, null);
    PrefabMsgFormatter formatter = formatPredicate("be parsable as integer", false, false);
    System.out.println(formatter.apply(args));
    assertEquals("eve4 must not be parsable as integer", formatter.apply(args));
  }

  @Test
  public void formatNegativePredicate_simple_00() {
    MsgArgs args = new MsgArgs("notNull", false, "jim1", null, int.class, null);
    PrefabMsgFormatter formatter = formatNegativePredicate("be null", false);
    System.out.println(formatter.apply(args));
    assertEquals("jim1 must not be null", formatter.apply(args));
  }

  @Test
  public void formatNegativePredicate00_simple_01() {
    MsgArgs args = new MsgArgs("notNull", false, "jim2", null, int.class, null);
    PrefabMsgFormatter formatter = formatNegativePredicate("be null", false);
    System.out.println(formatter.apply(args));
    assertEquals("jim2 must not be null", formatter.apply(args));
  }

  @Test
  public void formatNegativePredicate_simple_02() {
    MsgArgs args = new MsgArgs("notNull", true, "jim3", null, int.class, null);
    PrefabMsgFormatter formatter = formatNegativePredicate("be null", false);
    System.out.println(formatter.apply(args));
    assertEquals("jim3 must be null", formatter.apply(args));
  }

  @Test
  public void formatNegativePredicate00_simple_03() {
    MsgArgs args = new MsgArgs("notNull", true, "jim4", null, int.class, null);
    PrefabMsgFormatter formatter = formatNegativePredicate("be null", false);
    System.out.println(formatter.apply(args));
    assertEquals("jim4 must be null", formatter.apply(args));
  }

  @Test
  public void formatNegativePredicate_affirmative_00() {
    MsgArgs args = new MsgArgs("notNull", false, "foo1", null, int.class, null);
    PrefabMsgFormatter formatter = formatNegativePredicate("be null", true, true);
    System.out.println(formatter.apply(args));
    assertEquals("foo1 must not be null (was null)", formatter.apply(args));
  }

  @Test
  public void formatNegativePredicate_affirmative_01() {
    MsgArgs args = new MsgArgs("notNull", false, "foo2", null, int.class, null);
    PrefabMsgFormatter formatter = formatNegativePredicate("be null", true, false);
    System.out.println(formatter.apply(args));
    assertEquals("foo2 must not be null (was null)", formatter.apply(args));
  }

  @Test
  public void formatNegativePredicate_affirmative_02() {
    MsgArgs args = new MsgArgs("notNull", false, "foo3", null, int.class, null);
    PrefabMsgFormatter formatter = formatNegativePredicate("be null", false, true);
    System.out.println(formatter.apply(args));
    assertEquals("foo3 must not be null", formatter.apply(args));
  }

  @Test
  public void formatNegativePredicate_affirmative_03() {
    MsgArgs args = new MsgArgs("notNull", false, "foo4", null, int.class, null);
    PrefabMsgFormatter formatter = formatNegativePredicate("be null", false, false);
    System.out.println(formatter.apply(args));
    assertEquals("foo4 must not be null", formatter.apply(args));
  }

  @Test
  public void formatNegativePredicate_negated_00() {
    MsgArgs args = new MsgArgs("notNull", true, "bar1", null, int.class, null);
    PrefabMsgFormatter formatter = formatNegativePredicate("be null", true, true);
    System.out.println(formatter.apply(args));
    assertEquals("bar1 must be null (was null)", formatter.apply(args));
  }

  @Test
  public void formatNegativePredicate_negated_01() {
    MsgArgs args = new MsgArgs("notNull", true, "bar2", null, int.class, null);
    PrefabMsgFormatter formatter = formatNegativePredicate("be null", false, true);
    System.out.println(formatter.apply(args));
    assertEquals("bar2 must be null (was null)", formatter.apply(args));
  }

  @Test
  public void formatNegativePredicate_negated_02() {
    MsgArgs args = new MsgArgs("notNull", true, "bar3", null, int.class, null);
    PrefabMsgFormatter formatter = formatNegativePredicate("be null", true, false);
    System.out.println(formatter.apply(args));
    assertEquals("bar3 must be null", formatter.apply(args));
  }

  @Test
  public void formatNegativePredicate_negated_03() {
    MsgArgs args = new MsgArgs("notNull", true, "bar4", null, int.class, null);
    PrefabMsgFormatter formatter = formatNegativePredicate("be null", false, false);
    System.out.println(formatter.apply(args));
    assertEquals("bar4 must be null", formatter.apply(args));
  }

  @Test
  public void formatRelation_simple_00() {
    MsgArgs args = new MsgArgs("gt", false, "joseph1", 2, int.class, 7);
    PrefabMsgFormatter formatter = formatRelation("be >", true);
    System.out.println(formatter.apply(args));
    assertEquals("joseph1 must be > 7 (was 2)", formatter.apply(args));
  }

  @Test
  public void formatRelation_simple_01() {
    MsgArgs args = new MsgArgs("gt", false, "joseph2", 2, int.class, 7);
    PrefabMsgFormatter formatter = formatRelation("be >", false);
    System.out.println(formatter.apply(args));
    assertEquals("joseph2 must be > 7", formatter.apply(args));
  }

  @Test
  public void formatRelation_simple_02() {
    MsgArgs args = new MsgArgs("gt", true, "joseph3", 2, int.class, 7);
    PrefabMsgFormatter formatter = formatRelation("be >", true);
    System.out.println(formatter.apply(args));
    assertEquals("joseph3 must not be > 7 (was 2)", formatter.apply(args));
  }

  @Test
  public void formatRelation_simple_03() {
    MsgArgs args = new MsgArgs("gt", true, "joseph4", 2, int.class, 7);
    PrefabMsgFormatter formatter = formatRelation("be >", false);
    System.out.println(formatter.apply(args));
    assertEquals("joseph4 must not be > 7", formatter.apply(args));
  }

  @Test
  public void formatRelation_affirmative_00() {
    MsgArgs args = new MsgArgs("gt", false, "vanessa1", 2, int.class, 7);
    PrefabMsgFormatter formatter = formatNegativeRelation("be >", true, true);
    System.out.println(formatter.apply(args));
    assertEquals("vanessa1 must not be > 7 (was 2)", formatter.apply(args));
  }

  @Test
  public void formatRelation_affirmative_01() {
    MsgArgs args = new MsgArgs("gt", false, "vanessa2", 2, int.class, 7);
    PrefabMsgFormatter formatter = formatNegativeRelation("be >", true, false);
    System.out.println(formatter.apply(args));
    assertEquals("vanessa2 must not be > 7 (was 2)", formatter.apply(args));
  }

  @Test
  public void formatRelation_affirmative_02() {
    MsgArgs args = new MsgArgs("gt", false, "vanessa3", 2, int.class, 7);
    PrefabMsgFormatter formatter = formatNegativeRelation("be >", false, true);
    System.out.println(formatter.apply(args));
    assertEquals("vanessa3 must not be > 7", formatter.apply(args));
  }

  @Test
  public void formatRelation_affirmative_03() {
    MsgArgs args = new MsgArgs("gt", false, "vanessa4", 2, int.class, 7);
    PrefabMsgFormatter formatter = formatNegativeRelation("be >", false, false);
    System.out.println(formatter.apply(args));
    assertEquals("vanessa4 must not be > 7", formatter.apply(args));
  }

  @Test
  public void formatNegativeRelation_affirmative_00() {
    MsgArgs args = new MsgArgs("ne", false, "aaron1", 7, int.class, 7);
    PrefabMsgFormatter formatter = formatNegativeRelation("equal", true, true);
    System.out.println(formatter.apply(args));
    assertEquals("aaron1 must not equal 7 (was 7)", formatter.apply(args));
  }

  @Test
  public void formatNegativeRelation_affirmative_01() {
    MsgArgs args = new MsgArgs("ne", false, "aaron2", 7, int.class, 7);
    PrefabMsgFormatter formatter = formatNegativeRelation("equal", true, false);
    System.out.println(formatter.apply(args));
    assertEquals("aaron2 must not equal 7 (was 7)", formatter.apply(args));
  }

  @Test
  public void formatNegativeRelation_affirmative_02() {
    MsgArgs args = new MsgArgs("ne", false, "aaron3", 7, int.class, 7);
    PrefabMsgFormatter formatter = formatNegativeRelation("equal", false, true);
    System.out.println(formatter.apply(args));
    assertEquals("aaron3 must not equal 7", formatter.apply(args));
  }

  @Test
  public void formatNegativeRelation_affirmative_04() {
    MsgArgs args = new MsgArgs("ne", false, "aaron4", 7, int.class, 7);
    PrefabMsgFormatter formatter = formatNegativeRelation("equal", false, false);
    System.out.println(formatter.apply(args));
    assertEquals("aaron4 must not equal 7", formatter.apply(args));
  }

  @Test
  public void formatNegativeRelation_negated_00() {
    MsgArgs args = new MsgArgs("ne", true, "guiseppe1", 7, int.class, 7);
    PrefabMsgFormatter formatter = formatNegativeRelation("equal", true, true);
    System.out.println(formatter.apply(args));
    assertEquals("guiseppe1 must equal 7 (was 7)", formatter.apply(args));
  }

  @Test
  public void formatNegativeRelation_negated_01() {
    MsgArgs args = new MsgArgs("ne", true, "guiseppe2", 7, int.class, 7);
    PrefabMsgFormatter formatter = formatNegativeRelation("equal", true, false);
    System.out.println(formatter.apply(args));
    assertEquals("guiseppe2 must equal 7", formatter.apply(args));
  }

  @Test
  public void formatNegativeRelation_negated_02() {
    MsgArgs args = new MsgArgs("ne", true, "guiseppe3", 7, int.class, 7);
    PrefabMsgFormatter formatter = formatNegativeRelation("equal", false, true);
    System.out.println(formatter.apply(args));
    assertEquals("guiseppe3 must equal 7 (was 7)", formatter.apply(args));
  }

  @Test
  public void formatNegativeRelation_negated_03() {
    MsgArgs args = new MsgArgs("ne", true, "guiseppe4", 7, int.class, 7);
    PrefabMsgFormatter formatter = formatNegativeRelation("equal", false, false);
    System.out.println(formatter.apply(args));
    assertEquals("guiseppe4 must equal 7", formatter.apply(args));
  }

}
