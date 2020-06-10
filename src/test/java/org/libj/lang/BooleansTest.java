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
  public void testParseBooleanInteger() {
    assertNull(Booleans.parseBoolean((Integer)null));
    assertFalse(Booleans.parseBoolean(0));
    assertTrue(Booleans.parseBoolean(1));
    assertTrue(Booleans.parseBoolean(100));
  }

  @Test
  public void testParseBooleanString() {
    assertNull(Booleans.parseBoolean((String)null));
    assertFalse(Booleans.parseBoolean("0"));
    assertTrue(Booleans.parseBoolean("true"));
    assertTrue(Booleans.parseBoolean("TRUE"));
  }

  @Test
  public void testToDouble() {
    assertEquals(0, Booleans.toDouble(false), 0);
    assertEquals(1, Booleans.toDouble(true), 0);
  }

  @Test
  public void testToInt() {
    assertEquals(0, Booleans.toInt(false));
    assertEquals(1, Booleans.toInt(true));
  }

  @Test
  public void testToInteger() {
    assertNull(Booleans.toInteger(null));
    assertEquals(Integer.valueOf(0), Booleans.toInteger(Boolean.FALSE));
    assertEquals(Integer.valueOf(1), Booleans.toInteger(Boolean.TRUE));
  }
}