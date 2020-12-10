package nl.naturalis.common.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.BeforeClass;
import org.junit.Test;

public class REZipOutputStreamTest {

  private static byte[] THE_BEGINNING;
  private static byte[] ADAM_AND_EVE;
  private static byte[] THE_FALL;

  @BeforeClass
  public static void beforeClass() throws IOException, URISyntaxException {
    Class<?> c = REZipOutputStreamTest.class;
    THE_BEGINNING = Files.readAllBytes(Path.of(c.getResource("The Beginning.txt").toURI()));
    ADAM_AND_EVE = Files.readAllBytes(Path.of(c.getResource("Adam And Eve.txt").toURI()));
    THE_FALL = Files.readAllBytes(Path.of(c.getResource("The Fall.txt").toURI()));
  }

  @Test
  public void test01() throws IOException {
    File archive = Path.of(System.getProperty("user.home"), "genesis.zip").toFile();
    FileOutputStream fos = new FileOutputStream(archive);
    BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
    try (REZipOutputStream rezos =
        REZipOutputStream.withMainEntry("The Beginning", bos)
            .addEntry("Adam and Eve", 100)
            .addEntry("The Fall", 100)
            .build()) {
      rezos.write(THE_BEGINNING);
      rezos.setActiveEntry("Adam and Eve");
      rezos.write(ADAM_AND_EVE);
      rezos.setActiveEntry("The Fall");
      rezos.write(THE_FALL);
      rezos.mergeEntries().close();
    }
  }

  @Test // Example provided in class comments of REZipOutputStream
  public void example() throws IOException {
    File archive = Path.of(System.getProperty("user.home"), "genesis.zip").toFile();
    FileOutputStream fos = new FileOutputStream(archive);
    BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
    try (REZipOutputStream rezos =
        REZipOutputStream.withMainEntry("The Beginning.txt", bos)
            .addEntry("Adam and Eve.txt")
            .addEntry("The Fall.txt")
            .build()) {
      rezos.write("In the beginning there was nothing".getBytes());
      rezos.setActiveEntry("Adam and Eve.txt");
      rezos.write("Then came Adam & Eve".getBytes());
      rezos.setActiveEntry("The Fall.txt");
      rezos.write("It all went downhill from there".getBytes());
      rezos.mergeEntries().close();
    }
    // Now we have our archive
  }
}
