/* Copyright (c) 2009 LibJ
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

import org.junit.Test;

public class HexadecimalTest {
  private static void testHexToBytesDirect(final String hex) {
    final byte[] bytes = Hexadecimal.decode(hex);
    final String actual = Hexadecimal.encode(bytes);
    assertEquals(hex, actual);
  }

  private static void testHexToBytesOffset(final String hex) {
    for (int i = 0; i < 1000; ++i) {
      final int len = hex.length() / 2;
      final byte[] bytes = new byte[len + (int)(Math.random() * len)];
      final int offset = (int)((bytes.length - len) * Math.random());
      Hexadecimal.decode(hex, bytes, offset);
      final String actual = Hexadecimal.encode(bytes, offset, len);
      assertEquals(hex, actual);
    }
  }

  private static void testHexToBytes(final String hex) {
    testHexToBytesDirect(hex);
    testHexToBytesOffset(hex);
  }

  private static void testBytesToHex(final byte[] bytes) {
    final String hex = Hexadecimal.encode(bytes);
    final byte[] actual = Hexadecimal.decode(hex);
    assertArrayEquals(bytes, actual);
  }

  @Test
  public void testHexToBytes() {
    try {
      Hexadecimal.decode(null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Hexadecimal.decode("a");
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    testHexToBytes("");
    testHexToBytes("9c0da433e55947a66b97");
    testHexToBytes("9c0da433e55947a66b979c0da433e55947a66b97");
    testHexToBytes("9c0da433e55947a66b979c0da433e55947a66b979c0da433e55947a66b979c0da433e55947a66b97");
  }

  @Test
  public void testBytesToHex() {
    try {
      Hexadecimal.encode(null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    testBytesToHex(new byte[0]);
    testBytesToHex(new byte[] {0x12, 0x35, 0x57, 0x32, 0x57, 0x78});
    testBytesToHex(new byte[] {0x12, 0x35, 0x57, 0x32, 0x57, 0x78, 0x12, 0x35, 0x57, 0x32, 0x57, 0x78});
    testBytesToHex(new byte[] {0x12, 0x35, 0x57, 0x32, 0x57, 0x78, 0x12, 0x35, 0x57, 0x32, 0x57, 0x78, 0x12, 0x35, 0x57, 0x32, 0x57, 0x78, 0x12, 0x35, 0x57, 0x32, 0x57, 0x78});
  }
}