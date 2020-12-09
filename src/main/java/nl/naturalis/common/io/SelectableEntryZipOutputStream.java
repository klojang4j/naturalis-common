package nl.naturalis.common.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import nl.naturalis.common.Tuple;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.check.CommonChecks.*;

public class SelectableEntryZipOutputStream extends OutputStream {

  private static final String ERR_DUPLICATE_ENTRY = "Duplicate zip entry: %s";

  private ZipEntry mainEntry;
  private ZipOutputStream zipStream;

  private HashMap<String, Tuple<ZipEntry, RecallOutputStream>> streams = new HashMap<>();

  private OutputStream active;

  public SelectableEntryZipOutputStream(OutputStream out, String mainEntry) throws IOException {
    this(out, new ZipEntry(mainEntry));
  }

  public SelectableEntryZipOutputStream(OutputStream out, ZipEntry mainEntry) throws IOException {
    this.mainEntry = mainEntry;
    this.zipStream = new ZipOutputStream(out);
    this.zipStream.putNextEntry(mainEntry);
    this.active = zipStream;
  }

  public void addEntry(String name) {
    // addEntry(new ZipEntry(name), ZipFileSwapOutputStream.newInstance(1024 * 1024));
  }

  /**
   * Adds a new zip entry with the specified name. The in-memory buffer for the entry is swapped to
   * file once it grows beyond the specified {@code size} (in bytes).
   *
   * @param name
   * @param bufSize
   * @throws IOException
   */
  public void addEntry(String name, int bufSize) {
    // addEntry(new ZipEntry(name), ZipFileSwapOutputStream.newInstance(bufSize));
  }

  public void addEntry(ZipEntry entry, RecallOutputStream ros) {
    Check.notNull(entry, "entry");
    Check.notNull(ros, "ros");
    String name = entry.getName();
    Check.that(name)
        .is(notEqualTo(), mainEntry.getName(), ERR_DUPLICATE_ENTRY, name)
        .is(notKeyIn(), streams, ERR_DUPLICATE_ENTRY, name);
    streams.put(name, Tuple.of(entry, ros));
  }

  /**
   * Sets the zip entry for subsequent calls to any of the {@code write} methods. If the specified
   * entry does not exist, it will be created as though by calling {@link #addEntry(String)
   * addEntry}.
   *
   * @param name
   * @throws IOException
   */
  public void setActiveEntry(String name) throws IOException {
    Check.notNull(name);
    if (name.equals(mainEntry.getName())) {
      this.active = this.zipStream;
    } else {
      Tuple<ZipEntry, RecallOutputStream> t = streams.get(name);
      if (t == null) {
        throw new IOException("Undeclared zip entry: \"" + name + "\"");
      }
      this.active = t.getRight();
    }
  }

  @Override
  public void write(int b) throws IOException {
    // TODO Auto-generated method stub

  }
}
