/* Copyright (c) 2018 LibJ
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

public class BuffersTest {
  private static void assertSet(final byte[] buf, final int bit) {
    // System.err.println(Buffers.toString(buf));
    assertTrue(String.valueOf(bit), Buffers.get(buf, bit));
  }

  private static void assertSet(final long[] buf, final int bit) {
    // System.err.println(Buffers.toString(buf));
    assertTrue(String.valueOf(bit), Buffers.get(buf, bit));
  }

  private static void assertClear(final byte[] buf, final int bit) {
    // System.err.println(Buffers.toString(buf));
    assertFalse(String.valueOf(bit), Buffers.get(buf, bit));
  }

  private static void assertClear(final long[] buf, final int bit) {
    // System.err.println(Buffers.toString(buf));
    assertFalse(String.valueOf(bit), Buffers.get(buf, bit));
  }

  @Test
  public void testByteOne() {
    byte[] buf = {0};
    for (int i = 0; i < Byte.SIZE; ++i) { // [N]
      assertClear(buf, i);
      buf = Buffers.set(buf, i, -1);
      assertSet(buf, i);
      buf = Buffers.clear(buf, i);
      assertClear(buf, i);
    }
  }

  @Test
  public void testLongOne() {
    long[] buf = {0};
    for (int i = 0; i < Long.SIZE; ++i) { // [N]
      assertClear(buf, i);
      buf = Buffers.set(buf, i, -1);
      assertSet(buf, i);
      buf = Buffers.clear(buf, i);
      assertClear(buf, i);
    }
  }

  @Test
  public void testByteMany() {
    final int length = Byte.SIZE;
    byte[] buf = {};
    for (int i = 0; i < Byte.SIZE * length; i += 2) { // [N]
      for (int j = Byte.SIZE * length - 2; j >= 1; j -= 2) { // [N]
        buf = Buffers.set(buf, i, -2.1);
        buf = Buffers.set(buf, j, -2.1);
        assertSet(buf, i);
        assertSet(buf, j);
        buf = Buffers.clear(buf, i);
        buf = Buffers.clear(buf, j);
        assertClear(buf, i);
        assertClear(buf, j);
      }
    }
  }

  @Test
  public void testLongMany() {
    final int length = Long.SIZE;
    byte[] buf = {};
    for (int i = 0; i < Long.SIZE * length; i += 2) { // [N]
      for (int j = Long.SIZE * length - 2; j >= 1; j -= 2) { // [N]
        buf = Buffers.set(buf, i, -2.1);
        buf = Buffers.set(buf, j, -2.1);
        assertSet(buf, i);
        assertSet(buf, j);
        buf = Buffers.clear(buf, i);
        buf = Buffers.clear(buf, j);
        assertClear(buf, i);
        assertClear(buf, j);
      }
    }
  }

  @Test
  public void testByteLogicalSize() {
    final byte[] buf = {0, 4, 3, 0, 5, 0, 9, 7, 0, 0, 0};
    assertEquals(8, Buffers.length(buf));
    assertEquals(0, Buffers.length(new byte[] {0, 0, 0}));

    final byte[] trimmed = Buffers.trimToLength(buf);
    assertArrayEquals(new byte[] {0, 4, 3, 0, 5, 0, 9, 7}, trimmed);
    assertArrayEquals(trimmed, Buffers.trimToLength(trimmed));
    assertEquals(0, Buffers.trimToLength(new byte[0]).length);
  }

  @Test
  public void testLongLogicalSize() {
    final long[] buf = {0, 4, 3, 0, 5, 0, 9, 7, 0, 0, 0};
    assertEquals(8, Buffers.length(buf));
    assertEquals(0, Buffers.length(new long[] {0, 0, 0}));

    final long[] trimmed = Buffers.trimToLength(buf);
    assertArrayEquals(new long[] {0, 4, 3, 0, 5, 0, 9, 7}, trimmed);
    assertArrayEquals(trimmed, Buffers.trimToLength(trimmed));
    assertEquals(0, Buffers.trimToLength(new byte[0]).length);
  }
}