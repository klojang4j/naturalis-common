package nl.naturalis.common;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static nl.naturalis.common.FileMethods.newFile;

public class FileMethodsTest {

  @Test
  public void newFile1() {
    File dir = new File("/var/log");
    File f = newFile(dir, "nba");
    assertEquals("/var/log/nba", f.getAbsolutePath());
  }

  @Test
  public void newFile2() {
    File dir = new File("/var/log");
    File f = newFile(dir, "nba/output.txt");
    assertEquals("/var/log/nba/output.txt", f.getAbsolutePath());
  }

  @Test
  public void newFile3() {
    File dir = new File("/var/log");
    File f = newFile(dir, "nba", "output.txt");
    assertEquals("/var/log/nba/output.txt", f.getAbsolutePath());
  }
}
