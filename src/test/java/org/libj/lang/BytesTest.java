/* Copyright (c) 2012 LibJ
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

package org.libj.lang;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BytesTest {
  private static final Logger logger = LoggerFactory.getLogger(BytesTest.class);

  public static String toBinaryString(final long value, final int typeSize) {
    String byteValueString = "";
    for (int i = 0; i <= typeSize - 1; ++i) { // [N]
      final int mask = 1 << i;
      byteValueString = ((mask & value) > 0 ? "1" : "0") + byteValueString;
    }

    return byteValueString;
  }

  @Test
  public void testIndexOf() {
    final byte[] bytes = new byte[] {1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7};

    assertEquals(-1, Bytes.indexOf(new byte[] {}, new byte[] {}));
    assertEquals(-1, Bytes.indexOf(new byte[] {}, new byte[] {1}));

    assertEquals(0, Bytes.indexOf(bytes, new byte[] {1}));
    assertEquals(1, Bytes.indexOf(bytes, (byte)2, (byte)3));
    assertEquals(6, Bytes.indexOf(bytes, (byte)7, (byte)1, (byte)2));
    assertEquals(-1, Bytes.indexOf(bytes, (byte)9, (byte)11, (byte)13, (byte)8));

    assertEquals(7, Bytes.indexOf(bytes, 1, (byte)1));
    assertEquals(9, Bytes.indexOf(bytes, 3, (byte)3));
    assertEquals(13, Bytes.indexOf(bytes, 7, (byte)7));
    assertEquals(-1, Bytes.indexOf(bytes, 7, (byte)8));

    assertEquals(0, Bytes.indexOf(bytes, new byte[] {1, 2, 3}));
    assertEquals(4, Bytes.indexOf(bytes, new byte[] {5, 6, 7}));
    assertEquals(-1, Bytes.indexOf(bytes, new byte[] {6, 7, 8}));

    assertEquals(7, Bytes.indexOf(bytes, 1, new byte[] {1, 2, 3}));
    assertEquals(9, Bytes.indexOf(bytes, 3, new byte[] {3, 4, 5}));
    assertEquals(11, Bytes.indexOf(bytes, 5, new byte[] {5, 6, 7}));
    assertEquals(-1, Bytes.indexOf(bytes, 7, new byte[] {6, 7, 8}));
  }

  @Test
  public void testReplaceAll() {
    byte[] bytes = new byte[] {1, 2, 3, 4, 5, 6, 7};
    Bytes.replaceAll(bytes, new byte[] {1, 2, 3}, new byte[] {0, 0, 0});
    assertArrayEquals(new byte[] {0, 0, 0, 4, 5, 6, 7}, bytes);

    bytes = new byte[] {1, 2, 3, 4, 5, 6, 7};
    Bytes.replaceAll(bytes, new byte[] {2, 3, 4}, new byte[] {1, 1, 1});
    assertArrayEquals(new byte[] {1, 1, 1, 1, 5, 6, 7}, bytes);

    bytes = new byte[] {1, 2, 3, 4, 5, 6, 7};
    Bytes.replaceAll(bytes, new byte[] {5, 6, 7}, new byte[] {0, 0, 0});
    assertArrayEquals(new byte[] {1, 2, 3, 4, 0, 0, 0}, bytes);
  }

  @Test
  public void testShort() {
    final long l = 65535L;
    final short s = (short)l;
    final String binary = toBinaryString(l, Short.SIZE);
    logger.info("Binary: " + binary);
    logger.info("From binary: " + Integer.parseInt(toBinaryString(l, Short.SIZE), 2));
    final byte[] bytes = new byte[Short.SIZE / 8];
    Bytes.toBytes(s, bytes, 0, true);
    logger.info("Convert.toBytes: " + Arrays.toString(bytes));
    assertArrayEquals(new byte[] {(byte)-1, (byte)-1}, bytes);
    final int unsignedShort = Bytes.toShort(bytes, 0, true, false);
    logger.info("Convert.to[unsigned]Short: " + unsignedShort);
    assertEquals(l, unsignedShort);
    final short signedShort = Bytes.toShort(bytes, 0, true);
    logger.info("Convert.to[signed]Short: " + signedShort);
    assertEquals(s, signedShort);
    logger.info("Raw: " + s);
  }

  @Test
  public void testInt() {
    final long l = 4294967295L;
    final int i = (int)l;
    final String binary = toBinaryString(l, Integer.SIZE);
    logger.info("Binary: " + binary);
    logger.info("From binary: " + Long.parseLong(toBinaryString(l, Integer.SIZE), 2));
    final byte[] bytes = new byte[Integer.SIZE / 8];
    Bytes.toBytes(i, bytes, 0, true);
    logger.info("Convert.toBytes: " + Arrays.toString(bytes));
    assertArrayEquals(new byte[] {(byte)-1, (byte)-1, (byte)-1, (byte)-1}, bytes);
    final long unsignedInt = Bytes.toInt(bytes, 0, true, false);
    logger.info("Convert.to[unsigned]Int: " + unsignedInt);
    assertEquals(l, unsignedInt);
    final int signedInt = Bytes.toInt(bytes, 0, true);
    logger.info("Convert.to[signed]Int: " + signedInt);
    assertEquals(i, signedInt);
    logger.info("Raw: " + i);
  }

  @Test
  public void testLong() {
    final long l = 9223372036854775807L;
    final String binary = toBinaryString(l, Long.SIZE);
    logger.info("Binary: " + binary);
    // log("From binary: " + Long.parseLong(binary(l, Long.SIZE), 2));
    final byte[] bytes = new byte[Long.SIZE / 8];
    Bytes.toBytes(l, bytes, 0, true);
    logger.info("Convert.toBytes: " + Arrays.toString(bytes));
    assertArrayEquals(new byte[] {127, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1, (byte)-1}, bytes);
    final long signedInt = Bytes.toLong(bytes, 0, true);
    logger.info("Convert.to[signed]Int: " + signedInt);
    assertEquals(l, signedInt);
    logger.info("Raw: " + l);
  }

  @Test
  @Ignore("Implement this")
  public void testArbitrary() {
    // TODO: Implement this!
  }

  @Test
  public void testToOctal() {
    for (byte i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; ++i) // [N]
      assertEquals(Integer.toString(i, 8), String.valueOf(Bytes.toOctal(i)));
  }

  @Test
  public void testReadBitsFromByte() {
    final byte[] bytes = new byte[] {0b01011011, 0b01101101};
    assertEquals((byte)0b00000000, Bytes.readBitsFromByte(bytes, 0, (byte)0));
    assertEquals((byte)0b00000000, Bytes.readBitsFromByte(bytes, 0, (byte)1));
    assertEquals((byte)0b00000001, Bytes.readBitsFromByte(bytes, 0, (byte)2));
    assertEquals((byte)0b00000010, Bytes.readBitsFromByte(bytes, 0, (byte)3));
    assertEquals((byte)0b00000101, Bytes.readBitsFromByte(bytes, 0, (byte)4));
    assertEquals((byte)0b00001011, Bytes.readBitsFromByte(bytes, 0, (byte)5));
    assertEquals((byte)0b00010110, Bytes.readBitsFromByte(bytes, 0, (byte)6));
    assertEquals((byte)0b00101101, Bytes.readBitsFromByte(bytes, 0, (byte)7));
    assertEquals((byte)0b01011011, Bytes.readBitsFromByte(bytes, 0, (byte)8));
    assertEquals((byte)0b10110110, Bytes.readBitsFromByte(bytes, 1, (byte)8));
    assertEquals((byte)0b01011011, Bytes.readBitsFromByte(bytes, 1, (byte)7));
    assertEquals((byte)0b00101101, Bytes.readBitsFromByte(bytes, 1, (byte)6));
    assertEquals((byte)0b00010110, Bytes.readBitsFromByte(bytes, 1, (byte)5));
    assertEquals((byte)0b00011011, Bytes.readBitsFromByte(bytes, 2, (byte)6));
    assertEquals((byte)0b00110110, Bytes.readBitsFromByte(bytes, 2, (byte)7));
    assertEquals((byte)0b01101101, Bytes.readBitsFromByte(bytes, 2, (byte)8));
    assertEquals((byte)0b11011011, Bytes.readBitsFromByte(bytes, 3, (byte)8));
    assertEquals((byte)0b10110110, Bytes.readBitsFromByte(bytes, 4, (byte)8));
    assertEquals((byte)0b01101101, Bytes.readBitsFromByte(bytes, 5, (byte)8));
    assertEquals((byte)0b01101101, Bytes.readBitsFromByte(bytes, 6, (byte)7));
    assertEquals((byte)0b00101101, Bytes.readBitsFromByte(bytes, 7, (byte)6));
    assertEquals((byte)0b00001101, Bytes.readBitsFromByte(bytes, 8, (byte)5));
  }

  @Test
  public void testReadBitsFromBytes() {
    final byte[] bytes = new byte[] {0b01011011, 0b01101101, 0b01101001, 0b01010110};
    assertArrayEquals(new byte[] {0b00000000}, Bytes.readBitsFromBytes(bytes, 0, 0));
    assertArrayEquals(new byte[] {0b00000001}, Bytes.readBitsFromBytes(bytes, 0, 2));
    assertArrayEquals(new byte[] {0b00000101}, Bytes.readBitsFromBytes(bytes, 0, 4));
    assertArrayEquals(new byte[] {0b00010110}, Bytes.readBitsFromBytes(bytes, 0, 6));
    assertArrayEquals(new byte[] {(byte)0b10110110}, Bytes.readBitsFromBytes(bytes, 1, 8));
    assertArrayEquals(new byte[] {0b00000001, (byte)0b01101101}, Bytes.readBitsFromBytes(bytes, 1, 9));
    assertArrayEquals(new byte[] {0b00000011, (byte)0b01101101}, Bytes.readBitsFromBytes(bytes, 2, 11));
    assertArrayEquals(new byte[] {0b00011011, (byte)0b01101101}, Bytes.readBitsFromBytes(bytes, 3, 13));
    assertArrayEquals(new byte[] {0b01011011, (byte)0b01101011}, Bytes.readBitsFromBytes(bytes, 4, 15));
    assertArrayEquals(new byte[] {0b00000000, (byte)0b11011011, 0b01011010}, Bytes.readBitsFromBytes(bytes, 5, 17));
    assertArrayEquals(new byte[] {0b01011011, 0b01011010, 0b01010101}, Bytes.readBitsFromBytes(bytes, 7, 23));
  }

  @Test
  public void testWriteBitsBFromByte() {
    final byte by = 0b01011011;
    final byte[] dest = new byte[1];

    assertEquals(3, Bytes.writeBitsB(dest, 0, by, (byte)3));
    assertArrayEquals(new byte[] {0b01100000}, dest);

    dest[0] = 0x0;
    assertEquals(5, Bytes.writeBitsB(dest, 0, by, (byte)5));
    assertArrayEquals(new byte[] {(byte)0b11011000}, dest);

    dest[0] = 0x0;
    assertEquals(7, Bytes.writeBitsB(dest, 0, by, (byte)7));
    assertArrayEquals(new byte[] {(byte)0b10110110}, dest);

    dest[0] = 0x0;
    assertEquals(8, Bytes.writeBitsB(dest, 1, by, (byte)7));
    assertArrayEquals(new byte[] {(byte)0b01011011}, dest);

    dest[0] = 0x0;
    assertEquals(8, Bytes.writeBitsB(dest, 2, by, (byte)6));
    assertArrayEquals(new byte[] {(byte)0b00011011}, dest);

    dest[0] = 0x0;
    assertEquals(8, Bytes.writeBitsB(dest, 3, by, (byte)5));
    assertArrayEquals(new byte[] {(byte)0b00011011}, dest);

    dest[0] = 0x0;
    assertEquals(8, Bytes.writeBitsB(dest, 5, by, (byte)3));
    assertArrayEquals(new byte[] {(byte)0b00000011}, dest);

    dest[0] = 0x0;
    assertEquals(8, Bytes.writeBitsB(dest, 7, by, (byte)1));
    assertArrayEquals(new byte[] {(byte)0b00000001}, dest);
  }

  @Test
  public void testWriteBitsLFromByte() {
    final byte by = 0b01011011;
    final byte[] dest = new byte[1];

    assertEquals(3, Bytes.writeBitsL(dest, 0, by, (byte)3));
    assertArrayEquals(new byte[] {0b01000000}, dest);

    dest[0] = 0x0;
    assertEquals(5, Bytes.writeBitsL(dest, 0, by, (byte)5));
    assertArrayEquals(new byte[] {(byte)0b01011000}, dest);

    dest[0] = 0x0;
    assertEquals(7, Bytes.writeBitsL(dest, 0, by, (byte)7));
    assertArrayEquals(new byte[] {(byte)0b01011010}, dest);

    dest[0] = 0x0;
    assertEquals(8, Bytes.writeBitsL(dest, 1, by, (byte)7));
    assertArrayEquals(new byte[] {(byte)0b00101101}, dest);

    dest[0] = 0x0;
    assertEquals(8, Bytes.writeBitsL(dest, 2, by, (byte)6));
    assertArrayEquals(new byte[] {(byte)0b00010110}, dest);

    dest[0] = 0x0;
    assertEquals(8, Bytes.writeBitsL(dest, 3, by, (byte)5));
    assertArrayEquals(new byte[] {(byte)0b00001011}, dest);

    dest[0] = 0x0;
    assertEquals(8, Bytes.writeBitsL(dest, 5, by, (byte)3));
    assertArrayEquals(new byte[] {(byte)0b00000010}, dest);
  }

  @Test
  public void testWriteBitsBFromBytes() {
    final byte[] bytes = new byte[] {0b01011011, 0b01101101, 0b01101001, 0b01010110};
    final byte[] dest = new byte[4];
    assertEquals(3, Bytes.writeBitsB(dest, 0, bytes, (byte)3));
    assertArrayEquals(new byte[] {0b01100000, 0, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    assertEquals(8, Bytes.writeBitsB(dest, 3, bytes, (byte)5));
    assertArrayEquals(new byte[] {0b00011011, 0, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    assertEquals(12, Bytes.writeBitsB(dest, 5, bytes, (byte)7));
    assertArrayEquals(new byte[] {0b00000101, (byte)0b10110000, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    assertEquals(16, Bytes.writeBitsB(dest, 7, bytes, (byte)9));
    assertArrayEquals(new byte[] {0b00000001, 0b01101101, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    assertEquals(18, Bytes.writeBitsB(dest, 5, bytes, (byte)13));
    assertArrayEquals(new byte[] {0b00000110, (byte)0b11011011, 0b01000000, 0b00000000}, dest);

    Arrays.fill(dest, (byte)0);
    assertEquals(26, Bytes.writeBitsB(dest, 3, bytes, (byte)23));
    assertArrayEquals(new byte[] {0b00010110, (byte)0b11011011, 0b01011010, 0b01000000}, dest);
  }

  @Test
  public void testWriteBitsLFromBytes() {
    final byte[] bytes = new byte[] {0b01011011, 0b01101101, 0b01101001, 0b01010110};
    final byte[] dest = new byte[4];
    assertEquals(3, Bytes.writeBitsL(dest, 0, bytes, 3));
    assertArrayEquals(new byte[] {0b01000000, 0, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    assertEquals(8, Bytes.writeBitsL(dest, 3, bytes, 5));
    assertArrayEquals(new byte[] {0b00001011, 0, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    assertEquals(12, Bytes.writeBitsL(dest, 5, bytes, 7));
    assertArrayEquals(new byte[] {0b00000010, (byte)0b11010000, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    assertEquals(16, Bytes.writeBitsL(dest, 7, bytes, 9));
    assertArrayEquals(new byte[] {0b00000000, (byte)0b10110110, 0, 0}, dest);

    Arrays.fill(dest, (byte)0);
    assertEquals(18, Bytes.writeBitsL(dest, 5, bytes, 13));
    assertArrayEquals(new byte[] {0b00000010, (byte)0b11011011, 0b01000000, 0}, dest);

    Arrays.fill(dest, (byte)0);
    assertEquals(30, Bytes.writeBitsL(dest, 3, bytes, 27));
    assertArrayEquals(new byte[] {0b00001011, 0b01101101, (byte)0b10101101, 0b00101000}, dest);

    Arrays.fill(dest, (byte)0);
    assertEquals(30, Bytes.writeBitsL(dest, 5, bytes, 25));
    assertArrayEquals(new byte[] {0b00000010, (byte)0b11011011, 0b01101011, 0b01001000}, dest);
  }
}