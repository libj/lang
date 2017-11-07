/* Copyright (c) 2012 lib4j
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

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BytesTest {
  private static final Logger logger = LoggerFactory.getLogger(BytesTest.class);

  public static String toBinaryString(final long value, final int typeSize) {
    String byteValueString = "";
    for (int j = 0; j <= typeSize - 1; j++) {
      final int mask = 1 << j;
      byteValueString = ((mask & value) > 0 ? "1" : "0") + byteValueString;
    }

    return byteValueString;
  }

  @Test
  public void testIndexOf() {
    final byte[] bytes = new byte[] {1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7};

    Assert.assertEquals(-1, Bytes.indexOf(new byte[] {}, new byte[] {}));
    Assert.assertEquals(-1, Bytes.indexOf(new byte[] {}, new byte[] {1}));

    Assert.assertEquals(0, Bytes.indexOf(bytes, new byte[] {1}));
    Assert.assertEquals(1, Bytes.indexOf(bytes, (byte)2, (byte)3));
    Assert.assertEquals(6, Bytes.indexOf(bytes, (byte)7, (byte)1, (byte)2));
    Assert.assertEquals(-1, Bytes.indexOf(bytes, (byte)9, (byte)11, (byte)13, (byte)8));

    Assert.assertEquals(7, Bytes.indexOf(bytes, 1, (byte)1));
    Assert.assertEquals(9, Bytes.indexOf(bytes, 3, (byte)3));
    Assert.assertEquals(13, Bytes.indexOf(bytes, 7, (byte)7));
    Assert.assertEquals(-1, Bytes.indexOf(bytes, 7, (byte)8));

    Assert.assertEquals(0, Bytes.indexOf(bytes, new byte[] {1, 2, 3}));
    Assert.assertEquals(2, Bytes.indexOf(bytes, new byte[] {0, 4, 5}, new byte[] {3, 4, 5}));
    Assert.assertEquals(4, Bytes.indexOf(bytes, new byte[] {5, 6, 7}));
    Assert.assertEquals(-1, Bytes.indexOf(bytes, new byte[] {6, 7, 8}));

    Assert.assertEquals(7, Bytes.indexOf(bytes, 1, new byte[] {1, 2, 3}));
    Assert.assertEquals(9, Bytes.indexOf(bytes, 3, new byte[] {3, 4, 5}));
    Assert.assertEquals(11, Bytes.indexOf(bytes, 5, new byte[] {5, 6, 7}));
    Assert.assertEquals(-1, Bytes.indexOf(bytes, 7, new byte[] {6, 7, 8}));
  }

  @Test
  public void testIndicesOf() {
    try {
      Assert.assertArrayEquals(new int[0], Bytes.indicesOf(new byte[] {0}, -1, (byte)0));
      Assert.fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Assert.assertArrayEquals(new int[0], Bytes.indicesOf(new byte[] {0}, 1, (byte)0));
      Assert.fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    final byte[] bytes = new byte[] {1, 2, 3, 1, 2, 3, 7, 1, 2, 3, 8, 1, 2, 3};
    Assert.assertArrayEquals(new int[0], Bytes.indicesOf(bytes));
    Assert.assertArrayEquals(new int[0], Bytes.indicesOf(bytes, (byte)0));
    Assert.assertArrayEquals(new int[] {0, 3, 7, 11}, Bytes.indicesOf(bytes, (byte)1));
    Assert.assertArrayEquals(new int[] {1, 4, 8, 12}, Bytes.indicesOf(bytes, (byte)2));
    Assert.assertArrayEquals(new int[] {2, 5, 9, 13}, Bytes.indicesOf(bytes, (byte)3));
    Assert.assertArrayEquals(new int[] {6}, Bytes.indicesOf(bytes, (byte)7));
    Assert.assertArrayEquals(new int[] {0, 3, 7, 11}, Bytes.indicesOf(bytes, new byte[] {1, 2}));
    Assert.assertArrayEquals(new int[] {1, 4, 8, 12}, Bytes.indicesOf(bytes, new byte[] {2, 3}));
    Assert.assertArrayEquals(new int[] {2}, Bytes.indicesOf(bytes, new byte[] {3, 1}));
    Assert.assertArrayEquals(new int[] {0, 3, 7, 11}, Bytes.indicesOf(bytes, new byte[] {1, 2, 3}));
  }

  @Test
  public void testReplaceAll() {
    byte[] bytes = new byte[] {1, 2, 3, 4, 5, 6, 7};
    Bytes.replaceAll(bytes, new byte[] {1, 2, 3}, new byte[] {0, 0, 0});
    Assert.assertArrayEquals(new byte[] {0, 0, 0, 4, 5, 6, 7}, bytes);

    bytes = new byte[] {1, 2, 3, 4, 5, 6, 7};
    Bytes.replaceAll(bytes, new byte[] {2, 3, 4}, new byte[] {1, 1, 1});
    Assert.assertArrayEquals(new byte[] {1, 1, 1, 1, 5, 6, 7}, bytes);

    bytes = new byte[] {1, 2, 3, 4, 5, 6, 7};
    Bytes.replaceAll(bytes, new byte[] {5, 6, 7}, new byte[] {0, 0, 0});
    Assert.assertArrayEquals(new byte[] {1, 2, 3, 4, 0, 0, 0}, bytes);
  }

  @Test
  public void testShort() {
    long l = 65535l;
    short s = (short)l;
    String binary = toBinaryString(l, Short.SIZE);
    logger.info("Binary: " + binary);
    logger.info("From binary: " + Integer.parseInt(toBinaryString(l, Short.SIZE), 2));
    byte[] bytes = new byte[Short.SIZE / 8];
    Bytes.toBytes(s, bytes, 0, true);
    logger.info("Convert.toBytes: " + Arrays.toString(bytes));
    Assert.assertArrayEquals(new byte[] {(byte)-1, (byte)-1}, bytes);
    int unsignedShort = Bytes.toShort(bytes, 0, true, false);
    logger.info("Convert.to[unsigned]Short: " + unsignedShort);
    Assert.assertEquals(l, unsignedShort);
    short signedShort = Bytes.toShort(bytes, 0, true);
    logger.info("Convert.to[signed]Short: " + signedShort);
    Assert.assertEquals(s, signedShort);
    logger.info("Raw: " + s);
  }

  @Test
  public void testInt() {
    long l = 4294967295l;
    int i = (int)l;
    String binary = toBinaryString(l, Integer.SIZE);
    logger.info("Binary: " + binary);
    logger.info("From binary: " + Long.parseLong(toBinaryString(l, Integer.SIZE), 2));
    byte[] bytes = new byte[Integer.SIZE / 8];
    Bytes.toBytes(i, bytes, 0, true);
    logger.info("Convert.toBytes: " + Arrays.toString(bytes));
    Assert.assertArrayEquals(new byte[] {(byte)-1, (byte)-1, (byte)-1, (byte)-1}, bytes);
    long unsignedInt = Bytes.toInt(bytes, 0, true, false);
    logger.info("Convert.to[unsigned]Int: " + unsignedInt);
    Assert.assertEquals(l, unsignedInt);
    int signedInt = Bytes.toInt(bytes, 0, true);
    logger.info("Convert.to[signed]Int: " + signedInt);
    Assert.assertEquals(i, signedInt);
    logger.info("Raw: " + i);
  }

  @Test
  public void testLong() {
    long l = 9223372036854775807l;
    String binary = toBinaryString(l, Long.SIZE);
    logger.info("Binary: " + binary);
    //log("From binary: " + Long.parseLong(binary(l, Long.SIZE), 2));
    byte[] bytes = new byte[Long.SIZE / 8];
    Bytes.toBytes(l, bytes, 0, true);
    logger.info("Convert.toBytes: " + Arrays.toString(bytes));
    Assert.assertArrayEquals(new byte[] {127, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1}, bytes);
    long signedInt = Bytes.toLong(bytes, 0, true);
    logger.info("Convert.to[signed]Int: " + signedInt);
    Assert.assertEquals(l, signedInt);
    logger.info("Raw: " + l);
  }

  @Test
  @Ignore("Implement this")
  public void testArbitrary() {
    // TODO: Implement this!
  }

  @Test
  public void testToOctal() {
    for (byte i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; i++)
      Assert.assertEquals(Integer.toString(i, 8), String.valueOf(Bytes.toOctal(i)));
  }

  @Test
  public void testReadByte() {
    final byte[] bytes = {0b01011011, 0b01101101};
    Assert.assertEquals((byte)0b00000000, Bytes.readByteB2L(bytes, 0, (byte)0));
    Assert.assertEquals((byte)0b00000000, Bytes.readByteB2L(bytes, 0, (byte)1));
    Assert.assertEquals((byte)0b00000001, Bytes.readByteB2L(bytes, 0, (byte)2));
    Assert.assertEquals((byte)0b00000010, Bytes.readByteB2L(bytes, 0, (byte)3));
    Assert.assertEquals((byte)0b00000101, Bytes.readByteB2L(bytes, 0, (byte)4));
    Assert.assertEquals((byte)0b00001011, Bytes.readByteB2L(bytes, 0, (byte)5));
    Assert.assertEquals((byte)0b00010110, Bytes.readByteB2L(bytes, 0, (byte)6));
    Assert.assertEquals((byte)0b00101101, Bytes.readByteB2L(bytes, 0, (byte)7));
    Assert.assertEquals((byte)0b01011011, Bytes.readByteB2L(bytes, 0, (byte)8));
    Assert.assertEquals((byte)0b10110110, Bytes.readByteB2L(bytes, 1, (byte)8));
    Assert.assertEquals((byte)0b01011011, Bytes.readByteB2L(bytes, 1, (byte)7));
    Assert.assertEquals((byte)0b00101101, Bytes.readByteB2L(bytes, 1, (byte)6));
    Assert.assertEquals((byte)0b00010110, Bytes.readByteB2L(bytes, 1, (byte)5));
    Assert.assertEquals((byte)0b00011011, Bytes.readByteB2L(bytes, 2, (byte)6));
    Assert.assertEquals((byte)0b00110110, Bytes.readByteB2L(bytes, 2, (byte)7));
    Assert.assertEquals((byte)0b01101101, Bytes.readByteB2L(bytes, 2, (byte)8));
    Assert.assertEquals((byte)0b11011011, Bytes.readByteB2L(bytes, 3, (byte)8));
    Assert.assertEquals((byte)0b10110110, Bytes.readByteB2L(bytes, 4, (byte)8));
    Assert.assertEquals((byte)0b01101101, Bytes.readByteB2L(bytes, 5, (byte)8));
    Assert.assertEquals((byte)0b01101101, Bytes.readByteB2L(bytes, 6, (byte)7));
    Assert.assertEquals((byte)0b00101101, Bytes.readByteB2L(bytes, 7, (byte)6));
    Assert.assertEquals((byte)0b00001101, Bytes.readByteB2L(bytes, 8, (byte)5));
  }

  @Test
  public void testReadBytes() {
    final byte[] bytes = {0b01011011, 0b01101101, 0b01101001, 0b01010110};
    Assert.assertArrayEquals(new byte [] {0b00000000}, Bytes.readBytesB2L(bytes, 0, (byte)0));
    Assert.assertArrayEquals(new byte [] {0b00000001}, Bytes.readBytesB2L(bytes, 0, (byte)2));
    Assert.assertArrayEquals(new byte [] {0b00000101}, Bytes.readBytesB2L(bytes, 0, (byte)4));
    Assert.assertArrayEquals(new byte [] {0b00010110}, Bytes.readBytesB2L(bytes, 0, (byte)6));
    Assert.assertArrayEquals(new byte [] {(byte)0b10110110}, Bytes.readBytesB2L(bytes, 1, (byte)8));
    Assert.assertArrayEquals(new byte [] {0b00000001, (byte)0b01101101}, Bytes.readBytesB2L(bytes, 1, (byte)9));
    Assert.assertArrayEquals(new byte [] {0b00000011, (byte)0b01101101}, Bytes.readBytesB2L(bytes, 2, (byte)11));
    Assert.assertArrayEquals(new byte [] {0b00011011, (byte)0b01101101}, Bytes.readBytesB2L(bytes, 3, (byte)13));
    Assert.assertArrayEquals(new byte [] {0b01011011, (byte)0b01101011}, Bytes.readBytesB2L(bytes, 4, (byte)15));
    Assert.assertArrayEquals(new byte [] {0b00000000, (byte)0b11011011, 0b01011010}, Bytes.readBytesB2L(bytes, 5, (byte)17));
    Assert.assertArrayEquals(new byte [] {0b01011011, 0b01011010, 0b01010101}, Bytes.readBytesB2L(bytes, 7, (byte)23));
  }

  @Test
  public void testWriteByteL2B() {
    final byte by = (byte)0b01011011;
    final byte[] dest = new byte[1];

    Assert.assertEquals(3, Bytes.writeByteL2B(dest, 0, by, (byte)3));
    Assert.assertArrayEquals(new byte[] {0b01100000}, dest);

    dest[0] = 0x0;
    Assert.assertEquals(5, Bytes.writeByteL2B(dest, 0, by, (byte)5));
    Assert.assertArrayEquals(new byte[] {(byte)0b11011000}, dest);

    dest[0] = 0x0;
    Assert.assertEquals(7, Bytes.writeByteL2B(dest, 0, by, (byte)7));
    Assert.assertArrayEquals(new byte[] {(byte)0b10110110}, dest);

    dest[0] = 0x0;
    Assert.assertEquals(8, Bytes.writeByteL2B(dest, 1, by, (byte)7));
    Assert.assertArrayEquals(new byte[] {(byte)0b01011011}, dest);

    dest[0] = 0x0;
    Assert.assertEquals(8, Bytes.writeByteL2B(dest, 2, by, (byte)6));
    Assert.assertArrayEquals(new byte[] {(byte)0b00011011}, dest);

    dest[0] = 0x0;
    Assert.assertEquals(8, Bytes.writeByteL2B(dest, 3, by, (byte)5));
    Assert.assertArrayEquals(new byte[] {(byte)0b00011011}, dest);

    dest[0] = 0x0;
    Assert.assertEquals(8, Bytes.writeByteL2B(dest, 5, by, (byte)3));
    Assert.assertArrayEquals(new byte[] {(byte)0b00000011}, dest);

    dest[0] = 0x0;
    Assert.assertEquals(8, Bytes.writeByteL2B(dest, 7, by, (byte)1));
    Assert.assertArrayEquals(new byte[] {(byte)0b00000001}, dest);
  }

  @Test
  public void testWriteByteB2B() {
    final byte by = (byte)0b01011011;
    final byte[] dest = new byte[1];

    Assert.assertEquals(3, Bytes.writeByteB2B(dest, 0, by, (byte)3));
    Assert.assertArrayEquals(new byte[] {0b01000000}, dest);

    dest[0] = 0x0;
    Assert.assertEquals(5, Bytes.writeByteB2B(dest, 0, by, (byte)5));
    Assert.assertArrayEquals(new byte[] {(byte)0b01011000}, dest);

    dest[0] = 0x0;
    Assert.assertEquals(7, Bytes.writeByteB2B(dest, 0, by, (byte)7));
    Assert.assertArrayEquals(new byte[] {(byte)0b01011010}, dest);

    dest[0] = 0x0;
    Assert.assertEquals(8, Bytes.writeByteB2B(dest, 1, by, (byte)7));
    Assert.assertArrayEquals(new byte[] {(byte)0b00101101}, dest);

    dest[0] = 0x0;
    Assert.assertEquals(8, Bytes.writeByteB2B(dest, 2, by, (byte)6));
    Assert.assertArrayEquals(new byte[] {(byte)0b00010110}, dest);

    dest[0] = 0x0;
    Assert.assertEquals(8, Bytes.writeByteB2B(dest, 3, by, (byte)5));
    Assert.assertArrayEquals(new byte[] {(byte)0b00001011}, dest);

    dest[0] = 0x0;
    Assert.assertEquals(8, Bytes.writeByteB2B(dest, 5, by, (byte)3));
    Assert.assertArrayEquals(new byte[] {(byte)0b00000010}, dest);
  }

  @Test
  public void testWriteBytesL2B() {
    final byte[] bytes = {0b01011011, 0b01101101, 0b01101001, 0b01010110};
    final byte[] dest = new byte[4];
    Assert.assertEquals(3, Bytes.writeBytesL2B(dest, 0, bytes, (byte)3));
    Assert.assertArrayEquals(new byte[] {0b01100000, 0, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    Assert.assertEquals(8, Bytes.writeBytesL2B(dest, 3, bytes, (byte)5));
    Assert.assertArrayEquals(new byte[] {0b00011011, 0, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    Assert.assertEquals(12, Bytes.writeBytesL2B(dest, 5, bytes, (byte)7));
    Assert.assertArrayEquals(new byte[] {0b00000101, (byte)0b10110000, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    Assert.assertEquals(16, Bytes.writeBytesL2B(dest, 7, bytes, (byte)9));
    Assert.assertArrayEquals(new byte[] {0b00000001, 0b01101101, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    Assert.assertEquals(18, Bytes.writeBytesL2B(dest, 5, bytes, (byte)13));
    Assert.assertArrayEquals(new byte[] {0b00010110, (byte)0b11011011, 0b01000000, 0b00000000}, dest);

    Arrays.fill(dest, (byte)0);
    Assert.assertEquals(26, Bytes.writeBytesL2B(dest, 3, bytes, (byte)23));
    Assert.assertArrayEquals(new byte[] {0b00010110, (byte)0b11011011, 0b01011010, 0b01000000}, dest);
  }

  @Test
  public void testWriteBytesB2B() {
    final byte[] bytes = {0b01011011, 0b01101101, 0b01101001, 0b01010110};
    final byte[] dest = new byte[4];
    Assert.assertEquals(3, Bytes.writeBytesB2B(dest, 0, bytes, (byte)3));
    Assert.assertArrayEquals(new byte[] {0b01000000, 0, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    Assert.assertEquals(8, Bytes.writeBytesB2B(dest, 3, bytes, (byte)5));
    Assert.assertArrayEquals(new byte[] {0b00001011, 0, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    Assert.assertEquals(12, Bytes.writeBytesB2B(dest, 5, bytes, (byte)7));
    Assert.assertArrayEquals(new byte[] {0b00000010, (byte)0b11010000, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    Assert.assertEquals(16, Bytes.writeBytesB2B(dest, 7, bytes, (byte)9));
    Assert.assertArrayEquals(new byte[] {0b00000000, (byte)0b10110110, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    Assert.assertEquals(18, Bytes.writeBytesB2B(dest, 5, bytes, (byte)13));
    Assert.assertArrayEquals(new byte[] {0b00000010, (byte)0b11011011, 0b01000000, 0}, dest);

    Arrays.fill(dest, (byte)0);
    Assert.assertEquals(30, Bytes.writeBytesB2B(dest, 3, bytes, (byte)27));
    Assert.assertArrayEquals(new byte[] {0b00001011, 0b01101101, (byte)0b10101101, 0b00101000}, dest);

    Arrays.fill(dest, (byte)0);
    Assert.assertEquals(30, Bytes.writeBytesB2B(dest, 5, bytes, (byte)25));
    Assert.assertArrayEquals(new byte[] {0b00000010, (byte)0b11011011, 0b01101011, 0b01001000}, dest);
  }
}