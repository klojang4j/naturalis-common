package nl.naturalis.common.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.function.ThrowingSupplier;
import static nl.naturalis.common.check.CommonChecks.*;

public class ZipSwapOutputStream extends SwapOutputStream {

  private Deflater def;

  /**
   * Creates a {@code SwapOutputStream} with an internal buffer of 8 kilobytes, swapping to the
   * specified resource once the buffer overflows.
   *
   * @param swapTo A supplier supplying an outputstream to the swap-to resource. The supplier's
   *     {@link ThrowingSupplier#get() get()} method will only be called if the internal buffer
   *     overflows.
   */
  public ZipSwapOutputStream(ThrowingSupplier<OutputStream, IOException> swapTo) {
    super(swapTo);
  }

  /**
   * Creates a {@code SwapOutputStream} with an internal buffer of {@code treshold} bytes, swapping
   * to the specified resource once the buffer overflows.
   *
   * @param swapTo A supplier supplying an outputstream to the swap-to resource. The supplier's
   *     {@link ThrowingSupplier#get() get()} method will only be called if the internal buffer
   *     overflows.
   * @param bufSize The size in bytes of the internal buffer
   */
  public ZipSwapOutputStream(ThrowingSupplier<OutputStream, IOException> swapTo, int bufSize) {
    super(swapTo, bufSize);
  }

  @Override
  public void collect(OutputStream output) throws IOException {
    // TODO Auto-generated method stub

  }
}
