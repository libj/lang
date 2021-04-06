/* Copyright (c) 2020 Seva Safris, LibJ
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

import java.math.BigDecimal;
import java.util.Random;

import org.junit.Test;

public class BigDecimalsTest {
  private static final Random random = new Random();

  @Test
  public void testInternBigDecimal() {
    final BigDecimal a = new BigDecimal("589.21");
    final BigDecimal b = new BigDecimal("12.2414");
    assertSame(a, BigDecimals.intern(a));
    assertSame(b, BigDecimals.intern(b));

    for (int i = 0; i < 100; ++i) {
      assertSame(a, BigDecimals.intern(new BigDecimal("589.21")));
      assertSame(b, BigDecimals.intern(new BigDecimal("12.2414")));
    }
  }

  @Test
  public void testInternString() {
    for (int i = 0; i < 100; ++i) {
      new Thread(() -> {
        for (int j = 0; j < 1000; ++j) {
          BigDecimals.intern(String.valueOf(j));
        }
      }).start();
    }
  }

  @Test
  public void testInternlong() {
    for (int i = 0; i < 100; ++i) {
      new Thread(() -> {
        for (long j = 0; j < 1000; ++j) {
          BigDecimals.intern(j);
        }
      }).start();
    }
  }

  @Test
  public void testInternLong() {
    for (int i = 0; i < 100; ++i) {
      new Thread(() -> {
        for (Long j = 0L; j < 1000; ++j) {
          BigDecimals.intern(j);
        }
      }).start();
    }
  }

  @Test
  public void testInterndouble() {
    for (int i = 0; i < 100; ++i) {
      new Thread(() -> {
        for (double j = 0; j < 1000; ++j) {
          BigDecimals.intern(j);
        }
      }).start();
    }
  }

  @Test
  public void testInternDouble() {
    for (int i = 0; i < 100; ++i) {
      new Thread(() -> {
        for (Double j = 0D; j < 1000; ++j) {
          BigDecimals.intern(j);
        }
      }).start();
    }
  }

  private static void testBigDecimalInfinity(final BigDecimal infinity, final int signum, final boolean recurse) {
    final String expected = (signum == -1 ? "-" : "") + "Infinity";
    assertEquals(expected, infinity.toEngineeringString());
    assertEquals(expected, infinity.toPlainString());
    assertEquals(expected, infinity.toString());
    if (recurse) {
      testBigDecimalInfinity(infinity.stripTrailingZeros(), signum, false);
      testBigDecimalInfinity(infinity.scaleByPowerOfTen(random.nextInt()), signum, false);
    }
  }

  @Test
  public void testBigDecimalInfinity() {
    testBigDecimalInfinity(BigDecimals.POSITIVE_INFINITY, 1, true);
    testBigDecimalInfinity(BigDecimals.NEGATIVE_INFINITY, -1, true);
  }
}