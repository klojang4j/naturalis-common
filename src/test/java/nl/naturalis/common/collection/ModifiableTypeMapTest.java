package nl.naturalis.common.collection;

import java.io.OutputStream;
import java.util.function.Function;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModifiableTypeMapTest {

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
    ModifiableTypeMap<String> map = new ModifiableTypeMap<>();
    map.put(Integer.class, "integer");
    map.put(Number.class, "number");
    assertEquals("integer", map.get(Integer.class));
    assertEquals("number", map.get(Short.class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void test01() {
    ModifiableTypeMap<String> map = new ModifiableTypeMap<>();
    map.put(Number.class, "number");
    map.put(Integer.class, "integer");
  }

  @Test
  public void test02() {
    ModifiableTypeMap<String> map = new ModifiableTypeMap<>();
    map.put(Object.class, "object");
    assertTrue(map.containsKey(OutputStream.class));
    assertTrue(map.containsKey(Function.class));
    assertTrue(map.containsKey(CanSwim.class));
    assertTrue(map.containsKey(Enum.class));
  }

  @Test
  public void test03() {
    ModifiableTypeMap<String> map = new ModifiableTypeMap<>();
    map.put(Chameleon.class, "Chameleon");
    map.put(Lizard.class, "Lizard");
    map.put(Crocodile.class, "Crocodile");
    map.put(Jaguar.class, "Jaguar");
    map.put(Lion.class, "Lion");
    map.put(Cat.class, "Cat");
    map.put(Feline.class, "Feline");
    map.put(Dolphin.class, "Dolphin");
    map.put(Bat.class, "Bat");
    map.put(Platypus.class, "Platypus");
    map.put(Penguin.class, "Penguin");
    map.put(SeaGull.class, "SeaGull");
    map.put(HummingBird.class, "HummingBird");
    map.put(Insect.class, "Insect");
    map.put(Mammal.class, "Mammal");
    map.put(Plant.class, "Plant");
    map.put(Animal.class, "Animal");
    map.put(Organism.class, "Organism");
    map.put(LaysEggs.class, "LaysEggs");
    map.put(IsGreen.class, "IsGreen");
    map.put(CanRun.class, "CanRun");
    map.put(CanWalk.class, "CanWalk");
    map.put(CanFly.class, "CanFly");
    map.put(CanSwim.class, "CanSwim");
    map.put(CanMove.class, "CanMove");
    map.put(Object.class, "Object");
    // map.keySet().forEach(k -> System.out.print(k.getSimpleName() + ' '));
  }

  @Test
  public void test04() {
    ModifiableTypeMap<String> map = new ModifiableTypeMap<>();
    map.put(Animal.class, "Animal");
    map.put(IsGreen.class, "IsGreen");
    assertEquals("Animal", map.get(HummingBird.class));
  }
}
