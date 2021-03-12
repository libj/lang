/* Copyright (c) 2020 LibJ
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

public class BooleansTest {
  @Test
  public void testValueOfInteger() {
    assertNull(Booleans.valueOf((Integer)null));
    assertFalse(Booleans.parseBoolean(0));
    assertTrue(Booleans.parseBoolean(1));
    assertTrue(Booleans.parseBoolean(100));
  }

  @Test
  public void testValueOfString() {
    assertNull(Booleans.valueOf((String)null));
    assertFalse(Booleans.valueOf("0"));
    assertTrue(Booleans.valueOf("true"));
    assertTrue(Booleans.valueOf("TRUE"));
  }

  @Test
  public void testDoubleValue() {
    assertEquals(0, Booleans.doubleValue(false), 0);
    assertEquals(1, Booleans.doubleValue(true), 0);
  }

  @Test
  public void testByteValue() {
    assertEquals(0, Booleans.byteValue(false));
    assertEquals(1, Booleans.byteValue(true));
  }

  @Test
  public void testToInteger() {
    assertNull(Booleans.toByte(null));
    assertEquals(Byte.valueOf((byte)0), Booleans.toByte(Boolean.FALSE));
    assertEquals(Byte.valueOf((byte)1), Booleans.toByte(Boolean.TRUE));
  }
}