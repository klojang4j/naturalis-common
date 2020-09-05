package nl.naturalis.common.function;

@FunctionalInterface
public interface IntRelation {

  public static final IntRelation EQUALS = (arg0, arg1) -> arg0 == arg1;
  public static final IntRelation NOT_EQUALS = (arg0, arg1) -> arg0 != arg1;
  public static final IntRelation GT = (arg0, arg1) -> arg0 > arg1;
  public static final IntRelation GTE = (arg0, arg1) -> arg0 >= arg1;
  public static final IntRelation LT = (arg0, arg1) -> arg0 < arg1;
  public static final IntRelation LTE = (arg0, arg1) -> arg0 <= arg1;
  public static final IntRelation DIVISIBLE_BY = (arg0, arg1) -> arg0 % arg1 == 0;

  boolean exists(int arg0, int arg1);
}
