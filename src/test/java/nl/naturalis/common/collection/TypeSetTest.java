package nl.naturalis.common.collection;

import java.io.OutputStream;
import java.util.function.Function;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TypeSetTest {

  private static interface CanMove {};

  private static interface CanSwim extends CanMove {};

  private static interface CanFly extends CanMove {};

  private static interface CanWalk extends CanMove {};

  private static interface CanRun extends CanWalk {};

  private static interface IsGreen {};

  private static interface LaysEggs {};

  private static class Organism {};

  private static class Animal extends Organism {};

  private static class Plant extends Organism {};

  private static class Reptile extends Animal implements LaysEggs {};

  private static class Bird extends Animal {};

  private static class Mammal extends Animal {};

  private static class Insect extends Animal {};

  private static class HummingBird extends Bird implements CanFly, IsGreen {};

  private static class SeaGull extends Bird implements CanFly, CanSwim, CanWalk {};

  private static class Penguin extends Bird implements CanSwim, CanWalk {};

  private static class Bat extends Mammal implements CanFly {};

  private static class Platypus extends Mammal implements CanSwim, LaysEggs {};

  private static class Dolphin extends Mammal implements CanSwim {};

  private static class Feline extends Mammal implements CanWalk {};

  private static class Cat extends Feline implements CanRun {};

  private static class Lion extends Feline implements CanRun {};

  private static class Jaguar extends Feline implements CanRun {};

  private static class Crocodile extends Reptile implements CanSwim, CanWalk {};

  private static class Lizard extends Reptile implements CanWalk {};

  private static class Chameleon extends Lizard implements IsGreen {};

  @Test
  public void test00() {
    TypeSet ts = new TypeSet();
    ts.addTypes(
        Penguin.class,
        Insect.class,
        HummingBird.class,
        Platypus.class,
        CanMove.class,
        Dolphin.class,
        Reptile.class,
        Organism.class,
        Object.class,
        Mammal.class,
        Feline.class,
        Bat.class,
        LaysEggs.class,
        Chameleon.class,
        CanRun.class,
        Lion.class,
        IsGreen.class,
        CanWalk.class,
        Jaguar.class,
        Crocodile.class,
        Plant.class,
        Cat.class,
        CanSwim.class,
        SeaGull.class,
        Animal.class);
    for (Class<?> c : ts) {
      System.out.println(c.getSimpleName());
    }
  }
}
