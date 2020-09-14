package nl.naturalis.common;

import java.util.function.IntPredicate;

public class Foo {

  public static void main(String[] args) {
    IntPredicate ip = (i) -> i > 5;
    if (ip.test(3)) {
      System.out.println("Test succeded for " + ip);
    } else {
      System.out.println("Test failed for " + ip);
    }
  }
}
