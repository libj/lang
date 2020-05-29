/* Copyright (c) 2013 LibJ
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

import java.io.IOException;

import org.junit.Test;

public class ThrowablesTest {
  private static void assertToString(final Throwable t) {
    final String expected = t.getClass().getName() + "\n\tat " + ThrowablesTest.class.getName() + ".testToString(" + ThrowablesTest.class.getSimpleName() + ".java:";
    assertEquals(expected, Throwables.toString(t).substring(0, expected.length()));
  }

  @Test
  public void testToString() {
    assertToString(new Exception());
    assertToString(new IOException());
  }
}