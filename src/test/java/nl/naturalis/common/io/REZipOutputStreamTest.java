package nl.naturalis.common.io;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.junit.BeforeClass;
import org.junit.Test;
import nl.naturalis.common.IOMethods;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class REZipOutputStreamTest {

  private static byte[] THE_BEGINNING;
  private static byte[] ADAM_AND_EVE;
  private static byte[] THE_FALL;

  @BeforeClass
  public static void beforeClass() throws IOException, URISyntaxException {
    Class<?> c = REZipOutputStreamTest.class;
    THE_BEGINNING = Files.readAllBytes(Path.of(c.getResource("The Beginning.txt").toURI()));
    System.out.println("The beginning: " + THE_BEGINNING.length + " bytes");
    ADAM_AND_EVE = Files.readAllBytes(Path.of(c.getResource("Adam And Eve.txt").toURI()));
    System.out.println("Adam And Eve: " + ADAM_AND_EVE.length + " bytes");
    THE_FALL = Files.readAllBytes(Path.of(c.getResource("The Fall.txt").toURI()));
    System.out.println("The Fall: " + THE_FALL.length + " bytes");
  }

  @Test // With small buffers (100)
  public void test01() throws IOException {
    File archive = Path.of(System.getProperty("user.home"), "genesis.zip").toFile();
    FileOutputStream fos = new FileOutputStream(archive);
    BufferedOutputStream bos = new BufferedOutputStream(fos);
    try (REZipOutputStream rezos =
        REZipOutputStream.withMainEntry("The Beginning.txt", bos)
            .addEntry("Adam and Eve.txt", 100)
            .addEntry("The Fall.txt", 100)
            .build()) {
      rezos.write(THE_BEGINNING);
      rezos.setActiveEntry("Adam and Eve.txt");
      rezos.write(ADAM_AND_EVE);
      rezos.setActiveEntry("The Fall.txt");
      rezos.write(THE_FALL);
      rezos.mergeEntries().close();
    }
    try (ZipFile zf = new ZipFile(archive)) {
      ZipEntry ze = zf.getEntry("The Beginning.txt");
      ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
      try (InputStream is = zf.getInputStream(ze)) {
        IOMethods.pipe(is, baos, 10000);
      }
      assertArrayEquals("01", THE_BEGINNING, baos.toByteArray());
      ze = zf.getEntry("Adam and Eve.txt");
      baos.reset();
      try (InputStream is = zf.getInputStream(ze)) {
        IOMethods.pipe(is, baos, 10000);
      }
      assertArrayEquals("02", ADAM_AND_EVE, baos.toByteArray());
      ze = zf.getEntry("The Fall.txt");
      baos.reset();
      try (InputStream is = zf.getInputStream(ze)) {
        IOMethods.pipe(is, baos, 10000);
      }
      assertArrayEquals("03", THE_FALL, baos.toByteArray());
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
