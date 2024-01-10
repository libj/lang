/* Copyright (c) 2023 Seva Safris, LibJ
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

public class CountingAppendableTest {
  @Test
  @SuppressWarnings("unused")
  public void testExceptions() {
    try {
      new CountingAppendable(null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }
  }

  @Test
  public void test() throws IOException {
    final StringBuilder b = new StringBuilder();
    final CountingAppendable c = new CountingAppendable(b);
    assertEquals(0, c.getCount());
    c.append('a');
    assertEquals(1, c.getCount());
    c.append("abc");
    assertEquals(4, c.getCount());
    c.append("abc", 1, 2);
    assertEquals(5, c.getCount());
    assertEquals(b.toString(), c.toString());
    assertEquals(b.hashCode(), c.hashCode());
  }
}
