/* Copyright (c) 2017 lib4j
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.lib4j.lang;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class BytesReadWriteTest {
  private static final boolean debug = false;

  // Get LengthSize (5 bits) from beginning of byte
  private static byte getLengthSize(final byte src) {
    return (byte)((src & 0xff) >> 3);
  }

  protected static int getSize(final int length, final byte lengthSize) {
    return 2 + lengthSize + length * 8;
  }

  // 5 bits reserved for LengthSize, allowing max value of 32
  // (0) Write the lengthSize (5 bits)
  protected static int writeLengthSize(final byte[] dest, final byte lengthSize) {
    dest[0] |= lengthSize << 3;
    return 5;
  }

  // (1) Write the ordinal (2 bits)
  protected static int writeOrdinal(final byte[] dest, int offset, final byte ordinal) {
    return Bytes.writeByteL2B(dest, offset, ordinal, (byte)2);
  }

  // (2) Write the length (lengthSize bits)
  protected static int writeLength(final byte[] dest, int offset, final int length, final byte lengthSize) {
    final byte[] bytes = new byte[1 + (lengthSize - 1) / 8];
    Bytes.toBytes(length, bytes, 0, true);
    return Bytes.writeBytesL2B(dest, offset, bytes, lengthSize);
  }

  // (3) Write the text (length * 8 bits)
  protected static int writeText(final byte[] dest, int offset, final byte[] text, final int length) {
    return Bytes.writeBytesL2B(dest, offset, text, length * 8);
  }

  protected static int encode(final byte[] dest, int offset, final byte ordinal, final int length, final byte lengthSize, final byte[] text) {
    offset = writeOrdinal(dest, offset, ordinal);
    offset = writeLength(dest, offset, length, lengthSize);
    offset = writeText(dest, offset, text, length);
    return offset;
  }

  private static void assertEqual(final String string) throws IOException {
    // Encode...
    final byte ordinal = 0x2;
    final byte[] text = string.getBytes("UTF-8");
    final int length = text.length;
    final byte lengthSize = Bytes.getSize(length);
    if (debug) {
      System.out.println("LengthSize: " + lengthSize + " " + Bytes.toString(lengthSize).substring(3));
      System.out.println("Ordinal: " + ordinal + " " + Bytes.toString(ordinal).substring(6));
    }

    final byte[] lengthBytes = new byte[1 + (lengthSize - 1) / 8];
    Bytes.toBytes(length, lengthBytes, 0, true);

    final int size = 5 + getSize(length, lengthSize);
    final byte[] dest = new byte[1 + (size - 1) / 8];

    if (debug) {
      System.out.println("Length: " + length + " " + Bytes.toString(lengthBytes));
      System.out.println("dest.length = " + dest.length);
    }

    int offset = writeLengthSize(dest, lengthSize);
    offset = encode(dest, offset, ordinal, length, lengthSize, text);

//    if (debug)
//      Bytes.println(dest);

    // Decode...
    final byte lengthSizeDecoded = getLengthSize(dest[0]);
    Assert.assertEquals(lengthSize, lengthSizeDecoded);
    offset = 5;
    final byte ordinalDecoded = Bytes.readByteB2L(dest, offset, (byte)2);
    Assert.assertEquals(ordinal, ordinalDecoded);
    final int lengthDecoded = Bytes.toInt(Bytes.readBytesB2L(dest, offset + 2, lengthSizeDecoded), 0, true);
    Assert.assertEquals(length, lengthDecoded);
    final byte[] decodedText = Bytes.readBytesB2L(dest, offset + 2 + lengthSizeDecoded, lengthDecoded * 8);
    Assert.assertArrayEquals(text, decodedText);
//    System.err.println(out);
  }

  @Test
  public void test() throws IOException {
    String string = "≠≈∧∨∩∪";
    for (int i = 0; i < 23; i++) {
      if (debug)
        System.out.println(i);

      string += string;
      assertEqual(string);
    }
  }
}