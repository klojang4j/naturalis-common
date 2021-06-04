package nl.naturalis.common.collection;

import java.io.OutputStream;
import java.util.function.Function;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("unused")
public class TypeSetTest {

  public static interface CanMove {};

  public static interface CanSwim extends CanMove {};

  public static interface CanFly extends CanMove {};

  public static interface CanWalk extends CanMove {};

  public static interface CanRun extends CanWalk {};

  public static interface IsGreen {};

  public static interface LaysEggs {};

  public static class Organism {};

  public static class Animal extends Organism {};

  public static class Plant extends Organism {};

  public static class Reptile extends Animal implements LaysEggs {};

  public static class Bird extends Animal {};

  public static class Mammal extends Animal {};

  public static class Insect extends Animal {};

  public static class AppleTree extends Plant {};

  public static class HummingBird extends Bird implements CanFly, IsGreen {};

  public static class SeaGull extends Bird implements CanFly, CanSwim, CanWalk {};

  public static class Penguin extends Bird implements CanSwim, CanWalk {};

  public static class Bat extends Mammal implements CanFly {};

  public static class Platypus extends Mammal implements CanSwim, LaysEggs {};

  public static class Dolphin extends Mammal implements CanSwim {};

  public static class Feline extends Mammal implements CanWalk {};

  public static class Cat extends Feline implements CanRun {};

  public static class Lion extends Feline implements CanRun {};

  public static class Jaguar extends Feline implements CanRun {};

  public static class Crocodile extends Reptile implements CanSwim, CanWalk {};

  public static class Lizard extends Reptile implements CanWalk {};

  public static class Chameleon extends Lizard implements IsGreen {};

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
        Lizard.class,
        Plant.class,
        Feline.class,
        Bat.class,
        LaysEggs.class,
        Chameleon.class,
        CanRun.class,
        AppleTree.class,
        Lion.class,
        IsGreen.class,
        CanWalk.class,
        Jaguar.class,
        CanFly.class,
        Crocodile.class,
        Bird.class,
        Cat.class,
        CanSwim.class,
        SeaGull.class,
        Animal.class);
    for (Class<?> c : ts) {
      System.out.println(c.getSimpleName());
    }
  }
}
