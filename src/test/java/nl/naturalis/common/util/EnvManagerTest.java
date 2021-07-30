package nl.naturalis.common.util;

import static nl.naturalis.common.util.EnvManager.EmptyValue.DEFAULT;
import static nl.naturalis.common.util.EnvManager.EmptyValue.EMPTY;
import static nl.naturalis.common.util.EnvManager.EmptyValue.UNDEFINED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class EnvManagerTest {

  private final Map<String, String> env = new HashMap<>();

  public void before() {
    env.clear();
  }

  @Test
  public void getEmptyIsEmpty01() {
    env.put("FOO", "Hello World");
    env.put("BAR", "");
    EnvManager em = new EnvManager(EMPTY, env);
    assertEquals("01", "Hello World", em.get("FOO").get());
    assertEquals("02", "", em.get("BAR").get());
    assertTrue("03", em.get("XXX").isEmpty());
  }

  @Test
  public void getEmptyIsAbsent01() {
    env.put("FOO", "Hello World");
    env.put("BAR", "");
    EnvManager em = new EnvManager(UNDEFINED, env);
    assertEquals("01", "Hello World", em.get("FOO").get());
    assertTrue("02", em.get("BAR").isEmpty());
    assertTrue("03", em.get("XXX").isEmpty());
  }

  @Test
  public void getEmptyIsDefault01() {
    env.put("FOO", "Hello World");
    env.put("BAR", "");
    EnvManager em = new EnvManager(DEFAULT, env);
    assertEquals("01", "Hello World", em.get("FOO", "Tokyo"));
    assertEquals("02", "Tokyo", em.get("BAR", "Tokyo"));
    assertEquals("03", "Tokyo", em.get("XXX", "Tokyo"));
  }

  @Test
  public void getAsIntEmptyIsEmpty01() {
    env.put("FOO", "8");
    EnvManager em = new EnvManager(EMPTY, env);
    assertEquals("01", 8, em.getAsInt("FOO").getAsInt());
    assertTrue("03", em.getAsInt("XXX").isEmpty());
  }

  @Test(expected = InvalidEnvironmentException.class)
  public void getAsIntEmptyIsEmpty02() {
    env.put("FOO", "bla");
    EnvManager em = new EnvManager(EMPTY, env);
    em.getAsInt("FOO");
  }

  @Test
  public void getAsIntEmptyIsAbsent01() {
    env.put("FOO", "-123");
    EnvManager em = new EnvManager(UNDEFINED, env);
    assertEquals("01", -123, em.getAsInt("FOO").getAsInt());
  }

  @Test
  public void getAsIntEmptyIsAbsent02() {
    env.put("FOO", "");
    EnvManager em = new EnvManager(UNDEFINED, env);
    assertTrue("01", em.getAsInt("FOO").isEmpty());
    assertEquals("02", 123, em.getAsInt("FOO", 123));
    assertEquals("02", 123, em.getAsInt("XXX", 123));
  }

  @Test
  public void getAsIntEmptyIsDefault01() {
    env.put("FOO", "");
    EnvManager em = new EnvManager(DEFAULT, env);
    assertEquals("01", 123, em.getAsInt("FOO", 123));
    assertEquals("02", 123, em.getAsInt("XXX", 123));
  }

  @Test
  public void getRequiredAsIntEmptyIsEmpty01() {
    env.put("FOO", "8");
    EnvManager em = new EnvManager(EMPTY, env);
    assertEquals("01", 8, em.getRequiredAsInt("FOO"));
  }

  @Test(expected = InvalidEnvironmentException.class)
  public void getRequiredAsIntEmptyIsEmpty02() {
    env.put("FOO", "");
    EnvManager em = new EnvManager(EMPTY, env);
    em.getRequiredAsInt("FOO");
  }

  @Test(expected = InvalidEnvironmentException.class)
  public void getRequiredAsIntEmptyIsEmpty03() {
    EnvManager em = new EnvManager(EMPTY, env);
    em.getRequiredAsInt("XXX");
  }

  @Test(expected = InvalidEnvironmentException.class)
  public void getAsRequiredAsIntEmptyIsAbsent01() {
    env.put("FOO", "");
    EnvManager em = new EnvManager(UNDEFINED, env);
    em.getRequiredAsInt("FOO");
  }

  @Test
  public void getRequiredAsIntEmptyIsDefault01() {
    env.put("FOO", "6");
    EnvManager em = new EnvManager(DEFAULT, env);
    assertEquals("01", 6, em.getRequiredAsInt("FOO"));
  }
}
