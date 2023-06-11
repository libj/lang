/* Copyright (c) 2022 LibJ
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

public class SystemsTest {
  @Test
  public void testHasProperty() {
    assertTrue(Systems.hasProperty("java.io.tmpdir"));
    System.setProperty("foo", "bar");
    assertTrue(Systems.hasProperty("foo"));
    System.setProperty("foo", "false");
    assertFalse(Systems.hasProperty("foo"));
  }

  @Test
  public void testGetPropertyInt() {
    assertEquals(7, Systems.getProperty("I", "i", 7));
    System.setProperty("i", "x");
    assertEquals(7, Systems.getProperty("I", "i", 7));
    System.setProperty("i", "3");
    assertEquals(3, Systems.getProperty("I", "i", 7));
    System.setProperty("I", "0");
    assertEquals(0, Systems.getProperty("I", "i", 7));
  }

  @Test
  public void testGetPropertyBoolean() {
    assertEquals(true, Systems.getProperty("B", "b", true));
    System.setProperty("b", "x");
    assertEquals(true, Systems.getProperty("B", "b", true));
    System.setProperty("b", "0");
    assertEquals(true, Systems.getProperty("B", "b", true));
    System.setProperty("b", "false");
    assertEquals(false, Systems.getProperty("B", "b", true));
    System.setProperty("B", "true");
    assertEquals(true, Systems.getProperty("B", "b", true));
  }

  @Test
  public void testGetPropertyString() {
    assertEquals(null, Systems.getProperty("S", "s"));
    System.setProperty("s", "x");
    assertEquals("x", Systems.getProperty("S", "s"));
    System.setProperty("S", "y");
    assertEquals("y", Systems.getProperty("S", "s"));
  }
}