package nl.naturalis.common.function;

@FunctionalInterface
public interface IntRelation {

  public static final IntRelation GT = (arg0, arg1) -> arg0 > arg1;
  public static final IntRelation GTE = (arg0, arg1) -> arg0 >= arg1;
  public static final IntRelation LT = (arg0, arg1) -> arg0 < arg1;
  public static final IntRelation LTE = (arg0, arg1) -> arg0 <= arg1;

  boolean exists(int arg0, int arg1);
}
