package nl.naturalis.common.check;

import nl.naturalis.common.function.IntRelation;

public class CheckUtils {

  private CheckUtils() {
    throw new AssertionError();
  }

  public static IntRelation anonymize(IntRelation intRelation) {
    return (i, j) -> intRelation.exists(i, j);
  }

}
