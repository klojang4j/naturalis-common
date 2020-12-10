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
import static nl.naturalis.common.IOMethods.createTempFile;

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
  public void test01() throws IOException, URISyntaxException {
    File f = createTempFile(".bible.zip");
    FileOutputStream fos = new FileOutputStream(f);
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
    }
  }
}
