package org.freehep.graphicsio.font.truetype;

import java.io.IOException;
import java.util.*;

/**
 * Data input for true type files. All methods are named as the data formats in
 * the true type specification.
 * 
 * @author Simon Fischer
 * @version $Id: TTFInput.java,v 1.5 2009/08/17 21:44:45 murkle Exp $
 */
public abstract class TTFInput {

  protected static final boolean checkZeroBit(int b, int bit, String name)
      throws IOException {
    if (flagBit(b, bit)) {
      System.err.println("Reserved bit " + bit + " in " + name + " not 0.");
      return false;
    } else
      return true;
  }

  protected static boolean flagBit(int b, int bit) {
    return (b & 1 << bit) > 0;
  }

  // --------------- IO ---------------

  private final Stack<Long> filePosStack = new Stack<Long>();

  private int tempFlags;

  protected final void checkShortZero() throws IOException {
    if (readShort() != 0)
      System.err.println("Reserved bit should be 0.");
  }

  protected boolean flagBit(int bit) {
    return flagBit(tempFlags, bit);
  }

  // ---------- Simple Data Types --------------

  abstract long getPointer() throws IOException;

  protected void popPos() throws IOException {
    seek(filePosStack.pop().longValue());
  }

  protected void pushPos() throws IOException {
    filePosStack.push(new Long(getPointer()));
  }

  public abstract int readByte() throws IOException;

  /**
   * Reads byte flags into a temporary variable which can be queried using the
   * flagBit method.
   */
  public void readByteFlags() throws IOException {
    tempFlags = readByte();
  }

  public abstract byte readChar() throws IOException;

  protected final double readF2Dot14() throws IOException {
    int major = readByte();
    int minor = readByte();
    int fraction = minor + ((major & 0x3f) << 8);
    int mantissa = major >> 6;
    if (mantissa >= 2)
      mantissa -= 4;
    return mantissa + fraction / 16384d;
  }

  public int[] readFFFFTerminatedUShortArray() throws IOException {
    List<Integer> values = new LinkedList<Integer>();
    int ushort = -1;
    do {
      ushort = readUShort();
      values.add(new Integer(ushort));
    } while (ushort != 0xFFFF);
    int[] shorts = new int[values.size()];
    Iterator<Integer> i = values.iterator();
    int j = 0;
    while (i.hasNext())
      shorts[j++] = i.next().intValue();
    return shorts;
  }

  protected final double readFixed() throws IOException {
    int major = readShort();
    int minor = readShort();
    return major + minor / 16384d;
  }

  public abstract void readFully(byte[] b) throws IOException;

  protected final short readFWord() throws IOException {
    return readShort();
  }

  // ------------------------------------------------------------

  public abstract int readLong() throws IOException;

  public abstract int readRawByte() throws IOException;

  // ---------------- Flags --------------------

  public abstract short readShort() throws IOException;

  protected short[] readShortArray(int n) throws IOException {
    short[] temp = new short[n];
    for (int i = 0; i < temp.length; i++)
      temp[i] = readShort();
    return temp;
  }

  protected final int readUFWord() throws IOException {
    return readUShort();
  }

  public abstract long readULong() throws IOException;

  // ---------------- Arrays -------------------

  public abstract int readUShort() throws IOException;

  protected int[] readUShortArray(int n) throws IOException {
    int[] temp = new int[n];
    for (int i = 0; i < temp.length; i++)
      temp[i] = readUShort();
    return temp;
  }

  /**
   * Reads unsigned short flags into a temporary variable which can be queried
   * using the flagBit method.
   */
  protected void readUShortFlags() throws IOException {
    tempFlags = readUShort();
  }

  public abstract void seek(long offset) throws IOException;

}
